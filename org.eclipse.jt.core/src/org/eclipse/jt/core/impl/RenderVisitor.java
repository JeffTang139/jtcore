package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.query.GroupByType;
import org.eclipse.jt.core.def.table.TableJoinType;
import org.eclipse.jt.core.impl.NLogicalExpr.Operator;
import org.eclipse.jt.core.impl.NStrCompareExpr.Keywords;

class RenderVisitor implements SQLVisitor<StringFormatter> {
	final static RenderVisitor VISITOR = new RenderVisitor();

	public static String render(SQLVisitable visitable) {
		StringFormatter sf = new StringFormatter();
		visitable.accept(sf, RenderVisitor.VISITOR);
		return sf.toString();
	}

	public void visitLiteralBoolean(StringFormatter f, NLiteralBoolean b) {
		f.append(b.value);
	}

	public void visitLiteralInt(StringFormatter f, NLiteralInt i) {
		f.append(i.value);
	}

	public void visitLiteralLong(StringFormatter f, NLiteralLong l) {
		f.append(l.value);
	}

	public void visitLiteralDouble(StringFormatter f, NLiteralDouble d) {
		f.append(d.value);
	}

	public void visitLiteralString(StringFormatter f, NLiteralString s) {
		f.append(f.encodeString(s.value));
	}

	public void visitLiteralDate(StringFormatter f, NLiteralDate d) {
		f.append(d.value);
	}

	public void visitLiteralBytes(StringFormatter f, NLiteralBytes b) {
		StringBuilder sb = new StringBuilder();
		if (b.value != null) {
			for (byte bt : b.value) {
				int i = bt >> 4;
				i = i > 9 ? (i - 10 + 'A') : (i + '0');
				sb.append((char) i);
				i = bt & 0x0f;
				i = '0' + (i > 9 ? (i - 10) : i);
				sb.append((char) i);
			}
		}
		f.append("BYTES ").append(f.encodeString(sb.toString()));
	}

	public void visitLiteralGUID(StringFormatter f, NLiteralGUID g) {
		f.append(f.encodeString(g.value.toString()));
	}

	public void visitBinaryExpr(StringFormatter f, NBinaryExpr e) {
		int c = e.op.getPrec();
		if (NBinaryExpr.getPrec(e.left) < c) {
			f.append('(').append(e.left).append(')');
		} else {
			f.append(e.left);
		}
		f.space().append(e.op.toString()).space();
		if (NBinaryExpr.getPrec(e.right) <= c) {
			f.append('(').append(e.right).append(')');
		} else {
			f.append(e.right);
		}
	}

	public void visitAggregateExpr(StringFormatter f, NAggregateExpr e) {
		f.append(e.func.toString()).append('(');
		if (e.quantifier == SetQuantifier.DISTINCT) {
			f.append(e.quantifier.toString()).space();
		}
		if (e.expr == null) {
			f.append("*");
		} else {
			f.append(e.expr);
		}
		f.append(')');
	}

	public void visitCoalesceExpr(StringFormatter f, NCoalesceExpr e) {
		f.append("COALESCE(").singleline(e.params, ',').append(')');
	}

	public void visitColumnRefExpr(StringFormatter f, NColumnRefExpr e) {
		f.append(f.encodeID(e.source.value)).append('.').append(
				f.encodeID(e.field.value));
	}

	public void visitNameRefExpr(StringFormatter f, NNameRef e) {
		f.append(f.encodeID(e.name.value));
	}

	public void visitFunctionExpr(StringFormatter f, NFunctionExpr e) {
		f.append(e.func.toString()).append('(').singleline(e.params, ',')
				.append(')');
	}

	public void visitHaidExpr(StringFormatter f, NHaidExpr e) {
		f.append("h_aid(").append(e.source.value).append('.').append(
				e.path.value);
		if (e.offset != null) {
			f.space().append(e.relOrAbs ? "rel" : "abo").space().append(
					e.offset);
		}
	}

	public void visitHlvExpr(StringFormatter f, NHlvExpr e) {
		f.append("h_lv(").append(e.source.value).append('.').append(
				e.path.value);
	}

	public void visitNegativeExpr(StringFormatter f, NNegativeExpr e) {
		f.append('-');
		if (NBinaryExpr.getPrec(e.left) <= NBinaryExpr.getPrec(e)) {
			f.append('(').append(e.left).append(')');
		} else {
			f.append(e.left);
		}
	}

