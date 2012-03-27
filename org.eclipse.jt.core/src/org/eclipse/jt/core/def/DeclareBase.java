package org.eclipse.jt.core.def;

/**
 * 可修改定义的基接口
 * 
 * @author Jeff Tang
 * 
 */
public interface DeclareBase extends DefineBase {

	/**
	 * 设置描述
	 * 
	 * @param description
	 *            描述文本
	 */
	public void setDescription(String description);
}
