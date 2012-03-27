package org.eclipse.jt.core.impl;

/**
 * AST访问器接口
 * 
 * @author Jeff Tang
 * 
 */
public interface SQLVisitor<T> {
	// 变量和字面量
	public void visitParamDeclare(T visitorContext, NParamDeclare v);

	public void visitLiteralBoolean(T visitorContext, NLiteralBoolean b);

	public void visitLiteralInt(T visitorContext, NLiteralInt i);

	public void visitLiteralLong(T visitorContext, NLiteralLong l);

	public void visitLiteralDouble(T visitorContext, NLiteralDouble d);

	public void visitLiteralString(T visitorContext, NLiteralString s);

	public void visitLiteralDate(T visitorContext, NLiteralDate d);

	public void visitLiteralBytes(T visitorContext, NLiteralBytes b);

	public void visitLiteralGUID(T visitorContext, NLiteralGUID g);

	// 值表达式
	public void visitBinaryExpr(T visitorContext, NBinaryExpr e);

	public void visitAggregateExpr(T visitorContext, NAggregateExpr e);

	public void visitCoalesceExpr(T visitorContext, NCoalesceExpr e);

	public void visitColumnRefExpr(T visitorContext, NColumnRefExpr e);

	public void visitFunctionExpr(T visitorContext, NFunctionExpr e);

	public void visitHaidExpr(T visitorContext, NHaidExpr e);

	public void visitHlvExpr(T visitorContext, NHlvExpr e);

	public void visitNegativeExpr(T visitorContext, NNegativeExpr e);

	public void visitNullExpr(T visitorContext, NNullExpr e);

	public void visitSearchedCaseExpr(T visitorContext, NSearchedCaseExpr e);

	public void visitSimpleCaseExpr(T visitorContext, NSimpleCaseExpr e);

	public void visitVarRefExpr(T visitorContext, NVarRefExpr e);

	// 条件表达式
	public void visitCompareExpr(T visitorContext, NCompareExpr e);

	public void visitBetweenExpr(T visitorContext, NBetweenExpr e);

	public void visitExistsExpr(T visitorContext, NExistsExpr e);

	public void visitHierarchyExpr(T visitorContext, NHierarchyExpr e);

	public void visitInExpr(T visitorContext, NInExpr e);

	public void visitIsLeafExpr(T visitorContext, NIsLeafExpr e);

	public void visitIsNullExpr(T visitorContext, NIsNullExpr e);

	public void visitLogicalExpr(T visitorContext, NLogicalExpr e);

	public void visitPathExpr(T visitorContext, NPathExpr e);

	public void visitStrCompareExpr(T visitorContext, NStrCompareExpr e);

	// SOURCE
	public void visitSourceTable(T visitorContext, NSourceTable t);

	public void visitSourceJoin(T visitorContext, NSourceJoin j);

	public void visitSourceRelate(T visitorContext, NSourceRelate r);

	public void visitSourceSubQuery(T visitorContext, NSourceSubQuery q);

	// 查询
	public void visitQueryDeclare(T visitorContext, NQueryDeclare q);

	public void visitQueryStmt(T visitorContext, NQueryStmt q);

	public void visitQuerySpecific(T visitorContext, NQuerySpecific s);

	// 新增
	public void visitInsertDeclare(T visitorContext, NInsertDeclare i);

	public void visitInsertStmt(T visitorContext, NInsertStmt i);

	public void visitInsertValues(T visitorContext, NInsertValues v);

	public void visitInsertSubQuery(T visitorContext, NInsertSubQuery q);

	// 更新
	public void visitUpdateDeclare(T visitorContext, NUpdateDeclare u);

	public void visitUpdateStmt(T visitorContext, NUpdateStmt u);

	// 删除
	public void visitDeleteDeclare(T visitorContext, NDeleteDeclare d);

	public void visitDeleteStmt(T visitorContext, NDeleteStmt d);

	// ORM
	public void visitOrmDeclare(T visitorContext, NOrmDeclare o);

	public void visitOrmOverride(T visitorContext, NOrmOverride o);

	// 表
	public void visitAbstractTableDeclare(T visitorContext,
			NAbstractTableDeclare t);

	public void visitTableDeclare(T visitorContext, NTableDeclare t);

	// 过程和函数
	public void visitProcedureDeclare(T visitorContext, NProcedureDeclare p);

	public void visitFunctionDeclare(T visitorContext, NFunctionDeclare f);

	public void visitSegment(T visitorContext, NSegment s);

	public void visitVarStmt(T visitorContext, NVarStmt v);

	public void visitAssignStmt(T visitorContext, NAssignStmt a);

	public void visitIfStmt(T visitorContext, NIfStmt i);

	public void visitWhileStmt(T visitorContext, NWhileStmt w);

	public void visitLoopStmt(T visitorContext, NLoopStmt l);

	public void visitForeachStmt(T visitorContext, NForeachStmt f);

	public void visitReturnStmt(T visitorContext, NReturnStmt r);

	public void visitBreakStmt(T visitorContext, NBreakStmt b);

	public void visitPrintStmt(T visitorContext, NPrintStmt p);
}
