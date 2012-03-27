package org.eclipse.jt.core.impl;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.eclipse.jt.core.ContextKind;
import org.eclipse.jt.core.Filter;
import org.eclipse.jt.core.None;
import org.eclipse.jt.core.RemoteLoginInfo;
import org.eclipse.jt.core.RemoteLoginLife;
import org.eclipse.jt.core.SiteState;
import org.eclipse.jt.core.TreeNode;
import org.eclipse.jt.core.TreeNodeFilter;
import org.eclipse.jt.core.User;
import org.eclipse.jt.core.auth.Authority;
import org.eclipse.jt.core.auth.Operation;
import org.eclipse.jt.core.auth.Role;
import org.eclipse.jt.core.auth.RoleAuthorityChecker;
import org.eclipse.jt.core.auth.UserAuthorityChecker;
import org.eclipse.jt.core.da.RecordIterateAction;
import org.eclipse.jt.core.da.RecordSet;
import org.eclipse.jt.core.def.info.ErrorInfoDefine;
import org.eclipse.jt.core.def.info.HintInfoDefine;
import org.eclipse.jt.core.def.info.InfoDefine;
import org.eclipse.jt.core.def.info.ProcessInfoDefine;
import org.eclipse.jt.core.def.info.WarningInfoDefine;
import org.eclipse.jt.core.def.obja.StructDefine;
import org.eclipse.jt.core.def.query.MappingQueryStatementDefine;
import org.eclipse.jt.core.def.query.ModifyStatementDeclarator;
import org.eclipse.jt.core.def.query.ModifyStatementDefine;
import org.eclipse.jt.core.def.query.ORMDeclarator;
import org.eclipse.jt.core.def.query.QueryStatementDeclarator;
import org.eclipse.jt.core.def.query.QueryStatementDeclare;
import org.eclipse.jt.core.def.query.QueryStatementDefine;
import org.eclipse.jt.core.def.query.StatementDeclarator;
import org.eclipse.jt.core.def.query.StatementDeclare;
import org.eclipse.jt.core.def.query.StatementDefine;
import org.eclipse.jt.core.def.query.StoredProcedureDeclarator;
import org.eclipse.jt.core.def.query.StoredProcedureDefine;
import org.eclipse.jt.core.def.table.EntityTableDeclarator;
import org.eclipse.jt.core.def.table.HierarchyDefine;
import org.eclipse.jt.core.def.table.TableDeclarator;
import org.eclipse.jt.core.def.table.TableDefine;
import org.eclipse.jt.core.exception.AbortException;
import org.eclipse.jt.core.exception.DeadLockException;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.invoke.AsyncHandle;
import org.eclipse.jt.core.invoke.AsyncResult;
import org.eclipse.jt.core.invoke.AsyncResultList;
import org.eclipse.jt.core.invoke.AsyncTask;
import org.eclipse.jt.core.invoke.Event;
import org.eclipse.jt.core.invoke.OneKeyOverlappedResult;
import org.eclipse.jt.core.invoke.OneKeyOverlappedResultList;
import org.eclipse.jt.core.invoke.SimpleTask;
import org.eclipse.jt.core.invoke.Task;
import org.eclipse.jt.core.invoke.ThreeKeyOverlappedResult;
import org.eclipse.jt.core.invoke.ThreeKeyOverlappedResultList;
import org.eclipse.jt.core.invoke.TwoKeyOverlappedResult;
import org.eclipse.jt.core.invoke.TwoKeyOverlappedResultList;
import org.eclipse.jt.core.misc.MissingObjectException;
import org.eclipse.jt.core.resource.CategorialResourceQuerier;
import org.eclipse.jt.core.resource.ResourceHandle;
import org.eclipse.jt.core.resource.ResourceStub;
import org.eclipse.jt.core.resource.ResourceToken;
import org.eclipse.jt.core.service.AsyncInfo;
import org.eclipse.jt.core.service.ServiceInvoker;
import org.eclipse.jt.core.situation.MessageListener;
import org.eclipse.jt.core.spi.application.SituationSPI;
import org.eclipse.jt.core.spi.publish.SpaceToken;
import org.eclipse.jt.core.type.GUID;


/**
 * 情景的实现类
 * 
 * @author Jeff Tang
 * 
 */
final class SituationImpl implements SituationSPI, CategorialResourceQuerier {

	private SituationImpl parent;
	private SituationImpl next;
	private SituationImpl children;

	final Space space;
	private SessionImpl session;

	public final SiteState getSiteState() {
		return this.space.site.state;
	}

	/**
	 * 切换登陆用户
	 * 
	 * @param user
	 *            欲切换的用户
	 * @return 返回切换前的旧用户
	 */
	public User changeLoginUser(User user) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.changeLoginUser(user);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final GUID getSiteID() {
		return this.space.site.id;
	}

	public final int getSiteSimpleID() {
		return this.space.site.asSimpleID();
	}

	final ContextImpl<?, ?, ?> usingSituation() {
		if (this.session == null) {
			throw new IllegalStateException("情景接口已经关闭");
		}
		return this.session.usingSituation();
	}

	final boolean sameSession(SituationImpl other) {
		return this.session == other.session;
	}

	private PendingMessageImpl<?> pendingsTail;

	/**
	 * 该方法只在theme主线程中调用
	 */
	final synchronized void removePendingMessage(PendingMessageImpl<?> pending) {
		PendingMessageImpl<?> last, tail = last = this.pendingsTail;
		if (last != null) {
			PendingMessageImpl<?> one = last.nextInSituation;
			while (one != pending && one != tail) {
				last = one;
				one = one.nextInSituation;
			}
			if (one == pending) {
				last.nextInSituation = pending.nextInSituation;
				pending.nextInSituation = null;
				if (one == tail) {
					this.pendingsTail = last == one ? null : last;
				}
			}
		}
	}

	final void addPendingMessage(PendingMessageImpl<?> pending) {
		synchronized (this) {
			if (this.session == null) {
				throw new IllegalStateException("情景接口已经关闭");
			}
			PendingMessageImpl<?> tail = this.pendingsTail;
			if (tail == null) {
				this.pendingsTail = pending;
				pending.nextInSituation = pending;
			} else {
				pending.nextInSituation = tail.nextInSituation;
				tail.nextInSituation = pending;
			}
		}
		this.session.addPendingMessage(pending);
	}

