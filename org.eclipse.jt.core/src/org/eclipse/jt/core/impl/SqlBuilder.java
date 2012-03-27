package org.eclipse.jt.core.impl;

import java.io.IOException;

import org.eclipse.jt.core.type.DataType;


/**
 * Sql语句生成器
 * 
 * @author Jeff Tang
 * 
 */
public final class SqlBuilder implements CharSequence, Appendable {

	public static final int NEED_COMMA = 1 << 0;

	public static final int NEED_PERIOD = 1 << 1;

	public static final int NEED_SEMICOLON = 1 << 2;

	private static final int NEED_PUNCTUATON = NEED_COMMA | NEED_PERIOD
			| NEED_SEMICOLON;

	/**
	 * 需要空格
	 */
	public static final int NEED_SPACE = 1 << 8;

	/**
	 * 需要换行
	 */
	public static final int NEED_NEWLINE = 1 << 9;

	private static final int BOF = 0x80000000;

	/**
	 * 在下次追加字符前追加逗号
	 * 
	 * <p>
	 * 与nSemicolon,nPeriod相排斥,即会取消掉对semicolon与period的需求.
	 * 对space或newline的需求在comma之后实现.
	 */
	public final SqlBuilder nComma() {
		this.need = this.need & ~NEED_PUNCTUATON | NEED_COMMA;
		return this;
	}

	/**
	 * 取消在下次追加字符前的逗号
	 */
	public final SqlBuilder uComma() {
		this.need &= ~NEED_COMMA;
		return this;
	}

	/**
	 * 在下次追加字符前追加句点
	 * 
	 * <p>
	 * 与nComma,nSemicolon,即会取消掉对comma与semicolon的需求.
	 * 对space或newline的需求在period之后实现.
	 */
	public final SqlBuilder nPeriod() {
		this.need = this.need & ~NEED_PUNCTUATON | NEED_PERIOD;
		return this;
	}

	/**
	 * 取消在下次追加字符前的句点
	 */
	public final SqlBuilder uPeriod() {
		this.need &= ~NEED_PERIOD;
		return this;
	}

	/**
	 * 在下次追加字符前追加分号
	 * 
	 * <p>
	 * 与nComma,nPeriod相排斥,即会取消掉对comma与period的需求.
	 * 对space或newline的需求在semicolon之后实现.
	 */
	public final SqlBuilder nSemicolon() {
		this.need = this.need & ~NEED_PUNCTUATON | NEED_SEMICOLON;
		return this;
	}

	/**
	 * 取消在下次追加字符前的分号
	 */
	public final SqlBuilder uSemicolon() {
		this.need &= ~NEED_SEMICOLON;
		return this;
	}

	/**
	 * 在下次追加字符前追加空格
	 * 
	 * <p>
	 * 会被nNewline取消需求,再次需求后,会在换行符之后追加空白.
	 */
	public final SqlBuilder nSpace() {
		this.need |= NEED_SPACE;
		return this;
	}

	/**
	 * 取消在下次追加字符前的空格
	 */
	public final SqlBuilder uSpace() {
		this.need &= ~NEED_SPACE;
		return this;
	}

	/**
	 * 在下次追加字符前追加换行符
	 * 
	 * <p>
	 * 会取消对space的需求.再次nSpace,会在newline符号之后追加space.
	 */
	public final SqlBuilder nNewline() {
		this.need = this.need & ~NEED_SPACE | NEED_NEWLINE;
		return this;
	}

	/**
	 * 取消在下次追加字符前的换行符
	 */
	public final SqlBuilder uNewline() {
		this.need &= ~NEED_NEWLINE;
		return this;
	}

	/**
	 * 实现需求
	 */
	private final void resovleNeeds() {
		if (this.need != 0) {
			if ((this.need & NEED_COMMA) != 0) {
				this.str.append(',');
			} else if ((this.need & NEED_PERIOD) != 0) {
				this.str.append('.');
			} else if ((this.need & NEED_SEMICOLON) != 0) {
				this.str.append(';');
			}
			if ((this.need & SqlBuilder.BOF) == 0) {
				// HCL 新行BOF
				if ((this.need & NEED_NEWLINE) != 0) {
					this.str.append("\n");
					for (int i = this.indent; i > 0; i--) {
						this.str.append('\t');
					}
				} else if ((this.need & NEED_SPACE) != 0) {
					this.str.append(' ');
				}
			}
			this.need = 0;
		}
	}

	/**
	 * 增加缩进(plusIndent)
	 */
	public final SqlBuilder pi() {
		this.indent++;
		return this;
	}

	/**
	 * 减少缩进(reduceIndent)
	 */
	public final SqlBuilder ri() {
		this.indent--;
		return this;
	}

	/**
	 * 追加左圆括号(leftParenthesis)
	 */
	public final SqlBuilder lp() {
		this.append('(');
		return this;
	}

	/**
	 * 追加右圆括号(rightParenthesis)
	 */
	public final SqlBuilder rp() {
		this.append(')');
		this.need |= NEED_SPACE;
		return this;
	}

	public final char charAt(int index) {
		return this.str.charAt(index);
	}

	public final int length() {
		return this.str.length();
	}

	public final CharSequence subSequence(int start, int end) {
		throw new UnsupportedOperationException();
	}

