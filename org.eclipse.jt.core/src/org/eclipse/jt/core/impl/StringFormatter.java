package org.eclipse.jt.core.impl;

/**
 * 使用StringBuilder的格式化器
 * 
 * @author Jeff Tang
 * 
 */
class StringFormatter {
	public final static char EMPTY_DELIMITER = 0;

	private final StringBuilder sb = new StringBuilder();
	/**
	 * 单位缩进长度
	 */
	private final int indentSize;
	/**
	 * 记录缩进位置的栈
	 */
	private int[] indentStack = new int[8];
	/**
	 * 当前栈顶索引号
	 */
	private int stackIndex = -1;
	/**
	 * 当前行的其实位置
	 */
	private int lineStart;
	/**
	 * 当前行的缩进位置
	 */
	private int curIndent;

	private final SQLVisitor<StringFormatter> visitor;

	public StringFormatter(SQLVisitor<StringFormatter> visitor, int indentSize) {
		this.indentSize = indentSize;
		this.visitor = visitor;
	}

	public StringFormatter(SQLVisitor<StringFormatter> visitor) {
		this(visitor, 4);
	}

	public StringFormatter() {
		this(RenderVisitor.VISITOR);
	}

	private void ensureIndentStack(int max) {
		if (this.indentStack.length < max) {
			int i = this.indentStack.length + 8;
			if (i < max) {
				i = max;
			}
			int[] arr = new int[i];
			System.arraycopy(this.indentStack, 0, arr, 0, this.stackIndex);
			this.indentStack = arr;
		}
	}

	private void ensureIndent() {
		if (this.curIndent > 0 && this.sb.length() == this.lineStart) {
			int start = this.sb.length();
			int end = start + this.curIndent;
			this.sb.setLength(end);
			for (int i = start; i < end; i++) {
				this.sb.setCharAt(i, ' ');
			}
		}
	}

	public StringFormatter append(String s) {
		this.ensureIndent();
		this.sb.append(s);
		return this;
	}

	public StringFormatter append(char c) {
		this.ensureIndent();
		this.sb.append(c);
		return this;
	}

	public StringFormatter append(int i) {
		this.ensureIndent();
		this.sb.append(i);
		return this;
	}

	public StringFormatter append(long l) {
		this.ensureIndent();
		this.sb.append(l);
		return this;
	}

	public StringFormatter append(float f) {
		this.ensureIndent();
		this.sb.append(f);
		return this;
	}

	public StringFormatter append(double d) {
		this.ensureIndent();
		this.sb.append(d);
		return this;
	}

	public StringFormatter append(boolean b) {
		this.ensureIndent();
		this.sb.append(b);
		return this;
	}

	public StringFormatter append(SQLVisitable r) {
		if (r != null) {
			r.accept(this, this.visitor);
		}
		return this;
	}

	public StringFormatter indent() {
		this.curIndent += this.indentSize;
		this.ensureIndentStack(++this.stackIndex + 1);
		this.indentStack[this.stackIndex] = this.curIndent;
		return this;
	}

	public StringFormatter mark() {
		int indent = this.sb.length() - this.lineStart;
		if (indent == 0) {
			indent = this.curIndent;
		}
		this.ensureIndentStack(++this.stackIndex + 1);
		this.indentStack[this.stackIndex] = indent;
		return this;
	}

	public StringFormatter back() {
		this.stackIndex--;
		this.curIndent = this.stackIndex < 0 ? 0
				: this.indentStack[this.stackIndex];
		return this;
	}

	public StringFormatter multiline(SQLVisitable[] list, char delimiter) {
		if (list != null && list.length > 0) {
			this.mark();
			list[0].accept(this, this.visitor);
			for (int i = 1, c = list.length; i < c; i++) {
				if (delimiter != StringFormatter.EMPTY_DELIMITER) {
					this.append(delimiter);
				}
				this.newline();
				list[i].accept(this, this.visitor);
			}
			this.back();
		}
		return this;
	}

	public StringFormatter newline() {
		this.sb.append('\n');
		this.lineStart = this.sb.length();
		this.curIndent = this.stackIndex < 0 ? 0
				: this.indentStack[this.stackIndex];
		return this;
	}

	public StringFormatter singleline(SQLVisitable[] list, char delimiter) {
		if (list != null && list.length > 0) {
			list[0].accept(this, this.visitor);
			for (int i = 1, c = list.length; i < c; i++) {
				if (delimiter != StringFormatter.EMPTY_DELIMITER) {
					this.append(delimiter).space();
				}
				list[i].accept(this, this.visitor);
			}
		}
		return this;
	}

	public StringFormatter space() {
		this.ensureIndent();
		this.sb.append(' ');
		return this;
	}

	public String encodeString(String s) {
		return "'" + s.replace("\'", "''") + "'";
	}

	public String encodeID(String s) {
		return "\"" + s.replace("\"", "\"\"") + "\"";
	}

	@Override
	public String toString() {
		this.sb.trimToSize();
		return this.sb.toString();
	}
}
