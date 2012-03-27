package org.eclipse.jt.core.impl;

import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.jt.core.SessionKind;
import org.eclipse.jt.core.SiteState;
import org.eclipse.jt.core.exception.AbortException;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.exception.SessionDisposedException;
import org.eclipse.jt.core.misc.ExceptionCatcher;
import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.misc.SXElementBuilder;
import org.eclipse.jt.core.spi.application.Application;
import org.eclipse.jt.core.spi.application.Session;
import org.eclipse.jt.core.spi.application.SessionIniter;
import org.eclipse.jt.core.spi.metadata.LoadAllMetaDataTask;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.DateParser;
import org.eclipse.jt.core.type.GUID;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;


/**
 * 应用实例
 * 
 * @author Jeff Tang
 * 
 */
final class ApplicationImpl implements Application {
	private static long start_nanoTime = System.nanoTime();
	private static long start_time = System.currentTimeMillis();

	final static void printDateTime(PrintStream stream) {
		final long time = (System.nanoTime() - start_nanoTime) / 1000000
				+ start_time;
		stream.print(DateParser
				.format(time, DateParser.FORMAT_DATE_TIME_AUTOMS));
	}

	private final NetChannelManagerImpl netChannelManager;

	final NetNodeManagerImpl netNodeManager;

	final NetNodeImpl getNetNode(URL address) {
		return this.netNodeManager.getNetNode(address);
	}

	final NetChannelImpl ensureChannel(GUID remoteNodeID) {
		return this.netChannelManager.ensureChannel(remoteNodeID);
	}

	/**
	 * 本节点的网络ID，每次启动时随机产生
	 */
	final GUID localNodeID = GUID.randomID();

	private final long bornTime = System.currentTimeMillis();

	public long getBornTime() {
		return this.bornTime;
	}

	public final int getNormalSessionCount() {
		return this.sessionManager.getNormalSessionCount();
	}

	/**
	 * 获得所有普通会话列表
	 */
	public final List<? extends Session> getNormalSessions() {
		return this.sessionManager.getNormalSessions();
	}

	/**
	 * 获得HTTP请求的字节数累计
	 */
	public final long getHTTPRequestBytes() {
		return this.jettyServer.getHTTPRequestBytes();
	}

	/**
	 * 获得HTTP应答的字节数累计
	 */
	public final long getHTTPResponseBytes() {
		return this.jettyServer.getHTTPResponseBytes();
	}

	private HashMap<String, LanguagePackage> infoGroupLanguages;

	final void regInfoGroupLanguage(String infoGroupFullName, String language,
			String[] infoNameMessages) {
		if (this.infoGroupLanguages == null) {
			this.infoGroupLanguages = new HashMap<String, LanguagePackage>();
		}
		final LanguagePackage newLp = new LanguagePackage(language,
				infoNameMessages);
		newLp.next = this.infoGroupLanguages.put(infoGroupFullName, newLp);
	}

	final LanguagePackage findInfoGroupLanguages(String infoGroupFullName) {
		return this.infoGroupLanguages != null ? this.infoGroupLanguages
				.get(infoGroupFullName) : null;
	}

	public final <TUserData> SessionImpl newSession(
			SessionIniter<TUserData> sessionIniter, TUserData userData) {
		return this.sessionManager.newSession(SessionKind.NORMAL,
				InternalUser.anonymUser, sessionIniter, userData);
	}

	public final SessionImpl getSession(long sessionID)
			throws SessionDisposedException {
		return this.sessionManager.getOrFindSession(sessionID, true);
	}

	public final SessionImpl getSystemSession() {
		return this.sessionManager.getSystemSession();
	}

	private final Random random = new Random();

	/**
	 * 获得全局唯一又相对连续递增的GUID对象
	 */
	final GUID newRECID() {
		return GUID.valueOf(this.timeRelatedSequence.next(), this.random
				.nextLong());
	}

	private final AtomicLong resourceItemID;

	final long newResourceItemID() {
		return this.resourceItemID.incrementAndGet();
	}

	/**
	 * 获得连续递增的版本号
	 */
	final long newRECVER() {
		return this.timeRelatedSequence.next();
	}

	final TimeRelatedSequenceImpl timeRelatedSequence;

	final NetSelfClusterImpl getNetCluster() {
		return this.netNodeManager.thisCluster;
	}

