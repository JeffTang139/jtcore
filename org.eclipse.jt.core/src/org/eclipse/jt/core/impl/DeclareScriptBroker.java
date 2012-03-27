package org.eclipse.jt.core.impl;

import java.net.URL;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.None;
import org.eclipse.jt.core.def.DNASqlType;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.service.Publish;


/**
 * 声明脚本提供器
 * 
 * @author Jeff Tang
 * 
 */
public class DeclareScriptBroker extends
		ServiceInvokeeBase<URL, Context, String, DNASqlType, None> {
	private final Space space;

	private static class Entry {
		final int hash;
		final String name;
		final DNASqlType type;
		URL url;
		Entry next;

		Entry(int hash, String name, DNASqlType type, URL url, Entry next) {
			this.hash = hash;
			this.name = name;
			this.type = type;
			this.url = url;
			this.next = next;
		}
	}

	private Entry[] table;
	private int size;

	DeclareScriptBroker(Space space, String declareName, DNASqlType type,
			URL url) {
		if (space == null) {
			throw new NullArgumentException("space");
		}
		this.space = space;
		this.table = new Entry[4];
		this.publishMode = Publish.Mode.PROTECTED;
		this.putDeclareScriptURL(declareName, type, url);
	}

	@Override
	final Space getSpace() {
		return this.space;
	}

	@Override
	final ServiceBase<?> getService() {
		throw new UnsupportedOperationException();
	}

	final synchronized URL findDeclareScriptURL(String declareName,
			DNASqlType type) {
		if (declareName == null || declareName.length() == 0) {
			throw new NullArgumentException("declareName");
		}
		if (type == null) {
			throw new NullArgumentException("type");
		}
		final Entry[] table = this.table;
		final int hash = declareName.hashCode() ^ type.hashCode();
		for (Entry e = table[hash & (table.length - 1)]; e != null; e = e.next) {
			if (e.hash == hash && e.type == type && e.name.equals(declareName)) {
				return e.url;
			}
		}
		return null;
	}

	final synchronized URL putDeclareScriptURL(String declareName,
			DNASqlType type, URL url) {
		if (declareName == null || declareName.length() == 0) {
			throw new NullArgumentException("declareName");
		}
		if (type == null) {
			throw new NullArgumentException("type");
		}
		final int hash = declareName.hashCode() ^ type.hashCode();
		Entry[] table = this.table;
		final int length = table.length;
		int index = hash & (length - 1);
		for (Entry e = table[index], last = null; e != null; last = e, e = e.next) {
			if (e.hash == hash && e.type == type && e.name.equals(declareName)) {
				URL old = e.url;
				if (url == null) {// 删除操作
					if (last == null) {
						table[index] = e.next;
					} else {
						last.next = e.next;
					}
					e.next = null;
				} else {
					e.url = url;
				}
				return old;
			}
		}
		if (url == null) {
			return null;
		}
		if (++this.size > length * 0.75) {
			final int newLen = length * 2;
			final int newH = newLen - 1;
			final Entry[] newTable = new Entry[newLen];
			for (int j = 0; j < length; j++) {
				for (Entry e = table[j], next; e != null; e = next) {
					final int i = e.hash & newH;
					next = e.next;
					e.next = newTable[i];
					newTable[i] = e;
				}
			}
			this.table = table = newTable;
			index = hash & (newLen - 1);
		}
		table[index] = new Entry(hash, declareName, type, url, table[index]);
		return null;
	}

	@Override
	final URL provide(Context context, String declareName, DNASqlType type)
			throws Throwable {
		return this.findDeclareScriptURL(declareName, type);
	}

	@Override
	final Class<?> getTargetClass() {
		return URL.class;
	}

	@Override
	final boolean match(Class<?> key1Class, Class<?> key2Class,
			Class<?> key3Class, int mask) {
		return mask == MASK_DECLARE_SCRIPT && key1Class == String.class
				&& key2Class == DNASqlType.class && key3Class == null;
	}

	@Override
	final DeclareScriptBroker upperMatchBroker() {
		final Space space = this.space;
		if (space.site == space) {
			return null;// 到达站点了
		}
		return (DeclareScriptBroker) space.space.findInvokeeBase(URL.class,
				String.class, DNASqlType.class, null, MASK_DECLARE_SCRIPT,
				InvokeeQueryMode.IN_SITE);
	}
}
