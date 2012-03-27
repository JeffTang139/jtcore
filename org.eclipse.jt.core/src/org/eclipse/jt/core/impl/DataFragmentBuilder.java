package org.eclipse.jt.core.impl;

/**
 * 数据片断构造器
 * 
 * @author Jeff Tang
 * 
 * @param <TAttachment>附件类型
 */
public interface DataFragmentBuilder<TAttachment> {
	/**
	 * 构造或远程还原失败导致终止
	 */
	public void onFragmentOutError(TAttachment attachment);

	/**
	 * 由于网络的异常导致需要重置（重新开始传输）
	 * 
	 * @param attachment
	 *            附件
	 * @return 返回是否支持和已经重置，如果返回true则认为需要重新启动构造
	 */
	public boolean tryResetPackage(TAttachment attachment);

	public void onFragmentOutFinished(TAttachment attachment);

	/**
	 * 
	 * 处理fragment，<br>
	 * 
	 * @param fragment
	 *            数据片断，作为保存构造结果的buffer
	 * @param attachment
	 *            附件
	 * @return 返回true表示构造完成，返回false表示还需要后续的fragment
	 * @throws Throwable
	 *             可抛出异常
	 */
	public boolean buildFragment(DataOutputFragment fragment,
			TAttachment attachment) throws Throwable;

}
