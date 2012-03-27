package org.eclipse.jt.core.impl;

import java.net.URL;

import org.eclipse.jt.core.def.obja.StructClass;


@StructClass
final class NClusterNodeDetectTask extends ClusterSynTask {

	NClusterNodeDetectTask(final URL url) {
		this.url = url;
	}

	final transient URL url;

}
