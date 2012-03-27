package org.eclipse.jt.core.def.query;

/**
 * 关系引用域
 * 
 * <p>
 * 表示当前对象拥有多关系引用
 * 
 * @author Jeff Tang
 * 
 */
public interface RelationRefDomainDefine {

	/**
	 * 返回当前域的最近可引用域
	 * 
	 * <p>
	 * 最近可引用域不一定就是结构上的直接上级域
	 * 
	 * @return
	 */
	RelationRefDomainDefine getDomain();

	/**
	 * 在当前域内查找指定名称的关系引用
	 * 
	 * @param name
	 * @return
	 */
	RelationRefDefine findRelationRef(String name);

	/**
	 * 获取当前域内指定名称的关系引用
	 * 
	 * @param name
	 * @return
	 */
	RelationRefDefine getRelationRef(String name);

	/**
	 * 在当前域及有效可引用域内查找指定名称的关系引用
	 * 
	 * @param name
	 * @return
	 */
	RelationRefDefine findRelationRefRecursively(String name);

	/**
	 * 返回在当前域及有效可引用域内指定名称的关系引用
	 * 
	 * @param name
	 * @return
	 */
	RelationRefDefine getRelationRefRecursively(String name);
}
