package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.exp.ConditionalExpression;
import org.eclipse.jt.core.def.table.TableJoinType;

/**
 * ���ӵĹ�ϵ����
 * 
 * @author Jeff Tang
 * 
 */
@SuppressWarnings("deprecation")
public interface JoinedRelationRefDefine extends RelationRefDefine,
		MoJoinedRelationRefDefine {

	/**
	 * ��ȡ��������
	 * 
	 * @return
	 */
	public ConditionalExpression getJoinCondition();

	/**
	 * ��ȡ��������
	 * 
	 * @return
	 */
	public TableJoinType getJoinType();
}