	final void doDispose(ExceptionCatcher catcher) {
		if (this.jettyServer != null) {
			this.jettyServer.doDispose(catcher);
		}
		if (this.netManager != null) {
			this.netManager.doDispose(catcher);
		}
		if (this.sessionManager != null) {
			this.sessionManager.doDispose(catcher);
		}
		if (this.overlappedManager != null) {
			this.overlappedManager.doDispose(catcher);
		}
		if (this.dataSourceManager != null) {
			this.dataSourceManager.doDispose(catcher);
		}
	}

	final static String file_dna_server = "work/dna-server.xml";
	final static String xml_element_dna = "dna";
	final static String xml_element_sites = "sites";

	final DataSourceRef newSiteConnectionInfo2(SXElement siteElement) {
		if (this.dataSourceManager.isEmpty()) {
			return null;
		}
		if (siteElement != null) {
			for (SXElement datasourcerefE : siteElement.getChildren(
					Site.xml_element_datasourcerefs,
					DataSourceRef.xml_element_datasourceref)) {
				if (datasourcerefE.getAttribute(DataSourceRef.xml_attr_space)
						.length() == 0) {
					try {
						return new DataSourceRef(this.dataSourceManager,
								datasourcerefE);
					} catch (Throwable e) {
						this.catcher.catchException(e, this.dataSourceManager);
						return null;
					}
				}
			}
		}
		try {
			return new DataSourceRef(this.dataSourceManager.getDefaultSource());
		} catch (Throwable e) {
			this.catcher.catchException(e, this.dataSourceManager);
			return null;
		}
	}

	private final SXElement dnaServerConfig;

	public final SXElement getDNAConfig(String name) {
		return this.dnaServerConfig.firstChild(name);
	}

	public final SXElement getDNAConfig(String name1, String name2) {
		return this.dnaServerConfig.firstChild(name1, name2);
	}

	public final SXElement getDNAConfig(String name1, String name2,
			String... names) {
		return this.dnaServerConfig.firstChild(name1, name2, names);
	}

	private final SXElement getDefaultSiteConfig() {
		return this.getDNAConfig(xml_element_sites, Site.xml_element_site);
	}

	private static final SXElement getDNAServerConfig(File dnaRoot,
			SXElementBuilder builder, ExceptionCatcher catcher) {
		if (dnaRoot != null) {
			try {
				final File fDNAServer = new File(dnaRoot, file_dna_server);
				if (fDNAServer.isFile()) {
					final SXElement dna = builder.build(fDNAServer).firstChild(
							xml_element_dna);
					if (dna != null) {
						return dna;
					}
				}
			} catch (Throwable e) {
				catcher.catchException(e, null);
			}
		}
		return SXElement.newElement(xml_element_dna);
	}

	@Deprecated
	final GUID getAppID() {
		return this.netNodeManager.thisCluster.appID;
	}

