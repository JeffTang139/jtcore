package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.type.DataType;

/**
 * 对象访问上下文
 * 
 * @author Jeff Tang
 * 
 */
final class OBJAContext {

	OBJAContext() {
	}

	private ObjMap objMap;

	public final Object assign(Object srcObj, Object dest, DataType type) {
		if (srcObj == null || srcObj == dest) {
			return srcObj;
		}
		if (this.objMap != null) {
			final Object destObj = this.objMap.get(srcObj);
			if (destObj != null) {
				return destObj;
			}
		}
		final ObjectDataTypeInternal objectDataType;
		final DynObj dynSrcObj;
		resolveAssigner: {
			Class<?> srcObjClass = srcObj.getClass();
			if (type instanceof StructDefineImpl) {
				StructDefineImpl define = (StructDefineImpl) type;
				if (define.soClass == srcObjClass) {
					if (define.isDynObj) {
						dynSrcObj = (DynObj) srcObj;
						if (dynSrcObj.define != define
								&& dynSrcObj.define != null) {
							objectDataType = dynSrcObj.define;
							break resolveAssigner;
						}
					} else {
						dynSrcObj = null;
					}
					objectDataType = define;
					break resolveAssigner;
				}
			}
			if (srcObj instanceof DynObj) {
				dynSrcObj = (DynObj) srcObj;
				objectDataType = dynSrcObj.define != null ? dynSrcObj.define
						: (ObjectDataTypeInternal) DataTypeBase
								.dataTypeOfJavaClass(srcObjClass);
				break resolveAssigner;
			} else {
				dynSrcObj = null;
				objectDataType = (ObjectDataTypeInternal) DataTypeBase
						.dataTypeOfJavaClass(srcObjClass);
			}
		}
		final Object destObj;
		if (dynSrcObj != null) {
			destObj = objectDataType.assignNoCheckSrcD(dynSrcObj, dest, this);
		} else {
			destObj = objectDataType.assignNoCheckSrc(srcObj, dest, this);
		}
		if (this.objMap == null) {
			this.objMap = new ObjMap();
		}
		this.objMap.put(srcObj, destObj);
		return destObj;
	}
}
