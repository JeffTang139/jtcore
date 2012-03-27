package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.None;
import org.eclipse.jt.core.def.model.InspectPoint;
import org.eclipse.jt.core.def.model.ListPropertyValue;
import org.eclipse.jt.core.def.model.ModelActionDefine;
import org.eclipse.jt.core.def.model.ModelConstraintDefine;
import org.eclipse.jt.core.def.model.ModelInvokeStage;
import org.eclipse.jt.core.def.model.ModelPropertyDeclare;
import org.eclipse.jt.core.def.model.ModelPropertyDefine;
import org.eclipse.jt.core.def.model.ModelReferenceDefine;
import org.eclipse.jt.core.impl.ModelServiceBase.ModelPropertyAccessor;
import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.misc.SXMergeHelper;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.GUID;


/**
 * 模型属性定义实现
 * 
 * @author Jeff Tang
 * 
 */
final class ModelPropertyDefineImpl extends ModelInvokeDefineImpl implements
        ModelPropertyDeclare, ScriptCompilable {

	final DataType type;

	public final void tryCompileScript(ContextImpl<?, ?, ?> context) {
		if (this.setterInfo != null && this.setterInfo.script != null) {
			this.setterInfo.script.prepareAsSetter(context, this.setterInfo);

		}
		if (this.getterInfo != null && this.getterInfo.script != null) {
			this.getterInfo.script.prepareAsSetter(context, this.getterInfo);
		}
	}

	boolean isStateEffective;
	private ModelReferenceDefineImpl modelRef;
	private ModelPropertyDefineImpl refProperty;
	private MetaBaseContainerImpl<InspectPointImpl> changedInspacts;
	private ModelPropAccessDefineImpl getterInfo;
	private ModelPropAccessDefineImpl setterInfo;
	private ModelPropAccessor accessor;

	@SuppressWarnings("unchecked")
	final void checkModelAccessor(ModelPropertyAccessor accessor,
	        ModelPropAccessDefineImpl accessInfo) {
		if (accessInfo == this.getterInfo) {
			if (!accessor.canGet(this.type)) {
				throw new UnsupportedOperationException("模型属性访问器[" + accessor
				        + "]支持的类型与属性定义的类型(" + this.type + ")不符");
			}
		} else if (accessInfo == this.setterInfo) {
			if (!accessor.canSet(this.type)) {
				throw new UnsupportedOperationException("模型属性访问器[" + accessor
				        + "]支持的类型与属性定义的类型(" + this.type + ")不符");
			}
		} else {
			throw new UnsupportedOperationException();
		}
	}

	final void callChangedInspects(ContextImpl<?, ?, ?> context, Object mo) {
		if (this.changedInspacts != null) {
			for (int i = 0, c = this.changedInspacts.size(); i < c; i++) {
				InspectPointImpl ipi = this.changedInspacts.get(i);
				ipi.inspect(context, mo, this, null, null,
				        ModelInvokeStage.CHANGED);
			}
		}
	}

	ModelPropertyDefineImpl(ModelDefineImpl owner, String name, DataType type,
	        Class<?> aoClass) {
		super(owner, name, aoClass);
		if (type == null) {
			throw new NullPointerException();
		}
		this.type = type;
	}

	public final MetaBaseContainerImpl<InspectPointImpl> getChangedInspects() {
		if (this.changedInspacts == null) {
			this.changedInspacts = new MetaBaseContainerImpl<InspectPointImpl>();
		}
		return this.changedInspacts;
	}

	public InspectPointImpl newChangedInspect(ModelActionDefine a) {
		ModelActionDefineImpl action = (ModelActionDefineImpl) a;
		if (action.owner != this.owner) {
			throw new IllegalArgumentException();
		}
		InspectPointImpl point = new InspectPointImpl(action);
		this.getChangedInspects().add(point);
		return point;
	}

	public InspectPoint newChangedInspect(ModelConstraintDefine c) {
		ModelConstraintDefineImpl constraint = (ModelConstraintDefineImpl) c;
		if (constraint.owner != this.owner) {
			throw new IllegalArgumentException();
		}
		InspectPointImpl point = new InspectPointImpl(constraint);
		this.getChangedInspects().add(point);
		return point;
	}

	public final ModelPropAccessDefineImpl getGetterInfo() {
		if (this.getterInfo == null) {
			this.getterInfo = new ModelPropAccessDefineImpl(this);
		}
		return this.getterInfo;
	}

	public final ModelPropAccessDefineImpl getSetterInfo() {
		if (this.setterInfo == null) {
			this.setterInfo = new ModelPropAccessDefineImpl(this);
		}
		return this.setterInfo;
	}

	public final DataType getType() {
		return this.type;
	}

	public final boolean isStateEffective() {
		return this.isStateEffective;
	}

	public final ModelReferenceDefine getRefModel() {
		return this.modelRef;
	}

	public final ModelPropertyDefine getRefProperty() {
		return this.refProperty;
	}

	public final void setModelReference(ModelReferenceDefine value) {
		this.checkModifiable();
		if (value != null) {
			ModelReferenceDefineImpl mrd = (ModelReferenceDefineImpl) value;
			if (mrd.owner != this.owner) {
				throw new IllegalArgumentException("模型引用不属于该模型");
			}
			this.modelRef = mrd;
		} else {
			this.modelRef = null;
			this.refProperty = null;
		}
	}

	public final void setPropertyReference(ModelPropertyDefine value) {
		this.checkModifiable();
		if (this.modelRef == null) {
			throw new IllegalArgumentException("未指定模型引用");
		}
		if (value != null) {
			ModelPropertyDefineImpl mpd = (ModelPropertyDefineImpl) value;
			if (mpd.owner != this.modelRef.target) {
				throw new IllegalArgumentException("属性引用与模型引用不符");
			}
			this.refProperty = (ModelPropertyDefineImpl) value;
		}
	}

	public final void setStateEffective(boolean value) {
		this.checkModifiable();
		this.isStateEffective = value;
	}

	// /////////////////////////////////////
	// //// XML
	// /////////////////////////////////////
	static final String xml_element_property = "property";
	static final String xml_element_inspactChangeds = "changeds";
	static final String xml_element_set = "set";
	static final String xml_element_get = "get";
	static final String xml_attr_type = "type";
	static final String xml_attr_modelref = "model-ref";
	static final String xml_attr_propertyref = "property-ref";
	static final String xml_attr_isStateEffective = "isStateEffective";

	@Override
	public final String getXMLTagName() {
		return xml_element_property;
	}

	@Override
	public final void render(SXElement element) {
		super.render(element);
		element.setAsType(xml_attr_type, this.type);
		element.setBoolean(xml_attr_isStateEffective, this.isStateEffective);
		if (this.modelRef != null) {
			element.setString(xml_attr_modelref, this.modelRef.name);
			if (this.refProperty != null) {
				element.setString(xml_attr_propertyref, this.refProperty.name);
			}
		}
		if (this.setterInfo != null) {
			this.setterInfo.renderInto(element, xml_element_set);
		}
		if (this.getterInfo != null) {
			this.getterInfo.renderInto(element, xml_element_get);
		}
		if (this.changedInspacts != null) {
			this.changedInspacts.renderInto(element,
			        xml_element_inspactChangeds, 0);
		}
	}

	@Override
	final void merge(SXElement element, SXMergeHelper helper) {
		super.merge(element, helper);
		this.isStateEffective = element.getBoolean(xml_attr_isStateEffective,
		        this.isStateEffective);
		SXElement access = element.firstChild(xml_element_get);
		if (access != null) {
			this.getGetterInfo().merge(access, helper);
		}
		access = element.firstChild(xml_element_set);
		if (access != null) {
			this.getSetterInfo().merge(access, helper);
		}
		final SXElement changed = element.firstChild(
		        xml_element_inspactChangeds,
		        InspectPointImpl.xml_element_inspectpoint);
		if (changed != null) {
			InspectPointImpl.delayLoadInspectPoint(this.owner, this
			        .getChangedInspects(), changed, helper);
		}
	}

	//
	// ModelPropertyDefineImpl(ModelDefineImpl owner,
	// List<Runnable> inspectsLoader, SXElement element) {
	// super(owner, inspectsLoader, element);
	// this.type = element.getAsType(xml_attr_type);
	// this.isStateEffective = element.getBoolean(xml_attr_isStateEffective);
	// SXElement access = element.firstChild(xml_element_get);
	// if (access != null) {
	// this.getterInfo = new ModelPropAccessDefineImpl(this, access);
	// }
	// access = element.firstChild(xml_element_set);
	// if (access != null) {
	// this.setterInfo = new ModelPropAccessDefineImpl(this, access);
	// }
	// final SXElement changed = element.firstChild(
	// xml_element_inspactChangeds,
	// InspectPointImpl.xml_element_inspectpoint);
	// if (changed != null) {
	// inspectsLoader.add(new Runnable() {
	// public void run() {
	// for (SXElement element = changed; element != null; element = element
	// .nextSibling(InspectPointImpl.xml_element_inspectpoint)) {
	// InspectPointImpl point = new InspectPointImpl(
	// ModelPropertyDefineImpl.this.owner, element);
	// ModelPropertyDefineImpl.this.getChangedInspects().add(
	// point);
	// }
	// }
	// });
	// }
	// // TODO 下面两行是有问题的
	// this.refModel = owner.refs.get(element.getAttribute(xml_attr_modelref));
	// this.refProperty = this.refModel.target.properties.get(element
	// .getAttribute(xml_attr_propertyref));
	// }

	// /////////////////////////////////////////
	// //////////// runtime ////////////////////
	// /////////////////////////////////////////

	private final void checkGetAccess(ContextImpl<?, ?, ?> context) {
		if (this.getterInfo == null || !this.getterInfo.isValid()) {
			throw new UnsupportedOperationException("模型[" + this.owner.name
			        + "].属性[" + this.name + "]不支持读访问");
		}
		if (this.accessor == null) {
			this.accessor = ModelPropAccessor.setterOf(this.type);
		}
	}

	private final void checkSetAccess(ContextImpl<?, ?, ?> context) {
		if (this.setterInfo == null || !this.setterInfo.isValid()) {
			throw new UnsupportedOperationException("模型[" + this.owner.name
			        + "].属性[" + this.name + "]不支持写访问");
		}
		if (this.accessor == null) {
			this.accessor = ModelPropAccessor.setterOf(this.type);
		}
	}

	/**
	 * 获取列表属性的值列表
	 */
	public final ListPropertyValue getPropValueAsList(Context context, Object mo) {
		ContextImpl<?, ?, ?> c = ContextImpl.toContext(context);
		this.checkGetAccess(c);
		return this.accessor.getPropValueAsList(c, this.getterInfo, mo,
		        None.NONE);
	}

	public final boolean getPropValueAsBoolean(Context context, Object mo) {
		ContextImpl<?, ?, ?> c = ContextImpl.toContext(context);
		this.checkGetAccess(c);
		return this.accessor.getPropValueAsBoolean(c, this.getterInfo, mo);
	}

	public final void setPropValueAsBoolean(Context context, Object mo,
	        boolean value) {
		ContextImpl<?, ?, ?> c = ContextImpl.toContext(context);
		this.checkSetAccess(c);
		this.accessor.setPropValueAsBoolean(c, this.setterInfo, mo, value);
	}

	public final byte getPropValueAsByte(Context context, Object mo) {
		ContextImpl<?, ?, ?> c = ContextImpl.toContext(context);
		this.checkGetAccess(c);
		return this.accessor.getPropValueAsByte(c, this.getterInfo, mo);
	}

	public final void setPropValueAsByte(Context context, Object mo, byte value) {
		ContextImpl<?, ?, ?> c = ContextImpl.toContext(context);
		this.checkSetAccess(c);
		this.accessor.setPropValueAsByte(c, this.setterInfo, mo, value);
	}

	public final short getPropValueAsShort(Context context, Object mo) {
		ContextImpl<?, ?, ?> c = ContextImpl.toContext(context);
		this.checkGetAccess(c);
		return this.accessor.getPropValueAsShort(c, this.getterInfo, mo);
	}

	public final void setPropValueAsShort(Context context, Object mo,
	        short value) {
		ContextImpl<?, ?, ?> c = ContextImpl.toContext(context);
		this.checkSetAccess(c);
		this.accessor.setPropValueAsShort(c, this.setterInfo, mo, value);
	}

	public final int getPropValueAsInt(Context context, Object mo) {
		ContextImpl<?, ?, ?> c = ContextImpl.toContext(context);
		this.checkGetAccess(c);
		return this.accessor.getPropValueAsInt(c, this.getterInfo, mo);
	}

	public final void setPropValueAsInt(Context context, Object mo, int value) {
		ContextImpl<?, ?, ?> c = ContextImpl.toContext(context);
		this.checkSetAccess(c);
		this.accessor.setPropValueAsInt(c, this.setterInfo, mo, value);
	}

	public final long getPropValueAsLong(Context context, Object mo) {
		ContextImpl<?, ?, ?> c = ContextImpl.toContext(context);
		this.checkGetAccess(c);
		return this.accessor.getPropValueAsLong(c, this.getterInfo, mo);
	}

	public final void setPropValueAsLong(Context context, Object mo, long value) {
		ContextImpl<?, ?, ?> c = ContextImpl.toContext(context);
		this.checkSetAccess(c);
		this.accessor.setPropValueAsLong(c, this.setterInfo, mo, value);
	}

	public final long getPropValueAsDate(Context context, Object mo) {
		ContextImpl<?, ?, ?> c = ContextImpl.toContext(context);
		this.checkGetAccess(c);
		return this.accessor.getPropValueAsDate(c, this.getterInfo, mo);
	}

	public final void setPropValueAsDate(Context context, Object mo, long value) {
		ContextImpl<?, ?, ?> c = ContextImpl.toContext(context);
		this.checkSetAccess(c);
		this.accessor.setPropValueAsDate(c, this.setterInfo, mo, value);
	}

	public final double getPropValueAsDouble(Context context, Object mo) {
		ContextImpl<?, ?, ?> c = ContextImpl.toContext(context);
		this.checkGetAccess(c);
		return this.accessor.getPropValueAsDouble(c, this.getterInfo, mo);
	}

	public final void setPropValueAsDouble(Context context, Object mo,
	        double value) {
		ContextImpl<?, ?, ?> c = ContextImpl.toContext(context);
		this.checkSetAccess(c);
		this.accessor.setPropValueAsDouble(c, this.setterInfo, mo, value);
	}

	public final float getPropValueAsFloat(Context context, Object mo) {
		ContextImpl<?, ?, ?> c = ContextImpl.toContext(context);
		this.checkGetAccess(c);
		return this.accessor.getPropValueAsFloat(c, this.getterInfo, mo);
	}

	public final void setPropValueAsFloat(Context context, Object mo,
	        float value) {
		ContextImpl<?, ?, ?> c = ContextImpl.toContext(context);
		this.checkSetAccess(c);
		this.accessor.setPropValueAsFloat(c, this.setterInfo, mo, value);
	}

	public final String getPropValueAsString(Context context, Object mo) {
		ContextImpl<?, ?, ?> c = ContextImpl.toContext(context);
		this.checkGetAccess(c);
		return this.accessor.getPropValueAsString(c, this.getterInfo, mo);
	}

	public final void setPropValueAsString(Context context, Object mo,
	        String value) {
		ContextImpl<?, ?, ?> c = ContextImpl.toContext(context);
		this.checkSetAccess(c);
		this.accessor.setPropValueAsString(c, this.setterInfo, mo, value);
	}

	public final GUID getPropValueAsGUID(Context context, Object mo) {
		ContextImpl<?, ?, ?> c = ContextImpl.toContext(context);
		this.checkGetAccess(c);
		return this.accessor.getPropValueAsGUID(c, this.getterInfo, mo);
	}

	public final void setPropValueAsGUID(Context context, Object mo, GUID value) {
		ContextImpl<?, ?, ?> c = ContextImpl.toContext(context);
		this.checkSetAccess(c);
		this.accessor.setPropValueAsGUID(c, this.setterInfo, mo, value);
	}

	public final byte[] getPropValueAsBytes(Context context, Object mo) {
		ContextImpl<?, ?, ?> c = ContextImpl.toContext(context);
		this.checkGetAccess(c);
		return this.accessor.getPropValueAsBytes(c, this.getterInfo, mo);
	}

	public final void setPropValueAsBytes(Context context, Object mo,
	        byte[] value) {
		ContextImpl<?, ?, ?> c = ContextImpl.toContext(context);
		this.checkSetAccess(c);
		this.accessor.setPropValueAsBytes(c, this.setterInfo, mo, value);
	}

	public final Object getPropValueAsObject(Context context, Object mo) {
		ContextImpl<?, ?, ?> c = ContextImpl.toContext(context);
		this.checkGetAccess(c);
		return this.accessor.getPropValueAsObject(c, this.getterInfo, mo);
	}

	public final void setPropValueAsObject(Context context, Object mo,
	        Object value) {
		ContextImpl<?, ?, ?> c = ContextImpl.toContext(context);
		this.checkSetAccess(c);
		this.accessor.setPropValueAsObject(c, this.setterInfo, mo, value);
	}
}
