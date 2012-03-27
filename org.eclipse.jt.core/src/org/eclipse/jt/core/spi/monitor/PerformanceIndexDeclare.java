package org.eclipse.jt.core.spi.monitor;

import org.eclipse.jt.core.def.ModifiableNamedElementContainer;
import org.eclipse.jt.core.def.NamedDeclare;

public interface PerformanceIndexDeclare extends PerformanceIndexDefine,
		NamedDeclare {
	/**
	 * �������
	 * 
	 * @author Jeff Tang
	 * 
	 */
	public interface CommandDeclare extends CommandDefine, NamedDeclare {

	}

	/**
	 * �����Ƿ��ǻỰ������ָ�꣬����Ϊȫ������ָ��
	 */
	public void setIsUnderSession(boolean isUnderSession);

	/**
	 * ��ȡָ�����������
	 */
	public ModifiableNamedElementContainer<? extends CommandDeclare> getCommands();

	/**
	 * �����µ��������
	 * 
	 * @param name
	 *            ��������
	 */
	public CommandDeclare newCommand(String name);
}
