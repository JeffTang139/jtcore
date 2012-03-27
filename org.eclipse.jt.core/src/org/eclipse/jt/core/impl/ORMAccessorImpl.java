package org.eclipse.jt.core.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.eclipse.jt.core.def.obja.DynamicObject;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.misc.ObjectBuilder;
import org.eclipse.jt.core.type.GUID;


/**
 * 实体绑定数据库访问对象
 * 
 * @author Jeff Tang
 * 
 * @param <TEntity>
 *            绑定实体
 */
final class ORMAccessorImpl<TEntity> extends
		PsHolder<ORMAccessorProxy<TEntity>> {

	public final void insert(TEntity entity) {
		if (entity == null) {
			throw new NullArgumentException("entity");
		}
		this.ensureInserter().executeUpdate(entity);
	}

	public final void insert(TEntity entity, TEntity... others) {
		if (entity == null) {
			throw new NullArgumentException("entity");
		}
		Inserter inserter = this.ensureInserter();
		inserter.executeUpdate(entity);
		if (others != null) {
			for (TEntity other : others) {
				if (other == null) {
					throw new NullArgumentException("other");
				}
				inserter.executeUpdate(other);
			}
		}
	}

	public final void insert(TEntity[] entities) {
		if (entities == null) {
			throw new NullArgumentException("entities");
		}
		Inserter inserter = this.ensureInserter();
		for (TEntity entity : entities) {
			if (entity == null) {
				throw new NullArgumentException("entity");
			}
			inserter.executeUpdate(entity);
		}
	}

	public final void insert(Iterable<TEntity> entities) {
		if (entities == null) {
			throw new NullArgumentException("entities");
		}
		Inserter inserter = this.ensureInserter();
		for (TEntity entity : entities) {
			if (entity == null) {
				throw new NullArgumentException("entity");
			}
			inserter.executeUpdate(entity);
		}
	}

	public final boolean delete(GUID recid) {
		this.adapter.checkAccessible();
		if (recid == null) {
			throw new NullArgumentException("recid");
		}
		return this.ensureByRecidDeleter().delete(recid) > 0;
	}

	public final int delete(GUID recid, GUID... others) {
		this.adapter.checkAccessible();
		if (recid == null) {
			throw new NullArgumentException("recid");
		}
		if (others == null || others.length == 0) {
			return this.delete(recid) ? 1 : 0;
		} else {
			return this.ensureByRecidsDeleter().delete(recid, others);
		}
	}

	public final int delete(GUID[] recids) {
		this.adapter.checkAccessible();
		if (recids == null) {
			throw new NullArgumentException("recids");
		}
		if (recids.length > 0) {
			return this.ensureByRecidsDeleter().delete(null, recids);
		} else {
			return 0;
		}
	}

	public final boolean delete(TEntity entity) {
		this.adapter.checkAccessible();
		if (entity == null) {
			throw new NullArgumentException("entity");
		}
		return this.ensureDeleter().executeUpdate(entity) > 0;
	}

	public final boolean delete(GUID recid, long expectRECVER) {
		this.adapter.checkAccessible();
		if (recid == null) {
			throw new NullArgumentException("recid");
		}
		if (this.entityValueObj == null) {
			this.entityValueObj = this.mStatement.newEntity(null);
		}
		return this.ensureRecveredDeleter().delete(this.entityValueObj, recid,
				expectRECVER);
	}

	public final int delete(TEntity entity, TEntity... others) {
		this.adapter.checkAccessible();
		if (entity == null) {
			throw new NullArgumentException("entity");
		}
		int r = 0;
		Deleter deleter = this.ensureDeleter();
		if (deleter.executeUpdate(entity) > 0) {
			r++;
		}
		if (others != null) {
			for (TEntity other : others) {
				if (other == null) {
					throw new NullArgumentException("other");
				}
				if (deleter.executeUpdate(other) > 0) {
					r++;
				}
			}
		}
		return r;
	}

	public final int delete(TEntity[] entities) {
		this.adapter.checkAccessible();
		if (entities == null) {
			throw new NullArgumentException("entities");
		}
		Deleter deleter = this.ensureDeleter();
		int r = 0;
		for (TEntity entity : entities) {
			if (entity == null) {
				throw new NullArgumentException("entity");
			}
			if (deleter.executeUpdate(entity) > 0) {
				r++;
			}
		}
		return r;
	}

	public final int delete(Iterable<TEntity> entities) {
		this.adapter.checkAccessible();
		if (entities == null) {
			throw new NullArgumentException("entities");
		}
		Deleter deleter = this.ensureDeleter();
		int r = 0;
		for (TEntity entity : entities) {
			if (entity == null) {
				throw new NullArgumentException("entity");
			}
			if (deleter.executeUpdate(entity) > 0) {
				r++;
			}
		}
		return r;
	}

	public final int deleteByPKey(Object... keys) {
		this.adapter.checkAccessible();
		if (keys == null || keys.length == 0) {
			throw new NullArgumentException("逻辑主键值");
		}
		if (this.entityValueObj == null) {
			this.entityValueObj = this.mStatement.newEntity(null);
		}
		return this.ensureByLpkDeleter().executeUpdate(this.entityValueObj,
				keys);
	}

	public final boolean update(TEntity entity) {
		this.adapter.checkAccessible();
		if (entity == null) {
			throw new NullArgumentException("entity");
		}
		return this.ensureUpdater().executeUpdate(entity) > 0;
	}

	public final int update(TEntity entity, TEntity... others) {
		this.adapter.checkAccessible();
		if (entity == null) {
			throw new NullArgumentException("entity");
		}
		Updater updater = this.ensureUpdater();
		int r = updater.executeUpdate(entity) > 0 ? 1 : 0;
		if (others != null) {
			for (TEntity other : others) {
				if (other == null) {
					throw new NullArgumentException("other");
				}
				if (updater.executeUpdate(other) > 0) {
					r++;
				}
			}
		}
		return r;
	}

	public final int update(TEntity[] entities) {
		this.adapter.checkAccessible();
		if (entities == null) {
			throw new NullArgumentException("entities");
		}
		Updater updater = this.ensureUpdater();
		int r = 0;
		for (TEntity entity : entities) {
			if (entity == null) {
				throw new NullArgumentException("entity");
			}
			if (updater.executeUpdate(entity) > 0) {
				r++;
			}
		}
		return r;
	}

	public final int update(Iterable<TEntity> entities) {
		this.adapter.checkAccessible();
		if (entities == null) {
			throw new NullArgumentException("entities");
		}
		Updater updater = this.ensureUpdater();
		int r = 0;
		for (TEntity entity : entities) {
			if (entity == null) {
				throw new NullArgumentException("entity");
			}
			if (updater.executeUpdate(entity) > 0) {
				r++;
			}
		}
		return r;
	}

	public final boolean update(TEntity entity, long expectedRECVER) {
		this.adapter.checkAccessible();
		if (entity == null) {
			throw new NullArgumentException("entity");
		}
		return this.ensureRecveredUpdater().update(entity, expectedRECVER) > 0;
	}

	public final List<TEntity> fetch(Object... argValues) {
		return this.internalFetch(null, null, argValues);
	}

	public final List<TEntity> fetch(List<Object> argValues) {
		return this.internalFetch(null, null,
				argValues.toArray(new Object[argValues.size()]));
	}

	public final List<TEntity> fetch(ObjectBuilder<TEntity> entityFactory,
			Object... argValues) {
		if (entityFactory == null) {
			throw new NullPointerException();
		}
		return this.internalFetch(null, entityFactory, argValues);
	}

	public final List<TEntity> fetch(ObjectBuilder<TEntity> entityFactory,
			List<Object> argValues) {
		if (entityFactory == null) {
			throw new NullPointerException();
		}
		return this.internalFetch(null, entityFactory,
				argValues.toArray(new Object[argValues.size()]));
	}

	public final List<TEntity> fetchLimit(long offset, long rowCount,
			Object... argValues) {
		return this.internalFetchLimit(null, offset, rowCount, null, argValues);
	}

	public final List<TEntity> fetchLimit(long offset, long rowCount,
			List<Object> argValues) {
		return this.internalFetchLimit(null, offset, rowCount, null,
				argValues.toArray(new Object[argValues.size()]));
	}

	public final List<TEntity> fetchLimit(long offset, long rowCount,
			ObjectBuilder<TEntity> entityFactory, Object... argValues) {
		if (entityFactory == null) {
			throw new NullPointerException();
		}
		return this.internalFetchLimit(null, offset, rowCount, entityFactory,
				argValues);
	}

	public final List<TEntity> fetchLimit(long offset, long rowCount,
			ObjectBuilder<TEntity> entityFactory, List<Object> argValues) {
		if (entityFactory == null) {
			throw new NullPointerException();
		}
		return this.internalFetchLimit(null, offset, rowCount, entityFactory,
				argValues.toArray(new Object[argValues.size()]));
	}

	public final long fetchInto(List<TEntity> into, Object... argValues) {
		return this.internalFetchInto(into, null, argValues);
	}

	public final long fetchInto(List<TEntity> into, List<Object> argValues) {
		return this.internalFetchInto(into, null,
				argValues.toArray(new Object[argValues.size()]));
	}

	public final long fetchInto(List<TEntity> into,
			ObjectBuilder<TEntity> entityFactory, Object... argValues) {
		if (entityFactory == null) {
			throw new NullPointerException();
		}
		return this.internalFetchInto(into, entityFactory, argValues);
	}

	public final long fetchInto(List<TEntity> into,
			ObjectBuilder<TEntity> entityFactory, List<Object> argValues) {
		if (entityFactory == null) {
			throw new NullPointerException();
		}
		return this.internalFetchInto(into, entityFactory,
				argValues.toArray(new Object[argValues.size()]));
	}

	public final long fetchLimitInto(List<TEntity> into, long offset,
			long rowCount, Object... argValues) {
		return this.internalFetchLimitInto(into, offset, rowCount, null,
				argValues);
	}

	public final long fetchLimitInto(List<TEntity> into, long offset,
			long rowCount, List<Object> argValues) {
		return this.internalFetchLimitInto(into, offset, rowCount, null,
				argValues.toArray(new Object[argValues.size()]));
	}

	public final long fetchLimitInto(List<TEntity> into, long offset,
			long rowCount, ObjectBuilder<TEntity> entityFactory,
			Object... argValues) {
		if (entityFactory == null) {
			throw new NullPointerException();
		}
		return this.internalFetchLimitInto(into, offset, rowCount,
				entityFactory, argValues);
	}

	public final long fetchLimitInto(List<TEntity> into, long offset,
			long rowCount, ObjectBuilder<TEntity> entityFactory,
			List<Object> argValues) {
		if (entityFactory == null) {
			throw new NullPointerException();
		}
		return this.internalFetchLimitInto(into, offset, rowCount,
				entityFactory, argValues.toArray(new Object[argValues.size()]));
	}

	public final long rowCountOf(Object... argValues) {
		final RowCountQuerier querier = this.ensureRowCountQuerier();
		setArgumentValues(this.argValueObj, this.mStatement, argValues);
		return querier.executeLongScalar(this.argValueObj);
	}

	public final long rowCountOf(List<Object> argValues) {
		return this.rowCountOf(this.mStatement,
				argValues.toArray(new Object[argValues.size()]));
	}

	public final TEntity first(Object... argValues) {
		return this.internalFirst(null, argValues);
	}

	public final TEntity first(List<Object> argValues) {
		return this.internalFirst(null,
				argValues.toArray(new Object[argValues.size()]));
	}

	public final TEntity first(ObjectBuilder<TEntity> entityFactory,
			Object... argValues) {
		if (entityFactory == null) {
			throw new NullPointerException();
		}
		return this.internalFirst(entityFactory, argValues);
	}

	public final TEntity first(ObjectBuilder<TEntity> entityFactory,
			List<Object> argValues) {
		if (entityFactory == null) {
			throw new NullPointerException();
		}
		return this.internalFirst(entityFactory,
				argValues.toArray(new Object[argValues.size()]));
	}

	public final TEntity findByRECID(GUID recid) {
		return this.internalFindByRecid(null, recid);
	}

	public final TEntity findByRECID(ObjectBuilder<TEntity> entityFactory,
			GUID recid) {
		if (entityFactory == null) {
			throw new NullPointerException();
		}
		return this.internalFindByRecid(entityFactory, recid);
	}

	public final TEntity findByPKey(Object... keyValues) {
		return this.internalFindByPKey(null, keyValues);
	}

	public final TEntity findByPKey(ObjectBuilder<TEntity> entityFactory,
			Object... keyValues) {
		if (entityFactory == null) {
			throw new NullPointerException();
		}
		return this.internalFindByPKey(entityFactory, keyValues);
	}

	// public final List<TEntity> getChildren(HierarchyDefine hierarchy, GUID
	// recid) {
	// if (recid == null) {
	// throw new NullPointerException();
	// }
	// return this.getChildren0(null, this.checkHierarchy(hierarchy), recid);
	// }
	//
	// public final List<TEntity> getChildren(
	// ObjectBuilder<TEntity> entityFactory, HierarchyDefine hierarchy,
	// GUID recid) {
	// if (entityFactory == null) {
	// throw new NullPointerException();
	// }
	// if (recid == null) {
	// throw new NullPointerException();
	// }
	// return this.getChildren0(entityFactory, this.checkHierarchy(hierarchy),
	// recid);
	// }
	//
	// public final TreeNode<TEntity> getDescendant(HierarchyDefine hierarchy,
	// GUID recid) {
	// if (recid == null) {
	// throw new NullPointerException();
	// }
	// return this.getDescendant0(null, this.checkHierarchy(hierarchy), recid,
	// -1);
	// }
	//
	// public final TreeNode<TEntity> getDescendant(
	// ObjectBuilder<TEntity> entityFactory, HierarchyDefine hierarchy,
	// GUID recid) {
	// if (entityFactory == null) {
	// throw new NullPointerException();
	// }
	// if (recid == null) {
	// throw new NullPointerException();
	// }
	// return this.getDescendant0(entityFactory, this
	// .checkHierarchy(hierarchy), recid, -1);
	// }
	//
	// public final TreeNode<TEntity> getDescendant(HierarchyDefine hierarchy,
	// GUID recid, int range) {
	// if (recid == null) {
	// throw new NullPointerException();
	// }
	// if (range <= 0) {
	// throw new IllegalArgumentException();
	// }
	// return this.getDescendant0(null, this.checkHierarchy(hierarchy), recid,
	// range);
	// }
	//
	// public final TreeNode<TEntity> getDescendant(
	// ObjectBuilder<TEntity> entityFactory, HierarchyDefine hierarchy,
	// GUID recid, int range) {
	// if (entityFactory == null) {
	// throw new NullPointerException();
	// }
	// if (recid == null) {
	// throw new NullPointerException();
	// }
	// if (range <= 0) {
	// throw new IllegalArgumentException();
	// }
	// return this.getDescendant0(entityFactory, this
	// .checkHierarchy(hierarchy), recid, range);
	// }

	@Override
	public final void unuse() {
		for (HoldedExecutor<?> ps = this.executors; ps != null; ps = ps.next) {
			// 只是把数据库资源释放掉,对对象仍然可用
			ps.unuse();
		}
	}

	final MappingQueryStatementImpl mStatement;

	private DynamicObject argValueObj;

	private TEntity entityValueObj;

	private HoldedExecutor<?> executors;

	ORMAccessorImpl(DBAdapterImpl adapter, MappingQueryStatementImpl statement,
			ORMAccessorProxy<TEntity> proxy) {
		super(adapter, proxy);
		this.argValueObj = new DynamicObject();
		this.mStatement = statement;
	}

	// ------------------------------- internal -------------------------------

	private final List<TEntity> internalFetch(List<TEntity> into,
			ObjectBuilder<TEntity> entityFactory, Object[] argValues) {
		this.adapter.checkAccessible();
		try {
			StatementExecutor querier = this.ensureQuerier();
			setArgumentValues(this.argValueObj, this.mStatement, argValues);
			ResultSet resultSet = querier.executeQuery(this.argValueObj);
			try {
				return ResultSetReader.readEntities(entityFactory, into,
						this.mStatement, resultSet);
			} finally {
				resultSet.close();
			}
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	private final int internalFetchInto(List<TEntity> into,
			ObjectBuilder<TEntity> entityFactory, Object[] argValues) {
		if (into == null) {
			throw new NullArgumentException("into");
		}
		int s = into.size();
		this.internalFetch(into, entityFactory, argValues);
		return into.size() - s;
	}

	private final List<TEntity> internalFetchLimit(List<TEntity> into,
			long offset, long rowCount, ObjectBuilder<TEntity> entityFactory,
			Object[] argValues) {
		this.adapter.checkAccessible();
		try {
			ResultSet rs = null;
			if (offset == 0) {
				TopQuerier querier = this.ensureTopQuerier();
				setArgumentValues(this.argValueObj, this.mStatement, argValues);
				rs = querier.executeQuery(this.argValueObj, rowCount);
			} else {
				LimitQuerier querier = this.ensureLimitQuerier();
				setArgumentValues(this.argValueObj, this.mStatement, argValues);
				rs = querier.executeQuery(this.argValueObj, rowCount, offset);
			}
			try {
				return ResultSetReader.readEntities(entityFactory, into,
						this.mStatement, rs);
			} finally {
				if (rs != null) {
					rs.close();
				}
			}
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	private final int internalFetchLimitInto(List<TEntity> into, long offset,
			long rowCount, ObjectBuilder<TEntity> entityFactory,
			Object[] argValues) {
		if (into == null) {
			throw new NullArgumentException("into");
		}
		int s = into.size();
		this.internalFetchLimit(into, offset, rowCount, entityFactory,
				argValues);
		return into.size() - s;
	}

	private final TEntity internalFirst(ObjectBuilder<TEntity> entityFactory,
			Object[] argValues) {
		this.adapter.checkAccessible();
		try {
			StatementExecutor querier = this.ensureQuerier();
			setArgumentValues(this.argValueObj, this.mStatement, argValues);
			ResultSet resultSet = querier.executeQuery(this.argValueObj);
			try {
				return ResultSetReader.readNextEntity(entityFactory,
						this.mStatement, resultSet);
			} finally {
				resultSet.close();
			}
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	private final TEntity internalFindByRecid(
			ObjectBuilder<TEntity> entityFactory, GUID recid) {
		this.adapter.checkAccessible();
		if (recid == null) {
			throw new NullArgumentException("行标识");
		}
		try {
			ResultSet resultSet = this.ensureByRecidQuerier().executeQuery(
					recid);
			try {
				return ResultSetReader.readNextEntity(entityFactory,
						this.mStatement, resultSet);
			} finally {
				resultSet.close();
			}
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	private final TEntity internalFindByPKey(
			ObjectBuilder<TEntity> entityFactory, Object[] keys) {
		this.adapter.checkAccessible();
		if (keys == null || keys.length == 0) {
			throw new NullPointerException();
		}
		if (this.entityValueObj == null) {
			this.entityValueObj = this.mStatement.newEntity(null);
		}
		try {
			ResultSet resultSet = this.ensureByLpkQuerier().executeQuery(
					this.entityValueObj, keys);
			try {
				return ResultSetReader.readNextEntity(entityFactory,
						this.mStatement, resultSet);
			} finally {
				resultSet.close();
			}
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	// private final HierarchyDefineImpl checkHierarchy(HierarchyDefine
	// hierarchy) {
	// HierarchyDefineImpl h = (HierarchyDefineImpl) hierarchy;
	// if (h.owner != this.mStatement.rootRelationRef().getTarget()) {
	// throw new IllegalArgumentException("级次不属于主表引用的目标表");
	// }
	// return h;
	// }
	//
	// private final List<TEntity> getChildren0(
	// ObjectBuilder<TEntity> entityFactory,
	// HierarchyDefineImpl hierarchy, GUID recid) {
	// try {
	// byte[] path = this.ensureHierarchyPathQuerier(hierarchy).getPath(
	// recid);
	// ResultSet rs = this.getChildrenQuerier(hierarchy)
	// .executeQuery(path);
	// try {
	// return ResultSetReader.readEntities(entityFactory, null,
	// this.mStatement, rs);
	// } finally {
	// rs.close();
	// }
	// } catch (SQLException e) {
	// throw Utils.tryThrowException(e);
	// }
	// }
	//
	// private final TreeNodeImpl<TEntity> getDescendant0(
	// ObjectBuilder<TEntity> entityFactory,
	// HierarchyDefineImpl hierarchy, GUID rootRECID, int range) {
	// try {
	// byte[] rootPath = this.ensureHierarchyPathQuerier(hierarchy)
	// .getPath(rootRECID);
	// ResultSet rs;
	// if (range <= 0) {
	// rs = this.getDescendantQuerier(hierarchy)
	// .executeQuery(rootPath);
	// } else {
	// rs = this.getRangeDescendantQuerier(hierarchy, range)
	// .executeQuery(rootPath);
	// }
	// try {
	// TreeNodeImpl<TEntity> rootNode = new TreeNodeImpl<TEntity>(
	// null, null);
	// ArrayList<GUID> recidPath = new ArrayList<GUID>();
	// ArrayList<TreeNodeImpl<TEntity>> nodePath = new
	// ArrayList<TreeNodeImpl<TEntity>>();
	// recidPath.add(rootRECID);
	// nodePath.add(rootNode);
	// // 可能throw exception
	// int columnIndexOfParentRECID = rs.findColumn(DBLang.CA_PARENT);
	// QueryColumnImpl column = this.mStatement.findRootRecidColumn();
	// int columnIndexOfRECID;
	// StructFieldDefineImpl recidField;
	// if (column == null) {
	// columnIndexOfRECID = rs.findColumn(DBLang.CA_RECID);
	// recidField = null;
	// } else {
	// columnIndexOfRECID = 0;
	// recidField = column.targetField;
	// }
	// ResultSetReader reader = this.mStatement.mappingTarget
	// .newResultSetReader(rs);
	// while (rs.next()) {
	// TEntity entity = reader.readEntity(entityFactory,
	// this.mStatement);
	// GUID entityRECID;
	// if (recidField == null) {
	// entityRECID = GUID.valueOf(rs
	// .getBytes(columnIndexOfRECID));
	// } else {
	// entityRECID = recidField.getFieldValueAsGUID(entity);
	// }
	// GUID entityParent = GUID.valueOf(rs
	// .getBytes(columnIndexOfParentRECID));
	// for (int i = recidPath.size() - 1; i >= 0; i--) {
	// if (entityParent == recidPath.get(i)) {
	// recidPath.add(entityRECID);
	// nodePath.add(nodePath.get(i).append(entity));
	// break;
	// } else {
	// recidPath.remove(i);
	// nodePath.remove(i);
	// }
	// }
	// }
	// return rootNode;
	// } finally {
	// rs.close();
	// }
	// } catch (SQLException e) {
	// throw Utils.tryThrowException(e);
	// }
	// }

	private StatementExecutor querier;

	private TopQuerier topQuerier;

	private LimitQuerier limitQuerier;

	private RowCountQuerier rowCountQuerier;

	private Inserter inserter;

	private Deleter deleter;

	private Updater updater;

	private RecverUpdater recveredUpdater;

	private RecverDeleter recveredDeleter;

	private ByRecidDeleter recidDeleter;

	private ByRecidsDeleter recidsDeleter;

	private ByLpkDeleter lpkDeleter;

	private ByRecidQuerier recidQuerier;

	private ByLpkQuerier lpkQuerier;

	private final void enqueue(HoldedExecutor<?> executor) {
		if (this.executors == null) {
			this.executors = executor;
		} else {
			executor.next = this.executors;
			this.executors = executor;
		}
	}

	private final StatementExecutor ensureQuerier() {
		StatementExecutor querier = this.querier;
		if (querier == null) {
			this.querier = querier = new StatementExecutor(this,
					this.mStatement);
			this.enqueue(querier);
		}
		return querier;
	}

	private final TopQuerier ensureTopQuerier() {
		TopQuerier topQuerier = this.topQuerier;
		if (topQuerier == null) {
			this.topQuerier = topQuerier = new TopQuerier(this, this.mStatement);
			this.enqueue(topQuerier);
		}
		return topQuerier;
	}

	private final LimitQuerier ensureLimitQuerier() {
		LimitQuerier limitQuerier = this.limitQuerier;
		if (limitQuerier == null) {
			this.limitQuerier = limitQuerier = new LimitQuerier(this,
					this.mStatement);
			this.enqueue(limitQuerier);
		}
		return limitQuerier;
	}

	private final RowCountQuerier ensureRowCountQuerier() {
		RowCountQuerier rowCountQuerier = this.rowCountQuerier;
		if (rowCountQuerier == null) {
			this.rowCountQuerier = rowCountQuerier = new RowCountQuerier(this,
					this.mStatement);
			this.enqueue(rowCountQuerier);
		}
		return rowCountQuerier;
	}

	private final Inserter ensureInserter() {
		Inserter inserter = this.inserter;
		if (inserter == null) {
			this.inserter = inserter = new Inserter(this);
			this.enqueue(inserter);
		}
		return inserter;
	}

	private final Deleter ensureDeleter() {
		Deleter deleter = this.deleter;
		if (deleter == null) {
			this.deleter = deleter = new Deleter(this);
			this.enqueue(deleter);
		}
		return deleter;
	}

	private final Updater ensureUpdater() {
		Updater updater = this.updater;
		if (updater == null) {
			this.updater = updater = new Updater(this);
			this.enqueue(updater);
		}
		return updater;
	}

	private final RecverDeleter ensureRecveredDeleter() {
		RecverDeleter recveredDeleter = this.recveredDeleter;
		if (recveredDeleter == null) {
			this.recveredDeleter = recveredDeleter = new RecverDeleter(this);
			this.enqueue(recveredDeleter);
		}
		return recveredDeleter;
	}

	private final RecverUpdater ensureRecveredUpdater() {
		RecverUpdater recveredUpdater = this.recveredUpdater;
		if (recveredUpdater == null) {
			this.recveredUpdater = recveredUpdater = new RecverUpdater(this);
			this.enqueue(recveredUpdater);
		}
		return recveredUpdater;
	}

	private final ByRecidQuerier ensureByRecidQuerier() {
		ByRecidQuerier recidQuerier = this.recidQuerier;
		if (recidQuerier == null) {
			this.recidQuerier = recidQuerier = new ByRecidQuerier(this);
			this.enqueue(recidQuerier);
		}
		return recidQuerier;
	}

	private final ByLpkQuerier ensureByLpkQuerier() {
		ByLpkQuerier lpkQuerier = this.lpkQuerier;
		if (lpkQuerier == null) {
			this.lpkQuerier = lpkQuerier = new ByLpkQuerier(this);
			this.enqueue(lpkQuerier);
		}
		return lpkQuerier;
	}

	private final ByRecidDeleter ensureByRecidDeleter() {
		ByRecidDeleter recidDeleter = this.recidDeleter;
		if (recidDeleter == null) {
			this.recidDeleter = recidDeleter = new ByRecidDeleter(this);
			this.enqueue(recidDeleter);
		}
		return recidDeleter;
	}

	private final ByRecidsDeleter ensureByRecidsDeleter() {
		ByRecidsDeleter recidsDeleter = this.recidsDeleter;
		if (recidsDeleter == null) {
			this.recidsDeleter = recidsDeleter = new ByRecidsDeleter(this);
			this.enqueue(recidsDeleter);
		}
		return recidsDeleter;
	}

	private final ByLpkDeleter ensureByLpkDeleter() {
		ByLpkDeleter lpkDeleter = this.lpkDeleter;
		if (lpkDeleter == null) {
			this.lpkDeleter = lpkDeleter = new ByLpkDeleter(this);
			this.enqueue(lpkDeleter);
		}
		return lpkDeleter;
	}

	// private final HierarchyPathQuerier ensureHierarchyPathQuerier(
	// HierarchyDefineImpl hierarchy) {
	// for (PsExecutor<?> ps = this.stmts; ps != null; ps = ps.next) {
	// if (ps instanceof HierarchyPathQuerier
	// && ((HierarchyPathQuerier) ps).hierarchy == hierarchy) {
	// return (HierarchyPathQuerier) ps;
	// }
	// }
	// HierarchyPathQuerier querier = new HierarchyPathQuerier(this, hierarchy);
	// querier.next = this.stmts;
	// this.stmts = querier;
	// return querier;
	// }
	//
	// private final ChildrenQuerier getChildrenQuerier(
	// HierarchyDefineImpl hierarchy) {
	// for (PsExecutor<?> ps = this.stmts; ps != null; ps = ps.next) {
	// if (ps instanceof ChildrenQuerier
	// && ((ChildrenQuerier) ps).hierarchy == hierarchy) {
	// return (ChildrenQuerier) ps;
	// }
	// }
	// ChildrenQuerier querier = new ChildrenQuerier(this, hierarchy);
	// querier.next = this.stmts;
	// this.stmts = querier;
	// return querier;
	// }
	//
	// private final DescendantQuerier getDescendantQuerier(
	// HierarchyDefineImpl hierarchy) {
	// for (PsExecutor<?> ps = this.stmts; ps != null; ps = ps.next) {
	// if (ps instanceof DescendantQuerier
	// && ((DescendantQuerier) ps).hierarchy == hierarchy) {
	// return (DescendantQuerier) ps;
	// }
	// }
	// DescendantQuerier querier = new DescendantQuerier(this, hierarchy);
	// querier.next = this.stmts;
	// this.stmts = querier;
	// return querier;
	// }
	//
	// private final RangeDescendantQuerier getRangeDescendantQuerier(
	// HierarchyDefineImpl hierarchy, int range) {
	// for (PsExecutor<?> ps = this.stmts; ps != null; ps = ps.next) {
	// if (ps instanceof RangeDescendantQuerier) {
	// RangeDescendantQuerier q = (RangeDescendantQuerier) ps;
	// if (q.hierarchy == hierarchy && q.range == range) {
	// return q;
	// }
	// }
	// }
	// RangeDescendantQuerier querier = new RangeDescendantQuerier(this,
	// hierarchy, range);
	// querier.next = this.stmts;
	// this.stmts = querier;
	// return querier;
	// }

	static final class Inserter extends HoldedExecutor<RowInsertSql> {

		Inserter(ORMAccessorImpl<?> orm) {
			super(orm, orm.mStatement.getRowInsertSql(orm.adapter));
		}
	}

	static final class Deleter extends HoldedExecutor<RowDeleteSql> {

		Deleter(ORMAccessorImpl<?> orm) {
			super(orm, orm.mStatement.getRowDeleteSql(orm.adapter));
		}
	}

	static final class Updater extends HoldedExecutor<RowUpdateSql> {

		Updater(ORMAccessorImpl<?> orm) {
			super(orm, orm.mStatement.getRowUpdateSql(orm.adapter));
		}
	}

	static final class RecverDeleter extends HoldedExecutor<ObjRecverDeleteSql> {

		RecverDeleter(ORMAccessorImpl<?> orm) {
			super(orm, orm.mStatement.getObjRecveredDeleteSql(orm.adapter));
		}

		final boolean delete(Object entity, GUID recid, long expectRecver) {
			try {
				super.use(true);
				this.sql.arg_recid.setFieldValueAsGUID(entity, recid);
				this.flushParameters(entity);
				this.sql.recver.setLong(this.ps, expectRecver);
				return this.adapter.jdbcUpdate(this) > 0;
			} catch (SQLException e) {
				throw Utils.tryThrowException(e);
			}
		}

	}

	static final class RecverUpdater extends HoldedExecutor<ObjRecverUpdateSql> {

		RecverUpdater(ORMAccessorImpl<?> orm) {
			super(orm, orm.mStatement.getObjRecveredUpdateSql(orm.adapter));
		}

		final int update(Object entity, long expectRecver) {
			try {
				super.use(true);
				this.flushParameters(entity);
				this.sql.recver.setLong(this.ps, expectRecver);
				return this.adapter.jdbcUpdate(this);
			} catch (SQLException e) {
				throw Utils.tryThrowException(e);
			}
		}

	}

	static final class ByRecidDeleter extends
			HoldedExecutor<ObjByRecidDeleteSql> {

		ByRecidDeleter(ORMAccessorImpl<?> orm) {
			super(orm, orm.mStatement.getByRecidDeleteSql(orm.adapter));
		}

		final int delete(GUID recid) {
			try {
				super.use(true);
				this.sql.recid.setBytes(this.ps, recid.toBytes());
				return this.adapter.jdbcUpdate(this);
			} catch (SQLException e) {
				throw Utils.tryThrowException(e);
			}
		}
	}

	static final class ByRecidsDeleter extends
			HoldedExecutor<ObjByRecidsDeleteSql> {

		ByRecidsDeleter(ORMAccessorImpl<?> orm) {
			super(orm, orm.mStatement.getByRecidsDeleteSql(orm.adapter));
		}

		final int delete(GUID recid, GUID[] others) {
			try {
				int r = 0;
				int pi = 1;
				boolean used = false;
				if (recid != null) {
					if (!used) {
						super.use(true);
						used = true;
					}
					super.ps.setBytes(pi++, recid.toBytes());
				}
				for (int oi = 0; oi < others.length; oi++) {
					if (pi > SystemVariables.ORM_BYRECIDS_DELETE) {
						r += this.adapter.jdbcUpdate(this);
						pi = 1;
					}
					recid = others[oi];
					if (recid != null) {
						if (!used) {
							super.use(true);
							used = true;
						}
						super.ps.setBytes(pi++, recid.toBytes());
					}
				}
				if (pi > 1) {
					while (pi <= SystemVariables.ORM_BYRECIDS_DELETE) {
						super.ps.setBytes(pi++, null);
					}
					r += this.adapter.jdbcUpdate(this);
				}
				return r;
			} catch (SQLException e) {
				throw Utils.tryThrowException(e);
			}
		}
	}

	static final class ByLpkDeleter extends HoldedExecutor<ObjByLpkDeleteSql> {

		ByLpkDeleter(ORMAccessorImpl<?> orm) {
			super(orm, orm.mStatement.getByLpkDeleteSql(orm.adapter));
		}

		final int executeUpdate(Object entityArg, Object... keys) {
			try {
				this.use(true);
				for (int i = 0, c = Math.min(keys.length, this.sql.args.length); i < c; i++) {
					ParameterReserver pr = this.sql.args[i];
					if (pr instanceof ArgumentReserver) {
						ArgumentReserver ar = (ArgumentReserver) pr;
						ar.arg.setFieldValueAsObject(entityArg, keys[i]);
					}
				}
				this.flushParameters(entityArg);
				return this.adapter.jdbcUpdate(this);
			} catch (SQLException e) {
				throw Utils.tryThrowException(e);
			}
		}

	}

	static final class ByRecidQuerier extends
			HoldedExecutor<ObjByRecidQuerySql> {

		ByRecidQuerier(ORMAccessorImpl<?> orm) {
			super(orm, orm.mStatement.getByRecidQuerySql(orm.adapter));
		}

		final ResultSet executeQuery(GUID id) {
			try {
				super.use(false);
				this.sql.recid.setBytes(this.ps, id.toBytes());
				return this.adapter.jdbcQuery(this);
			} catch (SQLException e) {
				throw Utils.tryThrowException(e);
			}
		}
	}

	static final class ByLpkQuerier extends HoldedExecutor<ObjByLpkQuerySql> {

		ByLpkQuerier(ORMAccessorImpl<?> orm) {
			super(orm, orm.mStatement.getByLpkQuerySql(orm.adapter));
		}

		final ResultSet executeQuery(Object entityArg, Object... keys) {
			try {
				super.use(false);
				for (int i = 0, c = Math.min(keys.length,
						this.sql.parameters.size()); i < c; i++) {
					ParameterReserver pr = this.sql.parameters.get(i);
					if (pr instanceof ArgumentReserver) {
						ArgumentReserver ar = (ArgumentReserver) pr;
						ar.arg.setFieldValueAsObject(entityArg, keys[i]);
					}
				}
				this.flushParameters(entityArg);
				return this.adapter.jdbcQuery(this);
			} catch (SQLException e) {
				throw Utils.tryThrowException(e);
			}
		}

	}

}
