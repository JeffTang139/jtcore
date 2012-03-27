package org.eclipse.jt.core.impl;

import java.util.List;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.User;
import org.eclipse.jt.core.auth.AuthorizedResourceCategoryItem;
import org.eclipse.jt.core.auth.AuthorizedResourceItem;
import org.eclipse.jt.core.auth.Role;
import org.eclipse.jt.core.auth.RoleOperation;
import org.eclipse.jt.core.da.ORMAccessor;
import org.eclipse.jt.core.misc.MissingObjectException;
import org.eclipse.jt.core.resource.ResourceContext;
import org.eclipse.jt.core.resource.ResourceInserter;
import org.eclipse.jt.core.resource.ResourceToken;
import org.eclipse.jt.core.resource.ResourceService.WhenExists;
import org.eclipse.jt.core.service.Publish;
import org.eclipse.jt.core.spi.auth.DeleteRoleOrganizationMappingTask;
import org.eclipse.jt.core.spi.auth.DeleteRoleTask;
import org.eclipse.jt.core.spi.auth.GetAuthorizedResCategoryItemForRoleKey;
import org.eclipse.jt.core.spi.auth.GetRoleAssignInfoForRoleKey;
import org.eclipse.jt.core.spi.auth.GetSubAuthorizedResourceItemsForRoleKey;
import org.eclipse.jt.core.spi.auth.MaintainRoleAuthorityTask;
import org.eclipse.jt.core.spi.auth.NewRoleTask;
import org.eclipse.jt.core.spi.auth.UpdateRoleAuthorityTask;
import org.eclipse.jt.core.spi.auth.UpdateRoleBaseInfoTask;
import org.eclipse.jt.core.type.GUID;


/**
 * 角色资源服务
 * 
 * @author Jeff Tang 2009-12
 */
