package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.auth.Role;
import org.eclipse.jt.core.def.obja.StructClass;

/**
 * 角色实体定义<br>
 * 角色实体定义对应于角色物理表的存储结构。
 * 
 * <pre>
 * 列名                类型        空否
 * RECID            GUID       否
 * name             String     否
 * title            String     否
 * state            ActorState 否
 * description      String     是
 * </pre>
 * 
 * @see org.eclipse.jt.core.impl.IInternalRole
 * @see org.eclipse.jt.core.impl.CoreAuthActorEntity
 * @author Jeff Tang 2009-12
 */
@StructClass
final class CoreAuthRoleEntity extends CoreAuthActorEntity implements Role {

}
