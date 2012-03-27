package org.eclipse.jt.core.cb;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Connection;
import java.util.Map;

import org.eclipse.jt.core.Filter;
import org.eclipse.jt.core.def.MetaElementType;
import org.eclipse.jt.core.def.NamedDefine;
import org.eclipse.jt.core.def.table.TableDefine;
import org.eclipse.jt.core.impl.Utils;
import org.eclipse.jt.core.misc.ExceptionCatcher;
import org.eclipse.jt.core.misc.SXElement;


/**
 * Ԫ���ݶ�������
 * 
 * <p>
 * ���ڹ�����ʱ�Ļ���,����������.
 * 
 * @author Jeff Tang
 * 
 */
public interface DefineHolder {

	/**
	 * ����Ԫ���ݶ��嵽������,���������ͬ���ͺ����Ƶ�Ԫ���ݶ���,���滻.
	 * 
	 * @param type
	 *            ��������
	 * @param reader
	 *            ����ű����ַ���ȡ��
	 * @param pkgname
	 *            ����İ���
	 * @param clzname
	 *            ���������
	 * @param provider
	 *            �����ṩ��
	 */
	public void putDefine(MetaElementType type, Reader reader, String pkgname,
			String clzname, DefineProvider provider);

	/**
	 * ����Ԫ���ݶ��嵽������,���������ͬ���ͺ����Ƶ�Ԫ���ݶ���,���滻.
	 * 
	 * @param type
	 *            ��������
	 * @param input
	 *            ����ű���������
	 * @param pkgname
	 *            ����İ���
	 * @param clzname
	 *            ���������
	 * @param provider
	 *            �����ṩ��
	 */
	public void putDefine(MetaElementType type, InputStream input,
			String pkgname, String clzname, DefineProvider provider);

	/**
	 * ����Ԫ���ݶ��嵽������,���������ͬ���ͺ����Ƶ�Ԫ���ݶ���,���滻.
	 * 
	 * @param type
	 *            ��������
	 * @param file
	 *            ����ű����ļ�
	 * @param pkgname
	 *            ����İ���
	 * @param filename
	 *            ���������
	 * @param provider
	 *            �����ṩ��
	 */
	public void putDefine(MetaElementType type, File file, String pkgname,
			String filename, DefineProvider provider);

	/**
	 * ������嵽������,���������ͬ���Ƶı���,���滻.
	 * 
	 * @param in
	 *            �����<strong>XML��ʽ</strong>��������
	 * @param pkgname
	 *            ����İ���
	 * @param clzname
	 *            ���������
	 * @param provider
	 *            �����ṩ��
	 */
	public void putTableDefine(InputStream in, String pkgname, String clzname,
			DefineProvider provider);

	/**
	 * ������嵽������,���������ͬ���Ƶı���,���滻.
	 * 
	 * @param element
	 *            �����XML����
	 * @param pkgname
	 *            ����İ���
	 * @param clzname
	 *            ���������
	 * @param provider
	 *            �����ṩ��
	 */
	public void putTableDefine(SXElement element, String pkgname,
			String clzname, DefineProvider provider);

	/**
	 * �Ƴ�ָ����Ԫ����
	 * 
	 * @param type
	 *            Ԫ��������
	 * @param name
	 *            Ԫ��������
	 */
	public void removeDefine(MetaElementType type, String name);

	/**
	 * �����ݿ��Ԫ���ݱ���װ��Ԫ���ݶ���
	 * 
	 * <p>
	 * ͨ���˷�ʽװ�ص�Ԫ���ݶ���,��pkgname��clzname����Ϊ��.
	 * 
	 * @param type
	 *            Ԫ��������
	 * @param connection
	 *            ���ݿ�����
	 * @param provider
	 *            Ԫ�����ṩ��
	 * @param catcher
	 *            �쳣������
	 */
	public void loadDefine(MetaElementType type, Connection connection,
			DefineProvider provider, ExceptionCatcher catcher);

	/**
	 * ��������еĶ���
	 */
	public void clearDefines();

	/**
	 * ���ҷ���ָ�����Ƶı���
	 * 
	 * <p>
	 * ������ϵ���ý���ʧ��,ʹ���쳣�����������䳤,����Ȼ���ز������ϵ���߼���.
	 * 
	 * @param name
	 * @param catcher
	 * @return
	 */
	public TableDefine findTableDefine(String name, ExceptionCatcher catcher);

	/**
	 * ��ȡָ�����Ƶı���
	 * 
	 * <p>
	 * ������ϵ���ý���ʧ��,ʹ���쳣�����������䳤,����Ȼ���ز������ϵ���߼���.
	 * 
	 * @param name
	 * @param catcher
	 * @return
	 */
	public TableDefine getTableDefine(String name, ExceptionCatcher catcher);

	/**
	 * ֱ���׳��쳣���쳣������
	 */
	public static final ExceptionCatcher THROWS_CATCHER = new ExceptionCatcher() {

		public void catchException(Throwable e, Object sender) {
			throw Utils.tryThrowException(e);
		}
	};

	/**
	 * ���쳣ͨ������̨��ӡ�����쳣������
	 */
	public static final ExceptionCatcher PRINTS_CATCHER = new ExceptionCatcher() {

		public void catchException(Throwable e, Object sender) {
			System.err.println((sender == null ? "" : "�����쳣��:"
					+ sender.toString() + ".")
					+ "�쳣:" + e.toString());
		}
	};

	/**
	 * ��ָ�����͵�Ԫ���ݶ���װ�ص����ַ���Ϊ���ı���
	 * 
	 * <p>
	 * ����������ù���ʧ��ʱ,ʹ���쳣�������������쳣,����Ȼ������������.
	 * 
	 * @param map
	 *            �������ṩ��
	 * @param type
	 *            ��Ҫװ�ص�Ԫ��������
	 * @param catcher
	 *            �쳣������,����Ϊ��
	 * @param filter
	 *            Ԫ���ݹ�����:Ϊ����װ������ָ�����͵Ķ���;����װ�ر�������accept��Ԫ����.
	 */
	public void fillDefines(Map<String, NamedDefine> map, MetaElementType type,
			ExceptionCatcher catcher, Filter<String> filter);

}
