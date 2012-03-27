package org.eclipse.jt.core.impl;


class NTablePrimary implements TextLocalizable {
	public static final NTablePrimary EMPTY = new NTablePrimary(Token.EMPTY,
			new NTableField[] { NTableField.EMPTY });

	private final int startLine;
	private final int startCol;
	private final int endLine;
	private final int endCol;

	public final NTableField[] fields;

	public NTablePrimary(Token start, NTableField[] fields) {
		this(start.line, start.col, fields);
	}

	private NTablePrimary(int startLine, int startCol, NTableField[] fields) {
		this.fields = fields;
		this.startLine = startLine;
		this.startCol = startCol;
		NTableField f = fields[fields.length - 1];
		this.endLine = f.endLine();
		this.endCol = f.endCol();
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

	final NTablePrimary merge(NTablePrimary p) {
		int i = this.fields.length;
		NTableField[] arr = new NTableField[i + p.fields.length];
		System.arraycopy(this.fields, 0, arr, 0, i);
		for (NTableField f : p.fields) {
			// if (f.override) {
			String name = f.name.value;
			int j = 0;
			for (; j < i; j++) {
				if (name.equals(arr[j].name.value)) {
					arr[j] = arr[j].merge(f);
					break;
				}
			}
			if (j == i) {
				arr[i++] = f;
				// throw new SQLSyntaxException(f.startLine(), f.startCol(),
				// "找不到要重写的字段 '" + f.name.value + "'");
			}
			// } else {
			// arr[i++] = f;
			// }
		}
		if (i < arr.length) {
			NTableField[] arr2 = new NTableField[i];
			System.arraycopy(arr, 0, arr2, 0, i);
			arr = arr2;
		}
		return new NTablePrimary(this.startLine, this.startCol, arr);
	}
}
