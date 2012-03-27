package org.eclipse.jt.core.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jt.core.impl.ServiceBase.PerformanceProvider;
import org.eclipse.jt.core.spi.monitor.PerformanceIndexDefine;
import org.eclipse.jt.core.type.GUID;


/**
 * 性能监控指标管理器
 * 
 * @author Jeff Tang
 * 
 */
final class PerformanceIndexManagerImpl {
	private final HashMap<String, ServiceBase<?>.PerformanceProvider<?>> performanceProvidersByName = new HashMap<String, ServiceBase<?>.PerformanceProvider<?>>();
	private final HashMap<GUID, ServiceBase<?>.PerformanceProvider<?>> performanceProvidersByID = new HashMap<GUID, ServiceBase<?>.PerformanceProvider<?>>();

	final void regPerformanceProvider(ServiceBase<?>.PerformanceProvider<?> pp) {
		synchronized (this.performanceProvidersByName) {
			ServiceBase<?>.PerformanceProvider<?> e = this.performanceProvidersByName
					.put(pp.declare.name, pp);
			if (e != null) {
				this.performanceProvidersByName.put(e.declare.name, e);
				throw new UnsupportedOperationException("已经存在名为["
						+ pp.declare.name + "]的性能监控指标");
			}
			this.performanceProvidersByID.put(pp.declare.id, pp);
		}
	}

	final ServiceBase<?>.PerformanceProvider<?> find(String indexName) {
		synchronized (this.performanceProvidersByName) {
			return this.performanceProvidersByName.get(indexName);
		}
	}

	final ServiceBase<?>.PerformanceProvider<?> find(GUID indexID) {
		synchronized (this.performanceProvidersByName) {
			return this.performanceProvidersByID.get(indexID);
		}
	}

	@SuppressWarnings("unchecked")
	final int fillPerformanceIndexs(List<PerformanceIndexDefine> to) {
		final int size;
		synchronized (this.performanceProvidersByName) {
			size = this.performanceProvidersByName.size();
			final Iterator<? extends PerformanceProvider> itr = this.performanceProvidersByName
					.values().iterator();
			for (int i = 0; i < size; i++) {
				to.add(itr.next().declare);
			}
			return size;
		}
	}

	PerformanceIndexManagerImpl() {
	}
}
