package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.da.SQLFuncSpec.SQLFuncPattern;
import org.eclipse.jt.core.def.arg.ArgumentDeclare;
import org.eclipse.jt.core.def.arg.ArgumentableDeclare;
import org.eclipse.jt.core.def.exp.ConditionalExpression;
import org.eclipse.jt.core.def.exp.TableFieldRefExpr;
import org.eclipse.jt.core.def.exp.ValueExpression;
import org.eclipse.jt.core.def.query.QuRelationRefDeclare;
import org.eclipse.jt.core.def.query.RelationColumnDefine;
import org.eclipse.jt.core.def.query.SQLFunc;
import org.eclipse.jt.core.impl.NValueExpr.ResultType;
import org.eclipse.jt.core.spi.sql.SQLAliasUndefinedException;
import org.eclipse.jt.core.spi.sql.SQLColumnNotFoundException;
import org.eclipse.jt.core.spi.sql.SQLHierarchyNotFoundException;
import org.eclipse.jt.core.spi.sql.SQLNotSupportedException;
import org.eclipse.jt.core.spi.sql.SQLParseException;
import org.eclipse.jt.core.spi.sql.SQLSyntaxException;
import org.eclipse.jt.core.spi.sql.SQLVariableUndefinedException;
import org.eclipse.jt.core.type.DataType;

class SQLExprVisitor extends VisitorBase<SQLExprContext> {
	final static SQLExprVisitor VISITOR = new SQLExprVisitor();

	@Override
	public void visitLiteralBoolean(SQLExprContext visitorContext,
			NLiteralBoolean b) {
		visitorContext.valueExpr = BooleanConstExpr.valueOf(b.value);
	}

	@Override
	public void visitLiteralInt(SQLExprContext visitorContext, NLiteralInt i) {
		if (visitorContext.restrict && i.value != 0 && i.value != 1) {
			throw new SQLNotSupportedException(i.startLine(), i.startCol(),
					"��ǰ���������ò�����ʹ��������");
		}
		visitorContext.valueExpr = IntConstExpr.valueOf(i.value);
	}

	@Override
	public void visitLiteralLong(SQLExprContext visitorContext, NLiteralLong l) {
		if (visitorContext.restrict && l.value != 0 && l.value != 1) {
			throw new SQLNotSupportedException(l.startLine(), l.startCol(),
					"��ǰ���������ò�����ʹ��������");
		}
		visitorContext.valueExpr = LongConstExpr.valueOf(l.value);
	}

	@Override
	public void visitLiteralDouble(SQLExprContext visitorContext,
			NLiteralDouble d) {
		if (visitorContext.restrict && d.value != 0 && d.value != 1) {
			throw new SQLNotSupportedException(d.startLine(), d.startCol(),
					"��ǰ���������ò�����ʹ��������");
		}
		visitorContext.valueExpr = DoubleConstExpr.valueOf(d.value);
	}

	@Override
	public void visitLiteralString(SQLExprContext visitorContext,
			NLiteralString s) {
		if (visitorContext.restrict) {
			throw new SQLNotSupportedException(s.startLine(), s.startCol(),
					"��ǰ���������ò�����ʹ��������");
		}
		visitorContext.valueExpr = StringConstExpr.valueOf(s.value);
	}

	@Override
	public void visitLiteralDate(SQLExprContext visitorContext, NLiteralDate d) {
		if (visitorContext.restrict) {
			throw new SQLNotSupportedException(d.startLine(), d.startCol(),
					"��ǰ���������ò�����ʹ��������");
		}
		visitorContext.valueExpr = DateConstExpr.valueOf(d.value);
	}

	@Override
	public void visitLiteralBytes(SQLExprContext visitorContext, NLiteralBytes b) {
		if (visitorContext.restrict) {
			throw new SQLNotSupportedException(b.startLine(), b.startCol(),
					"��ǰ���������ò�����ʹ��������");
		}
		visitorContext.valueExpr = BytesConstExpr.valueOf(b.value);
	}

