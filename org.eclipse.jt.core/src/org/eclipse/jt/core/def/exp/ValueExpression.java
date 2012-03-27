package org.eclipse.jt.core.def.exp;

import org.eclipse.jt.core.def.query.SubQueryDefine;
import org.eclipse.jt.core.impl.NullExpr;
import org.eclipse.jt.core.impl.ValueExpr;
import org.eclipse.jt.core.type.DataTypable;

/**
 * ֵ���ʽ���ӿ�
 * 
 * @author Jeff Tang
 */
public interface ValueExpression extends DataTypable {

	/**
	 * NULL���ʽ
	 */
	public static final ValueExpression NULL = NullExpr.NULL;

	/**
	 * ֵ���ʽ�Ĺ���ӿ�
	 * 
	 * @author Jeff Tang
	 * 
	 */
	public static interface ValueExpressionBuilder {

		public ValueExpression expOf(Object object);
	}

	/**
	 * ֵ���ʽ�ľ�̬���칤��
	 */
	public static final ValueExpressionBuilder builder = ValueExpr.builder;

	/**
	 * ����
	 * 
	 * <p>
	 * ������������ͬ�����ʽת��
	 */
	public PredicateExpression xEq(Object value);

	/**
	 * ������
	 * 
	 * <p>
	 * ������������ͬ�����ʽת��
	 */
	public PredicateExpression xnEq(Object value);

	/**
	 * С��
	 * 
	 * <p>
	 * ������������ͬ�����ʽת��
	 */
	public PredicateExpression xLess(Object value);

	/**
	 * ����
	 * 
	 * <p>
	 * ������������ͬ�����ʽת��
	 */
	public PredicateExpression xGreater(Object value);

	/**
	 * С�ڻ����
	 * 
	 * <p>
	 * ������������ͬ�����ʽת��
	 */
	public PredicateExpression xLE(Object value);

	/**
	 * ���ڻ����
	 * 
	 * <p>
	 * ������������ͬ�����ʽת��
	 */
	public PredicateExpression xGE(Object value);

	/**
	 * �ڷ�Χ��
	 * 
	 * <p>
	 * ������������ͬ
	 */
	public PredicateExpression xBtwn(Object value1, Object value2);

	/**
	 * ���ڷ�Χ��
	 * 
	 * <p>
	 * ������������ͬ
	 */
	public PredicateExpression xnBtwn(Object value1, Object value2);

	/**
	 * �ַ���like
	 * 
	 * <p>
	 * ������������ͬ,�����ַ����������
	 */
	public PredicateExpression xLike(Object value);

	/**
	 * �ַ���like,ָ��ת���
	 * 
	 * <p>
	 * ������������ͬ,�����ַ����������
	 */
	public PredicateExpression xLike(Object value, Object escape);

	/**
	 * �ַ�����like
	 * 
	 * <p>
	 * ������������ͬ,�����ַ����������
	 */
	public PredicateExpression xnLike(Object value);

	/**
	 * �ַ�����like,ָ��ת���
	 * 
	 * <p>
	 * ������������ͬ,�����ַ����������
	 */
	public PredicateExpression xnLike(Object value, Object escape);

	/**
	 * �ַ�����ָ��ֵΪǰ׺
	 * 
	 * <p>
	 * ������������ͬ,�����ַ����������
	 */
	public PredicateExpression xStartW(Object value);

	/**
	 * �ַ�����ָ��ֵΪ��׺
	 * 
	 * <p>
	 * ������������ͬ,�����ַ����������
	 */
	public PredicateExpression xEndW(Object value);

	/**
	 * �ַ�������ָ��ֵ
	 * 
	 * <p>
	 * ������������ͬ,�����ַ����������
	 */
	public PredicateExpression xContain(Object value);

	/**
	 * �ַ���������ָ��ֵ
	 * 
	 * <p>
	 * ������������ͬ,�����ַ����������
	 */
	public PredicateExpression xnContain(Object value);

	/**
	 * �����б�ֵ
	 * 
	 * <p>
	 * ������������ͬ
	 */
	public PredicateExpression xIn(Object value);

