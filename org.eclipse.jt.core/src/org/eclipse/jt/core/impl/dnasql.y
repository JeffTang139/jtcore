%{
import java.io.Reader;

import org.eclipse.jt.core.da.SQLFuncSpec;
import org.eclipse.jt.core.def.DNASqlType;
import org.eclipse.jt.core.def.table.TableJoinType;
import org.eclipse.jt.core.def.query.GroupByType;
import org.eclipse.jt.core.spi.sql.*;
%}

%token ID	/*名称：表名，字段名，函数名*/
%token VAR_REF	/*变量名：参数*/
%token ENV_REF
%token INT_VAL	/*数值*/
%token LONG_VAL
%token DOUBLE_VAL
%token STR_VAL	/*字符串*/
%token LE	/*小于等于*/
%token GE	/*大于等于*/
%token NE	/*不等于*/
%token CB	/*连接*/
%token IF
%token COMMENT	/*注释*/
%token WHITESPACE	/*空白：空格、回车、换行*/
%nonassoc IFX
%nonassoc ELSE

%%

script:
	declare_stmt {
			$$ = new SQLScript((NStatement)$1, this.out);
		}
	;

declare_stmt:
	query_declare { $$ = $1; }
	| orm_declare { $$ = $1; }
	| insert_declare { $$ = $1; }
	| update_declare { $$ = $1; }
	| delete_declare { $$ = $1; }
	| table_declare { $$ = $1; }
	| procedure_declare { $$ = $1; }
	| function_declare { $$ = $1; }
	| declare_error { $$ = $1; }
	;

declare_error:
	'DEFINE' error {
			after($1, new SQLSyntaxException("无法识别的声明语句"));
			$$ = NStatement.EMPTY;
		}
	;

/* declare */
 
param_declare:
	VAR_REF param_type param_not_null param_default {
			$$ = new NParamDeclare(NParamDeclare.InOut.IN, (TString)$1,
					(NDataType)$2, true, (NLiteral)$4);
		}
	| VAR_REF param_type param_default param_not_null {
			$$ = new NParamDeclare(NParamDeclare.InOut.IN, (TString)$1,
					(NDataType)$2, true, (NLiteral)$3);
		}
	| VAR_REF param_type param_default {
			$$ = new NParamDeclare(NParamDeclare.InOut.IN, (TString)$1,
					(NDataType)$2, false, (NLiteral)$3);
		}
	| VAR_REF param_type param_not_null {
			$$ = new NParamDeclare(NParamDeclare.InOut.IN, (TString)$1,
					(NDataType)$2, true, null);
		}
	| VAR_REF param_type {
			$$ = new NParamDeclare(NParamDeclare.InOut.IN, (TString)$1,
					(NDataType)$2, false, null);
		}
	| 'INOUT' VAR_REF param_type param_not_null param_default {
			$$ = new NParamDeclare(NParamDeclare.InOut.INOUT, (TString)$2,
					(NDataType)$3, true, (NLiteral)$5);
		}
	| 'INOUT' VAR_REF param_type param_default param_not_null {
			$$ = new NParamDeclare(NParamDeclare.InOut.INOUT, (TString)$2,
					(NDataType)$3, true, (NLiteral)$4);
		}
	| 'INOUT' VAR_REF param_type param_default {
			$$ = new NParamDeclare(NParamDeclare.InOut.INOUT, (TString)$2,
					(NDataType)$3, false, (NLiteral)$4);
		}
	| 'INOUT' VAR_REF param_type param_not_null {
			$$ = new NParamDeclare(NParamDeclare.InOut.INOUT, (TString)$2,
					(NDataType)$3, true, null);
		}
	| 'INOUT' VAR_REF param_type {
			$$ = new NParamDeclare(NParamDeclare.InOut.INOUT, (TString)$2,
					(NDataType)$3, false, null);
		}
	| 'OUT' VAR_REF param_type {
			$$ = new NParamDeclare(NParamDeclare.InOut.OUT, (TString)$2,
					(NDataType)$3, false, null);
		}
	| 'OUT' VAR_REF param_type param_not_null {
			$$ = new NParamDeclare(NParamDeclare.InOut.OUT, (TString)$2,
					(NDataType)$3, true, null);
		}
	/* error */
	| VAR_REF error {
			after($1, new SQLSyntaxException("缺少参数类型"));
			$$ = NParamDeclare.EMPTY;
		}
	| 'INOUT' VAR_REF error {
			after($2, new SQLSyntaxException("缺少参数类型"));
			$$ = NParamDeclare.EMPTY;
		}
	| 'OUT' VAR_REF error {
			after($2, new SQLSyntaxException("缺少参数类型"));
			$$ = NParamDeclare.EMPTY;
		}
	;

param_not_null:
	'NOT' 'NULL'
	/* error */
	| 'NOT' error { after($1, new SQLTokenNotFoundException("NULL")); }
	| 'NULL' { after($1, new SQLTokenNotFoundException("NOT")); }
	;

param_default:
	'DEFAULT' literal { $$ = $2; }
	| 'DEFAULT' '-' literal { $$ = this.neg((NLiteral)$3); }
	/* error */
	| 'DEFAULT' error { after($1, new SQLSyntaxException("缺少默认值")); }
	| literal { after($1, new SQLTokenNotFoundException("DEFAULT")); }
	;

param_type:
	'BOOLEAN' { $$ = NDataType.BOOLEAN; }
	| 'BYTE' { $$ = NDataType.BYTE; }
	| 'BYTES' { $$ = NDataType.BYTES; }
	| 'DATE' { $$ = NDataType.DATE; }
	| 'DOUBLE' { $$ = NDataType.DOUBLE; }
	| 'ENUM' '<' class_name '>' { $$ = NDataType.ENUM((String)$3); }
	| 'FLOAT' { $$ = NDataType.FLOAT; }
	| 'GUID' { $$ = NDataType.GUID; }
	| 'INT' { $$ = NDataType.INT; }
	| 'LONG' { $$ = NDataType.LONG; }
	| 'SHORT' { $$ = NDataType.SHORT; }
	| 'STRING' { $$ = NDataType.STRING; }
	| 'RECORDSET' { $$ = NDataType.RECORDSET; }
	/* error */
	| 'ENUM' '<' class_name error {
			at($2, new SQLTokenNotFoundException(">"));
			$$ = NDataType.UNKNOWN;
		}
	| 'ENUM' '<' error {
			after($2, new SQLSyntaxException("无法识别的类名"));
			$$ = NDataType.UNKNOWN;
		}
	| 'ENUM' error {
			after($1, new SQLTokenNotFoundException("<"));
			$$ = NDataType.UNKNOWN;
		}
	;

param_declare_list:
	param_declare_list ',' param_declare {
			LinkList l = (LinkList)$1;
			l.add($3);
			$$ = l;
		}
	| param_declare {
			LinkList l = new LinkList();
			l.add($1);
			$$ = l;
		}
	/* error */
	| param_declare_list ',' error {
			after($2, new SQLSyntaxException("缺少参数定义"));
			$$ = $1;
		}
	;

param_declare_list_op:
	param_declare_list { $$ = $1; }
	| { $$ = null; }
	;

/* table & field */

column_ref:
	ID '.' ID { $$ = new NColumnRefExpr((TString)$3, (TString)$1); }
	/* error */
	| ID '.' error {
			after($2, new SQLSyntaxException("缺少字段名"));
			$$ = NValueExpr.EMPTY;
		}
	;

name_ref:
	ID { $$ = new NNameRef((TString)$1); }
	;

/* value & expression */

literal:
	STR_VAL { $$ = new NLiteralString((TString)$1); }
	| INT_VAL { $$ = new NLiteralInt((TInt)$1); }
	| LONG_VAL {$$ = new NLiteralLong((TLong)$1); }
	| DOUBLE_VAL {$$ = new NLiteralDouble((TDouble)$1); }
	| 'TRUE' { $$ = new NLiteralBoolean((TBoolean)$1); }
	| 'FALSE' { $$ = new NLiteralBoolean((TBoolean)$1); }
	| 'DATE' STR_VAL {
			try {
				$$ = new NLiteralDate((TString)$2);
			} catch (SQLValueFormatException ex) {
				at(null, ex);
				$$ = NLiteralDate.EMPTY;
			}
		}
	| 'GUID' STR_VAL {
			try {
				$$ = new NLiteralGUID((TString)$2);
			} catch (SQLValueFormatException ex) {
				at(null, ex);
				$$ = NLiteralGUID.EMPTY;
			}
		}
	| 'BYTES' STR_VAL {
			try {
				$$ = new NLiteralBytes((TString)$2);
			} catch (SQLValueFormatException ex) {
				at(null, ex);
				$$ = NLiteralBytes.EMPTY;
			}
		}
	/* error */
	| 'DATE' error {
			after($1, new SQLSyntaxException("缺少日期字符串"));
			$$ = NValueExpr.EMPTY;
		}
	| 'GUID' error {
			after($1, new SQLSyntaxException("缺少GUID字符串"));
			$$ = NValueExpr.EMPTY;
		}
	| 'BYTES' error {
			after($1, new SQLSyntaxException("缺少BYTES字符串"));
			$$ = NValueExpr.EMPTY;
		}
	;

condition_expr:
	condition_expr 'OR' and_expr {
			$$ = new NLogicalExpr(NLogicalExpr.Operator.OR, (NConditionExpr)$1, (NConditionExpr)$3);
		}
	| and_expr { $$ = $1; }
	/* error */
	| condition_expr 'OR' error {
			after($2, new SQLSyntaxException("缺少条件表达式"));
			$$ = NConditionExpr.EMPTY;
		}
	;
	
and_expr:
	and_expr 'AND' not_expr {
			$$ = new NLogicalExpr(NLogicalExpr.Operator.AND, (NConditionExpr)$1, (NConditionExpr)$3);
		}
	| not_expr { $$ = $1; }
	/* error */
	| and_expr 'AND' error {
			after($2, new SQLSyntaxException("缺少条件表达式"));
			$$ = NConditionExpr.EMPTY;
		}
	;

not_expr:
	'NOT' compare_expr {
			$$ = new NLogicalExpr(NLogicalExpr.Operator.NOT, (NConditionExpr)$2, null); 
		}
	| compare_expr { $$ = $1; }
	/* error */
	| 'NOT' error {
			after($1, new SQLSyntaxException("缺少条件表达式"));
			$$ = NConditionExpr.EMPTY;
		}
	;

compare_expr:
	'(' condition_expr ')' { $$ = $2; }
	| value_expr compare_operator value_expr {
			TValueCompare op = (TValueCompare)$2;
			$$ = new NCompareExpr(op.value, (NValueExpr)$1, (NValueExpr)$3);
		}
	| between_expr
	| like_expr
	| str_compare_expr
	| in_expr
	| is_null_expr
	| exists_expr
	| hierarchy_expr
	| path_expr
	/* error */
	| '(' condition_expr error {
			after($2, new SQLTokenNotFoundException(")"));
			$$ = NConditionExpr.EMPTY;
		}
	| value_expr compare_operator error {
			after($2, new SQLSyntaxException("缺少值表达式"));
			$$ = NConditionExpr.EMPTY;
		}
	;

compare_operator:
	'>' { $$ = $1; }
	| '<' { $$ = $1; }
	| GE { $$ = $1; }
	| LE { $$ = $1; }
	| '=' { $$ = $1; }
	| NE { $$ = $1; }
	;

between_expr:
	value_expr not_expr_op 'BETWEEN' value_expr 'AND' value_expr {
			$$ = new NBetweenExpr(((TBoolean)$2).value, (NValueExpr)$1, 
				(NValueExpr)$4, (NValueExpr)$6);
		}
	/* error */
	| value_expr not_expr_op 'BETWEEN' value_expr 'AND' error {
			after($5, new SQLSyntaxException("缺少值表达式"));
			$$ = NConditionExpr.EMPTY;
		}
	| value_expr not_expr_op 'BETWEEN' value_expr error {
			after($4, new SQLTokenNotFoundException("AND"));
			$$ = NConditionExpr.EMPTY;
		}
	| value_expr not_expr_op 'BETWEEN' error {
			after($3, new SQLSyntaxException("缺少值表达式"));
			$$ = NConditionExpr.EMPTY;
		}
	;

like_expr:
	value_expr not_expr_op 'LIKE' value_expr escape_expr_op {
			$$ = new NLikeExpr((NValueExpr)$1, (NValueExpr)$4, (NValueExpr)$5,
				((TBoolean)$2).value);
		}
	/* error */
	| value_expr not_expr_op 'LIKE' error {
			after($3, new SQLSyntaxException("缺少字符串表达式"));
			$$ = NConditionExpr.EMPTY;
		}
	;

escape_expr_op:
	'ESCAPE' value_expr { $$ = $2; }
	| { $$ = null; }
	/* error */
	| 'ESCAPE' error { after($1, new SQLSyntaxException("缺少值表达式")); }
	;

str_compare_expr:
	value_expr not_expr_op str_compare_predicate value_expr {
			TStrCompare t = (TStrCompare)$3;
			$$ = new NStrCompareExpr(t.value, (NValueExpr)$1, 
				(NValueExpr)$4, ((TBoolean)$2).value);
		}
	/* error */
	| value_expr not_expr_op str_compare_predicate error {
			after($3, new SQLSyntaxException("缺少字符串表达式"));
			$$ = NConditionExpr.EMPTY;
		}
	;

str_compare_predicate:
	'STARTS_WITH' { $$ = $1; }
	| 'ENDS_WITH' { $$ = $1; }
	| 'CONTAINS' { $$ = $1; }
	;

