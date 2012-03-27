package org.eclipse.jt.core.spi.monitor;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.invoke.Return;
import org.eclipse.jt.core.invoke.Task;
import org.eclipse.jt.core.type.GUID;


/**
 * ִ����������
 * 
 * @author Jeff Tang
 * 
 */
public class PerformanceCommandTask extends Task<PerformanceCommandTask.Method> {
	/**
	 * ���񷽷�
	 * 
	 * @author Jeff Tang
	 * 
	 */
	public enum Method {
		/**
		 * �����������
		 */
		TEST,
		/**
		 * ִ������
		 */
		EXECUTE,
	}

	/**
	 * �����Ƿ�ִ���ˣ�������ִ��
	 */
	@Return
	public boolean executed;
	/**
	 * ��������
	 */
	public final String commandName;
	/**
	 * ָ��ID
	 */
	public final GUID indexID;
	/**
	 * �ỰID
	 */
	public final long sessionID;

	public PerformanceCommandTask(GUID indexID, String commandName,
			long sessionID) {
		if (indexID == null) {
			throw new NullArgumentException("indexID");
		}
		if (commandName == null || commandName.length() == 0) {
			throw new NullArgumentException("commandName");
		}
		this.indexID = indexID;
		this.commandName = commandName;
		this.sessionID = sessionID;

	}

	public PerformanceCommandTask(GUID indexID, String commandName) {
		this(indexID, commandName, 0);
	}
}
