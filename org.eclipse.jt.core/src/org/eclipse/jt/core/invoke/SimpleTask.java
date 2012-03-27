package org.eclipse.jt.core.invoke;

import org.eclipse.jt.core.None;

/**
 * 简单任务，即只有一种处理方法的任务
 * 
 * @author Jeff Tang
 * 
 */
public abstract class SimpleTask extends Task<None> {
	public SimpleTask() {
		this.method = None.NONE;
	}
}
