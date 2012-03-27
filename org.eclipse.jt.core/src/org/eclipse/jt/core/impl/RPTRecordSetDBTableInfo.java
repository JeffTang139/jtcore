/**
 * 
 */
package org.eclipse.jt.core.impl;

import java.util.ArrayList;

final class RPTRecordSetDBTableInfo extends ArrayList<RPTRecordSetFieldImpl> {

	private static final long serialVersionUID = 1L;
	final DBTableDefineImpl dbTable;

	RPTRecordSetDBTableInfo(DBTableDefineImpl dbTable) {
		this.dbTable = dbTable;
	}

}