package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jt.core.def.obja.StructClass;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.resource.ResourceService.WhenExists;


@StructClass
final class NClusterResourceUpdateTask extends ClusterSynTask {

	NClusterResourceUpdateTask() {
		this.updateActionList = new ArrayList<UpdateAction>();
	}
	
	final List<UpdateAction> getUpdateActionList() {
		return this.updateActionList;
	}

	final void clearUpdateActionList() {
		this.updateActionList.clear();
	}

	final void addUpdateAction(final UpdateAction updateAction) {
		if (updateAction == null) {
			throw new NullArgumentException("updateAction");
		}
		this.updateActionList.add(updateAction);
	}

	final void addUpdateActionList(final List<UpdateAction> updateActionList) {
		if (updateActionList == null) {
			throw new NullArgumentException("updateActionList");
		}
		this.updateActionList = updateActionList;
	}

	final UpdateAction addCreateItemAction(final long resourceGroupID,
			final long resourceLongID, final Object resource,
			final Object keysHolder) {
		final UpdateAction updateAction = new CreateItemAction(resourceGroupID,
				resourceLongID, resource, keysHolder);
		this.updateActionList.add(updateAction);
		return updateAction;
	}

	final UpdateAction addModifyItemAction(final long resourceLongID,
			final Object resource, final Object keysHolder) {
		final UpdateAction updateAction = new ModifyItemAction(resourceLongID,
				resource, keysHolder);
		this.updateActionList.add(updateAction);
		return updateAction;
	}

	final UpdateAction addDeleteItemAction(final long resourceLongID) {
		final UpdateAction updateAction = new DeleteItemAction(resourceLongID);
		this.updateActionList.add(updateAction);
		return updateAction;
	}

	final UpdateAction addCreateTreeNodeAction(final long resourceLongID) {
		final UpdateAction updateAction = new CreateTreeNodeAction(
				resourceLongID);
		this.updateActionList.add(updateAction);
		return updateAction;
	}

	final UpdateAction addMoveTreeNodeAction(final long resourceLongID,
			final long parentLongID) {
		final UpdateAction updateAction = new MoveTreeNodeAction(
				resourceLongID, parentLongID);
		this.updateActionList.add(updateAction);
		return updateAction;
	}

	final UpdateAction addCreateReferenceAction(final long referenceLongID,
			final long referenceHolderLongID) {
		final UpdateAction updateAction = new CreateReferenceAction(
				referenceLongID, referenceHolderLongID);
		this.updateActionList.add(updateAction);
		return updateAction;
	}

	final UpdateAction addDeleteReferenceAction(final long referenceLongID,
			final long referenceHolderLongID) {
		final UpdateAction updateAction = new DeleteReferenceAction(
				referenceLongID, referenceHolderLongID);
		this.updateActionList.add(updateAction);
		return updateAction;
	}

	static final UpdateAction newCreateItemAction(final long resourceGroupID,
			final long resourceLongID, final Object resource,
			final Object keysHolder) {
		return new CreateItemAction(resourceGroupID, resourceLongID, resource,
				keysHolder);
	}

	final UpdateAction newModifyItemAction(final long resourceLongID,
			final Object resource, final Object keysHolder) {
		return new ModifyItemAction(resourceLongID, resource, keysHolder);
	}

	final UpdateAction newDeleteItemAction(final long resourceLongID) {
		return new DeleteItemAction(resourceLongID);
	}

	final UpdateAction newCreateTreeNodeAction(final long resourceLongID) {
		return new CreateTreeNodeAction(resourceLongID);
	}

	final UpdateAction newMoveTreeNodeAction(final long resourceLongID,
			final long parentLongID) {
		return new MoveTreeNodeAction(resourceLongID, parentLongID);
	}

	final UpdateAction newCreateReferenceAction(final long referenceLongID,
			final long referenceHolderLongID) {
		return new CreateReferenceAction(referenceLongID, referenceHolderLongID);
	}

	final UpdateAction newDeleteReferenceAction(final long referenceLongID,
			final long referenceHolderLongID) {
		return new DeleteReferenceAction(referenceLongID, referenceHolderLongID);
	}

	private List<UpdateAction> updateActionList;

	@StructClass
	static abstract class UpdateAction {

		protected UpdateAction() {

		}

		/**
		 * @return 返回更新是否成功
		 */
		abstract boolean execute(TransactionImpl transaction);

	}

	@StructClass
	private static final class CreateItemAction extends UpdateAction {

		private CreateItemAction(final long resourceGroupID,
				final long resourceLongID, final Object resource,
				final Object keysHolder) {
			super();
			if (resource == null) {
				throw new NullArgumentException("resource");
			}
			if (keysHolder == null) {
				throw new NullArgumentException("keysHolder");
			}
			this.resourceGroupID = resourceGroupID;
			this.resourceLongID = resourceLongID;
			this.resource = resource;
			this.keysHolder = keysHolder;
		}

