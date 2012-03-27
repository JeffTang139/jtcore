package org.eclipse.jt.core.impl;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jt.core.def.DNASqlType;
import org.eclipse.jt.core.def.info.InfoGroupDeclarator;
import org.eclipse.jt.core.def.model.ModelDeclarator;
import org.eclipse.jt.core.def.query.ModifyStatementDeclarator;
import org.eclipse.jt.core.def.query.ORMDeclarator;
import org.eclipse.jt.core.def.query.QueryStatementDeclarator;
import org.eclipse.jt.core.def.query.StoredProcedureDeclarator;
import org.eclipse.jt.core.def.table.TableDeclarator;
import org.eclipse.jt.core.def.table.TableDefine;


final class PublishedDeclarator extends PublishedElement {

	final Class<? extends DeclaratorBase> clazz;
	DeclaratorBase ref;

	PublishedDeclarator(Class<? extends DeclaratorBase> clazz) {
		this.clazz = clazz;
	}

	@Override
	public String toString() {
		return this.clazz == null ? null : "[" + this.clazz.getName() + "]";
	}

	static class CreateStep extends StartupStepBase<PublishedDeclarator> {

		final Class<? extends DeclaratorBase> baseClass;

		private final boolean tryLoadScript;

		/**
		 * 尝试对各种依赖脚本声明的声明器装载脚本的URL，备随后解析使用
		 */
		final boolean tryLoadScript(PublishedDeclarator pe, ResolveHelper helper) {
			if (this.tryLoadScript) {
				DNASqlType et = DNASqlType.typeOfDeclaratorClass(pe.clazz);
				if (et == null) {
					return false;
				}
				final String postfix = et.declareScriptPostfix;
				if (postfix == null) {
					return false;
				}
				final int postfixL = postfix.length();
				if (postfixL == 0) {
					return false;
				}
				final String className = pe.clazz.getName();
				final int classNameL = className.length();
				final char[] resourceName = new char[classNameL + postfixL + 1];
				className.getChars(0, classNameL, resourceName, 0);
				int nameCharStart = 0;
				for (int i = 0; i < classNameL; i++) {
					if (resourceName[i] == '.') {
						resourceName[i] = '/';
						nameCharStart = i + 1;
					}
				}
				resourceName[classNameL] = '.';
				postfix.getChars(0, postfixL, resourceName, classNameL + 1);
				final ClassLoader cl = pe.clazz.getClassLoader();
				final URL url = cl.getResource(Utils.fastString(resourceName));
				if (url != null) {
					return pe.space.regDeclareScript(className.substring(
							nameCharStart, classNameL), et, url,
							pe.publishMode, helper.catcher);
				}
			}
			return false;
		}

		CreateStep(int priority, String description,
				Class<? extends DeclaratorBase> baseClass) {
			super(priority, description);
			this.baseClass = baseClass;
			this.tryLoadScript = DNASqlType.declareScirptSupported(baseClass);
		}

		CreateStep(StartupStepBase<?> previous, int pd, String description,
				Class<? extends DeclaratorBase> baseClass) {
			super(previous, pd, description);
			this.baseClass = baseClass;
			this.tryLoadScript = DNASqlType.declareScirptSupported(baseClass);
		}

		StartupStep<PublishedDeclarator> nextStep(ResolveHelper helper,
				PublishedDeclarator target) throws Throwable {
			return null;
		}

		@Override
		public final StartupStep<PublishedDeclarator> doStep(
				ResolveHelper helper, PublishedDeclarator target)
				throws Throwable {
			synchronized (DeclaratorBase.class) {
				DeclaratorBase.newInstanceByCore = helper.context;
				try {
					target.ref = helper.newObject(target.clazz, target.space);
					target.ref.bundle = target.bundle;
				} finally {
					DeclaratorBase.newInstanceByCore = null;
				}
			}
			target.space.regDeclarator(target.ref, target.publishMode,
					helper.catcher);
			return this.nextStep(helper, target);
		}
	}

	static class RefStep extends StartupStepBase<PublishedDeclarator> {
		RefStep(StartupStepBase<?> previous, int pd, String discription) {
			super(previous, pd, discription);
		}

		StartupStep<PublishedDeclarator> nextStep(ResolveHelper helper,
				PublishedDeclarator target) {
			return null;
		}

