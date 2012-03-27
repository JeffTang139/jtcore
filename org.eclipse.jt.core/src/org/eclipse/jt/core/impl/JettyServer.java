package org.eclipse.jt.core.impl;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;

import org.eclipse.jetty.server.AbstractConnector;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ssl.SslSelectChannelConnector;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.FilterMapping;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.ServletMapping;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.jetty.DetachedSocketChannelHandler;
import org.eclipse.jt.core.jetty.DetachedSocketChannelWithSSLHandler;
import org.eclipse.jt.core.jetty.server.nio.DnaSSLSelectChannelConnector;
import org.eclipse.jt.core.jetty.server.nio.DnaSelectChannelConnector;
import org.eclipse.jt.core.jetty.server.nio.HTTPBytesRecorder;
import org.eclipse.jt.core.misc.ExceptionCatcher;
import org.eclipse.jt.core.misc.Obfuscater;
import org.eclipse.jt.core.misc.SXElement;


/**
 * HTTP服务器
 * 
 * @author Jeff Tang
 * 
 */
final class JettyServer implements HTTPBytesRecorder {
	private final AtomicLong requestBytes = new AtomicLong();
	private final AtomicLong resposeBytes = new AtomicLong();

	public final void onHTTPBytes(long request, long respose) {
		if (request > 0) {
			this.requestBytes.addAndGet(request);
		}
		if (respose > 0) {
			this.resposeBytes.addAndGet(respose);
		}
	}

	/**
	 * 获得HTTP请求的字节数累计
	 */
	public final long getHTTPRequestBytes() {
		return this.requestBytes.get();
	}

	/**
	 * 获得HTTP应答的字节数累计
	 */
	public final long getHTTPResponseBytes() {
		return this.resposeBytes.get();
	}

	private Server httpServer;
	private boolean supportHttpSession;
	private final ArrayList<AbstractConnector> connectors = new ArrayList<AbstractConnector>(
			1);
	private int defaultMaxThreads;
	private String appName;

	final void doDispose(ExceptionCatcher catcher) {
		try {
			this.stop();
		} catch (Throwable e) {
			catcher.catchException(e, e);
		}
	}

	static final int DEFAULT_PORT = 9797;
	static final int DEFAULT_MAX_THREADS = 500;
	static final int DEFAULT_ACCEPT_QUEUE_SIZE = 100;
	static final String DEFAULT_KEYSTORE = "dna.jks";
	static final int MAX_ACCEPTORS = Runtime.getRuntime().availableProcessors();

	final static String xml_attr_class = "class";
	final static String xml_attr_path = "path";
	final static String xml_attr_init_order = "init-order";

	static final String xml_element_servlet = "servlet";
	static final String xml_element_servlets = "servlets";
	static final String xml_element_filter = "filter";
	static final String xml_element_filters = "filters";
	static final String xml_element_http = "http";
	static final String xml_element_listen = "listen";
	static final String xml_attr_host = "host";
	static final String xml_attr_port = "port";
	static final String xml_attr_max_thread = "max-threads";
	static final String xml_attr_app_name = "path";
	static final String xml_attr_accept_queue_size = "accept-queue-size";
	static final String xml_attr_acceptor_threads = "acceptors";
	static final String xml_attr_ssl = "ssl";
	static final String xml_attr_http_sessions = "http-sessions";
	static final String xml_attr_ajp13 = "ajp13";
	static final String xml_attr_ssl_keystore = "ssl-keystore";
	static final String xml_attr_ssl_keystore_type = "ssl-keystore-type";
	static final String xml_attr_ssl_key_password = "ssl-key-password";
	static final String xml_attr_ssl_password = "ssl-password";

	private int http_port = -1;
	private int ssl_port = -1;

	synchronized final int getHttpPort() {
		return this.http_port;
	}

	synchronized final int getSslPort() {
		return this.ssl_port;
	}

