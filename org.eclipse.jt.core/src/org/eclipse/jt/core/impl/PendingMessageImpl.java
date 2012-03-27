package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.situation.MessageDirection;
import org.eclipse.jt.core.situation.MessageResult;
import org.eclipse.jt.core.situation.PendingMessage;

/**
 * �ж���Ϣ
 * 
 * @author Jeff Tang
 * 
 * @param <TMessage>
 */
abstract class PendingMessageImpl<TMessage> implements PendingMessage<TMessage> {

	public final boolean isValid() {
		return this.sender != null || this.result != null;
	}

	public final MessageResult<TMessage> tryGetResult() {
		return this.result;
	}

	private TMessage message;
	private volatile SituationImpl sender;
	private final int maxDistance;
	private volatile MessageTransmitterImpl<TMessage> result;
	PendingMessageImpl<?> next;
	PendingMessageImpl<?> prev;
	PendingMessageImpl<?> nextInSituation;

	/**
	 * �崦����
	 */
	final PendingMessageImpl<?> helpGC() {
		this.message = null;
		this.sender = null;
		this.prev = null;
		this.next = null;
		PendingMessageImpl<?> nextInSituation = this.nextInSituation;
		this.nextInSituation = null;
		return nextInSituation;
	}

	abstract MessageTransmitterImpl<TMessage> handle();

	private PendingMessageImpl(SituationImpl sender, TMessage message,
	        int maxDistance) {
		if (message == null) {
			throw new NullPointerException();
		}
		if (maxDistance < 0) {
			throw new IllegalArgumentException("��Ϣ������;��������ڻ������");
		}
		this.message = message;
		this.sender = sender;
		this.maxDistance = maxDistance;
		sender.addPendingMessage(this);
	}

	final void removeSelfFromSender() {
		this.sender.removePendingMessage(this);
	}

	static <TMessage> PendingMessageImpl<TMessage> brodcast(
	        SituationImpl sender, TMessage message, int maxDistance) {
		return new PendingMessageImpl<TMessage>(sender, message, maxDistance) {
			public final MessageDirection getDirection() {
				return MessageDirection.BRODCAST;
			}

			/**
			 * �÷���ֻ��theme���߳��е���
			 */
			@Override
			final MessageTransmitterImpl<TMessage> handle() {
				try {
					return super.result = super.sender.broadcastMessage(
					        super.message, super.maxDistance);
				} finally {
					super.helpGC();
				}
			}
		};
	}

	static <TMessage> PendingMessageImpl<TMessage> bubble(SituationImpl sender,
	        TMessage message, int maxDistance) {
		return new PendingMessageImpl<TMessage>(sender, message, maxDistance) {
			public final MessageDirection getDirection() {
				return MessageDirection.BUBBLE;
			}

			/**
			 * �÷���ֻ��theme���߳��е���
			 */
			@Override
			final MessageTransmitterImpl<TMessage> handle() {
				try {
					return super.result = super.sender.bubbleMessage(
					        super.message, super.maxDistance);
				} finally {
					super.helpGC();
				}
			}
		};
	}

}