		@Override
		public final StartupStep<PublishedDeclarator> doStep(
				ResolveHelper helper, PublishedDeclarator target)
				throws Throwable {
			target.ref.tryDeclareUseRef(helper.querier);
			return this.nextStep(helper, target);
		}
	}

	static class PrepareStep extends StartupStepBase<PublishedDeclarator> {
		PrepareStep(StartupStepBase<?> previous, int pd, String discription) {
			super(previous, pd, discription);
		}

		StartupStep<PublishedDeclarator> nextStep(ResolveHelper helper,
				PublishedDeclarator target) {
			return null;
		}

		@Override
		public final StartupStep<PublishedDeclarator> doStep(
				ResolveHelper helper, PublishedDeclarator target)
				throws Throwable {
			(helper).ensurePrepared(target.space, (Prepareble) target.ref
					.getDefine());
			return this.nextStep(helper, target);
		}
	}

	// 信息定义
	static final CreateStep information_create = new CreateStep(
			StartupStep.DECLARATOR_HIGHEST_PRI, "实例化信息定义",
			InfoGroupDeclarator.class) {
		@Override
		StartupStep<PublishedDeclarator> nextStep(ResolveHelper helper,
				PublishedDeclarator target) throws Throwable {
			return information_prepare;
		}
	};
	static final PrepareStep information_prepare = new PrepareStep(
			information_create, 0x100, "装载多语言信息") {
	};

	// 表实例化
	static final CreateStep table_create = new CreateStep(information_prepare,
			0x100, "实例化表定义", TableDeclarator.class) {
		@Override
		StartupStep<PublishedDeclarator> nextStep(ResolveHelper helper,
				PublishedDeclarator target) throws Throwable {
			if (target.clazz == TD_CoreMetaData.class) {
				helper.tryIntSyncTable(target.space.site,
						(TD_CoreMetaData) target.ref);
				helper.context.occorAt.site.regNamedDefineToSpace(
						TableDefine.class, TableDefineImpl.DUMMY,
						helper.catcher);
			} else if (target.clazz == TD_CoreSiteInfo.class) {
				helper.tryInitSiteInfoTable(target.space.site,
						(TD_CoreSiteInfo) target.ref);
			} else {
				return table_ref;
			}
			return null;
		}
	};

	// 装载自定义表
	static final int STEP_LOAD_CUSTOM_TABLES = table_create.getPriority() + 0x10;

	// 明确表对其他表的引用
	static final RefStep table_ref = new RefStep(table_create, 0x100,
			"确定表定义对其他元素的引用") {
		@Override
		StartupStep<PublishedDeclarator> nextStep(ResolveHelper helper,
				PublishedDeclarator target) {
			if (target.space.isDBValid()) {
				return table_sync;
			}
			return null;
		}
	};
	// 明确自定义表对其他元数据的引用
	static final int STEP_RESOLVE_CUSTOM_TABLES_REF = table_ref.getPriority() + 0x10;

	// 同步数据库表结构
	static final StartupStepBase<PublishedDeclarator> table_sync = new StartupStepBase<PublishedDeclarator>(
			table_ref, 0x100, "同步数据库表结构") {
		@Override
		public StartupStep<PublishedDeclarator> doStep(ResolveHelper helper,
				PublishedDeclarator target) throws Throwable {
			helper.syncDBTable(target.space, (TableDefineImpl) target.ref
					.getDefine());
			return null;
		}
	};

	static final int STEP_SAVE_CUSTOM_TABLES_METADATA = table_sync
			.getPriority() + 0x10;

