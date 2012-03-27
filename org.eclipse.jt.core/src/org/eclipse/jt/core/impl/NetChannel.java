package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.type.GUID;

/**
 * 网络连接接口
 * 
 * @author Jeff Tang
 * 
 */
public interface NetChannel {
	/**
	 * 获得远程节点ID
	 */
	public GUID getRemoteNodeID();

	/**
	 * 获得远程节点的集群序号
	 */
	public int getRemoteNodeClusterIndex();

	/**
	 * 获得远程应用ID，一个集群内部的全部节点共用一个AppID;
	 */
	public GUID getRemoteAppID();

	/**
	 * 获取远端序列化器的版本号
	 */
	public short getRemoteSerializeVersion();

	/**
	 * 获取远程应用的实例版本
	 */
	public long getRemoteAppInstanceVersion();

	/**
	 * 开始发送数据
	 * 
	 * @param <TAttachment>
	 *            附件类型
	 * @param handler
	 *            发送处理器
	 * @param attachment
	 *            附件
	 * @return 返回异步控制句柄
	 */
	public <TAttachment> AsyncIOStub<TAttachment> startSendingPackage(
			DataFragmentBuilder<? super TAttachment> builder,
			TAttachment attachment);

}
