package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.spi.sql.SQLSyntaxException;

/**
 * SQLÓï¾ä
 * 
 * @author Jeff Tang
 * 
 */
interface NStatement extends TextLocalizable, SQLVisitable {
	public static final NStatement EMPTY = new NStatement() {
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
