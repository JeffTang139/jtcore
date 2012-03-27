package org.eclipse.jt.core.impl;

import java.io.IOException;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.misc.TypeArgFinder;
import org.eclipse.jt.core.serial.DataObjectTranslator;
import org.eclipse.jt.core.type.AssignCapability;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.ObjectDataType;
import org.eclipse.jt.core.type.TypeDetector;


/**
 * 对象类型基类
 * 
 * @author Jeff Tang
 * 
 */
public abstract class ObjectDataTypeBase extends DataTypeBase implements
		ObjectDataTypeInternal {

	@SuppressWarnings("unchecked")
	private DataObjectTranslator translator;

	public final DataObjectTranslator<?, ?> getDataObjectTranslator() {
		return this.translator;
	}

	public final DataObjectTranslator<?, ?> registerDataObjectTranslator(
			final DataObjectTranslator<?, ?> serializer) {
		final DataObjectTranslator<?, ?> old = this.translator;
		this.translator = serializer;
		return old;
	}

	ObjectDataTypeBase(Class<?> javaClass) {
		super(javaClass);
	}

	public boolean isInstance(Object obj) {
		return this.javaClass.isInstance(obj);
	}

	public final Object assignNoCheckSrcD(DynObj dynSrc, Object dest,
			OBJAContext objaContext) {
		throw new UnsupportedOperationException("无效的调用");
	}

	@SuppressWarnings("unchecked")
	public Object assignNoCheckSrc(Object src, Object dest,
			OBJAContext objaContext) {
		final DataObjectTranslator translator = this.translator;
		if (translator != null && translator.supportAssign()) {
			Object delegate = translator.toDelegateObject(src);
			final Object delegateDest;
			if (dest != null && this.javaClass.isInstance(dest)) {
				delegateDest = translator.toDelegateObject(dest);
			} else {
				delegateDest = null;
			}
			delegate = ((ObjectDataTypeInternal) DataTypeBase
					.dataTypeOfJavaObj(delegate)).assignNoCheckSrc(delegate,
					delegateDest, objaContext);
			return translator.recoverObject(dest, delegate, null, translator
					.getVersion());
		} else {
			return src;
		}
	}

	public AssignCapability isAssignableFrom(DataType another) {
		if (another == null) {
			throw new NullArgumentException("类型");
		}
		if (another instanceof ObjectDataType) {
			Class<?> anotherJavaClass = ((ObjectDataType) another)
					.getJavaClass();
			if (anotherJavaClass == this.javaClass) {
				return AssignCapability.SAME;
			}
			if (this.javaClass.isAssignableFrom(anotherJavaClass)) {
				return AssignCapability.IMPLICIT;
			}
			if (anotherJavaClass.isAssignableFrom(this.javaClass)) {
				return AssignCapability.EXPLICIT;
			}
		}
		return AssignCapability.NO;
	}

	@Override
	public <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> caller, TUserData userData) {
		try {
			return caller.inObject(userData, this);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	// ///////////////////////////////////////
	// Serialization

	public boolean supportSerialization() {
		return false;
	}

	public void writeObjectData(InternalSerializer serializer, Object obj)
			throws IOException, StructDefineNotFoundException {
		throw new UnsupportedOperationException("incomplete");
	}

	public Object readObjectData(InternalDeserializer deserializer)
			throws IOException, StructDefineNotFoundException {
		throw new UnsupportedOperationException("incomplete");
	}

	static final String xml_element_types = "types";
	static final String xml_element_type = "type";
	static final String xml_attr_class = "class";
	static final String xml_attr_struct = "struct";
	static final String xml_value_struct_try = "try";
	static final String xml_value_struct_reject = "false";
	static final String xml_value_struct_strict = "true";
	static final String xml_value_struct_force = "force";
	static final String xml_attr_translator = "translator";

	@SuppressWarnings("unchecked")
	final static void loadCustomType(BundleStub bundle, SXElement typeE)
			throws Throwable {
		final Class<?> customClass = bundle.loadClass(typeE
				.getAttribute(xml_attr_class), null);
		if (customClass.isPrimitive()) {
			throw new UnsupportedOperationException("不支持自定义原始类型：" + customClass);
		}
		final String translatorClassName = typeE
				.getAttribute(xml_attr_translator);
		final DataObjectTranslator<?, ?> translator;
		if (translatorClassName != null && translatorClassName.length() != 0) {
			final Class<? extends DataObjectTranslator> translatorClass = bundle
					.loadClass(typeE.getAttribute(xml_attr_translator),
							DataObjectTranslator.class);
			final Class<?> sourceClass = TypeArgFinder.find(translatorClass,
					DataObjectTranslator.class, 0);
			if (sourceClass != customClass) {
				throw new UnsupportedOperationException("数据对象转换器类型不符：对象类型：\""
						+ customClass + "\"，转换器类型\"" + translatorClass + "<"
						+ sourceClass + ",?>\"");
			}
			try {

				translator = Utils.publicAccessibleObject(
						translatorClass.getDeclaredConstructor()).newInstance();
			} catch (ExceptionInInitializerError e) {
				throw e.getException();
			}
		} else {
			translator = null;
		}
		final ObjectDataTypeInternal odti;
		final DataType dt;
		final String struct = typeE.getAttribute(xml_attr_struct, null);
		if (xml_value_struct_strict.equals(struct)) {
			dt = odti = getStaticStructDefine(customClass, false);
		} else if (xml_value_struct_force.equals(struct)) {
			dt = odti = getStaticStructDefine(customClass, true);
		} else if (xml_value_struct_reject.equals(struct)) {
			dt = dataTypeOfJavaClass(customClass, true);
			if (dt instanceof ObjectDataTypeInternal) {
				odti = (ObjectDataTypeInternal) dt;
			} else {
				odti = null;
			}
		} else {
			dt = dataTypeOfJavaClass(customClass, false);
			if (dt instanceof ObjectDataTypeInternal) {
				odti = (ObjectDataTypeInternal) dt;
			} else {
				odti = null;
			}
		}
		if (translator != null) {
			if (odti == null) {
				throw new UnsupportedOperationException("类型\"" + dt
						+ "\"不支持自定义转换器");
			}
			final DataObjectTranslator<?, ?> old = odti
					.registerDataObjectTranslator(translator);
			if (old != null) {
				if (old.getClass() != translator.getClass()) {
					System.err.println("自定义对象类型警告：类型\"" + odti + "\"将自定义转换器\""
							+ old + "\"替换为\"" + translator + "\"");
				} else {
					System.err.println("自定义对象类型警告：类型\"" + odti
							+ "\"重复设定自定义转换器\"");
				}
			}
		}
	}

	// //////////////////////////////////////////
	// / NEW IO Serialization
	// //////////////////////////////////////////

	public boolean nioSerializeData(final NSerializer serializer,
			final Object object) {
		if (this.translator != null) {
			return serializer.writeCustomSerializeDataObject(object, this,
					this.translator);
		} else {
			return serializer.writeUnserializable();
		}
	}

}