in_expr:
	value_expr not_expr_op 'IN' in_expr_param {
			$$ = new NInExpr(((TBoolean)$2).value, (NValueExpr)$1, (NInExprParam)$4);
		}
	/* error */
	| value_expr not_expr_op 'IN' error {
			after($3, new SQLSyntaxException("缺少值列表或者子查询"));
			$$ = NConditionExpr.EMPTY;
		}
	;

in_expr_param:
	'(' in_value_list ')' {
			LinkList l = (LinkList)$2;
			NValueExpr[] arr = l.toArray(new NValueExpr[l.count()]);
			if (arr.length == 1 && arr[0] instanceof NQuerySpecific) {
				$$ = new NInParamSubQuery((Token)$1, (Token)$3, (NQuerySpecific)arr[0]);
			} else {
				$$ = new NInParamValueList((Token)$1, (Token)$3, arr);
			}
		}
	| '(' query_union ')' {
			$$ = new NInParamSubQuery((Token)$1, (Token)$3, (NQuerySpecific)$2);
		}
	/* error */
	| '(' in_value_list error {
			after($2, new SQLTokenNotFoundException(")"));
			$$ = NInExprParam.EMPTY;
		}
	| '(' error {
			after($1, new SQLSyntaxException("缺少值列表或者子查询"));
			$$ = NInExprParam.EMPTY;
		}
	;

in_value_list:
	in_value_list ',' value_expr {
			LinkList l = (LinkList)$1;
			l.add($3);
			$$ = l;
		}
	| value_expr {
			LinkList l = new LinkList();
			l.add($1);
			$$ = l;
		}
	/* error */
	| in_value_list ',' error {
			after($2, new SQLSyntaxException("缺少值表达式"));
			$$ = NInExprParam.EMPTY;
		}
	;

is_null_expr:
	value_expr 'IS' not_expr_op 'NULL' {
			$$ = new NIsNullExpr((Token)$4, ((TBoolean)$3).value, (NValueExpr)$1);
		}
	/* error */
	| value_expr 'IS' error {
			after($2, new SQLTokenNotFoundException("NULL"));
			$$ = NConditionExpr.EMPTY;
		}
	;

not_expr_op:
	'NOT' { $$ = new TBoolean(true, 0, 0, 0); }
	| { $$ = new TBoolean(false, 0, 0, 0); }
	;

exists_expr:
	'EXISTS' '(' query_union ')' {
			$$ = new NExistsExpr((Token)$1, (Token)$4, (NQuerySpecific)$3);
		}
	/* error */
	| 'EXISTS' '(' query_union error {
			after($3, new SQLTokenNotFoundException(")"));
			$$ = NConditionExpr.EMPTY;
		}
	| 'EXISTS' '(' error {
			after($2, new SQLSyntaxException("缺少查询语句"));
			$$ = NConditionExpr.EMPTY;
		}
	| 'EXISTS' error {
			after($1, new SQLTokenNotFoundException("("));
			$$ = NConditionExpr.EMPTY;
		}
	;

hierarchy_expr:
	ID hierarchy_predicate ID 'USING' ID {
			THierarchy t = (THierarchy)$2;
			if (t.value != NHierarchyExpr.Keywords.DESCENDANTOF) {
				$$ = new NHierarchyExpr(t.value, (TString)$1, (TString)$3,
						(TString)$5);
			} else {
				$$ = new NDescendantOfExpr((TString)$1, (TString)$3,
							(TString)$5, null, false);
			}
		}
	| ID hierarchy_predicate ID 'USING' ID 'RELATIVE' value_expr {
			THierarchy t = (THierarchy)$2;
			if (t.value != NHierarchyExpr.Keywords.DESCENDANTOF) {
				at($6, new SQLNotSupportedException("只有DESCENDANTOF谓词支持RELATIVE关键字"));
				$$ = NConditionExpr.EMPTY;
			} else {
				$$ = new NDescendantOfExpr((TString)$1, (TString)$3, (TString)$5, (NValueExpr)$7, false);
			}
		}
	| ID hierarchy_predicate ID 'USING' ID 'RANGE' value_expr {
			THierarchy t = (THierarchy)$2;
			if (t.value != NHierarchyExpr.Keywords.DESCENDANTOF) {
				at($6, new SQLNotSupportedException("只有DESCENDANTOF谓词支持RANGE关键字"));
				$$ = NConditionExpr.EMPTY;
			} else {
				$$ = new NDescendantOfExpr((TString)$1, (TString)$3, (TString)$5, (NValueExpr)$7, true);
			}
		}
	| ID 'IS' 'LEAF' 'USING' ID {
			$$ = new NIsLeafExpr((TString)$1, (TString)$5);
		}
	/* error */
	| ID hierarchy_predicate ID 'USING' ID 'RELATIVE' error {
			after($6, new SQLSyntaxException("缺少值表达式"));
			$$ = NConditionExpr.EMPTY;
		}
	| ID hierarchy_predicate ID 'USING' ID 'RANGE' error {
			after($6, new SQLSyntaxException("缺少值表达式"));
			$$ = NConditionExpr.EMPTY;
		}
	| ID hierarchy_predicate ID 'USING' error {
			after($4, new SQLSyntaxException("缺少表关系名称"));
			$$ = NConditionExpr.EMPTY;
		}
	| ID hierarchy_predicate ID error {
			after($3, new SQLTokenNotFoundException("USING"));
			$$ = NConditionExpr.EMPTY;
		}
	| ID hierarchy_predicate error {
			after($2, new SQLSyntaxException("缺少表别名"));
			$$ = NConditionExpr.EMPTY;
		}
	| ID 'IS' 'LEAF' 'USING' error {
			after($4, new SQLSyntaxException("缺少表关系名称"));
			$$ = NConditionExpr.EMPTY;
		}
	| ID 'IS' 'LEAF' error {
			after($3, new SQLTokenNotFoundException("USING"));
			$$ = NConditionExpr.EMPTY;
		}
	| ID 'IS' error {
			after($2, new SQLTokenNotFoundException("LEAF"));
			$$ = NConditionExpr.EMPTY;
		}
	;

hierarchy_predicate:
	'CHILDOF' { $$ = $1; }
	| 'PARENTOF' { $$ = $1; }
	| 'ANCESTOROF' { $$ = $1; }
	| 'DESCENDANTOF' { $$ = $1; }
	;

path_expr:
	ID hierarchy_predicate ID 'USING' '(' ID ',' ID ')' {
			THierarchy t = (THierarchy)$2;
			$$ = new NPathExpr(t.value, (TString)$1, (TString)$3,
						(TString)$6, (TString)$8, null);
		}
	| ID hierarchy_predicate ID 'USING' '(' ID ',' ID ')' 'RELATIVE' value_expr {
			THierarchy t = (THierarchy)$2;
			NValueExpr diff = (NValueExpr)$11;
			if (t.value != NHierarchyExpr.Keywords.ANCESTOROF && diff != null) {
				at($10, new SQLNotSupportedException("只有ANCESTOROF谓词支持相对级次"));
				$$ = NPathExpr.EMPTY;
			} else {
				$$ = new NPathExpr(t.value, (TString)$1, (TString)$3,
							(TString)$6, (TString)$8, diff);
			}
		}
	/* error */
	| ID hierarchy_predicate ID 'USING' '(' ID ',' ID ')' 'RELATIVE' error {
			after($10, new SQLSyntaxException("缺少值表达式"));
			$$ = NPathExpr.EMPTY;
		}
	| ID hierarchy_predicate ID 'USING' '(' ID ',' ID error {
			after($8, new SQLTokenNotFoundException(")"));
			$$ = NPathExpr.EMPTY;
		}
	| ID hierarchy_predicate ID 'USING' '(' ID ',' error {
			after($7, new SQLSyntaxException("缺少字段名称"));
			$$ = NPathExpr.EMPTY;
		}
	| ID hierarchy_predicate ID 'USING' '(' ID error {
			after($6, new SQLTokenNotFoundException(","));
			$$ = NPathExpr.EMPTY;
		}
	| ID hierarchy_predicate ID 'USING' '(' error {
			after($5, new SQLSyntaxException("缺少字段名称"));
			$$ = NPathExpr.EMPTY;
		}
	;

value_expr:
	value_expr '+' mul_expr {
			$$ = new NBinaryExpr(NBinaryExpr.Operator.ADD, (NValueExpr)$1, (NValueExpr)$3);
		}
	| value_expr '-' mul_expr {
			$$ = new NBinaryExpr(NBinaryExpr.Operator.SUB, (NValueExpr)$1, (NValueExpr)$3);
		}
	| value_expr CB mul_expr {
			$$ = new NBinaryExpr(NBinaryExpr.Operator.COMBINE, (NValueExpr)$1, (NValueExpr)$3);
		}
	| mul_expr { $$ = $1; }
	/* error */
	| value_expr '+' error {
			after($2, new SQLSyntaxException("缺少值表达式"));
			$$ = NValueExpr.EMPTY;
		}
	| value_expr '-' error {
			after($2, new SQLSyntaxException("缺少值表达式"));
			$$ = NValueExpr.EMPTY;
		}
	| value_expr CB error {
			after($2, new SQLSyntaxException("缺少值表达式"));
			$$ = NValueExpr.EMPTY;
		}
	;

mul_expr:
	mul_expr '*' neg_expr {
			$$ = new NBinaryExpr(NBinaryExpr.Operator.MUL, (NValueExpr)$1, (NValueExpr)$3);
		}
	| mul_expr '/' neg_expr {
			$$ = new NBinaryExpr(NBinaryExpr.Operator.DIV, (NValueExpr)$1, (NValueExpr)$3);
		}
	| mul_expr '%' neg_expr {
			$$ = new NBinaryExpr(NBinaryExpr.Operator.MOD, (NValueExpr)$1, (NValueExpr)$3);
		}
	| neg_expr { $$ = $1; }
	/* error */
	| mul_expr '*' error {
			after($2, new SQLSyntaxException("缺少值表达式"));
			$$ = NValueExpr.EMPTY;
		}
	| mul_expr '/' error {
			after($2, new SQLSyntaxException("缺少值表达式"));
			$$ = NValueExpr.EMPTY;
		}
	;

neg_expr:
	'-' factor {
			$$ = new NNegativeExpr((NValueExpr)$2);
		}
	| factor { $$ = $1; }
	/* error */
	| '-' error {
			after($1, new SQLSyntaxException("缺少值表达式"));
			$$ = NValueExpr.EMPTY;
		}
	;

var_ref:
	var_ref '.' ID { $$ = new NVarRefExpr((NVarRefExpr)$1, (TString)$3); }
	| VAR_REF { $$ = new NVarRefExpr(null, (TString)$1); }
	;

factor:
	column_ref { $$ = $1; }
	| literal { $$ = $1; }
	| var_ref { $$ = $1; }
	| 'NULL' { $$ = new NNullExpr((Token)$1); }
	| '(' value_expr ')' { $$ = $2; }
	| '(' query_union ')' { $$ = $2; }
	| set_func { $$ = $1; }
	| scalar_func { $$ = $1; }
	| hierarchy_func { $$ = $1; }
	| coalesce_func { $$= $1; }
	| simple_case { $$ = $1; }
	| searched_case { $$ = $1; }
	/* error */
	| '(' value_expr error {
			after($2, new SQLTokenNotFoundException(")"));
			$$ = NValueExpr.EMPTY;
		}
	| '(' error {
			after($1, new SQLSyntaxException("缺少值表达式"));
			$$ = NValueExpr.EMPTY;
		}
	;

set_func:
	set_func_operator '(' set_quantifier_op value_expr ')' {
			TSetFunction t = (TSetFunction)$1;
			SetQuantifier q = (SetQuantifier)$3;
			switch(t.value) {
			case MAX:
			case MIN:
				if (q != null)
					after($2, new SQLSyntaxException("不支持ALL/DISTINCT"));
				break;
			}
			$$ = new NAggregateExpr(t, (Token)$5, (NValueExpr)$4, q == null ? SetQuantifier.ALL : q);
		}
	| set_func_operator '(' '*' ')' {
			TSetFunction t = (TSetFunction)$1;
			if (t.value != NAggregateExpr.Func.COUNT) {
				at($3, new SQLSyntaxException("'*'只能用于COUNT函数"));
				$$ = NValueExpr.EMPTY;
			} else {
				$$ = new NAggregateExpr(t, (Token)$4, null, SetQuantifier.ALL);
			}
		}
	/* error */
	| set_func_operator '(' '*' error {
			after($3, new SQLTokenNotFoundException(")"));
			$$ = NValueExpr.EMPTY;
		}
	| set_func_operator '(' set_quantifier_op value_expr error {
			after($4, new SQLTokenNotFoundException(")"));
			$$ = NValueExpr.EMPTY;
		}
	| set_func_operator '(' error {
			after($2, new SQLSyntaxException("缺少值表达式或者'*'"));
			$$ = NValueExpr.EMPTY;
		}
	| set_func_operator error {
			after($1, new SQLTokenNotFoundException("("));
			$$ = NValueExpr.EMPTY;
		}
	;

set_func_operator:
	'COUNT' { $$ = $1; }
	| 'SUM' { $$ = $1; }
	| 'AVG' { $$ = $1; }
	| 'MAX' { $$ = $1; }
	| 'MIN' { $$ = $1; }
	;

