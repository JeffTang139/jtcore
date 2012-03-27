package org.eclipse.jt.core.def.model;

import java.util.List;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.def.NamedDefine;
import org.eclipse.jt.core.def.apc.CheckPointDefine;
import org.eclipse.jt.core.def.arg.ArgumentableDefine;
import org.eclipse.jt.core.def.query.MappingQueryStatementDefine;
import org.eclipse.jt.core.misc.ObjectBuilder;


/**
 * ģ��ʵ��Դ���壬���Է���ģ��ʵ���б�Ķ���
 * 
 * @author Jeff Tang
 * 
 */
public interface ModelObjSourceDefine extends NamedDefine, ArgumentableDefine,
        CheckPointDefine {
	/**
	 * ����ֶζ������ڵ�ģ�Ͷ���
	 * 
	 * @return ����ģ�Ͷ���
	 */
	public ModelDefine getOwner();

	/**
	 * ģ��ʵ��Դ�Ĳ�ѯ����
	 */
	public MappingQueryStatementDefine getMappingQueryRef();

	/**
	 * ģ��ʵ��Դ�Ľű�
	 * 
	 * @return ���ؽű��������
	 */
	public ScriptDefine getScript();

	/**
	 * ���ȡʵ������Ľű�
	 */
	public ScriptDefine getMOCountOfScript();

	// /////////////////////////////////////////////////////////
	// /////////////////////// runtime /////////////////////////
	// /////////////////////////////////////////////////////////
	/**
	 * ���ģ��ʵ�������б�
	 * 
	 * @param context
	 *            ������
	 * @param ao
	 *            ��������
	 * @param mos
	 *            �ȴ�����б�
	 * @param offset
	 *            ƫ��,���ڷ�ҳ,��0��ʼ,
	 * @param count
	 *            �������Ʒ��صĸ���0��ʾû������
	 * @return ����ʵ���б�
	 */
	public <TMO> void fetchMOs(Context context, Object ao, List<TMO> mos,
	        int offset, int count);

	public <TMO> void fetchMOs(Context context, Object ao, List<TMO> mos,
	        int offset, int count, ObjectBuilder<TMO> moFactory);

	/**
	 * �����ܼ�¼����<0 ��ʾ��֧�ַ�ҳ
	 * 
	 * @param context
	 *            ������
	 * @param ao
	 *            ��������
	 * @return �����ܼ�¼����<0 ��ʾ��֧�ַ�ҳ
	 */
	public int moCountOf(Context context, Object ao);

	/**
	 * �޲����ģ��ʵ�������б�
	 * 
	 * @param context
	 *            ������
	 * @param mos
	 *            �ȴ�����б�
	 * @param offset
	 *            ƫ��,���ڷ�ҳ,��0��ʼ,
	 * @param count
	 *            �������Ʒ��صĸ�����<0 ��ʾû������
	 * @return ����ʵ���б�
	 */
	public <TMO> void fetchMOs(Context context, List<TMO> mos, int offset,
	        int count);

	public <TMO> void fetchMOs(Context context, List<TMO> mos, int offset,
	        int count, ObjectBuilder<TMO> moFactory);

	/**
	 * �����ܼ�¼����<0 ��ʾ��֧�ַ�ҳ
	 * 
	 * @param context
	 *            ������
	 * @return �����ܼ�¼����<0 ��ʾ��֧�ַ�ҳ
	 */
	public int moCountOf(Context context);
}
