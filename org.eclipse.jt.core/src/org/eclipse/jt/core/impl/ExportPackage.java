/**
 * 
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.NullArgumentException;

/**
 * ��������Ϣ
 * 
 * @author Jeff Tang
 * 
 */
final class ExportPackage extends MetaBase {

	/**
	 * ����������
	 */
	private final String name;
	/**
	 * �������汾�� �����û�а汾����Ĭ��Ϊ1.0.0
	 */
	private final Version version;

	/**
	 * ������bundleStub
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
		return "��������Ϣ";
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
