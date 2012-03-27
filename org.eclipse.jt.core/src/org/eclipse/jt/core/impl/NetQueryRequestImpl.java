package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.auth.Operation;
import org.eclipse.jt.core.def.obja.StructClass;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.invoke.MoreKeyOverlappedResult;
import org.eclipse.jt.core.invoke.Return;
import org.eclipse.jt.core.misc.MissingObjectException;

final class NetQueryRequestImpl<TResult, TKey1, TKey2, TKey3> extends
		NetRequestImpl implements
		MoreKeyOverlappedResult<TResult, TKey1, TKey2, TKey3> {
	final static byte QUERY_RESULT_TYPE_FIND = 0;
	final static byte QUERY_RESULT_TYPE_GET = 1;
	final static byte QUERY_RESULT_TYPE_LIST = 2;
	final static byte QUERY_RESULT_TYPE_TREE = 3;

	@Override
	public final Object getDataObject() {
		return this.queryData;
	}

	/**
	 * 查询任务，用以装载查询键和返回结果的任务
	 * 
	 * @author Jeff Tang
	 * 
	 */
	@StructClass
	static final class RemoteQueryData<TResult, TKey1, TKey2, TKey3> {
		final byte resultType;
		final Class<TResult> resultClass;
		final Operation<? super TResult> operation;
		final TKey1 key1;
		final TKey2 key2;
		final TKey3 key3;
		final Object[] otherKeys;
		@Return
		TResult result;

		RemoteQueryData(byte resultType, Class<TResult> resultClass,
				Operation<? super TResult> operation, TKey1 key1, TKey2 key2,
				TKey3 key3, Object[] otherKeys) {
			switch (resultType) {
			case QUERY_RESULT_TYPE_FIND:
			case QUERY_RESULT_TYPE_GET:
			case QUERY_RESULT_TYPE_LIST:
			case QUERY_RESULT_TYPE_TREE:
				break;
			default:
				throw new IllegalArgumentException("resultType: " + resultType);
			}
			if (resultClass == null) {
				throw new NullArgumentException("resultClass");
			}
			this.resultType = resultType;
			this.resultClass = resultClass;
			this.operation = operation;
			this.key1 = key1;
			this.key2 = key2;
			this.key3 = key3;
			this.otherKeys = otherKeys;
		}
	}

	private final RemoteQueryData<TResult, TKey1, TKey2, TKey3> queryData;

	public final TResult getResult() throws IllegalStateException,
			MissingObjectException {
		this.checkFinished();
		final TResult result = this.queryData.result;
		if (result == null) {
			throw new MissingObjectException();
		}
		return result;
	}

	public final boolean isNull() throws IllegalStateException {
		this.checkFinished();
		return this.queryData.result == null;
	}

	public final TKey1 getKey1() {
		return this.queryData.key1;
	}

	public final TKey2 getKey2() {
		return this.queryData.key2;
	}

	public final TKey3 getKey3() {
		return this.queryData.key3;
	}

	public final Object[] getOtherKeys() {
		return this.queryData.otherKeys;
	}

	public final Class<TResult> getResultClass() {
		return this.queryData.resultClass;
	}

	NetQueryRequestImpl(NetSessionImpl session, byte resultType,
			Class<TResult> resultClass, Operation<? super TResult> operation,
			TKey1 key1, TKey2 key2, TKey3 key3, Object[] otherKeys) {
		super(session);
		this.queryData = new RemoteQueryData<TResult, TKey1, TKey2, TKey3>(
				resultType, resultClass, operation, key1, key2, key3, otherKeys);
	}

}