package org.eclipse.jt.core.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jt.core.invoke.SimpleTask;
import org.eclipse.jt.core.spi.http.HttpConnection;


public abstract class HttpConnectionImpl extends SimpleTask implements
		HttpConnection {
	String pathInContext;
	String pathParams;
	HttpServletRequest request;
	HttpServletResponse response;

	public void setContentType(String type) {
		this.response.setContentType(type);
	}

	public final String getContentType() {
		return this.request.getContentType();
	}

	public final void setContentLength(int length) {
		this.response.setContentLength(length);
	}

	public final InputStream getInputStream() throws IOException {
		return this.request.getInputStream();
	}

	public final OutputStream getOutputStream() throws IOException {
		return this.response.getOutputStream();
	}

	public final String getParameter(String name) {
		return this.request.getParameter(name);
	}

	public final String getHeader(String name) {
		return this.request.getHeader(name);
	}

	public final String getRequestMethod() {
		return this.request.getMethod();
	}

	public final void setHeader(String name, String value) {
		this.response.setHeader(name, value);
	}

	public final void setStatus(int status) {
		this.response.setStatus(status);

	}

}
