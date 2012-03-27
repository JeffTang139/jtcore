package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.type.GUID;


/**
 * 网络集群节点
 * 
 * @author Jeff Tang
 * 
 */
class NetClusterImpl {

	public static final int DEFAULT_MASTER_INDEX = 1;
	public static final int MIN_MASTER_INDEX = 1;
	public static final int MAX_MASTER_INDEX = 15;
	public static final int MAX_NODE_COUNT = MAX_MASTER_INDEX
			- MIN_MASTER_INDEX + 1;

	final GUID appID;
	private NetNodeImpl first;

	NetClusterImpl(GUID appID) {
		if (appID == null) {
			throw new NullArgumentException("appID");
		}
		this.appID = appID;
	}

	final NetNodeImpl getFirstNetNode() {
		return this.first;
	}

	NetNodeImpl createNode(NetNodeManagerImpl owner, NetChannelImpl channel) {
		return this.first = new NetNodeImpl(owner, this, channel, this.first);
	}

	final void removeNode(NetNodeImpl node) {
		if (node == null) {
			throw new NullArgumentException("node");
		}
		if (node.cluster != this) {
			throw new IllegalArgumentException();
		}
		if (node == this.first) {
			this.first = node.getNextNodeInCluster();
		} else {
			for (NetNodeImpl n = this.first; n != null; n = n
					.getNextNodeInCluster()) {
				if (n.getNextNodeInCluster() == node) {
					n.setNextNodeInCluster(node.getNextNodeInCluster());
					break;
				}
			}
		}
	}

	final boolean haveRemoteNode() {
		return this.first != null;
	}

	static final void checkNetNodeIndex(final int index) {
		if (index < NetClusterImpl.MIN_MASTER_INDEX
				|| index > NetClusterImpl.MAX_MASTER_INDEX) {
			throw new UnsupportedOperationException("集群节点索引号[" + index
					+ "]有误。[" + NetClusterImpl.MIN_MASTER_INDEX + ".."
					+ NetClusterImpl.MAX_MASTER_INDEX + "]");
		}
	}

}
