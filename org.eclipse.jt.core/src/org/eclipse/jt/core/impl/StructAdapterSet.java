/**
 * Copyright (C) 2007-2008 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File StructAdapterContext.java
 * Date 2008-12-3
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.type.DataType;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
// TODO 注释
final class StructAdapterSet {
	private static final float loadFactor = 2.0f;

	private final ApplicationImpl application;

	private StructAdapter[] adapters;
	private int size;

	StructAdapterSet(ApplicationImpl application) {
		if (application == null) {
			throw new NullArgumentException("application");
		}
		this.application = application;
		this.adapters = new StructAdapter[8];
		this.size = 0;
	}

	static StructAdapter newAdapter(StructSummary remoteStructSummary,
			short remoteFieldCount, StructAdapterSet saContext)
			throws StructDefineNotFoundException {
		if (remoteStructSummary == null || saContext == null) {
			throw new NullPointerException();
		}
		if (remoteFieldCount < 0) {
			throw new IllegalArgumentException();
		}
		return new StructAdapter(remoteStructSummary, remoteFieldCount,
				saContext.findLocal(remoteStructSummary));
	}

	@SuppressWarnings("unchecked")
	final Class getLocalClass(String className) throws ClassNotFoundException {
		long start = System.nanoTime();
		try {
			return this.application.loadClass(className);
		} finally {
			RITestHelper.loadclasscount++;
			RITestHelper.loadclasscost += System.nanoTime() - start;
		}
	}

	private StructDefineImpl findLocal(StructSummary summary)
			throws StructDefineNotFoundException {
		// XXX 查找结构定义
		try {
			// XXX 模型暂时没有应用，因此暂不使用
			// StructDefineImpl define = (StructDefineImpl) this.application
			// .getDefaultSite().findNamedDefine(ModelDefine.class,
			// summary.defineName);
			// if (define == null) {
			// define = ObjectDataTypeBase.staticStructDefineOf(this
			// .getLocalClass(summary.defineName), true);
			// }
			final DataType odt = DataTypeBase.dataTypeOfJavaClass(this
					.getLocalClass(summary.defineName));
			if (odt instanceof StructDefineImpl) {
				return (StructDefineImpl) odt;
			}
			throw new UnsupportedOperationException("unexpected data type: "
					+ odt);
		} catch (Throwable e) {
			throw new StructDefineNotFoundException(summary.defineName, e);
		}
	}

	synchronized void clear() {
		this.adapters = null;
		this.size = 0;
	}

	synchronized StructAdapter findAdapter(StructSummary summary) {
		int index = UtilHelper.indexForObjectKey(summary, this.adapters.length);
		StructAdapter a = this.adapters[index];
		while (a != null) {
			if (a.remoteStructSummary == summary
					|| a.remoteStructSummary.equalNameAndVUIDs(summary)) {
				RITestHelper.sdahitcount++;
				return a;
			}
			a = a.next;
		}
		RITestHelper.sdaunhitcount++;
		return null;
	}

	synchronized StructAdapter putAdapter(StructAdapter adapter) {
		if (adapter == null || adapter.remoteStructSummary == null) {
			throw new NullPointerException();
		}

		// check
		StructSummary summary = adapter.remoteStructSummary;
		int index = UtilHelper.indexForObjectKey(summary, this.adapters.length);
		StructAdapter a = this.adapters[index], last = null;
		while (a != null) {
			if (a.remoteStructSummary == summary
					|| a.remoteStructSummary.equalNameAndVUIDs(summary)) {
				if (a == adapter) {
					return a;
				}
				if (last == null) {
					this.adapters[index] = adapter;
				} else {
					adapter.next = a.next;
					last.next = adapter;
				}
				return a;
			}
			last = a;
			a = a.next;
		}

		// add new
		this.ensureCapacity();
		index = UtilHelper.indexForObjectKey(adapter.remoteStructSummary,
				this.adapters.length);
		adapter.next = this.adapters[index];
		this.adapters[index] = adapter;
		this.size++;
		return adapter;
	}

	private void ensureCapacity() {
		if (this.size >= this.adapters.length * loadFactor) {
			final int newSize = this.adapters.length << 1;
			StructAdapter[] newSpine = new StructAdapter[newSize];
			StructAdapter a, temp;
			int newIndex;
			for (int i = 0, len = this.adapters.length; i < len; i++) {
				a = this.adapters[i];
				while (a != null) {
					temp = a.next;
					newIndex = UtilHelper.indexForObjectKey(
							a.remoteStructSummary, newSize);
					a.next = newSpine[newIndex];
					newSpine[newIndex] = a;
					a = temp;
				}
			}
			this.adapters = newSpine;
		}
	}
}
