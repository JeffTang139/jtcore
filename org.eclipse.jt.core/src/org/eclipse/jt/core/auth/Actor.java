package org.eclipse.jt.core.auth;

import org.eclipse.jt.core.impl.InternalActor;
import org.eclipse.jt.core.type.GUID;


/**
 * 访问者<br>
 * 访问者即为拥有权限的主体，可以被授予权限。当前访问者主要分为用户和角色两种。
 * 
 * @author Jeff Tang 2009-11
 */
public interface Actor {

	/**
	 * 默认组织机构ID
	 */
	public static final GUID GLOBAL_ORG_ID = InternalActor.GLOBAL_ORG_ID;
	
	/**
	 * 获得访问者ID<br>
	 * 访问者ID在同种访问者类别里面唯一。
	 * 
	 * @return 返回访问者ID，返回结果不可能为空
	 */
	public GUID getID();

	/**
	 * 访问者名称<br>
	 * 访问者名称在同种访问者类别里面唯一。
	 * 
	 * @return 返回访问者名称，返回结果不可能为空
	 */
	public String getName();

	/**
	 * 获取访问者标题<br>
	 * 一般情况下，访问者标题只用于显示。
	 * 
	 * @return 返回访问者标题，返回结果不可能为空
	 */
	public String getTitle();

	/**
	 * 获得访问者的状态
	 * 
	 * @see org.eclipse.jt.core.auth.ActorState
	 * @return 返回访问者当前状态
	 */
	public ActorState getState();

	/**
	 * 获取访问的描述信息
	 * 
	 * @return 返回访问者标题，返回结果可能为空
	 */
	public String getDescription();

	/**
	 * 获取访问者关联的组织机构数，包括默认关联的组织机构
	 * 
	 * @return 返回访问者关联的组织机构数
	 */
	public int getMappingOrganizationCount();

	/**
	 * 根据索引号获取对应关联的组织机构ID
	 * 
	 * @return 返回对应关联的组织机构ID，如果<code>getDefaultOrganizationID()</code>
	 *         返回的结果为空，此处返回的结果肯定也为空
	 */
	public GUID getMappingOrganizationID(int index);

}
