package org.eclipse.jt.core.impl;

import java.util.List;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.None;
import org.eclipse.jt.core.auth.Actor;
import org.eclipse.jt.core.auth.ActorState;
import org.eclipse.jt.core.auth.AuthorizedResourceCategoryItem;
import org.eclipse.jt.core.auth.AuthorizedResourceItem;
import org.eclipse.jt.core.da.ORMAccessor;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.resource.ResourceContext;
import org.eclipse.jt.core.resource.ResourceKind;
import org.eclipse.jt.core.service.Publish;
import org.eclipse.jt.core.spi.auth.DeleteActorOrganizationMappingTask;
import org.eclipse.jt.core.spi.auth.DeleteActorTask;
import org.eclipse.jt.core.spi.auth.GetAuthorizedResCategoryItemForActorKey;
import org.eclipse.jt.core.spi.auth.GetSubAuthorizedResourceItemsForActorKey;
import org.eclipse.jt.core.spi.auth.MaintainActorAuthorityTask;
import org.eclipse.jt.core.spi.auth.NewActorTask;
import org.eclipse.jt.core.spi.auth.UpdateActorAuthorityTask;
import org.eclipse.jt.core.spi.auth.UpdateActorBaseInfoTask;
import org.eclipse.jt.core.type.GUID;


/**
 * ��������Դ���������
 * 
 * @param <TFacade>
 *            ��Դ�������
 * @param <TImpl>
 *            ��Դʵ������
 * @param <TKeysHolder>
 *            ��Դ��������
 * @author Jeff Tang 2009-12
 */
