package org.eclipse.jt.core.spi.monitor;

import org.eclipse.jt.core.def.NamedDefine;
import org.eclipse.jt.core.def.NamedElementContainer;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.GUID;


/**
 * 性能监控指标<br>
 * 使用举例: 获得全部有效指标。
 * 
 * <pre>
 * context.getList(PerformanceIndexDefine.class);
 * </pre>
 * 
 * @author Jeff Tang
 * 
 */
public interface PerformanceIndexDefine extends NamedDefine {
	/**
	 * 获得ID
	 */
	public GUID getID();

	/**
	 * 获取对应的值类型<br>
	 * 目前仅支持：
	 * <ul>
	 * <li>TypeFactory.BOOLEAN
	 * <li>TypeFactory.LONG
	 * <li>TypeFactory.DOUBLE
	 * <li>各种对象类型
	 * </ul>
	 * 
	 */
	public DataType getDataType();

	/**
	 * 指标的值是否是序列
	 */
	public boolean isSequence();

	/**
	 * 是否是会话级性能指标，否则为全局性能指标
	 */
	public boolean isUnderSession();

	/**
	 * 监控命令
	 * 
	 */
	public interface CommandDefine extends NamedDefine {

	}

	/**
	 * 获取指标包含的命令
	 */
	public NamedElementContainer<? extends CommandDefine> getCommands();
}
