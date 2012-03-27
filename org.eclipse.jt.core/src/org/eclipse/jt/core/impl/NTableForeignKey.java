package org.eclipse.jt.core.impl;


class NTableForeignKey {
	public final TString name;
	public final TString refTable;
	public final TString refField;

	public NTableForeignKey(TString name, TString refTable, TString refField) {
		this.name = name;
		this.refTable = refTable;
		this.refField = refField;
	}
}
