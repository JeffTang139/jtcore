package org.eclipse.jt.core.impl;

/**
 * 指明可由声明器构造
 * 
 * @author Jeff Tang
 * 
 */
public interface Declarative<TDeclarator extends DeclaratorBase> {
	/**
	 * 返回所对应的声明器，可能返回空
	 */
	public TDeclarator getDeclarator();
}
