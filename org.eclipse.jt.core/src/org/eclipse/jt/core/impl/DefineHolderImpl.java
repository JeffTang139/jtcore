package org.eclipse.jt.core.impl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import org.eclipse.jt.core.Filter;
import org.eclipse.jt.core.cb.DefineHolder;
import org.eclipse.jt.core.cb.DefineProvider;
import org.eclipse.jt.core.def.MetaElementType;
import org.eclipse.jt.core.def.MissingDefineException;
import org.eclipse.jt.core.def.NamedDefine;
import org.eclipse.jt.core.def.table.TableDefine;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.misc.ExceptionCatcher;
import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.misc.SXElementBuilder;
import org.eclipse.jt.core.misc.SXMergeHelper;
import org.eclipse.jt.core.type.GUID;


/**
 * 元数据定义容器(环境)
 * 
 * @author Jeff Tang
 * 
 */
class DefineHolderImpl extends ObjectQuerierImpl implements DefineHolder {

	public final void putDefine(MetaElementType type, InputStream input,
			String packageName, String filename, DefineProvider provider) {
		try {
			InputStreamReader isr = new InputStreamReader(input);
			try {
				this.putDefine(type, isr, packageName, filename, provider);
			} finally {
				isr.close();
			}
		} catch (IOException e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final void putDefine(MetaElementType type, File file,
			String pkgname, String filename, DefineProvider provider) {
		try {
			FileReader fr = new FileReader(file);
			try {
				this.putDefine(type, fr, pkgname, filename, provider);
			} finally {
				fr.close();
			}
		} catch (IOException e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final void putDefine(MetaElementType type, Reader reader,
			String pkgname, String clzname, DefineProvider provider) {
		if (type == null) {
			throw new NullArgumentException("元数据类型");
		}
		this.provider = Utils.coalesce(provider, this.provider);
		switch (type) {
		case QUERY:
		case ORM:
		case INSERT:
		case DELETE:
		case UPDATE:
			NDmlDeclare statement = DNASql.parseNStatement(reader,
					NDmlDeclare.class, this);
			this.put(type, statement.name.value, statement, pkgname, clzname);
			break;
		case TABLE:
			NAbstractTableDeclare raw = DNASql.parseNStatement(reader,
					NAbstractTableDeclare.class, this);
			if (raw instanceof NTableDeclare) {
				NTableDeclare nTable = (NTableDeclare) raw;
				TableDeclareStub stub = SQLTableVisitor.build(this, nTable);
				this.put(type, raw.name.value, stub, pkgname, clzname);
			} else {
				this.put(type, raw.name.value, raw, pkgname, clzname);
			}
			break;
		case INFO:
			throw new UnsupportedOperationException();
		case MODEL:
			throw new UnsupportedOperationException();
		case STORED_PROC:
			throw new UnsupportedOperationException();
		}
	}

	public final void putTableDefine(InputStream in, String pkgname,
			String filename, DefineProvider provider) {
		try {
			SXElement element = this.toXml.build(in).firstChild(
					TableDefineImpl.xml_name);
			this.putTableDefine(element, pkgname, filename, provider);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final void putTableDefine(SXElement element, String pkgname,
			String filename, DefineProvider provider) {
		if (element == null) {
			throw new NullArgumentException("XML对象");
		}
		this.provider = Utils.coalesce(provider, this.provider);
		String tn = element.getAttribute(NamedDefineImpl.xml_attr_name);
		TableDefineImpl table = new TableDefineImpl(tn, null);
		table.mergeDelayRelation(element, this.helper);
		this.put(MetaElementType.TABLE, tn, table, pkgname, filename);
	}

	public final void loadDefine(MetaElementType type, Connection connection,
			DefineProvider provider, ExceptionCatcher catcher) {
		switch (type) {
		case TABLE:
			break;
		default:
			throw new UnsupportedOperationException("不支持的类型.");
		}
		if (connection == null) {
			throw new NullArgumentException("数据库连接");
		}
		this.provider = Utils.coalesce(provider, this.provider);
		try {
			PreparedStatement ps = connection.prepareStatement(SELECT_METADATA);
			try {
				ps.setString(1, type.name());
				ResultSet rs = ps.executeQuery();
				try {
					while (rs.next()) {
						final GUID id = GUID.valueOf(rs.getBytes(1));
						final String name = rs.getString(2);
						if (type == MetaElementType.TABLE) {
							SXElement element = this.toXml.build(
									rs.getCharacterStream(3)).firstChild(
									TableDefineImpl.xml_name);
							TableDefineImpl table = new TableDefineImpl(name,
									null);
							table.id = id;
							try {
								table.mergeDelayRelation(element, this.helper);
								this.put(MetaElementType.TABLE, name, table,
										null, null);
							} catch (Throwable e) {
								catcher.catchException(e, table);
								continue;
							}
						} else {
							throw new UnsupportedOperationException();
						}
					}
				} finally {
					rs.close();
				}
			} finally {
				ps.close();
			}
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final void removeDefine(MetaElementType type, String name) {
		if (type == null || name == null || name.length() == 0) {
			throw new NullPointerException();
		}
		this.remove(type, name);
	}

	public final void clearDefines() {
		final TwoKeyEntry[] table = this.table;
		if (table == null) {
			return;
		}
		for (int i = 0; i < table.length; i++) {
			table[i] = null;
		}
		this.size = 0;
	}

	public final TableDefineImpl findTableDefine(String name,
			ExceptionCatcher catcher) {
		if (name == null || name.length() == 0) {
			throw new NullArgumentException("名称");
		}
		TwoKeyEntry e = this.get(MetaElementType.TABLE, name);
		if (e == null) {
			return null;
		} else {
			try {
				this.resovleTable(e);
			} catch (Throwable th) {
				catcher.catchException(th, e.define);
			}
			// 异常不抛出,返回未建立关系引用的表定义
			return getTableOnly(e);
		}
	}

	public final TableDefineImpl getTableDefine(String name,
			ExceptionCatcher catcher) {
		TableDefineImpl table = this.findTableDefine(name, catcher);
		if (table == null) {
			throw new MissingDefineException();
		}
		return table;
	}

	public final void fillDefines(Map<String, NamedDefine> map,
			MetaElementType type, ExceptionCatcher catcher,
			Filter<String> filter) {
		final TwoKeyEntry[] table = this.table;
		if (table == null || type != MetaElementType.TABLE) {
			return;
		}
		for (int i = 0; i < table.length; i++) {
			TwoKeyEntry e = table[i];
			while (e != null) {
				if (e.type == type) {
					try {
						this.resovleTable(e);
					} catch (Throwable th) {
						catcher.catchException(th, e.define);
					}
					if (e.define instanceof TableDefineImpl) {
						NamedDefine define = (NamedDefine) e.define;
						if (filter == null || filter.accept(define.getName())) {
							map.put(e.name, define);
						}
					}
				}
				e = e.next;
			}
		}
	}

	static final boolean notNull(String s) {
		return s != null && s.length() > 0;
	}

	final class TwoKeyEntry {

		final MetaElementType type;

		final String name;

		final int hash;

		Object define;

		String pkgname;

		String clzname;

		TwoKeyEntry next;

		private TwoKeyEntry(MetaElementType type, String name, int hash) {
			this.hash = DefineHolderImpl.hashCode(type, name);
			this.type = type;
			this.name = name;
		}

		private boolean equals(MetaElementType type, String name) {
			return this.type == type
					&& (this.name == name || this.name.equals(name));
		}

	}

	private TwoKeyEntry[] table;

	private int size;

	private static final String SELECT_METADATA = "select recid, name, xml from core_metadata where kind = ?";

	private static final int hashCode(MetaElementType type, String name) {
		return name.hashCode() ^ type.hashCode();
	}

	private static final int indexFor(int hash, int length) {
		return hash & length - 1;
	}

	/**
	 * 向hash表中增加元素
	 * 
	 * @param type
	 * @param name
	 * @param define
	 * @param pkgname
	 * @param clzname
	 */
	final void put(MetaElementType type, String name, Object define,
			String pkgname, String clzname) {
		final int hash = hashCode(type, name);
		TwoKeyEntry[] table = this.table;
		if (table == null) {
			table = new TwoKeyEntry[8];
			TwoKeyEntry e = new TwoKeyEntry(type, name, hash);
			e.define = define;
			e.pkgname = pkgname;
			e.clzname = clzname;
			table[indexFor(hash, 8)] = e;
			this.table = table;
			this.size = 1;
		}
		final int index = indexFor(hash, table.length);
		for (TwoKeyEntry e = table[index]; e != null; e = e.next) {
			if (e.hash == hash && e.equals(type, name)) {
				e.define = define;
				e.pkgname = pkgname;
				e.clzname = clzname;
			}
		}
		TwoKeyEntry ne = new TwoKeyEntry(type, name, hash);
		ne.define = define;
		ne.pkgname = pkgname;
		ne.clzname = clzname;
		ne.next = table[index];
		table[index] = ne;
		if (this.size++ >= table.length * 0.75f) {
			final int newlength = table.length * 2;
			final TwoKeyEntry[] newTable = new TwoKeyEntry[newlength];
			for (TwoKeyEntry e : table) {
				while (e != null) {
					final TwoKeyEntry next = e.next;
					final int h = hashCode(e.type, e.name);
					final int newIndex = indexFor(h, newlength);
					e.next = newTable[newIndex];
					newTable[newIndex] = e;
					e = next;
				}
			}
			this.table = newTable;
		}
	}

	/**
	 * 直接从hash表中取
	 * 
	 * @param type
	 * @param name
	 * @return
	 */
	final TwoKeyEntry get(MetaElementType type, String name) {
		final TwoKeyEntry[] table = this.table;
		if (table == null) {
			return null;
		}
		final int hash = hashCode(type, name);
		for (TwoKeyEntry e = table[indexFor(hash, table.length)]; e != null; e = e.next) {
			if (e.hash == hash && e.equals(type, name)) {
				return e;
			}
		}
		return null;
	}

	final void remove(MetaElementType type, String name) {
		final TwoKeyEntry[] table = this.table;
		if (table == null) {
			return;
		}
		final int hash = hashCode(type, name);
		final int index = indexFor(hash, table.length);
		for (TwoKeyEntry e = table[index], prev = null; e != null; prev = e, e = e.next) {
			if (e.hash == hash && e.equals(type, name)) {
				if (prev == null) {
					table[index] = e.next;
				} else {
					prev.next = e.next;
				}
				e.next = null;
			}
		}
	}

	final SXMergeHelper helper = new SXMergeHelper(this);

	final SXElementBuilder toXml;

	DefineProvider provider;

	DefineHolderImpl() {
		try {
			this.toXml = new SXElementBuilder();
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	/**
	 * 实现ObjectQuerier接口
	 * 
	 * facade必须为以下类型之一:
	 * <ul>
	 * <li>TableDefine.class
	 * <li>NInsertDeclare.class
	 * <li>NDeleteDeclare.class
	 * <li>NUpdateDeclare.class
	 * <li>NQueryDeclare.class
	 * <li>NOrmDeclare.class
	 * </ul>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public final <TFacade> TFacade find(Class<TFacade> facade, Object key)
			throws UnsupportedOperationException {
		if (key == null) {
			throw new NullArgumentException("元数据定义名称");
		}
		if (!(key instanceof String)) {
			throw new IllegalArgumentException("不支持的键类型.");
		}
		String k = (String) key;
		MetaElementType type = metaTypeOf(facade);
		if (type == null) {
			throw unsupported(facade);
		}
		TwoKeyEntry entry = this.get(type, k);
		if (entry == null && this.provider != null) {
			this.provider.demand(this, type, k);
			entry = this.get(type, k);
		}
		if (entry == null) {
			return null;
		} else {
			switch (type) {
			case TABLE:
				this.resovleTable(entry);
				return (TFacade) entry.define;
			default:
				return null;
			}
		}
	}

	private void resovleTable(TwoKeyEntry e) {
		if (e.define instanceof TableDeclareStub) {
			TableDeclareStub nTable = (TableDeclareStub) e.define;
			nTable.fillRelations(this);
			e.define = nTable.getTable();
		} else if (e.define instanceof NAbstractTableDeclare) {
		} else {
			TableDefineImpl table = (TableDefineImpl) e.define;
			this.helper.resolveDelayAction(table, null);
		}
	}

	private static final UnsupportedOperationException unsupported(Class<?> clz) {
		return new UnsupportedOperationException("不支持的元数据类型[" + clz.getName()
				+ "].");
	}

	static final MetaElementType metaTypeOf(Class<?> clz) {
		if (clz.isAssignableFrom(TableDefine.class)) {
			return MetaElementType.TABLE;
		} else if (clz.isAssignableFrom(NInsertDeclare.class)) {
			return MetaElementType.INSERT;
		} else if (clz.isAssignableFrom(NDeleteDeclare.class)) {
			return MetaElementType.DELETE;
		} else if (clz.isAssignableFrom(NUpdateDeclare.class)) {
			return MetaElementType.UPDATE;
		} else if (clz.isAssignableFrom(NQueryDeclare.class)) {
			return MetaElementType.QUERY;
		} else if (clz.isAssignableFrom(NOrmDeclare.class)) {
			return MetaElementType.ORM;
		}
		return null;
	}

	private static final TableDefineImpl getTableOnly(TwoKeyEntry e) {
		return e.define instanceof TableDefineImpl ? (TableDefineImpl) e.define
				: null;
	}

}
