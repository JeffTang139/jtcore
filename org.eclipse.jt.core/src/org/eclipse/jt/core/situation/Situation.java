package org.eclipse.jt.core.situation;

import org.eclipse.jt.core.Context;

/**
 * 情景
 * 
 * @author Jeff Tang
 * 
 */
public interface Situation extends Context {
	/**
	 * 获得父级情景
	 */
	public Situation getParent();

	/**
	 * 获得根级情景
	 */
	public Situation getRoot();

	/**
	 * 向自己以及所有下级发送消息，消息处理完成之后才返回。
	 * 
	 * @param message
	 *            消息对象
	 * @return 返回消息处理过程中的一些信息
	 */
	public <TMessage> MessageResult<TMessage> broadcastMessage(TMessage message);

	/**
	 * 向自己以及所有下级广播消息，消息处理完成之后才返回。
	 * 
	 * @param message
	 *            消息对象
	 * @param maxDistance
	 *            最大广播深度
	 * @return 返回消息处理过程中的一些信息
	 */
	public <TMessage> MessageResult<TMessage> broadcastMessage(
			TMessage message, int maxDistance);

	/**
	 * 向自己以及各上级依次发送消息，消息处理完成之后才返回。
	 * 
	 * @param message
	 *            消息对象
	 * @return 返回消息处理过程中的一些信息
	 */
	public <TMessage> MessageResult<TMessage> bubbleMessage(TMessage message);

	/**
	 * 向自己以及各上级依次发送消息，消息处理完成之后才返回。
	 * 
	 * @param message
	 *            消息对象
	 * @param maxDistance
	 *            最大冒泡高度
	 * @return 返回消息处理过程中的一些信息
	 */
	public <TMessage> MessageResult<TMessage> bubbleMessage(TMessage message,
			int maxDistance);

	/**
	 * 提交异步消息，消息放到队列结尾，等待情景对象下次响应
	 */
	public <TMessage> PendingMessage<TMessage> postBroadcastMessage(
			TMessage message, int maxDistance);

	/**
	 * 提交异步消息，消息放到队列结尾，等待情景对象下次响应
	 */
	public <TMessage> PendingMessage<TMessage> postBroadcastMessage(
			TMessage message);

	/**
	 * 提交异步消息，消息放到队列结尾，等待情景对象下次响应
	 */
	public <TMessage> PendingMessage<TMessage> postBubbleMessage(
			TMessage message, int maxDistance);

	/**
	 * 提交异步消息，消息放到队列结尾，等待情景对象下次响应
	 */
	public <TMessage> PendingMessage<TMessage> postBubbleMessage(
			TMessage message);

	/**
	 * 声明监听某消息
	 * 
	 * @param <TMessage>
	 *            消息类型
	 * @param messageClass
	 *            消息的类
	 * @param listener
	 *            监听器
	 * @param directions
	 *            监听消息的方向，不指定代表监听各种方向
	 */
	public <TMessage> MessageListenerRegHandle<TMessage> regMessageListener(
			Class<TMessage> messageClass,
			MessageListener<? super TMessage> listener);
}
