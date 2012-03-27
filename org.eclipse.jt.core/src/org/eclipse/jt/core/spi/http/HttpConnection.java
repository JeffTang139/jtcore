package org.eclipse.jt.core.spi.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * http连接
 * 
 * @author Jeff Tang
 * 
 */
public interface HttpConnection {
	/**
	 * 输出流，返回客户端
	 * 
	 * @return
	 */
	public OutputStream getOutputStream() throws IOException;

	/**
	 * 输入流，客户端post的内容
	 */
	public InputStream getInputStream() throws IOException;

	/**
	 * 根据名称获得URL的参数
	 */
	public String getParameter(String name);

	/**
	 * 获取请求头参数
	 */
	public String getHeader(String name);

	/**
	 * 获得请求的方法
	 */
	public String getRequestMethod();

	public void setContentType(String type);

	public String getContentType();

	public void setContentLength(int length);

	/**
	 * 设置返回头参数
	 */
	public void setHeader(String name, String value);

	/**
	 * 回写状态
	 */
	public void setStatus(int status);

}
