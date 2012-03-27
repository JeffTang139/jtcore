package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.exp.HierarchyOperateExpression;
import org.eclipse.jt.core.def.exp.HierarchyOperator;
import org.eclipse.jt.core.def.exp.ValueExpression;
import org.eclipse.jt.core.def.table.HierarchyDefine;
import org.eclipse.jt.core.misc.SXElement;

/**
 * 级次函数
 * 
 * @author Jeff Tang
 * 
 */
public class HierarchyOperateExpr extends ValueExpr implements
		HierarchyOperateExpression {

	public final ValueExpression getLevel() {
		return this.level;
	}

	public final HierarchyOperator getOperator() {
		return this.operator;
	}

	public final QuTableRef getSource() {
		return (QuTableRef) this.tableRef;
	}

	public final DataTypeBase getType() {
		return this.operator.getType();
	}

	@Override
	public final String getXMLTagName() {
		return HierarchyOperateExpr.xml_name_hierarchy_operate;
	}

	@Override
	public final void render(SXElement element) {
		super.render(element);
		element.setAttribute(xml_attr_reference, this.tableRef.getName());
		element.setAttribute(HierarchyOperateExpr.xml_attr_hierarchy,
				this.hierarchy.name);
		element.setEnum(HierarchyOperateExpr.xml_attr_op_type, this.operator);
	}

	@Override
	final String getDescription() {
		return "级次运算表达式";
	}

	static final String xml_name_hierarchy_operate = "hierarchy-operate";
	static final String xml_attr_reference = "reference";
	static final String xml_attr_hierarchy = "hierarchy";
	static final String xml_attr_op_type = "type";
	static final String xml_attr_level = "level";

	final TableRef tableRef;

	final HierarchyDefineImpl hierarchy;

	final HierarchyOperatorImpl operator;

	final ValueExpr level;

	HierarchyOperateExpr(RelationRef relationRef, HierarchyDefine hierarchy,
			HierarchyOperatorImpl operator, ValueExpr level) {
		this.tableRef = StandaloneTableRef.ensureHierarchyForTableRef(relationRef,
				hierarchy);
		this.hierarchy = (HierarchyDefineImpl) hierarchy;
		this.operator = operator;
		this.level = level;
	}

	@Override
	final ValueExpr clone(RelationRefDomain domain, ArgumentOwner args) {
		RelationRef relationRef = domain
				.getRelationRefRecursively(this.tableRef.getName());
		try {
			QuTableRef tableRef = (QuTableRef) relationRef;
			return new HierarchyOperateExpr(tableRef,
					tableRef.getTarget().hierarchies.get(this.hierarchy),
					this.operator, this.level == null ? null
							: this.level.clone(domain, args));
		} catch (ClassCastException e) {
			throw new IllegalArgumentException("关系引用类型错误");
		}
	}

	@Override
	final HierarchyOperateExpr clone(RelationRef fromSample, RelationRef from,
			RelationRef toSample, RelationRef to) {
		throw new UnsupportedOperationException();
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitHierarchyOperateExpr(this, context);
	}

	@Override
	final void render(ISqlExprBuffer buffer, TableUsages usages) {
		// HCL Auto-generated method stub
		throw Utils.notImplemented();
	}

}
