package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.obja.StructClass;
import org.eclipse.jt.core.type.GUID;


/**
 * ���л��ṹ��Ϣ������֧�����ڵ����ڰ汾��ͬ�Ľṹ���ͼ����������
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
