//### This file created by BYACC 1.8(/Java extension  1.15)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//###           04 Mar 02  -- Yuval Oren  -- improved java performance, added options
//###           14 Mar 02  -- Tomas Hurka -- -d support, static initializer workaround
//### Please send bug reports to tom@hukatronic.cz
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";



package org.eclipse.jt.core.impl;



//#line 1 "D:\Workspace\D&A\dnasql\meta\dnasql.y"

import java.io.Reader;

import org.eclipse.jt.core.da.SQLFuncSpec;
import org.eclipse.jt.core.def.DNASqlType;
import org.eclipse.jt.core.def.query.GroupByType;
import org.eclipse.jt.core.def.table.TableJoinType;
import org.eclipse.jt.core.spi.sql.SQLFunctionUndefinedException;
import org.eclipse.jt.core.spi.sql.SQLNotSupportedException;
import org.eclipse.jt.core.spi.sql.SQLOutput;
import org.eclipse.jt.core.spi.sql.SQLParseException;
import org.eclipse.jt.core.spi.sql.SQLSyntaxException;
import org.eclipse.jt.core.spi.sql.SQLTokenNotFoundException;
import org.eclipse.jt.core.spi.sql.SQLValueFormatException;





public class SQLParser
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
  if (yydebug)
    System.out.println(msg);
}

