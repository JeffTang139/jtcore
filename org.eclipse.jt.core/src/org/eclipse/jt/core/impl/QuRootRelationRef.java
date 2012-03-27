package org.eclipse.jt.core.impl;

/**
 * 查询定义中使用的<strong>根级</strong>关系引用
 * 
 * <p>
 * 根级关系引用为在sql的from子句中,处于最上层并以笛卡尔积连接的关系引用.例如,在如下省略连接类型及条件的sql语句中:
 * 
 * <blockquote>
 * 
 * <pre>
 * select * from a join (b join c join d) join e, f join g, h
 * </pre>
 * 
 * </blockquote>
 * 
 * a, f, h属于根级的关系引用.
 * 
 * <p>
 * QuRootRelationRef与QuJoinedRelationRef的区别在于:
 * <ul>
 * <li>QuRootRelationRef的prevSibling与nextSibling的类型同为QuRootRelationRef.
 * <li>QuJoinedRelationRef只有类型为QuJoinedRelationRef的nextSibling.
 * <li>两者的firstChild类型都为QuJoinedRelationRef.
 * </ul>
 * 
 * <p>
 * 从关系代数上讲,根级引用之间是表示笛卡尔积的运算,连接引用之间是以内外连接形式(跟DNA的接口定义有一定关系)连接-笛卡尔积加选择运算.
 * 虽然性质十分相似,但考虑到Root的newJoin方法导致的结构变化,将两者加以区分更容易处理,也跟针对通用的sql语法.
 * 
 * <p>
 * 所有根级关系引用组成一个双向不成环链表.对于每一个根级关系,则是一颗树的结构.
 * 根级关系引用继承了迭代器接口,迭代自身及之后的根级关系引用,对于每一个根级关系引用的树型结构,以先序遍历进行迭代.
 * 
 * @author Jeff Tang
 * 
 */
interface QuRootRelationRef extends QuRelationRef, Iterable<QuRelationRef> {

	QuRootQueryRef castAsQueryRef();

	QuRootTableRef castAsTableRef();

	QuRootRelationRef prev();

	QuRootRelationRef next();

	QuRootRelationRef last();

	QuRelationRef findRelationRef(String name);

	QuRelationRef findRelationRef(Relation target);

	QuRootRelationRef findRootRelationRef(String name);

	/**
	 * 目标查询定义增加以当前引用为样例的根关系引用,包括其所有递归的join及next
	 * 
	 * @param target
	 *            复制到的目标查询定义.
	 * @param args
	 *            参数容器,遇到参数表达式时从该目标查找参数定义.
	 */
	void cloneTo(SelectImpl<?, ?> target, ArgumentOwner args);

	void render(ISqlSelectBuffer buffer, TableUsages usages);
}
