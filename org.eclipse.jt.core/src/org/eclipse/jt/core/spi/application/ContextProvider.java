package org.eclipse.jt.core.spi.application;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.impl.ContextProviderBase;

/**
 * �����Ļ�ȡ��
 * 
 * @author Jeff Tang
 * 
 */
public final class ContextProvider extends ContextProviderBase {
	public ContextProvider(Context context) {
		super(context);
	}

	/**
	 * ��õ�ǰ������
	 */
	@Override
	public final Context getCurrentContext() throws IllegalStateException {
		return super.getCurrentContext();
	}
}
