package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.obja.StructClass;
import org.eclipse.jt.core.type.GUID;


/**
 * 序列化结构信息，用于支持两节点间存在版本不同的结构类型间兼容性问题
 * 
 * @author Jeff Tang
 * 
 */
@StructClass
public class SerializationStructInfo {
	String name;
	String[] fieldNames;
	GUID[] fieldTypeIDs;
}
