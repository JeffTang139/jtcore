/**
 * Copyright (C) 2007-2008 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File StructAdapter.java
 * Date 2008-12-3
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.type.DataType;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
// TODO ×¢ÊÍ
final class StructAdapter {

	final StructSummary remoteStructSummary;
	final StructDefineImpl localDefine;
	final NamedDefineContainerImpl<? extends StructFieldDefineImpl> localFields;

	final StructFieldAdapter[] fields;
	int pos = 0;

	StructAdapter next;

	StructAdapter(StructSummary remoteStructSummary, short remoteFieldCount,
	        StructDefineImpl localDefine) {
		if (remoteStructSummary == null || localDefine == null) {
			throw new NullPointerException();
		}
		if (remoteFieldCount < 0) {
			throw new IllegalArgumentException();
		}
		this.remoteStructSummary = remoteStructSummary;
		this.localDefine = localDefine;
		this.localFields = localDefine.fields;
		this.fields = new StructFieldAdapter[remoteFieldCount];
	}

	final void addNewFieldAdapter(String name, boolean isStateField,
	        boolean isReadOnly, boolean isKeepValid, DataType type) {
		this.fields[this.pos] = new StructFieldAdapter(this.localFields
		        .find(name), name, type, isStateField, isReadOnly, isKeepValid);
		this.pos++;
	}

	final Object newEmptySO() {
		return this.localDefine.newEmptySO();
	}
}
