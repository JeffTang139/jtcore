/**
 * 
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.ReadableValue;


public final class UNKNOWNReadableValue implements ReadableValue {
	public byte[] getBytes() {
		throw new UnsupportedOperationException();
	}

	public boolean getBoolean() {
		throw new UnsupportedOperationException();
	}

	public char getChar() {
		throw new UnsupportedOperationException();
	}

	public long getDate() {
		throw new UnsupportedOperationException();
	}

	public double getDouble() {
		throw new UnsupportedOperationException();
	}

	public byte getByte() {
		throw new UnsupportedOperationException();
	}

	public short getShort() {
		throw new UnsupportedOperationException();
	}

	public float getFloat() {
		throw new UnsupportedOperationException();
	}

	public int getInt() {
		throw new UnsupportedOperationException();
	}

	public long getLong() {
		throw new UnsupportedOperationException();
	}

	public String getString() {
		throw new UnsupportedOperationException();
	}

	public GUID getGUID() {
		throw new UnsupportedOperationException();
	}

	public Object getObject() {
		throw new UnsupportedOperationException();
	}

	public boolean isNull() {
		throw new UnsupportedOperationException();
	}

	public DataType getType() {
		return UnknownType.TYPE;
	}
	private UNKNOWNReadableValue(){
		
	}
	public static final UNKNOWNReadableValue INSTANCE = new UNKNOWNReadableValue();
}