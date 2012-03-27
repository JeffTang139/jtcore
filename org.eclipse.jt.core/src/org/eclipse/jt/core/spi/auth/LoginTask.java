package org.eclipse.jt.core.spi.auth;

import org.eclipse.jt.core.User;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.invoke.SimpleTask;

/**
 * ��½���񣬳���ʹ��ָ�����û����������½��ע����ǰ�û���<BR>
 * ʧ����Ϣ��ͨ����Ϣ���淵��<br>
 * ϵͳ��ʼ����anonym�û�������<br>
 * ʹ��anonym�û���½��Ч��ע����ǰ�û�<br>
 * 
 * ע�⣺���ڲ�ͬ��ҵ���¼���߼�Ҳ����ͬ�����ֻ���ṩ��һ����¼�����࣬��û���ṩ��Ӧ��������������Ҫ������Աʵ������������
 * 
 * <pre>
 * ʹ��ʾ����
 * task = new LoginTask(&quot;someuser&quot;, &quot;his password&quot;);
 * context.handle(task);
 * </pre>
 * 
 * @author Jeff Tang
 * 
 */
public final class LoginTask extends SimpleTask {

	/**
	 * �����û�����ϵͳ�������-Ddebugger=true�������������û��ſ���ʹ�á�<br>
	 * ���û������κ����룬Ȩ�������
	 */
	public static final String USER_NAME_DEBUGGER = User.USER_NAME_DEBUGGER;

	/**
	 * �����û���
	 */
	public static final String USER_NAME_ANONYM = User.USER_NAME_ANONYM;

	/**
	 * �û���
	 */
	public final String userName;

	/**
	 * ����
	 */
	public final String password;

	/**
	 * �Ƿ���֤����
	 */
	public final boolean validatePassword;

	/**
	 * ��¼�쳣��Ϣ
	 */
	public String exceptionMessage;

	/**
	 * �½���¼����
	 * 
	 * @param userName
	 *            ��¼�û���������Ϊ��
	 * @param password
	 *            ��¼���룬����Ϊ��
	 */
	public LoginTask(String userName, String password) {
		if (userName == null) {
			throw new NullArgumentException("userName");
		}
		if (password == null) {
			throw new NullArgumentException("password");
		}
		this.userName = userName;
		this.password = password;
		this.validatePassword = true;
	}

	/**
	 * �½���¼����<br>
	 * ���������½��
	 * 
	 * @param userName
	 *            ��¼�û���������Ϊ��
	 */
	public LoginTask(String userName) {
		if (userName == null) {
			throw new NullArgumentException("userName");
		}
		this.userName = userName;
		this.password = null;
		this.validatePassword = true;
	}

	/**
	 * �½���¼����
	 * 
	 * @param userName
	 *            ��¼�û���������Ϊ��
	 * @param password
	 *            ��¼���룬����Ϊ��
	 * @param validatePassword
	 *            �Ƿ���֤����
	 */
	public LoginTask(String userName, String password, boolean validatePassword) {
		if (userName == null) {
			throw new NullArgumentException("userName");
		}
		if (password == null) {
			throw new NullArgumentException("password");
		}
		this.userName = userName;
		this.password = password;
		this.validatePassword = validatePassword;
	}

	/**
	 * �½���¼����<br>
	 * ���������½��
	 * 
	 * @param userName
	 *            ��¼�û���������Ϊ��
	 * @param validatePassword
	 *            �Ƿ���֤����
	 */
	public LoginTask(String userName, boolean validatePassword) {
		if (userName == null) {
			throw new NullArgumentException("userName");
		}
		this.userName = userName;
		this.password = null;
		this.validatePassword = validatePassword;
	}

}
