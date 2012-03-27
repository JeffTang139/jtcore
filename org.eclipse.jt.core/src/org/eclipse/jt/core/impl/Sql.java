package org.eclipse.jt.core.impl;

import java.util.ArrayList;

import org.eclipse.jt.core.type.DataType;


/**
 * sql�����Ϣ
 * 
 * <p>
 * ��װ�˶�Ӧ���ݿ��sql�ı��Լ�ÿ�����������������Ϣ
 * 
 * @author Jeff Tang
 * 
 */
class Sql {

	final ArrayList<ParameterReserver> parameters = new ArrayList<ParameterReserver>();

	final boolean isAvailable() {
		return this.sqlstr != null && this.sqlstr.length() > 0;
	}

	final void checkAvailable() {
		if (!this.isAvailable()) {
			throw new UnsupportedOperationException("sql���Ϊ��");
		}
	}

	final void build(ISqlCommandBuffer buffer) {
		this.sqlstr = buffer.build(this.parameters);
	}

	static final ArgumentReserver arOf(StructFieldDefineImpl sf, DataType type) {
		return new ArgumentReserver(sf, type);
	}

	final void build(SqlBuilder sql) {
		this.sqlstr = sql.toSql();
	}

	private String sqlstr;

	final String sqlstr() {
		return this.sqlstr;
	}

	@Override
	public final String toString() {
		return this.sqlstr;
	}

}
