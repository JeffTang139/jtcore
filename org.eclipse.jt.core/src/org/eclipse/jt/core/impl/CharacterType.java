package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.type.AssignCapability;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.Digester;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.SequenceDataType;
import org.eclipse.jt.core.type.TypeDetector;


public final class CharacterType extends DataTypeBase {

	public static final CharacterType TYPE = new CharacterType();

	private CharacterType() {
		super(char.class);
	}

	@Override
	protected final GUID calcTypeID() {
		return calcNativeTypeID(PRIMITIVE_TYPE_CHAR);
	}

	@Override
	public final boolean canDBTypeConvertTo(final DataType target) {
		return false;
	}

	@Override
	public final <TResult, TUserData> TResult detect(
			final TypeDetector<TResult, TUserData> caller,
			final TUserData userData) {
		try {
			return caller.inCharacter(userData);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final DataType calcPrecedence(final DataType target) {
		throw new UnsupportedOperationException();
	}

	public final AssignCapability isAssignableFrom(final DataType source) {
		if (source == null) {
			throw new NullArgumentException("¿‡–Õ");
		}
		return source.detect(assignbility, this);
	}

	private static final TypeDetector<AssignCapability, DataType> assignbility = new AssignbilityBase() {

		@Override
		public AssignCapability inBoolean(DataType to) throws Throwable {
			return AssignCapability.CONVERT;
		}

		@Override
		public final AssignCapability inCharacter(final DataType to)
				throws Throwable {
			return AssignCapability.SAME;
		}

		@Override
		public AssignCapability inString(DataType to, SequenceDataType type)
				throws Throwable {
			return AssignCapability.CONVERT;
		}

		@Override
		public AssignCapability inNull(DataType to) throws Throwable {
			return AssignCapability.IMPLICIT;
		}

	};

	public void digestType(Digester digester) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final String toString() {
		return "character";
	}

	@Override
	public final boolean isDBType() {
		return false;
	}
}
