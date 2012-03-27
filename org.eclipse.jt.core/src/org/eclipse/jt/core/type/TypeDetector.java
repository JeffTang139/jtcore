package org.eclipse.jt.core.type;

import org.eclipse.jt.core.def.model.ModelDefine;
import org.eclipse.jt.core.def.obja.StructDefine;
import org.eclipse.jt.core.def.query.QueryStatementDefine;

/**
 * 类型多态回调器
 * 
 * @author Jeff Tang
 * 
 * @param <TResult>
 * @param <TUserData>
 */
public interface TypeDetector<TResult, TUserData> {

	public TResult inBoolean(TUserData userData) throws Throwable;

	public TResult inByte(TUserData userData) throws Throwable;

	public TResult inShort(TUserData userData) throws Throwable;

	public TResult inInt(TUserData userData) throws Throwable;

	public TResult inLong(TUserData userData) throws Throwable;

	public TResult inDate(TUserData userData) throws Throwable;

	public TResult inFloat(TUserData userData) throws Throwable;

	public TResult inDouble(TUserData userData) throws Throwable;

	public TResult inNumeric(TUserData userData, int precision, int scale)
			throws Throwable;

	public TResult inString(TUserData userData, SequenceDataType type)
			throws Throwable;

	public TResult inObject(TUserData userData, ObjectDataType type)
			throws Throwable;

	public TResult inVarChar(TUserData userData, SequenceDataType type)
			throws Throwable;

	public TResult inNVarChar(TUserData userData, SequenceDataType type)
			throws Throwable;

	public TResult inChar(TUserData userData, SequenceDataType type)
			throws Throwable;

	public TResult inNChar(TUserData userData, SequenceDataType type)
			throws Throwable;

	public TResult inText(TUserData userData) throws Throwable;

	public TResult inNText(TUserData userData) throws Throwable;

	public TResult inBytes(TUserData userData, SequenceDataType type)
			throws Throwable;

	public TResult inBinary(TUserData userData, SequenceDataType type)
			throws Throwable;

	public TResult inVarBinary(TUserData userData, SequenceDataType type)
			throws Throwable;

	public TResult inBlob(TUserData userData) throws Throwable;

	public TResult inGUID(TUserData userData) throws Throwable;

	public TResult inEnum(TUserData userData, EnumType<?> type)
			throws Throwable;

	public TResult inResource(TUserData userData, Class<?> facadeClass,
			Object category) throws Throwable;

	public TResult inUnknown(TUserData userData) throws Throwable;

	public TResult inStruct(TUserData userData, StructDefine type)
			throws Throwable;

	public TResult inModel(TUserData userData, ModelDefine type)
			throws Throwable;

	public TResult inQuery(TUserData userData, QueryStatementDefine type)
			throws Throwable;

	public TResult inTable(TUserData userData) throws Throwable;

	public TResult inRecordSet(TUserData userData) throws Throwable;

	public TResult inNull(TUserData userData) throws Throwable;

	public TResult inCharacter(TUserData userData) throws Throwable;
}