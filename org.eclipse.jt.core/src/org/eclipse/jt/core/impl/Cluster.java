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
 * 这里的集群只是一个逻辑上的概念，当一组服务器共同搭建并提供相同的服务时，我们称之为一个集群，每一台服务器都是这个集群中的一个节点。
 * 每个集群中都有一个主节点和若干从节点。节点的总个数不超过16个（其中最多只有15个用于部署应用程序，另有一台机器作数据库服务器）。
 * 当只有一台服务器工作时，我们也把它看作是工作在一个集群中，只不过这个集群中只有这一个节点而已，并且是这个集群的主节点。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class Cluster extends NetManagerBased {
	public static final int DEFAULT_MASTER_INDEX = 1;
	public static final int MAX_NODE_COUNT = 16 - 1;

	private static final NetNodeInfo[] EMPTY = {};

	/**
	 * 集群对象的唯一标识。
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
	 * 添加集群节点信息。
	 * 
	 * @param clusterNodeInfo
	 *            集群节点信息。
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
				// FIXME 单凭这一点判断可能不够，如果连接建立了，最好验证主机ID。
				if (clusterNodeInfo.hasSameTarget(cni)) {
					throw new UnsupportedOperationException("节点信息冲突");
				}
			}
		}
		if (len >= MAX_NODE_COUNT - 1) {
			throw new UnsupportedOperationException("提供的节点信息超出了集群所支持的个数");
		}
		NetNodeInfo[] newTable = new NetNodeInfo[len + 1];
		System.arraycopy(this.nodes, 0, newTable, 0, len);
		newTable[len] = clusterNodeInfo;
		clusterNodeInfo.owner = this;
		this.nodes = newTable;
	}

	/**
	 * ！！！目前只给集群启动初始化使用，其它任何地方不能调用！！！
	 */
	synchronized void putAllNodes(NetNodeInfo[] clusterNodeInfos) {
		if (clusterNodeInfos != null) {
			final int len = clusterNodeInfos.length;
			if (len > 0) {
				Assertion.ASSERT(this.nodes.length == 1
						&& this.nodes[0] != null);
				if (len >= MAX_NODE_COUNT - 2) {
					throw new UnsupportedOperationException(
							"提供的节点信息超出了集群所支持的个数");
				}
				NetNodeInfo[] newTable = new NetNodeInfo[len + 1];
				System.arraycopy(this.nodes, 0, newTable, 0, 1);
				System.arraycopy(clusterNodeInfos, 0, newTable, 1, len);
				this.nodes = newTable;
			}
		}
	}

	/**
	 * 移除集群节点信息。
	 * 
	 * @param clusterNodeInfo
	 *            集群节点信息。
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
			// 末位为1
			this.nextId = (((int) System.nanoTime()) | 0x00000001);
		}

		final synchronized int next() {
			return this.nextId += 2;
		}
	}

	/**
	 * 提交请求。<br/>
	 * 该请求是一个远程请求，并且是集群性质的。会把请求数据发送到本节点所在集群中的每一个节点（本节点除外）<br/>
	 * 该请求只接收资源信息类型的数据。
	 * 
	 * @param resourceInfo
	 *            要在集群中发送的资源信息。
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
						// 如果任务失败，应该考虑是否是由于连接问题造成的，
						// 如果确实是连接问题造成的，则踢除相关的节点。
						throw Utils.tryThrowException(exps[i]);
					}
				}
			}
		}
	}

	/**
	 * 实际的数据发送过程。
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
									// XXX 目前的实现即使连接断开在这里也不会有异常抛出。
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
						// 如果任务失败，应该考虑是否是由于连接问题造成的，
						// 如果确实是连接问题造成的，则踢除相关的节点。
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