//########## STATE STACK ##########
final static int YYSTACKSIZE = 500;  //maximum stack size
int statestk[] = new int[YYSTACKSIZE]; //state stack
int stateptr;
int stateptrmax;                     //highest index of stackptr
int statemax;                        //state when highest index reached
//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
final void state_push(int state)
{
  try {
		stateptr++;
		statestk[stateptr]=state;
	 }
	 catch (ArrayIndexOutOfBoundsException e) {
     int oldsize = statestk.length;
     int newsize = oldsize * 2;
     int[] newstack = new int[newsize];
     System.arraycopy(statestk,0,newstack,0,oldsize);
     statestk = newstack;
     statestk[stateptr]=state;
  }
}
final int state_pop()
{
  return statestk[stateptr--];
}
final void state_drop(int cnt)
{
  stateptr -= cnt; 
}
final int state_peek(int relative)
{
  return statestk[stateptr-relative];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
final boolean init_stacks()
{
  stateptr = -1;
  val_init();
  return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
  System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
  for (i=0;i<count;i++)
    System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
  System.out.println("======================");
}


//########## SEMANTIC VALUES ##########
//## **user defined:Object
String   yytext;//user variable to return contextual strings
Object yyval; //used to return semantic vals from action routines
Object yylval;//the 'lval' (result) I got from yylex()
Object valstk[] = new Object[YYSTACKSIZE];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
final void val_init()
{
  yyval=new Object();
  yylval=new Object();
  valptr=-1;
}
final void val_push(Object val)
{
  try {
    valptr++;
    valstk[valptr]=val;
  }
  catch (ArrayIndexOutOfBoundsException e) {
    int oldsize = valstk.length;
    int newsize = oldsize*2;
    Object[] newstack = new Object[newsize];
    System.arraycopy(valstk,0,newstack,0,oldsize);
    valstk = newstack;
    valstk[valptr]=val;
  }
}
final Object val_pop()
{
  return valstk[valptr--];
}
final void val_drop(int cnt)
{
  valptr -= cnt;
}
final Object val_peek(int relative)
{
  return valstk[valptr-relative];
}
final Object dup_yyval(Object val)
{
  return val;
}
//#### end semantic value section ####
public final static short ID=257;
public final static short VAR_REF=258;
public final static short ENV_REF=259;
public final static short INT_VAL=260;
public final static short LONG_VAL=261;
public final static short DOUBLE_VAL=262;
public final static short STR_VAL=263;
public final static short LE=264;
public final static short GE=265;
public final static short NE=266;
public final static short CB=267;
public final static short IF=268;
public final static short COMMENT=269;
public final static short WHITESPACE=270;
public final static short IFX=271;
public final static short ELSE=272;
public final static short DEFINE=273;
public final static short INOUT=274;
public final static short OUT=275;
public final static short NOT=276;
public final static short NULL=277;
public final static short DEFAULT=278;
public final static short BOOLEAN=279;
public final static short BYTE=280;
public final static short BYTES=281;
public final static short DATE=282;
public final static short DOUBLE=283;
public final static short ENUM=284;
public final static short FLOAT=285;
public final static short GUID=286;
public final static short INT=287;
public final static short LONG=288;
public final static short SHORT=289;
public final static short STRING=290;
public final static short RECORDSET=291;
public final static short TRUE=292;
public final static short FALSE=293;
public final static short OR=294;
public final static short AND=295;
public final static short BETWEEN=296;
public final static short LIKE=297;
public final static short ESCAPE=298;
public final static short STARTS_WITH=299;
public final static short ENDS_WITH=300;
public final static short CONTAINS=301;
public final static short IN=302;
public final static short IS=303;
public final static short EXISTS=304;
public final static short USING=305;
public final static short RELATIVE=306;
public final static short RANGE=307;
public final static short LEAF=308;
public final static short CHILDOF=309;
public final static short PARENTOF=310;
public final static short ANCESTOROF=311;
public final static short DESCENDANTOF=312;
public final static short COUNT=313;
public final static short SUM=314;
public final static short AVG=315;
public final static short MAX=316;
public final static short MIN=317;
public final static short H_LV=318;
public final static short H_AID=319;
public final static short REL=320;
public final static short ABO=321;
public final static short COALESCE=322;
public final static short CASE=323;
public final static short END=324;
public final static short WHEN=325;
public final static short THEN=326;
public final static short QUERY=327;
public final static short BEGIN=328;
public final static short WITH=329;
public final static short UNION=330;
public final static short ALL=331;
public final static short AS=332;
public final static short SELECT=333;
public final static short FROM=334;
public final static short WHERE=335;
public final static short CURRENT=336;
public final static short OF=337;
public final static short GROUP=338;
public final static short BY=339;
public final static short ROLLUP=340;
public final static short HAVING=341;
public final static short ORDER=342;
public final static short ASC=343;
public final static short DESC=344;
public final static short JOIN=345;
public final static short ON=346;
public final static short RELATE=347;
public final static short FOR=348;
public final static short UPDATE=349;
public final static short LEFT=350;
public final static short RIGHT=351;
public final static short FULL=352;
public final static short DISTINCT=353;
public final static short ORM=354;
public final static short MAPPING=355;
public final static short OVERRIDE=356;
public final static short RETURNING=357;
public final static short INTO=358;
public final static short INSERT=359;
public final static short VALUES=360;
public final static short SET=361;
public final static short DELETE=362;
public final static short TABLE=363;
public final static short ABSTRACT=364;
public final static short EXTEND=365;
public final static short FIELDS=366;
public final static short INDEXES=367;
public final static short RELATIONS=368;
public final static short HIERARCHIES=369;
public final static short PARTITION=370;
public final static short VAVLE=371;
public final static short MAXCOUNT=372;
public final static short PRIMARY=373;
public final static short KEY=374;
public final static short BINARY=375;
public final static short VARBINARY=376;
public final static short BLOB=377;
public final static short CHAR=378;
public final static short VARCHAR=379;
public final static short NCHAR=380;
public final static short NVARCHAR=381;
public final static short TEXT=382;
public final static short NTEXT=383;
public final static short NUMERIC=384;
public final static short RELATION=385;
public final static short TO=386;
public final static short UNIQUE=387;
public final static short MAXLEVEL=388;
public final static short PROCEDURE=389;
public final static short VAR=390;
public final static short WHILE=391;
public final static short LOOP=392;
public final static short FOREACH=393;
public final static short BREAK=394;
public final static short PRINT=395;
public final static short RETURN=396;
public final static short FUNCTION=397;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    1,    1,    1,    1,    1,    1,    1,    1,    1,
   10,   11,   11,   11,   11,   11,   11,   11,   11,   11,
   11,   11,   11,   11,   11,   11,   13,   13,   13,   14,
   14,   14,   14,   12,   12,   12,   12,   12,   12,   12,
   12,   12,   12,   12,   12,   12,   12,   12,   12,   17,
   17,   17,   18,   18,   19,   19,   20,   15,   15,   15,
   15,   15,   15,   15,   15,   15,   15,   15,   15,   21,
   21,   21,   22,   22,   22,   23,   23,   23,   24,   24,
   24,   24,   24,   24,   24,   24,   24,   24,   24,   24,
   26,   26,   26,   26,   26,   26,   27,   27,   27,   27,
   28,   28,   36,   36,   36,   29,   29,   37,   37,   37,
   30,   30,   38,   38,   38,   38,   39,   39,   39,   31,
   31,   35,   35,   32,   32,   32,   32,   33,   33,   33,
   33,   33,   33,   33,   33,   33,   33,   33,   33,   41,
   41,   41,   41,   34,   34,   34,   34,   34,   34,   34,
   25,   25,   25,   25,   25,   25,   25,   42,   42,   42,
   42,   42,   42,   43,   43,   43,   45,   45,   44,   44,
   44,   44,   44,   44,   44,   44,   44,   44,   44,   44,
   44,   44,   46,   46,   46,   46,   46,   46,   52,   52,
   52,   52,   52,   47,   47,   47,   47,   48,   48,   48,
   48,   48,   48,   48,   48,   48,   48,   48,   48,   48,
   48,   48,   48,   48,   48,   49,   49,   49,   49,   50,
   50,   50,   50,   55,   55,   57,   57,   57,   57,   56,
   56,   56,   51,   51,   58,   58,   59,   59,   59,   54,
   54,   54,   60,   60,    2,    2,    2,    2,    2,    2,
    2,    2,   61,   61,   61,   61,   63,   63,   63,   63,
   63,   63,   63,   66,   66,   66,   65,   65,   67,   67,
   40,   62,   62,   62,   73,   73,   73,   73,   68,   68,
   69,   69,   75,   75,   75,   70,   70,   70,   70,   70,
   70,   71,   71,   71,   71,   71,   71,   77,   77,   77,
   72,   72,   72,   64,   64,   64,   64,   78,   78,   79,
   79,   79,   79,   79,   79,   74,   74,   74,   80,   80,
   80,   76,   76,   76,   76,   76,   76,   76,   76,   76,
   76,   76,   82,   82,   82,   82,   82,   82,   82,   82,
   82,   82,   81,   81,   81,   81,   53,   53,   53,    3,
    3,    3,    3,    3,    3,    3,    3,    3,    3,    3,
    3,    3,   16,   83,   83,   83,   84,   84,   84,   84,
   84,    4,    4,    4,    4,    4,    4,    4,    4,   85,
   85,   85,   85,   86,   86,   86,   88,   88,   87,   87,
   87,   87,   87,   87,   90,   90,   90,   89,   89,   89,
    5,    5,    5,    5,    5,    5,    5,    5,   91,   91,
   92,   92,   93,   93,   94,   94,   94,   95,   95,   95,
    6,    6,    6,    6,    6,    6,    6,    6,   96,   97,
   97,   97,    7,    7,    7,    7,    7,    7,    7,    7,
    7,   98,   98,   98,   99,   99,  100,  100,  106,  106,
  107,  107,  107,  101,  101,  101,  102,  102,  102,  103,
  103,  103,  104,  104,  104,  104,  104,  104,  104,  104,
  104,  105,  105,  105,  112,  112,  112,  112,  112,  114,
  114,  115,  115,  115,  113,  113,  113,  113,  113,  113,
  113,  113,  113,  113,  113,  113,  113,  113,  113,  113,
  113,  113,  113,  113,  113,  113,  113,  113,  113,  113,
  113,  113,  113,  113,  113,  113,  113,  113,  113,  113,
  113,  113,  113,  113,  113,  116,  116,  116,  116,  116,
  116,  108,  108,  108,  117,  117,  117,  117,  117,  117,
  117,  117,  117,  118,  118,  118,  119,  119,  119,  109,
  109,  109,  120,  120,  120,  120,  120,  110,  110,  110,
  121,  121,  121,  121,  121,  111,  111,  111,    8,    8,
    8,    8,    8,    8,    8,  122,  122,  123,  123,  123,
  123,  123,  123,  123,  123,  123,  123,  123,  123,  123,
  123,  123,  123,  123,  123,  123,  123,  123,  123,  123,
  133,  133,  133,  124,  124,  124,  124,  124,  134,  134,
  134,  134,  134,  134,  134,  134,  134,  134,  134,  134,
  134,  134,  134,  125,  125,  125,  125,  125,  125,  125,
  125,  125,  125,  135,  135,  135,  135,  126,  126,  126,
  126,  126,  126,  127,  127,  127,  127,  128,  128,  129,
  129,  129,  129,  129,  129,  129,  129,  129,  130,  131,
  131,  132,  132,    9,    9,    9,    9,    9,    9,    9,
    9,
};
final static short yylen[] = {                            2,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    2,    4,    4,    3,    3,    2,    5,    5,    4,    4,
    3,    3,    4,    2,    3,    3,    2,    2,    1,    2,
    3,    2,    1,    1,    1,    1,    1,    1,    4,    1,
    1,    1,    1,    1,    1,    1,    4,    3,    2,    3,
    1,    3,    1,    0,    3,    3,    1,    1,    1,    1,
    1,    1,    1,    2,    2,    2,    2,    2,    2,    3,
    1,    3,    3,    1,    3,    2,    1,    2,    3,    3,
    1,    1,    1,    1,    1,    1,    1,    1,    3,    3,
    1,    1,    1,    1,    1,    1,    6,    6,    5,    4,
    5,    4,    2,    0,    2,    4,    4,    1,    1,    1,
    4,    4,    3,    3,    3,    2,    3,    1,    3,    4,
    3,    1,    0,    4,    4,    3,    2,    5,    7,    7,
    5,    7,    7,    5,    4,    3,    5,    4,    3,    1,
    1,    1,    1,    9,   11,   11,    9,    8,    7,    6,
    3,    3,    3,    1,    3,    3,    3,    3,    3,    3,
    1,    3,    3,    2,    1,    2,    3,    1,    1,    1,
    1,    1,    3,    3,    1,    1,    1,    1,    1,    1,
    3,    2,    5,    4,    4,    5,    3,    2,    1,    1,
    1,    1,    1,    4,    3,    4,    3,    6,    6,    8,
    8,    6,    5,    4,    3,    2,    8,    8,    7,    7,
    6,    5,    4,    3,    2,    4,    4,    3,    2,    5,
    5,    3,    2,    2,    1,    4,    4,    3,    2,    2,
    0,    2,    4,    4,    2,    1,    4,    4,    3,    3,
    1,    3,    4,    3,    9,   10,    9,    8,    7,    5,
    4,    3,    4,    2,    3,    2,    3,    4,    3,    4,
    1,    3,    3,    3,    3,    2,    1,    1,    5,    2,
    2,    3,    1,    3,    5,    5,    4,    3,    3,    2,
    2,    2,    3,    1,    3,    2,    4,    0,    2,    4,
    3,    3,    5,    0,    5,    3,    2,    3,    1,    3,
    2,    0,    2,    3,    0,    3,    2,    3,    1,    1,
    2,    2,    1,    2,    2,    3,    1,    3,    1,    3,
    3,    6,    6,    8,    1,    6,    5,    4,    8,    6,
    5,    4,    3,    5,    1,    3,    5,    3,    3,    5,
    3,    5,    1,    1,    1,    0,    1,    1,    0,   11,
   13,   11,   10,    9,    8,   10,    9,    8,    7,    5,
    4,    3,    1,    3,    1,    3,    4,    0,    4,    3,
    2,    9,   10,    9,    8,    7,    5,    4,    3,    3,
    3,    3,    2,    3,    3,    2,    3,    3,    7,    7,
    6,    5,    4,    3,    3,    1,    3,    3,    1,    3,
    9,   10,    9,    8,    7,    5,    4,    3,    4,    2,
    2,    2,    2,    2,    3,    1,    3,    3,    3,    2,
    9,   10,    9,    8,    7,    5,    4,    3,    3,    3,
    3,    2,   12,   13,    6,    4,    3,    7,    5,    4,
    3,    2,    0,    2,    2,    2,    1,    0,    2,    1,
    4,    3,    2,    2,    0,    2,    2,    0,    2,    2,
    0,    2,    8,    0,    8,    7,    6,    5,    4,    3,
    2,    3,    1,    3,    4,    6,    5,    6,    2,    1,
    0,    4,    5,    0,    1,    1,    1,    1,    1,    1,
    1,    1,    4,    4,    1,    4,    4,    4,    4,    1,
    1,    6,    4,    3,    2,    4,    3,    2,    4,    3,
    2,    4,    3,    2,    4,    3,    2,    4,    3,    2,
    6,    5,    4,    3,    2,    6,    6,    5,    4,    3,
    2,    3,    1,    3,    4,    5,    4,    3,    2,    5,
    4,    3,    2,    3,    1,    3,    2,    2,    2,    3,
    1,    3,    5,    5,    4,    3,    2,    3,    1,    3,
    5,    5,    4,    3,    2,    3,    1,    3,    9,    9,
    8,    7,    5,    4,    3,    2,    1,    2,    2,    2,
    1,    1,    2,    2,    1,    1,    1,    1,    2,    2,
    2,    1,    2,    2,    2,    2,    2,    2,    2,    2,
    3,    3,    2,    3,    5,    5,    3,    2,    1,    1,
    1,    1,    1,    4,    1,    1,    1,    1,    1,    1,
    4,    3,    2,    3,    7,    3,    5,    3,    7,    6,
    5,    3,    3,    3,    1,    3,    2,    4,    6,    6,
    4,    3,    2,    4,    4,    3,    2,    2,    2,    8,
    6,    8,    7,    6,    5,    4,    3,    2,    1,    2,
    2,    1,    2,   10,   10,    9,    8,    7,    5,    4,
    3,
};
final static short yydefred[] = {                         0,
    0,    0,    1,    2,    3,    4,    5,    6,    7,    8,
    9,   10,   11,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  252,    0,  408,    0,  362,    0,  379,    0,
  428,    0,  437,    0,  441,    0,  575,    0,  671,    0,
  251,    0,  407,    0,  361,    0,  378,    0,  427,    0,
  436,    0,    0,  440,    0,  574,    0,  670,    0,  250,
    0,    0,    0,   51,    0,    0,  406,    0,  360,    0,
  377,    0,  426,    0,  444,  442,    0,  439,    0,  573,
    0,  669,    0,   24,   34,   35,   36,   37,   38,    0,
   40,   41,   42,   43,   44,   45,   46,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  435,    0,    0,    0,
    0,    0,   49,    0,   59,   60,   61,   58,    0,   29,
    0,    0,    0,    0,   62,   63,    0,    0,   33,   25,
    0,   26,    0,   52,   50,  249,    0,  405,    0,  359,
    0,    0,  376,    0,  425,    0,  446,    0,    0,  473,
    0,    0,    0,  450,  438,    0,  572,    0,  668,    0,
   48,  365,    0,    0,   28,   27,   32,    0,   30,   69,
   66,   67,   64,   68,   65,   12,   13,    0,    0,   23,
  248,    0,    0,    0,    0,    0,    0,    0,  261,    0,
  404,    0,    0,    0,  355,    0,  358,    0,  375,    0,
    0,    0,  424,    0,    0,    0,  479,  485,  486,  487,
  488,  489,  490,  491,  492,    0,    0,  495,    0,    0,
    0,    0,  500,  501,    0,    0,    0,  453,    0,    0,
    0,  449,    0,  571,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  659,    0,    0,  581,  582,    0,    0,
    0,    0,  577,    0,    0,  585,  586,  587,  588,    0,
    0,    0,  592,    0,  667,    0,   47,   39,    0,   31,
   17,   18,  266,    0,    0,  256,    0,    0,  273,  280,
  347,  348,    0,    0,  247,  245,    0,    0,    0,    0,
  270,    0,    0,  412,   57,    0,    0,    0,  325,  403,
  401,    0,  410,    0,    0,  354,    0,  357,    0,  386,
    0,  374,  372,    0,  383,    0,    0,    0,  432,    0,
  423,  421,    0,    0,    0,  505,    0,  508,    0,  511,
    0,  514,    0,  517,    0,  520,    0,  525,    0,  480,
    0,  474,  472,  452,    0,  456,    0,    0,    0,  533,
    0,    0,    0,    0,  643,    0,  168,    0,  172,    0,
    0,    0,  189,  190,  191,  192,  193,    0,    0,    0,
    0,  170,  169,    0,    0,   74,   77,    0,   81,   82,
   83,   84,   85,   86,   87,   88,    0,  161,  165,    0,
  175,  176,  177,  178,  179,  180,    0,  635,    0,  603,
    0,  608,    0,  647,    0,  649,  648,  658,    0,  661,
    0,    0,    0,    0,  593,  578,  594,  579,  595,  580,
  570,  569,  576,  596,  583,  597,  584,  598,  589,  599,
  590,  600,  591,  637,    0,    0,  666,    0,  366,  364,
  265,  264,  271,    0,  255,    0,    0,    0,    0,  317,
  262,    0,  257,  267,  268,  246,  307,    0,  263,    0,
  259,  282,    0,    0,    0,    0,    0,    0,    0,    0,
  343,  344,  345,    0,  402,  414,    0,    0,  416,    0,
  353,    0,  356,    0,  385,  384,  373,  382,  399,    0,
    0,    0,  380,  381,  431,    0,  422,  289,    0,    0,
  429,  504,    0,  507,    0,  510,    0,  513,    0,  516,
    0,  519,    0,  524,    0,    0,    0,    0,  539,    0,
  543,    0,    0,  459,    0,    0,  551,    0,    0,    0,
  628,    0,    0,  626,    0,    0,    0,  140,  141,  142,
  143,    0,   78,   76,  166,  164,  182,    0,    0,    0,
    0,  127,    0,  206,    0,  215,    0,  219,    0,  223,
    0,    0,    0,  236,  642,    0,    0,    0,   94,   93,
   96,    0,  122,    0,   92,   91,   95,    0,    0,    0,
    0,    0,    0,    0,    0,  188,    0,    0,  632,  602,
  601,  607,  609,  610,  611,  612,  613,    0,  615,  616,
  617,  618,  619,  620,    0,  646,    0,  657,    0,    0,
  636,  634,  633,  665,  664,  278,    0,  274,  272,    0,
    0,    0,  258,  306,    0,    0,    0,  309,  260,    0,
    0,    0,    0,    0,  338,  341,    0,  339,  333,    0,
    0,  420,    0,    0,  409,  352,  350,    0,    0,  388,
  387,  394,    0,    0,  371,    0,  291,    0,  503,  493,
  506,  494,  509,  496,  512,  497,  515,  498,  518,  499,
  523,    0,    0,    0,    0,  477,  538,    0,    0,  545,
  542,    0,  534,  532,  557,    0,    0,  462,    0,    0,
  559,    0,    0,    0,    0,    0,   56,   55,  197,  195,
    0,    0,  139,    0,  136,    0,   89,   79,  181,  173,
  174,  126,    0,  205,    0,  214,    0,  218,    0,    0,
  222,    0,    0,  225,    0,    0,  235,   72,    0,  641,
    0,   75,   73,  157,    0,  156,    0,  121,    0,  155,
    0,   90,    0,    0,    0,  108,  109,  110,    0,    0,
  162,  158,  163,  159,  160,  167,  187,    0,    0,    0,
  623,    0,    0,  645,  644,  656,    0,    0,    0,  277,
    0,  321,  320,  318,  316,  314,  315,  311,  312,    0,
  285,    0,  297,    0,  269,    0,    0,    0,  328,    0,
  332,    0,  419,    0,  417,  415,  303,    0,    0,  400,
  398,  393,    0,  370,    0,  290,  287,  522,    0,    0,
    0,  478,  476,  531,    0,  549,  547,  548,  537,    0,
  535,  541,    0,  556,    0,  552,  550,  565,    0,    0,
  471,    0,  433,    0,    0,  196,    0,  194,  138,    0,
  135,    0,  125,  124,  204,    0,  213,    0,  217,  216,
  239,    0,  229,    0,    0,  224,  232,    0,  234,  233,
    0,  120,  100,    0,  102,    0,  112,    0,  111,  107,
    0,  185,  184,    0,  631,    0,  627,  622,    0,  606,
    0,    0,  655,    0,    0,  276,  275,  308,  296,    0,
    0,  342,  337,  340,  334,  327,    0,  331,    0,  351,
  392,    0,  369,  367,  521,  502,    0,  482,  530,    0,
  546,  544,  540,  536,  555,    0,  564,    0,  560,  558,
  470,  567,    0,  434,  242,    0,  137,  131,  134,    0,
    0,  203,    0,  212,    0,  238,    0,  228,    0,  221,
  220,  640,  639,   99,    0,    0,  101,  116,    0,    0,
    0,  186,  183,    0,    0,  621,  614,  244,    0,  654,
    0,  651,    0,    0,  326,    0,  330,    0,  391,    0,
    0,  483,  529,    0,  554,    0,  563,    0,  469,    0,
    0,    0,    0,  150,    0,  202,  198,  211,  199,    0,
    0,  227,    0,   98,    0,  105,    0,  115,    0,  113,
  114,  629,  625,  243,  653,    0,  300,    0,  295,  293,
    0,  390,    0,  389,  528,    0,  562,  561,  568,  566,
  468,    0,  132,    0,  133,    0,  149,    0,  209,    0,
  210,    0,  119,    0,  652,  650,  329,  324,  397,    0,
  527,  526,  467,    0,  148,    0,  207,  200,  208,  201,
  466,    0,  147,    0,  465,  463,    0,  146,    0,
};
final static short yydgoto[] = {                          2,
    3,  247,    5,    6,    7,    8,  248,   10,   11,   12,
   64,   98,  127,  128,  372,  163,   65,   66,  373,  297,
  549,  375,  376,  377,  378,  580,  379,  380,  381,  382,
  383,  384,  385,  386,  581,  947,  750,  869,  950,  185,
  542,  387,  388,  389,  390,  391,  392,  393,  394,  395,
  396,  397,  283,  702,  723,  726,  724,  563,  564,  769,
  186,  278,  275,  289,  453,  188,  189,  190,  293,  325,
  632,  649,  279,  449,  463,  468,  891,  627,  628,  450,
  474,  299,  164,  493,  249,  202,  317,  318,  491,  971,
  250,  194,  305,  478,  479,  251,  206,   53,  109,  152,
  231,  352,  529,  693,  149,  153,  154,  349,  526,  690,
  923,  150,  226,  341,  517,  676,  350,  679,  680,  527,
  691,  252,  253,  254,  255,  256,  257,  258,  259,  260,
  261,  262,  263,  605,  264,
};
final static short yysindex[] = {                       -22,
 1145,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0, 1184, 1218, 1289, 1293, 1311, 1325, -160,
 1328, 1337,    0,   50,    0,  159,    0,  189,    0,  422,
    0,  484,    0, -175,    0, 1352,    0,  492,    0,  502,
    0, 1191,    0, 1228,    0, 1255,    0, 1314,    0, 1385,
    0, 1355, -182,    0, -168,    0, 1414,    0, 1435,    0,
 1466,  -92,  245,    0,  467,  536,    0,  587,    0,  605,
    0,  620,    0,  728,    0,    0, -171,    0,  213,    0,
  740,    0,  822,    0,    0,    0,    0,    0,    0,   14,
    0,    0,    0,    0,    0,    0,    0, 2578, 1826, 2750,
 1461, -150,  659, -139,  737,  772,    0, 1367,  545, -165,
  774, 3208,    0, 1370,    0,    0,    0,    0, -134,    0,
  710,  419,  742,  973,    0,    0, 1420, 1229,    0,    0,
 2578,    0, 1229,    0,    0,    0,  -16,    0, -174,    0,
 1379, 1391,    0, -153,    0, -158,    0, 2621,  879,    0,
 -177,  561,  545,    0,    0,  545,    0,  160,    0,  813,
    0,    0,  660,  928,    0,    0,    0, 1558,    0,    0,
    0,    0,    0,    0,    0,    0,    0, 1420, 1229,    0,
    0,   16,  553, -159,  658,  -14,  668,  711,    0,  281,
    0,  -12,  250, -179,    0,  834,    0,  880,    0, -156,
  427,  554,    0,  354,  523,  732,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  555,  557,    0,  572,  589,
  593,  601,    0,    0,  604, 1229, 1396,    0, 1401, -162,
  707,    0,  561,    0, 1036, 2657, -212,  857,  167, -127,
 2803,  174,  678,    0, 3335, 4837,    0,    0,   -8,   43,
  581,   -6,    0,  612,  800,    0,    0,    0,    0,  893,
  900,  902,    0,  171,    0,  257,    0,    0, 1406,    0,
    0,    0,    0,   51,  668,    0,   18,    4,    0,    0,
    0,    0, 4837,  214,    0,    0,  816, -146,    0,  256,
    0,  225,  732,    0,    0,   32,  324, 1202,    0,    0,
    0,  830,    0, 1429,  732,    0,   28,    0,  -96,    0,
 1448,    0,    0,  849,    0,  -10,  863,  863,    0,  439,
    0,    0,  913, 2102,  863,    0,  704,    0,  899,    0,
  944,    0,  961,    0, 1083,    0, 1084,    0, 1104,    0,
  970,    0,    0,    0, 1006,    0,  615, 1471, 1234,    0,
 1482,  933,  707, 2278,    0,  488,    0, 3155,    0, 4880,
 2151,  618,    0,    0,    0,    0,    0,  619,  623,  636,
 2565,    0,    0,  993,  990,    0,    0, 2569,    0,    0,
    0,    0,    0,    0,    0,    0, 1371,    0,    0, 1269,
    0,    0,    0,    0,    0,    0,  646,    0,   19,    0,
    1,    0, 2264,    0, -207,    0,    0,    0, -137,    0,
 1207, 2327,    9,    9,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0, 1039, 1259,    0,   10,    0,    0,
    0,    0,    0,  106,    0,  647,  668,   41, 1302,    0,
    0,   18,    0,    0,    0,    0,    0, 3404,    0,   18,
    0,    0, 1329, 1202, 1017,   25,   -2,   -4, 1484, -129,
    0,    0,    0, 1062,    0,    0,  -15, 1345,    0,  863,
    0, -148,    0, 1074,    0,    0,    0,    0,    0,  143,
   23, -115,    0,    0,    0, 1202,    0,    0, -126, 1064,
    0,    0,  539,    0,  568,    0,  573,    0,  656,    0,
  709,    0,  734,    0,  -13, 1393, -252,  879,    0, 1502,
    0,  652, -152,    0, -194, 1409,    0, 1504, 1093,  933,
    0, 2395,    9,    0, 1525, 3223, -144,    0,    0,    0,
    0, 1530,    0,    0,    0,    0,    0, 2200,  190, 1560,
   34,    0,   49,    0, 1535,    0, 1540,    0, 3417,    0,
 3267,  579,  276,    0,    0, 2852,  315, 2901,    0,    0,
    0, 3486,    0, 3529,    0,    0,    0, -131, 3573, 3642,
 1470, 3655, 3724, 4837, 1236,    0,  -29, 1454,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  953,    0,    0,
    0,    0,    0,    0, 1465,    0,  340,    0,  456,  894,
    0,    0,    0,    0,    0,    0,  349,    0,    0,    0,
 1547, 3737,    0,    0,  -23,  111, 1491,    0,    0,  464,
  -83, 1074,  197, 1268,    0,    0, 1243,    0,    0,  500,
 1549,    0, 3813, 1572,    0,    0,    0, 2969,  668,    0,
    0,    0, 1590, -155,    0, -149,    0, 1242,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0, 1120,  628, -187, 1599,    0,    0, -107,  559,    0,
    0, 1616,    0,    0,    0, 1618, 1626,    0, -209, 1569,
    0,  674, 1280, 1093,    0,  386,    0,    0,    0,    0,
    9,  741,    0, -142,    0,  355,    0,    0,    0,    0,
    0,    0,  435,    0,  558,    0,  580,    0,  810, 1060,
    0, 3861,  277,    0, 3904,  330,    0,    0,  990,    0,
 1359,    0,    0,    0, 1371,    0, 1371,    0, 1356,    0,
 1371,    0,    9, 3951, 4021,    0,    0,    0,  677, 4064,
    0,    0,    0,    0,    0,    0,    0,  739, 4837,  168,
    0, 1629, 4111,    0,    0,    0, 1598,  188, 1248,    0,
 1632,    0,    0,    0,    0,    0,    0,    0,    0, 4927,
    0, 1202,    0, 4154,    0, 1268, 1637, -128,    0, -136,
    0,  624,    0,    9,    0,    0,    0, 1064, 1320,    0,
    0,    0,  688,    0, 1318,    0,    0,    0,  743, 1558,
 1609,    0,    0,    0, -190,    0,    0,    0,    0, 1641,
    0,    0,  888,    0, -120,    0,    0,    0,  712, 1649,
    0, 1653,    0, 1340,    0,    0, 4201,    0,    0, 1658,
    0,  506,    0,    0,    0, 1665,    0, 1668,    0,    0,
    0, 4245,    0,  218,  738,    0,    0,    9,    0,    0,
  411,    0,    0,   -3,    0,  -18,    0, 2408,    0,    0,
    9,    0,    0,  999,    0, 2486,    0,    0,  805,    0,
    9, 1019,    0,  753,  503,    0,    0,    0,    0,    9,
  -28,    0,    0,    0,    0,    0, 3037,    0, 1673,    0,
    0, 4288,    0,    0,    0,    0, 1630,    0,    0, 1676,
    0,    0,    0,    0,    0, 3106,    0, 1125,    0,    0,
    0,    0,  912,    0,    0,    9,    0,    0,    0, 1631,
 1683,    0,  781,    0,  497,    0,    9,    0, 4356,    0,
    0,    0,    0,    0, 4399, 4446,    0,    0,    9,  978,
   91,    0,    0,    0, 1009,    0,    0,    0, 1394,    0,
 -203,    0, 4489,  -85,    0, 1064,    0, 1347,    0,    9,
 1057,    0,    0,  762,    0, 1064,    0,  817,    0, 1686,
 -180, 4532, 4575,    0,   15,    0,    0,    0,    0, 4618,
 4665,    0,    9,    0,    9,    0,    9,    0, 4708,    0,
    0,    0,    0,    0,    0,  491,    0,    9,    0,    0,
 1688,    0, 4751,    0,    0, 1691,    0,    0,    0,    0,
    0, 1127,    0,    9,    0,    9,    0, 1705,    0, 1050,
    0, 1051,    0,    9,    0,    0,    0,    0,    0,    9,
    0,    0,    0, -186,    0,  886,    0,    0,    0,    0,
    0, 1156,    0, 1381,    0,    0, 4794,    0,    9,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0, 1362,    0,    0,    0,    0,    0,    0,
    0, 1651,    0, 1651,    0, 1651,    0, 1651,    0, 1651,
    0,    0,    0,    0, 1362,    0, 1651,    0, 1651,    0,
    0,    0,    0,    0, 1654,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0, 1404,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0, 1249,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0, 1413, 1418,    0,    0,
 1473,    0, 1479,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0, 1122,    0,
    0, -211, 1308,    0,    0, 1249,    0,    0,    0,    0,
    0,    0,    0,  -33,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0, 1481, 1500,    0,
    0,    0,    0, 4970,    0,    0,  775,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  -38,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   90,    0,    0,    0,    0,
  890,    0, -211,    0,  535,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  908,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  437,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  779,    0,
    0,    0,  780,    0,    0,    0, 1887,  909,    0,    0,
    0,    0,    0,    0,  -38,    0,    0,    0,  920,    0,
    0,    0,    0,    0,    0,    0,  532,  532,    0,    0,
    0,    0,    0,    0,  532,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   98,    0,    0,    0,    0,    0,    0,    0,  940,    0,
    0, -200,  890,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0, 1794,    0,    0, 1513,    0,    0,
    0,    0,    0,    0,    0,    0,  782,    0,    0,  682,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  910,  941,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  775,  118,  369,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  448,   96,  789,    0,    0, 1232,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  115,    0,  532,
    0,    0,    0, -189,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  163,    0,    0,    0,  -26,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  124, 1195,    0,    0,
    0,    0,    0,    0,    0, 1025,    0,    0, 1376, -200,
    0,    0,  956,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0, 1513,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  943,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0, 1430,    0,    0,
    0,    0,    0,    0,    0,    0, 4970,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  962,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  872,
    0,    0,    0,    0,  540,  733,  945,    0,    0,    0,
    0,    2,    0,    0,    0,    0, 1912,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0, 1392,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  631,
    0,    0,    0, 1376, 2692,    0,    0,    0,    0,    0,
 1094,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  943,    0,    0,    0,    0,    0, 1843,    0,
   17,    0,    0,    0,  881,    0, 1027,    0,    0,    0,
 1126,    0, 1186,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  531,    0,    0,    0, 1407,    0,    0,    0,    0,
    0,    0,    0,  -24,    0,    0,    0,  577,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0, 2751,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  947,    0,    0,
    0,    0,    0,    0,    0, 1245,    0,    0,    0,    0,
 1304,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  996,    0,    0,    0,    0,    0,    0,    0,    0,  576,
  916,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0, 1119,    0,    0,    0, 1373,
    0,    0,    0,    0,    0,    0, 1042,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0, 1148,    0,
    0,    0,    0,  -37,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0, 1947,    0, 1990,    0, 1149,
    0,    0,    0,    0,    0,  -30,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0, 1075,    0, 1438,    0, 1507,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  621,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0, 1566,    0, 1625,    0,    0,    0,    0,
    0,    0,    0, 1150,    0,    0,    0,    0,    0, 1154,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0, 1685,    0,    0,    0,    0, 1734,
};
final static short yygindex[] = {                         0,
    0, 1721,    0,    0,    0,    0, 1731,    0,    0,    0,
 1693, 1331, 1306, 1112,  -95, -135,    0, 1821,    0, 1463,
 -231, 1211, 1216, 1440, -245,    0,    0,    0,    0,    0,
    0,    0,    0,    0, 1222,    0,    0,    0,    0, -178,
    0,   60,  975, 1451,    0,    0,    0,    0,    0,    0,
    0,    0, 1230, -540,    0, 1100, 1109,    0, 1271,    0,
 -299,    0, -130, -266, -272,  951,  984,    0,    0,  979,
    0, 1209, 1397,    0,    0, -181,    0,    0, 1082, 1244,
    0, 1239,    0, -247, 1726,    0,    0,    0,    0,    0,
 1752,    0,    0,    0, 1251, 1754,    0, 1848, 1802, 1770,
 1701, 1597, 1422, 1260, 1612,    0, 1806,    0,    0,    0,
    0, 1737,    0,    0,    0,    0, 1442, 1286, 1153, 1282,
 1144, -116, -240,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0, 1740,
};
final static int YYTABLESIZE=5293;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                        413,
  414,  407,  129,  274,  374,  196,  187,  482,  443,  405,
  298,  423,  758,  553,  286,  963,  536,  461,  719,  418,
  288,  630,  535,  182,  579,  169,  574,  296,  363,  182,
  672,  129,  286,  238,  418,  129,  635,  448,  634,  579,
  238,  574,  302,  182,  287,  643,  828,  446,  606,  238,
  416,  579, 1005,  574,  534,  182,  638,  182, 1028,  588,
  302,  685,  435,  654,  466,  909,  653,  182,  812, 1051,
  494,  466,  270,  114,  711, 1021,  303,  501,  228,  589,
   51,  191,  129,  579,  107,  574,  566,   78,  182,   42,
  155,  442,  500,  346,  347,   35,  280,  203,  444,  310,
  802,  418,  199,  683,  347,  136,  804,  646,  533,  457,
  464,  703,  455,  839,   14,  550,  140,  467,  608,  896,
  674,  165,  401,  461,  738,  562,  638,  894,  402,  657,
  403, 1001,  675,  481,  302,  915,  284,  490,  496,  284,
  655,  484,  166,  656,  573,   77,  617,  447,  816,  438,
   19,   20,  302,  579,  284,  574,  455,  455,  455,  483,
  423,  319,  840,  704,  609,   99,  610,  475,  229,  461,
 1009,  281,  783,  413,  192,  647,  187,  137,  829,  623,
  620,  304,  551,  651,  607, 1052,  813,  629, 1006,   52,
 1022,  686,  458,  282,  108,  910,   52,  423,   44,  238,
  108,  311,   36,  204,  803,  200,  238,  876,  805,  897,
  658,  436,  626,  238,  435,  141,  142,  288,  630,  639,
  895,  430,  363,  187,  348,  916,  757,  182,   46,  286,
  708,  418,  645,  551,  348,  817,  818,  786,  324,  181,
  642,  285,  671,  294,  295,  488,  489,  415,  572,  421,
    1,  235,  944,  182, 1010,  784,  590,  302,  235,  445,
  579,  236,  574,  572,  296,  614,  237,  235,  236,  113,
 1027,  273,  638,  237,  638,  572,  434,  236,  652,  946,
  273,  295,  237,  481,  638,  288,  610,  633,  295,  638,
  701,  945,  266,  553,  363,  182,  238,  286,  417,  418,
  964,  281,  550,  286,  712,   41,  441,  572,  302,  286,
  418,  286,  183,  701,  286,  286,  184,  422,  288,  776,
  777,  239,  184,  282,  591,  302,  731,  284,  239,  720,
  286,  302,  418,  615,  743,  955,  184,  239,  553,  553,
  638,  959,  192,  302,  638,  471,  472,  473,  184,  192,
  184,  284,  200,  696,  238,  204,  183,  184,  192,  200,
  184,  616,  204,  284,  184,  638,  765,  481,  200,  696,
  413,  204,  621,  319,  713,  638,  448,  572,  638,  238,
  284,  184,  799,  240,  241,  242,  243,  244,  245,  246,
  240,  241,  242,  243,  244,  245,  246,  794,  650,  240,
  241,  242,  243,  244,  245,  246,  638,  638,  638,  638,
  638,  638,  638,  481,   43,  234,  798,  235,  430,  284,
  284,  484,  400,  875,  235,  284,  835,  236,  434,  406,
  284,  235,  237,  284,  236,  284,  284,  284,  413,  237,
  346,  236,  346,  883,   45,  707,  237,  475,  782,  413,
  238,  319,  441,  778,  779,  481,  481,  481,  481,  481,
  877,   48,  481,  484,  484,  484,  484,  484,  884,  451,
  484,  413,  284,  938,  481,  844,  854,  305,  296,  858,
  462,  295,  484,  566,  572,  314,  430,  239,  281,  475,
  475,  475,  475,  475,  239,  768,  183,  430,  864,  866,
  184,  239,  100,  296,  871,  300,  281,  346,  192,  346,
  101,  459,  437,  874,  235,  192,  183,  881,  200,  430,
  184,  204,  192,   50,  236,  200,  284,  536,  204,  237,
  238,   57,  200,  535,  626,  204,  291,  989,  890,  296,
  110,   59,  238,  939,  452,  931,  184,  725,  725,  240,
  241,  242,  243,  244,  245,  246,  240,  241,  242,  243,
  244,  245,  246,  240,  241,  242,  243,  244,  245,  246,
  730,  283,  235,  301,  283,  635,  102,  811,  635,  660,
  313,  323,  236,  313,  239,  859,  460,  237,  184,  283,
  368,  926,  277,  316,  327,  764,  329,  235,  313,  821,
  561,  722,  820,  846,  770,  192,  937,  236,  662,  319,
  841,  331,  237,  664,  292,  200,  299,  301,  204,  299,
  943,  579,  949,  574,  279,  848,  879,  103,  333,  187,
  701,  735,  335,  737,  299,  301,  701,  187,  741,  420,
  337,  441,  239,  339,  962,  104,  240,  241,  242,  243,
  244,  245,  246,  860,  520,  469,  970,  553,  555,  842,
  105,  298,  557,  192,  298,  966,  942,  239,  235,  899,
  425,  470,  810,  200,  170,  559,  204,   47,  236,  298,
  771,  171,  312,  237,  976,  587,  277,  320,  192,  951,
  843,  682,  305,  993,  495,  295,  666,  274,  200,  995,
  997,  204,  279,  281,  240,  241,  242,  243,  244,  245,
  246,  766,  767,  832,  907,  284,  868, 1008,  171,  781,
  295,  268,  171,  171,  171,  171,  171,  902,  171,  240,
  241,  242,  243,  244,  245,  246, 1024, 1026,  239,   49,
  171,  171,  171,  171, 1030, 1032, 1035,   56,  235,  668,
  313,  918,  988, 1034,  168,  789,  295,   58,  236,  192,
  235,  929,  930,  237,  284, 1036,  305, 1040,  106,  200,
  236,  281,  204,  310,  670,  237,  310,  281,  321,  873,
  111,  838,  281,  906,  837,  281,  283,  368,  281,  281,
  537,  310,  635,  961,  659,  313,  538,  539,  540,  541,
  240,  241,  242,  243,  244,  245,  246, 1016,  276,  315,
  326, 1059,  328,  845,  819,  305,  990,  991,  239,  254,
  288,  987,  154,  661,  154,  154,  154,  330,  663,  294,
  239,  299,  301,  305,  721,  847,  419,  254,  288,  192,
  154,  154,  154,  154,  332,  572,  322,  294,  334,  200,
  850,  192,  204,  837,  283,  368,  336, 1018,  427,  338,
  283,  200,  112,  313,  204,  283,  957,  424,  283,  313,
  519,  283,  283,  552,  554,  346,  298,  346,  556,  898,
  240,  241,  242,  243,  244,  245,  246,  115,  116,  117,
  118,  558,  240,  241,  242,  243,  244,  245,  246,  299,
  301,  586,  618,  722,  299,  299,  301,  681,  122,  123,
  151,  665,  253,  124,  138,  267,  299,  299,  301,  125,
  126,  153,  227,  153,  153,  153, 1054,  230,  914,  831,
  253,  820,  867,  408,  710,  409,  579,  171,  574,  153,
  153,  153,  153,  901,  298,  171,  171,  171,  171,  298,
  298,  429,  981,  171,  460,  980,  292,  171,  431,  502,
  433,  298,  298,  503,  667,  167,  662,  917,  660,  115,
  116,  117,  118,  269,  292,  171,  171,  171,  171,  171,
  171,  171,  171,  171,  171,  304,  139,  284,  310,  669,
  122,  123,  143,  940,  872,  124,  836,  172,  905,  663,
  460,  125,  126,  304,  173,  171,  171,  171,  960,  288,
  171,  171,  762,  171,  624,  171,  171, 1015, 1000,  171,
  604,  999,  171,  171,  171,  171,  171,  145,  171,  157,
  305,  171,  171,  171,  254,  288,  986,  154,  171,  953,
  290,  579,  171,  574,  294,  154,  154,  154,  154, 1003,
  171,  171,  837,  154,  605,  426,  310,  154,  412,  958,
  956,  941,  310,  360,  144,  849,  324,  152,  265,  152,
  152,  152, 1017,  171,  351,  154,  154,  154,  154,  154,
  154,  154,  154,  154,  154,  152,  152,  152,  152,  306,
 1048, 1050,  579,  579,  574,  574,  354, 1014,  305,  146,
 1013,  158,  254,  288,  305,  154,  154,  154,  271,  288,
  154,  154,  294,  154,  398,  154,  154,  288,  294,  154,
  288,  288,  154,  154,  154,  154,  154,  253,  154,  294,
  294,  154,  154,  154,  241,  308,  153,  241,  154,  456,
  266, 1053,  154,  913,  153,  153,  153,  153,  428,  709,
  154,  154,  153,  475,  504,  430,  153,  432,  505,  240,
  572,  307,  240,  662,  411,  660,  151,  979,  151,  151,
  151,  292,  487,  154,  153,  153,  153,  153,  153,  153,
  153,  153,  153,  153,  151,  151,  151,  151,  118,  396,
  117,  118,  396,  117,  395,  253,  663,  395,  231,  506,
  304,  271,  230,  507,  153,  153,  153,  309,  761,  153,
  153,  624,  153,  458,  153,  153,  508,  604,  153,  492,
  509,  153,  153,  153,  153,  153,   80,  153,  174,   80,
  153,  153,  153,  998,  454,  175,  497,  153,  176,  292,
  454,  153,  179,  288,   80,  292,  536,  516,  565,  153,
  153,  605,  535,  346,  952,  346,  292,  292,  458,  458,
  288,  288,  148,  454, 1002,  572,  231,  455,  304,  411,
  230,  465,  153,  455,  304,  411,  357,  523,  115,  116,
  117,  118,  152,  480,  568,  104,  566,  484,  104,  271,
  152,  152,  152,  152,  611,  359,  612,  237,  152,  122,
  123,  528,  152,  104,  124, 1047, 1049,  454,  454,  454,
  125,  126, 1012,  237,  585,  851,  572,  572,  567,  613,
  152,  152,  152,  152,  152,  152,  152,  152,  152,  152,
  226,  363,  364,  365,  366,  367,  368,  369,  510,  512,
  370,  371,  511,  513,  106,  622,  226,  106,  457,  241,
  152,  152,  152,  566,  631,  152,  152,  566,  152,  514,
  152,  152,  106,  515,  152,  237,  237,  152,  152,  152,
  152,  152,  630,  152,  240,  808,  152,  152,  152,  809,
  977,  151, 1043,  152,  978,  852, 1044,  152,  644,  151,
  151,  151,  151,  457,  457,  152,  152,  151,  226,  226,
   13,  151,  454,  118,  396,  117,  640,  584,  641,  395,
  454, 1055,  582,  128,  648, 1056,  128,  583,  152,  151,
  151,  151,  151,  151,  151,  151,  151,  151,  151,  131,
  133,  128,  673,  177, 1004,  455,  178,  837,  180,   23,
   24,   80,  160,  455,   16,  445,   60,   16,   61,  151,
  151,  151,  687,   15,  151,  151,   15,  151,   14,  151,
  151,   14,  692,  151,   62,   63,  151,  151,  151,  151,
  151,   14,  151,   25,   26,  151,  151,  151,   97,   80,
   80,   97,  151,   67,  272,   61,  151,  445,  445,  445,
  445,  445,  756,   15,  151,  151,   97,  806,   16,  807,
  104,   62,   63,   17,  119,  120,   18,   19,   20,   80,
   69,   80,   61,   21,  760,   80,   21,  151,  451,   22,
   80,   20,   22,   80,   20,  763,   80,   80,   62,   63,
   80,  340,   80,   21,  780,   80,   80,   80,  104,  104,
   19,   22,   80,   19,   27,   28,   80,  103,   29,   30,
  103,  471,  472,  473,   80,   80,  752,  754,  755,  106,
  451,  451,  451,  451,  451,  103,   31,   32,  104,   71,
  104,   61,  448,  903,  104,  904,  346,   80,  346,  104,
   33,   34,  104,   37,   38,  104,  104,   62,   63,  104,
  788,  104,   39,   40,  104,  104,  104,  106,  106,  787,
  710,  104,  579,  833,  574,  104,  129,   54,   55,  129,
   75,   76,  830,  104,  104,  448,  448,  448,  448,  575,
  577,  576,  147,  148,  129,  161,  162,  106,  128,  106,
  861,  447,  862,  106,  195,  162,  104,  882,  106,  885,
   73,  106,   61,  900,  106,  106,  197,  198,  106,  908,
  106,  342,  148,  106,  106,  106,  344,  345,   62,   63,
  106,  439,  440,  924,  106,  130,  128,  128,  130,   80,
  972,   61,  106,  106,  447,  447,  447,  447, 1011,  115,
  116,  117,  118,  130,  476,  477, 1057,   62,   63,  443,
   82,   54,   61,   97,   53,  106,  128,  121,  128,  464,
  122,  123,  128,  485,  295,  124,  123,  128,   62,   63,
  128,  125,  126,  128,  128,  305,  134,  128,   61,  128,
    4,   84,  128,  128,  128,  144,  521,  522,  144,  128,
    9,   97,   97,  128,   62,   63,  264,  524,  525,  636,
  637,  128,  128,  144,   85,   86,   87,   88,   89,   90,
   91,   92,   93,   94,   95,   96,   97,  677,  678,  688,
  689,   97,  103,   97,  128,  744,  745,   97,  746,  747,
  748,  749,   97,  486,  145,   97,  729,  145,   97,   97,
  697,  698,   97,  733,   97,  705,  706,   97,   97,   97,
  714,  715,  145,  135,   97,  716,  717,  544,   97,  739,
  103,  103,  772,  773,  791,  792,   97,   97,  123,  123,
  546,  123,  123,  123,  123,  709,  759,  115,  116,  117,
  118,  129,  855,  569,  570,  571,  572,  795,  477,   97,
  103,  856,  103,  727,   71,  573,  103,   71,  122,  123,
  785,  103,  619,  124,  103,  800,  801,  103,  103,  125,
  126,  103,   71,  103,  814,  815,  103,  103,  103,  129,
  129,  888,  578,  103,   68,  775,   70,  103,   72,  201,
   74,  822,  678,  824,  825,  103,  103,   81,  790,   83,
  130,  826,  525,   70,  878,  162,   70,  886,  887,  129,
  193,  129,  892,  893,  796,  129,  911,  678,  103,  205,
  129,   70,   79,  129,  919,  689,  129,  129,  921,  922,
  129,  156,  129,  927,  928,  129,  129,  129,  130,  130,
  932,  933,  129,  934,  935,  233,  129,  335,  967,  968,
  335,  973,  974,  353,  129,  129,  982,  983,  984,  985,
  144, 1019, 1020, 1037, 1038,  335, 1041, 1042,  130,  530,
  130,  694,  336,  834,  130,  336,  518,  129,  232,  130,
 1045, 1046,  130,  343,  684,  130,  130,  823,  827,  130,
  336,  130,  912,  920,  130,  130,  130,  399,  144,  144,
    0,  130,    0,    0,    0,  130,    0,  322,    0,  145,
  322,    0,    0,  130,  130,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  322,    0,    0,  144,    0,
  144,    0,    0,    0,  144,    0,  130,    0,    0,  144,
    0,    0,  144,    0,    0,  144,  144,  145,  145,  144,
  323,  144,    0,  323,  144,  144,  144,    0,    0,    0,
    0,  144,    0,    0,    0,  144,    0,    0,  323,   71,
    0,    0,    0,  144,  144,    0,    0,  145,    0,  145,
    0,    0,    0,  145,    0,    0,    0,    0,  145,    0,
    0,  145,    0,    0,  145,  145,  144,    0,  145,    0,
  145,  130,    0,  145,  145,  145,    0,   71,    0,    0,
  145,    0,    0,    0,  145,    0,    0,    0,   70,    0,
    0,    0,  145,  145,   85,   86,   87,   88,   89,   90,
   91,   92,   93,   94,   95,   96,   97,   71,    0,   71,
    0,    0,    0,   71,    0,  145,    0,    0,   71,    0,
    0,   71,    0,    0,   71,   71,   70,    0,   71,    0,
   71,  361,  335,   71,   71,   71,  360,    0,    0,    0,
   71,    0,    0,    0,   71,    0,    0,    0,    0,    0,
    0,    0,   71,   71,    0,    0,   70,  336,   70,    0,
    0,    0,   70,    0,    0,    0,    0,   70,    0,    0,
   70,    0,    0,   70,   70,   71,    0,   70,    0,   70,
  548,    0,   70,   70,   70,  360,    0,    0,    0,   70,
    0,    0,  322,   70,    0,    0,    0,    0,    0,    0,
  335,   70,   70,    0,    0,    0,  335,    0,    0,    0,
    0,  335,    0,    0,  335,    0,    0,  335,  335,    0,
    0,  335,  335,  335,   70,  336,  335,  335,  335,  548,
    0,  336,    0,  335,  360,  323,  336,  335,    0,  336,
    0,    0,  336,  336,    0,    0,  336,  336,  336,    0,
    0,  336,  336,  336,    0,    0,    0,    0,  336,    0,
  322,    0,  336,    0,    0,    0,  322,    0,    0,    0,
    0,  322,    0,    0,  322,    0,    0,  322,  322,    0,
    0,  322,    0,  322,    0,    0,  322,  322,  322,    0,
    0,    0,    0,  322,    0,    0,    0,  322,    0,    0,
    0,    0,    0,  323,    0,    0,    0,  532,    0,  323,
    0,    0,  360,    0,  323,    0,    0,  323,    0,    0,
  323,  323,    0,    0,  323,    0,  323,    0,    0,  323,
  323,  323,    0,    0,    0,    0,  323,    0,    0,    0,
  323,    0,    0,    0,    0,    0,    0,  498,  356,  357,
    0,  115,  116,  117,  118,    0,  532,    0,    0,    0,
    0,  360,    0,    0,    0,    0,    0,  358,  359,    0,
    0,    0,  122,  123,    0,    0,    0,  124,    0,    0,
    0,    0,    0,  125,  126,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  362,  547,  356,  357,    0,
  115,  116,  117,  118,  363,  364,  365,  366,  367,  368,
  369,    0,    0,  370,  371,    0,  358,  359,    0,    0,
    0,  122,  123,    0,  532,    0,  124,  499,    0,  360,
    0,    0,  125,  126,    0,    0,    0,  532,    0,    0,
    0,    0,  360,    0,  362,  695,  356,  357,    0,  115,
  116,  117,  118,  363,  364,  365,  366,  367,  368,  369,
    0,    0,  370,  371,    0,  358,  359,    0,    0,    0,
  122,  123,    0,  184,    0,  124,    0,    0,    0,    0,
    0,  125,  126,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  362,    0,    0,    0,    0,    0,    0,
    0,    0,  363,  364,  365,  366,  367,  368,  369,  592,
    0,  370,  371,    0,    0,  532,    0,    0,    0,    0,
  360,    0,  184,  531,  411,  357,    0,  115,  116,  117,
  118,    0,  593,  594,  595,  596,  597,  598,  599,  600,
  601,  602,  603,  604,  359,    0,    0,    0,  122,  123,
    0,    0,    0,  124,    0,    0,    0,    0,    0,  125,
  126,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  547,  411,  357,    0,  115,  116,  117,  118,
  363,  364,  365,  366,  367,  368,  369,    0,    0,  370,
  371,    0,    0,  359,  412,    0,  183,  122,  123,  360,
  184,  579,  124,  574,    0,    0,    0,    0,  125,  126,
    0,    0,    0,    0,    0,    0,    0,    0,  575,  577,
  576,    0,    0,    0,    0,    0,    0,    0,    0,  363,
  364,  365,  366,  367,  368,  369,    0,    0,  370,  371,
  695,  411,  357,    0,  115,  116,  117,  118,    0,  184,
    0,    0,    0,  948,  411,  357,    0,  115,  116,  117,
  118,  359,    0,    0,    0,  122,  123,    0,    0,    0,
  124,    0,    0,    0,  359,    0,  125,  126,  122,  123,
    0,    0,    0,  124,    0,    0,  361,    0,    0,  125,
  126,  360,    0,    0,    0,    0,    0,  363,  364,  365,
  366,  367,  368,  369,    0,    0,  370,  371,    0,    0,
  363,  364,  365,  366,  367,  368,  369,  184,  182,  370,
  371,    0,  182,  182,  182,  182,  182,    0,  182,    0,
  184,  954,  411,  357,    0,  115,  116,  117,  118,    0,
  182,  182,  182,  182,    0,    0,    0,    0,    0,    0,
    0,    0,  359,    0,    0,    0,  122,  123,    0,    0,
    0,  124,    0,    0,    0,    0,    0,  125,  126,    0,
    0,    0,    0,    0,    0,    0,    0,  174,    0,    0,
    0,  174,  174,  174,  174,  174,    0,  174,  363,  364,
  365,  366,  367,  368,  369,    0,    0,  370,  371,  174,
  174,  174,  174,    0,    0,    0,    0,    0,  184,    0,
  560,  411,  357,    0,  115,  116,  117,  118,    0,    0,
    0,    0,  569,  570,  571,  572,    0,  115,  116,  117,
  118,  359,  361,    0,  573,  122,  123,  360,    0,    0,
  124,    0,    0,  119,  120,  121,  125,  126,  122,  123,
    0,    0,    0,  124,    0,    0,    0,    0,    0,  125,
  126,  578,    0,    0,    0,    0,  207,  363,  364,  365,
  366,  367,  368,  369,    0,    0,  370,  371,    0,  561,
    0,  361,    0,    0,    0,    0,  360,    0,    0,  208,
    0,    0,  209,  210,    0,  211,  212,  213,  214,  215,
    0,    0,  355,  356,  357,    0,  115,  116,  117,  118,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  358,  359,    0,    0,    0,  122,  123,    0,
  361,    0,  124,    0,    0,  360,    0,  182,  125,  126,
    0,    0,    0,    0,    0,  182,  182,  182,  182,    0,
  362,    0,    0,    0,    0,    0,    0,  182,    0,  363,
  364,  365,  366,  367,  368,  369,    0,    0,  370,  371,
    0,    0,    0,    0,    0,    0,    0,  182,  182,    0,
  182,  182,  182,  182,  182,  216,  217,  218,  219,  220,
  221,  222,  223,  224,  225,  132,  174,    0,  361,    0,
    0,    0,    0,  360,  174,  174,  174,  174,    0,    0,
    0,  266,    0,    0,    0,    0,  174,    0,   85,   86,
   87,   88,   89,   90,   91,   92,   93,   94,   95,   96,
   97,    0,    0,    0,    0,    0,  174,  174,    0,  174,
  174,  174,  174,  174,    0,    0,    0,    0,  404,  356,
  357,    0,  115,  116,  117,  118,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  361,    0,  358,  359,
  264,  360,    0,  122,  123,    0,    0,    0,  124,    0,
    0,    0,    0,    0,  125,  126,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  362,  728,  356,  357,
    0,  115,  116,  117,  118,  363,  364,  365,  366,  367,
  368,  369,    0,    0,  370,  371,    0,  358,  359,    0,
    0,    0,  122,  123,    0,    0,    0,  124,    0,    0,
    0,    0,    0,  125,  126,  361,    0,    0,    0,    0,
  360,    0,    0,    0,    0,  362,  732,  356,  357,    0,
  115,  116,  117,  118,  363,  364,  365,  366,  367,  368,
  369,    0,    0,  370,  371,    0,  358,  359,    0,    0,
    0,  122,  123,    0,    0,    0,  124,    0,    0,    0,
    0,    0,  125,  126,  361,    0,    0,    0,    0,  360,
    0,    0,    0,    0,  362,    0,    0,    0,    0,    0,
    0,    0,    0,  363,  364,  365,  366,  367,  368,  369,
    0,    0,  370,  371,  797,  356,  357,    0,  115,  116,
  117,  118,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  358,  359,    0,    0,    0,  122,
  123,    0,    0,    0,  124,    0,    0,    0,    0,    0,
  125,  126,  412,  700,    0,    0,    0,  360,    0,    0,
    0,    0,  362,    0,    0,    0,    0,    0,    0,    0,
    0,  363,  364,  365,  366,  367,  368,  369,    0,    0,
  370,  371,  965,  356,  357,    0,  115,  116,  117,  118,
    0,    0,    0,    0,    0,    0,  361,    0,    0,    0,
    0,  360,  358,  359,    0,    0,    0,  122,  123,    0,
    0,    0,  124,    0,    0,    0,    0,    0,  125,  126,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  362,    0,    0,    0,    0,    0,    0,    0,    0,  363,
  364,  365,  366,  367,  368,  369,    0,    0,  370,  371,
    0,  975,  356,  357,    0,  115,  116,  117,  118,    0,
    0,    0,    0,    0,  412,    0,    0,    0,    0,  360,
    0,  358,  359,    0,    0,    0,  122,  123,    0,    0,
    0,  124,    0,    0,    0,    0,    0,  125,  126,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  362,
  543,  356,  357,    0,  115,  116,  117,  118,  363,  364,
  365,  366,  367,  368,  369,    0,    0,  370,  371,    0,
    0,  359,    0,    0,    0,  122,  123,    0,    0,    0,
  124,    0,    0,  412,    0,    0,  125,  126,  360,    0,
    0,    0,    0,    0,    0,    0,  412,    0,  362,    0,
    0,  360,    0,  159,    0,    0,    0,  363,  364,  365,
  366,  367,  368,  369,    0,    0,  370,  371,  699,  411,
  357,    0,  115,  116,  117,  118,   85,   86,   87,   88,
   89,   90,   91,   92,   93,   94,   95,   96,   97,  359,
    0,    0,    0,  122,  123,    0,    0,    0,  124,    0,
    0,    0,    0,    0,  125,  126,    0,    0,    0,    0,
    0,    0,    0,  356,  357,  412,  115,  116,  117,  118,
  360,    0,    0,    0,    0,  363,  364,  365,  366,  367,
  368,  369,  358,  359,  370,  371,    0,  122,  123,    0,
    0,    0,  124,    0,    0,    0,    0,    0,  125,  126,
    0,    0,    0,    0,    0,    0,    0,    0,  412,    0,
  362,    0,    0,  360,    0,    0,    0,    0,    0,  363,
  364,  365,  366,  367,  368,  369,    0,    0,  370,  371,
  410,  411,  357,    0,  115,  116,  117,  118,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  359,  412,    0,    0,  122,  123,  360,    0,    0,
  124,    0,    0,    0,    0,    0,  125,  126,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  363,  364,  365,
  366,  367,  368,  369,    0,    0,  370,  371,    0,  624,
  625,  357,    0,  115,  116,  117,  118,    0,    0,    0,
    0,    0,  718,  411,  357,    0,  115,  116,  117,  118,
  359,  412,    0,    0,  122,  123,  360,    0,    0,  124,
    0,    0,    0,  359,  412,  125,  126,  122,  123,  360,
    0,    0,  124,    0,    0,    0,    0,    0,  125,  126,
    0,    0,    0,    0,    0,    0,  363,  364,  365,  366,
  367,  368,  369,    0,    0,  370,  371,    0,    0,  363,
  364,  365,  366,  367,  368,  369,    0,    0,  370,  371,
    0,  734,  411,  357,    0,  115,  116,  117,  118,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  359,  412,    0,    0,  122,  123,  360,    0,
    0,  124,    0,    0,    0,    0,  412,  125,  126,    0,
    0,  360,    0,    0,  736,  411,  357,    0,  115,  116,
  117,  118,    0,    0,    0,    0,    0,    0,  363,  364,
  365,  366,  367,  368,  369,  359,    0,  370,  371,  122,
  123,    0,    0,    0,  124,    0,    0,    0,    0,    0,
  125,  126,    0,    0,    0,    0,    0,    0,  740,  411,
  357,    0,  115,  116,  117,  118,    0,    0,    0,    0,
    0,  363,  364,  365,  366,  367,  368,  369,    0,  359,
  370,  371,  412,  122,  123,    0,    0,  360,  124,    0,
    0,    0,    0,    0,  125,  126,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  363,  364,  365,  366,  367,
  368,  369,    0,    0,  370,  371,    0,  742,  411,  357,
  412,  115,  116,  117,  118,  360,    0,    0,    0,    0,
  751,  411,  357,    0,  115,  116,  117,  118,  359,    0,
    0,    0,  122,  123,    0,    0,    0,  124,    0,    0,
    0,  359,    0,  125,  126,  122,  123,    0,    0,    0,
  124,    0,    0,  412,    0,    0,  125,  126,  360,    0,
    0,    0,    0,    0,  363,  364,  365,  366,  367,  368,
  369,    0,    0,  370,  371,    0,    0,  363,  364,  365,
  366,  367,  368,  369,    0,    0,  370,  371,    0,  753,
  411,  357,    0,  115,  116,  117,  118,    0,    0,    0,
  412,    0,  774,  411,  357,  360,  115,  116,  117,  118,
  359,    0,    0,    0,  122,  123,    0,    0,    0,  124,
    0,    0,    0,  359,    0,  125,  126,  122,  123,    0,
    0,    0,  124,    0,    0,    0,    0,    0,  125,  126,
    0,    0,    0,    0,    0,    0,  363,  364,  365,  366,
  367,  368,  369,    0,    0,  370,  371,    0,    0,  363,
  364,  365,  366,  367,  368,  369,    0,    0,  370,  371,
  412,    0,    0,    0,    0,  360,    0,    0,  793,  411,
  357,    0,  115,  116,  117,  118,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  359,
    0,    0,    0,  122,  123,    0,    0,    0,  124,    0,
    0,    0,    0,  412,  125,  126,    0,    0,  360,    0,
    0,    0,    0,    0,    0,    0,  853,  411,  357,    0,
  115,  116,  117,  118,    0,  363,  364,  365,  366,  367,
  368,  369,    0,    0,  370,  371,    0,  359,    0,    0,
    0,  122,  123,    0,    0,    0,  124,    0,    0,    0,
  412,    0,  125,  126,    0,  360,    0,    0,    0,  857,
  411,  357,    0,  115,  116,  117,  118,    0,    0,    0,
    0,    0,    0,  363,  364,  365,  366,  367,  368,  369,
  359,    0,  370,  371,  122,  123,    0,    0,    0,  124,
    0,    0,    0,  412,    0,  125,  126,    0,  360,    0,
    0,    0,    0,    0,    0,    0,  863,  411,  357,    0,
  115,  116,  117,  118,    0,    0,  363,  364,  365,  366,
  367,  368,  369,    0,    0,  370,  371,  359,    0,    0,
    0,  122,  123,    0,    0,    0,  124,    0,    0,    0,
  412,    0,  125,  126,    0,  360,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  363,  364,  365,  366,  367,  368,  369,
    0,    0,  370,  371,    0,    0,  865,  411,  357,    0,
  115,  116,  117,  118,  412,    0,    0,    0,    0,  360,
    0,    0,    0,    0,    0,    0,    0,  359,    0,    0,
    0,  122,  123,    0,    0,    0,  124,    0,    0,    0,
    0,    0,  125,  126,    0,    0,    0,    0,    0,  870,
  411,  357,    0,  115,  116,  117,  118,  412,    0,    0,
    0,    0,  360,  363,  364,  365,  366,  367,  368,  369,
  359,    0,  370,  371,  122,  123,    0,    0,    0,  124,
    0,    0,    0,    0,    0,  125,  126,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  880,  411,  357,    0,
  115,  116,  117,  118,    0,    0,  363,  364,  365,  366,
  367,  368,  369,    0,    0,  370,  371,  359,    0,    0,
    0,  122,  123,    0,    0,  412,  124,    0,    0,    0,
  360,    0,  125,  126,    0,    0,    0,    0,    0,  889,
  411,  357,    0,  115,  116,  117,  118,    0,    0,    0,
    0,    0,    0,  363,  364,  365,  366,  367,  368,  369,
  359,    0,  370,  371,  122,  123,    0,    0,  412,  124,
    0,    0,    0,  360,    0,  125,  126,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  925,  411,  357,    0,
  115,  116,  117,  118,    0,    0,  363,  364,  365,  366,
  367,  368,  369,    0,    0,  370,  371,  359,    0,    0,
    0,  122,  123,    0,    0,  412,  124,    0,    0,    0,
  360,    0,  125,  126,    0,    0,    0,    0,    0,    0,
  936,  411,  357,    0,  115,  116,  117,  118,    0,    0,
    0,    0,    0,  363,  364,  365,  366,  367,  368,  369,
    0,  359,  370,  371,    0,  122,  123,    0,  412,    0,
  124,    0,    0,  360,    0,    0,  125,  126,    0,    0,
    0,    0,    0,  969,  411,  357,    0,  115,  116,  117,
  118,    0,    0,    0,    0,    0,    0,  363,  364,  365,
  366,  367,  368,  369,  359,    0,  370,  371,  122,  123,
    0,  412,    0,  124,    0,    0,  360,    0,    0,  125,
  126,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  363,  364,  365,  366,  367,  368,  369,    0,    0,  370,
  371,  992,  411,  357,  412,  115,  116,  117,  118,  360,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  359,    0,    0,    0,  122,  123,    0,    0,
    0,  124,    0,    0,    0,    0,    0,  125,  126,    0,
    0,    0,    0,    0,  994,  411,  357,  412,  115,  116,
  117,  118,  360,    0,    0,    0,    0,    0,  363,  364,
  365,  366,  367,  368,  369,  359,    0,  370,  371,  122,
  123,    0,    0,    0,  124,    0,    0,    0,    0,    0,
  125,  126,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  996,  411,  357,  412,  115,  116,  117,  118,  360,
    0,  363,  364,  365,  366,  367,  368,  369,    0,    0,
  370,  371,  359,    0,    0,    0,  122,  123,    0,    0,
    0,  124,    0,    0,    0,    0,    0,  125,  126,    0,
    0,    0,    0,    0, 1007,  411,  357,  412,  115,  116,
  117,  118,  360,    0,    0,    0,    0,    0,  363,  364,
  365,  366,  367,  368,  369,  359,    0,  370,  371,  122,
  123,    0,    0,    0,  124,    0,    0,    0,    0,    0,
  125,  126,    0,    0,    0,    0,    0, 1023,  411,  357,
  412,  115,  116,  117,  118,  360,    0,    0,    0,    0,
    0,  363,  364,  365,  366,  367,  368,  369,  359,    0,
  370,  371,  122,  123,    0,    0,    0,  124,    0,    0,
    0,    0,    0,  125,  126,    0,    0,    0,    0,    0,
 1025,  411,  357,  412,  115,  116,  117,  118,  360,    0,
    0,    0,    0,    0,  363,  364,  365,  366,  367,  368,
  369,  359,    0,  370,  371,  122,  123,    0,    0,    0,
  124,    0,    0,    0,    0,    0,  125,  126,    0,    0,
    0,    0,    0, 1029,  411,  357,  412,  115,  116,  117,
  118,  360,    0,    0,    0,    0,    0,  363,  364,  365,
  366,  367,  368,  369,  359,    0,  370,  371,  122,  123,
    0,    0,    0,  124,    0,    0,    0,    0,    0,  125,
  126,    0,    0,    0,    0,    0,    0,    0,    0,  412,
 1031,  411,  357,    0,  115,  116,  117,  118,    0,    0,
  363,  364,  365,  366,  367,  368,  369,    0,    0,  370,
  371,  359,    0,    0,    0,  122,  123,    0,    0,    0,
  124,    0,    0,    0,    0,    0,  125,  126,    0,    0,
    0,    0,    0, 1033,  411,  357,  412,  115,  116,  117,
  118,  360,    0,    0,    0,    0,    0,  363,  364,  365,
  366,  367,  368,  369,  359,    0,  370,  371,  122,  123,
    0,    0,    0,  124,    0,    0,    0,    0,    0,  125,
  126,    0,    0,    0,    0,    0, 1039,  411,  357,  349,
  115,  116,  117,  118,  349,    0,    0,    0,    0,    0,
  363,  364,  365,  366,  367,  368,  369,  359,    0,  370,
  371,  122,  123,    0,    0,    0,  124,    0,    0,    0,
    0,    0,  125,  126,    0,    0,    0,    0,    0, 1058,
  411,  357,    0,  115,  116,  117,  118,    0,    0,    0,
    0,    0,    0,  363,  364,  365,  366,  367,  368,  369,
  359,    0,  370,  371,  122,  123,    0,    0,    0,  124,
    0,    0,    0,    0,    0,  125,  126,    0,    0,    0,
    0,    0,    0,  411,  357,    0,  115,  116,  117,  118,
    0,    0,    0,    0,    0,    0,  363,  364,  365,  366,
  367,  368,  369,  359,    0,  370,  371,  122,  123,    0,
    0,    0,  124,    0,    0,    0,    0,    0,  125,  126,
    0,    0,    0,    0,    0,  545,  411,  357,    0,  115,
  116,  117,  118,    0,    0,    0,    0,    0,    0,  363,
  364,  365,  366,  367,  368,  369,  359,    0,  370,  371,
  122,  123,    0,    0,    0,  124,    0,    0,    0,    0,
    0,  125,  126,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  625,  357,    0,  115,  116,  117,  118,
    0,    0,  363,  364,  365,  366,  367,  368,  369,    0,
    0,  370,  371,  359,    0,    0,    0,  122,  123,    0,
    0,    0,  124,    0,    0,    0,    0,    0,  125,  126,
    0,    0,    0,    0,    0,    0,  349,  349,    0,  349,
  349,  349,  349,    0,    0,    0,    0,    0,    0,  363,
  364,  365,  366,  367,  368,  369,  349,    0,  370,  371,
  349,  349,    0,    0,    0,  349,    0,    0,    0,    0,
    0,  349,  349,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  349,  349,  349,  349,  349,  349,  349,    0,
    0,  349,  349,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                        245,
  246,  242,   98,  182,  236,  141,  137,  307,  275,  241,
  192,  252,   42,   44,   41,   44,   40,  290,  559,   44,
   59,   59,   46,   40,   43,  121,   45,   40,   62,   40,
   44,  127,   59,   40,   59,  131,   41,  283,   41,   43,
   40,   45,   41,   40,   59,   61,  256,   44,  256,   40,
   59,   43,  256,   45,  354,   40,   40,   40,   44,   41,
   59,  256,   44,   41,   40,  256,   44,   40,  256,  256,
  318,   40,  168,   60,   41,  256,  256,  325,  256,   61,
  256,  256,  178,   43,  256,   45,  294,  256,   40,   40,
  256,   41,  324,  256,  257,  256,  256,  256,  277,  256,
  256,   59,  256,  256,  257,  256,  256,  256,  354,  256,
  292,  256,  324,  256,  327,  361,  256,  296,  256,  256,
  373,  256,  239,  324,  256,  371,  256,  256,  256,  256,
  258,   41,  385,   44,  324,  256,   41,  316,  320,   44,
  256,   44,  277,  259,  276,  328,   41,  278,  256,  266,
  363,  364,  342,   43,   59,   45,  368,  369,  370,  256,
  401,   44,  305,  308,  302,  258,  412,   44,  346,  370,
  256,  331,  256,   59,  349,  324,  307,  328,  388,  452,
  447,  361,  361,   41,  392,  372,  374,  460,  392,  365,
  371,  386,  339,  353,  366,  386,  365,  438,   40,   40,
  366,  358,  363,  362,  360,  359,   40,   40,  358,  346,
  337,   41,  458,   40,   44,  355,  356,  256,  256,  349,
  349,   59,  256,  354,  387,  346,  256,   40,   40,  256,
   41,  256,  480,  412,  387,  343,  344,   41,  335,  256,
  256,  256,  256,  256,  257,  256,  257,  256,  267,  256,
  273,  258,  256,   40,  340,  339,  256,  256,  258,  256,
   43,  268,   45,  267,   40,  256,  273,  258,  268,  256,
  256,  256,  256,  273,  258,  267,  258,  268,  256,  298,
  256,  257,  273,  256,  268,  324,  532,  466,  257,  273,
  536,  295,  330,  324,  328,   40,   40,  324,  256,  324,
  329,  331,  548,  330,  256,  256,  256,  267,   59,  324,
  335,  338,  329,  559,  341,  342,  333,  324,  357,  343,
  344,  328,  333,  353,  324,  324,  567,  330,  328,  561,
  357,  330,  357,  324,  580,  876,  333,  328,  369,  370,
  324,  882,  349,  342,  328,  350,  351,  352,  333,  349,
  333,  256,  359,  532,   40,  362,  329,  333,  349,  359,
  333,  256,  362,  330,  333,  349,  607,  278,  359,  548,
  256,  362,  332,  256,  553,  359,  622,  267,  362,   40,
  330,  333,  649,  390,  391,  392,  393,  394,  395,  396,
  390,  391,  392,  393,  394,  395,  396,  643,  256,  390,
  391,  392,  393,  394,  395,  396,  390,  391,  392,  393,
  394,  395,  396,  324,  256,  256,  648,  258,  256,  324,
  330,  324,  256,  256,  258,  330,   41,  268,  258,  256,
  335,  258,  273,  338,  268,  330,  341,  342,  324,  273,
  345,  268,  347,  256,  256,  256,  273,  324,  630,  335,
   40,  334,  256,  343,  344,  366,  367,  368,  369,  370,
  760,   40,  373,  366,  367,  368,  369,  370,  768,  256,
  373,  357,  330,  256,  385,   41,  722,   41,   40,  725,
  256,  257,  385,  294,  267,   59,  324,  328,   41,  366,
  367,  368,  369,  370,  328,   40,  329,  335,  744,  745,
  333,  328,  258,   40,  750,  256,   59,  345,  349,  347,
   44,  256,  256,  759,  258,  349,  329,  763,  359,  357,
  333,  362,  349,   40,  268,  359,  330,   40,  362,  273,
   40,   40,  359,   46,  780,  362,  256,   41,  784,   40,
  328,   40,   40,  326,  331,   40,  333,  272,  272,  390,
  391,  392,  393,  394,  395,  396,  390,  391,  392,  393,
  394,  395,  396,  390,  391,  392,  393,  394,  395,  396,
  256,   41,  258,  324,   44,   41,   41,  673,   44,   41,
   41,   59,  268,   44,  328,  256,  331,  273,  333,   59,
   59,  837,   40,   40,   40,  256,   40,  258,   59,   41,
  325,  325,   44,   46,  256,  349,  852,  268,   41,  256,
  256,   40,  273,   41,  334,  359,   41,   41,  362,   44,
  861,   43,  868,   45,  256,   46,  762,   41,   40,  760,
  876,  572,   40,  574,   59,   59,  882,  768,  579,   59,
   40,  256,  328,   40,  885,   41,  390,  391,  392,  393,
  394,  395,  396,  324,   40,  332,  902,   40,   40,  305,
   41,   41,   40,  349,   44,  897,  256,  328,  258,   46,
   59,  348,   45,  359,  256,   40,  362,  256,  268,   59,
  332,  263,  256,  273,  916,   40,   40,  334,  349,  868,
  256,   40,  256,  939,  256,  257,   41,  876,  359,  945,
  946,  362,  334,  256,  390,  391,  392,  393,  394,  395,
  396,  256,  257,   40,  810,  330,   40,  963,   37,  256,
  257,   62,   41,   42,   43,   44,   45,   40,   47,  390,
  391,  392,  393,  394,  395,  396,  982,  983,  328,  256,
   59,   60,   61,   62,  990,  991,  256,  256,  258,   41,
  324,   40,  256,  999,   45,  256,  257,  256,  268,  349,
  258,  256,  257,  273,  330, 1006,  330, 1013,   41,  359,
  268,  324,  362,   41,   41,  273,   44,  330,  256,   41,
   41,   41,  335,   41,   44,  338,  256,  256,  341,  342,
  303,   59,  258,   41,  256,  256,  309,  310,  311,  312,
  390,  391,  392,  393,  394,  395,  396,   46,  256,  256,
  256, 1057,  256,  256,  256,   41,  320,  321,  328,   41,
   41,   41,   41,  256,   43,   44,   45,  256,  256,   41,
  328,  256,  256,   59,  256,  256,  256,   59,   59,  349,
   59,   60,   61,   62,  256,  267,  324,   59,  256,  359,
   41,  349,  362,   44,  324,  324,  256,   41,   59,  256,
  330,  359,   41,  324,  362,  335,   62,  256,  338,  330,
  256,  341,  342,  256,  256,  345,  256,  347,  256,  256,
  390,  391,  392,  393,  394,  395,  396,  260,  261,  262,
  263,  256,  390,  391,  392,  393,  394,  395,  396,  324,
  324,  256,  256,  325,  329,  330,  330,  256,  281,  282,
  366,  256,   41,  286,  256,  256,  341,  342,  342,  292,
  293,   41,   44,   43,   44,   45,   41,  367,   41,  256,
   59,   44,  256,  256,   41,  258,   43,  256,   45,   59,
   60,   61,   62,  256,  324,  264,  265,  266,  267,  329,
  330,   59,   41,  272,  324,   44,   41,  276,   59,  256,
   59,  341,  342,  260,  256,  256,   59,  256,   59,  260,
  261,  262,  263,   46,   59,  294,  295,  296,  297,  298,
  299,  300,  301,  302,  303,   41,  328,  330,  256,  256,
  281,  282,  256,  256,  256,  286,  256,  256,  256,   59,
  370,  292,  293,   59,  263,  324,  325,  326,  256,  342,
  329,  330,   60,  332,   59,  334,  335,  256,   41,  338,
   59,   44,  341,  342,  343,  344,  345,  256,  347,  256,
  256,  350,  351,  352,  256,  256,  256,  256,  357,   41,
  330,   43,  361,   45,  256,  264,  265,  266,  267,   41,
  369,  370,   44,  272,   59,  256,  324,  276,   40,   41,
  256,  324,  330,   45,  328,  256,  335,   41,  256,   43,
   44,   45,  256,  392,  368,  294,  295,  296,  297,  298,
  299,  300,  301,  302,  303,   59,   60,   61,   62,  256,
   41,   41,   43,   43,   45,   45,   61,   41,  324,  328,
   44,  328,  324,  324,  330,  324,  325,  326,  330,  330,
  329,  330,  324,  332,  258,  334,  335,  338,  330,  338,
  341,  342,  341,  342,  343,  344,  345,  256,  347,  341,
  342,  350,  351,  352,   41,  256,  256,   44,  357,  324,
  328,  256,  361,  256,  264,  265,  266,  267,  256,  256,
  369,  370,  272,  324,  256,  256,  276,  256,  260,   41,
  267,  328,   44,  256,  256,  256,   41,  256,   43,   44,
   45,  256,  324,  392,  294,  295,  296,  297,  298,  299,
  300,  301,  302,  303,   59,   60,   61,   62,   41,   41,
   41,   44,   44,   44,   41,  324,  256,   44,  256,  256,
  256,  330,  256,  260,  324,  325,  326,  328,  256,  329,
  330,  256,  332,  324,  334,  335,  256,  256,  338,  357,
  260,  341,  342,  343,  344,  345,   41,  347,  256,   44,
  350,  351,  352,  256,  284,  263,  324,  357,  127,  324,
  290,  361,  131,  324,   59,  330,   40,  278,  256,  369,
  370,  256,   46,  345,  256,  347,  341,  342,  369,  370,
  341,  342,  257,  324,  256,  267,  324,  284,  324,  361,
  324,  293,  392,  290,  330,  257,  258,   44,  260,  261,
  262,  263,  256,  305,  295,   41,  294,  309,   44,  178,
  264,  265,  266,  267,  256,  277,  258,  256,  272,  281,
  282,  369,  276,   59,  286,  256,  256,  368,  369,  370,
  292,  293,  256,  272,   46,  256,  267,  267,  326,   61,
  294,  295,  296,  297,  298,  299,  300,  301,  302,  303,
  256,  313,  314,  315,  316,  317,  318,  319,  256,  256,
  322,  323,  260,  260,   41,   44,  272,   44,  324,  256,
  324,  325,  326,  294,  338,  329,  330,  294,  332,  256,
  334,  335,   59,  260,  338,  324,  325,  341,  342,  343,
  344,  345,   44,  347,  256,  256,  350,  351,  352,  260,
  256,  256,  256,  357,  260,  326,  260,  361,   44,  264,
  265,  266,  267,  369,  370,  369,  370,  272,  324,  325,
  256,  276,  452,  256,  256,  256,  345,   37,  347,  256,
  460,  256,   42,   41,  341,  260,   44,   47,  392,  294,
  295,  296,  297,  298,  299,  300,  301,  302,  303,   99,
  100,   59,   40,  128,   41,  452,  131,   44,  133,  256,
  257,  256,  112,  460,   41,  324,  256,   44,  258,  324,
  325,  326,   44,   41,  329,  330,   44,  332,   41,  334,
  335,   44,  370,  338,  274,  275,  341,  342,  343,  344,
  345,  327,  347,  256,  257,  350,  351,  352,   41,  294,
  295,   44,  357,  256,  179,  258,  361,  366,  367,  368,
  369,  370,  257,  349,  369,  370,   59,  256,  354,  258,
  256,  274,  275,  359,  276,  277,  362,  363,  364,  324,
  256,  326,  258,   41,   61,  330,   44,  392,  324,   41,
  335,   41,   44,  338,   44,   61,  341,  342,  274,  275,
  345,  226,  347,  389,   44,  350,  351,  352,  294,  295,
   41,  397,  357,   44,  256,  257,  361,   41,  256,  257,
   44,  350,  351,  352,  369,  370,  582,  583,  584,  256,
  366,  367,  368,  369,  370,   59,  256,  257,  324,  256,
  326,  258,  324,  256,  330,  258,  345,  392,  347,  335,
  256,  257,  338,  256,  257,  341,  342,  274,  275,  345,
  348,  347,  256,  257,  350,  351,  352,  294,  295,  332,
   41,  357,   43,  324,   45,  361,   41,  256,  257,   44,
  256,  257,   44,  369,  370,  367,  368,  369,  370,   60,
   61,   62,  256,  257,   59,  256,  257,  324,  256,  326,
  272,  324,  277,  330,  256,  257,  392,   40,  335,  392,
  256,  338,  258,  324,  341,  342,  256,  257,  345,   41,
  347,  256,  257,  350,  351,  352,  256,  257,  274,  275,
  357,  256,  257,  324,  361,   41,  294,  295,   44,  256,
   41,  258,  369,  370,  367,  368,  369,  370,  332,  260,
  261,  262,  263,   59,  256,  257,  306,  274,  275,  328,
  256,   41,  258,  256,   41,  392,  324,  278,  326,  324,
  281,  282,  330,  256,  257,  286,  277,  335,  274,  275,
  338,  292,  293,  341,  342,  324,  256,  345,  258,  347,
    0,  256,  350,  351,  352,   41,  256,  257,   44,  357,
    0,  294,  295,  361,  274,  275,  330,  256,  257,  256,
  257,  369,  370,   59,  279,  280,  281,  282,  283,  284,
  285,  286,  287,  288,  289,  290,  291,  256,  257,  256,
  257,  324,  256,  326,  392,  296,  297,  330,  299,  300,
  301,  302,  335,  311,   41,  338,  566,   44,  341,  342,
  256,  257,  345,  568,  347,  256,  257,  350,  351,  352,
  256,  257,   59,  101,  357,  256,  257,  358,  361,  578,
  294,  295,  256,  257,  256,  257,  369,  370,  296,  297,
  360,  299,  300,  301,  302,  256,  587,  260,  261,  262,
  263,  256,  723,  264,  265,  266,  267,  256,  257,  392,
  324,  723,  326,  563,   41,  276,  330,   44,  281,  282,
  632,  335,  446,  286,  338,  256,  257,  341,  342,  292,
  293,  345,   59,  347,  256,  257,  350,  351,  352,  294,
  295,  780,  303,  357,   44,  622,   46,  361,   48,  144,
   50,  256,  257,  256,  257,  369,  370,   57,  640,   59,
  256,  256,  257,   41,  256,  257,   44,  256,  257,  324,
  139,  326,  256,  257,  644,  330,  256,  257,  392,  146,
  335,   59,   55,  338,  256,  257,  341,  342,  256,  257,
  345,  110,  347,  256,  257,  350,  351,  352,  294,  295,
  256,  257,  357,  256,  257,  156,  361,   41,  256,  257,
   44,  256,  257,  233,  369,  370,  306,  307,  256,  257,
  256,  256,  257,  256,  257,   59,  256,  257,  324,  353,
  326,  530,   41,  694,  330,   44,  345,  392,  153,  335,
  256,  257,  338,  227,  523,  341,  342,  682,  687,  345,
   59,  347,  820,  830,  350,  351,  352,  238,  294,  295,
   -1,  357,   -1,   -1,   -1,  361,   -1,   41,   -1,  256,
   44,   -1,   -1,  369,  370,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   59,   -1,   -1,  324,   -1,
  326,   -1,   -1,   -1,  330,   -1,  392,   -1,   -1,  335,
   -1,   -1,  338,   -1,   -1,  341,  342,  294,  295,  345,
   41,  347,   -1,   44,  350,  351,  352,   -1,   -1,   -1,
   -1,  357,   -1,   -1,   -1,  361,   -1,   -1,   59,  256,
   -1,   -1,   -1,  369,  370,   -1,   -1,  324,   -1,  326,
   -1,   -1,   -1,  330,   -1,   -1,   -1,   -1,  335,   -1,
   -1,  338,   -1,   -1,  341,  342,  392,   -1,  345,   -1,
  347,  256,   -1,  350,  351,  352,   -1,  294,   -1,   -1,
  357,   -1,   -1,   -1,  361,   -1,   -1,   -1,  256,   -1,
   -1,   -1,  369,  370,  279,  280,  281,  282,  283,  284,
  285,  286,  287,  288,  289,  290,  291,  324,   -1,  326,
   -1,   -1,   -1,  330,   -1,  392,   -1,   -1,  335,   -1,
   -1,  338,   -1,   -1,  341,  342,  294,   -1,  345,   -1,
  347,   40,  256,  350,  351,  352,   45,   -1,   -1,   -1,
  357,   -1,   -1,   -1,  361,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  369,  370,   -1,   -1,  324,  256,  326,   -1,
   -1,   -1,  330,   -1,   -1,   -1,   -1,  335,   -1,   -1,
  338,   -1,   -1,  341,  342,  392,   -1,  345,   -1,  347,
   40,   -1,  350,  351,  352,   45,   -1,   -1,   -1,  357,
   -1,   -1,  256,  361,   -1,   -1,   -1,   -1,   -1,   -1,
  324,  369,  370,   -1,   -1,   -1,  330,   -1,   -1,   -1,
   -1,  335,   -1,   -1,  338,   -1,   -1,  341,  342,   -1,
   -1,  345,  346,  347,  392,  324,  350,  351,  352,   40,
   -1,  330,   -1,  357,   45,  256,  335,  361,   -1,  338,
   -1,   -1,  341,  342,   -1,   -1,  345,  346,  347,   -1,
   -1,  350,  351,  352,   -1,   -1,   -1,   -1,  357,   -1,
  324,   -1,  361,   -1,   -1,   -1,  330,   -1,   -1,   -1,
   -1,  335,   -1,   -1,  338,   -1,   -1,  341,  342,   -1,
   -1,  345,   -1,  347,   -1,   -1,  350,  351,  352,   -1,
   -1,   -1,   -1,  357,   -1,   -1,   -1,  361,   -1,   -1,
   -1,   -1,   -1,  324,   -1,   -1,   -1,   40,   -1,  330,
   -1,   -1,   45,   -1,  335,   -1,   -1,  338,   -1,   -1,
  341,  342,   -1,   -1,  345,   -1,  347,   -1,   -1,  350,
  351,  352,   -1,   -1,   -1,   -1,  357,   -1,   -1,   -1,
  361,   -1,   -1,   -1,   -1,   -1,   -1,  256,  257,  258,
   -1,  260,  261,  262,  263,   -1,   40,   -1,   -1,   -1,
   -1,   45,   -1,   -1,   -1,   -1,   -1,  276,  277,   -1,
   -1,   -1,  281,  282,   -1,   -1,   -1,  286,   -1,   -1,
   -1,   -1,   -1,  292,  293,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,  304,  256,  257,  258,   -1,
  260,  261,  262,  263,  313,  314,  315,  316,  317,  318,
  319,   -1,   -1,  322,  323,   -1,  276,  277,   -1,   -1,
   -1,  281,  282,   -1,   40,   -1,  286,  336,   -1,   45,
   -1,   -1,  292,  293,   -1,   -1,   -1,   40,   -1,   -1,
   -1,   -1,   45,   -1,  304,  256,  257,  258,   -1,  260,
  261,  262,  263,  313,  314,  315,  316,  317,  318,  319,
   -1,   -1,  322,  323,   -1,  276,  277,   -1,   -1,   -1,
  281,  282,   -1,  333,   -1,  286,   -1,   -1,   -1,   -1,
   -1,  292,  293,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,  304,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  313,  314,  315,  316,  317,  318,  319,  256,
   -1,  322,  323,   -1,   -1,   40,   -1,   -1,   -1,   -1,
   45,   -1,  333,  256,  257,  258,   -1,  260,  261,  262,
  263,   -1,  279,  280,  281,  282,  283,  284,  285,  286,
  287,  288,  289,  290,  277,   -1,   -1,   -1,  281,  282,
   -1,   -1,   -1,  286,   -1,   -1,   -1,   -1,   -1,  292,
  293,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  256,  257,  258,   -1,  260,  261,  262,  263,
  313,  314,  315,  316,  317,  318,  319,   -1,   -1,  322,
  323,   -1,   -1,  277,   40,   -1,  329,  281,  282,   45,
  333,   43,  286,   45,   -1,   -1,   -1,   -1,  292,  293,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   60,   61,
   62,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  313,
  314,  315,  316,  317,  318,  319,   -1,   -1,  322,  323,
  256,  257,  258,   -1,  260,  261,  262,  263,   -1,  333,
   -1,   -1,   -1,  256,  257,  258,   -1,  260,  261,  262,
  263,  277,   -1,   -1,   -1,  281,  282,   -1,   -1,   -1,
  286,   -1,   -1,   -1,  277,   -1,  292,  293,  281,  282,
   -1,   -1,   -1,  286,   -1,   -1,   40,   -1,   -1,  292,
  293,   45,   -1,   -1,   -1,   -1,   -1,  313,  314,  315,
  316,  317,  318,  319,   -1,   -1,  322,  323,   -1,   -1,
  313,  314,  315,  316,  317,  318,  319,  333,   37,  322,
  323,   -1,   41,   42,   43,   44,   45,   -1,   47,   -1,
  333,  256,  257,  258,   -1,  260,  261,  262,  263,   -1,
   59,   60,   61,   62,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  277,   -1,   -1,   -1,  281,  282,   -1,   -1,
   -1,  286,   -1,   -1,   -1,   -1,   -1,  292,  293,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   37,   -1,   -1,
   -1,   41,   42,   43,   44,   45,   -1,   47,  313,  314,
  315,  316,  317,  318,  319,   -1,   -1,  322,  323,   59,
   60,   61,   62,   -1,   -1,   -1,   -1,   -1,  333,   -1,
  256,  257,  258,   -1,  260,  261,  262,  263,   -1,   -1,
   -1,   -1,  264,  265,  266,  267,   -1,  260,  261,  262,
  263,  277,   40,   -1,  276,  281,  282,   45,   -1,   -1,
  286,   -1,   -1,  276,  277,  278,  292,  293,  281,  282,
   -1,   -1,   -1,  286,   -1,   -1,   -1,   -1,   -1,  292,
  293,  303,   -1,   -1,   -1,   -1,  256,  313,  314,  315,
  316,  317,  318,  319,   -1,   -1,  322,  323,   -1,  325,
   -1,   40,   -1,   -1,   -1,   -1,   45,   -1,   -1,  279,
   -1,   -1,  282,  283,   -1,  285,  286,  287,  288,  289,
   -1,   -1,  256,  257,  258,   -1,  260,  261,  262,  263,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  276,  277,   -1,   -1,   -1,  281,  282,   -1,
   40,   -1,  286,   -1,   -1,   45,   -1,  256,  292,  293,
   -1,   -1,   -1,   -1,   -1,  264,  265,  266,  267,   -1,
  304,   -1,   -1,   -1,   -1,   -1,   -1,  276,   -1,  313,
  314,  315,  316,  317,  318,  319,   -1,   -1,  322,  323,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,  296,  297,   -1,
  299,  300,  301,  302,  303,  375,  376,  377,  378,  379,
  380,  381,  382,  383,  384,  256,  256,   -1,   40,   -1,
   -1,   -1,   -1,   45,  264,  265,  266,  267,   -1,   -1,
   -1,  330,   -1,   -1,   -1,   -1,  276,   -1,  279,  280,
  281,  282,  283,  284,  285,  286,  287,  288,  289,  290,
  291,   -1,   -1,   -1,   -1,   -1,  296,  297,   -1,  299,
  300,  301,  302,  303,   -1,   -1,   -1,   -1,  256,  257,
  258,   -1,  260,  261,  262,  263,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   40,   -1,  276,  277,
  330,   45,   -1,  281,  282,   -1,   -1,   -1,  286,   -1,
   -1,   -1,   -1,   -1,  292,  293,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,  304,  256,  257,  258,
   -1,  260,  261,  262,  263,  313,  314,  315,  316,  317,
  318,  319,   -1,   -1,  322,  323,   -1,  276,  277,   -1,
   -1,   -1,  281,  282,   -1,   -1,   -1,  286,   -1,   -1,
   -1,   -1,   -1,  292,  293,   40,   -1,   -1,   -1,   -1,
   45,   -1,   -1,   -1,   -1,  304,  256,  257,  258,   -1,
  260,  261,  262,  263,  313,  314,  315,  316,  317,  318,
  319,   -1,   -1,  322,  323,   -1,  276,  277,   -1,   -1,
   -1,  281,  282,   -1,   -1,   -1,  286,   -1,   -1,   -1,
   -1,   -1,  292,  293,   40,   -1,   -1,   -1,   -1,   45,
   -1,   -1,   -1,   -1,  304,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,  313,  314,  315,  316,  317,  318,  319,
   -1,   -1,  322,  323,  256,  257,  258,   -1,  260,  261,
  262,  263,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,  276,  277,   -1,   -1,   -1,  281,
  282,   -1,   -1,   -1,  286,   -1,   -1,   -1,   -1,   -1,
  292,  293,   40,   41,   -1,   -1,   -1,   45,   -1,   -1,
   -1,   -1,  304,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,  313,  314,  315,  316,  317,  318,  319,   -1,   -1,
  322,  323,  256,  257,  258,   -1,  260,  261,  262,  263,
   -1,   -1,   -1,   -1,   -1,   -1,   40,   -1,   -1,   -1,
   -1,   45,  276,  277,   -1,   -1,   -1,  281,  282,   -1,
   -1,   -1,  286,   -1,   -1,   -1,   -1,   -1,  292,  293,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
  304,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  313,
  314,  315,  316,  317,  318,  319,   -1,   -1,  322,  323,
   -1,  256,  257,  258,   -1,  260,  261,  262,  263,   -1,
   -1,   -1,   -1,   -1,   40,   -1,   -1,   -1,   -1,   45,
   -1,  276,  277,   -1,   -1,   -1,  281,  282,   -1,   -1,
   -1,  286,   -1,   -1,   -1,   -1,   -1,  292,  293,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  304,
  256,  257,  258,   -1,  260,  261,  262,  263,  313,  314,
  315,  316,  317,  318,  319,   -1,   -1,  322,  323,   -1,
   -1,  277,   -1,   -1,   -1,  281,  282,   -1,   -1,   -1,
  286,   -1,   -1,   40,   -1,   -1,  292,  293,   45,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   40,   -1,  304,   -1,
   -1,   45,   -1,  256,   -1,   -1,   -1,  313,  314,  315,
  316,  317,  318,  319,   -1,   -1,  322,  323,  256,  257,
  258,   -1,  260,  261,  262,  263,  279,  280,  281,  282,
  283,  284,  285,  286,  287,  288,  289,  290,  291,  277,
   -1,   -1,   -1,  281,  282,   -1,   -1,   -1,  286,   -1,
   -1,   -1,   -1,   -1,  292,  293,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,  257,  258,   40,  260,  261,  262,  263,
   45,   -1,   -1,   -1,   -1,  313,  314,  315,  316,  317,
  318,  319,  276,  277,  322,  323,   -1,  281,  282,   -1,
   -1,   -1,  286,   -1,   -1,   -1,   -1,   -1,  292,  293,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   40,   -1,
  304,   -1,   -1,   45,   -1,   -1,   -1,   -1,   -1,  313,
  314,  315,  316,  317,  318,  319,   -1,   -1,  322,  323,
  256,  257,  258,   -1,  260,  261,  262,  263,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,  277,   40,   -1,   -1,  281,  282,   45,   -1,   -1,
  286,   -1,   -1,   -1,   -1,   -1,  292,  293,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,  313,  314,  315,
  316,  317,  318,  319,   -1,   -1,  322,  323,   -1,  256,
  257,  258,   -1,  260,  261,  262,  263,   -1,   -1,   -1,
   -1,   -1,  256,  257,  258,   -1,  260,  261,  262,  263,
  277,   40,   -1,   -1,  281,  282,   45,   -1,   -1,  286,
   -1,   -1,   -1,  277,   40,  292,  293,  281,  282,   45,
   -1,   -1,  286,   -1,   -1,   -1,   -1,   -1,  292,  293,
   -1,   -1,   -1,   -1,   -1,   -1,  313,  314,  315,  316,
  317,  318,  319,   -1,   -1,  322,  323,   -1,   -1,  313,
  314,  315,  316,  317,  318,  319,   -1,   -1,  322,  323,
   -1,  256,  257,  258,   -1,  260,  261,  262,  263,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  277,   40,   -1,   -1,  281,  282,   45,   -1,
   -1,  286,   -1,   -1,   -1,   -1,   40,  292,  293,   -1,
   -1,   45,   -1,   -1,  256,  257,  258,   -1,  260,  261,
  262,  263,   -1,   -1,   -1,   -1,   -1,   -1,  313,  314,
  315,  316,  317,  318,  319,  277,   -1,  322,  323,  281,
  282,   -1,   -1,   -1,  286,   -1,   -1,   -1,   -1,   -1,
  292,  293,   -1,   -1,   -1,   -1,   -1,   -1,  256,  257,
  258,   -1,  260,  261,  262,  263,   -1,   -1,   -1,   -1,
   -1,  313,  314,  315,  316,  317,  318,  319,   -1,  277,
  322,  323,   40,  281,  282,   -1,   -1,   45,  286,   -1,
   -1,   -1,   -1,   -1,  292,  293,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,  313,  314,  315,  316,  317,
  318,  319,   -1,   -1,  322,  323,   -1,  256,  257,  258,
   40,  260,  261,  262,  263,   45,   -1,   -1,   -1,   -1,
  256,  257,  258,   -1,  260,  261,  262,  263,  277,   -1,
   -1,   -1,  281,  282,   -1,   -1,   -1,  286,   -1,   -1,
   -1,  277,   -1,  292,  293,  281,  282,   -1,   -1,   -1,
  286,   -1,   -1,   40,   -1,   -1,  292,  293,   45,   -1,
   -1,   -1,   -1,   -1,  313,  314,  315,  316,  317,  318,
  319,   -1,   -1,  322,  323,   -1,   -1,  313,  314,  315,
  316,  317,  318,  319,   -1,   -1,  322,  323,   -1,  256,
  257,  258,   -1,  260,  261,  262,  263,   -1,   -1,   -1,
   40,   -1,  256,  257,  258,   45,  260,  261,  262,  263,
  277,   -1,   -1,   -1,  281,  282,   -1,   -1,   -1,  286,
   -1,   -1,   -1,  277,   -1,  292,  293,  281,  282,   -1,
   -1,   -1,  286,   -1,   -1,   -1,   -1,   -1,  292,  293,
   -1,   -1,   -1,   -1,   -1,   -1,  313,  314,  315,  316,
  317,  318,  319,   -1,   -1,  322,  323,   -1,   -1,  313,
  314,  315,  316,  317,  318,  319,   -1,   -1,  322,  323,
   40,   -1,   -1,   -1,   -1,   45,   -1,   -1,  256,  257,
  258,   -1,  260,  261,  262,  263,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  277,
   -1,   -1,   -1,  281,  282,   -1,   -1,   -1,  286,   -1,
   -1,   -1,   -1,   40,  292,  293,   -1,   -1,   45,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,  256,  257,  258,   -1,
  260,  261,  262,  263,   -1,  313,  314,  315,  316,  317,
  318,  319,   -1,   -1,  322,  323,   -1,  277,   -1,   -1,
   -1,  281,  282,   -1,   -1,   -1,  286,   -1,   -1,   -1,
   40,   -1,  292,  293,   -1,   45,   -1,   -1,   -1,  256,
  257,  258,   -1,  260,  261,  262,  263,   -1,   -1,   -1,
   -1,   -1,   -1,  313,  314,  315,  316,  317,  318,  319,
  277,   -1,  322,  323,  281,  282,   -1,   -1,   -1,  286,
   -1,   -1,   -1,   40,   -1,  292,  293,   -1,   45,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,  256,  257,  258,   -1,
  260,  261,  262,  263,   -1,   -1,  313,  314,  315,  316,
  317,  318,  319,   -1,   -1,  322,  323,  277,   -1,   -1,
   -1,  281,  282,   -1,   -1,   -1,  286,   -1,   -1,   -1,
   40,   -1,  292,  293,   -1,   45,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,  313,  314,  315,  316,  317,  318,  319,
   -1,   -1,  322,  323,   -1,   -1,  256,  257,  258,   -1,
  260,  261,  262,  263,   40,   -1,   -1,   -1,   -1,   45,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,  277,   -1,   -1,
   -1,  281,  282,   -1,   -1,   -1,  286,   -1,   -1,   -1,
   -1,   -1,  292,  293,   -1,   -1,   -1,   -1,   -1,  256,
  257,  258,   -1,  260,  261,  262,  263,   40,   -1,   -1,
   -1,   -1,   45,  313,  314,  315,  316,  317,  318,  319,
  277,   -1,  322,  323,  281,  282,   -1,   -1,   -1,  286,
   -1,   -1,   -1,   -1,   -1,  292,  293,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,  256,  257,  258,   -1,
  260,  261,  262,  263,   -1,   -1,  313,  314,  315,  316,
  317,  318,  319,   -1,   -1,  322,  323,  277,   -1,   -1,
   -1,  281,  282,   -1,   -1,   40,  286,   -1,   -1,   -1,
   45,   -1,  292,  293,   -1,   -1,   -1,   -1,   -1,  256,
  257,  258,   -1,  260,  261,  262,  263,   -1,   -1,   -1,
   -1,   -1,   -1,  313,  314,  315,  316,  317,  318,  319,
  277,   -1,  322,  323,  281,  282,   -1,   -1,   40,  286,
   -1,   -1,   -1,   45,   -1,  292,  293,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,  256,  257,  258,   -1,
  260,  261,  262,  263,   -1,   -1,  313,  314,  315,  316,
  317,  318,  319,   -1,   -1,  322,  323,  277,   -1,   -1,
   -1,  281,  282,   -1,   -1,   40,  286,   -1,   -1,   -1,
   45,   -1,  292,  293,   -1,   -1,   -1,   -1,   -1,   -1,
  256,  257,  258,   -1,  260,  261,  262,  263,   -1,   -1,
   -1,   -1,   -1,  313,  314,  315,  316,  317,  318,  319,
   -1,  277,  322,  323,   -1,  281,  282,   -1,   40,   -1,
  286,   -1,   -1,   45,   -1,   -1,  292,  293,   -1,   -1,
   -1,   -1,   -1,  256,  257,  258,   -1,  260,  261,  262,
  263,   -1,   -1,   -1,   -1,   -1,   -1,  313,  314,  315,
  316,  317,  318,  319,  277,   -1,  322,  323,  281,  282,
   -1,   40,   -1,  286,   -1,   -1,   45,   -1,   -1,  292,
  293,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
  313,  314,  315,  316,  317,  318,  319,   -1,   -1,  322,
  323,  256,  257,  258,   40,  260,  261,  262,  263,   45,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  277,   -1,   -1,   -1,  281,  282,   -1,   -1,
   -1,  286,   -1,   -1,   -1,   -1,   -1,  292,  293,   -1,
   -1,   -1,   -1,   -1,  256,  257,  258,   40,  260,  261,
  262,  263,   45,   -1,   -1,   -1,   -1,   -1,  313,  314,
  315,  316,  317,  318,  319,  277,   -1,  322,  323,  281,
  282,   -1,   -1,   -1,  286,   -1,   -1,   -1,   -1,   -1,
  292,  293,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,  256,  257,  258,   40,  260,  261,  262,  263,   45,
   -1,  313,  314,  315,  316,  317,  318,  319,   -1,   -1,
  322,  323,  277,   -1,   -1,   -1,  281,  282,   -1,   -1,
   -1,  286,   -1,   -1,   -1,   -1,   -1,  292,  293,   -1,
   -1,   -1,   -1,   -1,  256,  257,  258,   40,  260,  261,
  262,  263,   45,   -1,   -1,   -1,   -1,   -1,  313,  314,
  315,  316,  317,  318,  319,  277,   -1,  322,  323,  281,
  282,   -1,   -1,   -1,  286,   -1,   -1,   -1,   -1,   -1,
  292,  293,   -1,   -1,   -1,   -1,   -1,  256,  257,  258,
   40,  260,  261,  262,  263,   45,   -1,   -1,   -1,   -1,
   -1,  313,  314,  315,  316,  317,  318,  319,  277,   -1,
  322,  323,  281,  282,   -1,   -1,   -1,  286,   -1,   -1,
   -1,   -1,   -1,  292,  293,   -1,   -1,   -1,   -1,   -1,
  256,  257,  258,   40,  260,  261,  262,  263,   45,   -1,
   -1,   -1,   -1,   -1,  313,  314,  315,  316,  317,  318,
  319,  277,   -1,  322,  323,  281,  282,   -1,   -1,   -1,
  286,   -1,   -1,   -1,   -1,   -1,  292,  293,   -1,   -1,
   -1,   -1,   -1,  256,  257,  258,   40,  260,  261,  262,
  263,   45,   -1,   -1,   -1,   -1,   -1,  313,  314,  315,
  316,  317,  318,  319,  277,   -1,  322,  323,  281,  282,
   -1,   -1,   -1,  286,   -1,   -1,   -1,   -1,   -1,  292,
  293,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   40,
  256,  257,  258,   -1,  260,  261,  262,  263,   -1,   -1,
  313,  314,  315,  316,  317,  318,  319,   -1,   -1,  322,
  323,  277,   -1,   -1,   -1,  281,  282,   -1,   -1,   -1,
  286,   -1,   -1,   -1,   -1,   -1,  292,  293,   -1,   -1,
   -1,   -1,   -1,  256,  257,  258,   40,  260,  261,  262,
  263,   45,   -1,   -1,   -1,   -1,   -1,  313,  314,  315,
  316,  317,  318,  319,  277,   -1,  322,  323,  281,  282,
   -1,   -1,   -1,  286,   -1,   -1,   -1,   -1,   -1,  292,
  293,   -1,   -1,   -1,   -1,   -1,  256,  257,  258,   40,
  260,  261,  262,  263,   45,   -1,   -1,   -1,   -1,   -1,
  313,  314,  315,  316,  317,  318,  319,  277,   -1,  322,
  323,  281,  282,   -1,   -1,   -1,  286,   -1,   -1,   -1,
   -1,   -1,  292,  293,   -1,   -1,   -1,   -1,   -1,  256,
  257,  258,   -1,  260,  261,  262,  263,   -1,   -1,   -1,
   -1,   -1,   -1,  313,  314,  315,  316,  317,  318,  319,
  277,   -1,  322,  323,  281,  282,   -1,   -1,   -1,  286,
   -1,   -1,   -1,   -1,   -1,  292,  293,   -1,   -1,   -1,
   -1,   -1,   -1,  257,  258,   -1,  260,  261,  262,  263,
   -1,   -1,   -1,   -1,   -1,   -1,  313,  314,  315,  316,
  317,  318,  319,  277,   -1,  322,  323,  281,  282,   -1,
   -1,   -1,  286,   -1,   -1,   -1,   -1,   -1,  292,  293,
   -1,   -1,   -1,   -1,   -1,  256,  257,  258,   -1,  260,
  261,  262,  263,   -1,   -1,   -1,   -1,   -1,   -1,  313,
  314,  315,  316,  317,  318,  319,  277,   -1,  322,  323,
  281,  282,   -1,   -1,   -1,  286,   -1,   -1,   -1,   -1,
   -1,  292,  293,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,  257,  258,   -1,  260,  261,  262,  263,
   -1,   -1,  313,  314,  315,  316,  317,  318,  319,   -1,
   -1,  322,  323,  277,   -1,   -1,   -1,  281,  282,   -1,
   -1,   -1,  286,   -1,   -1,   -1,   -1,   -1,  292,  293,
   -1,   -1,   -1,   -1,   -1,   -1,  257,  258,   -1,  260,
  261,  262,  263,   -1,   -1,   -1,   -1,   -1,   -1,  313,
  314,  315,  316,  317,  318,  319,  277,   -1,  322,  323,
  281,  282,   -1,   -1,   -1,  286,   -1,   -1,   -1,   -1,
   -1,  292,  293,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  313,  314,  315,  316,  317,  318,  319,   -1,
   -1,  322,  323,
};
}
final static short YYFINAL=2;
final static short YYMAXTOKEN=397;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,"'%'",null,null,"'('","')'","'*'","'+'",
"','","'-'","'.'","'/'",null,null,null,null,null,null,null,null,null,null,null,
"';'","'<'","'='","'>'",null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,"ID","VAR_REF","ENV_REF","INT_VAL",
"LONG_VAL","DOUBLE_VAL","STR_VAL","LE","GE","NE","CB","IF","COMMENT",
"WHITESPACE","IFX","ELSE","\"DEFINE\"","\"INOUT\"","\"OUT\"","\"NOT\"",
"\"NULL\"","\"DEFAULT\"","\"BOOLEAN\"","\"BYTE\"","\"BYTES\"","\"DATE\"",
"\"DOUBLE\"","\"ENUM\"","\"FLOAT\"","\"GUID\"","\"INT\"","\"LONG\"","\"SHORT\"",
"\"STRING\"","\"RECORDSET\"","\"TRUE\"","\"FALSE\"","\"OR\"","\"AND\"",
"\"BETWEEN\"","\"LIKE\"","\"ESCAPE\"","\"STARTS_WITH\"","\"ENDS_WITH\"",
"\"CONTAINS\"","\"IN\"","\"IS\"","\"EXISTS\"","\"USING\"","\"RELATIVE\"",
"\"RANGE\"","\"LEAF\"","\"CHILDOF\"","\"PARENTOF\"","\"ANCESTOROF\"",
"\"DESCENDANTOF\"","\"COUNT\"","\"SUM\"","\"AVG\"","\"MAX\"","\"MIN\"",
"\"H_LV\"","\"H_AID\"","\"REL\"","\"ABO\"","\"COALESCE\"","\"CASE\"","\"END\"",
"\"WHEN\"","\"THEN\"","\"QUERY\"","\"BEGIN\"","\"WITH\"","\"UNION\"","\"ALL\"",
"\"AS\"","\"SELECT\"","\"FROM\"","\"WHERE\"","\"CURRENT\"","\"OF\"","\"GROUP\"",
"\"BY\"","\"ROLLUP\"","\"HAVING\"","\"ORDER\"","\"ASC\"","\"DESC\"","\"JOIN\"",
"\"ON\"","\"RELATE\"","\"FOR\"","\"UPDATE\"","\"LEFT\"","\"RIGHT\"","\"FULL\"",
"\"DISTINCT\"","\"ORM\"","\"MAPPING\"","\"OVERRIDE\"","\"RETURNING\"",
"\"INTO\"","\"INSERT\"","\"VALUES\"","\"SET\"","\"DELETE\"","\"TABLE\"",
"\"ABSTRACT\"","\"EXTEND\"","\"FIELDS\"","\"INDEXES\"","\"RELATIONS\"",
"\"HIERARCHIES\"","\"PARTITION\"","\"VAVLE\"","\"MAXCOUNT\"","\"PRIMARY\"",
"\"KEY\"","\"BINARY\"","\"VARBINARY\"","\"BLOB\"","\"CHAR\"","\"VARCHAR\"",
"\"NCHAR\"","\"NVARCHAR\"","\"TEXT\"","\"NTEXT\"","\"NUMERIC\"","\"RELATION\"",
"\"TO\"","\"UNIQUE\"","\"MAXLEVEL\"","\"PROCEDURE\"","\"VAR\"","\"WHILE\"",
"\"LOOP\"","\"FOREACH\"","\"BREAK\"","\"PRINT\"","\"RETURN\"","\"FUNCTION\"",
};
final static String yyrule[] = {
"$accept : script",
"script : declare_stmt",
"declare_stmt : query_declare",
"declare_stmt : orm_declare",
"declare_stmt : insert_declare",
"declare_stmt : update_declare",
"declare_stmt : delete_declare",
"declare_stmt : table_declare",
"declare_stmt : procedure_declare",
"declare_stmt : function_declare",
"declare_stmt : declare_error",
"declare_error : \"DEFINE\" error",
"param_declare : VAR_REF param_type param_not_null param_default",
"param_declare : VAR_REF param_type param_default param_not_null",
"param_declare : VAR_REF param_type param_default",
"param_declare : VAR_REF param_type param_not_null",
"param_declare : VAR_REF param_type",
"param_declare : \"INOUT\" VAR_REF param_type param_not_null param_default",
"param_declare : \"INOUT\" VAR_REF param_type param_default param_not_null",
"param_declare : \"INOUT\" VAR_REF param_type param_default",
"param_declare : \"INOUT\" VAR_REF param_type param_not_null",
"param_declare : \"INOUT\" VAR_REF param_type",
"param_declare : \"OUT\" VAR_REF param_type",
"param_declare : \"OUT\" VAR_REF param_type param_not_null",
"param_declare : VAR_REF error",
"param_declare : \"INOUT\" VAR_REF error",
"param_declare : \"OUT\" VAR_REF error",
"param_not_null : \"NOT\" \"NULL\"",
"param_not_null : \"NOT\" error",
"param_not_null : \"NULL\"",
"param_default : \"DEFAULT\" literal",
"param_default : \"DEFAULT\" '-' literal",
"param_default : \"DEFAULT\" error",
"param_default : literal",
"param_type : \"BOOLEAN\"",
"param_type : \"BYTE\"",
"param_type : \"BYTES\"",
"param_type : \"DATE\"",
"param_type : \"DOUBLE\"",
"param_type : \"ENUM\" '<' class_name '>'",
"param_type : \"FLOAT\"",
"param_type : \"GUID\"",
"param_type : \"INT\"",
"param_type : \"LONG\"",
"param_type : \"SHORT\"",
"param_type : \"STRING\"",
"param_type : \"RECORDSET\"",
"param_type : \"ENUM\" '<' class_name error",
"param_type : \"ENUM\" '<' error",
"param_type : \"ENUM\" error",
"param_declare_list : param_declare_list ',' param_declare",
"param_declare_list : param_declare",
"param_declare_list : param_declare_list ',' error",
"param_declare_list_op : param_declare_list",
"param_declare_list_op :",
"column_ref : ID '.' ID",
"column_ref : ID '.' error",
"name_ref : ID",
"literal : STR_VAL",
"literal : INT_VAL",
"literal : LONG_VAL",
"literal : DOUBLE_VAL",
"literal : \"TRUE\"",
"literal : \"FALSE\"",
"literal : \"DATE\" STR_VAL",
"literal : \"GUID\" STR_VAL",
"literal : \"BYTES\" STR_VAL",
"literal : \"DATE\" error",
"literal : \"GUID\" error",
"literal : \"BYTES\" error",
"condition_expr : condition_expr \"OR\" and_expr",
"condition_expr : and_expr",
"condition_expr : condition_expr \"OR\" error",
"and_expr : and_expr \"AND\" not_expr",
"and_expr : not_expr",
"and_expr : and_expr \"AND\" error",
"not_expr : \"NOT\" compare_expr",
"not_expr : compare_expr",
"not_expr : \"NOT\" error",
"compare_expr : '(' condition_expr ')'",
"compare_expr : value_expr compare_operator value_expr",
"compare_expr : between_expr",
"compare_expr : like_expr",
"compare_expr : str_compare_expr",
"compare_expr : in_expr",
"compare_expr : is_null_expr",
"compare_expr : exists_expr",
"compare_expr : hierarchy_expr",
"compare_expr : path_expr",
"compare_expr : '(' condition_expr error",
"compare_expr : value_expr compare_operator error",
"compare_operator : '>'",
"compare_operator : '<'",
"compare_operator : GE",
"compare_operator : LE",
"compare_operator : '='",
"compare_operator : NE",
"between_expr : value_expr not_expr_op \"BETWEEN\" value_expr \"AND\" value_expr",
"between_expr : value_expr not_expr_op \"BETWEEN\" value_expr \"AND\" error",
"between_expr : value_expr not_expr_op \"BETWEEN\" value_expr error",
"between_expr : value_expr not_expr_op \"BETWEEN\" error",
"like_expr : value_expr not_expr_op \"LIKE\" value_expr escape_expr_op",
"like_expr : value_expr not_expr_op \"LIKE\" error",
"escape_expr_op : \"ESCAPE\" value_expr",
"escape_expr_op :",
"escape_expr_op : \"ESCAPE\" error",
"str_compare_expr : value_expr not_expr_op str_compare_predicate value_expr",
"str_compare_expr : value_expr not_expr_op str_compare_predicate error",
"str_compare_predicate : \"STARTS_WITH\"",
"str_compare_predicate : \"ENDS_WITH\"",
"str_compare_predicate : \"CONTAINS\"",
"in_expr : value_expr not_expr_op \"IN\" in_expr_param",
"in_expr : value_expr not_expr_op \"IN\" error",
"in_expr_param : '(' in_value_list ')'",
"in_expr_param : '(' sub_query ')'",
"in_expr_param : '(' in_value_list error",
"in_expr_param : '(' error",
"in_value_list : in_value_list ',' value_expr",
"in_value_list : value_expr",
"in_value_list : in_value_list ',' error",
"is_null_expr : value_expr \"IS\" not_expr_op \"NULL\"",
"is_null_expr : value_expr \"IS\" error",
"not_expr_op : \"NOT\"",
"not_expr_op :",
"exists_expr : \"EXISTS\" '(' sub_query ')'",
"exists_expr : \"EXISTS\" '(' sub_query error",
"exists_expr : \"EXISTS\" '(' error",
"exists_expr : \"EXISTS\" error",
"hierarchy_expr : ID hierarchy_predicate ID \"USING\" ID",
"hierarchy_expr : ID hierarchy_predicate ID \"USING\" ID \"RELATIVE\" value_expr",
"hierarchy_expr : ID hierarchy_predicate ID \"USING\" ID \"RANGE\" value_expr",
"hierarchy_expr : ID \"IS\" \"LEAF\" \"USING\" ID",
"hierarchy_expr : ID hierarchy_predicate ID \"USING\" ID \"RELATIVE\" error",
"hierarchy_expr : ID hierarchy_predicate ID \"USING\" ID \"RANGE\" error",
"hierarchy_expr : ID hierarchy_predicate ID \"USING\" error",
"hierarchy_expr : ID hierarchy_predicate ID error",
"hierarchy_expr : ID hierarchy_predicate error",
"hierarchy_expr : ID \"IS\" \"LEAF\" \"USING\" error",
"hierarchy_expr : ID \"IS\" \"LEAF\" error",
"hierarchy_expr : ID \"IS\" error",
"hierarchy_predicate : \"CHILDOF\"",
"hierarchy_predicate : \"PARENTOF\"",
"hierarchy_predicate : \"ANCESTOROF\"",
"hierarchy_predicate : \"DESCENDANTOF\"",
"path_expr : ID hierarchy_predicate ID \"USING\" '(' ID ',' ID ')'",
"path_expr : ID hierarchy_predicate ID \"USING\" '(' ID ',' ID ')' \"RELATIVE\" value_expr",
"path_expr : ID hierarchy_predicate ID \"USING\" '(' ID ',' ID ')' \"RELATIVE\" error",
"path_expr : ID hierarchy_predicate ID \"USING\" '(' ID ',' ID error",
"path_expr : ID hierarchy_predicate ID \"USING\" '(' ID ',' error",
"path_expr : ID hierarchy_predicate ID \"USING\" '(' ID error",
"path_expr : ID hierarchy_predicate ID \"USING\" '(' error",
"value_expr : value_expr '+' mul_expr",
"value_expr : value_expr '-' mul_expr",
"value_expr : value_expr CB mul_expr",
"value_expr : mul_expr",
"value_expr : value_expr '+' error",
"value_expr : value_expr '-' error",
"value_expr : value_expr CB error",
"mul_expr : mul_expr '*' neg_expr",
"mul_expr : mul_expr '/' neg_expr",
"mul_expr : mul_expr '%' neg_expr",
"mul_expr : neg_expr",
"mul_expr : mul_expr '*' error",
"mul_expr : mul_expr '/' error",
"neg_expr : '-' factor",
"neg_expr : factor",
"neg_expr : '-' error",
"var_ref : var_ref '.' ID",
"var_ref : VAR_REF",
"factor : column_ref",
"factor : literal",
"factor : var_ref",
"factor : \"NULL\"",
"factor : '(' value_expr ')'",
"factor : '(' sub_query ')'",
"factor : set_func",
"factor : scalar_func",
"factor : hierarchy_func",
"factor : coalesce_func",
"factor : simple_case",
"factor : searched_case",
"factor : '(' value_expr error",
"factor : '(' error",
"set_func : set_func_operator '(' set_quantifier_op value_expr ')'",
"set_func : set_func_operator '(' '*' ')'",
"set_func : set_func_operator '(' '*' error",
"set_func : set_func_operator '(' set_quantifier_op value_expr error",
"set_func : set_func_operator '(' error",
"set_func : set_func_operator error",
"set_func_operator : \"COUNT\"",
"set_func_operator : \"SUM\"",
"set_func_operator : \"AVG\"",
"set_func_operator : \"MAX\"",
"set_func_operator : \"MIN\"",
"scalar_func : ID '(' value_list ')'",
"scalar_func : ID '(' ')'",
"scalar_func : ID '(' value_list error",
"scalar_func : ID '(' error",
"hierarchy_func : \"H_LV\" '(' ID '.' ID ')'",
"hierarchy_func : \"H_AID\" '(' ID '.' ID ')'",
"hierarchy_func : \"H_AID\" '(' ID '.' ID \"REL\" value_expr ')'",
"hierarchy_func : \"H_AID\" '(' ID '.' ID \"ABO\" value_expr ')'",
"hierarchy_func : \"H_LV\" '(' ID '.' ID error",
"hierarchy_func : \"H_LV\" '(' ID '.' error",
"hierarchy_func : \"H_LV\" '(' ID error",
"hierarchy_func : \"H_LV\" '(' error",
"hierarchy_func : \"H_LV\" error",
"hierarchy_func : \"H_AID\" '(' ID '.' ID \"REL\" value_expr error",
"hierarchy_func : \"H_AID\" '(' ID '.' ID \"ABO\" value_expr error",
"hierarchy_func : \"H_AID\" '(' ID '.' ID \"REL\" error",
"hierarchy_func : \"H_AID\" '(' ID '.' ID \"ABO\" error",
"hierarchy_func : \"H_AID\" '(' ID '.' ID error",
"hierarchy_func : \"H_AID\" '(' ID '.' error",
"hierarchy_func : \"H_AID\" '(' ID error",
"hierarchy_func : \"H_AID\" '(' error",
"hierarchy_func : \"H_AID\" error",
"coalesce_func : \"COALESCE\" '(' value_list ')'",
"coalesce_func : \"COALESCE\" '(' value_list error",
"coalesce_func : \"COALESCE\" '(' error",
"coalesce_func : \"COALESCE\" error",
"simple_case : \"CASE\" value_expr simple_case_when_list case_else_expr_op \"END\"",
"simple_case : \"CASE\" value_expr simple_case_when_list case_else_expr_op error",
"simple_case : \"CASE\" value_expr error",
"simple_case : \"CASE\" error",
"simple_case_when_list : simple_case_when_list simple_case_when",
"simple_case_when_list : simple_case_when",
"simple_case_when : \"WHEN\" value_expr \"THEN\" value_expr",
"simple_case_when : \"WHEN\" value_expr \"THEN\" error",
"simple_case_when : \"WHEN\" value_expr error",
"simple_case_when : \"WHEN\" error",
"case_else_expr_op : ELSE value_expr",
"case_else_expr_op :",
"case_else_expr_op : ELSE error",
"searched_case : \"CASE\" searched_case_when_list case_else_expr_op \"END\"",
"searched_case : \"CASE\" searched_case_when_list case_else_expr_op error",
"searched_case_when_list : searched_case_when_list searched_case_when",
"searched_case_when_list : searched_case_when",
"searched_case_when : \"WHEN\" condition_expr \"THEN\" value_expr",
"searched_case_when : \"WHEN\" condition_expr \"THEN\" error",
"searched_case_when : \"WHEN\" condition_expr error",
"value_list : value_list ',' value_expr",
"value_list : value_expr",
"value_list : value_list ',' error",
"query_invoke : ID '(' value_list ')'",
"query_invoke : ID '(' ')'",
"query_declare : \"DEFINE\" \"QUERY\" ID '(' param_declare_list_op ')' \"BEGIN\" query_stmt \"END\"",
"query_declare : \"DEFINE\" \"QUERY\" ID '(' param_declare_list_op ')' \"BEGIN\" query_stmt ';' \"END\"",
"query_declare : \"DEFINE\" \"QUERY\" ID '(' param_declare_list_op ')' \"BEGIN\" query_stmt error",
"query_declare : \"DEFINE\" \"QUERY\" ID '(' param_declare_list_op ')' \"BEGIN\" error",
"query_declare : \"DEFINE\" \"QUERY\" ID '(' param_declare_list_op ')' error",
"query_declare : \"DEFINE\" \"QUERY\" ID '(' error",
"query_declare : \"DEFINE\" \"QUERY\" ID error",
"query_declare : \"DEFINE\" \"QUERY\" error",
"query_stmt : \"WITH\" query_with_list query_union orderby_op",
"query_stmt : query_union orderby_op",
"query_stmt : \"WITH\" query_with_list error",
"query_stmt : \"WITH\" error",
"query_union : sub_query \"UNION\" query_primary",
"query_union : sub_query \"UNION\" \"ALL\" query_primary",
"query_union : query_sub \"UNION\" query_primary",
"query_union : query_sub \"UNION\" \"ALL\" query_primary",
"query_union : query_select",
"query_union : sub_query \"UNION\" error",
"query_union : query_sub \"UNION\" error",
"query_sub : '(' sub_query ')'",
"query_sub : '(' sub_query error",
"query_sub : '(' error",
"query_primary : query_sub",
"query_primary : query_select",
"query_select : select from where_op groupby_op having_op",
"query_select : select error",
"sub_query : query_union orderby_op",
"query_with_list : query_with_list ',' query_with",
"query_with_list : query_with",
"query_with_list : query_with_list ',' error",
"query_with : '(' sub_query ')' \"AS\" ID",
"query_with : '(' sub_query ')' \"AS\" error",
"query_with : '(' sub_query ')' error",
"query_with : '(' sub_query error",
"select : \"SELECT\" set_quantifier_op query_column_list",
"select : \"SELECT\" error",
"from : \"FROM\" source_list",
"from : \"FROM\" error",
"source_list : source_list ',' source",
"source_list : source",
"source_list : source_list ',' error",
"where_op : \"WHERE\" condition_expr",
"where_op : \"WHERE\" \"CURRENT\" \"OF\" VAR_REF",
"where_op :",
"where_op : \"WHERE\" error",
"where_op : \"WHERE\" \"CURRENT\" \"OF\" error",
"where_op : \"WHERE\" \"CURRENT\" error",
"groupby_op : \"GROUP\" \"BY\" groupby_column_list",
"groupby_op : \"GROUP\" \"BY\" groupby_column_list \"WITH\" \"ROLLUP\"",
"groupby_op :",
"groupby_op : \"GROUP\" \"BY\" groupby_column_list \"WITH\" error",
"groupby_op : \"GROUP\" \"BY\" error",
"groupby_op : \"GROUP\" error",
"groupby_column_list : groupby_column_list ',' value_expr",
"groupby_column_list : value_expr",
"groupby_column_list : groupby_column_list ',' error",
"having_op : \"HAVING\" condition_expr",
"having_op :",
"having_op : \"HAVING\" error",
"orderby_op : \"ORDER\" \"BY\" orderby_column_list",
"orderby_op :",
"orderby_op : \"ORDER\" \"BY\" error",
"orderby_op : \"ORDER\" error",
"orderby_column_list : orderby_column_list ',' orderby_column",
"orderby_column_list : orderby_column",
"orderby_column : value_expr",
"orderby_column : value_expr \"ASC\"",
"orderby_column : value_expr \"DESC\"",
"orderby_column : ID",
"orderby_column : ID \"ASC\"",
"orderby_column : ID \"DESC\"",
"query_column_list : query_column_list ',' query_column",
"query_column_list : query_column",
"query_column_list : query_column_list ',' error",
"query_column : value_expr",
"query_column : value_expr \"AS\" ID",
"query_column : value_expr \"AS\" error",
"source : source join_type \"JOIN\" source_ref \"ON\" condition_expr",
"source : source join_type \"RELATE\" ID '.' ID",
"source : source join_type \"RELATE\" ID '.' ID \"AS\" ID",
"source : source_ref",
"source : source join_type \"JOIN\" source_ref \"ON\" error",
"source : source join_type \"JOIN\" source_ref error",
"source : source join_type \"JOIN\" error",
"source : source join_type \"RELATE\" ID '.' ID \"AS\" error",
"source : source join_type \"RELATE\" ID '.' error",
"source : source join_type \"RELATE\" ID error",
"source : source join_type \"RELATE\" error",
"source_ref : name_ref \"FOR\" \"UPDATE\"",
"source_ref : name_ref \"AS\" ID \"FOR\" \"UPDATE\"",
"source_ref : name_ref",
"source_ref : name_ref \"AS\" ID",
"source_ref : '(' sub_query ')' \"AS\" ID",
"source_ref : '(' source ')'",
"source_ref : name_ref \"FOR\" error",
"source_ref : name_ref \"AS\" ID \"FOR\" error",
"source_ref : name_ref \"AS\" error",
"source_ref : '(' sub_query ')' \"AS\" error",
"join_type : \"LEFT\"",
"join_type : \"RIGHT\"",
"join_type : \"FULL\"",
"join_type :",
"set_quantifier_op : \"ALL\"",
"set_quantifier_op : \"DISTINCT\"",
"set_quantifier_op :",
"orm_declare : \"DEFINE\" \"ORM\" ID '(' param_declare_list_op ')' \"MAPPING\" class_name \"BEGIN\" query_stmt \"END\"",
"orm_declare : \"DEFINE\" \"ORM\" ID '(' param_declare_list_op ')' \"OVERRIDE\" ID \"BEGIN\" where_op having_op orderby_op \"END\"",
"orm_declare : \"DEFINE\" \"ORM\" ID '(' param_declare_list_op ')' \"MAPPING\" class_name \"BEGIN\" query_stmt error",
"orm_declare : \"DEFINE\" \"ORM\" ID '(' param_declare_list_op ')' \"MAPPING\" class_name \"BEGIN\" error",
"orm_declare : \"DEFINE\" \"ORM\" ID '(' param_declare_list_op ')' \"MAPPING\" class_name error",
"orm_declare : \"DEFINE\" \"ORM\" ID '(' param_declare_list_op ')' \"MAPPING\" error",
"orm_declare : \"DEFINE\" \"ORM\" ID '(' param_declare_list_op ')' \"OVERRIDE\" ID \"BEGIN\" error",
"orm_declare : \"DEFINE\" \"ORM\" ID '(' param_declare_list_op ')' \"OVERRIDE\" ID error",
"orm_declare : \"DEFINE\" \"ORM\" ID '(' param_declare_list_op ')' \"OVERRIDE\" error",
"orm_declare : \"DEFINE\" \"ORM\" ID '(' param_declare_list_op ')' error",
"orm_declare : \"DEFINE\" \"ORM\" ID '(' error",
"orm_declare : \"DEFINE\" \"ORM\" ID error",
"orm_declare : \"DEFINE\" \"ORM\" error",
"class_name : class_list",
"class_list : class_list '.' ID",
"class_list : ID",
"class_list : class_list '.' error",
"returning_op : \"RETURNING\" ENV_REF \"INTO\" VAR_REF",
"returning_op :",
"returning_op : \"RETURNING\" ENV_REF \"INTO\" error",
"returning_op : \"RETURNING\" ENV_REF error",
"returning_op : \"RETURNING\" error",
"insert_declare : \"DEFINE\" \"INSERT\" ID '(' param_declare_list_op ')' \"BEGIN\" insert_stmt \"END\"",
"insert_declare : \"DEFINE\" \"INSERT\" ID '(' param_declare_list_op ')' \"BEGIN\" insert_stmt ';' \"END\"",
"insert_declare : \"DEFINE\" \"INSERT\" ID '(' param_declare_list_op ')' \"BEGIN\" insert_stmt error",
"insert_declare : \"DEFINE\" \"INSERT\" ID '(' param_declare_list_op ')' \"BEGIN\" error",
"insert_declare : \"DEFINE\" \"INSERT\" ID '(' param_declare_list_op ')' error",
"insert_declare : \"DEFINE\" \"INSERT\" ID '(' error",
"insert_declare : \"DEFINE\" \"INSERT\" ID error",
"insert_declare : \"DEFINE\" \"INSERT\" error",
"insert_stmt : insert insert_values returning_op",
"insert_stmt : insert insert_sub_query returning_op",
"insert_stmt : insert '(' error",
"insert_stmt : insert error",
"insert : \"INSERT\" \"INTO\" name_ref",
"insert : \"INSERT\" \"INTO\" error",
"insert : \"INSERT\" error",
"insert_sub_query : '(' sub_query ')'",
"insert_sub_query : '(' sub_query error",
"insert_values : '(' insert_column_list ')' \"VALUES\" '(' insert_value_list ')'",
"insert_values : '(' insert_column_list ')' \"VALUES\" '(' insert_value_list error",
"insert_values : '(' insert_column_list ')' \"VALUES\" '(' error",
"insert_values : '(' insert_column_list ')' \"VALUES\" error",
"insert_values : '(' insert_column_list ')' error",
"insert_values : '(' insert_column_list error",
"insert_value_list : insert_value_list ',' value_expr",
"insert_value_list : value_expr",
"insert_value_list : insert_value_list ',' error",
"insert_column_list : insert_column_list ',' ID",
"insert_column_list : ID",
"insert_column_list : insert_column_list ',' error",
"update_declare : \"DEFINE\" \"UPDATE\" ID '(' param_declare_list_op ')' \"BEGIN\" update_stmt \"END\"",
"update_declare : \"DEFINE\" \"UPDATE\" ID '(' param_declare_list_op ')' \"BEGIN\" update_stmt ';' \"END\"",
"update_declare : \"DEFINE\" \"UPDATE\" ID '(' param_declare_list_op ')' \"BEGIN\" update_stmt error",
"update_declare : \"DEFINE\" \"UPDATE\" ID '(' param_declare_list_op ')' \"BEGIN\" error",
"update_declare : \"DEFINE\" \"UPDATE\" ID '(' param_declare_list_op ')' error",
"update_declare : \"DEFINE\" \"UPDATE\" ID '(' error",
"update_declare : \"DEFINE\" \"UPDATE\" ID error",
"update_declare : \"DEFINE\" \"UPDATE\" error",
"update_stmt : update update_set where_op returning_op",
"update_stmt : update error",
"update : \"UPDATE\" source",
"update : \"UPDATE\" error",
"update_set : \"SET\" update_column_list",
"update_set : \"SET\" error",
"update_column_list : update_column_list ',' update_column_value",
"update_column_list : update_column_value",
"update_column_list : update_column_list ',' error",
"update_column_value : ID '=' value_expr",
"update_column_value : ID '=' error",
"update_column_value : ID error",
"delete_declare : \"DEFINE\" \"DELETE\" ID '(' param_declare_list_op ')' \"BEGIN\" delete_stmt \"END\"",
"delete_declare : \"DEFINE\" \"DELETE\" ID '(' param_declare_list_op ')' \"BEGIN\" delete_stmt ';' \"END\"",
"delete_declare : \"DEFINE\" \"DELETE\" ID '(' param_declare_list_op ')' \"BEGIN\" delete_stmt error",
"delete_declare : \"DEFINE\" \"DELETE\" ID '(' param_declare_list_op ')' \"BEGIN\" error",
"delete_declare : \"DEFINE\" \"DELETE\" ID '(' param_declare_list_op ')' error",
"delete_declare : \"DEFINE\" \"DELETE\" ID '(' error",
"delete_declare : \"DEFINE\" \"DELETE\" ID error",
"delete_declare : \"DEFINE\" \"DELETE\" error",
"delete_stmt : delete where_op returning_op",
"delete : \"DELETE\" \"FROM\" source",
"delete : \"DELETE\" \"FROM\" error",
"delete : \"DELETE\" error",
"table_declare : \"DEFINE\" \"TABLE\" ID table_extend_op \"BEGIN\" primary_section extend_section_op index_section_op relation_section_op hierarchy_section_op partition_section_op \"END\"",
"table_declare : \"DEFINE\" \"ABSTRACT\" \"TABLE\" ID table_extend_op \"BEGIN\" primary_section extend_section_op index_section_op relation_section_op hierarchy_section_op partition_section_op \"END\"",
"table_declare : \"DEFINE\" \"TABLE\" ID table_extend_op \"BEGIN\" error",
"table_declare : \"DEFINE\" \"TABLE\" ID error",
"table_declare : \"DEFINE\" \"TABLE\" error",
"table_declare : \"DEFINE\" \"ABSTRACT\" \"TABLE\" ID table_extend_op \"BEGIN\" error",
"table_declare : \"DEFINE\" \"ABSTRACT\" \"TABLE\" ID error",
"table_declare : \"DEFINE\" \"ABSTRACT\" \"TABLE\" error",
"table_declare : \"DEFINE\" \"ABSTRACT\" error",
"table_extend_op : \"EXTEND\" ID",
"table_extend_op :",
"table_extend_op : \"EXTEND\" error",
"primary_section : \"FIELDS\" table_field_list",
"primary_section : \"FIELDS\" error",
"extend_section_op : extend_section",
"extend_section_op :",
"extend_section : extend_section extend_declare",
"extend_section : extend_declare",
"extend_declare : \"FIELDS\" \"ON\" ID table_field_list",
"extend_declare : \"FIELDS\" \"ON\" error",
"extend_declare : \"FIELDS\" error",
"index_section_op : \"INDEXES\" index_declare_list",
"index_section_op :",
"index_section_op : \"INDEXES\" error",
"relation_section_op : \"RELATIONS\" relation_declare_list",
"relation_section_op :",
"relation_section_op : \"RELATIONS\" error",
"hierarchy_section_op : \"HIERARCHIES\" hierarchy_declare_list",
"hierarchy_section_op :",
"hierarchy_section_op : \"HIERARCHIES\" error",
"partition_section_op : \"PARTITION\" '(' partition_field_list ')' \"VAVLE\" INT_VAL \"MAXCOUNT\" INT_VAL",
"partition_section_op :",
"partition_section_op : \"PARTITION\" '(' partition_field_list ')' \"VAVLE\" INT_VAL \"MAXCOUNT\" error",
"partition_section_op : \"PARTITION\" '(' partition_field_list ')' \"VAVLE\" INT_VAL error",
"partition_section_op : \"PARTITION\" '(' partition_field_list ')' \"VAVLE\" error",
"partition_section_op : \"PARTITION\" '(' partition_field_list ')' error",
"partition_section_op : \"PARTITION\" '(' partition_field_list error",
"partition_section_op : \"PARTITION\" '(' error",
"partition_section_op : \"PARTITION\" error",
"table_field_list : table_field_list ',' table_field_declare",
"table_field_list : table_field_declare",
"table_field_list : table_field_list ',' error",
"table_field_declare : ID field_type field_not_null_op field_default_op",
"table_field_declare : ID field_type field_not_null_op field_default_op \"PRIMARY\" \"KEY\"",
"table_field_declare : ID field_type field_not_null_op field_default_op field_foreign_key",
"table_field_declare : ID field_type field_not_null_op field_default_op \"PRIMARY\" error",
"table_field_declare : ID error",
"field_not_null_op : param_not_null",
"field_not_null_op :",
"field_default_op : \"DEFAULT\" '(' literal ')'",
"field_default_op : \"DEFAULT\" '(' '-' literal ')'",
"field_default_op :",
"field_type : \"BOOLEAN\"",
"field_type : \"DATE\"",
"field_type : \"DOUBLE\"",
"field_type : \"FLOAT\"",
"field_type : \"GUID\"",
"field_type : \"INT\"",
"field_type : \"LONG\"",
"field_type : \"SHORT\"",
"field_type : \"BINARY\" '(' INT_VAL ')'",
"field_type : \"VARBINARY\" '(' INT_VAL ')'",
"field_type : \"BLOB\"",
"field_type : \"CHAR\" '(' INT_VAL ')'",
"field_type : \"VARCHAR\" '(' INT_VAL ')'",
"field_type : \"NCHAR\" '(' INT_VAL ')'",
"field_type : \"NVARCHAR\" '(' INT_VAL ')'",
"field_type : \"TEXT\"",
"field_type : \"NTEXT\"",
"field_type : \"NUMERIC\" '(' INT_VAL ',' INT_VAL ')'",
"field_type : \"BINARY\" '(' INT_VAL error",
"field_type : \"BINARY\" '(' error",
"field_type : \"BINARY\" error",
"field_type : \"VARBINARY\" '(' INT_VAL error",
"field_type : \"VARBINARY\" '(' error",
"field_type : \"VARBINARY\" error",
"field_type : \"CHAR\" '(' INT_VAL error",
"field_type : \"CHAR\" '(' error",
"field_type : \"CHAR\" error",
"field_type : \"VARCHAR\" '(' INT_VAL error",
"field_type : \"VARCHAR\" '(' error",
"field_type : \"VARCHAR\" error",
"field_type : \"NCHAR\" '(' INT_VAL error",
"field_type : \"NCHAR\" '(' error",
"field_type : \"NCHAR\" error",
"field_type : \"NVARCHAR\" '(' INT_VAL error",
"field_type : \"NVARCHAR\" '(' error",
"field_type : \"NVARCHAR\" error",
"field_type : \"NUMERIC\" '(' INT_VAL ',' INT_VAL error",
"field_type : \"NUMERIC\" '(' INT_VAL ',' error",
"field_type : \"NUMERIC\" '(' INT_VAL error",
"field_type : \"NUMERIC\" '(' error",
"field_type : \"NUMERIC\" error",
"field_foreign_key : \"RELATION\" ID \"TO\" ID '.' ID",
"field_foreign_key : \"RELATION\" ID \"TO\" ID '.' error",
"field_foreign_key : \"RELATION\" ID \"TO\" ID error",
"field_foreign_key : \"RELATION\" ID \"TO\" error",
"field_foreign_key : \"RELATION\" ID error",
"field_foreign_key : \"RELATION\" error",
"index_declare_list : index_declare_list ',' index_declare",
"index_declare_list : index_declare",
"index_declare_list : index_declare_list ',' error",
"index_declare : ID '(' index_order_list ')'",
"index_declare : \"UNIQUE\" ID '(' index_order_list ')'",
"index_declare : ID '(' index_order_list error",
"index_declare : ID '(' error",
"index_declare : ID error",
"index_declare : \"UNIQUE\" ID '(' index_order_list error",
"index_declare : \"UNIQUE\" ID '(' error",
"index_declare : \"UNIQUE\" ID error",
"index_declare : \"UNIQUE\" error",
"index_order_list : index_order_list ',' index_order",
"index_order_list : index_order",
"index_order_list : index_order_list ',' error",
"index_order : ID \"ASC\"",
"index_order : ID \"DESC\"",
"index_order : ID error",
"relation_declare_list : relation_declare_list ',' relation_declare",
"relation_declare_list : relation_declare",
"relation_declare_list : relation_declare_list ',' error",
"relation_declare : ID \"TO\" ID \"ON\" condition_expr",
"relation_declare : ID \"TO\" ID \"ON\" error",
"relation_declare : ID \"TO\" ID error",
"relation_declare : ID \"TO\" error",
"relation_declare : ID error",
"hierarchy_declare_list : hierarchy_declare_list ',' hierarchy_declare",
"hierarchy_declare_list : hierarchy_declare",
"hierarchy_declare_list : hierarchy_declare_list ',' error",
"hierarchy_declare : ID \"MAXLEVEL\" '(' INT_VAL ')'",
"hierarchy_declare : ID \"MAXLEVEL\" '(' INT_VAL error",
"hierarchy_declare : ID \"MAXLEVEL\" '(' error",
"hierarchy_declare : ID \"MAXLEVEL\" error",
"hierarchy_declare : ID error",
"partition_field_list : partition_field_list ',' ID",
"partition_field_list : ID",
"partition_field_list : partition_field_list ',' error",
"procedure_declare : \"DEFINE\" \"PROCEDURE\" ID '(' param_declare_list_op ')' \"BEGIN\" statement_list \"END\"",
"procedure_declare : \"DEFINE\" \"PROCEDURE\" ID '(' param_declare_list_op ')' \"BEGIN\" statement_list error",
"procedure_declare : \"DEFINE\" \"PROCEDURE\" ID '(' param_declare_list_op ')' \"BEGIN\" error",
"procedure_declare : \"DEFINE\" \"PROCEDURE\" ID '(' param_declare_list_op ')' error",
"procedure_declare : \"DEFINE\" \"PROCEDURE\" ID '(' error",
"procedure_declare : \"DEFINE\" \"PROCEDURE\" ID error",
"procedure_declare : \"DEFINE\" \"PROCEDURE\" error",
"statement_list : statement_list statement",
"statement_list : statement",
"statement : insert_stmt ';'",
"statement : update_stmt ';'",
"statement : delete_stmt ';'",
"statement : query_declare",
"statement : table_declare",
"statement : var_stmt ';'",
"statement : assign_stmt ';'",
"statement : if_stmt",
"statement : while_stmt",
"statement : loop_stmt",
"statement : foreach_stmt",
"statement : break ';'",
"statement : print ';'",
"statement : return ';'",
"statement : segment",
"statement : insert_stmt error",
"statement : update_stmt error",
"statement : delete_stmt error",
"statement : var_stmt error",
"statement : assign_stmt error",
"statement : break error",
"statement : print error",
"statement : return error",
"segment : \"BEGIN\" statement_list \"END\"",
"segment : \"BEGIN\" statement_list error",
"segment : \"BEGIN\" error",
"var_stmt : \"VAR\" VAR_REF var_type",
"var_stmt : \"VAR\" VAR_REF var_type '=' value_expr",
"var_stmt : \"VAR\" VAR_REF var_type '=' error",
"var_stmt : \"VAR\" VAR_REF error",
"var_stmt : \"VAR\" error",
"var_type : \"BOOLEAN\"",
"var_type : \"BYTE\"",
"var_type : \"BYTES\"",
"var_type : \"DATE\"",
"var_type : \"DOUBLE\"",
"var_type : \"ENUM\" '<' class_name '>'",
"var_type : \"FLOAT\"",
"var_type : \"GUID\"",
"var_type : \"INT\"",
"var_type : \"LONG\"",
"var_type : \"SHORT\"",
"var_type : \"STRING\"",
"var_type : \"ENUM\" '<' class_name error",
"var_type : \"ENUM\" '<' error",
"var_type : \"ENUM\" error",
"assign_stmt : VAR_REF '=' value_expr",
"assign_stmt : '(' primary_ref_list ')' '=' '(' value_list ')'",
"assign_stmt : VAR_REF '=' query_stmt",
"assign_stmt : '(' primary_ref_list ')' '=' query_stmt",
"assign_stmt : VAR_REF '=' error",
"assign_stmt : '(' primary_ref_list ')' '=' '(' value_list error",
"assign_stmt : '(' primary_ref_list ')' '=' '(' error",
"assign_stmt : '(' primary_ref_list ')' '=' error",
"assign_stmt : '(' primary_ref_list '='",
"assign_stmt : primary_ref_list ')' '='",
"primary_ref_list : primary_ref_list ',' VAR_REF",
"primary_ref_list : VAR_REF",
"primary_ref_list : primary_ref_list ',' error",
"primary_ref_list : primary_ref_list VAR_REF",
"if_stmt : IF condition_expr \"THEN\" statement",
"if_stmt : IF condition_expr \"THEN\" statement ELSE statement",
"if_stmt : IF condition_expr \"THEN\" statement ELSE error",
"if_stmt : IF condition_expr \"THEN\" error",
"if_stmt : IF condition_expr error",
"if_stmt : IF error",
"while_stmt : \"WHILE\" condition_expr \"LOOP\" statement",
"while_stmt : \"WHILE\" condition_expr \"LOOP\" error",
"while_stmt : \"WHILE\" condition_expr error",
"while_stmt : \"WHILE\" error",
"loop_stmt : \"LOOP\" statement",
"loop_stmt : \"LOOP\" error",
"foreach_stmt : \"FOREACH\" VAR_REF \"IN\" '(' query_stmt ')' \"LOOP\" statement",
"foreach_stmt : \"FOREACH\" VAR_REF \"IN\" query_invoke \"LOOP\" statement",
"foreach_stmt : \"FOREACH\" VAR_REF \"IN\" '(' query_stmt ')' \"LOOP\" error",
"foreach_stmt : \"FOREACH\" VAR_REF \"IN\" '(' query_stmt ')' error",
"foreach_stmt : \"FOREACH\" VAR_REF \"IN\" '(' query_stmt error",
"foreach_stmt : \"FOREACH\" VAR_REF \"IN\" '(' error",
"foreach_stmt : \"FOREACH\" VAR_REF \"IN\" error",
"foreach_stmt : \"FOREACH\" VAR_REF error",
"foreach_stmt : \"FOREACH\" error",
"break : \"BREAK\"",
"print : \"PRINT\" value_expr",
"print : \"PRINT\" error",
"return : \"RETURN\"",
"return : \"RETURN\" value_expr",
"function_declare : \"DEFINE\" \"FUNCTION\" ID '(' param_declare_list_op ')' param_type \"BEGIN\" statement_list \"END\"",
"function_declare : \"DEFINE\" \"FUNCTION\" ID '(' param_declare_list_op ')' param_type \"BEGIN\" statement_list error",
"function_declare : \"DEFINE\" \"FUNCTION\" ID '(' param_declare_list_op ')' param_type \"BEGIN\" error",
"function_declare : \"DEFINE\" \"FUNCTION\" ID '(' param_declare_list_op ')' param_type error",
"function_declare : \"DEFINE\" \"FUNCTION\" ID '(' param_declare_list_op ')' error",
"function_declare : \"DEFINE\" \"FUNCTION\" ID '(' error",
"function_declare : \"DEFINE\" \"FUNCTION\" ID error",
"function_declare : \"DEFINE\" \"FUNCTION\" error",
};

