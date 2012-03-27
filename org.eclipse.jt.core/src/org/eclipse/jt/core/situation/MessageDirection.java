package org.eclipse.jt.core.situation;

/**
 * 消息方向
 * 
 * @author Jeff Tang
 * 
 */
public enum MessageDirection {
	/**
	 * 向父极情景发送消息
	 */
	BRODCAST,
	/**
	 * 向子级情景发送消息
	 */
	BUBBLE
}
