/**
 * 
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.None;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.ReadableValue;


public final class ZEROReadableValue implements ReadableValue {

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
		return "";
	}

	public GUID getGUID() {
		return GUID.emptyID;
	}

	public Object getObject() {
		return None.NONE;
	}

	public boolean isNull() {
		return false;
	}

	public DataType getType() {
		return UnknownType.TYPE;
	}
	private ZEROReadableValue(){
		
	}
	public static final ZEROReadableValue INSTANCE = new ZEROReadableValue();
}