	synchronized final void update(SXElement httpConfig,
			DetachedSocketChannelHandler dscHandler,
			DetachedSocketChannelWithSSLHandler dscSSLHandler) {
		final boolean actived = this.httpServer != null;
		this.stop();
		this.connectors.clear();
		if (httpConfig == null) {
			this.defaultMaxThreads = 0;
			return;
		}
		this.appName = httpConfig.getAttribute(xml_attr_app_name);
		if (this.appName != null && this.appName.length() == 0) {
			this.appName = null;
		}
		this.defaultMaxThreads = httpConfig.getInt(xml_attr_max_thread);
		this.supportHttpSession = httpConfig.getBoolean(xml_attr_http_sessions);
		for (SXElement listenE = httpConfig.firstChild(xml_element_listen); listenE != null; listenE = listenE
				.nextSibling(xml_element_listen)) {
			try {
				final String host = listenE.getAttribute(xml_attr_host, null);
				final int port = listenE.getInt(xml_attr_port, DEFAULT_PORT);
				final int maxThreads = listenE.getInt(xml_attr_max_thread);
				final int acceptQueueSize = listenE.getInt(
						xml_attr_accept_queue_size, DEFAULT_ACCEPT_QUEUE_SIZE);
				int acceptorThreads = listenE.getInt(xml_attr_acceptor_threads,
						MAX_ACCEPTORS);
				if (acceptorThreads <= 0) {
					acceptorThreads = 1;
				} else if (acceptorThreads > MAX_ACCEPTORS) {
					acceptorThreads = MAX_ACCEPTORS;
				}
				final AbstractConnector connector;
				if (listenE.getBoolean(xml_attr_ajp13, false)) {
					final Class<AbstractConnector> clazz = this.application.coreBundle
							.loadClass(
									"org.eclipse.jetty.ajp.Ajp13SocketConnector",
									AbstractConnector.class);
					connector = clazz.newInstance();
				} else if (listenE.getBoolean(xml_attr_ssl, false)) {
					final SslSelectChannelConnector sslConnector = new DnaSSLSelectChannelConnector(
							dscSSLHandler);
					connector = sslConnector;
					sslConnector.setKeystore(this.application.getDNAWork()
							.getPath()
							+ File.separatorChar
							+ listenE.getAttribute(xml_attr_ssl_keystore,
									DEFAULT_KEYSTORE));
					sslConnector.setKeyPassword(Obfuscater.unobfuscate(listenE
							.getAttribute(xml_attr_ssl_key_password)));
					sslConnector.setPassword(Obfuscater.unobfuscate(listenE
							.getAttribute(xml_attr_ssl_password)));
					sslConnector.setKeystoreType(listenE.getAttribute(
							xml_attr_ssl_keystore_type, sslConnector
									.getKeystoreType()));

					if (this.ssl_port == -1) {
						this.ssl_port = port;
					}
				} else {
					connector = new DnaSelectChannelConnector(dscHandler, this);

					if (this.http_port == -1) {
						this.http_port = port;
					}
				}
				if (host != null && host.length() > 0) {
					connector.setHost(host);
				}
				connector.setPort(port);
				connector.setAcceptQueueSize(acceptQueueSize);
				connector.setAcceptors(acceptorThreads);
				if (maxThreads > 0) {
					connector.setThreadPool(new ThreadPoolForJetty(
							this.application.overlappedManager, maxThreads));
				}
				this.connectors.add(connector);
			} catch (Throwable e) {
				this.application.catcher.catchException(e, this);
			}
		}
		if (actived) {
			this.tryStart();
		}
	}

	private static volatile Method servletRegiser;
	private static volatile boolean servletRegiserResolved;

