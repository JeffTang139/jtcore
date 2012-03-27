package org.eclipse.jt.core.impl;

import java.util.ArrayList;

final class ProcedureCallSql extends Sql {

	ProcedureCallSql(DBLang lang, StoredProcedureDefineImpl proc) {
		SqlBuilder sql = new SqlBuilder(lang);
		sql.append("{call ").append(proc.name);
		if (proc.arguments.fields.size() > 0) {
			sql.lp();
			ArrayList<StructFieldDefineImpl> fields = proc.arguments.fields;
			for (int i = 0, c = fields.size(); i < c; i++) {
				StructFieldDefineImpl f = fields.get(i);
				this.parameters.add(arOf(f, f.type));
				sql.append('?');
				sql.nComma().nSpace();
			}
			sql.uSpace().uComma().rp();
		}
		sql.append('}');
		this.build(sql);
	}

}