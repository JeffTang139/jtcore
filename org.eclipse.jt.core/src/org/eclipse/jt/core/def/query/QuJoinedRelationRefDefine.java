package org.eclipse.jt.core.def.query;

/**
 * ��ѯ������ʹ�õ����ӵĹ�ϵ����
 * 
 * <p>
 * ���ӹ�ϵ���ñ��������������������͵Ĺ�ϵ�����ö���
 * 
 * <h4>�������ӵĹ���</h4>
 * <ul>
 * <li>sql�������:<strong>select * from A a join B b join C c</strong>.�乹�����Ϊ:
 * <blockquote>
 * 
 * <pre>
 * QuRelationRefDeclare a = query.newReference(A);
 * QuJoinedRelationRefDeclare b = a.newJoin(B);
 * QuJoinedRelationRefDeclare c = a.newJoin(C);
 * </pre>
 * 
 * </blockquote>
 * <li>sql�������:<strong>select * from A a join (B b join C c)</strong>.�乹�����Ϊ:
 * <blockquote>
 * 
 * <pre>
 * QuRelationRefDeclare a = query.newReference(A);
 * QuJoinedRelationRefDeclare b = a.newJoin(B);
 * QuJoinedRelationRefDeclare c = b.newJoin(C);
 * </pre>
 * 
 * </blockquote>
 * </ul>
 * 
 * <p>
 * ��TableReferenceDefine���ΪQuJoinedTableRefDefine;
 * ��QueryReferenceDefine���ΪQuJoinedQueryRefDefine.
 * 
 * @see org.eclipse.jt.core.def.query.TableJoinType
 * 
 * @author Jeff Tang
 * 
 */
public interface QuJoinedRelationRefDefine extends QuRelationRefDefine,
		JoinedRelationRefDefine {

	@Deprecated
	public QuJoinedTableRefDefine castAsTableRef();

	@Deprecated
	public QuJoinedQueryRefDefine castAsQueryRef();
}
