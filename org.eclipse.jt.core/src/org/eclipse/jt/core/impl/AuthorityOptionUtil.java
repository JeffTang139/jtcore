package org.eclipse.jt.core.impl;

import java.util.List;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.auth.ActorState;
import org.eclipse.jt.core.da.ORMAccessor;
import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.spi.metadata.ExtractMetaDataEvent;
import org.eclipse.jt.core.spi.metadata.LoadMetaDataEvent;
import org.eclipse.jt.core.spi.metadata.MetaDataEntry;


final class AuthorityOptionUtil {

	final TD_CoreAuthUser td_CoreAuthUser;
	final TD_CoreAuthRole td_CoreAuthRole;
	final TD_CoreAuthRA td_CoreAuthRA;
	final TD_CoreAuthUOM td_CoreAuthUOM;
	final TD_CoreAuthACL td_CoreAuthACL;
	final TD_CoreAuthAuthACL td_CoreAuthAuthACL;

	private AuthorityOptionUtil(final TD_CoreAuthUser td_CoreAuthUser,
			final TD_CoreAuthRole td_CoreAuthRole,
			final TD_CoreAuthRA td_CoreAuthRA,
			final TD_CoreAuthUOM td_CoreAuthUOM,
			final TD_CoreAuthACL td_CoreAuthACL,
			final TD_CoreAuthAuthACL td_CoreAuthAuthACL) {
		this.td_CoreAuthUser = td_CoreAuthUser;
		this.td_CoreAuthRole = td_CoreAuthRole;
		this.td_CoreAuthRA = td_CoreAuthRA;
		this.td_CoreAuthUOM = td_CoreAuthUOM;
		this.td_CoreAuthACL = td_CoreAuthACL;
		this.td_CoreAuthAuthACL = td_CoreAuthAuthACL;
	}

	static final AuthorityOptionUtil getInstance(
			final TD_CoreAuthUser td_CoreAuthUser,
			final TD_CoreAuthRole td_CoreAuthRole,
			final TD_CoreAuthRA td_CoreAuthRA,
			final TD_CoreAuthUOM td_CoreAuthUOM,
			final TD_CoreAuthACL td_CoreAuthACL,
			final TD_CoreAuthAuthACL td_CoreAuthAuthACL) {
		if (instance == null) {
			synchronized (AuthorityOptionUtil.class) {
				if (instance == null) {
					instance = new AuthorityOptionUtil(td_CoreAuthUser,
							td_CoreAuthRole, td_CoreAuthRA, td_CoreAuthUOM,
							td_CoreAuthACL, td_CoreAuthAuthACL);
				}
			}
		}
		return instance;
	}

	private static AuthorityOptionUtil instance;

	final void extract(Context context, ExtractMetaDataEvent event) {
		if (!event.extractAll()) {
			return;
		}
		event.getMetaStream().newEntry(name, version, des,
				this.serializationAuthInfoToXML(context));
	}

	final void load(Context context, LoadMetaDataEvent event) throws Throwable {
		final MetaDataEntry entry = event.getMetaStream().getRootEntry()
				.findSub(name);
		if (entry != null) {
			this.de_serializationAuthorityInfoFromXML(context, event
					.getMetaStream().getEntryAsXML(entry).firstChild());
		}
	}

	private static String name = "AUTHORITYINFORMATION";
	private static long version = 1;
	private static String des = "内置用户、角色、权限参数重新";

	private static final String XMLRootTagName = "root";

	private static final String userXMLTagName_Root = "user";
	private static final String userXMLTagName_RECID = "user_REC";
	private static final String userXMLTagName_name = "user_nam";
	private static final String userXMLTagName_title = "user_tit";
	private static final String userXMLTagName_password = "user_pas";
	private static final String userXMLTagName_state = "user_sta";
	private static final String userXMLTagName_priorityInfo = "user_pri";
	private static final String userXMLTagName_description = "user_des";

