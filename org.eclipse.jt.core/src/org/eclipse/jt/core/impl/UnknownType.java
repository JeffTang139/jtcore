package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.type.AssignCapability;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.Digester;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.TypeDetector;
import org.eclipse.jt.core.type.Undigester;


/**
 * Î´ÖªÀàÐÍ
 * 
 * @author Jeff Tang
 * 
 */
public final class UnknownType extends DataTypeBase {

	@Override
	protected final GUID calcTypeID() {
		return calcNativeTypeID(ENTRY_TYPE_UNKNOWN);
	}

	@Override
	public String toString() {
		return "unknown";
	}

	private UnknownType() {
		super(null);
	}

	public final AssignCapability isAssignableFrom(DataType another) {
		return AssignCapability.NO;
	}

	public static final UnknownType TYPE = new UnknownType();

	@Override
	public final <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> caller, TUserData userData) {
		try {
			return caller.inUnknown(userData);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final void digestType(Digester digester) {
		throw new UnsupportedOperationException();
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(
				TypeCodeSet.UNKNOWN) {
			@Override
			protected DataType doUndigest(Undigester undigester) {
				throw new UnsupportedOperationException();
			}
		});
	}

	public final DataType calcPrecedence(DataType target) {
		return target;
	}

}