	@Override
	public void visitLiteralGUID(SQLExprContext visitorContext, NLiteralGUID g) {
		if (visitorContext.restrict) {
			throw new SQLNotSupportedException(g.startLine(), g.startCol(),
					"��ǰ���������ò�����ʹ��������");
		}
		visitorContext.valueExpr = GUIDConstExpr.valueOf(g.value);
	}

	@Override
	public void visitBinaryExpr(SQLExprContext visitorContext, NBinaryExpr e) {
		ValueExpression l = visitorContext.build(this, e.left);
		ValueExpression r = visitorContext.build(this, e.right);
		switch (e.op) {
		case ADD:
			switch (ResultType.valueOf(l.getType())) {
			case STRING:
				visitorContext.valueExpr = l.xStrConcat(r);
				break;
			case NUMBER:
				visitorContext.valueExpr = l.xAdd(r);
				break;
			case ENUM:
				switch (ResultType.valueOf(r.getType())) {
				case STRING:
					visitorContext.valueExpr = l.xStrConcat(r);
					break;
				case NUMBER:
					visitorContext.valueExpr = l.xAdd(r);
					break;
				}
				break;
			default:
				throw new SQLNotSupportedException(e.left.startLine(), e.left
						.startCol(), "'+'���������Ӧ���ڸ����͵Ĳ�����");
			}
			break;
		case SUB:
			visitorContext.valueExpr = l.xSub(r);
			break;
		case MUL:
			visitorContext.valueExpr = l.xMul(r);
			break;
		case DIV:
			visitorContext.valueExpr = l.xDiv(r);
			break;
		case MOD:
			visitorContext.valueExpr = l.xMod(r);
			break;
		case COMBINE:
			visitorContext.valueExpr = l.xBinConcat(r);
			break;
		}
	}

	@Override
	public void visitAggregateExpr(SQLExprContext visitorContext,
			NAggregateExpr e) {
		if (!visitorContext.canUseSetFunc) {
			throw new SQLNotSupportedException(e.startLine(), e.startCol(),
					"�˴�����ʹ�þۺϺ���");
		}
		if (e.quantifier == SetQuantifier.DISTINCT
				&& !visitorContext.canUseSetFuncWithDistinct) {
			throw new SQLNotSupportedException(e.startLine(), e.startCol(),
					"�˴�����ʹ��distinct����");
		}
		ValueExpression value = null;
		if (e.expr != null) {
			value = visitorContext.build(this, e.expr);
		}
		switch (e.func) {
		case COUNT:
			if (value != null) {
				visitorContext.valueExpr = e.quantifier == SetQuantifier.DISTINCT ? SQLFunc
						.xCountDistinct(value)
						: SQLFunc.xCount(value);
			} else {
				visitorContext.valueExpr = SQLFunc.xCount();
			}
			break;
		case AVG:
			visitorContext.valueExpr = e.quantifier == SetQuantifier.DISTINCT ? SQLFunc
					.xAvgDistinct(value)
					: SQLFunc.xAvg(value);
			break;
		case SUM:
			visitorContext.valueExpr = e.quantifier == SetQuantifier.DISTINCT ? SQLFunc
					.xSumDistinct(value)
					: SQLFunc.xSum(value);
			break;
		case MAX:
			visitorContext.valueExpr = SQLFunc.xMax(value);
			break;
		case MIN:
			visitorContext.valueExpr = SQLFunc.xMin(value);
			break;
		}
	}

	@Override
	public void visitCoalesceExpr(SQLExprContext visitorContext, NCoalesceExpr e) {
		int len = e.params.length;
		ValueExpression first = visitorContext.build(this, e.params[0]);
		if (len < 2) {
			throw new SQLSyntaxException(e.startLine(), e.startCol(),
					"COALESCE����Ӧ������������");
		}
		ValueExpression second = visitorContext.build(this, e.params[1]);
		if (len == 2) {
			visitorContext.valueExpr = first.xCoalesce(second);
		} else {
			Object[] list = new ValueExpression[len - 2];
			for (int i = 2; i < len; i++) {
				list[i - 2] = visitorContext.build(this, e.params[i]);
			}
			visitorContext.valueExpr = first.xCoalesce(second, list);
		}
	}