	/**
	 * �����б�ֵ
	 * 
	 * <p>
	 * ������������ͬ
	 */
	public PredicateExpression xIn(Object value, Object... values);

	/**
	 * �����б�ֵ
	 * 
	 * <p>
	 * ������������ͬ
	 */
	public PredicateExpression xIn(Object[] values);

	/**
	 * �����Ӳ�ѯ
	 * 
	 * <p>
	 * �������������Ӳ�ѯ�����������ͬ
	 */
	public PredicateExpression xIn(SubQueryDefine subquery);

	/**
	 * �������б�ֵ
	 * 
	 * <p>
	 * ������������ͬ
	 */
	public PredicateExpression xnIn(Object value);

	/**
	 * �������б�ֵ
	 * 
	 * <p>
	 * ������������ͬ
	 */
	public PredicateExpression xnIn(Object value, Object... values);

	/**
	 * �������б�ֵ
	 * 
	 * <p>
	 * ������������ͬ
	 */
	public PredicateExpression xnIn(Object[] values);

	/**
	 * �������Ӳ�ѯ
	 * 
	 * <p>
	 * �������������Ӳ�ѯ�����������ͬ
	 */
	public PredicateExpression xnIn(SubQueryDefine subquery);

	/**
	 * Ϊ��
	 */
	public PredicateExpression xIsNull();

	/**
	 * ��Ϊ��
	 */
	public PredicateExpression xnNull();

	/**
	 * �ַ�������
	 * 
	 * <p>
	 * �ַ�������,lob�ֶβ���,��ֵ��Ϊ�մ�.
	 */
	public OperateExpression xStrConcat(Object value);

	/**
	 * �ַ�������
	 * 
	 * <p>
	 * �ַ�������,lob�ֶβ���,��ֵ��Ϊ�մ�.
	 */
	public OperateExpression xStrConcat(Object value, Object... values);

	/**
	 * �����ƴ�����
	 * 
	 * <p>
	 * ��ֵ��Ϊ�մ�.
	 */
	public OperateExpression xBinConcat(Object value);

	/**
	 * �����ƴ�����
	 * 
	 * <p>
	 * ��ֵ��Ϊ�մ�.
	 */
	public OperateExpression xBinConcat(Object value, Object... values);

	/**
	 * ��
	 * 
	 * <p>
	 * ���������Ϊ��ֵ����
	 */
	public OperateExpression xAdd(Object value);

	/**
	 * ��
	 * 
	 * <p>
	 * ���������Ϊ��ֵ����
	 */
	public OperateExpression xAdd(Object value, Object... values);

	/**
	 * ��
	 * 
	 * <p>
	 * ���������Ϊ��ֵ����
	 */
	public OperateExpression xSub(Object value);

	/**
	 * ��
	 * 
	 * <p>
	 * ���������Ϊ��ֵ����
	 */
	public OperateExpression xSub(Object value, Object... values);

	/**
	 * ��
	 * 
	 * <p>
	 * ���������Ϊ��ֵ����
	 */
	public OperateExpression xMul(Object value);

	/**
	 * ��
	 * 
	 * <p>
	 * ���������Ϊ��ֵ����
	 */
	public OperateExpression xMul(Object value, Object... values);

	/**
	 * ������
	 * 
	 * <p>
	 * ���������Ϊ��ֵ����
	 * 
	 * <p>
	 * �����嶼Ϊ����ʱΪ����
	 */
	public OperateExpression xDiv(Object value);

	/**
	 * ȡ��ֵ����
	 * 
	 * <p>
	 * ���������Ϊ��ֵ����
	 */
	public OperateExpression xMinus();

	/**
	 * ȡ������
	 * <p>
	 * ���������Ϊ��ֵ����
	 * 
	 * @return
	 */
	public OperateExpression xMod(Object value);

