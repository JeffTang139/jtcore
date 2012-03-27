package org.eclipse.jt.core.def.query.sp;

import org.eclipse.jt.core.def.exp.ConditionalExpression;
import org.eclipse.jt.core.def.query.SelectDeclare;

public interface CursorLoopSql {

	SelectDeclare getSelect();

	PrgmSqlSegment getLoopSegment();

	ConditionalExpression getCurrentPosition();

}