//#line 2950 "D:\Workspace\D&A\dnasql\meta\dnasql.y"


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
//#line 2651 "SQLParser.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
  if (ch < 0) ch=0;
  if (ch <= YYMAXTOKEN) //check index bounds
     s = yyname[ch];    //now get it
  if (s==null)
    s = "illegal-symbol";
  debug("state "+state+", reading "+ch+" ("+s+")");
}





//The following are now global, to aid in error reporting
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string


//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse()
{
boolean doaction;
  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  val_push(yylval);     //save empty value
  while (true) //until parsing is done, either correctly, or w/error
    {
    doaction=true;
    if (yydebug) debug("loop"); 
    //#### NEXT ACTION (from reduction table)
    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
      {
      if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
      if (yychar < 0)      //we want a char?
        {
        yychar = yylex();  //get next token
        if (yydebug) debug(" next yychar:"+yychar);
        //#### ERROR CHECK ####
        if (yychar < 0)    //it it didn't work/error
          {
          yychar = 0;      //change it to default string (no -1!)
          if (yydebug)
            yylexdebug(yystate,yychar);
          }
        }//yychar<0
      yyn = yysindex[yystate];  //get amount to shift by (shift index)
      if ((yyn != 0) && (yyn += yychar) >= 0 &&
          yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
        {
        if (yydebug)
          debug("state "+yystate+", shifting to state "+yytable[yyn]);
        //#### NEXT STATE ####
        yystate = yytable[yyn];//we are in a new state
        state_push(yystate);   //save it
        val_push(yylval);      //push our lval as the input for next rule
        yychar = -1;           //since we have 'eaten' a token, say we need another
        if (yyerrflag > 0)     //have we recovered an error?
           --yyerrflag;        //give ourselves credit
        doaction=false;        //but don't process yet
        break;   //quit the yyn=0 loop
        }

    yyn = yyrindex[yystate];  //reduce
    if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
      {   //we reduced!
      if (yydebug) debug("reduce");
      yyn = yytable[yyn];
      doaction=true; //get ready to execute
      break;         //drop down to actions
      }
    else //ERROR RECOVERY
      {
		//reduce on error
       yyn = yyrindex[yystate];
       if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
			yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE) {
	      if (yydebug) debug("reduce (error)");
         yyn = yytable[yyn];
         doaction=true;
         break;
       }
       if (yyerrflag==0) {
          // 判断是否存在error规则
          yyn = yysindex[state_peek(0)];
          if ((yyn == 0) || (yyn += YYERRCODE) < 0 ||
				yyn > YYTABLESIZE || yycheck[yyn] != YYERRCODE) {
             yyerror("syntax error");
          }
          yynerrs++;
        }
      if (yyerrflag < 3) //low error count?
        {
        yyerrflag = 3;
        while (true)   //do until break
          {
          if (stateptr<0)   //check for under & overflow here
            {
            yyerror("stack underflow. aborting...");  //note lower case 's'
            return 1;
            }
          yyn = yysindex[state_peek(0)];
          if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
            {
            if (yydebug)
              debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
            yystate = yytable[yyn];
            state_push(yystate);
            val_push(yylval);
            doaction=false;
            break;
            }
          else
            {
            if (yydebug)
              debug("error recovery discarding state "+state_peek(0)+" ");
            if (stateptr<0)   //check for under & overflow here
              {
              yyerror("Stack underflow. aborting...");  //capital 'S'
              return 1;
              }
            state_pop();
            val_pop();
            }
          }
        }
      else            //discard this token
        {
        if (yychar == 0)
          return 1; //yyabort
        if (yydebug)
          {
          yys = null;
          if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
          if (yys == null) yys = "illegal-symbol";
          debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
          }
        yychar = -1;  //read another
        }
      }//end error recovery
    }//yyn=0 loop
    if (!doaction)   //any reason not to proceed?
      continue;      //skip action
    yym = yylen[yyn];          //get count of terminals on rhs
    if (yydebug)
      debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
    if (yym>0)                 //if count of rhs not 'nil'
      yyval = val_peek(yym-1); //get current semantic value
    yyval = dup_yyval(yyval); //duplicate yyval if ParserVal is used as semantic value
    try {
    switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 1:
//#line 31 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new SQLScript((NStatement)val_peek(0), this.out);
		}
break;
case 2:
//#line 37 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 3:
//#line 38 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 4:
//#line 39 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 5:
//#line 40 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 6:
//#line 41 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 7:
//#line 42 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 8:
//#line 43 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 9:
//#line 44 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 10:
//#line 45 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 11:
//#line 49 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("无法识别的声明语句"));
			yyval = NStatement.EMPTY;
		}
break;
case 12:
//#line 58 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NParamDeclare(NParamDeclare.InOut.IN, (TString)val_peek(3),
					(NDataType)val_peek(2), true, (NLiteral)val_peek(0));
		}
