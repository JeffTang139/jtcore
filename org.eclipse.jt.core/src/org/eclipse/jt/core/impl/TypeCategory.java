package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.model.ModelDefine;
import org.eclipse.jt.core.def.obja.StructDefine;
import org.eclipse.jt.core.def.query.QueryStatementDefine;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.EnumType;
import org.eclipse.jt.core.type.ObjectDataType;
import org.eclipse.jt.core.type.SequenceDataType;
import org.eclipse.jt.core.type.TypeDetector;

enum TypeCategory {
	VAR, FIELD, BOTH, UNUSE;

	private static final TypeDetector<TypeCategory, DataType> detector = new TypeDetector<TypeCategory, DataType>() {

		public TypeCategory inVarChar(DataType userData, SequenceDataType type)
				throws Throwable {
			return FIELD;
		}

		public TypeCategory inVarBinary(DataType userData, SequenceDataType type)
				throws Throwable {
			return FIELD;
		}

		public TypeCategory inUnknown(DataType userData) throws Throwable {
			return UNUSE;
		}

		public TypeCategory inText(DataType userData) throws Throwable {
			return FIELD;
		}

		public TypeCategory inTable(DataType userData) throws Throwable {
			return UNUSE;
		}

		public TypeCategory inStruct(DataType userData, StructDefine type)
				throws Throwable {
			return UNUSE;
		}

		public TypeCategory inString(DataType userData, SequenceDataType type)
				throws Throwable {
			return VAR;
		}

		public TypeCategory inShort(DataType userData) throws Throwable {
			return BOTH;
		}

		public TypeCategory inResource(DataType userData, Class<?> facadeClass,
				Object category) throws Throwable {
			return UNUSE;
		}

		public TypeCategory inRecordSet(DataType userData) throws Throwable {
			return UNUSE;
		}

		public TypeCategory inQuery(DataType userData, QueryStatementDefine type)
				throws Throwable {
			return UNUSE;
		}

		public TypeCategory inObject(DataType userData, ObjectDataType type)
				throws Throwable {
			return UNUSE;
		}

		public TypeCategory inNumeric(DataType userData, int precision,
				int scale) throws Throwable {
			return FIELD;
		}

		public TypeCategory inNVarChar(DataType userData, SequenceDataType type)
				throws Throwable {
			return FIELD;
		}

		public TypeCategory inNText(DataType userData) throws Throwable {
			return FIELD;
		}

		public TypeCategory inNChar(DataType userData, SequenceDataType type)
				throws Throwable {
			return FIELD;
		}

		public TypeCategory inModel(DataType userData, ModelDefine type)
				throws Throwable {
			return UNUSE;
		}

		public TypeCategory inLong(DataType userData) throws Throwable {
			return BOTH;
		}

		public TypeCategory inInt(DataType userData) throws Throwable {
			return BOTH;
		}

		public TypeCategory inGUID(DataType userData) throws Throwable {
			return BOTH;
		}

		public TypeCategory inFloat(DataType userData) throws Throwable {
			return BOTH;
		}

		public TypeCategory inEnum(DataType userData, EnumType<?> type)
				throws Throwable {
			return VAR;
		}

		public TypeCategory inDouble(DataType userData) throws Throwable {
			return BOTH;
		}

		public TypeCategory inDate(DataType userData) throws Throwable {
			return BOTH;
		}

		public TypeCategory inChar(DataType userData, SequenceDataType type)
				throws Throwable {
			return FIELD;
		}

		public TypeCategory inBytes(DataType userData, SequenceDataType type)
				throws Throwable {
			return VAR;
		}

		public TypeCategory inByte(DataType userData) throws Throwable {
			return VAR;
		}

		public TypeCategory inBoolean(DataType userData) throws Throwable {
			return BOTH;
		}

		public TypeCategory inBlob(DataType userData) throws Throwable {
			return FIELD;
		}

		public TypeCategory inBinary(DataType userData, SequenceDataType type)
				throws Throwable {
			return FIELD;
		}

		public TypeCategory inNull(DataType userData) throws Throwable {
			return UNUSE;
		}

		public TypeCategory inCharacter(DataType userData) throws Throwable {
			return UNUSE;
		}
	};

	public static TypeCategory typeOf(DataType type) {
		return type.detect(detector, type);
	}
}