	@Override
	public void visitColumnRefExpr(SQLExprContext visitorContext,
			NColumnRefExpr e) {
		if (visitorContext.resolver == null) {
			throw new SQLNotSupportedException(e.startLine(), e.startCol(),
					"�˴�����ʹ���ֶ�����");
		}
		SQLColumnProvider provider = visitorContext.resolver.findProvider(
				SQLColumnProvider.class, e.source.value);
		if (provider == null) {
			throw new SQLAliasUndefinedException(e.source.line, e.source.col,
					e.source.value);
		}
		RelationColumnDefine c = provider.findColumn(e.field.value);
		if (c == null) {
			throw new SQLColumnNotFoundException(e.field.line, e.field.col,
					e.field.value);
		}
		visitorContext.valueExpr = provider.expOf(c);
	}

	@Override
	public void visitFunctionExpr(SQLExprContext visitorContext, NFunctionExpr e) {
		try {
			if (e.params != null && e.params.length > 0) {
				int len = e.params.length;
				ValueExpression[] values = new ValueExpression[len];
				DataType[] types = new DataType[len];
				for (int i = 0; i < len; i++) {
					e.params[i].accept(visitorContext, this);
					values[i] = visitorContext.valueExpr;
					types[i] = visitorContext.valueExpr.getType();
				}
				SQLFuncPattern pattern = e.func.accept(types);
				if (pattern == null) {
					StringBuilder sb = new StringBuilder();
					for (int i = 0, c = len - 1; i < c; i++) {
						sb.append(types[i].toString());
						sb.append(", ");
					}
					sb.append(types[len - 1].toString());
					throw new SQLNotSupportedException(e.startLine(), e
							.startCol(), "����'" + e.func.name() + "'�����ܲ�������Ϊ("
							+ sb.toString() + ")�ĵ���");
				}
				visitorContext.valueExpr = pattern.expOf(values);
			} else {
				SQLFuncPattern pattern = e.func
						.accept(NFunctionExpr.EMPTY_DATATYPE);
				if (pattern == null) {
					throw new SQLNotSupportedException(e.startLine(), e
							.startCol(), "����'" + e.func.name() + "'�������޲����ĵ���");
				}
				visitorContext.valueExpr = pattern.expOf(null);
			}
		} catch (SQLParseException ex) {
			ex.line = e.startLine();
			ex.col = e.startCol();
			throw ex;
		}
	}

	@Override
	public void visitHaidExpr(SQLExprContext visitorContext, NHaidExpr e) {
		if (visitorContext.resolver == null) {
			throw new SQLNotSupportedException(e.startLine(), e.startCol(),
					"�˴�����ʹ�ü��κ���");
		}
		SQLSourceProvider provider = visitorContext.resolver.findProvider(
				SQLSourceProvider.class, e.source.value);
		if (provider == null) {
			throw new SQLAliasUndefinedException(e.source.line, e.source.col,
					e.source.value);
		}
		String name = e.path.value;
		// ���ҹ�ϵ����
		if (provider instanceof SQLQuRelationRefProvider) {
			QuRelationRef ref = ((SQLQuRelationRefProvider) provider)
					.getQuRelationRef();
			HierarchyDefineImpl hier = provider.findHierarchy(name);
			if (hier != null) {
				if (e.offset != null) {
					e.offset.accept(visitorContext, this);
					visitorContext.valueExpr = e.relOrAbs ? ref.xAncestorRECID(
							hier, visitorContext.valueExpr) : ref
							.xAncestorRECIDOfLevel(hier,
									visitorContext.valueExpr);
				} else {
					visitorContext.valueExpr = ref.xParentRECID(hier);
				}
				return;
			}
		}
		// �����ֶ�����
		RelationColumnDefine c = provider.findColumn(name);
		if (c != null) {
			ValueExpression expr = provider.expOf(c);
			if (expr instanceof TableFieldRefExpr) {
				TableFieldRefExpr f = (TableFieldRefExpr) expr;
				if (e.offset != null) {
					e.offset.accept(visitorContext, this);
					visitorContext.valueExpr = e.relOrAbs ? f
							.xAncestorRECID(visitorContext.valueExpr) : f
							.xAncestorRECIDOfLevel(visitorContext.valueExpr);
				} else {
					visitorContext.valueExpr = f.xParentRECID();
				}
				return;
			}
		}
		throw new SQLNotSupportedException(e.path.line, e.path.col, "�Ҳ�������Ϊ'"
				+ name + "'�Ĺ�ϵ�����ֶ�");
	}

