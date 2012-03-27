package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.None;
import org.eclipse.jt.core.impl.NClusterResourceLockTask.LockAction;
import org.eclipse.jt.core.impl.NClusterResourceUpdateTask.UpdateAction;
import org.eclipse.jt.core.service.Publish;
import org.eclipse.jt.core.service.Publish.Mode;

@SuppressWarnings("unchecked")
final class ClusterSynService extends ServiceBase<ContextImpl> {

	static final String NAME = "集群资源同步服务";

	ClusterSynService() {
		super(NAME);
	}

	@Publish(Mode.SITE_PUBLIC)
	final class GetResGroupLongIDTaskHandler extends
			TaskMethodHandler<NClusterGetResGroupLongIDTask, None> {

		protected GetResGroupLongIDTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected void handle(final ContextImpl context,
				final NClusterGetResGroupLongIDTask task) throws Throwable {
			final ResourceGroup<?, ?, ?> resourceGroup = ClusterSynService.this.site.globalResourceContainer
					.findAuthResourceGroup(task.resourceGroupGUID);
			if (resourceGroup != null) {
				task.resourceGroupLongID = resourceGroup.id;
				task.setState(ClusterSynTask.State.HANDLE_SUCCESSED);
			} else {
				task.setState(ClusterSynTask.State.HANDLE_FAILED);
			}
		}

	}

	@Publish(Mode.SITE_PUBLIC)
	final class ResourceLockTaskHandler extends
			TaskMethodHandler<NClusterResourceLockTask, None> {

		protected ResourceLockTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected void handle(final ContextImpl context,
				final NClusterResourceLockTask task) throws Throwable {
			final TransactionImpl transaction = context.transaction;
			for (LockAction action : task.getLockActionList()) {
				if (action.execute(transaction)) {
					continue;
				}
				task.setState(ClusterSynTask.State.HANDLE_FAILED);
			}
			task.setState(ClusterSynTask.State.HANDLE_SUCCESSED);
		}

	}

	@Publish(Mode.SITE_PUBLIC)
	final class ResourceUpdateTaskHandler extends
			TaskMethodHandler<NClusterResourceUpdateTask, None> {

		protected ResourceUpdateTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected void handle(final ContextImpl context,
				final NClusterResourceUpdateTask task) throws Throwable {
			final TransactionImpl transaction = context.transaction;
			for (UpdateAction action : task.getUpdateActionList()) {
				if (action.execute(transaction)) {
					continue;
				}
				task.setState(ClusterSynTask.State.HANDLE_FAILED);
			}
			task.setState(ClusterSynTask.State.HANDLE_SUCCESSED);
		}

	}

	@Publish(Mode.SITE_PUBLIC)
	final class TransactionTaskHandler extends
			TaskMethodHandler<NClusterTransactionTask, None> {

		protected TransactionTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected void handle(final ContextImpl context,
				final NClusterTransactionTask task) throws Throwable {
			final TransactionImpl transaction = context.transaction;
			try {
				transaction.resetTrans(task.isDoCommit());
			} catch (Exception e) {
				task.setState(ClusterSynTask.State.HANDLE_FAILED);
			}
			task.setState(ClusterSynTask.State.HANDLE_SUCCESSED);
		}

	}

	@Publish(Mode.SITE_PUBLIC)
	final class ClusterNodeDetectTaskHandler extends
			TaskMethodHandler<NClusterNodeDetectTask, None> {

		protected ClusterNodeDetectTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected void handle(final ContextImpl context,
				final NClusterNodeDetectTask task) throws Throwable {
			task.setState(ClusterSynTask.State.HANDLE_SUCCESSED);
		}

	}

	@Publish(Mode.SITE_PUBLIC)
	final class ClusterResourceInitTaskHandler extends
			TaskMethodHandler<NClusterResourceInitTask, None> {

		protected ClusterResourceInitTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected void handle(final ContextImpl context,
				final NClusterResourceInitTask task) throws Throwable {
			final ResourceGroup<?, ?, ?> resourceGroup = ClusterSynService.this.site.globalResourceContainer
					.findResourceGroup(task.resourceGroupLongID);
			if (resourceGroup != null) {
				if (task.isGetTask()) {
					if (resourceGroup.isInited()) {
						resourceGroup.addResourceItemTo(task);
						resourceGroup.addResourceTreeTo(task);
						task.setGroupInited();
					} else {
						task.setGroupUninited();
					}
				} else {
					resourceGroup.initResourceItemFrom(context, task);
				}
			}
		}

	}

	@Publish(Mode.SITE_PUBLIC)
	final class ClusterNodeJoinTaskHandler extends
			TaskMethodHandler<NClusterNodeJoinTask, None> {

		protected ClusterNodeJoinTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected void handle(final ContextImpl context,
				final NClusterNodeJoinTask task) throws Throwable {
			ClusterSynService.this.site.application.getNetCluster()
					.onClusterNodeJoin(task.nodeIndex);
		}

	}

}
