package org.eclipse.jt.core.type;

/**
 * �ַ�������
 * 
 * @author Jeff Tang
 * 
 */
public interface SequenceDataType extends ObjectDataType {
	/**
	 * ��ȡ��󳤶�С�ڵ���0��ʾû������
	 */
	public int getMaxLength();

	/**
	 * �Ƿ��Ƕ�������
	 */
	public boolean isFixedLength();
}