	public void visitNullExpr(StringFormatter f, NNullExpr e) {
		f.append("NULL");
	}

	public void visitSearchedCaseExpr(StringFormatter f, NSearchedCaseExpr e) {
		f.mark().append("CASE").newline().indent();
		f.append("WHEN ").append(e.branches[0].condition).append(" THEN ")
				.append(e.branches[0].returnValue);
		for (int i = 1, c = e.branches.length; i < c; i++) {
			f.newline().append("WHEN ").append(e.branches[i].condition).append(
					" THEN ").append(e.branches[i].returnValue);
		}
		if (e.elseBranch != null) {
			f.newline().append("ELSE ").append(e.elseBranch);
		}
		f.back().append("END").back();
	}

	public void visitSimpleCaseExpr(StringFormatter f, NSimpleCaseExpr e) {
		f.mark().append("CASE ").append(e.value).newline().indent();
		f.append("WHEN ").append(e.branches[0].value).append(" THEN ").append(
				e.branches[0].returnValue);
		for (int i = 1, c = e.branches.length; i < c; i++) {
			f.newline().append("WHEN ").append(e.branches[i].value).append(
					" THEN ").append(e.branches[i].returnValue);
		}
		if (e.elseBranch != null) {
			f.append("ELSE ").append(e.elseBranch).newline();
		}
		f.back().append("END").back();
	}

	public void visitVarRefExpr(StringFormatter f, NVarRefExpr e) {
		if (e.owner != null) {
			f.append(e.owner).append('.');
		}
		f.append(e.name.value);
	}

	public void visitParamDeclare(StringFormatter f, NParamDeclare v) {
		if (v.modifier != NParamDeclare.InOut.IN) {
			f.append(v.modifier.toString()).space();
		}
		f.append(v.name.value).space().append(v.type.toString());
		if (v.notNull) {
			f.append(" NOT NULL");
		}
		if (v.defaultValue != null) {
			f.append(" DEFAULT ").append(v.defaultValue);
		}
	}

	public void visitCompareExpr(StringFormatter f, NCompareExpr e) {
		f.append(e.left).space().append(e.op.toString()).space()
				.append(e.right);
	}

	public void visitBetweenExpr(StringFormatter f, NBetweenExpr e) {
		f.append(e.value).append(" BETWEEN ").append(e.left).append(" AND ")
				.append(e.right);
	}

	public void visitExistsExpr(StringFormatter f, NExistsExpr e) {
		f.append("EXISTS (").append(e.query).append(')');
	}

	public void visitHierarchyExpr(StringFormatter f, NHierarchyExpr e) {
		f.append(f.encodeID(e.left.value)).space().append(e.keyword.toString())
				.space().append(f.encodeID(e.right.value)).append(" USING ")
				.append(f.encodeID(e.rel.value));
		if (e.keyword == NHierarchyExpr.Keywords.DESCENDANTOF) {
			NDescendantOfExpr d = (NDescendantOfExpr) e;
			if (d.diff != null) {
				if (d.leOrEq) {
					f.append(" RANGE ");
				} else {
					f.append(" RELATIVE ");
				}
				f.append(d.diff);
			}
		}
	}

	public void visitInExpr(StringFormatter f, NInExpr e) {
		f.append(e.value).append(e.not ? " NOT IN (" : " IN (");
		if (e.param instanceof NInParamValueList) {
			f.singleline(((NInParamValueList) e.param).values, ',');
		} else if (e.param instanceof NInParamSubQuery) {
			f.append(((NInParamSubQuery) e.param).query);
		}
		f.append(')');
	}

	public void visitIsLeafExpr(StringFormatter f, NIsLeafExpr e) {
		f.append(f.encodeID(e.left.value)).append(" IS LEAF ").append(
				f.encodeID(e.hier.value));
	}

	public void visitIsNullExpr(StringFormatter f, NIsNullExpr e) {
		f.append(e.value).append(e.not ? " IS NOT NULL" : " IS NULL");
	}

	public void visitLogicalExpr(StringFormatter f, NLogicalExpr e) {
		int c = e.op.getPrec();
		if (e.op == Operator.NOT) {
			f.append("NOT ");
			if (NLogicalExpr.getPrec(e.left) <= c) {
				f.append('(').append(e.left).append(')');
			} else {
				f.append(e.left);
			}
		} else {
			if (NLogicalExpr.getPrec(e.left) < c) {
				f.append('(').append(e.left).append(')');
			} else {
				f.append(e.left);
			}
			f.space().append(e.op.toString()).space();
			if (NLogicalExpr.getPrec(e.right) <= c) {
				f.append('(').append(e.right).append(')');
			} else {
				f.append(e.right);
			}
		}
	}

