package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.model.ListPropertyValue;
import org.eclipse.jt.core.def.obja.StructDefine;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.EnumType;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.ObjectDataType;
import org.eclipse.jt.core.type.SequenceDataType;
import org.eclipse.jt.core.type.TypeDetector;
import org.eclipse.jt.core.type.TypeDetectorBase;


/**
 * 模型属性设置器
 * 
 * @author Jeff Tang
 * 
 */
abstract class ModelPropAccessor {
	abstract void setPropValueAsBoolean(ContextImpl<?, ?, ?> context,

	ModelPropAccessDefineImpl propAccessDefine, Object mo, boolean value);

	abstract boolean getPropValueAsBoolean(ContextImpl<?, ?, ?> context,

	ModelPropAccessDefineImpl propAccessDefine, Object mo);

	abstract void setPropValueAsByte(ContextImpl<?, ?, ?> context,

	ModelPropAccessDefineImpl propAccessDefine, Object mo, byte value);

	abstract byte getPropValueAsByte(ContextImpl<?, ?, ?> context,

	ModelPropAccessDefineImpl propAccessDefine, Object mo);

	abstract void setPropValueAsShort(ContextImpl<?, ?, ?> context,

	ModelPropAccessDefineImpl propAccessDefine, Object mo, short value);

	abstract short getPropValueAsShort(ContextImpl<?, ?, ?> context,

	ModelPropAccessDefineImpl propAccessDefine, Object mo);

	abstract void setPropValueAsInt(ContextImpl<?, ?, ?> context,

	ModelPropAccessDefineImpl propAccessDefine, Object mo, int value);

	abstract int getPropValueAsInt(ContextImpl<?, ?, ?> context,

	ModelPropAccessDefineImpl propAccessDefine, Object mo);

	abstract void setPropValueAsLong(ContextImpl<?, ?, ?> context,

	ModelPropAccessDefineImpl propAccessDefine, Object mo, long value);

	abstract long getPropValueAsLong(ContextImpl<?, ?, ?> context,

	ModelPropAccessDefineImpl propAccessDefine, Object mo);

	abstract void setPropValueAsDate(ContextImpl<?, ?, ?> context,

	ModelPropAccessDefineImpl propAccessDefine, Object mo, long value);

	abstract long getPropValueAsDate(ContextImpl<?, ?, ?> context,

	ModelPropAccessDefineImpl propAccessDefine, Object mo);

	abstract void setPropValueAsDouble(ContextImpl<?, ?, ?> context,

	ModelPropAccessDefineImpl propAccessDefine, Object mo, double value);

	abstract double getPropValueAsDouble(ContextImpl<?, ?, ?> context,

	ModelPropAccessDefineImpl propAccessDefine, Object mo);

	abstract void setPropValueAsFloat(ContextImpl<?, ?, ?> context,

	ModelPropAccessDefineImpl propAccessDefine, Object mo, float value);

	abstract float getPropValueAsFloat(ContextImpl<?, ?, ?> context,

	ModelPropAccessDefineImpl propAccessDefine, Object mo);

	abstract void setPropValueAsString(ContextImpl<?, ?, ?> context,

	ModelPropAccessDefineImpl propAccessDefine, Object mo, String value);

	abstract String getPropValueAsString(ContextImpl<?, ?, ?> context,

	ModelPropAccessDefineImpl propAccessDefine, Object mo);

	abstract void setPropValueAsGUID(ContextImpl<?, ?, ?> context,

	ModelPropAccessDefineImpl propAccessDefine, Object mo, GUID value);

	abstract GUID getPropValueAsGUID(ContextImpl<?, ?, ?> context,

	ModelPropAccessDefineImpl propAccessDefine, Object mo);

	abstract void setPropValueAsBytes(ContextImpl<?, ?, ?> context,

	ModelPropAccessDefineImpl propAccessDefine, Object mo, byte[] value);

	abstract byte[] getPropValueAsBytes(ContextImpl<?, ?, ?> context,

	ModelPropAccessDefineImpl propAccessDefine, Object mo);

	abstract void setPropValueAsObject(ContextImpl<?, ?, ?> context,

	ModelPropAccessDefineImpl propAccessDefine, Object mo, Object value);

	abstract Object getPropValueAsObject(ContextImpl<?, ?, ?> context,

	ModelPropAccessDefineImpl propAccessDefine, Object mo);

	// /////////////////////////////////////////////////////////////////////////
	static ModelPropAccessor setterOf(DataType type) {
		return type.detect(ModelPropAccessor.accessorDetector, null);
	}

	ListPropertyValue getPropValueAsList(ContextImpl<?, ?, ?> context,
			ModelPropAccessDefineImpl propAccessDefine, Object mo, Object ao) {
		// 子类实现
		throw new UnsupportedOperationException();
	}

	private final static TypeDetector<ModelPropAccessor, Object> accessorDetector = new TypeDetectorBase<ModelPropAccessor, Object>() {
		@Override
		public final ModelPropAccessor inBoolean(Object userData) {
			return BooleanModelPropAccessor.ACCESSOR;
		}

		@Override
		public final ModelPropAccessor inByte(Object userData) {
			return ByteModelPropAccessor.ACCESSOR;
		}

		@Override
		public final ModelPropAccessor inShort(Object userData) {
			return ShortModelPropAccessor.ACCESSOR;
		}

		@Override
		public final ModelPropAccessor inInt(Object userData) {
			return IntModelPropAccessor.ACCESSOR;
		}

		@Override
		public final ModelPropAccessor inLong(Object userData) {
			return LongModelPropAccessor.ACCESSOR;
		}

		@Override
		public final ModelPropAccessor inDate(Object userData) {
			return DateModelPropAccessor.ACCESSOR;
		}

		@Override
		public final ModelPropAccessor inDouble(Object userData) {
			return DoubleModelPropAccessor.ACCESSOR;
		}

		@Override
		public final ModelPropAccessor inFloat(Object userData) {
			return FloatModelPropAccessor.ACCESSOR;
		}

		@Override
		public final ModelPropAccessor inString(Object userData,
				SequenceDataType type) {
			return StringModelPropAccessor.ACCESSOR;
		}

		@Override
		public final ModelPropAccessor inGUID(Object userData) {
			return GUIDModelPropAccessor.ACCESSOR;
		}

		@Override
		public final ModelPropAccessor inBytes(Object userData,
				SequenceDataType type) {
			return BytesModelPropAccessor.ACCESSOR;
		}

		@Override
		public ModelPropAccessor inObject(Object userData, ObjectDataType type)
				throws Throwable {
			return ObjectModelPropAccessor.ACCESSOR;
		}

		@Override
		public final ModelPropAccessor inEnum(Object userData, EnumType<?> type) {
			return ObjectModelPropAccessor.ACCESSOR;
		}

		@Override
		public final ModelPropAccessor inStruct(Object userData,
				StructDefine structDefine) {
			return ObjectModelPropAccessor.ACCESSOR;
		}
	};
}
