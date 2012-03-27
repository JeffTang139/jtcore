/**
 * Copyright (C) 2007-2008 JeffTang Software Co., Ltd. All rights reserved.
 *
 * File TreeNodeFilter.java
 * Date 2008-9-5
 */
package org.eclipse.jt.core;

/**
 * 树节点过滤器。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public interface TreeNodeFilter<TItem> {

	/**
	 * 接受程度
	 * 
	 * @author Jeff Tang
	 * @version 1.0
	 */
	public enum Acception {
		/**
		 * 接受当前节点以及子节点
		 */
		ALL,
		/**
		 * 接受当前，但排除子节点
		 */
		NO_CHILDREN,
		/**
		 * 不接受当前节点，但是接受子节点
		 */
		ONLY_CHILDREN
	}

	/**
	 * 过滤树形结构的节点。
	 * 
	 * 结点的级次说明：
	 * 
	 * <pre>
	 * 假设有下面这样一种树形结构：
	 * 
	 *                    (a)          (h)
	 *                   / | \        / | \
	 *                  /  |  \      /  |  \
	 *                (b) (c) (d)  (i) (j) (k)
	 *               / | \            / | \
	 *              /  |  \          /  |  \
	 *            (e) (f) (g)      (l) (m) (n)
	 * 
	 * 我们用“L”代表“level”，“A”代表“absolute”，“R”代表“relative”。
	 * 
	 * 1. 如果查询根节点（即所有节点）：
	 *                                                          A   R
	 * 
	 *                          ( )                   ----  L   0   0
	 *                        /     \
	 *                      /         \
	 *                    (a)          (h)            ----  L   1   1
	 *                   / | \        / | \
	 *                  /  |  \      /  |  \
	 *                (b) (c) (d)  (i) (j) (k)        ----  L   2   2
	 *               / | \            / | \
	 *              /  |  \          /  |  \
	 *            (e) (f) (g)      (l) (m) (n)        ----  L   3   3
	 * 
	 * 2. 如果查询节点(a)：
	 *                                                          A   R
	 * 
	 *                    (a)                         ----  L   1   0
	 *                   / | \
	 *                  /  |  \
	 *                (b) (c) (d)                     ----  L   2   1
	 *               / | \
	 *              /  |  \
	 *            (e) (f) (g)                         ----  L   3   2
	 * 
	 * 3. 如果查询节点(j)：
	 *                                                          A   R
	 * 
	 *                (j)                             ----  L   2   0
	 *               / | \
	 *              /  |  \
	 *            (l) (m) (n)                         ----  L   3   1
	 * 
	 * 4. 如果查询节点(g)：
	 *                                                          A   R
	 * 
	 *                (g)                             ----  L   3   0
	 * </pre>
	 * 
	 * @param item
	 *            节点元素
	 * @param absoluteLevel
	 *            绝对级次，从1开始
	 * @param relativeLevel
	 *            相对级次，从0开始
	 * @return 返回接受程度，或者返回null表示不接受
	 */
	public Acception accept(TItem item, int absoluteLevel, int relativeLevel);

	/**
	 * 只接受根结点及第一级子结点的过滤器
	 */
	@SuppressWarnings("unchecked")
	public static final TreeNodeFilter FIRST_LEVEL = new TreeNodeFilter() {
		public TreeNodeFilter.Acception accept(Object item, int absoluteLevel,
				int relativeLevel) {
			if (relativeLevel == 0) { // 相对0级表示查询出的根结点
				return Acception.ALL; // 接受本结点及其子结点
			} else if (relativeLevel == 1) { // 相对1级表示查询出的根结点的第一级子结点
				return Acception.NO_CHILDREN; // 只接受本结点（指item），不接受其子结点
			} else {
				return null; // 既不接受本结点，也不接受其子结点
			}
		}
	};
}
