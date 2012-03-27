package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.query.DerivedQueryDefine;
import org.eclipse.jt.core.def.query.RelationColumnDefine;
import org.eclipse.jt.core.def.table.TableDeclarator;
import org.eclipse.jt.core.def.table.TableDefine;
import org.eclipse.jt.core.def.table.TableRelationDefine;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.misc.SXElement;

/**
 * 查询定义中使用的关系引用基类
 * 
 * @author Jeff Tang
 * 
 * @param <TRelation>
 *            目标关系类型
 * @param <TLink>
 *            自身及链表类型
 * @param <TItrNode>
 *            迭代类型
 */
abstract class QuRelationRefImpl<TRelation extends Relation, TLink extends QuRelationRef, TItrNode extends QuRelationRef>
		extends
		NodableRelationRefImpl<TRelation, TLink, QuJoinedRelationRef, TItrNode>
		implements QuRelationRef {

	public final QuJoinedTableRef newJoin(TableDefine target) {
		if (target == null) {
			throw new NullArgumentException("表定义");
		}
		return this.newJoin(target.getName(), (TableDefineImpl) target);
	}

	public final QuJoinedTableRef newJoin(TableDefine target, String name) {
		if (target == null) {
			throw new NullArgumentException("表定义");
		}
		return this.newJoin(name, (TableDefineImpl) target);
	}

	public final QuJoinedTableRef newJoin(TableDeclarator target) {
		if (target == null) {
			throw new NullArgumentException("表声明");
		}
		TableDefineImpl table = (TableDefineImpl) target.getDefine();
		return this.newJoin(table.name, table);
	}

	public final QuJoinedTableRef newJoin(TableDeclarator target, String name) {
		if (target == null) {
			throw new NullArgumentException("表声明");
		}
		return this.newJoin(name, (TableDefineImpl) target.getDefine());
	}

	public final QuJoinedTableRef newJoin(TableRelationDefine sample) {
		if (sample == null) {
			throw new NullArgumentException("表关系定义");
		}
		return this.newJoin(sample.getName(), (TableRelationDefineImpl) sample);
	}

	public final QuJoinedTableRef newJoin(TableRelationDefine sample,
			String name) {
		if (sample == null) {
			throw new NullArgumentException("表关系定义");
		}
		return this.newJoin(name, (TableRelationDefineImpl) sample);
	}

	public final QuJoinedQueryRef newJoin(DerivedQueryDefine query) {
		if (query == null) {
			throw new NullArgumentException("连接查询结构定义");
		}
		return this.newJoin(query.getName(), (DerivedQueryImpl) query);
	}

	public final QuJoinedQueryRef newJoin(DerivedQueryDefine query, String name) {
		if (query == null) {
			throw new NullArgumentException("连接查询结构定义");
		}
		return this.newJoin(name, (DerivedQueryImpl) query);
	}

	public final SelectColumnImpl<?, ?> newColumn(RelationColumnDefine column) {
		if (column == null) {
			throw new NullArgumentException("关系列定义");
		}
		return this.owner.newColumn(column.getName(), this.expOf(column));
	}

	public final SelectColumnImpl<?, ?> newColumn(RelationColumnDefine column,
			String name) {
		if (name == null || name.length() == 0) {
			throw new NullArgumentException("查询列名");
		}
		if (column == null) {
			throw new NullArgumentException("关系列定义");
		}
		return this.owner.newColumn(column.getName(), this.expOf(column));
	}

	public final OrderByItemImpl newOrderBy(RelationColumnDefine column) {
		return null;
	}

	public final OrderByItemImpl newOrderBy(RelationColumnDefine column,
			boolean isDesc) {
		return null;
	}

	public final void setForUpdate(boolean forUpdate) {
		this.checkModifiable();
		if (this.owner instanceof QueryStatementBase) {
			QueryStatementBase owner = (QueryStatementBase) this.owner;
			owner.setForUpdate(this, forUpdate);
			return;
		}
		throw new IllegalArgumentException();
	}

	public final boolean getForUpdate() {
		if (this.owner instanceof QueryStatementBase) {
			QueryStatementBase owner = (QueryStatementBase) this.owner;
			Boolean forUpdate = owner.forUpdates.get(this);
			return forUpdate == null ? false : forUpdate.booleanValue();
		}
		throw new IllegalArgumentException();
	}

	static final String xml_element_joins = "joins";

	/**
	 * 所属查询定义
	 */
	// 没有使用泛型!否则,后果很严重!
	final SelectImpl<?, ?> owner;

	QuRelationRefImpl(SelectImpl<?, ?> owner, String name, TRelation target) {
		super(name, target);
		this.owner = owner;
	}

	public final SelectImpl<?, ?> getOwner() {
		return this.owner;
	}

	public final QuJoinedRelationRef newJoin0(String name, Relation target) {
		if (target == null) {
			throw new NullArgumentException("关系元定义");
		}
		if (target instanceof TableDefineImpl) {
			return this.newJoin(name, (TableDefineImpl) target);
		} else if (target instanceof DerivedQueryImpl) {
			return this.newJoin(name, (DerivedQueryImpl) target);
		}
		throw new UnsupportedOperationException();
	}

	/**
	 * 从表关系构造连接
	 * 
	 * @param name
	 * @param relation
	 * @return
	 */
	final QuJoinedTableRef newJoin(String name, TableRelationDefineImpl relation) {
		if (relation == null) {
			throw new NullArgumentException("表关系定义");
		}
		QuJoinedTableRef join = this.newJoin(name, relation.target);
		join.setJoinCondition(relation.condition.clone(relation.owner.selfRef,
				this, relation, join));
		return join;
	}

	final QuJoinedTableRef newJoin(String name, TableDefineImpl table) {
		this.checkModifiable();
		if (name == null || name.length() == 0) {
			throw new NullArgumentException("引用名称");
		}
		if (table == null) {
			throw new NullArgumentException("表定义");
		}
		if (this.owner.relationRefMap.get(name) != null) {
			name = Utils.buildIdentityName(name, SelectImpl.aliasDetector,
					this.owner.relationRefMap);
		}
		QuJoinedTableRef join = new QuJoinedTableRef(this.owner, name, table,
				this);
		this.addJoinNoCheck(join);
		this.owner.relationRefMap.put(name, join, true);
		return join;
	}

	final QuJoinedQueryRef newJoin(String name, DerivedQueryImpl query) {
		this.checkModifiable();
		if (name == null || name.length() == 0) {
			throw new NullArgumentException("引用名称");
		}
		if (query == null) {
			throw new NullArgumentException("查询定义");
		}
		if (SystemVariables.VALIDATE_DERIVED_DOMAIN) {
			query.validateDomain(this.owner);
		}
		if (this.owner.relationRefMap.get(name) != null) {
			name = Utils.buildIdentityName(name, SelectImpl.aliasDetector,
					this.owner.relationRefMap);
		}
		QuJoinedQueryRef join = new QuJoinedQueryRef(this.owner, name, query,
				this);
		this.addJoinNoCheck(join);
		this.owner.relationRefMap.put(name, join, true);
		return join;
	}

	public final void rendTreeInto(SXElement element) {
		SXElement self = element.append(this.getXMLTagName());
		this.render(self);
		QuJoinedRelationRef join = this.getJoins();
		if (join != null) {
			join.rendTreeInto(self.append(xml_element_joins));
		}
		QuRelationRef nextRef = this.next();
		if (nextRef != null) {
			nextRef.rendTreeInto(element);
		}
	}

	public void validate() {
	}

}
