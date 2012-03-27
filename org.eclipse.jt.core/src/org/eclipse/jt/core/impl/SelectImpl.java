package org.eclipse.jt.core.impl;

import static org.eclipse.jt.core.impl.SetOperatorImpl.UNION;
import static org.eclipse.jt.core.impl.SetOperatorImpl.UNION_ALL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.eclipse.jt.core.def.MissingDefineException;
import org.eclipse.jt.core.def.ModifiableContainer;
import org.eclipse.jt.core.def.exp.ConditionalExpression;
import org.eclipse.jt.core.def.exp.RelationColumnRefExpr;
import org.eclipse.jt.core.def.exp.ValueExpression;
import org.eclipse.jt.core.def.query.DerivedQueryDefine;
import org.eclipse.jt.core.def.query.GroupByItemDeclare;
import org.eclipse.jt.core.def.query.GroupByType;
import org.eclipse.jt.core.def.query.OrderByItemDeclare;
import org.eclipse.jt.core.def.query.RelationColumnDefine;
import org.eclipse.jt.core.def.query.RelationDefine;
import org.eclipse.jt.core.def.query.SelectDeclare;
import org.eclipse.jt.core.def.table.TableDeclarator;
import org.eclipse.jt.core.def.table.TableDefine;
import org.eclipse.jt.core.exception.CubeGroupbyNotSupportedException;
import org.eclipse.jt.core.exception.InvalidDerivedQueryDomainException;
import org.eclipse.jt.core.exception.InvalidStatementDefineException;
import org.eclipse.jt.core.exception.NamedDefineExistingException;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.exception.SubselectOrderbyNotSupportedException;
import org.eclipse.jt.core.type.DataType;


/**
 * ��ѯģ��
 * 
 * @param <TSelect>
 *            ��ѯ����ʵ����
 * @param <TColumn>
 *            ��ѯ�����ʵ����
 * 
 * @author Jeff Tang
 */
