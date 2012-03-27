package org.eclipse.jt.core.impl;


/**
 * 字段节点
 * 
 * @author Jeff Tang
 * 
 */
class NTableField implements TextLocalizable {
	public static final NTableField EMPTY = new NTableField(TString.EMPTY,
			NDataType.UNKNOWN, false, null, false);

	public final TString name;
	public final NDataType type;
	public final boolean notNull;
	public final NLiteral defaultValue;
	public final boolean primaryKey;
	public final NTableForeignKey foreignKey;
	boolean override;

	private NTableField(TString name, NDataType type, boolean notNull,
			NLiteral defaultValue, boolean primaryKey,
			NTableForeignKey foreignKey) {
		this.name = name;
		this.type = type;
		this.notNull = notNull;
		this.defaultValue = defaultValue;
		this.primaryKey = primaryKey;
		this.foreignKey = foreignKey;
	}

	public NTableField(TString name, NDataType type, boolean notNull,
			NLiteral defaultValue, boolean primaryKey) {
		this(name, type, notNull, defaultValue, primaryKey, null);
	}

	public NTableField(TString name, NDataType type, boolean notNull,
			NLiteral defaultValue, NTableForeignKey foreignKey) {
		this(name, type, notNull, defaultValue, false, foreignKey);
	}

	public int startLine() {
		return this.name.startLine();
	}

	public int startCol() {
		return this.name.startCol();
	}

	public int endLine() {
		return this.name.endLine();
	}

	public int endCol() {
		return this.name.endCol();
	}

	final NTableField merge(NTableField field) {
		// try {
		// NDataType type = this.type.merge(field.type);
		// NLiteral def = this.defaultValue;
		// if (field.defaultValue != null) {
		// def = field.defaultValue;
		// }
		// if (field.notNull) {
		// throw new SQLNotSupportedException("不支持重写NOT NULL");
		// }
		// if (field.primaryKey) {
		// throw new SQLNotSupportedException("不支持重写PRIMARY KEY");
		// }
		// if (field.foreignKey != null) {
		// throw new SQLNotSupportedException("不支持重写关系");
		// }
		// return new NTableField(this.name, type, this.notNull, def,
		// this.primaryKey, this.foreignKey);
		// } catch (SQLNotSupportedException ex) {
		// ex.line = this.startLine();
		// ex.col = this.startCol();
		// throw Utils.tryThrowException(ex);
		// }
		return field;
	}
}
