package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.misc.MissingObjectException;
import org.eclipse.jt.core.spi.metadata.MetaDataEntry;

/**
 * 条目信息
 * 
 * @author Jeff Tang
 * 
 */
final class MetaDataEntryImpl extends NamedDefineImpl implements MetaDataEntry {
	long version;
	int offset = -1;
	int size = -1;

	public final int getDataSize() {
		return this.size;
	}

	public final long getVersion() {
		return this.version;
	}

	public final int getSubCount() {
		if (this.subs == null) {
			return 0;
		}
		return this.subs.size();
	}

	public final MetaDataEntryImpl getSub(int index)
	        throws IndexOutOfBoundsException {
		if (this.subs == null) {
			throw new IndexOutOfBoundsException();
		}
		return this.subs.get(index);
	}

	public final MetaDataEntryImpl getSub(String name)
	        throws MissingObjectException {
		if (this.subs == null) {
			throw new MissingObjectException();
		}
		return this.subs.get(name);
	}

	public final MetaDataEntryImpl findSub(String name)
	        throws MissingObjectException {
		if (this.subs == null) {
			return null;
		}
		return this.subs.find(name);
	}

	private NamedDefineContainerImpl<MetaDataEntryImpl> subs;

	final MetaDataEntryImpl ensureEntry(String pathName) {
		if (pathName != null && pathName.length() > 0) {
			MetaDataEntryImpl entry = this;
			int start = pathName.charAt(0) == '/' ? 1 : 0;
			int eof = pathName.length();
			while (start < eof) {
				int end = pathName.indexOf('/', start);
				if (end < 0) {
					end = eof;
				}
				if (end > start) {
					final String subName = pathName.substring(start, end);
					MetaDataEntryImpl sub;
					if (entry.subs == null) {
						entry.subs = new NamedDefineContainerImpl<MetaDataEntryImpl>();
						sub = null;
					} else {
						sub = entry.subs.find(subName);
					}
					if (sub == null) {
						entry.subs.add(entry = new MetaDataEntryImpl(subName));
					} else {
						entry = sub;
					}
				}
				start = end + 1;
			}
			return entry;
		} else {
			return null;
		}
	}

	MetaDataEntryImpl(String name) {
		super(name);
	}

	@Override
	public final String getXMLTagName() {
		throw new UnsupportedOperationException();
	}
}
