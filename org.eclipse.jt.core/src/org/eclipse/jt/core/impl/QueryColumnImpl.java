package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.obja.StructFieldDefine;
import org.eclipse.jt.core.def.query.QueryColumnDeclare;
import org.eclipse.jt.core.type.Digester;

/**
 * 查询语句定义的输出列
 * 
 * @author Jeff Tang
 * 
 */
final class QueryColumnImpl extends
		SelectColumnImpl<QueryStatementBase, QueryColumnImpl> implements
		QueryColumnDeclare {

	public final void digestType(Digester digester) {
		this.digestAuthAndName(digester);
		this.value().getType().digestType(digester);
	}

	public final void setMapingField(StructFieldDefine value) {
		this.checkModifiable();
		StructFieldDefineImpl sf = (StructFieldDefineImpl) value;
		if (sf != null && sf.owner != this.owner.mapping) {
			throw new IllegalArgumentException("无效的结构字段");
		}
		this.field = sf;

	}

	public final void setMapingField(String structFieldName) {
		this.checkModifiable();
		this.field = this.owner.mapping.fields.get(structFieldName);

	}

	public final StructFieldDefine getMapingField() {
		return this.field;
	}

	static final String xml_attr_mapping_field = "m-field";

	StructFieldDefineImpl field;

	QueryColumnImpl(QueryStatementBase owner, String name, ValueExpr expr) {
		super(owner, name, expr);
	}

	@Override
	final void cloneTo(SelectImpl<?, ?> owner, ArgumentOwner args) {
		SelectColumnImpl<?, ?> column = owner.newColumn(this.name, this.value()
				.clone(owner, args));
		if (column instanceof QueryColumnImpl
				&& owner instanceof MappingQueryStatementImpl) {
			((QueryColumnImpl) column).field = this.field;
		}
	}

}
