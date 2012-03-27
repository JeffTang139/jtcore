package org.eclipse.jt.core.def;

/**
 * 声明器模板的参数接口
 * 
 * @author Jeff Tang
 * 
 */
public interface MetaElementTemplateParams {
	/**
	 * 实例的名称
	 */
	public String getName();

	/**
	 * 获得实例所需的参数
	 */
	public <TParam> TParam getParam(Class<TParam> paramClass);

	/**
	 * 获得实例所需的参数
	 */
	public <TParam> TParam getParam(Class<TParam> paramClass, int tag);

}