	// ORM实例化
	static final CreateStep orm_create = new CreateStep(table_sync, 0x100,
			"实例化ORM定义", ORMDeclarator.class) {
		@Override
		StartupStep<PublishedDeclarator> nextStep(ResolveHelper helper,
				PublishedDeclarator target) {
			return orm_ref;
		}

	};
	// 明确ORM对其他表的引用
	static final RefStep orm_ref = new RefStep(orm_create, 0x100,
			"确定ORM定义对其他元素的引用") {
		@Override
		StartupStep<PublishedDeclarator> nextStep(ResolveHelper helper,
				PublishedDeclarator target) {
			return orm_prepare;
		}

	};
	// 准备ORM
	static final PrepareStep orm_prepare = new PrepareStep(orm_ref, 0x100,
			"初始化ORM定义") {
	};
	// query实例化
	static final CreateStep query_create = new CreateStep(orm_prepare, 0x100,
			"实例化查询定义", QueryStatementDeclarator.class) {
		@Override
		StartupStep<PublishedDeclarator> nextStep(ResolveHelper helper,
				PublishedDeclarator target) {
			return query_ref;
		}
	};
	// 明确query对其他表的引用
	static final RefStep query_ref = new RefStep(query_create, 0x100,
			"确定查询定义对其他元素的引用") {
		@Override
		StartupStep<PublishedDeclarator> nextStep(ResolveHelper helper,
				PublishedDeclarator target) {
			return query_prepare;
		}

	};
	// 准备query
	static final PrepareStep query_prepare = new PrepareStep(query_ref, 0x100,
			"初始化查询定义") {
	};
	// command实例化
	static final CreateStep command_create = new CreateStep(query_prepare,
			0x100, "实例化命令定义", ModifyStatementDeclarator.class) {
		@Override
		StartupStep<PublishedDeclarator> nextStep(ResolveHelper helper,
				PublishedDeclarator target) {
			return command_ref;
		}
	};
	// 明确command对其他表的引用
	static final RefStep command_ref = new RefStep(command_create, 0x100,
			"确定命令定义对其他元素的引用") {
		@Override
		StartupStep<PublishedDeclarator> nextStep(ResolveHelper helper,
				PublishedDeclarator target) {
			return command_prepare;
		}

	};

	// 准备command
	static final PrepareStep command_prepare = new PrepareStep(command_ref,
			0x100, "初始化命令定义") {
		@Override
		StartupStep<PublishedDeclarator> nextStep(ResolveHelper helper,
				PublishedDeclarator target) {
			if (target.ref instanceof StoredProcedureDeclarator) {
				return sp_sync;
			}
			return null;
		}
	};

	// 明确command对其他表的引用
	static final StartupStepBase<PublishedDeclarator> sp_sync = new StartupStepBase<PublishedDeclarator>(
			command_prepare, 0x100, "同步存储过程") {
		@Override
		public StartupStep<PublishedDeclarator> doStep(ResolveHelper helper,
				PublishedDeclarator target) throws Throwable {
			StoredProcedureDefineImpl sp = (StoredProcedureDefineImpl) ((StoredProcedureDeclarator) target.ref)
					.getDefine();
			try {
				DBAdapterImpl adapter = helper.context.getDBAdapter();
				Statement stmt = adapter.createStatement();
				try {
					stmt.execute(sp.getProcedureDDL());
				} finally {
					adapter.freeStatement(stmt);
				}
			} catch (SQLException e) {
				throw new SQLException("创建存储过程[" + sp.name + "]错误");
			}
			return null;
		}

	};

	// model实例化
	static final CreateStep model_create = new CreateStep(sp_sync, 0x100,
			"实例化模型定义", ModelDeclarator.class) {
		@Override
		StartupStep<PublishedDeclarator> nextStep(ResolveHelper helper,
				PublishedDeclarator target) {
			return model_ref;
		}
	};
	// 装载自定义model
	static final int STEP_LOAD_CUSTOM_MODELS = model_create.getPriority() + 0x10;
	// 明确model对其他表的引用
	static final RefStep model_ref = new RefStep(model_create, 0x100,
			"确定模型定义对其他元素的引用") {
		@Override
		StartupStep<PublishedDeclarator> nextStep(ResolveHelper helper,
				PublishedDeclarator target) {
			return model_prepare;
		}
	};
	// 明确自定义model对其他元数据的引用
	static final int STEP_RESOLVE_CUSTOM_MODELS_REF = model_ref.getPriority() + 1;
	// 准备model
	static final PrepareStep model_prepare = new PrepareStep(model_ref, 0x100,
			"初始化模型定义") {
	};
	final static String xml_element_info = "info-group";
	final static String xml_element_table = "table";
	final static String xml_element_orm = "orm";
	final static String xml_element_query = "query";
	final static String xml_element_command = "command";
	final static String xml_element_model = "model";
	static final Map<String, CreateStep> beginSteps = new HashMap<String, CreateStep>();
	static {
		beginSteps.put(xml_element_info, information_create);
		beginSteps.put(xml_element_table, table_create);
		beginSteps.put(xml_element_query, query_create);
		beginSteps.put(xml_element_command, command_create);
		beginSteps.put(xml_element_orm, orm_create);
		beginSteps.put(xml_element_model, model_create);
	}
}
