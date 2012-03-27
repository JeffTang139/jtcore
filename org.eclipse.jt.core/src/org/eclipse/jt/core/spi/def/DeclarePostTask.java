package org.eclipse.jt.core.spi.def;

import org.eclipse.jt.core.def.NamedDeclare;
import org.eclipse.jt.core.invoke.Return;
import org.eclipse.jt.core.invoke.SimpleTask;

/**
 * 模型定义提交任务<br>
 * <code>context.handle(new DeclarePostTask(yourDeclare)) </code>
 * 
 * @author Jeff Tang
 * 
 */
public final class DeclarePostTask extends SimpleTask {
	/**
	 * 待提交的定义<br>
	 * 任务结束后会返回，会有相关的属性发生影响，可以继续使用。
	 */
	@Return
	public final NamedDeclare designed;
	/**
	 * 提示系统是否在提交后即影响运行时，<br>
	 * 即使设为true,系统也会根据情况考虑是否影响运行时<br>
	 * 任务结束后该属性会返回，true代表影响了运行时，false代表没有影响运行时
	 */
	@Return
	public boolean applyToRuntime;

	public DeclarePostTask(NamedDeclare designed) {
		this.designed = designed;
	}

	public DeclarePostTask(NamedDeclare designed, boolean applyToRuntime) {
		this(designed);
		this.applyToRuntime = applyToRuntime;
	}
}
