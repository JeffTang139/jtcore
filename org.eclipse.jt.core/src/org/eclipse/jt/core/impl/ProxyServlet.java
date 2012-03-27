package org.eclipse.jt.core.impl;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.PathMap;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * ¥˙¿ÌServlet
 * 
 * @author Jeff Tang
 * 
 */
public final class ProxyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final ServletHandler jettyServletHandler;

	public ProxyServlet(ServletHandler jettyServletHandler) {
		this.jettyServletHandler = jettyServletHandler;
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse rsp)
			throws ServletException, IOException {
		final String path = req.getPathInfo();
		PathMap.Entry e = this.jettyServletHandler.getHolderEntry(path);
		if (e != null) {
			final Servlet servlet = ((ServletHolder) e.getValue()).getServlet();
			if (servlet != null) {
				servlet.service(req, rsp);
				return;
			}
		}
		super.service(req, rsp);
	}
}
