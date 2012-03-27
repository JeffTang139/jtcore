package org.eclipse.jt.core.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.type.GUID;


/**
 * ����ͨ��ͨ��
 * 
 * @author Jeff Tang
 * 
 */
public final class NetChannelImpl implements NetChannel {
	/**
	 * ����״̬
	 * 
	 * @author Jeff Tang
	 * 
	 */
	private enum ConnectionState {
		/**
		 * û������
		 */
		NONE,
		/**
		 * �ȴ������������
		 */
		PASSIVE,
		/**
		 * �ȴ������������
		 */
		ACTIVE,
		/**
		 * �ȴ�������ɣ������������ӣ����б�������
		 */
		BOTH,
		/**
		 * ������ɣ����Խ���ͨ��
		 */
		STABLE,
		/**
		 * ����ʧ�ܣ������ӿ���
		 */
		FAIL,
		/**
		 * �ȴ����ӹر�
		 */
		CLOSE_WAIT
	}

	/**
	 * �����߳�״̬
	 * 
	 * @author Jeff Tang
	 * 
	 */
	private enum WorkingThreadState {
		/**
		 * ���ڹ���
		 */
		WORKING,
		/**
		 * ����
		 */
		IDLE,
		/**
		 * ��ʱ
		 */
		TIMEOUT
	}

	/**
	 * ��Ҫȷ�ϵ�����Ƭ�εĹ�����
	 * 
	 * @author Jeff Tang
	 * 
	 */
	private interface IAckRequiredFragmentBuilder {
		public DataFragment build();
	}

	/**
	 * ��Ҫȷ�ϵ�����Ƭ�ζ���Ԫ��
	 * 
	 * @author Jeff Tang
	 * 
	 */
	private static class AckRequiredFragmentStub {
		int ackID;
		final IAckRequiredFragmentBuilder builder;

		public AckRequiredFragmentStub(int ackID,
				IAckRequiredFragmentBuilder builder) {
			this.ackID = ackID;
			this.builder = builder;
		}
	}

	/**
	 * ��������
	 */
	static final byte CTRL_FLAG_TYPE_MASK = (byte) 0x0f;
	/**
	 * ����������
	 */
	static final byte CTRL_FLAG_SUBTYPE_MASK = (byte) 0xf0;
	/**
	 * ���Ʊ�ǣ����ݰ�Ƭ��
	 */
	static final byte CTRL_FLAG_PACKAGE = 1;
	/**
	 * ���Ʊ�ǣ����ݰ�Ƭ��ͷ
	 */
	static final byte CTRL_FLAG_PACKAGE_FIRST = (byte) 0x40;
	/**
	 * ���Ʊ�ǣ����ݰ�Ƭ��β
	 */
	static final byte CTRL_FLAG_PACKAGE_LAST = (byte) 0x80;
	/**
	 * ���Ʊ�ǣ���ֹ���ܷ��������ݰ����ñ���ɷ��ͷ�����
	 */
	static final byte CTRL_FLAG_BREAK_PACKAGE_RECEIVE = 2;
	/**
	 * ���Ʊ�ǣ���ֹ���ͷ��������ݰ����ñ���ɽ��ܷ�����
	 */
	static final byte CTRL_FLAG_BREAK_PACKAGE_SEND = 3;
	/**
	 * ���Ʊ�ǣ���������ϣ����ܷ���ԭ���ĳ����ط�
	 */
	static final byte CTRL_FLAG_PACKAGE_RESOLVED = 4;
	/**
	 * ���Ʊ�ǣ�֪ͨ���Ͷ����·������ݰ�
	 */
	static final byte CTRL_FLAG_RESEND_PACKAGE = 6;
	/**
	 * ���Ʊ�ǣ��Ͽ�����
	 */
	static final byte CTRL_FLAG_CLOSE = 7;
	/**
	 * Close��Ϣ��ȡ���Ͽ�����
	 */
	static final byte CTRL_FLAG_CLOSE_CANCEL = (byte) 0x40;
	/**
	 * ���Ʊ�ǣ��������ӣ�����Ҫȷ��
	 */
	static final byte CTRL_FLAG_KEEP_ALIVE = 12;
	/**
	 * ���Ʊ�ǣ�������Ϣ�����ڲ��������Ƿ���ͨ
	 */
	static final byte CTRL_FLAG_ECHO = 13;
	/**
	 * ���Ʊ�ǣ�ACKȷ����Ϣ
	 */
	static final byte CTRL_FLAG_ACK = 14;
	/**
	 * ����Ƭ����󳤶�
	 */
	static final int PACKAGE_FRAGMENT_SIZE = 1024 * 32;
	/**
	 * ����״̬��ʱʱ��
	 */
	static final long IDLE_TIMEOUT = 3000;
	/**
	 * keep-alive��Ϣ�ļ��ʱ��
	 */
	static final long KEEP_ALIVE_TIMING = IDLE_TIMEOUT / 2;

