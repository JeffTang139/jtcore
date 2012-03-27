package org.eclipse.jt.core.spi.def;

/**
 * 定义类型 <br>
 * Context 相关的调用:<br>
 * 《模型定义》:<br>
 * 获取模型定义的设计期列表:<br>
 * <code>contex.getList(ModelDefine.class,DefineKind.DESIGN);</code><br>
 * 通过作者和名字获得模型定义的可编辑副本: <br>
 * <code>contex.get(ModelDeclare.class,"author","name");</code><br>
 * 创建新模型时使用 <br>
 * <code>contex.get(ModelDeclare.class,"author","name",moClass);</code> <br>
 * 通过作者和名字获得模型定义的运行时: <br>
 * <code>contex.get(ModelDefine.class,"author","name");</code> <br>
 * <code>contex.get(ModelDefine.class,"name");</code> <br>
 * 《表定义》:<br>
 * 通过作者和名字获得表定义的可编辑副本，如果没有则创建新的定义: <br>
 * <code>contex.get(TableDeclare.class,"name");</code><br>
 * 通过作者和名字获得表定义的运行时: <br>
 * <code>contex.get(TableDefine.class,"name");</code><br>
 * <code>contex.get(TableDefine.class,"name");</code><br>
 *
 * @author Jeff Tang
 *
 */
public enum DefineKind {
	/**
	 * 运行期定义
	 */
	RUNTIME,
	/**
	 * 设计期定义
	 */
	DESIGN

}