break;
case 13:
//#line 62 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NParamDeclare(NParamDeclare.InOut.IN, (TString)val_peek(3),
					(NDataType)val_peek(2), true, (NLiteral)val_peek(1));
		}
break;
case 14:
//#line 66 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NParamDeclare(NParamDeclare.InOut.IN, (TString)val_peek(2),
					(NDataType)val_peek(1), false, (NLiteral)val_peek(0));
		}
break;
case 15:
//#line 70 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NParamDeclare(NParamDeclare.InOut.IN, (TString)val_peek(2),
					(NDataType)val_peek(1), true, null);
		}
break;
case 16:
//#line 74 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NParamDeclare(NParamDeclare.InOut.IN, (TString)val_peek(1),
					(NDataType)val_peek(0), false, null);
		}
break;
case 17:
//#line 78 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NParamDeclare(NParamDeclare.InOut.INOUT, (TString)val_peek(3),
					(NDataType)val_peek(2), true, (NLiteral)val_peek(0));
		}
break;
case 18:
//#line 82 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NParamDeclare(NParamDeclare.InOut.INOUT, (TString)val_peek(3),
					(NDataType)val_peek(2), true, (NLiteral)val_peek(1));
		}
break;
case 19:
//#line 86 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NParamDeclare(NParamDeclare.InOut.INOUT, (TString)val_peek(2),
					(NDataType)val_peek(1), false, (NLiteral)val_peek(0));
		}
