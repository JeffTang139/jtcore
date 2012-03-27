package org.eclipse.jt.core.def.info;

import org.eclipse.jt.core.def.MetaElement;
import org.eclipse.jt.core.def.NamedElementContainer;

/**
 * 信息组定义
 * 
 * @author Jeff Tang
 * 
 */
public interface InfoGroupDefine extends MetaElement {
	/**
	 * 得到参数容器
	 * 
	 * @return 返回参数容器
	 */
	public NamedElementContainer<? extends InfoDefine> getInfos();
}
