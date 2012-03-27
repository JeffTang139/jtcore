/**
 * Copyright (C) 2007-2008 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ResourceInserterImpl.java
 * Date 2008-9-18
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.auth.Operation;
import org.eclipse.jt.core.resource.ResourceInserter;
import org.eclipse.jt.core.resource.ResourceToken;
import org.eclipse.jt.core.resource.ResourceService.WhenExists;

/**
 * 为ResourceGroup对象提供适配功能，使其能够支持ResourceInserter接口。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class ResourceGroupAdapter<TFacade, TImpl extends TFacade, TKeysHolder>
		implements ResourceInserter<TFacade, TImpl, TKeysHolder> {

	final ContextImpl<TFacade, TImpl, TKeysHolder> context;
	final ResourceGroup<TFacade, TImpl, TKeysHolder> group;

	ResourceGroupAdapter(ContextImpl<TFacade, TImpl, TKeysHolder> context,
			ResourceGroup<TFacade, TImpl, TKeysHolder> wrappee) {
		if (context == null || wrappee == null) {
			throw new NullPointerException();
		}
		this.context = context;
		this.group = wrappee;
	}

	public final <TOwnerFacade> ResourceItem<TOwnerFacade, ?, ?> getOwnerResource(
			Class<TOwnerFacade> ownerFacadeClass) {
		return this.group.getOwnerResource(ownerFacadeClass);
	}

	@SuppressWarnings("unchecked")
	public final ResourceToken<TFacade> putResource(TImpl resource) {
		return this.putResource(resource, (TKeysHolder) resource);
	}

	public final ResourceToken<TFacade> putResource(TImpl resource,
			TKeysHolder keys) {
		ResourceItem<TFacade, TImpl, TKeysHolder> item = this.group
				.putAndCommit(this.context, resource, keys, WhenExists.REPLACE);
		// if (this.group.isGlobalResource && this.context.localCluster != null)
		// {
		// this.context.localCluster.broadcast(new ClusterResInfo_Item(
		// this.group.category,
		// this.group.resourceService.facadeClass, item.id,
		// item.state, ClusterResInfo_Item.Action.INIT));
		// }
		return item;
	}

	@SuppressWarnings("unchecked")
	public final ResourceToken<TFacade> putResource(
			ResourceToken<TFacade> treeParent, TImpl resource) {
		return this.putResource(treeParent, resource, (TKeysHolder) resource);
	}

	@SuppressWarnings("unchecked")
	public final ResourceToken<TFacade> putResource(
			ResourceToken<TFacade> treeParent, TImpl resource, TKeysHolder keys) {
		ResourceItem<TFacade, TImpl, TKeysHolder> item = this.group
				.putAndCommit(this.context,
						(ResourceItem<TFacade, TImpl, TKeysHolder>) treeParent,
						resource, keys, WhenExists.REPLACE);
		// if (this.group.isGlobalResource) {
		// LocalCluster lc = this.context.localCluster;
		// if (lc != null) {
		// Class<?> facadeClass = this.group.resourceService.facadeClass;
		// lc.broadcast(new ClusterResInfo_Item(this.group.category,
		// facadeClass, item.id, item.state,
		// ClusterResInfo_Item.Action.INIT));
		// lc.broadcast(new ClusterResInfo_TreeEntry(this.group.category,
		// facadeClass, None.NONE, (treeParent == null ? 0
		// : ((ResourceItem) treeParent).id), item.id,
		// ClusterResInfo_TreeEntry.Action.INIT));
		// }
		// }
		return item;
	}

	public final void putResource(ResourceToken<TFacade> treeParent,
			ResourceToken<TFacade> child) {
		this.context.putResourceAndCommit(treeParent, child);
		// if (this.group.isGlobalResource && this.context.localCluster != null)
		// {
		// this.context.localCluster.broadcast(new ClusterResInfo_TreeEntry(
		// this.group.category,
		// this.group.resourceService.facadeClass, None.NONE,
		// (treeParent == null ? 0 : ((ResourceItem) treeParent).id),
		// ((ResourceItem) child).id,
		// ClusterResInfo_TreeEntry.Action.INIT));
		// }
	}

	public final <THolderFacade> void putResourceReference(
			ResourceToken<THolderFacade> holder,
			ResourceToken<TFacade> reference) {
		this.context.putResourceReferenceAndCommit(holder, reference);
		// if (this.group.isGlobalResource && this.context.localCluster != null)
		// {
		// this.context.localCluster.broadcast(new ClusterResInfo_RefEntry(
		// reference.getCategory(), reference.getFacadeClass(),
		// ((ResourceItem) reference).id, holder.getCategory(), holder
		// .getFacadeClass(), ((ResourceItem) holder).id,
		// ClusterResInfo_RefEntry.Action.ADD));
		// }
	}

	public final <TReferenceFacade> void putResourceReferenceBy(
			ResourceToken<TFacade> holder,
			ResourceToken<TReferenceFacade> reference) {
		this.context.putResourceReferenceAndCommitBy(holder, reference);
	}

	public final <THolderFacade> void removeResourceReference(
			ResourceToken<THolderFacade> holder,
			ResourceToken<TFacade> reference) {
		this.context.removeResourceReferenceAndCommit(holder, reference);
		// if (this.group.isGlobalResource && this.context.localCluster != null)
		// {
		// this.context.localCluster.broadcast(new ClusterResInfo_RefEntry(
		// reference.getCategory(), reference.getFacadeClass(),
		// ((ResourceItem) reference).id, holder.getCategory(), holder
		// .getFacadeClass(), ((ResourceItem) holder).id,
		// ClusterResInfo_RefEntry.Action.DELETE));
		// }
	}

	public final <TReferenceFacade> void removeResourceReferenceBy(
			ResourceToken<TFacade> holder,
			ResourceToken<TReferenceFacade> reference) {
		this.context.removeResourceReferenceAndCommitBy(holder, reference);
	}

	public final Object getCategory() {
		return this.group.category;
	}

	// ----------------------------以下权限相关-----------------------------------------------------

	public final <THolderFacade> void removeResourceReference(
			Operation<? super TFacade> operation,
			ResourceToken<THolderFacade> holder,
			ResourceToken<TFacade> reference) {
		this.context.removeResourceReference(operation, holder, reference);
	}

	public final <TReferenceFacade> void removeResourceReferenceBy(
			Operation<? super TReferenceFacade> operation,
			ResourceToken<TFacade> holder,
			ResourceToken<TReferenceFacade> reference) {
		this.context.removeResourceReferenceBy(operation, holder, reference);
	}

	// ----------------------------以上权限相关-----------------------------------------------------

}