	private final void serializationUserToXML(SXElement userEle,
			CoreAuthUserEntity user) {
		userEle.setGUID(userXMLTagName_RECID, user.RECID);
		userEle.setString(userXMLTagName_name, user.name);
		userEle.setString(userXMLTagName_title, user.title);
		userEle.setGUID(userXMLTagName_password, user.password);
		userEle.setEnum(userXMLTagName_state, user.state);
		userEle.setInt(userXMLTagName_priorityInfo, user.priorityInfo);
		userEle.setString(userXMLTagName_description, user.description);
	}

	private final CoreAuthUserEntity de_serializationUserFromXML(
			SXElement userEle) {
		final CoreAuthUserEntity userEntity = new CoreAuthUserEntity();
		userEntity.RECID = userEle.getGUID(userXMLTagName_RECID);
		userEntity.name = userEle.getString(userXMLTagName_name);
		userEntity.title = userEle.getString(userXMLTagName_title);
		userEntity.password = userEle.getGUID(userXMLTagName_password);
		userEntity.state = userEle.getEnum(ActorState.class,
				userXMLTagName_state);
		userEntity.priorityInfo = userEle.getInt(userXMLTagName_priorityInfo);
		userEntity.description = userEle.getString(userXMLTagName_description);
		return userEntity;
	}

	private static final String roleXMLTagName_Root = "role";
	private static final String roleXMLTagName_RECID = "role_REC";
	private static final String roleXMLTagName_name = "role_nam";
	private static final String roleXMLTagName_title = "role_tit";
	private static final String roleXMLTagName_state = "role_sta";
	private static final String roleXMLTagName_description = "role_des";

	private final void serializationRoleToXML(SXElement roleEle,
			CoreAuthRoleEntity role) {
		roleEle.setGUID(roleXMLTagName_RECID, role.RECID);
		roleEle.setString(roleXMLTagName_name, role.name);
		roleEle.setString(roleXMLTagName_title, role.title);
		roleEle.setEnum(roleXMLTagName_state, role.state);
		roleEle.setString(roleXMLTagName_description, role.description);
	}

	private final CoreAuthRoleEntity de_serializationRoleFromXML(
			SXElement roleEle) {
		final CoreAuthRoleEntity roleEntity = new CoreAuthRoleEntity();
		roleEntity.RECID = roleEle.getGUID(roleXMLTagName_RECID);
		roleEntity.name = roleEle.getString(roleXMLTagName_name);
		roleEntity.title = roleEle.getString(roleXMLTagName_title);
		roleEntity.state = roleEle.getEnum(ActorState.class,
				roleXMLTagName_state);
		roleEntity.description = roleEle.getString(roleXMLTagName_description);
		return roleEntity;
	}

	private static final String raXMLTagName_Root = "ra";
	private static final String raXMLTagName_RECID = "ra_REC";
	private static final String raXMLTagName_actorID = "ra_act";
	private static final String raXMLTagName_roleID = "ra_role";
	private static final String raXMLTagName_priority = "ra_pri";

	private final void serializationRAToXML(SXElement raEle, CoreAuthRAEntity ra) {
		raEle.setGUID(raXMLTagName_RECID, ra.RECID);
		raEle.setGUID(raXMLTagName_actorID, ra.actorID);
		raEle.setGUID(raXMLTagName_roleID, ra.roleID);
		raEle.setInt(raXMLTagName_priority, ra.priority);
	}

	private final CoreAuthRAEntity de_serializationRAFromXML(SXElement raEle) {
		final CoreAuthRAEntity raEntity = new CoreAuthRAEntity();
		raEntity.RECID = raEle.getGUID(raXMLTagName_RECID);
		raEntity.actorID = raEle.getGUID(raXMLTagName_actorID);
		raEntity.roleID = raEle.getGUID(raXMLTagName_roleID);
		raEntity.priority = raEle.getInt(raXMLTagName_priority);
		return raEntity;
	}

	private static final String uomXMLTagName_Root = "uom";
	private static final String uomXMLTagName_RECID = "uom_REC";
	private static final String uomXMLTagName_actorID = "uom_act";
	private static final String uomXMLTagName_orgID = "uom_org";