		@SuppressWarnings("unchecked")
		@Override
		final boolean execute(TransactionImpl transaction) {
			try {
				final ResourceGroup resourceGroup = transaction.site.globalResourceContainer
						.findResourceGroup(this.resourceGroupID);
				if (resourceGroup != null) {
					resourceGroup.putResource(transaction, this.resource,
							this.keysHolder, WhenExists.EXCEPTION,
							this.resourceLongID);
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		final long resourceGroupID;

		final long resourceLongID;

		final Object resource;

		final Object keysHolder;

	}

	@StructClass
	private static final class ModifyItemAction extends UpdateAction {

		private ModifyItemAction(final long resourceLongID,
				final Object resource, final Object keysHolder) {
			super();
			if (resource == null) {
				throw new NullArgumentException("resource");
			}
			if (keysHolder == null) {
				throw new NullArgumentException("keysHolder");
			}
			this.resourceLongID = resourceLongID;
			this.resource = resource;
			this.keysHolder = keysHolder;
		}

		@SuppressWarnings("unchecked")
		@Override
		final boolean execute(TransactionImpl transaction) {
			try {
				ResourceItem item = transaction.site.globalResourceContainer
						.find(this.resourceLongID);
				item.clusterModifyResource(this.resource, this.keysHolder);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		final long resourceLongID;

		final Object resource;

		final Object keysHolder;

	}

	@StructClass
	private static final class DeleteItemAction extends UpdateAction {

		private DeleteItemAction(final long resourceLongID) {
			super();
			this.resourceLongID = resourceLongID;
		}

		@SuppressWarnings("unchecked")
		@Override
		final boolean execute(TransactionImpl transaction) {
			try {
				final ResourceItem resourceItem = transaction.site.globalResourceContainer
						.find(this.resourceLongID);
				if (resourceItem == null) {
					throw new RuntimeException("没有找到LongID为["
							+ this.resourceLongID + "]的资源");
				}
				resourceItem.group.lockRemove(transaction, resourceItem);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		final long resourceLongID;

	}

	@StructClass
	private static final class CreateTreeNodeAction extends UpdateAction {

		private CreateTreeNodeAction(final long resourceLongID) {
			super();
			this.resourceLongID = resourceLongID;
		}

		@SuppressWarnings("unchecked")
		@Override
		final boolean execute(TransactionImpl transaction) {
			try {
				final ResourceItem childItem = transaction.site.globalResourceContainer
						.find(this.resourceLongID);
				if (childItem == null) {
					throw new RuntimeException("没有找到LongID为["
							+ this.resourceLongID + "]的资源");
				}
				childItem.group.putResource(transaction, null, childItem);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		final long resourceLongID;

	}

	@StructClass
	private static final class MoveTreeNodeAction extends UpdateAction {

		private MoveTreeNodeAction(final long resourceLongID,
				final long parentLongID) {
			super();
			this.resourceLongID = resourceLongID;
			this.parentLongID = parentLongID;
		}

		@SuppressWarnings("unchecked")
		@Override
		final boolean execute(TransactionImpl transaction) {
			try {
				final GlobalResourceContainer globalContainer = transaction.site.globalResourceContainer;
				final ResourceItem childItem = globalContainer
						.find(this.resourceLongID);
				if (childItem == null) {
					throw new RuntimeException("没有找到LongID为["
							+ this.resourceLongID + "]的资源");
				}
				final ResourceItem parentItem = globalContainer
						.find(this.parentLongID);
				if (parentItem == null) {
					throw new RuntimeException("没有找到LongID为["
							+ this.parentLongID + "]的资源");
				}
				if (childItem.group != parentItem.group) {
					throw new RuntimeException("子资源的外观类型与父资源的外观类型不一致。子资源外观类["
							+ childItem.group.resourceService.facadeClass
							+ "]，父资源外观类["
							+ parentItem.group.resourceService.facadeClass
							+ "]");
				}
				childItem.group.putResource(transaction, parentItem, childItem);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		final long resourceLongID;

		final long parentLongID;

	}

	@StructClass
	private static final class CreateReferenceAction extends UpdateAction {

		private CreateReferenceAction(final long referenceLongID,
				final long referenceHolderLongID) {
			super();
			this.referenceLongID = referenceLongID;
			this.referenceHolderLongID = referenceHolderLongID;
		}

		@SuppressWarnings("unchecked")
		@Override
		final boolean execute(TransactionImpl transaction) {
			try {
				final GlobalResourceContainer globalContainer = transaction.site.globalResourceContainer;
				final ResourceItem holder = globalContainer
						.find(this.referenceHolderLongID);
				if (holder == null) {
					throw new RuntimeException("没有找到LongID为["
							+ this.referenceHolderLongID + "]的资源");
				}
				final ResourceItem reference = globalContainer
						.find(this.referenceLongID);
				if (reference == null) {
					throw new RuntimeException("没有找到LongID为["
							+ this.referenceLongID + "]的资源");
				}
				holder.putReference(transaction, reference);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		final long referenceLongID;

		final long referenceHolderLongID;

	}

	@StructClass
	private static final class DeleteReferenceAction extends UpdateAction {

		private DeleteReferenceAction(final long referenceLongID,
				final long referenceHolderLongID) {
			super();
			this.referenceLongID = referenceLongID;
			this.referenceHolderLongID = referenceHolderLongID;
		}

		@SuppressWarnings("unchecked")
		@Override
		final boolean execute(TransactionImpl transaction) {
			try {
				final GlobalResourceContainer globalContainer = transaction.site.globalResourceContainer;
				final ResourceItem holder = globalContainer
						.find(this.referenceHolderLongID);
				if (holder == null) {
					throw new RuntimeException("没有找到LongID为["
							+ this.referenceHolderLongID + "]的资源");
				}
				final ResourceItem reference = globalContainer
						.find(this.referenceLongID);
				if (reference == null) {
					throw new RuntimeException("没有找到LongID为["
							+ this.referenceLongID + "]的资源");
				}
				holder.removeReference(transaction, reference);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		final long referenceLongID;

		final long referenceHolderLongID;

	}

}
