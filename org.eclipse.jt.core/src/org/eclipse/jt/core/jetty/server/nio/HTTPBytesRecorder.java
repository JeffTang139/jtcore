package org.eclipse.jt.core.jetty.server.nio;

/**
 * HTTP请求与相应的字节记录器
 * 
 * @author Jeff Tang
 * 
 */
public interface HTTPBytesRecorder {
	public void onHTTPBytes(long request, long respose);
}
