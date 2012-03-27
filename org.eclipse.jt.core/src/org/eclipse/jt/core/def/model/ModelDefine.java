package org.eclipse.jt.core.def.model;

import org.eclipse.jt.core.def.MetaElement;
import org.eclipse.jt.core.def.NamedElementContainer;
import org.eclipse.jt.core.def.obja.StructDefine;
import org.eclipse.jt.core.def.query.MappingQueryStatementDefine;
import org.eclipse.jt.core.misc.SXElement;

/**
 * ģ�Ͷ���ӿ�
 * 
 * @author Jeff Tang
 * 
 */
public interface ModelDefine extends MetaElement, StructDefine {
	/**
	 * ��ģ�ͱ����XMLģ��
	 * 
	 * @param toElement
	 *            ��ģ�Ͷ���������Ľڵ�
	 */
	public void render(SXElement toElement);

	/**
	 * ��ȡģ��ʵ���������
	 * 
	 * @return ����ģ��ʵ��������
	 */
	public Class<?> getMOClass();

	/**
	 * ����ֶζ����б�
	 * 
	 * @return �����ֶζ����б�
	 */
	public NamedElementContainer<? extends ModelFieldDefine> getFields();

	/**
	 * ������Զ����б�
	 * 
	 * @return �������Զ����б�
	 */
	public NamedElementContainer<? extends ModelPropertyDefine> getProperties();

	/**
	 * ��ö��������б�
	 * 
	 * @return ���ض��������б�
	 */
	public NamedElementContainer<? extends ModelActionDefine> getActions();

	/**
	 * ��ù����������б�
	 * 
	 * @return ���ع����������б�
	 */
	public NamedElementContainer<? extends ModelConstructorDefine> getConstructors();

	/**
	 * ���Լ�������б�
	 * 
	 * @return ����Լ�������б�
	 */
	public NamedElementContainer<? extends ModelConstraintDefine> getConstraints();

	/**
	 * ��ò�ѯ����
	 * 
	 * @return ���ز�ѯ���弯��
	 */
	public NamedElementContainer<? extends MappingQueryStatementDefine> getQueries();

	/**
	 * ��òο�ģ�ͼ���
	 * 
	 * @return ���زο�ģ�ͼ���
	 */
	public NamedElementContainer<? extends ModelReferenceDefine> getReferences();

	/**
	 * ���ģ��ʵ��Դ
	 */
	public NamedElementContainer<? extends ModelObjSourceDefine> getSources();

	/**
	 * ���Ƕ��ģ�͵ļ���
	 */
	public NamedElementContainer<? extends ModelDefine> getNesteds();

	// /////////////////////////////////////////////////////////
	// /////////////////////// runtime /////////////////////////
	// /////////////////////////////////////////////////////////
	/**
	 * �����յ�ģ��ʵ������
	 */
	public Object newMO();
}
