package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.obja.StructClass;

@StructClass
final class NClusterTransactionTask extends ClusterSynTask {

	NClusterTransactionTask(final boolean commit) {
		this.commit = commit;
	}

	final boolean isDoCommit() {
		return this.commit;
	}

	final boolean isDoRollback() {
		return !this.commit;
	}

	final void setDoCommit() {
		this.commit = true;
	}

	final void setDoRollback() {
		this.commit = false;
	}

	private boolean commit;

}
