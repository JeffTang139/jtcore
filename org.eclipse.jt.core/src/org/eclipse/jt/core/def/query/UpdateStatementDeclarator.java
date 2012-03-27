package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.ObjectQuerier;
import org.eclipse.jt.core.def.table.TableDeclarator;
import org.eclipse.jt.core.def.table.TableDefine;
import org.eclipse.jt.core.impl.DNASql;
import org.eclipse.jt.core.impl.TableDefineImpl;
import org.eclipse.jt.core.impl.UpdateStatementImpl;

/**
 * �������������
 * 
 * @author Jeff Tang
 * 
 */
public abstract class UpdateStatementDeclarator extends
		ModifyStatementDeclarator<UpdateStatementDefine> {

	/**
	 * ʹ��dna-sql�ű�������䶨��
	 * 
	 * <p>
	 * �ű��ļ�����Ϊ<strong>[����.update]</strong>,�ұ�������ͬ�İ���.
	 */
	public UpdateStatementDeclarator() {
		super(false);
		this.statement = (UpdateStatementImpl) DNASql.parseForDeclarator(this);
	}

	/**
	 * ����ò��������Ĺ��캯��
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
