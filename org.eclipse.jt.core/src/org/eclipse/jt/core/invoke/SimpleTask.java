package org.eclipse.jt.core.invoke;

import org.eclipse.jt.core.None;

/**
 * �����񣬼�ֻ��һ�ִ�����������
 * 
 * @author Jeff Tang
 * 
 */
public abstract class SimpleTask extends Task<None> {
	public SimpleTask() {
		this.method = None.NONE;
	}
}
