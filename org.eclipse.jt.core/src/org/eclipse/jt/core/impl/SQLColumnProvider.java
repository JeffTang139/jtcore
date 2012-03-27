package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.exp.ValueExpression;
import org.eclipse.jt.core.def.query.RelationColumnDefine;

interface SQLColumnProvider {
	public RelationColumnDefine findColumn(String name);

	public ValueExpression expOf(RelationColumnDefine c);
}