	public void visitPathExpr(StringFormatter f, NPathExpr e) {
		f.append(f.encodeID(e.t1.value)).space().append(e.keyword.toString())
				.space().append(f.encodeID(e.t2.value)).append(" USING (")
				.append(f.encodeID(e.f1.value)).append(", ").append(e.f2.value)
				.append(')');
		if (e.diff != null) {
			f.append(" RELATIVE ").append(e.diff);
		}
	}

	public void visitStrCompareExpr(StringFormatter f, NStrCompareExpr e) {
		f.append(e.first).append(e.not ? " NOT" : "").space().append(
				e.keyword.toString()).space().append(e.second);
		if (e.keyword == Keywords.LIKE) {
			NLikeExpr l = (NLikeExpr) e;
			if (l.escape != null) {
				f.append(" ESCAPE ").append(l.escape);
			}
		}
	}

	public void visitQueryDeclare(StringFormatter f, NQueryDeclare q) {
		f.append("DEFINE QUERY ").append(f.encodeID(q.name.value)).append('(')
				.mark().multiline(q.params, ',').append(')').newline().back()
				.append("BEGIN").newline().indent().append(q.body).newline()
				.back().append("END");
	}

	public void visitQueryStmt(StringFormatter f, NQueryStmt q) {
		f.mark();
		if (q.predefines != null) {
			f.append("WITH").newline().indent().append('(').append(
					q.predefines[0].query).append(") AS ").append(
					q.predefines[0].name.value);
			for (int i = 1, c = q.predefines.length; i < c; i++) {
				f.append(',').newline().append('(').append(
						q.predefines[i].query).append(") AS ").append(
						q.predefines[i].name.value);
			}
			f.newline().back();
		}
		f.append(q.expr);
		if (q.order != null) {
			f.newline().append("ORDER BY").newline().indent();
			f.append(q.order.columns[0].column).space().append(
					q.order.columns[0].asc);
			for (int i = 1, c = q.order.columns.length; i < c; i++) {
				f.append(',').append(q.order.columns[i].column).space().append(
						q.order.columns[i].asc);
			}
			f.back();
		}
		f.back();
	}

	public void visitQuerySpecific(StringFormatter f, NQuerySpecific s) {
		f.mark().append("SELECT").newline().indent();
		f.append(s.select.columns[0].expr);
		if (s.select.columns[0].alias != null) {
			f.append(" AS ")
					.append(f.encodeID(s.select.columns[0].alias.value));
		}
		for (int i = 1, c = s.select.columns.length; i < c; i++) {
			f.append(',').newline().append(s.select.columns[i].expr);
			if (s.select.columns[i].alias != null) {
				f.append(" AS ").append(
						f.encodeID(s.select.columns[i].alias.value));
			}
		}
		f.back().newline().append("FROM").newline().indent().multiline(
				s.from.sources, ',').back();
		if (s.where != null) {
			f.newline().append("WHERE ").newline().indent()
					.append(s.where.expr).back();
		}
		if (s.group != null) {
			f.newline().append("GROUP BY").newline().indent().multiline(
					s.group.columns, ',');
			if (s.group.option == GroupByType.ROLL_UP) {
				f.append(" WITH ROLLUP");
			}
			f.back();
		}
		if (s.having != null) {
			f.newline().append("HAVING").newline().indent().append(
					s.having.expr).back();
		}
		f.back();
		if (s.unions != null) {
			for (NQuerySpecific u : s.unions) {
				f.newline().append("union");
				if (u.unionAll) {
					f.append(" all");
				}
				if (u.unions != null) {
					f.space().append('(').newline();
					u.accept(f, this);
					f.newline().append(')');
				} else {
					f.newline();
					u.accept(f, this);
				}
			}
		}
	}

	public void visitSourceJoin(StringFormatter f, NSourceJoin j) {
		f.append(j.left);
		if (j.joinType != TableJoinType.INNER) {
			f.space().append(j.joinType.toString());
		}
		f.append(" JOIN ").append(j.right).append(" ON ").append(j.condition);
	}

