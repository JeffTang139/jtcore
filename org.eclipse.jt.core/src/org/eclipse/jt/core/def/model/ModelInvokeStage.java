package org.eclipse.jt.core.def.model;

/**
 * ģ�͵ĵ��ý׶�
 * 
 * @author Jeff Tang
 * 
 */
public enum ModelInvokeStage {
	/**
	 * ĳ����ǰ
	 */
	BEFORE,
	/**
	 * ��ǰ���ã����������������ô�����
	 */
	DOING,
	/**
	 * ĳ���ú󣬵����г����쳣�����
	 */
	AFTER,
	/**
	 * �ı��ֻ���������Ч
	 */
	CHANGED,
	/**
	 * ���գ���ʹ�쳣Ҳ����
	 */
	FINALLY,
}
