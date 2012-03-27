package org.eclipse.jt.core.impl;

import java.util.HashMap;

import org.eclipse.jt.core.ObjectQuerier;
import org.eclipse.jt.core.def.exp.ValueExpression;
import org.eclipse.jt.core.def.query.ModifyStatementDeclarator;
import org.eclipse.jt.core.def.query.RelationColumnDefine;


class SQLModifyContext extends SQLVisitorContext implements SQLNameResolver {
	static class SQLModifyProvider implements SQLSourceProvider {
		private final MoRelationRef ref;

		public SQLModifyProvider(MoRelationRef ref) {
			this.ref = ref;
		}

		public RelationColumnDefine findColumn(String name) {
			return this.ref.getTarget().findColumn(name);
		}

		public HierarchyDefineImpl findHierarchy(String name) {
			Relation r = this.ref.getTarget();
			if (r instanceof TableDefineImpl) {
				return ((TableDefineImpl) r).hierarchies.find(name);
			}
			return null;
		}

		public TableRelationDefineImpl findRelation(String name) {
			Relation r = this.ref.getTarget();
			if (r instanceof TableDefineImpl) {
				return ((TableDefineImpl) r).relations.find(name);
			}
			return null;
		}

		public ValueExpression expOf(RelationColumnDefine c) {
			return this.ref.expOf(c);
		}
	}

	public final ModifyStatementDeclarator<?> declarator;
	public MoRelationRefImpl<?, ?, ?> returnRef;
	public String rootAlias;
	private HashMap<MoRelationRef, SQLModifyProvider> providerCache;

	public SQLModifyContext(SQLVisitorContext root) {
		super(root);
		this.declarator = null;
	}

	public SQLModifyContext(ObjectQuerier querier, boolean restrict,
			ModifyStatementDeclarator<?> declarator) {
		super(querier, restrict);
		this.declarator = declarator;
	}

	@SuppressWarnings("unchecked")
	public <T> T findProvider(Class<T> cls, String name) {
		MoRelationRef ref = null;
		if (name.equals(this.rootAlias)) {
			ref = ((ModifyStatementImpl) this.rootStmt).moTableRef;
		} else if (this.returnRef != null) {
			ref = this.findSource(this.returnRef, name);
		}
		if (ref != null) {
			SQLModifyProvider p = null;
			if (this.providerCache == null) {
				this.providerCache = new HashMap<MoRelationRef, SQLModifyProvider>();
			} else {
				p = this.providerCache.get(ref);
			}
			if (p == null) {
				this.providerCache.put(ref, p = new SQLModifyProvider(ref));
			}
			return (T) p;
		}
		return null;
	}

	private MoRelationRef findSource(MoRelationRef ref, String name) {
		if (name.equals(ref.getName())) {
			return ref;
		}
		MoJoinedRelationRef j = ref.getJoins();
		while (j != null) {
			ref = this.findSource(j, name);
			if (ref != null) {
				return ref;
			}
			j = j.next();
		}
		return null;
	}
}
