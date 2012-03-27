/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File StructDefineProvider.java
 * Date 2009-3-12
 */
package org.eclipse.jt.core.impl;

/**
 * �ṹ�����ṩ����
 * 
 * @author Jeff Tang
 * @version 1.0
 */
interface StructDefineProvider {
    /**
     * ����ָ���Ľṹ����ժҪ��Ϣ��������Ӧ�Ľṹ�����������
     * 
     * @param structSummary
     *            �ṹ����ժҪ��Ϣ��
     * @return �ṹ�����������
     * @throws StructDefineNotFoundException
     *             �Ҳ�����صĽṹ�������
     */
    StructAdapter getStructDefine(StructSummary structSummary)
            throws StructDefineNotFoundException;
}
