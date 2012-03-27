package org.eclipse.jt.core.spi.application;

import org.eclipse.jt.core.impl.Activator;

/**
 * 应用工具类
 * 
 * @author Jeff Tang
 * 
 */
@Deprecated
public final class AppUtil {
	public static Application getDefaultApp() {
		return Activator.getDefaultApp();
	}

	@Deprecated
	public static void initTypes() {
	}

	private AppUtil() {

	}
}
