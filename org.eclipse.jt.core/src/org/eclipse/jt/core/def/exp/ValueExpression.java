package org.eclipse.jt.core.def.exp;

import org.eclipse.jt.core.def.query.SubQueryDefine;
import org.eclipse.jt.core.impl.NullExpr;
import org.eclipse.jt.core.impl.ValueExpr;
import org.eclipse.jt.core.type.DataTypable;

/**
 * 值表达式基接口
 * 
 * @author Jeff Tang
 */
public interface ValueExpression extends DataTypable {

	/**
	 * NULL表达式
	 */
	public static final ValueExpression NULL = NullExpr.NULL;

	/**
	 * 值表达式的构造接口
	 * 
	 * @author Jeff Tang
	 * 
	 */
	public static interface ValueExpressionBuilder {

		public ValueExpression expOf(Object object);
	}

	/**
	 * 值表达式的静态构造工厂
	 */
	public static final ValueExpressionBuilder builder = ValueExpr.builder;

	/**
	 * 等于
	 * 
	 * <p>
	 * 运算体类型相同或可隐式转换
	 */
	public PredicateExpression xEq(Object value);

	/**
	 * 不等于
	 * 
	 * <p>
	 * 运算体类型相同或可隐式转换
	 */
	public PredicateExpression xnEq(Object value);

	/**
	 * 小于
	 * 
	 * <p>
	 * 运算体类型相同或可隐式转换
	 */
	public PredicateExpression xLess(Object value);

	/**
	 * 大于
	 * 
	 * <p>
	 * 运算体类型相同或可隐式转换
	 */
	public PredicateExpression xGreater(Object value);

	/**
	 * 小于或等于
	 * 
	 * <p>
	 * 运算体类型相同或可隐式转换
	 */
	public PredicateExpression xLE(Object value);

	/**
	 * 大于或等于
	 * 
	 * <p>
	 * 运算体类型相同或可隐式转换
	 */
	public PredicateExpression xGE(Object value);

	/**
	 * 在范围内
	 * 
	 * <p>
	 * 运算体类型相同
	 */
	public PredicateExpression xBtwn(Object value1, Object value2);

	/**
	 * 不在范围内
	 * 
	 * <p>
	 * 运算体类型相同
	 */
	public PredicateExpression xnBtwn(Object value1, Object value2);

	/**
	 * 字符串like
	 * 
	 * <p>
	 * 运算体类型相同,允许字符串或二进制
	 */
	public PredicateExpression xLike(Object value);

	/**
	 * 字符串like,指定转义符
	 * 
	 * <p>
	 * 运算体类型相同,允许字符串或二进制
	 */
	public PredicateExpression xLike(Object value, Object escape);

	/**
	 * 字符串不like
	 * 
	 * <p>
	 * 运算体类型相同,允许字符串或二进制
	 */
	public PredicateExpression xnLike(Object value);

	/**
	 * 字符串不like,指定转义符
	 * 
	 * <p>
	 * 运算体类型相同,允许字符串或二进制
	 */
	public PredicateExpression xnLike(Object value, Object escape);

	/**
	 * 字符串以指定值为前缀
	 * 
	 * <p>
	 * 运算体类型相同,允许字符串或二进制
	 */
	public PredicateExpression xStartW(Object value);

	/**
	 * 字符串以指定值为后缀
	 * 
	 * <p>
	 * 运算体类型相同,允许字符串或二进制
	 */
	public PredicateExpression xEndW(Object value);

	/**
	 * 字符串包含指定值
	 * 
	 * <p>
	 * 运算体类型相同,允许字符串或二进制
	 */
	public PredicateExpression xContain(Object value);

	/**
	 * 字符串不包含指定值
	 * 
	 * <p>
	 * 运算体类型相同,允许字符串或二进制
	 */
	public PredicateExpression xnContain(Object value);

	/**
	 * 属于列表值
	 * 
	 * <p>
	 * 运算体类型相同
	 */
	public PredicateExpression xIn(Object value);

	/**
	 * 属于列表值
	 * 
	 * <p>
	 * 运算体类型相同
	 */
	public PredicateExpression xIn(Object value, Object... values);

	/**
	 * 属于列表值
	 * 
	 * <p>
	 * 运算体类型相同
	 */
	public PredicateExpression xIn(Object[] values);

	/**
	 * 属于子查询
	 * 
	 * <p>
	 * 运算体类型与子查询输出列类型相同
	 */
	public PredicateExpression xIn(SubQueryDefine subquery);

	/**
	 * 不属于列表值
	 * 
	 * <p>
	 * 运算体类型相同
	 */
	public PredicateExpression xnIn(Object value);

	/**
	 * 不属于列表值
	 * 
	 * <p>
	 * 运算体类型相同
	 */
	public PredicateExpression xnIn(Object value, Object... values);

	/**
	 * 不属于列表值
	 * 
	 * <p>
	 * 运算体类型相同
	 */
	public PredicateExpression xnIn(Object[] values);

	/**
	 * 不属于子查询
	 * 
	 * <p>
	 * 运算体类型与子查询输出列类型相同
	 */
	public PredicateExpression xnIn(SubQueryDefine subquery);

	/**
	 * 为空
	 */
	public PredicateExpression xIsNull();

	/**
	 * 不为空
	 */
	public PredicateExpression xnNull();

	/**
	 * 字符串连接
	 * 
	 * <p>
	 * 字符串类型,lob字段不可,空值作为空串.
	 */
	public OperateExpression xStrConcat(Object value);

