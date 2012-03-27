package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.NamedDeclare;
import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.misc.SXMergeHelper;
import org.eclipse.jt.core.type.Digester;

/**
 * 含名称定义的基础类
 * 
 * @author Jeff Tang
 * 
 */
abstract class NamedDefineImpl extends DefineBaseImpl implements NamedDeclare {

	public final String getName() {
		return this.name;
	}

	public final String getTitle() {
		return this.title;
	}

	public final void setTitle(String title) {
		this.checkModifiable();
		this.title = Utils.noneNull(title);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[name:").append(this.name);
		if (!this.title.equals(this.name)) {
			sb.append(",title:").append(this.title);
		}
		if (this instanceof Declarative<?>) {
			DeclaratorBase db = ((Declarative<?>) this).getDeclarator();
			if (db != null) {
				sb.append(",declarator:").append(db.getClass().getName());
				if (db.bundle != null) {
					sb.append(",bundle:").append(db.bundle.name);
				}
			}
		}
		return sb.append(']').toString();
	}

	static final ExistingDetector<NamedDefineContainerImpl<? extends NamedDefineImpl>, NamedDefineImpl, String> defineDetector = new ExistingDetector<NamedDefineContainerImpl<? extends NamedDefineImpl>, NamedDefineImpl, String>() {

		public final boolean exists(
				NamedDefineContainerImpl<? extends NamedDefineImpl> container,
				String name, NamedDefineImpl ignore) {
			final NamedDefineImpl exists = container.find(name);
			return exists != null && (ignore == null || exists != ignore);
		}
	};

	static final ExistingDetector<NamedDefineContainerImpl<? extends NamedDefineImpl>, String, String> nameDetector = new ExistingDetector<NamedDefineContainerImpl<? extends NamedDefineImpl>, String, String>() {

		public boolean exists(
				NamedDefineContainerImpl<? extends NamedDefineImpl> container,
				String key, String ignore) {
			return !key.equals(ignore) && container.contains(key);
		}
	};

	static final String xml_attr_name = "name";
	static final String xml_attr_title = "title";

	/**
	 * 名称
	 */
	public final String name;
	/**
	 * 标题
	 */
	protected String title;

	public NamedDefineImpl(String name) {
		if (name == null || name.length() == 0) {
			throw new NullPointerException();
		}
		this.name = name;
		this.title = "";
	}

	NamedDefineImpl(NamedDefineImpl sample) {
		super(sample);
		this.name = sample.name;
		this.title = sample.title;
	}

	NamedDefineImpl(SXElement element) {
		super(element);
		this.name = element.getAttribute(xml_attr_name);
		if (this.name == null) {
			throw new NullPointerException();
		}
		this.title = element.getAttribute(xml_attr_title, this.title);
	}

	final void digestAuthAndName(Digester digester) {
		digester.update(this.name);
	}

	@Override
	public void render(SXElement element) {
		NamedDefineImpl.render(this, element);
	}

	static final void render(NamedDefineImpl define, SXElement element) {
		element.setAttribute(xml_attr_name, define.name);
		if (define.title != null && define.title.length() > 0) {
			element.setAttribute(xml_attr_title, define.title);
		}
		DefineBaseImpl.render(define, element);
	}

	@Override
	void merge(SXElement element, SXMergeHelper helper) {
		super.merge(element, helper);
		this.title = element.getAttribute(xml_attr_title, this.title);
	}

	static final void merge(NamedDefineImpl define, SXElement element) {
		define.title = element.getAttribute(xml_attr_title, define.title);
		DefineBaseImpl.merge(define, element);
	}

	@Override
	void assignFrom(Object sample) {
		super.assignFrom(sample);
		this.title = ((NamedDefineImpl) sample).title;
	}

}