break;
case 20:
//#line 90 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NParamDeclare(NParamDeclare.InOut.INOUT, (TString)val_peek(2),
					(NDataType)val_peek(1), true, null);
		}
break;
case 21:
//#line 94 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NParamDeclare(NParamDeclare.InOut.INOUT, (TString)val_peek(1),
					(NDataType)val_peek(0), false, null);
		}
break;
case 22:
//#line 98 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NParamDeclare(NParamDeclare.InOut.OUT, (TString)val_peek(1),
					(NDataType)val_peek(0), false, null);
		}
break;
case 23:
//#line 102 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NParamDeclare(NParamDeclare.InOut.OUT, (TString)val_peek(2),
					(NDataType)val_peek(1), true, null);
		}
break;
case 24:
//#line 107 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少参数类型"));
			yyval = NParamDeclare.EMPTY;
		}
break;
case 25:
//#line 111 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少参数类型"));
			yyval = NParamDeclare.EMPTY;
		}
break;
case 26:
//#line 115 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少参数类型"));
			yyval = NParamDeclare.EMPTY;
		}
break;
case 28:
//#line 124 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ after(val_peek(1), new SQLTokenNotFoundException("NULL")); }
break;
case 29:
//#line 125 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ after(val_peek(0), new SQLTokenNotFoundException("NOT")); }
break;
case 30:
//#line 129 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 31:
//#line 130 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = this.neg((NLiteral)val_peek(0)); }
break;
case 32:
//#line 132 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ after(val_peek(1), new SQLSyntaxException("缺少默认值")); }
break;
case 33:
//#line 133 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ after(val_peek(0), new SQLTokenNotFoundException("DEFAULT")); }
break;
case 34:
//#line 137 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = NDataType.BOOLEAN; }
break;
case 35:
//#line 138 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = NDataType.BYTE; }
break;
case 36:
//#line 139 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = NDataType.BYTES; }
break;
case 37:
//#line 140 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = NDataType.DATE; }
break;
case 38:
//#line 141 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = NDataType.DOUBLE; }
break;
case 39:
//#line 142 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = NDataType.ENUM((String)val_peek(1)); }
break;
case 40:
//#line 143 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = NDataType.FLOAT; }
break;
case 41:
//#line 144 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = NDataType.GUID; }
break;
case 42:
//#line 145 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = NDataType.INT; }
break;
case 43:
//#line 146 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = NDataType.LONG; }
break;
case 44:
//#line 147 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = NDataType.SHORT; }
break;
case 45:
//#line 148 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = NDataType.STRING; }
break;
case 46:
//#line 149 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = NDataType.RECORDSET; }
break;
case 47:
//#line 151 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			at(val_peek(2), new SQLTokenNotFoundException(">"));
			yyval = NDataType.UNKNOWN;
		}
