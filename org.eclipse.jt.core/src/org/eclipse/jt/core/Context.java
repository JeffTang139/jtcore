package org.eclipse.jt.core;

import org.eclipse.jt.core.auth.Authority;
import org.eclipse.jt.core.auth.Operation;
import org.eclipse.jt.core.auth.Role;
import org.eclipse.jt.core.auth.RoleAuthorityChecker;
import org.eclipse.jt.core.auth.UserAuthorityChecker;
import org.eclipse.jt.core.da.DBAdapter;
import org.eclipse.jt.core.info.InfoReporter;
import org.eclipse.jt.core.resource.CategorialResourceQuerier;
import org.eclipse.jt.core.resource.ResourceQuerier;
import org.eclipse.jt.core.resource.ResourceStub;
import org.eclipse.jt.core.service.ServiceInvoker;
import org.eclipse.jt.core.type.GUID;


/**
 * 调用上下文接口，整合了资源请求器，模块调用器，模型定位器和进度设置接口
 * 
 * @author Jeff Tang
 * 
 */
public interface Context extends ServiceInvoker, ResourceQuerier, DBAdapter,
		InfoReporter, Localizer {
	/**
	 * 返回是否有效
	 */
	public boolean isValid();

	/**
	 * 检查是否有效
	 */
	public void checkValid();

	/**
	 * 获得上下文的类型
	 */
	public ContextKind getKind();

	/**
	 * 获得当前站点的状态
	 */
	public SiteState getSiteState();

	/**
	 * 创建对象
	 */
	public <TObject> TObject newObject(Class<TObject> clazz,
			Object... aditionalArgs);

	/**
	 * 获得站点的唯一ID，该ID只和站点的数据库有关
	 */
	public GUID getSiteID();

	/**
	 * 获得站点的简单唯一ID，该ID只和站点的数据库有关
	 */
	public int getSiteSimpleID();

	/**
	 * 抛出的异常对象<br>
	 * 但为了保证异常通道，因此还需要沿用如下语法<br>
	 * 
	 * <pre>
	 * public void hasException()throws XException{
	 *     ...
	 * }
	 * public void foo(Context context){//或者从其他地方获得context
	 *    try{
	 *        hasException();
	 *    }catch(XException e){ //可以简化成：catch(Throwable e)
	 *        throw context.throwThrowable(e);
	 *    }
	 * }
	 * </pre>
	 * 
	 * @param throwable
	 *            需要抛出的异常对象
	 */
	@Deprecated
	public RuntimeException throwThrowable(Throwable throwable);

	/**
	 * 获得所属登陆
	 */
	public Login getLogin();

	/**
	 * 切换登陆用户
	 * 
	 * @param user
	 *            欲切换的用户
	 * @return 返回切换前的旧用户
	 */
	public User changeLoginUser(User user);

	/**
	 * 检查是否对某资源拥有某类权限<br>
	 * 请不要使用该方法用作为过滤有权限的资源项，<br>
	 * 请使用getXXX(Operation<? super TFacade> operation,...)<br>
	 * 或findXXX(Operation<? super TFacade> operation,...)<br>
	 * 
	 * @param operation
	 *            操作
	 * @param resource
	 *            资源
	 * @return 返回是权限信息
	 */
	public <TFacade> Authority getAuthority(
			Operation<? super TFacade> operation, ResourceStub<TFacade> resource);

	/**
	 * 检查是否对某资源拥有某类权限，<br>
	 * 请不要使用该方法用作为过滤有权限的资源项，<br>
	 * 请使用getXXX(Operation<? super TFacade> operation,...)<br>
	 * 或findXXX(Operation<? super TFacade> operation,...)<br>
	 * 
	 * @param operation
	 *            操作
	 * @param resource
	 *            资源
	 * @return 返回是否拥有权限
	 */
	public <TFacade> boolean hasAuthority(Operation<? super TFacade> operation,
			ResourceStub<TFacade> resource);

	/**
	 * 由于该方法执行效率较低，不建设频繁大量调用该方法。
	 */
	public <TFacade> Authority getAccreditAuthority(
			Operation<? super TFacade> operation, ResourceStub<TFacade> resource);

	/**
	 * 由于该方法执行效率较低，不建设频繁大量调用该方法。
	 */
	public <TFacade> boolean hasAccreditAuthority(
			Operation<? super TFacade> operation, ResourceStub<TFacade> resource);

	/**
	 * 返回一个在指定分类中查找资源的查询对象
	 * 
	 * @param category
	 *            指定的资源分类
	 * @return 返回一个在指定分类中查找资源的查询对象
	 */
	public CategorialResourceQuerier usingResourceCategory(Object category);

	/**
	 * 获取远程匿名登陆信息
	 * 
	 * @param host
	 *            远程主机名或IP
	 * @param port
	 *            远程端口号
	 * @return 返回远程匿名登陆信息
	 */
	public RemoteLoginInfo allocRemoteLoginInfo(String host, int port);

	/**
	 * 获取远程登陆信息
	 * 
	 * @param host
	 *            远程主机名或IP
	 * @param port
	 *            远程端口号
	 * @param user
	 *            登陆用名称
	 * @param password
	 *            登陆用密码
	 * @return 返回远程登陆信息
	 */
	public RemoteLoginInfo allocRemoteLoginInfo(String host, int port,
			String user, String password);

	/**
	 * 获取远程登陆信息
	 * 
	 * @param host
	 *            远程主机名或IP
	 * @param port
	 *            远程端口号
	 * @param user
	 *            登陆用名称
	 * @param password
	 *            登陆用密码
	 * @param life
	 *            登陆生命周期设置
	 * @return 返回远程登陆信息
	 */
	public RemoteLoginInfo allocRemoteLoginInfo(String host, int port,
			String user, String password, RemoteLoginLife life);

	/**
	 * 使用远程服务调用器
	 * 
	 * @param remoteLoginInfo
	 *            远程登陆信息
	 * @return 返回对应的远程服务调用器
	 */
	public ServiceInvoker usingRemoteInvoker(RemoteLoginInfo remoteLoginInfo);

	public void setUserCurrentOrg(GUID orgID);

	public GUID getUserCurrentOrg();

	public UserAuthorityChecker newUserAuthorityChecker(User user, GUID orgID,
			boolean operationAuthority);

	public RoleAuthorityChecker newRoleAuthorityChecker(Role role, GUID orgID,
			boolean operationAuthority);

	public UserAuthorityChecker newUserAuthorityChecker(GUID userID,
			GUID orgID, boolean operationAuthority);

	public RoleAuthorityChecker newRoleAuthorityChecker(GUID roleID,
			GUID orgID, boolean operationAuthority);

}