@SuppressWarnings("deprecation")
final class RoleResourceService extends
		ActorResourceService<Role, CoreAuthRoleEntity, CoreAuthRoleEntity> {

	private final TD_CoreAuthRole td_CoreAuthRole;

	private final ORM_CoreAuthRA_ByRole orm_CoreAuthRA_ByRole;

	public RoleResourceService(
			TD_CoreAuthRole td_CoreAuthRole,
			TD_CoreAuthACL td_CoreAuthACL,
			TD_CoreAuthAuthACL td_CoreAuthAuthACL,
			TD_CoreAuthUOM td_CoreAuthUOM,
			ORM_CoreAuthACL_ByActorAndOrg orm_CoreAuthACL_ByActorAndOrg,
			ORM_CoreAuthAuthACL_ByActorAndOrg orm_CoreAuthAuthACL_ByActorAndOrg,
			ORM_CoreAuthRA_ByActor orm_CoreAuthRA_ByActor,
			ORM_CoreAuthRA_ByRole orm_CoreAuthRA_ByRole,
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
		super("角色", td_CoreAuthACL, td_CoreAuthAuthACL, td_CoreAuthUOM,
				orm_CoreAuthACL_ByActorAndOrg,
				orm_CoreAuthAuthACL_ByActorAndOrg, orm_CoreAuthRA_ByActor,
				dd_CoreAuthACL_ByActor, dd_CoreAuthAuthACL_ByActor,
				dd_CoreAuthUOM_ByActor, dd_CoreAuthACL_OneRecord,
				dd_CoreAuthAuthACL_OneRecord, dd_CoreAuthUOM_OneRecord,
				dd_CoreAuthACL_ByActorAndOrg, dd_CoreAuthAuthACL_ByActorAndOrg,
				dd_CoreAuthACL_ByResCategory, dd_CoreAuthAuthACL_ByResCategory,
				dd_CoreAuthACL_ByResource, dd_CoreAuthAuthACL_ByResource);
		this.td_CoreAuthRole = td_CoreAuthRole;
		this.orm_CoreAuthRA_ByRole = orm_CoreAuthRA_ByRole;
	}

	final class ByIDRoleResourceProvider extends
			AuthorizableResourceProvider<RoleOperation> {

		protected ByIDRoleResourceProvider() {
			super(null, false);
		}

		@Override
		protected GUID getKey1(CoreAuthRoleEntity keys) {
			return keys.RECID;
		}

		@Override
		protected String getResourceTitle(CoreAuthRoleEntity resource,
				CoreAuthRoleEntity keys) {
			return resource.title;
		}

	}

	final class ByNameRoleResourceProvider extends
			OneKeyResourceProvider<String> {

		@Override
		protected String getKey1(CoreAuthRoleEntity keys) {
			return keys.name;
		}

	}

	@Override
	protected final void initResources(
			Context context,
			ResourceInserter<Role, CoreAuthRoleEntity, CoreAuthRoleEntity> initializer)
			throws Throwable {
		if (this.isDBValid()) {
			// 初始化所有角色资源，不加载ACL
			final ORMAccessor<CoreAuthRoleEntity> ormRole = context
					.newORMAccessor(this.td_CoreAuthRole);
			for (CoreAuthRoleEntity roleEntity : ormRole.fetch()) {
				roleEntity.setMappingOrgIDs(null);
				initializer.putResource(roleEntity);
			}
			ormRole.unuse();
		}
	}

	@Publish
	final class NewRoleTaskHandler extends NewActorTaskHandler<NewRoleTask> {

		@Override
		protected void handle(
				ResourceContext<Role, CoreAuthRoleEntity, CoreAuthRoleEntity> context,
				NewRoleTask task) throws Throwable {
			final CoreAuthRoleEntity roleEntity = new CoreAuthRoleEntity();
			super.initActorEntity(roleEntity, task);
			// 向资源上下文加入资源，向角色数据库中插入记录
			context.putResource(roleEntity, roleEntity, WhenExists.EXCEPTION);
			final ORMAccessor<CoreAuthRoleEntity> ormRole = context
					.newORMAccessor(RoleResourceService.this.td_CoreAuthRole);
			try {
				ormRole.insert(roleEntity);
			} finally {
				ormRole.unuse();
			}
		}

	}

	@Publish
	final class UpdateRoleBaseInfoTaskHandler extends
			UpdateActorBaseInfoTaskHandler<UpdateRoleBaseInfoTask> {

		@Override
		protected void handle(
				ResourceContext<Role, CoreAuthRoleEntity, CoreAuthRoleEntity> context,
				UpdateRoleBaseInfoTask task) throws Throwable {
			final CoreAuthRoleEntity roleEntity = context
					.modifyResource(task.actorID);
			if (roleEntity == null) {
				return;
			}
			if (task.name != null) {
				RoleResourceService.super.checkStringArgument(task.name,
						"name", false, CoreAuthActorEntity.NAME_FIELD_SIZE);
				if (context.find(Role.class, task.name) != null) {
					throw new IllegalArgumentException("角色名已经存在");
				}
				roleEntity.name = task.name;
			}
			// 修改角色实体，任务参数为空代表不修改
			super.updateActorEntity(roleEntity, task);
			// 更新角色资源和角色数据库
			context.postModifiedResource(roleEntity);
			final ORMAccessor<CoreAuthRoleEntity> ormRole = context
					.newORMAccessor(RoleResourceService.this.td_CoreAuthRole);
			try {
				ormRole.update(roleEntity);
			} finally {
				ormRole.unuse();
			}
		}

	}

	@Publish
	final class UpdateRoleAuthorityTaskHandler extends
			UpdateActorAuthorityTaskHandler<UpdateRoleAuthorityTask> {

		@Override
		protected void handle(
				ResourceContext<Role, CoreAuthRoleEntity, CoreAuthRoleEntity> context,
				UpdateRoleAuthorityTask task) throws Throwable {
			final CoreAuthRoleEntity roleEntity = context
					.modifyResource(task.actorID);
			if (roleEntity == null) {
				return;
			}
			super.updateActorAuthority(context, roleEntity, task);
			context.postModifiedResource(roleEntity);
		}

	}

	@Publish
	final class DeleteRoleTaskHandler extends
			DeleteActorTaskHandler<DeleteRoleTask> {

		@Override
		protected void handle(
				ResourceContext<Role, CoreAuthRoleEntity, CoreAuthRoleEntity> context,
				DeleteRoleTask task) throws Throwable {
			// 如果还存在用户被分配了当前角色，则不能被删除
			final CoreAuthRoleEntity roleEntity = context
					.removeResource(task.actorID);
			if (roleEntity == null) {
				return;
			}
			super.deleteActor(context, roleEntity, task);
			// 删除角色信息
			final ORMAccessor<CoreAuthRoleEntity> ormRole = context
					.newORMAccessor(RoleResourceService.this.td_CoreAuthRole);
			try {
				ormRole.delete(roleEntity);
			} finally {
				ormRole.unuse();
			}
		}

	}

	@Publish
	final class DeleteRoleOrganizationMappingTaskHaldler
			extends
			DeleteActorOrganizationMappingTaskHaldler<DeleteRoleOrganizationMappingTask> {

		@Override
		protected void handle(
				ResourceContext<Role, CoreAuthRoleEntity, CoreAuthRoleEntity> context,
				DeleteRoleOrganizationMappingTask task) throws Throwable {
			final CoreAuthRoleEntity roleEntity = context
					.modifyResource(task.actorID);
			if (roleEntity == null) {
				return;
			}
			super.deleteActorOrgMapping(context, roleEntity, task);
			context.postModifiedResource(roleEntity);
		}

	}

	@Publish
	final class ForRoleAuthorizedResCategoryItemProvider
			extends
			ForActorAuthorizedResCategoryItemProvider<GetAuthorizedResCategoryItemForRoleKey> {

		@SuppressWarnings("unchecked")
		@Override
		protected void provide(
				ResourceContext<Role, CoreAuthRoleEntity, CoreAuthRoleEntity> context,
				GetAuthorizedResCategoryItemForRoleKey key,
				List<AuthorizedResourceCategoryItem> resultList)
				throws Throwable {
			final CoreAuthRoleEntity roleEntity = ((ResourceItem<?, CoreAuthRoleEntity, ?>) context
					.getResourceToken(Role.class, key.actorID)).getImpl();
			super.provideAuthResCategoryItemList(context, roleEntity,
					resultList, key);
		}
	}

	@Publish
	final class ForRoleSubAuthorizedResourceItemsProvider
			extends
			ForActorSubAuthorizedResourceItemsProvider<GetSubAuthorizedResourceItemsForRoleKey> {

		@SuppressWarnings("unchecked")
		@Override
		protected void provide(
				ResourceContext<Role, CoreAuthRoleEntity, CoreAuthRoleEntity> context,
				GetSubAuthorizedResourceItemsForRoleKey key,
				List<AuthorizedResourceItem> resultList) throws Throwable {
			final CoreAuthRoleEntity roleEntity = ((ResourceItem<?, CoreAuthRoleEntity, ?>) context
					.getResourceToken(Role.class, key.actorID)).getImpl();
			super.provideAuthResItemList(context, roleEntity, resultList, key);
		}

	}

	@Publish
	final class MaintainRoleAuthority_Fill_Handler extends
			MaintainActorAuthority_Fill_Handler<MaintainRoleAuthorityTask> {

		@SuppressWarnings("unchecked")
		@Override
		protected void handle(
				ResourceContext<Role, CoreAuthRoleEntity, CoreAuthRoleEntity> context,
				MaintainRoleAuthorityTask task) throws Throwable {
			final ResourceToken<Role> roleToken = context.findResourceToken(
					Role.class, task.actorID);
			if (roleToken == null) {
				return;
			}
			final RoleProxy roleProxy = new RoleProxy(
					(ResourceItem<?, CoreAuthRoleEntity, ?>) roleToken);
			final ContextImpl<?, ?, ?> ctx = (ContextImpl<?, ?, ?>) context;
			final long[][] roleACL;
			final GUID orgID = task.orgID;
			if (task.operationAuthority) {
				roleACL = roleProxy.getOperationACLs(ctx, orgID);
			} else {
				roleACL = roleProxy.getAccreditACLs(ctx, orgID);
			}
			final long[][] loginUserAccreditACLs;
			User user = ctx.session.getUser();
			if (user instanceof UserProxy) {
				UserProxy loginUser = (UserProxy) user;
				loginUserAccreditACLs = loginUser.getAccreditACLs(ctx, orgID);
			} else {
				loginUserAccreditACLs = ActorResourceService.EMPTY_ACLS;
			}

			final TransactionImpl transaction = ctx.transaction;
			for (AbstractAuthorityItem item : task.authorizedItemList) {
				ResourceItem<?, ?, ?> resItem = item.resItem;
				if (resItem != null) {
					item.setAuthorityInfo(ACLHelper.getAuthCode(roleACL[0],
							resItem.id), resItem.generateAuthorityInfo(
							transaction, roleACL), resItem
							.generateAuthorityInfo(transaction,
									loginUserAccreditACLs));
				} else {
					ResourceGroup<?, ?, ?> group = RoleResourceService.this.site.globalResourceContainer
							.findAndResolveAuthResourceGroup(ctx,
									item.categoryID);
					if (group == null) {
						throw new MissingObjectException("找不到id为["
								+ item.categoryID + "]的资源组");
					}
					item.setAuthorityInfo(ACLHelper.getAuthCode(roleACL[0],
							item.itemID), group.generateAuthorityInfo(roleACL),
							group.generateAuthorityInfo(loginUserAccreditACLs));
				}
			}
		}
	}

	@Publish
	final class MaintainRoleAuthority_Update_Handler extends
			MaintainActorAuthority_Update_Handler<MaintainRoleAuthorityTask> {

		@Override
		protected void handle(
				ResourceContext<Role, CoreAuthRoleEntity, CoreAuthRoleEntity> context,
				MaintainRoleAuthorityTask task) throws Throwable {
			final CoreAuthRoleEntity roleEntity = context
					.modifyResource(task.actorID);
			if (roleEntity == null) {
				return;
			}
			super.updateActorAuthority(context, roleEntity, task);
			context.postModifiedResource(roleEntity);
		}

	}

	@Publish
	final class ForRoleRoleAssignInfoProvider extends
			OneKeyResultListProvider<User, GetRoleAssignInfoForRoleKey> {

		@Override
		protected void provide(
				ResourceContext<Role, CoreAuthRoleEntity, CoreAuthRoleEntity> context,
				GetRoleAssignInfoForRoleKey key, List<User> resultList)
				throws Throwable {
			final Role role = context.find(Role.class, key.roleID);
			if (role == null) {
				return;
			}
			final ORMAccessor<CoreAuthRAEntity> ormRA = context
					.newORMAccessor(RoleResourceService.this.orm_CoreAuthRA_ByRole);
			for (CoreAuthRAEntity raEntity : ormRA.fetch(role.getID())) {
				User user = context.find(User.class, raEntity.actorID);
				if (user != null) {
					resultList.add(user);
				}
			}
		}

	}

}
