package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.MissingDefineException;
import org.eclipse.jt.core.def.query.ModifyStatementDeclare;
import org.eclipse.jt.core.def.query.RelationColumnDefine;
import org.eclipse.jt.core.def.table.TableFieldDefine;
import org.eclipse.jt.core.exception.NullArgumentException;

/**
 * 更新语句
 * 
 * @author Jeff Tang
 * 
 */
abstract class ModifyStatementImpl extends StatementImpl implements
		ModifyStatementDeclare, RelationRefDomain, OMVisitable {

	public final DerivedQueryImpl newDerivedQuery() {
		return new DerivedQueryImpl(this);
	}

	public final SubQueryImpl newSubQuery() {
		return new SubQueryImpl(this);
	}

	public final TableFieldRefImpl expOf(RelationColumnDefine column) {
		if (column instanceof TableFieldDefineImpl) {
			TableFieldDefineImpl f = (TableFieldDefineImpl) column;
			return new TableFieldRefImpl(this.moTableRef, f);
		}
		throw RelationRefImpl.notSupportedRelationColumnRefException(
				this.moTableRef, column);
	}

	public final TableFieldRefImpl expOf(String columnName) {
		return this.moTableRef.expOf(this.moTableRef.getTarget().getColumn(
				columnName));
	}

	static final ExistingDetector<StringKeyMap<MoRelationRef>, MoRelationRef, String> detector = new ExistingDetector<StringKeyMap<MoRelationRef>, MoRelationRef, String>() {

		public boolean exists(StringKeyMap<MoRelationRef> container,
				String key, MoRelationRef ignore) {
			MoRelationRef relationRef = container.get(key);
			return relationRef != null
					&& (ignore == null || relationRef != ignore);
		}

	};

	/**
	 * 更新表引用
	 */
	final MoRootTableRef moTableRef;

	final StringKeyMap<MoRelationRef> map;

	ModifyStatementImpl(String name, TableDefineImpl table) {
		super(name);
		if (table == null) {
			throw new NullPointerException();
		}
		this.moTableRef = new MoRootTableRef(this, table.name, table);
		this.map = new StringKeyMap<MoRelationRef>();
		this.map.put(table.name, this.moTableRef);
	}

	ModifyStatementImpl(String name, StructDefineImpl arguments,
			TableDefineImpl table) {
		super(name, arguments);
		if (table == null) {
			throw new NullPointerException();
		}
		this.moTableRef = new MoRootTableRef(this, table.name, table);
		this.map = new StringKeyMap<MoRelationRef>();
		this.map.put(table.name, this.moTableRef);
	}

	/**
	 * 只是临时提供存储过程定义使用的构造方法
	 * 
	 * @param name
	 * @param declarator
	 */
	@Deprecated
	ModifyStatementImpl(String name) {
		super(name);
		this.moTableRef = null;
		this.map = null;
	}

	final TableFieldRefImpl expOf0(TableFieldDefineImpl field) {
		if (field == null) {
			throw new NullArgumentException("字段定义");
		}
		return new TableFieldRefImpl(this.moTableRef, field);
	}

	final TableFieldDefineImpl checkOwner(TableFieldDefine field) {
		TableFieldDefineImpl f = (TableFieldDefineImpl) field;
		if (f.owner != this.moTableRef.target) {
			throw new IllegalArgumentException("字段定义[" + f.name
					+ "]不属于当前更新语句的目标表定义[" + this.moTableRef.target.name + "].");
		}
		return f;
	}

	public final MoRelationRef findRelationRef(String name) {
		return this.map.get(name);
	}

	public final MoRelationRef getRelationRef(String name) {
		MoRelationRef relationRef = this.findRelationRef(name);
		if (relationRef != null) {
			return relationRef;
		}
		throw missingRelationRef(name);
	}

	public final MoRelationRef findRelationRefRecursively(String name) {
		return this.findRelationRef(name);
	}

	public final MoRelationRef getRelationRefRecursively(String name) {
		return this.getRelationRef(name);
	}

	public final DerivedQueryImpl getWith(String name) {
		throw new UnsupportedOperationException();
	}

	public final RelationRefDomain getDomain() {
		return null;
	}

	static final MissingDefineException missingRelationRef(String name) {
		return new MissingDefineException("不存在名称为[" + name + "]的关系引用定义");
	}

}
