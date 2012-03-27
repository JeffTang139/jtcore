package org.eclipse.jt.core.impl;

import java.io.IOException;

import org.eclipse.jt.core.exception.InvalidOperandTypeException;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.type.AssignCapability;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.Digester;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.SequenceDataType;
import org.eclipse.jt.core.type.TypeDetector;
import org.eclipse.jt.core.type.Undigester;


/**
 * 二进制类型
 * 
 * @author Jeff Tang
 * 
 */
public class BytesType extends ArrayDataTypeBase implements SequenceDataType {

	public static final BytesType TYPE = new BytesType();

	@Override
	protected final GUID calcTypeID() {
		return calcBytesTypeID(this.isFixedLength(), this.isLOB(), this
				.getMaxLength());
	}

	BytesType() {
		super(byte[].class, ByteType.TYPE);
	}

	public boolean isFixedLength() {
		return false;
	}

	public int getMaxLength() {
		return 0;
	}

	@Override
	public final AssignCapability isAssignableFrom(DataType another) {
		if (another == null) {
			throw new NullArgumentException("类型");
		}
		return another.detect(assignbility, this);
	}

	private static final TypeDetector<AssignCapability, DataType> assignbility = new AssignbilityBase() {

		@Override
		public AssignCapability inString(DataType to, SequenceDataType type)
				throws Throwable {
			return AssignCapability.CONVERT;
		}

		@Override
		public AssignCapability inBytes(DataType to, SequenceDataType type)
				throws Throwable {
			if (to == type) {
				return AssignCapability.SAME;
			}
			return AssignCapability.IMPLICIT;
		}

		@Override
		public AssignCapability inNull(DataType to) throws Throwable {
			return AssignCapability.IMPLICIT;
		}

		@Override
		public AssignCapability inGUID(DataType to) throws Throwable {
			return AssignCapability.CONVERT;
		}

	};

	@Override
	public DataType calcPrecedence(DataType target) {
		if (target == NullType.TYPE) {
			return this;
		}
		DataType root = target.getRootType();
		if (root == TYPE) {
			return target;
		}
		throw new InvalidOperandTypeException(this, target);
	}

	@Override
	public String toString() {
		return "bin";
	}

	@Override
	public final BytesType getRootType() {
		final BytesType type = TYPE;
		if (type != null) {
			return type;
		} else if (this.getClass() == BytesType.class) {
			return this;
		} else {
			return TYPE;
		}
	}

	@Override
	public final Class<?> getRegClass() {
		return this.getRootType() == this ? super.getRegClass() : null;
	}

	@Override
	public final boolean isBytes() {
		return true;
	}

	@Override
	public <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> caller, TUserData userData) {
		try {
			return caller.inBytes(userData, this);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	public void digestType(Digester digester) {
		digester.update(TypeCodeSet.BYTES);
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(
				TypeCodeSet.BYTES) {
			@Override
			protected DataType doUndigest(Undigester undigester) {
				return TYPE;
			}
		});
	}

	// ///////////////////////////////////////
	// Serialization

	@Override
	public final void writeObjectData(InternalSerializer serializer, Object obj)
			throws IOException, StructDefineNotFoundException {
		if (obj == null) {
			serializer.writeInt(-1);
		} else {
			byte[] ba = (byte[]) obj;
			serializer.writeInt(ba.length);
			serializer.writeBytes(ba);
		}
	}

	@Override
	public Object readObjectData(InternalDeserializer deserializer)
			throws IOException, StructDefineNotFoundException {
		int len = deserializer.readInt();
		if (len == -1) {
			return null;
		}
		byte[] ba = new byte[len];
		if (len > 0) {
			deserializer.readFully(ba);
		}
		return ba;
	}

	// //////////////////////////////////////////
	// / NEW IO Serialization
	// //////////////////////////////////////////

	@Override
	public final boolean nioSerializeData(final NSerializer serializer,
			final Object object) {
		return serializer.writeByteArrayData((byte[]) object);
	}

}
