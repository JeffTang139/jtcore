package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import org.eclipse.jt.core.exception.InvalidStatementDefineException;
import org.eclipse.jt.core.impl.UpdateStatementImpl.FieldAssign;


/**
 * 更新语句的Sql
 * 
 * <p>
 * dna-sql的Update语法主要参考了SQLServer的语法规则.相比SQLServer语法,省略from关键字,将join提前,
 * 但仍只是对单逻辑表的更新,并且等价于强制SQLServer的from子句的第一个表引用必须指向更新目标表.
 * 
 * <blockquote>
 * 
 * <pre>
 * update [Target] [left|right|full]
 * 	join [Reference] [,n..]
 * 	set [...]
 * 	where [...]</prev></blockquote>
 * 
 * <p>sqlserver对此语法的实际执行计划为:计算from子句的关系:如果包含了目标关系,则在目标关系上投影,
 * 对每个有效的投影行进行Update操作;如果不包含目标关系,则直接循环目标关系,对from关系流聚合后做更新操作.
 * 注意到连接的参考表实际提供了:行过滤,尤其内连接情况下;where条件的参考值;set子句的参考值.
 * 
 * <p>sqlserver的这种语法形式本质上是不够严谨的.
 * 例如,有关系A(id,fid)实例为((0,a),(1,b),(2,c))与关系B(id,k)实例为((a,10),(a,20),(c,30)),假设更新关系A,
 * from子句内做"A.fid = B.id"的内连接.由于关系B的id不是唯一的,导致对关系A的每一确定元祖,将会对应到多个B的元祖上.
 * 此时参考关系无论是提供where参考值或是set参考值都是不可行的.在这种情况下,sqlserver将对关系B做流聚合运算,实质为ANY运算.
 * 即取任意随机元祖,提供对关系A更新的参考.即意味着,更新结果是不稳定的.
 * 
 * <p>
 * oracle的Update语句,如使用标准sql提供类似实现,行过滤与where参考将单独组装成"子查询",set值参考将单独组装成"内联视图".
 * 这两个查询在执行计划中是分开进行的.而更接近于连接本质的语法:update(select ... from 目标关系 join 参考关系)set [...],
 * 这种语法,Oracle强制要求参考关系必须通过1对1或者1对n的关系连接到目标关系上,即强制要求(hint可取消)参考关系的等值连接列为主键或有唯一索引(多键唯一索引同理).
 * 这种处理方式才是严谨的.
 * 
 * <p>
 * 但由于提供了join连接,在实现上只能参考sqlserver的逻辑.
 * 
 * <p>
 * 由于dna-sql的update是针对单逻辑表的,即可能需要一次更新多个物理表.首先规定:
 * <ul>
 * <li>update指对整个逻辑表的更新.
 * <li>dbUpdate指对单一物理表的更新.
 * </ul>
 * 
 * <p>在多物理表的update时,存在情况:dbUpdate破坏了update的条件或其他dbUpdate的赋值来源.
 * 而从逻辑表update的层面来看,必须各dbUpdate的参考值都是针对 整个update的前值才是合理的.
 * 
 * <p>
 * 再定义术语:
 * <ol>
 * <li><strong>条件冲突(ConditonConflict)</strong>:update的目标字段,作为了update的条件,称该目标字段所在的物理表存在条件冲突.
 * <li><strong>赋值依赖(AssignDependOn)</strong>:目标dbUpdate的赋值(set值)参考了其他dbUpdate中会被更新的字段,称目标依赖于参考,实质为:目标字段必须先于参考字段进行.
 * <li><strong>条件来源(ConditionFrom)</strong>:在update的条件中,使用到的所有目标表的参考字段所属于的物理表.
 * </ol>
 * 
 * <p>
 * 在不考虑join的情况下,讨论多表更新可能的问题.当CC为1时,该物理表需要最后更新.当CC大于1时,任一dbUpdate都会破坏剩余dbUpdate的条件.
 * 赋值依赖本质为更新字段顺序的上的依赖,该依赖顺序是可能有解的,可以在字段级别,也可以在dbUpdate级别.目前为了简化编译,在dbUpdate级别处理.
 * 即ADO转换为dbUpdate顺序求解的问题,并且需要考虑CC来求解.
 */
// took too much time here...=.=
final class UpdateSql extends Sql {

