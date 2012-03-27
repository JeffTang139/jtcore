package org.eclipse.jt.core.impl;

public interface ISqlDeleteMultiCommandFactory {

	ISqlDeleteMultiBuffer deleteMulti(String table, String alias);
}
