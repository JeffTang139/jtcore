package org.eclipse.jt.core.def.model;

import java.util.List;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.def.NamedDefine;
import org.eclipse.jt.core.def.apc.CheckPointDefine;
import org.eclipse.jt.core.def.arg.ArgumentableDefine;
import org.eclipse.jt.core.def.query.MappingQueryStatementDefine;
import org.eclipse.jt.core.misc.ObjectBuilder;


/**
 * 模型实体源定义，用以返回模型实体列表的定义
 * 
 * @author Jeff Tang
 * 
 */
public interface ModelObjSourceDefine extends NamedDefine, ArgumentableDefine,
        CheckPointDefine {
	/**
	 * 获得字段定义属于的模型定义
	 * 
	 * @return 返回模型定义
	 */
	public ModelDefine getOwner();

	/**
	 * 模型实体源的查询定义
	 */
	public MappingQueryStatementDefine getMappingQueryRef();

	/**
	 * 模型实体源的脚本
	 * 
	 * @return 返回脚本定义对象
	 */
	public ScriptDefine getScript();

	/**
	 * 获得取实体个数的脚本
	 */
	public ScriptDefine getMOCountOfScript();

	// /////////////////////////////////////////////////////////
	// /////////////////////// runtime /////////////////////////
	// /////////////////////////////////////////////////////////
	/**
	 * 填充模型实力对象列表
	 * 
	 * @param context
	 *            上下文
	 * @param ao
	 *            参数对象
	 * @param mos
	 *            等待填充列表
	 * @param offset
	 *            偏移,用于分页,从0开始,
	 * @param count
	 *            用于限制返回的个数0表示没有限制
	 * @return 返回实例列表
	 */
	public <TMO> void fetchMOs(Context context, Object ao, List<TMO> mos,
	        int offset, int count);

	public <TMO> void fetchMOs(Context context, Object ao, List<TMO> mos,
	        int offset, int count, ObjectBuilder<TMO> moFactory);

	/**
	 * 返回总记录数，<0 表示不支持分页
	 * 
	 * @param context
	 *            上下文
	 * @param ao
	 *            参数对象
	 * @return 返回总记录数，<0 表示不支持分页
	 */
	public int moCountOf(Context context, Object ao);

	/**
	 * 无参填充模型实力对象列表
	 * 
	 * @param context
	 *            上下文
	 * @param mos
	 *            等待填充列表
	 * @param offset
	 *            偏移,用于分页,从0开始,
	 * @param count
	 *            用于限制返回的个数，<0 表示没有限制
	 * @return 返回实例列表
	 */
	public <TMO> void fetchMOs(Context context, List<TMO> mos, int offset,
	        int count);

	public <TMO> void fetchMOs(Context context, List<TMO> mos, int offset,
	        int count, ObjectBuilder<TMO> moFactory);

	/**
	 * 返回总记录数，<0 表示不支持分页
	 * 
	 * @param context
	 *            上下文
	 * @return 返回总记录数，<0 表示不支持分页
	 */
	public int moCountOf(Context context);
}
