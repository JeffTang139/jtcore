/**
 * 
 */
package org.eclipse.jt.core.impl;

/**
 * �汾�ţ�������߰���
 * 
 * @author Jeff Tang
 */
final class Version implements Comparable<Version> {

	/**
	 * �汾�ŵ�һ��
	 */
	private final byte v1;
	/**
	 * �汾�ŵڶ���
	 */
	private final byte v2;
	/**
	 * �汾�ŵ�����
	 */
	private final short v3;
	/**
	 * �汾���е��޶���
	 */
	private final String qualifier;

	Version(int v1, int v2, int v3, String qualifier) {
		this.v1 = (byte) v1;
		this.v2 = (byte) v2;
		this.v3 = (short) v3;
		this.qualifier = qualifier;
	}

	@Override
	public final int hashCode() {
		return ((this.v1 * 31 + this.v2) * 31 + this.v3) * 31 + this.qualifier == null ? 0
		        : this.qualifier.hashCode();
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof Version) {
			Version v = (Version) obj;
			return v.v1 == this.v1 && v.v2 == this.v2 && v.v3 == this.v3
			        && v.qualifier != null
			        && v.qualifier.equals(this.qualifier);
		}
		return false;
	}

	final static Version empty = new Version(1, 0, 0, null);

	/**
	 * �͵�ǰversion�Ƚϴ�С ����ֵΪ1����ǰֵ���ڲ���version ����ֵΪ-1����ǰֵС�ڲ���version
	 * ����ֵΪ0����ǰֵ���ڲ���version
	 * 
	 * @param version
	 * @return
	 */
	public final int compareTo(Version version) {
		return this.compareTo(version.v1, version.v2, version.v3);
	}

	final int compareTo(byte v1, byte v2, short v3) {
		if (this.v1 > v1) {
			return 1;
		} else if (this.v1 < v1) {
			return -1;
		} else if (this.v2 > v2) {
			return 1;
		} else if (this.v2 < v2) {
			return -1;
		} else if (this.v3 > v3) {
			return 1;
		} else if (this.v2 < v2) {
			return -1;
		} else {
			return 0;
		}
	}

	final byte getV1() {
		return this.v1;
	}

	final byte getV2() {
		return this.v2;
	}

	final short getV3() {
		return this.v3;
	}

	final String getQualifier() {
		return this.qualifier;
	}

}
