package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.None;
import org.eclipse.jt.core.User;
import org.eclipse.jt.core.auth.Actor;
import org.eclipse.jt.core.auth.AuthorizedResourceCategoryItem;
import org.eclipse.jt.core.auth.AuthorizedResourceItem;
import org.eclipse.jt.core.auth.Role;
import org.eclipse.jt.core.auth.UserOperation;
import org.eclipse.jt.core.da.ORMAccessor;
import org.eclipse.jt.core.misc.MissingObjectException;
import org.eclipse.jt.core.resource.ResourceContext;
import org.eclipse.jt.core.resource.ResourceInserter;
import org.eclipse.jt.core.resource.ResourceService.WhenExists;
import org.eclipse.jt.core.service.Publish;
import org.eclipse.jt.core.spi.auth.AuthorizableResourceCategoryItem;
import org.eclipse.jt.core.spi.auth.ChangeSessionUserTask;
import org.eclipse.jt.core.spi.auth.DeleteUserOrganizationMappingTask;
import org.eclipse.jt.core.spi.auth.DeleteUserTask;
import org.eclipse.jt.core.spi.auth.GetAuthorizedResCategoryItemForUserKey;
import org.eclipse.jt.core.spi.auth.GetRoleAssignInfoForUserKey;
import org.eclipse.jt.core.spi.auth.GetSubAuthorizedResourceItemsForUserKey;
import org.eclipse.jt.core.spi.auth.MaintainUserAuthorityTask;
import org.eclipse.jt.core.spi.auth.NewUserTask;
import org.eclipse.jt.core.spi.auth.UpdateUserAuthorityTask;
import org.eclipse.jt.core.spi.auth.UpdateUserBaseInfoTask;
import org.eclipse.jt.core.spi.auth.UpdateUserPasswordTask;
import org.eclipse.jt.core.spi.auth.UpdateUserRoleAssignTask;
import org.eclipse.jt.core.spi.metadata.ExtractMetaDataEvent;
import org.eclipse.jt.core.spi.metadata.LoadMetaDataEvent;
import org.eclipse.jt.core.type.GUID;


/**
 * 用户资源服务
 * 
 * @author Jeff Tang 2009-12
 */
