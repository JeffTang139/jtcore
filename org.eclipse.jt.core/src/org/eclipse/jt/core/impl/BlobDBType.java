package org.eclipse.jt.core.impl;

import java.sql.Types;

import org.eclipse.jt.core.exception.InvalidOperandTypeException;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.TypeDetector;


/**
 * ���ݿ���������
 * 
 * @author Jeff Tang
 * 
 */
public final class BlobDBType extends BytesType {

	public static final BlobDBType TYPE = new BlobDBType();

	private BlobDBType() {
		super();
	}

	/**
	 * ���ݿ��Ƿ�������ת����Ŀ������
	 */
	@Override
	public final boolean canDBTypeConvertTo(DataType target) {
		return target == this;
	}

	/**
	 * �Ƿ��Ǵ����
	 */
	@Override
	public final boolean isLOB() {
		return true;
	}

	@Override
	public final <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> caller, TUserData userData) {
		try {
			return caller.inBlob(userData);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	@Override
	public String toString() {
		return "blob";
	}

	protected int getSqlType() {
		return Types.BLOB;
	}

	@Override
	public final DataType calcPrecedence(DataType target) {
		if (target == NullType.TYPE) {
			return this;
		} else if (target.getRootType() == BytesType.TYPE) {
			return this;
		}
		throw new InvalidOperandTypeException(this, target);
	}

	@Override
	public final boolean isDBType() {
		return true;
	}

}