	public void visitSourceRelate(StringFormatter f, NSourceRelate r) {
		f.append(r.source);
		if (r.joinType != TableJoinType.INNER) {
			f.space().append(r.joinType.toString());
		}
		f.append(" RELATE ").append(f.encodeID(r.rel.value));
		if (r.alias != null) {
			f.append(" AS ").append(f.encodeID(r.alias.value));
		}
	}

	public void visitSourceTable(StringFormatter f, NSourceTable t) {
		f.append(f.encodeID(t.source.name.value));
		if (t.alias != null) {
			f.append(" AS ").append(f.encodeID(t.alias.value));
		}
		if (t.forUpdate) {
			f.append(" FOR UPDATE");
		}
	}

	public void visitSourceSubQuery(StringFormatter f, NSourceSubQuery q) {
		f.append('(').append(q.query).append(')').append(" AS ").append(
				f.encodeID(q.alias.value));
	}

	public void visitOrmDeclare(StringFormatter f, NOrmDeclare o) {
		f.append("DEFINE ORM ").append(f.encodeID(o.name.value)).append('(')
				.mark().multiline(o.params, ',').append(')').back().newline()
				.indent().append("MAPPING ").append(o.className).newline()
				.back().append("BEGIN").newline().indent().append(o.body)
				.newline().back().append("END");
	}

	public void visitOrmOverride(StringFormatter f, NOrmOverride o) {
		f.append("DEFINE ORM ").append(f.encodeID(o.name.value)).append('(')
				.mark().multiline(o.params, ',').append(')').back().newline()
				.indent().append("OVERRIDE ").append(o.superName.value).back()
				.newline().append("BEGIN").newline().indent().append(o.body)
				.newline().back().append("END");
	}

	public void visitInsertDeclare(StringFormatter f, NInsertDeclare i) {
		f.append("DEFINE INSERT ").append(f.encodeID(i.name.value)).append('(')
				.mark().multiline(i.params, ',').append(')').newline().back()
				.append("BEGIN").newline().indent().append(i.body).newline()
				.back().append("END");
	}

	public void visitInsertStmt(StringFormatter f, NInsertStmt i) {
		f.append("INSERT INTO ").append(i.insert.table.name.value).mark()
				.append(i.values).append(';').back();
	}

	public void visitInsertValues(StringFormatter f, NInsertValues v) {
		f.mark().append('(');
		f.append(f.encodeID(v.columns[0].value));
		for (int i = 1, len = v.columns.length; i < len; i++) {
			f.append(',').space();
			f.append(f.encodeID(v.columns[i].value));
		}
		f.append(')').newline().append("VALUES(").singleline(v.values, ',')
				.append(')').back();
	}

	public void visitInsertSubQuery(StringFormatter f, NInsertSubQuery q) {
		f.append('(').append(q.query).append(')');
	}

	public void visitUpdateDeclare(StringFormatter f, NUpdateDeclare u) {
		f.append("DEFINE INSERT ").append(f.encodeID(u.name.value)).append('(')
				.mark().multiline(u.params, ',').append(')').newline().back()
				.append("BEGIN").newline().indent().append(u.body).newline()
				.back().append("END");
	}

	public void visitUpdateStmt(StringFormatter f, NUpdateStmt u) {
		f.append("UPDATE ").newline().indent().append(u.update.source).back()
				.newline().append("SET").newline().indent();
		f.append(u.set.columns[0].column.value).append(" = ").append(
				u.set.columns[0].value);
		for (int i = 1, c = u.set.columns.length; i < c; i++) {
			f.append(',').append(u.set.columns[i].column.value).append(" = ")
					.append(u.set.columns[i].value).newline();
		}
		f.back();
		if (u.where != null) {
			if (u.where.expr != null) {
				f.newline().append("WHERE").newline().indent().append(
						u.where.expr).back();
			} else {
				f.newline().append("WHERE CURRENT OF ").append(
						u.where.cursor.value);
			}
		}
		f.append(';');
	}

	public void visitDeleteDeclare(StringFormatter f, NDeleteDeclare d) {
		f.append("DEFINE INSERT ").append(f.encodeID(d.name.value)).append('(')
				.mark().multiline(d.params, ',').append(')').newline().back()
				.append("BEGIN").newline().indent().append(d.body).newline()
				.back().append("END");
	}

	public void visitDeleteStmt(StringFormatter f, NDeleteStmt d) {
		f.append("DELETE FROM ").append(d.delete.source);
		if (d.where != null) {
			f.newline();
			if (d.where.expr != null) {
				f.indent().append(" WHERE ").append(d.where.expr).back();
			} else {
				f.indent().append(" WHERE CURRENT OF ").append(
						d.where.cursor.value).back();
			}
		}
		f.append(';');
	}

