/**
 * Copyright (C) 2007-2008 JeffTang Software Co., Ltd. All rights reserved.
 *
 * File TreeNodeFilter.java
 * Date 2008-9-5
 */
package org.eclipse.jt.core;

/**
 * ���ڵ��������
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public interface TreeNodeFilter<TItem> {

	/**
	 * ���̶ܳ�
	 * 
	 * @author Jeff Tang
	 * @version 1.0
	 */
	public enum Acception {
		/**
		 * ���ܵ�ǰ�ڵ��Լ��ӽڵ�
		 */
		ALL,
		/**
		 * ���ܵ�ǰ�����ų��ӽڵ�
		 */
		NO_CHILDREN,
		/**
		 * �����ܵ�ǰ�ڵ㣬���ǽ����ӽڵ�
		 */
		ONLY_CHILDREN
	}

	/**
	 * �������νṹ�Ľڵ㡣
	 * 
	 * ���ļ���˵����
	 * 
	 * <pre>
	 * ��������������һ�����νṹ��
	 * 
	 *                    (a)          (h)
	 *                   / | \        / | \
	 *                  /  |  \      /  |  \
	 *                (b) (c) (d)  (i) (j) (k)
	 *               / | \            / | \
	 *              /  |  \          /  |  \
	 *            (e) (f) (g)      (l) (m) (n)
	 * 
	 * �����á�L������level������A������absolute������R������relative����
	 * 
	 * 1. �����ѯ���ڵ㣨�����нڵ㣩��
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
	 * 2. �����ѯ�ڵ�(a)��
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
	 * 3. �����ѯ�ڵ�(j)��
	 *                                                          A   R
	 * 
	 *                (j)                             ----  L   2   0
	 *               / | \
	 *              /  |  \
	 *            (l) (m) (n)                         ----  L   3   1
	 * 
	 * 4. �����ѯ�ڵ�(g)��
	 *                                                          A   R
	 * 
	 *                (g)                             ----  L   3   0
	 * </pre>
	 * 
	 * @param item
	 *            �ڵ�Ԫ��
	 * @param absoluteLevel
	 *            ���Լ��Σ���1��ʼ
	 * @param relativeLevel
	 *            ��Լ��Σ���0��ʼ
	 * @return ���ؽ��̶ܳȣ����߷���null��ʾ������
	 */
	public Acception accept(TItem item, int absoluteLevel, int relativeLevel);

	/**
	 * ֻ���ܸ���㼰��һ���ӽ��Ĺ�����
	 */
	@SuppressWarnings("unchecked")
	public static final TreeNodeFilter FIRST_LEVEL = new TreeNodeFilter() {
		public TreeNodeFilter.Acception accept(Object item, int absoluteLevel,
				int relativeLevel) {
			if (relativeLevel == 0) { // ���0����ʾ��ѯ���ĸ����
				return Acception.ALL; // ���ܱ���㼰���ӽ��
			} else if (relativeLevel == 1) { // ���1����ʾ��ѯ���ĸ����ĵ�һ���ӽ��
				return Acception.NO_CHILDREN; // ֻ���ܱ���㣨ָitem�������������ӽ��
			} else {
				return null; // �Ȳ����ܱ���㣬Ҳ���������ӽ��
			}
		}
	};
}
