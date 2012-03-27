package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.query.DerivedQueryDefine;
import org.eclipse.jt.core.def.table.TableDeclarator;
import org.eclipse.jt.core.def.table.TableDefine;
import org.eclipse.jt.core.def.table.TableRelationDefine;

interface RelationJoinable extends
		org.eclipse.jt.core.def.query.RelationJoinable {

	public JoinedTableRef newJoin(TableDefine table);

	public JoinedTableRef newJoin(TableDefine table, String name);

	public JoinedTableRef newJoin(TableDeclarator table);

	public JoinedTableRef newJoin(TableDeclarator table, String name);

	public JoinedTableRef newJoin(TableRelationDefine sample);

	public JoinedTableRef newJoin(TableRelationDefine sample, String name);

	public JoinedQueryRef newJoin(DerivedQueryDefine query);

	public JoinedQueryRef newJoin(DerivedQueryDefine query, String name);
}
