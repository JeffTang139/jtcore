package org.eclipse.jt.core.jetty.server.nio;

/**
 * HTTP��������Ӧ���ֽڼ�¼��
 * 
 * @author Jeff Tang
 * 
 */
public interface HTTPBytesRecorder {
	public void onHTTPBytes(long request, long respose);
}
