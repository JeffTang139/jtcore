package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.query.QueryStatementDefine;
import org.eclipse.jt.core.spi.sql.SQLSyntaxException;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.EnumType;
import org.eclipse.jt.core.type.ObjectDataType;
import org.eclipse.jt.core.type.SequenceDataType;
import org.eclipse.jt.core.type.TypeDetectorBase;

/**
 * 值表达式节点
 * 
 * @author Jeff Tang
 * 
 */
interface NValueExpr extends TextLocalizable, SQLVisitable {
	/**
	 * 运算结果类型
	 * 
	 * @author Jeff Tang
	 * 
	 */
	enum ResultType {
		NUMBER, BOOLEAN, STRING, GUID, DATE, BYTES, ENUM;

		static ResultType valueOf(DataType type) {
			return type.detect(typeDetector, type);
		}

		private static TypeDetectorBase<ResultType, DataType> typeDetector = new TypeDetectorBase<ResultType, DataType>() {
			@Override
			public ResultType inString(DataType userData, SequenceDataType type)
					throws Throwable {
				return ResultType.STRING;
			}

			@Override
			public ResultType inShort(DataType userData) throws Throwable {
				return ResultType.NUMBER;
			}

			@Override
			public ResultType inResource(DataType userData,
					Class<?> facadeClass, Object category) throws Throwable {
				return null;
			}

			@Override
			public ResultType inRecordSet(DataType userData) throws Throwable {
				return null;
			}

			@Override
			public ResultType inQuery(DataType userData,
					QueryStatementDefine type) throws Throwable {
				return null;
			}

			@Override
			public ResultType inObject(DataType userData, ObjectDataType type)
					throws Throwable {
				return null;
			}

			@Override
			public ResultType inLong(DataType userData) throws Throwable {
				return ResultType.NUMBER;
			}

			@Override
			public ResultType inInt(DataType userData) throws Throwable {
				return ResultType.NUMBER;
			}

			@Override
			public ResultType inGUID(DataType userData) throws Throwable {
				return ResultType.GUID;
			}

			@Override
			public ResultType inFloat(DataType userData) throws Throwable {
				return ResultType.NUMBER;
			}

			@Override
			public ResultType inEnum(DataType userData, EnumType<?> type)
					throws Throwable {
				return ResultType.ENUM;
			}

			@Override
			public ResultType inDouble(DataType userData) throws Throwable {
				return ResultType.NUMBER;
			}

			@Override
			public ResultType inDate(DataType userData) throws Throwable {
				return ResultType.DATE;
			}

			@Override
			public ResultType inBytes(DataType userData, SequenceDataType type)
					throws Throwable {
				return ResultType.BYTES;
			}

			@Override
			public ResultType inByte(DataType userData) throws Throwable {
				return ResultType.NUMBER;
			}

			@Override
			public ResultType inBoolean(DataType userData) throws Throwable {
				return ResultType.BOOLEAN;
			}
		};
	}

	public static final NValueExpr EMPTY = new NValueExpr() {
		public int startLine() {
			return 0;
		}

		public int startCol() {
			return 0;
		}

		public int endLine() {
			return 0;
		}

		public int endCol() {
			return 0;
		}

		public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
			throw new SQLSyntaxException();
		}
	};
}
