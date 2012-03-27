package org.eclipse.jt.core.impl;
import org.eclipse.jt.core.None;
import org.eclipse.jt.core.type.Convert;
import org.eclipse.jt.core.type.GUID;

final class DoubleModelPropAccessor extends ModelPropAccessor {
	@SuppressWarnings("unchecked")
	@Override
	double getPropValueAsDouble(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo) {
		if (context == null || mo == null) {
			throw new NullPointerException();
		}
		if (propAccessDefine.script != null
				&& propAccessDefine.script.scriptCallable()) {
			return Convert.toDouble(propAccessDefine.script.executeScriptAsGetter(
					context, mo, propAccessDefine));
		} else if (propAccessDefine.accessor != null) {
			SpaceNode occorAtSave = propAccessDefine.accessor.getService()
					.updateContextSpace(context);
			try {
				return propAccessDefine.accessor.doGetDouble(context, mo,
						propAccessDefine.ownerProperty);
			} catch (Throwable e) {
				throw Utils.tryThrowException(e);
			} finally {
				occorAtSave.updateContextSpace(context);
			}
		} else if (propAccessDefine.field != null) {
			return propAccessDefine.field.getFieldValueAsDouble(mo);
		} else {
			throw new UnsupportedOperationException();
		}
	}
	@SuppressWarnings("unchecked")
	@Override
	void setPropValueAsDouble(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo, 
			double value) {
		if (context == null || mo == null) {
			throw new NullPointerException();
		}
		if (propAccessDefine.ownerProperty.hasBeforeInspects()){
			propAccessDefine.ownerProperty.callBeforeInspects(context, 
				mo, None.NONE, value);
		}
		try {
		if ((propAccessDefine.script == null || !propAccessDefine.script
				.executeScriptAsSetter(context, mo, value,
						propAccessDefine))
				&& propAccessDefine.accessor != null) {
				SpaceNode occorAtSave = propAccessDefine.accessor.getService()
						.updateContextSpace(context);
				try {
					propAccessDefine.accessor.doSetDouble(context, mo,
							value, propAccessDefine.ownerProperty);
				} catch (Throwable e) {
					throw Utils.tryThrowException(e);
				} finally {
					occorAtSave.updateContextSpace(context);
				}
			} else if (propAccessDefine.field != null) {
				propAccessDefine.field.setFieldValueAsDouble(mo, value);
			} else {
				throw new UnsupportedOperationException();
			}
			if (propAccessDefine.ownerProperty.hasAfterInspects()){
				propAccessDefine.ownerProperty.callAfterInspects(context, 
						mo, None.NONE, value);
			}
		} finally {
			if (propAccessDefine.ownerProperty.hasBeforeInspects()){
				propAccessDefine.ownerProperty.callFinallyInspects(context,
					mo, None.NONE, value);}
		}
	}
@Override
	void setPropValueAsBoolean(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo, 
			boolean value) {
		this.setPropValueAsDouble(context, propAccessDefine, mo, 
				Convert.toDouble(value));
	}
	@Override
	boolean getPropValueAsBoolean(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo) {
		return Convert.toBoolean(this.getPropValueAsDouble(context, propAccessDefine, mo));
	}
	@Override
	void setPropValueAsByte(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo, 
			byte value) {
		this.setPropValueAsDouble(context, propAccessDefine, mo, 
				Convert.toDouble(value));
	}
	@Override
	byte getPropValueAsByte(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo) {
		return Convert.toByte(this.getPropValueAsDouble(context, propAccessDefine, mo));
	}
	@Override
	void setPropValueAsShort(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo, 
			short value) {
		this.setPropValueAsDouble(context, propAccessDefine, mo, 
				Convert.toDouble(value));
	}
	@Override
	short getPropValueAsShort(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo) {
		return Convert.toShort(this.getPropValueAsDouble(context, propAccessDefine, mo));
	}
	@Override
	void setPropValueAsInt(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo, 
			int value) {
		this.setPropValueAsDouble(context, propAccessDefine, mo, 
				Convert.toDouble(value));
	}
	@Override
	int getPropValueAsInt(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo) {
		return Convert.toInt(this.getPropValueAsDouble(context, propAccessDefine, mo));
	}
	@Override
	void setPropValueAsLong(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo, 
			long value) {
		this.setPropValueAsDouble(context, propAccessDefine, mo, 
				Convert.toDouble(value));
	}
	@Override
	long getPropValueAsLong(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo) {
		return Convert.toLong(this.getPropValueAsDouble(context, propAccessDefine, mo));
	}
	@Override
	void setPropValueAsDate(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo, 
			long value) {
		this.setPropValueAsDouble(context, propAccessDefine, mo, 
				Convert.toDouble(value));
	}
	@Override
	long getPropValueAsDate(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo) {
		return Convert.toDate(this.getPropValueAsDouble(context, propAccessDefine, mo));
	}
	@Override
	void setPropValueAsFloat(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo, 
			float value) {
		this.setPropValueAsDouble(context, propAccessDefine, mo, 
				Convert.toDouble(value));
	}
	@Override
	float getPropValueAsFloat(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo) {
		return Convert.toFloat(this.getPropValueAsDouble(context, propAccessDefine, mo));
	}
	@Override
	void setPropValueAsString(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo, 
			String value) {
		this.setPropValueAsDouble(context, propAccessDefine, mo, 
				Convert.toDouble(value));
	}
	@Override
	String getPropValueAsString(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo) {
		return Convert.toString(this.getPropValueAsDouble(context, propAccessDefine, mo));
	}
	@Override
	void setPropValueAsGUID(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo, 
			GUID value) {
		this.setPropValueAsDouble(context, propAccessDefine, mo, 
				Convert.toDouble(value));
	}
	@Override
	GUID getPropValueAsGUID(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo) {
		return Convert.toGUID(this.getPropValueAsDouble(context, propAccessDefine, mo));
	}
	@Override
	void setPropValueAsBytes(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo, 
			byte[] value) {
		this.setPropValueAsDouble(context, propAccessDefine, mo, 
				Convert.toDouble(value));
	}
	@Override
	byte[] getPropValueAsBytes(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo) {
		return Convert.toBytes(this.getPropValueAsDouble(context, propAccessDefine, mo));
	}
	@Override
	void setPropValueAsObject(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo, 
			Object value) {
		this.setPropValueAsDouble(context, propAccessDefine, mo, 
				Convert.toDouble(value));
	}
	@Override
	Object getPropValueAsObject(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo) {
		return Convert.toObject(this.getPropValueAsDouble(context, propAccessDefine, mo));
	}
		private DoubleModelPropAccessor() {
	}
	static final DoubleModelPropAccessor ACCESSOR = new DoubleModelPropAccessor();
}
