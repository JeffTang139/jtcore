package org.eclipse.jt.core.impl;

/**
 * 序列化数据构造器
 * 
 * @author Jeff Tang
 * 
 */
public class SerializedDataBuilder<TAttachment extends NSerializerFactoryProvider>
		implements DataFragmentBuilder<TAttachment> {
	public final Object objectToSerialize;
	public final byte packageType;
	private NSerializer serializer;

	public SerializedDataBuilder(Object objectToSerialize, byte packageType) {
		this.objectToSerialize = objectToSerialize;
		this.packageType = packageType;
	}

	public final boolean tryResetPackage(TAttachment attachment) {
		if (this.serializer != null) {
			this.serializer.reset();
		}
		return true;
	}

	public final boolean buildFragment(DataOutputFragment fragment,
			TAttachment attachment) throws Throwable {
		if (this.serializer == null) {
			this.serializer = attachment.getNSerializerFactory()
					.newNSerializer();
		}
		if (this.serializer.isSerialized()) {
			this.writeHead(fragment, attachment);
			// 写入序列化器版本信息
			fragment.writeShort(this.serializer.getVersion());
			// 开始序列化
			return this.serializer.serializeStart(this.objectToSerialize,
					fragment);
		}
		return this.serializer.serializeRest(fragment);
	}

	protected void writeHead(DataOutputFragment fragment, TAttachment attachment) {
		fragment.writeByte(this.packageType);
	}

	public void onFragmentOutError(TAttachment attachment) {
	}

	public void onFragmentOutFinished(TAttachment attachment) {
	}
}
