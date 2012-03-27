package org.eclipse.jt.core;

/**
 * 远程登录信息
 * 
 * @author Jeff Tang
 * 
 */
public interface RemoteLoginInfo {
    /**
     * 获得空间所在应用中间件的主机
     */
    String getHost();

    /**
     * 获得空间所在应用中间件的端口
     */
    int getPort();

    /**
     * 检查是否使用安全（TLS/SSL）的连接
     */
    boolean isSecure();

    /**
     * 获取该空间连接的登陆用户
     */
    public String getUser();

    /**
     * 获得远程登录的生命周期
     */
    public RemoteLoginLife getLife();
}