	UpdateSql(DBLang lang, final UpdateStatementImpl update) {
		if (update.assigns.size() == 0) {
			throw new InvalidStatementDefineException("更新语句定义[" + update.name
					+ "]未定义任何更新列。");
		}
		final ISqlCommandFactory factory = lang.sqlbuffers();
		final ISqlUpdateMultiCommandFactory umf = factory
				.getFeature(ISqlUpdateMultiCommandFactory.class);
		final StatusVisitor status = new StatusVisitor();
		update.visit(status, null);
		if (update.moTableRef.target.dbTables.size() == 1) {
			single(update, status, factory, this);
		} else if (umf != null) {
			multipleOnce(update, status, umf, this);
		} else {
			MultipleResolver resolver = new MultipleResolver(update);
			if (resolver.list.size() == 1) {
				Single single = resolver.list.get(0);
				final String alias = Render.aliasOf(update.moTableRef,
						single.dbTable);
				ISqlUpdateBuffer buffer = factory.update(single.dbTable.name,
						alias, single.assignValueFromJoin());
				multiple(update, status, single, buffer, alias);
				this.build(buffer);
			} else if (resolver.tryResolveSequence()) {
				ISqlSegmentBuffer segment = factory.segment();
				for (Single single : resolver.list) {
					final String alias = Render.aliasOf(update.moTableRef,
							single.dbTable);
					ISqlUpdateBuffer buffer = segment.update(
							single.dbTable.name, alias,
							single.assignValueFromJoin());
					multiple(update, status, single, buffer, alias);
				}
				this.build(segment);
			} else {
				ISqlSegmentBuffer segment = factory.segment();
				cursor(update, status, resolver, segment);
				this.build(segment);
			}
		}
	}

