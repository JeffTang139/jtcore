package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.table.TableRelationType;
import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.type.DataType;

enum TableXML implements DefineXML<TableDefineImpl> {

	V19("1,9") {

		public final void render(TableDefineImpl table, SXElement element) {
			NamedDefineImpl.render(table, element);
			renderCat(table, element);
			SXElement dbTables = element.append(table_element_dbtables);
			for (DBTableDefineImpl dbTable : table.dbTables) {
				this.dbTableXML.render(dbTable,
						dbTables.append(DBTableDefineImpl.xml_name));
			}
			if (table.fields.size() > 2) {
				SXElement p = element.append(table_element_fields);
				for (TableFieldDefineImpl field : table.fields) {
					if (field.isRECID() || field.isRECVER()) {
						continue;
					}
					this.fieldXML.render(field,
							p.append(TableFieldDefineImpl.xml_name));
				}
			}
			if (table.indexes.size() > 0) {
				SXElement p = element.append(table_element_indexes);
				for (IndexDefineImpl index : table.indexes) {
					this.indexXML.render(index,
							p.append(IndexDefineImpl.xml_name));
				}
			}
			renderRelation(table, element);
			renderHierarchiy(table, element);
		}

		public final void merge(TableDefineImpl table, SXElement element) {
			NamedDefineImpl.merge(table, element);
			mergeCat(table, element);
			for (SXElement e = element.firstChild(table_element_dbtables,
					DBTableDefineImpl.xml_name); e != null; e = e
					.nextSibling(DBTableDefineImpl.xml_name)) {
				String name = e.getString(NamedDefineImpl.xml_attr_name);
				name = name.toUpperCase();
				DBTableDefineImpl dbTable = table.dbTables.find(name);
				if (dbTable == null) {
					dbTable = new DBTableDefineImpl(table, name);
					table.dbTables.add(dbTable);
				}
				this.dbTableXML.merge(dbTable, e);
			}
			for (SXElement e = element.firstChild(table_element_fields,
					TableFieldDefineImpl.xml_name); e != null; e = e
					.nextSibling(TableFieldDefineImpl.xml_name)) {
				final String tn = e.getString(FieldXML.field_attr_dbtable);
				final DBTableDefineImpl dbTable = table.dbTables.get(tn);
				final String fn = e.getString(NamedDefineImpl.xml_attr_name);
				TableFieldDefineImpl field = table.fields.find(fn);
				if (field == null) {
					field = TableFieldDefineImpl
							.newForMerge(table, dbTable, fn);
					dbTable.store(field);
					table.fields.add(field);
				} else if (field.dbTable != dbTable) {
					throw migrateField(field, field.dbTable, dbTable);
				}
				this.fieldXML.merge(field, e);
			}
			for (SXElement e = element.firstChild(table_element_indexes,
					IndexDefineImpl.xml_name); e != null; e = e
					.nextSibling(IndexDefineImpl.xml_name)) {
				final String in = e.getString(NamedDefineImpl.xml_attr_name);
				String dbTableName = e.getString(IndexXML.index_attr_dbtable);
				DBTableDefineImpl dbTable;
				if (dbTableName == null) {
					// td文件,不包含dbTable属性.
					TableFieldDefineImpl tf = table.fields.get(e.firstChild()
							.getString("field"));
					dbTable = tf.getDBTable();
				} else {
					dbTable = table.dbTables.get(dbTableName);
				}
				IndexDefineImpl index = table.indexes.find(in);
				if (index == null) {
					index = IndexDefineImpl.newForMerge(table, dbTable, in);
					table.indexes.add(index);
				} else if (index.dbTable != dbTable) {
					throw migrateIndex(index, index.dbTable, dbTable);
				}
				this.indexXML.merge(index, e);
			}
		}

		private static final String attr_namedb = "name-db";

		private final String namedbOf(SXElement element, String def) {
			return element.getAttribute(attr_namedb, def);
		}

		private final DBTableXML dbTableXML = new DBTableXML() {

			private static final String dbtable_attr_pkindex = "recid-ix-name-db";
			private static final String dbtable_attr_lpkindex = "lpk-ix-name-db";

			public final void render(DBTableDefineImpl dbTable, SXElement e) {
				NamedDefineImpl.render(dbTable, e);
				e.setString(attr_namedb, dbTable.name);
				e.setAttribute(dbtable_attr_pkindex, dbTable.getPkeyName());
				IndexDefineImpl logicalKey = dbTable.owner.logicalKey;
				if (dbTable.isPrimary() && logicalKey != null) {
					e.setAttribute(dbtable_attr_lpkindex, logicalKey.name);
				}
			}

			public final void merge(DBTableDefineImpl dbTable, SXElement e) {
				NamedDefineImpl.merge(dbTable, e);
				dbTable.setNamedb(namedbOf(e, dbTable.namedb()));
			}
		};

		private final FieldXML fieldXML = new FieldXML() {
			private static final String field_attr_type = "type";
			private static final String field_attr_key = "primary-key";
			private static final String field_attr_notnull = "keep-valid";
			private static final String field_element_default = "default";

			public final void render(TableFieldDefineImpl field, SXElement e) {
				NamedDefineImpl.render(field, e);
				e.setString(attr_namedb, field.namedb());
				e.setAsType(field_attr_type, field.getType());
				e.setAttribute(field_attr_dbtable, field.getDBTable().name);
				e.setBoolean(field_attr_key, field.isPrimaryKey());
				e.setBoolean(field_attr_notnull, field.isKeepValid());
				if (field.getDefault() != null) {
					field.getDefault().renderInto(
							e.append(field_element_default));
				}
			}

			public final void merge(TableFieldDefineImpl field, SXElement e) {
				NamedDefineImpl.merge(field, e);
				field.setNameInDB(namedbOf(e, field.namedb()));
				field.adjustType(e.getAsType(field_attr_type, null));
				field.setPrimaryKey(e.getBoolean(field_attr_key));
				field.setKeepValid(e.getBoolean(field_attr_notnull));
				SXElement defaultElement = e.firstChild(field_element_default);
				if (defaultElement != null) {
					field.setDefault(ConstExpr.loadConst(defaultElement
							.firstChild()));
				}
			}
		};

		private final IndexXML indexXML = new IndexXML() {

			public final void render(IndexDefineImpl index, SXElement e) {
				NamedDefineImpl.render(index, e);
				e.setString(attr_namedb, index.name);
				e.setBoolean(index_attr_unique, index.isUnique());
				e.setString(index_attr_dbtable, index.dbTable.name);
				for (IndexItemImpl item : index.items) {
					this.render(item, e.append(IndexItemImpl.xml_name));
				}
			}

			private final void render(IndexItemImpl item, SXElement e) {
				DefineBaseImpl.render(item, e);
				e.setString(item_attr_field, item.field.name);
				e.setBoolean(item_attr_desc, item.desc);
			}

			public final void merge(IndexDefineImpl index, SXElement element) {
				index.setNamedb(namedbOf(element, index.namedb()));
				index.setUnique(element.getBoolean(index_attr_unique));
				for (SXElement e = element.firstChild(IndexItemImpl.xml_name); e != null; e = e
						.nextSibling(IndexItemImpl.xml_name)) {
					String fn = e.getString(item_attr_field);
					TableFieldDefineImpl field = index.owner.fields.get(fn);
					IndexItemImpl item = index.findItem(field);
					if (item == null) {
						item = new IndexItemImpl(index, field,
								e.getBoolean(item_attr_desc));
						index.items.add(item);
					} else {
						this.merge(item, e);
					}
				}
			}

			private final void merge(IndexItemImpl item, SXElement element) {
				DefineBaseImpl.merge(item, element);
				item.desc = element.getBoolean(item_attr_desc);
			}
		};

	},

