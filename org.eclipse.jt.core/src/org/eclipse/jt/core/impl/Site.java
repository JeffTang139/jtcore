package org.eclipse.jt.core.impl;

import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.jt.core.ContextKind;
import org.eclipse.jt.core.SiteState;
import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.misc.SXElementBuilder;
import org.eclipse.jt.core.spi.application.SessionDisposeEvent;
import org.eclipse.jt.core.spi.metadata.LoadAllMetaDataTask;
import org.eclipse.jt.core.spi.metadata.LoadMetaDataEvent;
import org.eclipse.jt.core.type.GUID;


/**
 * 站点
 * 
 * @author Jeff Tang
 * 
 */
final class Site extends Space {
	private final AtomicInteger transactionIDs = new AtomicInteger();

	final TransactionImpl newTransaction() {
		int tid;
		for (;;) {
			tid = this.transactionIDs.incrementAndGet();
			if (tid != TransactionImpl.INVALID_TRANSACTION_ID) {
				break;
			}
		}
		return new TransactionImpl(this, tid, true);
	}

	/**
	 * 站点所使用的bundles
	 */
	private final BundleStub[] bundles;

	private final void doInit(ContextImpl<?, ?, ?> context, SiteState state)
			throws Throwable {
		final ResolveHelper helper = new ResolveHelper(context,
				new SXElementBuilder());
		if (state != SiteState.LOADING_METADATA) {
			// 不启动日志管理器
			helper.regStartupEntry(LogManagerStartupStep.PREPARE,
					new RefStartupEntry<LogManager>(this.logManager));
		}
		final SpaceNode spaceSave = this.updateContextSpace(context);
		try {
			// 各站点装载装载的bundle内发布的元素
			for (BundleStub bundle : this.bundles) {
				bundle.gatherElement(this, helper);
			}
			this.state = SiteState.INITING;
			helper.startup();
			this.state = state;
		} finally {
			spaceSave.updateContextSpace(context);
		}
	}

	final ContextImpl<?, ?, ?> newSystemSessionSiteContext(ContextKind kind) {
		return this.application.getSystemSession().newContext(this, kind);
	}

	final void active() throws Throwable {
		final ContextImpl<?, ?, ?> context = this
				.newSystemSessionSiteContext(ContextKind.INITER);
		try {
			this.doInit(context, SiteState.ACTIVE);
		} finally {
			context.dispose();
		}
	}

	final void load(ContextImpl<?, ?, ?> context, LoadAllMetaDataTask task)
			throws Throwable {
		// 是否需要考虑让启动与装载在同一事务里？
		context.setNextStep(0.05f);
		this.doInit(context, SiteState.LOADING_METADATA);
		final SpaceNode spaceSave = this.updateContextSpace(context);
		try {
			final LoadMetaDataEvent event = new LoadMetaDataEvent(task
					.getMethod() == LoadAllMetaDataTask.LoadMode.MERGE,
					task.metaData, task.logger);
			event.getMetaStream().use();
			try {
				context.setNextStep(0.95f);
				context.dispatch(event);
			} finally {
				event.getMetaStream().unuse();
			}
		} finally {
			this.state = SiteState.DISPOSING;
			spaceSave.updateContextSpace(context);
		}
		// 数秒
		final long finishTime = System.currentTimeMillis();
		task.finishTime = finishTime;
		synchronized (task) {
			for (long needWait = task.getRestartDelay(); needWait > 0; needWait = task
					.getRestartDelay()
					- (System.currentTimeMillis() - finishTime)) {
				task.wait(needWait);
			}
		}
	}

	private final static EventListenerChain unInintTag = new EventListenerChain(
			null);
	private EventListenerChain sessionDisposeEventListeners = unInintTag;

	private final static SessionDisposeEvent disposeEvent = new SessionDisposeEvent();

	final ContextImpl<?, ?, ?> sessionDisposing(SessionImpl session,
			ContextImpl<?, ?, ?> context) {
		EventListenerChain listeners = this.sessionDisposeEventListeners;
		if (listeners == unInintTag) {
			this.sessionDisposeEventListeners = listeners = this.collectEvent(
					SessionDisposeEvent.class, InvokeeQueryMode.IN_SITE);
		}
		if (listeners != null) {
			if (context == null) {
				context = session.newContext(this, ContextKind.DISPOSER);
			}
			try {
				context.processEvents(listeners, disposeEvent, false);
			} catch (Throwable e) {
				// 忽略
			}
		}
		return context;
	}

	/**
	 * 站点状态
	 */
	volatile SiteState state = SiteState.INITING;

	/**
	 * 清除站点
	 * 
	 * @param catcher
	 */
	@Override
	void doDispose(ContextImpl<?, ?, ?> context) {
		try {
			super.doDispose(context);
		} finally {
			this.state = SiteState.DISPOSED;
		}
	}

