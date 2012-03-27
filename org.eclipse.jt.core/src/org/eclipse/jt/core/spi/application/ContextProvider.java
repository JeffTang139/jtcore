package org.eclipse.jt.core.spi.application;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.impl.ContextProviderBase;

/**
 * 上下文获取类
 * 
 * @author Jeff Tang
 * 
 */
public final class ContextProvider extends ContextProviderBase {
	public ContextProvider(Context context) {
		super(context);
	}

	/**
	 * 获得当前上下文
	 */
	@Override
	public final Context getCurrentContext() throws IllegalStateException {
		return super.getCurrentContext();
	}
}