	V20("2.0") {

		// changing in 2.0 was a failure.

		public final void render(TableDefineImpl define, SXElement element) {
			throw new UnsupportedOperationException();
		}

		public final void merge(TableDefineImpl table, SXElement element) {
			NamedDefineImpl.merge(table, element);
			mergeCat(table, element);
			StringKeyMap<StringKeyMap<DBColumn>> ts = new StringKeyMap<StringKeyMap<DBColumn>>();
			for (SXElement e = element.firstChild(table_element_dbtables,
					DBTableDefineImpl.xml_name); e != null; e = e
					.nextSibling(DBTableDefineImpl.xml_name)) {
				// already uppercase
				String tn = e.getString(NamedDefineImpl.xml_attr_name);
				DBTableDefineImpl dbTable = table.dbTables.find(tn);
				if (dbTable == null) {
					dbTable = new DBTableDefineImpl(table, tn);
					table.dbTables.add(dbTable);
				}
				ts.put(tn, this.merge(dbTable, e));
			}
			for (SXElement e = element.firstChild(table_element_fields,
					TableFieldDefineImpl.xml_name); e != null; e = e
					.nextSibling(TableFieldDefineImpl.xml_name)) {
				final String fn = e.getString(NamedDefineImpl.xml_attr_name);
				final String tn = e.getString(FieldXML.field_attr_dbtable);
				final DBTableDefineImpl dbTable = table.dbTables.get(tn);
				final String cn = e.getString(field_attr_column);
				TableFieldDefineImpl field = table.fields.find(fn);
				if (field == null) {
					field = TableFieldDefineImpl
							.newForMerge(table, dbTable, fn);
					table.fields.add(field);
					dbTable.store(field);
					field.setNameInDB(cn);
				} else if (field.dbTable != dbTable) {
					throw migrateField(field, field.dbTable, dbTable);
				}
				if (field.isRECID() || field.isRECVER()) {
					continue;
				}
				NamedDefineImpl.merge(field, e);
				DBColumn column = ts.get(tn).get(cn);
				field.adjustType(column.type);
				if (column.defaultValue != null) {
					field.setDefault(column.defaultValue);
				}
				field.setKeepValid(column.notNull);
			}
			final String logicalKey = element.getString(table_attr_logicalkey);
			for (SXElement e = element.firstChild(table_element_indexes,
					IndexDefineImpl.xml_name); e != null; e = e
					.nextSibling(IndexDefineImpl.xml_name)) {
				final String in = e.getString(NamedDefineImpl.xml_attr_name);
				if (notEmpty(logicalKey) && in.equals(logicalKey)) {
					if (table.logicalKey == null) {
						table.logicalKey = new IndexDefineImpl(table,
								table.primary, in, true);
					}
					this.indexXML.merge(table.logicalKey, e);
				} else {
					final String tn = e.getString(IndexXML.index_attr_dbtable);
					final DBTableDefineImpl dbTable = table.dbTables.get(tn);
					IndexDefineImpl index = table.indexes.find(in);
					if (index == null) {
						index = IndexDefineImpl.newForMerge(table, dbTable, in);
						table.indexes.add(index);
					} else if (index.dbTable != dbTable) {
						throw migrateIndex(index, index.dbTable, dbTable);
					}
					this.indexXML.merge(index, e);
				}
			}
		}

		static final String field_attr_column = "column";

		static final String table_attr_logicalkey = "logical-key";

		static final String dbtable_element_columns = "columns";
		static final String column_tagname = "column";
		static final String column_element_default = "default";
		static final String column_attr_type = "type";
		static final String column_attr_not_null = "not-null";

		private final StringKeyMap<DBColumn> merge(DBTableDefineImpl table,
				SXElement element) {
			NamedDefineImpl.merge(table, element);
			StringKeyMap<DBColumn> columns = new StringKeyMap<DBColumn>();
			for (SXElement e = element.firstChild(dbtable_element_columns,
					column_tagname); e != null; e = e
					.nextSibling(column_tagname)) {
				final String name = e.getString(NamedDefineImpl.xml_attr_name);
				final DataType type = e.getAsType(column_attr_type, null);
				DBColumn column = new DBColumn(type);
				if (e.getBoolean(column_attr_not_null)) {
					column.notNull = true;
				}
				SXElement d = e.firstChild(column_element_default);
				if (d != null) {
					column.defaultValue = ConstExpr.loadConst(d.firstChild());
				}
				columns.put(name, column);
			}
			return columns;
		}

		final class DBColumn {

			final DataType type;
			boolean notNull;
			ConstExpr defaultValue;

			DBColumn(DataType type) {
				this.type = type;
			}

		}

		private final IndexXML indexXML = new IndexXML() {

			public final void render(IndexDefineImpl define, SXElement element) {
				throw new UnsupportedOperationException();
			}

			public final void merge(IndexDefineImpl index, SXElement element) {
				NamedDefineImpl.merge(index, element);
				index.setNamedb(index.name.toUpperCase());
				index.setUnique(element.getBoolean(index_attr_unique));
				for (SXElement e = element.firstChild(IndexItemImpl.xml_name); e != null; e = e
						.nextSibling(IndexItemImpl.xml_name)) {
					String fn = e.getString(item_attr_field);
					TableFieldDefineImpl field = index.owner.fields.get(fn);
					IndexItemImpl item = index.findItem(field);
					if (item == null) {
						item = IndexItemImpl.newForMerge(index, field);
						index.items.add(item);
					}
					this.merge(item, e);
				}
			}

			private final void merge(IndexItemImpl item, SXElement element) {
				DefineBaseImpl.merge(item, element);
				item.desc = element.getBoolean(item_attr_desc, item.desc);
			}

		};

	},

