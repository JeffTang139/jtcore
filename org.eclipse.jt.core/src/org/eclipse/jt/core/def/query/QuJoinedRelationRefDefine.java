package org.eclipse.jt.core.def.query;

/**
 * 查询定义中使用的连接的关系引用
 * 
 * <p>
 * 连接关系引用表达带连接条件与连接类型的关系因引用定义
 * 
 * <h4>关于连接的构造</h4>
 * <ul>
 * <li>sql语句例如:<strong>select * from A a join B b join C c</strong>.其构造过程为:
 * <blockquote>
 * 
 * <pre>
 * QuRelationRefDeclare a = query.newReference(A);
 * QuJoinedRelationRefDeclare b = a.newJoin(B);
 * QuJoinedRelationRefDeclare c = a.newJoin(C);
 * </pre>
 * 
 * </blockquote>
 * <li>sql语句例如:<strong>select * from A a join (B b join C c)</strong>.其构造过程为:
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
 * 和TableReferenceDefine组合为QuJoinedTableRefDefine;
 * 和QueryReferenceDefine组合为QuJoinedQueryRefDefine.
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
