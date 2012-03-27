package org.eclipse.jt.core.auth;

/**
 * 角色<br>
 * 角色可以分配给多个用户，被分配了当前角色的所有用户，都继承了当前角色的所有权限。
 * 
 * @see org.eclipse.jt.core.auth.Actor
 * @author Jeff Tang 2009-11
 */
public interface Role extends Actor {
	
}
