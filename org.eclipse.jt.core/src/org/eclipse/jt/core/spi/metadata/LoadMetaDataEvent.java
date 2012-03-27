package org.eclipse.jt.core.spi.metadata;

import java.util.logging.Logger;

import org.eclipse.jt.core.invoke.Event;


/**
 * 装载参数信息的事件<br>
 * 1. 处理该事件时首先通过<code>mergeOrReplace()<code>判断装载模式，是合并还是覆盖。<br>
 * 2. 调用<code>getMetaStream().getRootEntry()</code>并调用其下方法找到自己需要的entry<br>
 * 3. 调用<code>getMetaStream().locateEntry(entry)</code>定位某个条目，或
 * <code>getMetaStream().getEntryAsXML(entry)<code>直接获得XML<br>
 * 4. 做相应的装载处理，出现故障则抛出异常<br>
 * 5. 如果是覆盖操作则可能还需要删除参数中没有而系统中有的元数据对象<br>
 * 6. 完成事件处理退出
 * 
 * @author Jeff Tang
 * 
 */
public final class LoadMetaDataEvent extends Event {
	private final boolean mergeOrReplace;

	private final MetaDataInputStream metaStream;
	private final Logger logger;

	/**
	 * 指示采取合并装载还是覆盖装载
	 * 
	 * @return true表示合并装载，false表示覆盖装载
	 */
	public final boolean mergeOrReplace() {
		return this.mergeOrReplace;
	}

	/**
	 * 获取信息记录对象，用以记录处理过程中的错误和警告等等。
	 */
	public final Logger getLogger() {
		return this.logger;
	}

	/**
	 * 获得元数据流
	 */
	public final MetaDataInputStream getMetaStream() {
		return this.metaStream;
	}

	public LoadMetaDataEvent(boolean mergeOrReplace, byte[] buf, Logger logger) {
		this.mergeOrReplace = mergeOrReplace;
		this.metaStream = new MetaDataInputStream(buf);
		this.logger = logger;
	}
}