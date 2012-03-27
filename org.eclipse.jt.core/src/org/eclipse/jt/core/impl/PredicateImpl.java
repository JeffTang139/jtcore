package org.eclipse.jt.core.impl;

import java.io.IOException;

import org.eclipse.jt.core.def.exp.Predicate;


/**
 * 谓词实现类
 * 
 * <p>
 * 谓词运算符广义上指表达式运算结果为条件表达式的运算符
 * 
 * @author Jeff Tang
 * 
 */
public enum PredicateImpl implements Predicate {

	LESS_THAN {

		@Override
		protected final void buildCode(CodeBuilder builder, PredicateExpr expr) {
			buildCode(builder, expr, "xLess");
		}

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			renderValues(buffer, expr, usages);
			buffer.lt();
			renderNot(buffer, expr);
		}

	},

	LESS_THAN_OR_EQUAL_TO {

		@Override
		protected void buildCode(CodeBuilder builder, PredicateExpr expr) {
			buildCode(builder, expr, "xLE");
		}

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			renderValues(buffer, expr, usages);
			buffer.le();
			renderNot(buffer, expr);
		}

	},

	GREATER_THAN {

		@Override
		protected void buildCode(CodeBuilder builder, PredicateExpr expr) {
			buildCode(builder, expr, "xGreater");
		}

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			renderValues(buffer, expr, usages);
			buffer.gt();
			renderNot(buffer, expr);
		}

	},

	GREATER_THAN_OR_EQUAL_TO {

		@Override
		protected void buildCode(CodeBuilder builder, PredicateExpr expr) {
			buildCode(builder, expr, "xGE");
		}

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			renderValues(buffer, expr, usages);
			buffer.ge();
			renderNot(buffer, expr);
		}

	},

	EQUAL_TO {

		@Override
		final protected boolean canBeTableRelation() {
			return true;
		}

		@Override
		protected void buildCode(CodeBuilder builder, PredicateExpr expr) {
			buildCode(builder, expr, "xEq");
		}

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			renderValues(buffer, expr, usages);
			if (expr.not) {
				buffer.ne();
			} else {
				buffer.eq();
			}
		}

	},

	NOT_EQUAL_TO {

		@Override
		protected void buildCode(CodeBuilder builder, PredicateExpr expr) {
			buildCode(builder, expr, "xnEq");
		}

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			renderValues(buffer, expr, usages);
			if (expr.not) {
				buffer.eq();
			} else {
				buffer.ne();
			}
		}

	},

	BETWEEN {

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			renderValues(buffer, expr, usages);
			if (expr.not) {
				buffer.predicate(SqlPredicate.NOT_BETWEEN, expr.values.length);
			} else {
				buffer.predicate(SqlPredicate.BETWEEN, expr.values.length);
			}
		}

	},

	BETWEEN_EXCLUDE_LEFT_SIDE {

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			throw Utils.notImplemented();
		}

	},

	BETWEEN_EXCLUDE_RIGHT_SIDE {

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			throw Utils.notImplemented();
		}

	},

	BETWEEN_EXCLUDE_BOTH_SIDES {

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			throw Utils.notImplemented();
		}

	},

	IN {

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			renderValues(buffer, expr, usages);
			if (expr.not) {
				buffer.predicate(SqlPredicate.NOT_IN, expr.values.length);
			} else {
				buffer.predicate(SqlPredicate.IN, expr.values.length);
			}
		}

	},

	STR_LIKE {

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			renderValues(buffer, expr, usages);
			if (expr.not) {
				buffer.predicate(SqlPredicate.NOT_LIKE, expr.values.length);
			} else {
				buffer.predicate(SqlPredicate.LIKE, expr.values.length);
			}
		}

	},

	STR_STARTS_WITH {

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			expr.values[0].render(buffer, usages);
			expr.values[1].render(buffer, usages);
			buffer.loadStr("%");
			buffer.loadStr("#%");
			buffer.func(SqlFunction.REPLACE, 3);
			buffer.loadStr("_");
			buffer.loadStr("#_");
			buffer.func(SqlFunction.REPLACE, 3);
			buffer.loadStr("%");
			buffer.func(SqlFunction.STR_CONCAT, 2);
			buffer.loadStr("#");
			buffer.predicate(SqlPredicate.LIKE, 3);
		}

	},

	STR_ENDS_WITH {

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			expr.values[0].render(buffer, usages);
			buffer.loadStr("%");
			expr.values[1].render(buffer, usages);
			buffer.loadStr("%");
			buffer.loadStr("#%");
			buffer.func(SqlFunction.REPLACE, 3);
			buffer.loadStr("_");
			buffer.loadStr("#_");
			buffer.func(SqlFunction.REPLACE, 3);
			buffer.func(SqlFunction.STR_CONCAT, 2);
			buffer.loadStr("#");
			buffer.predicate(SqlPredicate.LIKE, 3);
		}

	},

	STR_CONTAINS {

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			expr.values[0].render(buffer, usages);
			buffer.loadStr("%");
			expr.values[1].render(buffer, usages);
			buffer.loadStr("%");
			buffer.loadStr("#%");
			buffer.func(SqlFunction.REPLACE, 3);
			buffer.loadStr("_");
			buffer.loadStr("#_");
			buffer.func(SqlFunction.REPLACE, 3);
			buffer.loadStr("%");
			buffer.func(SqlFunction.STR_CONCAT, 3);
			buffer.loadStr("#");
			buffer.predicate(SqlPredicate.LIKE, 3);
		}

	},

	IS_NULL {

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			renderValues(buffer, expr, usages);
			if (expr.not) {
				buffer.predicate(SqlPredicate.IS_NOT_NULL, expr.values.length);
			} else {
				buffer.predicate(SqlPredicate.IS_NULL, expr.values.length);
			}
		}

	},

	IS_NOT_NULL {

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			renderValues(buffer, expr, usages);
			if (expr.not) {
				buffer.predicate(SqlPredicate.IS_NULL, expr.values.length);
			} else {
				buffer.predicate(SqlPredicate.IS_NOT_NULL, expr.values.length);
			}
		}
	},

	EXISTS {

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			renderValues(buffer, expr, usages);
			buffer.predicate(SqlPredicate.EXISTS, expr.values.length);
			if (expr.not) {
				buffer.not();
			}
		}
	},

	NOT_EXISTS {

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			renderValues(buffer, expr, usages);
			buffer.predicate(SqlPredicate.EXISTS, expr.values.length);
			if (!expr.not) {
				buffer.not();
			}

		}

	},

	IS_CHILD_OF {

		@Override
		protected void checkValues(ValueExpr[] exprs) {
			Expr.checkHierarchyPathValue(exprs[0]);
			Expr.checkHierarchyPathValue(exprs[1]);
		}

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			throw Utils.notImplemented();
		}

	},

	IS_CHILD_OF_OR_SELF {

		@Override
		protected void checkValues(ValueExpr[] exprs) {
			Expr.checkHierarchyPathValue(exprs[0]);
			Expr.checkHierarchyPathValue(exprs[1]);
		}

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			throw Utils.notImplemented();
		}
	},

	IS_DESCENDANT_OF {

		@Override
		protected void checkValues(ValueExpr[] exprs) {
			Expr.checkHierarchyPathValue(exprs[0]);
			Expr.checkHierarchyPathValue(exprs[1]);
		}

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			throw Utils.notImplemented();
		}
	},

	IS_DESCENDANT_OF_OR_SELF {

		@Override
		protected void checkValues(ValueExpr[] exprs) {
			Expr.checkHierarchyPathValue(exprs[0]);
			Expr.checkHierarchyPathValue(exprs[1]);
		}

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			throw Utils.notImplemented();
		}
	},

	IS_RELATIVE_DESCENDANT_OF {

		@Override
		protected void checkValues(ValueExpr[] exprs) {
			Expr.checkHierarchyPathValue(exprs[0]);
			Expr.checkHierarchyPathValue(exprs[1]);
			Expr.checkNonDecimalNumber(this.toString(), exprs[2]);
		}

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			throw Utils.notImplemented();
		}
	},

	IS_RELATIVE_DESCENDANT_OF_OR_SELF {

		@Override
		protected void checkValues(ValueExpr[] exprs) {
			Expr.checkHierarchyPathValue(exprs[0]);
			Expr.checkHierarchyPathValue(exprs[1]);
			Expr.checkNonDecimalNumber(this.toString(), exprs[2]);
		}

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			throw Utils.notImplemented();
		}
	},

	IS_RANGE_DESCENDANT_OF {

		@Override
		protected void checkValues(ValueExpr[] exprs) {
			Expr.checkHierarchyPathValue(exprs[0]);
			Expr.checkHierarchyPathValue(exprs[1]);
			Expr.checkNonDecimalNumber(this.toString(), exprs[2]);
		}

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			throw Utils.notImplemented();
		}
	},

	IS_RANGE_DESCENDANT_OF_OR_SELF {

		@Override
		protected void checkValues(ValueExpr[] exprs) {
			Expr.checkHierarchyPathValue(exprs[0]);
			Expr.checkHierarchyPathValue(exprs[1]);
			Expr.checkNonDecimalNumber(this.toString(), exprs[2]);
		}

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			throw Utils.notImplemented();
		}
	},

	IS_PARENT_OF {

		@Override
		protected void checkValues(ValueExpr[] exprs) {
			Expr.checkHierarchyPathValue(exprs[0]);
			Expr.checkHierarchyPathValue(exprs[1]);
		}

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			throw Utils.notImplemented();
		}
	},

	IS_RELATIVE_ANCESTOR_OF {

		@Override
		protected void checkValues(ValueExpr[] exprs) {
			Expr.checkHierarchyPathValue(exprs[0]);
			Expr.checkHierarchyPathValue(exprs[1]);
			Expr.checkNonDecimalNumber(this.toString(), exprs[2]);
		}

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			throw Utils.notImplemented();
		}
	};

	/**
	 * 返回可否是表连接中的关系
	 * 
	 * @return 返回能否
	 */
	protected boolean canBeTableRelation() {
		return false;
	}

	protected void checkValues(ValueExpr[] values) {
	}

	protected void buildCode(CodeBuilder builder, PredicateExpr expr) {
		throw new UnsupportedOperationException("表达式[" + this.name()
				+ "]不支持代码生成.");
	}

	static final void buildCode(CodeBuilder builder, PredicateExpr expr,
			String method) {
		try {
			expr.values[0].visit(builder, null);
			builder.append('.');
			builder.append(method);
			builder.append('(');
			for (int i = 1; i < expr.values.length; i++) {
				if (i > 1) {
					builder.append(",");
				}
				expr.values[i].visit(builder, null);
			}
			builder.append(')');
		} catch (IOException e) {
			Utils.tryThrowException(e);
		}

	}

	abstract void render(ISqlExprBuffer buffer, PredicateExpr expr,
			TableUsages usages);

	private static final void renderValues(ISqlExprBuffer buffer,
			PredicateExpr expr, TableUsages usages) {
		for (int i = 0; i < expr.values.length; i++) {
			expr.values[i].render(buffer, usages);
		}
	}

	private static final void renderNot(ISqlExprBuffer buffer,
			PredicateExpr expr) {
		if (expr.not) {
			buffer.not();
		}
	}

}
