package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import org.eclipse.jt.core.exception.InvalidStatementDefineException;
import org.eclipse.jt.core.impl.UpdateStatementImpl.FieldAssign;


/**
 * ��������Sql
 * 
 * <p>
 * dna-sql��Update�﷨��Ҫ�ο���SQLServer���﷨����.���SQLServer�﷨,ʡ��from�ؼ���,��join��ǰ,
 * ����ֻ�ǶԵ��߼���ĸ���,���ҵȼ���ǿ��SQLServer��from�Ӿ�ĵ�һ�������ñ���ָ�����Ŀ���.
 * 
 * <blockquote>
 * 
 * <pre>
 * update [Target] [left|right|full]
 * 	join [Reference] [,n..]
 * 	set [...]
 * 	where [...]</prev></blockquote>
 * 
 * <p>sqlserver�Դ��﷨��ʵ��ִ�мƻ�Ϊ:����from�Ӿ�Ĺ�ϵ:���������Ŀ���ϵ,����Ŀ���ϵ��ͶӰ,
 * ��ÿ����Ч��ͶӰ�н���Update����;���������Ŀ���ϵ,��ֱ��ѭ��Ŀ���ϵ,��from��ϵ���ۺϺ������²���.
 * ע�⵽���ӵĲο���ʵ���ṩ��:�й���,���������������;where�����Ĳο�ֵ;set�Ӿ�Ĳο�ֵ.
 * 
 * <p>sqlserver�������﷨��ʽ�������ǲ����Ͻ���.
 * ����,�й�ϵA(id,fid)ʵ��Ϊ((0,a),(1,b),(2,c))���ϵB(id,k)ʵ��Ϊ((a,10),(a,20),(c,30)),������¹�ϵA,
 * from�Ӿ�����"A.fid = B.id"��������.���ڹ�ϵB��id����Ψһ��,���¶Թ�ϵA��ÿһȷ��Ԫ��,�����Ӧ�����B��Ԫ����.
 * ��ʱ�ο���ϵ�������ṩwhere�ο�ֵ����set�ο�ֵ���ǲ����е�.�����������,sqlserver���Թ�ϵB�����ۺ�����,ʵ��ΪANY����.
 * ��ȡ�������Ԫ��,�ṩ�Թ�ϵA���µĲο�.����ζ��,���½���ǲ��ȶ���.
 * 
 * <p>
 * oracle��Update���,��ʹ�ñ�׼sql�ṩ����ʵ��,�й�����where�ο���������װ��"�Ӳ�ѯ",setֵ�ο���������װ��"������ͼ".
 * ��������ѯ��ִ�мƻ����Ƿֿ����е�.�����ӽ������ӱ��ʵ��﷨:update(select ... from Ŀ���ϵ join �ο���ϵ)set [...],
 * �����﷨,Oracleǿ��Ҫ��ο���ϵ����ͨ��1��1����1��n�Ĺ�ϵ���ӵ�Ŀ���ϵ��,��ǿ��Ҫ��(hint��ȡ��)�ο���ϵ�ĵ�ֵ������Ϊ��������Ψһ����(���Ψһ����ͬ��).
 * ���ִ���ʽ�����Ͻ���.
 * 
 * <p>
 * �������ṩ��join����,��ʵ����ֻ�ܲο�sqlserver���߼�.
 * 
 * <p>
 * ����dna-sql��update����Ե��߼����,��������Ҫһ�θ��¶�������.���ȹ涨:
 * <ul>
 * <li>updateָ�������߼���ĸ���.
 * <li>dbUpdateָ�Ե�һ�����ĸ���.
 * </ul>
 * 
 * <p>�ڶ�������updateʱ,�������:dbUpdate�ƻ���update������������dbUpdate�ĸ�ֵ��Դ.
 * �����߼���update�Ĳ�������,�����dbUpdate�Ĳο�ֵ������� ����update��ǰֵ���Ǻ����.
 * 
 * <p>
 * �ٶ�������:
 * <ol>
 * <li><strong>������ͻ(ConditonConflict)</strong>:update��Ŀ���ֶ�,��Ϊ��update������,�Ƹ�Ŀ���ֶ����ڵ���������������ͻ.
 * <li><strong>��ֵ����(AssignDependOn)</strong>:Ŀ��dbUpdate�ĸ�ֵ(setֵ)�ο�������dbUpdate�лᱻ���µ��ֶ�,��Ŀ�������ڲο�,ʵ��Ϊ:Ŀ���ֶα������ڲο��ֶν���.
 * <li><strong>������Դ(ConditionFrom)</strong>:��update��������,ʹ�õ�������Ŀ���Ĳο��ֶ������ڵ������.
 * </ol>
 * 
 * <p>
 * �ڲ�����join�������,���۶����¿��ܵ�����.��CCΪ1ʱ,���������Ҫ������.��CC����1ʱ,��һdbUpdate�����ƻ�ʣ��dbUpdate������.
 * ��ֵ��������Ϊ�����ֶ�˳����ϵ�����,������˳���ǿ����н��,�������ֶμ���,Ҳ������dbUpdate����.ĿǰΪ�˼򻯱���,��dbUpdate������.
 * ��ADOת��ΪdbUpdate˳����������,������Ҫ����CC�����.
 */
