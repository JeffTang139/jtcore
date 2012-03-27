package org.eclipse.jt.core.spi.da;

import org.eclipse.jt.core.exception.NullArgumentException;

/**
 * dnaSql«Î«Ûª˘¿‡
 * 
 * @author Jeff Tang
 * 
 */
public abstract class NewByDnaSql {
	public final String dnaSql;

	NewByDnaSql(String dnaSql) {
		if (dnaSql == null || dnaSql.length() == 0) {
			throw new NullArgumentException("dnaSql");
		}
		this.dnaSql = dnaSql;

	}
}
