package org.eclipse.jt.core.impl;


import org.eclipse.jt.core.def.MetaElementType;
import org.eclipse.jt.core.def.query.QueryStatementDeclarator;

/**
 * 查询语句定义实现类
 * 
 * @author Jeff Tang
 * 
 */
public class QueryStatementImpl extends QueryStatementBase implements
		Declarative<QueryStatementDeclarator> {

	public final QueryStatementDeclarator getDeclarator() {
		return this.declarator;
	}

	public final MetaElementType getMetaElementType() {
		return MetaElementType.QUERY;
	}

	@Override
	public final String getXMLTagName() {
		return xml_element_query_statement;
	}

	@Override
	public final QueryStatementImpl clone() {
		QueryStatementImpl target = new QueryStatementImpl(this.name);
		this.cloneTo(target);
		return target;
	}

	static final String xml_element_query_statement = "query-statement";

	final QueryStatementDeclarator declarator;

	public QueryStatementImpl(String name) {
		super(name);
		this.declarator = null;
	}

	public QueryStatementImpl(String name, QueryStatementDeclarator declarator) {
		super(name);
		this.declarator = declarator;
	}

	/**
	 * 只用作构造MappingQueryStatement
	 * 
	 * @param name
	 * @param argumentsRef
	 */
	public QueryStatementImpl(String name, StructDefineImpl argumentsRef) {
		super(name, argumentsRef);
		this.declarator = null;
	}

	final DynObj newRecordObj(int state) {
		DynObj record = new DynObj();
		record.setRecordState(state);
		this.mapping.prepareSONoCheck(record);
		return record;
	}

	@Override
	final void doPrepare() throws Throwable {
		super.doPrepare();
		this.mapping = new RecordStructDefine(this);
		this.rowSaveSql = null;
		// this.queryRecidSqls = null;
	}

	private volatile RowSaveSql rowSaveSql;

	final RowSaveSql getRowSaveSql(DBAdapterImpl dbAdapter) {
		this.ensurePrepared(dbAdapter.getContext(), false);
		RowSaveSql recordSaveSql = this.rowSaveSql;
		if (recordSaveSql == null) {
			synchronized (this) {
				recordSaveSql = this.rowSaveSql;
				if (recordSaveSql == null) {
					this.rowSaveSql = recordSaveSql = new RowSaveSql(
							dbAdapter.lang, this);
				}
			}
		}
		return recordSaveSql;
	}

	// CORE2.5 废弃?
	// static final class QueryRecidByLpkSql extends Sql {
	//
	// final QuRelationRef relationRef;
	// final StructFieldDefineImpl f_recid;
	//
	// QueryRecidByLpkSql(DBLang lang, QueryStatementImpl statement,
	// QuRelationRef relationRef) {
	// statement.validate();
	// this.relationRef = relationRef;
	// if (!statement.supportModify(relationRef)) {
	// this.f_recid = null;
	// return;
	// }
	// QuTableRef tableRef = (QuTableRef) relationRef;
	// QueryColumnImpl c_recid = statement.findEqualColumn(tableRef,
	// tableRef.getTarget().f_recid);
	// if (c_recid == null) {
	// this.f_recid = null;
	// return;
	// }
	// this.f_recid = c_recid.targetField;
	// IndexDefineImpl index = tableRef.getTarget().getLogicalKeyIndex();
	// int c;
	// if ((c = index.items.size()) == 0) {
	// return;
	// }
	// StructFieldDefineImpl[] f_keys = new StructFieldDefineImpl[c];
	// QueryColumnImpl col;
	// for (int i = 0; i < c; i++) {
	// TableFieldDefineImpl key = index.items.get(i).getField();
	// if ((col = statement.findColumn(tableRef, key)) == null) {
	// return;
	// }
	// f_keys[i] = col.targetField;
	// }
	// SqlBuilder sql = new SqlBuilder(lang, this);
	// sql.appendSelect();
	// sql.nNewline().pi();
	// sql.appendRecidRef(tableRef, tableRef.getTarget().primary);
	// sql.nNewline().ri();
	// sql.appendFrom().nNewline().pi();
	// sql.appendIdAndRef(tableRef.getTarget().primary, tableRef);
	// sql.nNewline().ri();
	// sql.appendWhere().nNewline().pi();
	// for (int i = 0; i < c; i++) {
	// if (i > 0) {
	// sql.appendAnd();
	// }
	// sql.appendFieldRef(tableRef, index.items.get(i).getField());
	// sql.append('=');
	// this.addParameter(sql, f_keys[i], index.items.get(i).getField()
	// .getType());
	// sql.nNewline();
	// }
	// sql.ri();
	// this.completeBuild(sql);
	// }
	// }
	//
	// private volatile QueryRecidByLpkSql[] queryRecidSqls;
	//
	// final QueryRecidByLpkSql[] getQueryRecidSqls(DBAdapterImpl dbAdapter) {
	// this.ensurePrepared(dbAdapter.getContext(), false);
	// QueryRecidByLpkSql[] recidQuerySqls = this.queryRecidSqls;
	// if (recidQuerySqls == null) {
	// synchronized (this) {
	// recidQuerySqls = this.queryRecidSqls;
	// if (recidQuerySqls == null) {
	// ArrayList<QueryRecidByLpkSql> sqls = new ArrayList<QueryRecidByLpkSql>();
	// for (QuRelationRef relationRef : this.rootRelationRef()) {
	// sqls.add(new QueryRecidByLpkSql(dbAdapter.lang, this,
	// relationRef));
	// }
	// this.queryRecidSqls = sqls
	// .toArray(new QueryRecidByLpkSql[sqls.size()]);
	// }
	// }
	// }
	// return recidQuerySqls;
	// }

}
