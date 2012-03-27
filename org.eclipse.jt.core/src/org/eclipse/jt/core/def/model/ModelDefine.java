package org.eclipse.jt.core.def.model;

import org.eclipse.jt.core.def.MetaElement;
import org.eclipse.jt.core.def.NamedElementContainer;
import org.eclipse.jt.core.def.obja.StructDefine;
import org.eclipse.jt.core.def.query.MappingQueryStatementDefine;
import org.eclipse.jt.core.misc.SXElement;

/**
 * 模型定义接口
 * 
 * @author Jeff Tang
 * 
 */
public interface ModelDefine extends MetaElement, StructDefine {
	/**
	 * 将模型保存成XML模板
	 * 
	 * @param toElement
	 *            将模型定义输出到的节点
	 */
	public void render(SXElement toElement);

	/**
	 * 获取模型实例对象的类
	 * 
	 * @return 返回模型实例对象类
	 */
	public Class<?> getMOClass();

	/**
	 * 获得字段定义列表
	 * 
	 * @return 返回字段定义列表
	 */
	public NamedElementContainer<? extends ModelFieldDefine> getFields();

	/**
	 * 获得属性定义列表
	 * 
	 * @return 返回属性定义列表
	 */
	public NamedElementContainer<? extends ModelPropertyDefine> getProperties();

	/**
	 * 获得动作定义列表
	 * 
	 * @return 返回动作定义列表
	 */
	public NamedElementContainer<? extends ModelActionDefine> getActions();

	/**
	 * 获得构造器定义列表
	 * 
	 * @return 返回构造器定义列表
	 */
	public NamedElementContainer<? extends ModelConstructorDefine> getConstructors();

	/**
	 * 获得约束定义列表
	 * 
	 * @return 返回约束定义列表
	 */
	public NamedElementContainer<? extends ModelConstraintDefine> getConstraints();

	/**
	 * 获得查询定义
	 * 
	 * @return 返回查询定义集合
	 */
	public NamedElementContainer<? extends MappingQueryStatementDefine> getQueries();

	/**
	 * 获得参考模型集合
	 * 
	 * @return 返回参考模型集合
	 */
	public NamedElementContainer<? extends ModelReferenceDefine> getReferences();

	/**
	 * 获得模型实体源
	 */
	public NamedElementContainer<? extends ModelObjSourceDefine> getSources();

	/**
	 * 获得嵌套模型的集合
	 */
	public NamedElementContainer<? extends ModelDefine> getNesteds();

	// /////////////////////////////////////////////////////////
	// /////////////////////// runtime /////////////////////////
	// /////////////////////////////////////////////////////////
	/**
	 * 创建空的模型实例对象
	 */
	public Object newMO();
}