	public final Appendable append(CharSequence csq, int start, int end)
			throws IOException {
		throw new UnsupportedOperationException();
	}

	public final SqlBuilder append(CharSequence csq) throws IOException {
		this.resovleNeeds();
		this.str.append(csq);
		return this;
	}

	public final SqlBuilder append(char sql) {
		this.resovleNeeds();
		this.str.append(sql);
		return this;
	}

	public final SqlBuilder append(String sql) {
		if (sql != null && sql.length() > 0) {
			this.resovleNeeds();
			this.str.append(sql);
		}
		return this;
	}

	public final SqlBuilder append(int i) {
		this.resovleNeeds();
		this.str.append(i);
		return this;
	}

	@Override
	public final String toString() {
		return this.str.toString();
	}

	public final String toSql() {
		this.resovleNeeds();
		return this.str.toString();
	}

	public final SqlBuilder appendType(DataType type) {
		this.resovleNeeds();
		this.lang.format(this, type);
		return this;
	}

	final SqlBuilder appendEq() {
		this.need |= NEED_SPACE;
		this.append('=');
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendNEq() {
		this.need |= NEED_SPACE;
		this.append('<').append('>');
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendGt() {
		this.need |= NEED_SPACE;
		this.append('>');
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendGE() {
		this.need |= NEED_SPACE;
		this.append('>').append('=');
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendLt() {
		this.need |= NEED_SPACE;
		this.append('<');
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendLE() {
		this.need |= NEED_SPACE;
		this.append('<').append('=');
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendAdd() {
		this.need |= NEED_SPACE;
		this.append('a').append('d').append('d');
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendAlter() {
		this.need |= NEED_SPACE;
		this.append("alter");
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendAnd() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append('a').append('n').append('d');
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendAs() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append('a').append('s');
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendAsterisk() {
		this.resovleNeeds();
		this.str.append('*');
		return this;
	}

	final SqlBuilder appendBegin() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append("begin");
		this.need |= NEED_NEWLINE;
		this.indent++;
		return this;
	}

	final SqlBuilder appendBetween() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append("between");
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendCase() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append("case");
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendByRange() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append("by range");
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendByHash() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append("by hash");
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendColumn() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append("column");
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendConstraint() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append("constraint");
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendCreate() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append("create");
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendDeclare() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append("declare");
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendDefault() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append("default");
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendDelete() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append("delete");
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendDesc() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append("desc");
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendDistinct() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append("distinct");
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendDrop() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append("drop");
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendEscape() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append("escape");
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendElse() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append("else");
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendExists() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append("exists");
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendFor() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append('f').append('o').append('r');
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendFrom() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append("from");
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendFullJoin() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append("full outer join");
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendEnd() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append('e').append('n').append('d');
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendGroupby() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append("group by");
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendHaving() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append("having");
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendIf() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append('i').append('f');
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendIn() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append('i').append('n');
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendInnerJoin() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append("inner join");
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendIndex() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append("index");
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendInsert() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append("insert");
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendInto() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append('i').append('n').append('t').append('o');
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendIs() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append('i').append('s');
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendLike() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append("like");
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendLeftJoin() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append("left outer join");
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendLessThan() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append("less than");
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendLoop() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append("loop");
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendModify() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append("modify");
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendNot() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append('n').append('o').append('t');
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendNull() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append("null");
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendOn() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append('o').append('n');
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendOr() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append('o').append('r');
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendRightJoin() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append("right outer join");
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendSet() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append('s').append('e').append('t');
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendTable() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append("table");
		this.need |= NEED_SPACE;
		return this;
	}

	/**
	 * 追加then符号
	 */
	final SqlBuilder appendThen() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append("then");
		this.need |= NEED_SPACE;
		return this;
	}

	/**
	 * 追加orderby符号
	 */
	final SqlBuilder appendOrderby() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append("order by");
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendPartition() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append("partition");
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendPartitions() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append("partitions");
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendPrimaryKey() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append("primary key");
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendSelect() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append("select");
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendValues() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append("values");
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendWhen() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append("when");
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendWhere() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append("where");
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendWith() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append("with");
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendUnique() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append("unique");
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendUpdate() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append("update");
		this.need |= NEED_SPACE;
		return this;
	}

	final SqlBuilder appendUsing() {
		this.need |= NEED_SPACE;
		this.resovleNeeds();
		this.str.append("using");
		this.need |= NEED_SPACE;
		return this;
	}

	final DBLang lang;

	private final StringBuilder str = new StringBuilder();
	private int indent;
	private int need;

	SqlBuilder(DBLang lang) {
		if (lang == null) {
			throw new NullPointerException();
		}
		this.lang = lang;
		this.need = BOF;
	}

	final SqlBuilder appendId(String name) {
		this.lang.formatId(this, name);
		return this;
	}

	final SqlBuilder appendId(HierarchyDefineImpl hierarchy) {
		return this.appendId(hierarchy.tableName());
	}

	final SqlBuilder appendId(TableFieldDefineImpl field) {
		return this.appendId(field.namedb());
	}

	final SqlBuilder appendRef(TableRef tableRef, HierarchyDefineImpl hierarchy) {
		return this.appendId(tableRef.getName() + "_h" + hierarchy.index());
	}

}