	V25("2.5") {

		// simplify xml !
		public final void render(TableDefineImpl table, SXElement element) {
			NamedDefineImpl.render(table, element);
			this.renderVer(element);
			renderCat(table, element);
			SXElement dbTables = element.append(table_element_dbtables);
			for (DBTableDefineImpl dbTable : table.dbTables) {
				this.dbTableXML.render(dbTable,
						dbTables.append(DBTableDefineImpl.xml_name));
			}
			if (table.fields.size() > 2) {
				SXElement p = element.append(table_element_fields);
				for (TableFieldDefineImpl field : table.fields) {
					if (field.isRECID() || field.isRECVER()) {
						continue;
					}
					this.fieldXML.render(field,
							p.append(TableFieldDefineImpl.xml_name));
				}
			}
			SXElement indexes = null;
			if (table.indexes.size() > 0) {
				indexes = element.append(table_element_indexes);
				for (IndexDefineImpl index : table.indexes) {
					this.indexXML.render(index,
							indexes.append(IndexDefineImpl.xml_name));
				}
			}
			if (table.logicalKey != null) {
				if (indexes == null) {
					indexes = element.append(table_element_indexes);
				}
				this.indexXML.render(table.logicalKey,
						indexes.append(IndexDefineImpl.xml_name));
				element.setString(table_attr_logicalkey, table.logicalKey.name);
			}
			renderRelation(table, element);
			renderHierarchiy(table, element);
			// HCL partition
		}

		public final void merge(TableDefineImpl table, SXElement element) {
			NamedDefineImpl.merge(table, element);
			mergeCat(table, element);
			for (SXElement e = element.firstChild(table_element_dbtables,
					DBTableDefineImpl.xml_name); e != null; e = e
					.nextSibling(DBTableDefineImpl.xml_name)) {
				String tn = e.getString(NamedDefineImpl.xml_attr_name);
				DBTableDefineImpl dbTable = table.dbTables.find(tn);
				if (dbTable == null) {
					dbTable = new DBTableDefineImpl(table, tn);
					table.dbTables.add(dbTable);
				}
				this.dbTableXML.merge(dbTable, e);
			}
			for (SXElement e = element.firstChild(table_element_fields,
					TableFieldDefineImpl.xml_name); e != null; e = e
					.nextSibling(TableFieldDefineImpl.xml_name)) {
				final String tn = e.getString(FieldXML.field_attr_dbtable);
				final DBTableDefineImpl dbTable = notEmpty(tn) ? table.dbTables
						.get(tn) : table.primary;
				final String fn = e.getString(NamedDefineImpl.xml_attr_name);
				TableFieldDefineImpl field = table.fields.find(fn);
				if (field == null) {
					field = TableFieldDefineImpl
							.newForMerge(table, dbTable, fn);
					table.fields.add(field);
					dbTable.store(field);
				} else if (field.dbTable != dbTable) {
					throw migrateField(field, field.dbTable, dbTable);
				}
				this.fieldXML.merge(field, e);
			}
			final String logicalKey = element.getString(table_attr_logicalkey);
			for (SXElement e = element.firstChild(table_element_indexes,
					IndexDefineImpl.xml_name); e != null; e = e
					.nextSibling(IndexDefineImpl.xml_name)) {
				final String in = e.getString(NamedDefineImpl.xml_attr_name);
				if (notEmpty(logicalKey) && in.equals(logicalKey)) {
					if (table.logicalKey == null) {
						table.logicalKey = new IndexDefineImpl(table,
								table.primary, in, true);
					}
					this.indexXML.merge(table.logicalKey, e);
				} else {
					final String tn = e.getString(IndexXML.index_attr_dbtable);
					final DBTableDefineImpl dbTable = notEmpty(tn) ? table.dbTables
							.get(tn) : table.primary;
					IndexDefineImpl index = table.indexes.find(in);
					if (index == null) {
						index = IndexDefineImpl.newForMerge(table, dbTable, in);
						table.indexes.add(index);
					} else if (index.dbTable != dbTable) {
						throw migrateIndex(index, index.dbTable, dbTable);
					}
					this.indexXML.merge(index, e);
				}
			}
			mergeHierarchy(table, element);
			// HCL partition
		}

		private static final String table_attr_logicalkey = "logical-key";
		private static final String attr_namedb = "namedb";

		private final String namedbOf(SXElement e) {
			return e.getAttribute(attr_namedb);
		}

		private final DBTableXML dbTableXML = new DBTableXML() {

			public final void render(DBTableDefineImpl dbTable,
					SXElement element) {
				NamedDefineImpl.render(dbTable, element);
				element.setString(attr_namedb, dbTable.namedb());
				element.setString(dbtable_attr_pkname, dbTable.getPkeyName());
			}

			public final void merge(DBTableDefineImpl dbTable, SXElement element) {
				NamedDefineImpl.merge(dbTable, element);
				dbTable.setNamedb(namedbOf(element));
			}

			private static final String dbtable_attr_pkname = "pk-name";
		};

		private final FieldXML fieldXML = new FieldXML() {

			private static final String field_element_default = "default";

			public final void render(TableFieldDefineImpl field,
					SXElement element) {
				NamedDefineImpl.render(field, element);
				if (!field.dbTable.isPrimary()) {
					element.setString(field_attr_dbtable, field.dbTable.name);
				}
				element.setString(attr_namedb, field.namedb());
				element.setAsType(field_attr_type, field.getType());
				element.setTrue(field_attr_notnull, field.isKeepValid());
				element.setTrue(field_attr_templated, field.templated);
				if (field.getDefault() != null) {
					field.getDefault().renderInto(
							element.append(field_element_default));
				}
			}

			public final void merge(TableFieldDefineImpl field,
					SXElement element) {
				NamedDefineImpl.merge(field, element);
				field.setNameInDB(namedbOf(element));
				field.adjustType(element.getAsType(field_attr_type, null));
				field.setKeepValid(element.getBoolean(field_attr_notnull));
				field.setTemplated(element.getBoolean(field_attr_templated));
				SXElement defaultElement = element
						.firstChild(field_element_default);
				if (defaultElement != null) {
					field.setDefault(ConstExpr.loadConst(defaultElement
							.firstChild()));
				}
			}

			private static final String field_attr_type = "type";
			private static final String field_attr_notnull = "notnull";
			private static final String field_attr_templated = "templated";
		};

		private final IndexXML indexXML = new IndexXML() {

			public final void render(IndexDefineImpl index, SXElement element) {
				NamedDefineImpl.render(index, element);
				element.setString(attr_namedb, index.namedb());
				element.setTrue(index_attr_unique, index.isUnique());
				if (!index.dbTable.isPrimary()) {
					element.setString(index_attr_dbtable, index.dbTable.name);
				}
				for (IndexItemImpl item : index.items) {
					this.render(item, element.append(IndexItemImpl.xml_name));
				}
			}

			private final void render(IndexItemImpl item, SXElement element) {
				DefineBaseImpl.render(item, element);
				element.setString(item_attr_field, item.field.name);
				element.setTrue(item_attr_desc, item.desc);
			}

			public final void merge(IndexDefineImpl index, SXElement element) {
				NamedDefineImpl.merge(index, element);
				index.setNamedb(namedbOf(element));
				index.setUnique(element.getBoolean(index_attr_unique));
				for (SXElement e = element.firstChild(IndexItemImpl.xml_name); e != null; e = e
						.nextSibling(IndexItemImpl.xml_name)) {
					String fn = e.getString(item_attr_field);
					TableFieldDefineImpl field = index.owner.fields.get(fn);
					IndexItemImpl item = index.findItem(field);
					if (item == null) {
						item = IndexItemImpl.newForMerge(index, field);
						index.items.add(item);
					}
					this.merge(item, e);
				}
			}

			private final void merge(IndexItemImpl item, SXElement element) {
				DefineBaseImpl.merge(item, element);
				item.desc = element.getBoolean(item_attr_desc, item.desc);
			}
		};

	};

