package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.spi.sql.SQLNotSupportedException;

class VisitorBase<T> implements SQLVisitor<T> {
	public void visitLiteralBoolean(T visitorContext, NLiteralBoolean b) {
		throw new SQLNotSupportedException();
	}

	public void visitLiteralBytes(T visitorContext, NLiteralBytes b) {
		throw new SQLNotSupportedException();
	}

	public void visitLiteralDate(T visitorContext, NLiteralDate d) {
		throw new SQLNotSupportedException();
	}

	public void visitLiteralDouble(T visitorContext, NLiteralDouble d) {
		throw new SQLNotSupportedException();
	}

	public void visitLiteralGUID(T visitorContext, NLiteralGUID g) {
		throw new SQLNotSupportedException();
	}

	public void visitLiteralInt(T visitorContext, NLiteralInt i) {
		throw new SQLNotSupportedException();
	}

	public void visitLiteralLong(T visitorContext, NLiteralLong l) {
		throw new SQLNotSupportedException();
	}

	public void visitLiteralString(T visitorContext, NLiteralString s) {
		throw new SQLNotSupportedException();
	}

	public void visitParamDeclare(T visitorContext, NParamDeclare v) {
		throw new SQLNotSupportedException();
	}

	public void visitBinaryExpr(T visitorContext, NBinaryExpr e) {
		throw new SQLNotSupportedException();
	}

	public void visitAggregateExpr(T visitorContext, NAggregateExpr e) {
		throw new SQLNotSupportedException();
	}

	public void visitCoalesceExpr(T visitorContext, NCoalesceExpr e) {
		throw new SQLNotSupportedException();
	}

	public void visitColumnRefExpr(T visitorContext, NColumnRefExpr e) {
		throw new SQLNotSupportedException();
	}

	public void visitFunctionExpr(T visitorContext, NFunctionExpr e) {
		throw new SQLNotSupportedException();
	}

	public void visitHaidExpr(T visitorContext, NHaidExpr e) {
		throw new SQLNotSupportedException();
	}

	public void visitHlvExpr(T visitorContext, NHlvExpr e) {
		throw new SQLNotSupportedException();
	}

	public void visitNegativeExpr(T visitorContext, NNegativeExpr e) {
		throw new SQLNotSupportedException();
	}

	public void visitNullExpr(T visitorContext, NNullExpr e) {
		throw new SQLNotSupportedException();
	}

	public void visitSearchedCaseExpr(T visitorContext, NSearchedCaseExpr e) {
		throw new SQLNotSupportedException();
	}

	public void visitSimpleCaseExpr(T visitorContext, NSimpleCaseExpr e) {
		throw new SQLNotSupportedException();
	}

	public void visitVarRefExpr(T visitorContext, NVarRefExpr e) {
		throw new SQLNotSupportedException();
	}

	public void visitCompareExpr(T visitorContext, NCompareExpr e) {
		throw new SQLNotSupportedException();
	}

	public void visitBetweenExpr(T visitorContext, NBetweenExpr e) {
		throw new SQLNotSupportedException();
	}

	public void visitExistsExpr(T visitorContext, NExistsExpr e) {
		throw new SQLNotSupportedException();
	}

	public void visitHierarchyExpr(T visitorContext, NHierarchyExpr e) {
		throw new SQLNotSupportedException();
	}

	public void visitInExpr(T visitorContext, NInExpr e) {
		throw new SQLNotSupportedException();
	}

	public void visitIsLeafExpr(T visitorContext, NIsLeafExpr e) {
		throw new SQLNotSupportedException();
	}

	public void visitIsNullExpr(T visitorContext, NIsNullExpr e) {
		throw new SQLNotSupportedException();
	}

	public void visitLogicalExpr(T visitorContext, NLogicalExpr e) {
		throw new SQLNotSupportedException();
	}

	public void visitPathExpr(T visitorContext, NPathExpr e) {
		throw new SQLNotSupportedException();
	}

