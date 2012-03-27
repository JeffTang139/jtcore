package org.eclipse.jt.core.exception;

public class SubselectOrderbyNotSupportedException extends CoreException {

	private static final long serialVersionUID = -5546304362768363490L;

	public SubselectOrderbyNotSupportedException() {
		super("不支持在子查询结构中使用Orderby子句.");
	}

}
