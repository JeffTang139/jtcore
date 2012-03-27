/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ClassType.java
 * Date Apr 27, 2009
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.type.AssignCapability;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.Digester;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.Undigester;


/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public final class ClassType extends ObjectDataTypeBase {

	public static final ClassType TYPE = new ClassType();

	@Override
	protected final GUID calcTypeID() {
		return calcNativeTypeID(ENTRY_TYPE_CLASS);
	}

	private ClassType() {
		super(Class.class);
	}

	@Override
	public final AssignCapability isAssignableFrom(DataType another) {
		if (another == null) {
			throw new NullArgumentException("¿‡–Õ");
		}
		if (another == this) {
			return AssignCapability.SAME;
		}
		return AssignCapability.NO;
	}

	public final DataType calcPrecedence(DataType target) {
		throw new UnsupportedOperationException();
	}

	public void digestType(Digester digester) {
		digester.update(TypeCodeSet.CLASS);
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(
				TypeCodeSet.CLASS) {
			@Override
			protected DataType doUndigest(Undigester undigester) {
				return TYPE;
			}
		});
	}

	// //////////////////////////////////////////
	// / NEW IO Serialization
	// //////////////////////////////////////////

	@Override
	public final boolean nioSerializeData(final NSerializer serializer,
			final Object object) {
		return serializer.writeClassData((Class<?>) object);
	}

}
