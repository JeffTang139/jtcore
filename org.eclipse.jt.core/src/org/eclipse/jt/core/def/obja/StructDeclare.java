package org.eclipse.jt.core.def.obja;

import org.eclipse.jt.core.def.FieldDefine;
import org.eclipse.jt.core.def.ModifiableNamedElementContainer;
import org.eclipse.jt.core.def.NamedDeclare;
import org.eclipse.jt.core.type.DataTypable;
import org.eclipse.jt.core.type.DataType;

/**
 * �ṹ����
 * 
 * @author Jeff Tang
 * 
 */
public interface StructDeclare extends StructDefine, NamedDeclare {
	/**
	 * ����ֶζ����б�
	 * 
	 * @return �����ֶζ����б�
	 */
	public ModifiableNamedElementContainer<? extends StructFieldDeclare> getFields();

	public StructFieldDeclare newField(String name, DataType type);

	public StructFieldDeclare newField(FieldDefine sample);

	public StructFieldDeclare newField(String name, DataTypable typable);
}
