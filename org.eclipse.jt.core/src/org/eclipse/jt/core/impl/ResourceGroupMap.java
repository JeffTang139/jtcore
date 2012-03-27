/**
 * 
 */
package org.eclipse.jt.core.impl;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.eclipse.jt.core.None;
import org.eclipse.jt.core.misc.ExceptionCatcher;
import org.eclipse.jt.core.resource.ResourceCategory;


/**
 * 资源组Map
 * 
 */
final class ResourceGroupMap {
	final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	@SuppressWarnings("unchecked")
	private ResourceGroup[] byCategory;
	private volatile int sizeByCategory;
	@SuppressWarnings("unchecked")
	private ResourceGroup[] byCategoryId;
	private volatile int sizeByCategoryId;

	int size() {
		return this.sizeByCategory + this.sizeByCategoryId;
	}

	@SuppressWarnings("unchecked")
	final <TFacade, TImpl extends TFacade, TKeys> ResourceGroup<TFacade, TImpl, TKeys> ensureResourceGroup(
	        String title,
	        ResourceServiceBase<TFacade, TImpl, TKeys> resourceService,
	        Object category, ContextImpl context) {

		if (category == null) {
			category = None.NONE;
		}
		ResourceGroup resourceGroup;
		ensureGroup: {
			int sizeSave;// 检查有没有发生变化用.
			final ReentrantReadWriteLock.ReadLock findLock = this.lock
			        .readLock();
			findLock.lock();
			try {
				sizeSave = this.size();
				resourceGroup = this.get(resourceService, category);
			} finally {
				findLock.unlock();
			}
			// FIXME 这里的处理会受到集群的影响。
			if (resourceGroup != null) {
				break ensureGroup;
			}

			resourceGroup = new ResourceGroup<TFacade, TImpl, TKeys>(title,
			        null, resourceService, category);
			final ReentrantReadWriteLock.WriteLock modifyLock = this.lock
			        .writeLock();
			modifyLock.lock();
			try {
				int size = this.size();
				if (sizeSave != size && size > 0) {
					ResourceGroup group = this.get(resourceService, category);
					if (group != null) {
						resourceGroup = group;
						break ensureGroup;
					}
				}
				this.put(resourceGroup);
			} finally {
				modifyLock.unlock();
			}
		}
		resourceGroup.ensureInit(context);
		return resourceGroup;
	}

	@SuppressWarnings("unchecked")
	void reset(ExceptionCatcher catcher, boolean dispose) {
		final WriteLock modifyLock = this.lock.writeLock();
		modifyLock.lock();
		try {
			if (this.sizeByCategory > 0) {
				for (int i = 0, len = this.byCategory.length; i < len; i++) {
					for (ResourceGroup group = this.byCategory[i]; group != null; group = group.next) {
						group.reset(catcher, dispose);
					}
				}
				this.byCategory = null;
				this.sizeByCategory = 0;
			}
			if (this.sizeByCategoryId > 0) {
				for (int i = 0, len = this.byCategoryId.length; i < len; i++) {
					for (ResourceGroup group = this.byCategoryId[i]; group != null; group = group.next) {
						group.reset(catcher, dispose);
					}
				}
				this.byCategoryId = null;
				this.sizeByCategoryId = 0;
			}

		} finally {
			modifyLock.unlock();
		}
	}

	@SuppressWarnings("unchecked")
	ResourceGroup get(ResourceServiceBase resourceService, Object category) {
		ResourceGroup[] groups;
		int size;
		if (category instanceof ResourceCategory) {
			groups = this.byCategoryId;
			size = this.sizeByCategoryId;
		} else {
			groups = this.byCategory;
			size = this.sizeByCategory;
		}

		if (size > 0) {
			int hash = resourceService.calCategoryHashCode(category);
			for (ResourceGroup resourceGroup = groups[hash
			        & (groups.length - 1)]; resourceGroup != null; resourceGroup = resourceGroup.next) {
				if (resourceGroup.resourceService == resourceService
				        && (category == resourceGroup.category || resourceGroup.category
				                .equals(category))) {
					return resourceGroup;
				}
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private void put(ResourceGroup resourceGroup) {
		boolean rcType = resourceGroup.category instanceof ResourceCategory;
		ResourceGroup[] groups;
		int size;
		if (rcType) {
			groups = this.byCategoryId;
			size = this.sizeByCategoryId;
		} else {
			groups = this.byCategory;
			size = this.sizeByCategory;
		}

		int oldLen;
		if (groups == null) {
			oldLen = 4;
			groups = new ResourceGroup[oldLen];
		} else {
			oldLen = groups.length;
		}
		int index = resourceGroup.hash & (oldLen - 1);
		resourceGroup.next = groups[index];
		groups[index] = resourceGroup;
		if (++size > oldLen * 0.75) {
			int newLen = oldLen * 2;
			ResourceGroup[] newTable = new ResourceGroup[newLen];
			newLen--;
			for (int i = 0; i < oldLen; i++) {
				for (ResourceGroup group = groups[i], next; group != null; group = next) {
					index = group.hash & newLen;
					next = group.next;
					group.next = newTable[index];
					newTable[index] = group;
				}
			}
			groups = newTable;
		}

		if (rcType) {
			this.byCategoryId = groups;
			this.sizeByCategoryId = size;
		} else {
			this.byCategory = groups;
			this.sizeByCategory = size;
		}
	}
}