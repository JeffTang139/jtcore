package org.eclipse.jt.core.impl;

import java.util.List;

public interface ISqlBuffer {
	public void writeTo(SqlStringBuffer sql, List<ParameterReserver> args);
}
