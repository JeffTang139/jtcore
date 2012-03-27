package org.eclipse.jt.core.def;

import org.eclipse.jt.core.def.exp.ValueExpression;
import org.eclipse.jt.core.type.DataTypable;

/**
 * �ֶλ��ӿڶ���
 * 
 * @author Jeff Tang
 * 
 */
public interface FieldDefine extends NamedDefine, DataTypable {
	/**
	 * �Ƿ�����Ҫһֱ���ֿ���(�ǿ�)
	 * 
	 * @return �����Ƿ��Ǳ����ֶ�
	 */
	public boolean isKeepValid();

	/**
	 * �Ƿ���ֻ���ֶΣ�ֻ�ڹ���ʱ���ã�
	 * 
	 * @return �����Ƿ���ֻ���ֶ�
	 */
	public boolean isReadonly();

	/**
	 * ��ȡ�ֶε�Ĭ��ֵ
	 * 
	 * @return �����ֶζ����Ĭ��ֵ
	 */
	public ValueExpression getDefault();
}