break;
case 48:
//#line 155 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("无法识别的类名"));
			yyval = NDataType.UNKNOWN;
		}
break;
case 49:
//#line 159 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("<"));
			yyval = NDataType.UNKNOWN;
		}
break;
case 50:
//#line 166 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 51:
//#line 171 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = new LinkList();
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 52:
//#line 177 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少参数定义"));
			yyval = val_peek(2);
		}
break;
case 53:
//#line 184 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 54:
//#line 185 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = null; }
break;
case 55:
//#line 191 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = new NColumnRefExpr((TString)val_peek(0), (TString)val_peek(2)); }
break;
case 56:
//#line 193 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少字段名"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 57:
//#line 200 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = new NNameRef((TString)val_peek(0)); }
break;
case 58:
//#line 206 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = new NLiteralString((TString)val_peek(0)); }
break;
case 59:
//#line 207 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = new NLiteralInt((TInt)val_peek(0)); }
break;
case 60:
//#line 208 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{yyval = new NLiteralLong((TLong)val_peek(0)); }
break;
case 61:
//#line 209 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{yyval = new NLiteralDouble((TDouble)val_peek(0)); }
break;
case 62:
//#line 210 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = new NLiteralBoolean((TBoolean)val_peek(0)); }
break;
case 63:
//#line 211 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = new NLiteralBoolean((TBoolean)val_peek(0)); }
break;
case 64:
//#line 212 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			try {
				yyval = new NLiteralDate((TString)val_peek(0));
			} catch (SQLValueFormatException ex) {
				at(null, ex);
				yyval = NLiteralDate.EMPTY;
			}
		}
break;
case 65:
//#line 220 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			try {
				yyval = new NLiteralGUID((TString)val_peek(0));
			} catch (SQLValueFormatException ex) {
				at(null, ex);
				yyval = NLiteralGUID.EMPTY;
			}
		}
break;
case 66:
//#line 228 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			try {
				yyval = new NLiteralBytes((TString)val_peek(0));
			} catch (SQLValueFormatException ex) {
				at(null, ex);
				yyval = NLiteralBytes.EMPTY;
			}
		}
break;
case 67:
//#line 237 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少日期字符串"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 68:
//#line 241 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少GUID字符串"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 69:
//#line 245 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少BYTES字符串"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 70:
//#line 252 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NLogicalExpr(NLogicalExpr.Operator.OR, (NConditionExpr)val_peek(2), (NConditionExpr)val_peek(0));
		}
break;
case 71:
//#line 255 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 72:
//#line 257 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少条件表达式"));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 73:
//#line 264 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NLogicalExpr(NLogicalExpr.Operator.AND, (NConditionExpr)val_peek(2), (NConditionExpr)val_peek(0));
		}
break;
case 74:
//#line 267 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 75:
//#line 269 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少条件表达式"));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 76:
//#line 276 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NLogicalExpr(NLogicalExpr.Operator.NOT, (NConditionExpr)val_peek(0), null); 
		}
break;
case 77:
//#line 279 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 78:
//#line 281 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少条件表达式"));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 79:
//#line 288 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(1); }
break;
case 80:
//#line 289 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			TValueCompare op = (TValueCompare)val_peek(1);
			yyval = new NCompareExpr(op.value, (NValueExpr)val_peek(2), (NValueExpr)val_peek(0));
		}
break;
case 89:
//#line 302 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 90:
//#line 306 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式"));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 91:
//#line 313 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 92:
//#line 314 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 93:
//#line 315 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 94:
//#line 316 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 95:
//#line 317 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 96:
//#line 318 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 97:
//#line 322 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NBetweenExpr(((TBoolean)val_peek(4)).value, (NValueExpr)val_peek(5), 
				(NValueExpr)val_peek(2), (NValueExpr)val_peek(0));
		}
break;
case 98:
//#line 327 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式"));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 99:
//#line 331 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("AND"));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 100:
//#line 335 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式"));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 101:
//#line 342 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NLikeExpr((NValueExpr)val_peek(4), (NValueExpr)val_peek(1), (NValueExpr)val_peek(0),
				((TBoolean)val_peek(3)).value);
		}
break;
case 102:
//#line 347 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少字符串表达式"));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 103:
//#line 354 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 104:
//#line 355 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = null; }
break;
case 105:
//#line 357 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ after(val_peek(1), new SQLSyntaxException("缺少值表达式")); }
break;
case 106:
//#line 361 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			TStrCompare t = (TStrCompare)val_peek(1);
			yyval = new NStrCompareExpr(t.value, (NValueExpr)val_peek(3), 
				(NValueExpr)val_peek(0), ((TBoolean)val_peek(2)).value);
		}
break;
case 107:
//#line 367 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少字符串表达式"));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 108:
//#line 374 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 109:
//#line 375 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 110:
//#line 376 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 111:
//#line 380 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NInExpr(((TBoolean)val_peek(2)).value, (NValueExpr)val_peek(3), (NInExprParam)val_peek(0));
		}
break;
case 112:
//#line 384 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值列表或者子查询"));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 113:
//#line 391 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(1);
			NValueExpr[] arr = l.toArray(new NValueExpr[l.count()]);
			if (arr.length == 1 && arr[0] instanceof NQuerySpecific) {
				yyval = new NInParamSubQuery((Token)val_peek(2), (Token)val_peek(0), (NQuerySpecific)arr[0]);
			} else {
				yyval = new NInParamValueList((Token)val_peek(2), (Token)val_peek(0), arr);
			}
		}
break;
case 114:
//#line 400 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NInParamSubQuery((Token)val_peek(2), (Token)val_peek(0), (NQuerySpecific)val_peek(1));
		}
break;
case 115:
//#line 404 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NInExprParam.EMPTY;
		}
break;
case 116:
//#line 408 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值列表或者子查询"));
			yyval = NInExprParam.EMPTY;
		}
break;
case 117:
//#line 415 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 118:
//#line 420 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = new LinkList();
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 119:
//#line 426 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式"));
			yyval = NInExprParam.EMPTY;
		}
break;
case 120:
//#line 433 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NIsNullExpr((Token)val_peek(0), ((TBoolean)val_peek(1)).value, (NValueExpr)val_peek(3));
		}
break;
case 121:
//#line 437 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("NULL"));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 122:
//#line 444 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = new TBoolean(true, 0, 0, 0); }
break;
case 123:
//#line 445 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = new TBoolean(false, 0, 0, 0); }
break;
case 124:
//#line 449 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NExistsExpr((Token)val_peek(3), (Token)val_peek(0), (NQuerySpecific)val_peek(1));
		}
break;
case 125:
//#line 453 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 126:
//#line 457 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少查询语句"));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 127:
//#line 461 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 128:
//#line 468 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			THierarchy t = (THierarchy)val_peek(3);
			if (t.value != NHierarchyExpr.Keywords.DESCENDANTOF) {
				yyval = new NHierarchyExpr(t.value, (TString)val_peek(4), (TString)val_peek(2),
						(TString)val_peek(0));
			} else {
				yyval = new NDescendantOfExpr((TString)val_peek(4), (TString)val_peek(2),
							(TString)val_peek(0), null, false);
			}
		}
break;
case 129:
//#line 478 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			THierarchy t = (THierarchy)val_peek(5);
			if (t.value != NHierarchyExpr.Keywords.DESCENDANTOF) {
				at(val_peek(1), new SQLNotSupportedException("只有DESCENDANTOF谓词支持RELATIVE关键字"));
				yyval = NConditionExpr.EMPTY;
			} else {
				yyval = new NDescendantOfExpr((TString)val_peek(6), (TString)val_peek(4), (TString)val_peek(2), (NValueExpr)val_peek(0), false);
			}
		}
break;
case 130:
//#line 487 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			THierarchy t = (THierarchy)val_peek(5);
			if (t.value != NHierarchyExpr.Keywords.DESCENDANTOF) {
				at(val_peek(1), new SQLNotSupportedException("只有DESCENDANTOF谓词支持RANGE关键字"));
				yyval = NConditionExpr.EMPTY;
			} else {
				yyval = new NDescendantOfExpr((TString)val_peek(6), (TString)val_peek(4), (TString)val_peek(2), (NValueExpr)val_peek(0), true);
			}
		}
break;
case 131:
//#line 496 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NIsLeafExpr((TString)val_peek(4), (TString)val_peek(0));
		}
break;
case 132:
//#line 500 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式"));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 133:
//#line 504 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式"));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 134:
//#line 508 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少表关系名称"));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 135:
//#line 512 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("USING"));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 136:
//#line 516 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少表别名"));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 137:
//#line 520 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少表关系名称"));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 138:
//#line 524 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("USING"));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 139:
//#line 528 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("LEAF"));
			yyval = NConditionExpr.EMPTY;
		}
break;
case 140:
//#line 535 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 141:
//#line 536 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 142:
//#line 537 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 143:
//#line 538 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 144:
//#line 542 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			THierarchy t = (THierarchy)val_peek(7);
			yyval = new NPathExpr(t.value, (TString)val_peek(8), (TString)val_peek(6),
						(TString)val_peek(3), (TString)val_peek(1), null);
		}
break;
case 145:
//#line 547 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			THierarchy t = (THierarchy)val_peek(9);
			NValueExpr diff = (NValueExpr)val_peek(0);
			if (t.value != NHierarchyExpr.Keywords.ANCESTOROF && diff != null) {
				at(val_peek(1), new SQLNotSupportedException("只有ANCESTOROF谓词支持相对级次"));
				yyval = NPathExpr.EMPTY;
			} else {
				yyval = new NPathExpr(t.value, (TString)val_peek(10), (TString)val_peek(8),
							(TString)val_peek(5), (TString)val_peek(3), diff);
			}
		}
break;
case 146:
//#line 559 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式"));
			yyval = NPathExpr.EMPTY;
		}
break;
case 147:
//#line 563 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NPathExpr.EMPTY;
		}
break;
case 148:
//#line 567 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少字段名称"));
			yyval = NPathExpr.EMPTY;
		}
break;
case 149:
//#line 571 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(","));
			yyval = NPathExpr.EMPTY;
		}
break;
case 150:
//#line 575 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少字段名称"));
			yyval = NPathExpr.EMPTY;
		}
break;
case 151:
//#line 582 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NBinaryExpr(NBinaryExpr.Operator.ADD, (NValueExpr)val_peek(2), (NValueExpr)val_peek(0));
		}
break;
case 152:
//#line 585 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NBinaryExpr(NBinaryExpr.Operator.SUB, (NValueExpr)val_peek(2), (NValueExpr)val_peek(0));
		}
break;
case 153:
//#line 588 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NBinaryExpr(NBinaryExpr.Operator.COMBINE, (NValueExpr)val_peek(2), (NValueExpr)val_peek(0));
		}
break;
case 154:
//#line 591 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 155:
//#line 593 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 156:
//#line 597 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 157:
//#line 601 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 158:
//#line 608 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NBinaryExpr(NBinaryExpr.Operator.MUL, (NValueExpr)val_peek(2), (NValueExpr)val_peek(0));
		}
break;
case 159:
//#line 611 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NBinaryExpr(NBinaryExpr.Operator.DIV, (NValueExpr)val_peek(2), (NValueExpr)val_peek(0));
		}
break;
case 160:
//#line 614 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NBinaryExpr(NBinaryExpr.Operator.MOD, (NValueExpr)val_peek(2), (NValueExpr)val_peek(0));
		}
break;
case 161:
//#line 617 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 162:
//#line 619 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 163:
//#line 623 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 164:
//#line 630 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NNegativeExpr((NValueExpr)val_peek(0));
		}
break;
case 165:
//#line 633 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 166:
//#line 635 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 167:
//#line 642 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = new NVarRefExpr((NVarRefExpr)val_peek(2), (TString)val_peek(0)); }
break;
case 168:
//#line 643 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = new NVarRefExpr(null, (TString)val_peek(0)); }
break;
case 169:
//#line 647 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 170:
//#line 648 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 171:
//#line 649 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 172:
//#line 650 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = new NNullExpr((Token)val_peek(0)); }
break;
case 173:
//#line 651 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(1); }
break;
case 174:
//#line 652 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(1); }
break;
case 175:
//#line 653 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 176:
//#line 654 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 177:
//#line 655 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 178:
//#line 656 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval= val_peek(0); }
break;
case 179:
//#line 657 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 180:
//#line 658 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 181:
//#line 660 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 182:
//#line 664 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 183:
//#line 671 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			TSetFunction t = (TSetFunction)val_peek(4);
			SetQuantifier q = (SetQuantifier)val_peek(2);
			switch(t.value) {
			case MAX:
			case MIN:
				if (q != null)
					after(val_peek(3), new SQLSyntaxException("不支持ALL/DISTINCT"));
				break;
			}
			yyval = new NAggregateExpr(t, (Token)val_peek(0), (NValueExpr)val_peek(1), q == null ? SetQuantifier.ALL : q);
		}
