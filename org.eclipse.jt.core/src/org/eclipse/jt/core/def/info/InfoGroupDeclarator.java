package org.eclipse.jt.core.def.info;

import org.eclipse.jt.core.impl.DeclaratorBase;
import org.eclipse.jt.core.impl.InfoGroupDefineImpl;

/**
 * 信息分组声明器
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
	 * 信息分组声明
	 */
	protected final InfoGroupDeclare infos;

	/**
	 * 新建错误信息
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
	 * 新建警告信息
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
	 * 新建提示信息
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
	 * 新建过程信息
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
	 * 构造方法，请指定唯一的信息分组名
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