// took too much time here...=.=
final class UpdateSql extends Sql {

	UpdateSql(DBLang lang, final UpdateStatementImpl update) {
		if (update.assigns.size() == 0) {
			throw new InvalidStatementDefineException("������䶨��[" + update.name
					+ "]δ�����κθ����С�");
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
	 * �߼���ĸ�����Ϣ��¼
	 * 
	 * <p>
	 * ͬʱ�������������Լ����������ļ����,���������Դ��������ͻ
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
		 * ���Լ���������ĸ���˳��
		 * 
		 * <p>
		 * 
		 * @return �Ƿ�ɽ�
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
			// ����������
			final int a = this.table.dbTables.size();
			// ��Ҫ���±�ĸ���
			final int c = this.list.size();
			// ��Ҫ���±��index�б�
			final ArrayList<Integer> targets = new ArrayList<Integer>(c);
			for (int i = 0; i < c; i++) {
				targets.add(this.list.get(i).dbTable.index());
			}
			// ��������,ÿ�д���ÿ����Ҫ���µ�����������,
			final boolean[][] deponOns = new boolean[a][a];
			// �����������
			for (int i = 0; i < a; i++) {
				if (targets.contains(i)) {
					Single single = this.get(i);
					int from = 0;
					// ��ǰ������������������
					int dependOn;
					while ((dependOn = single.assignValueUsingTables
							.nextSetBit(from)) >= 0) {
						// ���������������ܲ���Ҫ����
						if (targets.contains(dependOn)) {
							deponOns[single.dbTableIndex][dependOn] = true;
						}
						from = dependOn + 1;
					}
				}
			}
			// ����������ͻ
			switch (this.conditionConflict.cardinality()) {
			case 0:
				break;
			case 1:
				// ������ͻ��Ϊ��,��ʾ����������и����ֶα���������,����������������
				// ���ñ������������������
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
			// �洢�Ѿ�����Ľ��,���ո��µ�˳��,�洢���������
			final int[] sequenced = new int[c];
			// ��ʼȫΪ-1,����Ӱ��contain��columnContainTrue����,������ȷ�����0��
			Arrays.fill(sequenced, -1);
			// �Ѿ�����ĸ���
			int sequencedCount = 0;
			// ÿ�δ����1��ʼ,�ҵ�һ��δ�����,����Ƿ��ܼ������
			sorting: while (sequencedCount < c) {
				next: for (int i = 0; i < a; i++) {
					// ����Ҫ���µ������
					if (!targets.contains(i)) {
						continue next;
					}
					if (sequencedCount != 0) {
						// ������Ķ��еĲ�Ϊ��
						if (contain(sequenced, i)) {
							// ������Ѿ��������������
							continue next;
						}
					}
					// ĳ�в�����true(�ų�������Ѿ������),����ζ��û�������������ñ�,�ñ�ɼ������
					if (columnContainTrue(deponOns, i, sequenced, i)) {
						continue next;
					} else {
						// �����û�ж�������ŵ�����,�������
						sequenced[sequencedCount++] = i;
						continue sorting;
					}
				}
				// �޷��ҵ�������Ϊ0�ı�
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
	 * �����ĸ�����Ϣ
	 * 
	 * <p>
	 * ͬʱ�Ǹ�ֵ����,��ֵ��Դ�ļ����
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
	 * �����б������Ҽ�ֵ���
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
	 * �������Ƿ����ĳ��ֵ
	 */
	private static final boolean contain(int[] a, int key) {
		return search(a, key) >= 0;
	}

	/**
	 * ������ĳ���Ƿ����trueֵ
	 * 
	 * @param m
	 *            ����
	 * @param column
	 *            Ŀ������
	 * @param excepts
	 *            �ų���
	 * @param except
	 *            �ų���
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