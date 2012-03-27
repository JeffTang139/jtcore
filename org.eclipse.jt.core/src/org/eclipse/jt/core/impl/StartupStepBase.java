package org.eclipse.jt.core.impl;

abstract class StartupStepBase<TTarget extends StartupEntry> implements
		StartupStep<TTarget> {

	private final String description;
	private final int priority;

	StartupStepBase(int priority, String description) {
		this.priority = priority;
		this.description = description;
	}

	StartupStepBase(StartupStepBase<?> previous, int pd, String description) {
		this.priority = previous.priority + pd;
		this.description = description;
	}

	public final String getDescription() {
		return this.description;
	}

	public final int getPriority() {
		return this.priority;
	}

	public abstract StartupStep<TTarget> doStep(ResolveHelper helper,
			TTarget target) throws Throwable;

}
