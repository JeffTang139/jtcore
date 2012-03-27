package org.eclipse.jt.core.misc;

/**
 * ��������
 * 
 * @author Jeff Tang
 * 
 * @param <TObject>
 */
public interface ObjectBuilder<TObject> {
	public TObject build() throws Throwable;
}
