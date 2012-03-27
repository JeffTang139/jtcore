package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.DeadLockException;
import org.eclipse.jt.core.exception.DisposedException;
import org.eclipse.jt.core.resource.ResourceHandle;
import org.eclipse.jt.core.resource.ResourceKind;
import org.eclipse.jt.core.resource.ResourceQuerier;
import org.eclipse.jt.core.resource.ResourceToken;

/**
 * 资源句柄接口实现
 * 
 * @author Jeff Tang
 * 
 * @param <TFacade>
 * @param <TLocator>
 */
final class ResourceHandleImpl<TFacade, TImpl extends TFacade, TKeysHolder>
		extends
		ResourceAcquirer<TFacade, TImpl, TKeysHolder, ResourceHandleImpl<TFacade, TImpl, TKeysHolder>>
		implements ResourceHandle<TFacade>// , ResourceQuerier
{
	// public final boolean isValid() {
	// return this.demandContext.isValid();
	// }
	//
	// public final void checkValid() {
	// this.demandContext.checkValid();
	// }

	final short depth;

	volatile boolean isExternalLock = false;

	// /**
	// * 资源的一个拷贝副本
	// */
	// TImpl copy;

	ResourceHandleImpl(
			ResourceAcquirerHolder<ResourceHandleImpl<TFacade, TImpl, TKeysHolder>> holder,
			ResourceItem<TFacade, TImpl, TKeysHolder> res,
			ResourceDemandFor demandFor, short depth) throws DeadLockException {
		super(holder);
		this.depth = depth;
		demandFor.acquire(this, res);
		holder.transaction.tryLockItemInCluster(this);
	}

	// /**
	// * 如果返回有效，表示资源不为空。
	// *
	// * XXX 事务相关？
	// *
	// * @return
	// */
	// public final boolean isValid() {
	// return this.res != null && this.res.facade != null;
	// }
	final void checkNotDisposed() throws DisposedException {
		if (this.res == null || this.res.impl == null) {
			throw new DisposedException("资源句柄已经无效");
		}
	}

	// public final void invalidResource() {
	// if (this.res != null) {
	// this.res.group.invalidResource(this);
	// this.res.idleLife = 0;
	// }
	// }
	//
	// public final void invalidResourceNoDelay() throws DeadLockException {
	// if (this.res != null) {
	// this.res.group.invalidResource(this);
	// try {
	// this.toBeCompatibleWithExclusive(0);
	// this.res.dispose(this.demandContext.catcher);
	// } finally {
	// this.dispose(this.demandContext.catcher);
	// }
	// }
	// }
	public final ResourceQuerier getOwnedResourceQuerier() {
		this.checkNotDisposed();
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	public final Class<TFacade> getFacadeClass() {
		this.checkNotDisposed();
		return (Class<TFacade>) this.res.group.resourceService.facadeClass;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jt.core.resource.ResourceHandle#closeHandle()
	 */
	public final void closeHandle() {
		if (this.isExternalLock && this.res != null) {
			this.removeSelfFromHolderAndRelease();
			this.isExternalLock = false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jt.core.resource.ResourceHandle#getFacade()
	 */
	public final TFacade getFacade() {
		this.checkNotDisposed();
		return this.res.impl;
	}

	public TFacade tryGetFacade() {
		return this.res != null ? this.res.impl : null;
	}

	public final ResourceToken<TFacade> getToken() {
		this.checkNotDisposed();
		return this.res;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jt.core.resource.ResourceHandle#getKind()
	 */
	public final ResourceKind getKind() {
		this.checkNotDisposed();
		return this.res.group.resourceService.kind;
	}

	public final Object getCategory() {
		return this.res.group.category;
	}

	// @SuppressWarnings("hiding")
	// public final <TFacade> void ensureResourceInited(Class<TFacade>
	// facadeClass) {
	// if (facadeClass == null) {
	// throw new NullArgumentException("facadeClass");
	// }
	// this.demandContext.makeSureResourceInited(facadeClass, this
	// .getCategory());
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see
	// * org.eclipse.jt.core.resource.ResourcesQuerier#demandResource(java.lang
	// * .Class)
	// */
	// public final <TSubFacade> ResourceHandle<TSubFacade> lockResourceS(
	// ResourceToken<TSubFacade> resourceToken) {
	// return this.demandContext.lockValid(ResourceDemandFor.READ,
	// resourceToken);
	// }
	//
	// public final <TSubFacade> ResourceHandle<TSubFacade> lockResourceU(
	// ResourceToken<TSubFacade> resourceToken) {
	// return this.demandContext.lockValid(ResourceDemandFor.READ_THEN_MODIFY,
	// resourceToken);
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see org.eclipse.jt.core.ObjectQuerier#get(java.lang.Class)
	// */
	// public <TSubFacade> TSubFacade get(Class<TSubFacade> facadeClass)
	// throws UnsupportedOperationException {
	// TSubFacade result = this.find(facadeClass, null, null, null,
	// (Object[]) null);
	// if (result == null) {
	// throw new MissingObjectException("找不到（" + facadeClass + "）类的对象");
	// }
	// return result;
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see org.eclipse.jt.core.ObjectQuerier#get(java.lang.Class,
	// * java.lang.Object)
	// */
	// public <TSubFacade> TSubFacade get(Class<TSubFacade> facadeClass, Object
	// key)
	// throws UnsupportedOperationException {
	// TSubFacade result = this.find(facadeClass, key, null, null,
	// (Object[]) null);
	// if (result == null) {
	// throw new MissingObjectException("找不到（" + facadeClass + "）类的键为（"
	// + key + "）对象");
	// }
	// return result;
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see org.eclipse.jt.core.ObjectQuerier#get(java.lang.Class,
	// * java.lang.Object, java.lang.Object)
	// */
	// public <TSubFacade> TSubFacade get(Class<TSubFacade> facadeClass,
	// Object key1, Object key2) throws UnsupportedOperationException {
	// TSubFacade result = this.find(facadeClass, key1, key2, null,
	// (Object[]) null);
	// if (result == null) {
	// throw new MissingObjectException("找不到（" + facadeClass + "）类的键为（"
	// + key1 + ", " + key2 + "）对象");
	// }
	// return result;
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see org.eclipse.jt.core.ObjectQuerier#get(java.lang.Class,
	// * java.lang.Object, java.lang.Object, java.lang.Object)
	// */
	// public <TSubFacade> TSubFacade get(Class<TSubFacade> facadeClass,
	// Object key1, Object key2, Object key3)
	// throws UnsupportedOperationException {
	// TSubFacade result = this.find(facadeClass, key1, key2, key3,
	// (Object[]) null);
	// if (result == null) {
	// throw new MissingObjectException("找不到（" + facadeClass + "）类的键为（"
	// + key1 + ", " + key2 + ", " + key3 + "）对象");
	// }
	// return result;
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see org.eclipse.jt.core.ObjectQuerier#get(java.lang.Class,
	// * java.lang.Object, java.lang.Object, java.lang.Object,
	// java.lang.Object[])
	// */
	// public <TSubFacade> TSubFacade get(Class<TSubFacade> facadeClass,
	// Object key1, Object key2, Object key3, Object... keys)
	// throws UnsupportedOperationException {
	// TSubFacade result = this.find(facadeClass, key1, key2, key3, keys);
	// if (result == null) {
	// throw new MissingObjectException("找不到（" + facadeClass + "）类的键为（"
	// + key1 + ", " + key2 + ", " + key3 + ", ...）对象");
	// }
	// return result;
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see org.eclipse.jt.core.ObjectQuerier#find(java.lang.Class)
	// */
	// public <TSubFacade> TSubFacade find(Class<TSubFacade> facadeClass)
	// throws UnsupportedOperationException {
	// return this.find(facadeClass, null, null, null, (Object[]) null);
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see org.eclipse.jt.core.ObjectQuerier#find(java.lang.Class,
	// * java.lang.Object)
	// */
	// public <TSubFacade> TSubFacade find(Class<TSubFacade> facadeClass,
	// Object key) throws UnsupportedOperationException {
	// return this.find(facadeClass, key, null, null, (Object[]) null);
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see org.eclipse.jt.core.ObjectQuerier#find(java.lang.Class,
	// * java.lang.Object, java.lang.Object)
	// */
	// public <TSubFacade> TSubFacade find(Class<TSubFacade> facadeClass,
	// Object key1, Object key2) throws UnsupportedOperationException {
	// return this.find(facadeClass, key1, key2, null, (Object[]) null);
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see org.eclipse.jt.core.ObjectQuerier#find(java.lang.Class,
	// * java.lang.Object, java.lang.Object, java.lang.Object)
	// */
	// public <TSubFacade> TSubFacade find(Class<TSubFacade> facadeClass,
	// Object key1, Object key2, Object key3)
	// throws UnsupportedOperationException {
	// return this.find(facadeClass, key1, key2, key3, (Object[]) null);
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see org.eclipse.jt.core.ObjectQuerier#find(java.lang.Class,
	// * java.lang.Object, java.lang.Object, java.lang.Object,
	// java.lang.Object[])
	// */
	// @SuppressWarnings("unchecked")
	// public <TSubFacade> TSubFacade find(Class<TSubFacade> facadeClass,
	// Object key1, Object key2, Object key3, Object... keys)
	// throws UnsupportedOperationException {
	// ResourceItem<TSubFacade, ?, ?> resourceItem = this.demandContext
	// .findSubResource(ContextImpl.FIND_RESOURCE, this.res, null,
	// facadeClass, null, key1, key2, key3, keys);
	// return resourceItem != null ? resourceItem
	// .getResource(this.demandContext) : null;
	// }
	//
	// public <TSubFacade> List<TSubFacade> getList(Class<TSubFacade>
	// facadeClass)
	// throws UnsupportedOperationException {
	// return this.list(facadeClass, null, null, null, null, null, null);
	// }
	//
	// public <TSubFacade> List<TSubFacade> getList(Class<TSubFacade>
	// facadeClass,
	// Object key) throws UnsupportedOperationException {
	// return this.list(facadeClass, null, null, key, null, null, null);
	// }
	//
	// public <TSubFacade> List<TSubFacade> getList(Class<TSubFacade>
	// facadeClass,
	// Object key1, Object key2) throws UnsupportedOperationException {
	// return this.list(facadeClass, null, null, key1, key2, null, null);
	// }
	//
	// public <TSubFacade> List<TSubFacade> getList(Class<TSubFacade>
	// facadeClass,
	// Object key1, Object key2, Object key3)
	// throws UnsupportedOperationException {
	// return this.list(facadeClass, null, null, key1, key2, key3, null);
	// }
	//
	// public <TSubFacade> List<TSubFacade> getList(Class<TSubFacade>
	// facadeClass,
	// Object key1, Object key2, Object key3, Object... otherKeys) {
	// return this.list(facadeClass, null, null, key1, key2, key3, otherKeys);
	// }
	//
	// public <TSubFacade> List<TSubFacade> getList(Class<TSubFacade>
	// facadeClass,
	// Filter<? super TSubFacade> filter, Object key1, Object key2,
	// Object key3, Object... otherKeys) {
	// return this
	// .list(facadeClass, filter, null, key1, key2, key3, otherKeys);
	// }
	//
	// public <TSubFacade> List<TSubFacade> getList(Class<TSubFacade>
	// facadeClass,
	// Filter<? super TSubFacade> filter, Object key1, Object key2,
	// Object key3) throws UnsupportedOperationException {
	// return this.list(facadeClass, filter, null, key1, key2, key3, null);
	// }
	//
	// public <TSubFacade> List<TSubFacade> getList(Class<TSubFacade>
	// facadeClass,
	// Filter<? super TSubFacade> filter, Object key1, Object key2)
	// throws UnsupportedOperationException {
	// return this.list(facadeClass, filter, null, key1, key2, null, null);
	// }
	//
	// public <TSubFacade> List<TSubFacade> getList(Class<TSubFacade>
	// facadeClass,
	// Filter<? super TSubFacade> filter, Object key)
	// throws UnsupportedOperationException {
	// return this.list(facadeClass, filter, null, key, null, null, null);
	// }
	//
	// public <TSubFacade> List<TSubFacade> getList(Class<TSubFacade>
	// facadeClass,
	// Filter<? super TSubFacade> filter)
	// throws UnsupportedOperationException {
	// return this.list(facadeClass, filter, null, null, null, null, null);
	// }
	//
	// @SuppressWarnings("unchecked")
	// private <TSubFacade> List<TSubFacade> list(Class<TSubFacade> facadeClass,
	// Filter<? super TSubFacade> filter,
	// SortComparator<? super TSubFacade> sortComparator, Object key1,
	// Object key2, Object key3, Object[] otherKeys) {
	// ResourceGroup group;
	// ResourceIndex index;
	// if (key1 == null) {
	// group = this.res.getSubResourceGroup(facadeClass,
	// this.demandContext);
	// } else {
	// ResourceItem parent = this.demandContext.findSubParentResource(
	// ContextImpl.FIND_RESOURCE, this.res, null, facadeClass,
	// null, key1, key2, key3, otherKeys);
	// if (parent == null) {
	// return new ArrayList(0);
	// }
	// group = parent.getSubResourceGroup(facadeClass, this.demandContext);
	// }
	// index = group.getResourceIndex((byte) 0);
	// List<TSubFacade> list = new ArrayList<TSubFacade>(index.size());
	// if (!index.isEmpty()) {
	// index.lockFillResources(list, this.demandContext, filter,
	// sortComparator);
	// }
	// return list;
	// }
	//
	// public <TSubFacade> List<TSubFacade> getList(Class<TSubFacade>
	// facadeClass,
	// Filter<? super TSubFacade> filter,
	// SortComparator<? super TSubFacade> sortComparator, Object key1,
	// Object key2, Object key3, Object... otherKeys) {
	// return this.list(facadeClass, filter, sortComparator, key1, key2, key3,
	// otherKeys);
	// }
	//
	// public <TSubFacade> List<TSubFacade> getList(Class<TSubFacade>
	// facadeClass,
	// Filter<? super TSubFacade> filter,
	// SortComparator<? super TSubFacade> sortComparator, Object key1,
	// Object key2, Object key3) throws UnsupportedOperationException {
	// return this.list(facadeClass, filter, sortComparator, key1, key2, key3,
	// null);
	// }
	//
	// public <TSubFacade> List<TSubFacade> getList(Class<TSubFacade>
	// facadeClass,
	// Filter<? super TSubFacade> filter,
	// SortComparator<? super TSubFacade> sortComparator, Object key1,
	// Object key2) throws UnsupportedOperationException {
	// return this.list(facadeClass, filter, sortComparator, key1, key2, null,
	// null);
	// }
	//
	// public <TSubFacade> List<TSubFacade> getList(Class<TSubFacade>
	// facadeClass,
	// Filter<? super TSubFacade> filter,
	// SortComparator<? super TSubFacade> sortComparator, Object key)
	// throws UnsupportedOperationException {
	// return this.list(facadeClass, filter, sortComparator, key, null, null,
	// null);
	// }
	//
	// public <TSubFacade> List<TSubFacade> getList(Class<TSubFacade>
	// facadeClass,
	// Filter<? super TSubFacade> filter,
	// SortComparator<? super TSubFacade> sortComparator)
	// throws UnsupportedOperationException {
	// return this.list(facadeClass, filter, sortComparator, null, null, null,
	// null);
	// }
	//
	// public <TSubFacade> List<TSubFacade> getList(Class<TSubFacade>
	// facadeClass,
	// SortComparator<? super TSubFacade> sortComparator, Object key1,
	// Object key2, Object key3, Object... otherKeys) {
	// return this.list(facadeClass, null, sortComparator, key1, key2, key3,
	// otherKeys);
	// }
	//
	// public <TSubFacade> List<TSubFacade> getList(Class<TSubFacade>
	// facadeClass,
	// SortComparator<? super TSubFacade> sortComparator, Object key1,
	// Object key2, Object key3) throws UnsupportedOperationException {
	// return this.list(facadeClass, null, sortComparator, key1, key2, key3,
	// null);
	// }
	//
	// public <TSubFacade> List<TSubFacade> getList(Class<TSubFacade>
	// facadeClass,
	// SortComparator<? super TSubFacade> sortComparator, Object key1,
	// Object key2) throws UnsupportedOperationException {
	// return this.list(facadeClass, null, sortComparator, key1, key2, null,
	// null);
	// }
	//
	// public <TSubFacade> List<TSubFacade> getList(Class<TSubFacade>
	// facadeClass,
	// SortComparator<? super TSubFacade> sortComparator, Object key)
	// throws UnsupportedOperationException {
	// return this.list(facadeClass, null, sortComparator, key, null, null,
	// null);
	// }
	//
	// public <TSubFacade> List<TSubFacade> getList(Class<TSubFacade>
	// facadeClass,
	// SortComparator<? super TSubFacade> sortComparator)
	// throws UnsupportedOperationException {
	// return this.list(facadeClass, null, sortComparator, null, null, null,
	// null);
	// }
	//
	// public <TSubFacade> TreeNode<TSubFacade> getTreeNode(
	// Class<TSubFacade> facadeClass) throws UnsupportedOperationException {
	// if (facadeClass == null) {
	// throw new NullPointerException("facadeClass is null");
	// }
	// ResourceGroup<TSubFacade, ?, ?> group = this.res.getSubResourceGroup(
	// facadeClass, this.demandContext);
	// return group.fillTree(null, this.demandContext, null, null);
	// }
	//
	// public <TSubFacade> TreeNode<TSubFacade> getTreeNode(
	// Class<TSubFacade> facadeClass, Object key)
	// throws UnsupportedOperationException {
	// return this.treeNode(facadeClass, key, null, null, null);
	// }
	//
	// public <TSubFacade> TreeNode<TSubFacade> getTreeNode(
	// Class<TSubFacade> facadeClass, Object key1, Object key2)
	// throws UnsupportedOperationException {
	// return this.treeNode(facadeClass, key1, key2, null, null);
	// }
	//
	// public <TSubFacade> TreeNode<TSubFacade> getTreeNode(
	// Class<TSubFacade> facadeClass, Object key1, Object key2, Object key3)
	// throws UnsupportedOperationException {
	// return this.treeNode(facadeClass, key1, key2, key3, null);
	// }
	//
	// public <TSubFacade> TreeNode<TSubFacade> getTreeNode(
	// Class<TSubFacade> facadeClass, Object key1, Object key2,
	// Object key3, Object... otherKeys)
	// throws UnsupportedOperationException {
	// return this.treeNode(facadeClass, key1, key2, key3, otherKeys);
	// }
	//
	// @SuppressWarnings("unchecked")
	// private <TSubFacade> TreeNode<TSubFacade> treeNode(
	// Class<TSubFacade> facadeClass, Object key1, Object key2,
	// Object key3, Object[] otherKeys)
	// throws UnsupportedOperationException {
	// if (facadeClass == null) {
	// throw new NullPointerException("facadeClass is null");
	// }
	// ResourceItem<TSubFacade, ?, ?> item = this.demandContext
	// .findSubResource(ContextImpl.GET_RESOURCE, this.res, null,
	// facadeClass, null, key1, key2, key3, otherKeys);
	// return item.fillTree(null, this.demandContext, null, null);
	// }
	//
	// @SuppressWarnings("unchecked")
	// private <TSubFacade> TreeNode<TSubFacade> treeNode(
	// Class<TSubFacade> facadeClass,
	// SortComparator<? super TSubFacade> sortComparator, Object key1,
	// Object key2, Object key3, Object[] otherKeys)
	// throws UnsupportedOperationException {
	// if (facadeClass == null) {
	// throw new NullPointerException("facadeClass is null");
	// }
	// if (sortComparator == null) {
	// throw new NullPointerException("sortComparator is null");
	// }
	// ResourceItem<TSubFacade, ?, ?> item = this.demandContext
	// .findSubResource(ContextImpl.GET_RESOURCE, this.res, null,
	// facadeClass, null, key1, key2, key3, otherKeys);
	// return item.fillTree(null, this.demandContext, null, sortComparator);
	// }
	//
	// public <TSubFacade> TreeNode<TSubFacade> getTreeNode(
	// Class<TSubFacade> facadeClass,
	// SortComparator<? super TSubFacade> sortComparator, Object key1,
	// Object key2, Object key3, Object... otherKeys)
	// throws UnsupportedOperationException {
	// return this.treeNode(facadeClass, sortComparator, key1, key2, key3,
	// otherKeys);
	// }
	//
	// public <TSubFacade> TreeNode<TSubFacade> getTreeNode(
	// Class<TSubFacade> facadeClass,
	// SortComparator<? super TSubFacade> sortComparator, Object key1,
	// Object key2, Object key3) throws UnsupportedOperationException {
	// return this.treeNode(facadeClass, sortComparator, key1, key2, key3,
	// null);
	// }
	//
	// public <TSubFacade> TreeNode<TSubFacade> getTreeNode(
	// Class<TSubFacade> facadeClass,
	// SortComparator<? super TSubFacade> sortComparator, Object key1,
	// Object key2) throws UnsupportedOperationException {
	// return this.treeNode(facadeClass, sortComparator, key1, key2, null,
	// null);
	// }
	//
	// public <TSubFacade> TreeNode<TSubFacade> getTreeNode(
	// Class<TSubFacade> facadeClass,
	// SortComparator<? super TSubFacade> sortComparator, Object key)
	// throws UnsupportedOperationException {
	// return this
	// .treeNode(facadeClass, sortComparator, key, null, null, null);
	// }
	//
	// public <TSubFacade> TreeNode<TSubFacade> getTreeNode(
	// Class<TSubFacade> facadeClass,
	// SortComparator<? super TSubFacade> sortComparator)
	// throws UnsupportedOperationException {
	// if (facadeClass == null) {
	// throw new NullPointerException("facadeClass is null");
	// }
	// ResourceGroup<TSubFacade, ?, ?> group = this.res.getSubResourceGroup(
	// facadeClass, this.demandContext);
	// return group.fillTree(null, this.demandContext, null, sortComparator);
	// }
	//
	// @SuppressWarnings("unchecked")
	// private <TSubFacade> TreeNode<TSubFacade> treeNode(
	// Class<TSubFacade> facadeClass,
	// TreeNodeFilter<? super TSubFacade> filter, Object key1,
	// Object key2, Object key3, Object[] otherKeys)
	// throws UnsupportedOperationException {
	// if (facadeClass == null) {
	// throw new NullPointerException("facadeClass is null");
	// }
	// if (filter == null) {
	// throw new NullPointerException("filter is null");
	// }
	// ResourceItem<TSubFacade, ?, ?> item = this.demandContext
	// .findSubResource(ContextImpl.GET_RESOURCE, this.res, null,
	// facadeClass, null, key1, key2, key3, otherKeys);
	// return item.fillTree(null, this.demandContext, filter, null);
	// }
	//
	// public <TSubFacade> TreeNode<TSubFacade> getTreeNode(
	// Class<TSubFacade> facadeClass,
	// TreeNodeFilter<? super TSubFacade> filter, Object key1,
	// Object key2, Object key3, Object... otherKeys)
	// throws UnsupportedOperationException {
	// return this.treeNode(facadeClass, filter, key1, key2, key3, otherKeys);
	// }
	//
	// public <TSubFacade> TreeNode<TSubFacade> getTreeNode(
	// Class<TSubFacade> facadeClass,
	// TreeNodeFilter<? super TSubFacade> filter, Object key1,
	// Object key2, Object key3) throws UnsupportedOperationException {
	// return this.treeNode(facadeClass, filter, key1, key2, key3, null);
	// }
	//
	// public <TSubFacade> TreeNode<TSubFacade> getTreeNode(
	// Class<TSubFacade> facadeClass,
	// TreeNodeFilter<? super TSubFacade> filter, Object key1, Object key2)
	// throws UnsupportedOperationException {
	// return this.treeNode(facadeClass, filter, key1, key2, null, null);
	// }
	//
	// public <TSubFacade> TreeNode<TSubFacade> getTreeNode(
	// Class<TSubFacade> facadeClass,
	// TreeNodeFilter<? super TSubFacade> filter, Object key)
	// throws UnsupportedOperationException {
	// return this.treeNode(facadeClass, filter, key, null, null, null);
	// }
	//
	// public <TSubFacade> TreeNode<TSubFacade> getTreeNode(
	// Class<TSubFacade> facadeClass,
	// TreeNodeFilter<? super TSubFacade> filter)
	// throws UnsupportedOperationException {
	// if (facadeClass == null) {
	// throw new NullPointerException("facadeClass is null");
	// }
	// ResourceGroup<TSubFacade, ?, ?> group = this.res.getSubResourceGroup(
	// facadeClass, this.demandContext);
	// return group.fillTree(null, this.demandContext, filter, null);
	// }
	//
	// @SuppressWarnings("unchecked")
	// private <TSubFacade> TreeNode<TSubFacade> treeNode(
	// Class<TSubFacade> facadeClass,
	// TreeNodeFilter<? super TSubFacade> filter,
	// SortComparator<? super TSubFacade> sortComparator, Object key1,
	// Object key2, Object key3, Object[] otherKeys)
	// throws UnsupportedOperationException {
	// if (facadeClass == null) {
	// throw new NullPointerException("facadeClass is null");
	// }
	// if (filter == null || sortComparator == null) {
	// throw new NullPointerException("filter or sortComparator is null");
	// }
	// ResourceItem<TSubFacade, ?, ?> item = this.demandContext
	// .findSubResource(ContextImpl.GET_RESOURCE, this.res, null,
	// facadeClass, null, key1, key2, key3, otherKeys);
	// return item.fillTree(null, this.demandContext, filter, sortComparator);
	// }
	//
	// public <TSubFacade> TreeNode<TSubFacade> getTreeNode(
	// Class<TSubFacade> facadeClass,
	// TreeNodeFilter<? super TSubFacade> filter,
	// SortComparator<? super TSubFacade> sortComparator, Object key1,
	// Object key2, Object key3, Object... otherKeys)
	// throws UnsupportedOperationException {
	// return this.treeNode(facadeClass, filter, sortComparator, key1, key2,
	// key3, otherKeys);
	// }
	//
	// public <TSubFacade> TreeNode<TSubFacade> getTreeNode(
	// Class<TSubFacade> facadeClass,
	// TreeNodeFilter<? super TSubFacade> filter,
	// SortComparator<? super TSubFacade> sortComparator, Object key1,
	// Object key2, Object key3) throws UnsupportedOperationException {
	// return this.treeNode(facadeClass, filter, sortComparator, key1, key2,
	// key3, null);
	// }
	//
	// public <TSubFacade> TreeNode<TSubFacade> getTreeNode(
	// Class<TSubFacade> facadeClass,
	// TreeNodeFilter<? super TSubFacade> filter,
	// SortComparator<? super TSubFacade> sortComparator, Object key1,
	// Object key2) throws UnsupportedOperationException {
	// return this.treeNode(facadeClass, filter, sortComparator, key1, key2,
	// null, null);
	// }
	//
	// public <TSubFacade> TreeNode<TSubFacade> getTreeNode(
	// Class<TSubFacade> facadeClass,
	// TreeNodeFilter<? super TSubFacade> filter,
	// SortComparator<? super TSubFacade> sortComparator, Object key)
	// throws UnsupportedOperationException {
	// return this.treeNode(facadeClass, filter, sortComparator, key, null,
	// null, null);
	// }
	//
	// public <TSubFacade> TreeNode<TSubFacade> getTreeNode(
	// Class<TSubFacade> facadeClass,
	// TreeNodeFilter<? super TSubFacade> filter,
	// SortComparator<? super TSubFacade> sortComparator)
	// throws UnsupportedOperationException {
	// if (facadeClass == null) {
	// throw new NullPointerException("facadeClass is null");
	// }
	// ResourceGroup<TSubFacade, ?, ?> group = this.res.getSubResourceGroup(
	// facadeClass, this.demandContext);
	// return group.fillTree(null, this.demandContext, filter, sortComparator);
	// }
	//
	// public <TSubFacade> ResourceItem<TSubFacade, ?, ?> getResourceToken(
	// Class<TSubFacade> facadeClass) {
	// ResourceItem<TSubFacade, ?, ?> token = this.resourceToken(facadeClass,
	// null, null, null, null);
	// if (token == null) {
	// throw new MissingObjectException("找不到（" + facadeClass + "）类的对象");
	// }
	// return token;
	// }
	//
	// public <TSubFacade> ResourceItem<TSubFacade, ?, ?> getResourceToken(
	// Class<TSubFacade> facadeClass, Object key) {
	// ResourceItem<TSubFacade, ?, ?> token = this.resourceToken(facadeClass,
	// key, null, null, null);
	// if (token == null) {
	// throw new MissingObjectException("找不到（" + facadeClass + "）类的键为（"
	// + key + "）对象");
	// }
	// return token;
	// }
	//
	// public <TSubFacade> ResourceItem<TSubFacade, ?, ?> getResourceToken(
	// Class<TSubFacade> facadeClass, Object key1, Object key2) {
	// ResourceItem<TSubFacade, ?, ?> token = this.resourceToken(facadeClass,
	// key1, key2, null, null);
	// if (token == null) {
	// throw new MissingObjectException("找不到（" + facadeClass + "）类的键为（"
	// + key1 + ", " + key2 + "）对象");
	// }
	// return token;
	// }
	//
	// public <TSubFacade> ResourceItem<TSubFacade, ?, ?> getResourceToken(
	// Class<TSubFacade> facadeClass, Object key1, Object key2, Object key3) {
	// ResourceItem<TSubFacade, ?, ?> token = this.resourceToken(facadeClass,
	// key1, key2, key3, null);
	// if (token == null) {
	// throw new MissingObjectException("找不到（" + facadeClass + "）类的键为（"
	// + key1 + ", " + key2 + ", " + key3 + "）对象");
	// }
	// return token;
	// }
	//
	// public <TSubFacade> ResourceItem<TSubFacade, ?, ?> getResourceToken(
	// Class<TSubFacade> facadeClass, Object key1, Object key2,
	// Object key3, Object... otherKeys) {
	// ResourceItem<TSubFacade, ?, ?> token = this.resourceToken(facadeClass,
	// key1, key2, key3, otherKeys);
	// if (token == null) {
	// throw new MissingObjectException("找不到（" + facadeClass + "）类的键为（"
	// + key1 + ", " + key2 + ", " + key3 + ", ...）对象");
	// }
	// return token;
	// }
	//
	// @SuppressWarnings("unchecked")
	// private <TSubFacade> ResourceItem<TSubFacade, ?, ?> resourceToken(
	// Class<TSubFacade> facadeClass, Object key1, Object key2,
	// Object key3, Object[] otherKeys) {
	// return this.demandContext.findSubResource(ContextImpl.FIND_RESOURCE,
	// this.res, null, facadeClass, null, key1, key2, key3, otherKeys);
	// }
	//
	// public <TSubFacade> ResourceItem<TSubFacade, ?, ?> findResourceToken(
	// Class<TSubFacade> facadeClass, Object key1, Object key2,
	// Object key3, Object... otherKeys) {
	// return this.resourceToken(facadeClass, key1, key2, key3, otherKeys);
	// }
	//
	// public <TSubFacade> ResourceItem<TSubFacade, ?, ?> findResourceToken(
	// Class<TSubFacade> facadeClass, Object key1, Object key2, Object key3) {
	// return this.resourceToken(facadeClass, key1, key2, key3, null);
	// }
	//
	// public <TSubFacade> ResourceItem<TSubFacade, ?, ?> findResourceToken(
	// Class<TSubFacade> facadeClass, Object key1, Object key2) {
	// return this.resourceToken(facadeClass, key1, key2, null, null);
	// }
	//
	// public <TSubFacade> ResourceItem<TSubFacade, ?, ?> findResourceToken(
	// Class<TSubFacade> facadeClass, Object key) {
	// return this.resourceToken(facadeClass, key, null, null, null);
	// }
	//
	// public <TSubFacade> ResourceItem<TSubFacade, ?, ?> findResourceToken(
	// Class<TSubFacade> facadeClass) {
	// return this.resourceToken(facadeClass, null, null, null, null);
	// }
	//
	// @SuppressWarnings("unchecked")
	// public <TSubFacade, THolderFacade> List<TSubFacade>
	// getResourceReferences(
	// Class<TSubFacade> facadeClass,
	// ResourceToken<THolderFacade> holderToken) {
	// List<TSubFacade> list = new ArrayList<TSubFacade>();
	// ((ResourceItem<THolderFacade, ?, ?>) holderToken).fillReferences(
	// facadeClass, list, this.demandContext, null, null);
	// return list;
	// }
	//
	// @SuppressWarnings("unchecked")
	// public <TSubFacade, THolderFacade> List<TSubFacade>
	// getResourceReferences(
	// Class<TSubFacade> facadeClass,
	// ResourceToken<THolderFacade> holderToken,
	// Filter<? super TSubFacade> filter,
	// SortComparator<? super TSubFacade> sortComparator) {
	// List<TSubFacade> list = new ArrayList<TSubFacade>();
	// ((ResourceItem<THolderFacade, ?, ?>) holderToken).fillReferences(
	// facadeClass, list, this.demandContext, filter, sortComparator);
	// return list;
	// }
	//
	// @SuppressWarnings("unchecked")
	// public <TSubFacade, THolderFacade> List<TSubFacade>
	// getResourceReferences(
	// Class<TSubFacade> facadeClass,
	// ResourceToken<THolderFacade> holderToken,
	// Filter<? super TSubFacade> filter) {
	// List<TSubFacade> list = new ArrayList<TSubFacade>();
	// ((ResourceItem<THolderFacade, ?, ?>) holderToken).fillReferences(
	// facadeClass, list, this.demandContext, filter, null);
	// return list;
	// }
	//
	// @SuppressWarnings("unchecked")
	// public <TSubFacade, THolderFacade> List<TSubFacade>
	// getResourceReferences(
	// Class<TSubFacade> facadeClass,
	// ResourceToken<THolderFacade> holderToken,
	// SortComparator<? super TSubFacade> sortComparator) {
	// List<TSubFacade> list = new ArrayList<TSubFacade>();
	// ((ResourceItem<THolderFacade, ?, ?>) holderToken).fillReferences(
	// facadeClass, list, this.demandContext, null, sortComparator);
	// return list;
	// }
}
