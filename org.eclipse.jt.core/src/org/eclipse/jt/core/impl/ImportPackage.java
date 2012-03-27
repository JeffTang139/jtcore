/**
 * 
 */
package org.eclipse.jt.core.impl;

import java.util.List;

import org.eclipse.jt.core.exception.NullArgumentException;


/**
 * 引入包信息
 * 
 * @author Jeff Tang
 * 
 */
final class ImportPackage extends MetaBase {

	/**
	 * 引入包名称
	 */
	private final String name;
	/**
	 * 引入包版本范围区间，如果没有版本区间限定默认为(0.0.0, 99999.0.0)
	 */
	private final VersionRegion versionRegion;

	/**
	 * 与之匹配的导出包（名称相同，版本号在其范围区间）
	 */
	private ExportPackage exportPackage;

	/**
	 * 获得包的来源
	 */
	public final ExportPackage getSource() {
		return this.exportPackage;
	}

	ImportPackage(String name, VersionRegion versionRegion) {
		if (name == null || name.length() == 0) {
			throw new NullArgumentException("name");
		}
		if (versionRegion == null) {
			throw new NullArgumentException("versionRegion");
		}
		this.name = name;
		this.versionRegion = versionRegion;
	}

	/**
	 * 建立引入包和导出包间的联系
	 * 
	 * @param bundleStubs
	 */
	final void resolveReference(List<BundleInfo> bundleStubs) {
		ExportPackage fitPackage = null;
		for (int i = 0, s = bundleStubs.size(); i < s; i++) {
			BundleInfo bundleInfo = bundleStubs.get(i);
			ExportPackage exp = bundleInfo.findExportPackage(this.name);
			if (exp == null) {
				continue;
			}
			if (!this.versionRegion.in(exp.getVersion())) {
				continue;
			}
			if (fitPackage == null) {
				fitPackage = exp;
			} else {
				int j = fitPackage.getVersion().compareTo(exp.getVersion());
				// 比较包版本号，取版本号大的
				if (j == -1) {
					fitPackage = exp;
					// 如果包版本相同则比较插件版本，取版本号大的
				} else if (j == 0) {
					if (fitPackage.getBundleInfo().getVersion().compareTo(
					        exp.getBundleInfo().getVersion()) == -1) {
						fitPackage = exp;
					}
				}
			}
		}
		this.exportPackage = fitPackage;
	}

	// ////////////////////////////////
	// //////////XML
	// ////////////////////////////////
	private final static String xml_element_import_package = "import-package";

	@Override
	final String getDescription() {
		return "引入包信息";
	}

	@Override
	public final String getXMLTagName() {
		return xml_element_import_package;
	}
}