	final String ver;

	TableXML(String ver) {
		this.ver = ver;
	}

	static final TableXML detect(SXElement element) {
		String ver = verOf(element);
		if (ver.equals(V25.ver)) {
			return V25;
		} else if (ver.equals(V20.ver)) {
			return V20;
		}
		return V19;
	}

	private static final String table_attr_ver = "ver";

	private static final String verOf(SXElement element) {
		return element.getAttribute(table_attr_ver);
	}

	final void renderVer(SXElement element) {
		element.setAttribute(table_attr_ver, this.ver);
	}

	private static final String table_attr_category = "category";

	private static final void renderCat(TableDefineImpl table, SXElement element) {
		element.setAttribute(table_attr_category, table.getCategory());
	}

	private static final void mergeCat(TableDefineImpl table, SXElement element) {
		table.category = element.getAttribute(table_attr_category,
				table.category);
	}

	static final String table_element_dbtables = "dbtables";
	static final String table_element_fields = "fields";
	static final String table_element_indexes = "indexs";
	static final String table_element_relations = "relations";
	static final String table_element_hierarchies = "hierarchies";

	static final UnsupportedOperationException migrateField(
			TableFieldDefineImpl field, DBTableDefineImpl from,
			DBTableDefineImpl to) {
		// HCL
		return new UnsupportedOperationException("尝试跨物理表移动字段定义[" + field.name
				+ "],从物理表[" + from.name + "]到[" + to.name + "].");
	}

