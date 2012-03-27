package org.eclipse.jt.core.impl;

import java.io.IOException;
import org.eclipse.jt.core.spi.sql.*;
%%

%byaccj
%unicode
%ignorecase
%line
%column
%pack
%class SQLLexer
%public
%final

%{
public Token val;

private StringBuilder sb = new StringBuilder();

public int read() {
    try {
    	this.val = null;
        return yylex();
    } catch (IOException ioe) {
        throw new IllegalStateException(ioe);
    }
}

private int SETFUNCTION(int t, NAggregateExpr.Func func) {
	this.val = new TSetFunction(func, yyline, yycolumn, yylength());
	return t;
}

private int HIERARCHY(int t, NHierarchyExpr.Keywords keyword) {
	this.val = new THierarchy(keyword, yyline, yycolumn, yylength());
	return t;
}

private int VALUECOMPARE(int t, NCompareExpr.Operator op) {
	this.val = new TValueCompare(op, yyline, yycolumn, yylength());
	return t;
}

private int STRCOMPARE(int t, NStrCompareExpr.Keywords keyword) {
	this.val = new TStrCompare(keyword, yyline, yycolumn, yylength());
	return t;
}

private int KEYWORD(int t) {
	this.val = new Token(yyline, yycolumn, yylength());
	return t;
}

private int IDENTIFIER(String value) {
	this.val = new TString(value, yyline, yycolumn, yylength());
	return SQLParser.ID;
}

private int STRING(String value) {
	this.val = new TString(value, yyline, yycolumn, yylength());
	return SQLParser.STR_VAL;
}

private int BOOLEAN(int t, boolean value) {
	this.val = new TBoolean(value, yyline, yycolumn, yylength());
	return t;
}

%}

DOUBLE = [0-9]+ "." [0-9]+ | [0-9]+ ("." [0-9]+)? [eE] "-"? [0-9]+
INT = [0-9]+
UNQUOTED_ID = [a-zA-Z] [a-zA-Z0-9_]*
VAR_REF = "@" {UNQUOTED_ID}
ENV_REF = "#" {UNQUOTED_ID}
COMMENT_TEXT = [^\r\n]*

%state STR
%state QUOTED_ID
%state COMMENT

%%

