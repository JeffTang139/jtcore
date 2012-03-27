/**
 * Copyright (C) 2007-2008 JeffTang Software Co., Ltd. All rights reserved.
 *
 * File IStruct.java
 * Date 2008-7-3
 */
package org.eclipse.jt.core.impl;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public interface IStruct {
	/**
	 * �����µ�ʵ�������ñ�ʵ�������ݳ�ʼ���µ�ʵ���� ��Ҫ����������ǣ��޸���ʵ�����κ����Զ�����Ӱ�쵽��ʵ�������ݡ�
	 * 
	 * @return �´������Ѿ���ʼ����ʵ��
	 */
	Object cloneInstance();

	/**
	 * ��ָ����ʵ���и������ݣ�״̬���� ���ƵĽ�����ܱ�֤���޸Ĳ��븴�Ƶ��κ�һ�������ݣ�����Ӱ����һ�������ݡ�
	 * 
	 * @param src
	 */
	void copyFrom(Object src);
}