	/**
	 * 逻辑表的更新信息记录
	 * 
	 * <p>
	 * 同时用作更新条件以及连接条件的检查器,检查条件来源及条件冲突
	 * 
	 * @author Jeff Tang
	 * 
	 */
	private static final class MultipleResolver extends
			TraversedExprVisitor<Object> {

		final ArrayList<Single> list = new ArrayList<Single>();

		final UpdateStatementImpl update;

		final TableDefineImpl table;

		private boolean assignValueDependOn;

		final IntBits conditionConflict = new IntBits();

		MultipleResolver(UpdateStatementImpl update) {
			this.update = update;
			this.table = update.moTableRef.target;
			Single single = null;
			for (int i = 0; i < update.assigns.size(); i++) {
				FieldAssign fa = update.assigns.get(i);
				DBTableDefineImpl dbTable = fa.field.dbTable;
				if (single != null && single.dbTable == dbTable) {
					single.list.add(fa);
				} else {
					ensure: {
						for (int ti = 0, c = this.list.size(); ti < c; ti++) {
							single = this.list.get(ti);
							if (single.dbTable == dbTable) {
								single.list.add(fa);
								break ensure;
							}
						}
						single = new Single(this, dbTable);
						this.list.add(single);
						single.list.add(fa);
					}
				}
			}
			if (this.update.condition != null) {
				this.update.condition.visit(this, null);
			}
			if (this.update.moTableRef.getJoins() != null) {
				for (MoRelationRef relationRef : this.update.moTableRef) {
					relationRef.visit(this, null);
				}
			}
			for (int i = 0, c = this.list.size(); i < c; i++) {
				this.list.get(i).visitAssignValue();
			}
		}

		private final Single get(int dbTableIndex) {
			for (int i = 0, c = this.list.size(); i < c; i++) {
				Single single = this.list.get(i);
				if (single.dbTableIndex == dbTableIndex) {
					return single;
				}
			}
			throw new NullPointerException();
		}

		boolean conditionNonDeterministic;

		@Override
		public void visitOperateExpr(OperateExpr expr, Object context) {
			super.visitOperateExpr(expr, context);
			if (expr.isNonDeterministic()) {
				this.conditionNonDeterministic = true;
			}
		}

		@Override
		public void visitTableFieldRef(TableFieldRefImpl fieldRef,
				Object context) {
			if (fieldRef.field.owner == this.table) {
				DBTableDefineImpl dbTable = fieldRef.field.dbTable;
				if (this.update.assigns.contains(fieldRef.field)) {
					this.conditionConflict.set(dbTable.index());
				}
			}
		}

		/**
		 * 尝试计算多物理表的更新顺序
		 * 
		 * <p>
		 * 
		 * @return 是否可解
		 */
		final boolean tryResolveSequence() {
			if (this.conditionNonDeterministic) {
				return false;
			}
			if (this.conditionConflict.cardinality() > 1) {
				return false;
			}
			if (this.conditionConflict.cardinality() == 0
					&& !this.assignValueDependOn) {
				return true;
			}
			// 总物理表个数
			final int a = this.table.dbTables.size();
			// 需要更新表的个数
			final int c = this.list.size();
			// 需要更新表的index列表
			final ArrayList<Integer> targets = new ArrayList<Integer>(c);
			for (int i = 0; i < c; i++) {
				targets.add(this.list.get(i).dbTable.index());
			}
			// 依赖矩阵,每行代表每个需要更新的物理表的依赖,
			final boolean[][] deponOns = new boolean[a][a];
			// 填充依赖矩阵
			for (int i = 0; i < a; i++) {
				if (targets.contains(i)) {
					Single single = this.get(i);
					int from = 0;
					// 当前物理表所依赖的物理表
					int dependOn;
					while ((dependOn = single.assignValueUsingTables
							.nextSetBit(from)) >= 0) {
						// 被依赖的物理表可能不需要更新
						if (targets.contains(dependOn)) {
							deponOns[single.dbTableIndex][dependOn] = true;
						}
						from = dependOn + 1;
					}
				}
			}
			// 考虑条件冲突
			switch (this.conditionConflict.cardinality()) {
			case 0:
				break;
			case 1:
				// 条件冲突不为空,表示此物理表上有更新字段被用作条件,此物理表必须最后更新
				// 即该表依赖所有其他物理表
				int column = this.conditionConflict.nextSetBit(0);
				for (int row = 0; row < a; row++) {
					if (targets.contains(row)) {
						deponOns[row][column] = true;
					}
				}
				break;
			default:
				// unreachable
				return false;
			}
			// 存储已经排序的结果,按照更新的顺序,存储物理表的序号
			final int[] sequenced = new int[c];
			// 初始全为-1,否则影响contain和columnContainTrue方法,不能正确处理第0行
			Arrays.fill(sequenced, -1);
			// 已经排序的个数
			int sequencedCount = 0;
			// 每次从序号1开始,找到一个未排序的,检查是否能加入队列
			sorting: while (sequencedCount < c) {
				next: for (int i = 0; i < a; i++) {
					// 不需要更新的物理表
					if (!targets.contains(i)) {
						continue next;
					}
					if (sequencedCount != 0) {
						// 已排序的队列的不为空
						if (contain(sequenced, i)) {
							// 物理表已经加入已排序队列
							continue next;
						}
					}
					// 某列不包含true(排除自身和已经排序的),即意味着没有其他表依赖该表,该表可加入队列
					if (columnContainTrue(deponOns, i, sequenced, i)) {
						continue next;
					} else {
						// 该序号没有对其他序号的依赖,加入队列
						sequenced[sequencedCount++] = i;
						continue sorting;
					}
				}
				// 无法找到被依赖为0的表
				return false;
			}
			Collections.sort(this.list, new Comparator<Single>() {
				public int compare(Single o1, Single o2) {
					return search(sequenced, o1.dbTableIndex)
							- search(sequenced, o2.dbTableIndex);
				}
			});
			return true;
		}

	}

	/**
	 * 物理表的更新信息
	 * 
	 * <p>
	 * 同时是赋值依赖,赋值来源的检查器
	 * 
	 * @author Jeff Tang
	 */
	private static final class Single extends TraversedExprVisitor<Object> {

		final ArrayList<FieldAssign> list = new ArrayList<FieldAssign>();
		final MultipleResolver resolver;
		final DBTableDefineImpl dbTable;
		final int dbTableIndex;
		final IntBits assignValueUsingTables = new IntBits();

		Single(MultipleResolver resolver, DBTableDefineImpl dbTable) {
			this.resolver = resolver;
			this.dbTable = dbTable;
			this.dbTableIndex = dbTable.index();
		}

		final void visitAssignValue() {
			for (int i = 0, c = this.list.size(); i < c; i++) {
				FieldAssign fa = this.list.get(i);
				fa.value.visit(this, null);
			}
		}

		private boolean assignValueFromJoinedRef;

		private boolean assignValueFromTargetRefOnSlave;

		boolean assignValueFromJoin() {
			return this.assignValueFromJoinedRef
					|| this.assignValueFromTargetRefOnSlave;
		}

		@Override
		public void visitSelectColumnRef(SelectColumnRefImpl expr,
				Object context) {
			if (expr.queryRef instanceof MoJoinedQueryRef) {
				this.assignValueFromJoinedRef = true;
			}
		}

