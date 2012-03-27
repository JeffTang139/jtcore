package org.eclipse.jt.core;

public final class EmptyStringFilter implements Filter<String> {

	public static final EmptyStringFilter INSTANCE = new EmptyStringFilter();

	private EmptyStringFilter() {
	}

	public final boolean accept(String item) {
		return false;
	}

}
