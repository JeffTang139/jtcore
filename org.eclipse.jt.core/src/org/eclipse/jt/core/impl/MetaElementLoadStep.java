package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jt.core.def.MetaElementType;
import org.eclipse.jt.core.def.exp.ConditionalExpression;
import org.eclipse.jt.core.def.model.ModelDefine;
import org.eclipse.jt.core.def.table.TableDefine;
import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.misc.SXMergeHelper;
import org.eclipse.jt.core.type.Convert;
import org.eclipse.jt.core.type.GUID;


/**
 * 数据库中保存的元数据装载步骤
 * 
 * @author Jeff Tang
 * 
 */
enum MetaElementLoadStep implements
		StartupStep<MetaElementLoadStep.MetaElementLoadState> {

	TABLES(MetaElementType.TABLE, PublishedDeclarator.STEP_LOAD_CUSTOM_TABLES,
			"装载自定义表定义", new StartupStepBase<MetaElementLoadState>(
					PublishedDeclarator.STEP_RESOLVE_CUSTOM_TABLES_REF,
					"明确自定义表定义对其他元数据的引用") {
				@Override
				public StartupStep<MetaElementLoadState> doStep(
						ResolveHelper helper, MetaElementLoadState target)
						throws Throwable {
					target.mergeHelper.resolveDelayAction(TABLES, null);
					return MODELS;
				}
			}) {

		@Override
		void doMerge(ContextImpl<?, ?, ?> context, SXMergeHelper helper,
				TD_CoreMetaData td_coremd, SXElement element, GUID id,
				String name, ResolveHelper resolveHelper) throws Throwable {
			TableDefineImpl tableDefine = (TableDefineImpl) helper.querier
					.find(TableDefine.class, name);
			if (tableDefine == null) {
				tableDefine = new TableDefineImpl(name, null);
				tableDefine.merge(element, helper);
				tableDefine.id = id;
				try {
					context.getDBAdapter().syncTable(tableDefine);
				} catch (Throwable e) {
					context.exception(e);
					throw e;
				} finally {
					context.resolveTrans();
				}
				context.occorAt.site.regNamedDefineToSpace(TableDefine.class,
						tableDefine, context.catcher);
			} else {
				// 留到后续阶段同步
				tableDefine.merge(element, helper);
				tableDefine.id = id;
			}
			resolveHelper.regStartupEntry(save_table_meta,
					new SaveTableMetaEntry(td_coremd, tableDefine));
		}
	},

	/**
	 * 所有的模型完成合并
	 */
	MODELS(MetaElementType.MODEL, PublishedDeclarator.STEP_LOAD_CUSTOM_MODELS,
			"装载自定义模型定义", new StartupStepBase<MetaElementLoadState>(
					PublishedDeclarator.STEP_RESOLVE_CUSTOM_MODELS_REF,
					"明确自定义模型对其他元数据的引用") {
				@Override
				public StartupStep<MetaElementLoadState> doStep(
						ResolveHelper helper, MetaElementLoadState target)
						throws Throwable {
					target.mergeHelper.resolveDelayAction(MODELS, null);
					return null;
				}
			}) {
		@Override
		final void doMerge(ContextImpl<?, ?, ?> context,
				SXMergeHelper mergeHelper, TD_CoreMetaData td_coremetadata,
				SXElement element, GUID id, String name,
				ResolveHelper resolveHelper) throws Throwable {
			ModelDefineImpl modelDefine = (ModelDefineImpl) context.find(
					ModelDefine.class, name);
			if (modelDefine == null) {
				String className = element
						.getAttribute(ModelDefineImpl.xml_attr_moClass);
				modelDefine = new ModelDefineImpl(name, context.get(
						Class.class, className), null);
				modelDefine.merge(element, mergeHelper);
				context.occorAt.site.regNamedDefineToSpace(ModelDefine.class,
						modelDefine, context.catcher);
			} else {
				modelDefine.merge(element, mergeHelper);
			}
			modelDefine.ensurePrepared(context, true);
			modelDefine.id = id;
		}
	};

	final MetaElementType kind;
	final int priority;
	final String description;
	final StartupStep<MetaElementLoadState> nextStep;

	public String getDescription() {
		return this.description;
	}

	public int getPriority() {
		return this.priority;
	}

	MetaElementLoadStep(MetaElementType kind, int priority, String description,
			StartupStep<MetaElementLoadState> nextStep) {
		this.priority = priority;
		this.description = description;
		this.kind = kind;
		this.nextStep = nextStep;
	}

	static class MetaElementLoadState extends StartupEntry {

		private final List<CoreMetaData> metas;
		final SXMergeHelper mergeHelper;
		final TD_CoreMetaData td_coremd;

		// 早期版本的数据迁移
		@SuppressWarnings("unused")
		private final boolean tryUpgrade(
				ORMAccessorImpl<CoreMetaData> accessor,
				TD_CoreMetaData coreMeta, ResolveHelper helper) {
			if (coreMeta.f_xml == null) {
				return false;
			}
			DBCommandProxy command = null;
			try {
				for (CoreMetaData meta : this.metas) {
					if (meta.xml == null || meta.xml.length() == 0) {
						if (command == null) {
							QueryStatementImpl query = new QueryStatementImpl(
									"xml_data2xml");
							QuRelationRef tr = query.newReference(coreMeta);
							tr.newColumn(coreMeta.getDefine().getFields().get(
									"xml_data"));
							ConditionalExpression conditon = tr.expOf(
									coreMeta.f_RECID).xEq(
									query.newArgument(coreMeta.f_RECID));
							query.setCondition(conditon);
							command = helper.context.prepareStatement(query);
						}
						command.setArgumentValue(0, meta.RECID);
						meta.xml = Convert.UTF8ToString((byte[]) command
								.executeScalar());
						accessor.update(meta);
					}
				}
			} finally {
				if (command != null) {
					command.unuse();
				}
			}
			return true;
		}

		MetaElementLoadState(SpaceNode space, ResolveHelper helper,
				TD_CoreMetaData td_coremd) {
			this.td_coremd = td_coremd;
			SpaceNode nodeSave = space.updateContextSpace(helper.context);
			try {
				if (space.isDBValid()) {
					ORMAccessorProxy<CoreMetaData> accessor = helper.context
							.newORMAccessor(td_coremd);
					try {
						this.metas = accessor.fetch();
						// this.tryUpgrade(accessor, coreMeta, helper);
					} finally {
						accessor.unuse();
					}
				} else {
					this.metas = new ArrayList<CoreMetaData>(0);
				}
			} finally {
				nodeSave.updateContextSpace(helper.context);
			}

			this.mergeHelper = new SXMergeHelper(helper.querier, helper.catcher);
		}

		final Iterable<CoreMetaData> getMetas(ResolveHelper helper)
				throws Throwable {
			return this.metas;
		}
	}

	abstract void doMerge(ContextImpl<?, ?, ?> context,
			SXMergeHelper mergeHelper, TD_CoreMetaData td_coremetadata,
			SXElement xml, GUID id, String name, ResolveHelper resolveHelper)
			throws Throwable;

	public final StartupStep<MetaElementLoadState> doStep(ResolveHelper helper,
			MetaElementLoadState target) throws Throwable {
		if (this.kind != null) {
			for (CoreMetaData meta : target.getMetas(helper)) {
				if (meta.kind == this.kind) {
					try {
						SXElement element = helper.sxBuilder.build(meta.xml)
								.firstChild();
						if (element != null) {
							String name = element
									.getAttribute(NamedDefineImpl.xml_attr_name);
							this.doMerge(helper.context, target.mergeHelper,
									target.td_coremd, element, meta.RECID,
									name, helper);
						} else {
							throw new UnsupportedOperationException(meta.kind
									+ "[" + meta.name + "] xml 为空");
						}
					} catch (Throwable e) {
						helper.catcher.catchException(e, meta);
					}
				}
			}
		}
		return this.nextStep;
	}

	static final StartupStep<SaveTableMetaEntry> save_table_meta = new StartupStep<SaveTableMetaEntry>() {

		public StartupStep<SaveTableMetaEntry> doStep(ResolveHelper helper,
				SaveTableMetaEntry target) throws Throwable {
			SystemService.saveTable(helper.context, target.td_coremd,
					target.table, false);
			return null;
		}

		public String getDescription() {
			return "保存自定义表元数据";
		}

		public int getPriority() {
			return PublishedDeclarator.STEP_SAVE_CUSTOM_TABLES_METADATA;
		}
	};

	static final class SaveTableMetaEntry extends StartupEntry {

		final TableDefineImpl table;

		final TD_CoreMetaData td_coremd;

		SaveTableMetaEntry(TD_CoreMetaData td_coremd, TableDefineImpl table) {
			this.td_coremd = td_coremd;
			this.table = table;
		}
	}

}