	private final void serializationUOMToXML(SXElement uomEle,
			CoreAuthUOMEntity uom) {
		uomEle.setGUID(uomXMLTagName_RECID, uom.RECID);
		uomEle.setGUID(uomXMLTagName_actorID, uom.actorID);
		uomEle.setGUID(uomXMLTagName_orgID, uom.orgID);
	}

	private final CoreAuthUOMEntity de_serializationUOMFromXML(SXElement uomEle) {
		final CoreAuthUOMEntity uomEntity = new CoreAuthUOMEntity();
		uomEntity.RECID = uomEle.getGUID(uomXMLTagName_RECID);
		uomEntity.actorID = uomEle.getGUID(uomXMLTagName_actorID);
		uomEntity.orgID = uomEle.getGUID(uomXMLTagName_orgID);
		return uomEntity;
	}

	private static final String aclXMLTagName_Root = "acl";
	private static final String aclXMLTagName_RECID = "acl_REC";
	private static final String aclXMLTagName_actorID = "acl_act";
	private static final String aclXMLTagName_authCode = "acl_aut";
	private static final String aclXMLTagName_orgID = "acl_org";
	private static final String aclXMLTagName_resID = "acl_res";
	private static final String aclXMLTagName_rescID = "acl_resc";

	private final void serializationACLToXML(SXElement aclEle,
			CoreAuthACLEntity acl) {
		aclEle.setGUID(aclXMLTagName_RECID, acl.RECID);
		aclEle.setGUID(aclXMLTagName_actorID, acl.actorID);
		aclEle.setInt(aclXMLTagName_authCode, acl.authorityCode);
		aclEle.setGUID(aclXMLTagName_orgID, acl.orgID);
		aclEle.setGUID(aclXMLTagName_resID, acl.resourceID);
		aclEle.setGUID(aclXMLTagName_rescID, acl.resCategoryID);
	}

	private final CoreAuthACLEntity de_serializationACLFromXML(SXElement aclEle) {
		final CoreAuthACLEntity aclEntity = new CoreAuthACLEntity();
		aclEntity.RECID = aclEle.getGUID(aclXMLTagName_RECID);
		aclEntity.actorID = aclEle.getGUID(aclXMLTagName_actorID);
		aclEntity.authorityCode = aclEle.getInt(aclXMLTagName_authCode);
		aclEntity.orgID = aclEle.getGUID(aclXMLTagName_orgID);
		aclEntity.resourceID = aclEle.getGUID(aclXMLTagName_resID);
		aclEntity.resCategoryID = aclEle.getGUID(aclXMLTagName_rescID);
		return aclEntity;
	}

	private static final String authAclXMLTagName_Root = "authAcl";

	static final class AuthorityInformation {

		public List<CoreAuthUserEntity> users;
		public List<CoreAuthRoleEntity> roles;
		public List<CoreAuthRAEntity> ras;
		public List<CoreAuthUOMEntity> uoms;
		public List<CoreAuthACLEntity> acls;
		public List<CoreAuthACLEntity> authAcls;

	}

