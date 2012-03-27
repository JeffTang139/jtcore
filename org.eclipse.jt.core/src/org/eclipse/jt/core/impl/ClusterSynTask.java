package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.obja.StructClass;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.invoke.SimpleTask;

@StructClass
abstract class ClusterSynTask extends SimpleTask {

	protected ClusterSynTask() {
		this.synState = ClusterSynTask.State.UNHANDLE;
	}

	final void reset() {
		this.synState = ClusterSynTask.State.UNHANDLE;
	}

	final ClusterSynTask.State getClusterTaskState() {
		return this.synState;
	}

	final void setState(final State state) {
		if (state == null) {
			throw new NullArgumentException("state");
		}
		this.synState = state;
	}

	private State synState;

	static enum State {

		UNHANDLE,

		HANDLE_SUCCESSED,

		HANDLE_FAILED

	}

}
