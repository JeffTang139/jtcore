package org.eclipse.jt.core.spi.sql;

/**
 * �������
 * 
 * @author Jeff Tang
 * 
 */
public enum SQLErrorCode {
	/**
	 * �﷨����
	 */
	SYNTAX,
	/**
	 * IO����
	 */
	IO_ERROR,
	/**
	 * ��֧��
	 */
	NOT_SUPPORT,
	/**
	 * ����δ����
	 */
	TOKEN_UNDEFINED,
	/**
	 * ��������ʽ����ȷ
	 */
	VALUE_FORMAT,
	/**
	 * ���������Ͳ���ȷ
	 */
	OPERAND_TYPE_INVALID,
	/**
	 * ��������ȷ
	 */
	COLUMN_COUNT,
	/**
	 * ����δ����
	 */
	FUNC_UNDEFINED,
	/**
	 * ����δ�ҵ�
	 */
	TOKEN_NOT_FOUND,
	/**
	 * ȱ�ٱ���
	 */
	TABLE_NOT_FOUND,
	/**
	 * ȱ���ж���
	 */
	COLUMN_NOT_FOUND,
	/**
	 * �������ظ�
	 */
	VAR_DUPLICATE,
	/**
	 * ����δ����
	 */
	VAR_UNDEFINED,
	/**
	 * �����ظ�
	 */
	ALIAS_DUPLICATE,
	/**
	 * ����δ����
	 */
	ALIAS_UNDEFINED,
	/**
	 * ����δ�ҵ�
	 */
	CLASS_NOT_FOUND,
	/**
	 * ���ϵδ�ҵ�
	 */
	RELATION_NOT_FOUND,
	/**
	 * ����δ�ҵ�
	 */
	HIERARCHY_NOT_FOUND,
	/**
	 * ORMδ�ҵ�
	 */
	ORM_NOT_FOUND,
	/**
	 * �����ظ�
	 */
	NAMED_REDEFINED
}