	/**
	 * ��Case
	 * 
	 * <p>
	 * ��sql�е�:
	 * 
	 * <pre>
	 * CASE value WHEN when_value THEN return_value [...n] [ELSE
	 * defaul_value] END
	 * </pre>
	 * 
	 * @param whenValue
	 *            �뵱ǰ���ʽ��Ƚϵ�ValueExpreesion
	 * @param resultValue
	 *            whenValue��Ӧ�ķ���ֵ
	 * @param others
	 *            ������whenValue��resultValue��,��defaultValue
	 * @return
	 */
	public OperateExpression xSimpleCase(Object whenValue, Object resultValue,
			Object... others);

	/**
	 * ���ص�һ���ǿձ��ʽ
	 */
	public OperateExpression xCoalesce(Object value);

	/**
	 * ���ص�һ���ǿձ��ʽ
	 */
	public OperateExpression xCoalesce(Object value, Object... values);

	/**
	 * ����ָʾ��ǰ���α��ʽ�ĸ��ڵ�RECIDֵ�ı��ʽ
	 */
	public OperateExpression xParentRECID();

	/**
	 * ����ָʾ��ǰ���α��ʽ�����ϵ�n�����Ƚڵ�RECIDֵ�ı��ʽ
	 * 
	 * @param relative
	 *            ָʾ���ϵ�n��,��ת��Ϊ���ͱ��ʽ
	 * @return
	 */
	public OperateExpression xAncestorRECID(Object relative);

	/**
	 * ����ָʾ��ǰ���α��ʽ�ľ������Ϊn�����Ƚڵ�RECIDֵ�ı��ʽ
	 * 
	 * @param absolute
	 *            ������ȵ�n��������,��ת��Ϊ���ͱ��ʽ
	 * @return
	 */
	public OperateExpression xAncestorRECIDOfLevel(Object absolute);

	/**
	 * ����ָʾ��ǰ���α��ʽ�����ֵ�ı��ʽ.
	 * 
	 * @return
	 */
	public OperateExpression xLevelOf();

	/**
	 * ���ص�ǰ�ڵ�ΪĿ��ڵ���ӽڵ���������ʽ.
	 * 
	 * @param parent
	 *            Ŀ��ڵ�ļ���·�����ʽ
	 * @return
	 */
	public PredicateExpression xIsChildOf(TableFieldRefExpr parent);

	/**
	 * ���ص�ǰ�ڵ�ΪĿ��ڵ������ڵ���������ʽ.
	 * 
	 * @param ancestor
	 *            Ŀ��ڵ�ļ���·�����ʽ
	 * @return
	 */
	public PredicateExpression xIsDescendantOf(TableFieldRefExpr ancestor);

	/**
	 * ���ص�ǰ�ڵ�ΪĿ��ڵ����n����n�����ڵ�����ڵ���������ʽ.
	 * 
	 * @param ancestor
	 *            Ŀ��ڵ�ļ���·�����ʽ
	 * @param range
	 *            ���Լ�����ȵı��ʽ
	 * @return
	 */
	public PredicateExpression xIsDescendantOf(TableFieldRefExpr ancestor,
			Object range);

	/**
	 * ���ص�ǰ�ڵ�ΪĿ��ڵ����n��������ڵ���������ʽ.
	 * 
	 * @param ancestor
	 *            Ŀ��ڵ�ļ���·�����ʽ
	 * @param relative
	 *            ��Լ�����ȵı��ʽ
	 * @return
	 */
	public PredicateExpression xIsRelativeDescendantOf(
			TableFieldRefExpr ancestor, Object relative);

	/**
	 * ���ص�ǰ�ڵ�ΪĿ��ڵ㸸�ڵ���������ʽ.
	 * 
	 * @param child
	 *            Ŀ��ڵ�ļ���·�����ʽ
	 * @return
	 */
	public PredicateExpression xIsParentOf(TableFieldRefExpr child);

	/**
	 * ���ص�ǰ�ڵ�ΪĿ��ڵ����n�������Ƚڵ���������ʽ
	 * 
	 * @param descendant
	 *            Ŀ��ڵ�ļ���·�����ʽ
	 * @param relative
	 *            ��Լ�����ȵı��ʽ
	 * @return
	 */
	public PredicateExpression xIsRelativeAncestorOf(
			TableFieldRefExpr descendant, Object relative);
}
