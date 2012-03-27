package org.eclipse.jt.core.impl;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Formatter;

/**
 * ����׷����
 */
public final class CodeBuilder extends ExprVisitor<Object> implements
		Closeable, Flushable {

	private final Appendable out;
	private final Formatter formatter;
	private int indent = 0;
	private boolean needIndent;

	/**
	 * ���캯��
	 * 
	 * @param out
	 *            ����ӿ�
	 */
	public CodeBuilder(Appendable out) {
		if (out == null) {
			throw new NullPointerException();
		}
		this.out = out;
		this.formatter = new Formatter(out);
	}

	/**
	 * ���캯��
	 * 
	 * @param out
	 *            �����
	 */
	public CodeBuilder(OutputStream out) {

		this(new OutputStreamWriter(out));
	}

	/**
	 * ���캯��
	 * 
	 * @param outFile
	 *            ����ļ�
	 * @throws FileNotFoundException
	 */
	public CodeBuilder(File outFile) throws FileNotFoundException {
		this(new FileOutputStream(outFile));

	}

	/**
	 * ���캯��
	 * 
	 * @param outFileName
	 *            ����ļ���
	 * @throws FileNotFoundException
	 */
	public CodeBuilder(String outFileName) throws FileNotFoundException {
		this(new File(outFileName));
	}

	/**
	 * �ر������
	 * 
	 * @throws IOException
	 */
	public final void flush() throws IOException {
		if (this.out instanceof Flushable) {
			((Flushable) this.out).flush();
		}
	}

	public final void close() throws IOException {
		if (this.out instanceof Closeable) {
			((Closeable) this.out).close();
		}
	}

	/**
	 * ����һ������
	 * 
	 * @return ���Ӻ����������
	 */
	public final CodeBuilder pi() {
		this.indent++;
		return this;
	}

	/**
	 * ����һ������
	 * 
	 * @return ���ٺ����������
	 */
	public final CodeBuilder ri() {
		this.indent--;
		return this;
	}

	/**
	 * ׷������
	 * 
	 * @throws IOException
	 */
	private final void doIndent() throws IOException {
		if (this.needIndent) {
			for (int i = 0; i < this.indent; i++) {
				this.out.append('\t');
			}
			this.needIndent = false;
		}
	}

	/**
	 * ׷�Ӵ�Ҫ�滻ֵ��һ�д���
	 * 
	 * @param linefmt
	 *            Ҫ׷�ӵ�һ�д���
	 * @param args
	 *            �滻��ֵ
	 * @return
	 * @throws IOException
	 */
	public final CodeBuilder appendLine(String linefmt, Object... args)
			throws IOException {
		this.doIndent();
		this.formatter.format(linefmt, args);
		this.appendLine();
		return this;
	}

	/**
	 * ׷��һ�й̶�����
	 */
	public final CodeBuilder appendLine(String line) throws IOException {
		this.doIndent();
		this.out.append(line);
		this.appendLine();
		return this;
	}

	/**
	 * ׷��ֻ��һ���ַ���һ�д���
	 */
	public final CodeBuilder appendLine(char c) throws IOException {
		this.doIndent();
		this.out.append(c);
		this.appendLine();
		return this;

	}

	/**
	 * ׷�Ӻ��滻ֵ�Ĵ���
	 * 
	 * @param fmt
	 *            ׷�ӵĴ���
	 * @param args
	 *            �滻ֵ
	 * @return
	 * @throws IOException
	 */
	public final CodeBuilder append(String fmt, Object... args)
			throws IOException {
		this.doIndent();
		this.formatter.format(fmt, args);
		return this;
	}

	/**
	 * ׷�Ӵ���
	 */
	public final CodeBuilder append(String str) throws IOException {
		this.doIndent();
		this.out.append(str);
		return this;
	}

	/**
	 * ׷���ַ�
	 */
	public final CodeBuilder append(char c) throws IOException {
		this.doIndent();
		this.out.append(c);
		return this;
	}

	/**
	 * ׷��һ���س�����
	 */
	public final CodeBuilder appendLine() throws IOException {
		this.out.append("\r\n");
		this.needIndent = true;
		return this;
	}

	final void importClass(String packageName, String classSimpleName)
			throws IOException {
		if (classSimpleName == null || classSimpleName.length() == 0) {
			throw new NullPointerException();
		}
		if (packageName == null || packageName.length() == 0) {
			this.append("import ").append(classSimpleName).appendLine(';');
		} else {
			this.append("import ").append(packageName).append('.')
					.append(classSimpleName).appendLine(';');
		}

	}

	final void importClass(Class<?> clz) throws IOException {
		this.append("import ").append(clz.getName()).appendLine(';');
	}

	private final void buildCommonConstExpr(ConstExpr expr) {
		try {
			this.append("ConstExpression.builder.expOf(");
			this.append(expr.getString());
			this.append(')');
		} catch (Throwable t) {
			throw Utils.tryThrowException(t);
		}
	}

	private void adjustNotForConditionExpr(ConditionalExpr condition)
			throws IOException {
		if (condition.not) {
			this.append(".not()");
		}
	}

	public void visitArgumentRefExpr(ArgumentRefExpr expr, Object context) {
		throw new UnsupportedOperationException();
	}

	public void visitBooleanExpr(BooleanConstExpr value, Object context) {
		this.buildCommonConstExpr(value);
	}

	public void visitByteExpr(ByteConstExpr value, Object context) {
		this.buildCommonConstExpr(value);
	}

	public void visitBytesExpr(BytesConstExpr value, Object context) {
		try {
			this.append("ConstExpression.builder.expOf(new byte[]{");
			for (int i = 0; i < value.getBytes().length; i++) {
				if (i > 0) {
					this.append(", ");
				}
				this.append(Byte.toString(value.getBytes()[i]));
			}
			this.append("})");
		} catch (Throwable t) {
			throw Utils.tryThrowException(t);
		}
	}

	public void visitCombinedExpr(CombinedExpr expr, Object context) {
		try {
			expr.conditions[0].visit(this, context);
			this.appendLine();
			this.pi();
			this.append(".%s(", expr.and ? "and" : "or");
			for (int i = 1; i < expr.conditions.length; i++) {
				expr.conditions[i].visit(this, context);
				if (i < expr.conditions.length - 1) {
					this.append(',');
				}
				this.appendLine();
			}
			this.ri();
			this.append(")");
			this.adjustNotForConditionExpr(expr);
		} catch (Throwable t) {
			throw Utils.tryThrowException(t);
		}

	}

	public void visitDateExpr(DateConstExpr value, Object context) {
		try {
			this.append("ConstExpression.builder.expOf(new Date(");
			this.append(Long.toString(value.getDate()));
			this.append("L))");
		} catch (Throwable t) {
			throw Utils.tryThrowException(t);
		}
	}

	public void visitDoubleExpr(DoubleConstExpr value, Object context) {
		this.buildCommonConstExpr(value);
	}

	public void visitFloatExpr(FloatConstExpr value, Object context) {
		this.buildCommonConstExpr(value);
	}

	public void visitGUIDExor(GUIDConstExpr value, Object context) {
		try {
			this.append("ConstExpression.builder.expOf(GUID.valueOf(\"");
			this.append(value.getString());
			this.append("\"))");
		} catch (Throwable t) {
			throw Utils.tryThrowException(t);
		}
	}

	public void visitHierarchyOperateExpr(HierarchyOperateExpr expr,
			Object context) {
		throw new UnsupportedOperationException();
	}

	public void visitHierarchyPredicateExpr(HierarchyPredicateExpr expr,
			Object context) {
		throw new UnsupportedOperationException();
	}

	public void visitIntExpr(IntConstExpr value, Object context) {
		this.buildCommonConstExpr(value);
	}

	public void visitLongExpr(LongConstExpr value, Object context) {
		this.buildCommonConstExpr(value);
	}

	public void visitNullExpr(NullExpr expr, Object context) {
		try {
			this.append("ValueExpression.NULL");
		} catch (Throwable t) {
			throw Utils.tryThrowException(t);
		}
	}

	public void visitOperateExpr(OperateExpr expr, Object context) {
		try {
			// TODO
			expr.operator.buildCode(this, expr);
		} catch (Throwable t) {
			throw Utils.tryThrowException(t);
		}
	}

	public void visitPredicateExpr(PredicateExpr expr, Object context) {
		try {
			// TODO
			expr.predicate.buildCode(this, expr);
			this.adjustNotForConditionExpr(expr);
		} catch (Throwable t) {
			throw Utils.tryThrowException(t);
		}
	}

	public void visitSearchedCase(SearchedCaseExpr expr, Object context) {
		throw new UnsupportedOperationException();
	}

	public void visitSelectColumnRef(SelectColumnRefImpl expr, Object context) {
		throw new UnsupportedOperationException();
	}

	public void visitShortExpr(ShortConstExpr value, Object context) {
		throw new UnsupportedOperationException();
	}

	public void visitStringExpr(StringConstExpr value, Object context) {
		throw new UnsupportedOperationException();
	}

	public void visitSubQueryExpr(SubQueryExpr expr, Object context) {
		throw new UnsupportedOperationException();
	}

	public void visitTableFieldRef(TableFieldRefImpl fieldRef, Object context) {
		// ��������ֻ���ڱ��ϵ�Ĵ�������,��tableRefֻ������TableSelfRef���ͻ�TableRelationDefineImpl����!
		try {
			if (fieldRef.tableRef instanceof TableSelfRef) {
				this.append("%s.expOf(this.%s)",
						DeclaratorBuilderImpl.THIS_TABLE,
						DeclaratorBuilderImpl.declareNameOf(fieldRef.field));
			} else {
				TableRelationDefineImpl rel = (TableRelationDefineImpl) fieldRef.tableRef;
				this.append(
						"this.%s.expOf(%s)",
						DeclaratorBuilderImpl.declareNameOf(rel),
						DeclaratorBuilderImpl.declareNameOf(rel.target)
								+ "."
								+ DeclaratorBuilderImpl
										.declareNameOf(fieldRef.field));
			}
		} catch (Throwable t) {
			throw Utils.tryThrowException(t);
		}
	}

	public void visitQueryColumnRef(QueryColumnRefExpr expr, Object context) {
	}

}