	/**
	 * 字符串连接
	 * 
	 * <p>
	 * 字符串类型,lob字段不可,空值作为空串.
	 */
	public OperateExpression xStrConcat(Object value, Object... values);

	/**
	 * 二进制串连接
	 * 
	 * <p>
	 * 空值作为空串.
	 */
	public OperateExpression xBinConcat(Object value);

	/**
	 * 二进制串连接
	 * 
	 * <p>
	 * 空值作为空串.
	 */
	public OperateExpression xBinConcat(Object value, Object... values);

	/**
	 * 加
	 * 
	 * <p>
	 * 运算体必须为数值类型
	 */
	public OperateExpression xAdd(Object value);

	/**
	 * 加
	 * 
	 * <p>
	 * 运算体必须为数值类型
	 */
	public OperateExpression xAdd(Object value, Object... values);

	/**
	 * 减
	 * 
	 * <p>
	 * 运算体必须为数值类型
	 */
	public OperateExpression xSub(Object value);

	/**
	 * 减
	 * 
	 * <p>
	 * 运算体必须为数值类型
	 */
	public OperateExpression xSub(Object value, Object... values);

	/**
	 * 乘
	 * 
	 * <p>
	 * 运算体必须为数值类型
	 */
	public OperateExpression xMul(Object value);

	/**
	 * 乘
	 * 
	 * <p>
	 * 运算体必须为数值类型
	 */
	public OperateExpression xMul(Object value, Object... values);

	/**
	 * 除运算
	 * 
	 * <p>
	 * 运算体必须为数值类型
	 * 
	 * <p>
	 * 运算体都为整型时为整除
	 */
	public OperateExpression xDiv(Object value);

	/**
	 * 取负值运算
	 * 
	 * <p>
	 * 运算体必须为数值类型
	 */
	public OperateExpression xMinus();

	/**
	 * 取余运算
	 * <p>
	 * 运算体必须为数值类型
	 * 
	 * @return
	 */
	public OperateExpression xMod(Object value);

	/**
	 * 简单Case
	 * 
	 * <p>
	 * 即sql中的:
	 * 
	 * <pre>
	 * CASE value WHEN when_value THEN return_value [...n] [ELSE
	 * defaul_value] END
	 * </pre>
	 * 
	 * @param whenValue
	 *            与当前表达式相比较的ValueExpreesion
	 * @param resultValue
	 *            whenValue对应的返回值
	 * @param others
	 *            其他的whenValue和resultValue对,及defaultValue
	 * @return
	 */
	public OperateExpression xSimpleCase(Object whenValue, Object resultValue,
			Object... others);

	/**
	 * 返回第一个非空表达式
	 */
	public OperateExpression xCoalesce(Object value);

	/**
	 * 返回第一个非空表达式
	 */
	public OperateExpression xCoalesce(Object value, Object... values);

	/**
	 * 返回指示当前级次表达式的父节点RECID值的表达式
	 */
	public OperateExpression xParentRECID();

	/**
	 * 返回指示当前级次表达式的向上第n级祖先节点RECID值的表达式
	 * 
	 * @param relative
	 *            指示向上第n级,可转化为整型表达式
	 * @return
	 */
	public OperateExpression xAncestorRECID(Object relative);

	/**
	 * 返回指示当前级次表达式的绝对深度为n的祖先节点RECID值的表达式
	 * 
	 * @param absolute
	 *            绝对深度第n级的祖先,可转化为整型表达式
	 * @return
	 */
	public OperateExpression xAncestorRECIDOfLevel(Object absolute);

	/**
	 * 返回指示当前级次表达式的深度值的表达式.
	 * 
	 * @return
	 */
	public OperateExpression xLevelOf();

	/**
	 * 返回当前节点为目标节点的子节点的条件表达式.
	 * 
	 * @param parent
	 *            目标节点的级次路径表达式
	 * @return
	 */
	public PredicateExpression xIsChildOf(TableFieldRefExpr parent);

	/**
	 * 返回当前节点为目标节点的子孙节点的条件表达式.
	 * 
	 * @param ancestor
	 *            目标节点的级次路径表达式
	 * @return
	 */
	public PredicateExpression xIsDescendantOf(TableFieldRefExpr ancestor);

	/**
	 * 返回当前节点为目标节点相对n级或n级以内的子孙节点的条件表达式.
	 * 
	 * @param ancestor
	 *            目标节点的级次路径表达式
	 * @param range
	 *            绝对级次深度的表达式
	 * @return
	 */
	public PredicateExpression xIsDescendantOf(TableFieldRefExpr ancestor,
			Object range);

	/**
	 * 返回当前节点为目标节点相对n级的子孙节点的条件表达式.
	 * 
	 * @param ancestor
	 *            目标节点的级次路径表达式
	 * @param relative
	 *            相对级次深度的表达式
	 * @return
	 */
	public PredicateExpression xIsRelativeDescendantOf(
			TableFieldRefExpr ancestor, Object relative);

	/**
	 * 返回当前节点为目标节点父节点的条件表达式.
	 * 
	 * @param child
	 *            目标节点的级次路径表达式
	 * @return
	 */
	public PredicateExpression xIsParentOf(TableFieldRefExpr child);

	/**
	 * 返回当前节点为目标节点相对n级内祖先节点的条件表达式
	 * 
	 * @param descendant
	 *            目标节点的级次路径表达式
	 * @param relative
	 *            相对级次深度的表达式
	 * @return
	 */
	public PredicateExpression xIsRelativeAncestorOf(
			TableFieldRefExpr descendant, Object relative);
}
