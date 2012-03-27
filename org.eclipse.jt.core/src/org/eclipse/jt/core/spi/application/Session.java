package org.eclipse.jt.core.spi.application;

import java.util.Locale;

import org.eclipse.jt.core.Login;
import org.eclipse.jt.core.exception.SessionDisposedException;
import org.eclipse.jt.core.exception.SituationReentrantException;
import org.eclipse.jt.core.type.GUID;


/**
 * 会话接口
 * 
 * @author Jeff Tang
 * 
 */
public interface Session extends Login {
	/**
	 * 会话的验证码
	 */
	public long getVerificationCode();

	/**
	 * 创建会话
	 * 
	 * @param asSituation
	 *            是否做为情景上下文（运行UI主线程）
	 * @throws SessionDisposedException
	 *             会话已经过期
	 * @throws SituationReentrantException
	 *             如果是情景上下文，则报告情景重入异常（已经存在正在运行未退出的UI主线程）
	 */
	public <TUserData> ContextSPI newContext(boolean asSituation)
			throws SessionDisposedException, SituationReentrantException;

	/**
	 * 获取Application
	 */
	public Application getApplication();

	/**
	 * 获得根情景对象
	 */
	public SituationSPI getSituation();

	/**
	 * 重置情景对象
	 */
	public SituationSPI resetSituation();

	/**
	 * 获取会话数据数据
	 */
	public Object getData();

	/**
	 * 设置会话数据
	 */
	public Object setData(Object data);

	/**
	 * 获取远程信息
	 */
	public RemoteInfoSPI getRemoteInfo();

	/**
	 * 设置会话的方位
	 */
	public void setLocale(Locale locale);

	/**
	 * 默认的心跳超时时间：5分钟
	 */
	public final static int DEFAULT_HEARTBEAT_SECs = 60 * 5;

	/**
	 * 获得心跳超时时间（秒），0表示永不超时，默认5分钟
	 */
	public int getHeartbeatTimeoutSec();

	/**
	 * 设置心跳超时时间（秒），0表示永不超时，默认为5分钟
	 */
	public void setHeartbeatTimeoutSec(int heartbeatTimeoutSec);

	/**
	 * 默认的会话超时时间：30分钟
	 */
	public final static int DEFAULT_TIMEOUT_MINUTEs = 30;

	/**
	 * 获得会话超时时间（分钟），0表示永不超时，默认30分钟
	 */
	public int getSessionTimeoutMinutes();

	/**
	 * 设置会话超时时间（分钟），0表示永不超时，默认30分钟
	 */
	public void setSessionTimeoutMinutes(int sessionTimeoutMinutes);

	/**
	 * 获得最后的交互时间，只针对普通会话有效
	 */
	public long getLastInteractiveTime();

	/**
	 * 尝试销毁，在超时前等待，超时后强制关闭所有上下文
	 * 
	 * @param timeout
	 *            毫秒数，<=0表示立即销毁
	 */
	public void dispose(long timeout);

	/**
	 * 权限相关，设置用户当前的组织机构映射
	 * 
	 * @param context
	 *            当前上下文，不能为空
	 * @param orgID
	 *            组织机构ID，不能为空
	 */
	public void setUserCurrentOrg(GUID orgID);

}
