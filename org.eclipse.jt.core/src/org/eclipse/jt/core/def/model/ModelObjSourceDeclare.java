package org.eclipse.jt.core.def.model;

import org.eclipse.jt.core.def.NamedDeclare;
import org.eclipse.jt.core.def.arg.ArgumentableDeclare;
import org.eclipse.jt.core.def.query.MappingQueryStatementDefine;
import org.eclipse.jt.core.model.ModelService;

/**
 * ģ��ʵ��Դ���壬���Է���ģ��ʵ���б�Ķ���
 * 
 * @author Jeff Tang
 * 
 */
public interface ModelObjSourceDeclare extends ModelObjSourceDefine,
        NamedDeclare, ArgumentableDeclare {
	/**
	 * ����ֶζ������ڵ�ģ�Ͷ���
	 * 
	 * @return ����ģ�Ͷ���
	 */
	public ModelDeclare getOwner();

	/**
	 * �������Ľű�
	 * 
	 * @return ���ؽű��������
	 */
	public ScriptDeclare getScript();

	/**
	 * ���ȡʵ������Ľű�
	 */
	public ScriptDeclare getMOCountOfScript();

	/**
	 * ����ģ��ʵ��Դ�ṩ����<br>
	 * 
	 * @return ���ؾɵ�ʵ��Դ�ṩ��
	 */
	public ModelService<?>.ModelObjProvider<?> setProvider(
	        ModelService<?>.ModelObjProvider<?> provider);

	public MappingQueryStatementDefine setMappingQueryRef(MappingQueryStatementDefine ref);
}
