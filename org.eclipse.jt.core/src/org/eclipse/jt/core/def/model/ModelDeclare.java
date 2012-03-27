package org.eclipse.jt.core.def.model;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.def.FieldDefine;
import org.eclipse.jt.core.def.ModifiableNamedElementContainer;
import org.eclipse.jt.core.def.NamedElementContainer;
import org.eclipse.jt.core.def.obja.StructDeclare;
import org.eclipse.jt.core.def.query.MappingQueryStatementDeclare;
import org.eclipse.jt.core.impl.ModelDefineImpl;
import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.type.DataTypable;
import org.eclipse.jt.core.type.DataType;

/**
 * 模型定义接口
 * 
 * @author Jeff Tang
 * 
 */
public interface ModelDeclare extends ModelDefine, StructDeclare {
	public interface Helper {
		/**
		 * 新建空临时模型定义（系统不负责持久化与注册）
		 */
		public ModelDeclare newTempModelDeclare(String author, String name,
				Class<?> moClass);

		/**
		 * 根据模板新建临时模型定义（系统不负责持久化与注册）
		 * 
		 * @param template
		 *            XML模板，ModelDefine.Helper
		 * @param context
		 *            上下文对象
		 */
		public ModelDeclare newTempModelDeclare(SXElement template,
				Context context);

		public void ensurePrepared(ModelDefine model, Context context);
	}

	// TODO 完成
	public final static Helper helper = new ModelDefineImpl.HelperImpl();

	/**
	 * 获得字段定义列表
	 * 
	 * @return 返回字段定义列表
	 */
	public ModifiableNamedElementContainer<? extends ModelFieldDeclare> getFields();

	public ModelFieldDeclare newField(String name, DataType type);

	public ModelFieldDeclare newField(FieldDefine sample);

	public ModelFieldDeclare newField(String name, DataTypable typable);

	/**
	 * 获得属性定义列表
	 * 
	 * @return 返回属性定义列表
	 */
	public ModifiableNamedElementContainer<? extends ModelPropertyDeclare> getProperties();

	public ModelPropertyDeclare newProperty(String name, DataType type);

	public ModelPropertyDeclare newProperty(String name, DataTypable typable);

	public ModelPropertyDeclare newProperty(ModelFieldDefine refField);

	/**
	 * 获得动作定义列表
	 * 
	 * @return 返回动作定义列表
	 */
	public ModifiableNamedElementContainer<? extends ModelActionDeclare> getActions();

	public ModelActionDeclare newAction(String name, Class<?> aoClass);

	public ModelActionDeclare newAction(String name);

	/**
	 * 获得构造器定义列表
	 * 
	 * @return 返回构造器定义列表
	 */
	public ModifiableNamedElementContainer<? extends ModelConstructorDeclare> getConstructors();

	public ModelConstructorDeclare newConstructor(String name, Class<?> aoClass);

	public ModelConstructorDeclare newConstructor(String name);

	/**
	 * 获得约束定义列表
	 * 
	 * @return 返回约束定义列表
	 */
	public ModifiableNamedElementContainer<? extends ModelConstraintDeclare> getConstraints();

	/**
	 * 新建错误约束
	 * 
	 * @param name
	 *            约束名
	 * @param messageFormat
	 *            消息格式化文本
	 */
	public ModelConstraintDeclare newConstraint(String name,
			String messageFormat);

	/**
	 * 获得查询定义
	 * 
	 * @return 返回查询定义集合
	 */
	public ModifiableNamedElementContainer<? extends MappingQueryStatementDeclare> getQueries();

	public MappingQueryStatementDeclare newQuery(String name);

	/**
	 * 获得模型实体源
	 */
	public NamedElementContainer<? extends ModelObjSourceDeclare> getSources();

	public ModelObjSourceDeclare newSource(String name);

	public ModelObjSourceDeclare newSource(String name, Class<?> aoClass);

	/**
	 * 获得查询定义
	 * 
	 * @return 返回查询定义集合
	 */
	public ModifiableNamedElementContainer<? extends ModelReferenceDeclare> getReferences();

	public ModelReferenceDeclare newReference(String name, ModelDefine target);

	public ModifiableNamedElementContainer<? extends ModelDeclare> getNesteds();

	public ModelDeclare newNested(String name, Class<?> moClass);

}
