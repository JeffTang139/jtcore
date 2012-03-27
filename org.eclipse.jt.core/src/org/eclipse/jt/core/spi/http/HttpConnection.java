package org.eclipse.jt.core.spi.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * http����
 * 
 * @author Jeff Tang
 * 
 */
public interface HttpConnection {
	/**
	 * ����������ؿͻ���
	 * 
	 * @return
	 */
	public OutputStream getOutputStream() throws IOException;

	/**
	 * ���������ͻ���post������
	 */
	public InputStream getInputStream() throws IOException;

	/**
	 * �������ƻ��URL�Ĳ���
	 */
	public String getParameter(String name);

	/**
	 * ��ȡ����ͷ����
	 */
	public String getHeader(String name);

	/**
	 * �������ķ���
	 */
	public String getRequestMethod();

	public void setContentType(String type);

	public String getContentType();

	public void setContentLength(int length);

	/**
	 * ���÷���ͷ����
	 */
	public void setHeader(String name, String value);

	/**
	 * ��д״̬
	 */
	public void setStatus(int status);

}
