package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.query.SetOperateDeclare;
import org.eclipse.jt.core.exception.InvalidStatementDefineException;

/**
 * 查询的集合运算
 * 
 * @author Jeff Tang
 * 
 */
final class SetOperateImpl extends MetaBase implements SetOperateDeclare,
		OMVisitable {

	public final SetOperatorImpl getOperator() {
		return this.operator;
	}

	public final DerivedQueryImpl getTarget() {
		return this.target;
	}

	@Override
	final String getDescription() {
		return "union子句";
	}

	@Override
	public final String getXMLTagName() {
		return xml_name;
	}

	static final String xml_name = "set-operate";

	final SelectImpl<?, ?> leftmost;
	final DerivedQueryImpl target;
	final SetOperatorImpl operator;

	SetOperateImpl(SelectImpl<?, ?> leftmost, DerivedQueryImpl so,
			SetOperatorImpl operator) {
		this.leftmost = leftmost;
		this.target = so;
		if (so.isWith) {
			throw new InvalidStatementDefineException("With定义不能union");
		}
		this.operator = operator;
	}

	public final RelationRefDomain getParentDomain() {
		return this.leftmost.getDomain();
	}

	public final RelationRefDomain getParentNode() {
		return this.leftmost;
	}

	public final DerivedQueryImpl getWith(String name) {
		return this.leftmost.getWith(name);
	}

	final void cloneTo(SelectImpl<?, ?> select, ArgumentOwner args) {
		DerivedQueryImpl dq = select.newDerivedQuery();
		this.target.cloneSelectTo(dq, args);
		switch (this.operator) {
		case UNION:
			select.union(dq);
			break;
		case UNION_ALL:
			select.unionAll(dq);
			break;
		default:
			throw new UnsupportedOperationException();
		}
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitSetOperate(this, context);
	}

}