break;
case 184:
//#line 683 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			TSetFunction t = (TSetFunction)val_peek(3);
			if (t.value != NAggregateExpr.Func.COUNT) {
				at(val_peek(1), new SQLSyntaxException("'*'只能用于COUNT函数"));
				yyval = NValueExpr.EMPTY;
			} else {
				yyval = new NAggregateExpr(t, (Token)val_peek(0), null, SetQuantifier.ALL);
			}
		}
break;
case 185:
//#line 693 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 186:
//#line 697 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 187:
//#line 701 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式或者'*'"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 188:
//#line 705 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NValueExpr.EMPTY;
		}
break;
case 189:
//#line 712 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 190:
//#line 713 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 191:
//#line 714 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 192:
//#line 715 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 193:
//#line 716 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 194:
//#line 720 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			TString name = (TString)val_peek(3);
			LinkList l = (LinkList)val_peek(1);
			NValueExpr[] params = l.toArray(new NValueExpr[l.count()]);
			try {
				SQLFuncSpec func = SQLFuncSpec.valueOf(name.value);
				yyval = new NFunctionExpr((Token)val_peek(3), (Token)val_peek(0), func, params);
			} catch (IllegalArgumentException ex) {
				at(val_peek(3), new SQLFunctionUndefinedException(name.value));
				yyval = NValueExpr.EMPTY;
			}
		}
break;
case 195:
//#line 732 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			TString name = (TString)val_peek(2);
			try {
				SQLFuncSpec func = SQLFuncSpec.valueOf(name.value);
				yyval = new NFunctionExpr((Token)val_peek(2), (Token)val_peek(0), func, null);
			} catch (IllegalArgumentException ex) {
				at(val_peek(2), new SQLFunctionUndefinedException(name.value));
				yyval = NValueExpr.EMPTY;
			}
		}
break;
case 196:
//#line 743 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 197:
//#line 747 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 198:
//#line 754 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NHlvExpr((Token)val_peek(5), (Token)val_peek(0), (TString)val_peek(3), (TString)val_peek(1));
		}
break;
case 199:
//#line 757 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NHaidExpr((Token)val_peek(5), (Token)val_peek(0), (TString)val_peek(3), (TString)val_peek(1), null, false);
		}
break;
case 200:
//#line 760 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NHaidExpr((Token)val_peek(7), (Token)val_peek(0), (TString)val_peek(5), (TString)val_peek(3), (NValueExpr)val_peek(1), true);
		}
break;
case 201:
//#line 763 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NHaidExpr((Token)val_peek(7), (Token)val_peek(0), (TString)val_peek(5), (TString)val_peek(3), (NValueExpr)val_peek(1), false);
		}
break;
case 202:
//#line 767 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 203:
//#line 771 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少表关系名称"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 204:
//#line 775 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("."));
			yyval = NValueExpr.EMPTY;
		}
break;
case 205:
//#line 779 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少表名称"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 206:
//#line 783 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NValueExpr.EMPTY;
		}
break;
case 207:
//#line 787 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 208:
//#line 791 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 209:
//#line 795 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少整型表达式"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 210:
//#line 799 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少整型表达式"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 211:
//#line 803 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 212:
//#line 807 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少表关系名称"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 213:
//#line 811 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("."));
			yyval = NValueExpr.EMPTY;
		}
break;
case 214:
//#line 815 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少表名称"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 215:
//#line 819 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NValueExpr.EMPTY;
		}
break;
case 216:
//#line 826 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(1);
			NValueExpr[] params = l.toArray(new NValueExpr[l.count()]);
			yyval = new NCoalesceExpr((Token)val_peek(3), (Token)val_peek(0), params);
		}
break;
case 217:
//#line 832 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 218:
//#line 836 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少参数"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 219:
//#line 840 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NValueExpr.EMPTY;
		}
break;
case 220:
//#line 847 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			yyval = new NSimpleCaseExpr((Token)val_peek(4), (Token)val_peek(0), (NValueExpr)val_peek(3),
					l.toArray(new NSimpleCaseWhen[l.count()]), (NValueExpr)val_peek(1));
		}
break;
case 221:
//#line 853 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			Object obj = val_peek(1);
			if (obj == null) {
				obj = val_peek(2);
			}
			after(obj, new SQLTokenNotFoundException("END"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 222:
//#line 861 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("WHEN"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 223:
//#line 865 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少表达式"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 224:
//#line 873 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(1);
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 225:
//#line 878 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = new LinkList();
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 226:
//#line 886 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NSimpleCaseWhen((NValueExpr)val_peek(2), (NValueExpr)val_peek(0));
		}
break;
case 227:
//#line 890 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式"));
			yyval = NSimpleCaseWhen.EMPTY;
		}
break;
case 228:
//#line 894 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("THEN"));
			yyval = NSimpleCaseWhen.EMPTY;
		}
break;
case 229:
//#line 898 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少表达式"));
			yyval = NSimpleCaseWhen.EMPTY;
		}
break;
case 230:
//#line 905 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 231:
//#line 906 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = null; }
break;
case 232:
//#line 908 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ after(val_peek(1), new SQLSyntaxException("缺少值表达式")); }
break;
case 233:
//#line 912 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			yyval = new NSearchedCaseExpr((Token)val_peek(3), (Token)val_peek(0),
					l.toArray(new NSearchedCaseWhen[l.count()]), (NValueExpr)val_peek(1));
		}
break;
case 234:
//#line 918 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			Object obj = val_peek(1);
			if (obj == null) {
				obj = val_peek(2);
			}
			after(obj, new SQLTokenNotFoundException("END"));
			yyval = NValueExpr.EMPTY;
		}
break;
case 235:
//#line 929 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(1);
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 236:
//#line 934 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = new LinkList();
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 237:
//#line 942 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NSearchedCaseWhen((NConditionExpr)val_peek(2), (NValueExpr)val_peek(0));
		}
break;
case 238:
//#line 946 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式"));
			yyval = NSearchedCaseWhen.EMPTY;
		}
break;
case 239:
//#line 950 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("THEN"));
			yyval = NSearchedCaseWhen.EMPTY;
		}
break;
case 240:
//#line 957 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 241:
//#line 962 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = new LinkList();
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 242:
//#line 968 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式"));
			yyval = val_peek(2);
		}
break;
case 243:
//#line 977 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(1);
			yyval = new NQueryInvoke((Token)val_peek(0), (TString)val_peek(3), l.toArray(new NValueExpr[l.count()]));
		}
break;
case 244:
//#line 981 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NQueryInvoke((Token)val_peek(0), (TString)val_peek(2), null);
		}
break;
case 245:
//#line 987 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(4);
			yyval = new NQueryDeclare((Token)val_peek(8), (Token)val_peek(0), (TString)val_peek(6),
					l == null ? null : l.toArray(new NParamDeclare[l.count()]),
					(NQueryStmt)val_peek(1));
		}
break;
case 246:
//#line 993 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(5);
			yyval = new NQueryDeclare((Token)val_peek(9), (Token)val_peek(0), (TString)val_peek(7),
					l == null ? null : l.toArray(new NParamDeclare[l.count()]),
					(NQueryStmt)val_peek(2));
		}
break;
case 247:
//#line 1001 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("END"));
			yyval = NStatement.EMPTY;
		}
break;
case 248:
//#line 1006 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少查询语句"));
			yyval = NStatement.EMPTY;
		}
break;
case 249:
//#line 1010 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("BEGIN"));
			yyval = NStatement.EMPTY;
		}
break;
case 250:
//#line 1014 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NStatement.EMPTY;
		}
break;
case 251:
//#line 1018 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NStatement.EMPTY;
		}
break;
case 252:
//#line 1022 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			at(val_peek(1), new SQLSyntaxException("缺少名称"));
			yyval = NStatement.EMPTY;
		}
break;
case 253:
//#line 1029 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			NQueryWith[] arr = l.toArray(new NQueryWith[l.count()]);
			yyval = new NQueryStmt((Token)val_peek(3), arr, (NQuerySpecific)val_peek(1), (NOrderBy)val_peek(0));
		}
break;
case 254:
//#line 1034 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NQueryStmt(null, null, (NQuerySpecific)val_peek(1), (NOrderBy)val_peek(0));
		}
break;
case 255:
//#line 1038 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少SELECT语句"));
			yyval = NQueryStmt.EMPTY;
		}
break;
case 256:
//#line 1042 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少子查询或者'('"));
			yyval = NQueryStmt.EMPTY;
		}
break;
case 257:
//#line 1049 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			NQuerySpecific s = (NQuerySpecific)val_peek(2);
			s.union((NQuerySpecific)val_peek(0), false);
			yyval = s;
		}
break;
case 258:
//#line 1054 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			NQuerySpecific s = (NQuerySpecific)val_peek(3);
			s.union((NQuerySpecific)val_peek(0), true);
			yyval = s;
		}
break;
case 259:
//#line 1059 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			NQuerySpecific s = (NQuerySpecific)val_peek(2);
			s.union((NQuerySpecific)val_peek(0), false);
			yyval = s;
		}
break;
case 260:
//#line 1064 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			NQuerySpecific s = (NQuerySpecific)val_peek(3);
			s.union((NQuerySpecific)val_peek(0), true);
			yyval = s;
		}
break;
case 261:
//#line 1069 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 262:
//#line 1071 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少SELECT语句"));
			yyval = val_peek(2);
		}
break;
case 263:
//#line 1075 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少SELECT语句"));
			yyval = val_peek(2);
		}
break;
case 264:
//#line 1082 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(1); }
break;
case 265:
//#line 1084 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = val_peek(1);
		}
break;
case 266:
//#line 1088 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少SELECT语句"));
			yyval = NQuerySpecific.EMPTY;
		}
break;
case 267:
//#line 1095 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 268:
//#line 1096 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 269:
//#line 1100 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			NWhere w = (NWhere)val_peek(2);
			if (w != null && w.cursor != null) {
				at(w, new SQLNotSupportedException("查询语句中不能使用WHERE CURRENT OF子句"));
				yyval = NQuerySpecific.EMPTY;
			} else {
				yyval = new NQuerySpecific((NSelect)val_peek(4), (NFrom)val_peek(3), w,
							(NGroupBy)val_peek(1), (NHaving)val_peek(0));
			}
		}
break;
case 270:
//#line 1111 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("FROM"));
			yyval = NQuerySpecific.EMPTY;
		}
break;
case 271:
//#line 1118 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(1); }
break;
case 272:
//#line 1122 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 273:
//#line 1127 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = new LinkList();
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 274:
//#line 1133 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少子查询"));
			yyval = val_peek(2);
		}
break;
case 275:
//#line 1140 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NQueryWith((Token)val_peek(4), (TString)val_peek(0), (NQuerySpecific)val_peek(3));
		}
break;
case 276:
//#line 1144 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少别名"));
			yyval = NQueryWith.EMPTY;
		}
break;
case 277:
//#line 1148 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("AS"));
			yyval = NQueryWith.EMPTY;
		}
break;
case 278:
//#line 1152 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NQueryWith.EMPTY;
		}
break;
case 279:
//#line 1159 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(0);
			NQueryColumn[] columns = l.toArray(new NQueryColumn[l.count()]);
			yyval = new NSelect((Token)val_peek(2), (SetQuantifier)val_peek(1), columns);
		}
break;
case 280:
//#line 1165 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少列选表达式"));
			yyval = NSelect.EMPTY;
		}
break;
case 281:
//#line 1172 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(0);
			yyval = new NFrom((Token)val_peek(1), l.toArray(new NSource[l.count()]));
		}
break;
case 282:
//#line 1177 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少数据源"));
			yyval = NFrom.EMPTY;
		}
break;
case 283:
//#line 1184 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 284:
//#line 1189 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = new LinkList();
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 285:
//#line 1195 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少数据源"));
			yyval = val_peek(2);
		}
break;
case 286:
//#line 1202 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = new NWhere((Token)val_peek(1), (NConditionExpr)val_peek(0)); }
break;
case 287:
//#line 1203 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = new NWhere((Token)val_peek(3), (TString)val_peek(0)); }
break;
case 288:
//#line 1204 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = null; }
break;
case 289:
//#line 1206 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ after(val_peek(1), new SQLSyntaxException("缺少条件表达式")); }
break;
case 290:
//#line 1207 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ after(val_peek(1), new SQLSyntaxException("缺少变量名")); }
break;
case 291:
//#line 1208 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ after(val_peek(1), new SQLTokenNotFoundException("OF")); }
break;
case 292:
//#line 1212 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(0);
			NValueExpr[] columns = l.toArray(new NValueExpr[l.count()]);
			yyval = new NGroupBy((Token)val_peek(2), null, columns, GroupByType.DEFAULT);
		}
break;
case 293:
//#line 1217 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			NValueExpr[] columns = l.toArray(new NValueExpr[l.count()]);
			yyval = new NGroupBy((Token)val_peek(4), (Token)val_peek(0), columns, GroupByType.ROLL_UP);
		}
break;
case 294:
//#line 1222 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = null; }
break;
case 295:
//#line 1224 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("ROLLUP"));
		}
break;
case 296:
//#line 1227 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ after(val_peek(1), new SQLSyntaxException("缺少列表达式")); }
break;
case 297:
//#line 1228 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ after(val_peek(1), new SQLTokenNotFoundException("BY")); }
break;
case 298:
//#line 1232 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 299:
//#line 1237 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = new LinkList();
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 300:
//#line 1243 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少列表达式"));
			yyval = val_peek(2);
		}
break;
case 301:
//#line 1250 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = new NHaving((Token)val_peek(1), (NConditionExpr)val_peek(0)); }
break;
case 302:
//#line 1251 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = null; }
break;
case 303:
//#line 1253 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ after(val_peek(1), new SQLSyntaxException("缺少条件表达式")); }
break;
case 304:
//#line 1257 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(0);
			NOrderByColumn[] columns = l.toArray(new NOrderByColumn[l.count()]);
			yyval = new NOrderBy((Token)val_peek(2), columns);
		}
break;
case 305:
//#line 1262 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = null; }
break;
case 306:
//#line 1264 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ after(val_peek(1), new SQLSyntaxException("缺少列表达式")); }
break;
case 307:
//#line 1265 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ after(val_peek(1), new SQLTokenNotFoundException("BY")); }
break;
case 308:
//#line 1269 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 309:
//#line 1274 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = new LinkList();
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 310:
//#line 1282 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			NValueExpr e = (NValueExpr)val_peek(0);
			yyval = new NOrderByColumn(e, e, true);
		}
break;
case 311:
//#line 1286 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = new NOrderByColumn((Token)val_peek(0), (NValueExpr)val_peek(1), true); }
break;
case 312:
//#line 1287 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = new NOrderByColumn((Token)val_peek(0), (NValueExpr)val_peek(1), false); }
break;
case 313:
//#line 1288 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			TString name = (TString)val_peek(0);
			yyval = new NOrderByColumn(name, name, true);
		}
break;
case 314:
//#line 1292 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = new NOrderByColumn((Token)val_peek(0), (TString)val_peek(1), true); }
break;
case 315:
//#line 1293 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = new NOrderByColumn((Token)val_peek(0), (TString)val_peek(1), false); }
break;
case 316:
//#line 1297 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 317:
//#line 1302 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = new LinkList();
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 318:
//#line 1308 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少列选表达式"));
			yyval = val_peek(2);
		}
break;
case 319:
//#line 1315 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NQueryColumn((NValueExpr)val_peek(0), null);
		}
break;
case 320:
//#line 1318 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			TString alias = (TString)val_peek(0);
			if (alias.value.charAt(0) == '$') {
				at(alias, new SQLSyntaxException("别名不能以$符号开始"));
				yyval = new NQueryColumn((NValueExpr)val_peek(2), null);
			} else {
				yyval = new NQueryColumn((NValueExpr)val_peek(2), alias);
			}
		}
break;
case 321:
//#line 1328 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少别名"));
			yyval = NQueryColumn.EMPTY;
		}
break;
case 322:
//#line 1335 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NSourceJoin((TableJoinType)val_peek(4), (NSource)val_peek(5), (NSource)val_peek(2), (NConditionExpr)val_peek(0));
		}
break;
case 323:
//#line 1338 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NSourceRelate((TableJoinType)val_peek(4), (NSource)val_peek(5), (TString)val_peek(2), (TString)val_peek(0), null);
		}
break;
case 324:
//#line 1341 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			TString alias = (TString)val_peek(0);
			if (alias.value.charAt(0) == '$') {
				at(alias, new SQLSyntaxException("别名不能以$符号开始"));
				yyval = new NSourceRelate((TableJoinType)val_peek(6), (NSource)val_peek(7), (TString)val_peek(4), (TString)val_peek(2), null);
			} else {
				yyval = new NSourceRelate((TableJoinType)val_peek(6), (NSource)val_peek(7), (TString)val_peek(4), (TString)val_peek(2), alias);
			}
		}
break;
case 325:
//#line 1350 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 326:
//#line 1352 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少条件表达式"));
			yyval = NSource.EMPTY;
		}
break;
case 327:
//#line 1356 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("ON"));
			yyval = NSource.EMPTY;
		}
break;
case 328:
//#line 1360 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少表引用或者子查询"));
			yyval = NSource.EMPTY;
		}
break;
case 329:
//#line 1364 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少别名"));
			yyval = NSource.EMPTY;
		}
break;
case 330:
//#line 1368 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少关系名"));
			yyval = NSource.EMPTY;
		}
break;
case 331:
//#line 1372 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("."));
			yyval = NSource.EMPTY;
		}
break;
case 332:
//#line 1376 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少表引用"));
			yyval = NSource.EMPTY;
		}
break;
case 333:
//#line 1383 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NSourceTable((NNameRef)val_peek(2), null, true);
		}
break;
case 334:
//#line 1386 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			TString alias = (TString)val_peek(2);
			if (alias.value.charAt(0) == '$') {
				at(alias, new SQLSyntaxException("别名不能以$符号开始"));
				yyval = new NSourceTable((NNameRef)val_peek(4), null, true);
			} else {
				yyval = new NSourceTable((NNameRef)val_peek(4), alias, true);
			}
		}
break;
case 335:
//#line 1395 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NSourceTable((NNameRef)val_peek(0), null, false);
		}
break;
case 336:
//#line 1398 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			TString alias = (TString)val_peek(0);
			if (alias.value.charAt(0) == '$') {
				at(alias, new SQLSyntaxException("别名不能以$符号开始"));
				yyval = new NSourceTable((NNameRef)val_peek(2), null, false);
			} else {
				yyval = new NSourceTable((NNameRef)val_peek(2), alias, false);
			}
		}
break;
case 337:
//#line 1407 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			TString alias = (TString)val_peek(0);
			if (alias.value.charAt(0) == '$') {
				at(alias, new SQLSyntaxException("别名不能以$符号开始"));
				yyval = NSource.EMPTY;
			} else {
				yyval = new NSourceSubQuery((Token)val_peek(4), (NQuerySpecific)val_peek(3), alias);
			}
		}
break;
case 338:
//#line 1416 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(1); }
break;
case 339:
//#line 1418 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("UPDATE"));
			yyval = NSource.EMPTY;
		}
break;
case 340:
//#line 1422 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("UPDATE"));
			yyval = NSource.EMPTY;
		}
break;
case 341:
//#line 1426 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少别名"));
			yyval = NSource.EMPTY;
		}
break;
case 342:
//#line 1430 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少别名"));
			yyval = NSource.EMPTY;
		}
break;
case 343:
//#line 1437 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = TableJoinType.LEFT; }
break;
case 344:
//#line 1438 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = TableJoinType.RIGHT; }
break;
case 345:
//#line 1439 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = TableJoinType.FULL; }
break;
case 346:
//#line 1440 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = TableJoinType.INNER; }
break;
case 347:
//#line 1444 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = SetQuantifier.ALL; }
break;
case 348:
//#line 1445 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = SetQuantifier.DISTINCT; }
break;
case 349:
//#line 1446 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = null; }
break;
case 350:
//#line 1451 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(6);
			yyval = new NOrmDeclare((Token)val_peek(10), (Token)val_peek(0), (TString)val_peek(8), 
						l == null ? null : l.toArray(new NParamDeclare[l.count()]),
						(String)val_peek(3), (NQueryStmt)val_peek(1));
		}
break;
case 351:
//#line 1458 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			String base = ((TString)val_peek(5)).value;
			try {
				NOrmDeclare orm = this.openSQL(NOrmDeclare.class,
						DNASqlType.ORM, base);
				NQueryStmt q = orm.body;
				if (q.expr.unions != null) {
					throw new SQLNotSupportedException("不支持重写包含UNION的查询");
				}
				LinkList l = (LinkList)val_peek(8);
				NParamDeclare[] params = l == null ? null : l.toArray(new NParamDeclare[l.count()]);
				NQuerySpecific s = q.expr;
				s = new NQuerySpecific(s.select, s.from, (NWhere)val_peek(3), s.group, (NHaving)val_peek(2));
				yyval = new NOrmOverride((Token)val_peek(12), (Token)val_peek(0), (TString)val_peek(10), 
						params, (TString)val_peek(5), orm.className,
						new NQueryStmt(null, null, s, (NOrderBy)val_peek(1)));
			} catch (SQLParseException ex) {
				at(val_peek(12), ex);
				yyval = NStatement.EMPTY;
			}
		}
break;
case 352:
//#line 1481 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("BEGIN"));
			yyval = NStatement.EMPTY;
		}
break;
case 353:
//#line 1486 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少查询语句"));
			yyval = NStatement.EMPTY;
		}
break;
case 354:
//#line 1490 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(2), new SQLTokenNotFoundException("BEGIN"));
			yyval = NStatement.EMPTY;
		}
break;
case 355:
//#line 1494 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少类名"));
			yyval = NStatement.EMPTY;
		}
break;
case 356:
//#line 1498 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("END"));
			yyval = NStatement.EMPTY;
		}
break;
case 357:
//#line 1502 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("BEGIN"));
			yyval = NStatement.EMPTY;
		}
break;
case 358:
//#line 1506 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少ORM名称"));
			yyval = NStatement.EMPTY;
		}
break;
case 359:
//#line 1510 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("MAPPING/OVERRIDE"));
			yyval = NStatement.EMPTY;
		}
break;
case 360:
//#line 1514 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NStatement.EMPTY;
		}
break;
case 361:
//#line 1518 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NStatement.EMPTY;
		}
break;
case 362:
//#line 1522 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			at(val_peek(1), new SQLSyntaxException("缺少名称"));
			yyval = NStatement.EMPTY;
		}
break;
case 363:
//#line 1529 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			StringBuilder sb = (StringBuilder)val_peek(0);
			yyval = sb.toString();
		}
break;
case 364:
//#line 1536 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			StringBuilder sb = (StringBuilder)val_peek(2);
			sb.append(".");
			sb.append(((TString)val_peek(0)).value);
			yyval = sb;
		}
break;
case 365:
//#line 1542 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			StringBuilder sb = new StringBuilder();
			sb.append(((TString)val_peek(0)).value);
			yyval = sb;
		}
break;
case 366:
//#line 1548 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少包名或类名"));
			yyval = val_peek(2);
		}
break;
case 367:
//#line 1555 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			TString ref = (TString)val_peek(2);
			if ("#rowcount".equals(ref.value)) {
				yyval = new NReturning((Token)val_peek(3), (TString)val_peek(2), (TString)val_peek(0));
			} else {
				at(ref, new SQLNotSupportedException("只支持#rowcount系统变量"));
			}
		}
break;
case 368:
//#line 1563 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = null; }
break;
case 369:
//#line 1565 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少变量名称"));
			yyval = null;
		}
break;
case 370:
//#line 1569 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("INTO"));
			yyval = null;
		}
break;
case 371:
//#line 1573 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少系统变量名称"));
			yyval = null;
		}
break;
case 372:
//#line 1582 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(4);
			yyval = new NInsertDeclare((Token)val_peek(8), (Token)val_peek(0), (TString)val_peek(6),
						l == null ? null : l.toArray(new NParamDeclare[l.count()]),
						(NInsertStmt)val_peek(1));
		}
break;
case 373:
//#line 1588 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(5);
			yyval = new NInsertDeclare((Token)val_peek(9), (Token)val_peek(0), (TString)val_peek(7),
						l == null ? null : l.toArray(new NParamDeclare[l.count()]),
						(NInsertStmt)val_peek(2));
		}
break;
case 374:
//#line 1596 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("END"));
			yyval = NStatement.EMPTY;
		}
break;
case 375:
//#line 1601 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少新增语句"));
			yyval = NStatement.EMPTY;
		}
break;
case 376:
//#line 1605 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("BEGIN"));
			yyval = NStatement.EMPTY;
		}
break;
case 377:
//#line 1609 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NStatement.EMPTY;
		}
break;
case 378:
//#line 1613 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NStatement.EMPTY;
		}
break;
case 379:
//#line 1617 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			at(val_peek(1), new SQLSyntaxException("缺少名称"));
			yyval = NStatement.EMPTY;
		}
break;
case 380:
//#line 1624 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NInsertStmt((NInsert)val_peek(2), (NInsertSource)val_peek(1), (NReturning)val_peek(0));
		}
break;
case 381:
//#line 1627 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NInsertStmt((NInsert)val_peek(2), (NInsertSource)val_peek(1), (NReturning)val_peek(0));
		}
break;
case 382:
//#line 1631 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少字段列表或者SELECT语句"));
			yyval = NInsertStmt.EMPTY;
		}
break;
case 383:
//#line 1635 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值列表或者子查询"));
			yyval = NInsertStmt.EMPTY;
		}
break;
case 384:
//#line 1642 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NInsert((Token)val_peek(2), (NNameRef)val_peek(0));
		}
break;
case 385:
//#line 1646 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少表名称"));
			yyval = NInsert.EMPTY;
		}
break;
case 386:
//#line 1650 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("INTO"));
			yyval = NInsert.EMPTY;
		}
break;
case 387:
//#line 1657 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NInsertSubQuery((Token)val_peek(2), (Token)val_peek(0), (NQuerySpecific)val_peek(1));
		}
break;
case 388:
//#line 1661 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NInsertSource.EMPTY;
		}
break;
case 389:
//#line 1668 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList column_list = (LinkList)val_peek(5);
			TString[] columns = column_list.toArray(new TString[column_list.count()]);
			LinkList value_list = (LinkList)val_peek(1);
			NValueExpr[] values = value_list.toArray(new NValueExpr[value_list.count()]);
			yyval = new NInsertValues((Token)val_peek(6), (Token)val_peek(0), columns, values);
		}
break;
case 390:
//#line 1676 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NInsertSource.EMPTY;
		}
break;
case 391:
//#line 1680 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值列表"));
			yyval = NInsertSource.EMPTY;
		}
break;
case 392:
//#line 1684 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NInsertSource.EMPTY;
		}
break;
case 393:
//#line 1688 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("VALUES"));
			yyval = NInsertSource.EMPTY;
		}
break;
case 394:
//#line 1692 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NInsertSource.EMPTY;
		}
break;
case 395:
//#line 1699 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 396:
//#line 1704 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = new LinkList();
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 397:
//#line 1710 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式"));
			yyval = val_peek(2);
		}
break;
case 398:
//#line 1717 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 399:
//#line 1722 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = new LinkList();
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 400:
//#line 1728 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少字段名称"));
			yyval = val_peek(2);
		}
break;
case 401:
//#line 1737 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(4);
			yyval = new NUpdateDeclare((Token)val_peek(8), (Token)val_peek(0), (TString)val_peek(6),
						l == null ? null : l.toArray(new NParamDeclare[l.count()]),
						(NUpdateStmt)val_peek(1));
		}
break;
case 402:
//#line 1743 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(5);
			yyval = new NUpdateDeclare((Token)val_peek(9), (Token)val_peek(0), (TString)val_peek(7),
						l == null ? null : l.toArray(new NParamDeclare[l.count()]),
						(NUpdateStmt)val_peek(2));
		}
break;
case 403:
//#line 1751 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("END"));
			yyval = NStatement.EMPTY;
		}
break;
case 404:
//#line 1756 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少更新语句"));
			yyval = NStatement.EMPTY;
		}
break;
case 405:
//#line 1760 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("BEGIN"));
			yyval = NStatement.EMPTY;
		}
break;
case 406:
//#line 1764 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NStatement.EMPTY;
		}
break;
case 407:
//#line 1768 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NStatement.EMPTY;
		}
break;
case 408:
//#line 1772 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			at(val_peek(1), new SQLSyntaxException("缺少名称"));
			yyval = NStatement.EMPTY;
		}
break;
case 409:
//#line 1779 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NUpdateStmt((NUpdate)val_peek(3), (NUpdateSet)val_peek(2), (NWhere)val_peek(1), (NReturning)val_peek(0));
		}
break;
case 410:
//#line 1783 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("SET"));
			yyval = NUpdateStmt.EMPTY;
		}
break;
case 411:
//#line 1790 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NUpdate((Token)val_peek(1), (NSource)val_peek(0));
		}
break;
case 412:
//#line 1794 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少目标表"));
			yyval = NUpdate.EMPTY;
		}
break;
case 413:
//#line 1801 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(0);
			NUpdateColumnValue[] columns = l.toArray(new NUpdateColumnValue[l.count()]);
			yyval = new NUpdateSet((Token)val_peek(1), columns);
		}
break;
case 414:
//#line 1807 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少赋值表达式"));
			yyval = NUpdateSet.EMPTY;
		}
