package org.eclipse.jt.core.spi.publish;

public abstract class PublishedElement extends
        org.eclipse.jt.core.impl.PublishedElement {
	public final BundleToken getBundle() {
		return super.bundle;
	}

	public final SpaceToken getSpace() {
		return super.space;
	}
}
