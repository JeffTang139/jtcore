/**
 * Copyright (C) 2007-2008 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File DataDeserializer.java
 * Date 2008-12-1
 */
package org.eclipse.jt.core.impl;

import java.io.IOException;

/**
 * ���ݷ����л�����<br/>
 * ���ӿ��ṩ�ӵײ�����������ж�ȡJava�Ļ����������ݵķ�����
 * 
 * @author Jeff Tang
 * @version 1.0
 */
interface DataDeserializer {
    /**
     * ��ȡһ���ֽڣ��������ֽ�Ϊ<code>0</code>���򷵻�<code>false</code>�����򷵻�<code>true</code>��
     * �÷��������ڶ�ȡ��<code>DataSerializer</code>�ӿڵ�<code>writeBoolean</code>����д����ֽڡ�
     * 
     * @return �����Ĳ�����<code>boolean</code>��ֵ��
     * @throws IOException
     *             �����κζ�д�쳣��
     */
    boolean readBoolean() throws IOException;

    /**
     * ��ȡ������һ���ֽڡ��÷��������ڶ�ȡ��<code>DataSerializer</code>�ӿڵ�<code>writeByte</code>
     * ����д����ֽڡ�
     * 
     * @return �������ֽڡ�
     * @throws IOException
     *             �����κζ�д�쳣��
     */
    byte readByte() throws IOException;

    /**
     * ��ȡһ���ֽڣ�������ǰ�油<code>0</code>ת��Ϊһ������������������������������ֵ�ķ�Χ��<code>0</code>��
     * <code>255</code>֮ǰ��������β��ֵ����
     * 
     * @return ������8λ�޷����ֽ�ֵ��
     * @throws IOException
     *             �����κζ�д�쳣��
     */
    int readUnsignedByte() throws IOException;

    /**
     * ��ȡ������һ��Unicode�ַ����������ֽ���ɣ����÷��������ڶ�ȡ��<code>DataSerializer</code>�ӿڵ�
     * <code>writeChar</code> ����д���Unicode�ַ���
     * 
     * @return ������Unicode�ַ���
     * @throws IOException
     *             �����κζ�д�쳣��
     */
    char readChar() throws IOException;

    /**
     * ��ȡ������һ��˫���ȸ��������ɰ˸��ֽ���ɣ����÷��������ڶ�ȡ��<code>DataSerializer</code>�ӿڵ�
     * <code>writeDouble</code> ����д���˫���ȸ�������
     * 
     * @return ������˫���ȸ�������
     * @throws IOException
     *             �����κζ�д�쳣��
     */
    double readDouble() throws IOException;

    /**
     * ��ȡ������һ�������ȸ����������ĸ��ֽ���ɣ����÷��������ڶ�ȡ��<code>DataSerializer</code>�ӿڵ�
     * <code>writeFloat</code> ����д��ĵ����ȸ�������
     * 
     * @return �����ĵ����ȸ�������
     * @throws IOException
     *             �����κζ�д�쳣��
     */
    float readFloat() throws IOException;

    /**
     * ��ȡ������һ�����������ĸ��ֽ���ɣ����÷��������ڶ�ȡ��<code>DataSerializer</code>�ӿڵ�
     * <code>writeInt</code> ����д���������
     * 
     * @return ������������
     * @throws IOException
     *             �����κζ�д�쳣��
     */
    int readInt() throws IOException;

    /**
     * ��ȡ������һ����������ֵ���ɰ˸��ֽ���ɣ����÷��������ڶ�ȡ��<code>DataSerializer</code>�ӿڵ�
     * <code>writeLong</code> ����д��ĳ�������ֵ��
     * 
     * @return �����ĳ�������ֵ��
     * @throws IOException
     *             �����κζ�д�쳣��
     */
    long readLong() throws IOException;

    /**
     * ��ȡ������һ����������ֵ���������ֽ���ɣ����÷��������ڶ�ȡ��<code>DataSerializer</code>�ӿڵ�
     * <code>writeShort</code> ����д��Ķ�������ֵ��
     * 
     * @return �����Ķ�������ֵ��
     * @throws IOException
     *             �����κζ�д�쳣��
     */
    short readShort() throws IOException;

    /**
     * ��ȡһЩ�ֽڣ���������������<code>b</code>�С���ȡ���ֽڵĸ�������<code>b</code>�ĳ��ȡ�
     * 
     * �÷�����һֱ������ֱ���������漸�������
     * <ul>
     * <li>��<code>b.length</code>���ֽڿɶ�����������·���������Ҫ���ֽڱ���������ء�
     * <li>�����κζ�д���󣨰����ڶ�����Ҫ���ֽ�֮ǰ�����ļ�������ǣ��������׳��쳣��
     * </ul>
     * 
     * ���<code>b</code>�գ�<code>null</code>�������׳�<code>NullPointerException</code>
     * �쳣�����<code>b.length</code>��<code>0</code>���򲻻��ȡ�κ��ֽڡ�����Ļ�����ȡ�����ֽڽ�������д�뻺������
     * <code>b</code>�У��ӵ�һ��λ�ÿ�ʼֱ�����һ��λ�ã������������������쳣�׳�����ô�����Ѿ���һ�����ֽڱ�д���˻�������
     * <code>b</code>�С�
     * 
     * @param b
     *            �������飬����װ�ض�ȡ�����ֽڡ�
     * @throws IOException
     *             �����κζ�д�쳣��
     */
    void readFully(byte[] b) throws IOException;

    /**
     * �鿴��������ȡһ���ֽڡ�
     * 
     * @return �����������е���һ��Ҫ��ȡ���ֽڡ�
     * @throws IOException
     *             �����κζ�д�쳣��
     */
    byte peekByte() throws IOException;

    /**
     * �ر����ݷ����л������ײ�����ݶ�дͨ��������ڴ�֮ǰ�÷����л����Ѿ����رգ��򲻽����κβ�����
     * 
     * @throws IOException
     *             �����κζ�д�쳣��
     */
    void close() throws IOException;
}