	static final UnsupportedOperationException migrateIndex(
			IndexDefineImpl index, DBTableDefineImpl from, DBTableDefineImpl to) {
		// HCL
		return new UnsupportedOperationException("尝试跨物理表移动索引定义[" + index.name
				+ "],从物理表[" + from.name + "]到[" + to.name + "].");
	}

	static abstract class DBTableXML implements DefineXML<DBTableDefineImpl> {
	}

	static abstract class FieldXML implements DefineXML<TableFieldDefineImpl> {

		static final String field_attr_dbtable = "dbtable";
	}

	static abstract class IndexXML implements DefineXML<IndexDefineImpl> {

		static final String index_attr_dbtable = "dbtable";
		static final String index_attr_unique = "unique";

		static final String item_attr_field = "field";
		static final String item_attr_desc = "desc";

	}

	static abstract class RelationXML implements
			DefineXML<TableRelationDefineImpl> {

		static final String relation_attr_type = "type";
		static final String relation_element_condition = "condition";

	}

	static final RelationXML relationXML = new RelationXML() {

		public void render(TableRelationDefineImpl relation, SXElement element) {
			NamedDefineImpl.render(relation, element);
			element.setString(TableRef.xml_attr_table, relation.target.name);
			element.setEnum(relation_attr_type, relation.type);
			relation.condition.renderInto(element
					.append(relation_element_condition));
		}

		public void merge(TableRelationDefineImpl relation, SXElement element) {
			NamedDefineImpl.merge(relation, element);
			relation.type = element.getEnum(TableRelationType.class,
					relation_attr_type, relation.type);
			relation.condition = ConditionalExpr.loadCondition(
					element.firstChild(relation_element_condition, null),
					relation.owner, null);
		}
	};