	private void registerServletDelegate(ProxyServlet proxy) {
		if (!servletRegiserResolved) {
			synchronized (JettyServer.class) {
				if (!servletRegiserResolved) {
					servletRegiserResolved = true;
					try {
						Class<?> brageServletClass = this
								.getClass()
								.getClassLoader()
								.loadClass(
										"org.eclipse.jt.core.bridgeservlet.DNABridgeServlet");
						servletRegiser = brageServletClass.getMethod(
								"registerServletDelegate", HttpServlet.class,
								HttpServlet.class);
					} catch (Throwable e) {
					}
				}
			}
		}

		try {
			if (proxy == null && this.proxyServlet == null) {
				return;
			}
			if (servletRegiser != null) {
				servletRegiser.invoke(null, this.proxyServlet, proxy);
			}
			this.proxyServlet = proxy;
		} catch (InvocationTargetException e) {
			this.application.catcher.catchException(e.getTargetException(),
					this);
		} catch (Throwable e) {
			this.application.catcher.catchException(e, this);
		}
	}

	synchronized final void stop() {
		if (this.httpServer != null) {
			try {
				this.registerServletDelegate(null);
				if (this.httpServer != null) {
					this.httpServer.stop();
					this.httpServer.destroy();
				}
			} catch (Throwable e) {
				this.application.catcher.catchException(e, this.httpServer);
			} finally {
				this.httpServer = null;
			}
		}
	}

	private final void initServletAndFilters(ServletHandler servlethandler) {
		final int sSize = this.servletAndFilters.size();
		if (sSize == 0) {
			return;
		}
		ArrayList<ServletHolder> servlets = null;
		ArrayList<ServletMapping> sMappings = null;
		ArrayList<FilterHolder> filters = new ArrayList<FilterHolder>(sSize);
		ArrayList<FilterMapping> fMappings = new ArrayList<FilterMapping>(sSize);

		int filterCount = 0;
		int servletCount = 0;
		for (Entry<Class<?>, ServletAndPaths> sapEntry : this.servletAndFilters
				.entrySet()) {
			final ServletAndPaths sap = sapEntry.getValue();
			if (sap.initOrder == SERVLET_ISFILTER) {
				if (filterCount == 0) {
					filters = new ArrayList<FilterHolder>(sSize);
					fMappings = new ArrayList<FilterMapping>(sSize);
				}
				final FilterHolder holder = new FilterHolder();
				final String filterName = Integer.toString(filterCount++);
				holder.setName(filterName);
				holder.setHeldClass(sapEntry.getKey());
				FilterMapping mapping = new FilterMapping();
				mapping.setServletName(filterName);
				mapping.setPathSpecs(sap.paths);
				filters.add(holder);
				fMappings.add(mapping);
			} else {
				if (servletCount == 0) {
					servlets = new ArrayList<ServletHolder>(sSize);
					sMappings = new ArrayList<ServletMapping>(sSize);
				}
				final ServletHolder holder = new ServletHolder();
				final String servletName = Integer.toString(servletCount++);
				if (sap.initOrder != SERVLET_NO_INIT) {
					holder.setInitOrder(sap.initOrder);
				}
				holder.setName(servletName);
				holder.setHeldClass(sapEntry.getKey());
				ServletMapping mapping = new ServletMapping();
				mapping.setServletName(servletName);
				mapping.setPathSpecs(sap.paths);
				servlets.add(holder);
				sMappings.add(mapping);
			}
		}
		if (servletCount > 0) {
			servlethandler.setServlets(servlets
					.toArray(new ServletHolder[servletCount]));
			servlethandler.setServletMappings(sMappings
					.toArray(new ServletMapping[servletCount]));
		}
		if (filterCount > 0) {
			servlethandler.setFilters(filters
					.toArray(new FilterHolder[filterCount]));
			servlethandler.setFilterMappings(fMappings
					.toArray(new FilterMapping[filterCount]));
		}
	}

	private ProxyServlet proxyServlet;
	final static String servlet_context_attr_application = "dna-application";

