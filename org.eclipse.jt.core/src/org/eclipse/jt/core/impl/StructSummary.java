/**
 * Copyright (C) 2007-2008 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File StructSummary.java
 * Date 2008-12-3
 */
package org.eclipse.jt.core.impl;

import java.util.Arrays;

import org.eclipse.jt.core.def.obja.StructClass;


/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
@StructClass
final class StructSummary {
	final String defineName;
	final byte[] serialVUID;
	final boolean isDynamic;

	private StructDefineImpl structDefine;

	StructSummary(String defineName, byte[] serialVUID, boolean isDynamic) {
		if (defineName == null || serialVUID == null) {
			throw new NullPointerException();
		}
		this.defineName = defineName;
		this.serialVUID = serialVUID;
		this.isDynamic = isDynamic;
	}

	StructSummary(StructDefineImpl structDefine) {
		this(structDefine.name, structDefine.getSerialVUID(),
		        structDefine.isDynObj);
		this.structDefine = structDefine;
	}

	static final StructSummary get(StructDefineImpl structDefine) {
		return new StructSummary(structDefine);
	}

	StructDefineImpl getStructDefine() {
		return this.structDefine;
	}

	@Override
	public int hashCode() {
		return this.defineName.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof StructSummary)) {
			return false;
		}
		StructSummary ss = (StructSummary) obj;
		if (!this.defineName.equals(ss.defineName)) {
			return false;
		}
		if (!Arrays.equals(this.serialVUID, ss.serialVUID)) {
			return false;
		}
		if (this.isDynamic != ss.isDynamic) {
			return false;
		}
		return true;
	}

	boolean equalNames(StructSummary sum) {
		if (this == sum) {
			return true;
		}
		if (sum == null) {
			return false;
		}
		return this.defineName.equals(sum.defineName);
	}

	boolean equalVUIDs(StructSummary sum) {
		if (this == sum) {
			return true;
		}
		if (sum == null) {
			return false;
		}
		return Arrays.equals(this.serialVUID, sum.serialVUID);
	}

	boolean equalNameAndVUIDs(StructSummary sum) {
		if (this == sum) {
			return true;
		}
		if (sum == null) {
			return false;
		}
		return this.defineName.equals(sum.defineName)
		        && Arrays.equals(this.serialVUID, sum.serialVUID);
	}
}
