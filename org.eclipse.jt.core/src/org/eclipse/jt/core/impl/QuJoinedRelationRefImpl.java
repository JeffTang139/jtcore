package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.exp.ConditionalExpression;
import org.eclipse.jt.core.def.table.TableJoinType;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.exception.NullJoinConditionException;
import org.eclipse.jt.core.misc.SXElement;

/**
 * 查询中使用的连接引用的基类
 * 
 * @param <TRelation>
 * 
 * @author Jeff Tang
 */
abstract class QuJoinedRelationRefImpl<TRelation extends Relation> extends
		QuRelationRefImpl<TRelation, QuJoinedRelationRef, QuJoinedRelationRef>
		implements QuJoinedRelationRef {

	public final ConditionalExpr getJoinCondition() {
		return this.condition;
	}

	public final TableJoinType getJoinType() {
		return this.type;
	}

	public final void setJoinCondition(ConditionalExpression condition) {
		this.checkModifiable();
		if (condition == null) {
			throw new NullJoinConditionException(this);
		}
		ConditionalExpr jc = (ConditionalExpr) condition;
		if (SystemVariables.VALIDATE_EXPR_DOMAIN) {
			jc.validateDomain(this.owner);
		}
		this.condition = jc;
	}

	public final void setJoinType(TableJoinType type) {
		this.checkModifiable();
		if (type == null) {
			throw new NullArgumentException("连接类型");
		}
		this.type = type;
	}

	static final String xml_attr_join_type = "join-type";
	static final String xml_element_join_condition = "join-condition";

	final QuRelationRef parent;

	ConditionalExpr condition;

	TableJoinType type = TableJoinType.INNER;

	QuJoinedRelationRefImpl(SelectImpl<?, ?> owner, String name,
			TRelation target, QuRelationRef parent) {
		super(owner, name, target);
		this.parent = parent;
	}

	public final QuRelationRef parent() {
		return this.parent;
	}

	public final void cloneTo(QuRelationRef from, ArgumentOwner args) {
		QuJoinedRelationRef selfClone = this.cloneSelfTo(from, args);
		selfClone.setJoinType(this.type);
		if (this.condition == null) {
			throw new NullJoinConditionException(this);
		}
		selfClone.setJoinCondition(this.condition.clone(from.getOwner(), args));
		QuJoinedRelationRef join = this.getJoins();
		if (join != null) {
			join.cloneTo(selfClone, args);
		}
		QuJoinedRelationRef next = this.next();
		if (next != null) {
			next.cloneTo(from, args);
		}
	}

	/**
	 * 只负责调用newJoin方法构建相应的连接对象,不设置条件及连接类型
	 * 
	 * @param from
	 *            连接的最左边,即调用newJoin的对象
	 * @param args
	 * @return
	 */
	protected abstract QuJoinedRelationRef cloneSelfTo(QuRelationRef from,
			ArgumentOwner args);

	@Override
	public void render(SXElement element) {
		super.render(element);
		element.setEnum(xml_attr_join_type, this.type);
		this.condition.renderInto(element.append(xml_element_join_condition));
	}

	@Override
	public void validate() {
		if (this.condition == null) {
			throw new NullJoinConditionException(this);
		}
	}

	public void render(ISqlRelationRefBuffer buffer, TableUsages usages) {
		ISqlJoinedRelationRefBuffer self = this.renderSelf(buffer, usages);
		this.condition.render(self.onCondition(), usages);
		QuJoinedRelationRef join = this.getJoins();
		if (join != null) {
			join.render(self, usages);
		}
		QuJoinedRelationRef next = this.next();
		if (next != null) {
			next.render(buffer, usages);
		}
	}

	abstract ISqlJoinedRelationRefBuffer renderSelf(
			ISqlRelationRefBuffer buffer, TableUsages usages);

}
