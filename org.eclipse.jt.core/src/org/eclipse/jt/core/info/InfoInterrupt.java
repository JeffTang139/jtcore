package org.eclipse.jt.core.info;

public final class InfoInterrupt extends RuntimeException {
	private static final long serialVersionUID = -5784417114707086781L;
	public final Info info;

	public InfoInterrupt(Info info) {
		super("–≈œ¢÷–∂œ");
		this.info = info;
	}
}
