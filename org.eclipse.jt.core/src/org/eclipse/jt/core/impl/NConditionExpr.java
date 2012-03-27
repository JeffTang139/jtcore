package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.spi.sql.SQLSyntaxException;

/**
 * 条件表达式节点
 * 
 * @author Jeff Tang
 * 
 */
interface NConditionExpr extends TextLocalizable, SQLVisitable {
	public static final NConditionExpr EMPTY = new NConditionExpr() {
		public int startLine() {
			return 0;
		}

		public int startCol() {
			return 0;
		}

		public int endLine() {
			return 0;
		}

		public int endCol() {
			return 0;
		}

		public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
			throw new SQLSyntaxException();
		}
	};
}
