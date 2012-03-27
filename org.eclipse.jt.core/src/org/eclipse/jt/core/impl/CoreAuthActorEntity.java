package org.eclipse.jt.core.impl;

import java.util.List;

import org.eclipse.jt.core.auth.Actor;
import org.eclipse.jt.core.auth.ActorState;
import org.eclipse.jt.core.def.obja.StructClass;
import org.eclipse.jt.core.def.table.AsTable;
import org.eclipse.jt.core.def.table.AsTableField;
import org.eclipse.jt.core.def.table.AsTableField.DBType;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.type.GUID;


/**
 * ������ʵ�嶨��
 * 
 * @see org.eclipse.jt.core.auth.IInternalActor
 * @author Jeff Tang 2009-12
 */
@StructClass
@AsTable
abstract class CoreAuthActorEntity implements Actor {

	/**
	 * ��¼ID
	 */
	@AsTableField(isRecid = true)
	public GUID RECID;

	/**
	 * ����������
	 */
	@AsTableField(dbType = DBType.Varchar, length = NAME_FIELD_SIZE, isRequired = true)
	public String name;

	/**
	 * �����߱���
	 */
	@AsTableField(dbType = DBType.Varchar, length = TITLE_FIELD_SIZE, isRequired = true)
	public String title;

	/**
	 * ������״̬
	 */
	@AsTableField(dbType = DBType.Varchar, length = STATE_FIELD_SIZE, isRequired = true)
	public ActorState state;

	/**
	 * ������������Ϣ
	 */
	@AsTableField(dbType = DBType.Varchar, length = DESCRIPTION_FIELD_SIZE)
	public String description;

	/**
	 * �����ֶο��
	 */
	static final int NAME_FIELD_SIZE = 100;

	/**
	 * �����ֶο��
	 */
	static final int TITLE_FIELD_SIZE = NAME_FIELD_SIZE;

	/**
	 * ״̬�ֶο��
	 */
	static final int STATE_FIELD_SIZE = 16;

	/**
	 * ������Ϣ�ֶο��
	 */
	static final int DESCRIPTION_FIELD_SIZE = 128;

	/**
	 * ������ȫ��ACL
	 */
	private volatile long[] globalACL;

	/**
	 * ��������֯����ID�б�
	 */
	private volatile GUID[] mappingOrgIDs;

	/**
	 * ���������֯����һһ��Ӧ��ACL�б�
	 */
	private volatile long[][] mappingACLs;

	/**
	 * ȫ����֯����ID
	 */
	static final GUID GLOBAL_ORG_ID = GUID.valueOf(0xAAAAAAAAAAAAAAAAL,
			0xAAAAAAAAAAAAAAAAL);

	/**
	 * ��ACL�����
	 */
	static final long[] NULL_ACL = Utils.emptyLongArray;

	private static final GUID[] NULL_MAPPING_ORG_IDS = Utils.emptyGUIDArray;

	private static final long[][] NULL_MAPPING_ACLS = new long[][] {};

	public final GUID getID() {
		return this.RECID;
	}

	public final String getName() {
		return this.name;
	}

	public final String getTitle() {
		return this.title;
	}

	public final ActorState getState() {
		return this.state;
	}

	public final String getDescription() {
		return this.description;
	}

	public final int getMappingOrganizationCount() {
		return this.mappingOrgIDs.length;
	}

	public final GUID getMappingOrganizationID(int index) {
		if (this.mappingOrgIDs == NULL_MAPPING_ORG_IDS) {
			return null;
		}
		if (index < 0 || this.mappingOrgIDs.length <= index) {
			return null;
		}
		return this.mappingOrgIDs[index];
	}

	final long[] getGlobalOperationACL(ContextImpl<?, ?, ?> context) {
		return this.ensureLoadOperationACL(context, GLOBAL_ORG_ID);
	}