	private final NetChannelManagerImpl owner;
	/**
	 * Զ�̽ڵ��ID
	 */
	final GUID remoteNodeID;
	/**
	 * send��
	 */
	final Object outLock = new Object();
	/**
	 * receive��
	 */
	final Object inLock = new Object();
	/**
	 * ACK�������
	 */
	private int ackSeed;
	/**
	 * �ȴ�ack����Ϣ����
	 */
	private final Queue<AckRequiredFragmentStub> waitingAckFragments = new LinkedList<AckRequiredFragmentStub>();
	/**
	 * �ȴ���������ݰ�
	 */
	private final Queue<NetPackageSendingEntry<?>> waitingBuildingPackages = new LinkedList<NetPackageSendingEntry<?>>();
	/**
	 * �����е����ݰ�
	 */
	final IntKeyMap<NetPackageSendingEntry<?>> sendingPackages = new IntKeyMap<NetPackageSendingEntry<?>>();
	/**
	 * ������Ƭ��<br>
	 * Ϊ��ʹ���������������һ��Ƭ�Ϸ���ʱ����һЩƬ�Ͼ�Ԥ��׼���á�
	 */
	protected final Queue<DataFragment> waitingSendingFragments = new LinkedList<DataFragment>();
	/**
	 * ���յ����ݰ�
	 */
	private final IntKeyMap<NetPackageReceivingEntry<?>> receivingPackages = new IntKeyMap<NetPackageReceivingEntry<?>>();
	/**
	 * ���ڹ����Fragment�ĸ���
	 */
	protected int buildingFragmentCount;
	/**
	 * �ȴ������жӵ���󳤶�
	 */
	private int waitingSendingQueueMaxSize = 5;
	/**
	 * ָʾ�Ƿ񱣳�����
	 */
	private boolean keepAlive;
	/**
	 * ���һ�λʱ��
	 */
	private long lastActiveTime;
	/**
	 * ����״̬
	 */
	private ConnectionState connectionState = ConnectionState.NONE;
	/**
	 * ������
	 */
	private final Object connectionLock = new Object();
	/**
	 * ��Ч����
	 */
	private NetConnectionImpl conn;
	/**
	 * ��ʱ����������
	 */
	private NetActiveConnectionImpl activeConn;
	/**
	 * ��ʱ�ı�������
	 */
	private NetPassiveConnectionImpl passiveConn;
	/**
	 * Զ��������ַ��ֻ����������ʱ����
	 */
	private URL url;

	NetChannelImpl(NetChannelManagerImpl owner, GUID remoteNodeID) {
		if (owner == null) {
			throw new NullArgumentException("owner");
		}
		if (remoteNodeID == null || GUID.emptyID.equals(remoteNodeID)) {
			throw new NullArgumentException("remoteNodeID");
		}
		this.owner = owner;
		this.remoteNodeID = remoteNodeID;
		DebugHelper.strike("new channel created at node " + remoteNodeID);
	}

	public final GUID getRemoteNodeID() {
		return this.remoteNodeID;
	}

	public final GUID getRemoteAppID() {
		this.connectAndStart();
		return this.conn.getRemoteAppID();
	}

	public final int getRemoteNodeClusterIndex() {
		return this.conn.getRemoteNodeClusterIndex();
	}

	public final long getRemoteAppInstanceVersion() {
		this.connectAndStart();
		return this.conn.getRemoteAppInstanceVersion();
	}

	public final short getRemoteSerializeVersion() {
		this.connectAndStart();
		return this.conn.getRemoteSerializeVersion();
	}

	public final void setKeepAlive(boolean isKeepAlive) {
		synchronized (this.outLock) {
			this.keepAlive = isKeepAlive;
		}
	}

	public final boolean getKeepAlive() {
		synchronized (this.outLock) {
			return this.keepAlive;
		}
	}

	final int getComponentVersion() {
		return this.owner.getVersion();
	}

	final long getChannelVersion() {
		return this.owner.channelVersion;
	}

	final ApplicationImpl getApplication() {
		return this.owner.application;
	}

	final int newPackageID() {
		return this.owner.newPackageID();
	}

	final void offerFragmentResolve(NetPackageReceivingEntry<?> rpe)
			throws InterruptedException {
		this.owner.offerFragmentResolve(rpe);
	}

	/**
	 * �����С�̶��Ļ���������СΪPACKAGE_FRAGMENT_SIZE
	 * 
	 * @return
	 */
	final DataFragment allocDataFragment() {
		return this.allocDataFragment(PACKAGE_FRAGMENT_SIZE);
	}

	/**
	 * �����ܹ������ֽ���Ϊcapacity�����ݵĻ�������������ǰ�������4���ֽ���Ϊͷ��
	 * 
	 * @param capacity
	 * @return
	 */
	final DataFragmentImpl allocDataFragment(int capacity) {
		// �ܳ��� = 4(size) + capacity + 4(next size)
		DataFragmentImpl f = new DataFragmentImpl(capacity + 8);
		// size
		f.skip(4);
		f.limit(capacity + 4);
		return f;
	}

	final void releaseDataFragment(DataFragment fragment) {
		// do nothing
	}

	private final void setConnStateNoSync(ConnectionState state) {
		this.connectionState = state;
		this.connectionLock.notifyAll();
	}

