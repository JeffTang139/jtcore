package org.eclipse.jt.core.def.exp;

import org.eclipse.jt.core.def.query.SQLFunc;

public final class HierarchyUtil {

	public static final ConstExpression X01 = ConstExpression.builder
			.expOf(new byte[] { 1 });

	public static final ConstExpression I1 = ConstExpression.builder.expOf(1);

	public static final ConstExpression I16 = ConstExpression.builder.expOf(16);

	public static final ConstExpression I17 = ConstExpression.builder.expOf(17);

	public static final ConstExpression I32 = ConstExpression.builder.expOf(32);

	/**
	 * 构造指定路径级次深度的表达式
	 *
	 * <p>
	 * 合法的级次路径最小深度为1
	 *
	 * @param path
	 * @return
	 */
	public static final ValueExpression levelOf(Object path) {
		return SQLFunc.xLen(ValueExpression.builder.expOf(path)).xDiv(I17);
	}

	/**
	 * 构造指定路径直接父节点recid的表达式
	 *
	 * @param path
	 * @return
	 */
	public static final ValueExpression parentOf(Object path) {
		ValueExpression p = ValueExpression.builder.expOf(path);
		return SQLFunc.xSubstr(p, SQLFunc.xLen(p).xSub(I32), I16);
	}

	/**
	 * 构造级次路径child为target的"子节点"的条件表达式
	 *
	 * <p>
	 * 从父节点查询子节点时使用,即targetPath为条件来源,否则效率低下.
	 *
	 * @param childPath
	 *            子节点路径的表达式
	 * @param targetPath
	 *            目标节点路径的表达式
	 * @param incluedTarget
	 *            是否输出目标节点本身
	 * @return
	 */
	public static final ConditionalExpression isChildOf(Object childPath,
			Object targetPath, boolean incluedTarget) {
		ValueExpression c = ValueExpression.builder.expOf(childPath);
		ValueExpression p = ValueExpression.builder.expOf(targetPath);
		return (incluedTarget ? c.xGE(p) : c.xGreater(p)).and(c.xLess(p
				.xBinConcat(X01)), SQLFunc.xLen(c).xEq(
				SQLFunc.xLen(p).xAdd(I17)));
	}

	/**
	 * 构造级次路径descendant为target的"子孙节点"的条件表达式
	 *
	 * <p>
	 * 从父节点查询子节点时使用,即targetPath为条件来源,否则效率低下.
	 *
	 * @param descendantPath
	 *            子孙节点路径的表达式
	 * @param targetPath
	 *            目标节点路径的表达式
	 * @param incluedTarget
	 *            是否输出目标节点本身
	 * @return
	 */
	public static final ConditionalExpression isDescendantOf(
			Object descendantPath, Object targetPath, boolean incluedTarget) {
		ValueExpression c = ValueExpression.builder.expOf(descendantPath);
		ValueExpression p = ValueExpression.builder.expOf(targetPath);
		return (incluedTarget ? c.xGE(p) : c.xGreater(p)).and(c.xLess(p
				.xBinConcat(X01)));
	}

	/**
	 * 构造级次路径descendant为target的"不超过n级的子孙节点"的条件表达式
	 *
	 * @param descendantPath
	 *            子孙节点路径的表达式
	 * @param targetPath
	 *            目标节点路径的表达式
	 * @param range
	 *            相对级次深度的限定
	 *@param incluedTarget
	 *            是否输出目标节点本身
	 * @return
	 */
	public static final ConditionalExpression isRangeDescendantOf(
			Object descendantPath, Object targetPath, Object range,
			boolean incluedTarget) {
		ValueExpression c = ValueExpression.builder.expOf(descendantPath);
		ValueExpression p = ValueExpression.builder.expOf(targetPath);
		ValueExpression r = ValueExpression.builder.expOf(range);
		return (incluedTarget ? c.xGE(p) : c.xGreater(p)).and(c.xLess(p
				.xBinConcat(X01)), SQLFunc.xLen(c).xLE(
				SQLFunc.xLen(p).xAdd(r.xMul(I17))));
	}

	/**
	 * 构造级次路径descendant为target的"相对第n级的子孙节点"的条件表达式
	 *
	 * @param descendantPath
	 * @param targetPath
	 * @param relative
	 * @param incluedTarget
	 * @return
	 */
	public static final ConditionalExpression isRelativeDescendantOf(
			Object descendantPath, Object targetPath, Object relative,
			boolean incluedTarget) {
		ValueExpression c = ValueExpression.builder.expOf(descendantPath);
		ValueExpression p = ValueExpression.builder.expOf(targetPath);
		ValueExpression r = ValueExpression.builder.expOf(relative);
		return (incluedTarget ? c.xGE(p) : c.xGreater(p)).and(c.xLess(p
				.xBinConcat(X01)), SQLFunc.xLen(c).xEq(
				SQLFunc.xLen(p).xAdd(r.xMul(I17))));
	}

	/**
	 * 构造级次路径parent为target的"父节点"的条件表达式
	 *
	 * @param parentPath
	 * @param targetPath
	 * @return
	 */
	public static final ConditionalExpression isParentOf(Object parentPath,
			Object targetPath) {
		ValueExpression p = ValueExpression.builder.expOf(parentPath);
		ValueExpression c = ValueExpression.builder.expOf(targetPath);
		return SQLFunc.xSubstr(c, I1, SQLFunc.xLen(c).xSub(I17)).xEq(p);
	}

}
