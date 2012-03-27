package org.eclipse.jt.core.impl;

final class NetPackageSendingEntry<TAttachment> implements
		AsyncIOStub<TAttachment> {
	/**
	 * �ȴ������ж��п�ȱ
	 */
	final static byte STATE_QUEUING = 0;
	/**
	 * ���첢�ҷ���Fragment
	 */
	final static byte STATE_BUILDING_AND_SENDING = 1;
	/**
	 * ������ɣ��ȴ�����
	 */
	final static byte STATE_BUILDING_COMPLETE = 2;
	/**
	 * ������ɣ��ȴ�ȷ��
	 */
	final static byte STATE_WAITING_RESOLVE = 4;
	/**
	 * �ɹ���ɣ�������
	 */
	final static byte STATE_RESOLVED = 5;
	/**
	 * ����;�г��ִ��󣬽�����
	 */
	final static byte STATE_ERROR = 7;

	/**
	 * ���ݰ�ID
	 */
	final int packageID;

	private final NetChannelImpl channel;
	private final DataFragmentBuilder<? super TAttachment> builder;
	private final TAttachment attachment;
	/**
	 * �����ߴ�
	 */
	private int senderGeneration;
	/**
	 * ״̬
	 */
	private byte state;
	/**
	 * ��ʼ��������
	 */
	private boolean beginSending;

	NetPackageSendingEntry(NetChannelImpl channel,
			DataFragmentBuilder<? super TAttachment> handler,
			TAttachment attachment) {
		this.channel = channel;
		this.packageID = channel.newPackageID();
		this.builder = handler;
		this.attachment = attachment;
	}

	public void cancel() {
		this.channel.breakSend(this.packageID);
		this.channel.postBreakReceivePackageCtrl(this.packageID);
	}

	public void suspend() {
		throw new UnsupportedOperationException();
	}

	public void resume() {
		throw new UnsupportedOperationException();
	}

	public final TAttachment getAttachment() {
		return this.attachment;
	}

	final byte getState() {
		return this.state;
	}

	final void setState(byte state) {
		this.state = state;
	}

	/**
	 * �ж��Ƿ���Ҫ�������ݰ�
	 */
	final boolean needResetPackage(int senderGeneration) {
		if (!this.beginSending) {
			this.beginSending = true;
			this.senderGeneration = senderGeneration;
		} else if (this.senderGeneration < senderGeneration) {
			// ��Ҫ�����������ݰ�
			return true;
		}
		return false;
	}

	/**
	 * �������ݰ���׼�����·��ͣ������Ƿ����������ݰ�
	 */
	final void tryResetPackage() {
		if (this.builder.tryResetPackage(this.attachment)) {
			this.beginSending = false;
			this.channel.tryStartFragmentBuild(this);
		}
	}

	final void setResolved(boolean done) {
		if (done) {
			this.state = STATE_RESOLVED;
			this.builder.onFragmentOutFinished(this.attachment);
		} else {
			this.state = STATE_ERROR;
			this.builder.onFragmentOutError(this.attachment);
		}
	}

	/**
	 * ���첢Ͷ��Fragment
	 * 
	 * @throws Throwable
	 */
	final void buildAndPostFragmentToSend() throws Throwable {
		// ���Fragment����������
		final DataFragment fragment = this.channel.allocDataFragment();
		final int ctrlFlagPos = fragment.getPosition();
		final byte ctrlFlag = this.beginSending ? NetChannelImpl.CTRL_FLAG_PACKAGE
				: (NetChannelImpl.CTRL_FLAG_PACKAGE_FIRST | NetChannelImpl.CTRL_FLAG_PACKAGE);
		fragment.writeByte(ctrlFlag);
		fragment.writeInt(this.packageID);
		try {
			if (this.builder.buildFragment(fragment, this.attachment)) {
				final int endPos = fragment.getPosition();
				fragment.setPosition(ctrlFlagPos);
				fragment
						.writeByte((byte) (ctrlFlag | NetChannelImpl.CTRL_FLAG_PACKAGE_LAST));
				fragment.setPosition(endPos);
				this.state = STATE_BUILDING_COMPLETE;
			} else {
				this.state = STATE_BUILDING_AND_SENDING;
			}
		} catch (Throwable e) {
			e.printStackTrace();
			this.channel.releaseDataFragment(fragment);
			this.channel.breakSend(this.packageID);
			if (this.beginSending) {
				// �Ѿ���Ƭ�η��ͣ�������ֹ��������Ϣ
				this.channel.postBreakReceivePackageCtrl(this.packageID);
			}
			return;
		}
		// ����Fragment
		this.channel.postDataFragmentToSend(this, fragment);
	}
}
