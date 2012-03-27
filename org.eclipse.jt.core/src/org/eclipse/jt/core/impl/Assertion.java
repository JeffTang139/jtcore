/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File Assertion.java
 * Date 2009-2-26
 */
package org.eclipse.jt.core.impl;

/**
 * ���ԡ���ҪĿ���������й����з���Ǳ�ڵ����⣨�����ڲ��Խ׶���֤�������ȷ���м����������<br/>
 * ����ģ��Java�Ķ��Ի��ơ�<br/>
 * ����֮��������������ʱ����Ҫ����Ӧ�Ŀ��ء�<br/>
 * ����֮�������޷���·һЩ���ܲ�������Ӱ����жϡ�����Ӧ���á�
 * 
 * @author Jeff Tang
 * @version 1.0
 */
// XXX ��ʽ��������ǰ���ɿ��ǽ�ASSERT�����е�����ע�͵���
public final class Assertion {
    private Assertion() {
    }

    /**
     * ������ֵ���ʽ��ֵΪ�棨<code>true</code>����<br/>
     * �����ֵ���ʽ��ֵΪ�棬��ʲô�����ᷢ�������൱û�е��ù��÷�����<br/>
     * �����ֵ���ʽ��ֵΪ�٣����׳����Դ��󣬴�����Ϣ���ǡ����Դ��󡱡�
     * 
     * @param exp
     *            ����Ϊ�����ֵ���ʽ��
     */
    public static void ASSERT(boolean exp) {
        if (!exp) {
            throw new AssertionError("���Դ���");
        }
    }

    /**
     * ������ֵ���ʽ��ֵΪ�棨<code>true</code>����<br/>
     * �����ֵ���ʽ��ֵΪ�棬��ʲô�����ᷢ�������൱û�е��ù��÷�����<br/>
     * �����ֵ���ʽ��ֵΪ�٣����׳����Դ��󣬴�����Ϣ���Ƿ��������и�����ʧ����Ϣ��<br/>
     * ���ʧ����Ϣ��������������ʧ��ʱ�ֳ��������
     * 
     * @param exp
     *            ����Ϊ�����ֵ���ʽ��
     * @param failedMsg
     *            ʧ����Ϣ��������������ʧ��ʱ�ֳ�����������
     */
    public static void ASSERT(boolean exp, String failedMsg) {
        if (!exp) {
            throw new AssertionError(failedMsg);
        }
    }

    /**
     * ������ֵ���ʽ��ֵΪ�棨<code>true</code>����<br/>
     * �����ֵ���ʽ��ֵΪ�棬��ʲô�����ᷢ�������൱û�е��ù��÷�����<br/>
     * �����ֵ���ʽ��ֵΪ�٣����׳����Դ��󣬴�����Ϣ���Ƿ��������и�����ʧ����Ϣ������Я������Ϣ��<br/>
     * ���ʧ����Ϣ��������������ʧ��ʱ�ֳ��������
     * 
     * @param exp
     *            ����Ϊ�����ֵ���ʽ��
     * @param failedMsgObj
     *            ʧ����Ϣ����������������ʧ��ʱ�ֳ����������Ķ���
     */
    public static void ASSERT(boolean exp, Object failedMsgObj) {
        if (!exp) {
            throw new AssertionError(failedMsgObj == null ? "null"
                    : failedMsgObj.toString());
        }
    }
}