	final long[] tryGetOperationACL(ContextImpl<?, ?, ?> context, GUID orgID) {
		return this.ensureLoadOperationACL(context, orgID);
	}

	final boolean hasMappingOrg(GUID orgID) {
		if (orgID == null) {
			throw new NullArgumentException("orgID");
		}
		if (orgID.equals(GLOBAL_ORG_ID)) {
			return true;
		}
		for (GUID mappingOrgID : this.mappingOrgIDs) {
			if (orgID.equals(mappingOrgID)) {
				return true;
			}
		}
		return false;
	}

	final void setGlobalOperationACL(long[] acl) {
		this.setOperationACL(GLOBAL_ORG_ID, acl);
	}

	final void setOperationACL(GUID orgID, long[] acl) {
		if (acl == null || acl.length == 0) {
			acl = NULL_ACL;
		}
		if (orgID == null || orgID.equals(GLOBAL_ORG_ID)) {
			this.globalACL = acl;
		} else {
			final GUID[] newMappingOrgIDs;
			final long[][] newMappingACLs;
			if (this.mappingOrgIDs == NULL_MAPPING_ORG_IDS) {
				newMappingOrgIDs = new GUID[] { orgID };
				newMappingACLs = new long[][] { acl };
			} else {
				final int size = this.mappingOrgIDs.length;
				for (int index = 0; index < size; index++) {
					GUID mappingOrgID = this.mappingOrgIDs[index];
					if (mappingOrgID.equals(orgID)) {
						this.mappingACLs[index] = acl;
						return;
					}
				}
				final int newSize = size + 1;
				newMappingOrgIDs = new GUID[newSize];
				newMappingACLs = new long[newSize][];
				System.arraycopy(this.mappingOrgIDs, 0, newMappingOrgIDs, 0,
						size);
				System.arraycopy(this.mappingACLs, 0, newMappingACLs, 0, size);
				newMappingOrgIDs[size] = orgID;
				newMappingACLs[size] = acl;
			}
			this.mappingOrgIDs = newMappingOrgIDs;
			this.mappingACLs = newMappingACLs;
		}
	}

	final void setMappingOrgIDs(GUID[] orgIDs) {
		if (orgIDs == null || orgIDs.length == 0) {
			this.mappingOrgIDs = NULL_MAPPING_ORG_IDS;
			this.mappingACLs = NULL_MAPPING_ACLS;
		} else {
			this.mappingOrgIDs = orgIDs;
			this.mappingACLs = new long[orgIDs.length][];
		}
	}

	/**
	 * ɾ��ָ����֯������ӳ���ϵ<br>
	 * ���ָ����֯������ȫ����֯��������ȫ��ACL��Ϊ��ACL.
	 * 
	 * @param orgID
	 *            ��֯����ID��Ϊ�մ���ȫ����֯����
	 * @return ���ض�Ӧ��ԭ����ACL���Ҳ��������ؿն���
	 */
	final long[] removeOrgMapping(GUID orgID) {
		final long[] tempACL;
		if (orgID == null || orgID.equals(GLOBAL_ORG_ID)) {
			tempACL = this.globalACL;
			this.globalACL = NULL_ACL;
		} else {
			if (this.mappingOrgIDs != NULL_MAPPING_ORG_IDS) {
				final int size = this.mappingOrgIDs.length;
				if (size == 1 && this.mappingOrgIDs[0].equals(orgID)) {
					tempACL = this.mappingACLs[0];
					this.mappingOrgIDs = NULL_MAPPING_ORG_IDS;
					this.mappingACLs = NULL_MAPPING_ACLS;
				} else {
					syn: {
						for (int index = 0; index < size; index++) {
							GUID mappingOrgID = this.mappingOrgIDs[index];
							if (mappingOrgID.equals(orgID)) {
								tempACL = this.mappingACLs[index];
								final int newSize = size - 1;
								final GUID[] newMappingOrgIDs = new GUID[newSize];
								final long[][] newMappingACLs = new long[newSize][];
								System.arraycopy(this.mappingOrgIDs, 0,
										newMappingOrgIDs, 0, index);
								System.arraycopy(this.mappingOrgIDs, index + 1,
										newMappingOrgIDs, index, newSize
												- index);
								System.arraycopy(this.mappingACLs, 0,
										newMappingACLs, 0, index);
								System.arraycopy(this.mappingACLs, index + 1,
										newMappingACLs, index, newSize - index);
								this.mappingOrgIDs = newMappingOrgIDs;
								this.mappingACLs = newMappingACLs;
								break syn;
							}
						}
						tempACL = null;
					}
				}
			} else {
				tempACL = null;
			}
		}
		return tempACL;
	}

