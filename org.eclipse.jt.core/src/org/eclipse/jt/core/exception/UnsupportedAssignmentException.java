package org.eclipse.jt.core.exception;

import org.eclipse.jt.core.def.obja.StructFieldDefine;
import org.eclipse.jt.core.def.query.InsertStatementDefine;
import org.eclipse.jt.core.def.query.MappingQueryStatementDefine;
import org.eclipse.jt.core.def.query.QueryColumnDefine;
import org.eclipse.jt.core.def.query.UpdateStatementDefine;
import org.eclipse.jt.core.def.table.TableFieldDefine;
import org.eclipse.jt.core.type.DataType;

/**
 * ��֧�ֵĸ�ֵ����
 * 
 * <p>
 * ��insert,update������,���ʽ�������Ӧ���ֶ����Ͳ�ƥ��;����ormӳ��Ĳ�ѯ������������Ӧ��java�ֶ����Ͳ�ƥ��ʱ�׳����쳣.
 * 
 * <p>
 * ��ͨ�����������رոü��:
 * 
 * <pre>
 * -Dcom.jiuq.dna.validate-assign-type=false
 * </pre>
 * 
 * @author Jeff Tang
 * 
 */
public final class UnsupportedAssignmentException extends CoreException {

	private static final long serialVersionUID = 7169287826812392421L;

	public UnsupportedAssignmentException(MappingQueryStatementDefine query,
			QueryColumnDefine column, StructFieldDefine field) {
		super("ORM����[" + query.getName() + "]��,ʵ���ֶ�[" + field.getName()
				+ "]������[" + field.getType().toString() + "]���ܰ󶨵�����Ϊ["
				+ column.getType() + "]���������.");
	}

	public UnsupportedAssignmentException(InsertStatementDefine insert,
			TableFieldDefine field, DataType type) {
		super("������䶨��[" + insert.getName() + "]��,����Ϊ["
				+ field.getType().toString() + "]���ֶ�[" + field.getName()
				+ "],���ܽ�������Ϊ[" + type.toString() + "]��ֵ.");
	}

	public UnsupportedAssignmentException(UpdateStatementDefine update,
			TableFieldDefine field, DataType type) {
		super("������䶨��[" + update.getName() + "]��,����Ϊ["
				+ field.getType().toString() + "]���ֶ�[" + field.getName()
				+ "],���ܽ�������Ϊ[" + type.toString() + "]��ֵ.");
	}
}
