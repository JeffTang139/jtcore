package org.eclipse.jt.core.type;
/**
 * ����ֵ������
 * 
 * @author Jeff Tang
 * 
 */
public interface ValueTreeIterator extends ValueIterator {
	/**
	 * ��ȡ��ǰ�ļ��Σ�-1Ϊ��λ�ã�0Ϊ��һ��
	 * 
	 * @return ���ص�ǰ����
	 */
	public int getLevel();
}
