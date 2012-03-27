package org.eclipse.jt.core.impl;

import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.type.GUID;


/**
 * 网络节点管理器
 * 
 * @author Jeff Tang
 * 
 */
public class NetNodeManagerImpl {
	final NetSelfClusterImpl thisCluster;
	final ApplicationImpl application;
	final NetChannelManagerImpl netChannelManager;
	private HashMap<NetChannel, NetNodeImpl> nodes = new HashMap<NetChannel, NetNodeImpl>();
	private HashMap<GUID, NetClusterImpl> clusters = new HashMap<GUID, NetClusterImpl>();

	private final NetClusterImpl ensureCluster(GUID appID) {
		if (appID == null) {
			throw new NullArgumentException("appID");
		}
		if (appID.equals(this.thisCluster.appID)) {
			return this.thisCluster;
		}
		synchronized (this.clusters) {
			NetClusterImpl cluster = this.clusters.get(appID);
			if (cluster == null) {
				cluster = new NetClusterImpl(appID);
				this.clusters.put(appID, cluster);
			}
			return cluster;
		}
	}

	private final NetNodeImpl ensureNetNode(NetChannel channel) {
		if (channel == null) {
			throw new NullArgumentException("channel");
		}
		final GUID appID = channel.getRemoteAppID();
		synchronized (this.nodes) {
			NetNodeImpl node = this.nodes.get(channel);
			if (node == null) {
				node = this.ensureCluster(appID).createNode(this,
						(NetChannelImpl) channel);
				this.nodes.put(channel, node);
			}
			return node;
		}
	}

	public final NetNodeImpl getNetNode(URL address) {
		return this.ensureNetNode(this.netChannelManager.getChannel(address));
	}

	public final NetNodeImpl getNetNode(NetChannel channel) {
		return this.ensureNetNode(channel);
	}

	private final AtomicInteger requestIDSeed = new AtomicInteger();

	final int newRequestID() {
		return this.requestIDSeed.incrementAndGet();
	}

	private final void onChannelDisabled(NetChannel channel) {
		NetNodeImpl node;
		synchronized (this.nodes) {
			// 从manager上移除node
			node = this.nodes.remove(channel);
			if (node == null) {
				return;
			}
			// 从cluster上移除node
			NetClusterImpl cluster = node.cluster;
			cluster.removeNode(node);
			// 从manager上移除cluster
			if (!cluster.haveRemoteNode()) {
				synchronized (this.clusters) {
					this.clusters.remove(cluster.appID);
				}
			}
		}
		// 销毁node
		node.dispose(null);
	}

	public NetNodeManagerImpl(NetChannelManagerImpl netChannelManager,
			NetSelfClusterImpl thisCluster, SXElement config) {
		if (netChannelManager == null) {
			throw new NullArgumentException("netChannelManager");
		}
		if (thisCluster == null) {
			throw new NullArgumentException("thisCluster");
		}
		this.application = netChannelManager.application;
		this.thisCluster = thisCluster;
		this.netChannelManager = netChannelManager;
		this.netChannelManager.setNetIOHandler(new DataPackageReceiver() {
			public void channelDisabled(NetChannel channel) {
				NetNodeManagerImpl.this.onChannelDisabled(channel);
			}

			public void packageArriving(NetChannel channel,
					DataInputFragment fragment,
					NetPackageReceivingStarter starter) throws Throwable {
				switch (fragment.readByte()) {
				case NetRequestImpl.REQUEST_PACKAGE:
					NetNodeManagerImpl.this.ensureNetNode(channel)
							.onRequestPackageArriving(fragment, starter);
					break;
				case NetNodeImpl.TYPE_PACKAGE:
					NetNodeManagerImpl.this.ensureNetNode(channel)
							.onTypePackageArriving(starter);
					break;
				}
			}
		});
		this.clusters.put(this.thisCluster.appID, this.thisCluster);
	}

	// ===========================以下集群相关=============================================

	final static String xml_element_net = "net";

	// ===========================以上集群相关=============================================

}
