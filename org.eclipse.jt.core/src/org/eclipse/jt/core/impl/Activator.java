package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.spi.application.Application;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;


public class Activator implements BundleActivator {

	public final static Application getDefaultApp() {
		return ApplicationImpl.getDefaultApp();

	}

	public void start(BundleContext context) throws Exception {
		ApplicationImpl.startApp(context);
	}

	public void stop(BundleContext context) throws Exception {
		ApplicationImpl.stopApp();
	}
}
