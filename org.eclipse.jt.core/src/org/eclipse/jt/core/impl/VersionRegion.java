/**
 * 
 */
package org.eclipse.jt.core.impl;

/**
 * �汾�ŷ�Χ����
 * 
 * @author Jeff Tang
 * 
 */
final class VersionRegion {

	/**
	 * ��С�汾�ŵĵ�һ��
	 */
	private final byte lv1;
	/**
	 * ��С�汾�ŵĵڶ���
	 */
	private final byte lv2;
	/**
	 * ��С�汾�ŵĵ�����
	 */
	private final short lv3;

	/**
	 * ���汾�ŵĵ�һ��
	 */
	private final byte hv1;
	/**
	 * ���汾�ŵĵڶ���
	 */
	private final byte hv2;
	/**
	 * ���汾�ŵĵ�����
	 */
	private final short hv3;

	/**
	 * �Ƿ������Сֵ
	 */
	private final boolean includeL;
	/**
	 * �Ƿ�������ֵ
	 */
	private final boolean indcludeH;

	VersionRegion(int lv1, int lv2, int lv3, int hv1, int hv2, int hv3,
	        boolean includeL, boolean indcludeH) {
		this.lv1 = (byte) lv1;
		this.lv2 = (byte) lv2;
		this.lv3 = (short) lv3;
		this.hv1 = (byte) hv1;
		this.hv2 = (byte) hv2;
		this.hv3 = (short) hv3;
		this.includeL = includeL;
		this.indcludeH = indcludeH;
	}

	final static VersionRegion empty = new VersionRegion(0, 0, 0, 99999, 0, 0,
	        false, false);

	/**
	 * �Ƿ��ڸð汾�����䷶Χ
	 * 
	 * @param version
	 * @return
	 */
	final boolean in(Version version) {
		if (version == null) {
			return false;
		}
		// ����Сֵ�Ƚ�
		int lc = version.compareTo(this.lv1, this.lv2, this.lv3);
		// �����ֵ�Ƚ�
		int hc = version.compareTo(this.hv1, this.hv2, this.hv3);
		return (lc == 1 && hc == -1) || (lc == 0 && this.includeL)
		        || (hc == 0 && this.indcludeH);
	}

}
