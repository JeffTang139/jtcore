package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.query.GroupByType;
import org.eclipse.jt.core.spi.sql.SQLSyntaxException;

/**
 * GROUP BY节点
 * 
 * @author Jeff Tang
 * 
 */
class NGroupBy implements TextLocalizable {
	private final int startLine;
	private final int startCol;
	private final int endLine;
	private final int endCol;

	public final NValueExpr[] columns;
	public final GroupByType option;

	public NGroupBy(Token start, Token end, NValueExpr[] columns,
			GroupByType option) {
		this.columns = columns;
		this.option = option;
		this.startLine = start.line;
		this.startCol = start.col;
		if (columns == null || columns.length == 0) {
			throw new SQLSyntaxException(start.line, start.col, "缺少值表达式");
		}
		if (end != null) {
			this.endLine = end.line;
			this.endCol = end.col;
		} else {
			NValueExpr c = columns[columns.length - 1];
			this.endLine = c.endLine();
			this.endCol = c.endCol();
		}
	}

	public int startLine() {
		return this.startLine;
	}

	public int startCol() {
		return this.startCol;
	}

	public int endLine() {
		return this.endLine;
	}

	public int endCol() {
		return this.endCol;
	}
}
