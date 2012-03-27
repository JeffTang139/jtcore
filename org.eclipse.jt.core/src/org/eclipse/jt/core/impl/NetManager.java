/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ConnectionManager.java
 * Date 2009-2-16
 */
package org.eclipse.jt.core.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.jt.core.impl.ByteBufferPool.ByteBufferWrapper;
import org.eclipse.jt.core.jetty.ConstantsForDnaRsi;
import org.eclipse.jt.core.misc.ExceptionCatcher;
import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.type.GUID;


/**
 * �����������<br/>
 * �������ϵ����л���Զ�̵��õ����Ӽ�������ض����Э����
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class NetManager implements ByteBufferManager {
	private volatile InetAddress[] locals;

	private void ensureInitLocals() {
		if (this.locals == null) {
			synchronized (this) {
				if (this.locals == null) {
					HashSet<InetAddress> lcs = new HashSet<InetAddress>();
					try {
						InetAddress[] temp = InetAddress
								.getAllByName(InetAddress.getLocalHost()
										.getHostName());
						for (InetAddress addr : temp) {
							lcs.add(addr);
						}
						temp = InetAddress.getAllByName("localhost");
						for (InetAddress addr : temp) {
							lcs.add(addr);
						}
						this.locals = lcs.toArray(new InetAddress[lcs.size()]);
					} catch (UnknownHostException e) {
						this.application.catcher.catchException(e, this);
						this.locals = new InetAddress[0];
					}
				}
			}
		}
	}

	final boolean isToSelf(String host, int port) {
		if (port != this.port) {
			return false;
		}
		this.ensureInitLocals();
		try {
			for (InetAddress addr : InetAddress.getAllByName(host)) {
				for (InetAddress local : this.locals) {
					if (addr.equals(local)) {
						return true;
					}
				}
			}
		} catch (UnknownHostException e) {
		}
		return false;
	}

	final boolean isToSelf(InetAddress target, int port) {
		if (port != this.port) {
			return false;
		}
		this.ensureInitLocals();
		for (InetAddress local : this.locals) {
			if (target.equals(local)) {
				return true;
			}
		}
		return false;
	}

	final ApplicationImpl application;
	final GUID ID;

	/**
	 * ������������Զ�̵��õļ����˿ڡ�
	 */
	private int port = -1;
	private boolean byJetty;

	private final WriteableDaemon wDaemon;
	private final ReadableDaemon rDaemon;

	private final DataSender sender;
	private final DataReceiver receiver;

	private final Dispatcher dispatcher;

	private final ByteBufferPool bufPool;

	final NetSocketChannelHandler ACCEPTOR;

	final void doDispose(ExceptionCatcher catcher) {
		// TODO
	}

	/**
	 * ���������������ʵ����
	 * 
	 * @param port
	 *            �������ӵ��������Ķ˿ںš�
	 * @throws IllegalArgumentException
	 *             �˿ںų�����ȡֵ��Χ��
	 * @throws CannotOpenSelectorException
	 *             ѡ����δ�ܿ�����
	 */
	NetManager(ApplicationImpl application) {
		this.application = application;
		this.ID = application.getNetCluster().appID;

		// XXX ����д�ļ໤Ӧ�ÿ���ʹ��ͬһ��Selector����
		this.wDaemon = new WriteableDaemon(this);
		this.rDaemon = new ReadableDaemon(this);

		this.sender = new DataSender(this);
		this.receiver = new DataReceiver(this);

		this.dispatcher = new Dispatcher(this);

		this.bufPool = new ByteBufferPool();

		this.ACCEPTOR = new NetSocketChannelHandler(this);
	}

	final void setPort(int port, boolean byJetty) {
		if (port < 0 || port > 0xFFFF) {
			throw new IllegalArgumentException("�˿ںų�����ȡֵ��Χ�� " + port);
		}
		this.port = port;
		this.byJetty = byJetty;
	}

	final int getPort() {
		return this.port;
	}

	final void config(SXElement clusterInfo) {
		if (clusterInfo != null) {
			String host = clusterInfo.getString(xml_a_cluster_masterhost);
			if (host != null) {
				int port = clusterInfo.getInt(xml_a_cluster_masterport,
						RIUtil.DEFAULT_PORT);
				try {
					this.clusterConfig = new ClusterConfig(this.nodeInfos
							.ensureGet(host, port, true));
				} catch (UnknownHostException e) {
					this.clusterConfig = null;
					throw Utils.tryThrowException(e);
				}
			} else {
				SXElement slave = clusterInfo.firstChild(xml_e_cluster_slave);
				if (slave != null) {
					ArrayList<NetNodeInfo> temp = new ArrayList<NetNodeInfo>();
					int port;
					do {
						/*
						 * XXX Ŀǰ�����host��ö���ʹ��ip
						 * Ҫ��Ȼ�����һ��host��Ӧ���ip�Ļ������ܾ�ƥ�䲻�ϣ���������ʧ�ܡ�
						 */
						host = slave.getString(xml_a_cluster_slave_host);
						port = slave.getInt(xml_a_cluster_slave_port,
								RIUtil.DEFAULT_PORT);
						try {
							temp.add(this.nodeInfos
									.ensureGet(host, port, false));
						} catch (UnknownHostException e) {
							throw Utils.tryThrowException(e);
						}
						slave = slave.nextSibling(xml_e_cluster_slave);
					} while (slave != null);
					this.clusterConfig = new ClusterConfig(temp
							.toArray(new NetNodeInfo[temp.size()]));
				} else {
					this.clusterConfig = null;
				}
			}
		} else {
			this.clusterConfig = null;
		}
		this.clusterConfigured = true;
	}

	private volatile boolean clusterConfigured;
	private volatile ClusterConfig clusterConfig;

	final boolean isInCluster() {
		if (!this.clusterConfigured) {
			throw new IllegalStateException("��Ⱥ��δ����");
		}
		return (this.clusterConfig != null);
	}

	private final boolean slaveNeedInitFromMaster() {
		return (this.isInCluster() && (this.clusterConfig.cluster == null));
	}

	final void start() {
		if (this.clusterConfig == null) {
			this.startAccept();
			this.startWorking();
		} else {
			if (this.clusterConfig.isThisMaster()) {
				this.startMaster();
			} else {
				this.startSlave();
			}
			Assertion.ASSERT(this.clusterConfig.cluster != null);
		}
	}

	private final void startMaster() {
		ConsoleLog.init("Cluster-Master starting ...");
		Cluster local = this.ensureGet(this.ID);
		this.clusterConfig.cluster = local;

		this.startAccept();

		long timeout = 30 * 60 * 1000; // half an hour.
		long startat = System.currentTimeMillis();
		synchronized (this.clusterConfig) {
			do {
				try {
					this.clusterConfig.wait(60000); // one minute.
				} catch (InterruptedException e) {
					throw Utils.tryThrowException(e);
				}
			} while (System.currentTimeMillis() - startat < timeout
					&& !this.clusterConfig.isStarted());
		}

		if (!this.clusterConfig.isStarted()) {
			throw new RuntimeException("��Ⱥ����ʧ�ܣ��ѳ�ʱ��");
		}

		// this.application.setClusterWholeMask(this.clusterConfig.wholeMask);
		this.startWorking();
		ConsoleLog.init("Cluster-Master started.");
	}

	private final void startSlave() {
		ConsoleLog.init("Cluster-Slave starting ...");
		try {
			this.connect(this.clusterConfig.master);
			this.clusterConfig.master.checkConnected();
		} catch (Throwable e) {
			ConsoleLog.debugError("���Ӽ�Ⱥ���ڵ�ʱ�����쳣��%s", e);
			throw Utils.tryThrowException(e);
		}
		this.startAccept();
		this.startWorking();
		ConsoleLog.init("Cluster-Slave started.");
	}

	private void startAccept() {
		if (!this.byJetty) {
			// TODO start a listener to accept rsi connections.
			throw new UnsupportedOperationException("Not implemented");
		}
	}

	private void startWorking() {
		// REMIND? ϵͳ����ʱû��Ҫ�������е��̣߳�������Щ�߳�ֻ��������������������ɡ�

		// ��дͨ���ļ໤�̡߳�
		// �����ֵ�һ��������ͨ��ʱ�������ϣ����ʱ������������Ҫ���͵ģ�����������̡߳�
		// RIUtil.startDaemon(this.wDaemon, "writable-dm");

		// �ɶ�ͨ���ļ໤�̡߳�
		// �����ֵ�һ�������ӵ�ͨ��ʱ����������̡߳�
		// RIUtil.startDaemon(this.rDaemon, "readable-dm");

		// ���ݷ����̡߳�
		// ����һ�������͵����ݰ��ύʱ����������̡߳�
		// RIUtil.startDaemon(this.sender, "data-sender");

		// ����Ӯȡ�̡߳�
		// ����һ���ɶ���ͨ������ʱ����������̡߳�
		// RIUtil.startDaemon(this.receiver, "data-receiver");

		// ���յ������ݰ��ķַ��̡߳�
		// ���ӵ���һ�����ݰ�ʱ����������̡߳�
		// RIUtil.startDaemon(this.dispatcher, "data-dispatcher");
	}

	private static volatile byte[] rsi_response_signal_data;

	static final byte[] getRsiResponseSignalData() {
		if (rsi_response_signal_data == null) {
			rsi_response_signal_data = ConstantsForDnaRsi
					.ascii2bytes(ConstantsForDnaRsi.RSI_RESPONSE_SIGNAL);
		}
		return rsi_response_signal_data.clone();
	}

	private volatile byte[] client_info;

	final byte[] getClientInfo() {
		if (this.client_info == null) {
			byte[] ci = new byte[21];
			Endianness localByteOrder = Endianness.LOCAL_ENDIAN;
			ci[0] = localByteOrder.code();
			localByteOrder.putInt(ci, 1, this.port);
			System.arraycopy(this.ID.toBytes(), 0, ci, 5, 16);
			this.client_info = ci;
		}
		return this.client_info.clone();
	}

	private volatile byte[] server_info;

	final byte[] getServerInfo() {
		if (this.server_info == null) {
			byte[] si = new byte[17];
			Endianness localByteOrder = Endianness.LOCAL_ENDIAN;
			si[0] = localByteOrder.code();
			System.arraycopy(this.ID.toBytes(), 0, si, 1, 16);
			this.server_info = si;
		}
		return this.server_info.clone();
	}

	final NetNodeInfo reconnect(NetNodeInfo netNodeInfo) {
		try {
			return this.connect(netNodeInfo);
		} catch (IOException e) {
			throw Utils.tryThrowException(e);
		}
	}

	private final NetNodeInfo connect(final NetNodeInfo netNodeInfo)
			throws IOException {
		if (netNodeInfo.isConnected()) {
			return netNodeInfo;
		}

		// open a new channel and connect.
		NetConnection nc = netNodeInfo.openNewConnection();

		try {
			NetNodeInfo nni = this.connect(nc, true);
			if (nni != netNodeInfo) {
				nc = nni.getConnection();
			}
			if (nc != null) {
				this.resolveClusterInfo(nc);
				this.deliverConnection(nc);
			} else {
				throw new CannotBuildConnectionException();
			}
		} catch (Throwable e) {
			try {
				nc.dispose();
			} catch (Exception ignore) {
			}
			if (e instanceof IOException) {
				throw (IOException) e;
			} else {
				throw Utils.tryThrowException(e);
			}
		}

		return nc.remoteNodeInfo;
	}

	private final void resolveClusterInfo(NetConnection nc) throws IOException,
			StructDefineNotFoundException {
		boolean inCluster = this.isInCluster();
		boolean startToMaster = this.slaveNeedInitFromMaster();
		Handshakor shakor = nc.getHandshakor();
		OutputStream out = shakor.getOutputStream();
		InputStream in = shakor.getInputStream();

		out.write(inCluster ? 1 : 0);

		if (inCluster) {
			if (startToMaster) {
				out.write(1);
			} else {
				out.write(0);

				byte[] buf = new byte[16];
				// cluster's id
				out.write(this.clusterConfig.cluster.id.toBytes(null), 0, 16);
				// node's index
				Endianness.LOCAL_ENDIAN.putInt(buf, 0, this.application
						.getNetCluster().thisClusterNodeIndex);
				out.write(buf, 0, 4);
			}
		}
		out.flush();

		inCluster = in.read() == 1;

		if (inCluster) {
			boolean slaveLaunch = in.read() == 1;

			if (slaveLaunch) {
				Assertion.ASSERT(!startToMaster);
				this.acceptSlaveNode(nc, out, in);
			} else {
				byte[] buf = new byte[16];
				GUID remoteClusterId = IOHelper.readGUID(in, buf);
				NetNodeInfo nni = nc.remoteNodeInfo;
				nni.index = IOHelper.readInt(in, nc.remoteEndian, buf);
				Cluster c = nni.owner;
				if (c == null) {
					c = this.ensureGet(remoteClusterId);
					c.putNode(nni);
				} else if (!c.id.equals(remoteClusterId)) {
					c.removeNode(nni);
					c = this.ensureGet(remoteClusterId);
					c.putNode(nni);
				}

				if (startToMaster) {
					this.receiveClusterConfig(nc, out, in);
				}
			}
		}
	}

	// Master�ڵ���Ϊ����������һ��Slave�ڵ��������������
	private final void acceptSlaveNode(NetConnection netConnection,
			OutputStream out, InputStream in) throws IOException,
			StructDefineNotFoundException {
		NetNodeInfo cni = netConnection.remoteNodeInfo;

		byte[] buf = new byte[16];
		Endianness.LOCAL_ENDIAN.putInt(buf, 0, cni.index);
		out.write(buf, 0, 4);
		Endianness.LOCAL_ENDIAN.putInt(buf, 0, this.clusterConfig.wholeMask);
		out.write(buf, 0, 4);
		out.flush();

		NetNodeInfo[] otherSlaves;
		try {
			this.clusterConfig.connected(cni);
			otherSlaves = this.clusterConfig.otherSlaves(cni);
		} catch (RuntimeException e) {
			Endianness.LOCAL_ENDIAN.putInt(buf, 0, -1);
			out.write(buf, 0, 4);
			out.flush();
			throw e;
		}

		Endianness.LOCAL_ENDIAN.putInt(buf, 0, otherSlaves.length);
		out.write(buf, 0, 4);
		if (otherSlaves.length > 0) {
			SOSerializer sos = new SOSerializer(new StreamBasedDataSerializer(
					out, Endianness.LOCAL_ENDIAN));
			sos.serialize(otherSlaves);
			sos.flush();
		}

		int code = in.read();
		if (code == 1) {
			this.clusterConfig.succeeded(cni);
		} else {
			try {
				netConnection
						.broken(new IllegalStateException("ʧ�ܵ���֤�룺" + code));
			} catch (Throwable ignore) {
			}
			this.clusterConfig.broken(cni);
		}
	}

	// Slave�ڵ���Ϊ�ͻ���������ʱ����Master�ڵ㷢���ļ�Ⱥ���á�
	private final void receiveClusterConfig(NetConnection netConnection,
			OutputStream out, InputStream in) throws IOException {
		this.clusterConfig.cluster = netConnection.remoteNodeInfo.owner;

		byte[] buf = new byte[16];
		// this.application.setClusterNodeIndex(IOHelper.readInt(in,
		// netConnection.remoteEndian, buf));
		int clusterWholeMask = IOHelper.readInt(in, netConnection.remoteEndian,
				buf);
		// this.application.setClusterWholeMask(clusterWholeMask);
		this.clusterConfig.wholeMask = clusterWholeMask;
		final int len = IOHelper.readInt(in, netConnection.remoteEndian, buf);
		if (len == -1) {
			netConnection.broken(new IllegalStateException());
			return;
		} else if (len > 0) {
			try {
				SODeserializer sod = new SODeserializer(netConnection
						.getStructAdapterSet(),
						new StreamBasedDataDeserializer(in,
								netConnection.remoteEndian));
				NetNodeInfo[] otherSlaves = (NetNodeInfo[]) sod.deserialize();
				Assertion.ASSERT(otherSlaves.length == len);

				this.clusterConfig.cluster.putAllNodes(otherSlaves);
			} catch (Throwable e) {
				out.write(-1);
				out.flush();
				ConsoleLog.init("Failed to connect to master.");
				throw Utils.tryThrowException(e);
			}
		}

		out.write(1);
		out.flush();
		ConsoleLog.init("Connect to Cluster-Master successfully.");
	}

	// XXX ���еĴ�����̶��ǰ�������������ӳ�ͻ�����еģ��������ж�����ͻ������Ҫ�޸ġ�
	private final NetNodeInfo connect(final NetConnection nc, boolean isClient)
			throws IOException {
		final GUID remoteServerId = nc.remoteServerId;
		Handshakor shakor = nc.getHandshakor();
		InputStream in = shakor.getInputStream();
		OutputStream out = shakor.getOutputStream();

		// XXX ID���ʱ��������鷳��Ӧ������δ���
		final boolean isJudge = this.ID.compareTo(remoteServerId) > 0;

		final NetConnection using;

		final NetConnection another = this.connections.tryPut(nc);
		boolean flag = another == null || another == nc;
		if (flag) {
			out.write(1);
			out.flush();
			if (in.read() == 1) {
				using = nc;
			} else {
				if (isJudge) {
					using = nc;
					out.write(1);
					out.flush();
				} else {
					Assertion.ASSERT(this.connections.tryRemove(nc));
					if (in.read() == 0) {
						using = this.connections.get(IOHelper.readInt(in,
								nc.remoteEndian, null));
						this.connections.tryPut(using);
					} else {
						throw new RuntimeException("HANDSHAKE ERROR");
					}
				}
			}
		} else {
			out.write(0);
			out.flush();
			if (in.read() == 0) {
				// XXX ��ʱ��Ϊ���ӵ���������Ϊ��ͬ���������Ӷ������ĳ�ͻ��
				using = another;
			} else {
				if (isJudge) {
					using = another;
					out.write(0); // use other
					IOHelper.writeInt(out, using.remoteId, null);
					out.flush();
				} else {
					if (in.read() == 1) {
						using = nc;
						Assertion.ASSERT(this.connections.tryRemove(another));
						this.connections.tryPut(using);
					} else {
						throw new RuntimeException("HANDSHAKE ERROR");
					}
				}
			}
		}

		final NetNodeInfo nni = using.remoteNodeInfo;

		// FIXME need modification
		Assertion.ASSERT(nni.tryBindConnection(nc));
		NetNodeInfo redirected = this.nodeInfos.tryPut(remoteServerId, nni);
		Assertion.ASSERT(redirected == null || redirected == nni);

		if (using != nc) {
			nc.dispose();
		}

		logConnectingEvent(using.getRemoteAddress(), nni.getPort(), isClient);

		return nni;
	}

	private void deliverConnection(NetConnection nc) throws IOException {
		nc.checkConnected();
		nc.channel.configureBlocking(false);
		nc.ready();
		this.pendWriteable(nc);
		this.pendReadable(nc);
	}

	private static final void logConnectingEvent(InetAddress address, int port,
			boolean isClient) {
		ConsoleLog.info("������%s����[%s:%s]�����ӡ�", isClient ? "��" : "����", address
				.getHostAddress(), port);
	}

	/**
	 * ����������ս��յ������ӡ�
	 */
	final void prepare(final NetConnection netConnection) {
		NetConnection nc = null;
		try {
			NetNodeInfo nni = this.connect(netConnection, false);
			nc = nni.getConnection();
			if (nc != null) {
				this.resolveClusterInfo(nc);
				this.deliverConnection(nc);
			}
		} catch (Throwable e) {
			if (nc != null) {
				try {
					nc.broken(e);
				} catch (Exception ignore) {
				}
			}
			ConsoleLog.debugError("������յ�������ʱ�����쳣��%s", e);
		} finally {
			if (nc != netConnection) {
				try {
					netConnection.dispose();
				} catch (Exception ignore) {
				}
			}
		}
	}

	// ------------------------------------------------------------------------
	// ��Ϣת��
	// ------------------------------------------------------------------------

	/**
	 * ��ָ�����Ӽ����д�໤����ĵȴ������С�
	 * 
	 * @param connection
	 */
	final void pendWriteable(NetConnection connection) {
		this.wDaemon.queue(connection);
	}

	/**
	 * ��ָ�����Ӽ���ɶ��໤����ĵȴ������С�
	 * 
	 * @param connection
	 */
	final void pendReadable(NetConnection connection) {
		this.rDaemon.queue(connection);
	}

	/**
	 * ��ָ�����Ӽ������ݿ�д�Ķ����У��Ա����ݷ�������д�̣߳����Դ������ǡ�
	 * 
	 * @param connection
	 */
	final void notifyWriteable(NetConnection connection) {
		this.sender.queue(connection);
	}

	final void unregisterWriteable(NetConnection connection) {
		this.wDaemon.unregister(connection);
	}

	/**
	 * ��ָ�����Ӽ������ݿɶ��Ķ����У��Ա����ݽ����������̣߳����Դ������ǡ�
	 * 
	 * @param connection
	 */
	final void notifyReadable(NetConnection connection) {
		this.receiver.queue(connection);
	}

	final void unregisterReadable(NetConnection connection) {
		this.rDaemon.unregister(connection);
	}

	/**
	 * ���뻺������
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	public final ByteBufferWrapper getBuffer() {
		return this.bufPool.get();
	}

	/**
	 * �ַ����յ������ݰ���
	 * 
	 * @param dataPacket
	 */
	final void dispatch(DataPacket dataPacket) {
		this.dispatcher.dispatch(dataPacket);
	}

	// ------------------------------------------------------------------------
	// �ڲ��洢
	// ------------------------------------------------------------------------
	private int _connectedSize;

	// REMIND? ������Ҫ�����������Ϊһ�ֿ����õı������֡�
	private final int POOL_X = 4;

	final void increaseConnectedSize(NetConnection netConnection) {
		this._connectedSize++;
		if (this._connectedSize > 0) {
			this.bufPool.ensureCapacity(this._connectedSize * this.POOL_X);
		}
	}

	final void decreaseConnectedSize(NetConnection netConnection) {
		this._connectedSize--;
		if (this._connectedSize > 0) {
			this.bufPool.ensureCapacity(this._connectedSize * this.POOL_X);
		}
	}

	private final Map<GUID, Cluster> clusters = new HashMap<GUID, Cluster>();

	private final Cluster ensureGet(GUID id) {
		synchronized (this.clusters) {
			Cluster c = this.clusters.get(id);
			if (c == null) {
				c = new Cluster(this, id);
				this.clusters.put(id, c);
			}
			return c;
		}
	}

	private final NetNodeInfoStorage nodeInfos = new NetNodeInfoStorage(
			new NetNodeInfoCreator() {
				public NetNodeInfo create(InetAddress address, int port,
						boolean isMaster) {
					return NetNodeInfo.createNNI(NetManager.this, address,
							port, isMaster);
				}

				public NetNodeInfo create(String host, int port,
						boolean isMaster) {
					return NetNodeInfo.createNNI(NetManager.this, host, port,
							isMaster);
				}
			});

	private static interface NetNodeInfoCreator {
		NetNodeInfo create(InetAddress address, int port, boolean isMaster);

		NetNodeInfo create(String host, int port, boolean isMaster);
	}

	private static class NetNodeInfoStorage {
		final NetNodeInfoCreator nodeCreator;
		final NetNodeInfoMap<InetAddress> addressNodes = new NetNodeInfoMap<InetAddress>();
		final NetNodeInfoMap<String> hostNodes = new NetNodeInfoMap<String>();
		final NetNodeInfoMap<GUID> idNodes = new NetNodeInfoMap<GUID>();

		NetNodeInfoStorage(NetNodeInfoCreator cniCreator) {
			this.nodeCreator = cniCreator;
		}

		final NetNodeInfo tryPut(GUID serverId, NetNodeInfo nodeInfo) {
			synchronized (this.idNodes) {
				NetNodeInfo old = this.idNodes
						.get(serverId, nodeInfo.getPort());
				if (old == nodeInfo || (old != null && old.isConnected())) {
					return old;
				}
				this.idNodes.put(serverId, nodeInfo);
				return null;
			}
		}

		final NetNodeInfo ensureGet(InetAddress address, int port,
				boolean isMaster) {
			NetNodeInfo cni;
			synchronized (this.addressNodes) {
				cni = this.addressNodes.get(address, port);
				if (cni != null) {
					return cni;
				}
				cni = this.nodeCreator.create(address, port, isMaster);
				this.addressNodes.put(address, cni);
			}
			return cni;
		}

		final NetNodeInfo ensureGet(String host, int port, boolean isMaster)
				throws UnknownHostException {
			NetNodeInfo found = null;
			synchronized (this.hostNodes) {
				found = this.hostNodes.get(host, port);
				if (found != null) {
					return found;
				}

				InetAddress[] addrs = InetAddress.getAllByName(host);
				synchronized (this.addressNodes) {
					for (int i = 0, len = addrs.length; i < len; i++) {
						if ((found = this.addressNodes.get(addrs[i], port)) != null) {
							this.hostNodes.put(host, found);
							for (int j = 0; j < i; j++) {
								this.addressNodes.put(addrs[j], found);
							}
							for (++i; i < len; i++) {
								this.addressNodes.put(addrs[i], found);
							}
							return found;
						}
					}

					found = this.nodeCreator.create(host, port, isMaster);
					this.hostNodes.put(host, found);
					for (int i = 0, len = addrs.length; i < len; i++) {
						this.addressNodes.put(addrs[i], found);
					}
				}
			}
			return found;
		}
	}

	private final class NetConnectionStorage {
		private final Map<GUID, NetConnection> cnns = new HashMap<GUID, NetConnection>();
		private final IntKeyMap<NetConnection> icnns = new IntKeyMap<NetConnection>();

		final NetConnection get(int id) {
			synchronized (this.icnns) {
				return this.icnns.get(id);
			}
		}

		final void put(NetConnection nc) {
			synchronized (this.icnns) {
				this.icnns.put(nc.id, nc);
			}
		}

		final void remove(NetConnection nc) {
			synchronized (this.icnns) {
				this.icnns.remove(nc.id);
			}
		}

		final NetConnection tryPut(NetConnection nc) {
			GUID id = nc.remoteServerId;
			synchronized (this.cnns) {
				NetConnection old = this.cnns.get(id);
				if (old == nc || nc.isConnected()) {
					return nc;
				}
				this.cnns.put(id, nc);
				return null;
			}
		}

		final boolean tryRemove(NetConnection nc) {
			GUID id = nc.remoteServerId;
			synchronized (this.cnns) {
				NetConnection old = this.cnns.get(id);
				if (old == nc) {
					this.cnns.remove(id);
					return true;
				} else {
					return false;
				}
			}
		}
	}

	private final NetConnectionStorage connections = new NetConnectionStorage();

	final void putNetConnection(NetConnection netConnection) {
		this.connections.put(netConnection);
	}

	final void removeNetConnection(NetConnection netConnection) {
		this.connections.remove(netConnection);
	}

	final NetNodeInfo ensureGet(String host, int port)
			throws UnknownHostException {
		return this.nodeInfos.ensureGet(host, port, true);
	}

	final NetNodeInfo ensureGet(InetAddress address, int port, boolean isMaster) {
		return this.nodeInfos.ensureGet(address, port, isMaster);
	}

	final void remove(NetNodeInfo clusterNodeInfo) {
		// TODO implement
		throw new UnsupportedOperationException("Not implemented");
	}

	// ///////////////////////////////////
	// ////////// xml /////////////////
	// /////////////////////////////////
	// final static String xml_element_remoteServiceInvoke = "rsi";
	// static final String xml_element_listen = "listen";
	// static final String xml_attr_host = "host";
	// static final String xml_attr_port = "port";

	static final String xml_e_cluster = "cluster";
	// static final String xml_a_cluster_mastername = "master-name";
	static final String xml_a_cluster_masterhost = "master-host";
	static final String xml_a_cluster_masterport = "master-port";
	static final String xml_e_cluster_slave = "slave";
	// static final String xml_a_cluster_slave_name = "name";
	static final String xml_a_cluster_slave_host = "host";
	static final String xml_a_cluster_slave_port = "rsi-port";
}
