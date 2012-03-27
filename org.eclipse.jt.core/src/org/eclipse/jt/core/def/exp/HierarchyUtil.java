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
	 * ����ָ��·��������ȵı��ʽ
	 *
	 * <p>
	 * �Ϸ��ļ���·����С���Ϊ1
	 *
	 * @param path
	 * @return
	 */
	public static final ValueExpression levelOf(Object path) {
		return SQLFunc.xLen(ValueExpression.builder.expOf(path)).xDiv(I17);
	}

	/**
	 * ����ָ��·��ֱ�Ӹ��ڵ�recid�ı��ʽ
	 *
	 * @param path
	 * @return
	 */
	public static final ValueExpression parentOf(Object path) {
		ValueExpression p = ValueExpression.builder.expOf(path);
		return SQLFunc.xSubstr(p, SQLFunc.xLen(p).xSub(I32), I16);
	}

	/**
	 * ���켶��·��childΪtarget��"�ӽڵ�"���������ʽ
	 *
	 * <p>
	 * �Ӹ��ڵ��ѯ�ӽڵ�ʱʹ��,��targetPathΪ������Դ,����Ч�ʵ���.
	 *
	 * @param childPath
	 *            �ӽڵ�·���ı��ʽ
	 * @param targetPath
	 *            Ŀ��ڵ�·���ı��ʽ
	 * @param incluedTarget
	 *            �Ƿ����Ŀ��ڵ㱾��
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
	 * ���켶��·��descendantΪtarget��"����ڵ�"���������ʽ
	 *
	 * <p>
	 * �Ӹ��ڵ��ѯ�ӽڵ�ʱʹ��,��targetPathΪ������Դ,����Ч�ʵ���.
	 *
	 * @param descendantPath
	 *            ����ڵ�·���ı��ʽ
	 * @param targetPath
	 *            Ŀ��ڵ�·���ı��ʽ
	 * @param incluedTarget
	 *            �Ƿ����Ŀ��ڵ㱾��
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
	 * ���켶��·��descendantΪtarget��"������n��������ڵ�"���������ʽ
	 *
	 * @param descendantPath
	 *            ����ڵ�·���ı��ʽ
	 * @param targetPath
	 *            Ŀ��ڵ�·���ı��ʽ
	 * @param range
	 *            ��Լ�����ȵ��޶�
	 *@param incluedTarget
	 *            �Ƿ����Ŀ��ڵ㱾��
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
	 * ���켶��·��descendantΪtarget��"��Ե�n��������ڵ�"���������ʽ
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
	 * ���켶��·��parentΪtarget��"���ڵ�"���������ʽ
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
