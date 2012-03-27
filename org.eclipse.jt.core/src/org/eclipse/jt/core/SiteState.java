package org.eclipse.jt.core;

import org.eclipse.jt.core.exception.UnsupportedContextKindException;
import org.eclipse.jt.core.exception.UnsupportedSessionKindException;

/**
 * վ���״̬
 * 
 * <pre>
 * 
 *          WAITING_LOAD_METADATA
 *               ��    ��    ��        
 *     INITING �� ACTIVE �� DISPOSING �� DISPOSED
 *          ��              ��
 *          LOADING_METADATA
 * 
 * </pre>
 * 
 * @author Jeff Tang
 * 
 */
public enum SiteState {
	/**
	 * ��ʼ״̬
	 */
	INITING {
		@Override
		public void checkContextKind(SessionKind sessionKind,
				ContextKind contextKind) {
			switch (contextKind) {
			case DISPOSER:
			case INITER:
				return;
			case TRANSIENT:
				if (sessionKind == SessionKind.SYSTEM) {
					return;
				}
			case SITUATION:
			case NORMAL:
				throw new UnsupportedContextKindException(this, sessionKind,
						contextKind);
			default:
				ContextKind.throwIllegalContextKind(contextKind);
			}
		}

		@Override
		public final void checkSessionKind(SessionKind sessionKind) {
		}
	},
	/**
	 * �״̬�������ṩ����
	 */
	ACTIVE {
		@Override
		public final void checkContextKind(SessionKind sessionKind,
				ContextKind contextKind) {
			switch (contextKind) {
			case TRANSIENT:
			case SITUATION:
			case DISPOSER:
			case NORMAL:
				return;
			case INITER:
				throw new UnsupportedContextKindException(this, sessionKind,
						contextKind);
			default:
				ContextKind.throwIllegalContextKind(contextKind);
			}
		}

		@Override
		public final void checkSessionKind(SessionKind sessionKind) {
		}
	},
	/**
	 * רΪװ��ԭ���ݶ����õ�վ�㣬װ����ɺ������
	 */
	LOADING_METADATA {
		@Override
		public final void checkContextKind(SessionKind sessionKind,
				ContextKind contextKind) {
			switch (contextKind) {
			case DISPOSER:
			case TRANSIENT:
			case NORMAL:
			case INITER:
			case SITUATION:
				throw new UnsupportedContextKindException(this, sessionKind,
						contextKind);
			default:
				ContextKind.throwIllegalContextKind(contextKind);
			}
		}

		@Override
		public final void checkSessionKind(SessionKind sessionKind) {
			throw new UnsupportedSessionKindException(this, sessionKind);
		}
	},
	/**
	 * �ȴ�ԭ����װ�ص�վ�㣬<br>
	 * ���װ�سɹ����վ�㽫������������ص�ACTIVE״̬
	 */
	WAITING_LOAD_METADATA {
		@Override
		public final void checkContextKind(SessionKind sessionKind,
				ContextKind contextKind) {
			switch (contextKind) {
			case TRANSIENT:
			case SITUATION:
			case DISPOSER:
			case NORMAL:
				return;
			case INITER:
				throw new UnsupportedContextKindException(this, sessionKind,
						contextKind);
			default:
				ContextKind.throwIllegalContextKind(contextKind);
			}
		}

		@Override
		public final void checkSessionKind(SessionKind sessionKind) {
		}
	},
	/**
	 * �����е�վ��
	 */
	DISPOSING {
		@Override
		public final void checkContextKind(SessionKind sessionKind,
				ContextKind contextKind) {
			switch (contextKind) {
			case DISPOSER:
			case TRANSIENT:
			case SITUATION:
			case NORMAL:
				return;
			case INITER:
				throw new UnsupportedContextKindException(this, sessionKind,
						contextKind);
			default:
				ContextKind.throwIllegalContextKind(contextKind);
			}
		}

		@Override
		public final void checkSessionKind(SessionKind sessionKind) {
			throw new UnsupportedSessionKindException(this, sessionKind);
		}
	},
	/**
	 * �����˵�վ��
	 */
	DISPOSED {
		@Override
		public final void checkContextKind(SessionKind sessionKind,
				ContextKind contextKind) {
			throw new UnsupportedContextKindException(this, sessionKind,
					contextKind);
		}

		@Override
		public final void checkSessionKind(SessionKind sessionKind) {
			throw new UnsupportedSessionKindException(this, sessionKind);
		}
	};

	/**
	 * ��������������Ƿ����
	 * 
	 * @param sessionKind
	 *            �Ự����
	 * @param contextKind
	 *            ����������
	 */
	public abstract void checkContextKind(SessionKind sessionKind,
			ContextKind contextKind);

	/**
	 * ���Ự�����Ƿ����
	 * 
	 * @param sessionKind
	 */
	public abstract void checkSessionKind(SessionKind sessionKind);

	/**
	 * ������ݿ��������
	 */
	public final void checkDBAccess(boolean writeOrReadonly) {
		if (this == DISPOSED) {
			throw new IllegalStateException("վ���Ѿ����٣���֧�����ݿ����");
		} else if (writeOrReadonly && this == DISPOSING) {
			throw new IllegalStateException("վ���������٣���֧�������޸Ĳ���");
		}
	}
}