	@Override
	public void visitHlvExpr(SQLExprContext visitorContext, NHlvExpr e) {
		if (visitorContext.resolver == null) {
			throw new SQLNotSupportedException(e.startLine(), e.startCol(),
					"�˴�����ʹ�ü��κ���");
		}
		SQLSourceProvider provider = visitorContext.resolver.findProvider(
				SQLSourceProvider.class, e.source.value);
		if (provider == null) {
			throw new SQLAliasUndefinedException(e.source.line, e.source.col,
					e.source.value);
		}
		String name = e.path.value;
		// ���ڼ��ζ����в���path
		if (provider instanceof SQLQuRelationRefProvider) {
			HierarchyDefineImpl hier = provider.findHierarchy(name);
			if (hier != null) {
				visitorContext.valueExpr = ((SQLQuRelationRefProvider) provider)
						.getQuRelationRef().xLevelOf(hier);
				return;
			}
		}
		// �ٲ����ֶ�����
		RelationColumnDefine c = provider.findColumn(name);
		if (c != null) {
			ValueExpression expr = provider.expOf(c);
			if (expr instanceof TableFieldRefExpr) {
				visitorContext.valueExpr = ((TableFieldRefExpr) expr)
						.xLevelOf();
				return;
			}
		}
		throw new SQLNotSupportedException(e.path.line, e.path.col, "�Ҳ�������Ϊ'"
				+ name + "'�Ĺ�ϵ�����ֶ�");
	}

	@Override
	public void visitNegativeExpr(SQLExprContext visitorContext, NNegativeExpr e) {
		try {
			e.left.accept(visitorContext, this);
			visitorContext.valueExpr = visitorContext.valueExpr.xMinus();
		} catch (Throwable ex) {
			throw new SQLNotSupportedException(e.left.startLine(), e.left
					.startCol(), ex.getMessage());
		}
	}

	@Override
	public void visitNullExpr(SQLExprContext visitorContext, NNullExpr e) {
		visitorContext.valueExpr = ValueExpression.NULL;
	}

	@Override
	public void visitSearchedCaseExpr(SQLExprContext visitorContext,
			NSearchedCaseExpr e) {
		if (e.branches == null || e.branches.length == 0) {
			throw new SQLNotSupportedException(e.startLine(), e.startCol(),
					"ȱ�ٷ�֧");
		}
		NSearchedCaseWhen w = e.branches[0];
		ConditionalExpression exp = visitorContext.build(this, w.condition);
		ValueExpression r = visitorContext.build(this, w.returnValue);
		int l = (e.branches.length - 1) * 2;
		if (e.elseBranch != null) {
			l++;
		}
		if (l == 0) {
			visitorContext.valueExpr = exp.searchedCase(r);
		} else {
			Object[] others = new Object[l];
			int j = 0;
			for (int i = 1, c = e.branches.length; i < c; i++) {
				w = e.branches[i];
				others[j++] = visitorContext.build(this, w.condition);
				others[j++] = visitorContext.build(this, w.returnValue);
			}
			if (e.elseBranch != null) {
				others[j] = visitorContext.build(this, e.elseBranch);
			}
			visitorContext.valueExpr = exp.searchedCase(r, others);
		}
	}

