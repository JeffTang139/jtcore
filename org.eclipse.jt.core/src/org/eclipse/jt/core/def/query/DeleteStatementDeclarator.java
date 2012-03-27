package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.ObjectQuerier;
import org.eclipse.jt.core.def.table.TableDeclarator;
import org.eclipse.jt.core.def.table.TableDefine;
import org.eclipse.jt.core.impl.DNASql;
import org.eclipse.jt.core.impl.DeleteStatementImpl;
import org.eclipse.jt.core.impl.TableDefineImpl;

/**
 * 删除语句定义的声明器
 * 
 * @author Jeff Tang
 * 
 */
public abstract class DeleteStatementDeclarator extends
		ModifyStatementDeclarator<DeleteStatementDefine> {

	public DeleteStatementDeclarator(String name, TableDefine targetTable) {
		super(true);
		this.statement = new DeleteStatementImpl(name,
				(TableDefineImpl) targetTable);
	}

	public DeleteStatementDeclarator(String name, TableDeclarator targetTable) {
		this(name, targetTable.getDefine());
	}

	/**
	 * 使用dna-sql脚本构造语句定义
	 * 
	 * <p>
	 * 脚本文件名称为<strong>[类名.delete]</strong>,且必须在相同的包下.
	 */
	public DeleteStatementDeclarator() {
		super(false);
		this.statement = (DeleteStatementImpl) DNASql.parseForDeclarator(this);
	}

	/**
	 * 请调用不带参数的构造函数
	 */
	@Deprecated
	public DeleteStatementDeclarator(ObjectQuerier oQuerier) {
		this();
	}

	@Override
	public final DeleteStatementDefine getDefine() {
		return this.statement;
	}

	/**
	 * 删除命令定义
	 */
	/**
	 * 
	 */
	protected final DeleteStatementDeclare statement;

	// ------------------------------------------------------------------

	private final static Class<?>[] intf_classes = { DeleteStatementDefine.class };

	@Override
	protected final Class<?>[] getDefineIntfRegClasses() {
		return DeleteStatementDeclarator.intf_classes;
	}
}
