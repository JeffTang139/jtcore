package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.ObjectQuerier;
import org.eclipse.jt.core.def.MetaElementType;
import org.eclipse.jt.core.def.MissingDefineException;
import org.eclipse.jt.core.def.table.DBTableDefine;
import org.eclipse.jt.core.def.table.HierarchyDeclare;
import org.eclipse.jt.core.def.table.TableDeclarator;
import org.eclipse.jt.core.def.table.TableDeclare;
import org.eclipse.jt.core.def.table.TableDefine;
import org.eclipse.jt.core.def.table.TableFieldDefine;
import org.eclipse.jt.core.def.table.TableRelationType;
import org.eclipse.jt.core.exception.InvalidTableDefineExceptiopn;
import org.eclipse.jt.core.exception.NamedDefineExistingException;
import org.eclipse.jt.core.exception.NoPartitionDefineException;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.misc.SXMergeDelayAction;
import org.eclipse.jt.core.misc.SXMergeHelper;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.Digester;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.SequenceDataType;
import org.eclipse.jt.core.type.Typable;
import org.eclipse.jt.core.type.TypeDetector;
import org.eclipse.jt.core.type.TypeDetectorBase;


/**
 * �߼�����ʵ����
 * 
 * @author Jeff Tang
 */
public final class TableDefineImpl extends NamedDefineImpl implements
		TableDeclare, Relation, RelationRefOwner, ContainerListener,
		Declarative<TableDeclarator> {

	public final MetaElementType getMetaElementType() {
		return MetaElementType.TABLE;
	}

	public final TableDeclarator getDeclarator() {
		return this.declarator;
	}

	public final TableDefineImpl getRootType() {
		return this;
	}

	public final <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> caller, TUserData userData) {
		try {
			return caller.inTable(userData);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final void digestType(Digester digester) {
		digester.update(TypeCodeSet.TABLE_H);
		this.digestAuthAndName(digester);
		short c = (short) this.fields.size();
		digester.update(c);
		for (int i = 0; i < c; i++) {
			this.fields.get(i).digestType(digester);
		}
	}

	public final int getTupleElementCount() {
		return this.fields.size();
	}

	public final Typable getTupleElementType(int index) {
		return this.fields.get(index);
	}

	public final boolean isOriginal() {
		return this.declarator != null;
	}

	public final DBTableDefineImpl getPrimaryDBTable() {
		return this.primary;
	}

	public final NamedDefineContainerImpl<DBTableDefineImpl> getDBTables() {
		return this.dbTables;
	}

	public final TableFieldDefineImpl f_RECID() {
		return this.f_recid;
	}

	public final TableFieldDefineImpl f_RECVER() {
		return this.f_recver;
	}

	public final NamedDefineContainerImpl<TableFieldDefineImpl> getFields() {
		return this.fields;
	}

	public final TableFieldDefineImpl getColumn(String columnName) {
		return this.fields.get(columnName);
	}

	public final TableFieldDefineImpl findColumn(String columnName) {
		return this.fields.find(columnName);
	}

	public final NamedDefineContainerImpl<IndexDefineImpl> getIndexes() {
		return this.indexes;
	}

	public final NamedDefineContainerImpl<TableRelationDefineImpl> getRelations() {
		return this.relations;
	}

	public final NamedDefineContainerImpl<? extends HierarchyDeclare> getHierarchies() {
		return this.hierarchies;
	}

	public final DBTableDefineImpl newDBTable(String tableName) {
		return this.newTable(tableName);
	}

	public final TableFieldDefineImpl newPrimaryField(String name, DataType type) {
		return this.newField(this.primary, name, type, true);
	}

	public final TableFieldDefineImpl newField(String name, DataType type) {
		return this.newField(this.primary, name, type, false);
	}

	public final TableFieldDefineImpl newField(String name, DataType type,
			DBTableDefine dbTable) {
		if (dbTable == null) {
			throw new NullArgumentException("�������");
		}
		return this.newField((DBTableDefineImpl) dbTable, name, type, false);
	}

	public final IndexDefineImpl newIndex(String name) {
		return this.newIndex(this.primary, name, false);
	}

	public final IndexDefineImpl newIndex(String name, TableFieldDefine field) {
		if (field == null) {
			throw new NullArgumentException("�����ֶ�");
		}
		TableFieldDefineImpl f = (TableFieldDefineImpl) field;
		IndexDefineImpl index = this.newIndex(f.dbTable, name, false);
		index.addItem(f, false);
		return index;
	}

	public final IndexDefineImpl newIndex(String name, TableFieldDefine field,
			TableFieldDefine... others) {
		if (field == null) {
			throw new NullArgumentException("�����ֶ�");
		}
		TableFieldDefineImpl f = (TableFieldDefineImpl) field;
		IndexDefineImpl index = this.newIndex(f.dbTable, name, false);
		index.addItem(f, false);
		for (TableFieldDefine o : others) {
			index.addItem(o, false);
		}
		return index;
	}

	public final TableRelationDefineImpl newRelation(String name,
			TableDefine target, TableRelationType type) {
		if (target == null) {
			throw new NullArgumentException("��ϵĿ�����");
		}
		return this.newRelation(name, (TableDefineImpl) target, type);
	}

	public final TableRelationDefineImpl newRelation(String name,
			TableDeclarator target, TableRelationType type) {
		if (target == null) {
			throw new NullArgumentException("��ϵĿ�����");
		}
		return this.newRelation(name, (TableDefineImpl) target.getDefine(),
				type);
	}

	public final TableRelationDefineImpl newRelation(String name,
			TableFieldDefine selfField, TableDeclarator target,
			TableFieldDefine targetField, TableRelationType type) {
		if (target == null) {
			throw new NullArgumentException("��ϵ�����Ŀ���");
		}
		return this.newRelation(name, selfField, target.getDefine(),
				targetField, type);
	}

	public final TableRelationDefineImpl newRelation(String name,
			TableFieldDefine selfField, TableDefine target,
			TableFieldDefine targetField, TableRelationType type) {
		if (target == null) {
			throw new NullArgumentException("��ϵ�����Ŀ���");
		}
		if (selfField == null || selfField.getOwner() != this) {
			throw new NullArgumentException("��ֵ���ϵ�ı����ֶ�");
		} else if (selfField.getOwner() != this) {
			throw new IllegalArgumentException("�½���ֵ���ϵ����[" + name
					+ "]��,ָ���ı����ֶ�[" + selfField.getName() + "]�����ڵ�ǰ�߼�����["
					+ this.name + "].");
		}
		if (targetField == null) {
			throw new NullArgumentException("��ֵ���ϵ��Ŀ���ֶ�");
		} else if (targetField.getOwner() != target) {
			throw new IllegalArgumentException("�½���ֵ���ϵ����[" + name
					+ "]��,ָ����Ŀ����ֶ�[" + targetField.getName() + "]������Ŀ���߼�����["
					+ target.getName() + "].");
		}
		TableRelationDefineImpl relation = this.newRelation(name,
				(TableDefineImpl) target, type);
		relation.setJoinCondition(this.expOf(selfField).xEq(
				relation.expOf(targetField)));
		return relation;
	}

	public final HierarchyDefineImpl newHierarchy(String name, int maxlevel) {
		this.checkModifiable();
		if (name == null || name.length() == 0) {
			throw new NullArgumentException("������");
		}
		if (maxlevel < 0 || HierarchyDefineImpl.MAX_LEVEL < maxlevel) {
			throw new IllegalArgumentException("�����֧�ֵ���󼶴�ֵ[" + maxlevel
					+ "].");
		}
		if (this.hierarchies.contains(name)) {
			throw existing(name, "����");
		}
		HierarchyDefineImpl hierarchy = new HierarchyDefineImpl(this, name,
				maxlevel);
		this.hierarchies.add(hierarchy);
		return hierarchy;
	}

	public final boolean isPartitioned() {
		return this.partfields.size() > 0;
	}

	public final NamedDefineContainerImpl<TableFieldDefineImpl> getPartitionFields() {
		return this.partfields;
	}

	public final void setPartitionFields(TableFieldDefine field,
			TableFieldDefine... others) {
		this.checkModifiable();
		this.partfields.clear();
		this.addPartField(field);
		if (others != null) {
			for (TableFieldDefine other : others) {
				this.addPartField(other);
			}
		}
	}

	public final void addPartitionField(TableFieldDefine field,
			TableFieldDefine... others) {
		this.checkModifiable();
		this.addPartField(field);
		if (others != null) {
			for (TableFieldDefine other : others) {
				this.addPartField(other);
			}
		}
	}

	public final int getMaxPartitionCount() {
		if (!this.isPartitioned()) {
			throw new NoPartitionDefineException(this);
		}
		return this.maxPartCount;
	}

	public final void setMaxPartitionCount(int maxPartCount) {
		this.checkModifiable();
		if (!this.isPartitioned()) {
			throw new NoPartitionDefineException(this);
		}
		if (maxPartCount < 0) {
			throw new IllegalArgumentException("���������������[" + maxPartCount
					+ "].");
		}
		this.maxPartCount = maxPartCount;
	}

	public final int getPartitionSuggestion() {
		if (!this.isPartitioned()) {
			throw new NoPartitionDefineException(this);
		}
		return this.partSuggestion;
	}

	public final void setParitionSuggestion(int suggestion) {
		this.checkModifiable();
		if (suggestion < 0) {
			throw new IllegalArgumentException("����ķ��������д�С[" + suggestion
					+ "].");
		}
		this.partSuggestion = suggestion;
	}

	public final String getCategory() {
		return this.category;
	}

	public final void setCategory(String category) {
		this.checkModifiable();
		// so strange !!!
		if (category == null || category.length() == 0) {
			return;
		}
		this.category = category;
	}

	public final TableFieldRefImpl expOf(TableFieldDefine field) {
		return this.selfRef.expOf(field);
	}

	static final String DUMMY_NAME = "DUMMY";

	public static final TableDefineImpl DUMMY = new TableDefineImpl(DUMMY_NAME,
			null);

	static final String FIELD_NAME_RECID = "RECID";
	static final String FIELD_NAME_RECVER = "RECVER";

	static final String FIELD_DBNAME_RECVER = "RECVER";
	static final String FIELD_DBNAME_RECID = "RECID";

	static final String FILED_TITLE_RECID = "�б�ʶ";
	static final String FILED_TITLE_RECVER = "�а汾";

	/**
	 * ����������ǰ׺��
	 */
	static final String DNA_PK_PREFIX = "PK_";

	/**
	 * �߼�������ǰ׺��
	 */
	static final String DNA_LK_PREFIX = "LK_";

	/**
	 * ������������
	 */
	static final int MAX_DBTABLE_SIZE = 32;

	/**
	 * ��󼶴ζ�����
	 */
	static final int MAX_HIERARCHY_SIZE = 32;

	/**
	 * ����Ԫ�������ݿ�洢��RECID
	 */
	GUID id;

	/**
	 * ��������
	 */
	final TableDeclarator declarator;

	/**
	 * �б�ʶ�ֶ�
	 */
	public final TableFieldDefineImpl f_recid;

	/**
	 * �а汾�ֶ�
	 */
	public final TableFieldDefineImpl f_recver;

	/**
	 * ���,��������,��������,���������
	 */
	String category;

	/**
	 * �������
	 */
	final DBTableDefineImpl primary;

	/**
	 * ��������б�
	 */
	final NamedDefineContainerImpl<DBTableDefineImpl> dbTables;

	/**
	 * �ֶζ����б�
	 */
	final NamedDefineContainerImpl<TableFieldDefineImpl> fields;

	/**
	 * �߼���������
	 */
	IndexDefineImpl logicalKey;

	/**
	 * ���������б�
	 */
	final NamedDefineContainerImpl<IndexDefineImpl> indexes;

	/**
	 * ���ϵ�����б�
	 */
	final NamedDefineContainerImpl<TableRelationDefineImpl> relations;

	/**
	 * ���ζ���
	 */
	final NamedDefineContainerImpl<HierarchyDefineImpl> hierarchies;

	/**
	 * ��ǰ��������,���ϵ��ʹ��
	 */
	final TableSelfRef selfRef;

	public TableDefineImpl(String name, TableDeclarator declarator) {
		super(name);
		this.declarator = declarator;
		this.dbTables = new NamedDefineContainerImpl<DBTableDefineImpl>(this);
		this.fields = new NamedDefineContainerImpl<TableFieldDefineImpl>(this);
		this.indexes = new NamedDefineContainerImpl<IndexDefineImpl>(this);
		this.relations = new NamedDefineContainerImpl<TableRelationDefineImpl>(
				this);
		this.hierarchies = new NamedDefineContainerImpl<HierarchyDefineImpl>(
				this);
		this.partfields = new NamedDefineContainerImpl<TableFieldDefineImpl>(
				this);
		this.primary = new DBTableDefineImpl(this, name);
		this.dbTables.add(this.primary);
		this.fields.add(this.f_recid = newRecid(this));
		this.primary.store(this.f_recid);
		this.fields.add(this.f_recver = newRecver(this));
		this.primary.store(this.f_recver);
		this.selfRef = new TableSelfRef(this);
	}

	private static final TableFieldDefineImpl newRecid(TableDefineImpl table) {
		TableFieldDefineImpl recid = new TableFieldDefineImpl(table,
				table.primary, FIELD_NAME_RECID, FIELD_DBNAME_RECID,
				GUIDType.TYPE, true);
		recid.setTitle(FILED_TITLE_RECID);
		return recid;
	}

	private static final TableFieldDefineImpl newRecver(TableDefineImpl table) {
		TableFieldDefineImpl recver = new TableFieldDefineImpl(table,
				table.primary, FIELD_NAME_RECVER, FIELD_DBNAME_RECVER,
				LongType.TYPE, false);
		recver.setTitle(FILED_TITLE_RECVER);
		recver.setDefault(LongConstExpr.ZERO);
		return recver;
	}

	final DBTableDefineImpl newTable(String tableName) {
		this.checkModifiable();
		if (tableName == null || tableName.length() == 0) {
			throw new NullArgumentException("��������");
		}
		if (this.dbTables.size() == TableDefineImpl.MAX_DBTABLE_SIZE) {
			throw new InvalidTableDefineExceptiopn(this, "�߼���[" + this.name
					+ "]����������Ѵ�����,���������Ӹ���������.");
		}
		// compatible for 2.0
		tableName = tableName.toUpperCase();
		if (this.dbTables.contains(tableName)) {
			throw existing(tableName, "�����");
		}
		DBTableDefineImpl dbTable = new DBTableDefineImpl(this, tableName);
		this.dbTables.add(dbTable);
		return dbTable;
	}

	final TableFieldDefineImpl newField(DBTableDefineImpl dbTable, String name,
			DataType type, boolean logicalKey) {
		this.checkModifiable();
		if (name == null || name.length() == 0) {
			throw new NullArgumentException("�ֶ�����");
		}
		if (type == null) {
			throw new NullArgumentException("�ֶ�����");
		}
		if (this.fields.contains(name)) {
			throw existing(name, "�ֶ�");
		}
		if (dbTable.owner != this) {
			throw notOwnTable(this, dbTable);
		}
		if (logicalKey && dbTable != this.primary) {
			throw keyOnSlave(this.name, name);
		}
		String namedb = name.toUpperCase();
		dbTable.fields.validateKey(namedb);
		TableFieldDefineImpl field = new TableFieldDefineImpl(this, dbTable,
				name, namedb, type, false);
		if (logicalKey) {
			field.setPrimaryKey(logicalKey);
		}
		this.fields.add(field);
		dbTable.store(field);
		return field;
	}

	final TableFieldDefineImpl findFieldUsingNamedb(String namedb) {
		for (int i = 0, c = this.dbTables.size(); i < c; i++) {
			TableFieldDefineImpl field = this.dbTables.get(i).fields
					.get(namedb);
			if (field != null) {
				return field;
			}
		}
		return null;
	}

	final IndexDefineImpl newIndex(DBTableDefineImpl dbTable, String name,
			boolean unique) {
		this.checkModifiable();
		if (name == null || name.length() == 0) {
			throw new NullArgumentException("��������");
		}
		if (dbTable.owner != this) {
			throw notOwnTable(this, dbTable);
		}
		if (this.indexes.contains(name)) {
			throw existing(name, "����");
		}
		IndexDefineImpl index = new IndexDefineImpl(this, dbTable, name, unique);
		this.indexes.add(index);
		return index;
	}

	private static final IllegalArgumentException notOwnTable(
			TableDefineImpl t, DBTableDefineImpl dt) {
		return new IllegalArgumentException("�����[" + dt.name + "]�����ڵ�ǰ�߼���["
				+ t.name + "].");
	}

	final TableRelationDefineImpl newRelation(String name,
			TableDefineImpl target, TableRelationType type) {
		this.checkModifiable();
		if (name == null || name.length() == 0) {
			throw new NullArgumentException("���ϵ����");
		}
		if (type == null) {
			throw new NullArgumentException("���ϵ����");
		}
		if (this.relations.contains(name)) {
			throw existing(name, "���ϵ");
		}
		if (name.equals(this.selfRef.name)) {
			throw new UnsupportedOperationException("�½����ϵ����[" + name
					+ "]�������߼�������һ��.");
		}
		TableRelationDefineImpl relation = new TableRelationDefineImpl(this,
				name, target);
		relation.type = type;
		this.relations.add(relation);
		return relation;
	}

	private static final NamedDefineExistingException existing(String name,
			String cat) {
		return new NamedDefineExistingException("����Ϊ[" + name + "]��" + cat
				+ "�����Ѿ�����.");
	}

	private static final InvalidTableDefineExceptiopn keyOnSlave(String table,
			String field) {
		return new InvalidTableDefineExceptiopn(null, "�߼������ֶ�[" + field
				+ "]�������߼���[" + table + "]���������.");
	}

	static final UnsupportedOperationException cantSetPrimaryKey(
			TableFieldDefineImpl field) {
		return new UnsupportedOperationException("�߼���[" + field.owner.name
				+ "]��֧�������ֶ�[" + field.name + "]���߼���������.");
	}

	final boolean addKey(TableFieldDefineImpl field) {
		this.checkModifiable();
		if (field.isRECID() || field.isRECVER()) {
			throw cantSetPrimaryKey(field);
		}
		if (!field.dbTable.isPrimary()) {
			throw keyOnSlave(this.name, field.name);
		}
		if (this.logicalKey == null) {
			this.logicalKey = this.newLogicalKey();
			this.logicalKey.addItem(field, false);
			field.setKeepValid(true);
			return true;
		} else if (this.logicalKey.findItem(field) == null) {
			this.logicalKey.addItem(field, false);
			field.setKeepValid(true);
			return true;
		}
		return false;
	}

	private final IndexDefineImpl newLogicalKey() {
		return new IndexDefineImpl(this, this.primary,
				DNA_LK_PREFIX.concat(this.name.toUpperCase()), true);
	}

	final boolean removeKey(TableFieldDefineImpl field) {
		this.checkModifiable();
		if (field.isRECID() || field.isRECVER()) {
			throw cantSetPrimaryKey(field);
		}
		if (this.logicalKey == null) {
			return false;
		}
		final IndexItemImpl item = this.logicalKey.findItem(field);
		if (item == null) {
			return false;
		} else {
			this.logicalKey.items.remove(item);
			if (this.logicalKey.items.size() == 0) {
				this.logicalKey = null;
			}
			return true;
		}
	}

	final void checkLogicalKeyAvaiable() {
		if (this.logicalKey == null) {
			throw new UnsupportedOperationException("�߼���[" + this.name
					+ "]δ�����߼�����.");
		}
	}

	/**
	 * ����������
	 */
	private int maxPartCount;

	/**
	 * ���������С
	 */
	private int partSuggestion;

	/**
	 * ������ֶ�
	 */
	final NamedDefineContainerImpl<TableFieldDefineImpl> partfields;

	/**
	 * ��齫����Ϊ�������ֶ�
	 * 
	 * <p>
	 * ���ڵ�ǰ�߼���;��������;���Ϳ���������;���ڷ����ֶ��б���.
	 */
	private final TableFieldDefineImpl checkPartField(TableFieldDefine field) {
		if (field == null) {
			throw new NullArgumentException("�ֶζ���");
		}
		TableFieldDefineImpl f = (TableFieldDefineImpl) field;
		if (f.owner != this) {
			throw new IllegalArgumentException("�����ֶ�[" + f.name + "]�����ڵ�ǰ�߼���["
					+ this.name + "].");
		}
		if (!f.dbTable.isPrimary()) {
			throw new InvalidTableDefineExceptiopn(this, "�����ֶ�[" + f.name
					+ "]δ�洢���߼���[" + this.name + "]�����������.");
		}
		if (this.partfields.find(f.name) != null) {
			throw new InvalidTableDefineExceptiopn(this, "�߼���[" + this.name
					+ "]�������ظ��ķ����ֶ�[" + f.name + "]");
		}
		f.getType().detect(partitionFieldTypeChecker, f);
		return f;
	}

	private static final TypeDetectorBase<Object, TableFieldDefineImpl> partitionFieldTypeChecker = new TypeDetectorBase<Object, TableFieldDefineImpl>() {

		@Override
		public Object inBoolean(TableFieldDefineImpl field) throws Throwable {
			return null;
		}

		@Override
		public Object inShort(TableFieldDefineImpl field) throws Throwable {
			return null;
		}

		@Override
		public Object inInt(TableFieldDefineImpl field) throws Throwable {
			return null;
		}

		@Override
		public Object inLong(TableFieldDefineImpl field) throws Throwable {
			return null;
		}

		@Override
		public Object inFloat(TableFieldDefineImpl field) throws Throwable {
			return null;
		}

		@Override
		public Object inDouble(TableFieldDefineImpl field) throws Throwable {
			return null;
		}

		@Override
		public Object inNumeric(TableFieldDefineImpl field, int precision,
				int scale) throws Throwable {
			return null;
		}

		@Override
		public Object inChar(TableFieldDefineImpl field, SequenceDataType type)
				throws Throwable {
			return null;
		}

		@Override
		public Object inVarChar(TableFieldDefineImpl field,
				SequenceDataType type) throws Throwable {
			return null;
		}

		@Override
		public Object inText(TableFieldDefineImpl field) throws Throwable {
			throw fieldTypeUnsupportPartition(field);
		}

		@Override
		public Object inNChar(TableFieldDefineImpl field, SequenceDataType type)
				throws Throwable {
			return null;
		}

		@Override
		public Object inNVarChar(TableFieldDefineImpl field,
				SequenceDataType type) throws Throwable {
			return null;
		}

		@Override
		public Object inNText(TableFieldDefineImpl field) throws Throwable {
			throw fieldTypeUnsupportPartition(field);
		}

		@Override
		public Object inBinary(TableFieldDefineImpl field, SequenceDataType type)
				throws Throwable {
			return null;
		}

		@Override
		public Object inVarBinary(TableFieldDefineImpl field,
				SequenceDataType type) throws Throwable {
			return null;
		}

		@Override
		public Object inBlob(TableFieldDefineImpl field) throws Throwable {
			throw fieldTypeUnsupportPartition(field);
		}

		@Override
		public Object inGUID(TableFieldDefineImpl field) throws Throwable {
			return null;
		}

		@Override
		public Object inDate(TableFieldDefineImpl field) throws Throwable {
			return null;
		}

	};

	private static final InvalidTableDefineExceptiopn fieldTypeUnsupportPartition(
			TableFieldDefineImpl field) {
		return new InvalidTableDefineExceptiopn(field.owner, "�ֶ�["
				+ field.owner.name + "." + field.name + "]����������["
				+ field.getType().toString() + "]������Ϊ�����ֶ�.");
	}

	final void addPartField(TableFieldDefine field) {
		if (field == null) {
			throw new NullArgumentException("�����ֶ�");
		}
		this.partfields.add(this.checkPartField(field));
	}

	final void initPartAttr(DBLang lang) {
		if (this.isPartitioned()) {
			if (this.maxPartCount <= 0) {
				this.maxPartCount = lang.getMaxTablePartCount();
			}
			if (this.partSuggestion <= 0) {
				this.partSuggestion = lang.getDefaultPartSuggestion();
			}
		}
	}

	public final void beforeMoving(ContainerImpl<?> container, int from, int to) {
		if (container == this.dbTables) {
			if (from == 0 || to == 0) {
				throw new UnsupportedOperationException("�����ƶ��������.");
			}
		} else if (container == this.fields) {
			if (from == 0 || to == 0) {
				throw new UnsupportedOperationException("��֧���ƶ�recid�ֶ�.");
			} else if (from == 1 || to == 1) {
				throw new UnsupportedOperationException("��֧���ƶ�recver�ֶ�.");
			}
		}
	}

	public final void beforeClearing(ContainerImpl<?> container) {
		if (container == this.dbTables) {
			throw new UnsupportedOperationException("��֧���Ƴ������������.");
		} else if (container == this.fields) {
			throw new UnsupportedOperationException("��֧���Ƴ������ֶζ���.");
		}
	}

	public final void beforeRemoving(ContainerImpl<?> container, int index) {
		if (container == this.dbTables) {
			this.beforeRemoving(this.dbTables.get(index));
		} else if (container == this.fields) {
			this.beforeRemoving(this.fields.get(index));
		}
	}

	public final void beforeRemoving(ContainerImpl<?> container, Object o) {
		this.checkModifiable();
		if (container == this.dbTables) {
			this.beforeRemoving((DBTableDefineImpl) o);
		} else if (container == this.fields) {
			this.beforeRemoving((TableFieldDefineImpl) o);
		}
	}

	private final void beforeRemoving(DBTableDefineImpl t) {
		if (t == this.primary) {
			throw new UnsupportedOperationException("��֧���Ƴ���������.");
		}
		if (t.getFieldCount() > 0) {
			throw new UnsupportedOperationException("��֧���Ƴ���Ȼ�����ֶε������[" + t.name
					+ "].");
		}
	}

	private final void beforeRemoving(TableFieldDefineImpl f) {
		if (f == this.f_recid) {
			throw new UnsupportedOperationException("��֧���Ƴ�recid��.");
		} else if (f == this.f_recver) {
			throw new UnsupportedOperationException("��֧���Ƴ�recver��.");
		}
		if (f.isPrimaryKey()) {
			throw new UnsupportedOperationException("��֧���Ƴ��߼�����[" + f.name + "]");
		}
		if (this.partfields.contains(f)) {
			throw new UnsupportedOperationException("��֧���Ƴ������ֶ�.");
		}
		for (int i = 0, c = this.indexes.size(); i < c; i++) {
			IndexDefineImpl index = this.indexes.get(i);
			if (index.findItem(f) != null) {
				throw new UnsupportedOperationException("��֧���Ƴ��ֶ�[" + f.name
						+ "],���ֶα�Ϊ����[" + index.getName() + "]��������.");
			}
		}
		f.dbTable.unstore(f);
	}

	public final TableRef findRelationRef(String name) {
		if (this.selfRef.name.equals(name)) {
			return this.selfRef;
		}
		return this.relations.find(name);
	}

	public final TableRef getRelationRef(String name) {
		TableRef relationRef = this.findRelationRef(name);
		if (relationRef != null) {
			return relationRef;
		}
		throw new MissingDefineException();
	}

	final Sequencer hierarchySequencer = new Sequencer();

	/**
	 * ��¡����
	 * 
	 * @param querier
	 * @return
	 */
	final TableDefineImpl clone(ObjectQuerier querier) {
		return new TableDefineImpl(this, querier);
	}

	private TableDefineImpl(TableDefineImpl sample, ObjectQuerier querier) {
		super(sample);
		this.id = sample.id;
		this.category = sample.category;
		this.declarator = null;
		this.dbTables = new NamedDefineContainerImpl<DBTableDefineImpl>(this);
		this.fields = new NamedDefineContainerImpl<TableFieldDefineImpl>(this);
		this.indexes = new NamedDefineContainerImpl<IndexDefineImpl>(this);
		this.relations = new NamedDefineContainerImpl<TableRelationDefineImpl>(
				this);
		this.hierarchies = new NamedDefineContainerImpl<HierarchyDefineImpl>(
				this);
		this.partfields = new NamedDefineContainerImpl<TableFieldDefineImpl>(
				this);
		this.primary = sample.primary.clone(this);
		this.dbTables.add(this.primary);
		for (int i = 1, c = sample.dbTables.size(); i < c; i++) {
			this.dbTables.add(sample.dbTables.get(i).clone(this));
		}
		this.fields.add(this.f_recid = sample.f_recid.clone(this));
		this.fields.add(this.f_recver = sample.f_recver.clone(this));
		for (int i = 2, c = sample.fields.size(); i < c; i++) {
			this.fields.add(sample.fields.get(i).clone(this));
		}
		if (sample.logicalKey != null) {
			this.logicalKey = sample.logicalKey.clone(this);
		}
		for (int i = 0, c = sample.indexes.size(); i < c; i++) {
			this.indexes.add(sample.indexes.get(i).clone(this));
		}
		for (int i = 0, c = sample.hierarchies.size(); i < c; i++) {
			this.hierarchies.add(sample.hierarchies.get(i).clone(this));
		}
		this.selfRef = new TableSelfRef(this);
		for (int i = 0, c = sample.relations.size(); i < c; i++) {
			this.relations.add(sample.relations.get(i).clone(this, querier));
		}
		if (sample.partfields.size() > 0) {
			for (int i = 0, c = sample.partfields.size(); i < c; i++) {
				this.partfields
						.add(this.fields.get(sample.partfields.get(i).name));
			}
		}
		this.maxPartCount = sample.maxPartCount;
		this.partSuggestion = sample.partSuggestion;
	}

	/**
	 * ��ȫת��Ϊ�����ṹ
	 * 
	 * @param cloned
	 *            ��ǰ��Ŀ�¡
	 * @param querier
	 */
	// only called from table post where has already synchronized invoke.
	final void assignFrom(TableDefineImpl cloned, ObjectQuerier querier) {
		if (cloned.id != this.id || !cloned.name.equals(this.name)) {
			throw new IllegalStateException("��������[" + cloned.name
					+ "]���ǵ�ǰ����Ŀ�¡.");
		}
		super.assignFrom(cloned);
		this.category = cloned.category;
		this.assignHierarchiesFrom(cloned);
		this.assignPartitionDefineFrom(cloned);
		this.assignIndexesFrom(cloned);
		// attentiton ! field removing depend on indexes & part define
		this.assignFieldsFrom(cloned);
		// attentiton ! dbtable removing depend on field
		this.assignTablesFrom(cloned);
		this.assignRelationsFrom(cloned, querier);
	}

	private final void assignHierarchiesFrom(TableDefineImpl cloned) {
		for (int i = 0, c = cloned.hierarchies.size(); i < c; i++) {
			HierarchyDefineImpl from = cloned.hierarchies.get(i);
			HierarchyDefineImpl to = this.hierarchies.find(from.name);
			if (to == null) {
				to = from.clone(this);
				this.hierarchies.add(i, to);
			} else {
				this.hierarchies.ensureElementAt(to, i);
				to.assignFrom(from);
			}
		}
		this.hierarchies.trunc(cloned.hierarchies.size());
	}

	private final void assignRelationsFrom(TableDefineImpl cloned,
			ObjectQuerier querier) {
		for (int i = 0, c = cloned.relations.size(); i < c; i++) {
			TableRelationDefineImpl from = cloned.relations.get(i);
			TableRelationDefineImpl to = this.relations.find(from.name);
			if (to == null) {
				to = from.clone(this, querier);
				this.relations.add(i, to);
			} else {
				this.relations.ensureElementAt(to, i);
				to.assignFrom(from);
			}
		}
		this.relations.trunc(cloned.relations.size());
	}

	private final void assignIndexesFrom(TableDefineImpl cloned) {
		if (cloned.logicalKey != null) {
			if (this.logicalKey != null) {
				this.logicalKey.assignFrom(cloned.logicalKey);
			} else {
				this.logicalKey = cloned.logicalKey.clone(this);
			}
		}
		for (int i = 0, c = cloned.indexes.size(); i < c; i++) {
			IndexDefineImpl from = cloned.indexes.get(i);
			IndexDefineImpl to = this.indexes.find(from.name);
			if (to == null) {
				to = from.clone(this);
				this.indexes.add(i, to);
			} else {
				this.indexes.ensureElementAt(to, i);
				to.assignFrom(from);
			}
		}
		this.indexes.trunc(cloned.indexes.size());
	}

	private final void assignPartitionDefineFrom(TableDefineImpl cloned) {
		this.partfields.clear();
		for (int i = 0, c = cloned.partfields.size(); i < c; i++) {
			this.partfields.add(this.fields.get(cloned.partfields.get(i).name));
		}
		this.maxPartCount = cloned.maxPartCount;
		this.partSuggestion = cloned.partSuggestion;
	}

	private final void assignFieldsFrom(TableDefineImpl cloned) {
		for (int i = 0, c = cloned.fields.size(); i < c; i++) {
			TableFieldDefineImpl from = cloned.fields.get(i);
			if (from.isRECID() || from.isRECVER()) {
				continue;
			}
			TableFieldDefineImpl to = this.fields.find(from.name);
			if (to == null) {
				to = from.clone(this);
				this.fields.add(i, to);
			} else {
				this.fields.ensureElementAt(to, i);
				to.assignFrom(from);
			}
		}
		// trunc operation lead to before removing field listener
		this.fields.trunc(cloned.fields.size());
	}

	private final void assignTablesFrom(TableDefineImpl cloned) {
		this.primary.assignFrom(cloned.primary);
		for (int i = 1, c = cloned.dbTables.size(); i < c; i++) {
			DBTableDefineImpl from = cloned.dbTables.get(i);
			DBTableDefineImpl to = this.dbTables.find(from.name);
			if (to == null) {
				to = from.clone(this);
				this.dbTables.add(i, to);
			} else {
				this.dbTables.ensureElementAt(to, i);
				to.assignFrom(from);
			}
		}
		for (int i = this.dbTables.size() - 1; i >= 1; i--) {
			DBTableDefineImpl cur = this.dbTables.get(i);
			if (cloned.dbTables.find(cur.name) == null) {
				this.dbTables.remove(i);
			}
		}
	}

	@Override
	public final String getXMLTagName() {
		return xml_name;
	}

	static final String xml_name = "tabledefine";

	@Override
	public final void render(SXElement element) {
		TableXML.V25.render(this, element);
	}

	// only called from startup step, thread safe
	@Override
	final void merge(SXElement element, SXMergeHelper helper) {
		TableXML.detect(element).merge(this, element);
		this.mergeRelationForLoadStep(element, helper);
	}

	private final void mergeRelationForLoadStep(SXElement element,
			SXMergeHelper helper) {
		for (SXElement e = element.firstChild(TableXML.table_element_relations,
				TableRelationDefineImpl.xml_name); e != null; e = e
				.nextSibling(TableRelationDefineImpl.xml_name)) {
			final String rn = e.getString(NamedDefineImpl.xml_attr_name);
			final String target = e.getString(TableRef.xml_attr_table);
			TableRelationDefineImpl relation = this.relations.find(rn);
			if (relation == null) {
				helper.addDelayAction(MetaElementLoadStep.TABLES,
						new RelationStartupMerger(e, rn));
			} else if (!target.equals(relation.target.name)) {
				// HCL
				throw new UnsupportedOperationException("ͬ�����ϵ����Ŀ���ͬ.");
			} else {
				TableXML.relationXML.merge(relation, e);
			}
		}
	}

	private final class RelationStartupMerger implements
			SXMergeDelayAction<MetaElementLoadStep> {

		final SXElement element;

		final String name;

		RelationStartupMerger(SXElement element, String name) {
			this.element = element;
			this.name = name;
		}

		public void doAction(MetaElementLoadStep at, SXMergeHelper helper,
				SXElement atElement) {
			String targetName = this.element.getAttribute(
					TableRef.xml_attr_table, null);
			if (targetName == null) {
				// �������ڰ汾
				targetName = this.element.getAttribute("target", null);
			}
			if (targetName == null) {
				throw new IllegalArgumentException("������ϵ��XML����ṹ����.");
			}
			TableRelationDefineImpl relation = TableDefineImpl.this.relations
					.find(this.name);
			if (relation == null) {
				TableDefineImpl target = (TableDefineImpl) helper.querier.get(
						TableDefine.class, targetName);
				relation = new TableRelationDefineImpl(TableDefineImpl.this,
						this.name, target);
				TableDefineImpl.this.relations.add(relation);
			}
			TableXML.relationXML.merge(relation, this.element);
		}
	}

	// support for ide
	final void mergeDelayRelation(SXElement element, SXMergeHelper helper) {
		TableXML.detect(element).merge(this, element);
		this.mergeRelationDelay(element, helper);
	}

	private final void mergeRelationDelay(SXElement element,
			SXMergeHelper helper) {
		for (SXElement e = element.firstChild(TableXML.table_element_relations,
				TableRelationDefineImpl.xml_name); e != null; e = e
				.nextSibling(TableRelationDefineImpl.xml_name)) {
			final String rn = e.getString(NamedDefineImpl.xml_attr_name);
			final String target = e.getString(TableRef.xml_attr_table);
			TableRelationDefineImpl relation = this.relations.find(rn);
			if (relation == null) {
				helper.addDelayAction(this, new RelationMerger(this, e));
			} else if (!target.equals(relation.target.name)) {
				// HCL
				throw new UnsupportedOperationException("ͬ�����ϵ����Ŀ���ͬ.");
			} else {
				TableXML.relationXML.merge(relation, e);
			}
		}
	}

	private static final class RelationMerger implements
			SXMergeDelayAction<TableDefineImpl> {

		final TableDefineImpl table;
		final SXElement element;

		RelationMerger(TableDefineImpl table, SXElement element) {
			this.table = table;
			this.element = element;
		}

		public void doAction(TableDefineImpl at, SXMergeHelper helper,
				SXElement atElement) {
			String targetName = this.element.getAttribute(
					TableRef.xml_attr_table, null);
			if (targetName == null) {
				// �������ڰ汾
				targetName = this.element.getAttribute("target", null);
			}
			if (targetName == null) {
				throw new IllegalArgumentException("������ϵ��XML����ṹ����.");
			}
			final String rn = this.element.getString(xml_attr_name);
			TableRelationDefineImpl relation = this.table.relations.find(rn);
			if (relation == null) {
				TableDefineImpl target = (TableDefineImpl) helper.querier.get(
						TableDefine.class, targetName);
				relation = this.table.newRelation(rn, target,
						TableRelationType.REFERENCE);
			}
			TableXML.relationXML.merge(relation, this.element);
		}

	}

	static final class Sequencer {

		private int seq;

		Sequencer() {
			this.seq = 0;
		}

		Sequencer(int from) {
			this.seq = from < 0 ? 0 : from;
		}

		final int next() {
			return this.seq++;
		}
	}

}
