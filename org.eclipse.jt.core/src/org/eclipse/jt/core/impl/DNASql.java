package org.eclipse.jt.core.impl;

import java.io.IOException;
import java.io.Reader;

import org.eclipse.jt.core.def.DNASqlType;
import org.eclipse.jt.core.def.MetaElement;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.spi.sql.SQLOutput;
import org.eclipse.jt.core.spi.sql.SQLParseException;


/**
 * D&ASql功能外观类
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
				this.sb.append("D&ASql出现语法错误：\r\n");
			}
			this.sb.append('行').append(line + 1).append('列').append(col + 1)
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
			throw new UnsupportedOperationException("声明器必须由框架构造");
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
	 * 解析D&ASql生成Statement出错则抛出异常
	 * 
	 * @param dnaSql
	 * @param oQuerier
	 * @param defineClass
	 *            限定类型，为空则不限制类型
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
				throw new IllegalArgumentException("D&A Sql 所定义的类型与要求不符：["
						+ defineClass.getName() + "]类型");
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
	 * 分析SQL生成NStatement结构，用于代码生成
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
