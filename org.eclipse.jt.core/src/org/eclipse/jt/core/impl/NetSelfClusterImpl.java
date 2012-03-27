package org.eclipse.jt.core.impl;

import java.net.URL;
import java.util.ArrayList;

import org.eclipse.jt.core.None;
import org.eclipse.jt.core.invoke.AsyncTask;
import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.type.GUID;


public class NetSelfClusterImpl extends NetClusterImpl {
	final int thisClusterNodeIndex;

	NetNodeImpl createNode(NetNodeManagerImpl owner, NetChannelImpl channel) {
		channel.setKeepAlive(true);
		return super.createNode(owner, channel);
	}

	private final SXElement config;

	@SuppressWarnings("unchecked")
	final void initClusterNodes() {
		if (this.config != null) {
			System.out.println("��ʼ��ʼ����Ⱥ�ڵ�");
			final ArrayList<AsyncTask<NClusterNodeDetectTask, ?>> taskList = new ArrayList<AsyncTask<NClusterNodeDetectTask, ?>>();
			for (SXElement config : this.config.getChildren(xml_element_node)) {
				final String address = config.getAttribute(xml_attr_url);
				if (address != null && address.length() > 0) {
					NetNodeImpl netNode;
					NetSessionImpl netSession;
					AsyncTask<NClusterNodeDetectTask, ?> taskHandle;
					try {
						final URL url = new URL(address);
						netNode = application.netNodeManager.getNetNode(url);
						netSession = netNode.newSession();
						taskHandle = netSession.newRequest(
								new NClusterNodeDetectTask(url), None.NONE);
						taskList.add(taskHandle);
					} catch (Throwable e) {
						System.err.println("�������ӵ���Ⱥ�ڵ�[" + address + "]\n" + e);
					}
				}
			}
			if (taskList.size() != 0) {
				try {
					final AsyncTask[] tasks = taskList
							.toArray(new AsyncTask[taskList.size()]);
					ContextImpl.internalWaitFor(0L, null, tasks);
					this.notifyClusterNodeJoin();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
			System.out.println("������ʼ����Ⱥ�ڵ�");
		}
	}

	final static String xml_attr_id = "id";
	final static String xml_element_cluster = "cluster";
	final static String xml_element_node = "node";
	final static String xml_attr_index = "index";
	final static String xml_attr_url = "url";

	private static GUID getAppID(SXElement config) {
		if (config == null) {
			return GUID.randomID();
		}
		return config.getGUID(xml_attr_id, GUID.randomID());

	}

	final ApplicationImpl application;

	NetSelfClusterImpl(ApplicationImpl application, SXElement config) {
		super(getAppID(config));
		this.application = application;
		this.config = config;
		final int cni;
		if (config != null) {
			cni = config.getInt(xml_attr_index,
					NetClusterImpl.DEFAULT_MASTER_INDEX);
			NetClusterImpl.checkNetNodeIndex(cni);
		} else {
			cni = NetClusterImpl.DEFAULT_MASTER_INDEX;
		}
		this.thisClusterNodeIndex = cni;
	}

	final void onClusterNodeJoin(final int nodeIndex) {
		for (SXElement config : this.config.getChildren(xml_element_node)) {
			final int index = config.getInt(xml_attr_index);
			if (index == nodeIndex) {
				final String urlString = config.getAttribute(xml_attr_url);
				try {
					final URL url = new URL(urlString);
					application.netNodeManager.getNetNode(url);
				} catch (Throwable e) {
					System.err.println("�������ӵ���Ⱥ�ڵ�[" + urlString + "]\n" + e);
				}
			}
		}
	}

	private final void notifyClusterNodeJoin() {
		// ���ͼ�������
		NetNodeImpl netNode = this.getFirstNetNode();
		final ArrayList<AsyncTask<NClusterNodeJoinTask, None>> taskList = new ArrayList<AsyncTask<NClusterNodeJoinTask, None>>();
		do {
			NClusterNodeJoinTask task = new NClusterNodeJoinTask(this.thisClusterNodeIndex);
			NetSessionImpl netSession = netNode.newSession();
			taskList.add(netSession.newRequest(task, None.NONE));
			netNode = netNode.getNextNodeInCluster();
		} while (netNode != null);
		// �ȴ���Ӧ
		try {
			ContextImpl.internalWaitFor(0L, null, taskList
					.toArray(new AsyncTask[taskList.size()]));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
