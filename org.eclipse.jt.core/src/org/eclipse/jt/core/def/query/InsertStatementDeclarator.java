package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.ObjectQuerier;
import org.eclipse.jt.core.def.table.TableDeclarator;
import org.eclipse.jt.core.def.table.TableDefine;
import org.eclipse.jt.core.impl.DNASql;
import org.eclipse.jt.core.impl.InsertStatementImpl;
import org.eclipse.jt.core.impl.TableDefineImpl;

/**
 * ������䶨��������
 * 
 * @author Jeff Tang
 * 
 */
public abstract class InsertStatementDeclarator extends
		ModifyStatementDeclarator<InsertStatementDefine> {

	/**
	 * ʹ��dna-sql�ű�������䶨��
	 * 
	 * <p>
	 * �ű��ļ�����Ϊ<strong>[����.insert]</strong>,�ұ�������ͬ�İ���.
	 * 
	 * @param oQuerier
	 */
	public InsertStatementDeclarator() {
		super(false);
		this.statement = (InsertStatementImpl) DNASql.parseForDeclarator(this);
	}

	/**
	 * ����ò��������Ĺ��캯��
	 */
	@Deprecated
	public InsertStatementDeclarator(ObjectQuerier oQuerier) {
		this();
	}

	public InsertStatementDeclarator(String name,
			TableDeclarator tableDeclarator) {
		this(name, tableDeclarator.getDefine());
	}

	public InsertStatementDeclarator(String name, TableDefine table) {
		super(true);
		this.statement = new InsertStatementImpl(name, (TableDefineImpl) table,
				this);
	}

	@Override
	public final InsertStatementDefine getDefine() {
		return this.statement;
	}

	protected final InsertStatementDeclare statement;

	// --------------------------------------------

	private final static Class<?>[] intf_classes = { InsertStatementDefine.class };

	@Override
	protected final Class<?>[] getDefineIntfRegClasses() {
		return InsertStatementDeclarator.intf_classes;
	}
}