scalar_func:
	ID '(' value_list ')' {
			TString name = (TString)$1;
			LinkList l = (LinkList)$3;
			NValueExpr[] params = l.toArray(new NValueExpr[l.count()]);
			try {
				SQLFuncSpec func = SQLFuncSpec.valueOf(name.value);
				$$ = new NFunctionExpr((Token)$1, (Token)$4, func, params);
			} catch (IllegalArgumentException ex) {
				at($1, new SQLFunctionUndefinedException(name.value));
				$$ = NValueExpr.EMPTY;
			}
		}
	| ID '(' ')' {
			TString name = (TString)$1;
			try {
				SQLFuncSpec func = SQLFuncSpec.valueOf(name.value);
				$$ = new NFunctionExpr((Token)$1, (Token)$3, func, null);
			} catch (IllegalArgumentException ex) {
				at($1, new SQLFunctionUndefinedException(name.value));
				$$ = NValueExpr.EMPTY;
			}
		}
	/* error */
	| ID '(' value_list error {
			after($3, new SQLTokenNotFoundException(")"));
			$$ = NValueExpr.EMPTY;
		}
	| ID '(' error {
			after($2, new SQLTokenNotFoundException(")"));
			$$ = NValueExpr.EMPTY;
		}
	;

hierarchy_func:
	'H_LV' '(' ID '.' ID ')' {
			$$ = new NHlvExpr((Token)$1, (Token)$6, (TString)$3, (TString)$5);
		}
	| 'H_AID' '(' ID '.' ID ')' {
			$$ = new NHaidExpr((Token)$1, (Token)$6, (TString)$3, (TString)$5, null, false);
		}
	| 'H_AID' '(' ID '.' ID 'REL' value_expr ')' {
			$$ = new NHaidExpr((Token)$1, (Token)$8, (TString)$3, (TString)$5, (NValueExpr)$7, true);
		}
	| 'H_AID' '(' ID '.' ID 'ABO' value_expr ')' {
			$$ = new NHaidExpr((Token)$1, (Token)$8, (TString)$3, (TString)$5, (NValueExpr)$7, false);
		}
	/* error */
	| 'H_LV' '(' ID '.' ID error {
			after($5, new SQLTokenNotFoundException(")"));
			$$ = NValueExpr.EMPTY;
		}
	| 'H_LV' '(' ID '.' error {
			after($4, new SQLSyntaxException("缺少表关系名称"));
			$$ = NValueExpr.EMPTY;
		}
	| 'H_LV' '(' ID error {
			after($3, new SQLTokenNotFoundException("."));
			$$ = NValueExpr.EMPTY;
		}
	| 'H_LV' '(' error {
			after($2, new SQLSyntaxException("缺少表名称"));
			$$ = NValueExpr.EMPTY;
		}
	| 'H_LV' error {
			after($1, new SQLTokenNotFoundException("("));
			$$ = NValueExpr.EMPTY;
		}
	| 'H_AID' '(' ID '.' ID 'REL' value_expr error {
			after($7, new SQLTokenNotFoundException(")"));
			$$ = NValueExpr.EMPTY;
		}
	| 'H_AID' '(' ID '.' ID 'ABO' value_expr error {
			after($7, new SQLTokenNotFoundException(")"));
			$$ = NValueExpr.EMPTY;
		}
	| 'H_AID' '(' ID '.' ID 'REL' error {
			after($6, new SQLSyntaxException("缺少整型表达式"));
			$$ = NValueExpr.EMPTY;
		}
	| 'H_AID' '(' ID '.' ID 'ABO' error {
			after($6, new SQLSyntaxException("缺少整型表达式"));
			$$ = NValueExpr.EMPTY;
		}
	| 'H_AID' '(' ID '.' ID error {
			after($5, new SQLTokenNotFoundException(")"));
			$$ = NValueExpr.EMPTY;
		}
	| 'H_AID' '(' ID '.' error {
			after($4, new SQLSyntaxException("缺少表关系名称"));
			$$ = NValueExpr.EMPTY;
		}
	| 'H_AID' '(' ID error {
			after($3, new SQLTokenNotFoundException("."));
			$$ = NValueExpr.EMPTY;
		}
	| 'H_AID' '(' error {
			after($2, new SQLSyntaxException("缺少表名称"));
			$$ = NValueExpr.EMPTY;
		}
	| 'H_AID' error {
			after($1, new SQLTokenNotFoundException("("));
			$$ = NValueExpr.EMPTY;
		}
	;

coalesce_func:
	'COALESCE' '(' value_list ')' {
			LinkList l = (LinkList)$3;
			NValueExpr[] params = l.toArray(new NValueExpr[l.count()]);
			$$ = new NCoalesceExpr((Token)$1, (Token)$4, params);
		}
	/* error */
	| 'COALESCE' '(' value_list error {
			after($3, new SQLTokenNotFoundException(")"));
			$$ = NValueExpr.EMPTY;
		}
	| 'COALESCE' '(' error {
			after($2, new SQLSyntaxException("缺少参数"));
			$$ = NValueExpr.EMPTY;
		}
	| 'COALESCE' error {
			after($1, new SQLTokenNotFoundException("("));
			$$ = NValueExpr.EMPTY;
		}
	;

simple_case:
	'CASE' value_expr simple_case_when_list case_else_expr_op 'END' {
			LinkList l = (LinkList)$3;
			$$ = new NSimpleCaseExpr((Token)$1, (Token)$5, (NValueExpr)$2,
					l.toArray(new NSimpleCaseWhen[l.count()]), (NValueExpr)$4);
		}
	/* error */
	| 'CASE' value_expr simple_case_when_list case_else_expr_op error {
			Object obj = $4;
			if (obj == null) {
				obj = $3;
			}
			after(obj, new SQLTokenNotFoundException("END"));
			$$ = NValueExpr.EMPTY;
		}
	| 'CASE' value_expr error {
			after($2, new SQLTokenNotFoundException("WHEN"));
			$$ = NValueExpr.EMPTY;
		}
	| 'CASE' error {
			after($1, new SQLSyntaxException("缺少表达式"));
			$$ = NValueExpr.EMPTY;
		}
	;
	

simple_case_when_list:
	simple_case_when_list simple_case_when {
			LinkList l = (LinkList)$1;
			l.add($2);
			$$ = l;
		}
	| simple_case_when {
			LinkList l = new LinkList();
			l.add($1);
			$$ = l;
		}
	;

simple_case_when:
	'WHEN' value_expr 'THEN' value_expr {
			$$ = new NSimpleCaseWhen((NValueExpr)$2, (NValueExpr)$4);
		}
	/* error */
	| 'WHEN' value_expr 'THEN' error {
			after($3, new SQLSyntaxException("缺少值表达式"));
			$$ = NSimpleCaseWhen.EMPTY;
		}
	| 'WHEN' value_expr error {
			after($2, new SQLTokenNotFoundException("THEN"));
			$$ = NSimpleCaseWhen.EMPTY;
		}
	| 'WHEN' error {
			after($1, new SQLSyntaxException("缺少表达式"));
			$$ = NSimpleCaseWhen.EMPTY;
		}
	;

case_else_expr_op:
	ELSE value_expr { $$ = $2; }
	| { $$ = null; }
	/* error */
	| ELSE error { after($1, new SQLSyntaxException("缺少值表达式")); }
	;

searched_case:
	'CASE' searched_case_when_list case_else_expr_op 'END' {
			LinkList l = (LinkList)$2;
			$$ = new NSearchedCaseExpr((Token)$1, (Token)$4,
					l.toArray(new NSearchedCaseWhen[l.count()]), (NValueExpr)$3);
		}
	/* error */
	| 'CASE' searched_case_when_list case_else_expr_op error {
			Object obj = $3;
			if (obj == null) {
				obj = $2;
			}
			after(obj, new SQLTokenNotFoundException("END"));
			$$ = NValueExpr.EMPTY;
		}
	;

searched_case_when_list:
	searched_case_when_list searched_case_when {
			LinkList l = (LinkList)$1;
			l.add($2);
			$$ = l;
		}
	| searched_case_when {
			LinkList l = new LinkList();
			l.add($1);
			$$ = l;
		}
	;

searched_case_when:
	'WHEN' condition_expr 'THEN' value_expr {
			$$ = new NSearchedCaseWhen((NConditionExpr)$2, (NValueExpr)$4);
		}
	/* error */
	| 'WHEN' condition_expr 'THEN' error {
			after($3, new SQLSyntaxException("缺少值表达式"));
			$$ = NSearchedCaseWhen.EMPTY;
		}
	| 'WHEN' condition_expr error {
			after($2, new SQLTokenNotFoundException("THEN"));
			$$ = NSearchedCaseWhen.EMPTY;
		}
	;

value_list:
	value_list ',' value_expr {
			LinkList l = (LinkList)$1;
			l.add($3);
			$$ = l;
		}
	| value_expr {
			LinkList l = new LinkList();
			l.add($1);
			$$ = l;
		}
	/* error */
	| value_list ',' error {
			after($2, new SQLSyntaxException("缺少值表达式"));
			$$ = $1;
		}
	;

/* query */

query_invoke:
	ID '(' value_list ')' {
			LinkList l = (LinkList)$3;
			$$ = new NQueryInvoke((Token)$4, (TString)$1, l.toArray(new NValueExpr[l.count()]));
		}
	| ID '(' ')' {
			$$ = new NQueryInvoke((Token)$3, (TString)$1, null);
		}
	;

