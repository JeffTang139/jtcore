package org.eclipse.jt.core.misc;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jt.core.ObjectQuerier;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.impl.Utils;


/**
 * 合并帮助器
 * 
 * @author Jeff Tang
 * 
 */
public final class SXMergeHelper implements ExceptionCatcher {

	/**
	 * 对象请求器
	 */
	public final ObjectQuerier querier;

	private final ExceptionCatcher catcher;

	private final static class ActionLinkNode {
		@SuppressWarnings("unchecked")
		final SXMergeDelayAction action;
		ActionLinkNode next;

		ActionLinkNode(SXMergeDelayAction<?> action) {
			this.action = action;
		}
	}

	private final Map<Object, ActionLinkNode> delays = new HashMap<Object, ActionLinkNode>();

	/**
	 * 添加延迟动作
	 */
	public final <TAt> void addDelayAction(TAt at,
			SXMergeDelayAction<TAt> action) {
		ActionLinkNode nw = new ActionLinkNode(action);
		ActionLinkNode old = this.delays.put(at, nw);
		// 环，指向环尾
		if (old == null) {
			nw.next = nw;
		} else {
			nw.next = old.next;
			old.next = nw;
		}
	}

	@SuppressWarnings("unchecked")
	public final void resolveDelayAction(Object at, SXElement atElement) {
		ActionLinkNode link = this.delays.remove(at);
		if (link != null) {
			ActionLinkNode first = link.next;
			ActionLinkNode node = first;
			do {
				try {
					ActionLinkNode cur = node;
					node = node.next;
					cur.action.doAction(at, this, atElement);
				} catch (Throwable e) {
					this.catchException(e, this);
					continue;
				}
			} while (node != first);
		}
	}

	public final void catchException(Throwable e, Object sender) {
		if (this.catcher != null) {
			this.catcher.catchException(e, sender);
		} else {
			throw Utils.tryThrowException(e);
		}
	}

	public SXMergeHelper(ObjectQuerier querier) {
		if (querier == null) {
			throw new NullArgumentException("querier");
		}
		this.querier = querier;
		this.catcher = null;
	}

	public SXMergeHelper(ObjectQuerier querier, ExceptionCatcher catcher) {
		if (querier == null) {
			throw new NullArgumentException("querier");
		}
		if (catcher == null) {
			throw new NullArgumentException("catcher");
		}
		this.querier = querier;
		this.catcher = catcher;
	}

}
