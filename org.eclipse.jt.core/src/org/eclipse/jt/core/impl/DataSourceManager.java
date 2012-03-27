package org.eclipse.jt.core.impl;

import java.util.ArrayList;

import org.eclipse.jt.core.def.table.TableDefine;
import org.eclipse.jt.core.misc.ExceptionCatcher;
import org.eclipse.jt.core.misc.SXElement;


/**
 * 连接池
 * 
 * @author Jeff Tang
 * 
 */
final class DataSourceManager {
	final ApplicationImpl application;

	/**
	 * 连接调整器<br>
	 * 释放过期的闲置连接，创始最小连接数等
	 * 
	 * @author Jeff Tang
	 * 
	 */
	private final class ConnectionAdjuster extends RepeatWork {
		ConnectionAdjuster() {
			super(5000);
		}

		@Override
		protected void workDoing(WorkingThread thread) throws Throwable {
			final ExceptionCatcher catcher = DataSourceManager.this.application.catcher;
			final ArrayList<DataSourceImpl> datasources = DataSourceManager.this.datasources;
			try {
				for (int i = 0, c = datasources.size(); i < c; i++) {
					datasources.get(i).adjust(catcher);
				}
			} catch (Throwable e) {
				catcher.catchException(e, this);
			}
		}
	}

	DataSourceManager(ApplicationImpl application, SXElement dataSourceConfig) {
		this.application = application;
		if (dataSourceConfig != null) {
			for (SXElement dataSourceE : dataSourceConfig
					.getChildren(DataSourceImpl.xml_element_datasource)) {
				try {
					this.datasources.add(new DataSourceImpl(this, dataSourceE));
				} catch (Throwable e) {
					application.catcher.catchException(e, this);
				}
			}
		}
		application.overlappedManager.postWork(new ConnectionAdjuster());
		application.overlappedManager.postWork(new PartitionSplitter());
	}

	private static final long t2oClock = 2 * 60 * 60 * 1000;
	private static final long tDay = 24 * 60 * 60 * 1000;

	private static long getNext2OfClockOfDay() {
		return System.currentTimeMillis() / tDay * tDay + tDay + t2oClock;
	}

	private final class PartitionSplitter extends Work {

		private long starttime;

		@Override
		protected long getStartTime() {
			return this.starttime;
		}

		@Override
		protected boolean regeneration() {
			this.starttime = getNext2OfClockOfDay();
			return true;
		}

		PartitionSplitter() {
			this.starttime = getNext2OfClockOfDay();
		}

		@Override
		protected void workDoing(WorkingThread thread) throws Throwable {
			final ExceptionCatcher catcher = DataSourceManager.this.application.catcher;
			try {
				final ArrayList<TableDefine> tables = new ArrayList<TableDefine>();
				DataSourceManager.this.application.getDefaultSite()
						.fillRuntimeDefines(TableDefine.class, tables);
				final DataSourceRef dataSourceRef = DataSourceManager.this.application
						.getDefaultSite().getDataSourceRef();
				TablePartitioner splitter = null;
				DBConnectionEntry conn = null;
				try {
					for (TableDefine table : tables) {
						if (table.isPartitioned()) {
							if (conn == null) {
								conn = dataSourceRef.allocDBConnectionEntry();
							}
							if (splitter == null) {
								splitter = dataSourceRef.getLang()
										.newPartitioner();
							}
							splitter.split(table, conn);
						}
					}
				} finally {
					if (conn != null) {
						conn.close(catcher);
					}
				}
			} catch (Throwable e) {
				catcher.catchException(e, this);
			}
		}
	}

	private final NamedDefineContainerImpl<DataSourceImpl> datasources = new NamedDefineContainerImpl<DataSourceImpl>();

	final boolean isEmpty() {
		return this.datasources.isEmpty();
	}

	final DataSourceImpl getDefaultSource() {
		if (this.datasources.isEmpty()) {
			throw new IllegalStateException("没有可用的数据源配置");
		}
		return this.datasources.get(0);
	}

	final DataSourceImpl getDataSource(String author, String name) {
		if (this.datasources.isEmpty()) {
			throw new IllegalStateException("没有可用的数据源配置");
		}
		if ((name == null) || (name.length() == 0)) {
			return this.datasources.get(0);
		}
		DataSourceImpl ds = this.datasources.find(name);
		if (ds == null) {
			throw new IllegalArgumentException("找不到指定的数据源[" + name + "]");
		}
		return ds;
	}

	final void doDispose(ExceptionCatcher catcher) {
		try {
			for (DataSourceImpl ds : this.datasources) {
				ds.doDispose(catcher);
			}
		} catch (Throwable e) {
			catcher.catchException(e, e);
		}
	}

	// /////////// xml //////////////
	final static String xml_element_datasources = "datasources";
}
