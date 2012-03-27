package org.eclipse.jt.core.def.table;

import org.eclipse.jt.core.ObjectQuerier;
import org.eclipse.jt.core.def.MetaElementTemplateParams;
import org.eclipse.jt.core.impl.DNASql;
import org.eclipse.jt.core.impl.DeclaratorBase;
import org.eclipse.jt.core.impl.TableDeclareStub;
import org.eclipse.jt.core.impl.TableDefineImpl;

/**
 * ���������������ش�������ԭ����
 * 
 * �����������Ƕ�����������,�ͶԱ����Ԫ���ṩǿ���õ���
 * 
 * @author Jeff Tang
 * 
 */
public abstract class TableDeclarator extends DeclaratorBase {

	private TableDeclareStub stub;

	public TableDeclarator() {
		super(false);
		// ����DNASQLʱ�������ر��ϵ
		this.stub = (TableDeclareStub) DNASql.parseForDeclarator(this);
		final TableDefineImpl table = this.stub.getTable();
		this.table = table;
		this.f_RECID = table.f_recid;
		this.f_RECVER = table.f_recver;
	}

	public TableDeclarator(String name) {
		super(true);
		final TableDefineImpl table = new TableDefineImpl(name, this);
		this.table = table;
		this.f_RECID = table.f_recid;
		this.f_RECVER = table.f_recver;
	}

	public TableDeclarator(MetaElementTemplateParams params) {
		super(true);
		final TableDefineImpl table = new TableDefineImpl(params.getName(),
				this);
		this.table = table;
		this.f_RECID = table.f_recid;
		this.f_RECVER = table.f_recver;
	}

	@Override
	public final TableDefine getDefine() {
		return this.table;
	}

	@Override
	protected void declareUseRef(ObjectQuerier querier) {
		super.declareUseRef(querier);
		// ���ر��ϵ����ʱ���еı��嶼�Ѿ�ʵ���������ᷢ���Ҳ�����Ĵ���
		if (this.stub != null) {
			final TableDeclareStub aStub = this.stub;
			this.stub = null;
			aStub.fillRelations(querier);
		}
	}

	/**
	 * �߼�����
	 */
	protected final TableDeclare table;

	/**
	 * �߼���recid�ֶ�
	 */
	public final TableFieldDefine f_RECID;

	public final TableFieldDefine f_RECVER;

	// ////////////////////////////////////////////////////////
	private final static Class<?>[] intf_classes = { TableDefine.class };

	@Override
	protected final Class<?>[] getDefineIntfRegClasses() {
		return TableDeclarator.intf_classes;
	}

}
