package org.eclipse.jt.core.info;

import org.eclipse.jt.core.LifeHandle;
import org.eclipse.jt.core.def.info.ErrorInfoDefine;
import org.eclipse.jt.core.def.info.HintInfoDefine;
import org.eclipse.jt.core.def.info.ProcessInfoDefine;
import org.eclipse.jt.core.def.info.WarningInfoDefine;
import org.eclipse.jt.core.exception.AbortException;

/**
 * ��Ϣ����ӿ�
 * 
 * @author Jeff Tang
 * 
 */
public interface InfoReporter extends LifeHandle {
	/**
	 * Ӧ����һ���Ĳ�������������һ���Ĳ���,<br>
	 * ������0f��getRestPartialProgress()֮�������<br>
	 * ���������getRestPartialProgress()����getRestPartialProgress()���ò���<br>
	 * 
	 * 
	 * @param step
	 *            ��һ���Ĳ���
	 */
	public float setNextStep(float nextStep);

	/**
	 * �����һ���Ĳ���
	 */
	public float getNextStep();

	/**
	 * Ӧ����һ���Ĳ�������������һ��֮��ֲ�������Ҫ�ִ��λ��,<br>
	 * ������getPartialProgress()+getNextStep()��1f֮�������<br>
	 * ���С��getPartialProgress()+getNextStep()����Ƚ���getPartialProgress()+
	 * getNextStep()��<br>
	 * �������1f�������Ϊ1f<br>
	 * 
	 * @param nextProgress
	 *            ��һ����ﵽ�ľֲ�����
	 */
	public float setNextPartialProgress(float nextProgress);

	/**
	 * ��õ�ǰ����ʣ��Ľ���
	 */
	public float getRestPartialProgress();

	/**
	 * ��õ�ǰ����Ľ���
	 * 
	 * @return ���ص�ǰ�����Ĵ���Ľ���
	 */
	public float getPartialProgress();

	/**
	 * �����þֲ�����<br>
	 * ������getPartialProgress()+getNextStep()��1f֮�������<br>
	 * ���С��getPartialProgress()+getNextStep()����Ƚ���getPartialProgress()+
	 * getNextStep()��<br>
	 * �������1f�������Ϊ1f<br>
	 * 
	 * @param progress
	 *            �ﵽ�ľֲ�����
	 */
	public float setPartialProgress(float progress);

	/**
	 * ���ȫ��������ܽ���
	 * 
	 * @return ���ص�ǰ��������ܽ���
	 */
	public float getTotalProgress();

	/**
	 * ��õ�ǰ������ռ��������������ı�����
	 * 
	 * @return ��ǰ������ռ��������������ı�����
	 */
	public float getPartialProgressQuotiety();

	/**
	 * ����Ƿ����ȡ��
	 */
	public boolean isCanceling();

	/**
	 * ����Ƿ�ȡ���������ֹ�����׳�InterruptedException�쳣
	 */
	public void throwIfCanceling();

	/**
	 * ��ֹ��ǰ������������������
	 */
	public void abort() throws AbortException;

	/**
	 * ������ʾ��Ϣ
	 */
	public void reportHint(HintInfoDefine infoDefine);

	/**
	 * ������ʾ��Ϣ
	 */
	public void reportHint(HintInfoDefine infoDefine, Object param1);

	/**
	 * ������ʾ��Ϣ
	 */
	public void reportHint(HintInfoDefine infoDefine, Object param1,
	        Object param2);

	/**
	 * ������ʾ��Ϣ
	 */
	public void reportHint(HintInfoDefine infoDefine, Object param1,
	        Object param2, Object param3);

	/**
	 * ������ʾ��Ϣ
	 */
	public void reportHint(HintInfoDefine info, Object param1, Object param2,
	        Object param3, Object... others);

	/**
	 * ���������Ϣ
	 */
	public void reportError(ErrorInfoDefine infoDefine);

	/**
	 * ���������Ϣ
	 */
	public void reportError(ErrorInfoDefine infoDefine, Object param1);

	/**
	 * ���������Ϣ
	 */
	public void reportError(ErrorInfoDefine infoDefine, Object param1,
	        Object param2);

	/**
	 * ���������Ϣ
	 */
	public void reportError(ErrorInfoDefine infoDefine, Object param1,
	        Object param2, Object param3);

	/**
	 * ���������Ϣ
	 */
	public void reportError(ErrorInfoDefine info, Object param1, Object param2,
	        Object param3, Object... others);

	/**
	 * ���澯����Ϣ
	 */
	public void reportWarning(WarningInfoDefine infoDefine);

	/**
	 * ���澯����Ϣ
	 */
	public void reportWarning(WarningInfoDefine infoDefine, Object param1);

	/**
	 * ���澯����Ϣ
	 */
	public void reportWarning(WarningInfoDefine infoDefine, Object param1,
	        Object param2);

	/**
	 * ���澯����Ϣ
	 */
	public void reportWarning(WarningInfoDefine infoDefine, Object param1,
	        Object param2, Object param3);

	/**
	 * ����Done��Ϣ
	 */
	public void reportWarning(WarningInfoDefine info, Object param1,
	        Object param2, Object param3, Object... others);

	/**
	 * ʼĳ�����̣�������Ϣ���涼�����ڸù��̡�<br>
	 * ���̵Ŀ�ʼ������������ʹ�ü�:
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
	 * ʼĳ�����̣�������Ϣ���涼�����ڸù��̡�<br>
	 * ���̵Ŀ�ʼ������������ʹ�ü�:
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
	 * ʼĳ�����̣�������Ϣ���涼�����ڸù��̡�<br>
	 * ���̵Ŀ�ʼ������������ʹ�ü�:
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
	 * ʼĳ�����̣�������Ϣ���涼�����ڸù��̡�<br>
	 * ���̵Ŀ�ʼ������������ʹ�ü�:
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
	 * ʼĳ�����̣�������Ϣ���涼�����ڸù��̡�<br>
	 * ���̵Ŀ�ʼ������������ʹ�ü�:
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
	 * ��ɱ�����Ϣ<br>
	 * ���̵Ŀ�ʼ������������ʹ�ü�:
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
