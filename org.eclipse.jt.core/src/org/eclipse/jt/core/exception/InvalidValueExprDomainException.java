package org.eclipse.jt.core.exception;

import org.eclipse.jt.core.def.exp.SelectColumnRefExpr;
import org.eclipse.jt.core.def.exp.TableFieldRefExpr;
import org.eclipse.jt.core.def.query.SubQueryExpression;

/**
 * 语句定义的表达式使用域非法
 * 
 * <p>
 * core2.5增加了语句定义的表达式使用域检查.该检查可以通过启动参数关闭.
 * 
 * <p>
 * 当语句定义抛出该异常时,假如强制忽略检查后语句可以正确执行,但仍意味着语句可能在某些场景下执行会失败,所以建议修改任何抛出该异常的语句定义.
 * 
 * <p>
 * 关闭表达式域检查的参数为:
 * 
 * <pre>
 * 	<b>-Dorg.eclipse.jt.validate-expr-domain=false</b>
 * </pre>
 * 
 * @since core2.5
 * 
 * @author Jeff Tang
 * 
 */
public class InvalidValueExprDomainException extends CoreException {

	private static final long serialVersionUID = -1720255180362049350L;

	public InvalidValueExprDomainException(SelectColumnRefExpr columnRef) {
		super("查询列引用[" + columnRef.toString() + "]的使用域错误.");
	}

	public InvalidValueExprDomainException(TableFieldRefExpr fieldRef) {
		super("字段引用[" + fieldRef.toString() + "]的使用域错误.");
	}

	public InvalidValueExprDomainException(SubQueryExpression expr) {
		super("子查询的使用域不是其构造域.");
	}

}
