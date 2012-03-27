package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.exp.ConditionalExpression;
import org.eclipse.jt.core.def.query.DerivedQueryDefine;
import org.eclipse.jt.core.def.table.TableDeclarator;
import org.eclipse.jt.core.def.table.TableDefine;
import org.eclipse.jt.core.def.table.TableRelationDefine;

/**
 * 带条件的更新语句
 * 
 * @author Jeff Tang
 * 
 */
abstract class ConditionalStatementImpl extends ModifyStatementImpl implements
		RelationJoinable {

	public final ConditionalExpr getCondition() {
		return this.condition;
	}

	public final void setCondition(ConditionalExpression condition) {
		this.checkModifiable();
		if (condition == null) {
			this.condition = null;
		} else {
			ConditionalExpr c = (ConditionalExpr) condition;
			if (SystemVariables.VALIDATE_EXPR_DOMAIN) {
				c.validateDomain(this);
			}
			this.condition = (ConditionalExpr) condition;
		}
	}

	public final MoJoinedTableRef newJoin(TableDefine table) {
		return this.moTableRef.newJoin(table);
	}

	public final MoJoinedTableRef newJoin(TableDefine table, String name) {
		return this.moTableRef.newJoin(table, name);
	}

	public final MoJoinedTableRef newJoin(TableDeclarator table) {
		return this.moTableRef.newJoin(table);
	}

	public final MoJoinedTableRef newJoin(TableDeclarator table, String name) {
		return this.moTableRef.newJoin(table, name);
	}

	public final MoJoinedTableRef newJoin(TableRelationDefine sample) {
		return this.moTableRef.newJoin(sample);
	}

	public final MoJoinedTableRef newJoin(TableRelationDefine sample,
			String name) {
		return this.moTableRef.newJoin(sample);
	}

	public final MoJoinedQueryRef newJoin(DerivedQueryDefine query) {
		return this.moTableRef.newJoin(query);
	}

	public final MoJoinedQueryRef newJoin(DerivedQueryDefine query, String name) {
		return this.moTableRef.newJoin(query, name);
	}

	ConditionalExpr condition;

	ConditionalStatementImpl(String name, TableDefineImpl table) {
		super(name, table);
	}

	ConditionalStatementImpl(String name, StructDefineImpl arguments,
			TableDefineImpl table) {
		super(name, arguments, table);
	}

	final void render(ISqlSelectBuffer buffer, TableUsages usages) {
		ISqlTableRefBuffer tableRef = Render.renderTableRef(this.moTableRef,
				buffer, usages);
		MoJoinedRelationRef join = this.moTableRef.getJoins();
		if (join != null) {
			join.render(tableRef, usages);
		}
		if (this.condition != null) {
			this.condition.render(buffer.where(), usages);
		}
	}

}