		@Override
		public void visitTableFieldRef(TableFieldRefImpl fieldRef,
				Object context) {
			if (fieldRef.tableRef instanceof MoJoinedTableRef) {
				this.assignValueFromJoinedRef = true;
			}
			DBTableDefineImpl dbTable = fieldRef.field.getDBTable();
			if (fieldRef.field.owner == this.resolver.table
					&& dbTable != this.dbTable) {
				if (fieldRef.tableRef == this.resolver.update.moTableRef) {
					this.assignValueFromTargetRefOnSlave = true;
				}
				if (this.resolver.update.assigns.contains(fieldRef.field)) {
					this.assignValueUsingTables.set(dbTable.index());
					this.resolver.assignValueDependOn = true;
				}
			}
		}

		@Override
		public final String toString() {
			return "dbUpdate[" + this.dbTableIndex + "]";
		}
	}

	private static final class StatusVisitor extends TableUsages {

		private boolean visitingAssignValue;

		private boolean assignValueFromJoinedRef;

		final boolean assignValueFromJoinedRef() {
			return this.assignValueFromJoinedRef;
		}

		@Override
		public void visitUpdateAssign(FieldAssign assign, Object context) {
			this.visitingAssignValue = true;
			super.visitUpdateAssign(assign, context);
			this.visitingAssignValue = false;
		}

		@Override
		public void visitSelectColumnRef(SelectColumnRefImpl expr,
				Object context) {
			super.visitSelectColumnRef(expr, context);
			if (this.visitingAssignValue
					&& expr.queryRef instanceof MoJoinedQueryRef) {
				this.assignValueFromJoinedRef = true;
			}
		}

		@Override
		public void visitTableFieldRef(TableFieldRefImpl expr, Object context) {
			super.visitTableFieldRef(expr, context);
			if (this.visitingAssignValue
					&& expr.tableRef instanceof MoJoinedTableRef) {
				this.assignValueFromJoinedRef = true;
			}
		}

	}

	private static final void single(UpdateStatementImpl update,
			StatusVisitor status, ISqlCommandFactory factory, UpdateSql sql) {
		ISqlUpdateBuffer buffer = factory.update(
				update.moTableRef.target.primary.name, update.moTableRef.name,
				status.assignValueFromJoinedRef());
		update.moTableRef.render(buffer.target(), status);
		for (int i = 0; i < update.assigns.size(); i++) {
			FieldAssign fa = update.assigns.get(i);
			ISqlExprBuffer value = buffer.newValue(fa.field.namedb());
			fa.value().render(value, status);
		}
		if (update.condition != null) {
			update.condition.render(buffer.where(), status);
		}
		sql.build(buffer);
	}

	private static final void multiple(UpdateStatementImpl update,
			StatusVisitor status, Single single, ISqlUpdateBuffer buffer,
			String alias) {
		join(buffer.target(), alias, update.moTableRef, status, single.dbTable);
		for (FieldAssign fa : single.list) {
			fa.value().render(buffer.newValue(fa.field.namedb()), status);
		}
		if (update.condition != null) {
			update.condition.render(buffer.where(), status);
		}
	}

	private static final void multipleOnce(UpdateStatementImpl update,
			StatusVisitor status, ISqlUpdateMultiCommandFactory umf,
			UpdateSql sql) {
		final MoRootTableRef tableRef = update.moTableRef;
		TableUsage usage = status.ensureUsageOf(tableRef);
		for (int i = 0; i < update.assigns.size(); i++) {
			usage.use(update.assigns.get(i).field.dbTable);
		}
		ISqlUpdateMultiBuffer buffer = null;
		String alias = null;
		for (DBTableDefineImpl dbTable : usage.tables()) {
			if (buffer == null) {
				alias = Render.aliasOf(tableRef, dbTable);
				buffer = umf.updateMulti(dbTable.name, alias);
			} else {
				if (alias == null) {
					throw new IllegalStateException();
				}
				String ja = Render.aliasOf(tableRef, dbTable);
				Render.renderRecidEqJoin(buffer.target(), alias, dbTable.name,
						ja);
			}
		}
		if (buffer == null) {
			throw new IllegalStateException();
		}
		tableRef.render(buffer.target(), status);
		for (int i = 0; i < update.assigns.size(); i++) {
			FieldAssign fa = update.assigns.get(i);
			ISqlExprBuffer value = buffer.newValue(
					Render.aliasOf(tableRef, fa.field.dbTable),
					fa.field.namedb());
			fa.value().render(value, status);
		}
		if (update.condition != null) {
			update.condition.render(buffer.where(), status);
		}
		sql.build(buffer);
	}