	private SituationImpl(SituationImpl parent, Space space) {
		if (space == null) {
			throw new NullPointerException();
		}
		this.session = parent.session;
		this.parent = parent;
		this.space = space;
		SituationImpl brother = parent.children;
		if (brother == null) {
			parent.children = this;
		} else {
			while (brother.next != null) {
				brother = brother.next;
			}
			brother.next = this;
		}
	}

	SituationImpl(SessionImpl session) {
		this.parent = null;
		if (session == null) {
			throw new NullArgumentException("session");
		}
		this.session = session;
		this.space = session.application.getDefaultSite();
	}

	/**
	 * 注册的监听器
	 */
	MessageListenerEntry<?> listeners;
	/**
	 * 需要通知释放的监听器
	 */
	MessageListenerEntry<?> owns;

	// ///////////////////////////////////
	// /////////Situation
	// ////////////////////////////////////

	public final SituationImpl getParent() {
		return this.parent;
	}

	public final SituationImpl getRoot() {
		SessionImpl session = this.session;
		return session != null ? session.getSituation() : null;
	}

	final void internalClose() {
		if (this.session == null) {
			return;
		}
		while (this.children != null) {
			this.children.internalClose();
		}
		if (this.parent != null) {
			SituationImpl brother = this.parent.children;
			if (brother == this) {
				this.parent.children = this.next;
			} else {
				while (brother.next != this) {
					brother = brother.next;
				}
				brother.next = this.next;
				this.next = null;
			}
			this.parent = null;
		}
		PendingMessageImpl<?> tail = this.pendingsTail;
		if (tail != null) {
			SessionImpl session = this.session;
			synchronized (session) {
				this.pendingsTail = null;
				PendingMessageImpl<?> one = tail.nextInSituation;
				do {
					session.removePendingMessageNoSync(one);
					one = one.helpGC();
				} while (one != tail);
			}
		}
		if (this.owns != null) {
			this.owns.unRegOwnInChain();
			this.owns = null;
		}
		if (this.listeners != null) {
			this.listeners.releaseInChain();
			this.listeners = null;
		}
		this.session = null;
	}

	public final void close() {
		if (this.session != null) {
			this.usingSituation();
			this.internalClose();
		}
	}

	public final <TMessage> MessageListenerEntry<TMessage> regMessageListener(
			Class<TMessage> messageClass,
			MessageListener<? super TMessage> listener) {
		if (messageClass == null || listener == null) {
			throw new NullPointerException();
		}
		this.usingSituation();
		if (this.listeners == null) {
			MessageListenerEntry<TMessage> entry = new MessageListenerEntry<TMessage>(
					messageClass, listener, this);
			this.listeners = entry;
			return entry;
		} else {
			return this.listeners.add(messageClass, listener);
		}
	}

	public final <TMessage> MessageTransmitterImpl<TMessage> bubbleMessage(
			TMessage message) {
		return this.bubbleMessage(message, Integer.MAX_VALUE);
	}

	public final <TMessage> MessageTransmitterImpl<TMessage> bubbleMessage(
			TMessage message, int maxDistance) {
		if (message == null) {
			throw new NullPointerException();
		}
		if (maxDistance < 0) {
			throw new IllegalArgumentException("最大冒泡高度值不可以小于零");
		}
		this.usingSituation();
		Class<?> messageClass = message.getClass();
		MessageTransmitterImpl<TMessage> transmitter = MessageTransmitterImpl
				.bubble(this, message, maxDistance);
		try {
			if (this.listeners != null) {
				transmitter.context = this;
				this.listeners.handleMessage(messageClass, transmitter);
			}
			for (SituationImpl p = this.parent; transmitter.distance < transmitter.maxDistance
					&& p != null; p = p.parent) {
				transmitter.distance++;
				if (p.listeners != null) {
					transmitter.context = p;
					p.listeners.handleMessage(messageClass, transmitter);
				}
			}
		} finally {
			transmitter.helpGC();
		}
		return transmitter;
	}

	private final void broadcastMessage(Class<?> messageClass,
			MessageTransmitterImpl<?> transmitter) {
		transmitter.distance++;
		for (SituationImpl p = this.children; p != null; p = p.next) {
			if (p.listeners != null) {
				transmitter.context = p;
				p.listeners.handleMessage(messageClass, transmitter);
			}
			if (transmitter.distance > transmitter.maxDistance) {
				break;
			}
			if (p.children != null) {
				p.broadcastMessage(messageClass, transmitter);
				if (transmitter.distance > transmitter.maxDistance) {
					break;
				}
			}
		}
		transmitter.distance--;
	}

	public final <TMessage> MessageTransmitterImpl<TMessage> broadcastMessage(
			TMessage message, int maxDistance) {
		if (message == null) {
			throw new NullPointerException();
		}
		if (maxDistance < 0) {
			throw new IllegalArgumentException("最大广播深度值不可以小于零");
		}
		this.usingSituation();
		Class<?> messageClass = message.getClass();
		MessageTransmitterImpl<TMessage> transmitter = MessageTransmitterImpl
				.brodcast(this, message, maxDistance);
		try {
			if (this.listeners != null) {
				transmitter.context = this;
				this.listeners.handleMessage(messageClass, transmitter);
			}
			if (this.children != null
					&& transmitter.distance < transmitter.maxDistance) {
				this.broadcastMessage(messageClass, transmitter);
			}
		} finally {
			transmitter.helpGC();
		}
		return transmitter;
	}

	public final <TMessage> MessageTransmitterImpl<TMessage> broadcastMessage(
			TMessage message) {
		return this.broadcastMessage(message, Integer.MAX_VALUE);
	}

	public final <TMessage> PendingMessageImpl<TMessage> postBroadcastMessage(
			TMessage message, int maxDistance) {
		return PendingMessageImpl.brodcast(this, message, maxDistance);
	}

	public final <TMessage> PendingMessageImpl<TMessage> postBroadcastMessage(
			TMessage message) {
		return this.postBroadcastMessage(message, Integer.MAX_VALUE);
	}

	public final <TMessage> PendingMessageImpl<TMessage> postBubbleMessage(
			TMessage message, int maxDistance) {
		return PendingMessageImpl.bubble(this, message, maxDistance);
	}

