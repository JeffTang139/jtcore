package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.obja.StructClass;
import org.eclipse.jt.core.invoke.SimpleTask;

@StructClass
final class NClusterNodeJoinTask extends SimpleTask {

	NClusterNodeJoinTask(final int nodeIndex) {
		NetClusterImpl.checkNetNodeIndex(nodeIndex);
		this.nodeIndex = nodeIndex;
	}

	final int nodeIndex;

}
