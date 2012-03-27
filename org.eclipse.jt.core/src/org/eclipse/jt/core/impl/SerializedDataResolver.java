package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.impl.NUnserializer.ObjectTypeQuerier;

/**
 * 序列化数据还原器
 * 
 * @author Jeff Tang
 * 
 * @param <TAttachment>
 */
public abstract class SerializedDataResolver<TAttachment> implements
		DataFragmentResolver<TAttachment> {
	private NUnserializer unserializer;
	private final ObjectTypeQuerier objectTypeQuerier;
	private final Object destHint;

	public SerializedDataResolver(ObjectTypeQuerier objectTypeQuerier,
			Object destHint) {
		this.objectTypeQuerier = objectTypeQuerier;
		this.destHint = destHint;
	}

	public SerializedDataResolver(ObjectTypeQuerier objectTypeQuerier) {
		this.objectTypeQuerier = objectTypeQuerier;
		this.destHint = null;
	}

	protected boolean readHead(DataInputFragment fragment,
			TAttachment attachment) {
		return false;
	}

	protected abstract void finishUnserialze(Object unserialzedObject,
			TAttachment attachment);

	public void onFragmentInFailed(TAttachment attachment) throws Throwable {
	}

	public final boolean resovleFragment(DataInputFragment fragment,
			TAttachment attachment) throws Throwable {
		final boolean finished;
		if (this.unserializer == null) {// 第一个片断
			if (this.readHead(fragment, attachment)) {
				return true;
			}
			this.unserializer = NUnserializer.newUnserializer(fragment
					.readShort(), this.objectTypeQuerier);
			finished = this.unserializer.unserializeStart(fragment,
					this.destHint);
		} else {
			finished = this.unserializer.unserializeRest(fragment);
		}
		if (finished) {
			this.finishUnserialze(this.unserializer.getUnserialzedObject(),
					attachment);
		}
		return finished;
	}
}
