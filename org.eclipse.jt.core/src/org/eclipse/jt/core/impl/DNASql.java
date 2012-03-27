package org.eclipse.jt.core.impl;

import java.io.IOException;
import java.io.Reader;

import org.eclipse.jt.core.def.DNASqlType;
import org.eclipse.jt.core.def.MetaElement;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.spi.sql.SQLOutput;
import org.eclipse.jt.core.spi.sql.SQLParseException;


/**
 * D&ASql���������
 * 
 * @author Jeff Tang
 * 
 */
public final class DNASql {
	private static class RaisableSQLOutput implements SQLOutput {
		private StringBuilder sb;

		private void append(String msg, int line, int col) {
			if (this.sb == null) {
				this.sb = new StringBuilder();
				this.sb.append("D&ASql�����﷨����\r\n");
			}
			this.sb.append('��').append(line + 1).append('��').append(col + 1)
					.append(':').append(' ').append(msg).append('\r').append(
							'\r').append('\n');
		}

		public void raise(SQLParseException ex) {
			this.append(ex.getMessage(), ex.line, ex.col);
		}

		public void tryRaise() {
			if (this.sb != null) {
				throw new IllegalArgumentException(this.sb.toString());
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static MetaElement parseForDeclarator(DeclaratorBase declarator) {
		Class declaratorClass = declarator.getClass();
		final DNASqlType dt = DNASqlType
				.declareScriptSupportedTypeOfDeclaratorClass(declaratorClass);
		final ContextImpl<?, ?, ?> context = DeclaratorBase.newInstanceByCore;
		if (context == null) {
			throw new UnsupportedOperationException("�����������ɿ�ܹ���");
		}
		DeclaratorBase.newInstanceByCore = null;
		final String className = declaratorClass.getName();
		final Reader reader = context.occorAt.openDeclareScriptReader(className
				.substring(className.lastIndexOf('.') + 1, className.length()),
				dt);
		return (MetaElement) parseDefine(reader, context, dt.declareBaseClass,
				declarator);
	}

	/**
	 * ����D&ASql����Statement�������׳��쳣
	 * 
	 * @param dnaSql
	 * @param oQuerier
	 * @param defineClass
	 *            �޶����ͣ�Ϊ������������
	 * @return
	 */
	public static <TDefine> TDefine parseDefine(Reader dnaSql,
			ContextImpl<?, ?, ?> context, Class<TDefine> defineClass) {
		return parseDefine(dnaSql, context, defineClass, null);
	}

	@SuppressWarnings("unchecked")
	public static <TDefine> TDefine parseDefine(Reader dnaSql,
			ContextImpl<?, ?, ?> context, Class<TDefine> defineClass,
			DeclaratorBase declarator) {
		if (dnaSql == null) {
			throw new NullArgumentException("dnaSql");
		}
		try {
			if (context == null) {
				throw new NullArgumentException("context");
			}
			RaisableSQLOutput out = new RaisableSQLOutput();
			SQLScript s = new SQLParser().parse(new SQLLexer(dnaSql), out,
					null, context);
			out.tryRaise();
			Object define = s.prepare(context, declarator);
			out.tryRaise();
			if (define == null) {
				throw new IllegalStateException();
			}
			if (defineClass != null && !defineClass.isInstance(define)) {
				throw new IllegalArgumentException("D&A Sql �������������Ҫ�󲻷���["
						+ defineClass.getName() + "]����");
			}
			return (TDefine) define;
		} finally {
			try {
				dnaSql.close();
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}
	}

	/**
	 * ����SQL����NStatement�ṹ�����ڴ�������
	 * 
	 * @param <TStatement>
	 * @param dnaSql
	 * @param statementClass
	 * @param holder
	 * @return
	 */
	public static <TStatement extends NStatement> TStatement parseNStatement(
			Reader dnaSql, Class<TStatement> statementClass,
			DefineHolderImpl holder) {
		if (dnaSql == null) {
			throw new NullArgumentException("dnaSql");
		}
		try {
			RaisableSQLOutput out = new RaisableSQLOutput();
			SQLScript s = new SQLParser().parse(new SQLLexer(dnaSql), out,
					holder, null);
			out.tryRaise();
			return s.content(statementClass);
		} finally {
			try {
				dnaSql.close();
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}
	}
}
