package org.eclipse.jt.core.impl;

import java.util.ArrayList;

import org.eclipse.jt.core.def.obja.StructClass;
import org.eclipse.jt.core.invoke.SimpleTask;


@StructClass
final class NClusterResourceInitTask extends SimpleTask {

	NClusterResourceInitTask(final long resourceGroupLongID,
			final boolean getOrPost, final boolean lockOrUnlock,
			final boolean returnResource) {
		this.resourceGroupLongID = resourceGroupLongID;
		this.getOrPost = getOrPost;
		this.lockOrUnlock = lockOrUnlock;
		this.returnResource = returnResource;
		this.groupInited = false;
	}

	final boolean isGetTask() {
		return this.getOrPost;
	}

	final boolean isLockTask() {
		return this.lockOrUnlock;
	}

	final boolean needReturnResource() {
		return this.returnResource;
	}

	final boolean isGroupInited() {
		return this.groupInited;
	}

	final void setGetTask() {
		this.getOrPost = true;
	}

	final void setPostTask() {
		this.getOrPost = false;
	}

	final void setLockTask() {
		this.lockOrUnlock = true;
	}

	final void setUnlockTask() {
		this.lockOrUnlock = false;
	}

	final void setGroupInited() {
		this.groupInited = true;
	}

	final void setGroupUninited() {
		this.groupInited = false;
	}

	final void setNeedReturnResource() {
		this.returnResource = true;
	}

	final void setNeedNotReturnResource() {
		this.returnResource = false;
	}

	final ResourceItem addResourceItem(final long id, final Object resource,
			final Object keysHolder) {
		if (this.resourceList == null) {
			this.resourceList = new ArrayList<ResourceItem>();
		}
		final ResourceItem item = new ResourceItem(id, resource, keysHolder);
		this.resourceList.add(item);
		return item;
	}

	final ReferenceStorage addReferenceStorage(final long holderLongID,
			final long referenceGroupLongID) {
		if (this.referenceStorageList == null) {
			this.referenceStorageList = new ArrayList<ReferenceStorage>();
		}
		final ReferenceStorage storage = new ReferenceStorage(holderLongID,
				referenceGroupLongID);
		this.referenceStorageList.add(storage);
		return storage;
	}

	final TreeNodeImpl<Long> getResourceTree() {
		if (this.resourceTree == null) {
			this.resourceTree = new TreeNodeImpl<Long>(null, null);
		}
		return this.resourceTree;
	}

	final ArrayList<ResourceItem> clearResourceList() {
		final ArrayList<ResourceItem> list = this.resourceList;
		this.resourceList = null;
		return list;
	}

	final ArrayList<ReferenceStorage> clearReferenceStorageList() {
		final ArrayList<ReferenceStorage> list = this.referenceStorageList;
		this.referenceStorageList = null;
		return list;
	}

	final TreeNodeImpl<Long> clearResourceTree() {
		final TreeNodeImpl<Long> resourceTree = this.resourceTree;
		this.resourceTree = null;
		return resourceTree;
	}

	final long resourceGroupLongID;

	private boolean getOrPost;

	private boolean lockOrUnlock;

	private boolean returnResource;

	private boolean groupInited;

	private ArrayList<ResourceItem> resourceList;

	private ArrayList<ReferenceStorage> referenceStorageList;

	private TreeNodeImpl<Long> resourceTree;

	@StructClass
	static final class ResourceItem {

		private ResourceItem(final long id, final Object resource,
				final Object keysHolder) {
			this.id = id;
			this.resource = resource;
			this.keysHolder = keysHolder;
		}

		final long id;

		final Object resource;

		final Object keysHolder;

	}

	@StructClass
	static final class ReferenceStorage {

		private ReferenceStorage(final long holderLongID,
				final long referenceGroupLongID) {
			this.holderLongID = holderLongID;
			this.refrenceGroupLongID = referenceGroupLongID;
			this.referenceLongIDList = new long[4];
			this.referenceCount = 0;
		}

		final void addReferenceItem(final long referenceLongID) {
			if (this.referenceCount == this.referenceLongIDList.length) {
				final long[] newList = new long[this.referenceCount * 2];
				System.arraycopy(this.referenceLongIDList, 0, newList, 0,
						this.referenceCount);
				this.referenceLongIDList = newList;
			}
			this.referenceLongIDList[this.referenceCount++] = referenceLongID;
		}

		final long[] getReferenceList() {
			if (this.referenceCount == 0) {
				return new long[0];
			}
			final long[] result = new long[this.referenceCount];
			System.arraycopy(this.referenceLongIDList, 0, result, 0,
					this.referenceCount);
			return result;
		}

		final int getReferenceCount() {
			return this.referenceCount;
		}

		final long holderLongID;

		final long refrenceGroupLongID;

		private long[] referenceLongIDList;

		private int referenceCount;;

	}

}