	synchronized final boolean tryStart() {
		if (this.httpServer == null) {
			final ServletContextHandler h = new ServletContextHandler(null,
					this.appName,
					this.supportHttpSession ? ServletContextHandler.SESSIONS
							: ServletContextHandler.NO_SESSIONS);
			h.getServletContext().setAttribute(
					servlet_context_attr_application, this.application);
			final ServletHandler sh = h.getServletHandler();
			this.initServletAndFilters(sh);
			Server httpServer = new Server();
			final int maxThreads = this.defaultMaxThreads > 0 ? this.defaultMaxThreads
					: DEFAULT_MAX_THREADS;
			final ThreadPoolForJetty tpf = new ThreadPoolForJetty(
					this.application.overlappedManager, maxThreads);
			httpServer.setThreadPool(tpf);
			httpServer.setConnectors(this.connectors
					.toArray(new Connector[this.connectors.size()]));
			httpServer.setHandler(h);
			try {
				httpServer.start();
			} catch (Throwable e) {
				httpServer.destroy();
				this.application.catcher.catchException(e, httpServer);
				httpServer = null;
			}
			this.httpServer = httpServer;
			if (httpServer != null) {
				this.registerServletDelegate(new ProxyServlet(sh));
			}
		}
		return this.httpServer != null;
	}

	final ApplicationImpl application;

	private static final class ServletAndPaths {
		int initOrder;
		String[] paths;
	}

	private final HashMap<Class<?>, ServletAndPaths> servletAndFilters = new HashMap<Class<?>, ServletAndPaths>();

	public final static int SERVLET_NO_INIT = Integer.MAX_VALUE;
	public final static int SERVLET_ISFILTER = Integer.MAX_VALUE - 1;

	private synchronized final void regServletAndFilters(Class<?> clazz,
			String path, int initOrder) {
		if (clazz == null) {
			throw new NullArgumentException("clazz");
		}
		if (initOrder == SERVLET_ISFILTER) {
			if (!Filter.class.isAssignableFrom(clazz)) {
				throw new IllegalArgumentException("无效的Filter类:"
						+ clazz.getName());
			}
		} else {
			if (!Servlet.class.isAssignableFrom(clazz)) {
				throw new IllegalArgumentException("无效的Servlet类:"
						+ clazz.getName());
			}

		}
		if (path == null || path.length() == 0) {
			throw new NullArgumentException("path");
		}
		ServletAndPaths sap = this.servletAndFilters.get(clazz);
		if (sap == null) {
			sap = new ServletAndPaths();
			sap.paths = new String[] { path };
			sap.initOrder = initOrder;
			this.servletAndFilters.put(clazz, sap);
		} else {
			if (sap.initOrder == SERVLET_ISFILTER) {
				// is a filter
				if (initOrder != SERVLET_ISFILTER) {
					throw new IllegalArgumentException(
							"不允许同时注册成Filter和Servlet: " + clazz.getName());
				}
			} else if (sap.initOrder > initOrder) {
				// is a servlet
				sap.initOrder = initOrder;
			}
			String[] paths = sap.paths;
			final int ol = paths.length;
			for (int i = 0; i < ol; i++) {
				if (paths[i].equals(path)) {
					return;
				}
			}
			String[] newPaths = new String[ol + 1];
			newPaths[ol] = path;
			System.arraycopy(paths, 0, newPaths, 0, ol);
			sap.paths = newPaths;
		}
	}

	final void loadServletOrFilter(BundleStub bundle, SXElement servletOrFilterE)
			throws Throwable {
		final boolean servletOrFilter = servletOrFilterE.name
				.equals(xml_element_servlet);
		if (!servletOrFilter
				&& !servletOrFilterE.name.equals(xml_element_filter)) {
			return;
		}
		final String path = servletOrFilterE
				.getAttribute(JettyServer.xml_attr_path);
		if (path == null || path.length() == 0) {
			return;
		}
		final Class<?> servletClass = bundle.loadClass(servletOrFilterE
				.getAttribute(xml_attr_class), null);
		final int initOrder = servletOrFilter ? servletOrFilterE.getInt(
				xml_attr_init_order, SERVLET_NO_INIT) : SERVLET_ISFILTER;
		this.regServletAndFilters(servletClass, path, initOrder);
	}

	JettyServer(ApplicationImpl application) {
		this.application = application;
	}
}
