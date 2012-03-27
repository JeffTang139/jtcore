package org.eclipse.jt.core.spi.publish;

import java.net.URL;

import org.eclipse.jt.core.misc.MissingObjectException;


public interface BundleToken {
	public <T> Class<T> loadClass(String className, Class<T> baseClass)
			throws ClassNotFoundException;

	public URL getResource(String path) throws MissingObjectException;

	public URL findResource(String path);

	public String getName();

}
