/**
 * Copyright (C) 2007-2008 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File CategoryContextAdapter.java
 * Date 2008-11-19
 */
package org.eclipse.jt.core.impl;

import java.util.Comparator;
import java.util.List;

import org.eclipse.jt.core.Filter;
import org.eclipse.jt.core.TreeNode;
import org.eclipse.jt.core.TreeNodeFilter;
import org.eclipse.jt.core.auth.Operation;
import org.eclipse.jt.core.exception.DeadLockException;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.misc.MissingObjectException;
import org.eclipse.jt.core.resource.CategorialResourceModifier;
import org.eclipse.jt.core.resource.ResourceHandle;
import org.eclipse.jt.core.resource.ResourceToken;
import org.eclipse.jt.core.resource.ResourceService.WhenExists;


final class CategorialResContextAdapter<TFacadeM, TImplM extends TFacadeM, TKeysHolderM>
		implements CategorialResourceModifier<TFacadeM, TImplM, TKeysHolderM> {

	public final boolean isValid() {
		return this.context.isValid();
	}

	public final void checkValid() {
		this.context.checkValid();
	}

	final ContextImpl<TFacadeM, TImplM, TKeysHolderM> context;
	private Object category;

	CategorialResContextAdapter(
			ContextImpl<TFacadeM, TImplM, TKeysHolderM> context, Object category) {
		this.context = context;
		this.category = category;
	}

	public final Object getCategory() {
		return this.category;
	}

	public void setCategory(Object category) {
		if (category == null) {
			throw new NullPointerException();
		}
		this.category = category;
	}

	public final <TFacade> void ensureResourceInited(Class<TFacade> facadeClass) {
		if (facadeClass == null) {
			throw new NullArgumentException("facadeClass");
		}
		this.context.makeSureResourceInited(facadeClass, this.category);
	}

	/* --------------------- Query Methods -------------------------- */

	@SuppressWarnings("unchecked")
	public <TFacade> ResourceToken<TFacade> findResourceToken(
			Class<TFacade> facadeClass) {
		if (facadeClass == null) {
			throw new NullPointerException();
		}
		return this.context.internalFindResource(null,
				TransactionImpl.FIND_RESOURCE, null, facadeClass,
				this.category, null, null, null, null);
	}

	@SuppressWarnings("unchecked")
	public <TFacade> ResourceToken<TFacade> findResourceToken(
			Class<TFacade> facadeClass, Object key) {
		if (facadeClass == null || key == null) {
			throw new NullPointerException();
		}
		return this.context.internalFindResource(null,
				TransactionImpl.FIND_RESOURCE, null, facadeClass,
				this.category, key, null, null, null);
	}

	@SuppressWarnings("unchecked")
	public <TFacade> ResourceToken<TFacade> findResourceToken(
			Class<TFacade> facadeClass, Object key1, Object key2) {
		if (facadeClass == null || key1 == null || key2 == null) {
			throw new NullPointerException();
		}
		return this.context.internalFindResource(null,
				TransactionImpl.FIND_RESOURCE, null, facadeClass,
				this.category, key1, key2, null, null);
	}

	@SuppressWarnings("unchecked")
	public <TFacade> ResourceToken<TFacade> findResourceToken(
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3) {
		if (facadeClass == null || key1 == null || key2 == null || key3 == null) {
			throw new NullPointerException();
		}
		return this.context.internalFindResource(null,
				TransactionImpl.FIND_RESOURCE, null, facadeClass,
				this.category, key1, key2, key3, null);
	}

	@SuppressWarnings("unchecked")
	public <TFacade> ResourceToken<TFacade> findResourceToken(
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3,
			Object... otherKeys) {
		if (facadeClass == null || key1 == null || key2 == null || key3 == null
				|| otherKeys == null) {
			throw new NullPointerException();
		}
		return this.context.internalFindResource(null,
				TransactionImpl.FIND_RESOURCE, null, facadeClass,
				this.category, key1, key2, key3, otherKeys);
	}

	public <TFacade, THolderFacade> List<TFacade> getResourceReferences(
			Class<TFacade> facadeClass, ResourceToken<THolderFacade> holderToken) {
		return this.context.getResourceReferences(facadeClass, holderToken);
	}

	public <TFacade, THolderFacade> List<TFacade> getResourceReferences(
			Class<TFacade> facadeClass,
			ResourceToken<THolderFacade> holderToken,
			Filter<? super TFacade> filter) {
		return this.context.getResourceReferences(facadeClass, holderToken,
				filter);
	}

	public <TFacade, THolderFacade> List<TFacade> getResourceReferences(
			Class<TFacade> facadeClass,
			ResourceToken<THolderFacade> holderToken,
			Comparator<? super TFacade> sortComparator) {
		return this.context.getResourceReferences(facadeClass, holderToken,
				sortComparator);
	}

	public <TFacade, THolderFacade> List<TFacade> getResourceReferences(
			Class<TFacade> facadeClass,
			ResourceToken<THolderFacade> holderToken,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator) {
		return this.context.getResourceReferences(facadeClass, holderToken,
				filter, sortComparator);
	}

	@SuppressWarnings("unchecked")
	public <TFacade> ResourceToken<TFacade> getResourceToken(
			Class<TFacade> facadeClass) throws MissingObjectException {
		if (facadeClass == null) {
			throw new NullPointerException();
		}
		ResourceToken<TFacade> result = this.context.internalFindResource(null,
				TransactionImpl.FIND_RESOURCE, null, facadeClass,
				this.category, null, null, null, null);
		if (result == null) {
			throw new MissingObjectException("在[" + this.category + "]分类中找不到["
					+ facadeClass + "]类的无键（单实例）资源");
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public <TFacade> ResourceToken<TFacade> getResourceToken(
			Class<TFacade> facadeClass, Object key)
			throws MissingObjectException {
		if (facadeClass == null || key == null) {
			throw new NullPointerException();
		}
		ResourceToken<TFacade> result = this.context.internalFindResource(null,
				TransactionImpl.FIND_RESOURCE, null, facadeClass,
				this.category, key, null, null, null);
		if (result == null) {
			throw new MissingObjectException("在[" + this.category + "]分类中找不到["
					+ facadeClass + "]类的键为[" + key + "]资源");
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public <TFacade> ResourceToken<TFacade> getResourceToken(
			Class<TFacade> facadeClass, Object key1, Object key2)
			throws MissingObjectException {
		if (facadeClass == null || key1 == null || key2 == null) {
			throw new NullPointerException();
		}
		ResourceToken<TFacade> result = this.context.internalFindResource(null,
				TransactionImpl.FIND_RESOURCE, null, facadeClass,
				this.category, key1, key2, null, null);
		if (result == null) {
			throw new MissingObjectException("在[" + this.category + "]分类中找不到["
					+ facadeClass + "]类的键为[" + key1 + ", " + key2 + "]资源");
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public <TFacade> ResourceToken<TFacade> getResourceToken(
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3)
			throws MissingObjectException {
		if (facadeClass == null || key1 == null || key2 == null || key3 == null) {
			throw new NullPointerException();
		}
		ResourceToken<TFacade> result = this.context.internalFindResource(null,
				TransactionImpl.FIND_RESOURCE, null, facadeClass,
				this.category, key1, key2, key3, null);
		if (result == null) {
			throw new MissingObjectException("在[" + this.category + "]分类中找不到["
					+ facadeClass + "]类的键为[" + key1 + ", " + key2 + ", " + key3
					+ "]资源");
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public <TFacade> ResourceToken<TFacade> getResourceToken(
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3,
			Object... otherKeys) throws MissingObjectException {
		if (facadeClass == null || key1 == null || key2 == null || key3 == null
				|| otherKeys == null) {
			throw new NullPointerException();
		}
		ResourceToken<TFacade> result = this.context.internalFindResource(null,
				TransactionImpl.FIND_RESOURCE, null, facadeClass,
				this.category, key1, key2, key3, otherKeys);
		if (result == null) {
			throw new MissingObjectException("在[" + this.category + "]分类中找不到["
					+ facadeClass + "]类的键为[" + key1 + ", " + key2 + ", " + key3
					+ ", ...]资源");
		}
		return result;
	}

	public <TFacade> ResourceHandle<TFacade> lockResourceS(
			ResourceToken<TFacade> resourceToken) {
		return this.context.lockResourceS(resourceToken);
	}

	public <TFacade> ResourceHandle<TFacade> lockResourceU(
			ResourceToken<TFacade> resourceToken) {
		return this.context.lockResourceU(resourceToken);
	}

	public <TFacade> TFacade find(Class<TFacade> facadeClass)
			throws UnsupportedOperationException {
		ResourceToken<TFacade> res = this.findResourceToken(facadeClass);
		return res == null ? null : res.getFacade();
	}

	public <TFacade> TFacade find(Class<TFacade> facadeClass, Object key)
			throws UnsupportedOperationException {
		ResourceToken<TFacade> res = this.findResourceToken(facadeClass, key);
		return res == null ? null : res.getFacade();
	}

	public <TFacade> TFacade find(Class<TFacade> facadeClass, Object key1,
			Object key2) throws UnsupportedOperationException {
		ResourceToken<TFacade> res = this.findResourceToken(facadeClass, key1,
				key2);
		return res == null ? null : res.getFacade();
	}

	public <TFacade> TFacade find(Class<TFacade> facadeClass, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		ResourceToken<TFacade> res = this.findResourceToken(facadeClass, key1,
				key2, key3);
		return res == null ? null : res.getFacade();
	}

	public <TFacade> TFacade find(Class<TFacade> facadeClass, Object key1,
			Object key2, Object key3, Object... keys)
			throws UnsupportedOperationException {
		ResourceToken<TFacade> res = this.findResourceToken(facadeClass, key1,
				key2, key3, keys);
		return res == null ? null : res.getFacade();
	}

	public <TFacade> TFacade get(Class<TFacade> facadeClass)
			throws UnsupportedOperationException, MissingObjectException {
		return this.getResourceToken(facadeClass).getFacade();
	}

	public <TFacade> TFacade get(Class<TFacade> facadeClass, Object key)
			throws UnsupportedOperationException, MissingObjectException {
		return this.getResourceToken(facadeClass, key).getFacade();
	}

	public <TFacade> TFacade get(Class<TFacade> facadeClass, Object key1,
			Object key2) throws UnsupportedOperationException,
			MissingObjectException {
		return this.getResourceToken(facadeClass, key1, key2).getFacade();
	}

	public <TFacade> TFacade get(Class<TFacade> facadeClass, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException,
			MissingObjectException {
		return this.getResourceToken(facadeClass, key1, key2, key3).getFacade();
	}

	public <TFacade> TFacade get(Class<TFacade> facadeClass, Object key1,
			Object key2, Object key3, Object... keys)
			throws UnsupportedOperationException, MissingObjectException {
		return this.getResourceToken(facadeClass, key1, key2, key3, keys)
				.getFacade();
	}

	private final <TFacade> List<TFacade> internalFillResourceList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> comparator, Object key1, Object key2,
			Object key3, Object[] otherKeys) {
		this.context.checkValid();
		DnaArrayList<TFacade> result = new DnaArrayList<TFacade>();
		if (this.context.tryFillResourceList(operation, result, facadeClass,
				this.category, filter, comparator, key1, key2, key3, otherKeys) < 0) {
			throw new UnsupportedOperationException("未定义[" + facadeClass
					+ "]类型的资源服务，或者服务中没有定义相关键类型的资源提供器");
		}
		return result;
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass)
			throws UnsupportedOperationException {
		if (facadeClass == null) {
			throw new NullPointerException();
		}
		return this.internalFillResourceList(null, facadeClass, null, null,
				null, null, null, null);
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter)
			throws UnsupportedOperationException {
		if (facadeClass == null || filter == null) {
			throw new NullPointerException();
		}
		return this.internalFillResourceList(null, facadeClass, filter, null,
				null, null, null, null);
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		if (facadeClass == null || sortComparator == null) {
			throw new NullPointerException();
		}
		return this.internalFillResourceList(null, facadeClass, null,
				sortComparator, null, null, null, null);
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		if (facadeClass == null || filter == null || sortComparator == null) {
			throw new NullPointerException();
		}
		return this.internalFillResourceList(null, facadeClass, filter,
				sortComparator, null, null, null, null);
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Object key) throws UnsupportedOperationException {
		if (facadeClass == null || key == null) {
			throw new NullPointerException();
		}
		return this.internalFillResourceList(null, facadeClass, null, null,
				key, null, null, null);
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter, Object key)
			throws UnsupportedOperationException {
		if (facadeClass == null || filter == null || key == null) {
			throw new NullPointerException();
		}
		return this.internalFillResourceList(null, facadeClass, filter, null,
				key, null, null, null);
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		if (facadeClass == null || sortComparator == null || key == null) {
			throw new NullPointerException();
		}
		return this.internalFillResourceList(null, facadeClass, null,
				sortComparator, key, null, null, null);
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		if (facadeClass == null || filter == null || sortComparator == null
				|| key == null) {
			throw new NullPointerException();
		}
		return this.internalFillResourceList(null, facadeClass, filter,
				sortComparator, key, null, null, null);
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Object key1, Object key2) throws UnsupportedOperationException {
		if (facadeClass == null || key1 == null || key2 == null) {
			throw new NullPointerException();
		}
		return this.internalFillResourceList(null, facadeClass, null, null,
				key1, key2, null, null);
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter, Object key1, Object key2)
			throws UnsupportedOperationException {
		if (facadeClass == null || filter == null || key1 == null
				|| key2 == null) {
			throw new NullPointerException();
		}
		return this.internalFillResourceList(null, facadeClass, filter, null,
				key1, key2, null, null);
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		if (facadeClass == null || sortComparator == null || key1 == null
				|| key2 == null) {
			throw new NullPointerException();
		}
		return this.internalFillResourceList(null, facadeClass, null,
				sortComparator, key1, key2, null, null);
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		if (facadeClass == null || filter == null || sortComparator == null
				|| key1 == null || key2 == null) {
			throw new NullPointerException();
		}
		return this.internalFillResourceList(null, facadeClass, filter,
				sortComparator, key1, key2, null, null);
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3)
			throws UnsupportedOperationException {
		if (facadeClass == null || key1 == null || key2 == null || key3 == null) {
			throw new NullPointerException();
		}
		return this.internalFillResourceList(null, facadeClass, null, null,
				key1, key2, key3, null);
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter, Object key1, Object key2,
			Object key3) throws UnsupportedOperationException {
		if (facadeClass == null || filter == null || key1 == null
				|| key2 == null || key3 == null) {
			throw new NullPointerException();
		}
		return this.internalFillResourceList(null, facadeClass, filter, null,
				key1, key2, key3, null);
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		if (facadeClass == null || sortComparator == null || key1 == null
				|| key2 == null || key3 == null) {
			throw new NullPointerException();
		}
		return this.internalFillResourceList(null, facadeClass, null,
				sortComparator, key1, key2, key3, null);
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		if (facadeClass == null || filter == null || sortComparator == null
				|| key1 == null || key2 == null || key3 == null) {
			throw new NullPointerException();
		}
		return this.internalFillResourceList(null, facadeClass, filter,
				sortComparator, key1, key2, key3, null);
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3, Object... otherKeys) {
		if (facadeClass == null || key1 == null || key2 == null || key3 == null
				|| otherKeys == null) {
			throw new NullPointerException();
		}
		return this.internalFillResourceList(null, facadeClass, null, null,
				key1, key2, key3, otherKeys);
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter, Object key1, Object key2,
			Object key3, Object... otherKeys) {
		if (facadeClass == null || filter == null || key1 == null
				|| key2 == null || key3 == null || otherKeys == null) {
			throw new NullPointerException();
		}
		return this.internalFillResourceList(null, facadeClass, filter, null,
				key1, key2, key3, otherKeys);
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys) {
		if (facadeClass == null || sortComparator == null || key1 == null
				|| key2 == null || key3 == null || otherKeys == null) {
			throw new NullPointerException();
		}
		return this.internalFillResourceList(null, facadeClass, null,
				sortComparator, key1, key2, key3, otherKeys);
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys) {
		if (facadeClass == null || filter == null || sortComparator == null
				|| key1 == null || key2 == null || key3 == null
				|| otherKeys == null) {
			throw new NullPointerException();
		}
		return this.internalFillResourceList(null, facadeClass, filter,
				sortComparator, key1, key2, key3, otherKeys);
	}

	private final <TFacade> TreeNodeImpl<TFacade> internalGetTreeNodeFromGroup(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator) {
		this.context.checkValid();
		TreeNodeImpl<TFacade> root = new TreeNodeImpl<TFacade>(null, null);
		if (this.context.internalFillResTreeNodeFromGroup(operation, root,
				facadeClass, this.category, filter, sortComparator) < 0) {
			throw new UnsupportedOperationException("查找不到[" + facadeClass
					+ "]类型的资源服务");
		}
		return root;
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass)
			throws UnsupportedOperationException {
		if (facadeClass == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromGroup(null, facadeClass, null, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter)
			throws UnsupportedOperationException {
		if (facadeClass == null || filter == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromGroup(null, facadeClass, filter,
				null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		if (facadeClass == null || sortComparator == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromGroup(null, facadeClass, null,
				sortComparator);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		if (facadeClass == null || filter == null || sortComparator == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromGroup(null, facadeClass, filter,
				sortComparator);
	}

	private final <TFacade> TreeNodeImpl<TFacade> internalGetTreeNodeFromItem(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object[] otherKeys) {
		this.context.checkValid();
		TreeNodeImpl<TFacade> root = new TreeNodeImpl<TFacade>(null, null);
		if (this.context.internalFillResTreeNodeFromItem(operation, root,
				facadeClass, this.category, filter, sortComparator, key1, key2,
				key3, otherKeys) < 0) {
			throw new UnsupportedOperationException("没有定义[" + facadeClass
					+ "]类型的资源服务， 或者相应的服务中没有定义相关键类型的资源提供器");
		}
		return root;
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			Object key) throws UnsupportedOperationException {
		if (facadeClass == null || key == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(null, facadeClass, null, null,
				key, null, null, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter, Object key)
			throws UnsupportedOperationException {
		if (facadeClass == null || filter == null || key == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(null, facadeClass, filter,
				null, key, null, null, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		if (facadeClass == null || sortComparator == null || key == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(null, facadeClass, null,
				sortComparator, key, null, null, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		if (facadeClass == null || filter == null || sortComparator == null
				|| key == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(null, facadeClass, filter,
				sortComparator, key, null, null, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			Object key1, Object key2) throws UnsupportedOperationException {
		if (facadeClass == null || key1 == null || key2 == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(null, facadeClass, null, null,
				key1, key2, null, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter, Object key1, Object key2)
			throws UnsupportedOperationException {
		if (facadeClass == null || filter == null || key1 == null
				|| key2 == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(null, facadeClass, filter,
				null, key1, key2, null, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		if (facadeClass == null || sortComparator == null || key1 == null
				|| key2 == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(null, facadeClass, null,
				sortComparator, key1, key2, null, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		if (facadeClass == null || filter == null || sortComparator == null
				|| key1 == null || key2 == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(null, facadeClass, filter,
				sortComparator, key1, key2, null, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3)
			throws UnsupportedOperationException {
		if (facadeClass == null || key1 == null || key2 == null || key3 == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(null, facadeClass, null, null,
				key1, key2, key3, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter, Object key1, Object key2,
			Object key3) throws UnsupportedOperationException {
		if (facadeClass == null || filter == null || key1 == null
				|| key2 == null || key3 == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(null, facadeClass, filter,
				null, key1, key2, key3, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		if (facadeClass == null || sortComparator == null || key1 == null
				|| key2 == null || key3 == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(null, facadeClass, null,
				sortComparator, key1, key2, key3, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		if (facadeClass == null || filter == null || sortComparator == null
				|| key1 == null || key2 == null || key3 == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(null, facadeClass, filter,
				sortComparator, key1, key2, key3, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		if (facadeClass == null || key1 == null || key2 == null || key3 == null
				|| otherKeys == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(null, facadeClass, null, null,
				key1, key2, key3, otherKeys);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter, Object key1, Object key2,
			Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		if (facadeClass == null || filter == null || key1 == null
				|| key2 == null || key3 == null || otherKeys == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(null, facadeClass, filter,
				null, key1, key2, key3, otherKeys);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		if (facadeClass == null || sortComparator == null || key1 == null
				|| key2 == null || key3 == null || otherKeys == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(null, facadeClass, null,
				sortComparator, key1, key2, key3, otherKeys);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		if (facadeClass == null || filter == null || sortComparator == null
				|| key1 == null || key2 == null || key3 == null
				|| otherKeys == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(null, facadeClass, filter,
				sortComparator, key1, key2, key3, otherKeys);
	}

	/* --------------------- Update Methods -------------------------- */

	private final <TKey1, TKey2, TKey3> void internalInvalidResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2,
			TKey3 key3, Object[] keys) throws DeadLockException {
		this.context.internalFindResource(operation,
				TransactionImpl.INVALID_RESOURCE, ResourceDemandFor.INVALID,
				this.context.occorAtResourceService.facadeClass, this.category,
				key1, key2, key3, keys);
	}

	public void invalidResource() throws DeadLockException {
		this.internalInvalidResource(null, null, null, null, null);
	}

	public <TKey> void invalidResource(TKey key) throws DeadLockException {
		if (key == null) {
			throw new NullPointerException();
		}
		this.internalInvalidResource(null, key, null, null, null);
	}

	public <TKey1, TKey2, TKey3> void invalidResource(TKey1 key1, TKey2 key2,
			TKey3 key3, Object... keys) throws DeadLockException {
		if (key1 == null || key2 == null || key3 == null || keys == null) {
			throw new NullPointerException();
		}
		this.internalInvalidResource(null, key1, key2, key3, keys);
	}

	public <TKey1, TKey2, TKey3> void invalidResource(TKey1 key1, TKey2 key2,
			TKey3 key3) throws DeadLockException {
		if (key1 == null || key2 == null || key3 == null) {
			throw new NullPointerException();
		}
		this.internalInvalidResource(null, key1, key2, key3, null);
	}

	public <TKey1, TKey2> void invalidResource(TKey1 key1, TKey2 key2)
			throws DeadLockException {
		if (key1 == null || key2 == null) {
			throw new NullPointerException();
		}
		this.internalInvalidResource(null, key1, key2, null, null);
	}

	@SuppressWarnings("unchecked")
	private final <TKey1, TKey2, TKey3> TImplM internalModifyResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2,
			TKey3 key3, Object[] keys) throws DeadLockException {
		ResourceHandleImpl<?, TImplM, ?> handle = this.context
				.internalFindResource(operation,
						TransactionImpl.MODIFY_RESOURCE,
						ResourceDemandFor.MODIFY,
						this.context.occorAtResourceService.facadeClass,
						this.category, key1, key2, key3, keys);
		if (handle.res == null) {
			throw new MissingObjectException(ServiceInvokeeBase
					.noResourceException(
							this.context.occorAtResourceService.facadeClass,
							key1, key2, key3, keys).getMessage()); // XXX
		}
		return this.context.internalModifyLockedResource(handle);
	}

	public TImplM modifyResource() throws DeadLockException {
		return this.internalModifyResource(null, null, null, null, null);
	}

	public <TKey> TImplM modifyResource(TKey key) throws DeadLockException {
		if (key == null) {
			throw new NullPointerException();
		}
		return this.internalModifyResource(null, key, null, null, null);
	}

	public <TKey1, TKey2, TKey3> TImplM modifyResource(TKey1 key1, TKey2 key2,
			TKey3 key3, Object... keys) throws DeadLockException {
		if (key1 == null || key2 == null || key3 == null || keys == null) {
			throw new NullPointerException();
		}
		return this.internalModifyResource(null, key1, key2, key3, keys);
	}

	public <TKey1, TKey2, TKey3> TImplM modifyResource(TKey1 key1, TKey2 key2,
			TKey3 key3) throws DeadLockException {
		if (key1 == null || key2 == null || key3 == null) {
			throw new NullPointerException();
		}
		return this.internalModifyResource(null, key1, key2, key3, null);
	}

	public <TKey1, TKey2> TImplM modifyResource(TKey1 key1, TKey2 key2)
			throws DeadLockException {
		if (key1 == null || key2 == null) {
			throw new NullPointerException();
		}
		return this.internalModifyResource(null, key1, key2, null, null);
	}

	/**
	 * 克隆资源
	 * 
	 * @param tryReuse
	 *            尝试被重用的实例（减少对象创建成本）
	 */
	public final TImplM cloneResource(ResourceToken<TFacadeM> token,
			TImplM tryReuse) {
		return this.context.internalCloneResource(null, token, tryReuse,
				this.category);
	}

	/**
	 * 克隆资源
	 */
	public final TImplM cloneResource(ResourceToken<TFacadeM> token) {
		return this.context.internalCloneResource(null, token, null,
				this.category);
	}

	public void postModifiedResource(TImplM modifiedResource) {
		this.context.postModifiedResource(modifiedResource);
	}

	@SuppressWarnings("unchecked")
	private final <TKey1, TKey2, TKey3> TImplM internalRemoveResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2,
			TKey3 key3, Object[] keys) throws DeadLockException {
		return (TImplM) this.context.internalFindResource(operation,
				TransactionImpl.REMOVE_RESOURCE, null,
				this.context.occorAtResourceService.facadeClass, this.category,
				key1, key2, key3, keys);
	}

	public TImplM removeResource() throws DeadLockException {
		return this.internalRemoveResource(null, null, null, null, null);
	}

	public <TKey> TImplM removeResource(TKey key) throws DeadLockException {
		if (key == null) {
			throw new NullPointerException();
		}
		return this.internalRemoveResource(null, key, null, null, null);
	}

	public <TKey1, TKey2, TKey3> TImplM removeResource(TKey1 key1, TKey2 key2,
			TKey3 key3, Object... keys) throws DeadLockException {
		if (key1 == null || key2 == null || key3 == null || keys == null) {
			throw new NullPointerException();
		}
		return this.internalRemoveResource(null, key1, key2, key3, keys);
	}

	public <TKey1, TKey2, TKey3> TImplM removeResource(TKey1 key1, TKey2 key2,
			TKey3 key3) throws DeadLockException {
		if (key1 == null || key2 == null || key3 == null) {
			throw new NullPointerException();
		}
		return this.internalRemoveResource(null, key1, key2, key3, null);
	}

	public <TKey1, TKey2> TImplM removeResource(TKey1 key1, TKey2 key2)
			throws DeadLockException {
		if (key1 == null || key2 == null) {
			throw new NullPointerException();
		}
		return this.internalRemoveResource(null, key1, key2, null, null);
	}

	@SuppressWarnings("unchecked")
	public ResourceToken<TFacadeM> putResource(TImplM resource) {
		return this.putResource(resource, (TKeysHolderM) resource,
				WhenExists.REPLACE);
	}

	public ResourceToken<TFacadeM> putResource(TImplM resource,
			TKeysHolderM keys) {
		return this.putResource(resource, keys, WhenExists.REPLACE);
	}

	public ResourceToken<TFacadeM> putResource(TImplM resource,
			TKeysHolderM keys, WhenExists policy) {
		if (resource == null || keys == null || policy == null) {
			throw new NullPointerException();
		}
		return this.context.internalPutResource(this.category, null, resource,
				keys, policy);
	}

	@SuppressWarnings("unchecked")
	public ResourceToken<TFacadeM> putResource(
			ResourceToken<TFacadeM> treeParent, TImplM resource) {
		return this.putResource(treeParent, resource, (TKeysHolderM) resource,
				WhenExists.REPLACE);
	}

	public ResourceToken<TFacadeM> putResource(
			ResourceToken<TFacadeM> treeParent, TImplM resource,
			TKeysHolderM keys) {
		return this.putResource(treeParent, resource, keys, WhenExists.REPLACE);
	}

	public ResourceToken<TFacadeM> putResource(
			ResourceToken<TFacadeM> treeParent, TImplM resource,
			TKeysHolderM keys, WhenExists policy) {
		if (resource == null || keys == null || policy == null) {
			throw new NullPointerException();
		}
		return this.context.internalPutResource(this.category, treeParent,
				resource, keys, policy);
	}

	public void putResource(ResourceToken<TFacadeM> treeParent,
			ResourceToken<TFacadeM> child) {
		this.context.putResource(treeParent, child);
	}

	public <THolderFacade> void putResourceReference(
			ResourceToken<THolderFacade> holder,
			ResourceToken<TFacadeM> reference) {
		this.context.putResourceReference(holder, reference);
	}

	public <TReferenceFacade> void putResourceReferenceBy(
			ResourceToken<TFacadeM> holder,
			ResourceToken<TReferenceFacade> reference) {
		this.context.putResourceReferenceBy(holder, reference);
	}

	public final <THolderFacade> void removeResourceReference(
			ResourceToken<THolderFacade> holder,
			ResourceToken<TFacadeM> reference) {
		this.context.removeResourceReference(holder, reference);
	}

	public final <TReferenceFacade> void removeResourceReferenceBy(
			ResourceToken<TFacadeM> holder,
			ResourceToken<TReferenceFacade> reference) {
		this.context.removeResourceReferenceBy(holder, reference);
	}

	// ---------------------------------以下权限相关-----------------------------------------------------

	public TImplM cloneResource(Operation<? super TFacadeM> operation,
			ResourceToken<TFacadeM> token) {
		return this.context.internalCloneResource(operation, token, null,
				this.category);
	}

	public TImplM cloneResource(Operation<? super TFacadeM> operation,
			ResourceToken<TFacadeM> token, TImplM tryReuse) {
		return this.context.internalCloneResource(operation, token, tryReuse,
				this.category);
	}

	public void invalidResource(Operation<? super TFacadeM> operation)
			throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		this.internalInvalidResource(operation, null, null, null, null);
	}

	public <TKey> void invalidResource(Operation<? super TFacadeM> operation,
			TKey key) throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key == null) {
			throw new NullPointerException();
		}
		this.internalInvalidResource(operation, key, null, null, null);
	}

	public <TKey1, TKey2> void invalidResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2)
			throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null || key2 == null) {
			throw new NullPointerException();
		}
		this.internalInvalidResource(operation, key1, key2, null, null);
	}

	public <TKey1, TKey2, TKey3> void invalidResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2,
			TKey3 key3) throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null || key2 == null || key3 == null) {
			throw new NullPointerException();
		}
		this.internalInvalidResource(operation, key1, key2, key3, null);
	}

	public <TKey1, TKey2, TKey3> void invalidResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2,
			TKey3 key3, Object... keys) throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null || key2 == null || key3 == null || keys == null) {
			throw new NullPointerException();
		}
		this.internalInvalidResource(operation, key1, key2, key3, keys);
	}

	public TImplM modifyResource(Operation<? super TFacadeM> operation)
			throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		return this.internalModifyResource(operation, null, null, null, null);
	}

	public <TKey> TImplM modifyResource(Operation<? super TFacadeM> operation,
			TKey key) throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key == null) {
			throw new NullPointerException();
		}
		return this.internalModifyResource(operation, key, null, null, null);
	}

	public <TKey1, TKey2> TImplM modifyResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2)
			throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null || key2 == null) {
			throw new NullPointerException();
		}
		return this.internalModifyResource(operation, key1, key2, null, null);
	}

	public <TKey1, TKey2, TKey3> TImplM modifyResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2,
			TKey3 key3) throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null || key2 == null || key3 == null) {
			throw new NullPointerException();
		}
		return this.internalModifyResource(operation, key1, key2, key3, null);
	}

	public <TKey1, TKey2, TKey3> TImplM modifyResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2,
			TKey3 key3, Object... keys) throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null || key2 == null || key3 == null || keys == null) {
			throw new NullPointerException();
		}
		return this.internalModifyResource(operation, key1, key2, key3, keys);
	}

	public TImplM removeResource(Operation<? super TFacadeM> operation)
			throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		return this.internalRemoveResource(operation, null, null, null, null);
	}

	public <TKey> TImplM removeResource(Operation<? super TFacadeM> operation,
			TKey key) throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key == null) {
			throw new NullPointerException();
		}
		return this.internalRemoveResource(operation, key, null, null, null);
	}

	public <TKey1, TKey2> TImplM removeResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2)
			throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null || key2 == null) {
			throw new NullPointerException();
		}
		return this.internalRemoveResource(operation, key1, key2, null, null);
	}

	public <TKey1, TKey2, TKey3> TImplM removeResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2,
			TKey3 key3) throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null || key2 == null || key3 == null) {
			throw new NullPointerException();
		}
		return this.internalRemoveResource(operation, key1, key2, key3, null);
	}

	public <TKey1, TKey2, TKey3> TImplM removeResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2,
			TKey3 key3, Object... keys) throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null || key2 == null || key3 == null || keys == null) {
			throw new NullPointerException();
		}
		return this.internalRemoveResource(operation, key1, key2, key3, keys);
	}

	@SuppressWarnings("unchecked")
	public <TFacade> ResourceToken<TFacade> findResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null) {
			throw new NullPointerException();
		}
		return this.context.internalFindResource(operation,
				TransactionImpl.FIND_RESOURCE, null, facadeClass,
				this.category, null, null, null, null);
	}

	@SuppressWarnings("unchecked")
	public <TFacade> ResourceToken<TFacade> findResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || key == null) {
			throw new NullPointerException();
		}
		return this.context.internalFindResource(operation,
				TransactionImpl.FIND_RESOURCE, null, facadeClass,
				this.category, key, null, null, null);
	}

	@SuppressWarnings("unchecked")
	public <TFacade> ResourceToken<TFacade> findResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || key1 == null || key2 == null) {
			throw new NullPointerException();
		}
		return this.context.internalFindResource(operation,
				TransactionImpl.FIND_RESOURCE, null, facadeClass,
				this.category, key1, key2, null, null);
	}

	@SuppressWarnings("unchecked")
	public <TFacade> ResourceToken<TFacade> findResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || key1 == null || key2 == null || key3 == null) {
			throw new NullPointerException();
		}
		return this.context.internalFindResource(operation,
				TransactionImpl.FIND_RESOURCE, null, facadeClass,
				this.category, key1, key2, key3, null);
	}

	@SuppressWarnings("unchecked")
	public <TFacade> ResourceToken<TFacade> findResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3, Object... otherKeys) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || key1 == null || key2 == null || key3 == null
				|| otherKeys == null) {
			throw new NullPointerException();
		}
		return this.context.internalFindResource(operation,
				TransactionImpl.FIND_RESOURCE, null, facadeClass,
				this.category, key1, key2, key3, otherKeys);
	}

	public <TFacade, THolderFacade> List<TFacade> getResourceReferences(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			ResourceToken<THolderFacade> holderToken) {
		return this.context.getResourceReferences(operation, facadeClass,
				holderToken);
	}

	public <TFacade, THolderFacade> List<TFacade> getResourceReferences(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			ResourceToken<THolderFacade> holderToken,
			Filter<? super TFacade> filter) {
		return this.context.getResourceReferences(operation, facadeClass,
				holderToken, filter);
	}

	public <TFacade, THolderFacade> List<TFacade> getResourceReferences(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			ResourceToken<THolderFacade> holderToken,
			Comparator<? super TFacade> sortComparator) {
		return this.context.getResourceReferences(operation, facadeClass,
				holderToken, sortComparator);
	}

	public <TFacade, THolderFacade> List<TFacade> getResourceReferences(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			ResourceToken<THolderFacade> holderToken,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator) {
		return this.context.getResourceReferences(operation, facadeClass,
				holderToken, filter, sortComparator);
	}

	public <TFacade> ResourceToken<TFacade> getResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass)
			throws MissingObjectException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null) {
			throw new NullPointerException();
		}
		@SuppressWarnings("unchecked")
		ResourceToken<TFacade> result = this.context.internalFindResource(
				operation, TransactionImpl.FIND_RESOURCE, null, facadeClass,
				this.category, null, null, null, null);
		if (result == null) {
			throw new MissingObjectException("在[" + this.category + "]分类中找不到["
					+ facadeClass + "]类的无键（单实例）资源");
		}
		return result;
	}

	public <TFacade> ResourceToken<TFacade> getResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key) throws MissingObjectException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || key == null) {
			throw new NullPointerException();
		}
		@SuppressWarnings("unchecked")
		ResourceToken<TFacade> result = this.context.internalFindResource(
				operation, TransactionImpl.FIND_RESOURCE, null, facadeClass,
				this.category, key, null, null, null);
		if (result == null) {
			throw new MissingObjectException("在[" + this.category + "]分类中找不到["
					+ facadeClass + "]类的键为[" + key + "]资源");
		}
		return result;
	}

	public <TFacade> ResourceToken<TFacade> getResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2) throws MissingObjectException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || key1 == null || key2 == null) {
			throw new NullPointerException();
		}
		@SuppressWarnings("unchecked")
		ResourceToken<TFacade> result = this.context.internalFindResource(
				operation, TransactionImpl.FIND_RESOURCE, null, facadeClass,
				this.category, key1, key2, null, null);
		if (result == null) {
			throw new MissingObjectException("在[" + this.category + "]分类中找不到["
					+ facadeClass + "]类的键为[" + key1 + ", " + key2 + "]资源");
		}
		return result;
	}

	public <TFacade> ResourceToken<TFacade> getResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3)
			throws MissingObjectException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || key1 == null || key2 == null || key3 == null) {
			throw new NullPointerException();
		}
		@SuppressWarnings("unchecked")
		ResourceToken<TFacade> result = this.context.internalFindResource(
				operation, TransactionImpl.FIND_RESOURCE, null, facadeClass,
				this.category, key1, key2, key3, null);
		if (result == null) {
			throw new MissingObjectException("在[" + this.category + "]分类中找不到["
					+ facadeClass + "]类的键为[" + key1 + ", " + key2 + ", " + key3
					+ "]资源");
		}
		return result;
	}

	public <TFacade> ResourceToken<TFacade> getResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3, Object... otherKeys)
			throws MissingObjectException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || key1 == null || key2 == null || key3 == null
				|| otherKeys == null) {
			throw new NullPointerException();
		}
		@SuppressWarnings("unchecked")
		ResourceToken<TFacade> result = this.context.internalFindResource(
				operation, TransactionImpl.FIND_RESOURCE, null, facadeClass,
				this.category, key1, key2, key3, otherKeys);
		if (result == null) {
			throw new MissingObjectException("在[" + this.category + "]分类中找不到["
					+ facadeClass + "]类的键为[" + key1 + ", " + key2 + ", " + key3
					+ ", ...]资源");
		}
		return result;
	}

	public <TFacade> TFacade find(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass) throws UnsupportedOperationException {
		ResourceToken<TFacade> res = this.findResourceToken(operation,
				facadeClass);
		return res == null ? null : res.getFacade();
	}

	public <TFacade> TFacade find(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass, Object key)
			throws UnsupportedOperationException {
		ResourceToken<TFacade> res = this.findResourceToken(operation,
				facadeClass, key);
		return res == null ? null : res.getFacade();
	}

	public <TFacade> TFacade find(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass, Object key1, Object key2)
			throws UnsupportedOperationException {
		ResourceToken<TFacade> res = this.findResourceToken(operation,
				facadeClass, key1, key2);
		return res == null ? null : res.getFacade();
	}

	public <TFacade> TFacade find(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3)
			throws UnsupportedOperationException {
		ResourceToken<TFacade> res = this.findResourceToken(operation,
				facadeClass, key1, key2, key3);
		return res == null ? null : res.getFacade();
	}

	public <TFacade> TFacade find(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3,
			Object... keys) throws UnsupportedOperationException {
		ResourceToken<TFacade> res = this.findResourceToken(operation,
				facadeClass, key1, key2, key3, keys);
		return res == null ? null : res.getFacade();
	}

	public <TFacade> TFacade get(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass) throws UnsupportedOperationException,
			MissingObjectException {
		return this.getResourceToken(operation, facadeClass).getFacade();
	}

	public <TFacade> TFacade get(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass, Object key)
			throws UnsupportedOperationException, MissingObjectException {
		return this.getResourceToken(operation, facadeClass, key).getFacade();
	}

	public <TFacade> TFacade get(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass, Object key1, Object key2)
			throws UnsupportedOperationException, MissingObjectException {
		return this.getResourceToken(operation, facadeClass, key1, key2)
				.getFacade();
	}

	public <TFacade> TFacade get(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3)
			throws UnsupportedOperationException, MissingObjectException {
		return this.getResourceToken(operation, facadeClass, key1, key2, key3)
				.getFacade();
	}

	public <TFacade> TFacade get(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3,
			Object... keys) throws UnsupportedOperationException,
			MissingObjectException {
		return this.getResourceToken(operation, facadeClass, key1, key2, key3,
				keys).getFacade();
	}

	public <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null) {
			throw new NullPointerException();
		}
		return this.internalFillResourceList(operation, facadeClass, null,
				null, null, null, null, null);
	}

	public <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key) throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || key == null) {
			throw new NullPointerException();
		}
		return this.internalFillResourceList(operation, facadeClass, null,
				null, key, null, null, null);
	}

	public <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2) throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || key1 == null || key2 == null) {
			throw new NullPointerException();
		}
		return this.internalFillResourceList(operation, facadeClass, null,
				null, key1, key2, null, null);
	}

	public <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || key1 == null || key2 == null || key3 == null) {
			throw new NullPointerException();
		}
		return this.internalFillResourceList(operation, facadeClass, null,
				null, key1, key2, key3, null);
	}

	public <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3, Object... otherKeys) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || key1 == null || key2 == null || key3 == null
				|| otherKeys == null) {
			throw new NullPointerException();
		}
		return this.internalFillResourceList(operation, facadeClass, null,
				null, key1, key2, key3, otherKeys);
	}

	public <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Filter<? super TFacade> filter)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || filter == null) {
			throw new NullPointerException();
		}
		return this.internalFillResourceList(operation, facadeClass, filter,
				null, null, null, null, null);
	}

	public <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Filter<? super TFacade> filter, Object key)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || filter == null || key == null) {
			throw new NullPointerException();
		}
		return this.internalFillResourceList(operation, facadeClass, filter,
				null, key, null, null, null);
	}

	public <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Filter<? super TFacade> filter, Object key1, Object key2)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || filter == null || key1 == null
				|| key2 == null) {
			throw new NullPointerException();
		}
		return this.internalFillResourceList(operation, facadeClass, filter,
				null, key1, key2, null, null);
	}

	public <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Filter<? super TFacade> filter, Object key1, Object key2,
			Object key3) throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || filter == null || key1 == null
				|| key2 == null || key3 == null) {
			throw new NullPointerException();
		}
		return this.internalFillResourceList(operation, facadeClass, filter,
				null, key1, key2, key3, null);
	}

	public <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Filter<? super TFacade> filter, Object key1, Object key2,
			Object key3, Object... otherKeys) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || filter == null || key1 == null
				|| key2 == null || key3 == null || otherKeys == null) {
			throw new NullPointerException();
		}
		return this.internalFillResourceList(operation, facadeClass, filter,
				null, key1, key2, key3, otherKeys);
	}

	public <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || sortComparator == null) {
			throw new NullPointerException();
		}
		return this.internalFillResourceList(operation, facadeClass, null,
				sortComparator, null, null, null, null);
	}

	public <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || sortComparator == null || key == null) {
			throw new NullPointerException();
		}
		return this.internalFillResourceList(operation, facadeClass, null,
				sortComparator, key, null, null, null);
	}

	public <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || sortComparator == null || key1 == null
				|| key2 == null) {
			throw new NullPointerException();
		}
		return this.internalFillResourceList(operation, facadeClass, null,
				sortComparator, key1, key2, null, null);
	}

	public <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || sortComparator == null || key1 == null
				|| key2 == null || key3 == null) {
			throw new NullPointerException();
		}
		return this.internalFillResourceList(operation, facadeClass, null,
				sortComparator, key1, key2, key3, null);
	}

	public <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || sortComparator == null || key1 == null
				|| key2 == null || key3 == null || otherKeys == null) {
			throw new NullPointerException();
		}
		return this.internalFillResourceList(operation, facadeClass, null,
				sortComparator, key1, key2, key3, otherKeys);
	}

	public <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || filter == null || sortComparator == null) {
			throw new NullPointerException();
		}
		return this.internalFillResourceList(operation, facadeClass, filter,
				sortComparator, null, null, null, null);
	}

	public <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || filter == null || sortComparator == null
				|| key == null) {
			throw new NullPointerException();
		}
		return this.internalFillResourceList(operation, facadeClass, filter,
				sortComparator, key, null, null, null);
	}

	public <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || filter == null || sortComparator == null
				|| key1 == null || key2 == null) {
			throw new NullPointerException();
		}
		return this.internalFillResourceList(operation, facadeClass, filter,
				sortComparator, key1, key2, null, null);
	}

	public <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || filter == null || sortComparator == null
				|| key1 == null || key2 == null || key3 == null) {
			throw new NullPointerException();
		}
		return this.internalFillResourceList(operation, facadeClass, filter,
				sortComparator, key1, key2, key3, null);
	}

	public <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || filter == null || sortComparator == null
				|| key1 == null || key2 == null || key3 == null
				|| otherKeys == null) {
			throw new NullPointerException();
		}
		return this.internalFillResourceList(operation, facadeClass, filter,
				sortComparator, key1, key2, key3, otherKeys);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromGroup(operation, facadeClass, null,
				null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key) throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || key == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(operation, facadeClass, null,
				null, key, null, null, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2) throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || key1 == null || key2 == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(operation, facadeClass, null,
				null, key1, key2, null, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || key1 == null || key2 == null || key3 == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(operation, facadeClass, null,
				null, key1, key2, key3, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || key1 == null || key2 == null || key3 == null
				|| otherKeys == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(operation, facadeClass, null,
				null, key1, key2, key3, otherKeys);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || filter == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromGroup(operation, facadeClass,
				filter, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter, Object key)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || filter == null || key == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(operation, facadeClass, filter,
				null, key, null, null, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter, Object key1, Object key2)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || filter == null || key1 == null
				|| key2 == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(operation, facadeClass, filter,
				null, key1, key2, null, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter, Object key1, Object key2,
			Object key3) throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || filter == null || key1 == null
				|| key2 == null || key3 == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(operation, facadeClass, filter,
				null, key1, key2, key3, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter, Object key1, Object key2,
			Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || filter == null || key1 == null
				|| key2 == null || key3 == null || otherKeys == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(operation, facadeClass, filter,
				null, key1, key2, key3, otherKeys);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || sortComparator == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromGroup(operation, facadeClass, null,
				sortComparator);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || sortComparator == null || key == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(operation, facadeClass, null,
				sortComparator, key, null, null, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || sortComparator == null || key1 == null
				|| key2 == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(operation, facadeClass, null,
				sortComparator, key1, key2, null, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || sortComparator == null || key1 == null
				|| key2 == null || key3 == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(operation, facadeClass, null,
				sortComparator, key1, key2, key3, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || sortComparator == null || key1 == null
				|| key2 == null || key3 == null || otherKeys == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(operation, facadeClass, null,
				sortComparator, key1, key2, key3, otherKeys);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || filter == null || sortComparator == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromGroup(operation, facadeClass,
				filter, sortComparator);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || filter == null || sortComparator == null
				|| key == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(operation, facadeClass, filter,
				sortComparator, key, null, null, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || filter == null || sortComparator == null
				|| key1 == null || key2 == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(operation, facadeClass, filter,
				sortComparator, key1, key2, null, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || filter == null || sortComparator == null
				|| key1 == null || key2 == null || key3 == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(operation, facadeClass, filter,
				sortComparator, key1, key2, key3, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || filter == null || sortComparator == null
				|| key1 == null || key2 == null || key3 == null
				|| otherKeys == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(operation, facadeClass, filter,
				sortComparator, key1, key2, key3, otherKeys);
	}

	public <THolderFacade> void removeResourceReference(
			Operation<? super TFacadeM> operation,
			ResourceToken<THolderFacade> holder,
			ResourceToken<TFacadeM> reference) {
		this.context.removeResourceReference(operation, holder, reference);
	}

	public final <TReferenceFacade> void removeResourceReferenceBy(
			Operation<? super TReferenceFacade> operation,
			ResourceToken<TFacadeM> holder,
			ResourceToken<TReferenceFacade> reference) {
		this.context.removeResourceReferenceBy(operation, holder, reference);
	}

	// ---------------------------------以上权限相关-----------------------------------------------------

}
