package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.ObjectQuerier;
import org.eclipse.jt.core.def.table.TableDeclarator;
import org.eclipse.jt.core.def.table.TableDefine;
import org.eclipse.jt.core.impl.DNASql;
import org.eclipse.jt.core.impl.DeleteStatementImpl;
import org.eclipse.jt.core.impl.TableDefineImpl;

/**
 * ɾ����䶨���������
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
	 * ʹ��dna-sql�ű�������䶨��
	 * 
	 * <p>
	 * �ű��ļ�����Ϊ<strong>[����.delete]</strong>,�ұ�������ͬ�İ���.
	 */
	public DeleteStatementDeclarator() {
		super(false);
		this.statement = (DeleteStatementImpl) DNASql.parseForDeclarator(this);
	}

	/**
	 * ����ò��������Ĺ��캯��
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
	 * ɾ�������
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