	@Override
	public void visitSimpleCaseExpr(SQLExprContext visitorContext,
			NSimpleCaseExpr e) {
		if (e.branches == null || e.branches.length == 0) {
			throw new SQLSyntaxException(e.startLine(), e.startCol(),
					"ȱ��WHEN-THEN���ʽ");
		}
		e.value.accept(visitorContext, this);
		ValueExpression value = visitorContext.build(this, e.value);
		ValueExpression first = visitorContext.build(this, e.branches[0].value);
		ValueExpression firstRet = visitorContext.build(this,
				e.branches[0].returnValue);
		int len = (e.branches.length - 1) * 2;
		if (e.elseBranch != null) {
			len++;
		}
		if (len == 0) {
			visitorContext.valueExpr = value.xSimpleCase(first, firstRet);
		} else {
			Object[] others = new Object[len];
			for (int i = 1, j = 0, c = e.branches.length; i < c; i++) {
				others[j++] = visitorContext.build(this, e.branches[i].value);
				others[j++] = visitorContext.build(this,
						e.branches[i].returnValue);
			}
			if (e.elseBranch != null) {
				others[len - 1] = visitorContext.build(this, e.elseBranch);
			}
			visitorContext.valueExpr = value.xSimpleCase(first, firstRet,
					others);
		}
	}

	@Override
	public void visitVarRefExpr(SQLExprContext visitorContext, NVarRefExpr e) {
		ArgumentableDeclare a = (ArgumentableDeclare) visitorContext.rootStmt;
		if (a == null) {
			throw new SQLVariableUndefinedException(e.startLine(),
					e.startCol(), e.argumentName);
		}
		ArgumentDeclare arg = a.getArguments().find(e.argumentName);
		if (arg == null) {
			throw new SQLVariableUndefinedException(e.startLine(),
					e.startCol(), e.argumentName);
		}
		visitorContext.valueExpr = ValueExpr.expOf(arg);
	}

	@Override
	public void visitQuerySpecific(SQLExprContext visitorContext,
			NQuerySpecific s) {
		if (s.select.columns.length != 1) {
			throw new SQLNotSupportedException(s.select.startLine(), s.select
					.startCol(), "����������Ӳ�ѯֻ�ܰ���һ��");
		}
		SubQueryImpl sub = visitorContext.newSubQuery();
		new SQLQueryContext(visitorContext).build(sub, s,
				visitorContext.resolver);
		visitorContext.valueExpr = sub.newExpression();
	}

	@Override
	public void visitCompareExpr(SQLExprContext visitorContext, NCompareExpr e) {
		ValueExpression left = visitorContext.build(e.left);
		ValueExpression right = visitorContext.build(e.right);
		try {
			switch (e.op) {
			case EQ:
				visitorContext.conditionalExpr = left.xEq(right);
				break;
			case NE:
				visitorContext.conditionalExpr = left.xnEq(right);
				break;
			case GE:
				visitorContext.conditionalExpr = left.xGE(right);
				break;
			case GT:
				visitorContext.conditionalExpr = left.xGreater(right);
				break;
			case LE:
				visitorContext.conditionalExpr = left.xLE(right);
				break;
			case LT:
				visitorContext.conditionalExpr = left.xLess(right);
				break;
			}
		} catch (Throwable ex) {
			throw new SQLNotSupportedException(e.left.endLine(), e.left
					.endCol(), ex.getMessage());
		}
	}

	@Override
	public void visitBetweenExpr(SQLExprContext visitorContext, NBetweenExpr e) {
		ValueExpression value = visitorContext.build(e.value);
		ValueExpression left = visitorContext.build(e.left);
		ValueExpression right = visitorContext.build(e.right);
		try {
			visitorContext.conditionalExpr = e.not ? value.xnBtwn(left, right)
					: value.xBtwn(left, right);
		} catch (Throwable ex) {
			throw new SQLNotSupportedException(e.value.endLine(), e.value
					.endCol(), ex.getMessage());
		}
	}

	@Override
	public void visitExistsExpr(SQLExprContext visitorContext, NExistsExpr e) {
		SubQueryImpl sub = visitorContext.newSubQuery();
		new SQLQueryContext(visitorContext).build(sub, e.query,
				visitorContext.resolver);
		visitorContext.conditionalExpr = sub.exists();
	}

