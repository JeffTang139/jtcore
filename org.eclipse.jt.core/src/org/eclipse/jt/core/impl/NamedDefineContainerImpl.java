package org.eclipse.jt.core.impl;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jt.core.def.MissingDefineException;
import org.eclipse.jt.core.def.ModifiableNamedElementContainer;
import org.eclipse.jt.core.exception.NamedDefineExistingException;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.misc.SXElement;


/**
 * 有名字的定义得容器的实现类
 * 
 * @author Jeff Tang
 * 
 * @param <TDefine>
 */
public class NamedDefineContainerImpl<TDefine extends NamedDefineImpl> extends
		MetaBaseContainerImpl<TDefine> implements
		ModifiableNamedElementContainer<TDefine> {

	private static final long serialVersionUID = 8003688456795448671L;

	NamedDefineContainerImpl() {
		super();
	}

	NamedDefineContainerImpl(ContainerListener listener) {
		super(listener);
	}

	private final StringKeyMap<TDefine> map = new StringKeyMap<TDefine>();

	@Override
	public boolean add(TDefine define) {
		this.map.put(define.name, define, true);
		return super.add(define);
	}

	@Override
	public void add(int index, TDefine define) {
		this.map.put(define.name, define, true);
		super.add(index, define);
	}

	@Override
	public void addAll(TDefine[] defines) {
		for (TDefine define : defines) {
			this.validate(define);
		}
		super.addAll(defines);
		for (TDefine define : defines) {
			this.map.put(define.name, define, false);
		}
	}

	@Override
	public boolean addAll(Collection<? extends TDefine> c) {
		for (TDefine define : c) {
			this.validate(define);
		}
		boolean r = super.addAll(c);
		for (TDefine define : c) {
			this.map.put(define.name, define, false);
		}
		return r;
	}

	@Override
	public boolean addAll(int index, Collection<? extends TDefine> c) {
		for (TDefine define : c) {
			this.validate(define);
		}
		boolean r = super.addAll(index, c);
		for (TDefine define : c) {
			this.map.put(define.name, define, false);
		}
		return r;
	}

	@Override
	public TDefine set(int index, TDefine define) {
		if (define == null) {
			throw new NullArgumentException("命名定义");
		}
		TDefine old = super.set(index, define);
		if (old != define) {
			this.map.remove(old.name, true);
			try {
				this.map.put(define.name, define, true);
			} catch (NamedDefineExistingException e) {
				super.set(index, old);
				this.map.put(old.name, old);
				throw e;
			}
		}
		return old;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(Object o) {
		if (o == null) {
			throw new NullArgumentException("命名定义");
		}
		if (super.remove(o)) {
			this.map.remove(((TDefine) o).name);
			return true;
		}
		return false;
	}

	@Override
	public TDefine remove(int index) {
		TDefine define = super.remove(index);
		this.map.remove(define.name);
		return define;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean modified = false;
		Iterator<TDefine> e = super.removableIterator();
		while (e.hasNext()) {
			TDefine d = e.next();
			if (c.contains(d)) {
				e.remove();
				this.map.remove(d.name);
				modified = true;
			}
		}
		return modified;
	}

	@Override
	protected void removeRange(int fromIndex, int toIndex) {
		for (int i = fromIndex; i <= toIndex; i++) {
			this.map.remove(this.get(i).name);
		}
		super.removeRange(fromIndex, toIndex);
	}

	@Override
	public void clear() {
		super.clear();
		this.map.clear();
	}

	public final TDefine find(String name) {
		if (name == null) {
			throw new NullArgumentException("名称");
		}
		if (this.size() == 0) {
			return null;
		}
		return this.map.get(name);
	}

	public final TDefine get(String name) throws MissingDefineException {
		TDefine define = this.find(name);
		if (define == null) {
			throw new MissingDefineException("无法找到名称为[" + name + "]的元数据");
		}
		return define;
	}

	final TDefine find(SXElement element, String nameAttr) {
		String name = element.getAttribute(nameAttr, null);
		if (name == null) {
			return null;
		}
		return this.find(name);
	}

	final TDefine get(SXElement element, String nameAttr)
			throws MissingDefineException {
		return this.get(element.getAttribute(nameAttr, null));
	}

	final TDefine get(TDefine sample) throws MissingDefineException {
		return this.get(sample.name);
	}

	final int indexOfName(String name) {
		if (name == null) {
			throw new NullPointerException();
		}
		for (int i = 0, c = this.size(); i < c; i++) {
			TDefine define = this.get(i);
			if (name.equals(define.name)) {
				return i;
			}
		}
		return -1;
	}

	final int indexOfName(String nameIn, int nameStart, int nameLen) {
		if (nameIn == null) {
			throw new NullPointerException();
		}
		for (int i = 0, c = this.size(); i < c; i++) {
			String name = this.get(i).name;
			if (name.length() == nameLen
					&& nameIn.regionMatches(nameStart, name, 0, nameLen)) {
				return i;
			}
		}
		return -1;
	}

	@SuppressWarnings("unchecked")
	@Override
	public final boolean contains(Object o) {
		try {
			return this.contains((TDefine) o);
		} catch (ClassCastException e) {
			return false;
		}
	}

	final boolean contains(String name) {
		return this.map.containsKey(name);
	}

	final boolean contains(TDefine define) {
		return this.map.containsKey(define.name);
	}

	@Deprecated
	final void validate(String name) {
		if (this.map.containsKey(name)) {
			throw new NamedDefineExistingException("名称为[" + name + "]的元素已经存在.");
		}
	}

	final void validate(TDefine define) {
		if (define == null) {
			throw new NullArgumentException("定义");
		}
		if (this.map.containsKey(define.name)) {
			throw new NamedDefineExistingException(define);
		}
	}

	final void mapPut(TDefine define) {
		if (define == null) {
			throw new NullPointerException();
		}
		final String name = define.getName();
		this.map.put(name, define, true);
	}

	@SuppressWarnings("unchecked")
	final void mapPut(Object obj) {
		if (obj == null) {
			throw new NullPointerException();
		}
		this.mapPut((TDefine) obj);
	}

	final void mapRemove(TDefine define) {
		if (define == null) {
			return;
		}
		this.map.remove(define.getName(), true);
	}

	@SuppressWarnings("unchecked")
	final void mapRemove(Object obj) {
		if (obj == null) {
			return;
		}
		this.mapRemove((TDefine) obj);
	}

}