abstract class SelectImpl<TSelect extends SelectImpl<TSelect, TColumn>, TColumn extends SelectColumnImpl<TSelect, TColumn>>
		extends NamedDefineImpl implements SelectDeclare, Relation,
		RelationRefDomain, RelationRefBuildable, OMVisitable {

	@Deprecated
	public final QuRelationRef findReference(String name) {
		return this.findRelationRef(name);
	}

	@Deprecated
	public final QuRelationRef getReference(String name) {
		return this.getRelationRef(name);
	}

	public final QuRelationRef findRelationRef(String name) {
		QuRootRelationRef rootRef = this.rootRelationRef;
		if (rootRef == null) {
			return null;
		} else if (rootRef.getName().equals(name)) {
			return rootRef;
		}
		return this.relationRefMap.get(name);
	}

	public final QuRelationRef getRelationRef(String name) {
		QuRelationRef relationRef = this.findRelationRef(name);
		if (relationRef == null) {
			throw missingRelationRef(name);
		}
		return relationRef;
	}

	public final RelationRef findRelationRefRecursively(String name) {
		RelationRef relationRef = this.findRelationRef(name);
		if (relationRef != null) {
			return relationRef;
		}
		for (RelationRefDomain parent = this.getDomain(); parent != null; parent = parent
				.getDomain()) {
			relationRef = parent.findRelationRef(name);
			if (relationRef != null) {
				return relationRef;
			}
		}
		return null;
	}

	public final RelationRef getRelationRefRecursively(String name) {
		RelationRef relationRef = this.findRelationRefRecursively(name);
		if (relationRef == null) {
			throw missingRelationRef(name);
		}
		return relationRef;
	}

	public final QuRelationRef getRootReference() {
		return this.rootRelationRef;
	}

	public final Iterable<QuRelationRef> getReferences() {
		if (this.rootRelationRef == null) {
			return emptyItrable;
		} else {
			return new Iterable<QuRelationRef>() {
				public Iterator<QuRelationRef> iterator() {
					return SelectImpl.this.rootRelationRef.iterator();
				}
			};
		}
	}

	public final QuRootTableRef newReference(TableDefine table) {
		if (table == null) {
			throw new NullArgumentException("����");
		}
		return this.newTableRef(table.getName(), (TableDefineImpl) table);
	}

	public final QuRootTableRef newReference(TableDeclarator table) {
		if (table == null) {
			throw new NullArgumentException("����");
		}
		TableDefineImpl t = (TableDefineImpl) table.getDefine();
		return this.newTableRef(t.name, t);
	}

	public final QuRootTableRef newReference(TableDefine table, String name) {
		return this.newTableRef(name, (TableDefineImpl) table);
	}

	public final QuRootTableRef newReference(TableDeclarator table, String name) {
		return this.newTableRef(name, (TableDefineImpl) table.getDefine());
	}

	public final DerivedQueryImpl newDerivedQuery() {
		return new DerivedQueryImpl(this);
	}

	public final QuRootQueryRef newReference(DerivedQueryDefine query) {
		if (query == null) {
			throw new NullArgumentException("��ѯ�ṹ");
		}
		DerivedQueryImpl dq = (DerivedQueryImpl) query;
		return this.newQueryRef(dq.name, dq);
	}

	public final QuRootQueryRef newReference(DerivedQueryDefine query,
			String name) {
		if (query == null) {
			throw new NullArgumentException("��ѯ�ṹ");
		}
		return this.newQueryRef(name, (DerivedQueryImpl) query);
	}

	public final RelationColumnRefImpl expOf(RelationColumnDefine column) {
		if (column == null) {
			throw new NullArgumentException("��ϵ�ж���");
		}
		return this.exprOf(column);
	}

	public final RelationColumnRefExpr expOf(String column) {
		if (column == null) {
			throw new NullArgumentException("��ϵ������");
		}
		if (this.rootRelationRef == null) {
			throw new UnsupportedOperationException("δ�����κι�ϵ����");
		}
		for (QuRelationRef relationRef : this.rootRelationRef) {
			RelationColumn c = relationRef.getTarget().findColumn(column);
			if (c != null) {
				return relationRef.expOf(c);
			}
		}
		throw new UnsupportedOperationException("�Ҳ���ָ�����ƵĹ�ϵ��");
	}

	public final boolean getDistinct() {
		return this.distinct;
	}

	public final void setDistinct(boolean distinct) {
		this.checkModifiable();
		this.distinct = distinct;
	}

	public final NamedDefineContainerImpl<TColumn> getColumns() {
		return this.columns;
	}

	public final TColumn findColumn(String columnName) {
		return this.columns.find(columnName);
	}

	public final TColumn getColumn(String columnName) {
		return this.columns.get(columnName);
	}

	public final TColumn newColumn(RelationColumnDefine column) {
		if (column == null) {
			throw new NullArgumentException("��ϵ�ж���");
		}
		return this.newColumn(column.getName(), this.exprOf(column));
	}

	public final TColumn newColumn(RelationColumnDefine column, String name) {
		if (column == null) {
			throw new NullArgumentException("��ϵ�ж���");
		}
		return this.newColumn(name, this.exprOf(column));
	}

	public final TColumn newColumn(ValueExpression expr) {
		if (expr == null) {
			throw new NullArgumentException("����б��ʽ");
		}
		return this.newColumn(this.generateColumnName(), (ValueExpr) expr);
	}

	public final TColumn newColumn(ValueExpression expr, String name) {
		if (expr == null) {
			throw new NullArgumentException("��ѯ�б��ʽ");
		}
		return this.newColumn(name, (ValueExpr) expr);
	}

	public final ConditionalExpression getCondition() {
		return this.where;
	}

	public final void setCondition(ConditionalExpression condition) {
		this.checkModifiable();
		if (condition == null) {
			this.where = null;
		} else {
			ConditionalExpr where = (ConditionalExpr) condition;
			if (SystemVariables.VALIDATE_EXPR_DOMAIN) {
				where.validateDomain(this);
			}
			this.where = where;
		}
	}

	public final GroupByType getGroupByType() {
		return this.groupbyType;
	}

	@SuppressWarnings("deprecation")
	public final void setGroupByType(GroupByType type) {
		this.checkModifiable();
		if (type == null) {
			throw new NullArgumentException("�����������");
		}
		if (type == GroupByType.CUBE) {
			if (SystemVariables.CUBE_GROUPBY_THROW_EXCEPTION) {
				throw new CubeGroupbyNotSupportedException();
			} else {
				System.err.println("��֧��Cube���͵Ļ���.");
			}
		}
		this.groupbyType = type;
	}

	public final MetaBaseContainerImpl<? extends GroupByItemImpl> getGroupBys() {
		return this.groupbys;
	}

	public final GroupByItemImpl newGroupBy(ValueExpression expr) {
		this.checkModifiable();
		if (expr == null) {
			throw new NullArgumentException("������ʽ");
		}
		ValueExpr value = (ValueExpr) expr;
		if (SystemVariables.VALIDATE_EXPR_DOMAIN) {
			value.validateDomain(this);
		}
		GroupByItemImpl groupby = new GroupByItemImpl(this, value);
		return this.addGroupByNoCheck(groupby);
	}

	public final GroupByItemDeclare newGroupBy(RelationColumnDefine column) {
		this.checkModifiable();
		if (column == null) {
			throw new NullArgumentException("��ϵ�ж���");
		}
		GroupByItemImpl groupby = new GroupByItemImpl(this, this.exprOf(column));
		return this.addGroupByNoCheck(groupby);
	}

	public final ConditionalExpression getHaving() {
		return this.having;
	}

	public final void setHaving(ConditionalExpression condition) {
		this.checkModifiable();
		if (condition == null) {
			this.having = null;
		} else {
			ConditionalExpr having = (ConditionalExpr) condition;
			if (SystemVariables.VALIDATE_EXPR_DOMAIN) {
				having.validateDomain(this);
			}
			this.having = having;
		}
	}

	public final SubQueryImpl newSubQuery() {
		return new SubQueryImpl(this);
	}

	public final void union(DerivedQueryDefine query) {
		if (query == null) {
			throw new NullArgumentException("union��ѯ�ṹ");
		}
		this.union((DerivedQueryImpl) query, false);
	}

	public final void unionAll(DerivedQueryDefine query) {
		if (query == null) {
			throw new NullArgumentException("union��ѯ�ṹ");
		}
		this.union((DerivedQueryImpl) query, true);
	}

	public final MetaBaseContainerImpl<SetOperateImpl> getSetOperates() {
		return this.sets;
	}

	@Deprecated
	public ModifiableContainer<? extends OrderByItemDeclare> getOrderBys() {
		if (SystemVariables.SUBSELECT_ORDERBY_THROW_EXCEPTION) {
			throw new SubselectOrderbyNotSupportedException();
		} else {
			subselectOrderbyNotSupported();
		}
		return null;
	}

	@Deprecated
	public OrderByItemDeclare newOrderBy(RelationColumnDefine column) {
		if (SystemVariables.SUBSELECT_ORDERBY_THROW_EXCEPTION) {
			throw new SubselectOrderbyNotSupportedException();
		} else {
			subselectOrderbyNotSupported();
		}
		return null;
	}

	@Deprecated
	public OrderByItemDeclare newOrderBy(RelationColumnDefine column,
			boolean isDesc) {
		if (SystemVariables.SUBSELECT_ORDERBY_THROW_EXCEPTION) {
			throw new SubselectOrderbyNotSupportedException();
		} else {
			subselectOrderbyNotSupported();
		}
		return null;
	}

	@Deprecated
	public OrderByItemDeclare newOrderBy(ValueExpression value) {
		if (SystemVariables.SUBSELECT_ORDERBY_THROW_EXCEPTION) {
			throw new SubselectOrderbyNotSupportedException();
		} else {
			subselectOrderbyNotSupported();
		}
		return null;
	}

	@Deprecated
	public OrderByItemDeclare newOrderBy(ValueExpression value, boolean isDesc) {
		if (SystemVariables.SUBSELECT_ORDERBY_THROW_EXCEPTION) {
			throw new SubselectOrderbyNotSupportedException();
		} else {
			subselectOrderbyNotSupported();
		}
		return null;
	}

	private static final void subselectOrderbyNotSupported() {
		System.err.println("��֧�����Ӳ�ѯ�ṹ��ʹ��Orderby�Ӿ�.");
	}

	private static final Iterable<QuRelationRef> emptyItrable = new Iterable<QuRelationRef>() {
		public Iterator<QuRelationRef> iterator() {
			return emptyIterator;
		}
	};

	private static final Iterator<QuRelationRef> emptyIterator = new Iterator<QuRelationRef>() {
		public boolean hasNext() {
			return false;
		}

		public QuRelationRef next() {
			throw new NoSuchElementException();
		}

		public void remove() {
			throw new NoSuchElementException();
		}
	};

	static final ExistingDetector<StringKeyMap<QuRelationRef>, QuRelationRef, String> aliasDetector = new ExistingDetector<StringKeyMap<QuRelationRef>, QuRelationRef, String>() {

		public boolean exists(StringKeyMap<QuRelationRef> container,
				String key, QuRelationRef ignore) {
			RelationRef relationRef = container.get(key);
			return relationRef != null
					&& (ignore == null || relationRef != ignore);
		}

	};

	/**
	 * ��һ��������ϵ����
	 */
	private QuRootRelationRef rootRelationRef;

	/**
	 * ��ǰ��ѯ�µĹ�ϵ���ù�ϣ��
	 */
	final StringKeyMap<QuRelationRef> relationRefMap = new StringKeyMap<QuRelationRef>();

	/**
	 * ��ѯ����
	 */
	ConditionalExpr where;

	/**
	 * ����Լ������
	 */
	ConditionalExpr having;

	/**
	 * �������
	 */
	MetaBaseContainerImpl<GroupByItemImpl> groupbys;

	/**
	 * ��������
	 */
	GroupByType groupbyType = GroupByType.DEFAULT;

	/**
	 * �Ƿ��ų��ظ���
	 */
	boolean distinct;

	/**
	 * ��ѯ�����
	 */
	final NamedDefineContainerImpl<TColumn> columns = new NamedDefineContainerImpl<TColumn>();

	/**
	 * ��������
	 */
	MetaBaseContainerImpl<SetOperateImpl> sets;

	/**
	 * ʹ��ָ�����ƹ����ѯ����
	 * 
	 * @param name
	 */
	SelectImpl(String name) {
		super(name);
	}

	/**
	 * ���ص�һ�������Ĺ�ϵ����
	 */
	final QuRootRelationRef rootRelationRef() {
		return this.rootRelationRef;
	}

	static final MissingDefineException missingRelationRef(String name) {
		return new MissingDefineException("����������Ϊ[" + name + "]�Ĺ�ϵ���ö���");
	}

	private final String ensureRefName(String name) {
		if (this.relationRefMap.containsKey(name)) {
			if (SystemVariables.REFERENCE_RENAME_ALIAS) {
				return Utils.buildIdentityName(name, aliasDetector,
						this.relationRefMap);
			} else {
				throw new NamedDefineExistingException("����Ϊ[" + name
						+ "]�Ĺ�ϵ�����Ѿ�����.");
			}
		}
		return name;
	}

	boolean dummyUsage;

	/**
	 * ���Ӹ����ı�����
	 * 
	 * @param name
	 * @param table
	 * @return
	 */
	final QuRootTableRef newTableRef(String name, TableDefineImpl table) {
		this.checkModifiable();
		if (name == null || name.length() == 0) {
			throw new NullArgumentException("������");
		}
		if (table == null) {
			throw new NullArgumentException("����");
		}
		if (this.rootRelationRef == null) {
			QuRootTableRef tableRef = new QuRootTableRef(this, name, table,
					null);
			if (this instanceof QueryStatementBase) {
				tableRef.setForUpdate(true);
			}
			this.rootRelationRef = tableRef;
			this.relationRefMap.put(name, tableRef);
			if (table == TableDefineImpl.DUMMY) {
				this.dummyUsage = true;
			}
			return tableRef;
		} else {
			name = this.ensureRefName(name);
			QuRootRelationRef last = this.rootRelationRef.last();
			QuRootTableRef tableRef = new QuRootTableRef(this, name, table,
					last);
			last.setNext(tableRef);
			this.relationRefMap.put(name, tableRef, true);
			return tableRef;
		}
	}

	/**
	 * ���Ӹ����Ĳ�ѯ����
	 * 
	 * @param name
	 * @param dq
	 * @return
	 */
	final QuRootQueryRef newQueryRef(String name, DerivedQueryImpl dq) {
		this.checkModifiable();
		if (name == null || name.length() == 0) {
			throw new NullArgumentException("������");
		}
		if (dq == null) {
			throw new NullArgumentException("��ѯ�ṹ����");
		}
		if (SystemVariables.VALIDATE_DERIVED_DOMAIN) {
			dq.validateDomain(this);
		}
		if (this.rootRelationRef == null) {
			QuRootQueryRef queryRef = new QuRootQueryRef(this, name, dq, null);
			this.rootRelationRef = queryRef;
			this.relationRefMap.put(name, queryRef);
			return queryRef;
		} else {
			name = this.ensureRefName(name);
			QuRootRelationRef last = this.rootRelationRef.last();
			QuRootQueryRef queryRef = new QuRootQueryRef(this, name, dq, last);
			last.setNext(queryRef);
			this.relationRefMap.put(name, queryRef, true);
			return queryRef;
		}
	}

	/**
	 * �����ϵ�����ñ��ʽ
	 * 
	 * <p>
	 * �ӵ�һ��ָ��ù�ϵ�����ڹ�ϵԪ����������й���
	 * 
	 * @param column
	 * @return
	 */
	final RelationColumnRefImpl exprOf(RelationColumnDefine column) {
		RelationDefine relation = column.getOwner();
		if (relation == this.rootRelationRef.getTarget()) {
			return this.rootRelationRef.expOf(column);
		} else {
			for (QuRelationRef relationRef : this.rootRelationRef) {
				if (relationRef.getTarget() == relation) {
					return relationRef.expOf(column);
				}
			}
		}
		throw new MissingDefineException("������ָ���ϵ[" + relation.getName()
				+ "]�Ĺ�ϵ����.");
	}

	final void appendAndCondition(ConditionalExpr condition) {
		if (this.where == null) {
			this.where = condition;
		} else {
			this.where = this.where.and(condition);
		}
	}

	private final String generateColumnName() {
		return "c".concat(String.valueOf(this.columns.size()));
	}

	final TColumn newColumn(ValueExpr expr) {
		return this.newColumn(this.generateColumnName(), expr);
	}

	/**
	 * ��������ж���
	 * 
	 * @param name
	 *            ��ѯ������,�ظ�ʱ��������
	 * @param expr
	 *            ��ѯ�еı��ʽ
	 * @return
	 */
	final TColumn newColumn(String name, ValueExpr expr) {
		this.checkModifiable();
		if (name == null || name.length() == 0) {
			throw new NullArgumentException("��ѯ�еı���");
		}
		if (expr == null) {
			throw new NullArgumentException("��ѯ�еı��ʽ");
		}
		if (this.columns.find(name) != null) {
			if (SystemVariables.REFERENCE_RENAME_ALIAS) {
				name = Utils.buildIdentityName(name, defineDetector,
						this.columns);
			} else {
				throw new NamedDefineExistingException("����Ϊ[" + name
						+ "]�Ĳ�ѯ������Ѿ�����.");
			}
		}
		if (SystemVariables.VALIDATE_EXPR_DOMAIN) {
			expr.validateDomain(this);
		}
		TColumn column = this.newColumnOnly(name, expr);
		this.columns.add(column);
		return column;
	}

	protected abstract TColumn newColumnOnly(String name, ValueExpr expr);

	/**
	 * ���ݹ�ϵ���ü���ϵ�в��Ҳ�ѯ�ж���
	 * 
	 * @param relationRef
	 * @param column
	 * @return
	 */
	final TColumn findColumn(RelationRef relationRef, RelationColumn column) {
		if (!(relationRef instanceof QuRelationRef)) {
			return null;
		}
		for (int i = 0, c = this.columns.size(); i < c; i++) {
			TColumn qc = this.columns.get(i);
			if (qc.value() instanceof RelationColumnRefExpr) {
				RelationColumnRefExpr columnRef = (RelationColumnRefExpr) qc
						.value();
				if (columnRef.getReference() == relationRef
						&& columnRef.getColumn() == column) {
					return qc;
				}
			}
		}
		return null;
	}

	/**
	 * ���Һ�ָ�������õ�ָ���ֶεȼ۵Ĳ�ѯ��
	 */
	final TColumn findEqualColumn(QuRelationRef relationRef,
			RelationColumn column) {
		TColumn qc = this.findColumn(relationRef, column);
		if (qc != null) {
			return qc;
		}
		EqualColumnRefDetector detector = new EqualColumnRefDetector();
		detector.relationRef = relationRef;
		detector.column = column;
		return this.findFirstEqualColumn0(detector);
	}

	private final TColumn findFirstEqualColumn0(EqualColumnRefDetector detector) {
		TColumn column;
		int start = detector.list.size();
		for (QuRelationRef relationRef : this.rootRelationRef) {
			relationRef.visit(detector, null);
		}
		if (this.where != null) {
			this.where.visit(detector, null);
		}
		int eSize = detector.list.size();
		for (int i = start; i < eSize; i++) {
			RelationColumnRefImpl columnRef = detector.list.get(i);
			if ((column = this.findColumn(columnRef.getReference(),
					columnRef.getColumn())) != null) {
				return column;
			}
		}
		// �������ҵȼ�������
		for (int i = start; i < eSize; i++) {
			RelationColumnRefImpl columnRef = detector.list.get(i);
			detector.relationRef = columnRef.getReference();
			detector.column = columnRef.getColumn();
			if ((column = this.findFirstEqualColumn0(detector)) != null) {
				return column;
			}
		}
		return null;
	}

	private static final class EqualColumnRefDetector extends
			TraversedExprVisitor<Object> {

		ArrayList<RelationColumnRefImpl> list = new ArrayList<RelationColumnRefImpl>();

		private RelationRef relationRef;
		private RelationColumn column;

		@Override
		public void visitPredicateExpr(PredicateExpr predicate, Object context) {
			if (predicate.isEqualsPredicate()
					&& predicate.values[0] instanceof TableFieldRefImpl
					&& predicate.values[1] instanceof TableFieldRefImpl) {
				TableFieldRefImpl fr0 = (TableFieldRefImpl) predicate.values[0];
				TableFieldRefImpl fr1 = (TableFieldRefImpl) predicate.values[1];
				if (fr0.tableRef == this.relationRef
						&& fr0.field == this.column) {
					if (this.list.contains(fr1)) {
						this.list.add(fr1);
					}
				} else if (fr1.tableRef == this.relationRef
						&& fr1.field == this.column) {
					if (this.list.contains(fr0)) {
						this.list.add(fr0);
					}
				}
			}
			super.visitPredicateExpr(predicate, context);
		}

	}

	final TColumn findRootRecidColumn() {
		QuRootRelationRef root = this.rootRelationRef;
		if (root != null && root instanceof QuRootTableRef) {
			return this
					.findColumn(root, ((QuRootTableRef) root).target.f_recid);
		} else {
			throw new IllegalArgumentException();
		}
	}

	final TColumn findRootRecverColumn() {
		QuRootRelationRef root = this.rootRelationRef;
		if (root != null && root instanceof QuRootTableRef) {
			return this.findColumn(root,
					((QuRootTableRef) root).target.f_recver);
		} else {
			throw new IllegalArgumentException();
		}
	}

	final DataType tryGetDeterminateColumnType(int columnIndex) {
		SelectColumnImpl<?, ?> column = this.columns.get(columnIndex);
		if (column.getType() != NullType.TYPE) {
			return column.getType();
		} else if (this.sets != null) {
			for (int i = 0, c = this.sets.size(); i < c; i++) {
				DataType type = this.sets.get(i).target
						.tryGetDeterminateColumnType(columnIndex);
				if (type != null) {
					return type;
				}
			}
		}
		return column.getType();
	}

	private final GroupByItemImpl addGroupByNoCheck(GroupByItemImpl groupby) {
		if (this.groupbys == null) {
			this.groupbys = new MetaBaseContainerImpl<GroupByItemImpl>();
		}
		this.groupbys.add(groupby);
		return groupby;
	}

	private final SetOperateImpl union(DerivedQueryImpl query, boolean all) {
		this.checkModifiable();
		if (query.isWith) {
			throw new InvalidStatementDefineException("With���岻������union");
		}
		if (SystemVariables.VALIDATE_DERIVED_DOMAIN && query.owner != this) {
			throw new InvalidDerivedQueryDomainException();
		}
		if (this.sets == null) {
			this.sets = new MetaBaseContainerImpl<SetOperateImpl>();
		}
		SetOperateImpl so = new SetOperateImpl(this, query, all ? UNION_ALL
				: UNION);
		this.sets.add(so);
		return so;
	}

	final void validate() {
		if (this.rootRelationRef == null) {
			throw new InvalidStatementDefineException("��ѯ[" + this.name
					+ "]δ�����κι�ϵ����.");
		}
		if (this.columns.size() == 0) {
			throw new InvalidStatementDefineException("��ѯ[" + this.name
					+ "]δ�����κ������.");
		}
		for (QuRelationRef relationRef : this.rootRelationRef) {
			relationRef.validate();
		}
	}

	final void validateSingleRoot() {
		if (this.rootRelationRef.next() != null) {
			throw new InvalidStatementDefineException("��ѯ�����˶��������ϵ����");
		}
	}

	/**
	 * ����ǰ��ѯ����ṹ��¡��Ŀ���ѯ����
	 * 
	 * @param target
	 * @param args
	 */
	final void cloneSelectTo(SelectImpl<?, ?> target, ArgumentOwner args) {
		if (this.rootRelationRef != null) {
			this.rootRelationRef.cloneTo(target, args);
		}
		target.setDistinct(this.distinct);
		if (this.where != null) {
			target.setCondition(this.where.clone(target, args));
		}
		if (this.groupbys != null) {
			for (int i = 0, c = this.groupbys.size(); i < c; i++) {
				this.groupbys.get(i).cloneTo(target, args);
			}
		}
		target.groupbyType = this.groupbyType;
		if (this.having != null) {
			target.setHaving(this.having.clone(target, args));
		}
		for (int i = 0, c = this.columns.size(); i < c; i++) {
			this.columns.get(i).cloneTo(target, args);
		}
		if (this.sets != null) {
			for (int i = 0, c = this.sets.size(); i < c; i++) {
				this.sets.get(i).cloneTo(target, args);
			}
		}
	}

	public <TContext> void visit(OMVisitor<TContext> visitor, TContext context) {
		visitor.visitSelect(this, context);
	}

	final void render(ISqlSelectBuffer buffer, TableUsages usages) {
		this.renderFrom(buffer, usages);
		this.renderWhere(buffer, usages);
		this.renderGroupby(buffer, usages);
		this.renderHaving(buffer, usages);
		this.renderSelect(buffer, usages);
		this.renderUnion(buffer, usages);
	}

	final void renderFilterColumnUsing(ISqlSelectBuffer buffer,
			TableUsages usages, HashMap<Integer, Object> filtered) {
		this.renderFrom(buffer, usages);
		this.renderWhere(buffer, usages);
		this.renderGroupby(buffer, usages);
		this.renderHaving(buffer, usages);
		filteredColumnRender.renderSelect(buffer, usages, this, filtered);
		this.renderUnionFilterColumnUsing(buffer, usages, filtered);
	}

	final <TContext> void renderFilterColumnFrom(ISqlSelectBuffer buffer,
			TableUsages usages,
			EFilter<SelectColumnImpl<?, ?>, TContext> filter, TContext context) {
		this.renderFrom(buffer, usages);
		this.renderWhere(buffer, usages);
		this.renderGroupby(buffer, usages);
		this.renderHaving(buffer, usages);
		HashMap<Integer, Object> filtered = this.renderSelectFiltered(buffer,
				usages, filter, context);
		this.renderUnionFilterColumnUsing(buffer, usages, filtered);
	}

	final void renderFrom(ISqlSelectBuffer buffer, TableUsages usages) {
		if (this.rootRelationRef() != null) {
			this.rootRelationRef().render(buffer, usages);
		}
	}

	final void renderWhere(ISqlSelectBuffer buffer, TableUsages usages) {
		if (this.where != null) {
			this.where.render(buffer.where(), usages);
		}
	}

	final void renderGroupby(ISqlSelectBuffer buffer, TableUsages usages) {
		if (this.groupbys != null) {
			for (int i = 0, c = this.groupbys.size(); i < c; i++) {
				this.groupbys.get(i).value().render(buffer.newGroup(), usages);
			}
		}
		if (this.groupbyType == GroupByType.ROLL_UP) {
			buffer.rollup();
		}
	}

	final void renderHaving(ISqlSelectBuffer buffer, TableUsages usages) {
		if (this.having != null) {
			this.having.render(buffer.having(), usages);
		}
	}

	final void renderSelect(ISqlSelectBuffer buffer, TableUsages usages) {
		if (this.distinct) {
			buffer.distinct();
		}
		for (int i = 0, c = this.columns.size(); i < c; i++) {
			SelectColumnImpl<?, ?> column = this.columns.get(i);
			column.value().render(buffer.newColumn(column.name), usages);
		}
	}

	final <TContext> HashMap<Integer, Object> renderSelectFiltered(
			ISqlSelectBuffer buffer, TableUsages usages,
			EFilter<SelectColumnImpl<?, ?>, TContext> filter, TContext context) {
		if (this.distinct) {
			buffer.distinct();
		}
		HashMap<Integer, Object> filtered = new HashMap<Integer, Object>();
		for (int i = 0, c = this.columns.size(); i < c; i++) {
			SelectColumnImpl<?, ?> column = this.columns.get(i);
			if (filter != null && filter.accept(column, context)) {
				filtered.put(i, null);
			} else {
				column.value().render(buffer.newColumn(column.name), usages);
			}
		}
		return filtered;
	}

	static final FilteredColumnRender<HashMap<Integer, Object>> filteredColumnRender = new FilteredColumnRender<HashMap<Integer, Object>>() {

		public void renderSelect(ISqlSelectBuffer buffer, TableUsages usages,
				SelectImpl<?, ?> select, HashMap<Integer, Object> filtered) {
			if (select.distinct) {
				buffer.distinct();
			}
			for (int i = 0, c = select.columns.size(); i < c; i++) {
				if (!filtered.containsKey(i)) {
					SelectColumnImpl<?, ?> column = select.columns.get(i);
					column.value()
							.render(buffer.newColumn(column.name), usages);
				}
			}

		}
	};

	static interface FilteredColumnRender<TContext> {

		void renderSelect(ISqlSelectBuffer buffer, TableUsages usages,
				SelectImpl<?, ?> select, TContext context);
	}

	final void renderUnion(ISqlSelectBuffer buffer, TableUsages usages) {
		if (this.sets != null) {
			for (int i = 0, c = this.sets.size(); i < c; i++) {
				SetOperateImpl set = this.sets.get(i);
				ISqlSelectBuffer sb = buffer.newUnion(set.operator.unionAll());
				set.target.render(sb, usages);
			}
		}
	}

	final <TContext> void renderUnionFilterColumnUsing(ISqlSelectBuffer buffer,
			TableUsages usages, HashMap<Integer, Object> filtered) {
		if (this.sets != null) {
			for (int i = 0, c = this.sets.size(); i < c; i++) {
				SetOperateImpl set = this.sets.get(i);
				ISqlSelectBuffer sb = buffer.newUnion(set.operator.unionAll());
				set.target.renderFilterColumnUsing(sb, usages, filtered);
			}
		}
	}

}
