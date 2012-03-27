package org.eclipse.jt.core.impl;

/**
 * ��־��������������<br>
 * ��ǰֻ��һ��
 * 
 * @author Jeff Tang
 * 
 */
public enum LogManagerStartupStep implements
		StartupStep<RefStartupEntry<LogManager>> {

	PREPARE {
		public final StartupStep<RefStartupEntry<LogManager>> doStep(
				ResolveHelper helper, RefStartupEntry<LogManager> target)
				throws Throwable {
			target.ref.setState(LogManager.S_PREPARING);
			return READY;
		}

		public final String getDescription() {
			return "��������־������";
		}

		public final int getPriority() {
			return LOGMGR_PREPARE_PRI;
		}
	},
	READY {
		public final StartupStep<RefStartupEntry<LogManager>> doStep(
				ResolveHelper helper, RefStartupEntry<LogManager> target)
				throws Throwable {
			target.ref.setState(LogManager.S_READY);
			return null;
		}

		public final int getPriority() {
			return LOGMGR_READY_PRI;
		}

		public final String getDescription() {
			return "������־������";
		}
	};

}