	public void visitStrCompareExpr(T visitorContext, NStrCompareExpr e) {
		throw new SQLNotSupportedException();
	}

	public void visitQueryDeclare(T visitorContext, NQueryDeclare q) {
		throw new SQLNotSupportedException();
	}

	public void visitQueryStmt(T visitorContext, NQueryStmt q) {
		throw new SQLNotSupportedException();
	}

	public void visitQuerySpecific(T visitorContext, NQuerySpecific s) {
		throw new SQLNotSupportedException();
	}

	public void visitSourceTable(T visitorContext, NSourceTable t) {
		throw new SQLNotSupportedException();
	}

	public void visitSourceJoin(T visitorContext, NSourceJoin j) {
		throw new SQLNotSupportedException();
	}

	public void visitSourceSubQuery(T visitorContext, NSourceSubQuery q) {
		throw new SQLNotSupportedException();
	}

	public void visitSourceRelate(T visitorContext, NSourceRelate r) {
		throw new SQLNotSupportedException();
	}

	public void visitOrmDeclare(T visitorContext, NOrmDeclare o) {
		throw new SQLNotSupportedException();
	}

	public void visitOrmOverride(T visitorContext, NOrmOverride o) {
		throw new SQLNotSupportedException();
	}

	public void visitInsertDeclare(T visitorContext, NInsertDeclare i) {
		throw new SQLNotSupportedException();
	}

	public void visitInsertStmt(T visitorContext, NInsertStmt i) {
		throw new SQLNotSupportedException();
	}

	public void visitInsertValues(T visitorContext, NInsertValues v) {
		throw new SQLNotSupportedException();
	}

	public void visitInsertSubQuery(T visitorContext, NInsertSubQuery q) {
		throw new SQLNotSupportedException();
	}

	public void visitUpdateDeclare(T visitorContext, NUpdateDeclare u) {
		throw new SQLNotSupportedException();
	}

	public void visitUpdateStmt(T visitorContext, NUpdateStmt u) {
		throw new SQLNotSupportedException();
	}

	public void visitDeleteDeclare(T visitorContext, NDeleteDeclare d) {
		throw new SQLNotSupportedException();
	}

	public void visitDeleteStmt(T visitorContext, NDeleteStmt d) {
		throw new SQLNotSupportedException();
	}

	public void visitAbstractTableDeclare(T visitorContext,
			NAbstractTableDeclare t) {
		throw new SQLNotSupportedException();
	}

	public void visitTableDeclare(T visitorContext, NTableDeclare t) {
		throw new SQLNotSupportedException();
	}

	public void visitProcedureDeclare(T visitorContext, NProcedureDeclare p) {
		throw new SQLNotSupportedException();
	}

	public void visitFunctionDeclare(T visitorContext, NFunctionDeclare f) {
		throw new SQLNotSupportedException();
	}

	public void visitVarStmt(T visitorContext, NVarStmt v) {
		throw new SQLNotSupportedException();
	}

	public void visitAssignStmt(T visitorContext, NAssignStmt a) {
		throw new SQLNotSupportedException();
	}

	public void visitIfStmt(T visitorContext, NIfStmt i) {
		throw new SQLNotSupportedException();
	}

	public void visitWhileStmt(T visitorContext, NWhileStmt w) {
		throw new SQLNotSupportedException();
	}

	public void visitLoopStmt(T visitorContext, NLoopStmt l) {
		throw new SQLNotSupportedException();
	}

	public void visitForeachStmt(T visitorContext, NForeachStmt f) {
		throw new SQLNotSupportedException();
	}

	public void visitBreakStmt(T visitorContext, NBreakStmt b) {
		throw new SQLNotSupportedException();
	}

	public void visitPrintStmt(T visitorContext, NPrintStmt p) {
		throw new SQLNotSupportedException();
	}

	public void visitReturnStmt(T visitorContext, NReturnStmt r) {
		throw new SQLNotSupportedException();
	}

	public void visitSegment(T visitorContext, NSegment s) {
		throw new SQLNotSupportedException();
	}
}