@SuppressWarnings("deprecation")
final class UserResourceService extends
		ActorResourceService<User, CoreAuthUserEntity, CoreAuthUserEntity> {

	private final TD_CoreAuthUser td_CoreAuthUser;

	private final TD_CoreAuthRA td_CoreAuthRA;

	private final TD_CoreAuthRole td_CoreAuthRole;

	private final ORM_CoreAuthUOM_OrderByActor orm_CoreAuthUOM_OrderByActor;

	private final DD_CoreAuthRA_ByActor dd_CoreAuthRA_ByActor;

	public UserResourceService(
			TD_CoreAuthUser td_CoreAuthUser,
			TD_CoreAuthRA td_CoreAuthRA,
			TD_CoreAuthACL td_CoreAuthACL,
			TD_CoreAuthAuthACL td_CoreAuthAuthACL,
			TD_CoreAuthUOM td_CoreAuthUOM,
			TD_CoreAuthRole td_CoreAuthRole,
			ORM_CoreAuthACL_ByActorAndOrg orm_CoreAuthACL_ByActorAndOrg,
			ORM_CoreAuthAuthACL_ByActorAndOrg orm_CoreAuthAuthACL_ByActorAndOrg,
			ORM_CoreAuthRA_ByActor orm_CoreAuthRA_ByActor,
			ORM_CoreAuthUOM_OrderByActor orm_CoreAuthUOM_OrderByActor,
			DD_CoreAuthRA_ByActor dd_CoreAuthRA_ByActor,
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
		super("用户", td_CoreAuthACL, td_CoreAuthAuthACL, td_CoreAuthUOM,
				orm_CoreAuthACL_ByActorAndOrg,
				orm_CoreAuthAuthACL_ByActorAndOrg, orm_CoreAuthRA_ByActor,
				dd_CoreAuthACL_ByActor, dd_CoreAuthAuthACL_ByActor,
				dd_CoreAuthUOM_ByActor, dd_CoreAuthACL_OneRecord,
				dd_CoreAuthAuthACL_OneRecord, dd_CoreAuthUOM_OneRecord,
				dd_CoreAuthACL_ByActorAndOrg, dd_CoreAuthAuthACL_ByActorAndOrg,
				dd_CoreAuthACL_ByResCategory, dd_CoreAuthAuthACL_ByResCategory,
				dd_CoreAuthACL_ByResource, dd_CoreAuthAuthACL_ByResource);
		this.td_CoreAuthUser = td_CoreAuthUser;
		this.td_CoreAuthRA = td_CoreAuthRA;
		this.td_CoreAuthRole = td_CoreAuthRole;
		this.orm_CoreAuthUOM_OrderByActor = orm_CoreAuthUOM_OrderByActor;
		this.dd_CoreAuthRA_ByActor = dd_CoreAuthRA_ByActor;
	}

	final class ByIDUserResourceProvider extends
			AuthorizableResourceProvider<UserOperation> {

		protected ByIDUserResourceProvider() {
			super(null, false);
		}

		@Override
		protected GUID getKey1(CoreAuthUserEntity keys) {
			return keys.RECID;
		}

		@Override
		protected String getResourceTitle(CoreAuthUserEntity resource,
				CoreAuthUserEntity keys) {
			return resource.title;
		}

	}

	final class ByNameUserResourceProvider extends
			OneKeyResourceProvider<String> {

		@Override
		protected String getKey1(CoreAuthUserEntity keys) {
			return keys.name;
		}

	}

	final class UserReferenceRole extends ResourceReference<Role, User> {

		public UserReferenceRole() {
			super(null, UserResourceService.this);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	protected final void initResources(
			Context context,
			ResourceInserter<User, CoreAuthUserEntity, CoreAuthUserEntity> initializer)
			throws Throwable {
		if (this.isDBValid()) {
			// 初始化所有用户资源，加载角色分配信息，暂不加载ACL
			final ORMAccessor<CoreAuthUserEntity> ormUser = context
					.newORMAccessor(this.td_CoreAuthUser);
			final ORMAccessor<CoreAuthRAEntity> ormRA = context
					.newORMAccessor(this.orm_CoreAuthRA_ByActor);
			final ORMAccessor<CoreAuthUOMEntity> ormUOM = context
					.newORMAccessor(this.orm_CoreAuthUOM_OrderByActor);
			try {
				final List<CoreAuthUOMEntity> uomEntitys = ormUOM.fetch();
				int roleAssignedCount;
				if (uomEntitys.isEmpty()) {
					for (CoreAuthUserEntity userEntity : ormUser.fetch()) {
						userEntity.setMappingOrgIDs(null);
						ResourceItem userItem = (ResourceItem) (initializer
								.putResource(userEntity));
						if (userEntity.getAssignedRoleCount() == 0) {
							continue;
						}
						roleAssignedCount = 0;
						for (CoreAuthRAEntity raEntity : ormRA
								.fetch(userEntity.RECID)) {
							ResourceItem roleItem = (ResourceItem) context
									.findResourceToken(Role.class,
											raEntity.roleID);
							if (roleItem != null) {
								initializer.putResourceReference(userItem,
										roleItem);
								roleAssignedCount++;
							} else {
								ormRA.delete(raEntity);
							}
						}
						if (roleAssignedCount != userEntity
								.getAssignedRoleCount()) {
							userEntity.setPriorityInfo(roleAssignedCount,
									userEntity.getPriorityIndex());
							ormUser.update(userEntity);
						}
					}
				} else {
					final int size = uomEntitys.size();
					int index = 0;
					List<GUID> tempMappingOrgIDs = new ArrayList<GUID>();
					int oldRoleAssignedCount;
					for (CoreAuthUserEntity userEntity : ormUser.fetch()) {
						ResourceItem userItem = (ResourceItem) (initializer
								.putResource(userEntity));
						if ((oldRoleAssignedCount = userEntity
								.getAssignedRoleCount()) != 0) {
							roleAssignedCount = 0;
							for (CoreAuthRAEntity raEntity : ormRA
									.fetch(userEntity.RECID)) {
								ResourceItem roleItem = (ResourceItem) context
										.findResourceToken(Role.class,
												raEntity.roleID);
								if (roleItem != null) {
									initializer.putResourceReference(userItem,
											roleItem);
									roleAssignedCount++;
								} else {
									ormRA.delete(raEntity);
								}
							}
							if (roleAssignedCount != oldRoleAssignedCount) {
								userEntity.setPriorityInfo(roleAssignedCount,
										userEntity.getPriorityIndex());
								ormUser.update(userEntity);
							}
						}
						CoreAuthUOMEntity uomEntity;
						while (index < size
								&& (uomEntity = uomEntitys.get(index)).actorID
										.equals(userEntity.RECID)) {
							tempMappingOrgIDs.add(uomEntity.orgID);
							index++;
						}
						int tempSize;
						if ((tempSize = tempMappingOrgIDs.size()) != 0) {
							GUID[] mappingOrgIDs = new GUID[tempSize];
							tempMappingOrgIDs.toArray(mappingOrgIDs);
							userEntity.setMappingOrgIDs(mappingOrgIDs);
							tempMappingOrgIDs.clear();
						} else {
							userEntity.setMappingOrgIDs(null);
						}
					}
				}
			} finally {
				ormUOM.unuse();
				ormRA.unuse();
				ormUser.unuse();
			}
		}
	}

	@Publish
	final class NewUserTaskHandler extends NewActorTaskHandler<NewUserTask> {

		@SuppressWarnings("unchecked")
		@Override
		protected void handle(
				ResourceContext<User, CoreAuthUserEntity, CoreAuthUserEntity> context,
				NewUserTask task) throws Throwable {
			this.checkUserName(task.name.toLowerCase());
			final CoreAuthUserEntity userEntity = new CoreAuthUserEntity();
			super.initActorEntity(userEntity, task);
			userEntity.password = task.passwordNeedEncrypt ? task.password == null ? GUID
					.MD5Of("")
					: GUID.MD5Of(task.password)
					: GUID.valueOf(task.password);
			// 向资源上下文加入资源
			final ResourceItem userItem = (ResourceItem) context.putResource(
					userEntity, userEntity, WhenExists.EXCEPTION);
			// 创建角色分配
			final long priorityInfo = UserResourceService.this
					.createUserRoleAssign(context, userItem,
							task.assignRoleIDList, userEntity.RECID);
			userEntity.setPriorityInfo((int) (priorityInfo >>> 32),
					(int) priorityInfo);
			// 向用户数据库中插入记录
			final ORMAccessor<CoreAuthUserEntity> ormRole = context
					.newORMAccessor(UserResourceService.this.td_CoreAuthUser);
			try {
				ormRole.insert(userEntity);
			} finally {
				ormRole.unuse();
			}
		}

		private final void checkUserName(String userName) {
			if (userName.equals("system") || userName.equals("debug")) {
				throw new IllegalArgumentException(userName + "为系统保留用户名。");
			}
		}

	}

	@Publish
	final class UpdateUserBaseInfoTaskHandler extends
			UpdateActorBaseInfoTaskHandler<UpdateUserBaseInfoTask> {

		@Override
		protected void handle(
				ResourceContext<User, CoreAuthUserEntity, CoreAuthUserEntity> context,
				UpdateUserBaseInfoTask task) throws Throwable {
			final CoreAuthUserEntity userEntity = context
					.modifyResource(task.actorID);
			if (userEntity == null) {
				return;
			}
			if (task.name != null) {
				UserResourceService.super.checkStringArgument(task.name,
						"name", false, CoreAuthActorEntity.NAME_FIELD_SIZE);
				if (context.find(User.class, task.name) != null) {
					throw new IllegalArgumentException("用户名已经存在");
				}
				userEntity.name = task.name;
			}
			// 修改角色实体，任务参数为空代表不修改
			super.updateActorEntity(userEntity, task);
			// 更新用户资源和用户数据库
			context.postModifiedResource(userEntity);
			final ORMAccessor<CoreAuthUserEntity> ormUser = context
					.newORMAccessor(UserResourceService.this.td_CoreAuthUser);
			try {
				ormUser.update(userEntity);
			} finally {
				ormUser.unuse();
			}
		}

	}

	@Publish
	final class UpdateUserPasswordTaskHandler extends
			TaskMethodHandler<UpdateUserPasswordTask, None> {

		protected UpdateUserPasswordTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected void handle(
				ResourceContext<User, CoreAuthUserEntity, CoreAuthUserEntity> context,
				UpdateUserPasswordTask task) throws Throwable {
			final CoreAuthUserEntity userEntity = context
					.modifyResource(task.userID);
			if (userEntity == null) {
				return;
			}
			userEntity.password = GUID.MD5Of(task.newPassword);
			context.postModifiedResource(userEntity);
			final ORMAccessor<CoreAuthUserEntity> ormUser = context
					.newORMAccessor(UserResourceService.this.td_CoreAuthUser);
			try {
				ormUser.update(userEntity);
			} finally {
				ormUser.unuse();
			}
		}

	}

	@Publish
	final class UpdateUserRoleAssignTaskHandler extends
			TaskMethodHandler<UpdateUserRoleAssignTask, None> {

		protected UpdateUserRoleAssignTaskHandler() {
			super(None.NONE, null);
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void handle(
				ResourceContext<User, CoreAuthUserEntity, CoreAuthUserEntity> context,
				UpdateUserRoleAssignTask task) throws Throwable {
			final CoreAuthUserEntity userEntity = context
					.modifyResource(task.userID);
			if (userEntity == null) {
				return;
			}
			final ResourceItem<?, CoreAuthUserEntity, ?> userItem = (ResourceItem<?, CoreAuthUserEntity, ?>) context
					.getResourceToken(User.class, task.userID);
			// 删除原有角色分配
			context.executeUpdate(
					UserResourceService.this.dd_CoreAuthRA_ByActor,
					userEntity.RECID);
			userItem.clearReferences(
					((ContextImpl<?, ?, ?>) context).transaction, Role.class,
					false);
			// 更新角色分配
			final long priorityInfo = UserResourceService.this
					.createUserRoleAssign(context, userItem,
							task.assignActorIDList, userEntity.RECID);
			userEntity.setPriorityInfo((int) (priorityInfo >>> 32),
					(int) priorityInfo);
			context.postModifiedResource(userEntity);
			final ORMAccessor<CoreAuthUserEntity> ormUser = context
					.newORMAccessor(UserResourceService.this.td_CoreAuthUser);
			try {
				ormUser.update(userEntity);
			} finally {
				ormUser.unuse();
			}
		}

	}

	@Publish
	final class UpdateUserAuthorityTaskHandler extends
			UpdateActorAuthorityTaskHandler<UpdateUserAuthorityTask> {

		@Override
		protected void handle(
				ResourceContext<User, CoreAuthUserEntity, CoreAuthUserEntity> context,
				UpdateUserAuthorityTask task) throws Throwable {
			final CoreAuthUserEntity userEntity = context
					.modifyResource(task.actorID);
			if (userEntity == null) {
				return;
			}
			super.updateActorAuthority(context, userEntity, task);
			context.postModifiedResource(userEntity);
			final ContextImpl<?, ?, ?> ctx = (ContextImpl<?, ?, ?>) context;
			if (task.actorID.equals(ctx.session.getUser().getID())) {
				ctx.resetACLCache();
			}
		}

	}

	@Publish
	final class DeleteUserTaskHandler extends
			DeleteActorTaskHandler<DeleteUserTask> {

		@Override
		protected void handle(
				ResourceContext<User, CoreAuthUserEntity, CoreAuthUserEntity> context,
				DeleteUserTask task) throws Throwable {
			@SuppressWarnings("unchecked")
			final ResourceItem<?, CoreAuthUserEntity, ?> userItem = (ResourceItem<?, CoreAuthUserEntity, ?>) context
					.getResourceToken(User.class, task.actorID);
			if (userItem == null) {
				return;
			}
			final CoreAuthUserEntity userEntity = context
					.removeResource(task.actorID);
			super.deleteActor(context, userEntity, task);
			// 删除角色分配表相关信息
			userItem.clearReferences(
					((ContextImpl<?, ?, ?>) context).transaction, Role.class,
					false);
			context.executeUpdate(
					UserResourceService.this.dd_CoreAuthRA_ByActor,
					userEntity.RECID);
			// 删除用户信息
			final ORMAccessor<CoreAuthUserEntity> ormUser = context
					.newORMAccessor(UserResourceService.this.td_CoreAuthUser);
			try {
				ormUser.delete(userEntity);
			} finally {
				ormUser.unuse();
			}
		}

	}

	@Publish
	final class DeleteUserOrganizationMappingTaskHaldler
			extends
			DeleteActorOrganizationMappingTaskHaldler<DeleteUserOrganizationMappingTask> {

		@Override
		protected void handle(
				ResourceContext<User, CoreAuthUserEntity, CoreAuthUserEntity> context,
				DeleteUserOrganizationMappingTask task) throws Throwable {
			final CoreAuthUserEntity userEntity = context
					.modifyResource(task.actorID);
			if (userEntity == null) {
				return;
			}
			super.deleteActorOrgMapping(context, userEntity, task);
			context.postModifiedResource(userEntity);
		}

	}

	@Publish
	final class ForUserAuthorizedResCategoryItemProvider
			extends
			ForActorAuthorizedResCategoryItemProvider<GetAuthorizedResCategoryItemForUserKey> {

		@Override
		protected void provide(
				ResourceContext<User, CoreAuthUserEntity, CoreAuthUserEntity> context,
				GetAuthorizedResCategoryItemForUserKey key,
				List<AuthorizedResourceCategoryItem> resultList)
				throws Throwable {
			@SuppressWarnings("unchecked")
			final CoreAuthUserEntity userEntity = ((ResourceItem<?, CoreAuthUserEntity, ?>) context
					.getResourceToken(User.class, key.actorID)).getImpl();
			super.provideAuthResCategoryItemList(context, userEntity,
					resultList, key);
		}

	}

	@Publish
	final class ForUserSubAuthorizedResourceItemsProvider
			extends
			ForActorSubAuthorizedResourceItemsProvider<GetSubAuthorizedResourceItemsForUserKey> {

		@SuppressWarnings("unchecked")
		@Override
		protected void provide(
				ResourceContext<User, CoreAuthUserEntity, CoreAuthUserEntity> context,
				GetSubAuthorizedResourceItemsForUserKey key,
				List<AuthorizedResourceItem> resultList) throws Throwable {
			final CoreAuthUserEntity userEntity = ((ResourceItem<?, CoreAuthUserEntity, ?>) context
					.getResourceToken(User.class, key.actorID)).getImpl();
			super.provideAuthResItemList(context, userEntity, resultList, key);
		}

	}

	@Publish
	final class AuthorizableResourceCategoryItemsProvider extends
			ResultListProvider<AuthorizableResourceCategoryItem> {

		@Override
		protected void provide(
				ResourceContext<User, CoreAuthUserEntity, CoreAuthUserEntity> context,
				List<AuthorizableResourceCategoryItem> resultList)
				throws Throwable {
			for (ResourceGroup<?, ?, ?> rg : UserResourceService.this.site.globalResourceContainer
					.getAllAuthGroup()) {
				resultList.add(new AuthResCateItem(rg));
			}
		}

	}

	@Publish
	final class MaintainUserAuthority_Fill_Handler extends
			MaintainActorAuthority_Fill_Handler<MaintainUserAuthorityTask> {

		@SuppressWarnings("unchecked")
		@Override
		protected void handle(
				ResourceContext<User, CoreAuthUserEntity, CoreAuthUserEntity> context,
				MaintainUserAuthorityTask task) throws Throwable {
			final CoreAuthUserEntity userEntity = (CoreAuthUserEntity) context
					.find(User.class, task.actorID);
			if (userEntity == null) {
				return;
			}
			final ResourceItem<?, CoreAuthUserEntity, ?> userItem = (ResourceItem<?, CoreAuthUserEntity, ?>) context
					.getResourceToken(User.class, task.actorID);
			final ContextImpl<?, ?, ?> ctx = (ContextImpl<?, ?, ?>) context;
			final UserProxy userProxy = new UserProxy(userItem);
			final long[][] userACL;
			final GUID orgID = task.orgID;
			if (task.operationAuthority) {
				userACL = userProxy.getOperationACLs(ctx, orgID);
			} else {
				userACL = userProxy.getAccreditACLs(ctx, orgID);
			}
			final long[][] loginUserAcceditACLs;
			User user = ctx.session.getUser();
			if (user instanceof UserProxy) {
				UserProxy loginUser = (UserProxy) user;
				loginUserAcceditACLs = loginUser.getAccreditACLs(ctx, orgID);
			} else {
				loginUserAcceditACLs = ActorResourceService.EMPTY_ACLS;
			}
			final TransactionImpl transaction = ctx.transaction;
			for (AbstractAuthorityItem item : task.authorizedItemList) {
				ResourceItem<?, ?, ?> resItem = item.resItem;
				if (resItem != null) {
					item.setAuthorityInfo(ACLHelper.getAuthCode(userACL[0],
							resItem.id), resItem.generateAuthorityInfo(
							transaction, userACL), resItem
							.generateAuthorityInfo(transaction,
									loginUserAcceditACLs));
				} else {
					ResourceGroup<?, ?, ?> group = UserResourceService.this.site.globalResourceContainer
							.findAndResolveAuthResourceGroup(ctx,
									item.categoryID);
					if (group == null) {
						throw new MissingObjectException("找不到id为["
								+ item.categoryID + "]的资源组");
					}
					item.setAuthorityInfo(ACLHelper.getAuthCode(userACL[0],
							item.itemID), group.generateAuthorityInfo(userACL),
							group.generateAuthorityInfo(loginUserAcceditACLs));
				}
			}
		}

	}

	@Publish
	final class MaintainUserAuthority_Update_Handler extends
			MaintainActorAuthority_Update_Handler<MaintainUserAuthorityTask> {

		@Override
		protected void handle(
				ResourceContext<User, CoreAuthUserEntity, CoreAuthUserEntity> context,
				MaintainUserAuthorityTask task) throws Throwable {
			final CoreAuthUserEntity userEntity = context
					.modifyResource(task.actorID);
			if (userEntity == null) {
				return;
			}
			ContextImpl<?, ?, ?> ctx = (ContextImpl<?, ?, ?>) context;
			super.updateActorAuthority(context, userEntity, task);
			context.postModifiedResource(userEntity);
			if (task.actorID.equals(ctx.session.getUser().getID())) {
				ctx.resetACLCache();
			}
		}

	}

	@Publish
	final class ForUserRoleAssignInfoWithUserProvider extends
			OneKeyResultListProvider<Actor, GetRoleAssignInfoForUserKey> {

		@SuppressWarnings("unchecked")
		@Override
		protected void provide(
				ResourceContext<User, CoreAuthUserEntity, CoreAuthUserEntity> context,
				GetRoleAssignInfoForUserKey key, List<Actor> resultList)
				throws Throwable {
			final ResourceItem<?, CoreAuthUserEntity, ?> userItem = (ResourceItem<?, CoreAuthUserEntity, ?>) context
					.findResourceToken(User.class, key.userID);
			if (userItem == null) {
				return;
			}
			List<Role> roles = context.getResourceReferences(Role.class,
					userItem);
			final User userEntity = context.find(User.class,
					userItem.getImpl().RECID);
			if (roles.isEmpty()) {
				resultList.add(userEntity);
			} else {
				resultList.addAll(roles);
				final int userPriorityIndex = userEntity.getPriorityIndex();
				resultList.add(userPriorityIndex, userEntity);
			}
		}

	}

	@Publish
	final class ForUserRoleAssignInfoWithoutUserProvider extends
			OneKeyResultListProvider<Role, GetRoleAssignInfoForUserKey> {

		@SuppressWarnings("unchecked")
		@Override
		protected void provide(
				ResourceContext<User, CoreAuthUserEntity, CoreAuthUserEntity> context,
				GetRoleAssignInfoForUserKey key, List<Role> resultList)
				throws Throwable {
			final ResourceItem<?, CoreAuthUserEntity, ?> userItem = (ResourceItem<?, CoreAuthUserEntity, ?>) context
					.findResourceToken(User.class, key.userID);
			if (userItem == null) {
				return;
			}
			List<Role> roles = context.getResourceReferences(Role.class,
					userItem);
			resultList.addAll(roles);
		}

	}

	@Publish
	final class ChangeSessionUserTaskHandler extends
			TaskMethodHandler<ChangeSessionUserTask, None> {

		protected ChangeSessionUserTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected void handle(
				ResourceContext<User, CoreAuthUserEntity, CoreAuthUserEntity> context,
				ChangeSessionUserTask task) throws Throwable {
			final ContextImpl<User, CoreAuthUserEntity, CoreAuthUserEntity> ctx = (ContextImpl<User, CoreAuthUserEntity, CoreAuthUserEntity>) context;
			ctx.changeLoginUser(task.user);
		}

	}

	/**
	 * @return 返回结果高32表示用户被分配的实际角色数，低32位表示用户自身的优先级索引
	 */
	@SuppressWarnings("unchecked")
	private final long createUserRoleAssign(
			ResourceContext<User, CoreAuthUserEntity, CoreAuthUserEntity> context,
			ResourceItem userItem, List<GUID> assignActorIDs, GUID userID) {
		this.tidyRoleAssignArgument(assignActorIDs);
		int assignRoleCount = 0;
		int userPriorityIndex = 0;
		if (assignActorIDs.size() != 0) {
			final CoreAuthRAEntity raEntity = new CoreAuthRAEntity();
			raEntity.actorID = userItem.group.tryGetAuthID(userItem);
			int priority = 0;
			final ORMAccessor<CoreAuthRAEntity> ormRA = context
					.newORMAccessor(this.td_CoreAuthRA);
			try {
				for (int index = 0, size = assignActorIDs.size(); index < size; index++) {
					GUID actorID = assignActorIDs.get(index);
					if (actorID.equals(userID)) {
						userPriorityIndex = index;
						continue;
					}
					ResourceItem roleItem = (ResourceItem) context
							.findResourceToken(Role.class, actorID);
					if (roleItem == null) {
						continue;
					}
					raEntity.RECID = context.newRECID();
					raEntity.roleID = actorID;
					raEntity.priority = priority++;
					ormRA.insert(raEntity);
					context.putResourceReference(userItem, roleItem);
					assignRoleCount++;
				}
			} finally {
				ormRA.unuse();
			}
		}
		return (((long) assignRoleCount) << 32) | (userPriorityIndex);
	}

	private final void tidyRoleAssignArgument(final List<GUID> assignRoleIDs) {
		final List<GUID> existRoleIDs = new ArrayList<GUID>();
		for (int index = 0; index < assignRoleIDs.size();) {
			GUID roleID = assignRoleIDs.get(index);
			if (roleID == null || existRoleIDs.contains(roleID)) {
				assignRoleIDs.remove(index);
				continue;
			}
			index++;
			existRoleIDs.add(roleID);
		}
	}

	// ==================================================================================

	private static final float LISTENER_PRIORITY = 1.0F;

	private final AuthorityOptionUtil getAuthorityOptionUtil() {
		return AuthorityOptionUtil.getInstance(this.td_CoreAuthUser,
				this.td_CoreAuthRole, this.td_CoreAuthRA, this.td_CoreAuthUOM,
				this.td_CoreAuthACL, this.td_CoreAuthAuthACL);
	}

	@Publish
	protected final class ExtractSysOptionsMeataDataListener extends
			EventListener<ExtractMetaDataEvent> {

		public ExtractSysOptionsMeataDataListener() {
			super(UserResourceService.LISTENER_PRIORITY);
		}

		@Override
		protected void occur(
				ResourceContext<User, CoreAuthUserEntity, CoreAuthUserEntity> context,
				ExtractMetaDataEvent event) throws Throwable {
			UserResourceService.this.getAuthorityOptionUtil().extract(context,
					event);
		}

	}

	@Publish
	protected final class LoadSystemOptionsMetaDataEventListener extends
			EventListener<LoadMetaDataEvent> {

		public LoadSystemOptionsMetaDataEventListener() {
			super(UserResourceService.LISTENER_PRIORITY);
		}

		@Override
		protected void occur(
				ResourceContext<User, CoreAuthUserEntity, CoreAuthUserEntity> context,
				LoadMetaDataEvent event) throws Throwable {
			UserResourceService.this.getAuthorityOptionUtil().load(context,
					event);
		}

	}

}
