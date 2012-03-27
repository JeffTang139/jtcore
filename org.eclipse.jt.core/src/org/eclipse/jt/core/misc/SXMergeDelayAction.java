package org.eclipse.jt.core.misc;


/**
 * 延迟合并动作接口
 * 
 * @author Jeff Tang
 * 
 * @param <TAt>
 *            延迟所在位置对象
 */
public interface SXMergeDelayAction<TAt> {
	
	public abstract void doAction(TAt at, SXMergeHelper helper,
	        SXElement atElement);
}
