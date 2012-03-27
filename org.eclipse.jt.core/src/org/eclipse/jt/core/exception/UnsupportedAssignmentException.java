package org.eclipse.jt.core.exception;

import org.eclipse.jt.core.def.obja.StructFieldDefine;
import org.eclipse.jt.core.def.query.InsertStatementDefine;
import org.eclipse.jt.core.def.query.MappingQueryStatementDefine;
import org.eclipse.jt.core.def.query.QueryColumnDefine;
import org.eclipse.jt.core.def.query.UpdateStatementDefine;
import org.eclipse.jt.core.def.table.TableFieldDefine;
import org.eclipse.jt.core.type.DataType;

/**
 * 不支持的赋值操作
 * 
 * <p>
 * 当insert,update定义中,表达式类型与对应的字段类型不匹配;或是orm映射的查询输出列类型与对应的java字段类型不匹配时抛出该异常.
 * 
 * <p>
 * 可通过启动参数关闭该检查:
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
		super("ORM定义[" + query.getName() + "]中,实体字段[" + field.getName()
				+ "]的类型[" + field.getType().toString() + "]不能绑定到类型为["
				+ column.getType() + "]的输出列上.");
	}

	public UnsupportedAssignmentException(InsertStatementDefine insert,
			TableFieldDefine field, DataType type) {
		super("插入语句定义[" + insert.getName() + "]中,类型为["
				+ field.getType().toString() + "]的字段[" + field.getName()
				+ "],不能接受类型为[" + type.toString() + "]的值.");
	}

	public UnsupportedAssignmentException(UpdateStatementDefine update,
			TableFieldDefine field, DataType type) {
		super("更新语句定义[" + update.getName() + "]中,类型为["
				+ field.getType().toString() + "]的字段[" + field.getName()
				+ "],不能接受类型为[" + type.toString() + "]的值.");
	}
}