	/**
	 * ��ȡ��ָ����֯������Ӧ��ACL<br>
	 * ���û�ҵ�������ȫ��ACL��
	 * 
	 * @param context
	 *            ������
	 * @param orgID
	 *            ��֯����ID��Ϊ�ձ�ʾȫ����֯����
	 * @return ������ָ����֯������Ӧ��ACL�����û�ҵ�������ȫ��ACL
	 */
	final long[] getOperationACL(ContextImpl<?, ?, ?> context, GUID orgID) {
		final long[] acl = this.ensureLoadOperationACL(context, orgID);
		return acl == null ? this
				.ensureLoadOperationACL(context, GLOBAL_ORG_ID) : acl;
	}

	/**
	 * ȷ����������ָ����֯������Ӧ��ACL
	 * 
	 * @param context
	 *            ������
	 * @param orgID
	 *            ��֯����ID��Ϊ�ձ�ʾȫ����֯����
	 * @return ���ض�Ӧ��ACL�����û�ҵ���֯����ӳ�䣬���ؿն���
	 */
	final long[] ensureLoadOperationACL(ContextImpl<?, ?, ?> context,
			final GUID orgID) {
		if (orgID == null || orgID.equals(GLOBAL_ORG_ID)) {
			if (this.globalACL == null) {
				synchronized (this) {
					if (this.globalACL == null) {
						this.globalACL = this.internalLoadOperationACL(context,
								GLOBAL_ORG_ID);
					}
				}
			}
			return this.globalACL;
		} else {
			if (this.mappingOrgIDs != NULL_MAPPING_ORG_IDS) {
				for (int index = 0, size = this.mappingOrgIDs.length; index < size; index++) {
					GUID mappingOrgID = this.mappingOrgIDs[index];
					if (mappingOrgID.equals(orgID)) {
						if (this.mappingACLs[index] == null) {
							synchronized (this) {
								if (this.mappingACLs[index] == null) {
									this.mappingACLs[index] = this
											.internalLoadOperationACL(context,
													orgID);
								}
							}
						}
						return this.mappingACLs[index];
					}
				}
			}
		}
		return null;
	}

