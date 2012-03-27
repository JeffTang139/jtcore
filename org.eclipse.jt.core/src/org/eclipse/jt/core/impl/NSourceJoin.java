package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.table.TableJoinType;

/**
 * 关系-连接节点
 * 
 * @author Jeff Tang
 * 
 */
class NSourceJoin extends NSource {
	public final NSource left;
	public final NSource right;
	public final TableJoinType joinType;
	public final NConditionExpr condition;

	public NSourceJoin(TableJoinType joinType, NSource left, NSource right,
			NConditionExpr condition) {
		this.joinType = joinType;
		this.left = left;
		this.right = right;
		this.condition = condition;
	}

	public int startLine() {
		return this.left.startLine();
	}

	public int startCol() {
		return this.left.startCol();
	}

	public int endLine() {
		return this.condition.endLine();
	}

	public int endCol() {
		return this.condition.endCol();
	}

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitSourceJoin(visitorContext, this);
	}
}