	private static final void join(ISqlTableRefBuffer from, String alias,
			MoRootTableRef tableRef, StatusVisitor status,
			DBTableDefineImpl except) {
		TableUsage usage = status.usageOf(tableRef);
		if (usage != null) {
			for (DBTableDefineImpl dbTable : usage.tables()) {
				if (dbTable == except) {
					continue;
				}
				String ja = Render.aliasOf(tableRef, dbTable);
				Render.renderRecidEqJoin(from, alias, dbTable.name, ja);
			}
		}
		tableRef.render(from, status);
	}

	private static final void cursor(UpdateStatementImpl update,
			StatusVisitor status, MultipleResolver resolver,
			ISqlSegmentBuffer segment) {
		segment.declare(VAR_LAST_RECID, GUIDType.TYPE);
		ISqlCursorLoopBuffer cursor = defineCursor(segment, update, status);
		ISqlConditionBuffer ifs = cursor.ifThenElse();
		whenNotLastRecid(ifs.newWhen());
		updateCurrent(ifs.newThen(), resolver);
	}

	private static final String VAR_LAST_RECID = "LAST_RECID";
	private static final String CUR_NAME = "CUR";
	private static final String RECID_OUTPUT_ALIAS = "RECID_OUTPUT_ALIAS";
	private static final String VAR_RECID_OUTPUT = "RECID_OUTPUT";

	private static final ISqlCursorLoopBuffer defineCursor(
			ISqlSegmentBuffer segment, UpdateStatementImpl update,
			StatusVisitor status) {
		MoRootTableRef tableRef = update.moTableRef;
		TableDefineImpl table = tableRef.target;
		ISqlCursorLoopBuffer cursor = segment.cursorLoop(CUR_NAME, true);
		ISqlSelectBuffer select = cursor.query().select();
		String alias = Render.aliasOf(tableRef, table.primary);
		ISqlTableRefBuffer from = select.newTableRef(table.primary.name, alias);
		join(from, alias, tableRef, status, table.primary);
		select.newColumn(RECID_OUTPUT_ALIAS).loadField(alias,
				TableDefineImpl.FIELD_DBNAME_RECID);
		cursor.declare(VAR_RECID_OUTPUT, GUIDType.TYPE);
		for (int i = 0, c = update.assigns.size(); i < c; i++) {
			FieldAssign fa = update.assigns.get(i);
			fa.value().render(select.newColumn(fa.field.name), status);
			cursor.declare(fa.field.name, fa.field.getType());
		}
		select.newOrder(false).loadField(alias,
				TableDefineImpl.FIELD_DBNAME_RECID);
		return cursor;
	}

	private static final void whenNotLastRecid(ISqlExprBuffer when) {
		when.loadVar(VAR_LAST_RECID).predicate(SqlPredicate.IS_NULL, 1);
		when.loadVar(VAR_LAST_RECID).loadVar(VAR_RECID_OUTPUT).ne();
		when.or(2);
	}

	private static final void updateCurrent(ISqlSegmentBuffer segment,
			MultipleResolver resolver) {
		segment.assign(VAR_LAST_RECID).loadVar(VAR_RECID_OUTPUT);
		for (Single single : resolver.list) {
			ISqlUpdateBuffer update = segment.update(single.dbTable.name, "T",
					false);
			update.whereCurrentOf(CUR_NAME);
			for (int i = 0, c = single.list.size(); i < c; i++) {
				FieldAssign fa = single.list.get(i);
				update.newValue(fa.field.namedb()).loadVar(fa.field.name);
			}
		}
	}

	/**
	 * 数组中遍历查找键值序号
	 */
	private static final int search(int[] a, int key) {
		for (int i = 0; i < a.length; i++) {
			if (a[i] == key) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 数组中是否存在某键值
	 */
	private static final boolean contain(int[] a, int key) {
		return search(a, key) >= 0;
	}

	/**
	 * 矩阵中某列是否包含true值
	 * 
	 * @param m
	 *            矩阵
	 * @param column
	 *            目标检查列
	 * @param excepts
	 *            排除行
	 * @param except
	 *            排除行
	 * @return
	 */
	private static final boolean columnContainTrue(boolean[][] m, int column,
			int[] excepts, int except) {
		for (int row = 0; row < m.length; row++) {
			if (contain(excepts, row)) {
				continue;
			}
			if (row == except) {
				continue;
			}
			if (m[row][column]) {
				return true;
			}
		}
		return false;
	}
}