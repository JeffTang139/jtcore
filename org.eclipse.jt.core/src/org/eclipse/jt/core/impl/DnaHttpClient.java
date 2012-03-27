package org.eclipse.jt.core.impl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.eclipse.jt.core.type.GUID;

import sun.net.www.MessageHeader;
import sun.net.www.http.ChunkedInputStream;
import sun.net.www.http.ChunkedOutputStream;
import sun.net.www.http.HttpClient;


class DnaHttpClient extends HttpClient {
	static final String HTTP_HEADER_NODE_ID = "DNA-NET-NodeID";
	static final String USER_AGENT = "DNA/2.5";
	static final String KEEP_ALIVE = "115";

	private final GUID localNodeID;

	public DnaHttpClient(URL address, GUID localNodeID) throws IOException {
		super(address, false);
		this.serverInput = new BufferedInputStream(this.serverSocket
				.getInputStream());
		this.localNodeID = localNodeID;
	}

	public final InputStream get(MessageHeader mh) throws IOException {
		if (mh == null) {
			mh = new MessageHeader();
		}
		mh.prepend("GET " + this.url.getPath() + " HTTP/1.1", null);
		mh.set("Host", this.url.getHost());
		mh.set("User-Agent", USER_AGENT);
		mh.add("Connection", "close");
		this.writeRequests(mh, null);
		this.serverOutput.flush();
		mh.parseHeader(this.serverInput);
		String resp = mh.getValue(0);
		if (resp == null || !resp.startsWith("HTTP/1.1 200 OK")) {
			throw new IOException("收到错误的HTTP回复:" + resp);
		}
		return this.serverInput;
	}

	public final OutputStream openOutput() throws IOException {
		MessageHeader mh = new MessageHeader();
		mh.prepend("POST " + this.url.getPath() + " HTTP/1.1", null);
		mh.add("Host", this.url.getHost());
		mh.add("User-Agent", USER_AGENT);
		mh.add("Transfer-Encoding", "chunked");
		mh.add(HTTP_HEADER_NODE_ID, this.localNodeID.toString());
		mh.add("Keep-Alive", KEEP_ALIVE);
		mh.add("Connection", "keep-alive");
		this.writeRequests(mh, null);
		return new ChunkedOutputStream(this.serverOutput);
	}

	public final InputStream openInput() throws IOException {
		MessageHeader mh = new MessageHeader();
		mh.prepend("GET " + this.url.getPath() + " HTTP/1.1", null);
		mh.add("Host", this.url.getHost());
		mh.add("User-Agent", USER_AGENT);
		mh.add(HTTP_HEADER_NODE_ID, this.localNodeID.toString());
		mh.add("Keep-Alive", KEEP_ALIVE);
		mh.add("Connection", "keep-alive");
		this.writeRequests(mh, null);
		this.getOutputStream().flush();
		mh.parseHeader(this.serverInput);
		String resp = mh.getValue(0);
		if (!resp.startsWith("HTTP/1.1 200 OK")) {
			throw new IOException("收到错误的HTTP回复:" + resp);
		}
		if ("chunked".equalsIgnoreCase(mh.findValue("Transfer-Encoding"))) {
			return new ChunkedInputStream(this.serverInput, this, mh);
		}
		return this.serverInput;
	}
}
