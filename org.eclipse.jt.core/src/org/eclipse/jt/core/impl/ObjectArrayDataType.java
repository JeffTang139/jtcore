/**
 * 
 */
package org.eclipse.jt.core.impl;

import java.io.IOException;
import java.lang.reflect.Array;

import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.Digester;
import org.eclipse.jt.core.type.Undigester;


class ObjectArrayDataType extends ArrayDataTypeBase {
	@Override
	final void regArrayDataTypeInConstructor() {
	}

	ObjectArrayDataType(Class<?> arrayClass, DataTypeInternal componentType) {
		super(arrayClass, componentType);
		if (this.isPrimitive) {
			throw new IllegalArgumentException("非对象数组类型: " + arrayClass);
		}
		regDataType(this);
	}

	public void digestType(Digester digester) {
		digester.update(TypeCodeSet.OBJECTS);
		digester.update(this.javaClass);
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(
				TypeCodeSet.OBJECTS) {
			@Override
			protected DataType doUndigest(Undigester undigester)
					throws IOException, StructDefineNotFoundException {
				return DataTypeBase.dataTypeOfJavaClass(undigester
						.extractClass());
			}
		});
	}

	@Override
	public void writeObjectData(InternalSerializer serializer, Object obj)
			throws IOException, StructDefineNotFoundException {
		if (obj == null) {
			serializer.writeInt(-1);
		} else if (obj.getClass().isArray()
				&& this.componentJavaClass.isAssignableFrom(obj.getClass()
						.getComponentType())) {
			Object[] objs = (Object[]) obj;
			int len = objs.length;
			serializer.writeInt(len);
			for (int i = 0; i < len; i++) {
				serializer.writeObject(objs[i]);
			}
		} else {
			throw new UnsupportedOperationException("Unsupported data type: "
					+ obj.getClass());
		}
	}

	@Override
	public Object readObjectData(InternalDeserializer deserializer)
			throws IOException, StructDefineNotFoundException {
		int len = deserializer.readInt();
		if (len == -1) {
			return null;
		}

		Object[] objs = (Object[]) Array.newInstance(this.componentJavaClass,
				len);
		int oldHandle = deserializer.backupHandle();
		for (int i = 0; i < len; i++) {
			deserializer.restoreHandle(oldHandle);
			objs[i] = deserializer.readObject();
		}
		deserializer.restoreHandle(oldHandle);
		return objs;
	}

	// //////////////////////////////////////////
	// / NEW IO Serialization
	// //////////////////////////////////////////

	@Override
	public final boolean nioSerializeData(final NSerializer serializer,
			final Object object) {
		return serializer.writeObjectArrayData(this, (Object[]) object);
	}

}