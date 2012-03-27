/**
 * 
 */
package org.eclipse.jt.core.impl;

/**
 * 版本号范围区间
 * 
 * @author Jeff Tang
 * 
 */
final class VersionRegion {

	/**
	 * 最小版本号的第一段
	 */
	private final byte lv1;
	/**
	 * 最小版本号的第二段
	 */
	private final byte lv2;
	/**
	 * 最小版本号的第三段
	 */
	private final short lv3;

	/**
	 * 最大版本号的第一段
	 */
	private final byte hv1;
	/**
	 * 最大版本号的第二段
	 */
	private final byte hv2;
	/**
	 * 最大版本号的第三段
	 */
	private final short hv3;

	/**
	 * 是否包含最小值
	 */
	private final boolean includeL;
	/**
	 * 是否包含最大值
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
	 * 是否在该版本号区间范围
	 * 
	 * @param version
	 * @return
	 */
	final boolean in(Version version) {
		if (version == null) {
			return false;
		}
		// 与最小值比较
		int lc = version.compareTo(this.lv1, this.lv2, this.lv3);
		// 与最大值比较
		int hc = version.compareTo(this.hv1, this.hv2, this.hv3);
		return (lc == 1 && hc == -1) || (lc == 0 && this.includeL)
		        || (hc == 0 && this.indcludeH);
	}

}
