package org.eclipse.jt.core.misc;


/**
 * �ӳٺϲ������ӿ�
 * 
 * @author Jeff Tang
 * 
 * @param <TAt>
 *            �ӳ�����λ�ö���
 */
public interface SXMergeDelayAction<TAt> {
	
	public abstract void doAction(TAt at, SXMergeHelper helper,
	        SXElement atElement);
}
