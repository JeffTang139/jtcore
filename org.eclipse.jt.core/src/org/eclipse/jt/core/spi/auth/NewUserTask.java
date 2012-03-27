package org.eclipse.jt.core.spi.auth;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jt.core.type.GUID;


/**
 * �½��û�����
 * 
 * <pre>
 * ʹ��ʾ����
 * task = new NewUserTask(userID, userName);
 * task.title = &quot;user title&quot;;
 * task.state = ActorState.DISABLE;
 * task.description = &quot;description string&quot;
 * task.password = &quot;password&quot;;
 * context.handle(task);
 * </pre>
 * 
 * @see org.eclipse.jt.core.spi.auth.NewActorTask
 * @author Jeff Tang 2009-11
 */
public final class NewUserTask extends NewActorTask {

	/**
	 * �û����룬Ϊ��ʱĬ��Ϊ���ַ�������
	 */
	public String password;

	public boolean passwordNeedEncrypt;

	/**
	 * ���ȼ������ţ�δָ��ʱĬ��Ϊ0<br>
	 * ���û��̳��˶����ɫ��ʱ��Ϊ����û��͸���ɫ֮����ܴ��ڵ���Ȩ��ͻ���⣬��Ҫָ�� �û��ͽ�ɫ��Ȩ����֤ʱ�����ȼ�˳�򣬸�ֵ�û������ȼ������š�
	 */
	public int priorityIndex;

	/**
	 * Ϊ�û�����Ľ�ɫID�б�<br>
	 * Խ�ȼ�������ȼ�Խ�ߡ�
	 */
	public final List<GUID> assignRoleIDList = new ArrayList<GUID>();

	/**
	 * �����½��û�����
	 * 
	 * @param id
	 *            �û�ID������Ϊ��
	 * @param name
	 *            �û����ƣ�����Ϊ��
	 */
	public NewUserTask(GUID id, String name) {
		super(id, name);
		this.passwordNeedEncrypt = true;
	}

}
