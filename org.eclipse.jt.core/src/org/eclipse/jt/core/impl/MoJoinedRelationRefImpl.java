package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.exp.ConditionalExpression;
import org.eclipse.jt.core.def.table.TableJoinType;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.exception.NullJoinConditionException;

/**
 * 更新语句的连接关系引用
 * 
 * @param <TRelation>
 *            引用目标关系类型
 * 
 * @author Jeff Tang
 */
abstract class MoJoinedRelationRefImpl<TRelation extends Relation> extends
		MoRelationRefImpl<TRelation, MoJoinedRelationRef, MoJoinedRelationRef>
		implements MoJoinedRelationRef {

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
		this.condition = (ConditionalExpr) condition;

	}

	public final void setJoinType(TableJoinType type) {
		this.checkModifiable();
		if (type == null) {
			throw new NullArgumentException("连接类型");
		}
		this.type = type;
	}

	/**
	 * 连接条件
	 */
	private ConditionalExpr condition;

	/**
	 * 连接类型
	 */
	private TableJoinType type = TableJoinType.INNER;

	MoJoinedRelationRefImpl(ModifyStatementImpl statement, String name,
			TRelation target) {
		super(statement, name, target);
	}

	public final void render(ISqlRelationRefBuffer buffer, TableUsages usages) {
		ISqlJoinedRelationRefBuffer self = this.renderSelf(buffer, usages);
		this.condition.render(self.onCondition(), usages);
		MoJoinedRelationRef join = this.getJoins();
		if (join != null) {
			join.render(self, usages);
		}
		MoJoinedRelationRef next = this.next();
		if (next != null) {
			next.render(buffer, usages);
		}
	}

	abstract protected ISqlJoinedRelationRefBuffer renderSelf(
			ISqlRelationRefBuffer buffer, TableUsages usages);
}
