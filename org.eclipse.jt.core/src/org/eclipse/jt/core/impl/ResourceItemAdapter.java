/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ResourceItemAdapter.java
 * Date 2009-1-16
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.auth.Operation;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.resource.ResourceInserter;
import org.eclipse.jt.core.resource.ResourceToken;
import org.eclipse.jt.core.resource.ResourceService.WhenExists;

/**
 * 为ResourceItem对象提供适配功能，使其能够支持ResourceInserter接口。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class ResourceItemAdapter<TFacade, TImpl extends TFacade, TKeysHolder>
		implements ResourceInserter<TFacade, TImpl, TKeysHolder> {

	final TransactionImpl transaction;
	final ResourceItem<TFacade, TImpl, TKeysHolder> item;

	ResourceItemAdapter(TransactionImpl transaction,
			ResourceItem<TFacade, TImpl, TKeysHolder> wrappee) {
		if (transaction == null) {
			throw new NullArgumentException("transaction");
		}
		if (wrappee == null) {
			throw new NullArgumentException("wrappee");
		}
		this.transaction = transaction;
		this.item = wrappee;
	}

	public final <TOwnerFacade> ResourceItem<TOwnerFacade, ?, ?> getOwnerResource(
			Class<TOwnerFacade> ownerFacadeClass) {
		return this.item.getOwnerResource(ownerFacadeClass);
	}

	public final Object getCategory() {
		return this.item.getCategory();
	}

	@SuppressWarnings("unchecked")
	public final ResourceItem<TFacade, TImpl, TKeysHolder> putResource(
			TImpl resource) {
		return this.item.putResource(resource, (TKeysHolder) resource,
				WhenExists.REPLACE);
	}

	public final ResourceItem<TFacade, TImpl, TKeysHolder> putResource(
			TImpl resource, TKeysHolder keys) {
		return this.item.putResource(resource, keys, WhenExists.REPLACE);
	}

	@SuppressWarnings("unchecked")
	public final ResourceItem<TFacade, TImpl, TKeysHolder> putResource(
			ResourceToken<TFacade> treeParent, TImpl resource) {
		return this.putResource(treeParent, resource, (TKeysHolder) resource);
	}

	public final ResourceItem<TFacade, TImpl, TKeysHolder> putResource(
			ResourceToken<TFacade> treeParent, TImpl resource, TKeysHolder keys) {
		return this.item.putResource(this.transaction, treeParent, resource,
				keys);
	}

	public final void putResource(ResourceToken<TFacade> treeParent,
			ResourceToken<TFacade> child) {
		this.item.putResource(this.transaction, treeParent, child);
	}

	@SuppressWarnings("unchecked")
	public final <THolderFacade> void putResourceReference(
			ResourceToken<THolderFacade> holder,
			ResourceToken<TFacade> reference) {
		((ResourceItem<THolderFacade, ?, ?>) holder).putReference(
				this.transaction, (ResourceItem<TFacade, ?, ?>) reference);
	}

	@SuppressWarnings("unchecked")
	public final <TReferenceFacade> void putResourceReferenceBy(
			ResourceToken<TFacade> holder,
			ResourceToken<TReferenceFacade> reference) {
		((ResourceItem<TFacade, ?, ?>) holder).putReference(this.transaction,
				(ResourceItem<TReferenceFacade, ?, ?>) reference);
	}

	@SuppressWarnings("unchecked")
	public final <THolderFacade> void removeResourceReference(
			ResourceToken<THolderFacade> holder,
			ResourceToken<TFacade> reference) {
		((ResourceItem<THolderFacade, ?, ?>) holder).removeReference(
				this.transaction, (ResourceItem<TFacade, ?, ?>) reference);
	}

	@SuppressWarnings("unchecked")
	public final <TReferenceFacade> void removeResourceReferenceBy(
			ResourceToken<TFacade> holder,
			ResourceToken<TReferenceFacade> reference) {
		((ResourceItem<TFacade, ?, ?>) holder).removeReference(
				this.transaction,
				(ResourceItem<TReferenceFacade, ?, ?>) reference);
	}

	// ------------------------------以下权限相关-------------------------------------------
	@SuppressWarnings("unchecked")
	public final <THolderFacade> void removeResourceReference(
			Operation<? super TFacade> operation,
			ResourceToken<THolderFacade> holder,
			ResourceToken<TFacade> reference) {
		((ResourceItem<THolderFacade, ?, ?>) holder).removeReference(operation,
				this.transaction, (ResourceItem<TFacade, ?, ?>) reference);
	}

	@SuppressWarnings("unchecked")
	public final <TReferenceFacade> void removeResourceReferenceBy(
			Operation<? super TReferenceFacade> operation,
			ResourceToken<TFacade> holder,
			ResourceToken<TReferenceFacade> reference) {
		((ResourceItem<TFacade, ?, ?>) holder).removeReference(operation,
				this.transaction,
				(ResourceItem<TReferenceFacade, ?, ?>) reference);
	}

	// ------------------------------以上权限相关-------------------------------------------
}
