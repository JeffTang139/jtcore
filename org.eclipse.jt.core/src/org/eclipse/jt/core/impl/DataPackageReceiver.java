package org.eclipse.jt.core.impl;

/**
 * 网络IO处理器
 * 
 * @author Jeff Tang
 * 
 * @param <TPackageStub>包的存根
 */
public interface DataPackageReceiver {

	/**
	 * 数据接收启动器
	 * 
	 * @author Jeff Tang
	 * 
	 */
	public interface NetPackageReceivingStarter {
		/**
		 * 启动接受package，否则该package将被取消接受
		 * 
		 * @param <TAttachment>
		 *            数据接收处理器的附件类型
		 * @param handler
		 *            数据接收处理器
		 * @param attachment
		 *            数据接收处理器的附件
		 * @return 返回异步控制句柄
		 */
		public <TAttachment> AsyncIOStub<TAttachment> startReceivingPackage(
				DataFragmentResolver<? super TAttachment> handler,
				TAttachment attachment);
	}

	/**
	 * 
	 * 当接受到了数据后，消化buffer
	 * 
	 * @param channel
	 *            连接对象
	 * @param starter
	 *            数据接收启动器
	 * @throws Throwable
	 *             可抛出异常
	 */
	public void packageArriving(NetChannel channel, DataInputFragment fragment,
			NetPackageReceivingStarter starter) throws Throwable;

	public void channelDisabled(NetChannel channel);
}
