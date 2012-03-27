package org.eclipse.jt.core.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.jt.core.None;
import org.eclipse.jt.core.auth.ActorState;
import org.eclipse.jt.core.def.obja.StructClass;
import org.eclipse.jt.core.impl.NUnserializer.ObjectTypeQuerier;
import org.eclipse.jt.core.invoke.TaskState;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.GUID;

import sun.misc.Unsafe;

import com.sun.jmx.snmp.tasks.Task;

/**
 * 序列化反序列化过程测试程序，可根据需要删除该类，并不影响其它类的正常使用
 * 
 * @author Jeff Tang
 */
final class NSerializeTest {

	public static void main(String[] args) {
		// serializeTest();
		loadDataType();
		unserializeTest();
	}

	static final void serializeTest() {
		NSerializeTest.internalSerializeTest(null);
	}

	static final void loadDataType() {
		DataTypeBase.dataTypeOfJavaClass(Task.class);
		DataTypeBase.dataTypeOfJavaClass(ClusterSynTask.class);
		DataTypeBase.dataTypeOfJavaClass(NClusterResourceInitTask.class);
		DataTypeBase.dataTypeOfJavaClass(TaskState.class);
		DataTypeBase.dataTypeOfJavaClass(CoreAuthUserEntity.class);
		DataTypeBase.dataTypeOfJavaClass(CoreAuthActorEntity.class);
		DataTypeBase.dataTypeOfJavaClass(ActorState.class);
		DataTypeBase.dataTypeOfJavaClass(None.class);
		DataTypeBase.dataTypeOfJavaClass(Object[].class);
		DataTypeBase
				.dataTypeOfJavaClass(NClusterResourceInitTask.ResourceItem.class);
		DataTypeBase.dataTypeOfJavaClass(long[].class);
		DataTypeBase.dataTypeOfJavaClass(GUID[].class);
		DataTypeBase.dataTypeOfJavaClass(String.class);
		DataTypeBase.dataTypeOfJavaClass(GUID.class);
	}

	static final void internalSerializeTest(final Object testObject) {
		try {
			final File directory = new File(PATH_NAME);
			final File[] files = directory.listFiles();
			if (files != null) {
				for (File file : files) {
					file.delete();
				}
			}
			final NSerializer_1_0 serializer = new NSerializer_1_0();
			int fragmentIndex = 0;
			FileOutputStream fos = new FileOutputStream(FILE_NAME
					+ fragmentIndex);
			int length;
			byte[] bytes;
			long beginAddress;
			DataFragment fragment = new DataFragmentImpl(BYTE_BUFFER_SIZE);
			if (!serializer.serializeStart(testObject, fragment)) {
				do {
					length = fragment.getPosition();
					bytes = fragment.getBytes();
					beginAddress = START_OFFSET_BYTEARRAY;
					for (int index = 0; index < length; index++) {
						fos.write(UNSAFE.getByte(bytes, beginAddress++));
					}
					fos.flush();
					fos.close();
					fos = new FileOutputStream(FILE_NAME + (++fragmentIndex));
					fragment = new DataFragmentImpl(BYTE_BUFFER_SIZE);
				} while (!serializer.serializeRest(fragment));
			}
			length = fragment.getPosition();
			bytes = fragment.getBytes();
			beginAddress = START_OFFSET_BYTEARRAY;
			for (int index = 0; index < length; index++) {
				fos.write(UNSAFE.getByte(bytes, beginAddress++));
			}
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static final void unserializeTest() {
		try {
			final NUnserializer_1_0 unserializer = new NUnserializer_1_0(
					new ObjectTypeQuerier() {

						public DataType findElseAsync(GUID typeID) {
							return DataTypeBase.findDataType(typeID);
						}

					});
			final byte[] tempBuffer = new byte[BYTE_BUFFER_SIZE];
			int fragmentIndex = 0;
			FileInputStream fis = new FileInputStream(FILE_NAME + fragmentIndex);
			int len = fis.read(tempBuffer);
			DataFragment fragment = new DataFragmentImpl(BYTE_BUFFER_SIZE);
			for (int index = 0; index < len; index++) {
				fragment.writeByte(tempBuffer[index]);
			}
			fis.close();
			fragment.limit(len);
			fragment.setPosition(0);
			if (!unserializer.unserializeStart(fragment, null)) {
				do {
					fis = new FileInputStream(FILE_NAME + (++fragmentIndex));
					len = fis.read(tempBuffer);
					fragment = new DataFragmentImpl(BYTE_BUFFER_SIZE);
					for (int index = 0; index < len; index++) {
						fragment.writeByte(tempBuffer[index]);
					}
					fis.close();
					fragment.limit(len);
					fragment.setPosition(0);
				} while (!unserializer.unserializeRest(fragment));
			}
			final Object object = unserializer.getUnserialzedObject();
			System.out.println(object);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// ===========================================================================

	static final Unsafe UNSAFE = Unsf.unsafe;

	static final long START_OFFSET_BYTEARRAY = UNSAFE
			.arrayBaseOffset(byte[].class);

	// 测试参数
	static final String PATH_NAME = "C:\\Serialize Test\\";

	static final String FILE_NAME = PATH_NAME + "fragment.";

	static final int BYTE_BUFFER_SIZE = 1024;

	// FIXME 还有枚举类型和char类型没有通过测试
	static final ArrayList<Object> arrayList = new ArrayList<Object>();

	static {
		arrayList.add("xxxx");
		arrayList.add("yyyy");
		arrayList.add(true);
	}

	static final Object[] TEST_OBJECTS = new Object[] {
			new int[] { 1, 2, 3, 4 }, null, new SerializeTestClassI(),
			arrayList };

	static final Object[] _TEST_OBJECTS = new Object[] {
			new Object[] { null, "aaaa", new int[] { 4, 3, 1, 2 },
					new boolean[] { true, false, true, false, true } },
			new Object[] { null, new SerializeTestClassII(), 1 },
			Integer.class, "abcdef", new int[] { 1, 2, 3, 4 }, null,
			new SerializeTestClassI(), arrayList, 97, new Integer(97) };

	static final Object[] __TEST_OBJECTS = new Object[] { "aaaa",
			new SerializeTestClassII(), "abcdef", new SerializeTestClassI(),
			arrayList, 97 };

	// 测试类
	@StructClass
	static class SerializeTestClassI {

		SerializeTestClassII obj = new SerializeTestClassII();

		transient int i = 100;

		boolean b = false;

		boolean b2 = true;

		long[][] laa = new long[][] { new long[] { 0L, 1L, 2L },
				new long[] { 3L, 4L, 5L } };

	}

	@StructClass
	static class SerializeTestClassII {

		String ss = "aaaaaa";

		int ii = 5;

		int[] ia = new int[] { 1, 2, 3, 4 };

	}

	static class SerializeTestClassIII extends SerializeTestClassII {

		byte b = 3;

	}

}
