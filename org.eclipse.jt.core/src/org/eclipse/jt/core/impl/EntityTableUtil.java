package org.eclipse.jt.core.impl;

import java.util.ArrayList;

import org.eclipse.jt.core.def.table.AsTable;
import org.eclipse.jt.core.def.table.AsTableField;
import org.eclipse.jt.core.def.table.AsTableField.DBType;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.EnumType;
import org.eclipse.jt.core.type.SequenceDataType;
import org.eclipse.jt.core.type.TypeDetectorBase;
import org.eclipse.jt.core.type.TypeFactory;


/**
 * 实体表工具类
 * 
 * @author Jeff Tang
 * 
 */
public final class EntityTableUtil {

	private EntityTableUtil() {
		// nothing
	}

	public static void buildTableAndOrm(TableDefineImpl table,
			MappingQueryStatementImpl orm) {
		// 设置表属性
		AsTable asTable = orm.mapping.soClass.getAnnotation(AsTable.class);
		if (asTable != null) {
			table.setTitle(asTable.title());
			table.setDescription(asTable.description());
			String namedb = asTable.dbName();
			if (namedb != null && namedb.length() > 0) {
				table.primary.setNamedb(namedb);
			}
		}
		QuRelationRef tableRef = orm.newTableRef("T", table);
		// 主键
		StructFieldDefineImpl recidField = null;
		// 行版本
		StructFieldDefineImpl recverField = null;
		// 逻辑主键字段
		ArrayList<StructFieldDefineImpl> pkFields = new ArrayList<StructFieldDefineImpl>(
				4);
		for (StructFieldDefineImpl sf : orm.mapping.fields) {
			if (sf.asTableField != null && sf.asTableField.pkOrdinal() >= 0) {
				// 逻辑主键按ordinal排序
				insert: {
					int ordinal = sf.asTableField.pkOrdinal();
					for (int i = 0, c = pkFields.size(); i < c; i++) {
						if (ordinal < pkFields.get(i).asTableField.pkOrdinal()) {
							pkFields.add(i, sf);
							break insert;
						}
					}
					pkFields.add(sf);
				}
			}
			// recid
			if (sf.asTableField != null && sf.asTableField.isRecid()) {
				if (recidField != null) {
					throw new IllegalArgumentException("重复定义recid字段");
				} else if (sf.type != GUIDType.TYPE) {
					throw new IllegalArgumentException("recid字段类型不为GUID");
				}
				recidField = sf;
			}
			// recver
			if (sf.asTableField != null && sf.asTableField.isRecver()) {
				if (recverField != null) {
					throw new IllegalArgumentException("重复定义recver字段");
				} else if (sf.type != LongType.TYPE) {
					throw new IllegalArgumentException("recver字段类型不是为LONG");
				}
				recverField = sf;
			}
		}
		if (recidField != null) {
			orm.newColumn(recidField.name, tableRef.expOf(table.f_recid),
					recidField);
		}
		if (recverField != null) {
			orm.newColumn(recverField.name, tableRef.expOf(table.f_recver),
					recverField);
		}
		// 创建逻辑主键字段
		for (StructFieldDefineImpl sf : pkFields) {
			// 逻辑主键必在主表上,不会检查atf的dbTable字段
			String assignName = sf.asTableField.name();
			TableFieldDefineImpl tf = table
					.newPrimaryField(
							(assignName != null) && (assignName.length() > 0) ? assignName
									: sf.name,
							sf.type.getRootType().detect(parser,
									sf.asTableField));
			setFieldAttr(tf, sf.asTableField);
			orm.newColumn(sf.name, tableRef.expOf(tf), sf);
		}
		// 非逻辑主键字段
		for (StructFieldDefineImpl sf : orm.mapping.fields) {
			// 标记AsTable的所有字段或者标记了AsTableField则判定为表的字段
			if ((asTable != null || sf.asTableField != null)
					&& !pkFields.contains(sf) && sf != recidField
					&& sf != recverField) {
				String fn;
				if (sf.asTableField != null) {
					fn = sf.asTableField.name();
					if (fn == null || fn.length() == 0) {
						fn = sf.name;
					}
				} else {
					fn = sf.name;
				}
				TableFieldDefineImpl tf = table.newField(fn, sf.type
						.getRootType().detect(parser, sf.asTableField));
				setFieldAttr(tf, sf.asTableField);
				orm.newColumn(sf.name, tableRef.expOf(tf), sf);
			}
		}
	}

