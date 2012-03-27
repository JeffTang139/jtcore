package org.eclipse.jt.core.impl;

import java.io.IOException;
import java.util.List;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.None;
import org.eclipse.jt.core.ObjectQuerier;
import org.eclipse.jt.core.def.MetaElementType;
import org.eclipse.jt.core.def.info.InfoKind;
import org.eclipse.jt.core.def.model.ModelDeclarator;
import org.eclipse.jt.core.def.model.ModelDeclare;
import org.eclipse.jt.core.def.model.ModelDefine;
import org.eclipse.jt.core.def.model.ModelFieldDefine;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.impl.ModelServiceBase.ModelInvokee;
import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.misc.SXMergeHelper;
import org.eclipse.jt.core.type.DataTypable;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.Digester;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.TypeDetector;
import org.eclipse.jt.core.type.Undigester;


/**
 * 模型定义的实现类
 * 
 * @author Jeff Tang
 * 
 */
public final class ModelDefineImpl extends StructDefineImpl implements
		ModelDeclare, Prepareble {

	public final boolean ignorePrepareIfDBInvalid() {
		return !this.queries.isEmpty();
	}

	public final static class HelperImpl implements Helper {

		public ModelDeclare newTempModelDeclare(String author, String name,
				Class<?> moClass) {
			return new ModelDefineImpl(name, moClass, null);
		}

		public final ModelDefineImpl newTempModelDeclare(SXElement template,
				Context context) {
			if (template == null) {
				throw new NullArgumentException("template");
			}
			if (context == null) {
				throw new NullArgumentException("context");
			}
			ModelDefineImpl modelDefine;
			String name = template.getAttribute(NamedDefineImpl.xml_attr_name);
			String className = template
					.getAttribute(ModelDefineImpl.xml_attr_moClass);
			modelDefine = new ModelDefineImpl(name, context.get(Class.class,
					className), null);
			SXMergeHelper mergeHelper = new SXMergeHelper(context);
			modelDefine.merge(template, mergeHelper);
			modelDefine.ensurePrepared(ContextImpl.toContext(context), true);
			return modelDefine;
		}

		public void ensurePrepared(ModelDefine model, Context context) {
			if (context == null) {
				throw new NullArgumentException("context");
			}
			if (model == null) {
				throw new NullArgumentException("model");
			}
			((ModelDefineImpl) model).ensurePrepared(ContextImpl
					.toContext(context), false);
		}
	}

	final static String type_name_prefix = "model:";

	@Override
	final String structTypeNamePrefix() {
		return type_name_prefix;
	}

	/**
	 * 根据名称查找类型
	 */
	public final static ModelDefineImpl tryParse(String fullName,
			ObjectQuerier querier) {
		if (fullName.startsWith(type_name_prefix)) {
			int start = type_name_prefix.length();
			int authorEnd = fullName.indexOf('.', start);
			String author, name;
			if (authorEnd > start) {
				author = fullName.substring(start, authorEnd++);
				name = fullName.substring(authorEnd);
			} else {
				author = "";
				name = fullName.substring(start);
			}
			return (ModelDefineImpl) querier.find(ModelDefine.class, author,
					name);
		}
		return null;
	}

	@Override
	public final void digestType(Digester digester) {
		digester.update(TypeCodeSet.MODEL_H);
		super.digestType(digester);
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(
				TypeCodeSet.MODEL_H) {
			@Override
			protected DataType doUndigest(Undigester undigester)
					throws IOException, StructDefineNotFoundException {
				return undigestType(undigester);
			}
		});
	}

	public final MetaElementType getMetaElementType() {
		return MetaElementType.MODEL;
	}

	final ModelDeclarator<?> declarator;

	// XXX
	GUID id;

	private volatile boolean prepared;

	private static void prepareMembers(ContextImpl<?, ?, ?> context,
			List<?> list) {
		for (int i = 0, c = list.size(); i < c; i++) {
			Object m = list.get(i);
			if (m instanceof ArgumentableImpl) {
				((ArgumentableImpl) m).ensurePrepared(context, true);
			}
			if (m instanceof ScriptCompilable) {
				((ScriptCompilable) m).tryCompileScript(context);
			}
		}
	}

	public final boolean isPrepared() {
		return this.prepared;
	}

	/**
	 * 准备
	 */
	public final void ensurePrepared(ContextImpl<?, ?, ?> context,
			boolean rePrepare) {
		if (rePrepare || !this.prepared) {
			synchronized (this) {
				if (rePrepare || !this.prepared) {
					this.prepared = true;
					super.prepareAccessInfo();
					for (MappingQueryStatementImpl mq : this.queries) {
						mq.ensurePrepared(context, true);
					}
					prepareMembers(context, this.actions);
					prepareMembers(context, this.properties);
					prepareMembers(context, this.constructors);
					prepareMembers(context, this.constraints);
					prepareMembers(context, this.sources);
				}
			}
		}
	}

	/**
	 * 检查模型是否支持相关的模型访问器
	 * 
	 * @param modelInvokee
	 *            需要检查的访问器
	 */
	final void checkModelServiceMO(ModelInvokee modelInvokee) {
		Class<?> invokeeMO = modelInvokee.getService().moClass;
		if (invokeeMO != this.soClass
				&& !invokeeMO.isAssignableFrom(this.soClass)) {
			throw new UnsupportedOperationException("模型定义[" + this.name
					+ "]的实例类型与所要绑定的模型服务不匹配");
		}
	}

	public ModelDefineImpl(String name, Class<?> moClass,
			ModelDeclarator<?> declarator) {
		super(name, moClass);
		this.tryLoadJavaFields(true);
		this.declarator = declarator;
	}

	final NamedDefineContainerImpl<ModelPropertyDefineImpl> properties = new NamedDefineContainerImpl<ModelPropertyDefineImpl>();

	public final NamedDefineContainerImpl<? extends ModelPropertyDefineImpl> getProperties() {
		return this.properties;
	}

	public final ModelPropertyDefineImpl newProperty(String name, DataType type) {
		this.checkModifiable();
		ModelPropertyDefineImpl property = new ModelPropertyDefineImpl(this,
				name, type, None.class);
		this.properties.add(property);
		return property;
	}

	public final ModelPropertyDefineImpl newProperty(String name,
			DataTypable typable) {
		return this.newProperty(name, typable.getType());
	}

	public final ModelPropertyDefineImpl newProperty(ModelFieldDefine refField) {
		if (refField == null) {
			throw new NullPointerException();
		}
		StructFieldDefineImpl rf = (StructFieldDefineImpl) refField;
		if (rf.owner != this) {
			throw new IllegalArgumentException("引用子段不属于当前模型");
		}
		ModelPropertyDefineImpl property = this.newProperty(rf.name, rf.type);
		property.getGetterInfo().setRefField(rf);
		property.getSetterInfo().setRefField(rf);
		return property;
	}

	final NamedDefineContainerImpl<ModelConstraintDefineImpl> constraints = new NamedDefineContainerImpl<ModelConstraintDefineImpl>();

	public final NamedDefineContainerImpl<? extends ModelConstraintDefineImpl> getConstraints() {
		return this.constraints;
	}

	private final ModelConstraintDefineImpl internalNewConstraint(String name,
			InfoKind kind, String messageFormat) {
		this.checkModifiable();
		ModelConstraintDefineImpl constraint = new ModelConstraintDefineImpl(
				this, name, kind, messageFormat);
		this.constraints.add(constraint);
		return constraint;
	}

	@Deprecated
	public final ModelConstraintDefineImpl newConstraint(String name) {
		return this.newConstraint(name, "");
	}

	/**
	 * 新建错误约束
	 * 
	 * @param name
	 *            约束名
	 * @param messageFormat
	 *            消息格式化文本
	 */
	public final ModelConstraintDefineImpl newConstraint(String name,
			String messageFormat) {
		return this.internalNewConstraint(name, InfoKind.ERROR, messageFormat);
	}

	final NamedDefineContainerImpl<ModelConstructorDefineImpl> constructors = new NamedDefineContainerImpl<ModelConstructorDefineImpl>();

	public final NamedDefineContainerImpl<? extends ModelConstructorDefineImpl> getConstructors() {
		return this.constructors;
	}

	public final ModelConstructorDefineImpl newConstructor(String name,
			Class<?> aoClass) {
		this.checkModifiable();
		ModelConstructorDefineImpl cnstruct = new ModelConstructorDefineImpl(
				this, name, aoClass);
		this.constructors.add(cnstruct);
		return cnstruct;
	}

	public final ModelConstructorDefineImpl newConstructor(String name) {
		return this.newConstructor(name, None.class);
	}

	final NamedDefineContainerImpl<ModelActionDefineImpl> actions = new NamedDefineContainerImpl<ModelActionDefineImpl>();

	public final NamedDefineContainerImpl<? extends ModelActionDefineImpl> getActions() {
		return this.actions;
	}

	public final ModelActionDefineImpl newAction(String name, Class<?> aoClass) {
		this.checkModifiable();
		ModelActionDefineImpl action = new ModelActionDefineImpl(this, name,
				aoClass);
		this.actions.add(action);
		return action;
	}

	public final ModelActionDefineImpl newAction(String name) {
		return this.newAction(name, None.class);
	}

	final NamedDefineContainerImpl<MappingQueryStatementImpl> queries = new NamedDefineContainerImpl<MappingQueryStatementImpl>();

	public final NamedDefineContainerImpl<? extends MappingQueryStatementImpl> getQueries() {
		return this.queries;
	}

	public final MappingQueryStatementImpl newQuery(String name) {
		this.checkModifiable();
		MappingQueryStatementImpl query = new MappingQueryStatementImpl(name,
				this);
		this.queries.add(query);
		return query;
	}

	final NamedDefineContainerImpl<ModelReferenceDefineImpl> refs = new NamedDefineContainerImpl<ModelReferenceDefineImpl>();

	public NamedDefineContainerImpl<? extends ModelReferenceDefineImpl> getReferences() {
		return this.refs;
	}

	public final ModelReferenceDefineImpl newReference(String name,
			ModelDefine target) {
		this.checkModifiable();
		ModelReferenceDefineImpl ref = new ModelReferenceDefineImpl(this, name,
				(ModelDefineImpl) target);
		this.refs.add(ref);
		return ref;
	}

	final NamedDefineContainerImpl<ModelDefineImpl> nesteds = new NamedDefineContainerImpl<ModelDefineImpl>();

	public final NamedDefineContainerImpl<ModelDefineImpl> getNesteds() {
		return this.nesteds;
	}

	public final ModelDefineImpl newNested(String name, Class<?> moClass) {
		this.checkModifiable();
		ModelDefineImpl nested = new ModelDefineImpl(name, moClass, null);
		this.nesteds.add(nested);
		return nested;
	}

	final NamedDefineContainerImpl<ModelObjSourceDefineImpl> sources = new NamedDefineContainerImpl<ModelObjSourceDefineImpl>();

	public final NamedDefineContainerImpl<ModelObjSourceDefineImpl> getSources() {
		return this.sources;
	}

	public final ModelObjSourceDefineImpl newSource(String name,
			Class<?> aoClass) {
		this.checkModifiable();
		ModelObjSourceDefineImpl source = new ModelObjSourceDefineImpl(this,
				name, aoClass);
		this.sources.add(source);
		return source;
	}

	public final ModelObjSourceDefineImpl newSource(String name) {
		return this.newSource(name, None.class);
	}

	@SuppressWarnings("unchecked")
	public final Class getMOClass() {
		return this.soClass;
	}

	public final Object newMO() {
		return this.newInitedSO();
	}

	@Override
	public final <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> detector, TUserData userData) {
		try {
			return detector.inModel(userData, this);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	// ////////////////////////////////////
	// //////// XML
	// ////////////////////////////////////
	static final String xml_element_model = "model";
	static final String xml_element_propertys = "propertys";
	static final String xml_element_actions = "actions";
	static final String xml_element_constraints = "constraints";
	static final String xml_element_constructors = "constructors";
	static final String xml_element_querys = "querys";
	static final String xml_element_sources = "sources";
	static final String xml_element_refs = "refs";
	static final String xml_element_nesteds = "nesteds";

	static final String xml_attr_moClass = "mo";

	@Override
	public final String getXMLTagName() {
		return xml_element_model;
	}

	@Override
	public final void render(SXElement element) {
		super.render(element);
		element.setAttribute(xml_attr_moClass, this.soClass.getName());
		this.properties.renderInto(element, xml_element_propertys, 0);
		this.actions.renderInto(element, xml_element_actions, 0);
		this.constraints.renderInto(element, xml_element_constraints, 0);
		this.constructors.renderInto(element, xml_element_constructors, 0);
		this.queries.renderInto(element, xml_element_querys, 0);
		this.sources.renderInto(element, xml_element_sources, 0);
		this.nesteds.renderInto(element, xml_element_nesteds, 0);
		this.refs.renderInto(element, xml_element_refs, 0);
	}

	@Override
	void merge(SXElement element, SXMergeHelper helper) {
		super.merge(element, helper);
		for (SXElement constructorE : element.getChildren(
				xml_element_constructors,
				ModelConstructorDefineImpl.xml_element_constructor)) {
			String name = constructorE.getAttribute(
					NamedDefineImpl.xml_attr_name, null);
			ModelConstructorDefineImpl c = this.constructors.find(name);
			if (c == null) {
				c = new ModelConstructorDefineImpl(this, name, ArgumentableImpl
						.getAOClass(helper, constructorE));
				this.constructors.add(c);
			}
			c.merge(constructorE, helper);
		}
		for (SXElement propertyE : element.getChildren(xml_element_propertys,
				ModelPropertyDefineImpl.xml_element_property)) {
			String name = propertyE.getAttribute(NamedDefineImpl.xml_attr_name,
					null);
			ModelPropertyDefineImpl p = this.properties.find(name);
			if (p == null) {
				p = new ModelPropertyDefineImpl(this, name, propertyE
						.getAsType(ModelPropertyDefineImpl.xml_attr_type,
								helper.querier), ArgumentableImpl.getAOClass(
						helper, propertyE));
				this.properties.add(p);
			}
			p.merge(propertyE, helper);
		}
		for (SXElement actionE : element.getChildren(xml_element_actions,
				ModelActionDefineImpl.xml_element_action)) {
			String name = actionE.getAttribute(NamedDefineImpl.xml_attr_name,
					null);
			ModelActionDefineImpl a = this.actions.find(name);
			if (a == null) {
				a = new ModelActionDefineImpl(this, name, ArgumentableImpl
						.getAOClass(helper, actionE));
				this.actions.add(a);
			}
			a.merge(actionE, helper);
		}
		for (SXElement constraintE : element.getChildren(
				xml_element_constraints,
				ModelConstraintDefineImpl.xml_element_constraint)) {
			String name = constraintE.getAttribute(
					NamedDefineImpl.xml_attr_name, null);
			ModelConstraintDefineImpl c = this.constraints.find(name);
			if (c == null) {
				InfoKind kind = constraintE.getEnum(InfoKind.class,
						InfoDefineImpl.xml_attr_kind, InfoKind.ERROR);
				c = new ModelConstraintDefineImpl(this, name, kind, "");
				this.constraints.add(c);
			}
			c.merge(constraintE, helper);
		}
		for (SXElement queryE : element.getChildren(xml_element_querys,
				MappingQueryStatementImpl.xml_tag)) {
			String name = queryE.getAttribute(NamedDefineImpl.xml_attr_name,
					null);
			MappingQueryStatementImpl q = this.queries.find(name);
			if (q == null) {
				q = new MappingQueryStatementImpl(name, this);
				this.queries.add(q);
			}
			q.merge(queryE, helper);
		}
		for (SXElement objSourceE : element.getChildren(xml_element_sources,
				ModelObjSourceDefineImpl.xml_element_modelobjsource)) {
			String name = objSourceE.getAttribute(
					NamedDefineImpl.xml_attr_name, null);
			ModelObjSourceDefineImpl s = this.sources.find(name);
			if (s == null) {
				s = new ModelObjSourceDefineImpl(this, name, ArgumentableImpl
						.getAOClass(helper, objSourceE));
				this.sources.add(s);
			}
			s.merge(objSourceE, helper);
		}

		for (SXElement nestedE : element.getChildren(xml_element_nesteds,
				xml_element_model)) {
			String name = nestedE.getAttribute(NamedDefineImpl.xml_attr_name,
					null);
			String moClassName = nestedE.getAttribute(xml_attr_moClass);
			ModelDefineImpl model = this.nesteds.find(name);
			if (model == null) {
				model = new ModelDefineImpl(name, helper.querier.get(
						Class.class, moClassName), null);
				this.nesteds.add(model);
			}
			model.merge(nestedE, helper);
		}
		helper.resolveDelayAction(this, element);
	}
}
