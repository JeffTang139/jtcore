package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.query.DerivedQueryDefine;
import org.eclipse.jt.core.def.table.TableDeclarator;
import org.eclipse.jt.core.def.table.TableDefine;

interface RelationRefBuildable extends
		org.eclipse.jt.core.def.query.RelationRefBuildable {

	public TableRef newReference(TableDefine table);

	public TableRef newReference(TableDefine table, String name);

	public TableRef newReference(TableDeclarator table);

	public TableRef newReference(TableDeclarator table, String name);

	public QueryRef newReference(DerivedQueryDefine query);

	public QueryRef newReference(DerivedQueryDefine query, String name);

}