break;
case 415:
//#line 1814 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 416:
//#line 1819 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = new LinkList();
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 417:
//#line 1825 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少赋值表达式"));
			yyval = val_peek(2);
		}
break;
case 418:
//#line 1832 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NUpdateColumnValue((TString)val_peek(2), (NValueExpr)val_peek(0));
		}
break;
case 419:
//#line 1836 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式"));
			yyval = NUpdateColumnValue.EMPTY;
		}
break;
case 420:
//#line 1840 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(0), new SQLTokenNotFoundException("="));
			yyval = NUpdateColumnValue.EMPTY;
		}
break;
case 421:
//#line 1849 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(4);
			yyval = new NDeleteDeclare((Token)val_peek(8), (Token)val_peek(0), (TString)val_peek(6),
						l == null ? null : l.toArray(new NParamDeclare[l.count()]),
						(NDeleteStmt)val_peek(1));
		}
break;
case 422:
//#line 1855 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(5);
			yyval = new NDeleteDeclare((Token)val_peek(9), (Token)val_peek(0), (TString)val_peek(7),
						l == null ? null : l.toArray(new NParamDeclare[l.count()]),
						(NDeleteStmt)val_peek(2));
		}
break;
case 423:
//#line 1863 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("END"));
			yyval = NStatement.EMPTY;
		}
break;
case 424:
//#line 1868 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少删除语句"));
			yyval = NStatement.EMPTY;
		}
break;
case 425:
//#line 1872 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("BEGIN"));
			yyval = NStatement.EMPTY;
		}
break;
case 426:
//#line 1876 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NStatement.EMPTY;
		}
break;
case 427:
//#line 1880 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NStatement.EMPTY;
		}
break;
case 428:
//#line 1884 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			at(val_peek(1), new SQLSyntaxException("缺少名称"));
			yyval = NStatement.EMPTY;
		}
break;
case 429:
//#line 1891 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NDeleteStmt((NDelete)val_peek(2), (NWhere)val_peek(1), (NReturning)val_peek(0));
		}
break;
case 430:
//#line 1897 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NDelete((Token)val_peek(2), (NSource)val_peek(0));
		}
break;
case 431:
//#line 1901 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少目标表"));
			yyval = NDelete.EMPTY;
		}
break;
case 432:
//#line 1905 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(0), new SQLTokenNotFoundException("FROM"));
			yyval = NDelete.EMPTY;
		}
break;
case 433:
//#line 1918 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NTableDeclare((Token)val_peek(11), (Token)val_peek(0), (TString)val_peek(9),
					(NAbstractTableDeclare)val_peek(8), (NTablePrimary)val_peek(6),
					(NTableExtend[])val_peek(5), (NTableIndex[])val_peek(4), (NTableRelation[])val_peek(3),
					(NTableHierarchy[])val_peek(2), (NTablePartition)val_peek(1));
		}
break;
case 434:
//#line 1928 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			NAbstractTableDeclare base = (NAbstractTableDeclare)val_peek(8);
			NTableExtend[] extend = (NTableExtend[])val_peek(5);
			NTableRelation[] relation = (NTableRelation[])val_peek(3);
			NTableHierarchy[] hierarchy = (NTableHierarchy[])val_peek(2);
			NTablePartition partition = (NTablePartition)val_peek(1);
			/* 暂时仅支持主表字段和索引
*/
			Object start = val_peek(12);
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
			yyval = new NAbstractTableDeclare((Token)start, (Token)val_peek(0), (TString)val_peek(9),
					base, (NTablePrimary)val_peek(6), extend, (NTableIndex[])val_peek(4),
					relation, hierarchy, partition);
		}
break;
case 435:
//#line 1956 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("END"));
			yyval = NStatement.EMPTY;
		}
break;
case 436:
//#line 1960 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("BEGIN"));
			yyval = NStatement.EMPTY;
		}
break;
case 437:
//#line 1964 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少名称"));
			yyval = NStatement.EMPTY;
		}
break;
case 438:
//#line 1968 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("END"));
			yyval = NStatement.EMPTY;
		}
break;
case 439:
//#line 1972 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("BEGIN"));
			yyval = NStatement.EMPTY;
		}
break;
case 440:
//#line 1976 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少名称"));
			yyval = NStatement.EMPTY;
		}
break;
case 441:
//#line 1980 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("TABLE"));
			yyval = NStatement.EMPTY;
		}
break;
case 442:
//#line 1987 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			try {
				NAbstractTableDeclare base = this.openSQL(NAbstractTableDeclare.class,
						DNASqlType.ABSTRACT_TABLE, ((TString)val_peek(0)).value);
				yyval = base;
			} catch (SQLParseException ex) {
				at(val_peek(1), ex);
			}
		}
break;
case 443:
//#line 1996 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = null; }
break;
case 444:
//#line 1998 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少基础表名称"));
		}
break;
case 445:
//#line 2004 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(0);
			NTableField[] arr = l.toArray(new NTableField[l.count()]);
			yyval = new NTablePrimary((Token)val_peek(1), arr);
		}
break;
case 446:
//#line 2010 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少字段列表"));
			yyval = NTablePrimary.EMPTY;
		}
break;
case 447:
//#line 2017 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(0);
			yyval = l.toArray(new NTableExtend[l.count()]);
		}
break;
case 448:
//#line 2021 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = null; }
break;
case 449:
//#line 2025 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(1);
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 450:
//#line 2030 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = new LinkList();
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 451:
//#line 2038 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(0);
			NTableField[] arr = l.toArray(new NTableField[l.count()]);
			yyval = new NTableExtend((Token)val_peek(3), (TString)val_peek(1), arr);
		}
break;
case 452:
//#line 2044 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少字段列表"));
		}
break;
case 453:
//#line 2047 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("ON"));
		}
break;
case 454:
//#line 2053 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(0);
			yyval = l.toArray(new NTableIndex[l.count()]);
		}
break;
case 455:
//#line 2057 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = null; }
break;
case 456:
//#line 2059 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少索引列表"));
		}
break;
case 457:
//#line 2065 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(0);
			yyval = l.toArray(new NTableRelation[l.count()]);
		}
break;
case 458:
//#line 2069 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = null; }
break;
case 459:
//#line 2071 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少关系列表"));
		}
break;
case 460:
//#line 2077 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(0);
			yyval = l.toArray(new NTableHierarchy[l.count()]);
		}
break;
case 461:
//#line 2081 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = null; }
break;
case 462:
//#line 2083 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少级次列表"));
		}
break;
case 463:
//#line 2090 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(5);
			TString[] arr = l.toArray(new TString[l.count()]);
			yyval = new NTablePartition((Token)val_peek(7), arr, (TInt)val_peek(2), (TInt)val_peek(0));
		}
break;
case 464:
//#line 2095 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = null; }
break;
case 465:
//#line 2097 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少最大分区数目"));
		}
break;
case 466:
//#line 2100 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("MAXCOUNT"));
		}
break;
case 467:
//#line 2103 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少阀值"));
		}
break;
case 468:
//#line 2106 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("VAVLE"));
		}
break;
case 469:
//#line 2109 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
		}
break;
case 470:
//#line 2112 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少字段列表"));
		}
break;
case 471:
//#line 2115 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
		}
break;
case 472:
//#line 2121 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 473:
//#line 2126 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = new LinkList();
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 474:
//#line 2132 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少字段声明"));
			yyval = val_peek(2);
		}
break;
case 475:
//#line 2139 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NTableField((TString)val_peek(3), (NDataType)val_peek(2),
					((Boolean)val_peek(1)).booleanValue(), (NLiteral)val_peek(0), false);
		}
break;
case 476:
//#line 2143 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NTableField((TString)val_peek(5), (NDataType)val_peek(4),
					((Boolean)val_peek(3)).booleanValue(), (NLiteral)val_peek(2), true);
		}
break;
case 477:
//#line 2148 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NTableField((TString)val_peek(4), (NDataType)val_peek(3),
					((Boolean)val_peek(2)).booleanValue(), (NLiteral)val_peek(1), (NTableForeignKey)val_peek(0));
		}
break;
case 478:
//#line 2153 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("KEY"));
			yyval = new NTableField((TString)val_peek(5), (NDataType)val_peek(4),
					((Boolean)val_peek(3)).booleanValue(), (NLiteral)val_peek(2), true);
		}
break;
case 479:
//#line 2158 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少字段类型"));
			yyval = NTableField.EMPTY;
		}
break;
case 480:
//#line 2165 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = Boolean.TRUE; }
break;
case 481:
//#line 2166 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = Boolean.FALSE; }
break;
case 482:
//#line 2170 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(1); }
break;
case 483:
//#line 2171 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = this.neg((NLiteral)val_peek(1)); }
break;
case 484:
//#line 2172 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = null; }
break;
case 485:
//#line 2176 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = NDataType.BOOLEAN; }
break;
case 486:
//#line 2177 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = NDataType.DATE; }
break;
case 487:
//#line 2178 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = NDataType.DOUBLE; }
break;
case 488:
//#line 2179 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = NDataType.FLOAT; }
break;
case 489:
//#line 2180 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = NDataType.GUID; }
break;
case 490:
//#line 2181 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = NDataType.INT; }
break;
case 491:
//#line 2182 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = NDataType.LONG; }
break;
case 492:
//#line 2183 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = NDataType.SHORT; }
break;
case 493:
//#line 2184 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = NDataType.BINARY((TInt)val_peek(1)); }
break;
case 494:
//#line 2185 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = NDataType.VARBINARY((TInt)val_peek(1)); }
break;
case 495:
//#line 2186 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = NDataType.BLOB; }
break;
case 496:
//#line 2187 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = NDataType.CHAR((TInt)val_peek(1)); }
break;
case 497:
//#line 2188 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = NDataType.VARCHAR((TInt)val_peek(1)); }
break;
case 498:
//#line 2189 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = NDataType.NCHAR((TInt)val_peek(1)); }
break;
case 499:
//#line 2190 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = NDataType.NVARCHAR((TInt)val_peek(1)); }
break;
case 500:
//#line 2191 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = NDataType.TEXT; }
break;
case 501:
//#line 2192 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = NDataType.NTEXT; }
break;
case 502:
//#line 2193 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = NDataType.NUMERIC((TInt)val_peek(3), (TInt)val_peek(1));
		}
break;
case 503:
//#line 2197 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NDataType.UNKNOWN;
		}
break;
case 504:
//#line 2201 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少长度"));
			yyval = NDataType.UNKNOWN;
		}
break;
case 505:
//#line 2205 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NDataType.UNKNOWN;
		}
break;
case 506:
//#line 2209 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NDataType.UNKNOWN;
		}
break;
case 507:
//#line 2213 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少长度"));
			yyval = NDataType.UNKNOWN;
		}
break;
case 508:
//#line 2217 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NDataType.UNKNOWN;
		}
break;
case 509:
//#line 2221 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NDataType.UNKNOWN;
		}
break;
case 510:
//#line 2225 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少长度"));
			yyval = NDataType.UNKNOWN;
		}
break;
case 511:
//#line 2229 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NDataType.UNKNOWN;
		}
break;
case 512:
//#line 2233 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NDataType.UNKNOWN;
		}
break;
case 513:
//#line 2237 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少长度"));
			yyval = NDataType.UNKNOWN;
		}
break;
case 514:
//#line 2241 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NDataType.UNKNOWN;
		}
break;
case 515:
//#line 2245 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NDataType.UNKNOWN;
		}
break;
case 516:
//#line 2249 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少长度"));
			yyval = NDataType.UNKNOWN;
		}
break;
case 517:
//#line 2253 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NDataType.UNKNOWN;
		}
break;
case 518:
//#line 2257 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NDataType.UNKNOWN;
		}
break;
case 519:
//#line 2261 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少长度"));
			yyval = NDataType.UNKNOWN;
		}
break;
case 520:
//#line 2265 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NDataType.UNKNOWN;
		}
break;
case 521:
//#line 2269 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NDataType.UNKNOWN;
		}
break;
case 522:
//#line 2273 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少精度"));
			yyval = NDataType.UNKNOWN;
		}
break;
case 523:
//#line 2277 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(","));
			yyval = NDataType.UNKNOWN;
		}
break;
case 524:
//#line 2281 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少长度"));
			yyval = NDataType.UNKNOWN;
		}
break;
case 525:
//#line 2285 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NDataType.UNKNOWN;
		}
break;
case 526:
//#line 2292 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
		yyval = new NTableForeignKey((TString)val_peek(4), (TString)val_peek(2), (TString)val_peek(0));
	}
break;
case 527:
//#line 2296 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少字段名称"));
		}
break;
case 528:
//#line 2299 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("."));
		}
break;
case 529:
//#line 2302 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少表名称"));
		}
break;
case 530:
//#line 2305 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("TO"));
		}
break;
case 531:
//#line 2308 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少名称"));
		}
break;
case 532:
//#line 2314 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 533:
//#line 2319 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = new LinkList();
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 534:
//#line 2325 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少索引声明"));
			yyval = val_peek(2);
		}
break;
case 535:
//#line 2332 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(1);
			NTableIndexField[] arr = l.toArray(new NTableIndexField[l.count()]);
			TString name = (TString)val_peek(3);
			yyval = new NTableIndex(name, (Token)val_peek(0), name, arr, false);
		}
break;
case 536:
//#line 2338 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(1);
			NTableIndexField[] arr = l.toArray(new NTableIndexField[l.count()]);
			yyval = new NTableIndex((Token)val_peek(4), (Token)val_peek(0), (TString)val_peek(3), arr, true);
		}
break;
case 537:
//#line 2344 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NTableIndex.EMPTY;
		}
break;
case 538:
//#line 2348 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少顺序列表"));
			yyval = NTableIndex.EMPTY;
		}
break;
case 539:
//#line 2352 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NTableIndex.EMPTY;
		}
break;
case 540:
//#line 2356 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NTableIndex.EMPTY;
		}
break;
case 541:
//#line 2360 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少顺序列表"));
			yyval = NTableIndex.EMPTY;
		}
break;
case 542:
//#line 2364 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NTableIndex.EMPTY;
		}
break;
case 543:
//#line 2368 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少名称"));
			yyval = NTableIndex.EMPTY;
		}
break;
case 544:
//#line 2375 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 545:
//#line 2380 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = new LinkList();
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 546:
//#line 2386 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少顺序声明"));
			yyval = val_peek(2);
		}
break;
case 547:
//#line 2393 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = new NTableIndexField((TString)val_peek(1), false); }
break;
case 548:
//#line 2394 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = new NTableIndexField((TString)val_peek(1), true); }
break;
case 549:
//#line 2396 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("ASC/DESC"));
			yyval = new NTableIndexField((TString)val_peek(1), false);
		}
break;
case 550:
//#line 2403 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 551:
//#line 2408 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = new LinkList();
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 552:
//#line 2414 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少关系声明"));
			yyval = val_peek(2);
		}
break;
case 553:
//#line 2421 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NTableRelation((TString)val_peek(4), (TString)val_peek(2), (NConditionExpr)val_peek(0));
		}
break;
case 554:
//#line 2425 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少条件表达式"));
			yyval = NTableRelation.EMPTY;
		}
break;
case 555:
//#line 2429 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("ON"));
			yyval = NTableRelation.EMPTY;
		}
break;
case 556:
//#line 2433 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少表名称"));
			yyval = NTableRelation.EMPTY;
		}
break;
case 557:
//#line 2437 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("TO"));
			yyval = NTableRelation.EMPTY;
		}
break;
case 558:
//#line 2444 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 559:
//#line 2449 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = new LinkList();
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 560:
//#line 2455 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少级次声明"));
			yyval = val_peek(2);
		}
break;
case 561:
//#line 2462 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NTableHierarchy((Token)val_peek(0), (TString)val_peek(4), (TInt)val_peek(1));
		}
break;
case 562:
//#line 2466 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NTableHierarchy.EMPTY;
		}
break;
case 563:
//#line 2470 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少最大级次数目"));
			yyval = NTableHierarchy.EMPTY;
		}
break;
case 564:
//#line 2474 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NTableHierarchy.EMPTY;
		}
break;
case 565:
//#line 2478 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("MAXLEVEL"));
			yyval = NTableHierarchy.EMPTY;
		}
break;
case 566:
//#line 2485 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 567:
//#line 2490 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = new LinkList();
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 568:
//#line 2496 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少字段名称"));
			yyval = val_peek(2);
		}
break;
case 569:
//#line 2506 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(4);
			NParamDeclare[] params = l == null ? null :
										l.toArray(new NParamDeclare[l.count()]);
			l = (LinkList)val_peek(1);
			NStatement[] stmts = l.toArray(new NStatement[l.count()]);
			yyval = new NProcedureDeclare((Token)val_peek(8), (Token)val_peek(0), (TString)val_peek(6),
						params, stmts);
		}
break;
case 570:
//#line 2517 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("END"));
			yyval = NStatement.EMPTY;
		}
break;
case 571:
//#line 2522 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少语句"));
			yyval = NStatement.EMPTY;
		}
break;
case 572:
//#line 2527 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("BEGIN"));
			yyval = NStatement.EMPTY;
		}
break;
case 573:
//#line 2531 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NStatement.EMPTY;
		}
break;
case 574:
//#line 2535 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NStatement.EMPTY;
		}
break;
case 575:
//#line 2539 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少名称"));
			yyval = NStatement.EMPTY;
		}
break;
case 576:
//#line 2546 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(1);
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 577:
//#line 2551 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = new LinkList();
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 578:
//#line 2559 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(1); }
break;
case 579:
//#line 2560 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(1); }
break;
case 580:
//#line 2561 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(1); }
break;
case 581:
//#line 2562 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 582:
//#line 2563 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			NTableDeclare t = (NTableDeclare)val_peek(0);
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
				yyval = t;
			} catch(SQLParseException ex) {
				raise(null, false, ex);
				yyval = NStatement.EMPTY;
			}
		}
break;
case 583:
//#line 2598 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(1); }
break;
case 584:
//#line 2599 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(1); }
break;
case 585:
//#line 2600 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 586:
//#line 2601 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 587:
//#line 2602 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 588:
//#line 2603 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 589:
//#line 2604 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(1); }
break;
case 590:
//#line 2605 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(1); }
break;
case 591:
//#line 2606 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(1); }
break;
case 592:
//#line 2607 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = val_peek(0); }
break;
case 593:
//#line 2609 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(";"));
			yyval = val_peek(1);
		}
break;
case 594:
//#line 2613 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(";"));
			yyval = val_peek(1);
		}
break;
case 595:
//#line 2617 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(";"));
			yyval = val_peek(1);
		}
break;
case 596:
//#line 2621 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(";"));
			yyval = val_peek(1);
		}
break;
case 597:
//#line 2625 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(";"));
			yyval = val_peek(1);
		}
break;
case 598:
//#line 2629 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(";"));
			yyval = val_peek(1);
		}
break;
case 599:
//#line 2633 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(";"));
			yyval = val_peek(1);
		}
break;
case 600:
//#line 2637 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(";"));
			yyval = val_peek(1);
		}
break;
case 601:
//#line 2644 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(1);
			yyval = new NSegment((Token)val_peek(2), (Token)val_peek(0), l.toArray(new NStatement[l.count()]));
		}
break;
case 602:
//#line 2649 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(1);
			after(l, new SQLTokenNotFoundException("END"));
			yyval = new NSegment((Token)val_peek(2), l, l.toArray(new NStatement[l.count()]));
		}
break;
case 603:
//#line 2654 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少语句"));
			yyval = NStatement.EMPTY;
		}
break;
case 604:
//#line 2661 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NVarStmt((Token)val_peek(2), (TString)val_peek(1), (NDataType)val_peek(0), null);
		}
break;
case 605:
//#line 2664 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NVarStmt((Token)val_peek(4), (TString)val_peek(3), (NDataType)val_peek(2), (NValueExpr)val_peek(0));
		}
break;
case 606:
//#line 2668 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式"));
			yyval = new NVarStmt((Token)val_peek(4), (TString)val_peek(3), (NDataType)val_peek(2), null);
		}
break;
case 607:
//#line 2672 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少数据类型"));
			yyval = NStatement.EMPTY;
		}
break;
case 608:
//#line 2676 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少变量名称"));
			yyval = NStatement.EMPTY;
		}
break;
case 609:
//#line 2683 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = NDataType.BOOLEAN; }
break;
case 610:
//#line 2684 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = NDataType.BYTE; }
break;
case 611:
//#line 2685 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = NDataType.BYTES; }
break;
case 612:
//#line 2686 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = NDataType.DATE; }
break;
case 613:
//#line 2687 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = NDataType.DOUBLE; }
break;
case 614:
//#line 2688 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = NDataType.ENUM((String)val_peek(1)); }
break;
case 615:
//#line 2689 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = NDataType.FLOAT; }
break;
case 616:
//#line 2690 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = NDataType.GUID; }
break;
case 617:
//#line 2691 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = NDataType.INT; }
break;
case 618:
//#line 2692 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = NDataType.LONG; }
break;
case 619:
//#line 2693 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = NDataType.SHORT; }
break;
case 620:
//#line 2694 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = NDataType.STRING; }
break;
case 621:
//#line 2696 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			at(val_peek(2), new SQLTokenNotFoundException(">"));
			yyval = NDataType.UNKNOWN;
		}
break;
case 622:
//#line 2700 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("无法识别的类名"));
			yyval = NDataType.UNKNOWN;
		}
break;
case 623:
//#line 2704 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("<"));
			yyval = NDataType.UNKNOWN;
		}
break;
case 624:
//#line 2711 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			TString ref = (TString)val_peek(2);
			NValueExpr val = (NValueExpr)val_peek(0);
			yyval = new NAssignStmt(ref, val, new TString[] { ref }, new NValueExpr[] { val });
		}
break;
case 625:
//#line 2716 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(5);
			TString[] refs = l.toArray(new TString[l.count()]);
			l = (LinkList)val_peek(1);
			if (refs.length == l.count()) {
				yyval = new NAssignStmt((Token)val_peek(6), (Token)val_peek(0), refs,
							l.toArray(new NValueExpr[l.count()]));
			} else {
				at(val_peek(3), new SQLNotSupportedException("赋值运算符两端操作数个数不同"));
				yyval = NStatement.EMPTY;
			}
		}
break;
case 626:
//#line 2728 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			TString ref = (TString)val_peek(2);
			yyval = new NAssignStmt(ref, new TString[] { ref }, (NQueryStmt)val_peek(0));
		}
break;
case 627:
//#line 2732 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(3);
			TString[] refs = l.toArray(new TString[l.count()]);
			NQueryStmt q = (NQueryStmt)val_peek(0);
			if (refs.length == q.getMasterSelect().select.columns.length) {
				yyval = new NAssignStmt((Token)val_peek(4), refs, q);
			} else {
				at(val_peek(1), new SQLNotSupportedException("赋值运算符左侧操作数个数与查询输出列个数不同"));
				yyval = NStatement.EMPTY;
			}
		}
break;
case 628:
//#line 2744 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式"));
			yyval = NStatement.EMPTY;
		}
break;
case 629:
//#line 2748 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NStatement.EMPTY;
		}
break;
case 630:
//#line 2752 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值列表"));
			yyval = NStatement.EMPTY;
		}
break;
case 631:
//#line 2756 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值列表或查询"));
			yyval = NStatement.EMPTY;
		}
break;
case 632:
//#line 2760 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NStatement.EMPTY;
		}
break;
case 633:
//#line 2764 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			at(val_peek(2), new SQLTokenNotFoundException("("));
			yyval = NStatement.EMPTY;
		}
break;
case 634:
//#line 2771 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(2);
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 635:
//#line 2776 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = new LinkList();
			l.add(val_peek(0));
			yyval = l;
		}
break;
case 636:
//#line 2782 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少变量名称"));
			yyval = val_peek(2);
		}
break;
case 637:
//#line 2786 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			Object l = val_peek(1);
			after(l, new SQLTokenNotFoundException(","));
			yyval = l;
		}
break;
case 638:
//#line 2794 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NIfStmt((Token)val_peek(3), (NConditionExpr)val_peek(2), (NStatement)val_peek(0), null);
		}
break;
case 639:
//#line 2797 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NIfStmt((Token)val_peek(5), (NConditionExpr)val_peek(4), (NStatement)val_peek(2), (NStatement)val_peek(0));
		}
break;
case 640:
//#line 2801 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少语句"));
			yyval = NStatement.EMPTY;
		}
break;
case 641:
//#line 2805 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少语句"));
			yyval = NStatement.EMPTY;
		}
break;
case 642:
//#line 2809 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("THEN"));
			yyval = NStatement.EMPTY;
		}
break;
case 643:
//#line 2813 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少谓词表达式"));
			yyval = NStatement.EMPTY;
		}
break;
case 644:
//#line 2820 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NWhileStmt((Token)val_peek(3), (NConditionExpr)val_peek(2), (NStatement)val_peek(0));
		}
break;
case 645:
//#line 2824 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少语句"));
			yyval = NStatement.EMPTY;
		}
break;
case 646:
//#line 2828 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("LOOP"));
			yyval = NStatement.EMPTY;
		}
break;
case 647:
//#line 2832 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少谓词表达式"));
			yyval = NStatement.EMPTY;
		}
break;
case 648:
//#line 2839 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = new NLoopStmt((Token)val_peek(1), (NStatement)val_peek(0)); }
break;
case 649:
//#line 2841 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少语句"));
			yyval = NStatement.EMPTY;
		}
break;
case 650:
//#line 2848 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NForeachStmt((Token)val_peek(7), (TString)val_peek(6), (NQueryStmt)val_peek(3), (NStatement)val_peek(0));
		}
break;
case 651:
//#line 2851 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			yyval = new NForeachStmt((Token)val_peek(5), (TString)val_peek(4), (NQueryInvoke)val_peek(2), (NStatement)val_peek(0));
		}
break;
case 652:
//#line 2855 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少语句"));
			yyval = NStatement.EMPTY;
		}
break;
case 653:
//#line 2859 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("LOOP"));
			yyval = NStatement.EMPTY;
		}
break;
case 654:
//#line 2863 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NStatement.EMPTY;
		}
break;
case 655:
//#line 2867 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少查询"));
			yyval = NStatement.EMPTY;
		}
break;
case 656:
//#line 2871 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少查询或查询调用"));
			yyval = NStatement.EMPTY;
		}
break;
case 657:
//#line 2875 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("IN"));
			yyval = NStatement.EMPTY;
		}
break;
case 658:
//#line 2879 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少变量名称"));
			yyval = NStatement.EMPTY;
		}
break;
case 659:
//#line 2886 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = new NBreakStmt((Token)val_peek(0)); }
break;
case 660:
//#line 2890 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = new NPrintStmt((Token)val_peek(1), (NValueExpr)val_peek(0)); }
break;
case 661:
//#line 2892 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少值表达式"));
			yyval = NStatement.EMPTY;
		}
break;
case 662:
//#line 2899 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = new NReturnStmt((Token)val_peek(0), null); }
break;
case 663:
//#line 2900 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{ yyval = new NReturnStmt((Token)val_peek(1), (NValueExpr)val_peek(0)); }
break;
case 664:
//#line 2907 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			LinkList l = (LinkList)val_peek(5);
			NParamDeclare[] params = l == null ? null :
										l.toArray(new NParamDeclare[l.count()]);
			l = (LinkList)val_peek(1);
			NStatement[] stmts = l.toArray(new NStatement[l.count()]);
			yyval = new NFunctionDeclare((Token)val_peek(9), (Token)val_peek(0), (TString)val_peek(7),
						params, (NDataType)val_peek(3), stmts);
		}
break;
case 665:
//#line 2918 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("END"));
			yyval = NStatement.EMPTY;
		}
break;
case 666:
//#line 2923 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少语句"));
			yyval = NStatement.EMPTY;
		}
break;
case 667:
//#line 2928 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("BEGIN"));
			yyval = NStatement.EMPTY;
		}
break;
case 668:
//#line 2932 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少返回值类型"));
			yyval = NStatement.EMPTY;
		}
break;
case 669:
//#line 2936 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException(")"));
			yyval = NStatement.EMPTY;
		}
break;
case 670:
//#line 2940 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLTokenNotFoundException("("));
			yyval = NStatement.EMPTY;
		}
break;
case 671:
//#line 2944 "D:\Workspace\D&A\dnasql\meta\dnasql.y"
{
			after(val_peek(1), new SQLSyntaxException("缺少名称"));
			yyval = NStatement.EMPTY;
		}
break;
//#line 7145 "SQLParser.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
    }//switch
    } catch(Throwable ex) { yyexception(ex); }
    //#### Now let's reduce... ####
    if (yydebug) debug("reduce");
    state_drop(yym);             //we just reduced yylen states
    yystate = state_peek(0);     //get new state
    val_drop(yym);               //corresponding value drop
    yym = yylhs[yyn];            //select next TERMINAL(on lhs)
    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
      {
      if (yydebug) debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
      yystate = YYFINAL;         //explicitly say we're done
      state_push(YYFINAL);       //and save it
      val_push(yyval);           //also save the semantic value of parsing
      if (yychar < 0)            //we want another character?
        {
        yychar = yylex();        //get next character
        if (yychar<0) yychar=0;  //clean, if necessary
        if (yydebug)
          yylexdebug(yystate,yychar);
        }
      if (yychar == 0)          //Good exit (if lex returns 0 ;-)
         break;                 //quit the loop--all DONE
      }//if yystate
    else                        //else not done yet
      {                         //get next state and push, for next yydefred[]
      yyn = yygindex[yym];      //find out where to go
      if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
        yystate = yytable[yyn]; //get new state
      else
        yystate = yydgoto[yym]; //else go to new defred
      if (yydebug) debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
      state_push(yystate);     //going again, so push state & val...
      val_push(yyval);         //for next action
      }
    }//main loop
  return 0;//yyaccept!!
}
//## end of method parse() ######################################



//## run() --- for Thread #######################################
//## The -Jnorun option was used ##
//## end of method run() ########################################



//## Constructors ###############################################
//## The -Jnoconstruct option was used ##
//###############################################################



}
//################### END OF CLASS ##############################
