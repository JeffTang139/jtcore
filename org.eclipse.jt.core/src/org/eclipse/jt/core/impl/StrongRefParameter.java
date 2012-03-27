package org.eclipse.jt.core.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

class StrongRefParameter implements ParameterReserver {

	private final ArrayList<Integer> indexes = new ArrayList<Integer>();

	public final void reserve(int index) {
		this.indexes.add(index);
	}

	final void setInt(PreparedStatement ps, int value) throws SQLException {
		if (this.indexes.size() > 0) {
			for (int i = 0, c = this.indexes.size(); i < c; i++) {
				ps.setInt(this.indexes.get(i) + 1, value);
			}
		}

	}

	final void setLong(PreparedStatement ps, long value) throws SQLException {

		if (this.indexes.size() > 0) {
			for (int i = 0, c = this.indexes.size(); i < c; i++) {
				ps.setLong(this.indexes.get(i) + 1, value);
			}
		}
	}

	final void setBytes(PreparedStatement ps, byte[] value) throws SQLException {
		if (this.indexes.size() > 0) {
			for (int i = 0, c = this.indexes.size(); i < c; i++) {
				ps.setBytes(this.indexes.get(i) + 1, value);
			}
		}
	}

}
