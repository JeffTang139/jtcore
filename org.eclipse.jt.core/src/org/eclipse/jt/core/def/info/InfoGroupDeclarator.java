package org.eclipse.jt.core.def.info;

import org.eclipse.jt.core.impl.DeclaratorBase;
import org.eclipse.jt.core.impl.InfoGroupDefineImpl;

/**
 * ��Ϣ����������
 * 
 * @author Jeff Tang
 * 
 */
public abstract class InfoGroupDeclarator extends DeclaratorBase {
	@Override
	public final InfoGroupDefine getDefine() {
		return this.infos;
	}

	/**
	 * ��Ϣ��������
	 */
	protected final InfoGroupDeclare infos;

	/**
	 * �½�������Ϣ
	 */
	protected final ErrorInfoDeclare newError(String name, String messageFrmt) {
		return this.infos.newError(name, messageFrmt);
	}

	protected final ErrorInfoDeclare newError(String name, String messageFrmt,
			boolean needLog) {
		ErrorInfoDeclare info = this.infos.newError(name, messageFrmt);
		info.setNeedLog(needLog);
		return info;
	}

	/**
	 * �½�������Ϣ
	 */
	protected final WarningInfoDeclare newWarning(String name,
			String messageFrmt) {
		return this.infos.newWarning(name, messageFrmt);
	}

	protected final WarningInfoDeclare newWarning(String name,
			String messageFrmt, boolean needLog) {
		WarningInfoDeclare info = this.infos.newWarning(name, messageFrmt);
		info.setNeedLog(needLog);
		return info;
	}

	/**
	 * �½���ʾ��Ϣ
	 */
	protected final HintInfoDeclare newHint(String name, String messageFrmt) {
		return this.infos.newHint(name, messageFrmt);
	}

	protected final HintInfoDeclare newHint(String name, String messageFrmt,
			boolean needLog) {
		final HintInfoDeclare info = this.infos.newHint(name, messageFrmt);
		info.setNeedLog(needLog);
		return info;
	}

	/**
	 * �½�������Ϣ
	 */

	protected final ProcessInfoDeclare newProcess(String name,
			String messageFrmt) {
		return this.infos.newProcess(name, messageFrmt);
	}

	protected final ProcessInfoDeclare newProcess(String name,
			String messageFrmt, boolean needLog) {
		final ProcessInfoDeclare info = this.infos
				.newProcess(name, messageFrmt);
		info.setNeedLog(needLog);
		return info;
	}

	/**
	 * ���췽������ָ��Ψһ����Ϣ������
	 */
	public InfoGroupDeclarator(String name) {
		super(true);
		this.infos = new InfoGroupDefineImpl(name, this);
	}

	// //////////////////////////////////////////////////
	private final static Class<?>[] intf_classes = { InfoDefine.class };

	@Override
	protected final Class<?>[] getDefineIntfRegClasses() {
		return intf_classes;
	}

}
