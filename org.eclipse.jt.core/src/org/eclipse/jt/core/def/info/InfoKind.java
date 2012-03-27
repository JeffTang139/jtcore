package org.eclipse.jt.core.def.info;

/**
 * ��Ϣ����
 * 
 * @author Jeff Tang
 * 
 */
public enum InfoKind {
	/**
	 * ��ʾ��Ϣ<br>
	 * ����ҵ�����м���̵�һЩ��ʾ��Ϣ<br>
	 * <li>��Ϣ���жϺ�������</li><br>
	 */
	HINT(true),
	/**
	 * ������Ϣ<br>
	 * ��������ĬĬ��¼ĳЩ���̵���Ϣ<br>
	 * <li>��Ϣ���жϺ�������</li><br>
	 */
	WARNING(true),
	/**
	 * ������Ϣ<br>
	 * ������������ͻ����������Ч�����ݺͷǷ��Ĳ���ʱ���õ���Ϣ���档<br>
	 * �����ʧ�ܣ������ص�����ԭ�򣬴Ӷ������û��ı���Ϊ�����ݡ�<br>
	 * <li>��Ϣ���жϺ�������</li><br>
	 */
	ERROR(true),
	/**
	 * ������Ϣ<br>
	 * ������Ҫ��¼����ϸ�ڵĸ�����Ϣ���棬���ù������͡�<br>
	 * ����������Ϣ������ʱ���ͱ��棬�ڱ��濪ʼ�ͽ���֮�����Ϣ���涼�Ǳ�������ӱ���<br>
	 * ���̱��汾��ֻ���Թ��̵������Լ�ʱ�䣬���̵ĳɰ���Ƕ���ڹ�����Ϣ�е�ʱ�㱨�����<br>
	 * <li>��Ϣ���жϺ�����������Ƕ�׵���Ϣ�������ж�</li><br>
	 */
	PROCESS(false);
	/**
	 * ָ��������Ϣ�Ƿ�Ĭ�ϱ�����û�<br>
	 * 
	 * @see InfoDeclare.setNeedToUser()
	 */
	public final boolean defaultReportToUser;

	private InfoKind(boolean defaultReportToUser) {
		this.defaultReportToUser = defaultReportToUser;
	}
}
