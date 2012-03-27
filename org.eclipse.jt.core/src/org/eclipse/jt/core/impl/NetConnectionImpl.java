package org.eclipse.jt.core.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.type.GUID;


abstract class NetConnectionImpl {
	protected enum State {
		/**
		 * δ����
		 */
		NONE,
		/**
		 * ��������
		 */
		CONNECTING,
		/**
		 * ���ӳɹ�
		 */
		STABLE,
		/**
		 * ���ͺͽ����߳̿�ʼ����
		 */
		RUNNING,
		/**
		 * �������ڹر�
		 */
		CLOSING,
		/**
		 * ����ʧ��
		 */
		FAIL,
		/**
		 * ���ӹر�
		 */
		CLOSED,
		/**
		 * ������
		 */
		DISPOSED
	}

	/**
	 * ���ӳ�ʱ
	 */
	static final int CONNECTION_TIMEOUT = 30000;

	final NetChannelImpl owner;
	private short remoteSerializeVersion;
	private long remoteAppInstanceVersion;
	private GUID remoteAppID;
	private int remoteNodeClusterIndex;
	/**
	 * ���ӵĴ�
	 */
	protected int generation;
	/**
	 * ������
	 */
	protected final Object lock = new Object();
	/**
	 * ����״̬
	 */
	protected State state = State.NONE;
	/**
	 * ָʾ�Ƿ����öϿ�����
	 */
	protected boolean shutdown;

	/**
	 * ������
	 */
	protected InputStream input;
	/**
	 * �����
	 */
	protected OutputStream output;
	/**
	 * �����߳�
	 */
	protected Thread sendThread;
	/**
	 * �����߳�
	 */
	protected Thread receiveThread;

	NetConnectionImpl(NetChannelImpl owner) {
		if (owner == null) {
			throw new NullArgumentException("owner");
		}
		this.owner = owner;
	}

	public final GUID getRemoteAppID() {
		this.ensureConnected();
		return this.remoteAppID;
	}

	public final int getRemoteNodeClusterIndex() {
		return this.remoteNodeClusterIndex;
	}

	public final long getRemoteAppInstanceVersion() {
		this.ensureConnected();
		return this.remoteAppInstanceVersion;
	}

	public final short getRemoteSerializeVersion() {
		this.ensureConnected();
		return this.remoteSerializeVersion;
	}

	/**
	 * �����Ƿ����öϿ����ӣ����Ϊtrue���ڷ����������ʱ������������Ͽ�ʱҲ���׳��쳣
	 * 
	 * @param allow
	 */
	public final void shutdown(boolean allow) {
		synchronized (this.lock) {
			this.shutdown = allow;
		}
	}

	/**
	 * ���𲢵ȴ����ӳɹ��������׳��쳣
	 */
	public abstract void ensureConnected();

	/**
	 * �������ͺͽ����߳�
	 */
	public abstract void start();

	/**
	 * ֪ͨ���ӶϿ�
	 */
	public final void disconnect() {
		synchronized (this.lock) {
			this.exitSendAndReceiveThreadNoSync();
			switch (this.state) {
			case NONE:
			case CLOSING:
			case CLOSED:
			case FAIL:
				this.setStateNoSync(State.DISPOSED);
				break;
			case DISPOSED:
				return;
			default:
				throw new IllegalStateException("����״̬��" + this.state);
			}
		}
		this.owner.onConnectionClose(this);
	}

	protected final void setStateNoSync(State state) {
		this.state = state;
		this.lock.notifyAll();
	}

	/**
	 * ֪ͨ���ȴ������̺߳ͽ����߳���ֹ
	 */
	protected void exitSendAndReceiveThreadNoSync() {
		for (;;) {
			switch (this.state) {
			case NONE:
			case CLOSED:
			case FAIL:
			case DISPOSED:
				return;
			case CLOSING:
				try {
					this.lock.wait();
				} catch (InterruptedException e) {
				}
				continue;
			default:
				this.setStateNoSync(State.CLOSING);
				break;
			}
			break;
		}
		this.generation++;
		if (this.sendThread != null) {
			this.sendThread.interrupt();
		}
		if (this.receiveThread != null) {
			this.receiveThread.interrupt();
		}
		if (this.output != null) {
			try {
				this.output.close();
			} catch (Throwable e) {
			}
		}
		if (this.input != null) {
			try {
				this.input.close();
			} catch (Throwable e) {
			}
		}
		while (this.sendThread != null || this.receiveThread != null) {
			try {
				this.lock.wait();
			} catch (InterruptedException e) {
			}
		}
		switch (this.state) {
		case CLOSING:
			break;
		default:
			throw new IllegalStateException("����״̬��" + this.state);
		}
	}

