package org.eclipse.jt.core.impl;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.Map;

import org.eclipse.jt.core.impl.Utils.ObjectAccessor;
import org.eclipse.jt.core.misc.MissingObjectException;
import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.misc.SXElementBuilder;
import org.eclipse.jt.core.spi.publish.BundleToken;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;


/**
 * Bundle存根
 * 
 * @author Jeff Tang
 * 
 */
final class BundleStub implements BundleToken {
	final String name;
	final Version version;
	final ApplicationImpl application;

	public final String getName() {
		return this.name;
	}

	@Override
	public String toString() {
		return this.name;
	}

	private final Bundle bundle;

	final boolean sameBundle(Bundle bundle) {
		return this.bundle == bundle;
	}

	final BundleContext getBundleContext() {
		return this.bundle.getBundleContext();
	}

	/**
	 * 发布元素的信息
	 */
	final SXElement dna;

	/**
	 * 下一个同名但版本低的bundle
	 */
	BundleStub next;

	final int gatherElement(Site site, ResolveHelper helper) {
		int count = 0;
		if (this.dna != null) {
			final Map<String, PublishedElementGathererGroup> gathererGroupMap = this.application.gathererGroupMap;
			for (SXElement publish = this.dna
					.firstChild(Site.xml_element_publish); publish != null; publish = publish
					.nextSibling(Site.xml_element_publish)) {
				for (SXElement group = publish.firstChild(); group != null; group = group
						.nextSibling()) {
					final PublishedElementGathererGroup gathererGroup = gathererGroupMap
							.get(group.name);
					if (gathererGroup != null) {
						for (SXElement element = group.firstChild(); element != null; element = element
								.nextSibling()) {
							count += gathererGroup.gatherElement(site, this,
									element, helper);
						}
					}
				}
			}
		}
		return count;
	}

	private volatile Bundle host;

	@SuppressWarnings("unchecked")
	private final static Bundle getHost(Bundle bundle) {
		Class<?> bundleClass = bundle.getClass();
		if (bundleClass.getName().equals(
				"org.eclipse.osgi.framework.internal.core.BundleFragment")) {
			if (BundleFragment_hosts_getter == null) {
				synchronized (BundleStub.class) {
					if (BundleFragment_hosts_getter == null) {
						BundleFragment_hosts_getter = Utils.newObjectAccessor(
								(Class) bundleClass, Object.class, "hosts");
					}
				}
			}
			Object hosts = BundleFragment_hosts_getter.get(bundle);
			if ((hosts == null) || (Array.getLength(hosts) == 0)) {
				return null;
			}
			Object hostProxy = Array.get(hosts, 0);
			if (hostProxy == null) {
				return null;
			} else if (hostProxy instanceof Bundle) {
				return (Bundle) hostProxy;
			}
			if (BundleLoaderProxy_bundle_getter == null) {
				synchronized (BundleStub.class) {
					if (BundleLoaderProxy_bundle_getter == null) {
						BundleLoaderProxy_bundle_getter = Utils
								.newObjectAccessor(
										(Class) hostProxy.getClass(),
										Bundle.class, "bundle");
					}
				}
			}
			return BundleLoaderProxy_bundle_getter.isReadable() ? BundleLoaderProxy_bundle_getter
					.get(hostProxy)
					: null;
		} else {
			return bundle;
		}
	}

	@SuppressWarnings("unchecked")
	public final <T> Class<T> loadClass(String className, Class<T> baseClass)
			throws ClassNotFoundException {
		Bundle host = this.host;
		if (host == null) {
			synchronized (this) {
				host = this.host;
				if (host == null) {
					this.host = host = BundleStub.getHost(this.bundle);
				}
			}
			if (host == null) {
				throw new ClassNotFoundException("host of bundle[" + this.name
						+ "] not found, class can not load: " + className);
			}
		}
		Class<T> clazz;
		try {
			clazz = this.host.loadClass(className);
		} catch (ClassNotFoundException e) {
			throw new ClassNotFoundException("class in bundle[" + this.name
					+ "] not found: " + className, e);
		}
		if ((baseClass != null) && (baseClass != Object.class)
				&& !baseClass.isAssignableFrom(clazz)) {
			throw new ClassNotFoundException("class " + className
					+ " in bundle[" + this.name + "] is not the sub class of "
					+ baseClass);
		}
		return clazz;
	}

	public final URL findResource(String path) {
		return this.bundle.getResource(path);
	}

	public final URL getResource(String path) throws MissingObjectException {
		final URL resource = this.bundle.getResource(path);
		if (resource == null) {
			throw new MissingObjectException("bundle[" + this.name + "]未找到资源["
					+ path + "]");
		}
		return resource;
	}

	final static String entry_file_dna = "/dna.xml";
	final static String xml_element_dna = "dna";
	final static String xml_element_factory = "factory";
	final static String bundle_version = "Bundle-Version";

	private static volatile ObjectAccessor<Object, Object> BundleFragment_hosts_getter;
	private static volatile ObjectAccessor<Object, Bundle> BundleLoaderProxy_bundle_getter;

	BundleStub(Bundle bundle, SXElementBuilder sxBuilder,
			ApplicationImpl application) {
		if ((bundle == null) || (sxBuilder == null) || (application == null)) {
			throw new NullPointerException();
		}
		this.application = application;
		this.bundle = bundle;

		this.name = bundle.getSymbolicName();
		this.version = ManifestParser.parseVersion((String) bundle.getHeaders()
				.get(BundleStub.bundle_version));
		URL url = bundle.getEntry(BundleStub.entry_file_dna);
		SXElement dna = null;
		if (url != null) {
			try {
				dna = sxBuilder.build(url).firstChild(
						BundleStub.xml_element_dna);
				if (dna != null) {
					application.loadBaseConfigs(this, dna);
				}
			} catch (Throwable e) {
				application.catcher.catchException(e, this);
			}
		}
		this.dna = dna;
	}

}
