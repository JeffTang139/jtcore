package org.eclipse.jt.core.def.query.sp;

import org.eclipse.jt.core.def.exp.ConditionalExpression;

public interface IfSql extends PrgmSql {

	void setCondition(ConditionalExpression condition);

	PrgmSqlSegment getThenSegement();

	PrgmSqlSegment getElseSegment();
}
