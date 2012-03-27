package org.eclipse.jt.core.impl;

import java.lang.reflect.Array;

class NAbstractTableDeclare implements NStatement {
	private final int startLine;
	private final int startCol;
	private final int endLine;
	private final int endCol;

	public final TString name;
	public final NAbstractTableDeclare base;
	public final NTablePrimary primary;
	public final NTableExtend[] extend;
	public final NTableIndex[] index;
	public final NTableRelation[] relation;
	public final NTableHierarchy[] hierarchy;
	public final NTablePartition partition;

	public NAbstractTableDeclare(Token start, Token end, TString name,
			NAbstractTableDeclare base, NTablePrimary primary,
			NTableExtend[] extend, NTableIndex[] index,
			NTableRelation[] relation, NTableHierarchy[] hierarchy,
			NTablePartition partition) {
		this.name = name;
		this.base = base;
		this.primary = primary;
		this.extend = extend;
		this.index = index;
		this.relation = relation;
		this.hierarchy = hierarchy;
		this.partition = partition;
		this.startLine = start.line;
		this.startCol = start.col;
		this.endLine = end.line;
		this.endCol = end.endCol();
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

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitAbstractTableDeclare(visitorContext, this);
	}

	protected NTablePrimary getMergedPrimary() {
		if (this.base != null) {
			return this.base.getMergedPrimary().merge(this.primary);
		}
		return this.primary;
	}

	protected NTableExtend[] getMergedExtend() {
		if (this.base != null) {
			return mergeArray(this.base.extend, this.extend);
		}
		return this.extend;
	}

	protected NTableIndex[] getMergedIndex() {
		if (this.base != null) {
			NTableIndex[] baseIndex = this.base.getMergedIndex();
			if (this.index == null) {
				return baseIndex;
			}
			int i = baseIndex.length;
			NTableIndex[] arr = new NTableIndex[i + this.index.length];
			System.arraycopy(baseIndex, 0, arr, 0, i);
			for (NTableIndex idx : this.index) {
				String name = idx.name.value;
				int j = 0;
				for (; j < i; j++) {
					if (name.equals(arr[j].name.value)) {
						arr[j] = arr[j].merge(idx);
						break;
					}
				}
				if (j == i) {
					arr[i++] = idx;
				}
			}
			if (i < arr.length) {
				NTableIndex[] arr2 = new NTableIndex[i];
				System.arraycopy(arr, 0, arr2, 0, i);
				arr = arr2;
			}
			return arr;
		}
		return this.index;
	}

	protected NTableRelation[] getMergedRelation() {
		if (this.base != null) {
			return mergeArray(this.base.relation, this.relation);
		}
		return this.relation;
	}

	protected NTableHierarchy[] getMergedHierarchy() {
		if (this.base != null) {
			return mergeArray(this.base.hierarchy, this.hierarchy);
		}
		return this.hierarchy;
	}

	protected NTablePartition getMergedPartition() {
		return this.partition;
	}

	@SuppressWarnings("unchecked")
	static final <T> T[] mergeArray(T[] a, T[] b) {
		if (a == null) {
			return b;
		}
		if (b == null) {
			return a;
		}
		int i = a.length;
		T[] arr = (T[]) Array.newInstance(a.getClass().getComponentType(),
				new int[] { i + b.length });
		System.arraycopy(a, 0, arr, 0, i);
		System.arraycopy(b, 0, arr, i, b.length);
		return arr;
	}

	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}
}
