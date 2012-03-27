package org.eclipse.jt.core.impl;

import java.util.HashMap;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.ContextKind;
import org.eclipse.jt.core.None;
import org.eclipse.jt.core.SiteState;
import org.eclipse.jt.core.da.ORMAccessor;
import org.eclipse.jt.core.def.MetaElementType;
import org.eclipse.jt.core.def.NamedDefine;
import org.eclipse.jt.core.def.model.ModelDeclare;
import org.eclipse.jt.core.def.model.ModelDefine;
import org.eclipse.jt.core.def.table.TableDeclare;
import org.eclipse.jt.core.def.table.TableDefine;
import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.service.Publish;
import org.eclipse.jt.core.spi.application.RestartRootSiteTask;
import org.eclipse.jt.core.spi.def.DeclarePostTask;
import org.eclipse.jt.core.spi.def.DeclareRemoveTask;
import org.eclipse.jt.core.spi.metadata.LoadAllMetaDataTask;
import org.eclipse.jt.core.spi.metadata.LoadMetaDataEvent;
import org.eclipse.jt.core.type.GUID;


/**
 * 系统服务
 * 
 * @author Jeff Tang
 * 
 */
final class SystemService extends ServiceBase<ContextImpl<?, ?, ?>> {

	private final TD_CoreMetaData td_coremd;

	SystemService(TD_CoreMetaData td_coremd) {
		super("系统服务");
		this.td_coremd = td_coremd;
	}

	@Override
	protected void init(Context context) throws Throwable {
		super.init(context);
	}

	// ///////////////////////////////////////////////
	// //////// Model ////////////////////////////////
	// ///////////////////////////////////////////////

	@Publish
	final class ModelDeclareByNameProvider extends
			OneKeyResultProvider<ModelDeclare, String> {

		@Override
		protected ModelDeclare provide(ContextImpl<?, ?, ?> context, String name)
				throws Throwable {
			// TODO 克隆出来再修改
			ModelDeclare md = (ModelDeclare) SystemService.this.site
					.findNamedDefine(ModelDefine.class, name);
			return md;
		}
	}

	static final void saveTable(ContextImpl<?, ?, ?> context,
			TD_CoreMetaData td_coremd, TableDefineImpl table, boolean forRemove)
			throws Throwable {
		ORMAccessorProxy<CoreMetaData> orm = context.newORMAccessor(td_coremd);
		try {
			saveTable(context, orm, table, forRemove);
		} finally {
			orm.unuse();
		}
	}

	static final void saveTable(ContextImpl<?, ?, ?> context,
			ORMAccessorProxy<CoreMetaData> orm, TableDefineImpl table,
			boolean forRemove) throws Throwable {
		if (forRemove) {
			if (table.id != null) {
				orm.delete(table.id);
			}
			return;
		}
		SXElement xml = SXElement.newDoc();
		table.renderInto(xml);
		CoreMetaData meta = new CoreMetaData();
		meta.kind = MetaElementType.TABLE;
		meta.name = table.name;
		meta.space = "";
		meta.xml = xml.toString();
		if ((meta.RECID = table.id) != null) {
			orm.update(meta);
		} else {
			orm.deleteByPKey(meta.kind, meta.name);
			table.id = meta.RECID = context.newRECID();
			orm.insert(meta);
		}
	}

	static final void saveModel(ContextImpl<?, ?, ?> context,
			TD_CoreMetaData td_coremd, ModelDefineImpl model) throws Throwable {
		if (td_coremd == null) {
			return;
		}
		SXElement xml = SXElement.newDoc();
		model.renderInto(xml);
		CoreMetaData meta = new CoreMetaData();
		ORMAccessorProxy<CoreMetaData> orm = context.getDBAdapter()
				.newORMAccessor(
						(MappingQueryStatementImpl) td_coremd
								.getMappingQueryDefine());
		try {
			meta.kind = MetaElementType.MODEL;
			meta.name = model.name;
			meta.space = "";
			meta.xml = xml.toString();
			if ((meta.RECID = model.id) != null) {
				orm.update(meta);
			} else {
				model.id = meta.RECID = context.newRECID();
				orm.insert(meta);
			}
		} finally {
			orm.unuse();
		}
	}

