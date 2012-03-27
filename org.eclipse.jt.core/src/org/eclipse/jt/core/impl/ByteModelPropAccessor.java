package org.eclipse.jt.core.impl;
import org.eclipse.jt.core.None;
import org.eclipse.jt.core.type.Convert;
import org.eclipse.jt.core.type.GUID;

final class ByteModelPropAccessor extends ModelPropAccessor {
	@SuppressWarnings("unchecked")
	@Override
	byte getPropValueAsByte(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo) {
		if (context == null || mo == null) {
			throw new NullPointerException();
		}
		if (propAccessDefine.script != null
				&& propAccessDefine.script.scriptCallable()) {
			return Convert.toByte(propAccessDefine.script.executeScriptAsGetter(
					context, mo, propAccessDefine));
		} else if (propAccessDefine.accessor != null) {
			SpaceNode occorAtSave = propAccessDefine.accessor.getService()
					.updateContextSpace(context);
			try {
				return propAccessDefine.accessor.doGetByte(context, mo,
						propAccessDefine.ownerProperty);
			} catch (Throwable e) {
				throw Utils.tryThrowException(e);
			} finally {
				occorAtSave.updateContextSpace(context);
			}
		} else if (propAccessDefine.field != null) {
			return propAccessDefine.field.getFieldValueAsByte(mo);
		} else {
			throw new UnsupportedOperationException();
		}
	}
	@SuppressWarnings("unchecked")
	@Override
	void setPropValueAsByte(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo, 
			byte value) {
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
					propAccessDefine.accessor.doSetByte(context, mo,
							value, propAccessDefine.ownerProperty);
				} catch (Throwable e) {
					throw Utils.tryThrowException(e);
				} finally {
					occorAtSave.updateContextSpace(context);
				}
			} else if (propAccessDefine.field != null) {
				propAccessDefine.field.setFieldValueAsByte(mo, value);
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
		this.setPropValueAsByte(context, propAccessDefine, mo, 
				Convert.toByte(value));
	}
	@Override
	boolean getPropValueAsBoolean(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo) {
		return Convert.toBoolean(this.getPropValueAsByte(context, propAccessDefine, mo));
	}
	@Override
	void setPropValueAsShort(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo, 
			short value) {
		this.setPropValueAsByte(context, propAccessDefine, mo, 
				Convert.toByte(value));
	}
	@Override
	short getPropValueAsShort(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo) {
		return Convert.toShort(this.getPropValueAsByte(context, propAccessDefine, mo));
	}
	@Override
	void setPropValueAsInt(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo, 
			int value) {
		this.setPropValueAsByte(context, propAccessDefine, mo, 
				Convert.toByte(value));
	}
	@Override
	int getPropValueAsInt(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo) {
		return Convert.toInt(this.getPropValueAsByte(context, propAccessDefine, mo));
	}
	@Override
	void setPropValueAsLong(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo, 
			long value) {
		this.setPropValueAsByte(context, propAccessDefine, mo, 
				Convert.toByte(value));
	}
	@Override
	long getPropValueAsLong(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo) {
		return Convert.toLong(this.getPropValueAsByte(context, propAccessDefine, mo));
	}
	@Override
	void setPropValueAsDate(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo, 
			long value) {
		this.setPropValueAsByte(context, propAccessDefine, mo, 
				Convert.toByte(value));
	}
	@Override
	long getPropValueAsDate(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo) {
		return Convert.toDate(this.getPropValueAsByte(context, propAccessDefine, mo));
	}
	@Override
	void setPropValueAsDouble(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo, 
			double value) {
		this.setPropValueAsByte(context, propAccessDefine, mo, 
				Convert.toByte(value));
	}
	@Override
	double getPropValueAsDouble(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo) {
		return Convert.toDouble(this.getPropValueAsByte(context, propAccessDefine, mo));
	}
	@Override
	void setPropValueAsFloat(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo, 
			float value) {
		this.setPropValueAsByte(context, propAccessDefine, mo, 
				Convert.toByte(value));
	}
	@Override
	float getPropValueAsFloat(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo) {
		return Convert.toFloat(this.getPropValueAsByte(context, propAccessDefine, mo));
	}
	@Override
	void setPropValueAsString(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo, 
			String value) {
		this.setPropValueAsByte(context, propAccessDefine, mo, 
				Convert.toByte(value));
	}
	@Override
	String getPropValueAsString(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo) {
		return Convert.toString(this.getPropValueAsByte(context, propAccessDefine, mo));
	}
	@Override
	void setPropValueAsGUID(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo, 
			GUID value) {
		this.setPropValueAsByte(context, propAccessDefine, mo, 
				Convert.toByte(value));
	}
	@Override
	GUID getPropValueAsGUID(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo) {
		return Convert.toGUID(this.getPropValueAsByte(context, propAccessDefine, mo));
	}
	@Override
	void setPropValueAsBytes(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo, 
			byte[] value) {
		this.setPropValueAsByte(context, propAccessDefine, mo, 
				Convert.toByte(value));
	}
	@Override
	byte[] getPropValueAsBytes(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo) {
		return Convert.toBytes(this.getPropValueAsByte(context, propAccessDefine, mo));
	}
	@Override
	void setPropValueAsObject(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo, 
			Object value) {
		this.setPropValueAsByte(context, propAccessDefine, mo, 
				Convert.toByte(value));
	}
	@Override
	Object getPropValueAsObject(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo) {
		return Convert.toObject(this.getPropValueAsByte(context, propAccessDefine, mo));
	}
		private ByteModelPropAccessor() {
	}
	static final ByteModelPropAccessor ACCESSOR = new ByteModelPropAccessor();
}
