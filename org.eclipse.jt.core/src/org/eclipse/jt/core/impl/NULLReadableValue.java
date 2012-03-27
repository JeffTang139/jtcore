package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.ReadableValue;


/**
 * Пе
 */
public final class NULLReadableValue implements ReadableValue {
	public byte[] getBytes() {
		return null;
	}

	public boolean getBoolean() {
		return false;
	}

	public char getChar() {
		return 0;
	}

	public long getDate() {
		return 0l;
	}

	public double getDouble() {
		return 0d;
	}

	public byte getByte() {
		return 0;
	}

	public short getShort() {
		return 0;
	}

	public float getFloat() {
		return 0f;
	}

	public int getInt() {
		return 0;
	}

	public long getLong() {
		return 0l;
	}

	public String getString() {
		return null;
	}

	public GUID getGUID() {
		return null;
	}

	public Object getObject() {
		return null;
	}

	public boolean isNull() {
		return true;
	}

	public DataType getType() {
		return UnknownType.TYPE;
	}
	private NULLReadableValue(){
		
	}
	public static final NULLReadableValue INSTANCE = new NULLReadableValue();
}