/**
 * 
 */
package org.eclipse.jt.core.impl;

import java.io.File;
import java.util.List;
import java.util.jar.Attributes;

/**
 * 插件信息
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
	 * 插件名称
	 */
	final String name;
	/**
	 * 插件版本信息
	 */
	final Version version;
	/**
	 * 插件文件对象
	 */
	final File bundleFile;
	/**
	 * 插件导出包列表
	 */
	final MetaBaseContainerImpl<ExportPackage> exportPackages;
	/**
	 * 插件引入包列表
	 */
	final MetaBaseContainerImpl<ImportPackage> importPackages;
	/**
	 * 插件classpath列表
	 */
	final MetaBaseContainerImpl<BundleClassPath> bundleClassPathes;
	/**
	 * 所依赖插件列表
	 */
	final MetaBaseContainerImpl<RequiredBundle> requiredBundles;

	/**
	 * 建立插件以及包之间的联系
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
	 * 查找与参数值匹配的导出包
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
		return "插件信息";
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
