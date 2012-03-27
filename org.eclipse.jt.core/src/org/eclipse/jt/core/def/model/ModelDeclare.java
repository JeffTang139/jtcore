package org.eclipse.jt.core.def.model;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.def.FieldDefine;
import org.eclipse.jt.core.def.ModifiableNamedElementContainer;
import org.eclipse.jt.core.def.NamedElementContainer;
import org.eclipse.jt.core.def.obja.StructDeclare;
import org.eclipse.jt.core.def.query.MappingQueryStatementDeclare;
import org.eclipse.jt.core.impl.ModelDefineImpl;
import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.type.DataTypable;
import org.eclipse.jt.core.type.DataType;

/**
 * ģ�Ͷ���ӿ�
 * 
 * @author Jeff Tang
 * 
 */
public interface ModelDeclare extends ModelDefine, StructDeclare {
	public interface Helper {
		/**
		 * �½�����ʱģ�Ͷ��壨ϵͳ������־û���ע�ᣩ
		 */
		public ModelDeclare newTempModelDeclare(String author, String name,
				Class<?> moClass);

		/**
		 * ����ģ���½���ʱģ�Ͷ��壨ϵͳ������־û���ע�ᣩ
		 * 
		 * @param template
		 *            XMLģ�壬ModelDefine.Helper
		 * @param context
		 *            �����Ķ���
		 */
		public ModelDeclare newTempModelDeclare(SXElement template,
				Context context);

		public void ensurePrepared(ModelDefine model, Context context);
	}

	// TODO ���
	public final static Helper helper = new ModelDefineImpl.HelperImpl();

	/**
	 * ����ֶζ����б�
	 * 
	 * @return �����ֶζ����б�
	 */
	public ModifiableNamedElementContainer<? extends ModelFieldDeclare> getFields();

	public ModelFieldDeclare newField(String name, DataType type);

	public ModelFieldDeclare newField(FieldDefine sample);

	public ModelFieldDeclare newField(String name, DataTypable typable);

	/**
	 * ������Զ����б�
	 * 
	 * @return �������Զ����б�
	 */
	public ModifiableNamedElementContainer<? extends ModelPropertyDeclare> getProperties();

	public ModelPropertyDeclare newProperty(String name, DataType type);

	public ModelPropertyDeclare newProperty(String name, DataTypable typable);

	public ModelPropertyDeclare newProperty(ModelFieldDefine refField);

	/**
	 * ��ö��������б�
	 * 
	 * @return ���ض��������б�
	 */
	public ModifiableNamedElementContainer<? extends ModelActionDeclare> getActions();

	public ModelActionDeclare newAction(String name, Class<?> aoClass);

	public ModelActionDeclare newAction(String name);

	/**
	 * ��ù����������б�
	 * 
	 * @return ���ع����������б�
	 */
	public ModifiableNamedElementContainer<? extends ModelConstructorDeclare> getConstructors();

	public ModelConstructorDeclare newConstructor(String name, Class<?> aoClass);

	public ModelConstructorDeclare newConstructor(String name);

	/**
	 * ���Լ�������б�
	 * 
	 * @return ����Լ�������б�
	 */
	public ModifiableNamedElementContainer<? extends ModelConstraintDeclare> getConstraints();

	/**
	 * �½�����Լ��
	 * 
	 * @param name
	 *            Լ����
	 * @param messageFormat
	 *            ��Ϣ��ʽ���ı�
	 */
	public ModelConstraintDeclare newConstraint(String name,
			String messageFormat);

	/**
	 * ��ò�ѯ����
	 * 
	 * @return ���ز�ѯ���弯��
	 */
	public ModifiableNamedElementContainer<? extends MappingQueryStatementDeclare> getQueries();

	public MappingQueryStatementDeclare newQuery(String name);

	/**
	 * ���ģ��ʵ��Դ
	 */
	public NamedElementContainer<? extends ModelObjSourceDeclare> getSources();

	public ModelObjSourceDeclare newSource(String name);

	public ModelObjSourceDeclare newSource(String name, Class<?> aoClass);

	/**
	 * ��ò�ѯ����
	 * 
	 * @return ���ز�ѯ���弯��
	 */
	public ModifiableNamedElementContainer<? extends ModelReferenceDeclare> getReferences();

	public ModelReferenceDeclare newReference(String name, ModelDefine target);

	public ModifiableNamedElementContainer<? extends ModelDeclare> getNesteds();

	public ModelDeclare newNested(String name, Class<?> moClass);

}
