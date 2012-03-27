/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File AllDeserializer.java
 * Date 2009-3-12
 */
package org.eclipse.jt.core.impl;

import java.io.IOException;

import org.eclipse.jt.core.type.GUID;


/**
 * �ڲ������л�����
 * 
 * @author Jeff Tang
 * @version 1.0
 */
interface InternalDeserializer {
    /**
     * ��ȡһ������ֵ��
     * 
     * @return ����ֵ��
     * @throws IOException
     *             �����κζ�д�쳣��
     */
    boolean readBoolean() throws IOException;

    /**
     * ��ȡһ���ֽ�ֵ��
     * 
     * @return �ֽ�ֵ��
     * @throws IOException
     *             �����κζ�д�쳣��
     */
    byte readByte() throws IOException;

    /**
     * ��ȡһ���ַ�ֵ��
     * 
     * @return �ַ�ֵ��
     * @throws IOException
     *             �����κζ�д�쳣��
     */
    char readChar() throws IOException;

    /**
     * ��ȡһ��˫���ȸ�����ֵ��
     * 
     * @return ˫���ȸ�����ֵ��
     * @throws IOException
     *             �����κζ�д�쳣��
     */
    double readDouble() throws IOException;

    /**
     * ��ȡһ�������ȸ�����ֵ��
     * 
     * @return �����ȸ�����ֵ��
     * @throws IOException
     *             �����κζ�д�쳣��
     */
    float readFloat() throws IOException;

    /**
     * ��ȡһ������ֵ��
     * 
     * @return ����ֵ��
     * @throws IOException
     *             �����κζ�д�쳣��
     */
    int readInt() throws IOException;

    /**
     * ��ȡһ����������ֵ��
     * 
     * @return ��������ֵ��
     * @throws IOException
     *             �����κζ�д�쳣��
     */
    long readLong() throws IOException;

    /**
     * ��ȡһ����������ֵ��
     * 
     * @return ��������ֵ��
     * @throws IOException
     *             �����κζ�д�쳣��
     */
    short readShort() throws IOException;

    /**
     * ��ȡһ���ַ�������
     * 
     * @return �ַ�������
     * @throws IOException
     *             �����κζ�д�쳣��
     * @throws StructDefineNotFoundException
     *             �Ҳ������ʵĽṹ���塣
     */
    String readString() throws IOException, StructDefineNotFoundException;

    /**
     * ��ȡһ��ȫ��Ψһ��ʶ������
     * 
     * @return ȫ��Ψһ��ʶ������
     * @throws IOException
     *             �����κζ�д�쳣��
     * @throws StructDefineNotFoundException
     *             �Ҳ������ʵĽṹ���塣
     */
    GUID readGUID() throws IOException, StructDefineNotFoundException;

    /**
     * ��ȡһ��ö��ֵ��
     * 
     * @return ö��ֵ��
     * @throws IOException
     *             �����κζ�д�쳣��
     * @throws StructDefineNotFoundException
     *             �Ҳ������ʵĽṹ���塣
     */
    Enum<?> readEnum() throws IOException, StructDefineNotFoundException;

    // /**
    // * ��ȡһ���������
    // *
    // * @return �������
    // * @throws IOException
    // * �����κζ�д�쳣��
    // * @throws StructDefineNotFoundException
    // * �Ҳ������ʵĽṹ���塣
    // */
    // Object readArray() throws IOException, StructDefineNotFoundException;

    /**
     * ��ȡһ������
     * 
     * @return �����Ķ���
     * @throws IOException
     *             �����κζ�д�쳣��
     * @throws StructDefineNotFoundException
     *             �����κζ�д�쳣��
     */
    Object readObject() throws IOException, StructDefineNotFoundException;

    void readFully(byte[] bytes) throws IOException;

    // //////////////////////////////////////////
    // ���·����д����ġ�

    /**
     * ���ݷ����л����м�¼�ĸն����Ķ���ľ����
     * 
     * @return �����л����м�¼�ĸն����Ķ���ľ����
     */
    int backupHandle();

    /**
     * ��ԭ�����л����м�¼�ĸս����Ķ���ľ����
     * 
     * @param backupHandle
     *            ��ǰ���ݵķ����л����м�¼�ĸն����Ķ���ľ����
     */
    void restoreHandle(int backupHandle);
}