	private static final DefineXML<HierarchyDefineImpl> hierarchyXML = new DefineXML<HierarchyDefineImpl>() {

		static final String hierarchy_attr_maxlevel = "maxlevel";
		static final String hierarchy_attr_table = "table";
		static final String hierarchy_attr_pkindex = "pk-index";
		static final String hierarchy_attr_pathindex = "path-index";

		public void render(HierarchyDefineImpl hierarchy, SXElement element) {
			NamedDefineImpl.render(hierarchy, element);
			element.setInt(hierarchy_attr_maxlevel, hierarchy.maxlevel);
			element.setString(hierarchy_attr_table, hierarchy.tableName);
			element.setString(hierarchy_attr_pkindex, hierarchy.pkIndex);
			element.setString(hierarchy_attr_pathindex, hierarchy.pathIndex);
		}

		public void merge(HierarchyDefineImpl hierarchy, SXElement element) {
			NamedDefineImpl.merge(hierarchy, element);
			hierarchy.maxlevel = element.getInt(hierarchy_attr_maxlevel,
					hierarchy.maxlevel);
			hierarchy.tableName = element.getAttribute(hierarchy_attr_table,
					hierarchy.tableName);
			hierarchy.pkIndex = element.getAttribute(hierarchy_attr_pkindex,
					hierarchy.pkIndex);
			hierarchy.pathIndex = element.getAttribute(
					hierarchy_attr_pathindex, hierarchy.pathIndex);
		}
	};

