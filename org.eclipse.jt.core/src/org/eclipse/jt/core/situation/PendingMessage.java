package org.eclipse.jt.core.situation;

/**
 * 列队消息
 * 
 * @author Jeff Tang
 * 
 * @param <TMessage>
 */
public interface PendingMessage<TMessage> {
	/**
	 * 获取该列队消息是否有效
	 */
	public boolean isValid();

	/**
	 * 获得方向
	 */
	public MessageDirection getDirection();

	/**
	 * 尝试得到结果
	 * 
	 * @return
	 */
	public MessageResult<TMessage> tryGetResult();
}
