package org.eclipse.jt.core.da;

import java.text.Format;

import org.eclipse.jt.core.def.query.QueryColumnDefine;
import org.eclipse.jt.core.type.ReadableValue;


/**
 * ֻ��������ֶ�
 * 
 * @author Jeff Tang
 * 
 */
public interface ReadOnlyRecordSetField extends ReadableValue {

	/**
	 * ��ö�Ӧ�Ĳ�ѯ�ж���
	 */
	public QueryColumnDefine getDefine();

	/**
	 * ��ȡ�ֶ�������Sqlװ�ؽ����ʱ�п���������
	 */
	public String getName();

	/**
	 * ��ȡ��ʽ������
	 */
	public Format getFormat();

	/**
	 * ���ø�ʽ������
	 */
	public void setFormat(Format format);

	/**
	 * ��ʽ�����
	 */
	public String formatText();

	/**
	 * ������ʽ���ı�
	 */
	public void parseText(String text);
}