	static final void renderRelation(TableDefineImpl table, SXElement element) {
		if (table.relations.size() > 0) {
			SXElement p = element.append(table_element_relations);
			for (TableRelationDefineImpl relation : table.relations) {
				relationXML.render(relation,
						p.append(TableRelationDefineImpl.xml_name));
			}
		}
	}

	static final void renderHierarchiy(TableDefineImpl table, SXElement element) {
		if (table.hierarchies.size() > 0) {
			SXElement p = element.append(table_element_hierarchies);
			for (HierarchyDefineImpl hierarchy : table.hierarchies) {
				hierarchyXML.render(hierarchy,
						p.append(HierarchyDefineImpl.xml_name));
			}
		}
	}

	static final void mergeHierarchy(TableDefineImpl table, SXElement element) {
		for (SXElement e = element.firstChild(table_element_hierarchies,
				HierarchyDefineImpl.xml_name); e != null; e = e
				.nextSibling(HierarchyDefineImpl.xml_name)) {
			String hn = e.getString(NamedDefineImpl.xml_attr_name);
			HierarchyDefineImpl hierarchy = table.hierarchies.find(hn);
			if (hierarchy == null) {
				hierarchy = HierarchyDefineImpl.newForMerge(table, hn);
				table.hierarchies.add(hierarchy);
			}
			hierarchyXML.merge(hierarchy, e);
		}
	}

	private static boolean notEmpty(String s) {
		return s != null && s.length() != 0;
	}
}
