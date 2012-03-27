package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.auth.ActorState;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.type.GUID;


/**
 * �����ߴ�����
 * 
 * @param <TActorEntity>
 *            ������ʵ����
 * @see org.eclipse.jt.core.auth.Actor
 * @author Jeff Tang 2009-12
 */
abstract class ActorProxy<TActorEntity extends CoreAuthActorEntity> implements
		IInternalActor {

	/**
	 * �Բ�������Դ���������
	 */
	final ResourceItem<?, TActorEntity, ?> resourceItemRef;

	public final GUID getID() {
		return this.resourceItemRef.getImpl().RECID;
	}

	public final String getName() {
		return this.resourceItemRef.getImpl().name;
	}

	public final String getTitle() {
		return this.resourceItemRef.getImpl().title;
	}

	public final ActorState getState() {
		return this.resourceItemRef.getImpl().state;
	}

	public final String getDescription() {
		return this.resourceItemRef.getImpl().description;
	}

	public final boolean supportAuthority() {
		return true;
	}

	public final GUID getMappingOrganizationID(int index) {
		return this.resourceItemRef.getImpl().getMappingOrganizationID(index);
	}

	public final int getMappingOrganizationCount() {
		return this.resourceItemRef.getImpl().getMappingOrganizationCount();
	}

	public final boolean hasMappingOrg(GUID orgID) {
		return this.resourceItemRef.getImpl().hasMappingOrg(orgID);
	}

	/**
	 * ��ȡ�������õ�ʵ�����
	 * 
	 * @return ���ش������õ�ʵ�����
	 */
	final TActorEntity getEntity() {
		return this.resourceItemRef.getImpl();
	}

	protected ActorProxy(ResourceItem<?, TActorEntity, ?> resourceItemRef) {
		if (resourceItemRef == null) {
			throw new NullArgumentException("resourceItemRef");
		}
		this.resourceItemRef = resourceItemRef;
	}

}
