package org.eclipse.jt.core.situation;

/**
 * ÏûÏ¢¼àÌıÆ÷
 * 
 * @author Jeff Tang
 * 
 */
public interface MessageListener<TMessage> {
	public void onMessage(Situation context, TMessage message,
			MessageTransmitter<TMessage> transmitter);
}
