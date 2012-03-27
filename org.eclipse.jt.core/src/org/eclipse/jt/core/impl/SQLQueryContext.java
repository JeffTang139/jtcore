package org.eclipse.jt.core.impl;

import java.util.HashMap;

import org.eclipse.jt.core.ObjectQuerier;
import org.eclipse.jt.core.def.exp.ValueExpression;
import org.eclipse.jt.core.def.query.RelationColumnDefine;
import org.eclipse.jt.core.def.query.StatementDeclarator;


class SQLQueryContext extends SQLVisitorContext implements SQLNameResolver {
	static class SQLQueryProvider implements SQLSourceProvider,
			SQLQuRelationRefProvider {
		private final QuRelationRef ref;

		public SQLQueryProvider(QuRelationRef ref) {
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

		public QuRelationRef getQuRelationRef() {
			return this.ref;
		}
	}

	public final StatementDeclarator<?> declarator;
	public SelectImpl<?, ?> query;
	public QuRelationRefImpl<?, ?, ?> returnRef;
	public SQLNameResolver resolver;
	private HashMap<QuRelationRef, SQLQueryProvider> providerCache;

	public SQLQueryContext(SQLVisitorContext root) {
		super(root);
		this.declarator = null;
	}

	public SQLQueryContext(ObjectQuerier querier, boolean restrict,
			StatementDeclarator<?> declarator) {
		super(querier, restrict);
		this.declarator = declarator;
	}

	@SuppressWarnings("unchecked")
	public <T> T findProvider(Class<T> cls, String name) {
		QuRelationRef ref = null;
		if (this.returnRef != null) {
			ref = this.findSource(this.returnRef, name);
		} else {
			ref = this.query.findRelationRef(name);
		}
		if (ref != null) {
			SQLQueryProvider p = null;
			if (this.providerCache == null) {
				this.providerCache = new HashMap<QuRelationRef, SQLQueryProvider>();
			} else {
				p = this.providerCache.get(ref);
			}
			if (p == null) {
				this.providerCache.put(ref, p = new SQLQueryProvider(ref));
			}
			return (T) p;
		}
		return null;
	}

	private QuRelationRef findSource(QuRelationRef ref, String name) {
		if (name.equals(ref.getName())) {
			return ref;
		}
		QuJoinedRelationRef j = ref.getJoins();
		while (j != null) {
			ref = this.findSource(j, name);
			if (ref != null) {
				return ref;
			}
			j = j.next();
		}
		return null;
	}

	public void build(SQLQueryVisitor visitor, SelectImpl<?, ?> query,
			NQuerySpecific q, SQLNameResolver resolver) {
		SelectImpl<?, ?> oldQuery = this.query;
		SQLNameResolver oldResolver = this.resolver;
		QuRelationRefImpl<?, ?, ?> returnRef = this.returnRef;
		this.query = query;
		this.resolver = resolver;
		this.returnRef = null;
		q.accept(this, visitor);
		this.query = oldQuery;
		this.resolver = oldResolver;
		this.returnRef = returnRef;
	}

	public void build(SelectImpl<?, ?> query, NQuerySpecific q,
			SQLNameResolver resolver) {
		this.build(SQLQueryVisitor.VISITOR, query, q, resolver);
	}
}