	/**
	 * 站点ID
	 */
	GUID id;

	final int asSimpleID() {
		final GUID siteid = this.id;
		return siteid == null ? 0
				: (int) ((siteid.getMostSigBits() >>> TimeRelatedSequenceImpl.TIME_ZOOM_SHIFT) ^ siteid
						.getLeastSigBits());
	}

	final void setSiteInfo(CoreSiteInfo siteInfo) {
		this.id = siteInfo.RECID;
	}

	@Override
	public final String toString() {
		return this.name + " (site)";
	}

	final Space tryLocateSpace(String spacePath, char spaceSeparator) {
		Space space = this;
		if (spacePath != null && spacePath.length() > 0) {
			int start = spacePath.charAt(0) == spaceSeparator ? 1 : 0;
			int eof = spacePath.length();
			while (start < eof) {
				int end = spacePath.indexOf(spaceSeparator, start);
				if (end < 0) {
					end = eof;
				}
				if (end > start) {
					Space childSpace = space.findSub(spacePath, start, end
							- start);
					if (childSpace == null) {
						break;
					}
					space = childSpace;
				}
				start = end + 1;
			}
		}
		return space;
	}

	final Space ensureSpace(String spacePath, char spaceSeparator) {
		Space space = this;
		if (spacePath != null && spacePath.length() > 0) {
			int start = spacePath.charAt(0) == spaceSeparator ? 1 : 0;
			int eof = spacePath.length();
			while (start < eof) {
				int end = spacePath.indexOf(spaceSeparator, start);
				if (end < 0) {
					end = eof;
				}
				if (end > start) {
					Space sub = space.findSub(spacePath, start, end - start);
					if (sub == null) {
						sub = new Space(space, spacePath.substring(start, end));
					}
					space = sub;
				}
				start = end + 1;
			}
		}
		return space;
	}

	/**
	 * 应用对象
	 */
	final ApplicationImpl application;
	final LogManager logManager;

	@Override
	final Site asSite() {
		return this;
	}

	final static String xml_attr_name = "name";

	private static final String getSiteName(SXElement siteInfo) {
		if (siteInfo != null) {
			final String name = siteInfo.getAttribute(xml_attr_name);
			if (name != null && name.length() > 0) {
				return name;
			}
		}
		return "default";
	}

	final void fillSiteConnectionInfos(SXElement siteInfo) {
		if (this.application.dataSourceManager.isEmpty()) {
			return;
		}
		final TreeMap<String, DataSourceRef> sourceRefs = new TreeMap<String, DataSourceRef>();
		if (siteInfo != null) {
			for (SXElement datasourcerefE : siteInfo.getChildren(
					Site.xml_element_datasourcerefs,
					DataSourceRef.xml_element_datasourceref)) {
				final String space = datasourcerefE.getAttribute(
						DataSourceRef.xml_attr_space, "");
				if (!sourceRefs.containsKey(space)) {
					try {
						DataSourceRef ref = new DataSourceRef(
								this.application.dataSourceManager,
								datasourcerefE);
						sourceRefs.put(space, ref);
					} catch (Throwable e) {
						this.application.catcher.catchException(e,
								this.application.dataSourceManager);
					}
				}
			}
		}
		if (!sourceRefs.containsKey("")) {
			this.dataSourceRef = new DataSourceRef(
					this.application.dataSourceManager.getDefaultSource());
		}
		for (Entry<String, DataSourceRef> e : sourceRefs.entrySet()) {
			this.ensureSpace(e.getKey(), '/').dataSourceRef = e.getValue();
		}
	}

	/**
	 * 空根站点的构造函数
	 */
	Site(ApplicationImpl application, SXElement siteInfo) {
		super(null, getSiteName(siteInfo));
		this.application = application;
		this.performanceIndexManager = new PerformanceIndexManagerImpl();
		this.globalResourceContainer = new GlobalResourceContainer(this);
		this.logManager = new LogManager(this);
		this.bundles = application.bundles.values().toArray(
				new BundleStub[application.bundles.size()]);
		this.fillSiteConnectionInfos(siteInfo);
	}

	// /////////////////////////////////////////////////////
	// ////// 启动
	// ////////////////////////////////////////////////////
	final static String xml_element_site = "site";
	final static String xml_element_publish = "publish";
	final static String xml_element_datasourcerefs = "datasource-refs";
	// ///////////////////////////// 全局资源 ///////////////////////////
	final GlobalResourceContainer globalResourceContainer;
	// /////////////////////////////////////////////
	// //////////性能监控///////////////////////////

	final PerformanceIndexManagerImpl performanceIndexManager;

	// //////////性能监控///////////////////////////
	// /////////////////////////////////////////////

}
