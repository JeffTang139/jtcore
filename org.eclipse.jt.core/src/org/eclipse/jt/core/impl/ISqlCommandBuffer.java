package org.eclipse.jt.core.impl;

import java.util.List;

public interface ISqlCommandBuffer {
	public String build(List<ParameterReserver> reservers);
}