	/**
	 * 根据表字段的annotation设置其属性
	 * 
	 * @param tf
	 *            表字段定义
	 * @param atf
	 *            实体字段的注释
	 */
	private static final void setFieldAttr(TableFieldDefineImpl tf,
			AsTableField atf) {
		if (atf == null) {
			return;
		}
		tf.setTitle(atf.title());
		tf.setDescription(atf.description());
		String namedb = atf.nameInDB();
		if (namedb != null && namedb.length() > 0) {
			tf.setNameInDB(namedb);
		}
		tf.setKeepValid(atf.isRequired());
	}

	/**
	 * @param origType
	 *            实体字段的类型,对返回数据库类型起主要作用
	 * @param atf
	 *            实体字段的注释,可以明确指定部分实体字段的数据库类型(如String类型),为空时根据实体字段的类型,返回默认的数据库类型
	 * @return
	 */
	@SuppressWarnings("unused")
	private static final DataType getDBType(DataType origType, AsTableField atf) {
		origType = origType.getRootType();
		if (origType == StringType.TYPE) {
			if (atf == null) {
				return TypeFactory.VARCHAR32;
			} else if (atf.dbType() == DBType.Default) {
				return TypeFactory.VARCHAR(atf.length());
			} else {
				switch (atf.dbType()) {
				case Char:
					return TypeFactory.CHAR(atf.length());
				case Varchar:
					return TypeFactory.VARCHAR(atf.length());
				case Text:
					return TypeFactory.TEXT;
				case NChar:
					return TypeFactory.NCHAR(atf.length());
				case NVarchar:
					return TypeFactory.NVARCHAR(atf.length());
				case NText:
					return TypeFactory.NTEXT;
				default:
					return TypeFactory.VARCHAR32;
				}
			}
		} else if (origType == IntType.TYPE) {
			return TypeFactory.INT;
		} else if (origType == GUIDType.TYPE) {
			return TypeFactory.GUID;
		} else if (origType == BytesType.TYPE) {
			if (atf == null) {
				return TypeFactory.VARBINARY32;
			} else if (atf.dbType() == DBType.Default) {
				return TypeFactory.VARBINARY(atf.length());
			} else {
				switch (atf.dbType()) {
				case Binary:
					return TypeFactory.BINARY(atf.length());
				case Varbinary:
					return TypeFactory.VARBINARY(atf.length());
				case Blob:
					return TypeFactory.BLOB;
				default:
					return TypeFactory.VARBINARY32;
				}
			}
		} else if (origType == BooleanType.TYPE) {
			return TypeFactory.BOOLEAN;
		} else if (origType == ShortType.TYPE) {
			return TypeFactory.SHORT;
		} else if (origType == LongType.TYPE) {
			if ((atf == null) || (atf.dbType() != DBType.Date)) {
				return TypeFactory.LONG;
			} else {
				return TypeFactory.DATE;
			}
		} else if (origType == FloatType.TYPE) {
			if ((atf == null) || (atf.dbType() != DBType.Numeric)) {
				return TypeFactory.FLOAT;
			} else {
				return TypeFactory.NUMERIC(atf.precision(), atf.scale());
			}
		} else if (origType == DoubleType.TYPE) {
			if ((atf == null) || (atf.dbType() != DBType.Numeric)) {
				return TypeFactory.DOUBLE;
			} else {
				return TypeFactory.NUMERIC(atf.precision(), atf.scale());
			}
		} else if (origType == DateType.TYPE) {
			return TypeFactory.DATE;
		} else if (origType instanceof EnumTypeImpl<?>) {
			switch (atf.dbType()) {
			case Char:
				return TypeFactory.CHAR(atf.length());
			case Varchar:
				return TypeFactory.VARCHAR(atf.length());
			case NChar:
				return TypeFactory.NCHAR(atf.length());
			case NVarchar:
				return TypeFactory.NVARCHAR(atf.length());
			default:
				return TypeFactory.INT;
			}
		} else if (origType == RefDataType.dateRefType) {
			return DateType.TYPE;
		} else {
			throw new IllegalArgumentException("无法支持的类型");
		}
	}

