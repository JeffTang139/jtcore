/**
 * 
 */
package org.eclipse.jt.core.impl;

import java.io.File;
import java.util.List;
import java.util.jar.Attributes;

/**
 * �����Ϣ
 * 
 * @author Jeff Tang
 * 
 */
final class BundleInfo extends MetaBase {

	BundleInfo(Attributes attrs, File bundleFile) {
		this.name = ManifestParser.parserBundleName(attrs);
		this.version = ManifestParser.parseVersion(attrs);
		this.bundleFile = bundleFile;
		this.exportPackages = ManifestParser.parseExportPackage(attrs, this);
		this.importPackages = ManifestParser.parseImportPackage(attrs);
		this.bundleClassPathes = ManifestParser.parseBundleClassPath(attrs);
		this.requiredBundles = ManifestParser.parseRequiredBundle(attrs);
	}

	/**
	 * �������
	 */
	final String name;
	/**
	 * ����汾��Ϣ
	 */
	final Version version;
	/**
	 * ����ļ�����
	 */
	final File bundleFile;
	/**
	 * ����������б�
	 */
	final MetaBaseContainerImpl<ExportPackage> exportPackages;
	/**
	 * ���������б�
	 */
	final MetaBaseContainerImpl<ImportPackage> importPackages;
	/**
	 * ���classpath�б�
	 */
	final MetaBaseContainerImpl<BundleClassPath> bundleClassPathes;
	/**
	 * ����������б�
	 */
	final MetaBaseContainerImpl<RequiredBundle> requiredBundles;

	/**
	 * ��������Լ���֮�����ϵ
	 * 
	 * @param bundleStubs
	 */
	final void resolveReference(List<BundleInfo> bundleStubs) {
		if (this.requiredBundles != null) {
			return;
		}
		for (int i = 0, s = this.requiredBundles.size(); i < s; i++) {
			this.requiredBundles.get(i).resolveReference(bundleStubs);
		}
		if (this.importPackages != null) {
			return;
		}
		for (int i = 0, s = this.importPackages.size(); i < s; i++) {
			this.importPackages.get(i).resolveReference(bundleStubs);
		}
	}

	/**
	 * ���������ֵƥ��ĵ�����
	 * 
	 * @param name
	 * @return
	 */
	final ExportPackage findExportPackage(String name) {
		if (this.exportPackages == null) {
			return null;
		}
		for (int i = 0, size = this.exportPackages.size(); i < size; i++) {
			ExportPackage exp = this.exportPackages.get(i);
			if (name.equals(exp.getName())) {
				return exp;
			}
		}
		return null;
	}

	final Version getVersion() {
		return this.version;
	}

	final MetaBaseContainerImpl<ExportPackage> getExportPackages() {
		return this.exportPackages;
	}

	@Override
	final String getDescription() {
		return "�����Ϣ";
	}

	// ////////////////////////////////////////////////
	// /////////////////xml
	// /////////////////////////////////////////////////
	private final static String xml_element_bundle_info = "bundle-info";

	@Override
	public final String getXMLTagName() {
		return xml_element_bundle_info;
	}
}