	final static Class<? extends NamedDefine> defineIntfClassOf(
			NamedDefineImpl declare) {
		if (declare instanceof TableDefineImpl) {
			return TableDefine.class;
		} else if (declare instanceof ModelDefineImpl) {
			return ModelDefine.class;
		}
		throw new UnsupportedOperationException("暂不支持提交的定义:" + declare);
	}

	private static class PendingTableEntry {

		TableDefineImpl table;
		boolean forRemove;

		PendingTableEntry(TableDefineImpl table, boolean forRemove) {
			this.table = table;
			this.forRemove = forRemove;
		}
	}

	private final HashMap<String, PendingTableEntry> pendingTables = new HashMap<String, PendingTableEntry>();

	private final void postTable(ContextImpl<?, ?, ?> context,
			TableDefineImpl post) throws Throwable {
		if (post.name.equals(TableDefineImpl.DUMMY_NAME)) {
			throw new UnsupportedOperationException("不支持修改DUMMY表.");
		}
		synchronized (this.pendingTables) {
			TableDefineImpl runtime = (TableDefineImpl) this.site
					.findNamedDefine(TableDefine.class, post.name);
			if (runtime == null) {
				runtime = post.clone(context);
				context.getDBAdapter().syncTable(runtime);
				if (this.site.state == SiteState.LOADING_METADATA) {
					// 保留到最后再一次性提交
					this.pendingTables.put(runtime.name, new PendingTableEntry(
							runtime, false));
				} else {
					saveTable(context, this.td_coremd, runtime, false);
				}
				this.site.regNamedDefineToSpace(TableDefine.class, runtime,
						context.catcher);
			} else {
				if (post.id != null && !post.id.equals(runtime.id)) {
					throw new IllegalArgumentException();
				}
				context.getDBAdapter().postTable(post, runtime);
				runtime.assignFrom(post, context);
				if (this.site.state == SiteState.LOADING_METADATA) {
					// 保留到最后再一次性提交
					this.pendingTables.put(runtime.name, new PendingTableEntry(
							runtime, false));
				} else {
					saveTable(context, this.td_coremd, runtime, false);
				}
			}
		}
	}

	private final void postPendingTables(ContextImpl<?, ?, ?> context)
			throws Throwable {
		synchronized (this.pendingTables) {
			if (this.pendingTables.isEmpty()) {
				return;
			}
			DBAdapterImpl dbAdapter = context.getDBAdapter();
			final ORMAccessorProxy<CoreMetaData> orm = context
					.newORMAccessor(this.td_coremd);
			try {
				try {
					for (PendingTableEntry entry : this.pendingTables.values()) {
						saveTable(context, orm, entry.table, entry.forRemove);
						if (entry.forRemove && entry.table.id != null) {
							dbAdapter.dropTable(entry.table);
						}
					}
				} finally {
					this.pendingTables.clear();
				}
			} finally {
				orm.unuse();
			}
		}
	}

	@Publish
	final class LoadMetaEventListener extends EventListener<LoadMetaDataEvent> {
		protected LoadMetaEventListener() {
			super(Float.MAX_VALUE);
		}

		@Override
		protected void occur(ContextImpl<?, ?, ?> context,
				LoadMetaDataEvent event) throws Throwable {
			SystemService.this.postPendingTables(context);
		}
	}

	@Publish
	final class DeclarePostHandler extends
			TaskMethodHandler<DeclarePostTask, None> {

		protected DeclarePostHandler() {
			super(None.NONE, null);
		}

		@Override
		protected void handle(ContextImpl<?, ?, ?> context, DeclarePostTask task)
				throws Throwable {
			NamedDefineImpl declare = (NamedDefineImpl) task.designed;
			if (declare instanceof TableDefineImpl) {
				SystemService.this
						.postTable(context, (TableDefineImpl) declare);
			} else if (declare instanceof ModelDefineImpl) {
				ModelDefineImpl model = (ModelDefineImpl) declare;
				model.ensurePrepared(context, true);
				saveModel(context, SystemService.this.td_coremd, model);
				NamedDefineImpl define = SystemService.this.site
						.findNamedDefine(ModelDefine.class, declare.name);
				if (define != declare) {
					SystemService.this.site.regNamedDefineToSpace(
							ModelDefine.class, declare, context.catcher);
				}
			} else {
				throw new UnsupportedOperationException();
			}
		}
	}

