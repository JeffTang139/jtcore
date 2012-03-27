package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jt.core.def.obja.StructClass;
import org.eclipse.jt.core.exception.NullArgumentException;


@StructClass
final class NClusterResourceLockTask extends ClusterSynTask {

	NClusterResourceLockTask() {
		this.lockActionList = new ArrayList<LockAction>(1);
	}

	final List<LockAction> getLockActionList() {
		return this.lockActionList;
	}

	final void clearLockActionList() {
		this.lockActionList.clear();
	}

	final void addLockAction(final LockAction lockAction) {
		if (lockAction == null) {
			throw new NullArgumentException("lockAction");
		}
		this.lockActionList.add(lockAction);
	}

	final LockAction addLockItemAction(final long resourceLongID) {
		final LockAction lockAction = new LockItemAction(resourceLongID);
		this.lockActionList.add(lockAction);
		return lockAction;
	}

	final LockAction addLockGroupAction(long resourceGroupID) {
		final LockAction lockAction = new LockGroupAction(resourceGroupID);
		this.lockActionList.add(lockAction);
		return lockAction;
	}

	final LockAction addLockReferenceAction(final long resourceLongID,
			final Class<?> referenceFacadeClass) {
		final LockAction lockAction = new LockReferenceAction(resourceLongID,
				referenceFacadeClass);
		this.lockActionList.add(lockAction);
		return lockAction;
	}

	static final LockAction newLockItemAction(final long resourceLongID) {
		return new LockItemAction(resourceLongID);
	}

	static final LockAction newLockGroupAction(long resourceGroupID) {
		return new LockGroupAction(resourceGroupID);
	}

	static final LockAction newLockReferenceAction(final long resourceLongID,
			final Class<?> referenceFacadeClass) {
		return new LockReferenceAction(resourceLongID, referenceFacadeClass);
	}

	private final List<LockAction> lockActionList;

	@StructClass
	static abstract class LockAction {

		protected LockAction() {

		}

		/**
		 * @return 返回加锁是否成功
		 */
		abstract boolean execute(TransactionImpl transaction);

	}

	@StructClass
	private static final class LockItemAction extends LockAction {

		private LockItemAction(final long resourceLongID) {
			super();
			this.resourceLongID = resourceLongID;
		}

		@Override
		final boolean execute(TransactionImpl transaction) {
			try {
				final ResourceItem<?, ?, ?> resource = transaction.site.globalResourceContainer
						.find(this.resourceLongID);
				if (resource != null) {
					transaction.tryHandleItemIntoContextIfNot(resource,
							ResourceDemandFor.MODIFY);
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		final long resourceLongID;

	}

	@StructClass
	private static final class LockGroupAction extends LockAction {

		private LockGroupAction(final long resourceGroupID) {
			this.resourceGroupID = resourceGroupID;
		}

		@Override
		final boolean execute(TransactionImpl transaction) {
			try {
				final ResourceGroup<?, ?, ?> group = transaction.site.globalResourceContainer
						.findResourceGroup(this.resourceGroupID);
				if (group != null) {
					transaction.tryHandleGroupIntoContextIfNot(group);
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		final long resourceGroupID;

	}

	@StructClass
	private static final class LockReferenceAction extends LockAction {

		private LockReferenceAction(final long resourceLongID,
				final Class<?> referenceFacadeClass) {
			if (referenceFacadeClass == null) {
				throw new NullArgumentException("referenceFacadeClass");
			}
			this.resourceLongID = resourceLongID;
			this.referenceFacadeClass = referenceFacadeClass;
		}

		@Override
		final boolean execute(TransactionImpl transaction) {
			try {
				final ResourceItem<?, ?, ?> resource = transaction.site.globalResourceContainer
						.find(this.resourceLongID);
				if (resource != null) {
					ResourceReferenceStorage<?> resourceReferenceStorage = resource
							.getRRS(this.referenceFacadeClass);
					if (resourceReferenceStorage != null) {
						transaction.tryHandleResRefStorageIntoContextIfNot(
								resourceReferenceStorage);
						return true;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		final long resourceLongID;

		final Class<?> referenceFacadeClass;

	}

}
