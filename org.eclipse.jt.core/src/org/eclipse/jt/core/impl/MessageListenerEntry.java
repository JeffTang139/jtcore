package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.situation.MessageDirection;
import org.eclipse.jt.core.situation.MessageListener;
import org.eclipse.jt.core.situation.MessageListenerRegHandle;
import org.eclipse.jt.core.situation.Situation;

/**
 * 消息监听器注册项
 * 
 * @author Jeff Tang
 * 
 * @param <TMessage>
 */
final class MessageListenerEntry<TMessage> implements
        MessageListenerRegHandle<TMessage> {
	private MessageListener<? super TMessage> listener;
	private SituationImpl situation;
	private SituationImpl owner;
	private MessageListenerEntry<TMessage> nextListener;
	private MessageListenerEntry<?> nextMessage;
	private MessageListenerEntry<?> nextOwn;

	final Class<TMessage> messageClass;

	private boolean disabled;
	private boolean disableListenBroadcast;
	private boolean disableListenBubble;
	private boolean listenSubMessage;

	private final void unOwn() {
		MessageListenerEntry<?> ownerOwns = this.owner.owns;
		if (ownerOwns == this) {
			this.owner.owns = this.nextOwn;
		} else {
			while (ownerOwns != null) {
				MessageListenerEntry<?> next = ownerOwns.nextOwn;
				if (next == this) {
					ownerOwns.nextOwn = this.nextOwn;
					break;
				}
				ownerOwns = next;
			}
		}
		this.nextOwn = null;
		this.owner = this.situation;
	}

	/**
	 * 清理自己以及链表的所有后续
	 */
	final void releaseInChain() {
		MessageListenerEntry<?> e = this;
		do {
			MessageListenerEntry<?> e2 = e;
			do {
				if (e2.situation != e2.owner) {
					e2.unOwn();
				}
				e2.situation = null;
				e2.owner = null;
				e2.listener = null;
				MessageListenerEntry<?> e3 = e2;
				e2 = e2.nextListener;
				e3.nextListener = null;
			} while (e2 != null);
			e2 = e;
			e = e.nextMessage;
			e2.nextMessage = null;
		} while (e != null);
	}

	/**
	 * 注销自己以及链表的所有后续
	 */
	final void unRegOwnInChain() {
		MessageListenerEntry<?> e = this;
		do {
			MessageListenerEntry<?> e2 = e.nextOwn;
			e.nextOwn = null;
			e.owner = e.situation;
			e.situation.listeners.unReg(e);
			e = e2;
		} while (e != null);
	}

	@SuppressWarnings("unchecked")
	private final void unReg(MessageListenerEntry<?> one) {
		MessageListenerEntry<?> group = this;
		MessageListenerEntry<?> last = null;
		do {
			if (group.messageClass == one.messageClass) {
				MessageListenerEntry<?> listener = group;
				do {
					if (listener == one) {
						if (last == null) {
							if (listener.nextListener != null) {
								listener.nextListener.nextMessage = listener.nextMessage;
								listener.nextMessage = listener.nextListener;
							}
							listener.situation.listeners = listener.nextMessage;
						} else if (last.messageClass != listener.messageClass) {
							if (listener.nextListener != null) {
								listener.nextListener.nextMessage = listener.nextMessage;
								listener.nextMessage = listener.nextListener;
							}
							last.nextMessage = listener.nextMessage;
						} else {
							last.nextListener = (MessageListenerEntry) listener.nextListener;
						}
						listener.nextListener = null;
						listener.nextMessage = null;
						if (listener.situation != listener.owner) {
							listener.unOwn();
						}
						listener.situation = null;
						listener.owner = null;
						listener.listener = null;
						return;
					}
					last = listener;
					listener = listener.nextListener;
				} while (listener != null);
			} else {
				last = group;
				group = group.nextMessage;
			}
		} while (group != null);
	}

	@SuppressWarnings("unchecked")
	final <TMSG> MessageListenerEntry<TMSG> add(Class<TMSG> msgClass,
	        MessageListener<? super TMSG> listener) {
		MessageListenerEntry<?> t = this;
		for (;;) {
			if (t.messageClass == msgClass) {
				while (t.nextListener != null) {
					t = t.nextListener;
				}
				MessageListenerEntry mle = new MessageListenerEntry<TMSG>(
				        msgClass, listener, this.situation);
				t.nextListener = mle;
				return mle;
			}
			if (t.nextMessage == null) {
				MessageListenerEntry entry = new MessageListenerEntry<TMSG>(
				        msgClass, listener, this.situation);
				t.nextMessage = entry;
				return entry;
			}
			t = t.nextMessage;
		}
	}

	@SuppressWarnings("unchecked")
	final void handleMessage(Class<?> messageClass,
	        MessageTransmitterImpl<?> transmitter) {
		MessageListenerEntry<?> group = this;
		do {
			MessageListenerEntry entry = group;
			int sameClass = group.messageClass == messageClass ? 1 : 0;
			do {
				// unRegIfWeak可能会注销自己因此需要预先保留下一个监听器
				final MessageListenerEntry next = entry.nextListener;
				handleMessage: {
					if (entry.disabled) {
						break handleMessage;
					}
					if (transmitter.getDirection() == MessageDirection.BRODCAST) {
						if (this.disableListenBroadcast) {
							break handleMessage;
						}
					} else if (this.disableListenBubble) {
						break handleMessage;
					}
					if (entry.listenSubMessage) {
						if (sameClass == 0) {
							if (entry.messageClass
							        .isAssignableFrom(messageClass)) {
								sameClass = -1;
							} else {
								// 类型完全不同，退出该组
								break;// do {...}while (entry != null);
							}
						}
					} else if (sameClass <= 0) {
						break handleMessage;
					}
					final MessageListener listener = entry.listener;
					if (listener != null) {
						transmitter.entry = entry;
						listener.onMessage(transmitter.context,
						        transmitter.message, transmitter);
						transmitter.listeneds++;
						if (transmitter.distance > transmitter.maxDistance) {
							return;
						}
					}
				}
				entry = next;
			} while (entry != null);
			group = group.nextMessage;
		} while (group != null);
	}

	public final MessageListener<? super TMessage> getListener() {
		return this.listener;
	}

	public final Class<TMessage> getMessageClass() {
		return this.messageClass;
	}

	public final boolean isEnabled() {
		return !this.disabled;
	}

	public final boolean isListenBroadcast() {
		return !this.disableListenBroadcast;
	}

	public final boolean isListenBubble() {
		return !this.disableListenBubble;
	}

	public final boolean isListenSubMessage() {
		return this.listenSubMessage;
	}

	public final void setListenBroadcast(boolean value) {
		this.disableListenBroadcast = !value;
	}

	public final void setListenBubble(boolean value) {
		this.disableListenBubble = !value;
	}

	public final void setListenSubMessage(boolean value) {
		this.listenSubMessage = value;
	}

	public final void setEnabled(boolean value) {
		this.disabled = !value;
	}

	public final void unRegister() {
		if (this.situation != null) {
			this.situation.usingSituation();
			this.situation.listeners.unReg(this);
		}
	}

	/**
	 * 返回是否是注册状态
	 */
	public final boolean isRegistered() {
		return this.situation != null;
	}

	public final SituationImpl getOwner() {
		return this.owner;
	}

	public final SituationImpl getSituation() {
		return this.situation;
	}

	public final void setOwner(Situation owner) {
		if (owner == null) {
			throw new NullArgumentException("owner");
		}
		final SituationImpl thisSituation = this.situation;
		if (thisSituation != null) {
			thisSituation.usingSituation();
			SituationImpl o = (SituationImpl) owner;
			if (o == this.owner) {
				return;
			}
			if (!thisSituation.sameSession(o)) {
				throw new IllegalArgumentException("owner与监听器不属于同一会话");
			}
			this.unOwn();
			if (o != thisSituation) {
				this.nextOwn = o.owns;
				o.owns = this;
				this.owner = o;
			}
		} else {
			throw new IllegalStateException("监听器已经注销");
		}
	}

	MessageListenerEntry(Class<TMessage> messageClass,
	        MessageListener<? super TMessage> listener, SituationImpl situation) {
		if (situation == null) {
			throw new NullArgumentException("situation");
		}
		this.listener = listener;
		this.messageClass = messageClass;
		this.situation = situation;
		this.owner = situation;
	}

}