	private static final TypeDetectorBase<DataType, AsTableField> parser = new TypeDetectorBase<DataType, AsTableField>() {

		@Override
		public DataType inBoolean(AsTableField atf) throws Throwable {
			return TypeFactory.BOOLEAN;
		}

		@Override
		public DataType inBytes(AsTableField atf, SequenceDataType type)
				throws Throwable {
			if (atf == null) {
				return TypeFactory.VARBINARY32;
			} else {
				switch (atf.dbType()) {
				case Default:
					return TypeFactory.VARBINARY(atf.length());
				case Binary:
					return TypeFactory.BINARY(atf.length());
				case Varbinary:
					return TypeFactory.VARBINARY(atf.length());
				case Blob:
					return TypeFactory.BLOB;
				default:
					return TypeFactory.VARBINARY(atf.length());
				}
			}
		}

		@Override
		public DataType inDate(AsTableField atf) throws Throwable {
			return TypeFactory.DATE;
		}

		@Override
		public DataType inDouble(AsTableField atf) throws Throwable {
			if (atf == null || atf.dbType() != DBType.Numeric) {
				return TypeFactory.DOUBLE;
			} else {
				return TypeFactory.NUMERIC(atf.precision(), atf.scale());
			}
		}

		@Override
		public DataType inEnum(AsTableField atf, EnumType<?> type)
				throws Throwable {
			if (atf == null) {
				return TypeFactory.INT;
			}
			switch (atf.dbType()) {
			case Char:
				return TypeFactory.CHAR(atf.length());
			case Varchar:
				return TypeFactory.VARCHAR(atf.length());
			case NChar:
				return TypeFactory.NCHAR(atf.length());
			case NVarchar:
				return TypeFactory.NVARCHAR(atf.length());
			default:
				return TypeFactory.INT;
			}
		}

		@Override
		public DataType inFloat(AsTableField atf) throws Throwable {
			if (atf == null || atf.dbType() != DBType.Numeric) {
				return TypeFactory.FLOAT;
			} else {
				return TypeFactory.NUMERIC(atf.precision(), atf.scale());
			}
		}

		@Override
		public DataType inGUID(AsTableField atf) throws Throwable {
			return TypeFactory.GUID;
		}

		@Override
		public DataType inInt(AsTableField atf) throws Throwable {
			return TypeFactory.INT;
		}

		@Override
		public DataType inLong(AsTableField atf) throws Throwable {
			if (atf == null || atf.dbType() != DBType.Date) {
				return TypeFactory.LONG;
			} else {
				return TypeFactory.DATE;
			}
		}

		@Override
		public DataType inShort(AsTableField atf) throws Throwable {
			return TypeFactory.SHORT;
		}

		@Override
		public DataType inString(AsTableField atf, SequenceDataType type)
				throws Throwable {
			if (atf == null) {
				return TypeFactory.VARCHAR32;
			} else {
				switch (atf.dbType()) {
				case Default:
					return TypeFactory.VARCHAR(atf.length());
				case Char:
					return TypeFactory.CHAR(atf.length());
				case Varchar:
					return TypeFactory.VARCHAR(atf.length());
				case Text:
					return TypeFactory.TEXT;
				case NChar:
					return TypeFactory.NCHAR(atf.length());
				case NVarchar:
					return TypeFactory.NVARCHAR(atf.length());
				case NText:
					return TypeFactory.NTEXT;
				default:
					return TypeFactory.VARCHAR(atf.length());
				}
			}
		}

	};
}
