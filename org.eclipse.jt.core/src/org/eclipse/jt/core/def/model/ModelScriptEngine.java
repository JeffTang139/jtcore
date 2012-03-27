package org.eclipse.jt.core.def.model;

import org.eclipse.jt.core.Context;

/**
 * �ű�����ӿ�
 * 
 * @author Jeff Tang
 * 
 */
public interface ModelScriptEngine<TPreparedData> {
	/**
	 * �ýű������ж��Ƿ�֧��
	 * 
	 * @param language
	 *            �ű����������ƣ�����ֵһ��Сд��
	 * @return ���ش����������ʾ֧�֣�������ֵԽ�󣬱�ʾ֧�ֶ�Խ�ߣ��������������������汾����
	 */
	public int suport(String language);

	/**
	 * ����뵱ǰ�����ģ���ǰ�̣߳���صĽű�������
	 */
	public ModelScriptContext<TPreparedData> allocContext(Context context);
}
