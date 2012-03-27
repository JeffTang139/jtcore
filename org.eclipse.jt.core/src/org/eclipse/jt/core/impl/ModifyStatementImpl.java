package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.MissingDefineException;
import org.eclipse.jt.core.def.query.ModifyStatementDeclare;
import org.eclipse.jt.core.def.query.RelationColumnDefine;
import org.eclipse.jt.core.def.table.TableFieldDefine;
import org.eclipse.jt.core.exception.NullArgumentException;

/**
 * �������
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
	 * ���±�����
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
	 * ֻ����ʱ�ṩ�洢���̶���ʹ�õĹ��췽��
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
			throw new NullArgumentException("�ֶζ���");
		}
		return new TableFieldRefImpl(this.moTableRef, field);
	}

	final TableFieldDefineImpl checkOwner(TableFieldDefine field) {
		TableFieldDefineImpl f = (TableFieldDefineImpl) field;
		if (f.owner != this.moTableRef.target) {
			throw new IllegalArgumentException("�ֶζ���[" + f.name
					+ "]�����ڵ�ǰ��������Ŀ�����[" + this.moTableRef.target.name + "].");
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
		return new MissingDefineException("����������Ϊ[" + name + "]�Ĺ�ϵ���ö���");
	}

}