	@Override
	public void visitHierarchyExpr(SQLExprContext visitorContext,
			NHierarchyExpr e) {
		SQLSourceProvider lp = visitorContext.resolver.findProvider(
				SQLSourceProvider.class, e.left.value);
		if (lp == null) {
			throw new SQLAliasUndefinedException(e.left.line, e.left.col,
					e.left.value);
		}
		if (!(lp instanceof SQLQuRelationRefProvider)) {
			throw new SQLNotSupportedException(e.startLine(), e.startCol(),
					"ֻ֧���ڲ�ѯ�����ʹ�ü���ν��");
		}
		QuRelationRefDeclare left = ((SQLQuRelationRefProvider) lp)
				.getQuRelationRef();
		HierarchyDefineImpl hier = lp.findHierarchy(e.rel.value);
		if (hier == null) {
			throw new SQLHierarchyNotFoundException(e.rel.line, e.rel.col,
					e.rel.value);
		}
		SQLQuRelationRefProvider rp = visitorContext.resolver.findProvider(
				SQLQuRelationRefProvider.class, e.right.value);
		if (rp == null) {
			throw new SQLAliasUndefinedException(e.right.line, e.right.col,
					e.right.value);
		}
		QuRelationRefDeclare right = rp.getQuRelationRef();
		if (!(left.getTarget() instanceof TableDefineImpl)) {
			throw new SQLNotSupportedException("ֻ֧�ֶԱ�ʹ�ü���ν��");
		}
		switch (e.keyword) {
		case CHILDOF:
			visitorContext.conditionalExpr = left.xIsChildOf(hier, right);
			break;
		case PARENTOF:
			visitorContext.conditionalExpr = left.xIsParentOf(hier, right);
			break;
		case ANCESTOROF:
			visitorContext.conditionalExpr = left.xIsAncestorOf(hier, right);
			break;
		case DESCENDANTOF:
			NDescendantOfExpr d = (NDescendantOfExpr) e;
			if (d.diff != null) {
				ValueExpression diff = visitorContext.build(d.diff);
				if (d.leOrEq) {
					visitorContext.conditionalExpr = left.xIsDescendantOf(hier,
							right, diff);
				} else {
					visitorContext.conditionalExpr = left.xIsDescendantOf(hier,
							right, diff);
				}
			} else {
				visitorContext.conditionalExpr = left.xIsDescendantOf(hier,
						right);
			}
			break;
		case UNKNOWN:
			throw new SQLSyntaxException();
		}
	}

	@Override
	public void visitInExpr(SQLExprContext visitorContext, NInExpr e) {
		ValueExpression value = visitorContext.build(e.value);
		if (e.param instanceof NInParamValueList) {
			NInParamValueList p = (NInParamValueList) e.param;
			if (p.values.length == 0) {
				throw new SQLNotSupportedException(p.startLine(), p.startCol(),
						"INֵ�б�������Ӧ����һ��ֵ");
			}
			ValueExpression[] list = new ValueExpression[p.values.length];
			for (int i = 0, c = list.length; i < c; i++) {
				list[i] = visitorContext.build(p.values[i]);
			}
			visitorContext.conditionalExpr = e.not ? value.xnIn(list) : value
					.xIn(list);
		} else if (e.param instanceof NInParamSubQuery) {
			SubQueryImpl sub = visitorContext.newSubQuery();
			NInParamSubQuery p = (NInParamSubQuery) e.param;
			new SQLQueryContext(visitorContext).build(sub, p.query,
					visitorContext.resolver);
			if (sub.columns.size() != 1) {
				throw new SQLNotSupportedException(p.startLine(), p.startCol(),
						"IN�Ӳ�ѯӦ����һ��");
			}
			visitorContext.conditionalExpr = e.not ? value.xnIn(sub) : value
					.xIn(sub);
		} else {
			throw new SQLNotSupportedException(e.param.startLine(), e.param
					.startCol(), "��֧�ֵĲ���������");
		}
	}

	@Override
	public void visitIsLeafExpr(SQLExprContext visitorContext, NIsLeafExpr e) {
		SQLSourceProvider provider = visitorContext.resolver.findProvider(
				SQLSourceProvider.class, e.left.value);
		if (provider == null) {
			throw new SQLAliasUndefinedException(e.left.line, e.left.col,
					e.left.value);
		}
		if (!(provider instanceof SQLQuRelationRefProvider)) {
			throw new SQLNotSupportedException(e.left.line, e.left.col,
					"ֻ֧���ڲ�ѯ��ʹ�ü���ν��");
		}
		HierarchyDefineImpl hier = provider.findHierarchy(e.hier.value);
		if (hier == null) {
			throw new SQLHierarchyNotFoundException(e.hier.line, e.hier.col,
					e.hier.value);
		}
		visitorContext.conditionalExpr = ((SQLQuRelationRefProvider) provider)
				.getQuRelationRef().xIsLeaf(hier);
	}

