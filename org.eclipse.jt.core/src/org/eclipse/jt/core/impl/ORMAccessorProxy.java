package org.eclipse.jt.core.impl;

import java.util.List;

import org.eclipse.jt.core.da.ORMAccessor;
import org.eclipse.jt.core.misc.ObjectBuilder;
import org.eclipse.jt.core.type.GUID;


final class ORMAccessorProxy<TEntity> implements ORMAccessor<TEntity>,
		PsHolderProxy {

	public final void insert(TEntity entity) {
		this.orm.insert(entity);
	}

	public final void insert(TEntity entity, TEntity... others) {
		this.orm.insert(entity, others);
	}

	public final void insert(TEntity[] entities) {
		this.orm.insert(entities);
	}

	public final void insert(Iterable<TEntity> entities) {
		this.orm.insert(entities);
	}

	public final boolean delete(GUID recid) {
		return this.orm.delete(recid);
	}

	public final boolean delete(GUID recid, long expectRECVER) {
		return this.orm.delete(recid, expectRECVER);
	}

	public final int delete(GUID recid, GUID... others) {
		return this.orm.delete(recid, others);
	}

	public final int delete(GUID[] recids) {
		return this.orm.delete(recids);
	}

	public final boolean delete(TEntity entity) {
		return this.orm.delete(entity);
	}

	public final int delete(TEntity entity, TEntity... others) {
		return this.orm.delete(entity, others);
	}

	public final int delete(TEntity[] entities) {
		return this.orm.delete(entities);
	}

	public final int delete(Iterable<TEntity> entities) {
		return this.orm.delete(entities);
	}

	public final int deleteByPKey(Object... keys) {
		return this.orm.deleteByPKey(keys);
	}

	public final boolean update(TEntity entity) {
		return this.orm.update(entity);
	}

	public final int update(TEntity entity, TEntity... others) {
		return this.orm.update(entity, others);
	}

	public final int update(TEntity[] entities) {
		return this.orm.update(entities);
	}

	public final int update(Iterable<TEntity> entities) {
		return this.orm.update(entities);
	}

	public final boolean update(TEntity entity, long expectRECVER) {
		return this.orm.update(entity, expectRECVER);
	}

	public final TEntity first(Object... argValues) {
		return this.orm.first(argValues);
	}

	public final TEntity first(List<Object> argValues) {
		return this.orm.first(argValues);
	}

	public final TEntity first(ObjectBuilder<TEntity> entityFactory,
			Object... argValues) {
		return this.orm.first(entityFactory, argValues);
	}

	public final TEntity first(ObjectBuilder<TEntity> entityFactory,
			List<Object> argValues) {
		return this.orm.first(entityFactory, argValues);
	}

	public final List<TEntity> fetch(Object... argValues) {
		return this.orm.fetch(argValues);
	}

	public final List<TEntity> fetch(List<Object> argValues) {
		return this.orm.fetch(argValues);
	}

	public final List<TEntity> fetch(ObjectBuilder<TEntity> entityFactory,
			Object... argValues) {
		return this.orm.fetch(entityFactory, argValues);
	}

	public final List<TEntity> fetch(ObjectBuilder<TEntity> entityFactory,
			List<Object> argValues) {
		return this.orm.fetch(entityFactory, argValues);
	}

	public final List<TEntity> fetchLimit(long offset, long rowCount,
			Object... argValues) {
		return this.orm.fetchLimit(offset, rowCount, argValues);
	}

	public final List<TEntity> fetchLimit(long offset, long rowCount,
			List<Object> argValues) {
		return this.orm.fetchLimit(offset, rowCount, argValues);
	}

	public final List<TEntity> fetchLimit(long offset, long rowCount,
			ObjectBuilder<TEntity> entityFactory, Object... argValues) {
		return this.orm.fetchLimit(offset, rowCount, entityFactory, argValues);
	}

	public final List<TEntity> fetchLimit(long offset, long rowCount,
			ObjectBuilder<TEntity> entityFactory, List<Object> argValues) {
		return this.orm.fetchLimit(offset, rowCount, entityFactory, argValues);
	}

	public final int fetchInto(List<TEntity> into, Object... argValues) {
		return (int) this.orm.fetchInto(into, argValues);
	}

	public final int fetchInto(List<TEntity> into, List<Object> argValues) {
		return (int) this.orm.fetchInto(into, argValues);
	}

	public final int fetchInto(List<TEntity> into,
			ObjectBuilder<TEntity> entityFactory, Object... argValues) {
		return (int) this.orm.fetchInto(into, entityFactory, argValues);
	}

	public final int fetchInto(List<TEntity> into,
			ObjectBuilder<TEntity> entityFactory, List<Object> argValues) {
		return (int) this.orm.fetchInto(into, entityFactory, argValues);
	}

	public final int fetchIntoLimit(List<TEntity> into, long offset,
			long rowCount, Object... argValues) {
		return (int) this.orm.fetchLimitInto(into, offset, rowCount, argValues);
	}

	public final int fetchIntoLimit(List<TEntity> into, long offset,
			long rowCount, List<Object> argValues) {
		return (int) this.orm.fetchLimitInto(into, offset, rowCount, argValues);
	}

	public final int fetchIntoLimit(List<TEntity> into, long offset,
			long rowCount, ObjectBuilder<TEntity> entityFactory,
			Object... argValues) {
		return (int) this.orm.fetchLimitInto(into, offset, rowCount,
				entityFactory, argValues);
	}

	public final int fetchIntoLimit(List<TEntity> into, long offset,
			long rowCount, ObjectBuilder<TEntity> entityFactory,
			List<Object> argValues) {
		return (int) this.orm.fetchLimitInto(into, offset, rowCount,
				entityFactory, argValues);
	}

	public final TEntity findByPKey(Object... keyValues) {
		return this.orm.findByPKey(keyValues);
	}

	public final TEntity findByPKey(ObjectBuilder<TEntity> entityFactory,
			Object... keyValues) {
		return this.orm.findByPKey(entityFactory, keyValues);
	}

	public final TEntity findByRECID(GUID recid) {
		return this.orm.findByRECID(recid);
	}

	public final TEntity findByRECID(ObjectBuilder<TEntity> entityFactory,
			GUID recid) {
		return this.orm.findByRECID(entityFactory, recid);
	}

	public final int rowCountOf(Object... argValues) {
		return (int) this.orm.rowCountOf(argValues);
	}

	public final int rowCountOf(List<Object> argValues) {
		return (int) this.orm.rowCountOf(argValues);
	}

	public long rowCountOfL(List<Object> argValues) {
		return this.orm.rowCountOf(argValues);
	}

	public long rowCountOfL(Object... argValues) {
		return this.orm.rowCountOf(argValues);
	}

	public final void unuse() {
		this.orm.unuse();
	}

	final ORMAccessorImpl<TEntity> orm;

	public ORMAccessorProxy(DBAdapterImpl adapter,
			MappingQueryStatementImpl statement) {
		this.orm = new ORMAccessorImpl<TEntity>(adapter, statement, this);
	}

}
