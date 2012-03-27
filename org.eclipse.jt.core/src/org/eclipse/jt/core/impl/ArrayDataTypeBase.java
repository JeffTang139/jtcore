package org.eclipse.jt.core.impl;

import java.io.IOException;
import java.lang.reflect.Array;

import org.eclipse.jt.core.type.ArrayDataType;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.GUID;


/**
 * 数组复制器
 * 
 * @author Jeff Tang
 * 
 */
abstract class ArrayDataTypeBase extends ObjectDataTypeBase implements
		ArrayDataType {

	final Class<?> componentJavaClass;
	final boolean isPrimitive;
	final DataTypeInternal componentType;
	private String toString;

	@Override
	public String toString() {
		if (this.toString == null) {
			this.toString = this.getComponentType().toString() + "[]";
		}
		return this.toString;
	};

	@Override
	protected GUID calcTypeID() {
		return calcArryTypeID(this.componentType);
	}

	public final DataTypeInternal getComponentType() {
		return this.componentType;
	}

	public final Class<?> getComponentJavaClass() {
		return this.componentJavaClass;
	}

	public final boolean isPrimitive() {
		return this.isPrimitive;
	}

	@Override
	public final boolean isArray() {
		return true;
	}

	@Override
	final void regThisDataTypeInConstructor() {
	}

	void regArrayDataTypeInConstructor() {
		regDataType(this);
	}

	ArrayDataTypeBase(Class<?> arrayClass, DataTypeInternal componentType) {
		super(arrayClass);
		this.componentJavaClass = arrayClass.getComponentType();
		this.isPrimitive = this.componentJavaClass.isPrimitive();
		if (componentType == null) {
			componentType = DataTypeBase
					.dataTypeOfJavaClass(this.componentJavaClass);
		} else {
			final Class<?> cc = componentType.getJavaClass();
			if (cc != null && cc != this.componentJavaClass) {
				throw new IllegalArgumentException("无效的元素类型:" + componentType);
			}
		}
		this.componentType = componentType;
		if (this.getRegClass() != null) {
			this.componentType.setArrayOf(this);
		}
		this.regArrayDataTypeInConstructor();
	}

	public DataType calcPrecedence(DataType target) {
		throw new UnsupportedOperationException();
	}

	public final Object newArray(int length) {
		return Array.newInstance(this.componentJavaClass, length);
	}

	@Override
	public final Object assignNoCheckSrc(Object src, Object dest,
			OBJAContext objaContext) {
		if (this.isPrimitive) {
			final int sl = Array.getLength(src);
			if (sl == 0) {
				return src;
			}
			if (dest == null) {
				dest = Array.newInstance(this.componentJavaClass, sl);
			} else {
				final int dl = Array.getLength(dest);
				if (dl != sl) {
					dest = Array.newInstance(this.componentJavaClass, sl);
				}
			}
			System.arraycopy(src, 0, dest, 0, sl);
			return dest;
		} else {
			final Object[] srcA = (Object[]) src;
			Object[] destAsA = (Object[]) dest;
			final int sl = srcA.length;
			if (sl == 0) {
				return srcA;
			}
			final int dl = destAsA != null ? destAsA.length : 0;
			final Object[] destA = dl != sl ? (Object[]) Array.newInstance(
					this.componentJavaClass, sl) : destAsA;
			for (int i = 0; i < sl; i++) {
				destA[i] = objaContext.assign(srcA[i], i < dl ? destAsA[i]
						: null, null);
			}
			return destA;
		}
	}

	@Override
	public final boolean isInstance(Object obj) {
		if (obj == null) {
			return false;
		}
		Class<?> cl = obj.getClass();
		if (!cl.isArray()) {
			return false;
		}
		cl = cl.getComponentType();
		if (cl.isPrimitive() && cl == this.componentJavaClass) {
			return true;
		}
		return this.componentJavaClass.isAssignableFrom(cl); // 接受兼容的类型。
	}

	// //////////////////////////////////
	// Serialization

	@Override
	public final boolean supportSerialization() {
		return true;
	}

	@Override
	public abstract void writeObjectData(InternalSerializer serializer,
			Object obj) throws IOException, StructDefineNotFoundException;

	@Override
	public abstract Object readObjectData(InternalDeserializer deserializer)
			throws IOException, StructDefineNotFoundException;
}