<YYINITIAL> {
	"'" { sb.setLength(0); yybegin(STR); }
	\" { sb.setLength(0); yybegin(QUOTED_ID); }
	"--" { yybegin(COMMENT); }
	
	">" { return VALUECOMPARE('>', NCompareExpr.Operator.GT); }
	"<" { return VALUECOMPARE('<', NCompareExpr.Operator.LT); }
	"=" { return VALUECOMPARE('=', NCompareExpr.Operator.EQ); }
	">=" { return VALUECOMPARE(SQLParser.GE, NCompareExpr.Operator.GE); }
	"<=" { return VALUECOMPARE(SQLParser.LE, NCompareExpr.Operator.LE); }
	"<>" { return VALUECOMPARE(SQLParser.NE, NCompareExpr.Operator.NE); }

	"," { return KEYWORD(','); }
	"||" { return KEYWORD(SQLParser.CB); }
	"-" { return KEYWORD('-'); }
	"+" { return KEYWORD('+'); }
	"*" { return KEYWORD('*'); }
	"/" { return KEYWORD('/'); }
	"%" { return KEYWORD('%'); }
	"," { return KEYWORD(','); }
	"." { return KEYWORD('.'); }
	"(" { return KEYWORD('('); }
	")" { return KEYWORD(')'); }
	";" { return KEYWORD(';'); }

	"DEFINE" { return KEYWORD(SQLParser.DEFINE); }
	"QUERY" { return KEYWORD(SQLParser.QUERY); }
	"ORM" { return KEYWORD(SQLParser.ORM); }
	"MAPPING" { return KEYWORD(SQLParser.MAPPING); }
	"OVERRIDE" { return KEYWORD(SQLParser.OVERRIDE); }
	"TABLE" { return KEYWORD(SQLParser.TABLE); }
	"ABSTRACT" { return KEYWORD(SQLParser.ABSTRACT); }
	"EXTEND" { return KEYWORD(SQLParser.EXTEND); }
	"BEGIN" { return KEYWORD(SQLParser.BEGIN); }
	"END" { return KEYWORD(SQLParser.END); }
	"DEFAULT" { return KEYWORD(SQLParser.DEFAULT); }
	
	"WITH" { return KEYWORD(SQLParser.WITH); }
	"AS" { return KEYWORD(SQLParser.AS); }
	"SELECT" { return KEYWORD(SQLParser.SELECT); }
	"DISTINCT" { return KEYWORD(SQLParser.DISTINCT); }
	"ALL" { return KEYWORD(SQLParser.ALL); }
	"FOR" { return KEYWORD(SQLParser.FOR); }
	"FROM" { return KEYWORD(SQLParser.FROM); }
	"LEFT" { return KEYWORD(SQLParser.LEFT); }
	"RIGHT" { return KEYWORD(SQLParser.RIGHT); }
	"FULL" { return KEYWORD(SQLParser.FULL); }
	"JOIN" { return KEYWORD(SQLParser.JOIN); }
	"RELATE" { return KEYWORD(SQLParser.RELATE); }
	"ON" { return KEYWORD(SQLParser.ON); }
	"GROUP" { return KEYWORD(SQLParser.GROUP); }
	"ROLLUP" { return KEYWORD(SQLParser.ROLLUP); }
	"HAVING" { return KEYWORD(SQLParser.HAVING); }
	"ORDER" { return KEYWORD(SQLParser.ORDER); }
	"BY" { return KEYWORD(SQLParser.BY); }
	"ASC" { return KEYWORD(SQLParser.ASC); }
	"DESC" { return KEYWORD(SQLParser.DESC); }
	"WHERE" { return KEYWORD(SQLParser.WHERE); }
	"UNION" { return KEYWORD(SQLParser.UNION); }
	
	"INSERT" { return KEYWORD(SQLParser.INSERT); }
	"INTO" { return KEYWORD(SQLParser.INTO); }
	"VALUES" { return KEYWORD(SQLParser.VALUES); }
	
	"UPDATE" { return KEYWORD(SQLParser.UPDATE); }
	"SET" { return KEYWORD(SQLParser.SET); }
	
	"DELETE" { return KEYWORD(SQLParser.DELETE); }
	
	"AVG" { return SETFUNCTION(SQLParser.AVG, NAggregateExpr.Func.AVG); }
	"MAX" { return SETFUNCTION(SQLParser.MAX, NAggregateExpr.Func.MAX); }
	"MIN" { return SETFUNCTION(SQLParser.MIN, NAggregateExpr.Func.MIN); }
	"SUM" { return SETFUNCTION(SQLParser.SUM, NAggregateExpr.Func.SUM); }
	"COUNT" { return SETFUNCTION(SQLParser.COUNT, NAggregateExpr.Func.COUNT); }
	
	"AND" { return KEYWORD(SQLParser.AND); }
	"OR" { return KEYWORD(SQLParser.OR); }
	"NOT" { return KEYWORD(SQLParser.NOT); }
	
	"LIKE" { return STRCOMPARE(SQLParser.LIKE, NStrCompareExpr.Keywords.LIKE); }
	"ESCAPE" { return KEYWORD(SQLParser.ESCAPE); }
	"CONTAINS" { return STRCOMPARE(SQLParser.CONTAINS, NStrCompareExpr.Keywords.CONTAINS); }
	"STARTS_WITH" { return STRCOMPARE(SQLParser.STARTS_WITH, NStrCompareExpr.Keywords.STARTS_WITH); }
	"ENDS_WITH" { return STRCOMPARE(SQLParser.ENDS_WITH, NStrCompareExpr.Keywords.ENDS_WITH); }

	"CHILDOF" { return HIERARCHY(SQLParser.CHILDOF, NHierarchyExpr.Keywords.CHILDOF); }
	"PARENTOF" { return HIERARCHY(SQLParser.PARENTOF, NHierarchyExpr.Keywords.PARENTOF); }
	"ANCESTOROF" { return HIERARCHY(SQLParser.ANCESTOROF, NHierarchyExpr.Keywords.ANCESTOROF); }
	"DESCENDANTOF" { return HIERARCHY(SQLParser.DESCENDANTOF, NHierarchyExpr.Keywords.DESCENDANTOF); }
	"RELATIVE" { return KEYWORD(SQLParser.RELATIVE); }
	"USING" { return KEYWORD(SQLParser.USING); }
	"RANGE" { return KEYWORD(SQLParser.RANGE); }
	"H_AID" { return KEYWORD(SQLParser.H_AID); }
	"H_LV" { return KEYWORD(SQLParser.H_LV); }
	"ABO" { return KEYWORD(SQLParser.ABO); }
	"REL" { return KEYWORD(SQLParser.REL); }
	
	"IN" { return KEYWORD(SQLParser.IN); }
	"BETWEEN" { return KEYWORD(SQLParser.BETWEEN); }
	"IS" { return KEYWORD(SQLParser.IS); }
	"EXISTS" { return KEYWORD(SQLParser.EXISTS); }
	
	"CASE" { return KEYWORD(SQLParser.CASE); }
	"WHEN" { return KEYWORD(SQLParser.WHEN); }
	"THEN" { return KEYWORD(SQLParser.THEN); }
	"ELSE" { return KEYWORD(SQLParser.ELSE); }
	"COALESCE" { return KEYWORD(SQLParser.COALESCE); }
	
	"SHORT" { return KEYWORD(SQLParser.SHORT); }
	"INT" { return KEYWORD(SQLParser.INT); }
	"LONG" { return KEYWORD(SQLParser.LONG); }
	"FLOAT" { return KEYWORD(SQLParser.FLOAT); }
	"DOUBLE" { return KEYWORD(SQLParser.DOUBLE); }
	"BOOLEAN" { return KEYWORD(SQLParser.BOOLEAN); }
	"BYTE" { return KEYWORD(SQLParser.BYTE); }
	"BYTES" { return KEYWORD(SQLParser.BYTES); }
	"DATE" { return KEYWORD(SQLParser.DATE); }
	"STRING" { return KEYWORD(SQLParser.STRING); }
	"GUID" { return KEYWORD(SQLParser.GUID); }
	"ENUM" { return KEYWORD(SQLParser.ENUM); }
	
	"FIELDS" { return KEYWORD(SQLParser.FIELDS); }
	"PRIMARY" { return KEYWORD(SQLParser.PRIMARY); }
	"KEY" { return KEYWORD(SQLParser.KEY); }
	"RELATION" { return KEYWORD(SQLParser.RELATION); }
	"TO" { return KEYWORD(SQLParser.TO); }
	"INDEXES" { return KEYWORD(SQLParser.INDEXES); }
	"UNIQUE" { return KEYWORD(SQLParser.UNIQUE); }
	"RELATIONS" { return KEYWORD(SQLParser.RELATIONS); }
	"HIERARCHIES" { return KEYWORD(SQLParser.HIERARCHIES); }
	"MAXLEVEL" { return KEYWORD(SQLParser.MAXLEVEL); }
	"PARTITION" { return KEYWORD(SQLParser.PARTITION); }
	"VAVLE" { return KEYWORD(SQLParser.VAVLE); }
	"MAXCOUNT" { return KEYWORD(SQLParser.MAXCOUNT); }
	
	"CHAR" { return KEYWORD(SQLParser.CHAR); }
	"NCHAR" { return KEYWORD(SQLParser.NCHAR); }
	"VARCHAR" { return KEYWORD(SQLParser.VARCHAR); }
	"NVARCHAR" { return KEYWORD(SQLParser.NVARCHAR); }
	"TEXT" { return KEYWORD(SQLParser.TEXT); }
	"NTEXT" { return KEYWORD(SQLParser.NTEXT); }
	"BINARY" { return KEYWORD(SQLParser.BINARY); }
	"VARBINARY" { return KEYWORD(SQLParser.VARBINARY); }
	"BLOB" { return KEYWORD(SQLParser.BLOB); }
	"NUMERIC" { return KEYWORD(SQLParser.NUMERIC); }
	
	"TRUE" { return BOOLEAN(SQLParser.FALSE, true); }
	"FALSE" { return BOOLEAN(SQLParser.TRUE, false); }
	"NULL" { return KEYWORD(SQLParser.NULL); }
	"COMMENT" { return KEYWORD(SQLParser.COMMENT); }
	
	"PROCEDURE" { return KEYWORD(SQLParser.PROCEDURE); }
	"FUNCTION" { return KEYWORD(SQLParser.FUNCTION); }
	"VAR" { return KEYWORD(SQLParser.VAR); }
	"IF" { return KEYWORD(SQLParser.IF); }
	"FOREACH" { return KEYWORD(SQLParser.FOREACH); }
	"WHILE" { return KEYWORD(SQLParser.WHILE); }
	"LOOP" { return KEYWORD(SQLParser.LOOP); }
	"BREAK" { return KEYWORD(SQLParser.BREAK); }
	"PRINT" { return KEYWORD(SQLParser.PRINT); }
	"RETURN" { return KEYWORD(SQLParser.RETURN); }
	"RETURNING" { return KEYWORD(SQLParser.RETURNING); }
	"CURRENT" { return KEYWORD(SQLParser.CURRENT); }
	"OF" { return KEYWORD(SQLParser.OF); }
	"OUT" { return KEYWORD(SQLParser.OUT); }
	"INOUT" { return KEYWORD(SQLParser.INOUT); }
	"RECORDSET" { return KEYWORD(SQLParser.RECORDSET); }

	{DOUBLE} {
		try {
		    this.val = new TDouble(Double.parseDouble(yytext()), yyline, yycolumn, yylength());
			return SQLParser.DOUBLE_VAL;
		} catch (NumberFormatException nfe) {
		    throw new SQLNumberFormatException(yyline, yycolumn, yytext());
		}
	}
	{INT} {
		try {
			long l = Long.parseLong(yytext());
			int i = (int)l;
			if (l == i) {
				this.val = new TInt(i, yyline, yycolumn, yylength());
				return SQLParser.INT_VAL;
			}
			this.val = new TLong(l, yyline, yycolumn, yylength());
			return SQLParser.LONG_VAL;
		} catch (NumberFormatException nfe) {
		    throw new SQLNumberFormatException(yyline, yycolumn, yytext());
		}
	}
	{UNQUOTED_ID} {
		return IDENTIFIER(yytext());
	}
	{VAR_REF} {
		this.val = new TString(yytext(), yyline, yycolumn, yylength());
		return SQLParser.VAR_REF;
	}
	{ENV_REF} {
		this.val = new TString(yytext(), yyline, yycolumn, yylength());
		return SQLParser.ENV_REF;
	}
	
	[ \t\r\n]+ { }
	. { throw new SQLTokenUndefinedException(yyline, yycolumn, yytext()); }
}

<STR> {
	"''" { sb.append("'"); }
	[^'\r\n]+ { sb.append(yytext()); }
	"'" {
		yybegin(YYINITIAL);
		return STRING(sb.toString());
	}
}

<QUOTED_ID> {
	"\"\"" { sb.append("\""); }
	[^\"]+ { sb.append(yytext()); }
	\" {
		yybegin(YYINITIAL);
		return IDENTIFIER(sb.toString());
	}
}

<COMMENT> {
	[\r\n] { yybegin(YYINITIAL); }
	{COMMENT_TEXT} {}
}