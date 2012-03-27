package org.eclipse.jt.core.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jt.core.misc.SafeItrList;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.Digester;
import org.eclipse.jt.core.type.Undigester;


/**
 * 列表复制器
 * 
 * @author Jeff Tang
 * 
 */
abstract class ListDataType<TList extends List<?>> extends RefDataType {

	@SuppressWarnings("unchecked")
	private static <TList extends List<?>> ListDataType<TList> findAssigner(
			Class<TList> listClass) {
		if (listClass == arrayListType.javaClass) {
			return (ListDataType<TList>) arrayListType;
		}
		if (listClass == dnaArrayListType.javaClass) {
			return (ListDataType<TList>) dnaArrayListType;
		}
		if (listClass == safeItrListType.javaClass) {
			return (ListDataType<TList>) safeItrListType;
		}
		return null;
	}

	private ListDataType(Class<TList> listClass) {
		super(listClass);
	}

	@Override
	public final void digestType(Digester digester) {
		digester.update(TypeCodeSet.LIST);
		digester.update(this.javaClass);
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(
				TypeCodeSet.LIST) {
			@SuppressWarnings("unchecked")
			@Override
			protected DataType doUndigest(Undigester undigester)
					throws IOException, StructDefineNotFoundException {
				return ListDataType.findAssigner(undigester.extractClass());
			}
		});
	}

	abstract TList newDest(int size);

	@SuppressWarnings("unchecked")
	static final ListDataType<ArrayList> arrayListType = new ListDataType<ArrayList>(
			ArrayList.class) {
		@Override
		final ArrayList newDest(int size) {
			return new ArrayList(size);
		}
	};
	@SuppressWarnings("unchecked")
	static final ListDataType<DnaArrayList> dnaArrayListType = new ListDataType<DnaArrayList>(
			DnaArrayList.class) {
		@Override
		final DnaArrayList newDest(int size) {
			return new DnaArrayList(size);
		}
	};
	@SuppressWarnings("unchecked")
	static final ListDataType<SafeItrList> safeItrListType = new ListDataType<SafeItrList>(
			SafeItrList.class) {
		@Override
		final SafeItrList newDest(int size) {
			return new SafeItrList(size);
		}
	};

	/**
	 * 确保该类静态数据被JVM初始
	 */
	static void ensureStaticInited() {
	}

	// ////////////////////////////////////////////
	// Serialization

	@Override
	public boolean supportSerialization() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void writeObjectData(InternalSerializer serializer, Object obj)
			throws IOException, StructDefineNotFoundException {
		if (obj == null) {
			serializer.writeInt(-1);
		} else if (obj instanceof List) {
			List list = (List) obj;
			int size = list.size();
			serializer.writeInt(size);
			for (int i = 0; i < size; i++) {
				serializer.writeObject(list.get(i));
			}
		} else {
			throw new UnsupportedOperationException("unsupported data type: "
					+ obj.getClass());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object readObjectData(InternalDeserializer deserializer)
			throws IOException, StructDefineNotFoundException {
		int size = deserializer.readInt();
		if (size == -1) {
			return null;
		}
		List list = this.newDest(size);
		int oldHandle = deserializer.backupHandle();
		for (int i = 0; i < size; i++) {
			deserializer.restoreHandle(oldHandle);
			list.add(deserializer.readObject());
		}
		deserializer.restoreHandle(oldHandle);
		return list;
	}

}
