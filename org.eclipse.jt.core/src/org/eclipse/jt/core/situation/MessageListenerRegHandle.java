package org.eclipse.jt.core.situation;

/**
 * 消息监听器注册句柄
 * 
 * @author Jeff Tang
 * 
 */
public interface MessageListenerRegHandle<TMessage> {
	/**
	 * 获取注册的消息监听器
	 */
	public MessageListener<? super TMessage> getListener();

	/**
	 * 获取消息类型
	 */
	public Class<TMessage> getMessageClass();

	/**
	 * 获得有效性，默认为true
	 */
	public boolean isEnabled();

	/**
	 * 设置有效性，默认为true
	 * 
	 * @param value
	 *            监听器是否生效
	 */
	public void setEnabled(boolean value);

	/**
	 * 是否监听注册消息类型的子类型，默认为false
	 */
	public boolean isListenSubMessage();

	/**
	 * 设置是否监听注册消息类型的子类型，默认为false
	 */
	public void setListenSubMessage(boolean value);

	/**
	 * 是否监听冒泡消息，默认为true
	 */
	public boolean isListenBubble();

	/**
	 * 设置是否监听冒泡（向上级发送）消息，默认为true
	 */
	public void setListenBubble(boolean value);

	/**
	 * 是否监听广播（向下级发送）消息，默认为true
	 */
	public boolean isListenBroadcast();

	/**
	 * 设置是否监听广播（向下级发送）消息，默认为true
	 */
	public void setListenBroadcast(boolean value);

	/**
	 * 注销该监听器
	 */
	public void unRegister();

	/**
	 * 返回是否是注册状态
	 */
	public boolean isRegistered();

	/**
	 * 获得所有者,默认是注册到的情景对象,所有者关闭时会注销拥有的监听器，注销后返回null
	 */
	public Situation getOwner();

	/**
	 * 设置所有者,便于在所有者关闭时注销监听器
	 */
	public void setOwner(Situation owner);

	/**
	 * 获得监听器注册到的情景，注销后返回null
	 * 
	 * @return
	 */
	public Situation getSituation();
}
