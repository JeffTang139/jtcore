package org.eclipse.jt.core.info;

import org.eclipse.jt.core.LifeHandle;
import org.eclipse.jt.core.def.info.ErrorInfoDefine;
import org.eclipse.jt.core.def.info.HintInfoDefine;
import org.eclipse.jt.core.def.info.ProcessInfoDefine;
import org.eclipse.jt.core.def.info.WarningInfoDefine;
import org.eclipse.jt.core.exception.AbortException;

/**
 * 信息报告接口
 * 
 * @author Jeff Tang
 * 
 */
public interface InfoReporter extends LifeHandle {
	/**
	 * 应用上一部的步长，并设置下一步的步长,<br>
	 * 必须是0f到getRestPartialProgress()之间的数，<br>
	 * 如果超过了getRestPartialProgress()，则按getRestPartialProgress()设置步长<br>
	 * 
	 * 
	 * @param step
	 *            下一步的步长
	 */
	public float setNextStep(float nextStep);

	/**
	 * 获得下一步的步长
	 */
	public float getNextStep();

	/**
	 * 应用上一部的步长，并设置下一步之后局部进度所要抵达的位置,<br>
	 * 必须是getPartialProgress()+getNextStep()到1f之间的数，<br>
	 * 如果小于getPartialProgress()+getNextStep()则进度进到getPartialProgress()+
	 * getNextStep()，<br>
	 * 如果大于1f，则进度为1f<br>
	 * 
	 * @param nextProgress
	 *            下一步后达到的局部进度
	 */
	public float setNextPartialProgress(float nextProgress);

	/**
	 * 获得当前处理剩余的进度
	 */
	public float getRestPartialProgress();

	/**
	 * 获得当前处理的进度
	 * 
	 * @return 返回当前上下文处理的进度
	 */
	public float getPartialProgress();

	/**
	 * 并设置局部进度<br>
	 * 必须是getPartialProgress()+getNextStep()到1f之间的数，<br>
	 * 如果小于getPartialProgress()+getNextStep()则进度进到getPartialProgress()+
	 * getNextStep()，<br>
	 * 如果大于1f，则进度为1f<br>
	 * 
	 * @param progress
	 *            达到的局部进度
	 */
	public float setPartialProgress(float progress);

	/**
	 * 获得全部请求的总进度
	 * 
	 * @return 返回当前请求处理的总进度
	 */
	public float getTotalProgress();

	/**
	 * 获得当前处理所占整个整个请求处理的比例。
	 * 
	 * @return 当前处理所占整个整个请求处理的比例。
	 */
	public float getPartialProgressQuotiety();

	/**
	 * 检查是否被外界取消
	 */
	public boolean isCanceling();

	/**
	 * 检查是否被取消，如果终止了则抛出InterruptedException异常
	 */
	public void throwIfCanceling();

	/**
	 * 终止当前操作，将引发事务会滚
	 */
	public void abort() throws AbortException;

	/**
	 * 报告提示信息
	 */
	public void reportHint(HintInfoDefine infoDefine);

	/**
	 * 报告提示信息
	 */
	public void reportHint(HintInfoDefine infoDefine, Object param1);

	/**
	 * 报告提示信息
	 */
	public void reportHint(HintInfoDefine infoDefine, Object param1,
	        Object param2);

	/**
	 * 报告提示信息
	 */
	public void reportHint(HintInfoDefine infoDefine, Object param1,
	        Object param2, Object param3);

	/**
	 * 报告提示信息
	 */
	public void reportHint(HintInfoDefine info, Object param1, Object param2,
	        Object param3, Object... others);

	/**
	 * 报告错误信息
	 */
	public void reportError(ErrorInfoDefine infoDefine);

	/**
	 * 报告错误信息
	 */
	public void reportError(ErrorInfoDefine infoDefine, Object param1);

	/**
	 * 报告错误信息
	 */
	public void reportError(ErrorInfoDefine infoDefine, Object param1,
	        Object param2);

	/**
	 * 报告错误信息
	 */
	public void reportError(ErrorInfoDefine infoDefine, Object param1,
	        Object param2, Object param3);

	/**
	 * 报告错误信息
	 */
	public void reportError(ErrorInfoDefine info, Object param1, Object param2,
	        Object param3, Object... others);

	/**
	 * 报告警告信息
	 */
	public void reportWarning(WarningInfoDefine infoDefine);

	/**
	 * 报告警告信息
	 */
	public void reportWarning(WarningInfoDefine infoDefine, Object param1);

	/**
	 * 报告警告信息
	 */
	public void reportWarning(WarningInfoDefine infoDefine, Object param1,
	        Object param2);

	/**
	 * 报告警告信息
	 */
	public void reportWarning(WarningInfoDefine infoDefine, Object param1,
	        Object param2, Object param3);

	/**
	 * 报告Done信息
	 */
	public void reportWarning(WarningInfoDefine info, Object param1,
	        Object param2, Object param3, Object... others);

	/**
	 * 始某个过程，随后的信息报告都从属于该过程。<br>
	 * 过程的开始必须与结束配对使用即:
	 * 
	 * <pre>
	 * context.beginProcess(...);
	 * try{
	 *      XXXXX
	 * }finally{
	 *     context.endProcess();
	 * }
	 * </pre>
	 */
	public void beginProcess(ProcessInfoDefine infoDefine);

	/**
	 * 始某个过程，随后的信息报告都从属于该过程。<br>
	 * 过程的开始必须与结束配对使用即:
	 * 
	 * <pre>
	 * context.beginProcess(...);
	 * try{
	 *      XXXXX
	 * }finally{
	 *     context.endProcess();
	 * }
	 * </pre>
	 */
	public void beginProcess(ProcessInfoDefine infoDefine, Object param1);

	/**
	 * 始某个过程，随后的信息报告都从属于该过程。<br>
	 * 过程的开始必须与结束配对使用即:
	 * 
	 * <pre>
	 * context.beginProcess(...);
	 * try{
	 *      XXXXX
	 * }finally{
	 *     context.endProcess();
	 * }
	 * </pre>
	 */
	public void beginProcess(ProcessInfoDefine infoDefine, Object param1,
	        Object param2);

	/**
	 * 始某个过程，随后的信息报告都从属于该过程。<br>
	 * 过程的开始必须与结束配对使用即:
	 * 
	 * <pre>
	 * context.beginProcess(...);
	 * try{
	 *      XXXXX
	 * }finally{
	 *     context.endProcess();
	 * }
	 * </pre>
	 */
	public void beginProcess(ProcessInfoDefine infoDefine, Object param1,
	        Object param2, Object param3);

	/**
	 * 始某个过程，随后的信息报告都从属于该过程。<br>
	 * 过程的开始必须与结束配对使用即:
	 * 
	 * <pre>
	 * context.beginProcess(...);
	 * try{
	 *      XXXXX
	 * }finally{
	 *     context.endProcess();
	 * }
	 * </pre>
	 */
	public void beginProcess(ProcessInfoDefine infoDefine, Object param1,
	        Object param2, Object param3, Object... others);

	/**
	 * 完成报告信息<br>
	 * 过程的开始必须与结束配对使用即:
	 * 
	 * <pre>
	 * context.beginProcess(...);
	 * try{
	 *      XXXXX
	 * }finally{
	 *     context.endProcess();
	 * }
	 * </pre>
	 */
	public void endProcess();
}
