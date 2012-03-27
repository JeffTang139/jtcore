package org.eclipse.jt.core.impl;

import static org.eclipse.jt.core.impl.TypeCompatiblity.Exactly;
import static org.eclipse.jt.core.impl.TypeCompatiblity.NotSuggest;
import static org.eclipse.jt.core.impl.TypeCompatiblity.Overflow;
import static org.eclipse.jt.core.impl.TypeCompatiblity.Unable;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.SequenceDataType;
import org.eclipse.jt.core.type.TypeDetector;
import org.eclipse.jt.core.type.TypeDetectorBase;


final class OracleLang extends DBLang {

	OracleLang(Connection conn) {
		super(CHECK_CONN, "oracle");
	}

	static final String CHECK_CONN = "select 1 from dual";

	@Override
	final String getDefaultSchema(DataSourceImpl source) {
		if (SystemVariables.ORACLE_USER_AUTO_UPPERCASE) {
			return source.getUser().toUpperCase();
		} else {
			return source.getUser();
		}
	}

	@Override
	final int getMaxColumnNameLength() {
		return 30;
	}

	@Override
	final int getMaxIndexNameLength() {
		return 30;
	}

	@Override
	final int getMaxTableNameLength() {
		return 30;
	}

	@Override
	final int getMaxTablePartCount() {
		return 1048575;
	}

	@Override
	final int getDefaultPartSuggestion() {
		return 65536;
	}

	@Override
	final void format(Appendable str, DataType type) {
		type.detect(OracleTypeFormatter.instance, str);
	}

	@Override
	final OracleTableSynchronizer newSynchronizer(DBAdapterImpl dbAdapter)
			throws SQLException {
		return new OracleTableSynchronizer(this, dbAdapter);
	}

	@Override
	final TablePartitioner newPartitioner() {
		return null;
	}

	@Override
	final void formatId(Appendable str, String name) {
		try {
			str.append('"').append(name).append('"');
		} catch (IOException e) {
			throw Utils.tryThrowException(e);
		}
	}

	@Override
	final ISqlCommandFactory sqlbuffers() {
		return OracleCommandFactory.INSTANCE;
	}

	final TypeDetector<TypeCompatiblity, OracleColumn> getTypeCompatibleDetector() {
		return compatible;
	}

	// for oracle 10
	private static final TypeDetector<TypeCompatiblity, OracleColumn> compatible = new TypeDetectorBase<TypeCompatiblity, OracleColumn>() {

		@Override
		public TypeCompatiblity inBoolean(OracleColumn column) throws Throwable {
			if (column.type == OracleDataType.NUMBER && column.scale == 0) {
				if (column.precision == 1) {
					return Exactly;
				}
				return Overflow;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inShort(OracleColumn column) throws Throwable {
			if (column.type == OracleDataType.NUMBER && column.scale == 0) {
				if (column.precision == 5) {
					return Exactly;
				} else if (column.precision > 5 || column.precision == 0) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inInt(OracleColumn column) throws Throwable {
			if (column.type == OracleDataType.NUMBER && column.scale == 0) {
				if (column.precision == 10) {
					return Exactly;
				} else if (column.precision > 10 || column.precision == 0) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inLong(OracleColumn column) throws Throwable {
			if (column.type == OracleDataType.NUMBER && column.scale == 0) {
				if (column.precision == 19) {
					return Exactly;
				} else if (column.precision > 19 || column.precision == 0) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inFloat(OracleColumn column) throws Throwable {
			if (column.type == OracleDataType.FLOAT && column.precision >= 63) {
				return Exactly;
			} else if (column.type == OracleDataType.BINARY_FLOAT
					|| column.type == OracleDataType.BINARY_DOUBLE) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inDouble(OracleColumn column) throws Throwable {
			if (column.type == OracleDataType.FLOAT && column.precision >= 126) {
				return Exactly;
			} else if (column.type == OracleDataType.BINARY_FLOAT
					|| column.type == OracleDataType.BINARY_DOUBLE) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inNumeric(OracleColumn column, int precision,
				int scale) throws Throwable {
			if (column.type == OracleDataType.NUMBER) {
				if (column.precision == precision && column.scale == scale) {
					return Exactly;
				} else if (((column.precision - column.scale) >= (precision - scale))
						&& column.scale >= scale) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inChar(OracleColumn column,
				SequenceDataType type) throws Throwable {
			final int length = type.getMaxLength();
			if (column.type == OracleDataType.CHAR) {
				if (column.length == length) {
					return Exactly;
				} else if (column.length > length) {
					return NotSuggest;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inVarChar(OracleColumn column,
				SequenceDataType type) throws Throwable {
			final int length = type.getMaxLength();
			if (column.type == OracleDataType.VARCHAR2) {
				if (column.length == length) {
					return Exactly;
				} else if (column.length > length) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inText(OracleColumn column) throws Throwable {
			if (column.type == OracleDataType.CLOB) {
				return Exactly;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inNChar(OracleColumn column,
				SequenceDataType type) throws Throwable {
			final int length = type.getMaxLength();
			if (column.type == OracleDataType.NCHAR) {
				if (column.length == length) {
					return Exactly;
				} else if (column.length > length) {
					return NotSuggest;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inNVarChar(OracleColumn column,
				SequenceDataType type) throws Throwable {
			final int length = type.getMaxLength();
			if (column.type == OracleDataType.NVARCHAR2) {
				if (column.length == length) {
					return Exactly;
				} else if (column.length > length) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inNText(OracleColumn column) throws Throwable {
			if (column.type == OracleDataType.NCLOB) {
				return Exactly;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inBinary(OracleColumn column,
				SequenceDataType type) throws Throwable {
			final int length = type.getMaxLength();
			if (column.type == OracleDataType.RAW) {
				if (column.length == length) {
					return Exactly;
				} else if (column.length > length) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inVarBinary(OracleColumn column,
				SequenceDataType type) throws Throwable {
			final int length = type.getMaxLength();
			if (column.type == OracleDataType.RAW) {
				if (column.length == length) {
					return Exactly;
				} else if (column.length > length) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inBlob(OracleColumn column) throws Throwable {
			if (column.type == OracleDataType.BLOB) {
				return Exactly;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inGUID(OracleColumn column) throws Throwable {
			if (column.type == OracleDataType.RAW) {
				if (column.length == 16) {
					return Exactly;
				} else if (column.length > 16) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inDate(OracleColumn column) throws Throwable {
			if (column.type == OracleDataType.TIMESTAMP) {
				return Exactly;
			} else if (column.type == OracleDataType.TIMESTAMP_WITH_TIME_ZONE
					|| column.type == OracleDataType.TIMESTAMP_WITH_LOCAL_TIME_ZONE) {
				return Overflow;
			}
			return Unable;
		}

	};

	@Override
	final void setupPackage(Connection conn, String ds) {
		execSqls(conn, this.getClass(), DNA_PACKAGE_SETUP + "." + this.postfix,
				true);
	}

}
