package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.ObjectQuerier;
import org.eclipse.jt.core.def.exp.ValueExpression;
import org.eclipse.jt.core.def.query.RelationColumnDefine;
import org.eclipse.jt.core.def.table.TableDeclarator;
import org.eclipse.jt.core.def.table.TableFieldDefine;

class SQLTableContext extends SQLVisitorContext implements SQLNameResolver {
	static class SQLRelationProvider implements SQLSourceProvider {
		private final TableRelationDefineImpl relation;

		public SQLRelationProvider(TableRelationDefineImpl relation) {
			this.relation = relation;
		}

		public RelationColumnDefine findColumn(String name) {
			return this.relation.target.fields.find(name);
		}

		public ValueExpression expOf(RelationColumnDefine c) {
			return this.relation.expOf((TableFieldDefine) c);
		}
		
		public HierarchyDefineImpl findHierarchy(String name) {
			return this.relation.target.hierarchies.find(name);
		}
		
		public TableRelationDefineImpl findRelation(String name) {
			return this.relation.target.relations.find(name);
		}
	}

	public final TableDeclarator declarator;
	public TableDefineImpl table;
	private SQLSourceProvider thisProvider = new SQLSourceProvider() {

		public RelationColumnDefine findColumn(String name) {
			return SQLTableContext.this.table.fields.find(name);
		}

		public ValueExpression expOf(RelationColumnDefine c) {
			return SQLTableContext.this.table.expOf((TableFieldDefine) c);
		}

		public HierarchyDefineImpl findHierarchy(String name) {
			return SQLTableContext.this.table.hierarchies.find(name);
		}

		public TableRelationDefineImpl findRelation(String name) {
			return SQLTableContext.this.table.relations.find(name);
		}
	};

	public SQLTableContext(ObjectQuerier querier, TableDeclarator declarator) {
		super(querier, false);
		this.declarator = declarator;
	}

	@SuppressWarnings("unchecked")
	public <T> T findProvider(Class<T> cls, String name) {
		if (cls.equals(SQLColumnProvider.class)) {
			if (name.equals("this")) {
				return (T) this.thisProvider;
			} else {
				TableRelationDefineImpl ref = this.table.relations.find(name);
				if (ref != null) {
					return (T) new SQLRelationProvider(ref);
				}
			}
		}
		return null;
	}
}
