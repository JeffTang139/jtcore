/**
 * 
 */
package org.eclipse.jt.core.impl;

import java.util.List;

import org.eclipse.jt.core.exception.NullArgumentException;


/**
 * �����������Ϣ
 * 
 * @author Jeff Tang
 * 
 */
final class RequiredBundle extends MetaBase {

	/**
	 * �������������
	 */
	private final String name;
	/**
	 * ����������汾��Χ����,û�����һ��������Խ�ķ�Χ
	 */
	private final VersionRegion versionRegion;

	/**
	 * ��֮ƥ���bundleStub��������ͬ���汾�����䷶Χ���䣩
	 */
	private BundleInfo bundleStub;

	/**
	 * ��֮ƥ���bundleStub��������ͬ���汾�����䷶Χ���䣩
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
	 * ������������ϵ
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
		return "�����������Ϣ";
	}

	@Override
	public final String getXMLTagName() {
		return xml_element_required_bundle;
	}
}
