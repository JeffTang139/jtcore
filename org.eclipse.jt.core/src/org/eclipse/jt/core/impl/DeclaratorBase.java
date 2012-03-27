package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.ObjectQuerier;
import org.eclipse.jt.core.def.NamedDefine;
import org.eclipse.jt.core.spi.publish.Bundleable;

/**
 * ����������
 * 
 * @author Jeff Tang
 * 
 * @param <TDefine>
 */
public abstract class DeclaratorBase implements Bundleable {
	/**
	 * ����׶��޷�������ö��޷���ɵ�����,�ڸ÷���������
	 * 
	 * @param querier
	 *            ����������
	 */
	protected void declareUseRef(ObjectQuerier querier) {
		// do nothing
	}

	public abstract NamedDefine getDefine();

	/**
	 * ������߼���ܷ����ʵ����
	 */
	static ContextImpl<?, ?, ?> newInstanceByCore;

	/**
	 * ����Bundle;
	 */
	BundleStub bundle;

	public final BundleStub getBundle() {
		return this.bundle;
	}

	public DeclaratorBase(boolean cleanByCoreTag) {
		if (newInstanceByCore == null) {
			throw new UnsupportedOperationException("����������ֻ������ܴ�������֧�ֶ�������:"
					+ this.getClass().getName());
		} else if (cleanByCoreTag) {
			newInstanceByCore = null;
		}
	}

	protected abstract Class<?>[] getDefineIntfRegClasses();

	private boolean refDeclared;

	final boolean tryDeclareUseRef(ObjectQuerier querier) {
		if (!this.refDeclared) {
			this.declareUseRef(querier);
			return this.refDeclared = true;
		} else {
			return false;
		}
	}

}