	private void appendTableField(StringFormatter f, NTableField field) {
		f.append(field.name.value).space().append(field.type.toString());
		if (field.notNull) {
			f.append(" NOT NULL");
		}
		if (field.defaultValue != null) {
			f.append(" DEFAULT(").append(field.defaultValue).append(')');
		}
		if (field.primaryKey) {
			f.append(" PRIMARY KEY");
		}
		if (field.foreignKey != null) {
			f.append(" RELATION ").append(field.foreignKey.name.value).append(
					" TO ").append(field.foreignKey.refTable.value).append('.')
					.append(field.foreignKey.refField.value);
		}
	}

	private void appendTableIndex(StringFormatter f, NTableIndex index) {
		if (index.unique) {
			f.append("UNIQUE ");
		}
		f.append(index.name.value).append('(');
		if (index.fields != null && index.fields.length > 0) {
			NTableIndexField fd = index.fields[0];
			f.append(fd.name.value);
			if (fd.desc) {
				f.append(" DESC");
			}
			for (int i = 1, c = index.fields.length; i < c; i++) {
				f.append(", ");
				fd = index.fields[i];
				f.append(fd.name.value);
				if (fd.desc) {
					f.append(" DESC");
				}
			}
		}
		f.append(')');
	}

	private void appendTableBlock(StringFormatter f, NAbstractTableDeclare t) {
		f.newline().append("BEGIN").newline().indent().append("FIELDS")
				.newline().indent();
		this.appendTableField(f, t.primary.fields[0]);
		for (int i = 1, c = t.primary.fields.length; i < c; i++) {
			f.append(',').newline();
			this.appendTableField(f, t.primary.fields[i]);
		}
		f.newline().back();
		if (t.extend != null) {
			for (NTableExtend ext : t.extend) {
				f.append("FIELDS ON ").append(ext.name.value).newline()
						.indent();
				this.appendTableField(f, ext.fields[0]);
				for (int i = 1, c = ext.fields.length; i < c; i++) {
					f.append(',').newline();
					this.appendTableField(f, ext.fields[i]);
				}
			}
		}
		if (t.index != null) {
			f.append("INDEXES").newline().indent();
			this.appendTableIndex(f, t.index[0]);
			for (int i = 1, c = t.index.length; i < c; i++) {
				f.append(',').newline();
				this.appendTableIndex(f, t.index[i]);
			}
			f.newline().back();
		}
		if (t.relation != null) {
			f.append("RELATIONS").newline().indent();
			f.append(t.relation[0].name.value).append(" TO ").append(
					t.relation[0].target.value).append(" ON ").append(
					t.relation[0].expr);
			for (int i = 1, c = t.index.length; i < c; i++) {
				f.append(',').newline();
				f.append(t.relation[i].name.value).append(" TO ").append(
						t.relation[i].target.value).append(" ON ").append(
						t.relation[i].expr);
			}
			f.newline().back();
		}
		if (t.hierarchy != null) {
			f.append("HIERARCHIES").newline().indent();
			f.append(t.hierarchy[0].name.value).append(" MAXLEVEL(").append(
					t.hierarchy[0].limit.value).append(')');
			for (int i = 1, c = t.index.length; i < c; i++) {
				f.append(',').newline();
				f.append(t.hierarchy[i].name.value).append(" MAXLEVEL(")
						.append(t.hierarchy[i].limit.value).append(')');
			}
			f.newline().back();
		}
		if (t.partition != null) {
			f.append("PARTITION (");
			if (t.partition.fields != null && t.partition.fields.length > 0) {
				f.append(t.partition.fields[0].value);
				for (int i = 1, c = t.partition.fields.length; i < c; i++) {
					f.append(", ").append(t.partition.fields[i].value);
				}
			}
			f.append(')').newline().indent().append("VALVE ").append(
					t.partition.size.value).append(" MAXCOUNT ").append(
					t.partition.limit.value).newline().back();
		}
		f.back().append("END");
	}

	public void visitAbstractTableDeclare(StringFormatter f,
			NAbstractTableDeclare t) {
		f.append("DEFINE ABSTRACT TABLE ").append(t.name.value);
		if (t.base != null) {
			f.append(" EXTEND ").append(t.base.name.value);
		}
		this.appendTableBlock(f, t);
	}

