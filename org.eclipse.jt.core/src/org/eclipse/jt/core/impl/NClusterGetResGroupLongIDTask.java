package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.obja.StructClass;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.type.GUID;


@StructClass
public class NClusterGetResGroupLongIDTask extends ClusterSynTask {

	public NClusterGetResGroupLongIDTask(final GUID resourceGroupGUID) {
		if (resourceGroupGUID == null) {
			throw new NullArgumentException("resourceGroupGUID");
		}
		this.resourceGroupGUID = resourceGroupGUID;
	}

	final long getResourceGroupLongID() {
		return this.resourceGroupLongID;
	}

	final GUID resourceGroupGUID;

	long resourceGroupLongID;

}
