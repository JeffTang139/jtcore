package org.eclipse.jt.core.impl;

public abstract class NamedFactoryElement extends PublishedElement {

	protected final String name;

	@Override
	public final String toString() {
		return "ÃüÃûÔªËØ:" + this.name + "[" + this.bundle.name + " : "
		        + this.getClass().getSimpleName() + "]";
	}

	public NamedFactoryElement(String name) {
		if (name == null) {
			throw new NullPointerException();
		}
		this.name = name;
	}

}
