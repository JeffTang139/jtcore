/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File AllSerializer.java
 * Date 2009-3-12
 */
package org.eclipse.jt.core.impl;

import java.io.IOException;

import org.eclipse.jt.core.type.GUID;


/**
 * �ڲ����л�����
 * 
 * @author Jeff Tang
 * @version 1.0
 */
interface InternalSerializer {
    /**
     * д��һ������ֵ��
     * 
     * @param v
     *            Ҫд��Ĳ���ֵ��
     * @throws IOException
     *             �����κζ�д�쳣��
     */
    void writeBoolean(boolean v) throws IOException;

    /**
     * д��һ���ֽ�ֵ��
     * 
     * @param v
     *            Ҫд����ֽ�ֵ��
     * @throws IOException
     *             �����κζ�д�쳣��
     */
    void writeByte(byte v) throws IOException;

    /**
     * д��һ���ַ�ֵ��
     * 
     * @param v
     *            Ҫд����ַ�ֵ��
     * @throws IOException
     *             �����κζ�д�쳣��
     */
    void writeChar(char v) throws IOException;

    /**
     * д��һ��˫���ȸ�����ֵ��
     * 
     * @param v
     *            Ҫд���˫���ȸ�����ֵ��
     * @throws IOException
     *             �����κζ�д�쳣��
     */
    void writeDouble(double v) throws IOException;

    /**
     * д��һ�������ȸ�����ֵ��
     * 
     * @param v
     *            Ҫд��ĵ����ȸ�����ֵ��
     * @throws IOException
     *             �����κζ�д�쳣��
     */
    void writeFloat(float v) throws IOException;

    /**
     * д��һ������ֵ��
     * 
     * @param v
     *            Ҫд�������ֵ��
     * @throws IOException
     *             �����κζ�д�쳣��
     */
    void writeInt(int v) throws IOException;

    /**
     * д��һ����������ֵ��
     * 
     * @param v
     *            Ҫд��ĳ�������ֵ��
     * @throws IOException
     *             �����κζ�д�쳣��
     */
    void writeLong(long v) throws IOException;

    /**
     * д��һ����������ֵ��
     * 
     * @param v
     *            Ҫд��Ķ�������ֵ��
     * @throws IOException
     *             �����κζ�д�쳣��
     */
    void writeShort(short v) throws IOException;

    /**
     * д��һ���ֽ����С�
     * 
     * @param bytes
     *            Ҫд����ֽ����С�
     * @throws IOException
     *             �����κζ�д�쳣��
     */
    void writeBytes(byte[] bytes) throws IOException;

    /**
     * д��һ���ַ�������
     * 
     * @param str
     *            Ҫд����ַ�������
     * @throws IOException
     *             �����κζ�д�쳣��
     */
    void writeString(String str) throws IOException;

    /**
     * д��һ��ȫ��Ψһ��ʶ������
     * 
     * @param guid
     *            Ҫд���ȫ��Ψһ��ʶ������
     * @throws IOException
     *             �����κζ�д�쳣��
     */
    void writeGUID(GUID guid) throws IOException;

    /**
     * д��һ������ʵ����
     * 
     * @param clazz
     *            Ҫд�������ʵ����
     * @throws IOException
     *             �����κζ�д�쳣��
     */
    void writeClass(Class<?> clazz) throws IOException;

    /**
     * д��һ��ö��ֵ��
     * 
     * @param str
     *            Ҫд���ö��ֵ��
     * @throws IOException
     *             �����κζ�д�쳣��
     */
    void writeEnum(Enum<?> en) throws IOException;

    /**
     * д��һ������
     * 
     * @param array
     *            Ҫд��Ķ���
     * @throws IOException
     *             �����κζ�д�쳣��
     * @throws StructDefineNotFoundException
     *             �Ҳ������ʵĽṹ���塣
     */
    void writeObject(Object obj) throws IOException,
            StructDefineNotFoundException;

    // ��Ҫ����
    void writeSpecialObject(ObjectDataTypeInternal assigner, Object specialObj)
            throws IOException, StructDefineNotFoundException;
}
