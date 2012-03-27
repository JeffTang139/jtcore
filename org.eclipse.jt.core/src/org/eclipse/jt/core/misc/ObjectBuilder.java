package org.eclipse.jt.core.misc;

/**
 * 对象构造借口
 * 
 * @author Jeff Tang
 * 
 * @param <TObject>
 */
public interface ObjectBuilder<TObject> {
	public TObject build() throws Throwable;
}
