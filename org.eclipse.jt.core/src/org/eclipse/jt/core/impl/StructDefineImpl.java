package org.eclipse.jt.core.impl;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.util.Arrays;

import org.eclipse.jt.core.None;
import org.eclipse.jt.core.def.FieldDefine;
import org.eclipse.jt.core.def.obja.DynamicObject;
import org.eclipse.jt.core.def.obja.StructClass;
import org.eclipse.jt.core.def.obja.StructDeclare;
import org.eclipse.jt.core.def.obja.StructField;
import org.eclipse.jt.core.def.table.AsTableField;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.misc.SXMergeHelper;
import org.eclipse.jt.core.serial.DataObjectTranslator;
import org.eclipse.jt.core.type.AssignCapability;
import org.eclipse.jt.core.type.DataTypable;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.Digester;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.Typable;
import org.eclipse.jt.core.type.TypeDetector;
import org.eclipse.jt.core.type.Undigester;


/**
 * �ṹ����Ļ���
 * 
 * @author Jeff Tang
 * 
 */
abstract class StructDefineImpl extends NamedDefineImpl implements
		StructDeclare, ObjectDataTypeInternal {

	public final boolean canDBTypeConvertTo(DataType target) {
		return false;
	}

	public final boolean isArray() {
		return false;
	}

	public Class<?> getRegClass() {
		return null;
	}

	@Override
	public final String toString() {
		return this.getTypeName();
	}

	private ArrayDataTypeBase arrayOf;

	public final ArrayDataTypeBase arrayOf() {
		final ArrayDataTypeBase a = this.arrayOf;
		if (a != null) {
			return a;
		}
		return this.arrayOf = DataTypeBase.arrayOf(this);
	}

	public final void setArrayOf(ArrayDataTypeBase type) {
		if (type == null) {
			throw new NullArgumentException("type");
		}
		if (this.arrayOf != null) {
			throw new UnsupportedOperationException("\"" + this + "\"����������\""
					+ this.arrayOf + "\"�Ѿ����ڣ��������ظ�����:\"" + type + "\"");
		}
		this.arrayOf = type;
	}

	/**
	 * ��ȡ����Ľṹ���壬�����͵ĸ��Ӷζ���Ч�����Ա�ǣ�
	 */
	public final static StructDefineImpl getStructDefine(Object obj) {
		if (obj == null) {
			throw new NullPointerException();
		}
		if (obj instanceof DynObj) {
			DynObj dynObj = (DynObj) obj;
			if (dynObj.define != null) {
				return dynObj.define;
			}
		}
		return DataTypeBase.getStaticStructDefine(obj.getClass());
	}

	public final Class<?> getJavaClass() {
		return this.soClass;
	}

	public void digestType(Digester digester) {
		digester.update(this.soClass);
		short c = (short) this.fields.size();
		digester.update(c);
		for (int i = 0; i < c; i++) {
			this.fields.get(i).digestType(digester);
		}
	}

	static StructDefineImpl undigest(Undigester undigester) {
		// TODO undigest struct define
		throw new UnsupportedOperationException();
	}

	public final StructDefineImpl getRootType() {
		return this;
	}

	public final boolean isInstance(Object obj) {
		// FIXME StructDefine �ļ̳���
		if (obj != null && this.soClass.isInstance(obj)) {
			if (this.isDynObj) {
				return ((DynObj) obj).define == this;
			} else {
				return true;
			}
		}
		return false;
	}

	public final Object tryConvert(Object convertFrom)
			throws NullArgumentException {
		if (convertFrom == null) {
			throw new NullArgumentException("convertFrom");
		}
		if (convertFrom instanceof DynamicObject) {
			DynObj fromDyn = (DynObj) convertFrom;
			StructDefineImpl fromSD = fromDyn.define;
			if (fromSD == null) {
				return null;
			}
			if (fromSD == this) {
				return convertFrom;
			}
			final int thisFsize = this.fields.size();
			final int thatFsize = fromSD.fields.size();
			if (this.isDynObj) {// ˫�����Ƕ�̬����
				DynObj to = this.newEmptyDynSO();
				DStructFieldVisitor dsfv = new DStructFieldVisitor();
				for (int i = 0; i < thisFsize; i++) {
					StructFieldDefineImpl thisField = this.fields.get(i);
					if (i < thatFsize) {
						dsfv.reset(thisField, to);
						try {
							fromSD.fields.get(i).assignFieldValueToNoCheck(
									fromDyn, dsfv);
						} catch (Throwable e) {
							return null;
						}
					} else if (thisField.hasDefaultReadableValue()) {
						thisField.loadDefaultNoCheck(to);
					} else {
						return null;
					}
				}
				return to;
			} else {// �Լ��Ƕ�̬���󣬶�Դ����
				Object to = this.newEmptySO();
				StructFieldVisitor dsfv = new StructFieldVisitor();
				for (int i = 0; i < thisFsize; i++) {
					StructFieldDefineImpl thisField = this.fields.get(i);
					if (i < thatFsize) {
						dsfv.reset(thisField, to);
						fromSD.fields.get(i).assignFieldValueToNoCheck(fromDyn,
								dsfv);
					} else if (thisField.hasDefaultReadableValue()) {
						thisField.loadDefaultNoCheck(to);
					} else {
						return null;
					}
				}
				return to;
			}
		} else {
			Class<?> fromClass = convertFrom.getClass();
			if (this.soClass == fromClass) {
				return convertFrom;
			}
			StructDefineImpl fromSD = DataTypeBase
					.getStaticStructDefine(fromClass);
			if (fromSD == null) {
				return null;
			}
			final int thisFsize = this.fields.size();
			final int thatFsize = fromSD.fields.size();
			if (this.isDynObj) {// �Լ��Ƕ�̬���󣬶�Դ����
				DynObj to = this.newEmptyDynSO();
				DStructFieldVisitor dsfv = new DStructFieldVisitor();
				for (int i = 0; i < thisFsize; i++) {
					StructFieldDefineImpl thisField = this.fields.get(i);
					if (i < thatFsize) {
						dsfv.reset(thisField, to);
						fromSD.fields.get(i).assignFieldValueToNoCheck(
								convertFrom, dsfv);
					} else if (thisField.hasDefaultReadableValue()) {
						thisField.loadDefaultNoCheck(to);
					} else {
						return null;
					}
				}
				return to;
			} else {// �����Ƕ�̬����
				Object to = this.newEmptySO();
				StructFieldVisitor dsfv = new StructFieldVisitor();
				for (int i = 0; i < thisFsize; i++) {
					StructFieldDefineImpl thisField = this.fields.get(i);
					if (i < thatFsize) {
						dsfv.reset(thisField, to);
						fromSD.fields.get(i).assignFieldValueToNoCheck(
								convertFrom, dsfv);
					} else if (thisField.hasDefaultReadableValue()) {
						thisField.loadDefaultNoCheck(to);
					} else {
						return null;
					}
				}
				return to;
			}
		}
	}

	public final AssignCapability isAssignableFrom(DataType another) {
		if (this == another) {
			return AssignCapability.SAME;
		}
		if (another instanceof StructDefineImpl) {
			StructDefineImpl anotherSD = (StructDefineImpl) another;
			AssignCapability ac = AssignCapability.IMPLICIT;
			int thisFsize = this.fields.size();
			int thatFsize = anotherSD.fields.size();
			for (int i = 0; i < thisFsize; i++) {
				StructFieldDefineImpl thisField = this.fields.get(i);
				if (i < thatFsize) {
					AssignCapability fac = thisField.type
							.isAssignableFrom(anotherSD.fields.get(i).type);
					switch (fac) {
					case NO:
						return AssignCapability.NO;
					case EXPLICIT:
						if (ac == AssignCapability.IMPLICIT) {
							ac = AssignCapability.EXPLICIT;
						}
						break;
					case CONVERT:
						ac = AssignCapability.CONVERT;
						break;
					case IMPLICIT:
					case SAME:
						break;
					default:
						throw new IllegalStateException();
					}
				} else if (thisField.hasDefaultReadableValue()) {
					ac = AssignCapability.CONVERT;
				} else {
					return AssignCapability.NO;
				}
			}
			if (thisFsize < thatFsize && ac != AssignCapability.CONVERT) {
				ac = AssignCapability.EXPLICIT;
			}
			return ac;
		} else {
			return AssignCapability.NO;
		}
	}

	public boolean isBytes() {
		return false;
	}

	public boolean isNumber() {
		return false;
	}

	public boolean isString() {
		return false;
	}

	public DataType calcPrecedence(DataType target) {
		throw new UnsupportedOperationException();
	}

	public boolean accept(DataType type) {
		return false;
	}

	/**
	 * �Ƿ��Ǵ����
	 */
	public final boolean isLOB() {
		return false;
	}

	public final boolean isDBType() {
		return false;
	}

	/**
	 * �ṹ�����Ӧ��Java��
	 */
	protected final Class<?> soClass;
	/**
	 * �Ƿ��Ƕ�̬����
	 */
	protected final boolean isDynObj;
	/**
	 * �ֶ�����
	 */
	protected final NamedDefineContainerImpl<StructFieldDefineImpl> fields = new NamedDefineContainerImpl<StructFieldDefineImpl>();
	private final static StructFieldDefineImpl[] emptyFieldArray = {};
	/**
	 * Ϊ����߸��ƻ��¡ʱ��Ч�ʣ����Ļ���
	 */
	private StructFieldDefineImpl[] fieldArray = emptyFieldArray;
	private StructFieldDefineImpl[] serializableFields;

	final StructFieldDefineImpl[] serializableFields() {
		if (this.serializableFields == null) {
			final int totalSize = this.fields.size();
			StructFieldDefineImpl[] fs = new StructFieldDefineImpl[totalSize];
			int size = 0;
			StructFieldDefineImpl field;
			for (int i = 0; i < totalSize; i++) {
				field = this.fields.get(i);
				if (field.isStateField()) {
					fs[size++] = field;
				}
			}
			StructFieldDefineImpl[] fs0 = new StructFieldDefineImpl[size];
			System.arraycopy(fs, 0, fs0, 0, size);
			this.serializableFields = fs0;
		}
		return this.serializableFields;
	}

	final ResultSetReader newResultSetReader(ResultSet resultSet) {
		return this.isDynObj ? new ResultSetDynObjReader(resultSet)
				: new ResultSetStdObjReader(resultSet);
	}

	final Object[] soAsValues(Object so, int reserved) {
		if (so == null) {
			throw new NullPointerException();
		}
		int l = this.fields.size() + reserved;
		if (l == 0) {
			return Utils.emptyObjectArray;
		}
		Object[] array = new Object[l];
		for (int i = reserved, j = 0; i < l; j++, i++) {
			array[i] = this.fields.get(j).getFieldValueAsObject(so);
		}
		return array;
	}

	public final Object valuesAsSo(Object[] values) {
		Object so = this.newEmptySO();
		int c = this.fields.size();
		int ac = values != null ? values.length : 0;
		int i = 0;
		while (i < c && i < ac) {
			this.fields.get(i).setFieldValueAsObject(so, values[i]);
			i++;
		}
		while (i < c) {
			this.fields.get(i).loadDefaultNoCheck(so);
			i++;
		}
		return so;
	}

	/**
	 * �����Ʊ���Ĵ�С
	 */
	int binSize;
	/**
	 * �ձ�ǣ��ֽڣ�ƫ��
	 */
	int nullByteOffset;
	/**
	 * ���ñ���ĸ���
	 */
	int refCount;

	final void tryLoadJavaFields(boolean allJavaFields) {
		Class<?> endClass = this.soClass;
		if (endClass == None.class) {
			return;
		}
		while (endClass != DynamicObject.class && endClass != DynObj.class
				&& endClass != Object.class) {
			endClass = endClass.getSuperclass();
		}
		allJavaFields |= this.isDynObj;
		boolean structClass = allJavaFields; // XXX Ŀǰ�������¶��ϵĹᴩ��
		for (Class<?> clazz = this.soClass; clazz != endClass; clazz = clazz
				.getSuperclass()) {
			if (!structClass && clazz.getAnnotation(StructClass.class) != null) {
				structClass = true;
			}
			Field[] javaFields = clazz.getDeclaredFields();
			for (int i = 0; i < javaFields.length; i++) {
				Field javaField = javaFields[i];
				int javaFieldModifiers = javaField.getModifiers();
				if ((javaFieldModifiers & Modifier.STATIC) != 0) {
					continue;
				}
				StructField anStructField = javaField
						.getAnnotation(StructField.class);
				AsTableField anTableField = javaField
						.getAnnotation(AsTableField.class);
				if (!structClass && anStructField == null
						&& anTableField == null) {
					continue;
				}
				String fieldName = anStructField != null ? anStructField.name()
						: anTableField != null ? anTableField.name() : null;
				if (fieldName == null || fieldName.length() == 0) {
					fieldName = javaField.getName();
				}
				DataType fieldType = DataTypeBase.typeOf(javaField.getType(),
						javaField.getGenericType(), this.soClass);
				if (fieldType == UnknownType.TYPE) {
					continue;
				}
				if (fieldType == LongType.TYPE && anStructField != null
						&& anStructField.asDate()) {
					fieldType = DateType.TYPE;
				}
				StructFieldDefineImpl structField = this.fields.find(fieldName);
				if (structField == null) {
					structField = new StructFieldDefineImpl(this, fieldName,
							fieldType);
					structField.setJavaField(javaField, anStructField,
							anTableField);
					this.fields.add(structField);
				} else if (structField.typeCompatible(fieldType)) {
					structField.setJavaField(javaField, anStructField,
							anTableField);
				} else {
					continue;
				}
			}
		}
	}

	private static IllegalArgumentException illegalSO() {
		return new IllegalArgumentException("ʵ��������");
	}

	/**
	 * ���캯��
	 * 
	 * @param soClass
	 *            �ṹ�����Ӧ��Java��
	 * @param name
	 *            ����
	 */
	StructDefineImpl(String name, Class<?> soClass) {
		super(name);
		if (soClass == null) {
			throw new NullArgumentException("soClass");
		}
		if (soClass.isInterface()) {
			throw new IllegalArgumentException("soClass in StructDefine("
					+ name + ") can't be interface");
		} else if ((soClass.getModifiers() & Modifier.ABSTRACT) != 0) {
			throw new IllegalArgumentException("soClass in StructDefine("
					+ name + ") can't be abstract");
		} else if ((soClass.getModifiers() & DataTypeBase.class_modifier_ENUM) != 0) {
			if (soClass != None.class) {
				throw new IllegalArgumentException("in StructDefine(" + name
						+ ") soClass can't be enum");
			}
		} else if (soClass.isArray()) {
			throw new IllegalArgumentException("in StructDefine(" + name
					+ ") soClass can't be array");
		} else if (soClass.isPrimitive()) {
			throw new IllegalArgumentException("in StructDefine(" + name
					+ ") soClass can't be primitive");
		}
		this.soClass = soClass;
		this.isDynObj = DynObj.class.isAssignableFrom(soClass);
	}

	/**
	 * ������
	 */
	private volatile String typeName;

	/**
	 * �����������������ǰ׺
	 */
	abstract String structTypeNamePrefix();

	final String getTypeName() {
		String typeName = this.typeName;
		if (typeName == null) {
			synchronized (this) {
				typeName = this.typeName;
				if (typeName == null) {
					this.typeName = typeName = this.structTypeNamePrefix()
							.concat(this.name);
				}
			}
		}
		return typeName;
	}

	/**
	 * ���so���Ƿ���Ч�������׳��쳣
	 * 
	 * @param so
	 *            �ṹʵ������
	 */
	final DynObj checkSO(Object so) throws IllegalArgumentException {
		if (so.getClass() != this.soClass) {
			throw illegalSO();
		}
		if (this.isDynObj) {
			return this.toDynObj(so);
		}
		return null;
	}

	/**
	 * ���������Ч��
	 * 
	 * @param dynObj
	 * @throws IllegalArgumentException
	 */
	final void checkSO(DynObj dynObj) throws IllegalArgumentException {
		if (dynObj.getClass() != this.soClass) {
			throw illegalSO();
		}
		if (dynObj.define == null) {
			dynObj.define = this;
			if (this.binSize > 0) {
				dynObj.bin = new byte[this.binSize];
			}
			if (this.refCount > 0) {
				dynObj.objs = new Object[this.refCount];
			}
		} else if (dynObj.define != this) {
			throw illegalSO();
		}
	}

	/**
	 * ׼����̬���󣬶����������Ч��
	 */
	final void prepareSONoCheck(DynObj dynObj) {
		if (dynObj.define == null) {
			dynObj.define = this;
			if (this.binSize > 0) {
				dynObj.bin = new byte[this.binSize];
			}
			if (this.refCount > 0) {
				dynObj.objs = new Object[this.refCount];
			}
		}
	}

	/**
	 * ͨ�����ƺ�����ȷ�������ֶλ��������ֶ�
	 */
	public final StructFieldDefineImpl newField(String name, DataType type) {
		this.checkModifiable();
		StructFieldDefineImpl field = this.fields.find(name);
		if (field == null) {
			if (!this.isDynObj) {
				throw new UnsupportedOperationException(
						"ʵ������Ƕ�̬�����޷���չ��̬�ֶΣ����DynamicObject�̳�");
			}
			field = new StructFieldDefineImpl(this, name, type);
			this.fields.add(field);
		} else if (!field.typeCompatible(type)) {
			throw new IllegalArgumentException("�����ֶ�(" + name + ")����(" + type
					+ ")���Ѿ����ڵ��ֶ�����(" + field.type + ")��ͻ");
		}
		this.serialVUID = null;
		return field;
	}

	public final StructFieldDefineImpl newField(FieldDefine sample) {
		return this.newField(sample.getName(), sample.getType());
	}

	public final StructFieldDefineImpl newField(String name, DataTypable typable) {
		return this.newField(name, typable.getType());
	}

	/**
	 * ����ֶη�����Ϣ
	 */
	final void clearAccessInfo() {
		this.fieldArray = emptyFieldArray;
	}

	/**
	 * �����ֶ����飨���ڻ�������Ч�ʣ����������ֶη�����
	 */
	final void prepareAccessInfo() {
		this.fieldArray = this.fields
				.toArray(new StructFieldDefineImpl[this.fields.size()]);
		for (int i = 0; i < this.fieldArray.length; i++) {
			this.fieldArray[i].loadAccessorAndDefaultValue(i);
		}
		Arrays.sort(this.fieldArray, StructFieldDefineImpl.FIELD_NAME_CMP);
		if (this.isDynObj) {
			this.binSize = 0;
			this.refCount = 0;
			int nullOffset = 0;
			for (StructFieldDefineImpl field : this.fieldArray) {
				nullOffset = field.initDynObjOffsets(nullOffset);
			}
			this.nullByteOffset = this.binSize;
			if (nullOffset >= DynObj.null_mask_bits) {// ��������nullmask�Ŀռ�
				this.binSize += nullOffset - DynObj.null_mask_bits + 8 >> 3;
			}
		} else {
			for (StructFieldDefineImpl field : this.fieldArray) {
				field.initObjNullOffset();
			}
		}
	}

	/**
	 * ����̬����Ϊ��ʹ��̬����Խṹ�����������Ч��ʹ�䶯̬�洢�ṹ��Ч
	 * 
	 * @param dynObj
	 */
	final DynObj toDynObj(Object so) {
		DynObj dynObj = (DynObj) so;
		if (dynObj.define == null) {
			dynObj.define = this;
			if (this.binSize > 0) {
				dynObj.bin = new byte[this.binSize];
			}
			if (this.refCount > 0) {
				dynObj.objs = new Object[this.refCount];
			}
		} else if (dynObj.define != this) {
			throw illegalSO();
		}
		return dynObj;
	}

	/**
	 * �����յ�SO�����������Ϊ��̬���󣬸÷��������𣬳�ʼ����̬����Ĵ洢�ṹ
	 * 
	 * @return
	 */
	private final Object allocateSOInstance() {
		if (this.soClass == None.class) {
			return None.NONE;
		}
		try {
			return Unsf.unsafe.allocateInstance(this.soClass);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	/**
	 * �����յ�SO����
	 * 
	 * @return
	 */
	final Object newEmptySO() {
		Object so = this.allocateSOInstance();
		if (this.isDynObj) {
			this.toDynObj(so);
		}
		return so;
	}

	final DynObj newEmptyDynSO() {
		if (!this.isDynObj) {
			throw new UnsupportedOperationException();
		}
		return this.toDynObj(this.allocateSOInstance());
	}

	/**
	 * ��������Ĭ��ֵ���ó�ʼ���õ�SO����
	 */
	final Object newInitedSO() {
		Object so = this.allocateSOInstance();
		if (this.fieldArray.length > 0) {
			if (this.isDynObj) {
				DynObj dynSO = this.toDynObj(so);
				for (StructFieldDefineImpl field : this.fieldArray) {
					if (field.hasDefaultReadableValue()) {
						field.loadDefaultNoCheck(dynSO);
					}
				}
			} else {
				for (StructFieldDefineImpl field : this.fieldArray) {
					if (field.hasDefaultReadableValue()) {
						field.loadDefaultNoCheck(so);
					}
				}
			}
		}
		return so;
	}

	/**
	 * 
	 * ���ƶ��󣬵�Ŀ�����Ϊnullʱ��¡Դ����
	 * 
	 * @param src
	 *            Դ����
	 * @param src
	 *            Ŀ�����
	 * @return ����Ŀ�����
	 */
	public final Object assignNoCheckSrc(Object src, Object dest,
			OBJAContext objaContext) {
		if (this.fieldArray.length > 0) {
			if (dest == null || dest.getClass() != this.soClass) {
				dest = this.allocateSOInstance();
			}
			for (StructFieldDefineImpl field : this.fieldArray) {
				field.assignNoCheck(src, dest, objaContext);
			}
			return dest;
		} else {
			return src;
		}
	}

	/**
	 * 
	 * ���ƶ��󣬵�Ŀ�����Ϊnullʱ��¡Դ����
	 * 
	 * @param src
	 *            Դ����
	 * @param src
	 *            Ŀ�����
	 * @return ����Ŀ�����
	 */
	public final Object assignNoCheckSrcD(DynObj dynSrc, Object dest,
			OBJAContext objaContext) {
		if (this.fieldArray.length > 0) {
			if (dest == null || dest.getClass() != this.soClass) {
				dest = this.allocateSOInstance();
			}
			DynObj dynDest = this.toDynObj(dest);
			for (StructFieldDefineImpl field : this.fieldArray) {
				field.assignNoCheck(dynSrc, dynDest, objaContext);
			}
			return dest;
		} else {
			return dynSrc;
		}
	}

	/**
	 * �����Ƿ�֧�ֿ���
	 */
	public final boolean supportCopy() {
		return this.fieldArray.length > 0;
	}

	final void cloneFieldsTo(StructDefineImpl target) {
		for (int i = 0, c = this.fields.size(); i < c; i++) {
			StructFieldDefineImpl f = this.fields.get(i);
			target.newField(f);
		}
	}

	// /////////////////////////////////////////////
	// ///// Ψһ��ʶ��Ϣ
	// ////////////////////////////////////////////
	/* serial version unique identifier */
	// REMIND if any changes to StructDefine, serialVUID will be set to null.
	private volatile byte[] serialVUID;

	private static MessageDigest newMD5() {
		try {
			return MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new UnsupportedOperationException(e);
		}
	}

	private static final byte[] ZERO = { 0, 0, 0, 0 };

	/** big-endian */
	// REMIND if i == 0 then a will not be modified, and the returned array
	// should not be modified by the caller.
	private static byte[] toBytes(int i, byte[] a) {
		if (i == 0) {
			return ZERO;
		}
		a[0] = (byte) (i >>> 24);
		a[1] = (byte) (i >>> 16);
		a[2] = (byte) (i >>> 8);
		a[3] = (byte) i;
		return a;
	}

	private void rebuildSerialVUID() {
		synchronized (this) {
			if (this.serialVUID != null) {
				return;
			}
			MessageDigest MD5 = newMD5();
			byte[] buf = new byte[4];
			// name, 4 bytes
			MD5.update(toBytes(this.name.hashCode(), buf));
			// isDynObj, 1 byte
			MD5.update((byte) (this.isDynObj ? 1 : 0));
			// binSize, 4 bytes
			MD5.update(toBytes(this.binSize, buf));
			// refCount, 4 bytes
			MD5.update(toBytes(this.refCount, buf));
			final int len = this.fields.size();
			// fields' count, 4 bytes
			MD5.update(toBytes(len, buf));
			StructFieldDefineImpl f;
			for (int i = 0; i < len; i++) {
				f = this.fields.get(i);
				// name, 4 bytes
				MD5.update(toBytes(f.name.hashCode(), buf));
				// data type, 4 bytes
				MD5.update(toBytes(f.type.hashCode(), buf));
				// is java field, 1 byte
				MD5.update((byte) (f.isJavaField ? 1 : 0));
				// is state field, 1 byte
				MD5.update((byte) (f.isStateField() ? 1 : 0));
				// is keep valid, 1 byte
				MD5.update((byte) (f.isKeepValid ? 1 : 0));
				// is read only, 1 byte
				MD5.update((byte) (f.isReadonly ? 1 : 0));
			}
			this.serialVUID = MD5.digest();
		}
	}

	/**
	 * ���ظö����Ψһ��ʶ��Ϣ��������Ϊֻ������ʹ�á�
	 */
	final byte[] getSerialVUID() {
		if (this.serialVUID == null) {
			this.rebuildSerialVUID();
		}
		return this.serialVUID;
	}

	// ///////////////////////
	// /// �ӿ�ʵ��
	// //////////////////////
	public int getTupleElementCount() {
		return this.fields.size();
	}

	public Typable getTupleElementType(int index) {
		return this.fields.get(index);
	}

	public <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> caller, TUserData userData) {
		try {
			return caller.inStruct(userData, this);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	public NamedDefineContainerImpl<? extends StructFieldDefineImpl> getFields() {
		return this.fields;
	}

	// ///////////////////////////
	// //// XML
	// //////////////////////////
	static final String xml_element_fields = "fields";

	@Override
	public void render(SXElement element) {
		super.render(element);
		this.fields.renderInto(element, xml_element_fields, 0);
	}

	final void mergeFields(SXElement fieldsElement, SXMergeHelper helper,
			String fieldTag) {
		if (fieldsElement == null) {
			return;
		}
		for (SXElement fieldElement = fieldsElement.firstChild(fieldTag); fieldElement != null; fieldElement = fieldElement
				.nextSibling(fieldTag)) {
			String fn = fieldElement.getAttribute(
					NamedDefineImpl.xml_attr_name, null);
			StructFieldDefineImpl field = this.fields.find(fn);
			if (field == null) {
				field = new StructFieldDefineImpl(this, fn, fieldElement
						.getAsType(FieldDefineImpl.xml_attr_type,
								helper.querier));
				this.fields.add(field);
			}
			field.merge(fieldElement, helper);
		}
	}

	@Override
	public String getXMLTagName() {
		throw new UnsupportedOperationException();
	}

	@Override
	void merge(SXElement element, SXMergeHelper helper) {
		super.merge(element, helper);
		this.mergeFields(element.firstChild(xml_element_fields), helper,
				StructFieldDefineImpl.xml_element_field);
	}

	// ////////////////////////////////////////////////////
	// Serialization

	public boolean supportSerialization() {
		return false;
	}

	public void writeObjectData(InternalSerializer serializer, Object obj) {
		throw new UnsupportedOperationException("incomplete");
	}

	public Object readObjectData(InternalDeserializer deserializer)
			throws IOException, StructDefineNotFoundException {
		throw new UnsupportedOperationException("incomplete");
	}

	// //////////////////////////////////////////
	// / NEW IO Serialization

	private StructFieldDefineImpl firstNIOSerializableField = StructFieldDefineImpl.tag_field;

	public final boolean isNIOSerializable() {
		return true;
	}

	/**
	 * ����
	 */
	private GUID typeID;

	public final GUID getID() {
		GUID id = this.typeID;
		if (id != null) {
			return id;
		}
		this.getFirstNIOSerializableField();
		return this.typeID;
	}

	public final StructFieldDefineImpl getFirstNIOSerializableField() {
		if (this.firstNIOSerializableField == StructFieldDefineImpl.tag_field) {
			final StructFieldDefineImpl[] nsFields = new StructFieldDefineImpl[this.fieldArray.length];
			final StringBuilder serialIDDigest = new StringBuilder();
			int size = 0;
			serialIDDigest.append(this.name).append(':');
			serialIDDigest.append(this.soClass.getName()).append('{');
			for (StructFieldDefineImpl field : this.fieldArray) {
				if (field.isStateField()) {
					nsFields[size++] = field;
					serialIDDigest.append(field.name).append(':');
					if (field.type == this) {
						GUID.emptyID.appendTo(serialIDDigest);
					} else {
						field.type.getID().appendTo(serialIDDigest);
					}
					serialIDDigest.append(';');
				}
			}
			serialIDDigest.append('}');
			this.typeID = DataTypeBase.calcNativeTypeID(serialIDDigest);
			if (size > 0) {
				Arrays.sort(nsFields, 0, size,
						StructFieldDefineImpl.FIELD_NIO_SRLZ_CMP);
				StructFieldDefineImpl last = this.firstNIOSerializableField = nsFields[0];
				for (int i = 1; i < size; i++) {
					final StructFieldDefineImpl field = nsFields[i];
					last.nextNIOSerializableField = field;
					last = field;
				}
			} else {
				this.firstNIOSerializableField = null;
			}
		}
		return this.firstNIOSerializableField;
	}

	public final boolean nioSerializeData(final NSerializer serializer,
			final Object object) {
		return serializer.writeStructData(object, this, false);
	}

	public DataObjectTranslator<?, ?> getDataObjectTranslator() {
		throw new UnsupportedOperationException();
	}

	public DataObjectTranslator<?, ?> registerDataObjectTranslator(
			final DataObjectTranslator<?, ?> serializer) {
		throw new UnsupportedOperationException();
	}

}
