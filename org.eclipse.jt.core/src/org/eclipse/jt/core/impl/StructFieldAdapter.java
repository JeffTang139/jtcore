/**
 * Copyright (C) 2007-2008 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File StructFieldAdapter.java
 * Date 2008-12-3
 */
package org.eclipse.jt.core.impl;

import java.io.IOException;

import org.eclipse.jt.core.type.DataType;


/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
// TODO 注释
final class StructFieldAdapter {
	final String name;
	final DataType type; // XXX 处理类型兼容问题
	final boolean isStateField;
	final boolean isReadOnly;
	final boolean isKeepValid;

	final StructFieldDefineImpl field;

	StructFieldAdapter(StructFieldDefineImpl field, String name, DataType type,
	        boolean isStateField, boolean isReadOnly, boolean isKeepValid) {
		if (name == null || type == null) {
			throw new NullPointerException();
		}
		this.field = field;
		this.name = name;
		this.type = type;
		this.isStateField = isStateField;
		this.isReadOnly = isReadOnly;
		this.isKeepValid = isKeepValid;
	}

	final void readValueFor(Object obj, InternalDeserializer reader)
	        throws IOException, StructDefineNotFoundException {
		if (this.field == null) {
			DataTypeHelper.skipData(reader, this.type);
		} else {
			this.field.readIn(obj, reader);
		}
	}
}
