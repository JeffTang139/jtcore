package org.eclipse.jt.core.situation;

/**
 * 消息传递控制器
 * 
 * @author Jeff Tang
 * 
 * @param <TMessage>
 */
public interface MessageTransmitter<TMessage> extends MessageResult<TMessage> {
	/**
	 * 获得消息的发送者的情景
	 */
	public Situation getSender();

	/**
	 * 获取当前情景（上下文）
	 */
	public Situation getContext();

	/**
	 * 获得当前消息的传递方向
	 */
	public MessageDirection getDirection();

	/**
	 * 获得监听器注册句柄，用以改变监听设置
	 */
	public MessageListenerRegHandle<TMessage> getRegHandle();

	/**
	 * 获得当前情景与消息的发送情景间的距离(与发送者之间)
	 */
	public int getDistance();

	/**
	 * 获取消息允许传播到的最远距离（与发送者之间）
	 */
	public int getMaxDistance();

	/**
	 * 设置消息允许传播到的最远距离（与发送者之间）
	 */
	public void setMaxDistance(int value);

	/**
	 * 中止当前消息的继续传递（将传播最远距离限制设为<0）
	 */
	public void terminate();
}
