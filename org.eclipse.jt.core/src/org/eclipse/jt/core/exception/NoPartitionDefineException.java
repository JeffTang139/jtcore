package org.eclipse.jt.core.exception;

import org.eclipse.jt.core.def.table.TableDefine;

/**
 * δ���������쳣
 * 
 * <p>
 * δ����������ȴ�����˱��������ط���ʱ�׳����쳣
 * 
 * @author Jeff Tang
 * 
 */
public class NoPartitionDefineException extends CoreException {

	private static final long serialVersionUID = 2962995469146334871L;

	public final TableDefine table;

	public NoPartitionDefineException(TableDefine table) {
		super("�߼���[" + table.getName() + "]û�б��������.");
		this.table = table;
	}
}
