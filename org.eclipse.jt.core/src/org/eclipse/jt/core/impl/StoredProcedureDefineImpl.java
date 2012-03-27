package org.eclipse.jt.core.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.eclipse.jt.core.def.MetaElementType;
import org.eclipse.jt.core.def.query.StoredProcedureDeclarator;
import org.eclipse.jt.core.def.query.StoredProcedureDeclare;


public final class StoredProcedureDefineImpl extends ModifyStatementImpl
		implements StoredProcedureDeclare,
		Declarative<StoredProcedureDeclarator> {

	public final StoredProcedureDeclarator getDeclarator() {
		return this.declarator;
	}

	public final MetaElementType getMetaElementType() {
		return MetaElementType.STORED_PROC;
	}

	@Override
	public final String getXMLTagName() {
		return xml_element_stored_proc;
	}

	static final String xml_element_stored_proc = "stored_proc";
	final StoredProcedureDeclarator declarator;

	@SuppressWarnings("deprecation")
	public StoredProcedureDefineImpl(String name,
			StoredProcedureDeclarator declarator) {
		super(name);
		this.declarator = declarator;
	}

	final void loadDdl(DBLang lang) {
		final String postfix = lang.getPostfix();
		if (postfix == null) {
			throw new IllegalArgumentException();
		}
		final int postfixL = postfix.length();
		if (postfixL == 0) {
			throw new IllegalArgumentException();
		}
		final Class<?> clz = this.declarator.getClass();
		final String className = clz.getName();
		final int classNameL = className.length();
		final char[] resourceName = new char[classNameL + postfixL + 1];
		className.getChars(0, classNameL, resourceName, 0);
		for (int i = 0; i < classNameL; i++) {
			if (resourceName[i] == '.') {
				resourceName[i] = '/';
			}
		}
		resourceName[classNameL] = '.';
		postfix.getChars(0, postfixL, resourceName, classNameL + 1);
		final ClassLoader cl = clz.getClassLoader();
		final URL url = cl.getResource(Utils.fastString(resourceName));
		if (url == null) {
			throw new IllegalArgumentException("找不到存储过程脚本");
		}
		try {
			final InputStream is = url.openStream();
			try {
				final InputStreamReader isr = new InputStreamReader(is, "UTF8");
				try {
					char[] str = new char[500];
					int strl = 0;
					for (int start = 0;;) {
						int l = str.length;
						int r = isr.read(str, start, l - start);
						if (r >= 0) {
							strl += r;
							if (strl == l) {
								char[] newstr = new char[l * 2];
								System.arraycopy(str, 0, newstr, 0, l);
								str = newstr;
							}
							start = strl;
						} else {
							break;
						}
					}
					this.ddl = (new String(str, 0, strl)).replaceAll("\r", "");
				} finally {
					isr.close();
				}
			} finally {
				is.close();
			}
		} catch (IOException e) {
			throw new UnsupportedOperationException("打开名为[" + className
					+ "]的存储过程脚本时出错", e);
		}
	}

	private String ddl;

	final String getProcedureDDL() {
		return this.ddl;
	}

	@Override
	public Sql getSql(DBAdapterImpl dbAdapter) {
		this.ensurePrepared(dbAdapter.getContext(), false);
		ProcedureCallSql sql = this.sql;
		if (sql == null) {
			synchronized (this) {
				sql = this.sql;
				if (sql == null) {
					this.sql = sql = new ProcedureCallSql(dbAdapter.lang, this);
				}
			}
		}
		return sql;
	}

	private ProcedureCallSql sql;

	@Override
	protected void doPrepare(DBLang lang) throws Throwable {
		super.doPrepare(lang);
		this.sql = null;
		this.loadDdl(lang);
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		throw new UnsupportedOperationException();
	}
}
