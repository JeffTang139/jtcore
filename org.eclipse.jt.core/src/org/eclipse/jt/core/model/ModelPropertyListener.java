package org.eclipse.jt.core.model;

import java.util.List;

import org.eclipse.jt.core.Context;


public interface ModelPropertyListener<TPropertyData> {
	public void PropertyChanged(Context context, ModelMonitor monitor,
	        List<TPropertyData> changes);
}