	public final <TMessage> PendingMessageImpl<TMessage> postBubbleMessage(
			TMessage message) {
		return this.postBubbleMessage(message, Integer.MAX_VALUE);
	}

	public final SituationImpl newSubSituation(SpaceToken space) {
		this.usingSituation();
		return new SituationImpl(this, space != null ? (Space) space
				: this.space);
	}

	// ///////////////////////////////////////////////////
	// /////// context impl
	// ///////////////////////////////////////////////////

	public final SessionImpl getLogin() {
		this.usingSituation();
		return this.session;

	}

	public final ContextKind getKind() {
		return ContextKind.SITUATION;
	}

	public final RuntimeException throwThrowable(Throwable throwable) {
		return Utils.tryThrowException(throwable);
	}

	public final <TFacade> TFacade find(Class<TFacade> facadeClass)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.find(facadeClass);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TFacade find(Class<TFacade> facadeClass, Object key)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.find(facadeClass, key);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TFacade find(Class<TFacade> facadeClass,
			Object key1, Object key2) throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.find(facadeClass, key1, key2);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TFacade find(Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.find(facadeClass, key1, key2, key3);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TFacade find(Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3, Object... keys)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.find(facadeClass, key1, key2, key3, keys);
		} finally {
			save.updateContextSpace(context);
		}

	}

	public final <TFacade> TFacade get(Class<TFacade> facadeClass)
			throws UnsupportedOperationException, MissingObjectException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.get(facadeClass);
		} finally {
			save.updateContextSpace(context);
		}

	}

	public final <TFacade> TFacade get(Class<TFacade> facadeClass, Object key)
			throws UnsupportedOperationException, MissingObjectException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.get(facadeClass, key);
		} finally {
			save.updateContextSpace(context);
		}

	}

	public final <TFacade> TFacade get(Class<TFacade> facadeClass, Object key1,
			Object key2) throws UnsupportedOperationException,
			MissingObjectException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.get(facadeClass, key1, key2);
		} finally {
			save.updateContextSpace(context);
		}

	}

	public final <TFacade> TFacade get(Class<TFacade> facadeClass, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException,
			MissingObjectException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.get(facadeClass, key1, key2, key3);
		} finally {
			save.updateContextSpace(context);
		}

	}

	public final <TFacade> TFacade get(Class<TFacade> facadeClass, Object key1,
			Object key2, Object key3, Object... keys)
			throws UnsupportedOperationException, MissingObjectException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.get(facadeClass, key1, key2, key3, keys);
		} finally {
			save.updateContextSpace(context);
		}

	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> facadeClass)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getList(facadeClass);
		} finally {
			save.updateContextSpace(context);
		}

	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getList(facadeClass, filter);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getList(facadeClass, sortComparator);
		} finally {
			save.updateContextSpace(context);
		}

	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getList(facadeClass, filter, sortComparator);
		} finally {
			save.updateContextSpace(context);
		}

	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Object key) throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getList(facadeClass, key);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter, Object key)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getList(facadeClass, filter, key);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getList(facadeClass, sortComparator, key);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getList(facadeClass, filter, sortComparator, key);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Object key1, Object key2) throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getList(facadeClass, key1, key2);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter, Object key1, Object key2)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getList(facadeClass, filter, key1, key2);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getList(facadeClass, sortComparator, key1, key2);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getList(facadeClass, filter, sortComparator, key1,
					key2);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getList(facadeClass, key1, key2, key3);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter, Object key1, Object key2,
			Object key3) throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getList(facadeClass, filter, key1, key2, key3);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getList(facadeClass, sortComparator, key1, key2,
					key3);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getList(facadeClass, filter, sortComparator, key1,
					key2, key3);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3, Object... otherKeys) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getList(facadeClass, key1, key2, key3, otherKeys);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter, Object key1, Object key2,
			Object key3, Object... otherKeys) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getList(facadeClass, filter, key1, key2, key3,
					otherKeys);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getList(facadeClass, sortComparator, key1, key2,
					key3, otherKeys);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getList(facadeClass, filter, sortComparator, key1,
					key2, key3, otherKeys);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass) throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getTreeNode(facadeClass);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass, TreeNodeFilter<? super TFacade> filter)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getTreeNode(facadeClass, filter);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getTreeNode(facadeClass, sortComparator);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass, TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getTreeNode(facadeClass, filter, sortComparator);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass, Object key)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getTreeNode(facadeClass, key);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass, TreeNodeFilter<? super TFacade> filter,
			Object key) throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getTreeNode(facadeClass, filter, key);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getTreeNode(facadeClass, sortComparator, key);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass, TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context
					.getTreeNode(facadeClass, filter, sortComparator, key);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass, Object key1, Object key2)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getTreeNode(facadeClass, key1, key2);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass, TreeNodeFilter<? super TFacade> filter,
			Object key1, Object key2) throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getTreeNode(facadeClass, filter, key1, key2);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getTreeNode(facadeClass, sortComparator, key1, key2);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass, TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getTreeNode(facadeClass, filter, sortComparator,
					key1, key2);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getTreeNode(facadeClass, key1, key2, key3);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass, TreeNodeFilter<? super TFacade> filter,
			Object key1, Object key2, Object key3)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getTreeNode(facadeClass, filter, key1, key2, key3);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getTreeNode(facadeClass, sortComparator, key1, key2,
					key3);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass, TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getTreeNode(facadeClass, filter, sortComparator,
					key1, key2, key3);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3,
			Object... otherKeys) throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context
					.getTreeNode(facadeClass, key1, key2, key3, otherKeys);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass, TreeNodeFilter<? super TFacade> filter,
			Object key1, Object key2, Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getTreeNode(facadeClass, filter, key1, key2, key3,
					otherKeys);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getTreeNode(facadeClass, sortComparator, key1, key2,
					key3, otherKeys);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass, TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getTreeNode(facadeClass, filter, sortComparator,
					key1, key2, key3, otherKeys);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> ResourceToken<TFacade> findResourceToken(
			Class<TFacade> facadeClass) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.findResourceToken(facadeClass);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> ResourceToken<TFacade> findResourceToken(
			Class<TFacade> facadeClass, Object key) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.findResourceToken(facadeClass, key);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> ResourceToken<TFacade> findResourceToken(
			Class<TFacade> facadeClass, Object key1, Object key2) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.findResourceToken(facadeClass, key1, key2);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> ResourceToken<TFacade> findResourceToken(
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.findResourceToken(facadeClass, key1, key2, key3);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> ResourceToken<TFacade> findResourceToken(
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3,
			Object... otherKeys) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.findResourceToken(facadeClass, key1, key2, key3,
					otherKeys);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade, THolderFacade> List<TFacade> getResourceReferences(
			Class<TFacade> facadeClass, ResourceToken<THolderFacade> holderToken) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getResourceReferences(facadeClass, holderToken);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade, THolderFacade> List<TFacade> getResourceReferences(
			Class<TFacade> facadeClass,
			ResourceToken<THolderFacade> holderToken,
			Filter<? super TFacade> filter) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getResourceReferences(facadeClass, holderToken,
					filter);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade, THolderFacade> List<TFacade> getResourceReferences(
			Class<TFacade> facadeClass,
			ResourceToken<THolderFacade> holderToken,
			Comparator<? super TFacade> sortComparator) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getResourceReferences(facadeClass, holderToken,
					sortComparator);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade, THolderFacade> List<TFacade> getResourceReferences(
			Class<TFacade> facadeClass,
			ResourceToken<THolderFacade> holderToken,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getResourceReferences(facadeClass, holderToken,
					filter, sortComparator);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> ResourceToken<TFacade> getResourceToken(
			Class<TFacade> facadeClass) throws MissingObjectException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getResourceToken(facadeClass);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> ResourceToken<TFacade> getResourceToken(
			Class<TFacade> facadeClass, Object key)
			throws MissingObjectException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getResourceToken(facadeClass, key);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> ResourceToken<TFacade> getResourceToken(
			Class<TFacade> facadeClass, Object key1, Object key2)
			throws MissingObjectException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getResourceToken(facadeClass, key1, key2);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> ResourceToken<TFacade> getResourceToken(
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3)
			throws MissingObjectException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getResourceToken(facadeClass, key1, key2, key3);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> ResourceToken<TFacade> getResourceToken(
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3,
			Object... otherKeys) throws MissingObjectException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getResourceToken(facadeClass, key1, key2, key3,
					otherKeys);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> ResourceHandle<TFacade> lockResourceS(
			ResourceToken<TFacade> resourceToken) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.lockResourceS(resourceToken);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> ResourceHandle<TFacade> lockResourceU(
			ResourceToken<TFacade> resourceToken) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.lockResourceU(resourceToken);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TResult> AsyncResult<TResult> asyncGet(
			Class<TResult> resultClass) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.asyncGet(resultClass);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TResult, TKey> OneKeyOverlappedResult<TResult, TKey> asyncGet(
			Class<TResult> resultClass, TKey key) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.asyncGet(resultClass, key);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TResult, TKey1, TKey2> TwoKeyOverlappedResult<TResult, TKey1, TKey2> asyncGet(
			Class<TResult> resultClass, TKey1 key, TKey2 key2) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.asyncGet(resultClass, key, key2);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TResult, TKey1, TKey2, TKey3> ThreeKeyOverlappedResult<TResult, TKey1, TKey2, TKey3> asyncGet(
			Class<TResult> resultClass, TKey1 key, TKey2 key2, TKey3 key3) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.asyncGet(resultClass, key, key2, key3);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TResult> AsyncResultList<TResult> asyncGetList(
			Class<TResult> resultClass) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.asyncGetList(resultClass);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TResult, TKey1> OneKeyOverlappedResultList<TResult, TKey1> asyncGetList(
			Class<TResult> resultClass, TKey1 key1) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.asyncGetList(resultClass, key1);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TResult, TKey1, TKey2> TwoKeyOverlappedResultList<TResult, TKey1, TKey2> asyncGetList(
			Class<TResult> resultClass, TKey1 key1, TKey2 key2) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.asyncGetList(resultClass, key1, key2);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TResult, TKey1, TKey2, TKey3> ThreeKeyOverlappedResultList<TResult, TKey1, TKey2, TKey3> asyncGetList(
			Class<TResult> resultClass, TKey1 key1, TKey2 key2, TKey3 key3) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.asyncGetList(resultClass, key1, key2, key3);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TTask extends Task<TMethod>, TMethod extends Enum<TMethod>> AsyncTask<TTask, TMethod> asyncHandle(
			TTask task, TMethod method) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.asyncHandle(task, method);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TSimpleTask extends SimpleTask> AsyncTask<TSimpleTask, None> asyncHandle(
			TSimpleTask task) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.asyncHandle(task);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TTask extends Task<TMethod>, TMethod extends Enum<TMethod>> AsyncTask<TTask, TMethod> asyncHandle(
			TTask task, TMethod method, AsyncInfo info) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.asyncHandle(task, method, info);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TSimpleTask extends SimpleTask> AsyncTask<TSimpleTask, None> asyncHandle(
			TSimpleTask task, AsyncInfo info) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.asyncHandle(task, info);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final float getResistance() {
		return this.usingSituation().getResistance();
	}

	public final <TMethod extends Enum<TMethod>> void handle(
			Task<TMethod> task, TMethod method) throws DeadLockException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			context.handle(task, method);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final void handle(SimpleTask task) throws DeadLockException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			context.handle(task);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final AsyncHandle occur(Event event) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.occur(event);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final boolean dispatch(Event event) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.dispatch(event);
		} finally {
			save.updateContextSpace(context);
		}
	}

	/**
	 * 返回是否有效
	 */
	public final boolean isValid() {
		final SessionImpl session = this.session;
		if (session == null) {
			return false;
		}
		final ContextImpl<?, ?, ?> context = session.themeContext;
		return context != null && context.isValid();
	}

	/**
	 * 检查是否有效
	 */
	public final void checkValid() {
		this.usingSituation().checkValid();
	}

	public final void waitFor(AsyncHandle one, AsyncHandle... others)
			throws InterruptedException {
		this.usingSituation().waitFor(one, others);
	}

	public final void waitFor(long timeout, AsyncHandle one,
			AsyncHandle... others) throws InterruptedException {
		this.usingSituation().waitFor(timeout, one, others);
	}

	public final GUID newRECID() {
		this.usingSituation();
		return this.space.site.application.newRECID();
	}

	public final long newRECVER() {
		this.usingSituation();
		return this.space.site.application.newRECVER();
	}

	public final QueryStatementImpl newQueryStatement() {
		return this.usingSituation().newQueryStatement();
	}

	public final QueryStatementDeclare newQueryStatement(
			QueryStatementDefine sample) {
		return this.usingSituation().newQueryStatement(sample);
	}

	public final MappingQueryStatementImpl newMappingQueryStatement(
			EntityTableDeclarator<?> table) {
		return this.usingSituation().newMappingQueryStatement(table);
	}

	public final MappingQueryStatementImpl newMappingQueryStatement(
			StructDefine model) {
		return this.usingSituation().newMappingQueryStatement(model);
	}

	public final MappingQueryStatementImpl newMappingQueryStatement(
			StructDefine model, String name) {
		return this.usingSituation().newMappingQueryStatement(model, name);
	}

	public final MappingQueryStatementImpl newMappingQueryStatement(
			Class<?> entityClass) {
		return this.usingSituation().newMappingQueryStatement(entityClass);
	}

	public final MappingQueryStatementImpl newMappingQueryStatement(
			Class<?> entityClass, String name) {
		return this.usingSituation()
				.newMappingQueryStatement(entityClass, name);
	}

	public final InsertStatementImpl newInsertStatement(TableDeclarator table) {
		return this.usingSituation().newInsertStatement(table);
	}

	public final InsertStatementImpl newInsertStatement(TableDefine table) {
		return this.usingSituation().newInsertStatement(table);
	}

	public final DeleteStatementImpl newDeleteStatement(TableDeclarator table) {
		return this.usingSituation().newDeleteStatement(table);
	}

	public final DeleteStatementImpl newDeleteStatement(TableDefine table) {
		return this.usingSituation().newDeleteStatement(table);
	}

	public final UpdateStatementImpl newUpdateStatement(TableDeclarator table) {
		return this.usingSituation().newUpdateStatement(table);
	}

	public final UpdateStatementImpl newUpdateStatement(TableDeclarator table,
			String name) {
		return this.usingSituation().newUpdateStatement(table, name);
	}

	public final UpdateStatementImpl newUpdateStatement(TableDefine table) {
		return this.usingSituation().newUpdateStatement(table);
	}

	public final UpdateStatementImpl newUpdateStatement(TableDefine table,
			String name) {
		return this.usingSituation().newUpdateStatement(table, name);
	}

	public final DBCommandProxy prepareStatement(StatementDefine statement) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.prepareStatement(statement);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final DBCommandProxy prepareStatement(CharSequence dnaSql) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.prepareStatement(dnaSql);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final IStatement parseStatement(CharSequence dnaSql) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.parseStatement(dnaSql);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public <TStatement extends StatementDeclare> TStatement parseStatement(
			CharSequence dnaSql, Class<TStatement> clz) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.parseStatement(dnaSql, clz);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final DBCommandProxy prepareStatement(
			StatementDeclarator<?> statement) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.prepareStatement(statement);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final Object executeScalar(QueryStatementDeclarator query,
			Object... argValues) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.executeScalar(query, argValues);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final Object executeScalar(QueryStatementDefine query,
			Object... argValues) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.executeScalar(query, argValues);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TEntity> ORMAccessorProxy<TEntity> newORMAccessor(
			ORMDeclarator<TEntity> orm) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.newORMAccessor(orm);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final ORMAccessorProxy<Object> newORMAccessor(
			MappingQueryStatementDefine mappingQuery) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.newORMAccessor(mappingQuery);
		} finally {
			save.updateContextSpace(context);
		}
	}

	/**
	 * 创建实体对象访问器 @
	 */
	public final <TEntity> ORMAccessorProxy<TEntity> newORMAccessor(
			Class<TEntity> entityClass, MappingQueryStatementDefine mappingQuery) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.newORMAccessor(entityClass, mappingQuery);
		} finally {
			save.updateContextSpace(context);
		}
	}

	/**
	 * 创建实体对象访问器 @
	 */
	public final <TEntity> ORMAccessorProxy<TEntity> newORMAccessor(
			EntityTableDeclarator<TEntity> table) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.newORMAccessor(table);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TObject> TObject newObject(Class<TObject> clazz,
			Object... aditionalArgs) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.newObject(clazz, aditionalArgs);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final void hierarchyMoveTo(HierarchyDefine hierarchy, GUID parent,
			GUID child) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		context.hierarchyMoveTo(hierarchy, parent, child);
	}

	public final void hierarchyMoveTo(HierarchyDefine hierarchy, GUID parent,
			GUID child, GUID... others) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		context.hierarchyMoveTo(hierarchy, parent, child, others);
	}

	public final void hierarchyMoveTo(HierarchyDefine hierarchy, GUID parent,
			List<GUID> children) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		context.hierarchyMoveTo(hierarchy, parent, children);
	}

	public final void hierarchyMoveTo(HierarchyDefine hierarchy, GUID parent,
			Iterable<GUID> children) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		context.hierarchyMoveTo(hierarchy, parent, children);
	}

	public final RecordSet openQuery(QueryStatementDefine query,
			Object... argumetns) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.openQuery(query, argumetns);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final RecordSet openQuery(QueryStatementDeclarator query,
			Object... argumetns) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.openQuery(query, argumetns);
		} finally {
			save.updateContextSpace(context);
		}
	}

	// public RecordSet openQueryTop(QueryDefine query, int top,
	// Object... argValues) {
	// ContextImpl<?, ?, ?> context = this.usingSituation();
	// SpaceNode save = this.space.updateContextCurrentService(context);
	// try {
	// return context.openQueryTop(query, top, argValues);
	// } finally {
	// save.updateContextCurrentService(context);
	// }
	// }
	//
	// public RecordSet openQueryTop(QueryDeclarator query, int top,
	// Object... argValues) {
	// ContextImpl<?, ?, ?> context = this.usingSituation();
	// SpaceNode save = this.space.updateContextCurrentService(context);
	// try {
	// return context.openQueryTop(query, top, argValues);
	// } finally {
	// save.updateContextCurrentService(context);
	// }
	// }

	public final RecordSet openQueryLimit(QueryStatementDefine query,
			long offset, long rowCount, Object... argValues) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.openQueryLimit(query, offset, rowCount, argValues);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final RecordSet openQueryLimit(QueryStatementDeclarator query,
			long offset, long rowCount, Object... argValues) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.openQueryLimit(query, offset, rowCount, argValues);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final void iterateQuery(QueryStatementDefine query,
			RecordIterateAction action, Object... argValues) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			context.iterateQuery(query, action, argValues);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final void iterateQuery(QueryStatementDeclarator query,
			RecordIterateAction action, Object... argValues) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			context.iterateQuery(query, action, argValues);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final void iterateQueryLimit(QueryStatementDefine query,
			RecordIterateAction action, long offset, long rowCount,
			Object... argValues) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			context.iterateQueryLimit(query, action, offset, rowCount,
					argValues);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final void iterateQueryLimit(QueryStatementDeclarator query,
			RecordIterateAction action, long offset, long rowCount,
			Object... argValues) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			context.iterateQueryLimit(query, action, offset, rowCount,
					argValues);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final int rowCountOf(QueryStatementDefine query, Object... argValues) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.rowCountOf(query, argValues);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final int rowCountOf(QueryStatementDeclarator query,
			Object... argValues) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.rowCountOf(query, argValues);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final long rowCountOfL(QueryStatementDefine query,
			Object... argValues) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.rowCountOfL(query, argValues);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final long rowCountOfL(QueryStatementDeclarator query,
			Object... argValues) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.rowCountOfL(query, argValues);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final int executeUpdate(ModifyStatementDefine statement,
			Object... argValues) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.executeUpdate(statement, argValues);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final int executeUpdate(ModifyStatementDeclarator<?> statement,
			Object... argValues) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.executeUpdate(statement, argValues);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public void executeUpdate(StoredProcedureDeclarator procedure,
			Object... argValues) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			context.executeUpdate(procedure, argValues);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public void executeUpdate(StoredProcedureDefine procedure,
			Object... argValues) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			context.executeUpdate(procedure, argValues);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final float getPartialProgress() {
		return this.usingSituation().getPartialProgress();
	}

	public final float getPartialProgressQuotiety() {
		return this.usingSituation().getPartialProgressQuotiety();
	}

	public final float getTotalProgress() {
		return this.usingSituation().getTotalProgress();
	}

	public final float getRestPartialProgress() {
		return this.usingSituation().getRestPartialProgress();
	}

	/**
	 * 报告Hint信息
	 * 
	 */
	public final void reportHint(HintInfoDefine infoDefine) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		context.reportHint(infoDefine);
	}

	/**
	 * 报告Hint信息
	 */
	public final void reportHint(HintInfoDefine infoDefine, Object param1) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		context.reportHint(infoDefine, param1);
	}

	/**
	 * 报告Hint信息
	 */

	public final void reportHint(HintInfoDefine infoDefine, Object param1,
			Object param2) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		context.reportHint(infoDefine, param1, param2);
	}

	/**
	 * 报告Hint信息
	 */

	public final void reportHint(HintInfoDefine infoDefine, Object param1,
			Object param2, Object param3) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		context.reportHint(infoDefine, param1, param2, param3);
	}

	/**
	 * 报告Hint信息
	 */
	public final void reportHint(HintInfoDefine infoDefine, Object param1,
			Object param2, Object param3, Object... others) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		context.reportHint(infoDefine, param1, param2, param3, others);
	}

	/**
	 * 报告Error信息
	 * 
	 */
	public final void reportError(ErrorInfoDefine infoDefine) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		context.reportError(infoDefine);
	}

	/**
	 * 报告Error信息
	 */
	public final void reportError(ErrorInfoDefine infoDefine, Object param1) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		context.reportError(infoDefine, param1);
	}

	/**
	 * 报告Error信息
	 */

	public final void reportError(ErrorInfoDefine infoDefine, Object param1,
			Object param2) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		context.reportError(infoDefine, param1, param2);
	}

	/**
	 * 报告Error信息
	 */

	public final void reportError(ErrorInfoDefine infoDefine, Object param1,
			Object param2, Object param3) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		context.reportError(infoDefine, param1, param2, param3);
	}

	/**
	 * 报告Error信息
	 */
	public final void reportError(ErrorInfoDefine infoDefine, Object param1,
			Object param2, Object param3, Object... others) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		context.reportError(infoDefine, param1, param2, param3, others);
	}

	/**
	 * 报告Done信息
	 * 
	 */
	public final void reportWarning(WarningInfoDefine infoDefine) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		context.reportWarning(infoDefine);
	}

	/**
	 * 报告Done信息
	 */
	public final void reportWarning(WarningInfoDefine infoDefine, Object param1) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		context.reportWarning(infoDefine, param1);
	}

	/**
	 * 报告Done信息
	 */

	public final void reportWarning(WarningInfoDefine infoDefine,
			Object param1, Object param2) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		context.reportWarning(infoDefine, param1, param2);
	}

	/**
	 * 报告Done信息
	 */

	public final void reportWarning(WarningInfoDefine infoDefine,
			Object param1, Object param2, Object param3) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		context.reportWarning(infoDefine, param1, param2, param3);
	}

	/**
	 * 报告Done信息
	 */
	public final void reportWarning(WarningInfoDefine infoDefine,
			Object param1, Object param2, Object param3, Object... others) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		context.reportWarning(infoDefine, param1, param2, param3, others);
	}

	public final void beginProcess(ProcessInfoDefine infoDefine) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		context.beginProcess(infoDefine);
	}

	public final void beginProcess(ProcessInfoDefine infoDefine, Object param1) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		context.beginProcess(infoDefine, param1);
	}

	public final void beginProcess(ProcessInfoDefine infoDefine, Object param1,
			Object param2) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		context.beginProcess(infoDefine, param1, param2);
	}

	public final void beginProcess(ProcessInfoDefine infoDefine, Object param1,
			Object param2, Object param3) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		context.beginProcess(infoDefine, param1, param2, param3);
	}

	public final void beginProcess(ProcessInfoDefine infoDefine, Object param1,
			Object param2, Object param3, Object... others) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		context.beginProcess(infoDefine, param1, param2, param3, others);
	}

	public final void endProcess() {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		context.endProcess();
	}

	public final void abort() throws AbortException {
		this.usingSituation().abort();
	}

	public final float setNextStep(float nextStep) {
		return this.usingSituation().setNextStep(nextStep);
	}

	public final float getNextStep() {
		return this.usingSituation().getNextStep();
	}

	public final float setNextPartialProgress(float nextProgress) {
		return this.usingSituation().setNextPartialProgress(nextProgress);
	}

	public final float setPartialProgress(float progress) {
		return this.usingSituation().setPartialProgress(progress);
	}

	public final <TResFacade> Authority getAuthority(
			Operation<? super TResFacade> operation,
			ResourceStub<TResFacade> resource) {
		return this.usingSituation().getAuthority(operation, resource);
	}

	public final <TResFacade> boolean hasAuthority(
			Operation<? super TResFacade> operation,
			ResourceStub<TResFacade> resource) {
		return this.usingSituation().hasAuthority(operation, resource);
	}

	public final <TFacade> Authority getAccreditAuthority(
			Operation<? super TFacade> operation, ResourceStub<TFacade> resource) {
		return this.usingSituation().getAccreditAuthority(operation, resource);
	}

	public final <TFacade> boolean hasAccreditAuthority(
			Operation<? super TFacade> operation, ResourceStub<TFacade> resource) {
		return this.usingSituation().hasAccreditAuthority(operation, resource);
	}

	public final RoleAuthorityChecker newRoleAuthorityChecker(Role role,
			GUID orgID, boolean operationAuthority) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		return context.newRoleAuthorityChecker(role, orgID, operationAuthority);
	}

	public final RoleAuthorityChecker newRoleAuthorityChecker(GUID roleID,
			GUID orgID, boolean operationAuthority) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		return context.newRoleAuthorityChecker(roleID, orgID,
				operationAuthority);
	}

	public final UserAuthorityChecker newUserAuthorityChecker(User user,
			GUID orgID, boolean operationAuthority) {
		return this.usingSituation().newUserAuthorityChecker(user, orgID,
				operationAuthority);
	}

	public final UserAuthorityChecker newUserAuthorityChecker(GUID userID,
			GUID orgID, boolean operationAuthority) {
		return this.usingSituation().newUserAuthorityChecker(userID, orgID,
				operationAuthority);
	}

	final DBAdapterImpl getDBAdapter() throws SQLException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getDBAdapter();
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final boolean isCanceling() {
		return this.usingSituation().isCanceling();
	}

	public final void throwIfCanceling() {
		this.usingSituation().throwIfCanceling();
	}

	public final Throwable resolveTrans() {
		return this.usingSituation().resolveTrans();
	}

	public final void setCategory(Object category) {
		throw new UnsupportedOperationException("情景不支持设置资源类别");
	}

	public final CategorialResourceQuerier usingResourceCategory(Object category) {
		if (category == None.NONE) {
			return this;
		}
		return this.usingSituation().usingResourceCategory(category);
	}

	public final Object getCategory() {
		return None.NONE;
	}

	public final <TFacade> void ensureResourceInited(Class<TFacade> facadeClass) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			context.ensureResourceInited(facadeClass);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final ServiceInvoker usingRemoteInvoker(
			RemoteLoginInfo remoteLoginInfo) {
		return this.usingSituation().usingRemoteInvoker(remoteLoginInfo);
	}

	/**
	 * 获取远程调用信息
	 */
	public final RemoteLoginInfoImpl allocRemoteLoginInfo(String host, int port) {
		return this.usingSituation().allocRemoteLoginInfo(host, port);
	}

	/**
	 * 获取远程调用信息
	 */
	public final RemoteLoginInfoImpl allocRemoteLoginInfo(String host,
			int port, String user, String password) {
		return this.usingSituation().allocRemoteLoginInfo(host, port, user,
				password);
	}

	/**
	 * 获取远程调用信息
	 */
	public final RemoteLoginInfoImpl allocRemoteLoginInfo(String host,
			int port, String user, String password, RemoteLoginLife life) {
		return this.usingSituation().allocRemoteLoginInfo(host, port, user,
				password, life);
	}

	public final void exception(Throwable e) {
		this.usingSituation().exception(e);
	}

	// /////////////////////////////本地化/////////////////////////////////////
	public final Locale getLocale() {
		return this.session.locate;
	}

	public final String localize(InfoDefine info) {
		return this.usingSituation().internalLocalize(info, null, null, null,
				null);
	}

	public final String localize(InfoDefine info, Object param1) {
		return this.usingSituation().internalLocalize(info, param1, null, null,
				null);
	}

	public final String localize(InfoDefine info, Object param1, Object param2) {
		return this.usingSituation().internalLocalize(info, param1, param2,
				null, null);
	}

	public final String localize(InfoDefine info, Object param1, Object param2,
			Object param3) {
		return this.usingSituation().internalLocalize(info, param1, param2,
				param3, null);
	}

	public final String localize(InfoDefine info, Object param1, Object param2,
			Object param3, Object... others) {
		return this.usingSituation().internalLocalize(info, param1, param2,
				param3, others);
	}

	public final void localize(Appendable to, InfoDefine info) {
		this.usingSituation()
				.internalLocalize(info, to, null, null, null, null);
	}

	public final void localize(Appendable to, InfoDefine info, Object param1) {
		this.usingSituation().internalLocalize(info, to, param1, null, null,
				null);
	}

	public final void localize(Appendable to, InfoDefine info, Object param1,
			Object param2) {
		this.usingSituation().internalLocalize(info, to, param1, param2, null,
				null);
	}

	public final void localize(Appendable to, InfoDefine info, Object param1,
			Object param2, Object param3) {
		this.usingSituation().internalLocalize(info, to, param1, param2,
				param3, null);
	}

	public final void localize(Appendable to, InfoDefine info, Object param1,
			Object param2, Object param3, Object... others) {
		this.usingSituation().internalLocalize(info, to, param1, param2,
				param3, others);
	}

	public final <TFacade> TFacade find(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass) throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.find(operation, facadeClass);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TFacade find(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass, Object key)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.find(operation, facadeClass, key);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TFacade find(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass, Object key1, Object key2)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.find(operation, facadeClass, key2, key2);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TFacade find(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.find(operation, facadeClass, key2, key2, key3);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TFacade find(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3,
			Object... keys) throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.find(operation, facadeClass, key2, key2, keys);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> ResourceToken<TFacade> findResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.findResourceToken(operation, facadeClass);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> ResourceToken<TFacade> findResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.findResourceToken(operation, facadeClass, key);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> ResourceToken<TFacade> findResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context
					.findResourceToken(operation, facadeClass, key1, key2);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> ResourceToken<TFacade> findResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.findResourceToken(operation, facadeClass, key1,
					key2, key3);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> ResourceToken<TFacade> findResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3, Object... otherKeys) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.findResourceToken(operation, facadeClass, key1,
					key2, key3, otherKeys);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TFacade get(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass) throws UnsupportedOperationException,
			MissingObjectException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.get(operation, facadeClass);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TFacade get(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass, Object key)
			throws UnsupportedOperationException, MissingObjectException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.get(operation, facadeClass, key);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TFacade get(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass, Object key1, Object key2)
			throws UnsupportedOperationException, MissingObjectException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.get(operation, facadeClass, key1, key2);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TFacade get(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3)
			throws UnsupportedOperationException, MissingObjectException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.get(operation, facadeClass, key1, key2, key3);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TFacade get(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3,
			Object... keys) throws UnsupportedOperationException,
			MissingObjectException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.get(operation, facadeClass, key1, key2, key3, keys);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getList(operation, facadeClass);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key) throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getList(operation, facadeClass, key);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2) throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getList(operation, facadeClass, key1, key2);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getList(operation, facadeClass, key1, key2, key3);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3, Object... otherKeys) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getList(operation, facadeClass, key1, key2, key3,
					otherKeys);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Filter<? super TFacade> filter)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getList(operation, facadeClass, filter);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Filter<? super TFacade> filter, Object key)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getList(operation, facadeClass, filter, key);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Filter<? super TFacade> filter, Object key1, Object key2)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getList(operation, facadeClass, filter, key1, key2);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Filter<? super TFacade> filter, Object key1, Object key2,
			Object key3) throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getList(operation, facadeClass, filter, key1, key2,
					key3);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Filter<? super TFacade> filter, Object key1, Object key2,
			Object key3, Object... otherKeys) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getList(operation, facadeClass, filter, key1, key2,
					key3, otherKeys);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getList(operation, facadeClass, sortComparator);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getList(operation, facadeClass, sortComparator, key);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getList(operation, facadeClass, sortComparator,
					key1, key2);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getList(operation, facadeClass, sortComparator,
					key1, key2, key3);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getList(operation, facadeClass, sortComparator,
					key1, key2, key2, otherKeys);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getList(operation, facadeClass, filter,
					sortComparator);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getList(operation, facadeClass, filter,
					sortComparator, key);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getList(operation, facadeClass, filter,
					sortComparator, key1, key2);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getList(operation, facadeClass, filter,
					sortComparator, key1, key2, key3);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getList(operation, facadeClass, filter,
					sortComparator, key1, key2, key3, otherKeys);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade, THolderFacade> List<TFacade> getResourceReferences(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			ResourceToken<THolderFacade> holderToken) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getResourceReferences(operation, facadeClass,
					holderToken);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade, THolderFacade> List<TFacade> getResourceReferences(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			ResourceToken<THolderFacade> holderToken,
			Filter<? super TFacade> filter) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getResourceReferences(operation, facadeClass,
					holderToken, filter);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade, THolderFacade> List<TFacade> getResourceReferences(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			ResourceToken<THolderFacade> holderToken,
			Comparator<? super TFacade> sortComparator) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getResourceReferences(operation, facadeClass,
					holderToken, sortComparator);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade, THolderFacade> List<TFacade> getResourceReferences(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			ResourceToken<THolderFacade> holderToken,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getResourceReferences(operation, facadeClass,
					holderToken, filter, sortComparator);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> ResourceToken<TFacade> getResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass)
			throws MissingObjectException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getResourceToken(operation, facadeClass);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> ResourceToken<TFacade> getResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key) throws MissingObjectException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getResourceToken(operation, facadeClass, key);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> ResourceToken<TFacade> getResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2) throws MissingObjectException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getResourceToken(operation, facadeClass, key1, key2);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> ResourceToken<TFacade> getResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3)
			throws MissingObjectException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getResourceToken(operation, facadeClass, key1, key2,
					key3);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> ResourceToken<TFacade> getResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3, Object... otherKeys)
			throws MissingObjectException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getResourceToken(operation, facadeClass, key1, key2,
					key3, otherKeys);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getTreeNode(operation, facadeClass);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key) throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getTreeNode(operation, facadeClass, key);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2) throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getTreeNode(operation, facadeClass, key1, key2);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context
					.getTreeNode(operation, facadeClass, key1, key2, key3);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getTreeNode(operation, facadeClass, key1, key2,
					key3, otherKeys);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getTreeNode(operation, facadeClass, filter);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter, Object key)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getTreeNode(operation, facadeClass, filter, key);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter, Object key1, Object key2)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getTreeNode(operation, facadeClass, filter, key1,
					key2);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter, Object key1, Object key2,
			Object key3) throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getTreeNode(operation, facadeClass, filter, key1,
					key2, key3);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter, Object key1, Object key2,
			Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getTreeNode(operation, facadeClass, filter, key1,
					key2, key3, otherKeys);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getTreeNode(operation, facadeClass, sortComparator);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getTreeNode(operation, facadeClass, sortComparator,
					key);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getTreeNode(operation, facadeClass, sortComparator,
					key1, key2);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getTreeNode(operation, facadeClass, sortComparator,
					key1, key2, key3);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getTreeNode(operation, facadeClass, sortComparator,
					key1, key2, key3, otherKeys);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getTreeNode(operation, facadeClass, filter,
					sortComparator);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getTreeNode(operation, facadeClass, filter,
					sortComparator, key);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getTreeNode(operation, facadeClass, filter,
					sortComparator, key1, key2);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getTreeNode(operation, facadeClass, filter,
					sortComparator, key1, key2, key3);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getTreeNode(operation, facadeClass, filter,
					sortComparator, key1, key2, key3, otherKeys);
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final GUID getUserCurrentOrg() {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			return context.getUserCurrentOrg();
		} finally {
			save.updateContextSpace(context);
		}
	}

	public final void setUserCurrentOrg(GUID orgID) {
		final ContextImpl<?, ?, ?> context = this.usingSituation();
		final SpaceNode save = this.space.updateContextSpace(context);
		try {
			context.setUserCurrentOrg(orgID);
		} finally {
			save.updateContextSpace(context);
		}
	}

}
