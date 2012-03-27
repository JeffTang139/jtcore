/**
 * Copyright (C) 2007-2008 JeffTang Software Co., Ltd. All rights reserved.
 *
 * File NotImplementedException.java
 * Date 2008-8-22
 */
package org.eclipse.jt.core.impl;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public class NotImplementedException extends RuntimeException {
	private static final long serialVersionUID = 5061515014627633345L;

	public NotImplementedException() {
		super("该功能还未实现.");
	}

	/**
	 * @param message
	 */
	public NotImplementedException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public NotImplementedException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public NotImplementedException(String message, Throwable cause) {
		super(message, cause);
	}

}
