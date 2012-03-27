package org.eclipse.jt.core.impl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jt.core.misc.MissingObjectException;
import org.eclipse.jt.core.type.GUID;


public class NetConnectionListener extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * GET������Է���������
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setHeader("Connection", "close");
		checkUserAgent(req, resp);
		// �������HTTP_HEADER_NODE_ID�ֶΣ�����Output��������������ѯNodeID������
		final String remoteNodeIDStr = req
				.getHeader(DnaHttpClient.HTTP_HEADER_NODE_ID);
		if (remoteNodeIDStr != null) {
			try {
				this.getApplication().ensureChannel(
						GUID.valueOf(remoteNodeIDStr)).attachServletOutput(
						resp.getOutputStream());
			} catch (Throwable e) {
				throw Utils.tryThrowException(e);
			}
		} else {
			// ����NodeID
			resp.setHeader(DnaHttpClient.HTTP_HEADER_NODE_ID, this
					.getApplication().localNodeID.toString());
		}
	}

	/**
	 * POST���ڽ��նԷ����͵�����
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setHeader("Connection", "close");
		checkUserAgent(req, resp);
		// �������HTTP_HEADER_NODE_ID�ֶΣ�����Output��������������ѯNodeID������
		String remoteNodeIDStr = req
				.getHeader(DnaHttpClient.HTTP_HEADER_NODE_ID);
		try {
			this.getApplication().ensureChannel(GUID.valueOf(remoteNodeIDStr))
					.attachServletInput(req.getInputStream());
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	/**
	 * ���UserAgent�Ƿ���Ч
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws IOException
	 */
	private static final void checkUserAgent(HttpServletRequest req,
			HttpServletResponse resp) throws IOException {
		String userAgent = req.getHeader("User-Agent");
		if (userAgent == null || !userAgent.equals(DnaHttpClient.USER_AGENT)) {
			throw new IOException("UserAgent���Ͳ���ȷ");
		}
	}

	private final ApplicationImpl getApplication() {
		final ApplicationImpl app = (ApplicationImpl) this.getServletContext()
				.getAttribute(JettyServer.servlet_context_attr_application);
		if (app == null) {
			throw new MissingObjectException("�Ҳ���Application");
		}
		return app;
	}
}