	private final SXElement serializationAuthInfoToXML(Context context) {
		SXElement xml = SXElement.newDoc();
		SXElement root = xml.append(XMLRootTagName);
		// User
		final ORMAccessor<CoreAuthUserEntity> ormUser = context
				.newORMAccessor(this.td_CoreAuthUser);
		for (CoreAuthUserEntity user : ormUser.fetch()) {
			SXElement userEle = root.append(userXMLTagName_Root);
			this.serializationUserToXML(userEle, user);
		}
		// Role
		final ORMAccessor<CoreAuthRoleEntity> ormRole = context
				.newORMAccessor(this.td_CoreAuthRole);
		for (CoreAuthRoleEntity role : ormRole.fetch()) {
			SXElement roleEle = root.append(roleXMLTagName_Root);
			this.serializationRoleToXML(roleEle, role);
		}
		// RA
		final ORMAccessor<CoreAuthRAEntity> ormRA = context
				.newORMAccessor(this.td_CoreAuthRA);
		for (CoreAuthRAEntity ra : ormRA.fetch()) {
			SXElement raEle = root.append(raXMLTagName_Root);
			this.serializationRAToXML(raEle, ra);
		}
		// UOM
		final ORMAccessor<CoreAuthUOMEntity> ormUOM = context
				.newORMAccessor(this.td_CoreAuthUOM);
		for (CoreAuthUOMEntity uom : ormUOM.fetch()) {
			SXElement uomEle = root.append(uomXMLTagName_Root);
			this.serializationUOMToXML(uomEle, uom);
		}
		// ACL
		final ORMAccessor<CoreAuthACLEntity> ormACL = context
				.newORMAccessor(this.td_CoreAuthACL);
		for (CoreAuthACLEntity acl : ormACL.fetch()) {
			SXElement aclEle = root.append(aclXMLTagName_Root);
			this.serializationACLToXML(aclEle, acl);
		}
		// ACL
		final ORMAccessor<CoreAuthACLEntity> ormAuthACL = context
				.newORMAccessor(this.td_CoreAuthAuthACL);
		for (CoreAuthACLEntity acl : ormAuthACL.fetch()) {
			SXElement aclEle = root.append(authAclXMLTagName_Root);
			this.serializationACLToXML(aclEle, acl);
		}
		return xml;
	}

	private final void de_serializationAuthorityInfoFromXML(Context context,
			SXElement root) {
		this.clearAuthorityInfo(context);
		// User
		final ORMAccessor<CoreAuthUserEntity> ormUser = context
				.newORMAccessor(this.td_CoreAuthUser);
		for (SXElement userEle : root.getChildren(userXMLTagName_Root)) {
			ormUser.insert(this.de_serializationUserFromXML(userEle));
		}
		// Role
		final ORMAccessor<CoreAuthRoleEntity> ormRole = context
				.newORMAccessor(this.td_CoreAuthRole);
		for (SXElement roleEle : root.getChildren(roleXMLTagName_Root)) {
			ormRole.insert(this.de_serializationRoleFromXML(roleEle));
		}
		// RA
		final ORMAccessor<CoreAuthRAEntity> ormRA = context
				.newORMAccessor(this.td_CoreAuthRA);
		for (SXElement raEle : root.getChildren(raXMLTagName_Root)) {
			ormRA.insert(this.de_serializationRAFromXML(raEle));
		}
		// UOM
		final ORMAccessor<CoreAuthUOMEntity> ormUOM = context
				.newORMAccessor(this.td_CoreAuthUOM);
		for (SXElement uomEle : root.getChildren(uomXMLTagName_Root)) {
			ormUOM.insert(this.de_serializationUOMFromXML(uomEle));
		}
		// ACL
		final ORMAccessor<CoreAuthACLEntity> ormACL = context
				.newORMAccessor(this.td_CoreAuthACL);
		for (SXElement aclEle : root.getChildren(aclXMLTagName_Root)) {
			ormACL.insert(this.de_serializationACLFromXML(aclEle));
		}
		// ACL
		final ORMAccessor<CoreAuthACLEntity> ormAuthACL = context
				.newORMAccessor(this.td_CoreAuthAuthACL);
		for (SXElement aclEle : root.getChildren(authAclXMLTagName_Root)) {
			ormAuthACL.insert(this.de_serializationACLFromXML(aclEle));
		}
	}

	private final void clearAuthorityInfo(Context context) {
		// User
		context.executeUpdate(context.newDeleteStatement(this.td_CoreAuthUser));
		// Role
		context.executeUpdate(context.newDeleteStatement(this.td_CoreAuthRole));
		// RA
		context.executeUpdate(context.newDeleteStatement(this.td_CoreAuthRA));
		// UOM
		context.executeUpdate(context.newDeleteStatement(this.td_CoreAuthUOM));
		// ACL
		context.executeUpdate(context.newDeleteStatement(this.td_CoreAuthACL));
		// ACL
		context.executeUpdate(context
				.newDeleteStatement(this.td_CoreAuthAuthACL));
	}

}
