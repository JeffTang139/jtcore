/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File RemoteStructQuery.java
 * Date 2009-3-25
 */
package org.eclipse.jt.core.impl;

import java.io.IOException;

import org.eclipse.jt.core.def.model.ModelDefine;
import org.eclipse.jt.core.def.obja.StructClass;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.type.DataType;


/**
 * 远程结构定义查询。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
@StructClass
final class RemoteStructQuery implements RemoteRequest<RemoteQueryStubImpl> {
	/**
	 * 结构定义的摘要信息。
	 */
	final StructSummary structSummary;

	/**
	 * 远程结构定义查询的构造器。
	 * 
	 * @param structSummary
	 *            结构定义的摘要信息。
	 */
	RemoteStructQuery(StructSummary structSummary) {
		this.structSummary = structSummary;
	}

	public RemoteReturn execute(ContextImpl<?, ?, ?> context) throws Throwable {
		StructDefineImpl define = (StructDefineImpl) context.occorAt
				.findNamedDefine(ModelDefine.class,
						this.structSummary.defineName);
		if (define == null) {
			Class<?> soClass = context.session.application
					.tryLoadClass(this.structSummary.defineName);
			if (soClass != null) {
				DataType odt = DataTypeBase.dataTypeOfJavaClass(soClass);
				if (odt instanceof StructDefineImpl) {
					define = (StructDefineImpl) odt;
				} else {
					throw new UnsupportedOperationException(
							"unexpected data type: " + odt);
				}
			}
		}
		return new StructReturn(define);
	}

	public void writeTo(StructuredObjectSerializer serializer)
			throws IOException, StructDefineNotFoundException {
		serializer.serialize(this);
	}

	/**
	 * 获取远程请求的数据包的代码。
	 * 
	 * @return 远程请求的数据包的代码。
	 */
	public final PacketCode getPacketCode() {
		return PacketCode.STRUCT_REQUEST;
	}

	public RemoteQueryStubImpl newStub(NetConnection netConnection) {
		if (netConnection == null) {
			throw new NullArgumentException("netConnection");
		}
		return new RemoteQueryStubImpl(netConnection, this);
	}
}
