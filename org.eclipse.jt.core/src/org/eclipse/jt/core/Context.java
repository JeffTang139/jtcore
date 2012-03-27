package org.eclipse.jt.core;

import org.eclipse.jt.core.auth.Authority;
import org.eclipse.jt.core.auth.Operation;
import org.eclipse.jt.core.auth.Role;
import org.eclipse.jt.core.auth.RoleAuthorityChecker;
import org.eclipse.jt.core.auth.UserAuthorityChecker;
import org.eclipse.jt.core.da.DBAdapter;
import org.eclipse.jt.core.info.InfoReporter;
import org.eclipse.jt.core.resource.CategorialResourceQuerier;
import org.eclipse.jt.core.resource.ResourceQuerier;
import org.eclipse.jt.core.resource.ResourceStub;
import org.eclipse.jt.core.service.ServiceInvoker;
import org.eclipse.jt.core.type.GUID;


/**
 * ���������Ľӿڣ���������Դ��������ģ���������ģ�Ͷ�λ���ͽ������ýӿ�
 * 
 * @author Jeff Tang
 * 
 */
public interface Context extends ServiceInvoker, ResourceQuerier, DBAdapter,
		InfoReporter, Localizer {
	/**
	 * �����Ƿ���Ч
	 */
	public boolean isValid();

	/**
	 * ����Ƿ���Ч
	 */
	public void checkValid();

	/**
	 * ��������ĵ�����
	 */
	public ContextKind getKind();

	/**
	 * ��õ�ǰվ���״̬
	 */
	public SiteState getSiteState();

	/**
	 * ��������
	 */
	public <TObject> TObject newObject(Class<TObject> clazz,
			Object... aditionalArgs);

	/**
	 * ���վ���ΨһID����IDֻ��վ������ݿ��й�
	 */
	public GUID getSiteID();

	/**
	 * ���վ��ļ�ΨһID����IDֻ��վ������ݿ��й�
	 */
	public int getSiteSimpleID();

	/**
	 * �׳����쳣����<br>
	 * ��Ϊ�˱�֤�쳣ͨ������˻���Ҫ���������﷨<br>
	 * 
	 * <pre>
	 * public void hasException()throws XException{
	 *     ...
	 * }
	 * public void foo(Context context){//���ߴ������ط����context
	 *    try{
	 *        hasException();
	 *    }catch(XException e){ //���Լ򻯳ɣ�catch(Throwable e)
	 *        throw context.throwThrowable(e);
	 *    }
	 * }
	 * </pre>
	 * 
	 * @param throwable
	 *            ��Ҫ�׳����쳣����
	 */
	@Deprecated
	public RuntimeException throwThrowable(Throwable throwable);

	/**
	 * ���������½
	 */
	public Login getLogin();

	/**
	 * �л���½�û�
	 * 
	 * @param user
	 *            ���л����û�
	 * @return �����л�ǰ�ľ��û�
	 */
	public User changeLoginUser(User user);

	/**
	 * ����Ƿ��ĳ��Դӵ��ĳ��Ȩ��<br>
	 * �벻Ҫʹ�ø÷�������Ϊ������Ȩ�޵���Դ�<br>
	 * ��ʹ��getXXX(Operation<? super TFacade> operation,...)<br>
	 * ��findXXX(Operation<? super TFacade> operation,...)<br>
	 * 
	 * @param operation
	 *            ����
	 * @param resource
	 *            ��Դ
	 * @return ������Ȩ����Ϣ
	 */
	public <TFacade> Authority getAuthority(
			Operation<? super TFacade> operation, ResourceStub<TFacade> resource);

	/**
	 * ����Ƿ��ĳ��Դӵ��ĳ��Ȩ�ޣ�<br>
	 * �벻Ҫʹ�ø÷�������Ϊ������Ȩ�޵���Դ�<br>
	 * ��ʹ��getXXX(Operation<? super TFacade> operation,...)<br>
	 * ��findXXX(Operation<? super TFacade> operation,...)<br>
	 * 
	 * @param operation
	 *            ����
	 * @param resource
	 *            ��Դ
	 * @return �����Ƿ�ӵ��Ȩ��
	 */
	public <TFacade> boolean hasAuthority(Operation<? super TFacade> operation,
			ResourceStub<TFacade> resource);

	/**
	 * ���ڸ÷���ִ��Ч�ʽϵͣ�������Ƶ���������ø÷�����
	 */
	public <TFacade> Authority getAccreditAuthority(
			Operation<? super TFacade> operation, ResourceStub<TFacade> resource);

	/**
	 * ���ڸ÷���ִ��Ч�ʽϵͣ�������Ƶ���������ø÷�����
	 */
	public <TFacade> boolean hasAccreditAuthority(
			Operation<? super TFacade> operation, ResourceStub<TFacade> resource);

	/**
	 * ����һ����ָ�������в�����Դ�Ĳ�ѯ����
	 * 
	 * @param category
	 *            ָ������Դ����
	 * @return ����һ����ָ�������в�����Դ�Ĳ�ѯ����
	 */
	public CategorialResourceQuerier usingResourceCategory(Object category);

	/**
	 * ��ȡԶ��������½��Ϣ
	 * 
	 * @param host
	 *            Զ����������IP
	 * @param port
	 *            Զ�̶˿ں�
	 * @return ����Զ��������½��Ϣ
	 */
	public RemoteLoginInfo allocRemoteLoginInfo(String host, int port);

	/**
	 * ��ȡԶ�̵�½��Ϣ
	 * 
	 * @param host
	 *            Զ����������IP
	 * @param port
	 *            Զ�̶˿ں�
	 * @param user
	 *            ��½������
	 * @param password
	 *            ��½������
	 * @return ����Զ�̵�½��Ϣ
	 */
	public RemoteLoginInfo allocRemoteLoginInfo(String host, int port,
			String user, String password);

	/**
	 * ��ȡԶ�̵�½��Ϣ
	 * 
	 * @param host
	 *            Զ����������IP
	 * @param port
	 *            Զ�̶˿ں�
	 * @param user
	 *            ��½������
	 * @param password
	 *            ��½������
	 * @param life
	 *            ��½������������
	 * @return ����Զ�̵�½��Ϣ
	 */
	public RemoteLoginInfo allocRemoteLoginInfo(String host, int port,
			String user, String password, RemoteLoginLife life);

	/**
	 * ʹ��Զ�̷��������
	 * 
	 * @param remoteLoginInfo
	 *            Զ�̵�½��Ϣ
	 * @return ���ض�Ӧ��Զ�̷��������
	 */
	public ServiceInvoker usingRemoteInvoker(RemoteLoginInfo remoteLoginInfo);

	public void setUserCurrentOrg(GUID orgID);

	public GUID getUserCurrentOrg();

	public UserAuthorityChecker newUserAuthorityChecker(User user, GUID orgID,
			boolean operationAuthority);

	public RoleAuthorityChecker newRoleAuthorityChecker(Role role, GUID orgID,
			boolean operationAuthority);

	public UserAuthorityChecker newUserAuthorityChecker(GUID userID,
			GUID orgID, boolean operationAuthority);

	public RoleAuthorityChecker newRoleAuthorityChecker(GUID roleID,
			GUID orgID, boolean operationAuthority);

}
