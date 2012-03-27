package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.Login;
import org.eclipse.jt.core.service.Publish;
import org.eclipse.jt.core.service.Publish.Mode;
import org.eclipse.jt.core.spi.monitor.PerformanceIndexDefine;
import org.eclipse.jt.core.spi.monitor.PerformanceLongValueCollector;
import org.eclipse.jt.core.spi.monitor.PerformanceMonitorStartResult;
import org.eclipse.jt.core.spi.monitor.PerformanceObjValueCollector;
import org.eclipse.jt.core.spi.monitor.PerformanceSequenceValueCollector;
import org.eclipse.jt.core.spi.monitor.PerformanceValueCollector;
import org.eclipse.jt.core.spi.monitor.PerformanceValuesQueryBy;
import org.eclipse.jt.core.spi.monitor.PerformanceValuesQueryBy.PerformanceIndex;
import org.eclipse.jt.core.type.GUID;


/**
 * 性能监控服务
 * 
 */
final class PM_Service extends ServiceBase<ContextImpl<?, ?, ?>> {
	@Override
	protected void init(Context context) throws Throwable {
		super.init(context);
		List<PerformanceIndexDefine> pids = context
				.getList(PerformanceIndexDefine.class);
		final PerformanceValuesQueryBy pvqb = new PerformanceValuesQueryBy(GUID
				.randomID());
		for (PerformanceIndexDefine pid : pids) {
			if (!pid.isUnderSession()) {
				pvqb.addPerformanceIndex(pid.getID());
			}
		}
		List<Object> v = context.getList(Object.class, pvqb);
		System.out.println(v);
	}

	abstract class PM_SessionManagerMonitor<TPerformanceValueContainer extends PerformanceValueCollector<SessionManager>>
			extends PerformanceProvider<TPerformanceValueContainer> {
		protected PM_SessionManagerMonitor(String name) {
			super(name);
		}

		@Override
		protected PerformanceMonitorStartResult startMonitor(Login login,
				TPerformanceValueContainer valueCollector) throws Throwable {
			valueCollector
					.setAttachment(((SessionImpl) login).application.sessionManager);
			return super.startMonitor(login, valueCollector);
		}
	}

	/**
	 * 普通会话个数监控器
	 * 
	 * @author Jeff Tang
	 * 
	 */
	@Publish(Mode.SITE_PUBLIC)
	final class PM_NomalSessionCount
			extends
			PM_SessionManagerMonitor<PerformanceLongValueCollector<SessionManager>> {

		protected PM_NomalSessionCount() {
			super("系统\\会话\\数量");
			this.declare.setTitle("会话数量");
			this.declare.setDescription("系统的普通会话数量");
		}

		@Override
		protected boolean update(Login login,
				PerformanceLongValueCollector<SessionManager> valueCollector)
				throws Throwable {
			valueCollector.setValue(valueCollector.getAttachment()
					.getNormalSessionCount());
			return true;
		}
	}

	/**
	 * 普通会话列表监控器
	 * 
	 * @author Jeff Tang
	 * 
	 */
	@Publish(Mode.SITE_PUBLIC)
	final class PM_NomalSessions
			extends
			PM_SessionManagerMonitor<PerformanceObjValueCollector<SessionManager, PMV_SessionInfo[]>> {

		protected PM_NomalSessions() {
			super("系统\\会话\\列表");
			this.declare.setTitle("会话列表");
			this.declare.setDescription("系统的普通会话列表");
		}

		@Override
		protected boolean update(
				Login login,
				PerformanceObjValueCollector<SessionManager, PMV_SessionInfo[]> valueCollector)
				throws Throwable {
			final ArrayList<PMV_SessionInfo> sessions = new ArrayList<PMV_SessionInfo>();
			for (SessionImpl session : valueCollector.getAttachment()
					.getNormalSessions()) {
				sessions.add(new PMV_SessionInfo(session));
			}
			valueCollector.setValue(sessions
					.toArray(new PMV_SessionInfo[sessions.size()]));
			return true;
		}
	}

	/**
	 * 简单Sql监控
	 * 
	 */
	@Publish(Mode.SITE_PUBLIC)
	final class PM_SimpleSql
			extends
			PerformanceProvider<PerformanceSequenceValueCollector<Object, PMV_SimpleSql>> {
		protected PM_SimpleSql() {
			super("系统\\会话\\SQL");
			this.declare.setTitle("SQL监控");
			this.declare.setDescription("会话的执行Sql");
			this.declare.setIsUnderSession(true);
		}

		@Override
		protected PerformanceMonitorStartResult startMonitor(
				Login login,
				PerformanceSequenceValueCollector<Object, PMV_SimpleSql> valueCollector)
				throws Throwable {
			valueCollector.setValueMaxAge(Long.MAX_VALUE);
			((SessionImpl) login).pmv_SimpleSql = valueCollector;
			return super.startMonitor(login, valueCollector);
		}

		@Override
		protected boolean stopMonitor(
				Login login,
				PerformanceSequenceValueCollector<Object, PMV_SimpleSql> valueCollector)
				throws Throwable {
			((SessionImpl) login).pmv_SimpleSql = null;
			return super.stopMonitor(login, valueCollector);
		}
	}

