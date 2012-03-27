package org.eclipse.jt.core.spi.publish;


/**
 * 元素源数据
 * 
 * @author Jeff Tang
 * 
 */
public abstract class NamedFactoryElement extends
		org.eclipse.jt.core.impl.NamedFactoryElement {
	public final BundleToken getBundle() {
		return super.bundle;
	}

	public final SpaceToken getSpace() {
		return super.space;
	}

	public final String getName() {
		return super.name;
	}

	public NamedFactoryElement(String name) {
		super(name);
	}
}
