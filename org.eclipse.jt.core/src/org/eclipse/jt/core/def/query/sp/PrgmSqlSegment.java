package org.eclipse.jt.core.def.query.sp;

import java.util.List;

import org.eclipse.jt.core.def.exp.ConditionalExpression;
import org.eclipse.jt.core.def.exp.ValueExpression;
import org.eclipse.jt.core.def.query.SelectDeclare;
import org.eclipse.jt.core.type.DataType;


public interface PrgmSqlSegment extends PrgmSql {

	PrgmSqlSegment appendSegment();

	VariableDeclaration declareVar(DataType type);

	VariableDeclaration declareVar(DataType type, ValueExpression value);

	VariableDeclaration declareVar(String varName, DataType type);

	VariableDeclaration declareVar(String varName, DataType type,
			ValueExpression value);

	void assignVar(VariableDeclaration var, ValueExpression value);

	SelectDeclare assignVar(VariableDeclaration var);

	SelectDeclare assignVar(VariableDeclaration var,
			VariableDeclaration... vars);

	SelectDeclare assignVar(List<VariableDeclaration> vars);

	IfSql appendIf();

	IfSql appendIf(ConditionalExpression condition);

	PrgmSqlSegment appendWhileLoop(ConditionalExpression condition);

	CursorLoopSql appendCursorLoop();

	void appendBreak();

	void appendPrint(ValueExpression value);

	InsertDeclare appendInsert();

	DeleteDeclare appendDelete();

	UpdateDeclare appendUpdate();

}
