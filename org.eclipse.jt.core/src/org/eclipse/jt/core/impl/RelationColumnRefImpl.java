package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.exp.RelationColumnRefExpr;

/**
 * 关系列引用抽象类
 * 
 * @author Jeff Tang
 * 
 */
abstract class RelationColumnRefImpl extends ValueExpr implements
		RelationColumnRefExpr {

	public abstract RelationRef getReference();

	public abstract RelationColumn getColumn();

	@Override
	final RelationColumnRefImpl clone(RelationRefDomain domain,
			ArgumentOwner arguments) {
		RelationRef relationRef = domain.getRelationRefRecursively(this
				.getReference().getName());
		RelationColumn column = relationRef.getTarget().getColumn(
				this.getColumn().getName());
		return relationRef.expOf(column);
	}
}