@SuppressWarnings("deprecation")
abstract class ActorResourceService<TFacade extends Actor, TImpl extends TFacade, TKeysHolder>
		extends ResourceServiceBase<TFacade, TImpl, TKeysHolder> {

	protected final TD_CoreAuthACL td_CoreAuthACL;

	protected final TD_CoreAuthAuthACL td_CoreAuthAuthACL;

	protected final TD_CoreAuthUOM td_CoreAuthUOM;

	protected final ORM_CoreAuthRA_ByActor orm_CoreAuthRA_ByActor;

	private final DD_CoreAuthACL_ByActor dd_CoreAuthACL_ByActor;

	private final DD_CoreAuthAuthACL_ByActor dd_CoreAuthAuthACL_ByActor;

	private final DD_CoreAuthUOM_ByActor dd_CoreAuthUOM_ByActor;

	private final DD_CoreAuthACL_OneRecord dd_CoreAuthACL_OneRecord;

	private final DD_CoreAuthAuthACL_OneRecord dd_CoreAuthAuthACL_OneRecord;

	private final DD_CoreAuthUOM_OneRecord dd_CoreAuthUOM_OneRecord;

	private final DD_CoreAuthACL_ByActorAndOrg dd_CoreAuthACL_ByActorAndOrg;

	private final DD_CoreAuthAuthACL_ByActorAndOrg dd_CoreAuthAuthACL_ByActorAndOrg;

	protected ActorResourceService(
			String title,
			TD_CoreAuthACL td_CoreAuthACL,
			TD_CoreAuthAuthACL td_CoreAuthAuthACL,
			TD_CoreAuthUOM td_CoreAuthUOM,
			ORM_CoreAuthACL_ByActorAndOrg orm_CoreAuthACL_ByActorAndOrg,
			ORM_CoreAuthAuthACL_ByActorAndOrg orm_CoreAuthAuthACL_ByActorAndOrg,
			ORM_CoreAuthRA_ByActor orm_CoreAuthRA_ByActor,
			DD_CoreAuthACL_ByActor dd_CoreAuthACL_ByActor,
			DD_CoreAuthAuthACL_ByActor dd_CoreAuthAuthACL_ByActor,
			DD_CoreAuthUOM_ByActor dd_CoreAuthUOM_ByActor,
			DD_CoreAuthACL_OneRecord dd_CoreAuthACL_OneRecord,
			DD_CoreAuthAuthACL_OneRecord dd_CoreAuthAuthACL_OneRecord,
			DD_CoreAuthUOM_OneRecord dd_CoreAuthUOM_OneRecord,
			DD_CoreAuthACL_ByActorAndOrg dd_CoreAuthACL_ByActorAndOrg,
			DD_CoreAuthAuthACL_ByActorAndOrg dd_CoreAuthAuthACL_ByActorAndOrg,
			DD_CoreAuthACL_ByResCategory dd_CoreAuthACL_ByResCategory,
			DD_CoreAuthAuthACL_ByResCategory dd_CoreAuthAuthACL_ByResCategory,
			DD_CoreAuthACL_ByResource dd_CoreAuthACL_ByResource,
			DD_CoreAuthAuthACL_ByResource dd_CoreAuthAuthACL_ByResource) {
		super(title, ResourceKind.SINGLETON_IN_CLUSTER);
		this.td_CoreAuthACL = td_CoreAuthACL;
		this.td_CoreAuthAuthACL = td_CoreAuthAuthACL;
		this.td_CoreAuthUOM = td_CoreAuthUOM;
		ActorResourceService.orm_CoreAuthACL_ByActorAndOrg = orm_CoreAuthACL_ByActorAndOrg;
		ActorResourceService.orm_CoreAuthAuthACL_ByActorAndOrg = orm_CoreAuthAuthACL_ByActorAndOrg;
		this.orm_CoreAuthRA_ByActor = orm_CoreAuthRA_ByActor;
		this.dd_CoreAuthACL_ByActor = dd_CoreAuthACL_ByActor;
		this.dd_CoreAuthAuthACL_ByActor = dd_CoreAuthAuthACL_ByActor;
		this.dd_CoreAuthUOM_ByActor = dd_CoreAuthUOM_ByActor;
		this.dd_CoreAuthACL_OneRecord = dd_CoreAuthACL_OneRecord;
		this.dd_CoreAuthAuthACL_OneRecord = dd_CoreAuthAuthACL_OneRecord;
		this.dd_CoreAuthUOM_OneRecord = dd_CoreAuthUOM_OneRecord;
		this.dd_CoreAuthACL_ByActorAndOrg = dd_CoreAuthACL_ByActorAndOrg;
		this.dd_CoreAuthAuthACL_ByActorAndOrg = dd_CoreAuthAuthACL_ByActorAndOrg;
		ActorResourceService.dd_CoreAuthACL_ByResCategory = dd_CoreAuthACL_ByResCategory;
		ActorResourceService.dd_CoreAuthAuthACL_ByResCategory = dd_CoreAuthAuthACL_ByResCategory;
		ActorResourceService.dd_CoreAuthACL_ByResource = dd_CoreAuthACL_ByResource;
		ActorResourceService.dd_CoreAuthAuthACL_ByResource = dd_CoreAuthAuthACL_ByResource;
	}

	/**
	 * �½���������������
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TTask>
	 *            �½�����������
	 */
	@Publish
	protected abstract class NewActorTaskHandler<TTask extends NewActorTask>
			extends TaskMethodHandler<TTask, None> {

		protected NewActorTaskHandler() {
			super(None.NONE, null);
		}

		/**
		 * ��ʼ������ʵ�����
		 * 
		 * @param actorEntity
		 *            ������ʵ�����
		 * @param task
		 *            �½�����������
		 */
		protected final void initActorEntity(
				final CoreAuthActorEntity actorEntity, TTask task) {
			// ������������ĺϷ���
			if (task.name.length() > CoreAuthActorEntity.NAME_FIELD_SIZE) {
				throw new IllegalArgumentException("name");
			}
			ActorResourceService.this.checkStringArgument(task.title, "title",
					true, CoreAuthActorEntity.TITLE_FIELD_SIZE);
			ActorResourceService.this.checkStringArgument(task.description,
					"description", true,
					CoreAuthActorEntity.DESCRIPTION_FIELD_SIZE);
			actorEntity.RECID = task.id;
			actorEntity.name = task.name.toLowerCase();
			actorEntity.title = task.title = task.title == null ? task.name
					: task.title;
			actorEntity.state = task.state = task.state == null ? ActorState.NORMAL
					: task.state;
			actorEntity.description = task.description;
			actorEntity.setMappingOrgIDs(null);
		}

	}

	/**
	 * �޸ķ����߻�����Ϣ��������
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TTask>
	 *            �޸ķ����߻�����Ϣ����
	 */
	@Publish
	protected abstract class UpdateActorBaseInfoTaskHandler<TTask extends UpdateActorBaseInfoTask>
			extends TaskMethodHandler<TTask, None> {

		protected UpdateActorBaseInfoTaskHandler() {
			super(None.NONE, null);
		}

		/**
		 * �޸ķ�����ʵ�����
		 * 
		 * @param actorEntity
		 *            ������ʵ�����
		 * @param task
		 *            �޸ķ����߻�����Ϣ����
		 */
		protected final void updateActorEntity(
				final CoreAuthActorEntity actorEntity, TTask task) {
			if (task.title != null) {
				ActorResourceService.this.checkStringArgument(task.title,
						"title", false, CoreAuthActorEntity.TITLE_FIELD_SIZE);
				actorEntity.title = task.title;
			}
			if (task.state != null) {
				actorEntity.state = task.state;
			}
			if (task.description != null) {
				ActorResourceService.this.checkStringArgument(task.description,
						"description", false,
						CoreAuthActorEntity.DESCRIPTION_FIELD_SIZE);
				actorEntity.description = task.description;
			}
		}

	}

	/**
	 * �޸ķ�����Ȩ����������
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TTask>
	 *            �޸ķ�����Ȩ������
	 */
	@Publish
	protected abstract class UpdateActorAuthorityTaskHandler<TTask extends UpdateActorAuthorityTask>
			extends TaskMethodHandler<TTask, None> {

		protected UpdateActorAuthorityTaskHandler() {
			super(None.NONE, null);
		}

		/**
		 * �޸ķ�����Ȩ��
		 * 
		 * @param context
		 *            ������
		 * @param actorEntity
		 *            ������ʵ�����
		 * @param task
		 *            �޸ķ�����Ȩ������
		 */
		protected final void updateActorAuthority(
				ResourceContext<TFacade, TImpl, TKeysHolder> context,
				final CoreAuthActorEntity actorEntity, TTask task) {
			if (task.authorityResourceTable.size() == 0) {
				return;
			}
			final ContextImpl<?, ?, ?> ctx = (ContextImpl<?, ?, ?>) context;
			final GlobalResourceContainer globalResourceContainer = ActorResourceService.this.site.globalResourceContainer;
			final ResourceGroup<?, ?, ?> group = globalResourceContainer
					.findAndResolveAuthResourceGroup(ctx,
							task.resourceCategoryID);
			if (group == null) {
				return;
			}
			final ORMAccessor<CoreAuthACLEntity> ormACL = context
					.newORMAccessor(ActorResourceService.this.td_CoreAuthACL);
			try {
				if (task.orgID == null) {
					task.orgID = CoreAuthActorEntity.GLOBAL_ORG_ID;
				}
				long[] acl = actorEntity.tryGetOperationACL(ctx, task.orgID);
				final CoreAuthACLEntity aclEntity = new CoreAuthACLEntity();
				aclEntity.actorID = actorEntity.RECID;
				aclEntity.orgID = task.orgID;
				aclEntity.resCategoryID = task.resourceCategoryID;
				if (acl == null) {
					// ����һ����֯ӳ��
					for (int index = 0, size = task.authorityResourceTable
							.size(); index < size; index++) {
						AuthResItemImpl item = (AuthResItemImpl) task.authorityResourceTable
								.get(index);
						if (item.authCode == 0) {
							continue;
						}
						if (item instanceof AuthResCategoryItemImpl) {
							aclEntity.resourceID = CoreAuthACLEntity.ROOT_RESOURCE_GUID;
						} else {
							ResourceItem<?, ?, ?> resItem = globalResourceContainer
									.find(item.itemID);
							if (resItem == null) {
								continue;
							}
							if ((aclEntity.resourceID = group
									.tryGetAuthID(resItem)) == null) {
								continue;
							}
						}
						aclEntity.RECID = context.newRECID();
						aclEntity.authorityCode = item.authCode;
						ormACL.insert(aclEntity);
						acl = ACLHelper.setAuthCode(acl, item.itemID,
								item.authCode);
					}
					if (!ACLHelper.isEmpty(acl)) {
						if (!task.orgID
								.equals(CoreAuthActorEntity.GLOBAL_ORG_ID)) {
							this.insertUOMRecord(context, aclEntity.actorID,
									aclEntity.orgID);
						}
						actorEntity.setOperationACL(task.orgID, acl);
					}
				} else {
					for (int index = 0, size = task.authorityResourceTable
							.size(); index < size; index++) {
						AuthResItemImpl item = (AuthResItemImpl) task.authorityResourceTable
								.get(index);
						syn: {
							if (item instanceof AuthResCategoryItemImpl) {
								aclEntity.resourceID = CoreAuthACLEntity.ROOT_RESOURCE_GUID;
							} else {
								ResourceItem<?, ?, ?> resItem = globalResourceContainer
										.find(item.itemID);
								if (resItem == null) {
									break syn;
								}
								if ((aclEntity.resourceID = group
										.tryGetAuthID(resItem)) == null) {
									break syn;
								}
							}
							// ɾ����ɫָ����Դ���ACL��Ϣ
							context
									.executeUpdate(
											ActorResourceService.this.dd_CoreAuthACL_OneRecord,
											aclEntity.actorID, aclEntity.orgID,
											aclEntity.resCategoryID,
											aclEntity.resourceID);
						}
						if (item.authCode != 0) {
							aclEntity.RECID = context.newRECID();
							aclEntity.authorityCode = item.authCode;
							ormACL.insert(aclEntity);
						}
						acl = ACLHelper.setAuthCode(acl, item.itemID,
								item.authCode);
					}
					actorEntity.setOperationACL(task.orgID, acl);
				}
			} finally {
				ormACL.unuse();
			}
		}

		private final void insertUOMRecord(Context context, GUID actorID,
				GUID orgID) {
			final ORMAccessor<CoreAuthUOMEntity> ormUOM = context
					.newORMAccessor(ActorResourceService.this.td_CoreAuthUOM);
			try {
				final CoreAuthUOMEntity uomEntity = new CoreAuthUOMEntity();
				uomEntity.RECID = context.newRECID();
				uomEntity.actorID = actorID;
				uomEntity.orgID = orgID;
				ormUOM.insert(uomEntity);
			} finally {
				ormUOM.unuse();
			}
		}

	}

	/**
	 * ɾ����������������
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TTask>
	 *            ɾ������������
	 */
	@Publish
	protected abstract class DeleteActorTaskHandler<TTask extends DeleteActorTask>
			extends TaskMethodHandler<TTask, None> {

		protected DeleteActorTaskHandler() {
			super(None.NONE, null);
		}

		/**
		 * ɾ��������
		 * 
		 * @param context
		 *            ������
		 * @param actorEntity
		 *            ������ʵ��
		 * @param task
		 *            ɾ������������
		 */
		protected final void deleteActor(
				ResourceContext<TFacade, TImpl, TKeysHolder> context,
				final CoreAuthActorEntity actorEntity, TTask task) {
			actorEntity.state = ActorState.DISPOSED;
			// ɾ��ACL�������Ϣ
			context.executeUpdate(
					ActorResourceService.this.dd_CoreAuthACL_ByActor,
					actorEntity.RECID);
			context.executeUpdate(
					ActorResourceService.this.dd_CoreAuthAuthACL_ByActor,
					actorEntity.RECID);
			// ɾ����֯ӳ��������Ϣ
			context.executeUpdate(
					ActorResourceService.this.dd_CoreAuthUOM_ByActor,
					actorEntity.RECID);
		}

	}

	/**
	 * ɾ����������֯����ӳ����������
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TTask>
	 *            ɾ����������֯����ӳ������
	 */
	@Publish
	protected abstract class DeleteActorOrganizationMappingTaskHaldler<TTask extends DeleteActorOrganizationMappingTask>
			extends TaskMethodHandler<TTask, None> {

		protected DeleteActorOrganizationMappingTaskHaldler() {
			super(None.NONE, null);
		}

		/**
		 * ɾ����������֯����ӳ��
		 * 
		 * @param context
		 *            ������
		 * @param actorEntity
		 *            ������ʵ��
		 * @param task
		 *            ɾ����������֯����ӳ������
		 */
		protected final void deleteActorOrgMapping(
				ResourceContext<TFacade, TImpl, TKeysHolder> context,
				CoreAuthActorEntity actorEntity, TTask task) {
			if (actorEntity.hasMappingOrg(task.orgID)) {
				context.executeUpdate(
						ActorResourceService.this.dd_CoreAuthACL_ByActorAndOrg,
						task.actorID, task.orgID);
				context
						.executeUpdate(
								ActorResourceService.this.dd_CoreAuthAuthACL_ByActorAndOrg,
								task.actorID, task.orgID);
				context.executeUpdate(
						ActorResourceService.this.dd_CoreAuthUOM_OneRecord,
						task.actorID, task.orgID);
				actorEntity.removeOrgMapping(task.orgID);
			}
		}

	}

	/**
	 * ����Ȩ��Դ������б��ṩ��
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TKey>
	 */
	@Publish
	protected abstract class ForActorAuthorizedResCategoryItemProvider<TKey extends GetAuthorizedResCategoryItemForActorKey>
			extends
			OneKeyResultListProvider<AuthorizedResourceCategoryItem, TKey> {

		protected final void provideAuthResCategoryItemList(
				ResourceContext<TFacade, TImpl, TKeysHolder> context,
				CoreAuthActorEntity actorEntity,
				final List<AuthorizedResourceCategoryItem> resultList, TKey key) {
			final ResourceGroup<?, ?, ?>[] rgs = ActorResourceService.this.site.globalResourceContainer
					.getAllAuthGroup();
			final long[] acl = actorEntity.tryGetOperationACL(
					(ContextImpl<?, ?, ?>) context, key.orgID);
			if (acl == null || acl == CoreAuthActorEntity.NULL_ACL) {
				for (ResourceGroup<?, ?, ?> authGroup : rgs) {
					resultList.add(new AuthResCategoryItemImpl(authGroup, 0));
				}
			} else {
				for (ResourceGroup<?, ?, ?> authGroup : rgs) {
					resultList.add(new AuthResCategoryItemImpl(authGroup,
							ACLHelper.getAuthCode(acl, authGroup.id)));
				}
			}

		}

	}

	/**
	 * ����Ȩ��Դ���б��ṩ��
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TKey>
	 */
	@Publish
	protected abstract class ForActorSubAuthorizedResourceItemsProvider<TKey extends GetSubAuthorizedResourceItemsForActorKey>
			extends OneKeyResultListProvider<AuthorizedResourceItem, TKey> {

		protected final void provideAuthResItemList(
				ResourceContext<TFacade, TImpl, TKeysHolder> context,
				CoreAuthActorEntity actorEntity,
				final List<AuthorizedResourceItem> resultList, TKey key) {
			final ContextImpl<?, ?, ?> ctx = (ContextImpl<?, ?, ?>) context;
			final ResourceGroup<?, ?, ?> group = ActorResourceService.this.site.globalResourceContainer
					.findAndResolveAuthResourceGroup(ctx,
							key.resourceCategoryID);
			if (group == null) {
				return;
			}
			final long[] acl = actorEntity.tryGetOperationACL(ctx, key.orgID);
			if (acl == null || acl == CoreAuthActorEntity.NULL_ACL) {
				for (ResourceEntry<?, ?, ?> subResItems = group
						.getResItemChildren(ctx, key.currentResID); subResItems != null; subResItems = subResItems
						.internalNext(ctx.transaction)) {
					ResourceItem<?, ?, ?> resItem = subResItems.resourceItem;
					resultList.add(new AuthResItemImpl(resItem.id, group
							.tryGetAuthID(resItem), group
							.tryGetAuthTitle(resItem), 0));
				}
			} else {
				for (ResourceEntry<?, ?, ?> subResItems = group
						.getResItemChildren(ctx, key.currentResID); subResItems != null; subResItems = subResItems
						.internalNext(ctx.transaction)) {
					ResourceItem<?, ?, ?> resItem = subResItems.resourceItem;
					resultList.add(new AuthResItemImpl(resItem.id, group
							.tryGetAuthID(resItem), group
							.tryGetAuthTitle(resItem), ACLHelper.getAuthCode(
							acl, resItem.id)));
				}
			}
		}

	}

	@Publish
	protected abstract class MaintainActorAuthority_Fill_Handler<TTask extends MaintainActorAuthorityTask>
			extends TaskMethodHandler<TTask, MaintainActorAuthorityTask.Method> {

		protected MaintainActorAuthority_Fill_Handler() {
			super(MaintainActorAuthorityTask.Method.FILL_AUTHORIZED_ITEM, null);
		}

		protected final void fillAuthorizedItems(
				ResourceContext<TFacade, TImpl, TKeysHolder> context,
				CoreAuthActorEntity actorEntity,
				final MaintainActorAuthorityTask task) {
			long[] acl = actorEntity.getOperationACL(
					(ContextImpl<?, ?, ?>) context, task.orgID);
			for (AbstractAuthorityItem item : task.authorizedItemList) {
				item.authCode = ACLHelper.getAuthCode(acl, item.itemID);
				item.filled = true;
			}
		}

	}

	@Publish
	protected abstract class MaintainActorAuthority_Update_Handler<TTask extends MaintainActorAuthorityTask>
			extends TaskMethodHandler<TTask, MaintainActorAuthorityTask.Method> {

		protected MaintainActorAuthority_Update_Handler() {
			super(MaintainActorAuthorityTask.Method.UPDATE_AUTHORITY, null);
		}

		protected final void updateActorAuthority(
				ResourceContext<TFacade, TImpl, TKeysHolder> context,
				CoreAuthActorEntity actorEntity,
				final MaintainActorAuthorityTask task) {
			if (task.authorizedItemList.size() == 0) {
				return;
			}
			final ContextImpl<?, ?, ?> ctx = (ContextImpl<?, ?, ?>) context;
			final GlobalResourceContainer globalResourceContainer = ActorResourceService.this.site.globalResourceContainer;
			if (task.orgID == null) {
				task.orgID = CoreAuthActorEntity.GLOBAL_ORG_ID;
			}
			final CoreAuthACLEntity aclEntity = new CoreAuthACLEntity();
			aclEntity.actorID = actorEntity.RECID;
			aclEntity.orgID = task.orgID;
			final ORMAccessor<CoreAuthACLEntity> ormACL;
			if (task.operationAuthority) {
				ormACL = context
						.newORMAccessor(ActorResourceService.this.td_CoreAuthACL);
				long[] acl = actorEntity.tryGetOperationACL(ctx, task.orgID);
				if (acl == null) {
					// ����һ����֯ӳ��
					for (AbstractAuthorityItem item : task.authorizedItemList) {
						if (item.authCode == 0) {
							continue;
						}
						if (item.isCategoryItem) {
							aclEntity.resourceID = CoreAuthACLEntity.ROOT_RESOURCE_GUID;
							aclEntity.resCategoryID = item.categoryID;
						} else {
							ResourceItem<?, ?, ?> resourceItem = globalResourceContainer
									.find(item.itemID);
							if (resourceItem == null) {
								continue;
							}
							aclEntity.resourceID = resourceItem.group
									.tryGetAuthID(resourceItem);
							aclEntity.resCategoryID = resourceItem.group.groupID;
						}
						aclEntity.RECID = context.newRECID();
						aclEntity.authorityCode = item.authCode;
						ormACL.insert(aclEntity);
						acl = ACLHelper.setAuthCode(acl, item.itemID,
								item.authCode);
					}
					if (ACLHelper.isEmpty(acl)) {
						acl = CoreAuthActorEntity.NULL_ACL;
					}
					if (!task.orgID.equals(CoreAuthActorEntity.GLOBAL_ORG_ID)) {
						this.insertUOMRecord(context, aclEntity.actorID,
								aclEntity.orgID);
					}
					actorEntity.setOperationACL(task.orgID, acl);
				} else {
					for (AbstractAuthorityItem item : task.authorizedItemList) {
						if (item.isCategoryItem) {
							aclEntity.resourceID = CoreAuthACLEntity.ROOT_RESOURCE_GUID;
							aclEntity.resCategoryID = item.categoryID;
						} else {
							ResourceItem<?, ?, ?> resourceItem = globalResourceContainer
									.find(item.itemID);
							if (resourceItem == null) {
								continue;
							}
							aclEntity.resourceID = resourceItem.group
									.tryGetAuthID(resourceItem);
							aclEntity.resCategoryID = resourceItem.group.groupID;
						}
						// ɾ����ɫָ����Դ���ACL��Ϣ
						context
								.executeUpdate(
										ActorResourceService.this.dd_CoreAuthACL_OneRecord,
										aclEntity.actorID, aclEntity.orgID,
										aclEntity.resCategoryID,
										aclEntity.resourceID);
						if (item.authCode != 0) {
							aclEntity.RECID = context.newRECID();
							aclEntity.authorityCode = item.authCode;
							ormACL.insert(aclEntity);
						}
						acl = ACLHelper.setAuthCode(acl, item.itemID,
								item.authCode);
					}
					actorEntity.setOperationACL(task.orgID, acl);
				}
			} else {
				ormACL = context
						.newORMAccessor(ActorResourceService.this.td_CoreAuthAuthACL);
				for (AbstractAuthorityItem item : task.authorizedItemList) {
					if (item.isCategoryItem) {
						aclEntity.resourceID = CoreAuthACLEntity.ROOT_RESOURCE_GUID;
						aclEntity.resCategoryID = item.categoryID;
					} else {
						ResourceItem<?, ?, ?> resourceItem = globalResourceContainer
								.find(item.itemID);
						if (resourceItem == null) {
							continue;
						}
						aclEntity.resourceID = resourceItem.group
								.tryGetAuthID(resourceItem);
						aclEntity.resCategoryID = resourceItem.group.groupID;
					}
					// ɾ����ɫָ����Դ���ACL��Ϣ
					context
							.executeUpdate(
									ActorResourceService.this.dd_CoreAuthAuthACL_OneRecord,
									aclEntity.actorID, aclEntity.orgID,
									aclEntity.resCategoryID,
									aclEntity.resourceID);
					if (item.authCode != 0) {
						aclEntity.RECID = context.newRECID();
						aclEntity.authorityCode = item.authCode;
						ormACL.insert(aclEntity);
					}
				}
			}
		}

		private final void insertUOMRecord(Context context, GUID actorID,
				GUID orgID) {
			final ORMAccessor<CoreAuthUOMEntity> ormUOM = context
					.newORMAccessor(ActorResourceService.this.td_CoreAuthUOM);
			try {
				final CoreAuthUOMEntity uomEntity = new CoreAuthUOMEntity();
				uomEntity.RECID = context.newRECID();
				uomEntity.actorID = actorID;
				uomEntity.orgID = orgID;
				ormUOM.insert(uomEntity);
			} finally {
				ormUOM.unuse();
			}
		}

	}

	/**
	 * �����ַ�������
	 * 
	 * @param arg
	 *            ����
	 * @param argName
	 *            ������
	 * @param nullable
	 *            �Ƿ�����Ϊ��
	 * @param maxLen
	 *            ��󳤶�
	 */
	protected final void checkStringArgument(String arg, String argName,
			boolean nullable, int maxLen) {
		if (arg == null) {
			if (!nullable) {
				throw new NullArgumentException(argName);
			}
		} else if (maxLen < arg.length()) {
			throw new IllegalArgumentException("[" + argName + "]��������󳤶�"
					+ maxLen);
		}
	}

	protected static final long[][] EMPTY_ACLS = new long[][] {};

	// XXX �������������ɿ����ɣ���������ʹ����԰�ȫ
	private static ORM_CoreAuthACL_ByActorAndOrg orm_CoreAuthACL_ByActorAndOrg;

	// TODO ��ʱ��ʱ�����Ȩ�����ݿ���������
	@SuppressWarnings("unused")
	private static DD_CoreAuthACL_ByResCategory dd_CoreAuthACL_ByResCategory;

	@SuppressWarnings("unused")
	private static DD_CoreAuthACL_ByResource dd_CoreAuthACL_ByResource;

	private static ORM_CoreAuthAuthACL_ByActorAndOrg orm_CoreAuthAuthACL_ByActorAndOrg;

	@SuppressWarnings("unused")
	private static DD_CoreAuthAuthACL_ByResCategory dd_CoreAuthAuthACL_ByResCategory;

	@SuppressWarnings("unused")
	private static DD_CoreAuthAuthACL_ByResource dd_CoreAuthAuthACL_ByResource;

	/**
	 * ���ݷ�����ID����֯����ID��ȡACL���б�
	 * 
	 * @param context
	 *            �����ļ�
	 * @param actorID
	 *            ������ID
	 * @param orgID
	 *            ��֯����ID
	 * @return ����ACL���б�
	 */
	static final List<CoreAuthACLEntity> getACLEntityByActorAndOrg(
			Context context, GUID actorID, GUID orgID) {
		final ORMAccessor<CoreAuthACLEntity> ormACL = context
				.newORMAccessor(ActorResourceService.orm_CoreAuthACL_ByActorAndOrg);
		try {
			return ormACL.fetch(actorID, orgID);
		} finally {
			ormACL.unuse();
		}
	}

	/**
	 * ������Դ���IDɾ��ACL��
	 * 
	 * @param context
	 *            ������
	 * @param resCategoryID
	 *            ��Դ���ID
	 */
	static final void deleteACLRecordByResCategory(Context context,
			GUID resCategoryID) {
		// context.executeUpdate(
		// ActorResourceService.dd_CoreAuthACL_ByResCategory,
		// resCategoryID);
	}

	/**
	 * ������ԴIDɾ��ACL��
	 * 
	 * @param context
	 *            ������
	 * @param resourceID
	 *            ��ԴID
	 */
	static final void deleteACLRecordByResource(Context context,
			GUID resCategoryID, GUID resourceID) {
		// context.executeUpdate(ActorResourceService.dd_CoreAuthACL_ByResource,
		// resourceID);
	}

	/**
	 * ���ݷ�����ID����֯����ID��ȡACL���б�
	 * 
	 * @param context
	 *            �����ļ�
	 * @param actorID
	 *            ������ID
	 * @param orgID
	 *            ��֯����ID
	 * @return ����ACL���б�
	 */
	static final List<CoreAuthACLEntity> getAuthACLEntityByActorAndOrg(
			Context context, GUID actorID, GUID orgID) {
		final ORMAccessor<CoreAuthACLEntity> ormACL = context
				.newORMAccessor(ActorResourceService.orm_CoreAuthAuthACL_ByActorAndOrg);
		try {
			return ormACL.fetch(actorID, orgID);
		} finally {
			ormACL.unuse();
		}
	}

	/**
	 * ������Դ���IDɾ��ACL��
	 * 
	 * @param context
	 *            ������
	 * @param resCategoryID
	 *            ��Դ���ID
	 */
	static final void deleteAuthACLRecordByResCategory(Context context,
			GUID resCategoryID) {
		// context.executeUpdate(
		// ActorResourceService.dd_CoreAuthAuthACL_ByResCategory,
		// resCategoryID);
	}

	/**
	 * ������ԴIDɾ��ACL��
	 * 
	 * @param context
	 *            ������
	 * @param resourceID
	 *            ��ԴID
	 */
	static final void deleteAuthACLRecordByResource(Context context,
			GUID resCategoryID, GUID resourceID) {
		// context.executeUpdate(
		// ActorResourceService.dd_CoreAuthAuthACL_ByResource, resourceID);
	}

}