	/**
	 * 高级Sql监控
	 * 
	 */
	@Publish(Mode.SITE_PUBLIC)
	final class PM_Sql
			extends
			PerformanceProvider<PerformanceSequenceValueCollector<Object, PMV_Sql>> {
		protected PM_Sql() {
			super("系统\\会话\\SQL高级");
			this.declare.setTitle("高级SQL监控");
			this.declare.setDescription("会话的执行Sql");
			this.declare.setIsUnderSession(true);
		}

		@Override
		protected PerformanceMonitorStartResult startMonitor(
				Login login,
				PerformanceSequenceValueCollector<Object, PMV_Sql> valueCollector)
				throws Throwable {
			valueCollector.setValueMaxAge(Long.MAX_VALUE);
			((SessionImpl) login).pmv_Sql = valueCollector;
			return super.startMonitor(login, valueCollector);
		}

		@Override
		protected boolean stopMonitor(
				Login login,
				PerformanceSequenceValueCollector<Object, PMV_Sql> valueCollector)
				throws Throwable {
			((SessionImpl) login).pmv_Sql = null;
			return super.stopMonitor(login, valueCollector);
		}
	}

	protected PM_Service() {
		super("逻辑框架性能监控服务");
	}

	// ////////////////////////////////////////////////////
	// // 指标监控
	// ///////////////////////////////////////////////////
	/**
	 * 提供全部指标定义
	 */
	@Publish(value = Mode.SITE_PUBLIC)
	final class PerformanceIndexDefinesProvider extends
			ResultListProvider<PerformanceIndexDefine> {

		@Override
		protected void provide(ContextImpl<?, ?, ?> context,
				List<PerformanceIndexDefine> resultList) throws Throwable {
			PM_Service.this.site.performanceIndexManager
					.fillPerformanceIndexs(resultList);
		}
	}

	/**
	 * 根据名称获取指标定义
	 */
	@Publish(value = Mode.SITE_PUBLIC)
	final class PerformanceIndexDefineByNameProvider extends
			OneKeyResultProvider<PerformanceIndexDefine, String> {

		@Override
		protected PerformanceIndexDefine provide(ContextImpl<?, ?, ?> context,
				String key) throws Throwable {
			final ServiceBase<?>.PerformanceProvider<?> provider = PM_Service.this.site.performanceIndexManager
					.find(key);
			if (provider == null) {
				return null;
			}
			return provider.declare;
		}
	}

	/**
	 * 根据名称获取指标定义
	 */
	@Publish(value = Mode.SITE_PUBLIC)
	final class PerformanceIndexDefineByIDProvider extends
			OneKeyResultProvider<PerformanceIndexDefine, GUID> {

		@Override
		protected PerformanceIndexDefine provide(ContextImpl<?, ?, ?> context,
				GUID key) throws Throwable {
			final ServiceBase<?>.PerformanceProvider<?> provider = PM_Service.this.site.performanceIndexManager
					.find(key);
			if (provider == null) {
				return null;
			}
			return provider.declare;
		}
	}

	@Publish(value = Mode.SITE_PUBLIC)
	final class PerformanceValuesProvider extends
			OneKeyResultListProvider<Object, PerformanceValuesQueryBy> {

		@Override
		protected void provide(ContextImpl<?, ?, ?> context,
				PerformanceValuesQueryBy key, final List<Object> resultList)
				throws Throwable {
			final Site site = PM_Service.this.site;
			final PerformanceIndexManagerImpl pm = site.performanceIndexManager;
			final SessionManager sm = site.application.sessionManager;
			final long systemSessionID = sm.getSystemSession().getID();
			final LongKeyMap<PerformanceValueRequestEntry> requests = new LongKeyMap<PerformanceValueRequestEntry>();
			final int piSize = key.getPerformanceIndexCount();
			for (int i = 0; i < piSize; i++) {
				final PerformanceIndex pi = key.getPerformanceIndex(i);
				final ServiceBase<?>.PerformanceProvider<?> provider = pm
						.find(pi.getIndexID());
				final long sessionID;
				if (provider != null) {
					if (provider.declare.isUnderSession()) {
						sessionID = pi.getSessionID();
					} else {
						sessionID = systemSessionID;
					}
					final PerformanceValueRequestEntry request = new PerformanceValueRequestEntry(
							provider, i, pi.getNextCapacity());
					request.nextInSameSession = requests
							.put(sessionID, request);
				}
				resultList.add(null);
			}
			// //////////////性能监控//////////////
			// ///////////////////////////////////
			if (!requests.isEmpty()) {
				final GUID monitorID = key.monitorID;
				final LongKeyValueVisitor<PerformanceValueRequestEntry> visitor = new LongKeyValueVisitor<PerformanceValueRequestEntry>() {
					public void visit(long sessionID,
							PerformanceValueRequestEntry requestsInSession) {
						final SessionImpl session = sm.getOrFindSession(
								sessionID, false);
						if (session != null) {
							session.pmv_UpdateValues(requestsInSession,
									monitorID);
						}
						for (PerformanceValueRequestEntry req = requestsInSession, next; req != null; req = next) {
							next = req.nextInSameSession;
							if (req.value != null) {
								resultList.set(req.position, req.value);
								req.value = null;// helpt GC
							}
							req.nextInSameSession = null;// helpt GC
							req.provider = null;// helpt GC
						}
					}
				};
				requests.visitAll(visitor);
			}
		}
	}
}
