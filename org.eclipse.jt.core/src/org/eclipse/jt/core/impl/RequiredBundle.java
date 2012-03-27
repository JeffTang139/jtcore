/**
 * 
 */
package org.eclipse.jt.core.impl;

import java.util.List;

import org.eclipse.jt.core.exception.NullArgumentException;


/**
 * 依赖插件包信息
 * 
 * @author Jeff Tang
 * 
 */
final class RequiredBundle extends MetaBase {

	/**
	 * 依赖插件包名称
	 */
	private final String name;
	/**
	 * 依赖插件包版本范围区间,没有则给一个不可逾越的范围
	 */
	private final VersionRegion versionRegion;

	/**
	 * 与之匹配的bundleStub（名称相同，版本号在其范围区间）
	 */
	private BundleInfo bundleStub;

	/**
	 * 与之匹配的bundleStub（名称相同，版本号在其范围区间）
	 */
	final BundleInfo getSource() {
		return this.bundleStub;
	}

	RequiredBundle(String name, VersionRegion versionRegion) {
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
	 * 建立插件间的联系
	 * 
	 * @param bundleStubs
	 */
	final void resolveReference(List<BundleInfo> bundleStubs) {
		BundleInfo fitBundleStub = null;
		for (int i = 0, s = bundleStubs.size(); i < s; i++) {
			BundleInfo bundleStub = bundleStubs.get(i);
			if (!this.name.equals(bundleStub.name)) {
				continue;
			}
			if (!this.versionRegion.in(bundleStub.getVersion())) {
				continue;
			}
			if (fitBundleStub == null) {
				fitBundleStub = bundleStub;
			} else {
				if (fitBundleStub.getVersion().compareTo(
				        bundleStub.getVersion()) == -1) {
					fitBundleStub = bundleStub;
				}
			}
		}
		this.bundleStub = fitBundleStub;
	}

	// /////////////////////////////////////
	// ////////////XML
	// ////////////////////////////////////////
	private final static String xml_element_required_bundle = "required_bundle";

	@Override
	final String getDescription() {
		return "依赖插件包信息";
	}

	@Override
	public final String getXMLTagName() {
		return xml_element_required_bundle;
	}
}
