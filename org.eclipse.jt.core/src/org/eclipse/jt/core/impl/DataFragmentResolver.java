package org.eclipse.jt.core.impl;

/**
 * BufferIO处理器
 * 
 * @author Jeff Tang
 * 
 * @param <TAttachment>附件类型
 */
public interface DataFragmentResolver<TAttachment> {
	/**
	 * 失败时调用
	 * 
	 * @param exception
	 *            异常
	 * @param attachment
	 *            附件
	 * @throws Throwable
	 *             可抛出异常
	 */
	public void onFragmentInFailed(TAttachment attachment) throws Throwable;

	/**
	 * 
	 * 处理fragment，<br>
	 * 
	 * @param fragment
	 *            待处理的片断
	 * @param attachment
	 *            附件
	 * @return 返回true表示处理完成，返回false表示还需要后续的处理
	 * @throws Throwable
	 *             可抛出异常
	 */
	public boolean resovleFragment(DataInputFragment fragment,
			TAttachment attachment) throws Throwable;
}
