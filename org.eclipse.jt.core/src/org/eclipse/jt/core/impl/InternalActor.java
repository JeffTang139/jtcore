package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.auth.ActorState;
import org.eclipse.jt.core.type.GUID;


/**
 * 内部访问者基础类
 * 
 * @see org.eclipse.jt.core.impl.IInternalActor
 * @author Jeff Tang
 */
public abstract class InternalActor extends NamedDefineImpl implements
		IInternalActor {

	public static final GUID GLOBAL_ORG_ID = CoreAuthActorEntity.GLOBAL_ORG_ID;

	public final GUID getID() {
		return this.id;
	}

	public ActorState getState() {
		return ActorState.NORMAL;
	}

	public final boolean supportAuthority() {
		return true;
	}

	public final boolean hasMappingOrg(GUID orgID) {
		return false;
	}

	public final GUID getMappingOrganizationID(int index) {
		return null;
	}

	public final int getMappingOrganizationCount() {
		return 0;
	}

	/**
	 * 不支持该操作
	 */
	@Override
	public final String getXMLTagName() {
		throw new UnsupportedOperationException();
	}

	/**
	 * 构造一个内部访问者对象
	 * 
	 * @param id
	 *            访问者ID
	 * @param name
	 *            访问者名称
	 * @param title
	 *            访问者标题
	 * @param description
	 *            访问者描述信息
	 */
	InternalActor(GUID id, String name, String title, String description) {
		super(name);
		this.id = id;
		this.title = title;
		this.description = description;
	}

	/**
	 * 操作者ID
	 */
	final GUID id;

	private static final long[] EMPTY_ACL = new long[] {};

	private static final long[][] EMPTY_ACLS = new long[][] { EMPTY_ACL };

	public final long[] getOperationACL(ContextImpl<?, ?, ?> context, GUID orgID) {
		return EMPTY_ACL;
	}

	public final long[][] getOperationACLs(ContextImpl<?, ?, ?> context,
			GUID orgID) {
		return EMPTY_ACLS;
	}

	public final long[] getAccreditACL(ContextImpl<?, ?, ?> context, GUID orgID) {
		return EMPTY_ACL;
	}

	public final long[][] getAccreditACLs(ContextImpl<?, ?, ?> context,
			GUID orgID) {
		return EMPTY_ACLS;
	}

}
