package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.auth.Role;
import org.eclipse.jt.core.def.obja.StructClass;

/**
 * ��ɫʵ�嶨��<br>
 * ��ɫʵ�嶨���Ӧ�ڽ�ɫ�����Ĵ洢�ṹ��
 * 
 * <pre>
 * ����                ����        �շ�
 * RECID            GUID       ��
 * name             String     ��
 * title            String     ��
 * state            ActorState ��
 * description      String     ��
 * </pre>
 * 
 * @see org.eclipse.jt.core.impl.IInternalRole
 * @see org.eclipse.jt.core.impl.CoreAuthActorEntity
 * @author Jeff Tang 2009-12
 */
@StructClass
final class CoreAuthRoleEntity extends CoreAuthActorEntity implements Role {

}
