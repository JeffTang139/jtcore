package org.eclipse.jt.core.impl;

/**
 * �����Ͷ��߼����ֶ����ʹ洢�ļ�����
 * 
 * @author Jeff Tang
 * 
 */
enum TypeCompatiblity {

	/**
	 * ���Լ��ݴ洢,�����ڿռ��˷�
	 */
	Exactly,

	/**
	 * ���Լ��ݴ洢,�����ڿռ��˷�
	 */
	Overflow,

	/**
	 * ���Լ��ݴ洢,�������޸�
	 */
	NotSuggest,

	/**
	 * ���ܼ��ݴ洢
	 */
	Unable
}
