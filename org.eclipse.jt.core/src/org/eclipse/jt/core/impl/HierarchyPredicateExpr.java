package org.eclipse.jt.core.impl;

import java.util.ArrayList;

import org.eclipse.jt.core.def.exp.ConditionalExpression;
import org.eclipse.jt.core.def.exp.HierarchyPredicate;
import org.eclipse.jt.core.def.exp.HierarchyPredicateExpression;
import org.eclipse.jt.core.def.exp.ValueExpression;
import org.eclipse.jt.core.def.query.RelationRefDefine;
import org.eclipse.jt.core.def.table.HierarchyDefine;
import org.eclipse.jt.core.misc.SXElement;


/**
 * ����ν�ʱ��ʽ
 * 
 * @author Jeff Tang
 * 
 */
public class HierarchyPredicateExpr extends ConditionalExpr implements
		HierarchyPredicateExpression {

	public final HierarchyPredicate getPredicate() {
		return this.predicate;
	}

	public final ValueExpression getLevel() {
		return this.level;
	}

	public final QuTableRef getSource() {
		return (QuTableRef) this.left;
	}

	public final QuTableRef getTarget() {
		return (QuTableRef) this.right;
	}

	public final ConditionalExpression not() {
		return new HierarchyPredicateExpr(!this.not, this.left, this.hierarchy,
				this.predicate, this.right, this.level);
	}

	@Override
	public final String getXMLTagName() {
		return xml_name_hierarchy_predicate;
	}

	@Override
	public final void render(SXElement element) {
		super.render(element);
		element.setAttribute(xml_attr_left, this.left.getName());
		element.setAttribute(HierarchyPredicateExpr.xml_attr_hrchy,
				this.hierarchy.name);
		element.setEnum(HierarchyPredicateExpr.xml_attr_hrchy_type,
				this.predicate);
		if (this.right != null) {
			element.setAttribute(xml_attr_right, this.right.getName());
		}
	}

	@Override
	final String getDescription() {
		return "����ν�ʱ��ʽ";
	}

	static final String xml_name_hierarchy_predicate = "hrchy-predicate";
	static final String xml_attr_left_author = "left-author";
	static final String xml_attr_left = "left";
	static final String xml_attr_hrchy = "hierarchy";
	static final String xml_attr_hrchy_type = "type";
	static final String xml_attr_right_author = "right-author";
	static final String xml_attr_right = "right";
	static final String xml_attr_relative = "relative";

	/**
	 * Դ������
	 */
	final TableRef left;

	/**
	 * ʹ�õļ��ζ���
	 */
	final HierarchyDefineImpl hierarchy;

	/**
	 * ����ν��
	 */
	final HierarchyPredicateImpl predicate;

	/**
	 * Ŀ�������
	 */
	final TableRef right;

	/**
	 * ������Ը��ڵ�,����ӽڵ��ν��,�˲�����ʾ������<br>
	 * ���ڷ�Χ���ڵ�,��Χ�ӽڵ��ν��,�˲�����ʾ���Χ
	 */
	final ValueExpr level;

	HierarchyPredicateExpr(boolean not, RelationRef left,
			HierarchyDefine hierarchy, HierarchyPredicateImpl predicate,
			RelationRefDefine right, ValueExpr level) {
		super(not);
		this.left = StandaloneTableRef.ensureHierarchyForTableRef(left, hierarchy);
		this.predicate = predicate;
		this.hierarchy = (HierarchyDefineImpl) hierarchy;
		this.right = StandaloneTableRef.ensureHierarchyForTableRef(right, hierarchy);
		this.level = level;
	}

	@Override
	final HierarchyPredicateExpr clone(RelationRefDomain domain,
			ArgumentOwner args) {
		TableRef left = (TableRef) domain.getRelationRefRecursively(this.left
				.getName());
		TableRef right = (TableRef) domain.getRelationRefRecursively(this.right
				.getName());
		ValueExpr level = this.level != null ? this.level.clone(domain, args)
				: null;
		return new HierarchyPredicateExpr(this.not, left, this.hierarchy,
				this.predicate, right, level);
	}

	@Override
	final ConditionalExpr clone(RelationRef fromSample, RelationRef from,
			RelationRef toSample, RelationRef to) {
		throw new UnsupportedOperationException();
	}

	@Override
	final void fillEqualsRelationColumnRef(RelationRef relaitonRef,
			RelationColumn relationColumn,
			ArrayList<RelationColumnRefImpl> resultList) {
		// do nothing
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitHierarchyPredicateExpr(this, context);
	}

	@Override
	final void render(ISqlExprBuffer buffer, TableUsages usages) {
		// HCL Auto-generated method stub
		throw Utils.notImplemented();
	}

}
