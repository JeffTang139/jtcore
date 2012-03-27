package org.eclipse.jt.core.impl;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Comparator;

import org.eclipse.jt.core.def.arg.ArgumentDeclare;
import org.eclipse.jt.core.def.model.ModelFieldDeclare;
import org.eclipse.jt.core.def.obja.DynamicObject;
import org.eclipse.jt.core.def.obja.StructField;
import org.eclipse.jt.core.def.obja.StructFieldDeclare;
import org.eclipse.jt.core.def.table.AsTableField;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.misc.SXMergeHelper;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.Digester;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.ObjectDataType;
import org.eclipse.jt.core.type.ReadableValue;
import org.eclipse.jt.core.type.WritableValue;


/**
 * 结构字段定义实现
 * 
 * @author Jeff Tang
 * 
 */
class StructFieldDefineImpl extends FieldDefineImpl implements
		StructFieldDeclare, ArgumentDeclare, ModelFieldDeclare,
		FieldValueAccessor {
	public final void digestType(Digester digester) {
		this.digestAuthAndName(digester);
		this.type.digestType(digester);
		digester.update(this.isStateField);
		digester.update(this.isReadonly);
		digester.update(this.isKeepValid);
	}

	public final StructDefineImpl getOwner() {
		return this.owner;
	}

	/**
	 * 拥有者
	 */
	final StructDefineImpl owner;
	/**
	 * 是否是状态字段
	 */
	private boolean isStateField;
	/**
	 * 是否是Java的类字段
	 */
	boolean isJavaField; // default is false
	/**
	 * 偏移量
	 */
	private int offset;
	/**
	 * 空值偏移量，-1代表字段为object类型
	 */
	private int nullOffset;
	/**
	 * 字段访问器,需要在定义各确定后再设置
	 */
	private FieldAccessor accessor;
	/**
	 * 默认值
	 */
	private ReadableValue defaultReadableValue;

	Field javaField;
	AsTableField asTableField;
	/**
	 * 字段所在位置
	 */
	int structFieldIndex = -1;

	/**
	 * 返回null标记
	 */
	final boolean isFieldValueNullNoCheck(DynObj dynSO) {
		if (this.nullOffset < 0) {
			return this.accessor.getObjectD(this.offset, dynSO) == null;
		} else if (this.nullOffset < DynObj.null_mask_bits) {
			return (dynSO.masks & (1 << this.nullOffset)) != 0;
		} else {
			int bitOff = this.nullOffset - DynObj.null_mask_bits;
			int byteOff = this.owner.nullByteOffset + (bitOff >> 3);// 2^3=8
			return (dynSO.bin[byteOff] & (1 << (bitOff & 0x7))) != 0;
		}
	}

	final boolean isFieldValueNullNoCheck(Object so) {
		return this.accessor.memBytes == 0
				&& this.accessor.getObject(this.offset, so) == null;
	}

	/**
	 * 一定要在调用前检查this.nullOffset>=0
	 * 
	 * @param dynSO
	 * @param isnull
	 */
	private final void setIsNullMask(DynObj dynSO, boolean isnull) {
		if (this.nullOffset < DynObj.null_mask_bits) {
			if (isnull) {
				dynSO.masks |= 1 << this.nullOffset;
			} else {
				dynSO.masks &= ~(1 << this.nullOffset);
			}
		} else {
			int bitOff = this.nullOffset - DynObj.null_mask_bits;
			int byteOff = this.owner.nullByteOffset + (bitOff >> 3);// 2^3=8
			if (isnull) {
				dynSO.bin[byteOff] |= (1 << (bitOff & 0x7));
			} else {
				dynSO.bin[byteOff] &= ~(1 << (bitOff & 0x7));
			}
		}
	}

	/**
	 * 装载字段和默认值
	 */
	final void loadAccessorAndDefaultValue(int structFieldIndex) {
		this.structFieldIndex = structFieldIndex;
		this.accessor = FieldAccessor.getAccessor(this.type, this.isJavaField);
		if (super.defaultValue instanceof ReadableValue) {
			this.defaultReadableValue = (ReadableValue) super.defaultValue;
		} else {
			this.defaultReadableValue = ReadableValue.NULL;
		}
	}

	final boolean hasDefaultReadableValue() {
		return this.defaultReadableValue != ReadableValue.NULL;
	}

	/**
	 * 比较两个字段的类型的大小，若用于排序，会使字段按其类型的大小降序排列。
	 */
	static final Comparator<StructFieldDefineImpl> FIELD_NAME_CMP = new Comparator<StructFieldDefineImpl>() {
		public int compare(StructFieldDefineImpl o1, StructFieldDefineImpl o2) {
			return o1.name.compareTo(o2.name);
		}
	};
	/**
	 * 比较两个字段的类型的大小，若用于排序，按照字段的序列化顺序进行排序<br>
	 * 即，原始类型最先，bool类型随后，对象类型最后。
	 */
	static final Comparator<StructFieldDefineImpl> FIELD_NIO_SRLZ_CMP = new Comparator<StructFieldDefineImpl>() {
		public int compare(StructFieldDefineImpl o1, StructFieldDefineImpl o2) {
			final int o1i = (o1.type == BooleanType.TYPE) ? 1
					: (o1.type instanceof ObjectDataType ? 2 : 0);
			final int o2i = (o2.type == BooleanType.TYPE) ? 1
					: (o2.type instanceof ObjectDataType ? 2 : 0);
			return o1i - o2i;
		}
	};

	final void loadDefaultNoCheck(DynObj dynSO) {
		this.accessor.setValueD(this.type, this.offset, dynSO,
				this.defaultReadableValue);
	}

	final void loadDefaultNoCheck(Object so) {
		this.accessor.setValue(this.type, this.offset, so,
				this.defaultReadableValue);
	}

	final void assignNoCheck(Object src, Object dest, OBJAContext objaContext) {
		if (this.isStateField) {
			this.accessor
					.assign(this.type, this.offset, src, dest, objaContext);
		} else {
			this.accessor.setValue(this.type, this.offset, dest,
					this.defaultReadableValue);
		}
	}

	final void assignNoCheck(DynObj src, DynObj dest, OBJAContext objaContext) {
		if (this.isStateField) {
			this.accessor.assignD(this.type, this.offset, src, dest,
					objaContext);
		} else {
			this.accessor.setValueD(this.type, this.offset, dest,
					this.defaultReadableValue);
		}
	}

	/**
	 * 计算字段的偏移量
	 * 
	 * @param nullOffset
	 *            可用的nullOffset;
	 * @return 返回下一个可用的nullOffset
	 */
	final int initDynObjOffsets(int nullOffset) {
		if (this.accessor.memBytes == 0) {// Object field
			if (!this.isJavaField) {
				this.offset = this.owner.refCount++;
			}
			this.nullOffset = -1;
			return nullOffset;
		} else {
			if (!this.isJavaField) {
				this.offset = this.accessor
						.getBinDynFieldOffset(this.owner.binSize);
				this.owner.binSize += this.accessor.memBytes;
			}
			this.nullOffset = nullOffset;
			return nullOffset + 1;
		}
	}

	final void initObjNullOffset() {
		if (this.accessor.memBytes == 0) {// Object field
			this.nullOffset = -1;
		}
	}

	final void setJavaField(Field javaField, StructField ano,
			AsTableField anTableField) {
		this.javaField = javaField;
		this.asTableField = anTableField;
		if (this.title == null && this.title.length() == 0) {
			this.title = ano != null ? Utils.noneNull(ano.title())
					: anTableField != null ? Utils.noneNull(anTableField
							.title()) : "";
		}
		if (this.description == null && this.description.length() == 0) {
			this.description = ano != null ? Utils.noneNull(ano.description())
					: anTableField != null ? Utils.noneNull(anTableField
							.description()) : "";
		}
		this.isReadonly = (Modifier.FINAL & javaField.getModifiers()) != 0;
		// XXX 借用了Java的transient机制。
		this.isStateField = ano != null ? ano.stateField() : ((javaField
				.getModifiers() & Modifier.TRANSIENT) == 0);
		this.offset = (int) Utils.objectFieldOffset(javaField);
		this.isJavaField = true;
	}

	/**
	 * 当前字段类型与目标类型兼容（可以被目标类型赋值）
	 * 
	 * @param type
	 * @return
	 */
	final boolean typeCompatible(DataType type) {
		DataType targetRoot = type.getRootType();
		return this.type.getRootType() == targetRoot
				|| (this.type instanceof EnumTypeImpl<?> && (targetRoot == StringType.TYPE || EnumTypeImpl
						.ordinalSupport(targetRoot)));
	}

	public StructFieldDefineImpl(StructDefineImpl owner, String name,
			DataType type) {
		super(name, type);
		if (owner == null) {
			throw new NullArgumentException("owner");
		}
		this.owner = owner;
		this.isStateField = true;
	}

	// /////////////////////////////////////////////////
	// /////// implements
	// ///////////////////////////////////////////////////
	public final boolean isStateField() {
		return this.isStateField;
	}

	public final void setStateField(boolean value) {
		this.checkModifiable();
		this.isStateField = value;
	}

	public final void setFieldValue(Object so, ReadableValue value) {
		DynObj dynSO = this.owner.checkSO(so);
		if (dynSO != null) {
			this.accessor.setValueD(this.type, this.offset, dynSO, value);
			if (this.nullOffset >= 0 && value.isNull()) {
				this.setIsNullMask(dynSO, true);
			}
		} else {
			this.accessor.setValue(this.type, this.offset, so, value);
		}
	}

	public final void setFieldValue(DynamicObject dynSO, ReadableValue value) {
		this.owner.checkSO(dynSO);
		this.accessor.setValueD(this.type, this.offset, dynSO, value);
		if (this.nullOffset >= 0 && value.isNull()) {
			this.setIsNullMask(dynSO, true);
		}
	}

	public final void assignFieldValueTo(Object so, WritableValue target) {
		DynObj dynSO = this.owner.checkSO(so);
		if (dynSO != null) {
			if (this.nullOffset >= 0 && this.isFieldValueNullNoCheck(dynSO)) {
				target.setNull();
			} else {
				this.accessor.assignToD(this.offset, dynSO, target);
			}
		} else {
			this.accessor.assignTo(this.offset, so, target);
		}

	}

	public final void assignFieldValueTo(DynamicObject dynSO,
			WritableValue target) {
		this.owner.checkSO(dynSO);
		if (this.nullOffset >= 0 && this.isFieldValueNullNoCheck(dynSO)) {
			target.setNull();
		} else {
			this.accessor.assignToD(this.offset, dynSO, target);
		}
	}

	public final void loadFieldDefaultValue(DynamicObject dynSO) {
		this.owner.checkSO(dynSO);
		this.accessor.setValueD(this.type, this.offset, dynSO,
				this.defaultReadableValue);
	}

	public final void loadFieldDefaultValue(Object so) {
		DynObj dynSO = this.owner.checkSO(so);
		if (dynSO != null) {
			this.accessor.setValueD(this.type, this.offset, dynSO,
					this.defaultReadableValue);
		} else {
			this.accessor.setValue(this.type, this.offset, so,
					this.defaultReadableValue);
		}
	}

	public final boolean isFieldValueNull(DynamicObject dynSO) {
		this.owner.checkSO(dynSO);
		return this.isFieldValueNullNoCheck(dynSO);
	}

	public final boolean isFieldValueNull(Object so) {
		DynObj dynSO = this.owner.checkSO(so);
		if (dynSO != null) {
			return this.isFieldValueNullNoCheck(dynSO);
		} else {
			return this.isFieldValueNullNoCheck(so);
		}
	}

	public final void setFieldValueNull(DynamicObject dynSO) {
		this.owner.checkSO(dynSO);
		this.setFieldValueNullNoCheck(dynSO);
	}

	public final void setFieldValueNull(Object so) {
		DynObj dynSO = this.owner.checkSO(so);
		if (dynSO != null) {
			this.setFieldValueNullNoCheck(dynSO);
		} else {
			this.setFieldValueNullNoCheck(so);
		}
	}

	/**
	 * 为了资源引用而设计
	 */
	public final Object internalGet(Object so) {
		if (this.owner.isDynObj) {
			return this.accessor.getObjectD(this.offset, this.owner
					.toDynObj(so));
		} else {
			return this.accessor.getObject(this.offset, so);
		}
	}

	/**
	 * 为了资源引用而设计
	 */
	public final void internalSet(Object so, Object value) {
		if (this.owner.isDynObj) {
			this.accessor.setObjectD(this.type, this.offset, this.owner
					.toDynObj(so), value);
		} else {
			this.accessor.setObject(this.type, this.offset, so, value);
		}
	}

	final void assignFieldValueToNoCheck(DynObj dynSO, WritableValue target) {
		if (this.nullOffset >= 0 && this.isFieldValueNullNoCheck(dynSO)) {
			target.setNull();
		} else {
			this.accessor.assignToD(this.offset, dynSO, target);
		}
	}

	final void assignFieldValueToNoCheck(Object so, WritableValue target) {
		if (this.owner.isDynObj) {
			DynObj dynSO = this.owner.toDynObj(so);
			if (this.nullOffset >= 0 && this.isFieldValueNullNoCheck(dynSO)) {
				target.setNull();
			} else {
				this.accessor.assignToD(this.offset, dynSO, target);
			}
		} else {
			this.accessor.assignTo(this.offset, so, target);
		}

	}

	final void setFieldValueNoCheck(DynObj dynSO, ReadableValue value) {
		this.accessor.setValueD(this.type, this.offset, dynSO, value);
		if (this.nullOffset >= 0 && value.isNull()) {
			this.setIsNullMask(dynSO, true);
		}
	}

	final void setFieldValueNoCheck(Object so, ReadableValue value) {
		if (this.owner.isDynObj) {
			DynObj dynSO = this.owner.toDynObj(so);
			this.accessor.setValueD(this.type, this.offset, dynSO, value);
			if (this.nullOffset >= 0 && value.isNull()) {
				this.setIsNullMask(dynSO, true);
			}
		} else {
			this.accessor.setValue(this.type, this.offset, so, value);
		}
	}

	final void setFieldValueNullNoCheck(DynObj dynSO) {
		this.accessor.setValueD(this.type, this.offset, dynSO,
				ReadableValue.NULL);
		if (this.nullOffset >= 0) {
			this.setIsNullMask(dynSO, true);
		}
	}

	final void setFieldValueNullNoCheck(Object so) {
		this.accessor.setValue(this.type, this.offset, so, ReadableValue.NULL);
	}

	final void SETLMergeLongValueNoCheck(DynObj entity, long value) {
		this.accessor.SETLMergeLongValueNoCheck(this.offset, entity, value);
	}

	final void SETLMergeDoubleValueNoCheck(DynObj entity, double value) {
		this.accessor.SETLMergeDoubleValueNoCheck(this.offset, entity, value);
	}

	final void SETLMergeNullNoCheck(DynObj entity) {
		if (this.accessor.SETLMergeNullNoCheck(this.offset, entity)
				&& this.nullOffset >= 0) {
			this.setIsNullMask(entity, true);
		}
	}

	// /////////////////////////////////////////////////////
	// /////// 以下自动生成的代码，不要轻易修改
	// /////////////////////////////////////////////////////
	public final boolean getFieldValueAsBoolean(Object so) {
		DynObj dynSO = this.owner.checkSO(so);
		return dynSO != null ? this.accessor.getBooleanD(this.offset, dynSO)
				: this.accessor.getBoolean(this.offset, so);
	}

	public final boolean getFieldValueAsBoolean(DynamicObject dynSO) {
		this.owner.checkSO(dynSO);
		return this.accessor.getBooleanD(this.offset, dynSO);
	}

	public final void setFieldValueAsBoolean(Object so, boolean value) {
		DynObj dynSO = this.owner.checkSO(so);
		if (dynSO != null) {
			this.accessor.setBooleanD(this.type, this.offset, dynSO, value);
			if (this.nullOffset >= 0) {
				this.setIsNullMask(dynSO, false);
			}
		} else {
			this.accessor.setBoolean(this.type, this.offset, so, value);
		}
	}

	public final void setFieldValueAsBoolean(DynamicObject dynSO, boolean value) {
		this.owner.checkSO(dynSO);
		this.accessor.setBooleanD(this.type, this.offset, dynSO, value);
		if (this.nullOffset >= 0) {
			this.setIsNullMask(dynSO, false);
		}
	}

	public final byte getFieldValueAsByte(Object so) {
		DynObj dynSO = this.owner.checkSO(so);
		return dynSO != null ? this.accessor.getByteD(this.offset, dynSO)
				: this.accessor.getByte(this.offset, so);
	}

	public final byte getFieldValueAsByte(DynamicObject dynSO) {
		this.owner.checkSO(dynSO);
		return this.accessor.getByteD(this.offset, dynSO);
	}

	public final void setFieldValueAsByte(Object so, byte value) {
		DynObj dynSO = this.owner.checkSO(so);
		if (dynSO != null) {
			this.accessor.setByteD(this.type, this.offset, dynSO, value);
			if (this.nullOffset >= 0) {
				this.setIsNullMask(dynSO, false);
			}
		} else {
			this.accessor.setByte(this.type, this.offset, so, value);
		}
	}

	public final void setFieldValueAsByte(DynamicObject dynSO, byte value) {
		this.owner.checkSO(dynSO);
		this.accessor.setByteD(this.type, this.offset, dynSO, value);
		if (this.nullOffset >= 0) {
			this.setIsNullMask(dynSO, false);
		}
	}

	public final short getFieldValueAsShort(Object so) {
		DynObj dynSO = this.owner.checkSO(so);
		return dynSO != null ? this.accessor.getShortD(this.offset, dynSO)
				: this.accessor.getShort(this.offset, so);
	}

	public final short getFieldValueAsShort(DynamicObject dynSO) {
		this.owner.checkSO(dynSO);
		return this.accessor.getShortD(this.offset, dynSO);
	}

	public final void setFieldValueAsShort(Object so, short value) {
		DynObj dynSO = this.owner.checkSO(so);
		if (dynSO != null) {
			this.accessor.setShortD(this.type, this.offset, dynSO, value);
			if (this.nullOffset >= 0) {
				this.setIsNullMask(dynSO, false);
			}
		} else {
			this.accessor.setShort(this.type, this.offset, so, value);
		}
	}

	public final void setFieldValueAsShort(DynamicObject dynSO, short value) {
		this.owner.checkSO(dynSO);
		this.accessor.setShortD(this.type, this.offset, dynSO, value);
		if (this.nullOffset >= 0) {
			this.setIsNullMask(dynSO, false);
		}
	}

	public final int getFieldValueAsInt(Object so) {
		DynObj dynSO = this.owner.checkSO(so);
		return dynSO != null ? this.accessor.getIntD(this.offset, dynSO)
				: this.accessor.getInt(this.offset, so);
	}

	public final int getFieldValueAsInt(DynamicObject dynSO) {
		this.owner.checkSO(dynSO);
		return this.accessor.getIntD(this.offset, dynSO);
	}

	public final void setFieldValueAsInt(Object so, int value) {
		DynObj dynSO = this.owner.checkSO(so);
		if (dynSO != null) {
			this.accessor.setIntD(this.type, this.offset, dynSO, value);
			if (this.nullOffset >= 0) {
				this.setIsNullMask(dynSO, false);
			}
		} else {
			this.accessor.setInt(this.type, this.offset, so, value);
		}
	}

	public final void setFieldValueAsInt(DynamicObject dynSO, int value) {
		this.owner.checkSO(dynSO);
		this.accessor.setIntD(this.type, this.offset, dynSO, value);
		if (this.nullOffset >= 0) {
			this.setIsNullMask(dynSO, false);
		}
	}

	public final long getFieldValueAsLong(Object so) {
		DynObj dynSO = this.owner.checkSO(so);
		return dynSO != null ? this.accessor.getLongD(this.offset, dynSO)
				: this.accessor.getLong(this.offset, so);
	}

	public final long getFieldValueAsLong(DynamicObject dynSO) {
		this.owner.checkSO(dynSO);
		return this.accessor.getLongD(this.offset, dynSO);
	}

	public final void setFieldValueAsLong(Object so, long value) {
		DynObj dynSO = this.owner.checkSO(so);
		if (dynSO != null) {
			this.accessor.setLongD(this.type, this.offset, dynSO, value);
			if (this.nullOffset >= 0) {
				this.setIsNullMask(dynSO, false);
			}
		} else {
			this.accessor.setLong(this.type, this.offset, so, value);
		}
	}

	public final void setFieldValueAsLong(DynamicObject dynSO, long value) {
		this.owner.checkSO(dynSO);
		this.accessor.setLongD(this.type, this.offset, dynSO, value);
		if (this.nullOffset >= 0) {
			this.setIsNullMask(dynSO, false);
		}
	}

	public final long getFieldValueAsDate(Object so) {
		DynObj dynSO = this.owner.checkSO(so);
		return dynSO != null ? this.accessor.getDateD(this.offset, dynSO)
				: this.accessor.getDate(this.offset, so);
	}

	public final long getFieldValueAsDate(DynamicObject dynSO) {
		this.owner.checkSO(dynSO);
		return this.accessor.getDateD(this.offset, dynSO);
	}

	public final void setFieldValueAsDate(Object so, long value) {
		DynObj dynSO = this.owner.checkSO(so);
		if (dynSO != null) {
			this.accessor.setDateD(this.type, this.offset, dynSO, value);
			if (this.nullOffset >= 0) {
				this.setIsNullMask(dynSO, false);
			}
		} else {
			this.accessor.setDate(this.type, this.offset, so, value);
		}
	}

	public final void setFieldValueAsDate(DynamicObject dynSO, long value) {
		this.owner.checkSO(dynSO);
		this.accessor.setDateD(this.type, this.offset, dynSO, value);
		if (this.nullOffset >= 0) {
			this.setIsNullMask(dynSO, false);
		}
	}

	public final double getFieldValueAsDouble(Object so) {
		DynObj dynSO = this.owner.checkSO(so);
		return dynSO != null ? this.accessor.getDoubleD(this.offset, dynSO)
				: this.accessor.getDouble(this.offset, so);
	}

	public final double getFieldValueAsDouble(DynamicObject dynSO) {
		this.owner.checkSO(dynSO);
		return this.accessor.getDoubleD(this.offset, dynSO);
	}

	public final void setFieldValueAsDouble(Object so, double value) {
		DynObj dynSO = this.owner.checkSO(so);
		if (dynSO != null) {
			this.accessor.setDoubleD(this.type, this.offset, dynSO, value);
			if (this.nullOffset >= 0) {
				this.setIsNullMask(dynSO, false);
			}
		} else {
			this.accessor.setDouble(this.type, this.offset, so, value);
		}
	}

	public final void setFieldValueAsDouble(DynamicObject dynSO, double value) {
		this.owner.checkSO(dynSO);
		this.accessor.setDoubleD(this.type, this.offset, dynSO, value);
		if (this.nullOffset >= 0) {
			this.setIsNullMask(dynSO, false);
		}
	}

	public final float getFieldValueAsFloat(Object so) {
		DynObj dynSO = this.owner.checkSO(so);
		return dynSO != null ? this.accessor.getFloatD(this.offset, dynSO)
				: this.accessor.getFloat(this.offset, so);
	}

	public final float getFieldValueAsFloat(DynamicObject dynSO) {
		this.owner.checkSO(dynSO);
		return this.accessor.getFloatD(this.offset, dynSO);
	}

	public final void setFieldValueAsFloat(Object so, float value) {
		DynObj dynSO = this.owner.checkSO(so);
		if (dynSO != null) {
			this.accessor.setFloatD(this.type, this.offset, dynSO, value);
			if (this.nullOffset >= 0) {
				this.setIsNullMask(dynSO, false);
			}
		} else {
			this.accessor.setFloat(this.type, this.offset, so, value);
		}
	}

	public final void setFieldValueAsFloat(DynamicObject dynSO, float value) {
		this.owner.checkSO(dynSO);
		this.accessor.setFloatD(this.type, this.offset, dynSO, value);
		if (this.nullOffset >= 0) {
			this.setIsNullMask(dynSO, false);
		}
	}

	public final String getFieldValueAsString(Object so) {
		DynObj dynSO = this.owner.checkSO(so);
		return dynSO != null ? this.accessor.getStringD(this.offset, dynSO)
				: this.accessor.getString(this.offset, so);
	}

	public final String getFieldValueAsString(DynamicObject dynSO) {
		this.owner.checkSO(dynSO);
		return this.accessor.getStringD(this.offset, dynSO);
	}

	public final void setFieldValueAsString(Object so, String value) {
		DynObj dynSO = this.owner.checkSO(so);
		if (dynSO != null) {
			this.accessor.setStringD(this.type, this.offset, dynSO, value);
			if (this.nullOffset >= 0) {
				this.setIsNullMask(dynSO, false);
			}
		} else {
			this.accessor.setString(this.type, this.offset, so, value);
		}
	}

	public final void setFieldValueAsString(DynamicObject dynSO, String value) {
		this.owner.checkSO(dynSO);
		this.accessor.setStringD(this.type, this.offset, dynSO, value);
		if (this.nullOffset >= 0) {
			this.setIsNullMask(dynSO, false);
		}
	}

	public final GUID getFieldValueAsGUID(Object so) {
		DynObj dynSO = this.owner.checkSO(so);
		return dynSO != null ? this.accessor.getGUIDD(this.offset, dynSO)
				: this.accessor.getGUID(this.offset, so);
	}

	public final GUID getFieldValueAsGUID(DynamicObject dynSO) {
		this.owner.checkSO(dynSO);
		return this.accessor.getGUIDD(this.offset, dynSO);
	}

	public final void setFieldValueAsGUID(Object so, GUID value) {
		DynObj dynSO = this.owner.checkSO(so);
		if (dynSO != null) {
			this.accessor.setGUIDD(this.type, this.offset, dynSO, value);
		} else {
			this.accessor.setGUID(this.type, this.offset, so, value);
		}
	}

	public final void setFieldValueAsGUID(DynamicObject dynSO, GUID value) {
		this.owner.checkSO(dynSO);
		this.accessor.setGUIDD(this.type, this.offset, dynSO, value);
	}

	public final byte[] getFieldValueAsBytes(Object so) {
		DynObj dynSO = this.owner.checkSO(so);
		return dynSO != null ? this.accessor.getBytesD(this.offset, dynSO)
				: this.accessor.getBytes(this.offset, so);
	}

	public final byte[] getFieldValueAsBytes(DynamicObject dynSO) {
		this.owner.checkSO(dynSO);
		return this.accessor.getBytesD(this.offset, dynSO);
	}

	public final void setFieldValueAsBytes(Object so, byte[] value) {
		DynObj dynSO = this.owner.checkSO(so);
		if (dynSO != null) {
			this.accessor.setBytesD(this.type, this.offset, dynSO, value);
		} else {
			this.accessor.setBytes(this.type, this.offset, so, value);
		}
	}

	public final void setFieldValueAsBytes(DynamicObject dynSO, byte[] value) {
		this.owner.checkSO(dynSO);
		this.accessor.setBytesD(this.type, this.offset, dynSO, value);
	}

	public final Object getFieldValueAsObject(Object so) {
		DynObj dynSO = this.owner.checkSO(so);
		if (dynSO != null) {
			if (this.nullOffset >= 0 && this.isFieldValueNullNoCheck(dynSO)) {
				return null;
			}
			return this.accessor.getObjectD(this.offset, dynSO);
		} else {
			return this.accessor.getObject(this.offset, so);
		}
	}

	public final Object getFieldValueAsObject(DynamicObject dynSO) {
		this.owner.checkSO(dynSO);
		if (this.nullOffset >= 0 && this.isFieldValueNullNoCheck(dynSO)) {
			return null;
		}
		return this.accessor.getObjectD(this.offset, dynSO);
	}

	public final void setFieldValueAsObject(Object so, Object value) {
		DynObj dynSO = this.owner.checkSO(so);
		if (dynSO != null) {
			this.accessor.setObjectD(this.type, this.offset, dynSO, value);
			if (this.nullOffset >= 0) {
				this.setIsNullMask(dynSO, value == null);
			}
		} else {
			this.accessor.setObject(this.type, this.offset, so, value);
		}
	}

	public final void setFieldValueAsObject(DynamicObject dynSO, Object value) {
		this.owner.checkSO(dynSO);
		this.accessor.setObjectD(this.type, this.offset, dynSO, value);
		if (this.nullOffset >= 0) {
			this.setIsNullMask(dynSO, value == null);
		}
	}

	public final char getFieldValueAsChar(Object so) {
		DynObj dynSO = this.owner.checkSO(so);
		return dynSO != null ? this.accessor.getCharD(this.offset, dynSO)
				: this.accessor.getChar(this.offset, so);
	}

	public final char getFieldValueAsChar(DynamicObject dynSO) {
		this.owner.checkSO(dynSO);
		return this.accessor.getCharD(this.offset, dynSO);
	}

	public final void setFieldValueAsChar(Object so, char value) {
		DynObj dynSO = this.owner.checkSO(so);
		if (dynSO != null) {
			this.accessor.setCharD(this.type, this.offset, dynSO, value);
			if (this.nullOffset >= 0) {
				this.setIsNullMask(dynSO, false);
			}
		} else {
			this.accessor.setChar(this.type, this.offset, so, value);
		}
	}

	public final void setFieldValueAsChar(DynamicObject dynSO, char value) {
		this.owner.checkSO(dynSO);
		this.accessor.setCharD(this.type, this.offset, dynSO, value);
		if (this.nullOffset >= 0) {
			this.setIsNullMask(dynSO, false);
		}
	}

	final boolean getFieldValueAsBooleanNoCheck(DynObj dynSO) {
		return this.accessor.getBooleanD(this.offset, dynSO);
	}

	final boolean getFieldValueAsBooleanNoCheck(Object so) {
		return this.accessor.getBoolean(this.offset, so);
	}

	final void setFieldValueAsBooleanNoCheck(DynObj dynSO, boolean value) {
		this.accessor.setBooleanD(this.type, this.offset, dynSO, value);
		if (this.nullOffset >= 0) {
			this.setIsNullMask(dynSO, false);
		}
	}

	final void setFieldValueAsBooleanNoCheck(Object so, boolean value) {
		this.accessor.setBoolean(this.type, this.offset, so, value);
	}

	final byte getFieldValueAsByteNoCheck(DynObj dynSO) {
		return this.accessor.getByteD(this.offset, dynSO);
	}

	final byte getFieldValueAsByteNoCheck(Object so) {
		return this.accessor.getByte(this.offset, so);
	}

	final void setFieldValueAsByteNoCheck(DynObj dynSO, byte value) {
		this.accessor.setByteD(this.type, this.offset, dynSO, value);
		if (this.nullOffset >= 0) {
			this.setIsNullMask(dynSO, false);
		}
	}

	final void setFieldValueAsByteNoCheck(Object so, byte value) {
		this.accessor.setByte(this.type, this.offset, so, value);
	}

	final short getFieldValueAsShortNoCheck(DynObj dynSO) {
		return this.accessor.getShortD(this.offset, dynSO);
	}

	final short getFieldValueAsShortNoCheck(Object so) {
		return this.accessor.getShort(this.offset, so);
	}

	final void setFieldValueAsShortNoCheck(DynObj dynSO, short value) {
		this.accessor.setShortD(this.type, this.offset, dynSO, value);
		if (this.nullOffset >= 0) {
			this.setIsNullMask(dynSO, false);
		}
	}

	final void setFieldValueAsShortNoCheck(Object so, short value) {
		this.accessor.setShort(this.type, this.offset, so, value);
	}

	final int getFieldValueAsIntNoCheck(DynObj dynSO) {
		return this.accessor.getIntD(this.offset, dynSO);
	}

	final int getFieldValueAsIntNoCheck(Object so) {
		return this.accessor.getInt(this.offset, so);
	}

	final void setFieldValueAsIntNoCheck(DynObj dynSO, int value) {
		this.accessor.setIntD(this.type, this.offset, dynSO, value);
		if (this.nullOffset >= 0) {
			this.setIsNullMask(dynSO, false);
		}
	}

	final void setFieldValueAsIntNoCheck(Object so, int value) {
		this.accessor.setInt(this.type, this.offset, so, value);
	}

	final long getFieldValueAsLongNoCheck(DynObj dynSO) {
		return this.accessor.getLongD(this.offset, dynSO);
	}

	final long getFieldValueAsLongNoCheck(Object so) {
		return this.accessor.getLong(this.offset, so);
	}

	final void setFieldValueAsLongNoCheck(DynObj dynSO, long value) {
		this.accessor.setLongD(this.type, this.offset, dynSO, value);
		if (this.nullOffset >= 0) {
			this.setIsNullMask(dynSO, false);
		}
	}

	final void setFieldValueAsLongNoCheck(Object so, long value) {
		this.accessor.setLong(this.type, this.offset, so, value);
	}

	final long getFieldValueAsDateNoCheck(DynObj dynSO) {
		return this.accessor.getDateD(this.offset, dynSO);
	}

	final long getFieldValueAsDateNoCheck(Object so) {
		return this.accessor.getDate(this.offset, so);
	}

	final void setFieldValueAsDateNoCheck(DynObj dynSO, long value) {
		this.accessor.setDateD(this.type, this.offset, dynSO, value);
		if (this.nullOffset >= 0) {
			this.setIsNullMask(dynSO, false);
		}
	}

	final void setFieldValueAsDateNoCheck(Object so, long value) {
		this.accessor.setDate(this.type, this.offset, so, value);
	}

	final double getFieldValueAsDoubleNoCheck(DynObj dynSO) {
		return this.accessor.getDoubleD(this.offset, dynSO);
	}

	final double getFieldValueAsDoubleNoCheck(Object so) {
		return this.accessor.getDouble(this.offset, so);
	}

	final void setFieldValueAsDoubleNoCheck(DynObj dynSO, double value) {
		this.accessor.setDoubleD(this.type, this.offset, dynSO, value);
		if (this.nullOffset >= 0) {
			this.setIsNullMask(dynSO, false);
		}
	}

	final void setFieldValueAsDoubleNoCheck(Object so, double value) {
		this.accessor.setDouble(this.type, this.offset, so, value);
	}

	final float getFieldValueAsFloatNoCheck(DynObj dynSO) {
		return this.accessor.getFloatD(this.offset, dynSO);
	}

	final float getFieldValueAsFloatNoCheck(Object so) {
		return this.accessor.getFloat(this.offset, so);
	}

	final void setFieldValueAsFloatNoCheck(DynObj dynSO, float value) {
		this.accessor.setFloatD(this.type, this.offset, dynSO, value);
		if (this.nullOffset >= 0) {
			this.setIsNullMask(dynSO, false);
		}
	}

	final void setFieldValueAsFloatNoCheck(Object so, float value) {
		this.accessor.setFloat(this.type, this.offset, so, value);
	}

	final String getFieldValueAsStringNoCheck(DynObj dynSO) {
		return this.accessor.getStringD(this.offset, dynSO);
	}

	final String getFieldValueAsStringNoCheck(Object so) {
		return this.accessor.getString(this.offset, so);
	}

	final void setFieldValueAsStringNoCheck(DynObj dynSO, String value) {
		this.accessor.setStringD(this.type, this.offset, dynSO, value);
		if (this.nullOffset >= 0) {
			this.setIsNullMask(dynSO, false);
		}
	}

	final void setFieldValueAsStringNoCheck(Object so, String value) {
		this.accessor.setString(this.type, this.offset, so, value);
	}

	final GUID getFieldValueAsGUIDNoCheck(DynObj dynSO) {
		return this.accessor.getGUIDD(this.offset, dynSO);
	}

	final GUID getFieldValueAsGUIDNoCheck(Object so) {
		return this.accessor.getGUID(this.offset, so);
	}

	final void setFieldValueAsGUIDNoCheck(DynObj dynSO, GUID value) {
		this.accessor.setGUIDD(this.type, this.offset, dynSO, value);
	}

	final void setFieldValueAsGUIDNoCheck(Object so, GUID value) {
		this.accessor.setGUID(this.type, this.offset, so, value);
	}

	final byte[] getFieldValueAsBytesNoCheck(DynObj dynSO) {
		return this.accessor.getBytesD(this.offset, dynSO);
	}

	final byte[] getFieldValueAsBytesNoCheck(Object so) {
		return this.accessor.getBytes(this.offset, so);
	}

	final void setFieldValueAsBytesNoCheck(DynObj dynSO, byte[] value) {
		this.accessor.setBytesD(this.type, this.offset, dynSO, value);
	}

	final void setFieldValueAsBytesNoCheck(Object so, byte[] value) {
		this.accessor.setBytes(this.type, this.offset, so, value);
	}

	final Object getFieldValueAsObjectNoCheck(DynObj dynSO) {
		if (this.nullOffset >= 0 && this.isFieldValueNullNoCheck(dynSO)) {
			return null;
		}
		return this.accessor.getObjectD(this.offset, dynSO);
	}

	final Object getFieldValueAsObjectNoCheck(Object so) {
		return this.accessor.getObject(this.offset, so);
	}

	final void setFieldValueAsObjectNoCheck(DynObj dynSO, Object value) {
		this.accessor.setObjectD(this.type, this.offset, dynSO, value);
		if (this.nullOffset >= 0) {
			this.setIsNullMask(dynSO, false);
		}
	}

	final void setFieldValueAsObjectNoCheck(Object so, Object value) {
		this.accessor.setObject(this.type, this.offset, so, value);
	}

	final char getFieldValueAsCharNoCheck(DynObj dynSO) {
		return this.accessor.getCharD(this.offset, dynSO);
	}

	final char getFieldValueAsCharNoCheck(Object so) {
		return this.accessor.getChar(this.offset, so);
	}

	final void setFieldValueAsCharNoCheck(DynObj dynSO, char value) {
		this.accessor.setCharD(this.type, this.offset, dynSO, value);
		if (this.nullOffset >= 0) {
			this.setIsNullMask(dynSO, false);
		}
	}

	final void setFieldValueAsCharNoCheck(Object so, char value) {
		this.accessor.setChar(this.type, this.offset, so, value);
	}

	// /////////////////////////////////////////////////////
	// /////// 以上自动生成的代码，不要轻易修改
	// /////////////////////////////////////////////////////
	// ///////////////////////////
	// //// XML
	// //////////////////////////
	static final String xml_element_field = "field";
	static final String xml_attr_transient = "transient";

	@Override
	public String getXMLTagName() {
		return StructFieldDefineImpl.xml_element_field;
	}

	@Override
	public void render(SXElement element) {
		super.render(element);
		element.setBoolean(StructFieldDefineImpl.xml_attr_transient,
				!this.isStateField);
	}

	@Override
	void merge(SXElement element, SXMergeHelper helper) {
		super.merge(element, helper);
		this.isStateField = !element.getBoolean(
				StructFieldDefineImpl.xml_attr_transient, !this.isStateField);
	}

	/* -------------------------------------------------------------------- */
	// Serialization
	/* -------------------------------------------------------------------- */
	/**
	 * read from obj, write to sos.
	 */
	final void writeOut(Object obj, InternalSerializer serializer)
			throws IOException, StructDefineNotFoundException {
		if (this.owner.isDynObj) {
			DynObj dynObj = this.owner.toDynObj(obj);
			this.accessor.writeOutD(this.type, this.offset, dynObj, serializer);
		} else {
			this.accessor.writeOut(this.type, this.offset, obj, serializer);
		}
	}

	/**
	 * read from sod, write to obj.
	 */
	final void readIn(Object obj, InternalDeserializer deserializer)
			throws IOException, StructDefineNotFoundException {
		if (this.owner.isDynObj) {
			DynObj dynObj = this.owner.toDynObj(obj);
			this.accessor.readInD(this.type, this.offset, dynObj, deserializer);
		} else {
			this.accessor.readIn(this.type, this.offset, obj, deserializer);
		}
	}

	private StructFieldDefineImpl() {
		super("?", UnknownType.TYPE);
		this.owner = null;
	}

	static final StructFieldDefineImpl tag_field = new StructFieldDefineImpl();

	// /////////////////////////////////////////
	// / NEW IO Serialization

	static final StructFieldDefineImpl NIOSERIALIZE_OBJECTARRAY_FIELD = new StructFieldDefineImpl(
			true);

	static final StructFieldDefineImpl NIOSERIALIZE_CUSTOMDATA_FIELD = new StructFieldDefineImpl(
			false);

	StructFieldDefineImpl nextNIOSerializableField;

	final DynObj nioAsDynObj(Object obj) {
		if (this.owner != null && this.owner.isDynObj) {
			return this.owner.toDynObj(obj);
		}
		return null;
	}

	private StructFieldDefineImpl(boolean asObjArrayField) {
		super(asObjArrayField ? "ObjArray" : "CustomData", UnknownType.TYPE);
		this.owner = null;
		this.nextNIOSerializableField = this;
	}

	final boolean serialize(final NSerializer serializer, final Object object) {
		final DynObj dynamicObject = this.nioAsDynObj(object);
		return dynamicObject == null ? this.accessor.nioSerialize(serializer,
				this.type, object, this.offset) : this.accessor.nioSerialize(
				serializer, this.type, dynamicObject, this.offset);
	}

	final void unserialize(final NUnserializer unserializer, final Object object) {
		final DynObj dynamicObject = this.nioAsDynObj(object);
		if (dynamicObject == null) {
			this.accessor.nioUnserialize(unserializer, this.type, object,
					this.offset);
		} else {
			this.accessor.nioUnserialize(unserializer, this.type,
					dynamicObject, this.offset);
		}
	}
}