query_declare:
	'DEFINE' 'QUERY' ID '(' param_declare_list_op ')' 'BEGIN' query_stmt 'END' {
			LinkList l = (LinkList)$5;
			$$ = new NQueryDeclare((Token)$1, (Token)$9, (TString)$3,
					l == null ? null : l.toArray(new NParamDeclare[l.count()]),
					(NQueryStmt)$8);
		}
	| 'DEFINE' 'QUERY' ID '(' param_declare_list_op ')' 'BEGIN' query_stmt ';' 'END' {
			LinkList l = (LinkList)$5;
			$$ = new NQueryDeclare((Token)$1, (Token)$10, (TString)$3,
					l == null ? null : l.toArray(new NParamDeclare[l.count()]),
					(NQueryStmt)$8);
		}
	/* error */
	| 'DEFINE' 'QUERY' ID '(' param_declare_list_op ')'
		'BEGIN' query_stmt error {
			after($8, new SQLTokenNotFoundException("END"));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'QUERY' ID '(' param_declare_list_op ')'
		'BEGIN' error {
			after($7, new SQLSyntaxException("缺少查询语句"));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'QUERY' ID '(' param_declare_list_op ')' error {
			after($6, new SQLTokenNotFoundException("BEGIN"));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'QUERY' ID '(' error {
			after($4, new SQLTokenNotFoundException(")"));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'QUERY' ID error {
			after($3, new SQLTokenNotFoundException("("));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'QUERY' error {
			at($2, new SQLSyntaxException("缺少名称"));
			$$ = NStatement.EMPTY;
		}
	;

query_stmt:
	'WITH' query_with_list query_union orderby_op {
			LinkList l = (LinkList)$2;
			NQueryWith[] arr = l.toArray(new NQueryWith[l.count()]);
			$$ = new NQueryStmt((Token)$1, arr, (NQuerySpecific)$3, (NOrderBy)$4);
		}
	| query_union orderby_op {
			$$ = new NQueryStmt(null, null, (NQuerySpecific)$1, (NOrderBy)$2);
		}
	/* error */
	| 'WITH' query_with_list error {
			after($2, new SQLSyntaxException("缺少SELECT语句"));
			$$ = NQueryStmt.EMPTY;
		}
	| 'WITH' error {
			after($1, new SQLSyntaxException("缺少子查询或者'('"));
			$$ = NQueryStmt.EMPTY;
		}
	;

query_union:
	query_union 'UNION' query_primary {
			NQuerySpecific s = (NQuerySpecific)$1;
			s.union((NQuerySpecific)$3, false);
			$$ = s;
		}
	| query_union 'UNION' 'ALL' query_primary {
			NQuerySpecific s = (NQuerySpecific)$1;
			s.union((NQuerySpecific)$4, true);
			$$ = s;
		}
	| query_sub 'UNION' query_primary {
			NQuerySpecific s = (NQuerySpecific)$1;
			s.union((NQuerySpecific)$3, false);
			$$ = s;
		}
	| query_sub 'UNION' 'ALL' query_primary {
			NQuerySpecific s = (NQuerySpecific)$1;
			s.union((NQuerySpecific)$4, true);
			$$ = s;
		}
	| query_select { $$ = $1; }
	/* error */
	| query_union 'UNION' error {
			after($2, new SQLSyntaxException("缺少SELECT语句"));
			$$ = $1;
		}
	| query_sub 'UNION' error {
			after($2, new SQLSyntaxException("缺少SELECT语句"));
			$$ = $1;
		}
	;

query_sub:
	'(' query_union ')' { $$ = $2; }
	/* error */
	| '(' query_union error {
			after($2, new SQLTokenNotFoundException(")"));
			$$ = $2;
		}
	| '(' error {
			after($1, new SQLSyntaxException("缺少SELECT语句"));
			$$ = NQuerySpecific.EMPTY;
		}
	;

query_primary:
	query_sub { $$ = $1; }
	| query_select { $$ = $1; }
	;

query_select:
	select from where_op groupby_op having_op {
			NWhere w = (NWhere)$3;
			if (w != null && w.cursor != null) {
				at(w, new SQLNotSupportedException("查询语句中不能使用WHERE CURRENT OF子句"));
				$$ = NQuerySpecific.EMPTY;
			} else {
				$$ = new NQuerySpecific((NSelect)$1, (NFrom)$2, w,
							(NGroupBy)$4, (NHaving)$5);
			}
		}
	/* error */
	| select error {
			after($1, new SQLTokenNotFoundException("FROM"));
			$$ = NQuerySpecific.EMPTY;
		}
	;

query_with_list:
	query_with_list ',' query_with {
			LinkList l = (LinkList)$1;
			l.add($3);
			$$ = l;
		}
	| query_with {
			LinkList l = new LinkList();
			l.add($1);
			$$ = l;
		}
	/* error */
	| query_with_list ',' error {
			after($2, new SQLSyntaxException("缺少子查询"));
			$$ = $1;
		}
	;

query_with:
	'(' query_union ')' 'AS' ID {
			$$ = new NQueryWith((Token)$1, (TString)$5, (NQuerySpecific)$2);
		}
	/* error */
	| '(' query_union ')' 'AS' error {
			after($4, new SQLSyntaxException("缺少别名"));
			$$ = NQueryWith.EMPTY;
		}
	| '(' query_union ')' error {
			after($3, new SQLTokenNotFoundException("AS"));
			$$ = NQueryWith.EMPTY;
		}
	| '(' query_union error {
			after($2, new SQLTokenNotFoundException(")"));
			$$ = NQueryWith.EMPTY;
		}
	;

select:
	'SELECT' set_quantifier_op query_column_list {
			LinkList l = (LinkList)$3;
			NQueryColumn[] columns = l.toArray(new NQueryColumn[l.count()]);
			$$ = new NSelect((Token)$1, (SetQuantifier)$2, columns);
		}
	/* error */
	| 'SELECT' error {
			after($1, new SQLSyntaxException("缺少列选表达式"));
			$$ = NSelect.EMPTY;
		}
	;

from:
	'FROM' source_list {
			LinkList l = (LinkList)$2;
			$$ = new NFrom((Token)$1, l.toArray(new NSource[l.count()]));
		}
	/* error */
	| 'FROM' error {
			after($1, new SQLSyntaxException("缺少数据源"));
			$$ = NFrom.EMPTY;
		}
	;

source_list:
	source_list ',' source {
			LinkList l = (LinkList)$1;
			l.add($3);
			$$ = l;
		}
	| source {
			LinkList l = new LinkList();
			l.add($1);
			$$ = l;
		}
	/* error */
	| source_list ',' error {
			after($2, new SQLSyntaxException("缺少数据源"));
			$$ = $1;
		}
	;

where_op:
	'WHERE' condition_expr { $$ = new NWhere((Token)$1, (NConditionExpr)$2); }
	| 'WHERE' 'CURRENT' 'OF' VAR_REF { $$ = new NWhere((Token)$1, (TString)$4); }
	| { $$ = null; }
	/* error */
	| 'WHERE' error { after($1, new SQLSyntaxException("缺少条件表达式")); }
	| 'WHERE' 'CURRENT' 'OF' error { after($3, new SQLSyntaxException("缺少变量名")); }
	| 'WHERE' 'CURRENT' error { after($2, new SQLTokenNotFoundException("OF")); } 
	;

groupby_op:
	'GROUP' 'BY' groupby_column_list {
			LinkList l = (LinkList)$3;
			NValueExpr[] columns = l.toArray(new NValueExpr[l.count()]);
			$$ = new NGroupBy((Token)$1, null, columns, GroupByType.DEFAULT);
		}
	| 'GROUP' 'BY' groupby_column_list 'WITH' 'ROLLUP' {
			LinkList l = (LinkList)$3;
			NValueExpr[] columns = l.toArray(new NValueExpr[l.count()]);
			$$ = new NGroupBy((Token)$1, (Token)$5, columns, GroupByType.ROLL_UP);
		}
	| { $$ = null; }
	/* error */
	| 'GROUP' 'BY' groupby_column_list 'WITH' error {
			after($4, new SQLTokenNotFoundException("ROLLUP"));
		}
	| 'GROUP' 'BY' error { after($2, new SQLSyntaxException("缺少列表达式")); }
	| 'GROUP' error { after($1, new SQLTokenNotFoundException("BY")); }
	;

groupby_column_list:
	groupby_column_list ',' value_expr {
			LinkList l = (LinkList)$1;
			l.add($3);
			$$ = l;
		}
	| value_expr {
			LinkList l = new LinkList();
			l.add($1);
			$$ = l;
		}
	/* error */
	| groupby_column_list ',' error {
			after($2, new SQLSyntaxException("缺少列表达式"));
			$$ = $1;
		}
	;

having_op:
	'HAVING' condition_expr { $$ = new NHaving((Token)$1, (NConditionExpr)$2); }
	| { $$ = null; }
	/* error */
	| 'HAVING' error { after($1, new SQLSyntaxException("缺少条件表达式")); }
	;

orderby_op:
	'ORDER' 'BY' orderby_column_list {
			LinkList l = (LinkList)$3;
			NOrderByColumn[] columns = l.toArray(new NOrderByColumn[l.count()]);
			$$ = new NOrderBy((Token)$1, columns);
		}
	| { $$ = null; }
	/* error */
	| 'ORDER' 'BY' error { after($2, new SQLSyntaxException("缺少列表达式")); }
	| 'ORDER' error { after($1, new SQLTokenNotFoundException("BY")); }
	;

orderby_column_list:
	orderby_column_list ',' orderby_column {
			LinkList l = (LinkList)$1;
			l.add((NOrderByColumn)$3);
			$$ = l;
		}
	| orderby_column {
			LinkList l = new LinkList();
			l.add((NOrderByColumn)$1);
			$$ = l;
		}
	;

orderby_column:
	value_expr {
			NValueExpr e = (NValueExpr)$1;
			$$ = new NOrderByColumn(e, e, true);
		}
	| value_expr 'ASC' { $$ = new NOrderByColumn((Token)$2, (NValueExpr)$1, true); }
	| value_expr 'DESC' { $$ = new NOrderByColumn((Token)$2, (NValueExpr)$1, false); }
	| ID {
			TString name = (TString)$1;
			$$ = new NOrderByColumn(name, name, true);
		}
	| ID 'ASC' { $$ = new NOrderByColumn((Token)$2, (TString)$1, true); }
	| ID 'DESC' { $$ = new NOrderByColumn((Token)$2, (TString)$1, false); }
	;

query_column_list:
	query_column_list ',' query_column {
			LinkList l = (LinkList)$1;
			l.add($3);
			$$ = l;
		}
	| query_column {
			LinkList l = new LinkList();
			l.add($1);
			$$ = l;
		}
	/* error */
	| query_column_list ',' error {
			after($2, new SQLSyntaxException("缺少列选表达式"));
			$$ = $1;
		}
	;

query_column:
	value_expr {
			$$ = new NQueryColumn((NValueExpr)$1, null);
		}
	| value_expr 'AS' ID {
			TString alias = (TString)$3;
			if (alias.value.charAt(0) == '$') {
				at(alias, new SQLSyntaxException("别名不能以$符号开始"));
				$$ = new NQueryColumn((NValueExpr)$1, null);
			} else {
				$$ = new NQueryColumn((NValueExpr)$1, alias);
			}
		}
	/* error */
	| value_expr 'AS' error {
			after($2, new SQLSyntaxException("缺少别名"));
			$$ = NQueryColumn.EMPTY;
		}
	;

source:
	source join_type 'JOIN' source_ref 'ON' condition_expr {
			$$ = new NSourceJoin((TableJoinType)$2, (NSource)$1, (NSource)$4, (NConditionExpr)$6);
		}
	| source join_type 'RELATE' ID '.' ID {
			$$ = new NSourceRelate((TableJoinType)$2, (NSource)$1, (TString)$4, (TString)$6, null);
		}
	| source join_type 'RELATE' ID '.' ID 'AS' ID {
			TString alias = (TString)$8;
			if (alias.value.charAt(0) == '$') {
				at(alias, new SQLSyntaxException("别名不能以$符号开始"));
				$$ = new NSourceRelate((TableJoinType)$2, (NSource)$1, (TString)$4, (TString)$6, null);
			} else {
				$$ = new NSourceRelate((TableJoinType)$2, (NSource)$1, (TString)$4, (TString)$6, alias);
			}
		}
	| source_ref { $$ = $1; }
	/* error */
	| source join_type 'JOIN' source_ref 'ON' error {
			after($5, new SQLSyntaxException("缺少条件表达式"));
			$$ = NSource.EMPTY;
		}
	| source join_type 'JOIN' source_ref error {
			after($4, new SQLTokenNotFoundException("ON"));
			$$ = NSource.EMPTY;
		}
	| source join_type 'JOIN' error {
			after($3, new SQLSyntaxException("缺少表引用或者子查询"));
			$$ = NSource.EMPTY;
		}
	| source join_type 'RELATE' ID '.' ID 'AS' error {
			after($7, new SQLSyntaxException("缺少别名"));
			$$ = NSource.EMPTY;
		}
	| source join_type 'RELATE' ID '.' error {
			after($5, new SQLSyntaxException("缺少关系名"));
			$$ = NSource.EMPTY;
		}
	| source join_type 'RELATE' ID error {
			after($4, new SQLTokenNotFoundException("."));
			$$ = NSource.EMPTY;
		}
	| source join_type 'RELATE' error {
			after($3, new SQLSyntaxException("缺少表引用"));
			$$ = NSource.EMPTY;
		}
	;

source_ref:
	name_ref 'FOR' 'UPDATE' {
			$$ = new NSourceTable((NNameRef)$1, null, true);
		}
	| name_ref 'AS' ID 'FOR' 'UPDATE' {
			TString alias = (TString)$3;
			if (alias.value.charAt(0) == '$') {
				at(alias, new SQLSyntaxException("别名不能以$符号开始"));
				$$ = new NSourceTable((NNameRef)$1, null, true);
			} else {
				$$ = new NSourceTable((NNameRef)$1, alias, true);
			}
		}
	| name_ref {
			$$ = new NSourceTable((NNameRef)$1, null, false);
		}
	| name_ref 'AS' ID {
			TString alias = (TString)$3;
			if (alias.value.charAt(0) == '$') {
				at(alias, new SQLSyntaxException("别名不能以$符号开始"));
				$$ = new NSourceTable((NNameRef)$1, null, false);
			} else {
				$$ = new NSourceTable((NNameRef)$1, alias, false);
			}
		}
	| '(' query_union ')' 'AS' ID {
			TString alias = (TString)$5;
			if (alias.value.charAt(0) == '$') {
				at(alias, new SQLSyntaxException("别名不能以$符号开始"));
				$$ = NSource.EMPTY;
			} else {
				$$ = new NSourceSubQuery((Token)$1, (NQuerySpecific)$2, alias);
			}
		}
	| '(' source ')' { $$ = $2; }
	/* error */
	| name_ref 'FOR' error {
			after($2, new SQLTokenNotFoundException("UPDATE"));
			$$ = NSource.EMPTY;
		}
	| name_ref 'AS' ID 'FOR' error {
			after($4, new SQLTokenNotFoundException("UPDATE"));
			$$ = NSource.EMPTY;
		}
	| name_ref 'AS' error {
			after($2, new SQLSyntaxException("缺少别名"));
			$$ = NSource.EMPTY;
		}
	| '(' query_union ')' 'AS' error {
			after($4, new SQLSyntaxException("缺少别名"));
			$$ = NSource.EMPTY;
		}
	;

join_type:
	'LEFT' { $$ = TableJoinType.LEFT; }
	| 'RIGHT' { $$ = TableJoinType.RIGHT; }
	| 'FULL' { $$ = TableJoinType.FULL; }
	| { $$ = TableJoinType.INNER; }
	;

set_quantifier_op:
	'ALL' { $$ = SetQuantifier.ALL; }
	| 'DISTINCT' { $$ = SetQuantifier.DISTINCT; }
	| { $$ = null; }
	;

orm_declare:
	'DEFINE' 'ORM' ID '(' param_declare_list_op ')'
		'MAPPING' class_name 'BEGIN' query_stmt 'END' {
			LinkList l = (LinkList)$5;
			$$ = new NOrmDeclare((Token)$1, (Token)$11, (TString)$3, 
						l == null ? null : l.toArray(new NParamDeclare[l.count()]),
						(String)$8, (NQueryStmt)$10);
		}
	| 'DEFINE' 'ORM' ID '(' param_declare_list_op ')'
		'OVERRIDE' ID 'BEGIN' where_op having_op orderby_op 'END' {
			String base = ((TString)$8).value;
			try {
				NOrmDeclare orm = this.openSQL(NOrmDeclare.class,
						DNASqlType.ORM, base);
				NQueryStmt q = orm.body;
				if (q.expr.unions != null) {
					throw new SQLNotSupportedException("不支持重写包含UNION的查询");
				}
				LinkList l = (LinkList)$5;
				NParamDeclare[] params = l == null ? null : l.toArray(new NParamDeclare[l.count()]);
				NQuerySpecific s = q.expr;
				s = new NQuerySpecific(s.select, s.from, (NWhere)$10, s.group, (NHaving)$11);
				$$ = new NOrmOverride((Token)$1, (Token)$13, (TString)$3, 
						params, (TString)$8, orm.className,
						new NQueryStmt(null, null, s, (NOrderBy)$12));
			} catch (SQLParseException ex) {
				at($1, ex);
				$$ = NStatement.EMPTY;
			}
		}
	/* error */
	| 'DEFINE' 'ORM' ID '(' param_declare_list_op ')'
		'MAPPING' class_name 'BEGIN' query_stmt error {
			after($10, new SQLTokenNotFoundException("BEGIN"));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'ORM' ID '(' param_declare_list_op ')'
		'MAPPING' class_name 'BEGIN' error {
			after($9, new SQLSyntaxException("缺少查询语句"));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'ORM' ID '(' param_declare_list_op ')' 'MAPPING' class_name error {
			after($7, new SQLTokenNotFoundException("BEGIN"));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'ORM' ID '(' param_declare_list_op ')' 'MAPPING' error {
			after($7, new SQLSyntaxException("缺少类名"));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'ORM' ID '(' param_declare_list_op ')' 'OVERRIDE' ID 'BEGIN' error {
			after($9, new SQLTokenNotFoundException("END"));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'ORM' ID '(' param_declare_list_op ')' 'OVERRIDE' ID error {
			after($8, new SQLTokenNotFoundException("BEGIN"));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'ORM' ID '(' param_declare_list_op ')' 'OVERRIDE' error {
			after($7, new SQLSyntaxException("缺少ORM名称"));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'ORM' ID '(' param_declare_list_op ')' error {
			after($6, new SQLTokenNotFoundException("MAPPING/OVERRIDE"));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'ORM' ID '(' error {
			after($4, new SQLTokenNotFoundException(")"));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'ORM' ID error {
			after($3, new SQLTokenNotFoundException("("));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'ORM' error {
			at($2, new SQLSyntaxException("缺少名称"));
			$$ = NStatement.EMPTY;
		}
	;

class_name:
	class_list {
			StringBuilder sb = (StringBuilder)$1;
			$$ = sb.toString();
		}
	;
	
class_list:
	class_list '.' ID {
			StringBuilder sb = (StringBuilder)$1;
			sb.append(".");
			sb.append(((TString)$3).value);
			$$ = sb;
		}
	| ID {
			StringBuilder sb = new StringBuilder();
			sb.append(((TString)$1).value);
			$$ = sb;
		}
	/* error */
	| class_list '.' error {
			after($2, new SQLSyntaxException("缺少包名或类名"));
			$$ = $1;
		}
	;

returning_op:
	'RETURNING' ENV_REF 'INTO' VAR_REF {
			TString ref = (TString)$2;
			if ("#rowcount".equals(ref.value)) {
				$$ = new NReturning((Token)$1, (TString)$2, (TString)$4);
			} else {
				at(ref, new SQLNotSupportedException("只支持#rowcount系统变量"));
			}
		}
	| { $$ = null; }
	/* error */
	| 'RETURNING' ENV_REF 'INTO' error {
			after($3, new SQLSyntaxException("缺少变量名称"));
			$$ = null;
		}
	| 'RETURNING' ENV_REF error {
			after($2, new SQLTokenNotFoundException("INTO"));
			$$ = null;
		}
	| 'RETURNING' error {
			after($1, new SQLSyntaxException("缺少系统变量名称"));
			$$ = null;
		}
	;

/* insert */

insert_declare:
	'DEFINE' 'INSERT' ID '(' param_declare_list_op ')' 'BEGIN' insert_stmt 'END' {
			LinkList l = (LinkList)$5;
			$$ = new NInsertDeclare((Token)$1, (Token)$9, (TString)$3,
						l == null ? null : l.toArray(new NParamDeclare[l.count()]),
						(NInsertStmt)$8);
		}
	| 'DEFINE' 'INSERT' ID '(' param_declare_list_op ')' 'BEGIN' insert_stmt ';' 'END' {
			LinkList l = (LinkList)$5;
			$$ = new NInsertDeclare((Token)$1, (Token)$10, (TString)$3,
						l == null ? null : l.toArray(new NParamDeclare[l.count()]),
						(NInsertStmt)$8);
		}
	/* error */
	| 'DEFINE' 'INSERT' ID '(' param_declare_list_op ')'
		'BEGIN' insert_stmt error {
			after($8, new SQLTokenNotFoundException("END"));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'INSERT' ID '(' param_declare_list_op ')'
		'BEGIN' error {
			after($7, new SQLSyntaxException("缺少新增语句"));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'INSERT' ID '(' param_declare_list_op ')' error {
			after($6, new SQLTokenNotFoundException("BEGIN"));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'INSERT' ID '(' error {
			after($4, new SQLTokenNotFoundException(")"));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'INSERT' ID error {
			after($3, new SQLTokenNotFoundException("("));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'INSERT' error {
			at($2, new SQLSyntaxException("缺少名称"));
			$$ = NStatement.EMPTY;
		}
	;

insert_stmt:
	insert insert_values returning_op {
			$$ = new NInsertStmt((NInsert)$1, (NInsertSource)$2, (NReturning)$3);
		}
	| insert insert_sub_query returning_op {
			$$ = new NInsertStmt((NInsert)$1, (NInsertSource)$2, (NReturning)$3);
		}
	/* error */
	| insert '(' error {
			after($2, new SQLSyntaxException("缺少字段列表或者SELECT语句"));
			$$ = NInsertStmt.EMPTY;
		}
	| insert error {
			after($1, new SQLSyntaxException("缺少值列表或者子查询"));
			$$ = NInsertStmt.EMPTY;
		}
	;

insert:
	'INSERT' 'INTO' name_ref {
			$$ = new NInsert((Token)$1, (NNameRef)$3);
		}
	/* error */
	| 'INSERT' 'INTO' error {
			after($2, new SQLSyntaxException("缺少表名称"));
			$$ = NInsert.EMPTY;
		}
	| 'INSERT' error {
			after($1, new SQLTokenNotFoundException("INTO"));
			$$ = NInsert.EMPTY;
		}
	;

insert_sub_query:
	'(' query_union ')' {
			$$ = new NInsertSubQuery((Token)$1, (Token)$3, (NQuerySpecific)$2);
		}
	/* error */
	| '(' query_union error {
			after($2, new SQLTokenNotFoundException(")"));
			$$ = NInsertSource.EMPTY;
		}
	;

insert_values:
	'(' insert_column_list ')' 'VALUES' '(' insert_value_list ')' {
			LinkList column_list = (LinkList)$2;
			TString[] columns = column_list.toArray(new TString[column_list.count()]);
			LinkList value_list = (LinkList)$6;
			NValueExpr[] values = value_list.toArray(new NValueExpr[value_list.count()]);
			$$ = new NInsertValues((Token)$1, (Token)$7, columns, values);
		}
	/* error */
	| '(' insert_column_list ')' 'VALUES' '(' insert_value_list error {
			after($6, new SQLTokenNotFoundException(")"));
			$$ = NInsertSource.EMPTY;
		}
	| '(' insert_column_list ')' 'VALUES' '(' error {
			after($5, new SQLSyntaxException("缺少值列表"));
			$$ = NInsertSource.EMPTY;
		}
	| '(' insert_column_list ')' 'VALUES' error {
			after($4, new SQLTokenNotFoundException("("));
			$$ = NInsertSource.EMPTY;
		}
	| '(' insert_column_list ')' error {
			after($3, new SQLTokenNotFoundException("VALUES"));
			$$ = NInsertSource.EMPTY;
		}
	| '(' insert_column_list error {
			after($2, new SQLTokenNotFoundException(")"));
			$$ = NInsertSource.EMPTY;
		}
	;

insert_value_list:
	insert_value_list ',' value_expr {
			LinkList l = (LinkList)$1;
			l.add($3);
			$$ = l;
		}
	| value_expr {
			LinkList l = new LinkList();
			l.add($1);
			$$ = l;
		}
	/* error */
	| insert_value_list ',' error {
			after($2, new SQLSyntaxException("缺少值表达式"));
			$$ = $1;
		}
	;

insert_column_list:
	insert_column_list ',' ID {
			LinkList l = (LinkList)$1;
			l.add($3);
			$$ = l;
		}
	| ID {
			LinkList l = new LinkList();
			l.add($1);
			$$ = l;
		}
	/* error */
	| insert_column_list ',' error {
			after($2, new SQLSyntaxException("缺少字段名称"));
			$$ = $1;
		}
	;

/* update */

update_declare:
	'DEFINE' 'UPDATE' ID '(' param_declare_list_op ')' 'BEGIN' update_stmt 'END' {
			LinkList l = (LinkList)$5;
			$$ = new NUpdateDeclare((Token)$1, (Token)$9, (TString)$3,
						l == null ? null : l.toArray(new NParamDeclare[l.count()]),
						(NUpdateStmt)$8);
		}
	| 'DEFINE' 'UPDATE' ID '(' param_declare_list_op ')' 'BEGIN' update_stmt ';' 'END' {
			LinkList l = (LinkList)$5;
			$$ = new NUpdateDeclare((Token)$1, (Token)$10, (TString)$3,
						l == null ? null : l.toArray(new NParamDeclare[l.count()]),
						(NUpdateStmt)$8);
		}
	/* error */
	| 'DEFINE' 'UPDATE' ID '(' param_declare_list_op ')'
		'BEGIN' update_stmt error {
			after($8, new SQLTokenNotFoundException("END"));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'UPDATE' ID '(' param_declare_list_op ')'
		'BEGIN' error {
			after($7, new SQLSyntaxException("缺少更新语句"));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'UPDATE' ID '(' param_declare_list_op ')' error {
			after($6, new SQLTokenNotFoundException("BEGIN"));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'UPDATE' ID '(' error {
			after($4, new SQLTokenNotFoundException(")"));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'UPDATE' ID error {
			after($3, new SQLTokenNotFoundException("("));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'UPDATE' error {
			at($2, new SQLSyntaxException("缺少名称"));
			$$ = NStatement.EMPTY;
		}
	;

update_stmt:
	update update_set where_op returning_op{
			$$ = new NUpdateStmt((NUpdate)$1, (NUpdateSet)$2, (NWhere)$3, (NReturning)$4);
		}
	/* error */
	| update error {
			after($1, new SQLTokenNotFoundException("SET"));
			$$ = NUpdateStmt.EMPTY;
		}
	;

update:
	'UPDATE' source {
			$$ = new NUpdate((Token)$1, (NSource)$2);
		}
	/* error */
	| 'UPDATE' error {
			after($1, new SQLSyntaxException("缺少目标表"));
			$$ = NUpdate.EMPTY;
		}
	;
	
update_set:
	'SET' update_column_list {
			LinkList l = (LinkList)$2;
			NUpdateColumnValue[] columns = l.toArray(new NUpdateColumnValue[l.count()]);
			$$ = new NUpdateSet((Token)$1, columns);
		}
	/* error */
	| 'SET' error {
			after($1, new SQLSyntaxException("缺少赋值表达式"));
			$$ = NUpdateSet.EMPTY;
		}
	;

update_column_list:
	update_column_list ',' update_column_value {
			LinkList l = (LinkList)$1;
			l.add($3);
			$$ = l;
		}
	| update_column_value {
			LinkList l = new LinkList();
			l.add($1);
			$$ = l;
		}
	/* error */
	| update_column_list ',' error {
			after($2, new SQLSyntaxException("缺少赋值表达式"));
			$$ = $1;
		}
	;

update_column_value:
	ID '=' value_expr {
			$$ = new NUpdateColumnValue((TString)$1, (NValueExpr)$3);
		}
	/* error */
	| ID '=' error {
			after($2, new SQLSyntaxException("缺少值表达式"));
			$$ = NUpdateColumnValue.EMPTY;
		}
	| ID error {
			after($2, new SQLTokenNotFoundException("="));
			$$ = NUpdateColumnValue.EMPTY;
		}
	;

/* delete */

delete_declare:
	'DEFINE' 'DELETE' ID '(' param_declare_list_op ')' 'BEGIN' delete_stmt 'END' {
			LinkList l = (LinkList)$5;
			$$ = new NDeleteDeclare((Token)$1, (Token)$9, (TString)$3,
						l == null ? null : l.toArray(new NParamDeclare[l.count()]),
						(NDeleteStmt)$8);
		}
	| 'DEFINE' 'DELETE' ID '(' param_declare_list_op ')' 'BEGIN' delete_stmt ';' 'END' {
			LinkList l = (LinkList)$5;
			$$ = new NDeleteDeclare((Token)$1, (Token)$10, (TString)$3,
						l == null ? null : l.toArray(new NParamDeclare[l.count()]),
						(NDeleteStmt)$8);
		}
	/* error */
	| 'DEFINE' 'DELETE' ID '(' param_declare_list_op ')'
		'BEGIN' delete_stmt error {
			after($8, new SQLTokenNotFoundException("END"));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'DELETE' ID '(' param_declare_list_op ')'
		'BEGIN' error {
			after($7, new SQLSyntaxException("缺少删除语句"));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'DELETE' ID '(' param_declare_list_op ')' error {
			after($6, new SQLTokenNotFoundException("BEGIN"));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'DELETE' ID '(' error {
			after($4, new SQLTokenNotFoundException(")"));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'DELETE' ID error {
			after($3, new SQLTokenNotFoundException("("));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'DELETE' error {
			at($2, new SQLSyntaxException("缺少名称"));
			$$ = NStatement.EMPTY;
		}
	;

delete_stmt:
	delete where_op returning_op {
			$$ = new NDeleteStmt((NDelete)$1, (NWhere)$2, (NReturning)$3);
		}
	;

delete:
	'DELETE' 'FROM' source {
			$$ = new NDelete((Token)$1, (NSource)$3);
		}
	/* error */
	| 'DELETE' 'FROM' error {
			after($2, new SQLSyntaxException("缺少目标表"));
			$$ = NDelete.EMPTY;
		}
	| 'DELETE' error {
			after($2, new SQLTokenNotFoundException("FROM"));
			$$ = NDelete.EMPTY;
		}
	;

/* table */

table_declare:
	'DEFINE' 'TABLE' ID table_extend_op
	'BEGIN'
		primary_section extend_section_op index_section_op
		relation_section_op hierarchy_section_op partition_section_op
	'END' {
			$$ = new NTableDeclare((Token)$1, (Token)$12, (TString)$3,
					(NAbstractTableDeclare)$4, (NTablePrimary)$6,
					(NTableExtend[])$7, (NTableIndex[])$8, (NTableRelation[])$9,
					(NTableHierarchy[])$10, (NTablePartition)$11);
		}
	| 'DEFINE' 'ABSTRACT' 'TABLE' ID table_extend_op
	'BEGIN'
		primary_section extend_section_op index_section_op
		relation_section_op hierarchy_section_op partition_section_op
	'END' {
			NAbstractTableDeclare base = (NAbstractTableDeclare)$5;
			NTableExtend[] extend = (NTableExtend[])$8;
			NTableRelation[] relation = (NTableRelation[])$10;
			NTableHierarchy[] hierarchy = (NTableHierarchy[])$11;
			NTablePartition partition = (NTablePartition)$12;
			// 暂时仅支持主表字段和索引
			Object start = $1;
			if (base != null) {
				at(start, new SQLNotSupportedException("抽象表不能继承"));
			}
			if (extend != null) {
				at(start, new SQLNotSupportedException("抽象表定义不支持物理表"));
			}
			if (relation != null) {
				at(start, new SQLNotSupportedException("抽象表定义不支持关系"));
			}
			if (hierarchy != null) {
				at(start, new SQLNotSupportedException("抽象表定义不支持级次"));
			}
			if (partition != null) {
				at(start, new SQLNotSupportedException("抽象表定义不支持分区"));
			}
			$$ = new NAbstractTableDeclare((Token)start, (Token)$13, (TString)$4,
					base, (NTablePrimary)$7, extend, (NTableIndex[])$9,
					relation, hierarchy, partition);
		}
	/* error */
	| 'DEFINE' 'TABLE' ID table_extend_op 'BEGIN' error {
			after($5, new SQLTokenNotFoundException("END"));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'TABLE' ID error {
			after($3, new SQLTokenNotFoundException("BEGIN"));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'TABLE' error {
			after($2, new SQLSyntaxException("缺少名称"));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'ABSTRACT' 'TABLE' ID table_extend_op 'BEGIN' error {
			after($6, new SQLTokenNotFoundException("END"));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'ABSTRACT' 'TABLE' ID error {
			after($4, new SQLTokenNotFoundException("BEGIN"));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'ABSTRACT' 'TABLE' error {
			after($3, new SQLSyntaxException("缺少名称"));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'ABSTRACT' error {
			after($2, new SQLTokenNotFoundException("TABLE"));
			$$ = NStatement.EMPTY;
		}
	;

table_extend_op:
	'EXTEND' ID {
			try {
				NAbstractTableDeclare base = this.openSQL(NAbstractTableDeclare.class,
						DNASqlType.ABSTRACT_TABLE, ((TString)$2).value);
				$$ = base;
			} catch (SQLParseException ex) {
				at($1, ex);
			}
		}
	| { $$ = null; }
	/* error */
	| 'EXTEND' error {
			after($1, new SQLSyntaxException("缺少基础表名称"));
		}
	;

primary_section:
	'FIELDS' table_field_list {
			LinkList l = (LinkList)$2;
			NTableField[] arr = l.toArray(new NTableField[l.count()]);
			$$ = new NTablePrimary((Token)$1, arr);
		}
	/* error */
	| 'FIELDS' error {
			after($1, new SQLSyntaxException("缺少字段列表"));
			$$ = NTablePrimary.EMPTY;
		}
	;

extend_section_op:
	extend_section {
			LinkList l = (LinkList)$1;
			$$ = l.toArray(new NTableExtend[l.count()]);
		}
	| { $$ = null; }
	;

extend_section:
	extend_section extend_declare {
			LinkList l = (LinkList)$1;
			l.add($2);
			$$ = l;
		}
	| extend_declare {
			LinkList l = new LinkList();
			l.add($1);
			$$ = l;
		}
	;

extend_declare:
	'FIELDS' 'ON' ID table_field_list {
			LinkList l = (LinkList)$4;
			NTableField[] arr = l.toArray(new NTableField[l.count()]);
			$$ = new NTableExtend((Token)$1, (TString)$3, arr);
		}
	/* error */
	| 'FIELDS' 'ON' error {
			after($2, new SQLSyntaxException("缺少字段列表"));
		}
	| 'FIELDS' error {
			after($1, new SQLTokenNotFoundException("ON"));
		}
	;

index_section_op:
	'INDEXES' index_declare_list {
			LinkList l = (LinkList)$2;
			$$ = l.toArray(new NTableIndex[l.count()]);
		}
	| { $$ = null; }
	/* error */
	| 'INDEXES' error {
			after($1, new SQLSyntaxException("缺少索引列表"));
		}
	;

relation_section_op:
	'RELATIONS' relation_declare_list {
			LinkList l = (LinkList)$2;
			$$ = l.toArray(new NTableRelation[l.count()]);
		}
	| { $$ = null; }
	/* error */
	| 'RELATIONS' error {
			after($1, new SQLSyntaxException("缺少关系列表"));
		}
	;

hierarchy_section_op:
	'HIERARCHIES' hierarchy_declare_list {
			LinkList l = (LinkList)$2;
			$$ = l.toArray(new NTableHierarchy[l.count()]);
		}
	| { $$ = null; }
	/* error */
	| 'HIERARCHIES' error {
			after($1, new SQLSyntaxException("缺少级次列表"));
		}
	;

partition_section_op:
	'PARTITION' '(' partition_field_list ')'
		'VAVLE' INT_VAL 'MAXCOUNT' INT_VAL {
			LinkList l = (LinkList)$3;
			TString[] arr = l.toArray(new TString[l.count()]);
			$$ = new NTablePartition((Token)$1, arr, (TInt)$6, (TInt)$8);
		}
	| { $$ = null; }
	/* error */
	| 'PARTITION' '(' partition_field_list ')' 'VAVLE' INT_VAL 'MAXCOUNT' error {
			after($7, new SQLSyntaxException("缺少最大分区数目"));
		}
	| 'PARTITION' '(' partition_field_list ')' 'VAVLE' INT_VAL error {
			after($6, new SQLTokenNotFoundException("MAXCOUNT"));
		}
	| 'PARTITION' '(' partition_field_list ')' 'VAVLE' error {
			after($5, new SQLSyntaxException("缺少阀值"));
		}
	| 'PARTITION' '(' partition_field_list ')' error {
			after($4, new SQLTokenNotFoundException("VAVLE"));
		}
	| 'PARTITION' '(' partition_field_list error {
			after($3, new SQLTokenNotFoundException(")"));
		}
	| 'PARTITION' '(' error {
			after($2, new SQLSyntaxException("缺少字段列表"));
		}
	| 'PARTITION' error {
			after($1, new SQLTokenNotFoundException("("));
		}
	;

table_field_list:
	table_field_list ',' table_field_declare {
			LinkList l = (LinkList)$1;
			l.add($3);
			$$ = l;
		}
	| table_field_declare {
			LinkList l = new LinkList();
			l.add($1);
			$$ = l;
		}
	/* error */
	| table_field_list ',' error {
			after($2, new SQLSyntaxException("缺少字段声明"));
			$$ = $1;
		}
	;

table_field_declare:
	ID field_type field_not_null_op field_default_op {
			$$ = new NTableField((TString)$1, (NDataType)$2,
					((Boolean)$3).booleanValue(), (NLiteral)$4, false);
		}
	| ID field_type field_not_null_op field_default_op 'PRIMARY' 'KEY' {
			$$ = new NTableField((TString)$1, (NDataType)$2,
					((Boolean)$3).booleanValue(), (NLiteral)$4, true);
		}
	| ID field_type field_not_null_op field_default_op field_foreign_key
	 {
			$$ = new NTableField((TString)$1, (NDataType)$2,
					((Boolean)$3).booleanValue(), (NLiteral)$4, (NTableForeignKey)$5);
		}
	/* error */
	| ID field_type field_not_null_op field_default_op 'PRIMARY' error {
			after($5, new SQLTokenNotFoundException("KEY"));
			$$ = new NTableField((TString)$1, (NDataType)$2,
					((Boolean)$3).booleanValue(), (NLiteral)$4, true);
		}
	| ID error {
			after($1, new SQLSyntaxException("缺少字段类型"));
			$$ = NTableField.EMPTY;
		}
	;

field_not_null_op:
	param_not_null { $$ = Boolean.TRUE; }
	| { $$ = Boolean.FALSE; }
	;

field_default_op:
	'DEFAULT' '(' literal ')' { $$ = $3; }
	| 'DEFAULT' '(' '-' literal ')' { $$ = this.neg((NLiteral)$4); }
	| { $$ = null; }
	;

field_type:
	'BOOLEAN' { $$ = NDataType.BOOLEAN; }
	| 'DATE' { $$ = NDataType.DATE; }
	| 'DOUBLE' { $$ = NDataType.DOUBLE; }
	| 'FLOAT' { $$ = NDataType.FLOAT; }
	| 'GUID' { $$ = NDataType.GUID; }
	| 'INT' { $$ = NDataType.INT; }
	| 'LONG' { $$ = NDataType.LONG; }
	| 'SHORT' { $$ = NDataType.SHORT; }
	| 'BINARY' '(' INT_VAL ')' { $$ = NDataType.BINARY((TInt)$3); }
	| 'VARBINARY' '(' INT_VAL ')' { $$ = NDataType.VARBINARY((TInt)$3); }
	| 'BLOB' { $$ = NDataType.BLOB; }
	| 'CHAR' '(' INT_VAL ')' { $$ = NDataType.CHAR((TInt)$3); }
	| 'VARCHAR' '(' INT_VAL ')' { $$ = NDataType.VARCHAR((TInt)$3); }
	| 'NCHAR' '(' INT_VAL ')' { $$ = NDataType.NCHAR((TInt)$3); }
	| 'NVARCHAR' '(' INT_VAL ')' { $$ = NDataType.NVARCHAR((TInt)$3); }
	| 'TEXT' { $$ = NDataType.TEXT; }
	| 'NTEXT' { $$ = NDataType.NTEXT; }
	| 'NUMERIC' '(' INT_VAL ',' INT_VAL ')' {
			$$ = NDataType.NUMERIC((TInt)$3, (TInt)$5);
		}
	/* error */
	| 'BINARY' '(' INT_VAL error {
			after($3, new SQLTokenNotFoundException(")"));
			$$ = NDataType.UNKNOWN;
		}
	| 'BINARY' '(' error {
			after($2, new SQLSyntaxException("缺少长度"));
			$$ = NDataType.UNKNOWN;
		}
	| 'BINARY' error {
			after($1, new SQLTokenNotFoundException("("));
			$$ = NDataType.UNKNOWN;
		}
	| 'VARBINARY' '(' INT_VAL error {
			after($3, new SQLTokenNotFoundException(")"));
			$$ = NDataType.UNKNOWN;
		}
	| 'VARBINARY' '(' error {
			after($2, new SQLSyntaxException("缺少长度"));
			$$ = NDataType.UNKNOWN;
		}
	| 'VARBINARY' error {
			after($1, new SQLTokenNotFoundException("("));
			$$ = NDataType.UNKNOWN;
		}
	| 'CHAR' '(' INT_VAL error {
			after($3, new SQLTokenNotFoundException(")"));
			$$ = NDataType.UNKNOWN;
		}
	| 'CHAR' '(' error {
			after($2, new SQLSyntaxException("缺少长度"));
			$$ = NDataType.UNKNOWN;
		}
	| 'CHAR' error {
			after($1, new SQLTokenNotFoundException("("));
			$$ = NDataType.UNKNOWN;
		}
	| 'VARCHAR' '(' INT_VAL error {
			after($3, new SQLTokenNotFoundException(")"));
			$$ = NDataType.UNKNOWN;
		}
	| 'VARCHAR' '(' error {
			after($2, new SQLSyntaxException("缺少长度"));
			$$ = NDataType.UNKNOWN;
		}
	| 'VARCHAR' error {
			after($1, new SQLTokenNotFoundException("("));
			$$ = NDataType.UNKNOWN;
		}
	| 'NCHAR' '(' INT_VAL error {
			after($3, new SQLTokenNotFoundException(")"));
			$$ = NDataType.UNKNOWN;
		}
	| 'NCHAR' '(' error {
			after($2, new SQLSyntaxException("缺少长度"));
			$$ = NDataType.UNKNOWN;
		}
	| 'NCHAR' error {
			after($1, new SQLTokenNotFoundException("("));
			$$ = NDataType.UNKNOWN;
		}
	| 'NVARCHAR' '(' INT_VAL error {
			after($3, new SQLTokenNotFoundException(")"));
			$$ = NDataType.UNKNOWN;
		}
	| 'NVARCHAR' '(' error {
			after($2, new SQLSyntaxException("缺少长度"));
			$$ = NDataType.UNKNOWN;
		}
	| 'NVARCHAR' error {
			after($1, new SQLTokenNotFoundException("("));
			$$ = NDataType.UNKNOWN;
		}
	| 'NUMERIC' '(' INT_VAL ',' INT_VAL error {
			after($5, new SQLTokenNotFoundException(")"));
			$$ = NDataType.UNKNOWN;
		}
	| 'NUMERIC' '(' INT_VAL ',' error {
			after($4, new SQLSyntaxException("缺少精度"));
			$$ = NDataType.UNKNOWN;
		}
	| 'NUMERIC' '(' INT_VAL error {
			after($3, new SQLTokenNotFoundException(","));
			$$ = NDataType.UNKNOWN;
		}
	| 'NUMERIC' '(' error {
			after($2, new SQLSyntaxException("缺少长度"));
			$$ = NDataType.UNKNOWN;
		}
	| 'NUMERIC' error {
			after($1, new SQLTokenNotFoundException("("));
			$$ = NDataType.UNKNOWN;
		}
	;

field_foreign_key:
	'RELATION' ID 'TO' ID '.' ID {
		$$ = new NTableForeignKey((TString)$2, (TString)$4, (TString)$6);
	}
	/* error */
	| 'RELATION' ID 'TO' ID '.' error {
			after($5, new SQLSyntaxException("缺少字段名称"));
		}
	| 'RELATION' ID 'TO' ID error {
			after($4, new SQLTokenNotFoundException("."));
		}
	| 'RELATION' ID 'TO' error {
			after($3, new SQLSyntaxException("缺少表名称"));
		}
	| 'RELATION' ID error {
			after($2, new SQLTokenNotFoundException("TO"));
		}
	| 'RELATION' error {
			after($1, new SQLSyntaxException("缺少名称"));
		}
	;

index_declare_list:
	index_declare_list ',' index_declare {
			LinkList l = (LinkList)$1;
			l.add($3);
			$$ = l;
		}
	| index_declare {
			LinkList l = new LinkList();
			l.add($1);
			$$ = l;
		}
	/* error */
	| index_declare_list ',' error {
			after($2, new SQLSyntaxException("缺少索引声明"));
			$$ = $1;
		}
	;

index_declare:
	ID '(' index_order_list ')' {
			LinkList l = (LinkList)$3;
			NTableIndexField[] arr = l.toArray(new NTableIndexField[l.count()]);
			TString name = (TString)$1;
			$$ = new NTableIndex(name, (Token)$4, name, arr, false);
		}
	| 'UNIQUE' ID '(' index_order_list ')' {
			LinkList l = (LinkList)$4;
			NTableIndexField[] arr = l.toArray(new NTableIndexField[l.count()]);
			$$ = new NTableIndex((Token)$1, (Token)$5, (TString)$2, arr, true);
		}
	/* error */
	| ID '(' index_order_list error {
			after($3, new SQLTokenNotFoundException(")"));
			$$ = NTableIndex.EMPTY;
		}
	| ID '(' error {
			after($2, new SQLSyntaxException("缺少顺序列表"));
			$$ = NTableIndex.EMPTY;
		}
	| ID error {
			after($1, new SQLTokenNotFoundException("("));
			$$ = NTableIndex.EMPTY;
		}
	| 'UNIQUE' ID '(' index_order_list error {
			after($4, new SQLTokenNotFoundException(")"));
			$$ = NTableIndex.EMPTY;
		}
	| 'UNIQUE' ID '(' error {
			after($3, new SQLSyntaxException("缺少顺序列表"));
			$$ = NTableIndex.EMPTY;
		}
	| 'UNIQUE' ID error {
			after($2, new SQLTokenNotFoundException("("));
			$$ = NTableIndex.EMPTY;
		}
	| 'UNIQUE' error {
			after($1, new SQLSyntaxException("缺少名称"));
			$$ = NTableIndex.EMPTY;
		}
	;

index_order_list:
	index_order_list ',' index_order {
			LinkList l = (LinkList)$1;
			l.add($3);
			$$ = l;
		}
	| index_order {
			LinkList l = new LinkList();
			l.add($1);
			$$ = l;
		}
	/* error */
	| index_order_list ',' error {
			after($2, new SQLSyntaxException("缺少顺序声明"));
			$$ = $1;
		}
	;

index_order:
	ID 'ASC' { $$ = new NTableIndexField((TString)$1, false); }
	| ID 'DESC' { $$ = new NTableIndexField((TString)$1, true); }
	/* error */
	| ID error {
			after($1, new SQLTokenNotFoundException("ASC/DESC"));
			$$ = new NTableIndexField((TString)$1, false);
		}
	;

relation_declare_list:
	relation_declare_list ',' relation_declare {
			LinkList l = (LinkList)$1;
			l.add($3);
			$$ = l;
		}
	| relation_declare {
			LinkList l = new LinkList();
			l.add($1);
			$$ = l;
		}
	/* error */
	| relation_declare_list ',' error {
			after($2, new SQLSyntaxException("缺少关系声明"));
			$$ = $1;
		}
	;

relation_declare:
	ID 'TO' ID 'ON' condition_expr {
			$$ = new NTableRelation((TString)$1, (TString)$3, (NConditionExpr)$5);
		}
	/* error */
	| ID 'TO' ID 'ON' error {
			after($4, new SQLSyntaxException("缺少条件表达式"));
			$$ = NTableRelation.EMPTY;
		}
	| ID 'TO' ID error {
			after($3, new SQLTokenNotFoundException("ON"));
			$$ = NTableRelation.EMPTY;
		}
	| ID 'TO' error {
			after($2, new SQLSyntaxException("缺少表名称"));
			$$ = NTableRelation.EMPTY;
		}
	| ID error {
			after($1, new SQLTokenNotFoundException("TO"));
			$$ = NTableRelation.EMPTY;
		}
	;

hierarchy_declare_list:
	hierarchy_declare_list ',' hierarchy_declare {
			LinkList l = (LinkList)$1;
			l.add($3);
			$$ = l;
		}
	| hierarchy_declare {
			LinkList l = new LinkList();
			l.add($1);
			$$ = l;
		}
	/* error */
	| hierarchy_declare_list ',' error {
			after($2, new SQLSyntaxException("缺少级次声明"));
			$$ = $1;
		}
	;

hierarchy_declare:
	ID 'MAXLEVEL' '(' INT_VAL ')' {
			$$ = new NTableHierarchy((Token)$5, (TString)$1, (TInt)$4);
		}
	/* error */
	| ID 'MAXLEVEL' '(' INT_VAL error {
			after($4, new SQLTokenNotFoundException(")"));
			$$ = NTableHierarchy.EMPTY;
		}
	| ID 'MAXLEVEL' '(' error {
			after($3, new SQLSyntaxException("缺少最大级次数目"));
			$$ = NTableHierarchy.EMPTY;
		}
	| ID 'MAXLEVEL' error {
			after($2, new SQLTokenNotFoundException("("));
			$$ = NTableHierarchy.EMPTY;
		}
	| ID error {
			after($1, new SQLTokenNotFoundException("MAXLEVEL"));
			$$ = NTableHierarchy.EMPTY;
		}
	;

partition_field_list:
	partition_field_list ',' ID {
			LinkList l = (LinkList)$1;
			l.add($3);
			$$ = l;
		}
	| ID {
			LinkList l = new LinkList();
			l.add($1);
			$$ = l;
		}
	/* error */
	| partition_field_list ',' error {
			after($2, new SQLSyntaxException("缺少字段名称"));
			$$ = $1;
		}
	;

procedure_declare:
	'DEFINE' 'PROCEDURE' ID '(' param_declare_list_op ')'
	'BEGIN'
		statement_list
	'END' {
			LinkList l = (LinkList)$5;
			NParamDeclare[] params = l == null ? null :
										l.toArray(new NParamDeclare[l.count()]);
			l = (LinkList)$8;
			NStatement[] stmts = l.toArray(new NStatement[l.count()]);
			$$ = new NProcedureDeclare((Token)$1, (Token)$9, (TString)$3,
						params, stmts);
		}
	/* error */
	| 'DEFINE' 'PROCEDURE' ID '(' param_declare_list_op ')'
		'BEGIN' statement_list error {
			after($8, new SQLTokenNotFoundException("END"));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'PROCEDURE' ID '(' param_declare_list_op ')'
		'BEGIN' error {
			after($7, new SQLSyntaxException("缺少语句"));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'PROCEDURE' ID '(' param_declare_list_op ')'
		error {
			after($6, new SQLTokenNotFoundException("BEGIN"));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'PROCEDURE' ID '(' error {
			after($4, new SQLTokenNotFoundException(")"));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'PROCEDURE' ID error {
			after($3, new SQLTokenNotFoundException("("));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'PROCEDURE' error {
			after($2, new SQLSyntaxException("缺少名称"));
			$$ = NStatement.EMPTY;
		}
	;

statement_list:
	statement_list statement {
			LinkList l = (LinkList)$1;
			l.add($2);
			$$ = l;
		}
	| statement {
			LinkList l = new LinkList();
			l.add($1);
			$$ = l;
		}
	;

statement:
	insert_stmt ';' { $$ = $1; }
	| update_stmt ';' { $$ = $1; }
	| delete_stmt ';' { $$ = $1; }
	| query_declare { $$ = $1; }
	| table_declare {
			NTableDeclare t = (NTableDeclare)$1;
			try {
				if (t.base != null) {
					throw new SQLNotSupportedException(t.name.line, t.name.col,
							"临时表不支持继承");
				}
				if (t.extend != null) {
					throw new SQLNotSupportedException(t.extend[0].startLine(),
						t.extend[0].startCol(), "临时表不支持物理表");
				}
				if (t.relation != null) {
					throw new SQLNotSupportedException(t.relation[0].startLine(),
						t.relation[0].startCol(), "临时表不支持关系");
				}
				if (t.hierarchy != null) {
					throw new SQLNotSupportedException(t.hierarchy[0].startLine(),
						t.hierarchy[0].startCol(), "临时表不支持级次");
				}
				if (t.partition != null) {
					throw new SQLNotSupportedException(t.partition.startLine(),
						t.partition.startCol(), "临时表不支持分区");
				}
				for (NTableField f : t.primary.fields) {
					if (f.foreignKey != null) {
						throw new SQLNotSupportedException(f.startLine(),
								f.startCol(), "临时表不支持关系");
					}
				}
				$$ = t;
			} catch(SQLParseException ex) {
				raise(null, false, ex);
				$$ = NStatement.EMPTY;
			}
		}
	| var_stmt ';' { $$ = $1; }
	| assign_stmt ';' { $$ = $1; }
	| if_stmt { $$ = $1; }
	| while_stmt { $$ = $1; }
	| loop_stmt { $$ = $1; }
	| foreach_stmt { $$ = $1; }
	| break ';' { $$ = $1; }
	| print ';' { $$ = $1; }
	| return ';' { $$ = $1; }
	| segment { $$ = $1; }
	/* error */
	| insert_stmt error {
			after($1, new SQLTokenNotFoundException(";"));
			$$ = $1;
		}
	| update_stmt error {
			after($1, new SQLTokenNotFoundException(";"));
			$$ = $1;
		}
	| delete_stmt error {
			after($1, new SQLTokenNotFoundException(";"));
			$$ = $1;
		}
	| var_stmt error {
			after($1, new SQLTokenNotFoundException(";"));
			$$ = $1;
		}
	| assign_stmt error {
			after($1, new SQLTokenNotFoundException(";"));
			$$ = $1;
		}
	| break error {
			after($1, new SQLTokenNotFoundException(";"));
			$$ = $1;
		}
	| print error {
			after($1, new SQLTokenNotFoundException(";"));
			$$ = $1;
		}
	| return error {
			after($1, new SQLTokenNotFoundException(";"));
			$$ = $1;
		}
	;

segment:
	'BEGIN' statement_list 'END' {
			LinkList l = (LinkList)$2;
			$$ = new NSegment((Token)$1, (Token)$3, l.toArray(new NStatement[l.count()]));
		}
	/* error */
	| 'BEGIN' statement_list error {
			LinkList l = (LinkList)$2;
			after(l, new SQLTokenNotFoundException("END"));
			$$ = new NSegment((Token)$1, l, l.toArray(new NStatement[l.count()]));
		}
	| 'BEGIN' error {
			after($1, new SQLSyntaxException("缺少语句"));
			$$ = NStatement.EMPTY;
		} 
	;

var_stmt:
	'VAR' VAR_REF var_type {
			$$ = new NVarStmt((Token)$1, (TString)$2, (NDataType)$3, null);
		}
	| 'VAR' VAR_REF var_type '=' value_expr {
			$$ = new NVarStmt((Token)$1, (TString)$2, (NDataType)$3, (NValueExpr)$5);
		}
	/* error */
	| 'VAR' VAR_REF var_type '=' error {
			after($4, new SQLSyntaxException("缺少值表达式"));
			$$ = new NVarStmt((Token)$1, (TString)$2, (NDataType)$3, null);
		}
	| 'VAR' VAR_REF error {
			after($2, new SQLSyntaxException("缺少数据类型"));
			$$ = NStatement.EMPTY;
		}
	| 'VAR' error {
			after($1, new SQLSyntaxException("缺少变量名称"));
			$$ = NStatement.EMPTY;
		}
	;

var_type:
	'BOOLEAN' { $$ = NDataType.BOOLEAN; }
	| 'BYTE' { $$ = NDataType.BYTE; }
	| 'BYTES' { $$ = NDataType.BYTES; }
	| 'DATE' { $$ = NDataType.DATE; }
	| 'DOUBLE' { $$ = NDataType.DOUBLE; }
	| 'ENUM' '<' class_name '>' { $$ = NDataType.ENUM((String)$3); }
	| 'FLOAT' { $$ = NDataType.FLOAT; }
	| 'GUID' { $$ = NDataType.GUID; }
	| 'INT' { $$ = NDataType.INT; }
	| 'LONG' { $$ = NDataType.LONG; }
	| 'SHORT' { $$ = NDataType.SHORT; }
	| 'STRING' { $$ = NDataType.STRING; }
	/* error */
	| 'ENUM' '<' class_name error {
			at($2, new SQLTokenNotFoundException(">"));
			$$ = NDataType.UNKNOWN;
		}
	| 'ENUM' '<' error {
			after($2, new SQLSyntaxException("无法识别的类名"));
			$$ = NDataType.UNKNOWN;
		}
	| 'ENUM' error {
			after($1, new SQLTokenNotFoundException("<"));
			$$ = NDataType.UNKNOWN;
		}
	;

assign_stmt:
	VAR_REF '=' value_expr {
			TString ref = (TString)$1;
			NValueExpr val = (NValueExpr)$3;
			$$ = new NAssignStmt(ref, val, new TString[] { ref }, new NValueExpr[] { val });
		}
	| '(' primary_ref_list ')' '=' '(' value_list ')' {
			LinkList l = (LinkList)$2;
			TString[] refs = l.toArray(new TString[l.count()]);
			l = (LinkList)$6;
			if (refs.length == l.count()) {
				$$ = new NAssignStmt((Token)$1, (Token)$7, refs,
							l.toArray(new NValueExpr[l.count()]));
			} else {
				at($4, new SQLNotSupportedException("赋值运算符两端操作数个数不同"));
				$$ = NStatement.EMPTY;
			}
		}
	| VAR_REF '=' query_stmt {
			TString ref = (TString)$1;
			$$ = new NAssignStmt(ref, new TString[] { ref }, (NQueryStmt)$3);
		}
	| '(' primary_ref_list ')' '=' query_stmt {
			LinkList l = (LinkList)$2;
			TString[] refs = l.toArray(new TString[l.count()]);
			NQueryStmt q = (NQueryStmt)$5;
			if (refs.length == q.getMasterSelect().select.columns.length) {
				$$ = new NAssignStmt((Token)$1, refs, q);
			} else {
				at($4, new SQLNotSupportedException("赋值运算符左侧操作数个数与查询输出列个数不同"));
				$$ = NStatement.EMPTY;
			}
		}
	/* error */
	| VAR_REF '=' error {
			after($2, new SQLSyntaxException("缺少值表达式"));
			$$ = NStatement.EMPTY;
		}
	| '(' primary_ref_list ')' '=' '(' value_list error {
			after($6, new SQLTokenNotFoundException(")"));
			$$ = NStatement.EMPTY;
		}
	| '(' primary_ref_list ')' '=' '(' error {
			after($5, new SQLSyntaxException("缺少值列表"));
			$$ = NStatement.EMPTY;
		}
	|  '(' primary_ref_list ')' '=' error {
			after($4, new SQLSyntaxException("缺少值列表或查询"));
			$$ = NStatement.EMPTY;
		}
	|  '(' primary_ref_list '=' {
			after($2, new SQLTokenNotFoundException(")"));
			$$ = NStatement.EMPTY;
		}
	|  primary_ref_list ')' '=' {
			at($1, new SQLTokenNotFoundException("("));
			$$ = NStatement.EMPTY;
		}
	;

primary_ref_list:
	primary_ref_list ',' VAR_REF {
			LinkList l = (LinkList)$1;
			l.add($3);
			$$ = l;
		}
	| VAR_REF {
			LinkList l = new LinkList();
			l.add($1);
			$$ = l;
		}
	/* error */
	| primary_ref_list ',' error {
			after($2, new SQLSyntaxException("缺少变量名称"));
			$$ = $1;
		}
	| primary_ref_list VAR_REF {
			Object l = $1;
			after(l, new SQLTokenNotFoundException(","));
			$$ = l;
		}
	;

if_stmt:
	IF condition_expr 'THEN' statement %prec IFX {
			$$ = new NIfStmt((Token)$1, (NConditionExpr)$2, (NStatement)$4, null);
		}
	| IF condition_expr 'THEN' statement ELSE statement {
			$$ = new NIfStmt((Token)$1, (NConditionExpr)$2, (NStatement)$4, (NStatement)$6);
		}
	/* error */
	| IF condition_expr 'THEN' statement ELSE error {
			after($5, new SQLSyntaxException("缺少语句"));
			$$ = NStatement.EMPTY;
		}
	| IF condition_expr 'THEN' error {
			after($3, new SQLSyntaxException("缺少语句"));
			$$ = NStatement.EMPTY;
		}
	| IF condition_expr error {
			after($2, new SQLTokenNotFoundException("THEN"));
			$$ = NStatement.EMPTY;
		}
	| IF error {
			after($1, new SQLSyntaxException("缺少谓词表达式"));
			$$ = NStatement.EMPTY;
		}
	;

while_stmt:
	'WHILE' condition_expr 'LOOP' statement {
			$$ = new NWhileStmt((Token)$1, (NConditionExpr)$2, (NStatement)$4);
		}
	/* error */
	| 'WHILE' condition_expr 'LOOP' error {
			after($3, new SQLSyntaxException("缺少语句"));
			$$ = NStatement.EMPTY;
		}
	| 'WHILE' condition_expr error {
			after($2, new SQLTokenNotFoundException("LOOP"));
			$$ = NStatement.EMPTY;
		}
	| 'WHILE' error {
			after($1, new SQLSyntaxException("缺少谓词表达式"));
			$$ = NStatement.EMPTY;
		}
	;

loop_stmt:
	'LOOP' statement { $$ = new NLoopStmt((Token)$1, (NStatement)$2); }
	/* error */
	| 'LOOP' error {
			after($1, new SQLSyntaxException("缺少语句"));
			$$ = NStatement.EMPTY;
		}
	;

foreach_stmt:
	'FOREACH' VAR_REF 'IN' '(' query_stmt ')' 'LOOP' statement {
			$$ = new NForeachStmt((Token)$1, (TString)$2, (NQueryStmt)$5, (NStatement)$8);
		}
	| 'FOREACH' VAR_REF 'IN' query_invoke 'LOOP' statement {
			$$ = new NForeachStmt((Token)$1, (TString)$2, (NQueryInvoke)$4, (NStatement)$6);
		}
	/* error */
	| 'FOREACH' VAR_REF 'IN' '(' query_stmt ')' 'LOOP' error {
			after($7, new SQLSyntaxException("缺少语句"));
			$$ = NStatement.EMPTY;
		}
	| 'FOREACH' VAR_REF 'IN' '(' query_stmt ')' error {
			after($6, new SQLTokenNotFoundException("LOOP"));
			$$ = NStatement.EMPTY;
		}
	| 'FOREACH' VAR_REF 'IN' '(' query_stmt error {
			after($5, new SQLTokenNotFoundException(")"));
			$$ = NStatement.EMPTY;
		}
	| 'FOREACH' VAR_REF 'IN' '(' error {
			after($4, new SQLSyntaxException("缺少查询"));
			$$ = NStatement.EMPTY;
		}
	| 'FOREACH' VAR_REF 'IN' error {
			after($3, new SQLSyntaxException("缺少查询或查询调用"));
			$$ = NStatement.EMPTY;
		}
	| 'FOREACH' VAR_REF error {
			after($2, new SQLTokenNotFoundException("IN"));
			$$ = NStatement.EMPTY;
		}
	| 'FOREACH' error {
			after($1, new SQLSyntaxException("缺少变量名称"));
			$$ = NStatement.EMPTY;
		}
	;

break:
	'BREAK' { $$ = new NBreakStmt((Token)$1); }
	;

print:
	'PRINT' value_expr { $$ = new NPrintStmt((Token)$1, (NValueExpr)$2); }
	/* error */
	| 'PRINT' error {
			after($1, new SQLSyntaxException("缺少值表达式"));
			$$ = NStatement.EMPTY;
		}
	;

return:
	'RETURN' { $$ = new NReturnStmt((Token)$1, null); }
	| 'RETURN' value_expr { $$ = new NReturnStmt((Token)$1, (NValueExpr)$2); }
	;

function_declare:
	'DEFINE' 'FUNCTION' ID '(' param_declare_list_op ')' param_type
	'BEGIN'
		statement_list
	'END' {
			LinkList l = (LinkList)$5;
			NParamDeclare[] params = l == null ? null :
										l.toArray(new NParamDeclare[l.count()]);
			l = (LinkList)$9;
			NStatement[] stmts = l.toArray(new NStatement[l.count()]);
			$$ = new NFunctionDeclare((Token)$1, (Token)$10, (TString)$3,
						params, (NDataType)$7, stmts);
		}
	/* error */
	| 'DEFINE' 'FUNCTION' ID '(' param_declare_list_op ')' param_type
		'BEGIN' statement_list error {
			after($9, new SQLTokenNotFoundException("END"));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'FUNCTION' ID '(' param_declare_list_op ')' param_type
		'BEGIN' error {
			after($8, new SQLSyntaxException("缺少语句"));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'FUNCTION' ID '(' param_declare_list_op ')' param_type
		error {
			after($7, new SQLTokenNotFoundException("BEGIN"));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'FUNCTION' ID '(' param_declare_list_op ')' error {
			after($6, new SQLSyntaxException("缺少返回值类型"));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'FUNCTION' ID '(' error {
			after($4, new SQLTokenNotFoundException(")"));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'FUNCTION' ID error {
			after($3, new SQLTokenNotFoundException("("));
			$$ = NStatement.EMPTY;
		}
	| 'DEFINE' 'FUNCTION' error {
			after($2, new SQLSyntaxException("缺少名称"));
			$$ = NStatement.EMPTY;
		}
	;

%%

private SQLLexer lexer;
private SQLOutput out;
private boolean hasError;
private int token;
private DefineHolderImpl holder;
private ContextImpl<?, ?, ?> context;
private final static boolean debug = Boolean.getBoolean("org.eclipse.jt.debug.dnasqlparser");

private void yyerror(String s) {
	this.hasError = true;
	Token t = (Token) this.yylval;
	if (t != null) {
		this.out.raise(new SQLSyntaxException(t.line, t.col, "在符号"
				+ yyname[this.token] + "处发现语法错误"));
	} else {
		this.out.raise(new SQLSyntaxException("语法错误"));
	}
}

private void yyexception(Throwable ex) {
	this.hasError = true;
	if (ex instanceof SQLParseException) {
		this.out.raise((SQLParseException)ex);
	} else {
		throw Utils.tryThrowException(ex);
	}
}

private int yylex() {
	try {
		this.token = this.lexer.read();
	} catch (SQLParseException ex) {
		raise(null, true, ex);
		this.yyval = 0;
		return 0;
	}
    this.yylval = this.lexer.val;
    return this.token;
}

private NLiteral neg(NLiteral l) {
	if (l instanceof NLiteralInt) {
		NLiteralInt i = (NLiteralInt) l;
		i.value = -i.value;
	} else if (l instanceof NLiteralLong) {
		NLiteralLong i = (NLiteralLong) l;
		i.value = -i.value;
	} else if (l instanceof NLiteralDouble) {
		NLiteralDouble i = (NLiteralDouble) l;
		i.value = -i.value;
	} else {
		raise(l, true, new SQLSyntaxException("该类型的字面量不支持负值"));
	}
	return l;
}

@SuppressWarnings("unchecked")
private <T extends NStatement> T openSQL(Class<T> statementClass, DNASqlType type, String name) {
	if (this.holder != null) {
		T stmt = this.holder.find(statementClass, name);
		if (stmt == null) {
			throw new SQLNotSupportedException("找不到D&A Sql定义[" + name + "]");
		}
		return stmt;
	} else if (this.context != null) {
		Reader reader = context.occorAt.openDeclareScriptReader(name, type);
		SQLScript s = new SQLParser().parse(new SQLLexer(reader),
				this.out, null, this.context);
		if (s == null) {
			throw new SQLNotSupportedException("无法解析D&A Sql[" + name + "]");
		}
		Object stmt = s.content(null);
		if (stmt == null) {
			throw new SQLNotSupportedException("找不到D&A Sql定义[" + name + "]");
		}
		if (!statementClass.isInstance(stmt)) {
			throw new SQLSyntaxException("D&A Sql定义[" + name + "]不是[" + type + "]类型");
		}
		return (T)stmt;
	}
	throw new SQLNotSupportedException("当前环境不支持读取D&A Sql");
}

private void after(Object obj, SQLParseException ex) {
	raise(obj, false, ex);
}

private void at(Object obj, SQLParseException ex) {
	raise(obj, true, ex);
}

private void raise(Object obj, boolean startOrEnd, SQLParseException ex) {
	this.hasError = true;
	this.yyval = null;
	if (obj != null && obj instanceof TextLocalizable) {
		TextLocalizable n = (TextLocalizable)obj;
		if (startOrEnd) {
			ex.line = n.startLine();
			ex.col = n.startCol();
		} else {
			ex.line = n.endLine();
			ex.col = n.endCol();
		}
	}
	if (this.out != null)
		this.out.raise(ex);
}

public SQLScript parse(SQLLexer lex, SQLOutput out, DefineHolderImpl holder, ContextImpl<?, ?, ?> context) {
	this.lexer = lex;
    this.out = out;
    this.holder = holder;
    this.context = context;
    this.yydebug = debug;
	this.hasError = false;
	if (this.yyparse() == 0 && !this.hasError) {
		return (SQLScript)this.yyval;
	}
	return null;
}

public SQLScript parse(SQLLexer lex, DefineHolderImpl holder, ContextImpl<?, ?, ?> context) {
	return this.parse(lex, SQLOutput.PRINT_TO_CONSOLE, holder, context);
}