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
 * 元数据定义容器
 * 
 * <p>
 * 用于构建临时的环境,中量级对象.
 * 
 * @author Jeff Tang
 * 
 */
public interface DefineHolder {

	/**
	 * 加入元数据定义到环境中,如果存在相同类型和名称的元数据定义,则替换.
	 * 
	 * @param type
	 *            定义类型
	 * @param reader
	 *            定义脚本的字符读取器
	 * @param pkgname
	 *            定义的包名
	 * @param clzname
	 *            定义的类名
	 * @param provider
	 *            引用提供器
	 */
	public void putDefine(MetaElementType type, Reader reader, String pkgname,
			String clzname, DefineProvider provider);

	/**
	 * 加入元数据定义到环境中,如果存在相同类型和名称的元数据定义,则替换.
	 * 
	 * @param type
	 *            定义类型
	 * @param input
	 *            定义脚本的输入流
	 * @param pkgname
	 *            定义的包名
	 * @param clzname
	 *            定义的类名
	 * @param provider
	 *            引用提供器
	 */
	public void putDefine(MetaElementType type, InputStream input,
			String pkgname, String clzname, DefineProvider provider);

	/**
	 * 加入元数据定义到环境中,如果存在相同类型和名称的元数据定义,则替换.
	 * 
	 * @param type
	 *            定义类型
	 * @param file
	 *            定义脚本的文件
	 * @param pkgname
	 *            定义的包名
	 * @param filename
	 *            定义的类名
	 * @param provider
	 *            引用提供器
	 */
	public void putDefine(MetaElementType type, File file, String pkgname,
			String filename, DefineProvider provider);

	/**
	 * 加入表定义到环境中,如果存在相同名称的表定义,则替换.
	 * 
	 * @param in
	 *            表定义的<strong>XML格式</strong>的输入流
	 * @param pkgname
	 *            表定义的包名
	 * @param clzname
	 *            表定义的类名
	 * @param provider
	 *            引用提供器
	 */
	public void putTableDefine(InputStream in, String pkgname, String clzname,
			DefineProvider provider);

	/**
	 * 加入表定义到环境中,如果存在相同名称的表定义,则替换.
	 * 
	 * @param element
	 *            表定义的XML对象
	 * @param pkgname
	 *            表定义的包名
	 * @param clzname
	 *            表定义的类名
	 * @param provider
	 *            引用提供器
	 */
	public void putTableDefine(SXElement element, String pkgname,
			String clzname, DefineProvider provider);

	/**
	 * 移除指定的元数据
	 * 
	 * @param type
	 *            元数据类型
	 * @param name
	 *            元数据名称
	 */
	public void removeDefine(MetaElementType type, String name);

	/**
	 * 从数据库的元数据表中装载元数据定义
	 * 
	 * <p>
	 * 通过此方式装载的元数据定义,其pkgname和clzname属性为空.
	 * 
	 * @param type
	 *            元数据类型
	 * @param connection
	 *            数据库连接
	 * @param provider
	 *            元数据提供器
	 * @param catcher
	 *            异常处理器
	 */
	public void loadDefine(MetaElementType type, Connection connection,
			DefineProvider provider, ExceptionCatcher catcher);

	/**
	 * 清除环境中的定义
	 */
	public void clearDefines();

	/**
	 * 查找返回指定名称的表定义
	 * 
	 * <p>
	 * 如果表关系引用建立失败,使用异常捕获器捕获其长,并仍然返回不带表关系的逻辑表.
	 * 
	 * @param name
	 * @param catcher
	 * @return
	 */
	public TableDefine findTableDefine(String name, ExceptionCatcher catcher);

	/**
	 * 获取指定名称的表定义
	 * 
	 * <p>
	 * 如果表关系引用建立失败,使用异常捕获器捕获其长,并仍然返回不带表关系的逻辑表.
	 * 
	 * @param name
	 * @param catcher
	 * @return
	 */
	public TableDefine getTableDefine(String name, ExceptionCatcher catcher);

	/**
	 * 直接抛出异常的异常捕获器
	 */
	public static final ExceptionCatcher THROWS_CATCHER = new ExceptionCatcher() {

		public void catchException(Throwable e, Object sender) {
			throw Utils.tryThrowException(e);
		}
	};

	/**
	 * 将异常通过控制台打印出的异常捕获器
	 */
	public static final ExceptionCatcher PRINTS_CATCHER = new ExceptionCatcher() {

		public void catchException(Throwable e, Object sender) {
			System.err.println((sender == null ? "" : "发生异常在:"
					+ sender.toString() + ".")
					+ "异常:" + e.toString());
		}
	};

	/**
	 * 将指定类型的元数据对象装载到以字符串为键的表中
	 * 
	 * <p>
	 * 当对象的引用关联失败时,使用异常捕获器来捕获异常,并仍然将对象放入表中.
	 * 
	 * @param map
	 *            调用者提供表
	 * @param type
	 *            需要装载的元数据类型
	 * @param catcher
	 *            异常捕获器,不能为空
	 * @param filter
	 *            元数据过滤器:为空则装载所有指定类型的对象;否则不装载被过滤器accept的元数据.
	 */
	public void fillDefines(Map<String, NamedDefine> map, MetaElementType type,
			ExceptionCatcher catcher, Filter<String> filter);

}
