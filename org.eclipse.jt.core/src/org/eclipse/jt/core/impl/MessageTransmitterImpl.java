package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.situation.MessageDirection;
import org.eclipse.jt.core.situation.MessageTransmitter;

/**
 * ÏûÏ¢´«ÊäÆ÷
 * 
 * @author Jeff Tang
 * 
 * @param <TMessage>
 */
abstract class MessageTransmitterImpl<TMessage> implements
		MessageTransmitter<TMessage> {
	int listeneds;
	int distance;
	int maxDistance;
	SituationImpl context;
	MessageListenerEntry<TMessage> entry;
	SituationImpl sender;
	final TMessage message;

	final void helpGC() {
		this.context = null;
		this.entry = null;
		this.sender = null;
	}

	static <TMessage> MessageTransmitterImpl<TMessage> brodcast(
			SituationImpl sender, TMessage message, int maxDistance) {
		return new MessageTransmitterImpl<TMessage>(sender, message,
				maxDistance) {
			public final MessageDirection getDirection() {
				return MessageDirection.BRODCAST;
			}
		};
	}

	static <TMessage> MessageTransmitterImpl<TMessage> bubble(
			SituationImpl sender, TMessage message, int maxDistance) {
		return new MessageTransmitterImpl<TMessage>(sender, message,
				maxDistance) {
			public final MessageDirection getDirection() {
				return MessageDirection.BUBBLE;
			}
		};
	}

	private MessageTransmitterImpl(SituationImpl sender, TMessage message,
			int maxDistance) {
		this.sender = sender;
		this.message = message;
		this.maxDistance = maxDistance;
	}

	public final SituationImpl getContext() {
		return this.context;
	}

	public final int getListeneds() {
		return this.listeneds;
	}

	public final int getDistance() {
		return this.distance;
	}

	public final TMessage getMessage() {
		return this.message;
	}

	public final MessageListenerEntry<TMessage> getRegHandle() {
		return this.entry;
	}

	public final SituationImpl getSender() {
		return this.sender;
	}

	public final void terminate() {
		this.maxDistance = -1;
	}

	public final int getMaxDistance() {
		return this.maxDistance;
	}

	public final boolean isTerminated() {
		return this.maxDistance < 0;
	}

	public final void setMaxDistance(int value) {
		this.maxDistance = value;
	}
}