	/**
	 * �������Ӳ����������ͺͽ����߳�
	 * 
	 * @return
	 * @throws IOException
	 */
	public final void connectAndStart() {
		try {
			NetConnectionImpl channel;
			ACTIVE_WAIT: {
				for (;;) {
					synchronized (this.connectionLock) {
						switch (this.connectionState) {
						case NONE:
							if (this.url != null) {
								this.setConnStateNoSync(ConnectionState.ACTIVE);
								// û�����ӣ�������������
								channel = this.activeConn = new NetActiveConnectionImpl(
										this, this.url);
								break ACTIVE_WAIT;
							}
							// ���Եȴ���������
							this.passiveConn = new NetPassiveConnectionImpl(
									this);
							this.setConnStateNoSync(ConnectionState.PASSIVE);
						case PASSIVE:
							// ���ڳ��Ա�������
							channel = this.passiveConn;
							break;
						case STABLE:
							// �����ѽ����ã�ֱ�ӷ���
							return;
						case FAIL:
							throw new IOException("����ʧ��");
						default:
							// ACTIVE
							// BOTH
							// CLOSE_WAIT
							// �ȴ�״̬
							try {
								long left = NetConnectionImpl.CONNECTION_TIMEOUT;
								long start = System.currentTimeMillis();
								while (left > 0) {
									this.connectionLock.wait(left);
									switch (this.connectionState) {
									case ACTIVE:
									case BOTH:
									case CLOSE_WAIT:
										left = start
												+ NetConnectionImpl.CONNECTION_TIMEOUT
												- System.currentTimeMillis();
										if (left <= 0) {
											throw new ConnectException("���ӳ�ʱ");
										}
										continue;
									}
									break;
								}
							} catch (InterruptedException e) {
								throw Utils.tryThrowException(e);
							}
							continue;
						}
					}
					// PASSIVE
					this.waitPassiveConnection(channel);
				}
			}
			boolean available = true;
			// ȷ���������ӳɹ�
			try {
				channel.ensureConnected();
			} catch (Throwable e) {
				e.printStackTrace();
				available = false;
			}
			synchronized (this.connectionLock) {
				try {
					switch (this.connectionState) {
					case ACTIVE:
						if (!available) {
							this.setConnStateNoSync(ConnectionState.FAIL);
							throw new IOException("�޷����ӵ�" + this.url);
						}
						this.conn = this.activeConn;
						this.setConnStateNoSync(ConnectionState.STABLE);
						return;
					case BOTH:
						if (available && this.useActiveChannel()) { // �����������ӣ����б������ӣ��Ƚ����ȼ�
							// �����������ȣ��򷵻��������ӣ���ձ�������
							this.passiveConn.disconnect();
							// ������������
							this.conn = this.activeConn;
							this.setConnStateNoSync(ConnectionState.STABLE);
							return;
						}
						// �����������ȣ������������
						this.activeConn.disconnect();
						// �ȴ���������
						this.setConnStateNoSync(ConnectionState.PASSIVE);
						channel = this.passiveConn;
						break;
					case FAIL:
						throw new IOException("����ʧ��");
					default:
						throw new IllegalStateException("״̬����"
								+ this.connectionState);
					}
				} finally {
					switch (this.connectionState) {
					case STABLE:
						this.conn.start();
					case FAIL:
						this.activeConn = null;
						this.passiveConn = null;
						break;
					default:
						throw new IllegalStateException("״̬����"
								+ this.connectionState);
					}
				}
			}
			this.waitPassiveConnection(channel);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	private final NetPassiveConnectionImpl getPassiveConnection()
			throws Throwable {
		for (;;) {
			synchronized (this.connectionLock) {
				switch (this.connectionState) {
				case NONE:
					this.setConnStateNoSync(ConnectionState.PASSIVE);
					// ������������
					return this.passiveConn = new NetPassiveConnectionImpl(this);
				case ACTIVE:
					this.setConnStateNoSync(ConnectionState.BOTH);
					// ���ر�������
					return this.passiveConn = new NetPassiveConnectionImpl(this);
				case STABLE:
					this.setConnStateNoSync(ConnectionState.CLOSE_WAIT);
					this.conn.shutdown(true);
					break;
				case CLOSE_WAIT:
					this.connectionLock.wait();
					continue;
				case FAIL:
					throw new IOException("����ʧ��");
				default:
					// PASSIVE
					// WAIT
					// BOTH
					// �Ѿ����ڱ������ӣ�ֱ�ӷ���
					return this.passiveConn;
				}
			}
			// �ر�
			this.owner.application.overlappedManager.startWork(new Work() {
				@Override
				protected void doWork(WorkingThread thread) throws Throwable {
					close();
				}
			});
		}
	}

	/**
	 * ���ñ������ӵ�OutputStream���ҳ������������߳�
	 * 
	 * @param out
	 */
	public final void attachServletOutput(OutputStream out) {
		try {
			this.owner.application.overlappedManager.startWork(new Work() {
				@Override
				protected void doWork(WorkingThread thread) throws Throwable {
					// ȷ����������׼����
					connectAndStart();
				}
			});
			this.getPassiveConnection().servletOutputThreadEntry(out);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	/**
	 * ���ñ������ӵ�InputStream���ҳ������������߳�
	 * 
	 * @return
	 * @throws IOException
	 */
	public final void attachServletInput(InputStream in) {
		try {
			this.getPassiveConnection().servletInputThreadEntry(in);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	private final void waitPassiveConnection(NetConnectionImpl conn)
			throws Throwable {
		try {
			conn.ensureConnected();
		} catch (Throwable e) {
			conn = null;
			throw e;
		} finally {
			synchronized (this.connectionLock) {
				if (conn == null) {
					this.setConnStateNoSync(ConnectionState.FAIL);
				} else {
					if (conn != this.passiveConn) {
						throw new IllegalStateException();
					}
					this.conn = conn;
					this.conn.start();
					this.setConnStateNoSync(ConnectionState.STABLE);
				}
				this.passiveConn = null;
				this.activeConn = null;
			}
		}
	}

	/**
	 * ����NodeID�ж��ĸ��������ȼ���
	 * 
	 * @return
	 */
	private final boolean useActiveChannel() {
		int i = this.remoteNodeID.compareTo(this.owner.application.localNodeID);
		if (i > 0) {
			return false;
		} else if (i < 0) {
			return true;
		}
		throw new UnsupportedOperationException("���ܽ��������������");
	}

	/**
	 * �����������ӻ���ܱ������ӣ����ȴ����ӳɹ������׳��쳣
	 */
	public final void setURL(URL address) {
		synchronized (this.connectionLock) {
			this.url = address;
		}
	}

	/**
	 * �Ͽ����ӣ�����������״̬ΪNONE
	 */
	private final void close() {
		NetConnectionImpl conn;
		synchronized (this.connectionLock) {
			if (this.connectionState != ConnectionState.CLOSE_WAIT) {
				return;
			}
			conn = this.conn;
		}
		conn.disconnect();
		synchronized (this.connectionLock) {
			if (this.connectionState != ConnectionState.CLOSE_WAIT) {
				return;
			}
			this.conn = null;
			this.setConnStateNoSync(ConnectionState.NONE);
		}
		synchronized (this.outLock) {
			// �ٴμ���Ƿ��������ڵȴ�
			if (this.waitingAckFragments.isEmpty()
					&& this.sendingPackages.isEmpty()) {
				synchronized (this.inLock) {
					if (this.receivingPackages.isEmpty()) {
						return;
					}
				}
			}
		}
		// ��������
		this.connectAndStart();
	}

	final void onConnectionClose(NetConnectionImpl conn) {
		synchronized (this.connectionLock) {
			if (this.conn != conn) {
				return;
			}
			switch (this.connectionState) {
			case STABLE:
				this.conn = null;
				this.setConnStateNoSync(ConnectionState.FAIL);
				break;
			case CLOSE_WAIT:
				this.conn = null;
				this.setConnStateNoSync(ConnectionState.NONE);
			default:
				return;
			}
		}
		DebugHelper.strike("unuseChannel " + this.remoteNodeID);
		this.owner.unuseChannel(this);
		this.reset();
	}

	final void reset() {
		final ArrayList<AsyncIOStub<?>> arr = new ArrayList<AsyncIOStub<?>>();
		synchronized (this.outLock) {
			this.sendingPackages
					.visitAll(new ValueVisitor<NetPackageSendingEntry<?>>() {
						public void visit(int key,
								NetPackageSendingEntry<?> value) {
							arr.add(value);
						}
					});
			this.waitingAckFragments.clear();
			this.waitingBuildingPackages.clear();
			this.waitingSendingFragments.clear();
			this.sendingPackages.clear();
		}
		synchronized (this.inLock) {
			this.receivingPackages
					.visitAll(new ValueVisitor<NetPackageReceivingEntry<?>>() {
						public void visit(int key,
								NetPackageReceivingEntry<?> value) {
							arr.add(value);
						}
					});
			this.receivingPackages.clear();
		}
		for (AsyncIOStub<?> stub : arr) {
			try {
				stub.cancel();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * ������Ϣ
	 */
	protected final IAckRequiredFragmentBuilder echoBuilder = new IAckRequiredFragmentBuilder() {
		public DataFragment build() {
			DataFragment fragment = NetChannelImpl.this.allocDataFragment(1);
			fragment.writeByte(CTRL_FLAG_ECHO);
			return fragment;
		}
	};

	/**
	 * ����resend��Ϣ
	 */
	protected final IAckRequiredFragmentBuilder resendBuilder() {
		final ArrayList<NetPackageReceivingEntry<?>> arr = new ArrayList<NetPackageReceivingEntry<?>>();
		synchronized (this.inLock) {
			this.receivingPackages
					.visitAll(new ValueVisitor<NetPackageReceivingEntry<?>>() {
						public void visit(int key,
								NetPackageReceivingEntry<?> value) {
							arr.add(value);
						}
					});
		}
		for (int i = arr.size() - 1; i >= 0; i--) {
			if (arr.get(i).isReceivingComplete()) {
				arr.remove(i);
			}
		}
		if (!arr.isEmpty()) {
			synchronized (this.inLock) {
				for (NetPackageReceivingEntry<?> rpe : arr) {
					this.receivingPackages.remove(rpe.packageID);
					rpe.breakResolve();
				}
			}
			return new IAckRequiredFragmentBuilder() {
				public DataFragment build() {
					DataFragment fragment = NetChannelImpl.this
							.allocDataFragment(5 + 4 * arr.size());
					fragment.writeByte(CTRL_FLAG_RESEND_PACKAGE);
					fragment.writeInt(conn.generation);
					for (NetPackageReceivingEntry<?> rpe : arr) {
						fragment.writeInt(rpe.packageID);
					}
					return fragment;
				}
			};
		}
		return null;
	}

	// /////////////////////////////////////////////////
	// //////////////�����߳����////////////////////////

	public final <TAttachment> AsyncIOStub<TAttachment> startSendingPackage(
			DataFragmentBuilder<? super TAttachment> builder,
			TAttachment attachment) {
		if (builder == null) {
			throw new NullArgumentException("builder");
		}
		// ȷ��������Ч
		this.connectAndStart();
		final NetPackageSendingEntry<TAttachment> spe = new NetPackageSendingEntry<TAttachment>(
				this, builder, attachment);
		synchronized (this.outLock) {
			this.sendingPackages.put(spe.packageID, spe);
			this.tryStartFragmentBuildNoSync(spe);
		}
		return spe;
	}

	final void tryStartFragmentBuild(NetPackageSendingEntry<?> newOne) {
		synchronized (this.outLock) {
			this.tryStartFragmentBuildNoSync(newOne);
		}
	}

	protected final void tryStartFragmentBuildNoSync(
			NetPackageSendingEntry<?> newOne) {
		if (newOne != null) {
			newOne.setState(NetPackageSendingEntry.STATE_QUEUING);
			this.waitingBuildingPackages.add(newOne);
		}
		for (int needStartBuildCount = this.waitingSendingQueueMaxSize
				- this.waitingSendingFragments.size()
				- this.buildingFragmentCount; needStartBuildCount > 0; needStartBuildCount--) {
			// ��Ҫ��fragment����
			final NetPackageSendingEntry<?> one = this.waitingBuildingPackages
					.poll();
			if (one != null) {
				one.setState(NetPackageSendingEntry.STATE_QUEUING);
				this.owner.offerFragmentBuild(one);
				this.buildingFragmentCount++;
			} else {
				break;
			}
		}
	}

	// //////////////�����߳����////////////////////////
	// /////////////////////////////////////////////////

	// /////////////////////////////////////////////////
	// //////////////�����߳����////////////////////////

	/**
	 * ���ͽ��յ�������Ϣ��ظ���ȷ����Ϣ
	 */
	final void postAckCtrl(int ackID) {
		DataFragment fragment = this.allocDataFragment(5);
		fragment.writeByte(CTRL_FLAG_ACK);
		fragment.writeInt(ackID);
		this.postDataFragmentToSend(null, fragment);
	}

	/**
	 * �����жϽ������ݰ�������Ϣ������Ҫ�ظ�ȷ��
	 */
	final void postBreakReceivePackageCtrl(int packageID) {
		// �����Ƴ����Ͷ���
		synchronized (this.outLock) {
			this.sendingPackages.remove(packageID);
		}
		DataFragment fragment = this.allocDataFragment(5);
		fragment.writeByte(CTRL_FLAG_BREAK_PACKAGE_RECEIVE);
		fragment.writeInt(packageID);
		this.postDataFragmentToSend(null, fragment);
	}

	/**
	 * �����жϷ������ݰ�������Ϣ����Ҫ�ظ�ȷ��
	 */
	final void postBreakSendPackageCtrl(final int packageID) {
		this.postAckReqiredFragmentToSend(new IAckRequiredFragmentBuilder() {
			public DataFragment build() {
				DataFragment fragment = NetChannelImpl.this
						.allocDataFragment(5);
				fragment.writeByte(CTRL_FLAG_BREAK_PACKAGE_SEND);
				fragment.writeInt(packageID);
				return fragment;
			}
		});
	}

	final void packageResolved(final int packageID) {
		synchronized (this.inLock) {
			this.receivingPackages.remove(packageID);
		}
		// �������ݰ���ԭ��Ͽ�����Ϣ����Ҫ�ظ�ȷ��
		this.postAckReqiredFragmentToSend(new IAckRequiredFragmentBuilder() {
			public DataFragment build() {
				DataFragment fragment = NetChannelImpl.this
						.allocDataFragment(5);
				fragment.writeByte(CTRL_FLAG_PACKAGE_RESOLVED);
				fragment.writeInt(packageID);
				return fragment;
			}
		});
	}

	/**
	 * ���첢Ͷ����Ҫack��Ƭ��
	 * 
	 * @param builder
	 * @param otherBuilders
	 */
	final void postAckReqiredFragmentToSend(
			IAckRequiredFragmentBuilder builder,
			IAckRequiredFragmentBuilder... otherBuilders) {
		synchronized (this.outLock) {
			int ackID = this.ackSeed++;
			DataFragment fragment = builder.build();
			fragment.writeInt(ackID);
			fragment.limit(fragment.getPosition());
			// ����ack����
			this.waitingAckFragments.offer(new AckRequiredFragmentStub(ackID,
					builder));
			DebugHelper.trace("post ", fragment);
			// ���뷢�Ͷ���
			this.waitingSendingFragments.add(fragment);
			for (IAckRequiredFragmentBuilder b : otherBuilders) {
				ackID = this.ackSeed++;
				fragment = b.build();
				fragment.writeInt(ackID);
				fragment.limit(fragment.getPosition());
				// ����ack����
				this.waitingAckFragments.offer(new AckRequiredFragmentStub(
						ackID, builder));
				DebugHelper.trace("post ", fragment);
				// ���뷢�Ͷ���
				this.waitingSendingFragments.add(fragment);
			}
			// ֪ͨ�����߳�����
			this.outLock.notifyAll();
		}
	}

	/**
	 * �����Fragment��Ͷ�ݵ������߳�
	 * 
	 * @param asyncStub
	 * @param fragment
	 */
	final void postDataFragmentToSend(NetPackageSendingEntry<?> asyncStub,
			DataFragment fragment) {
		POST: {
			synchronized (this.outLock) {
				if (asyncStub != null) {
					this.buildingFragmentCount--;
					if (asyncStub.needResetPackage(this.conn.generation)) {
						break POST;
					}
					switch (asyncStub.getState()) {
					case NetPackageSendingEntry.STATE_BUILDING_AND_SENDING:
						// �����ٴ������������
						this.tryStartFragmentBuildNoSync(asyncStub);
						break;
					case NetPackageSendingEntry.STATE_BUILDING_COMPLETE:
						asyncStub
								.setState(NetPackageSendingEntry.STATE_WAITING_RESOLVE);
						break;
					}
				}
				fragment.limit(fragment.getPosition());
				this.waitingSendingFragments.add(fragment);
				// ֪ͨ�����߳�����
				this.outLock.notifyAll();
				DebugHelper.trace("post ", fragment);
			}
			return;
		}
		DebugHelper.fault("reset package " + asyncStub.packageID);
		asyncStub.tryResetPackage();
	}

	protected final void sendThreadRun(OutputStream out) throws Throwable {
		// ����echo
		this.postAckReqiredFragmentToSend(this.echoBuilder);
		DataFragment toSend;
		for (;;) {
			SEND: {
				synchronized (this.outLock) {
					toSend = this.waitingSendingFragments.poll();
					// �����¹����߳�
					this.tryStartFragmentBuildNoSync(null);
					if (toSend == null) {
						// ������ɣ��������״̬
						// ����ʱ���
						this.lastActiveTime = System.currentTimeMillis();
						break SEND;
					}
				}
				this.conn.send(out, toSend);
				continue;
			}
			this.doIdle(out);
		}
	}

	private final void doIdle(OutputStream out) throws Throwable {
		DataFragment toSend;
		for (;;) {
			synchronized (this.outLock) {
				if (!this.waitingSendingFragments.isEmpty()
						|| this.waitingBuildingPackages.size() > 0) {
					// ��Ƭ�οɷ��ͣ����뷢��״̬
					return;
				}
				// �ȴ�
				this.outLock.wait(KEEP_ALIVE_TIMING);
				if (!this.waitingSendingFragments.isEmpty()
						|| this.waitingBuildingPackages.size() > 0) {
					// ��Ƭ�οɷ��ͣ����뷢��״̬
					return;
				}
				// ���״̬��
				// 1.��ʱ���������ӣ�����close
				// 2.δ��ʱ������keep-alive��
				// a)���У����keepAlive�����ʱ���
				// b)�ǿ��У�����ʱ���
				KEEP_ALIVE: {
					switch (this.detectWorkingThreadStateNoSync()) {
					case IDLE:
						if (!this.keepAlive) {
							break;
						}
					case WORKING:
						// ����ʱ���
						this.lastActiveTime = System.currentTimeMillis();
						break;
					case TIMEOUT:
						synchronized (this.connectionLock) {
							if (this.connectionState == ConnectionState.CLOSE_WAIT) {
								// ����keep-alive
								break;
							} else {
								switch (this.connectionState) {
								case STABLE:
									break;
								default:
									throw new IllegalStateException("����״̬��"
											+ this.connectionState);
								}
							}
							// ���������߳�
							this.setConnStateNoSync(ConnectionState.CLOSE_WAIT);
							this.conn.shutdown(true);
						}
						DebugHelper.strike("idle timeout");
						// ����close��Ϣ
						toSend = allocDataFragment(5);
						toSend.writeByte(CTRL_FLAG_CLOSE);
						toSend.limit(toSend.getPosition());
						DebugHelper.trace("post ", toSend);
						break KEEP_ALIVE;
					}
					// ����keep-alive
					toSend = this.allocDataFragment(1);
					toSend.writeByte(CTRL_FLAG_KEEP_ALIVE);
					toSend.limit(toSend.getPosition());
				}
			}
			this.conn.send(out, toSend);
		}
	}

	private final WorkingThreadState detectWorkingThreadStateNoSync() {
		if (this.waitingAckFragments.isEmpty()
				&& this.sendingPackages.isEmpty()) {
			synchronized (this.inLock) {
				if (this.receivingPackages.isEmpty()) {
					// �жϳ�ʱ
					if (System.currentTimeMillis() - this.lastActiveTime > IDLE_TIMEOUT) {
						// ��ʱ
						return WorkingThreadState.TIMEOUT;
					}
					// ����
					return WorkingThreadState.IDLE;
				}
			}
		}
		return WorkingThreadState.WORKING;
	}

	// //////////////�����߳����////////////////////////
	// /////////////////////////////////////////////////

	// /////////////////////////////////////////////////
	// //////////////�����߳����////////////////////////

	/**
	 * �������ݰ�Ƭ��
	 */
	private final void onPackageFragment(byte ctrlFlag, DataFragment received)
			throws InterruptedException {
		final int packageID = received.readInt();
		NetPackageReceivingEntry<?> rpe;
		synchronized (this.inLock) {
			rpe = this.receivingPackages.get(packageID);
		}
		if (rpe != null && rpe.receiverGeneration != this.conn.generation) {
			// �������ã����½������ݰ�
			synchronized (this.inLock) {
				this.receivingPackages.remove(packageID);
			}
			rpe.cancel();
			rpe = null;
		}
		if (rpe == null) {
			if ((ctrlFlag & CTRL_FLAG_PACKAGE_FIRST) == 0) {
				// ������������Ƭ�ϣ�˵�����жϽ��յ�Ƭ��
				this.releaseDataFragment(received);
				DebugHelper.fault("drop " + packageID);
				this.postBreakSendPackageCtrl(packageID);
				return;
			}
			// �����ݰ���һ��Ƭ��
			rpe = new NetPackageReceivingEntry<Object>(this, packageID);
			rpe.receiverGeneration = this.conn.generation;
			this.owner.offerPackageReceiving(rpe, received);
			if (!rpe.resolverValid()) {
				DebugHelper.fault("refuse package " + packageID);
				// ���ݰ�û�б�����
				this.releaseDataFragment(received);
				this.postBreakSendPackageCtrl(packageID);
				return;
			}
			synchronized (this.inLock) {
				this.receivingPackages.put(packageID, rpe);
			}
		}
		// ��ӵ�resolve����
		rpe.queueToResolve(received, (ctrlFlag & CTRL_FLAG_PACKAGE_LAST) != 0);
	}

	private final void onBreakPackageReceiveFragment(DataFragment received)
			throws InterruptedException {
		this.breakReceive(received.readInt());
	}

	private final void onBreakPackageSendFragment(DataFragment received) {
		final int packageID = received.readInt();
		final int ackID = received.readInt();
		try {
			this.breakSend(packageID);
		} finally {
			this.postAckCtrl(ackID);
		}
	}

	/**
	 * ��������ϣ����ܷ���ԭ���ĳ����ط�
	 * 
	 * @param received
	 */
	private final void onPackageResolvedFragment(DataFragment received) {
		final int packageID = received.readInt();
		final int ackID = received.readInt();
		NetPackageSendingEntry<?> spe = null;
		try {
			synchronized (this.outLock) {
				spe = this.sendingPackages.remove(packageID);
				if (spe == null) {
					return;
				}
			}
			spe.setResolved(true);
		} finally {
			this.postAckCtrl(ackID);
		}
	}

	private final void onResendPackageFragment(DataFragment received) {
		int start = received.getPosition();
		received.setPosition(start + received.remain() - 4);
		int ackID = received.readInt();
		try {
			received.setPosition(start);
			int senderGeneration = received.readInt();
			while (received.remain() > 4) {
				final int packageID = received.readInt();
				NetPackageSendingEntry<?> spe = null;
				synchronized (this.outLock) {
					spe = this.sendingPackages.remove(packageID);
					if (spe == null
							|| spe.getState() != NetPackageSendingEntry.STATE_WAITING_RESOLVE
							|| !spe.needResetPackage(senderGeneration)) {
						continue;
					}
				}
				// XXX ����ڱ���߳���ȥ��
				spe.tryResetPackage();
			}
		} finally {
			this.postAckCtrl(ackID);
		}
	}

	private final void onCloseFragment(byte ctrlFlag, DataFragment received) {
		this.commitAck(received.readInt());
		switch (ctrlFlag & CTRL_FLAG_SUBTYPE_MASK) {
		case 0:
			CANCEL: {
				synchronized (this.outLock) {
					boolean idle = false;
					if (this.sendingPackages.isEmpty()
							&& this.waitingAckFragments.isEmpty()) {
						synchronized (this.inLock) {
							if (this.receivingPackages.isEmpty()) {
								idle = true;
							}
						}
					}
					synchronized (this.connectionLock) {
						switch (this.connectionState) {
						case STABLE:
							if (!idle) {
								break;
							}
							this.setConnStateNoSync(ConnectionState.CLOSE_WAIT);
							this.conn.shutdown(true);
						default:
							break CANCEL;
						}
					}
				}
				// ȡ���Ͽ�
				DataFragment fragment = this.allocDataFragment(1);
				fragment
						.writeByte((byte) (CTRL_FLAG_CLOSE | CTRL_FLAG_CLOSE_CANCEL));
				this.postDataFragmentToSend(null, fragment);
				break;
			}
			// ȷ�϶Ͽ������ڶϿ����Ӷ�����Ҫ�ȴ������߳��˳�������Ҫ�������߳���ִ��
			this.owner.application.overlappedManager.startWork(new Work() {
				@Override
				protected void doWork(WorkingThread thread) throws Throwable {
					close();
				}
			});
			break;
		case CTRL_FLAG_CLOSE_CANCEL:
			// ����
			synchronized (this.connectionLock) {
				switch (this.connectionState) {
				case CLOSE_WAIT:
					this.setConnStateNoSync(ConnectionState.STABLE);
				default:
					this.conn.shutdown(false);
					break;
				}
			}
			break;
		}
	}

	private final void onEchoFragment(DataFragment received) {
		this.postAckCtrl(received.readInt());
	}

	private final void onAckFragment(DataFragment received) {
		this.commitAck(received.readInt());
	}

	/**
	 * У��ack������ack�������Ƴ�
	 * 
	 * @param ackID
	 */
	private final void commitAck(int ackID) {
		ArrayList<IAckRequiredFragmentBuilder> arr = null;
		synchronized (this.outLock) {
			for (;;) {
				AckRequiredFragmentStub stub = this.waitingAckFragments.peek();
				if (stub == null) {
					break;
				}
				if (stub.ackID < ackID) {
					// �Ƴ�
					if (arr == null) {
						arr = new ArrayList<IAckRequiredFragmentBuilder>();
					}
					arr.add(this.waitingAckFragments.poll().builder);
					DebugHelper.fault("resend ctrl [ack " + stub.ackID + "]");
					continue;
				} else if (stub.ackID == ackID) {
					// �Ƴ�
					this.waitingAckFragments.poll();
				}
				break;
			}
		}
		if (arr != null) {
			if (arr.size() > 1) {
				int c = arr.size();
				IAckRequiredFragmentBuilder[] otherBuilders = new IAckRequiredFragmentBuilder[c - 1];
				for (int i = 1; i < c; i++) {
					otherBuilders[i - 1] = arr.get(i);
				}
				this.postAckReqiredFragmentToSend(arr.get(0), otherBuilders);
			} else {
				this.postAckReqiredFragmentToSend(arr.get(0));
			}
		}
	}

	final void breakReceive(int packageID) {
		DebugHelper.fault("break receive " + packageID);
		final NetPackageReceivingEntry<?> rpe;
		synchronized (this.inLock) {
			rpe = this.receivingPackages.remove(packageID);
		}
		if (rpe != null) {
			rpe.breakResolve();
		}
	}

	final void breakSend(int packageID) {
		DebugHelper.fault("break send " + packageID);
		NetPackageSendingEntry<?> spe;
		synchronized (this.outLock) {
			spe = this.sendingPackages.remove(packageID);
			if (spe == null) {
				return;
			}
			this.waitingBuildingPackages.remove(spe);
		}
		spe.setResolved(false);
	}

	/**
	 * ������յ�Ƭ�Σ������ػ������ɷ�����
	 * 
	 * @param received
	 * @return
	 * @throws Throwable
	 */
	protected final boolean dispatchFragment(DataFragment received)
			throws Throwable {
		DebugHelper.trace("receive ", received);
		final byte ctrlFlag = received.readByte();
		switch (ctrlFlag & CTRL_FLAG_TYPE_MASK) {
		case CTRL_FLAG_PACKAGE:
			this.onPackageFragment(ctrlFlag, received);
			break;
		case CTRL_FLAG_BREAK_PACKAGE_RECEIVE:
			this.onBreakPackageReceiveFragment(received);
			return true;
		case CTRL_FLAG_BREAK_PACKAGE_SEND:
			this.onBreakPackageSendFragment(received);
			return true;
		case CTRL_FLAG_PACKAGE_RESOLVED:
			this.onPackageResolvedFragment(received);
			return true;
		case CTRL_FLAG_RESEND_PACKAGE:
			this.onResendPackageFragment(received);
			return true;
		case CTRL_FLAG_CLOSE:
			this.onCloseFragment(ctrlFlag, received);
			return true;
		case CTRL_FLAG_KEEP_ALIVE:
			// do nothing
			return true;
		case CTRL_FLAG_ECHO:
			this.onEchoFragment(received);
			return true;
		case CTRL_FLAG_ACK:
			this.onAckFragment(received);
			return true;
		default:
			throw new IllegalStateException("�޷�ʶ�����������");
		}
		return false;
	}

	/**
	 * �����߳���ȡƬ��������<br>
	 * ������Ҫ���͵�Ƭ�ϣ�<br>
	 * �������null��ʾû����Ҫ���͵�Ƭ�ϣ�<br>
	 * ��ʱ�����߳̿��Իع����
	 * 
	 * @return ������Ҫ���͵�Ƭ�ϻ�null
	 */
	final void receiveThreadRun(InputStream in) throws Throwable {
		// ����echo��resend
		IAckRequiredFragmentBuilder builder = this.resendBuilder();
		if (builder != null) {
			this.postAckReqiredFragmentToSend(this.echoBuilder, builder);
		} else {
			this.postAckReqiredFragmentToSend(this.echoBuilder);
		}
		for (;;) {
			DataFragment received = this.allocDataFragment();
			for (;;) {
				received = this.conn.receive(in, received);
				if (!this.dispatchFragment(received)) {
					break;
				}
			}
		}
	}

	// //////////////�����߳����////////////////////////
	// /////////////////////////////////////////////////
}