	public void visitTableDeclare(StringFormatter f, NTableDeclare t) {
		f.append("DEFINE TABLE ").append(t.name.value);
		if (t.base != null) {
			f.append(" EXTEND ").append(t.base.name.value);
		}
		this.appendTableBlock(f, t);
	}

	public void visitProcedureDeclare(StringFormatter f, NProcedureDeclare p) {
		f.append("DEFINE PROCEDURE ").append(f.encodeID(p.name.value)).append(
				'(').mark().multiline(p.params, ',').append(')').newline()
				.back().append("BEGIN").newline().indent().multiline(p.stmts,
						StringFormatter.EMPTY_DELIMITER).newline().back()
				.append("END");
	}

	public void visitFunctionDeclare(StringFormatter f, NFunctionDeclare func) {
		f.append("DEFINE FUNCTION ").append(f.encodeID(func.name.value))
				.append('(').mark().multiline(func.params, ',').append(')')
				.space().append(func.returnType.toString()).newline().back()
				.append("BEGIN").newline().indent().multiline(func.stmts,
						StringFormatter.EMPTY_DELIMITER).newline().back()
				.append("END");
	}

	public void visitSegment(StringFormatter f, NSegment s) {
		f.append("BEGIN").newline().indent().multiline(s.stmts,
				StringFormatter.EMPTY_DELIMITER).newline().back().append("END");
	}

	public void visitVarStmt(StringFormatter f, NVarStmt v) {
		f.append("VAR ").append(v.name.value).space().append(v.type.toString());
		if (v.init != null) {
			f.append(" = ").append(v.init);
		}
		f.append(';');
	}

	public void visitAssignStmt(StringFormatter f, NAssignStmt a) {
		if (a.vars.length == 1) {
			f.append(a.vars[0].value);
		} else {
			f.append('(').append(a.vars[0].value);
			for (int i = 1, c = a.vars.length; i < c; i++) {
				f.append(',').space().append(a.vars[i].value);
			}
			f.append(')');
		}
		f.append(" = ");
		if (a.query != null) {
			f.append(a.query);
		} else {
			if (a.values.length == 1) {
				f.append(a.values[0]);
			} else {
				f.append('(').singleline(a.values, ',').append(')');
			}
		}
		f.append(';');
	}

	public void visitIfStmt(StringFormatter f, NIfStmt i) {
		f.append("IF ").append(i.condition).append(" THEN").newline();
		if (i.trueBranch instanceof NSegment) {
			f.append(i.trueBranch);
		} else {
			f.indent().append(i.trueBranch).back();
		}
		if (i.falseBranch != null) {
			f.newline().append("ELSE");
			if (i.falseBranch instanceof NIfStmt) {
				f.space().append(i.falseBranch);
			} else if (i.falseBranch instanceof NSegment) {
				f.newline().append(i.falseBranch);
			} else {
				f.newline().indent().append(i.falseBranch).back();
			}
		}
	}

	public void visitWhileStmt(StringFormatter f, NWhileStmt w) {
		f.append("WHILE ").append(w.condition).append(" LOOP").newline();
		if (w.stmt instanceof NSegment) {
			f.append(w.stmt);
		} else {
			f.indent().append(w.stmt).back();
		}
	}

	public void visitLoopStmt(StringFormatter f, NLoopStmt l) {
		f.append("LOOP").newline();
		if (l.stmt instanceof NSegment) {
			f.append(l.stmt);
		} else {
			f.indent().append(l.stmt).back();
		}
	}

	public void visitForeachStmt(StringFormatter f, NForeachStmt fe) {
		f.append("FOREACH ").append(fe.var.value).append(" IN ");
		if (fe.query != null) {
			f.append('(').append(fe.query).append(')');
		} else {
			f.append(fe.call.name.value).append('(').singleline(fe.call.params,
					',').append(')');
		}
		f.append(" LOOP").newline();
		if (fe.stmt instanceof NSegment) {
			f.append(fe.stmt);
		} else {
			f.indent().append(fe.stmt).back();
		}
	}

	public void visitBreakStmt(StringFormatter f, NBreakStmt b) {
		f.append("BREAK;");
	}

	public void visitPrintStmt(StringFormatter f, NPrintStmt p) {
		f.append("PRINT ").append(p.expr).append(';');
	}

	public void visitReturnStmt(StringFormatter f, NReturnStmt r) {
		f.append("RETURN");
		if (r.expr != null) {
			f.space().append(r.expr);
		}
		f.append(';');
	}
}
