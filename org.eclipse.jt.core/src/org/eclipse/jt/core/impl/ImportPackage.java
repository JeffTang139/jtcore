/**
 * 
 */
package org.eclipse.jt.core.impl;

import java.util.List;

import org.eclipse.jt.core.exception.NullArgumentException;


/**
 * �������Ϣ
 * 
 * @author Jeff Tang
 * 
 */
final class ImportPackage extends MetaBase {

	/**
	 * ���������
	 */
	private final String name;
	/**
	 * ������汾��Χ���䣬���û�а汾�����޶�Ĭ��Ϊ(0.0.0, 99999.0.0)
	 */
	private final VersionRegion versionRegion;

	/**
	 * ��֮ƥ��ĵ�������������ͬ���汾�����䷶Χ���䣩
	 */
	private ExportPackage exportPackage;

	/**
	 * ��ð�����Դ
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
	 * ����������͵����������ϵ
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
				// �Ƚϰ��汾�ţ�ȡ�汾�Ŵ��
				if (j == -1) {
					fitPackage = exp;
					// ������汾��ͬ��Ƚϲ���汾��ȡ�汾�Ŵ��
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
		return "�������Ϣ";
	}

	@Override
	public final String getXMLTagName() {
		return xml_element_import_package;
	}
}
