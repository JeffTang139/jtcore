package org.eclipse.jt.core.serial;

import org.eclipse.jt.core.ObjectQuerier;

/**
 * 对象转化器，用于序列化和克隆数据对象，<br>
 * 当某些对象无法达到D&A序列化要求时，注册专有的对象转化器辅助序列化动作。
 * 
 * @author Jeff Tang
 * 
 */
public interface DataObjectTranslator<TSourceObject, TDelegateObject> {

	/**
	 * 获取当前自定义序列化器版本
	 */
	public short getVersion();

	/**
	 * 最小支持的序列化版本
	 */
	public short supportedVerionMin();

	/**
	 * 是否支持复制对象
	 */
	public boolean supportAssign();

	/**
	 * 获取可序列化数据对象
	 */
	public TDelegateObject toDelegateObject(TSourceObject obj);

	/**
	 * 还原并返回还原后的对象
	 */
	public TSourceObject recoverObject(TSourceObject destHint,
			TDelegateObject delegate, ObjectQuerier querier, short serialVersion);

}
