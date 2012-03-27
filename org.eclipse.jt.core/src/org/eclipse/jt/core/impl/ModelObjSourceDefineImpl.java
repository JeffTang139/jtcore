package org.eclipse.jt.core.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.None;
import org.eclipse.jt.core.def.model.ModelDeclare;
import org.eclipse.jt.core.def.model.ModelObjSourceDeclare;
import org.eclipse.jt.core.def.obja.DynamicObject;
import org.eclipse.jt.core.def.query.MappingQueryStatementDefine;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.impl.ModelServiceBase.ModelObjProvider;
import org.eclipse.jt.core.misc.ObjectBuilder;
import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.misc.SXMergeHelper;
import org.eclipse.jt.core.model.ModelService;
import org.eclipse.jt.core.type.AssignCapability;


/**
 * 模型实体源定义实现类
 * 
 * @author Jeff Tang
 * 
 */
final class ModelObjSourceDefineImpl extends ArgumentableImpl implements
		ModelObjSourceDeclare, ObjectBuilder<Object>, ScriptCompilable {
	public final boolean ignorePrepareIfDBInvalid() {
		return this.mappingQueryRef != null;
	}

	ModelObjSourceDefineImpl(ModelDefineImpl owner, String name,
			Class<?> aoClass) {
		super(name, aoClass);
		if (owner == null) {
			throw new NullArgumentException("owner");
		}
		this.owner = owner;
	}

	final ModelDefineImpl owner;

	public ModelDeclare getOwner() {
		return this.owner;
	}

	private ScriptImpl script;

	public final ScriptImpl getScript() {
		if (this.script == null) {
			this.script = new ScriptImpl(this.owner);
		}
		return this.script;
	}

	private ScriptImpl moCoutnOfScript;

	public final ScriptImpl getMOCountOfScript() {
		if (this.moCoutnOfScript == null) {
			this.moCoutnOfScript = new ScriptImpl(this.owner);
		}
		return this.moCoutnOfScript;
	}

	@SuppressWarnings("unchecked")
	private ModelObjProvider provider;

	final void internalSetProvider(
			ModelServiceBase<?>.ModelObjProvider<?> provider) {
		if (provider != null && provider != this.provider) {
			this.owner.checkModelServiceMO(provider);
			if (provider.aoClass != None.class
					&& !provider.aoClass.isAssignableFrom(this.getAOClass())) {
				throw new UnsupportedOperationException("模型实体源提供器与模型实体源定义的参数不符");
			}
		}
		this.provider = provider;

	}

	public ModelService<?>.ModelObjProvider<?> setProvider(
			ModelService<?>.ModelObjProvider<?> provider) {
		ModelServiceBase<?>.ModelObjProvider<?> old = this.provider;
		this.internalSetProvider(provider);
		return (ModelService<?>.ModelObjProvider<?>) old;
	}

	public final Object build() throws Throwable {
		return this.owner.newEmptySO();
	}

	@SuppressWarnings("unchecked")
	final <TMO> void internalFetchMOs(ContextImpl<?, ?, ?> context, Object ao,
			List<TMO> mos, int offset, int count, ObjectBuilder<TMO> moFactory) {
		if (context == null) {
			throw new NullArgumentException("context");
		}
		if (ao == null) {
			throw new NullArgumentException("ao");
		}
		if (mos == null) {
			throw new NullArgumentException("mos");
		}
		this.arguments.checkSO(ao);
		if (this.script != null
				&& !this.script.executeScriptAsSource(context, ao, mos, offset,
						count, moFactory, this) && this.mappingQueryRef != null) {
			ao = this.mappingQueryRef.args.tryConvert(ao);
			if (ao == null) {
				throw new IllegalArgumentException("参数不匹配");
			}
			DBCommandProxy proxy = context
					.prepareStatement(this.mappingQueryRef);
			try {
				// TODO cmd.executeQueryLimit
				ResultSet resultSet = proxy.command.executor.executeQuery(ao);
				try {
					ResultSetReader.readEntities(moFactory, mos,
							this.mappingQueryRef, resultSet);
				} finally {
					resultSet.close();
				}
			} catch (SQLException e) {
				throw Utils.tryThrowException(e);
			} finally {
				proxy.unuse();
			}
		} else if (this.provider != null) {
			SpaceNode occorAtSave = this.provider.getService()
					.updateContextSpace(context);
			try {
				if (moFactory == null) {
					moFactory = (ObjectBuilder<TMO>) this;
				}
				this.provider.provide(context, ao, this, mos, offset, count,
						moFactory);
			} catch (Throwable e) {
				throw Utils.tryThrowException(e);
			} finally {
				occorAtSave.updateContextSpace(context);
			}
		}
	}

	@SuppressWarnings("unchecked")
	final int internalMOCountOf(ContextImpl<?, ?, ?> context, Object ao) {
		if (context == null) {
			throw new NullArgumentException("context");
		}
		if (ao == null) {
			throw new NullArgumentException("ao");
		}
		this.arguments.checkSO(ao);
		if (this.script != null && this.script.scriptCallable()) {
			if (this.moCoutnOfScript != null
					&& this.moCoutnOfScript.scriptCallable()) {
				return this.moCoutnOfScript.executeScriptAsSourceCountOf(
						context, ao, this);
			} else {
				return -1;
			}
		} else if (this.mappingQueryRef != null) {
			ao = this.mappingQueryRef.args.tryConvert(ao);
			if (ao == null) {
				throw new IllegalArgumentException("参数不匹配");
			}
			try {
				return (int) context.getDBAdapter().rowCountOf(
						this.mappingQueryRef, (DynamicObject) ao);
			} catch (SQLException e) {
				throw Utils.tryThrowException(e);
			}
		} else if (this.provider != null) {
			SpaceNode occorAtSave = this.provider.getService()
					.updateContextSpace(context);
			try {
				return this.provider.moCountOf(context, ao, this);
			} catch (Throwable e) {
				throw Utils.tryThrowException(e);
			} finally {
				occorAtSave.updateContextSpace(context);
			}
		} else {
			return -1;
		}
	}

	public <TMO> void fetchMOs(Context context, Object ao, List<TMO> mos,
			int offset, int count) {
		this.internalFetchMOs(ContextImpl.toContext(context), ao, mos, offset,
				count, null);
	}

	public final <TMO> void fetchMOs(Context context, List<TMO> mos,
			int offset, int count) {
		this.internalFetchMOs(ContextImpl.toContext(context), None.NONE, mos,
				offset, count, null);
	}

	public <TMO> void fetchMOs(Context context, Object ao, List<TMO> mos,
			int offset, int count, ObjectBuilder<TMO> moFactory) {
		if (moFactory == null) {
			throw new NullArgumentException("moFactory");
		}
		this.internalFetchMOs(ContextImpl.toContext(context), None.NONE, mos,
				offset, count, moFactory);
	}

	public <TMO> void fetchMOs(Context context, List<TMO> mos, int offset,
			int count, ObjectBuilder<TMO> moFactory) {
		if (moFactory == null) {
			throw new NullArgumentException("moFactory");
		}
		this.internalFetchMOs(ContextImpl.toContext(context), None.NONE, mos,
				offset, count, moFactory);
	}

	public final int moCountOf(Context context) {
		return this
				.internalMOCountOf(ContextImpl.toContext(context), None.NONE);
	}

	public final int moCountOf(Context context, Object ao) {
		return this.internalMOCountOf(ContextImpl.toContext(context), ao);
	}

	private MappingQueryStatementImpl mappingQueryRef;

	public final MappingQueryStatementImpl getMappingQueryRef() {
		return this.mappingQueryRef;
	}

	public final MappingQueryStatementImpl setMappingQueryRef(
			MappingQueryStatementDefine ref) {
		MappingQueryStatementImpl orm = (MappingQueryStatementImpl) ref;
		if (orm != null && orm.mapping != this.owner) {
			throw new IllegalArgumentException("ORM定义不属于该模型");
		}
		if (orm.args.isAssignableFrom(this.arguments) == AssignCapability.NO) {
			throw new IllegalArgumentException("ORM定义的参数与模型实体源定义的参数不匹配");
		}
		MappingQueryStatementImpl old = this.mappingQueryRef;
		this.mappingQueryRef = orm;
		return old;
	}

	public void tryCompileScript(ContextImpl<?, ?, ?> context) {
		if (this.script != null) {
			this.script.prepareAsSource(context, this);
		}
		if (this.moCoutnOfScript != null) {
			this.moCoutnOfScript.prepareAsSourceCountOf(context, this);
		}
	}

	// ///////////////////////////////
	final static String xml_element_modelobjsource = "source";
	static final String xml_element_script_countof = "script-countof";
	final static String xml_attr_provider = "provider";
	final static String xml_attr_orm_name = "orm-name";
	final static String xml_attr_orm_author = "orm-author";

	@Override
	public final String getXMLTagName() {
		return ModelObjSourceDefineImpl.xml_element_modelobjsource;
	}

	@Override
	void merge(SXElement element, SXMergeHelper helper) {
		super.merge(element, helper);
		SXElement scriptE = element.firstChild(ScriptImpl.xml_element_script);
		if (scriptE != null) {
			this.getScript().merge(scriptE, helper);
		}
		scriptE = element
				.firstChild(ModelObjSourceDefineImpl.xml_element_script_countof);
		if (scriptE != null) {
			this.getScript().merge(scriptE, helper);
		}
		String handlerClassName = element.getAttribute(
				ModelObjSourceDefineImpl.xml_attr_provider, null);
		if (handlerClassName != null && handlerClassName.length() > 0) {
			this.provider = helper.querier.find(ModelObjProvider.class,
					handlerClassName);
		}
		String ormName = element.getAttribute(
				ModelObjSourceDefineImpl.xml_attr_orm_name, null);
		if (ormName != null && ormName.length() > 0) {
			this.mappingQueryRef = this.owner.queries.get(ormName);
		}
	}

	@Override
	public void render(SXElement element) {
		super.render(element);
		if (this.script != null) {
			this.script.tryRender(element);
		}
		if (this.moCoutnOfScript != null) {
			this.moCoutnOfScript.tryRender(element,
					ModelObjSourceDefineImpl.xml_element_script_countof);
		}
		if (this.provider != null) {
			element.setAttribute(ModelObjSourceDefineImpl.xml_attr_provider,
					this.provider.getClass().getName());
		}
		if (this.mappingQueryRef != null) {
			element.setString(xml_attr_orm_name, this.mappingQueryRef.name);
		}
	}
}
