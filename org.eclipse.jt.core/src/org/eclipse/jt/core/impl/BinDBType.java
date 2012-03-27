/**
 * 
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.type.DataType;

/**
 * 数据库二进制类型
 * 
 * @author Jeff Tang
 * 
 */
public abstract class BinDBType extends BytesType {

	public final static int BIN_LENGTH_MAX = 2000;

	/**
	 * 数据库是否允许本类转换成目标类型
	 */
	@Override
	public final boolean canDBTypeConvertTo(DataType target) {
		if (target == this) {
			return true;
		}
		return target instanceof BinDBType
				&& this.length <= ((BinDBType) target).length;
	}

	public static DataType tryParse(String str) {
		DataType type = VarBinDBType.tryParse(str);
		if (type == null) {
			type = FixBinDBType.tryParse(str);
		}
		return type;
	}

	public final int length;

	@Override
	public int getMaxLength() {
		return this.length;
	}

	@Override
	public final boolean isDBType() {
		return true;
	}

	@Override
	final void regArrayDataTypeInConstructor() {
	}

	public BinDBType(int length) {
		super();
		this.length = length;
		regDataType(this);
	}
}