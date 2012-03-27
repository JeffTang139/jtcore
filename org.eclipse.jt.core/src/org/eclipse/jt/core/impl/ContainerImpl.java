package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.ModifiableContainer;
import org.eclipse.jt.core.misc.SafeItrList;

/**
 * ÈÝÆ÷ÊµÏÖÀà
 * 
 * @author Jeff Tang
 * 
 * @param <TDefine>
 */
class ContainerImpl<TDefine> extends SafeItrList<TDefine> implements
		ModifiableContainer<TDefine> {

	private static final long serialVersionUID = 1L;

	/**
	 * ÈÝÆ÷¼àÌýÆ÷
	 */
	final ContainerListener listener;

	public ContainerImpl(ContainerListener listener) {
		super(0);
		this.listener = listener;
	}

	public ContainerImpl() {
		super(0);
		this.listener = null;
	}

	public final void move(int from, int to) {
		if (this.listener != null) {
			this.listener.beforeMoving(this, from, to);
		}
		if (to < 0 || this.size() <= to) {
			throw new IndexOutOfBoundsException("Index: " + to + ", Size: "
					+ this.size());
		}
		if (to < from) {
			TDefine save = this.get(from);
			for (int i = to;; i++) {
				if (i < from) {
					TDefine save2 = super.get(i);
					super.set(i, save);
					save = save2;
				} else {
					super.set(i, save);
					break;
				}
			}
		} else if (from < to) {
			TDefine save = this.get(from);
			for (int i = to;; i--) {
				if (i > from) {
					TDefine save2 = super.get(i);
					super.set(i, save);
					save = save2;
				} else {
					super.set(i, save);
					break;
				}
			}
		}
	}

	@Override
	public TDefine remove(int index) {
		if (this.listener != null) {
			this.listener.beforeRemoving(this, index);
		}
		return super.remove(index);
	}

	@Override
	public boolean remove(Object o) {
		if (this.listener != null) {
			this.listener.beforeRemoving(this, o);
		}
		return super.remove(o);
	}

	@Override
	public void clear() {
		if (this.listener != null) {
			this.listener.beforeClearing(this);
		}
		super.clear();
	}
}
