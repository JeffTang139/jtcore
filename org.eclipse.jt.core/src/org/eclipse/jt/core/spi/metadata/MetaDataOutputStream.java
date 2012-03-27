package org.eclipse.jt.core.spi.metadata;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.jt.core.impl.MetaDataZipOutputStream;
import org.eclipse.jt.core.misc.SXElement;


/**
 * 原数据打包流
 * 
 * @author Jeff Tang
 * 
 */
public final class MetaDataOutputStream extends FilterOutputStream {
	/**
	 * 创建一个新条目，名称冲突时抛出异常，并且返回该条目的输出流<br>
	 * 该流得有效期为下次调用<code>newEntry</code>或<code></code>
	 * 
	 * @param pathName
	 *            条目名称，用"/"分隔开
	 * @param verion
	 *            版本号 ，建议用原数据对应的RECVER
	 * @param description
	 *            描述
	 */
	public final void newEntry(String pathName, long version, String description) {
		((MetaDataZipOutputStream) super.out).newEntry(pathName, version,
		        description);
	}

	/**
	 * 将SXElement作为参数条目写入参数流，随后关闭条目
	 * 
	 * @param pathName
	 *            条目名称，用"/"分隔开
	 * @param version
	 *            参数条目版本
	 * @param description
	 *            参数条目描述
	 * @param metaData
	 *            XML 元数据
	 */
	public final void newEntry(String pathName, long version,
	        String description, SXElement metaData) {
		((MetaDataZipOutputStream) super.out).newEntry(pathName, version,
		        description, metaData);
	}

	public MetaDataOutputStream(OutputStream out) {
		super(new MetaDataZipOutputStream(out));
	}

	private int usecount;

	public final void use() {
		if (this.usecount != Integer.MIN_VALUE) {
			this.usecount++;
		}
	}

	public final void unuse() throws IOException {
		if (this.usecount > 0 && --this.usecount == 0) {
			this.usecount = Integer.MIN_VALUE;
			super.close();
		}
	}

	@Override
	public final void close() throws IOException {
		if (this.usecount == 0) {
			super.close();
		}
	}
}
