/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File Cluster.java
 * Date May 4, 2009
 */
package org.eclipse.jt.core.impl;

import java.io.IOException;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.impl.ByteBufferPool.ByteBufferWrapper;
import org.eclipse.jt.core.type.GUID;


/**
 * ����ļ�Ⱥֻ��һ���߼��ϵĸ����һ���������ͬ����ṩ��ͬ�ķ���ʱ�����ǳ�֮Ϊһ����Ⱥ��ÿһ̨���������������Ⱥ�е�һ���ڵ㡣
 * ÿ����Ⱥ�ж���һ�����ڵ�����ɴӽڵ㡣�ڵ���ܸ���������16�����������ֻ��15�����ڲ���Ӧ�ó�������һ̨���������ݿ����������
 * ��ֻ��һ̨����������ʱ������Ҳ���������ǹ�����һ����Ⱥ�У�ֻ���������Ⱥ��ֻ����һ���ڵ���ѣ������������Ⱥ�����ڵ㡣
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class Cluster extends NetManagerBased {
	public static final int DEFAULT_MASTER_INDEX = 1;
	public static final int MAX_NODE_COUNT = 16 - 1;

	private static final NetNodeInfo[] EMPTY = {};

	/**
	 * ��Ⱥ�����Ψһ��ʶ��
	 */
	final GUID id;

	private NetNodeInfo[] nodes = EMPTY;

	Cluster(NetManager manager, GUID id) {
		super(manager);
		if (id == null) {
			throw new NullArgumentException("id");
		}
		this.id = id;
	}

	/**
	 * ��Ӽ�Ⱥ�ڵ���Ϣ��
	 * 
	 * @param clusterNodeInfo
	 *            ��Ⱥ�ڵ���Ϣ��
	 */
	synchronized void putNode(NetNodeInfo clusterNodeInfo) {
		if (clusterNodeInfo == null) {
			throw new NullArgumentException("clusterNodeInfo");
		}
		if (this.nodes == null) {
			this.nodes = new NetNodeInfo[1];
		}
		final int len = this.nodes.length;
		NetNodeInfo cni = null;
		for (int i = 0; i < len; i++) {
			cni = this.nodes[i];
			Assertion.ASSERT(cni != null);
			if (cni == clusterNodeInfo) {
				return;
			} else {
				// FIXME ��ƾ��һ���жϿ��ܲ�����������ӽ����ˣ������֤����ID��
				if (clusterNodeInfo.hasSameTarget(cni)) {
					throw new UnsupportedOperationException("�ڵ���Ϣ��ͻ");
				}
			}
		}
		if (len >= MAX_NODE_COUNT - 1) {
			throw new UnsupportedOperationException("�ṩ�Ľڵ���Ϣ�����˼�Ⱥ��֧�ֵĸ���");
		}
		NetNodeInfo[] newTable = new NetNodeInfo[len + 1];
		System.arraycopy(this.nodes, 0, newTable, 0, len);
		newTable[len] = clusterNodeInfo;
		clusterNodeInfo.owner = this;
		this.nodes = newTable;
	}

	/**
	 * ������Ŀǰֻ����Ⱥ������ʼ��ʹ�ã������κεط����ܵ��ã�����
	 */
	synchronized void putAllNodes(NetNodeInfo[] clusterNodeInfos) {
		if (clusterNodeInfos != null) {
			final int len = clusterNodeInfos.length;
			if (len > 0) {
				Assertion.ASSERT(this.nodes.length == 1
						&& this.nodes[0] != null);
				if (len >= MAX_NODE_COUNT - 2) {
					throw new UnsupportedOperationException(
							"�ṩ�Ľڵ���Ϣ�����˼�Ⱥ��֧�ֵĸ���");
				}
				NetNodeInfo[] newTable = new NetNodeInfo[len + 1];
				System.arraycopy(this.nodes, 0, newTable, 0, 1);
				System.arraycopy(clusterNodeInfos, 0, newTable, 1, len);
				this.nodes = newTable;
			}
		}
	}

	/**
	 * �Ƴ���Ⱥ�ڵ���Ϣ��
	 * 
	 * @param clusterNodeInfo
	 *            ��Ⱥ�ڵ���Ϣ��
	 */
	synchronized void removeNode(NetNodeInfo clusterNodeInfo) {
		if (clusterNodeInfo != null && this.nodes != null) {
			for (int i = 0, len = this.nodes.length; i < len; i++) {
				if (this.nodes[i] == clusterNodeInfo) {
					NetNodeInfo[] newTable = new NetNodeInfo[len - 1];
					System.arraycopy(this.nodes, 0, newTable, 0, i);
					System.arraycopy(this.nodes, i + 1, newTable, i, len - i
							- 1);
					this.nodes = newTable;
					clusterNodeInfo.owner = null;
					return;
				}
			}
		}
	}

	private StructAdapterSet saSet;
	private final Object SASET_LOCK = new Object();

	final StructAdapterSet getStructAdapterSet() {
		synchronized (this.SASET_LOCK) {
			if (this.saSet == null) {
				this.saSet = new StructAdapterSet(this.netManager.application);
			}
			return this.saSet;
		}
	}

	// /////////////////////////////////////////////////////////////////////////

	private final IDGen stubIdGen = new IDGen();

	final int nextStubId() {
		return this.stubIdGen.next();
	}

	private static final class IDGen {
		private int nextId;

		IDGen() {
			// ĩλΪ1
			this.nextId = (((int) System.nanoTime()) | 0x00000001);
		}

		final synchronized int next() {
			return this.nextId += 2;
		}
	}

	/**
	 * �ύ����<br/>
	 * ��������һ��Զ�����󣬲����Ǽ�Ⱥ���ʵġ�����������ݷ��͵����ڵ����ڼ�Ⱥ�е�ÿһ���ڵ㣨���ڵ���⣩<br/>
	 * ������ֻ������Դ��Ϣ���͵����ݡ�
	 * 
	 * @param resourceInfo
	 *            Ҫ�ڼ�Ⱥ�з��͵���Դ��Ϣ��
	 */
	final void postRequest(AbstractClusterResInfo resourceInfo) {
		NetNodeInfo[] nodes;
		synchronized (this) {
			nodes = this.nodes.clone();
		}
		final int count = nodes.length;
		if (count == 0) {
			return;
		}

		final int requestId = this.stubIdGen.next();

		final ClusterNodeRequestStubImpl[] stubs = new ClusterNodeRequestStubImpl[count];
		NetConnection nc;

		boolean flag = false;

		for (int i = 0; i < count; i++) {
			try {
				nc = nodes[i].ensureGetConnection();
			} catch (Exception e) {
				// TODO delete cluster node.
				ConsoleLog.debugError("%s", e);
				continue;
			}
			stubs[i] = nc.postRequest(resourceInfo);
			flag = true;
		}

		if (flag) {
			Throwable[] exps = new Throwable[count];
			exps = writeDataOut(requestId, resourceInfo, this.netManager,
					stubs, exps);

			flag = false;
			ClusterNodeRequestStubImpl stub;
			for (int i = 0; i < count; i++) {
				stub = stubs[i];
				if (stub != null) {
					Assertion.ASSERT(exps[i] != null);
					exps[i] = null;
					try {
						nc = nodes[i].ensureGetConnection();
					} catch (Exception e) {
						stubs[i] = null;

						// TODO delete cluster node.
						ConsoleLog.debugError("%s", e);
						continue;
					}
					stubs[i] = nc.postRequest(resourceInfo);
					flag = true;
				}
			}

			if (flag) {
				exps = writeDataOut(requestId, resourceInfo, this.netManager,
						stubs, exps);
				for (int i = 0; i < count; i++) {
					if (exps[i] != null) {
						// TODO delete cluster node.
						// �������ʧ�ܣ�Ӧ�ÿ����Ƿ�����������������ɵģ�
						// ���ȷʵ������������ɵģ����߳���صĽڵ㡣
						throw Utils.tryThrowException(exps[i]);
					}
				}
			}
		}
	}

	/**
	 * ʵ�ʵ����ݷ��͹��̡�
	 */
	private static <T extends ClusterNodeRequestStubImpl> Throwable[] writeDataOut(
			int requestId, RemoteRequest<T> request,
			ByteBufferManager byteBufferManager, final T[] stubs,
			Throwable[] exps) {
		final int count = stubs.length;
		try {
			request.writeTo(new SOSerializer(new ByteBufferSerializer(
					new ByteBufferSender() {
						public void toSend(ByteBufferWrapper src) {
							ClusterNodeRequestStubImpl stub;
							for (int i = 0; i < count; i++) {
								stub = stubs[i];
								if (stub != null) {
									// XXX Ŀǰ��ʵ�ּ�ʹ���ӶϿ�������Ҳ�������쳣�׳���
									stub.sendData(src);
								}
							}
						}
					}, byteBufferManager, request.getPacketCode(), requestId)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StructDefineNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (exps == null || exps.length < count) {
			exps = new Throwable[count];
		}
		ClusterNodeRequestStubImpl stub;
		for (int i = 0; i < count; i++) {
			stub = stubs[i];
			if (stub != null) {
				try {
					stub.syncWork();
					exps[i] = stub.getException();
				} catch (Throwable e) {
					exps[i] = e;
				}
				if (exps[i] == null) {
					stubs[i] = null;
				}
			}
		}

		return exps;
	}

	final void postLockRequest(AbstractClusterLockInfo lockInfo,
			NewAcquirer<?, ?> localLock) {
		NetNodeInfo[] nodes;
		synchronized (this) {
			nodes = this.nodes.clone();
		}
		final int count = nodes.length;
		if (count == 0) {
			return;
		}

		final int requestId = this.stubIdGen.next();

		final ClusterNodeLockRequestStubImpl[] stubs = new ClusterNodeLockRequestStubImpl[count];
		NetConnection nc;

		boolean flag = false;

		for (int i = 0; i < count; i++) {
			try {
				nc = nodes[i].ensureGetConnection();
			} catch (Exception e) {
				// TODO delete cluster node.
				e.printStackTrace();
				continue;
			}
			stubs[i] = nc.postLockRequest(lockInfo, localLock);
			flag = true;
		}

		if (flag) {
			Throwable[] exps = new Throwable[count];
			exps = writeDataOut(requestId, lockInfo, this.netManager, stubs,
					exps);

			flag = false;
			ClusterNodeRequestStubImpl stub;
			for (int i = 0; i < count; i++) {
				stub = stubs[i];
				if (stub != null) {
					Assertion.ASSERT(exps[i] != null);
					exps[i] = null;
					try {
						nc = nodes[i].ensureGetConnection();
					} catch (Exception e) {
						stubs[i] = null;

						// TODO delete cluster node.
						e.printStackTrace();
						continue;
					}
					stubs[i] = nc.postLockRequest(lockInfo, localLock);
					flag = true;
				}
			}

			if (flag) {
				exps = writeDataOut(requestId, lockInfo, this.netManager,
						stubs, exps);
				for (int i = 0; i < count; i++) {
					if (exps[i] != null) {
						// TODO delete cluster node.
						// �������ʧ�ܣ�Ӧ�ÿ����Ƿ�����������������ɵģ�
						// ���ȷʵ������������ɵģ����߳���صĽڵ㡣
						throw Utils.tryThrowException(exps[i]);
					}
				}
			}
		}
	}

	final boolean haveRemoteNode() {
		return false;
	}

}
