package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.ObjectQuerier;
import org.eclipse.jt.core.def.table.TableDeclarator;
import org.eclipse.jt.core.def.table.TableDefine;
import org.eclipse.jt.core.impl.DNASql;
import org.eclipse.jt.core.impl.TableDefineImpl;
import org.eclipse.jt.core.impl.UpdateStatementImpl;

/**
 * 更新语句声明器
 * 
 * @author Jeff Tang
 * 
 */
public abstract class UpdateStatementDeclarator extends
		ModifyStatementDeclarator<UpdateStatementDefine> {

	/**
	 * 使用dna-sql脚本构造语句定义
	 * 
	 * <p>
	 * 脚本文件名称为<strong>[类名.update]</strong>,且必须在相同的包下.
	 */
	public UpdateStatementDeclarator() {
		super(false);
		this.statement = (UpdateStatementImpl) DNASql.parseForDeclarator(this);
	}

	/**
	 * 请调用不带参数的构造函数
	 */
	@Deprecated
	public UpdateStatementDeclarator(ObjectQuerier oQuerier) {
		this();
	}

	public UpdateStatementDeclarator(String name,
			TableDeclarator tableDeclarator) {
		this(name, tableDeclarator.getDefine());
	}

	public UpdateStatementDeclarator(String name, TableDefine table) {
		super(true);
		this.statement = new UpdateStatementImpl(name, (TableDefineImpl) table);
	}

	@Override
	public final UpdateStatementDefine getDefine() {
		return this.statement;
	}

	protected final UpdateStatementDeclare statement;

	// --------------------------------------------

	private final static Class<?>[] intf_classes = { UpdateStatementDefine.class };

	@Override
	protected final Class<?>[] getDefineIntfRegClasses() {
		return UpdateStatementDeclarator.intf_classes;
	}
}
