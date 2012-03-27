package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.type.DataType;

/**
 * ¼üÖµ»º´æ¶¨Òå
 * 
 * @author Jeff Tang
 * 
 */
final class RPTRecordSetKeyValuesCacheDefine extends StructDefineImpl {

	static class RPTKeyValuesCache extends DynObj {

	}

	RPTRecordSetKeyValuesCacheDefine() {
		super("rpt-kvc", RPTKeyValuesCache.class);
	}

	@Override
	String structTypeNamePrefix() {
		throw new UnsupportedOperationException();
	}

	final void reset() {
		this.fields.clear();
		this.clearAccessInfo();
	}

	final StructFieldDefineImpl newField(DataType type) {
		return super.newField(Integer.toString(this.fields.size()), type);
	}

	final RPTKeyValuesCache newKeyValuesCache() {
		RPTKeyValuesCache keyValues = new RPTKeyValuesCache();
		this.prepareSONoCheck(keyValues);
		return keyValues;
	}
}