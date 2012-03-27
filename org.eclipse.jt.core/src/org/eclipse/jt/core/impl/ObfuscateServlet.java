package org.eclipse.jt.core.impl;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jt.core.misc.Obfuscater;


/**
 * ��������Servlet
 * 
 * @author Jeff Tang
 * 
 */
public final class ObfuscateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private long lastTime;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		@SuppressWarnings("unchecked")
		final Enumeration<String> names = req.getParameterNames();
		if (names.hasMoreElements()) {
			final String obf = names.nextElement();
			if (!names.hasMoreElements()) {
				final String v = req.getParameter(obf);
				if (v == null || v.length() == 0) {
					final long now = System.currentTimeMillis();
					final String respStr;
					final long c = now - this.lastTime;
					if (c > 5000) {
						respStr = "��������Ϊ���Һ���ַ�����<br>".concat(Obfuscater
								.obfuscate(obf));
					} else {
						respStr = "������Ƶ�����ã���5���Ӻ����ԣ�";
					}
					this.lastTime = now;
					resp.setCharacterEncoding("GBK");
					resp.getWriter().write(respStr);
					return;
				}
			}
		}
		super.doGet(req, resp);
	}
}