	protected final void initializeInputStream(InputStream in)
			throws IOException {
		DataFragmentImpl f = this.owner.allocDataFragment(34);
		// ��������
		int offset = f.getAvailableOffset();
		readBytes(f.getBytes(), offset, 4, in);
		f.setPosition(offset);
		readBytes(f.getBytes(), offset + 4, f.readInt(), in);
		// ��ȡ����ͨ������汾
		int ver = f.readInt();
		if (ver != this.owner.getComponentVersion()) {
			throw new IOException("����Э�鲻����");
		}
		// ��ȡtimestamp
		long remoteTimestamp = f.readLong();
		DebugHelper.strike("obtain remote timestamp " + remoteTimestamp);
		if (this.remoteAppInstanceVersion == 0L) {
			this.remoteAppInstanceVersion = remoteTimestamp;
		} else if (this.remoteAppInstanceVersion != remoteTimestamp) {
			// ������ǰ����
			this.disconnect();
			throw new ConnectException("Զ�̷���������");
		}
		// ��Զ��appID
		this.remoteAppID = GUID.valueOf(f.getBytes(), f.getPosition());
		DebugHelper.strike("obtain remote appID " + this.remoteAppID);
		f.skip(16);
		// ��ȡserializeVersion
		this.remoteSerializeVersion = f.readShort();
		DebugHelper.strike("obtain remote serializer version "
				+ this.remoteSerializeVersion);
		// ��ȡsenderGeneration
		int g = f.readInt();
		DebugHelper.strike("obtain remote generation " + g);
		if (this.generation < g) {
			this.generation = g;
		}
	}

	protected final void initializeOutputStream(OutputStream out)
			throws IOException {
		DataFragmentImpl f = this.owner.allocDataFragment(34);
		// д������ͨ������汾
		f.writeInt(this.owner.getComponentVersion());
		// д��timestamp
		f.writeLong(this.owner.getChannelVersion());
		// д��appID
		this.owner.getApplication().netNodeManager.thisCluster.appID.toBytes(f
				.getBytes(), f.getPosition());
		f.skip(16);
		// д��serializeVersion
		f.writeShort(NSerializer.getHighestSerializeVersion());
		// д��generation
		f.writeInt(this.generation);
		// ��������
		int offset = f.getAvailableOffset();
		int size = f.getPosition() - offset;
		f.setPosition(offset);
		f.writeInt(size - 4);
		out.write(f.getBytes(), offset, size);
		out.flush();
	}

	public final void send(OutputStream out, DataFragment fragment)
			throws IOException {
		int offset = fragment.getAvailableOffset();
		int size = fragment.getPosition() - offset;
		fragment.setPosition(offset);
		fragment.writeInt(size - 4);
		out.write(fragment.getBytes(), offset, size);
		DebugHelper.trace("write[" + (size - 4) + "] ", fragment.getBytes(),
				offset + 4, size - 4);
		out.flush();
	}

	public final DataFragment receive(InputStream in, DataFragment fragment)
			throws Throwable {
		byte[] buff = fragment.getBytes();
		int offset = fragment.getAvailableOffset();
		fragment.setPosition(offset);
		readBytes(buff, offset, 4, in);
		int size = fragment.readInt();
		if (size > 0) {
			if (size > fragment.getAvailableLength() - 4) {
				fragment = this.owner.allocDataFragment(size);
				fragment.setPosition(fragment.getAvailableOffset());
				fragment.writeInt(size);
				// throw new IndexOutOfBoundsException("����Ƭ�����޷�����");
			}
			offset = fragment.getPosition();
			readBytes(buff, offset, size, in);
			DebugHelper.trace("read [" + size + "] ", buff, offset, size);
			fragment.setPosition(offset);
		}
		fragment.limit(offset + size);
		return fragment;
	}

	private static final void readBytes(byte[] buff, int offset, int size,
			InputStream input) throws IOException {
		do {
			int i = input.read(buff, offset, size);
			if (i < 0) {
				throw new IOException("Զ�������ر�������");
			}
			offset += i;
			size -= i;
		} while (size > 0);
	}
}
