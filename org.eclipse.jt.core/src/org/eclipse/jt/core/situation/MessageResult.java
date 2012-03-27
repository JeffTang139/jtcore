package org.eclipse.jt.core.situation;

/**
 * 消息结果
 * 
 * @author Jeff Tang
 * 
 * @param <TMessage>
 */
public interface MessageResult<TMessage> {

	/**
	 * 获得当前消息对象
	 */
	public TMessage getMessage();

	/**
	 * 获取该消息此次传输传递到当前监听器前已经被监听（处理）的次数
	 */
	public int getListeneds();

	/**
	 * 是否被中止
	 */
	public boolean isTerminated();
}
