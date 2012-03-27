package org.eclipse.jt.core.model;

import org.eclipse.jt.core.def.Container;
import org.eclipse.jt.core.def.model.ModelDefine;

/**
 * ģ�ͷ����������ڽ����¼�����֯����ģ�͵ȡ�
 * 
 * @author Jeff Tang
 * 
 */
public interface ModelMonitor {
	/**
	 * ���ģ�Ͷ���
	 */
	public ModelDefine getModelDefine();

	/**
	 * ��ô�ģ�Ͷ���
	 */
	public Container<ModelMonitor> getSubMonitor();

}
