package org.eclipse.jt.core;

import org.eclipse.jt.core.exception.UnsupportedContextKindException;
import org.eclipse.jt.core.exception.UnsupportedSessionKindException;

/**
 * 站点的状态
 * 
 * <pre>
 * 
 *          WAITING_LOAD_METADATA
 *               ↑    ↓    ↓        
 *     INITING → ACTIVE → DISPOSING → DISPOSED
 *          ↓              ↑
 *          LOADING_METADATA
 * 
 * </pre>
 * 
 * @author Jeff Tang
 * 
 */
public enum SiteState {
	/**
	 * 初始状态
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
	 * 活动状态，可以提供服务
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
	 * 专为装载原数据而启用的站点，装载完成后会抛弃
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
	 * 等待原数据装载的站点，<br>
	 * 如果装载成功则该站点将被抛弃，否则回到ACTIVE状态
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
	 * 抛弃中的站点
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
	 * 抛弃了的站点
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
	 * 检查上下文类型是否可用
	 * 
	 * @param sessionKind
	 *            会话类型
	 * @param contextKind
	 *            上下文类型
	 */
	public abstract void checkContextKind(SessionKind sessionKind,
			ContextKind contextKind);

	/**
	 * 检查会话类型是否可用
	 * 
	 * @param sessionKind
	 */
	public abstract void checkSessionKind(SessionKind sessionKind);

	/**
	 * 检查数据库访问能力
	 */
	public final void checkDBAccess(boolean writeOrReadonly) {
		if (this == DISPOSED) {
			throw new IllegalStateException("站点已经销毁，不支持数据库访问");
		} else if (writeOrReadonly && this == DISPOSING) {
			throw new IllegalStateException("站点正在销毁，不支持数据修改操作");
		}
	}
}
