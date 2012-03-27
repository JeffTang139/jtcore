package org.eclipse.jt.core.exception;

import org.eclipse.jt.core.def.exp.SelectColumnRefExpr;
import org.eclipse.jt.core.def.exp.TableFieldRefExpr;
import org.eclipse.jt.core.def.query.SubQueryExpression;

/**
 * ��䶨��ı��ʽʹ����Ƿ�
 * 
 * <p>
 * core2.5��������䶨��ı��ʽʹ������.�ü�����ͨ�����������ر�.
 * 
 * <p>
 * ����䶨���׳����쳣ʱ,����ǿ�ƺ��Լ�����������ȷִ��,������ζ����������ĳЩ������ִ�л�ʧ��,���Խ����޸��κ��׳����쳣����䶨��.
 * 
 * <p>
 * �رձ��ʽ����Ĳ���Ϊ:
 * 
 * <pre>
 * 	<b>-Dorg.eclipse.jt.validate-expr-domain=false</b>
 * </pre>
 * 
 * @since core2.5
 * 
 * @author Jeff Tang
 * 
 */
public class InvalidValueExprDomainException extends CoreException {

	private static final long serialVersionUID = -1720255180362049350L;

	public InvalidValueExprDomainException(SelectColumnRefExpr columnRef) {
		super("��ѯ������[" + columnRef.toString() + "]��ʹ�������.");
	}

	public InvalidValueExprDomainException(TableFieldRefExpr fieldRef) {
		super("�ֶ�����[" + fieldRef.toString() + "]��ʹ�������.");
	}

	public InvalidValueExprDomainException(SubQueryExpression expr) {
		super("�Ӳ�ѯ��ʹ�������乹����.");
	}

}
