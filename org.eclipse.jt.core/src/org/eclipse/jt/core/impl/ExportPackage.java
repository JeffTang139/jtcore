/**
 * 
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.NullArgumentException;

/**
 * 导出包信息
 * 
 * @author Jeff Tang
 * 
 */
final class ExportPackage extends MetaBase {

	/**
	 * 导出包名称
	 */
	private final String name;
	/**
	 * 导出包版本号 ，如果没有版本号则默认为1.0.0
	 */
	private final Version version;

	/**
	 * 所属的bundleStub
	 */
	private final BundleInfo bundleInfo;

	ExportPackage(String name, Version version, BundleInfo bundleInfo) {
		if (name == null || name.length() == 0) {
			throw new NullArgumentException("name");
		}
		if (version == null) {
			throw new NullArgumentException("version");
		}
		if (bundleInfo == null) {
			throw new NullArgumentException("bundleInfo");
		}
		this.name = name;
		this.version = version;
		this.bundleInfo = bundleInfo;
	}

	// //////////////////////////////
	// ///////XML
	// /////////////////////////////

	private final static String xml_element_export_package = "export-package";

	@Override
	final String getDescription() {
		return "导出包信息";
	}

	@Override
	public final String getXMLTagName() {
		return xml_element_export_package;
	}

	final String getName() {
		return this.name;
	}

	final Version getVersion() {
		return this.version;
	}

	final BundleInfo getBundleInfo() {
		return this.bundleInfo;
	}
}
