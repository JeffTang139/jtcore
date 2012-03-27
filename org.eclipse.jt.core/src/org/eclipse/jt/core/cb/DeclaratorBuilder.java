package org.eclipse.jt.core.cb;

import org.eclipse.jt.core.def.MetaElementType;
import org.eclipse.jt.core.impl.DeclaratorBuilderImpl;

/**
 * 声明器的代码生成器
 * 
 * @author Jeff Tang
 * 
 */
public interface DeclaratorBuilder extends DefineHolder {

	/**
	 * 代码生成器的工厂类
	 * 
	 * @author Jeff Tang
	 * 
	 */
	public interface DeclaratorBuilderFactory {

		public DeclaratorBuilder newInstance();
	}

	/**
	 * 代码生成器的静态工厂
	 */
	public static final DeclaratorBuilderFactory factory = new DeclaratorBuilderFactory() {

		public DeclaratorBuilder newInstance() {
			return DeclaratorBuilderImpl.newInstance();
		}
	};

	/**
	 * 构造声明器的代码
	 * 
	 * @param out
	 *            代码输出
	 * @param type
	 *            元数据类型
	 * @param name
	 *            目标元数据定义
	 * @param provider
	 *            元数据提供器
	 * @throws IllegalArgumentException
	 */
	public void build(Appendable out, MetaElementType type, String name,
			DefineProvider provider) throws IllegalArgumentException;

}
