/**
 * 
 */
package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jt.core.ObjectQuerier;
import org.eclipse.jt.core.def.NamedDefine;
import org.eclipse.jt.core.def.model.ModelScriptEngine;
import org.eclipse.jt.core.impl.MetaElementLoadStep.MetaElementLoadState;
import org.eclipse.jt.core.misc.ExceptionCatcher;
import org.eclipse.jt.core.misc.SXElementBuilder;


final class ResolveHelper {

	public final ObjectQuerier querier;
	public final ExceptionCatcher catcher;
	public final SXElementBuilder sxBuilder;
	public final ContextImpl<?, ?, ?> context;
	private final Object[] adArgs;

	final <TObject> TObject newObject(Class<TObject> clazz, Space space) {
		final SpaceNode oldSave = space.updateContextSpace(this.context);
		try {
			return space.newObjectInNode(clazz, null, this.adArgs);
		} finally {
			oldSave.updateContextSpace(this.context);
		}
	}

	final void regInfoGroupLanguage(String infoGroupFullName, String language,
			String[] infoNameMessages) {
		this.context.session.application.regInfoGroupLanguage(
				infoGroupFullName, language, infoNameMessages);
	}

	final boolean tryInitService(ServiceBase<?> service) throws Throwable {
		return service.tryInit(this.context);
	}

	private final ArrayList<ResourceServiceBase<?, ?, ?>> servicesCache = new ArrayList<ResourceServiceBase<?, ?, ?>>();

	final void tryBuildResourceKeyPathInfos(ServiceBase<?> service) {
		service.tryBuildResourceKeyPathInfos(this.servicesCache);
	}

	final void ensurePrepared(Space space, Prepareble toEnsure) {
		if (space.isDBValid() || !toEnsure.ignorePrepareIfDBInvalid()) {
			SpaceNode old = space.updateContextSpace(this.context);
			try {
				toEnsure.ensurePrepared(this.context, false);
			} finally {
				old.updateContextSpace(this.context);
			}
		}
	}

	private OperationMapImpl<?, ?> mapCache;

	@SuppressWarnings("unchecked")
	final void tryBuildResourceRefInfos(ServiceBase<?> service) {
		SpaceNode old = service.updateContextSpace(this.context);
		try {
			if (this.mapCache == null) {
				this.mapCache = new OperationMapImpl();
			}
			service.tryBuildResourceRefAuthInfo(this.mapCache);
		} finally {
			old.updateContextSpace(this.context);
		}
	}

	final void regModelScriptEngine(ModelScriptEngine<?> engine) {
		this.context.session.application.mseManager.regEngine(engine);
	}

	final void syncDBTable(Space space, TableDefineImpl table) throws Throwable {
		SpaceNode nodeSave = space.updateContextSpace(this.context);
		try {
			try {
				this.context.getDBAdapter().syncTable(table);
			} catch (Throwable e) {
				this.context.exception(e);
				throw Utils.tryThrowException(e);
			} finally {
				this.context.resolveTrans();
			}
		} finally {
			nodeSave.updateContextSpace(this.context);
		}
	}

	final void tryIntSyncTable(Site site, TD_CoreMetaData table)
			throws Throwable {
		if (site.isDBValid()) {
			this.syncDBTable(site, (TableDefineImpl) table.getDefine());
			this.regStartupEntry(MetaElementLoadStep.TABLES,
					new MetaElementLoadState(site, this, table));
		}
	}

	final void tryInitSiteInfoTable(Site site, TD_CoreSiteInfo table)
			throws Throwable {
		if (!site.isDBValid()) {
			return;
		}
		CoreSiteInfo siteInfo;
		SpaceNode nodeSave = site.updateContextSpace(this.context);
		try {
			try {
				final DBAdapterImpl dbadapter = this.context.getDBAdapter();
				dbadapter.syncTable((TableDefineImpl) table.getDefine());
				ORMAccessorProxy<CoreSiteInfo> orm = dbadapter
						.newORMAccessor((MappingQueryStatementImpl) table
								.getMappingQueryDefine());
				try {
					siteInfo = orm.first();
					if (siteInfo == null) {
						siteInfo = new CoreSiteInfo();
						siteInfo.RECID = site.application.newRECID();
						siteInfo.createTime = System.currentTimeMillis();
						orm.insert(siteInfo);
					}
				} finally {
					orm.unuse();
				}
			} catch (Throwable e) {
				this.context.exception(e);
				throw Utils.tryThrowException(e);
			} finally {
				this.context.resolveTrans();
			}
		} finally {
			nodeSave.updateContextSpace(this.context);
		}
		site.setSiteInfo(siteInfo);
	}

	private Map<StartupStep<StartupEntry>, StartupEntry> startupMap = new HashMap<StartupStep<StartupEntry>, StartupEntry>();

	@SuppressWarnings("unchecked")
	final void regStartupEntry(StartupStep<? extends StartupEntry> beginStep,
			StartupEntry entry) {
		StartupEntry oldTail = this.startupMap.put(
				(StartupStep<StartupEntry>) beginStep, entry);
		if (oldTail != null) {
			entry.nextInStep = oldTail.nextInStep;
			oldTail.nextInStep = entry;
		} else {
			entry.nextInStep = entry;
		}
	}

	final static void logStartInfo(String info) {
		synchronized (System.out) {
			ApplicationImpl.printDateTime(System.out);
			System.out.print(": D&A 启动...");
			System.out.println(info);
		}
	}

	final void startup() {
		while (!this.startupMap.isEmpty()) {
			// 得到优先级最高的步骤
			StartupStep<StartupEntry> highest = null;
			int highestPRI = Integer.MAX_VALUE;
			for (StartupStep<StartupEntry> aStep : this.startupMap.keySet()) {
				int pri = aStep.getPriority();
				// 最小的,优先级最高
				if (highest == null || pri < highestPRI) {
					highest = aStep;
					highestPRI = pri;
				}
			}
			logStartInfo(highest.getDescription());
			// 移除
			StartupEntry tail = this.startupMap.remove(highest);
			StartupEntry head;
			do {
				head = tail.nextInStep;
				tail.nextInStep = head.nextInStep;
				StartupStep<StartupEntry> nextStep;
				try {
					nextStep = highest.doStep(this, head);
				} catch (Throwable e) {
					this.catcher.catchException(e, head);
					continue;
				}
				if (nextStep != null && nextStep.getPriority() > highestPRI) {
					this.regStartupEntry(nextStep, head);
				} else {
					head.nextInStep = null;// helpGC
				}
			} while (head != tail);
		}
		logStartInfo("完毕");
	}

	ResolveHelper(ContextImpl<?, ?, ?> context, SXElementBuilder sxBuilder) {
		if (context == null || sxBuilder == null) {
			throw new NullPointerException();
		}
		this.context = context;
		this.catcher = context.catcher;
		this.querier = new FilteredObjectQuerier(context) {
			@Override
			protected final boolean isValidFacadeClass(Class<?> facadeClass) {
				return facadeClass == Class.class
						|| NamedDefine.class.isAssignableFrom(facadeClass)
						|| DeclaratorBase.class.isAssignableFrom(facadeClass)
						|| ServiceBase.class.isAssignableFrom(facadeClass);
			}
		};
		this.sxBuilder = sxBuilder;
		this.adArgs = new Object[] { this.querier };
	}
}