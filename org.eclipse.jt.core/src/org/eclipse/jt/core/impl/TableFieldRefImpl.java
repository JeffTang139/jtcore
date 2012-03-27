package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.exp.TableFieldRefExpr;
import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.type.DataType;

/**
 * 表字段引用表达式
 * 
 * @author Jeff Tang
 * 
 */
final class TableFieldRefImpl extends RelationColumnRefImpl implements
		TableFieldRefExpr {

	@Override
	public final TableFieldDefineImpl getColumn() {
		return this.field;
	}

	@Override
	public final TableRef getReference() {
		return this.tableRef;
	}

	public final DataType getType() {
		return this.field.getType();
	}

	@Override
	public final String getXMLTagName() {
		return xml_name_fieldref;
	}

	@Override
	public final void render(SXElement element) {
		super.render(element);
		element.setString(xml_attr_tableref, this.tableRef.getName());
		element.setString(xml_attr_field, this.field.name);
	}

	@Override
	final String getDescription() {
		return "字段引用表达式";
	}

	@Override
	public final String toString() {
		return this.getReference() == null || this.field == null ? null : this
				.getReference().getName().concat(".").concat(this.field.name);
	}

	static final String xml_name_fieldref = "field-ref";
	static final String xml_attr_tableref = "reference";
	static final String xml_attr_field = "field";

	/**
	 * 所属表引用
	 */
	final TableRef tableRef;
	/**
	 * 表字段定义
	 */
	final TableFieldDefineImpl field;

	TableFieldRefImpl(TableRef tableRef, TableFieldDefineImpl field) {
		if (tableRef.getTarget() != field.owner) {
			throw new IllegalArgumentException("字段定义[" + field.name
					+ "]不属于表引用的目标逻辑表[" + tableRef.getTarget().name + "]");
		}
		this.tableRef = tableRef;
		this.field = field;
	}

	/**
	 * 从xml对象中构造字段引用对象
	 * 
	 * @param sample
	 * @param owner
	 */
	TableFieldRefImpl(SXElement sample, RelationRefOwner owner) {
		RelationRef relationRef = owner.findRelationRef(sample
				.getAttribute(xml_attr_tableref));
		if (relationRef == null || !(relationRef instanceof TableRef)) {
			throw new IllegalArgumentException();
		}
		this.tableRef = (TableRef) relationRef;
		this.field = this.tableRef.getTarget().fields.get(sample
				.getAttribute(xml_attr_field));
	}

	@Override
	final ValueExpr clone(RelationRef fromSample, RelationRef from,
			RelationRef toSample, RelationRef to) {
		try {
			// HCL 还需要检查下逻辑,特别是克隆副本的判断
			if (this.tableRef == fromSample) {
				// 引用的目标可能是克隆的副本!!
				if (!from.getTarget().getName()
						.equals(fromSample.getTarget().getName())) {
					throw new IllegalArgumentException();
				}
				return ((TableRef) from).expOf(this.field.name);
			} else if (this.tableRef == toSample) {
				// 引用的目标可能是克隆的副本!!
				if (!to.getTarget().getName()
						.equals(toSample.getTarget().getName())) {
					throw new IllegalArgumentException();
				}
				return ((TableRef) to).expOf(this.field.name);
			}
		} catch (ClassCastException e) {
			throw new IllegalArgumentException();
		}
		throw new IllegalArgumentException();
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitTableFieldRef(this, context);
	}

	@Override
	final void render(ISqlExprBuffer buffer, TableUsages usages) {
		buffer.loadField(
				Render.aliasOf(this.tableRef, this.field.getDBTable()),
				this.field.namedb());
	}

}
