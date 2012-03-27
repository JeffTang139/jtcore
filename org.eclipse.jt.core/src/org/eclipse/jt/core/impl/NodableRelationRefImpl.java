package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 作为树节点的关系引用定义
 * 
 * @author Jeff Tang
 * 
 * @param <TRelation>
 *            目标关系类型
 * @param <TLink>
 *            自身及链表类型
 * @param <TJoin>
 *            子节点即连接类型
 * @param <TBase>
 *            树节点基类,迭代类型
 */
abstract class NodableRelationRefImpl<TRelation extends Relation, TLink extends NodableRelationRef, TJoin extends JoinedRelationRef, TBase extends NodableRelationRef>
		extends RelationRefImpl<TRelation> implements NodableRelationRef,
		Iterable<TBase> {

	NodableRelationRefImpl(String name, TRelation target) {
		super(name, target);
	}

	private TLink next;

	// how to comment more efficient?

	public final TLink next() {
		return this.next;
	}

	@SuppressWarnings("unchecked")
	public final TLink last() {
		for (TLink current = (TLink) this, next = (TLink) current.next();;) {
			if (next == null) {
				return current;
			} else {
				current = next;
				next = (TLink) next.next();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public final void setNext(NodableRelationRef next) {
		if (this.next != null) {
			throw new IllegalArgumentException("当前元素不是队尾元素");
		}
		try {
			this.next = (TLink) next;
			this.increaseModCount();
		} catch (ClassCastException e) {
			throw Utils.tryThrowException(e);
		}
	}

	/**
	 * 连接链表
	 */
	private TJoin joins;

	public final TJoin getJoins() {
		return this.joins;
	}

	final void addJoinNoCheck(TJoin join) {
		this.increaseModCount();
		if (this.joins == null) {
			this.joins = join;
		} else {
			JoinedRelationRef last = this.joins.last();
			last.setNext(join);
		}
	}

	@SuppressWarnings("unchecked")
	public final Iterator<TBase> iterator() {
		return new PreOrderTraverseItr<TBase>((TBase) this);
	}

	@SuppressWarnings("serial")
	private static final class PreOrderTraverseItr<E extends NodableRelationRef>
			extends ArrayList<E> implements Iterator<E> {

		private final int expectedModCount;

		private final E root;

		PreOrderTraverseItr(E root) {
			this.root = root;
			this.expectedModCount = root.modCount();
			this.next = root;
		}

		private E next;

		public final boolean hasNext() {
			return this.next != null;
		}

		@SuppressWarnings("unchecked")
		public final E next() {
			if (this.root.modCount() != this.expectedModCount) {
				throw new ConcurrentModificationException();
			}
			E current = this.next;
			if (current == null) {
				throw new NoSuchElementException();
			}
			E node = (E) current.getJoins();
			// 当前节点有子节点且有兄弟兄弟时,节点入栈,递归子节点
			// 当前节点有子节点且无兄弟节点时,直接递归子节点
			if (node != null) {
				this.next = node;
				if (current.next() != null) {
					this.add(current);
				}
			} else {
				// 当前节点无子节点,则尝试递归兄弟节点
				node = (E) current.next();
				if (node != null) {
					// 兄弟节点不为空,递归兄弟节点
					this.next = node;
				} else {
					// 兄弟节点为空,递归栈顶的节点,栈空则整个递归结束
					if (this.isEmpty()) {
						this.next = null;
					} else {
						this.next = (E) this.pop().next();
					}
				}
			}
			return current;
		}

		private final E pop() {
			int last = this.size() - 1;
			// 外层使用先判断了isEmpty(),这里必定有值
			E node = this.get(last);
			this.remove(last);
			return node;
		}

		public final void remove() {
			throw new UnsupportedOperationException();
		}

	}

}
