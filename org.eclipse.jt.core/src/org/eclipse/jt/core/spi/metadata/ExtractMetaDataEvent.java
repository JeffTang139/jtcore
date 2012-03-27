package org.eclipse.jt.core.spi.metadata;

import java.io.OutputStream;

import org.eclipse.jt.core.invoke.Event;


/**
 * 收集提取参数条目事件 <br>
 * 1. 遍历自己管理的元数据对象，依次：<br>
 * 1.1. 调用
 * <code>getMetaStream().newEntry("yourfolder/youentry1",RECVER,"XYZ")</code>
 * 返回新条目的输出流。并使用该流序列化参数。或：<br>
 * 1.2. 直接使用
 * <code>getMetaStream().newEntry("yourfolder/youentry1",RECVER,"XYZ",xml)</code>
 * 创建新条目<br>
 * 2. 完成提取处理
 * 
 * @author Jeff Tang
 * 
 */
public final class ExtractMetaDataEvent extends Event {
	private final MetaDataOutputStream metaStream;
	private final boolean extractAll;

	/**
	 * 获得原数据打包流
	 */
	public final MetaDataOutputStream getMetaStream() {
		return this.metaStream;
	}

	/**
	 * 获得是否收集全部参数，而非仅仅下发参数
	 */
	public final boolean extractAll() {
		return this.extractAll;
	}

	/**
	 * 构造方法，构造收集下发或全部参数 的事件
	 * 
	 * @param out
	 *            目标流
	 * @param isExtractAll
	 *            是否收集下发参数
	 */
	public ExtractMetaDataEvent(OutputStream out, boolean isExtractAll) {
		this.metaStream = new MetaDataOutputStream(out);
		this.extractAll = isExtractAll;
	}

	/**
	 * 构造方法，构造收集下发参数的事件
	 * 
	 * @param out
	 *            目标流
	 */
	public ExtractMetaDataEvent(OutputStream out) {
		this(out, false);
	}

}