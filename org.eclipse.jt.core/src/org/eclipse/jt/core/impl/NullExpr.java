package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.GUID;


/**
 * 空表达式
 * 
 * @author Jeff Tang
 * 
 */
public final class NullExpr extends ConstExpr {

	public final boolean isNull() {
		return true;
	}

	public final Object getObject() {
		return null;
	}

	public final boolean getBoolean() {
		return false;
	}

	public final char getChar() {
		return 0;
	}

	public final byte getByte() {
		return 0;
	}

	public final short getShort() {
		return 0;
	}

	public final int getInt() {
		return 0;
	}

	public final long getLong() {
		return 0;
	}

	public final long getDate() {
		return 0;
	}

	public final float getFloat() {
		return 0;
	}

	public final double getDouble() {
		return 0;
	}

	public final byte[] getBytes() {
		return null;
	}

	public final String getString() {
		return null;
	}

	public final GUID getGUID() {
		return null;
	}

	public final DataTypeBase getType() {
		return NullType.TYPE;
	}

	@Override
	public final String toString() {
		return "NULL";
	}

	@Override
	final String getDescription() {
		return "空表达式";
	}

	public final static NullExpr NULL = new NullExpr();

	static final String xml_element_null = "null-exp";

	private NullExpr() {
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitNullExpr(this, context);
	}

	@Override
	final void render(ISqlExprBuffer buffer, TableUsages usages) {
		buffer.loadNull(null);
	}

	DataType determine;

}
