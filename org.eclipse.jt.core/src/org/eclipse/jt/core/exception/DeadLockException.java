package org.eclipse.jt.core.exception;

/**
 * 死锁错误，当框架在调用模块或者分配资源时检测到会造成死锁的操作时，会抛出该异常，以便程序不至于无法继续运行
 * 
 * @author Jeff Tang
 * 
 */
public class DeadLockException extends CoreException {
	private static final long serialVersionUID = -3909785566666101028L;
}
