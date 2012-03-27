package org.eclipse.jt.core.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.eclipse.jt.core.type.AssignCapability;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.Digester;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.ResourceType;
import org.eclipse.jt.core.type.Type;
import org.eclipse.jt.core.type.TypeDetector;
import org.eclipse.jt.core.type.Undigester;


@Deprecated
public final class ResourceTypeImpl<TFacade> extends DataTypeBase implements
		ResourceType<TFacade> {
	final Class<TFacade> facadeClass;
	final Object category;
	private ResourceTypeImpl<TFacade> next;

	@Override
	protected final GUID calcTypeID() {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	private ResourceTypeImpl(Class<TFacade> facadeClass, Object category) {
		super(null);
		if (facadeClass == null) {
			throw new NullPointerException();
		}
		this.facadeClass = facadeClass;
		this.category = category; // accepts null value
	}

	@Override
	public final <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> caller, TUserData userData) {
		try {
			return caller.inResource(userData, this.facadeClass, this.category);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final Object getCategory() {
		return this.category;
	}

	public final Class<TFacade> getFacadeClass() {
		return this.facadeClass;
	}

	public final AssignCapability isAssignableFrom(DataType another) {
		return another == this ? AssignCapability.SAME : AssignCapability.NO;
	}

	public final DataType calcPrecedence(DataType target) {
		throw new UnsupportedOperationException();
	}

	public final boolean accept(DataType type) {
		return false;
	}

	final static byte category_type_TYPE = 1;
	final static byte category_type_UnKnown = 0;

	public final void digestType(Digester digester) {
		digester.update(TypeCodeSet.RESOURCE_H);
		digester.update(this.facadeClass);
		if (this.category instanceof Type) {
			digester.update(category_type_TYPE);
			((Type) this.category).digestType(digester);
		} else {
			digester.update(category_type_UnKnown);
		}
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(
				TypeCodeSet.RESOURCE_H) {
			@SuppressWarnings("unchecked")
			@Override
			protected DataType doUndigest(Undigester undigester)
					throws IOException, StructDefineNotFoundException {
				Class facade = undigester.extractClass();
				Object category = null;
				byte ct = undigester.extractByte();
				if (category_type_TYPE == ct) {
					category = DataTypeHelper.undigestType(undigester);
				}
				return RESOURCE(facade, category);
			}
		});
	}

	@Deprecated
	@SuppressWarnings("unchecked")
	public static <TFacade> ResourceTypeImpl<TFacade> RESOURCE(
			Class<TFacade> facadeClass, Object category) {
		if (facadeClass == null || category == null) {
			throw new NullPointerException();
		}
		int mv;
		ResourceTypeImpl last = null;
		resourceTypeMapRL.lock();
		try {
			mv = modifyVersion;
			for (ResourceTypeImpl type = resourceTypes.get(facadeClass); type != null; last = type, type = type.next) {
				if (type.category == category) {
					return type;
				}
			}
		} finally {
			resourceTypeMapRL.unlock();
		}
		resourceTypeMapWL.lock();
		try {
			if (modifyVersion != mv) {
				for (ResourceTypeImpl type = last != null ? last
						: resourceTypes.get(facadeClass); type != null; type = type.next) {
					if (type.category == category) {
						return type;
					}
				}
			}
			ResourceTypeImpl type = new ResourceTypeImpl<TFacade>(facadeClass,
					category);
			if (last != null) {
				last.next = type;
			} else {
				resourceTypes.put(facadeClass, type);
			}
			modifyVersion++;
			return type;
		} finally {
			resourceTypeMapWL.unlock();
		}
	}

	private static final Lock resourceTypeMapRL;
	private static final Lock resourceTypeMapWL;
	private static volatile int modifyVersion;
	private final static Map<Class<?>, ResourceTypeImpl<?>> resourceTypes = new HashMap<Class<?>, ResourceTypeImpl<?>>();
	static {
		ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
		resourceTypeMapRL = rwl.readLock();
		resourceTypeMapWL = rwl.writeLock();
	}

}
