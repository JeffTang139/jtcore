package org.eclipse.jt.core.impl;

interface SQLVisitable {
	public <T> void accept(T visitorContext, SQLVisitor<T> visitor);
}
