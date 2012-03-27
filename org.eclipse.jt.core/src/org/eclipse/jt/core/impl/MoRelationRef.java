package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.query.DerivedQueryDefine;
import org.eclipse.jt.core.def.table.TableDeclarator;
import org.eclipse.jt.core.def.table.TableDefine;
import org.eclipse.jt.core.def.table.TableRelationDefine;

/**
 * 更新语句的关系引用
 * 
 * @author Jeff Tang
 * 
 */
interface MoRelationRef extends NodableRelationRef {

	ModifyStatementImpl getOwner();

	MoRelationRef next();

	MoRelationRef last();

	MoJoinedRelationRef getJoins();

	MoJoinedTableRef newJoin(TableDefine target);

	MoJoinedTableRef newJoin(TableDefine target, String name);

	MoJoinedTableRef newJoin(TableDeclarator target);

	MoJoinedTableRef newJoin(TableDeclarator target, String name);

	MoJoinedTableRef newJoin(TableRelationDefine sample);

	MoJoinedTableRef newJoin(TableRelationDefine sample, String name);

	MoJoinedQueryRef newJoin(DerivedQueryDefine query);

	MoJoinedQueryRef newJoin(DerivedQueryDefine query, String name);

}