	/**
	 * @return ���ܷ��ؿն������û����Ȩ��¼�����ؿ��б�
	 */
	private final long[] internalLoadOperationACL(ContextImpl<?, ?, ?> context,
			GUID orgID) {
		List<CoreAuthACLEntity> aclEntitys = ActorResourceService
				.getACLEntityByActorAndOrg(context, this.RECID, orgID);
		final int size = aclEntitys.size();
		if (size == 0) {
			return NULL_ACL;
		}
		long[] acl = ACLHelper.initACL(size);
		final GlobalResourceContainer globalResourceContainer = context.occorAt.site.globalResourceContainer;
		int index = 0;
		GUID lastCategoryID = null;
		ResourceGroup<?, ?, ?> group;
		CoreAuthACLEntity aclEntity = aclEntitys.get(index);
		while (index < size) {
			lastCategoryID = aclEntity.resCategoryID;
			group = globalResourceContainer.findAndResolveAuthResourceGroup(
					context, lastCategoryID);
			if (group != null) {
				do {
					@SuppressWarnings("unchecked")
					final ResourceItem resItem = group.findAuthResourceItem(
							context.transaction, aclEntity.resourceID);
					if (resItem != null) {
						acl = ACLHelper.setAuthCode(acl, resItem.id,
								aclEntity.authorityCode);
					} else if (aclEntity.resourceID == CoreAuthACLEntity.ROOT_RESOURCE_GUID) {
						acl = ACLHelper.setAuthCode(acl, group.id,
								aclEntity.authorityCode);
					} else {
						ActorResourceService.deleteACLRecordByResource(context,
								aclEntity.resCategoryID, aclEntity.resourceID);
					}
					index++;
					if (index < size) {
						aclEntity = aclEntitys.get(index);
					} else {
						break;
					}
				} while (aclEntity.resCategoryID.equals(lastCategoryID));
			} else {
				ActorResourceService.deleteACLRecordByResCategory(context,
						aclEntity.resCategoryID);
				do {
					index++;
					if (index < size) {
						aclEntity = aclEntitys.get(index);
					} else {
						break;
					}
				} while (aclEntity.resCategoryID.equals(lastCategoryID));
			}
		}
		if (ACLHelper.isEmpty(acl)) {
			return NULL_ACL;
		}
		return acl;
	}

	final long[] getAccreditACL(ContextImpl<?, ?, ?> context, GUID orgID) {
		if (orgID == null) {
			orgID = GLOBAL_ORG_ID;
		}
		long[] acl = this.internalLoadAccreditACL(context, orgID);
		if (acl == NULL_ACL && !(orgID.equals(GLOBAL_ORG_ID))) {
			acl = this.internalLoadAccreditACL(context, GLOBAL_ORG_ID);
		}
		return acl;
	}

	private final long[] internalLoadAccreditACL(ContextImpl<?, ?, ?> context,
			GUID orgID) {
		List<CoreAuthACLEntity> aclEntitys = ActorResourceService
				.getAuthACLEntityByActorAndOrg(context, this.RECID, orgID);
		final int size = aclEntitys.size();
		if (size == 0) {
			return NULL_ACL;
		}
		long[] acl = ACLHelper.initACL(size);
		final GlobalResourceContainer globalResourceContainer = context.occorAt.site.globalResourceContainer;
		int index = 0;
		GUID lastCategoryID = null;
		ResourceGroup<?, ?, ?> group;
		CoreAuthACLEntity aclEntity = aclEntitys.get(index);
		while (index < size) {
			lastCategoryID = aclEntity.resCategoryID;
			group = globalResourceContainer.findAndResolveAuthResourceGroup(
					context, lastCategoryID);
			if (group != null) {
				do {
					@SuppressWarnings("unchecked")
					final ResourceItem resItem = group.findAuthResourceItem(
							context.transaction, aclEntity.resourceID);
					if (resItem != null) {
						acl = ACLHelper.setAuthCode(acl, resItem.id,
								aclEntity.authorityCode);
					} else if (aclEntity.resourceID == CoreAuthACLEntity.ROOT_RESOURCE_GUID) {
						acl = ACLHelper.setAuthCode(acl, group.id,
								aclEntity.authorityCode);
					} else {
						ActorResourceService.deleteAuthACLRecordByResource(
								context, aclEntity.resCategoryID,
								aclEntity.resourceID);
					}
					index++;
					if (index < size) {
						aclEntity = aclEntitys.get(index);
					} else {
						break;
					}
				} while (aclEntity.resCategoryID.equals(lastCategoryID));
			} else {
				ActorResourceService.deleteAuthACLRecordByResCategory(context,
						aclEntity.resCategoryID);
				do {
					index++;
					if (index < size) {
						aclEntity = aclEntitys.get(index);
					} else {
						break;
					}
				} while (aclEntity.resCategoryID.equals(lastCategoryID));
			}
		}
		if (ACLHelper.isEmpty(acl)) {
			return NULL_ACL;
		}
		return acl;
	}

}
