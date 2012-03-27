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
		 * 未连接
		 */
		NONE,
		/**
		 * 正在连接
		 */
		CONNECTING,
		/**
		 * 连接成功
		 */
		STABLE,
		/**
		 * 发送和接收线程开始运行
		 */
		RUNNING,
		/**
		 * 连接正在关闭
		 */
		CLOSING,
		/**
		 * 连接失败
		 */
		FAIL,
		/**
		 * 连接关闭
		 */
		CLOSED,
		/**
		 * 已销毁
		 */
		DISPOSED
	}

	/**
	 * 连接超时
	 */
	static final int CONNECTION_TIMEOUT = 30000;

	final NetChannelImpl owner;
	private short remoteSerializeVersion;
	private long remoteAppInstanceVersion;
	private GUID remoteAppID;
	private int remoteNodeClusterIndex;
	/**
	 * 连接的代
	 */
	protected int generation;
	/**
	 * 连接锁
	 */
	protected final Object lock = new Object();
	/**
	 * 连接状态
	 */
	protected State state = State.NONE;
	/**
	 * 指示是否永久断开连接
	 */
	protected boolean shutdown;

	/**
	 * 输入流
	 */
	protected InputStream input;
	/**
	 * 输出流
	 */
	protected OutputStream output;
	/**
	 * 发送线程
	 */
	protected Thread sendThread;
	/**
	 * 接收线程
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
	 * 设置是否永久断开连接，如果为true则在发生网络故障时不重连，网络断开时也不抛出异常
	 * 
	 * @param allow
	 */
	public final void shutdown(boolean allow) {
		synchronized (this.lock) {
			this.shutdown = allow;
		}
	}

	/**
	 * 发起并等待连接成功，否则抛出异常
	 */
	public abstract void ensureConnected();

	/**
	 * 启动发送和接收线程
	 */
	public abstract void start();

	/**
	 * 通知连接断开
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
				throw new IllegalStateException("错误状态：" + this.state);
			}
		}
		this.owner.onConnectionClose(this);
	}

	protected final void setStateNoSync(State state) {
		this.state = state;
		this.lock.notifyAll();
	}

	/**
	 * 通知并等待发送线程和接收线程终止
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
			throw new IllegalStateException("错误状态：" + this.state);
		}
	}

	protected final void initializeInputStream(InputStream in)
			throws IOException {
		DataFragmentImpl f = this.owner.allocDataFragment(34);
		// 接收数据
		int offset = f.getAvailableOffset();
		readBytes(f.getBytes(), offset, 4, in);
		f.setPosition(offset);
		readBytes(f.getBytes(), offset + 4, f.readInt(), in);
		// 读取网络通信组件版本
		int ver = f.readInt();
		if (ver != this.owner.getComponentVersion()) {
			throw new IOException("网络协议不兼容");
		}
		// 读取timestamp
		long remoteTimestamp = f.readLong();
		DebugHelper.strike("obtain remote timestamp " + remoteTimestamp);
		if (this.remoteAppInstanceVersion == 0L) {
			this.remoteAppInstanceVersion = remoteTimestamp;
		} else if (this.remoteAppInstanceVersion != remoteTimestamp) {
			// 废弃当前连接
			this.disconnect();
			throw new ConnectException("远程服务已重置");
		}
		// 读远程appID
		this.remoteAppID = GUID.valueOf(f.getBytes(), f.getPosition());
		DebugHelper.strike("obtain remote appID " + this.remoteAppID);
		f.skip(16);
		// 读取serializeVersion
		this.remoteSerializeVersion = f.readShort();
		DebugHelper.strike("obtain remote serializer version "
				+ this.remoteSerializeVersion);
		// 读取senderGeneration
		int g = f.readInt();
		DebugHelper.strike("obtain remote generation " + g);
		if (this.generation < g) {
			this.generation = g;
		}
	}

	protected final void initializeOutputStream(OutputStream out)
			throws IOException {
		DataFragmentImpl f = this.owner.allocDataFragment(34);
		// 写入网络通信组件版本
		f.writeInt(this.owner.getComponentVersion());
		// 写入timestamp
		f.writeLong(this.owner.getChannelVersion());
		// 写入appID
		this.owner.getApplication().netNodeManager.thisCluster.appID.toBytes(f
				.getBytes(), f.getPosition());
		f.skip(16);
		// 写入serializeVersion
		f.writeShort(NSerializer.getHighestSerializeVersion());
		// 写入generation
		f.writeInt(this.generation);
		// 发送数据
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
				// throw new IndexOutOfBoundsException("数据片过大，无法接收");
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
				throw new IOException("远程主机关闭了连接");
			}
			offset += i;
			size -= i;
		} while (size > 0);
	}
}
