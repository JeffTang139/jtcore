package org.eclipse.jt.core.testing;

import org.eclipse.jt.core.Context;

/**
 * 用例测试器实例<br>
 * 可以通过context.getList(CaseTesterInstance.class)来获取列表,<br>
 * 需要排序和过滤的请指定过滤和比较器
 * 
 * @author Jeff Tang
 * 
 */
public interface CaseTesterInstance {
	/**
	 * 代码
	 */
	public String getCode();

	/**
	 * 名称
	 */
	public String getName();

	/**
	 * 描述
	 */
	public String getDescription();

	/**
	 * 调用测试用例<br>
	 * 框架会准备好相关上下文然后调用CaseTester.testCase方法<br>
	 * 
	 * @param context
	 *            上下文
	 * @param testContext
	 *            测试上下文，需要测试框架实现，该方法将被直接传递给CaseTester.testCase
	 */
	public void test(Context context, TestContext testContext) throws Throwable;
}