	@Publish
	final class DeclareRemoveHandler extends
			TaskMethodHandler<DeclareRemoveTask, None> {

		protected DeclareRemoveHandler() {
			super(None.NONE, null);
		}

		@Override
		protected void handle(ContextImpl<?, ?, ?> context,
				DeclareRemoveTask task) throws Throwable {
			GUID id = null;
			switch (task.type) {
			case TABLE:
				synchronized (SystemService.this.pendingTables) {
					TableDefineImpl runtime = (TableDefineImpl) SystemService.this.site
							.unRegNamedDefineFromSpace(TableDefine.class,
									task.name);
					if (runtime == null) {
						return;
					}
					if (SystemService.this.site.state == SiteState.LOADING_METADATA) {
						if (runtime.id != null) {
							// 保留到最后再一次性提交
							SystemService.this.pendingTables.put(runtime.name,
									new PendingTableEntry(runtime, true));
						} else {
							SystemService.this.pendingTables
									.remove(runtime.name);
						}
					} else {
						saveTable(context, SystemService.this.td_coremd,
								runtime, true);
						context.getDBAdapter().dropTable(runtime);
					}
				}
				break;
			case MODEL:
				NamedDefine model = SystemService.this.site
						.unRegNamedDefineFromSpace(ModelDefine.class, task.name);
				if (model != null) {
					id = ((ModelDefineImpl) model).id;
					if (id != null) {
						ORMAccessor<CoreMetaData> orm = context
								.getDBAdapter()
								.newORMAccessor(
										(MappingQueryStatementImpl) SystemService.this.td_coremd
												.getMappingQueryDefine());
						try {
							orm.delete(id);
						} finally {
							orm.unuse();
						}
					}
				}
				break;
			default:
				throw new UnsupportedOperationException("暂时还不支持[" + task.type
						+ "]原数据类型");

			}
		}
	}

	@Publish
	final class TableDeclareByNameProvider extends
			OneKeyResultProvider<TableDeclare, String> {

		@Override
		protected TableDeclare provide(ContextImpl<?, ?, ?> context, String name)
				throws Throwable {
			TableDeclare runtime = (TableDeclare) SystemService.this.site
					.findNamedDefine(TableDefine.class, name);
			if (runtime == null) {
				return new TableDefineImpl(name, null);
			}
			return ((TableDefineImpl) runtime).clone(context);
		}
	}

	// ////////////////////////////////////////////////////
	// // 参数合并
	// ///////////////////////////////////////////////////
	@Publish
	final class LoadAllMetaDataTaskHandler
			extends
			TaskMethodHandler<LoadAllMetaDataTask, LoadAllMetaDataTask.LoadMode> {

		protected LoadAllMetaDataTaskHandler() {
			super(
					LoadAllMetaDataTask.LoadMode.MERGE,
					new LoadAllMetaDataTask.LoadMode[] { LoadAllMetaDataTask.LoadMode.REPLACE });
		}

		@Override
		protected void handle(ContextImpl<?, ?, ?> context,
				LoadAllMetaDataTask task) throws Throwable {
			if (context.kind != ContextKind.TRANSIENT) {
				throw new UnsupportedOperationException(
						"必须在临时上下文中执行该任务（远程调用或异步调用）");
			}
			context.session.application.reLoadRootSite(context, task);
		}
	}

	@Publish
	final class RestartRootSiteTaskHandler extends
			TaskMethodHandler<RestartRootSiteTask, None> {

		protected RestartRootSiteTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected void handle(ContextImpl<?, ?, ?> context,
				RestartRootSiteTask task) throws Throwable {
			if (context.kind != ContextKind.TRANSIENT) {
				throw new UnsupportedOperationException(
						"必须在临时上下文中执行该任务（远程调用或异步调用）");
			}
			context.session.application.restartRootSite(context);
		}
	}

}
