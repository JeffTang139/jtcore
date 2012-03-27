package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.impl.ServiceBase.EventListener;

/**
 * ÊÂ¼þ¼àÌýÆ÷Á´±í
 * 
 * @author Jeff Tang
 * 
 */
@SuppressWarnings("unchecked")
final class EventListenerChain {
	EventListenerChain(EventListener eventListener, EventListenerChain next) {
		this.eventListener = eventListener;
		this.next = next;
	}

	EventListenerChain(EventListener eventListener) {
		this.eventListener = eventListener;
	}

	final EventListener eventListener;

	final int getChainSize() {
		int size = 0;
		EventListenerChain c = this;
		do {
			size++;
			c = c.next;
		} while (c != null);
		return size;
	}

	EventListenerChain next;

	final EventListenerChain putIn(EventListener eventListener) {
		final float ep = eventListener.priority;
		EventListener el = this.eventListener;
		if (el.priority > ep) {
			return new EventListenerChain(eventListener, this);
		} else if (el == eventListener) {
			return this;
		}
		EventListenerChain pre = this;
		EventListenerChain next = this.next;
		while (next != null) {
			el = next.eventListener;
			if (el == eventListener) {
				return this;
			}
			if (el.priority > ep) {
				break;
			}
			pre = next;
			next = next.next;
		}
		pre.next = new EventListenerChain(eventListener, next);
		return this;

	}
}
