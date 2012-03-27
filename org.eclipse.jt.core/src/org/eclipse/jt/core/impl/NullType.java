package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.type.AssignCapability;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.Digester;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.TypeDetector;


public final class NullType extends DataTypeBase {

	static final NullType TYPE = new NullType();

	@Override
	protected final GUID calcTypeID() {
		return calcNativeTypeID(ENTRY_TYPE_NULL);
	}

	@Override
	public String toString() {
		return "null";
	}

	private NullType() {
		super(null);
	}

	public AssignCapability isAssignableFrom(DataType another) {
		if (another == null) {
			throw new NullArgumentException("¿‡–Õ");
		}
		return AssignCapability.NO;
	}

	public DataType calcPrecedence(DataType target) {
		return target;
	}

	public final void digestType(Digester digester) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> caller, TUserData userData) {
		try {
			return caller.inNull(userData);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}
}
