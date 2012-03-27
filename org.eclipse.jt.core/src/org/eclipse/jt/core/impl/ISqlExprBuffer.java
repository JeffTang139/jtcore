package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.type.DataType;

public interface ISqlExprBuffer extends ISqlBuffer {
	public ISqlExprBuffer load(long val);

	public ISqlExprBuffer load(double val);

	public ISqlExprBuffer load(byte[] val);

	public ISqlExprBuffer load(boolean val);

	public ISqlExprBuffer loadStr(String val);

	public ISqlExprBuffer loadDate(long val);

	/**
	 * 装载内部声明变量
	 * 
	 * @param name
	 * @return
	 */
	public ISqlExprBuffer loadVar(String name);

	/**
	 * 装载外部参数
	 * 
	 * @param reserver
	 * @return
	 */
	public ISqlExprBuffer loadVar(ParameterReserver reserver);

	public ISqlExprBuffer loadField(String tableRef, String field);

	public ISqlExprBuffer loadNull(DataType type);

	public ISqlSelectBuffer subQuery();

	public ISqlExprBuffer neg();

	public ISqlExprBuffer add(int paramCount);

	public ISqlExprBuffer sub(int paramCount);

	public ISqlExprBuffer mul(int paramCount);

	public ISqlExprBuffer div(int paramCount);

	public ISqlExprBuffer mod();

	public ISqlExprBuffer func(SqlFunction func, int paramCount);

	public ISqlExprBuffer searchedCase(int paramCount);

	public ISqlExprBuffer simpleCase(int paramCount);

	public ISqlExprBuffer coalesce(int paramCount);

	public ISqlExprBuffer lt();

	public ISqlExprBuffer le();

	public ISqlExprBuffer gt();

	public ISqlExprBuffer ge();

	public ISqlExprBuffer eq();

	public ISqlExprBuffer ne();

	public ISqlExprBuffer and(int paramCount);

	public ISqlExprBuffer or(int paramCount);

	public ISqlExprBuffer not();

	public ISqlExprBuffer predicate(SqlPredicate pred, int paramCount);
}