	/**
	 * 构造函数
	 */
	private ApplicationImpl(BundleContext coreBundleContext) {
		if (coreBundleContext == null) {
			throw new NullArgumentException("coreBundleContext");
		}
		// 初始化bundle库的位置
		ResolveHelper.logStartInfo("开始");
		this.dnaRoot = getDNARootPath(coreBundleContext.getProperty(ROOT_PATH));
		this.dnaWork = this.dnaRoot != null ? new File(this.dnaRoot,
				folder_work) : null;
		final SXElementBuilder sxBuilder;
		try {
			sxBuilder = new SXElementBuilder();
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
		this.dnaServerConfig = getDNAServerConfig(this.dnaRoot, sxBuilder,
				this.catcher);

		try {
			final NetSelfClusterImpl selfCluster = new NetSelfClusterImpl(this,
					this.getDNAConfig(NetSelfClusterImpl.xml_element_cluster));
			this.timeRelatedSequence = new TimeRelatedSequenceImpl(
					selfCluster.thisClusterNodeIndex);
			this.resourceItemID = new AtomicLong(
					((long) selfCluster.thisClusterNodeIndex) << 60);
			this.overlappedManager = new WorkingManager(this);
			this.netChannelManager = new NetChannelManagerImpl(this);
			this.netNodeManager = new NetNodeManagerImpl(
					this.netChannelManager, selfCluster, this
							.getDNAConfig(NetNodeManagerImpl.xml_element_net));
			this.jettyServer = new JettyServer(this);
			this.coreBundle = this
					.initBundleStubs(coreBundleContext, sxBuilder);
			this.mseManager = new ModelScriptEngineManager();
			this.sessionManager = new SessionManager(this, this
					.getDNAConfig(SessionManager.xml_element_session));
			this.dataSourceManager = new DataSourceManager(this, this
					.getDNAConfig(DataSourceManager.xml_element_datasources));
			this.netManager = new NetManager(this);
			this.jettyServer.update(this
					.getDNAConfig(JettyServer.xml_element_http),
					this.netManager.ACCEPTOR, this.netManager.ACCEPTOR);
			this.netManager.config(this.getDNAConfig(NetManager.xml_e_cluster));
			if (this.jettyServer.tryStart()) {
				startNetManager: {
					int port = this.jettyServer.getHttpPort();
					if (port < 0) {
						port = this.jettyServer.getSslPort();
						if (port < 0) {
							break startNetManager;
						}
					}
					this.netManager.setPort(port, true);
					this.netManager.start();
				}
			}
			this.rootSite = new Site(this, this.getDefaultSiteConfig());
			defaultApp = this;
			this.netNodeManager.thisCluster.initClusterNodes();
			this.rootSite.active();
		} catch (Throwable e) {
			defaultApp = null;
			this.doDispose(this.catcher);
			throw Utils.tryThrowException(e);
		}
	}

	// //////////////////////////////////////////////////////////////////////
	// ////
	// //////////////////以下是内部方法//////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////

	/**
	 * 根站点
	 */
	private Site rootSite;

	/**
	 * 获取默认的站点
	 */
	final Site getDefaultSite() {
		return this.rootSite;
	}

	// 异常收集器
	final ExceptionCatcher catcher = new ExceptionCatcher() {
		public void catchException(Throwable e, Object sender) {
			if (!(e instanceof AbortException)) {
				e.printStackTrace();
				if (e instanceof InterruptedException) {
					throw Utils.tryThrowException(e);
				}
			}
		}
	};

	/**
	 * 异步处理线程池
	 */
	final WorkingManager overlappedManager;
	/**
	 * 数据源集合
	 */
	final DataSourceManager dataSourceManager;
	/**
	 * http服务器
	 */
	final JettyServer jettyServer;
	/**
	 * 会话池
	 */
	final SessionManager sessionManager;
	/**
	 * 当前线程的上下文
	 */
	final ThreadLocal<ContextImpl<?, ?, ?>> contextLocal = new ThreadLocal<ContextImpl<?, ?, ?>>();

	// /////////////////////////////////////////////////////////////
	// /////启动
	// /////////////////////////////////////////////////////////////
	final static String xml_file_site = "site.xml";
	final static String folder_site_root = "work/org.eclipse.jt";
	final static String folder_work = "work";
	private final File dnaRoot;
	private final File dnaWork;

	public final File getDNARoot() {
		final File dnaRoot = this.dnaRoot;
		if (dnaRoot == null) {
			throw new IllegalStateException("D&A 根目录无法确定");
		}
		return dnaRoot;
	}

	public final File getDNAWork() {
		final File dnaWork = this.dnaWork;
		if (dnaWork == null) {
			throw new IllegalStateException("D&A 工作目录无法确定");
		}
		return dnaWork;
	}

	private static final File getDNARootPath(String bundleRootPath) {
		if (bundleRootPath != null && bundleRootPath.length() > 0) {
			File root = new File(bundleRootPath);
			if (root.isDirectory()) {
				return root;
			}
		}
		return null;
	}

	final BundleStub coreBundle;
	final Map<String, BundleStub> bundles = new HashMap<String, BundleStub>();

	private final void putBundle(BundleStub bundle) {

		BundleStub exist = this.bundles.get(bundle.name);
		BundleStub last = null;
		while (exist != null && exist.version.compareTo(bundle.version) >= 0) {
			last = exist;
			exist = exist.next;
		}
		if (last != null) {
			bundle.next = last.next;
			last.next = bundle;
		} else {
			bundle.next = exist;
			this.bundles.put(bundle.name, bundle);
		}
	}

	final Class<?> loadClass(String className, String bundleName)
			throws ClassNotFoundException {
		BundleStub b = this.bundles.get(bundleName);
		if (b == null) {
			throw new ClassNotFoundException("bundle[" + bundleName
					+ "]不存在，定位不到类[" + className + "]");
		}
		return b.loadClass(className, null);
	}

	final Class<?> tryLoadClass(String className, String bundleName) {
		try {
			return this.loadClass(className, bundleName);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	final Class<?> loadClass(String className) throws ClassNotFoundException {
		try {
			return this.coreBundle.loadClass(className, null);
		} catch (ClassNotFoundException e) {
			for (BundleStub b : this.bundles.values()) {
				if (b != this.coreBundle) {
					try {
						return b.loadClass(className, null);
					} catch (ClassNotFoundException e2) {
					}
				}
			}
			throw e;
		}
	}

	final Class<?> tryLoadClass(String className) {
		try {
			return this.loadClass(className);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	final DataType findDataType(GUID typeID) {
		return DataTypeBase.findDataType(typeID);
	}

	static {
		DataTypeBase.ensureStaticInited();
	}

	private final void loadGather(BundleStub bundle, SXElement gatherE) {
		String groupTag = gatherE
				.getAttribute(PublishedElementGatherer.xml_attr_group);
		if (groupTag == null || groupTag.length() == 0) {
			return;
		}
		PublishedElementGatherer<?> gather;
		try {
			Class<?> gatherClass = bundle.loadClass(gatherE
					.getAttribute(PublishedElementGatherer.xml_attr_class),
					PublishedElementGatherer.class);
			Constructor<?> c1, c2;
			try {
				c1 = gatherClass.getDeclaredConstructor(SXElement.class);
				c2 = null;
			} catch (Throwable e) {
				c2 = gatherClass.getDeclaredConstructor();
				c1 = null;
			}
			if (c1 != null) {
				Utils.publicAccessibleObject(c1);
				gather = (PublishedElementGatherer<?>) c1.newInstance(gatherE);
			} else {
				Utils.publicAccessibleObject(c2);
				gather = (PublishedElementGatherer<?>) c2.newInstance();
			}
		} catch (Throwable e) {
			this.catcher.catchException(e, bundle);
			return;
		}
		PublishedElementGathererGroup gathererGroup = this.gathererGroupMap
				.get(groupTag);
		if (gathererGroup == null) {
			gathererGroup = new PublishedElementGathererGroup();
			this.gathererGroupMap.put(groupTag, gathererGroup);
		}
		String elementTag = gatherE
				.getAttribute(PublishedElementGatherer.xml_attr_element);
		gathererGroup.putGather(elementTag, gather);
	}

	/**
	 * 装载基础配置，并返回发布配置
	 */
	final void loadBaseConfigs(BundleStub bundle, SXElement dna) {
		for (SXElement base = dna.firstChild(); base != null; base = base
				.nextSibling()) {
			if (base.name
					.equals(PublishedElementGatherer.xml_element_gathering)) {
				for (SXElement gatherE = base
						.firstChild(PublishedElementGatherer.xml_element_gatherer); gatherE != null; gatherE = gatherE
						.nextSibling(PublishedElementGatherer.xml_element_gatherer)) {
					this.loadGather(bundle, gatherE);
				}
			} else if (base.name.equals(JettyServer.xml_element_servlets)
					|| base.name.equals(JettyServer.xml_element_filters)) {
				for (SXElement servletE = base.firstChild(); servletE != null; servletE = servletE
						.nextSibling()) {
					try {
						this.jettyServer.loadServletOrFilter(bundle, servletE);
					} catch (Throwable e) {
						this.catcher.catchException(e, bundle);
					}
				}
			} else if (base.name.equals(ObjectDataTypeBase.xml_element_types)) {
				for (SXElement typeE = base
						.firstChild(ObjectDataTypeBase.xml_element_type); typeE != null; typeE = typeE
						.nextSibling(ObjectDataTypeBase.xml_element_type)) {
					try {
						ObjectDataTypeBase.loadCustomType(bundle, typeE);
					} catch (Throwable e) {
						this.catcher.catchException(e, bundle);
					}
				}
			}
		}
	}

	private final BundleStub initBundleStubs(BundleContext coreBundleContext,
			SXElementBuilder sxBuilder) {
		BundleStub coreBundleStub = new BundleStub(coreBundleContext
				.getBundle(), sxBuilder, this);
		this.putBundle(coreBundleStub);
		for (Bundle bundle : coreBundleContext.getBundles()) {
			if (!coreBundleStub.sameBundle(bundle)) {
				BundleStub bundleStub = new BundleStub(bundle, sxBuilder, this);
				this.putBundle(bundleStub);
			}
		}
		return coreBundleStub;
	}

	final Map<String, PublishedElementGathererGroup> gathererGroupMap = new HashMap<String, PublishedElementGathererGroup>();

	private static ApplicationImpl defaultApp;
	public static final String ROOT_PATH = "org.eclipse.jt.rootpath";

	static void startApp(BundleContext context) {
		if (ApplicationImpl.defaultApp != null) {
			throw new IllegalStateException("应用已经启动");
		}

		// 将在ApplicationImpl构造的内部设置ApplicationImpl.defaultApp
		new ApplicationImpl(context);
	}

	static void stopApp() {
		if (ApplicationImpl.defaultApp != null) {
			try {
				ApplicationImpl.defaultApp
						.doDispose(ApplicationImpl.defaultApp.catcher);
			} finally {
				ApplicationImpl.defaultApp = null;
			}
		}
	}

	public static ApplicationImpl getDefaultApp() {
		if (ApplicationImpl.defaultApp == null) {
			throw new IllegalStateException("D&A服务器还未启动完毕");
		}
		return ApplicationImpl.defaultApp;
	}

	// //////////////////
	// //// remote
	// //////////////////
	private NetManager netManager;

	final NetNodeInfo getNetNodeInfo(String host, int port) {
		if (this.netManager == null) {
			throw new UnsupportedOperationException("不支持远程调用");
		}
		try {
			return this.netManager.ensureGet(host, port);
		} catch (UnknownHostException e) {
			throw Utils.tryThrowException(e);
		}
	}

	// ////////////////////////////
	// ///// scripting
	// ////////////////////////////
	final ModelScriptEngineManager mseManager;

	// /////////////////////////////////////
	// // 参数合并
	// /////////////////////////////////////

	/**
	 * 重启根站点
	 */
	final void restartRootSite(ContextImpl<?, ?, ?> context) throws Throwable {
		final Site oldRootSite = this.rootSite;
		synchronized (oldRootSite) {
			if (oldRootSite.state != SiteState.ACTIVE) {
				throw new IllegalStateException("站点必须处于激活状态才允许重新启动");
			}
			oldRootSite.state = SiteState.DISPOSING;
		}
		final Exception stack = new Exception();
		final PrintStream out = System.err;
		synchronized (out) {
			printDateTime(out);
			out.println(": D&A 站点重新启动，调用栈：");
			for (StackTraceElement trace : stack.getStackTrace()) {
				out.println("\t->" + trace);
			}
		}
		final Site newSite;
		try {
			this.sessionManager.doReset(this.catcher);
			newSite = new Site(this, this.getDefaultSiteConfig());
		} catch (Throwable e) {
			oldRootSite.state = SiteState.ACTIVE;
			throw e;
		}
		// 重启
		this.rootSite = newSite;
		try {
			newSite.active();
		} finally {
			// 最后再清理，保证延迟短
			oldRootSite.doDispose(context);
		}
	}

	/**
	 * 重新装载站点
	 */
	final void reLoadRootSite(ContextImpl<?, ?, ?> context,
			LoadAllMetaDataTask task) throws Throwable {
		final Site oldRootSite = this.rootSite;
		synchronized (oldRootSite) {
			if (oldRootSite.state != SiteState.ACTIVE) {
				throw new IllegalStateException("站点必须处于激活状态才允许装载参数");
			}
			oldRootSite.state = SiteState.WAITING_LOAD_METADATA;
		}
		Site tmpSite = null;
		try {
			final Site newSite;
			try {
				tmpSite = new Site(this, this.getDefaultSiteConfig());
				tmpSite.load(context, task);
				context.resolveTrans();
				tmpSite.state = SiteState.DISPOSING;
				oldRootSite.state = SiteState.DISPOSING;
				this.sessionManager.doReset(this.catcher);
				newSite = new Site(this, this.getDefaultSiteConfig());
			} catch (Throwable e) {
				oldRootSite.state = SiteState.ACTIVE;
				throw e;
			}
			// 重启
			this.rootSite = newSite;
			newSite.active();
			// 最后再清理，保证延迟短
			oldRootSite.doDispose(context);
		} finally {
			if (tmpSite != null) {
				// 最后再清理，保证延迟短
				tmpSite.doDispose(context);
			}
		}
	}

}
