package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.ObjectQuerier;
import org.eclipse.jt.core.def.NamedDefine;
import org.eclipse.jt.core.spi.publish.Bundleable;

/**
 * 定义声明器
 * 
 * @author Jeff Tang
 * 
 * @param <TDefine>
 */
public abstract class DeclaratorBase implements Bundleable {
	/**
	 * 构造阶段无法获得引用而无法完成的声明,在该方法中声明
	 * 
	 * @param querier
	 *            对象请求器
	 */
	protected void declareUseRef(ObjectQuerier querier) {
		// do nothing
	}

	public abstract NamedDefine getDefine();

	/**
	 * 标记是逻辑框架发起的实力化
	 */
	static ContextImpl<?, ?, ?> newInstanceByCore;

	/**
	 * 所属Bundle;
	 */
	BundleStub bundle;

	public final BundleStub getBundle() {
		return this.bundle;
	}

	public DeclaratorBase(boolean cleanByCoreTag) {
		if (newInstanceByCore == null) {
			throw new UnsupportedOperationException("定义声明器只允许被框架创建，不支持独立创建:"
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
