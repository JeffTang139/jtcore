package org.eclipse.jt.core.cb;

import org.eclipse.jt.core.def.MetaElementType;
import org.eclipse.jt.core.impl.DeclaratorBuilderImpl;

/**
 * �������Ĵ���������
 * 
 * @author Jeff Tang
 * 
 */
public interface DeclaratorBuilder extends DefineHolder {

	/**
	 * �����������Ĺ�����
	 * 
	 * @author Jeff Tang
	 * 
	 */
	public interface DeclaratorBuilderFactory {

		public DeclaratorBuilder newInstance();
	}

	/**
	 * �����������ľ�̬����
	 */
	public static final DeclaratorBuilderFactory factory = new DeclaratorBuilderFactory() {

		public DeclaratorBuilder newInstance() {
			return DeclaratorBuilderImpl.newInstance();
		}
	};

	/**
	 * �����������Ĵ���
	 * 
	 * @param out
	 *            �������
	 * @param type
	 *            Ԫ��������
	 * @param name
	 *            Ŀ��Ԫ���ݶ���
	 * @param provider
	 *            Ԫ�����ṩ��
	 * @throws IllegalArgumentException
	 */
	public void build(Appendable out, MetaElementType type, String name,
			DefineProvider provider) throws IllegalArgumentException;

}
