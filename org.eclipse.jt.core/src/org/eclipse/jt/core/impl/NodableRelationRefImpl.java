package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * ��Ϊ���ڵ�Ĺ�ϵ���ö���
 * 
 * @author Jeff Tang
 * 
 * @param <TRelation>
 *            Ŀ���ϵ����
 * @param <TLink>
 *            ������������
 * @param <TJoin>
 *            �ӽڵ㼴��������
 * @param <TBase>
 *            ���ڵ����,��������
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
			throw new IllegalArgumentException("��ǰԪ�ز��Ƕ�βԪ��");
		}
		try {
			this.next = (TLink) next;
			this.increaseModCount();
		} catch (ClassCastException e) {
			throw Utils.tryThrowException(e);
		}
	}

	/**
	 * ��������
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
			// ��ǰ�ڵ����ӽڵ������ֵ��ֵ�ʱ,�ڵ���ջ,�ݹ��ӽڵ�
			// ��ǰ�ڵ����ӽڵ������ֵܽڵ�ʱ,ֱ�ӵݹ��ӽڵ�
			if (node != null) {
				this.next = node;
				if (current.next() != null) {
					this.add(current);
				}
			} else {
				// ��ǰ�ڵ����ӽڵ�,���Եݹ��ֵܽڵ�
				node = (E) current.next();
				if (node != null) {
					// �ֵܽڵ㲻Ϊ��,�ݹ��ֵܽڵ�
					this.next = node;
				} else {
					// �ֵܽڵ�Ϊ��,�ݹ�ջ���Ľڵ�,ջ���������ݹ����
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
			// ���ʹ�����ж���isEmpty(),����ض���ֵ
			E node = this.get(last);
			this.remove(last);
			return node;
		}

		public final void remove() {
			throw new UnsupportedOperationException();
		}

	}

}