	@Override
	public void visitIsNullExpr(SQLExprContext visitorContext, NIsNullExpr e) {
		ValueExpression value = visitorContext.build(e.value);
		visitorContext.conditionalExpr = e.not ? value.xnNull() : value
				.xIsNull();
	}

	@Override
	public void visitLogicalExpr(SQLExprContext visitorContext, NLogicalExpr e) {
		ConditionalExpression left = visitorContext.build(this, e.left);
		ConditionalExpression right = e.right != null ? visitorContext.build(
				this, e.right) : null;
		switch (e.op) {
		case AND:
			visitorContext.conditionalExpr = left.and(right);
			break;
		case NOT:
			visitorContext.conditionalExpr = left.not();
			break;
		case OR:
			visitorContext.conditionalExpr = left.or(right);
			break;
		}
	}

	@Override
	public void visitPathExpr(SQLExprContext visitorContext, NPathExpr e) {
		TableFieldRefExpr left = this.getFieldRef(visitorContext, e.t1, e.f1);
		TableFieldRefExpr right = this.getFieldRef(visitorContext, e.t2, e.f2);
		switch (e.keyword) {
		case CHILDOF:
			visitorContext.conditionalExpr = left.xIsChildOf(right);
			break;
		case PARENTOF:
			visitorContext.conditionalExpr = left.xIsParentOf(right);
			break;
		case ANCESTOROF:
			if (e.diff == null) {
				throw new SQLSyntaxException(e.startLine(), e.startCol(),
						"ȱ����Լ���");
			}
			ValueExpression diff = visitorContext.build(e.diff);
			visitorContext.conditionalExpr = left.xIsRelativeAncestorOf(right,
					diff);
			break;
		case DESCENDANTOF:
			visitorContext.conditionalExpr = left.xIsDescendantOf(right);
			break;
		default:
			throw new IllegalStateException();
		}
	}

	private TableFieldRefExpr getFieldRef(SQLExprContext visitorContext,
			TString t, TString f) {
		SQLColumnProvider provider = visitorContext.resolver.findProvider(
				SQLColumnProvider.class, t.value);
		if (provider == null) {
			throw new SQLAliasUndefinedException(t.line, t.col, t.value);
		}
		RelationColumnDefine c = provider.findColumn(f.value);
		if (c == null) {
			throw new SQLColumnNotFoundException(f.line, f.col, f.value);
		}
		ValueExpression expr = provider.expOf(c);
		if (!(expr instanceof TableFieldRefExpr)) {
			throw new SQLNotSupportedException(f.line, f.col, "����ν��ֻ֧�����Ա���ֶ�");
		}
		return ((TableFieldRefExpr) expr);
	}

	@Override
	public void visitStrCompareExpr(SQLExprContext visitorContext,
			NStrCompareExpr e) {
		ValueExpression first = visitorContext.build(e.first);
		ValueExpression second = visitorContext.build(e.second);
		switch (e.keyword) {
		case STARTS_WITH:
			visitorContext.conditionalExpr = e.not ? first.xStartW(second)
					.not() : first.xStartW(second);
			break;
		case ENDS_WITH:
			visitorContext.conditionalExpr = e.not ? first.xEndW(second).not()
					: first.xEndW(second);
			break;
		case CONTAINS:
			visitorContext.conditionalExpr = e.not ? first.xnContain(second)
					: first.xContain(second);
			break;
		case LIKE:
			NLikeExpr l = (NLikeExpr) e;
			if (l.escape != null) {
				ValueExpression third = visitorContext.build(l.escape);
				visitorContext.conditionalExpr = l.not ? first.xnLike(second,
						third) : first.xLike(second, third);
			} else {
				visitorContext.conditionalExpr = e.not ? first.xnLike(second)
						: first.xLike(second);
			}
			break;
		}
	}
}
