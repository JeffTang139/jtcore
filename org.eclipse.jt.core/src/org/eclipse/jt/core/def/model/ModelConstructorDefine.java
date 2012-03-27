package org.eclipse.jt.core.def.model;

import org.eclipse.jt.core.Context;

/**
 * 模型构造器定义接口
 * 
 * @author Jeff Tang
 * 
 */
public interface ModelConstructorDefine extends ModelInvokeDefine {
	/**
	 * 构造器的脚本
	 * 
	 * @return 返回脚本定义对象
	 */
	public ScriptDefine getScript();

	// /////////////////////////////////////////////////////////
	// /////////////////////// runtime /////////////////////////
	// /////////////////////////////////////////////////////////

	/**
	 * 构造模型实例对象
	 * 
	 * @param context
	 *            上下文对象
	 * @param ao
	 *            参数
	 * @return 返回构造好的模型实例对象
	 */
	public Object newMO(Context context, Object ao);

	/**
	 * 无参构造模型实例对象
	 * 
	 * @param constructor
	 *            模型构造器
	 */
	public Object newMO(Context context);

}
