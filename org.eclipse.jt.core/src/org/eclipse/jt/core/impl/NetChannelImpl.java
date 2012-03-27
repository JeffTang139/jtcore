package org.eclipse.jt.core.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.type.GUID;


/**
 * 网络通信通道
 * 
 * @author Jeff Tang
 * 
 */
public final class NetChannelImpl implements NetChannel {
	/**
	 * 连接状态
	 * 
	 * @author Jeff Tang
	 * 
	 */
	private enum ConnectionState {
		/**
		 * 没有连接
		 */
		NONE,
		/**
		 * 等待被动连接完成
		 */
		PASSIVE,
		/**
		 * 等待主动连接完成
		 */
		ACTIVE,
		/**
		 * 等待连接完成，既有主动连接，又有被动连接
		 */
		BOTH,
		/**
		 * 连接完成，可以进行通信
		 */
		STABLE,
		/**
		 * 连接失败，无连接可用
		 */
		FAIL,
		/**
		 * 等待连接关闭
		 */
		CLOSE_WAIT
	}

	/**
	 * 工作线程状态
	 * 
	 * @author Jeff Tang
	 * 
	 */
	private enum WorkingThreadState {
		/**
		 * 正在工作
		 */
		WORKING,
		/**
		 * 空闲
		 */
		IDLE,
		/**
		 * 超时
		 */
		TIMEOUT
	}

	/**
	 * 需要确认的数据片段的构造器
	 * 
	 * @author Jeff Tang
	 * 
	 */
	private interface IAckRequiredFragmentBuilder {
		public DataFragment build();
	}

	/**
	 * 需要确认的数据片段队列元素
	 * 
	 * @author Jeff Tang
	 * 
	 */
	private static class AckRequiredFragmentStub {
		int ackID;
		final IAckRequiredFragmentBuilder builder;

		public AckRequiredFragmentStub(int ackID,
				IAckRequiredFragmentBuilder builder) {
			this.ackID = ackID;
			this.builder = builder;
		}
	}

	/**
	 * 类型掩码
	 */
	static final byte CTRL_FLAG_TYPE_MASK = (byte) 0x0f;
	/**
	 * 子类型掩码
	 */
	static final byte CTRL_FLAG_SUBTYPE_MASK = (byte) 0xf0;
	/**
	 * 控制标记：数据包片断
	 */
	static final byte CTRL_FLAG_PACKAGE = 1;
	/**
	 * 控制标记：数据包片断头
	 */
	static final byte CTRL_FLAG_PACKAGE_FIRST = (byte) 0x40;
	/**
	 * 控制标记：数据包片断尾
	 */
	static final byte CTRL_FLAG_PACKAGE_LAST = (byte) 0x80;
	/**
	 * 控制标记：终止接受方接受数据包，该标记由发送方发送
	 */
	static final byte CTRL_FLAG_BREAK_PACKAGE_RECEIVE = 2;
	/**
	 * 控制标记：终止发送方发送数据包，该标记由接受方发送
	 */
	static final byte CTRL_FLAG_BREAK_PACKAGE_SEND = 3;
	/**
	 * 控制标记：包接受完毕，接受方还原完毕某包后回发
	 */
	static final byte CTRL_FLAG_PACKAGE_RESOLVED = 4;
	/**
	 * 控制标记：通知发送端重新发送数据包
	 */
	static final byte CTRL_FLAG_RESEND_PACKAGE = 6;
	/**
	 * 控制标记：断开连接
	 */
	static final byte CTRL_FLAG_CLOSE = 7;
	/**
	 * Close消息：取消断开连接
	 */
	static final byte CTRL_FLAG_CLOSE_CANCEL = (byte) 0x40;
	/**
	 * 控制标记：保持连接，不需要确认
	 */
	static final byte CTRL_FLAG_KEEP_ALIVE = 12;
	/**
	 * 控制标记：回声消息，用于测试网络是否连通
	 */
	static final byte CTRL_FLAG_ECHO = 13;
	/**
	 * 控制标记：ACK确认信息
	 */
	static final byte CTRL_FLAG_ACK = 14;
	/**
	 * 数据片的最大长度
	 */
	static final int PACKAGE_FRAGMENT_SIZE = 1024 * 32;
	/**
	 * 空闲状态超时时间
	 */
	static final long IDLE_TIMEOUT = 3000;
	/**
	 * keep-alive消息的间隔时间
	 */
	static final long KEEP_ALIVE_TIMING = IDLE_TIMEOUT / 2;

	private final NetChannelManagerImpl owner;
	/**
	 * 远程节点的ID
	 */
	final GUID remoteNodeID;
	/**
	 * send锁
	 */
	final Object outLock = new Object();
	/**
	 * receive锁
	 */
	final Object inLock = new Object();
	/**
	 * ACK序号种子
	 */
	private int ackSeed;
	/**
	 * 等待ack的消息队列
	 */
	private final Queue<AckRequiredFragmentStub> waitingAckFragments = new LinkedList<AckRequiredFragmentStub>();
	/**
	 * 等待构造的数据包
	 */
	private final Queue<NetPackageSendingEntry<?>> waitingBuildingPackages = new LinkedList<NetPackageSendingEntry<?>>();
	/**
	 * 发送中的数据包
	 */
	final IntKeyMap<NetPackageSendingEntry<?>> sendingPackages = new IntKeyMap<NetPackageSendingEntry<?>>();
	/**
	 * 待发送片断<br>
	 * 为了使发送连续，因此在一个片断发送时，另一些片断就预先准备好。
	 */
	protected final Queue<DataFragment> waitingSendingFragments = new LinkedList<DataFragment>();
	/**
	 * 接收的数据包
	 */
	private final IntKeyMap<NetPackageReceivingEntry<?>> receivingPackages = new IntKeyMap<NetPackageReceivingEntry<?>>();
	/**
	 * 正在构造的Fragment的个数
	 */
	protected int buildingFragmentCount;
	/**
	 * 等待发送列队的最大长度
	 */
	private int waitingSendingQueueMaxSize = 5;
	/**
	 * 指示是否保持连接
	 */
	private boolean keepAlive;
	/**
	 * 最后一次活动时间
	 */
	private long lastActiveTime;
	/**
	 * 连接状态
	 */
	private ConnectionState connectionState = ConnectionState.NONE;
	/**
	 * 连接锁
	 */
	private final Object connectionLock = new Object();
	/**
	 * 有效连接
	 */
	private NetConnectionImpl conn;
	/**
	 * 临时的主动连接
	 */
	private NetActiveConnectionImpl activeConn;
	/**
	 * 临时的被动连接
	 */
	private NetPassiveConnectionImpl passiveConn;
	/**
	 * 远程主机地址，只有主动连接时可用
	 */
	private URL url;

	NetChannelImpl(NetChannelManagerImpl owner, GUID remoteNodeID) {
		if (owner == null) {
			throw new NullArgumentException("owner");
		}
		if (remoteNodeID == null || GUID.emptyID.equals(remoteNodeID)) {
			throw new NullArgumentException("remoteNodeID");
		}
		this.owner = owner;
		this.remoteNodeID = remoteNodeID;
		DebugHelper.strike("new channel created at node " + remoteNodeID);
	}

	public final GUID getRemoteNodeID() {
		return this.remoteNodeID;
	}

	public final GUID getRemoteAppID() {
		this.connectAndStart();
		return this.conn.getRemoteAppID();
	}

	public final int getRemoteNodeClusterIndex() {
		return this.conn.getRemoteNodeClusterIndex();
	}

	public final long getRemoteAppInstanceVersion() {
		this.connectAndStart();
		return this.conn.getRemoteAppInstanceVersion();
	}

	public final short getRemoteSerializeVersion() {
		this.connectAndStart();
		return this.conn.getRemoteSerializeVersion();
	}

	public final void setKeepAlive(boolean isKeepAlive) {
		synchronized (this.outLock) {
			this.keepAlive = isKeepAlive;
		}
	}

	public final boolean getKeepAlive() {
		synchronized (this.outLock) {
			return this.keepAlive;
		}
	}

	final int getComponentVersion() {
		return this.owner.getVersion();
	}

	final long getChannelVersion() {
		return this.owner.channelVersion;
	}

	final ApplicationImpl getApplication() {
		return this.owner.application;
	}

	final int newPackageID() {
		return this.owner.newPackageID();
	}

	final void offerFragmentResolve(NetPackageReceivingEntry<?> rpe)
			throws InterruptedException {
		this.owner.offerFragmentResolve(rpe);
	}

	/**
	 * 分配大小固定的缓冲区，大小为PACKAGE_FRAGMENT_SIZE
	 * 
	 * @return
	 */
	final DataFragment allocDataFragment() {
		return this.allocDataFragment(PACKAGE_FRAGMENT_SIZE);
	}

	/**
	 * 分配能够容纳字节数为capacity的数据的缓冲区，缓冲区前后各空余4个字节作为头部
	 * 
	 * @param capacity
	 * @return
	 */
	final DataFragmentImpl allocDataFragment(int capacity) {
		// 总长度 = 4(size) + capacity + 4(next size)
		DataFragmentImpl f = new DataFragmentImpl(capacity + 8);
		// size
		f.skip(4);
		f.limit(capacity + 4);
		return f;
	}

	final void releaseDataFragment(DataFragment fragment) {
		// do nothing
	}

	private final void setConnStateNoSync(ConnectionState state) {
		this.connectionState = state;
		this.connectionLock.notifyAll();
	}

	/**
	 * 建立连接并且启动发送和接收线程
	 * 
	 * @return
	 * @throws IOException
	 */
	public final void connectAndStart() {
		try {
			NetConnectionImpl channel;
			ACTIVE_WAIT: {
				for (;;) {
					synchronized (this.connectionLock) {
						switch (this.connectionState) {
						case NONE:
							if (this.url != null) {
								this.setConnStateNoSync(ConnectionState.ACTIVE);
								// 没有连接，则建立主动连接
								channel = this.activeConn = new NetActiveConnectionImpl(
										this, this.url);
								break ACTIVE_WAIT;
							}
							// 尝试等待被动连接
							this.passiveConn = new NetPassiveConnectionImpl(
									this);
							this.setConnStateNoSync(ConnectionState.PASSIVE);
						case PASSIVE:
							// 正在尝试被动连接
							channel = this.passiveConn;
							break;
						case STABLE:
							// 连接已建立好，直接返回
							return;
						case FAIL:
							throw new IOException("连接失败");
						default:
							// ACTIVE
							// BOTH
							// CLOSE_WAIT
							// 等待状态
							try {
								long left = NetConnectionImpl.CONNECTION_TIMEOUT;
								long start = System.currentTimeMillis();
								while (left > 0) {
									this.connectionLock.wait(left);
									switch (this.connectionState) {
									case ACTIVE:
									case BOTH:
									case CLOSE_WAIT:
										left = start
												+ NetConnectionImpl.CONNECTION_TIMEOUT
												- System.currentTimeMillis();
										if (left <= 0) {
											throw new ConnectException("连接超时");
										}
										continue;
									}
									break;
								}
							} catch (InterruptedException e) {
								throw Utils.tryThrowException(e);
							}
							continue;
						}
					}
					// PASSIVE
					this.waitPassiveConnection(channel);
				}
			}
			boolean available = true;
			// 确保主动连接成功
			try {
				channel.ensureConnected();
			} catch (Throwable e) {
				e.printStackTrace();
				available = false;
			}
			synchronized (this.connectionLock) {
				try {
					switch (this.connectionState) {
					case ACTIVE:
						if (!available) {
							this.setConnStateNoSync(ConnectionState.FAIL);
							throw new IOException("无法连接到" + this.url);
						}
						this.conn = this.activeConn;
						this.setConnStateNoSync(ConnectionState.STABLE);
						return;
					case BOTH:
						if (available && this.useActiveChannel()) { // 既有主动连接，又有被动连接，比较优先级
							// 主动连接优先，则返回主动连接，清空被动连接
							this.passiveConn.disconnect();
							// 返回主动连接
							this.conn = this.activeConn;
							this.setConnStateNoSync(ConnectionState.STABLE);
							return;
						}
						// 被动连接优先，清空主动连接
						this.activeConn.disconnect();
						// 等待被动连接
						this.setConnStateNoSync(ConnectionState.PASSIVE);
						channel = this.passiveConn;
						break;
					case FAIL:
						throw new IOException("连接失败");
					default:
						throw new IllegalStateException("状态错误："
								+ this.connectionState);
					}
				} finally {
					switch (this.connectionState) {
					case STABLE:
						this.conn.start();
					case FAIL:
						this.activeConn = null;
						this.passiveConn = null;
						break;
					default:
						throw new IllegalStateException("状态错误："
								+ this.connectionState);
					}
				}
			}
			this.waitPassiveConnection(channel);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	private final NetPassiveConnectionImpl getPassiveConnection()
			throws Throwable {
		for (;;) {
			synchronized (this.connectionLock) {
				switch (this.connectionState) {
				case NONE:
					this.setConnStateNoSync(ConnectionState.PASSIVE);
					// 建立被动连接
					return this.passiveConn = new NetPassiveConnectionImpl(this);
				case ACTIVE:
					this.setConnStateNoSync(ConnectionState.BOTH);
					// 返回被动连接
					return this.passiveConn = new NetPassiveConnectionImpl(this);
				case STABLE:
					this.setConnStateNoSync(ConnectionState.CLOSE_WAIT);
					this.conn.shutdown(true);
					break;
				case CLOSE_WAIT:
					this.connectionLock.wait();
					continue;
				case FAIL:
					throw new IOException("连接失败");
				default:
					// PASSIVE
					// WAIT
					// BOTH
					// 已经存在被动连接，直接返回
					return this.passiveConn;
				}
			}
			// 关闭
			this.owner.application.overlappedManager.startWork(new Work() {
				@Override
				protected void doWork(WorkingThread thread) throws Throwable {
					close();
				}
			});
		}
	}

	/**
	 * 设置被动连接的OutputStream并且尝试启动发送线程
	 * 
	 * @param out
	 */
	public final void attachServletOutput(OutputStream out) {
		try {
			this.owner.application.overlappedManager.startWork(new Work() {
				@Override
				protected void doWork(WorkingThread thread) throws Throwable {
					// 确保被动连接准备好
					connectAndStart();
				}
			});
			this.getPassiveConnection().servletOutputThreadEntry(out);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	/**
	 * 设置被动连接的InputStream并且尝试启动接收线程
	 * 
	 * @return
	 * @throws IOException
	 */
	public final void attachServletInput(InputStream in) {
		try {
			this.getPassiveConnection().servletInputThreadEntry(in);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	private final void waitPassiveConnection(NetConnectionImpl conn)
			throws Throwable {
		try {
			conn.ensureConnected();
		} catch (Throwable e) {
			conn = null;
			throw e;
		} finally {
			synchronized (this.connectionLock) {
				if (conn == null) {
					this.setConnStateNoSync(ConnectionState.FAIL);
				} else {
					if (conn != this.passiveConn) {
						throw new IllegalStateException();
					}
					this.conn = conn;
					this.conn.start();
					this.setConnStateNoSync(ConnectionState.STABLE);
				}
				this.passiveConn = null;
				this.activeConn = null;
			}
		}
	}

	/**
	 * 根据NodeID判断哪个连接优先级高
	 * 
	 * @return
	 */
	private final boolean useActiveChannel() {
		int i = this.remoteNodeID.compareTo(this.owner.application.localNodeID);
		if (i > 0) {
			return false;
		} else if (i < 0) {
			return true;
		}
		throw new UnsupportedOperationException("不能建立到自身的连接");
	}

	/**
	 * 发起主动连接或接受被动连接，并等待连接成功或者抛出异常
	 */
	public final void setURL(URL address) {
		synchronized (this.connectionLock) {
			this.url = address;
		}
	}

	/**
	 * 断开连接，并设置连接状态为NONE
	 */
	private final void close() {
		NetConnectionImpl conn;
		synchronized (this.connectionLock) {
			if (this.connectionState != ConnectionState.CLOSE_WAIT) {
				return;
			}
			conn = this.conn;
		}
		conn.disconnect();
		synchronized (this.connectionLock) {
			if (this.connectionState != ConnectionState.CLOSE_WAIT) {
				return;
			}
			this.conn = null;
			this.setConnStateNoSync(ConnectionState.NONE);
		}
		synchronized (this.outLock) {
			// 再次检查是否有任务在等待
			if (this.waitingAckFragments.isEmpty()
					&& this.sendingPackages.isEmpty()) {
				synchronized (this.inLock) {
					if (this.receivingPackages.isEmpty()) {
						return;
					}
				}
			}
		}
		// 重新连接
		this.connectAndStart();
	}

	final void onConnectionClose(NetConnectionImpl conn) {
		synchronized (this.connectionLock) {
			if (this.conn != conn) {
				return;
			}
			switch (this.connectionState) {
			case STABLE:
				this.conn = null;
				this.setConnStateNoSync(ConnectionState.FAIL);
				break;
			case CLOSE_WAIT:
				this.conn = null;
				this.setConnStateNoSync(ConnectionState.NONE);
			default:
				return;
			}
		}
		DebugHelper.strike("unuseChannel " + this.remoteNodeID);
		this.owner.unuseChannel(this);
		this.reset();
	}

	final void reset() {
		final ArrayList<AsyncIOStub<?>> arr = new ArrayList<AsyncIOStub<?>>();
		synchronized (this.outLock) {
			this.sendingPackages
					.visitAll(new ValueVisitor<NetPackageSendingEntry<?>>() {
						public void visit(int key,
								NetPackageSendingEntry<?> value) {
							arr.add(value);
						}
					});
			this.waitingAckFragments.clear();
			this.waitingBuildingPackages.clear();
			this.waitingSendingFragments.clear();
			this.sendingPackages.clear();
		}
		synchronized (this.inLock) {
			this.receivingPackages
					.visitAll(new ValueVisitor<NetPackageReceivingEntry<?>>() {
						public void visit(int key,
								NetPackageReceivingEntry<?> value) {
							arr.add(value);
						}
					});
			this.receivingPackages.clear();
		}
		for (AsyncIOStub<?> stub : arr) {
			try {
				stub.cancel();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 回声消息
	 */
	protected final IAckRequiredFragmentBuilder echoBuilder = new IAckRequiredFragmentBuilder() {
		public DataFragment build() {
			DataFragment fragment = NetChannelImpl.this.allocDataFragment(1);
			fragment.writeByte(CTRL_FLAG_ECHO);
			return fragment;
		}
	};

	/**
	 * 发送resend消息
	 */
	protected final IAckRequiredFragmentBuilder resendBuilder() {
		final ArrayList<NetPackageReceivingEntry<?>> arr = new ArrayList<NetPackageReceivingEntry<?>>();
		synchronized (this.inLock) {
			this.receivingPackages
					.visitAll(new ValueVisitor<NetPackageReceivingEntry<?>>() {
						public void visit(int key,
								NetPackageReceivingEntry<?> value) {
							arr.add(value);
						}
					});
		}
		for (int i = arr.size() - 1; i >= 0; i--) {
			if (arr.get(i).isReceivingComplete()) {
				arr.remove(i);
			}
		}
		if (!arr.isEmpty()) {
			synchronized (this.inLock) {
				for (NetPackageReceivingEntry<?> rpe : arr) {
					this.receivingPackages.remove(rpe.packageID);
					rpe.breakResolve();
				}
			}
			return new IAckRequiredFragmentBuilder() {
				public DataFragment build() {
					DataFragment fragment = NetChannelImpl.this
							.allocDataFragment(5 + 4 * arr.size());
					fragment.writeByte(CTRL_FLAG_RESEND_PACKAGE);
					fragment.writeInt(conn.generation);
					for (NetPackageReceivingEntry<?> rpe : arr) {
						fragment.writeInt(rpe.packageID);
					}
					return fragment;
				}
			};
		}
		return null;
	}

	// /////////////////////////////////////////////////
	// //////////////构造线程相关////////////////////////

	public final <TAttachment> AsyncIOStub<TAttachment> startSendingPackage(
			DataFragmentBuilder<? super TAttachment> builder,
			TAttachment attachment) {
		if (builder == null) {
			throw new NullArgumentException("builder");
		}
		// 确保连接有效
		this.connectAndStart();
		final NetPackageSendingEntry<TAttachment> spe = new NetPackageSendingEntry<TAttachment>(
				this, builder, attachment);
		synchronized (this.outLock) {
			this.sendingPackages.put(spe.packageID, spe);
			this.tryStartFragmentBuildNoSync(spe);
		}
		return spe;
	}

	final void tryStartFragmentBuild(NetPackageSendingEntry<?> newOne) {
		synchronized (this.outLock) {
			this.tryStartFragmentBuildNoSync(newOne);
		}
	}

	protected final void tryStartFragmentBuildNoSync(
			NetPackageSendingEntry<?> newOne) {
		if (newOne != null) {
			newOne.setState(NetPackageSendingEntry.STATE_QUEUING);
			this.waitingBuildingPackages.add(newOne);
		}
		for (int needStartBuildCount = this.waitingSendingQueueMaxSize
				- this.waitingSendingFragments.size()
				- this.buildingFragmentCount; needStartBuildCount > 0; needStartBuildCount--) {
			// 需要起动fragment构造
			final NetPackageSendingEntry<?> one = this.waitingBuildingPackages
					.poll();
			if (one != null) {
				one.setState(NetPackageSendingEntry.STATE_QUEUING);
				this.owner.offerFragmentBuild(one);
				this.buildingFragmentCount++;
			} else {
				break;
			}
		}
	}

	// //////////////构造线程相关////////////////////////
	// /////////////////////////////////////////////////

	// /////////////////////////////////////////////////
	// //////////////发送线程相关////////////////////////

	/**
	 * 发送接收到控制消息后回复的确认信息
	 */
	final void postAckCtrl(int ackID) {
		DataFragment fragment = this.allocDataFragment(5);
		fragment.writeByte(CTRL_FLAG_ACK);
		fragment.writeInt(ackID);
		this.postDataFragmentToSend(null, fragment);
	}

	/**
	 * 发送中断接收数据包控制信息，不需要回复确认
	 */
	final void postBreakReceivePackageCtrl(int packageID) {
		// 将包移出发送队列
		synchronized (this.outLock) {
			this.sendingPackages.remove(packageID);
		}
		DataFragment fragment = this.allocDataFragment(5);
		fragment.writeByte(CTRL_FLAG_BREAK_PACKAGE_RECEIVE);
		fragment.writeInt(packageID);
		this.postDataFragmentToSend(null, fragment);
	}

	/**
	 * 发送中断发送数据包控制信息，需要回复确认
	 */
	final void postBreakSendPackageCtrl(final int packageID) {
		this.postAckReqiredFragmentToSend(new IAckRequiredFragmentBuilder() {
			public DataFragment build() {
				DataFragment fragment = NetChannelImpl.this
						.allocDataFragment(5);
				fragment.writeByte(CTRL_FLAG_BREAK_PACKAGE_SEND);
				fragment.writeInt(packageID);
				return fragment;
			}
		});
	}

	final void packageResolved(final int packageID) {
		synchronized (this.inLock) {
			this.receivingPackages.remove(packageID);
		}
		// 发送数据包还原完毕控制信息，需要回复确认
		this.postAckReqiredFragmentToSend(new IAckRequiredFragmentBuilder() {
			public DataFragment build() {
				DataFragment fragment = NetChannelImpl.this
						.allocDataFragment(5);
				fragment.writeByte(CTRL_FLAG_PACKAGE_RESOLVED);
				fragment.writeInt(packageID);
				return fragment;
			}
		});
	}

	/**
	 * 构造并投递需要ack的片段
	 * 
	 * @param builder
	 * @param otherBuilders
	 */
	final void postAckReqiredFragmentToSend(
			IAckRequiredFragmentBuilder builder,
			IAckRequiredFragmentBuilder... otherBuilders) {
		synchronized (this.outLock) {
			int ackID = this.ackSeed++;
			DataFragment fragment = builder.build();
			fragment.writeInt(ackID);
			fragment.limit(fragment.getPosition());
			// 放入ack队列
			this.waitingAckFragments.offer(new AckRequiredFragmentStub(ackID,
					builder));
			DebugHelper.trace("post ", fragment);
			// 放入发送对列
			this.waitingSendingFragments.add(fragment);
			for (IAckRequiredFragmentBuilder b : otherBuilders) {
				ackID = this.ackSeed++;
				fragment = b.build();
				fragment.writeInt(ackID);
				fragment.limit(fragment.getPosition());
				// 放入ack队列
				this.waitingAckFragments.offer(new AckRequiredFragmentStub(
						ackID, builder));
				DebugHelper.trace("post ", fragment);
				// 放入发送对列
				this.waitingSendingFragments.add(fragment);
			}
			// 通知发送线程启动
			this.outLock.notifyAll();
		}
	}

	/**
	 * 构造好Fragment后投递到发送线程
	 * 
	 * @param asyncStub
	 * @param fragment
	 */
	final void postDataFragmentToSend(NetPackageSendingEntry<?> asyncStub,
			DataFragment fragment) {
		POST: {
			synchronized (this.outLock) {
				if (asyncStub != null) {
					this.buildingFragmentCount--;
					if (asyncStub.needResetPackage(this.conn.generation)) {
						break POST;
					}
					switch (asyncStub.getState()) {
					case NetPackageSendingEntry.STATE_BUILDING_AND_SENDING:
						// 尝试再次启动构造过程
						this.tryStartFragmentBuildNoSync(asyncStub);
						break;
					case NetPackageSendingEntry.STATE_BUILDING_COMPLETE:
						asyncStub
								.setState(NetPackageSendingEntry.STATE_WAITING_RESOLVE);
						break;
					}
				}
				fragment.limit(fragment.getPosition());
				this.waitingSendingFragments.add(fragment);
				// 通知发送线程启动
				this.outLock.notifyAll();
				DebugHelper.trace("post ", fragment);
			}
			return;
		}
		DebugHelper.fault("reset package " + asyncStub.packageID);
		asyncStub.tryResetPackage();
	}

	protected final void sendThreadRun(OutputStream out) throws Throwable {
		// 发送echo
		this.postAckReqiredFragmentToSend(this.echoBuilder);
		DataFragment toSend;
		for (;;) {
			SEND: {
				synchronized (this.outLock) {
					toSend = this.waitingSendingFragments.poll();
					// 启动新构造线程
					this.tryStartFragmentBuildNoSync(null);
					if (toSend == null) {
						// 发送完成，进入空闲状态
						// 更新时间戳
						this.lastActiveTime = System.currentTimeMillis();
						break SEND;
					}
				}
				this.conn.send(out, toSend);
				continue;
			}
			this.doIdle(out);
		}
	}

	private final void doIdle(OutputStream out) throws Throwable {
		DataFragment toSend;
		for (;;) {
			synchronized (this.outLock) {
				if (!this.waitingSendingFragments.isEmpty()
						|| this.waitingBuildingPackages.size() > 0) {
					// 有片段可发送，进入发送状态
					return;
				}
				// 等待
				this.outLock.wait(KEEP_ALIVE_TIMING);
				if (!this.waitingSendingFragments.isEmpty()
						|| this.waitingBuildingPackages.size() > 0) {
					// 有片段可发送，进入发送状态
					return;
				}
				// 检查状态：
				// 1.超时：锁定连接，发送close
				// 2.未超时：发送keep-alive，
				// a)空闲：如果keepAlive则更新时间戳
				// b)非空闲：更新时间戳
				KEEP_ALIVE: {
					switch (this.detectWorkingThreadStateNoSync()) {
					case IDLE:
						if (!this.keepAlive) {
							break;
						}
					case WORKING:
						// 更新时间戳
						this.lastActiveTime = System.currentTimeMillis();
						break;
					case TIMEOUT:
						synchronized (this.connectionLock) {
							if (this.connectionState == ConnectionState.CLOSE_WAIT) {
								// 发送keep-alive
								break;
							} else {
								switch (this.connectionState) {
								case STABLE:
									break;
								default:
									throw new IllegalStateException("错误状态："
											+ this.connectionState);
								}
							}
							// 锁定发送线程
							this.setConnStateNoSync(ConnectionState.CLOSE_WAIT);
							this.conn.shutdown(true);
						}
						DebugHelper.strike("idle timeout");
						// 发送close消息
						toSend = allocDataFragment(5);
						toSend.writeByte(CTRL_FLAG_CLOSE);
						toSend.limit(toSend.getPosition());
						DebugHelper.trace("post ", toSend);
						break KEEP_ALIVE;
					}
					// 发送keep-alive
					toSend = this.allocDataFragment(1);
					toSend.writeByte(CTRL_FLAG_KEEP_ALIVE);
					toSend.limit(toSend.getPosition());
				}
			}
			this.conn.send(out, toSend);
		}
	}

	private final WorkingThreadState detectWorkingThreadStateNoSync() {
		if (this.waitingAckFragments.isEmpty()
				&& this.sendingPackages.isEmpty()) {
			synchronized (this.inLock) {
				if (this.receivingPackages.isEmpty()) {
					// 判断超时
					if (System.currentTimeMillis() - this.lastActiveTime > IDLE_TIMEOUT) {
						// 超时
						return WorkingThreadState.TIMEOUT;
					}
					// 空闲
					return WorkingThreadState.IDLE;
				}
			}
		}
		return WorkingThreadState.WORKING;
	}

	// //////////////发送线程相关////////////////////////
	// /////////////////////////////////////////////////

	// /////////////////////////////////////////////////
	// //////////////接收线程相关////////////////////////

	/**
	 * 处理数据包片断
	 */
	private final void onPackageFragment(byte ctrlFlag, DataFragment received)
			throws InterruptedException {
		final int packageID = received.readInt();
		NetPackageReceivingEntry<?> rpe;
		synchronized (this.inLock) {
			rpe = this.receivingPackages.get(packageID);
		}
		if (rpe != null && rpe.receiverGeneration != this.conn.generation) {
			// 连接重置，重新接收数据包
			synchronized (this.inLock) {
				this.receivingPackages.remove(packageID);
			}
			rpe.cancel();
			rpe = null;
		}
		if (rpe == null) {
			if ((ctrlFlag & CTRL_FLAG_PACKAGE_FIRST) == 0) {
				// 丢弃掉无主的片断，说明是中断接收的片段
				this.releaseDataFragment(received);
				DebugHelper.fault("drop " + packageID);
				this.postBreakSendPackageCtrl(packageID);
				return;
			}
			// 新数据包第一个片断
			rpe = new NetPackageReceivingEntry<Object>(this, packageID);
			rpe.receiverGeneration = this.conn.generation;
			this.owner.offerPackageReceiving(rpe, received);
			if (!rpe.resolverValid()) {
				DebugHelper.fault("refuse package " + packageID);
				// 数据包没有被接受
				this.releaseDataFragment(received);
				this.postBreakSendPackageCtrl(packageID);
				return;
			}
			synchronized (this.inLock) {
				this.receivingPackages.put(packageID, rpe);
			}
		}
		// 添加到resolve队列
		rpe.queueToResolve(received, (ctrlFlag & CTRL_FLAG_PACKAGE_LAST) != 0);
	}

	private final void onBreakPackageReceiveFragment(DataFragment received)
			throws InterruptedException {
		this.breakReceive(received.readInt());
	}

	private final void onBreakPackageSendFragment(DataFragment received) {
		final int packageID = received.readInt();
		final int ackID = received.readInt();
		try {
			this.breakSend(packageID);
		} finally {
			this.postAckCtrl(ackID);
		}
	}

	/**
	 * 包接受完毕，接受方还原完毕某包后回发
	 * 
	 * @param received
	 */
	private final void onPackageResolvedFragment(DataFragment received) {
		final int packageID = received.readInt();
		final int ackID = received.readInt();
		NetPackageSendingEntry<?> spe = null;
		try {
			synchronized (this.outLock) {
				spe = this.sendingPackages.remove(packageID);
				if (spe == null) {
					return;
				}
			}
			spe.setResolved(true);
		} finally {
			this.postAckCtrl(ackID);
		}
	}

	private final void onResendPackageFragment(DataFragment received) {
		int start = received.getPosition();
		received.setPosition(start + received.remain() - 4);
		int ackID = received.readInt();
		try {
			received.setPosition(start);
			int senderGeneration = received.readInt();
			while (received.remain() > 4) {
				final int packageID = received.readInt();
				NetPackageSendingEntry<?> spe = null;
				synchronized (this.outLock) {
					spe = this.sendingPackages.remove(packageID);
					if (spe == null
							|| spe.getState() != NetPackageSendingEntry.STATE_WAITING_RESOLVE
							|| !spe.needResetPackage(senderGeneration)) {
						continue;
					}
				}
				// XXX 最好在别的线程中去做
				spe.tryResetPackage();
			}
		} finally {
			this.postAckCtrl(ackID);
		}
	}

	private final void onCloseFragment(byte ctrlFlag, DataFragment received) {
		this.commitAck(received.readInt());
		switch (ctrlFlag & CTRL_FLAG_SUBTYPE_MASK) {
		case 0:
			CANCEL: {
				synchronized (this.outLock) {
					boolean idle = false;
					if (this.sendingPackages.isEmpty()
							&& this.waitingAckFragments.isEmpty()) {
						synchronized (this.inLock) {
							if (this.receivingPackages.isEmpty()) {
								idle = true;
							}
						}
					}
					synchronized (this.connectionLock) {
						switch (this.connectionState) {
						case STABLE:
							if (!idle) {
								break;
							}
							this.setConnStateNoSync(ConnectionState.CLOSE_WAIT);
							this.conn.shutdown(true);
						default:
							break CANCEL;
						}
					}
				}
				// 取消断开
				DataFragment fragment = this.allocDataFragment(1);
				fragment
						.writeByte((byte) (CTRL_FLAG_CLOSE | CTRL_FLAG_CLOSE_CANCEL));
				this.postDataFragmentToSend(null, fragment);
				break;
			}
			// 确认断开，由于断开连接动作需要等待发送线程退出，所以要在其他线程中执行
			this.owner.application.overlappedManager.startWork(new Work() {
				@Override
				protected void doWork(WorkingThread thread) throws Throwable {
					close();
				}
			});
			break;
		case CTRL_FLAG_CLOSE_CANCEL:
			// 解锁
			synchronized (this.connectionLock) {
				switch (this.connectionState) {
				case CLOSE_WAIT:
					this.setConnStateNoSync(ConnectionState.STABLE);
				default:
					this.conn.shutdown(false);
					break;
				}
			}
			break;
		}
	}

	private final void onEchoFragment(DataFragment received) {
		this.postAckCtrl(received.readInt());
	}

	private final void onAckFragment(DataFragment received) {
		this.commitAck(received.readInt());
	}

	/**
	 * 校验ack，并从ack队列中移除
	 * 
	 * @param ackID
	 */
	private final void commitAck(int ackID) {
		ArrayList<IAckRequiredFragmentBuilder> arr = null;
		synchronized (this.outLock) {
			for (;;) {
				AckRequiredFragmentStub stub = this.waitingAckFragments.peek();
				if (stub == null) {
					break;
				}
				if (stub.ackID < ackID) {
					// 移除
					if (arr == null) {
						arr = new ArrayList<IAckRequiredFragmentBuilder>();
					}
					arr.add(this.waitingAckFragments.poll().builder);
					DebugHelper.fault("resend ctrl [ack " + stub.ackID + "]");
					continue;
				} else if (stub.ackID == ackID) {
					// 移除
					this.waitingAckFragments.poll();
				}
				break;
			}
		}
		if (arr != null) {
			if (arr.size() > 1) {
				int c = arr.size();
				IAckRequiredFragmentBuilder[] otherBuilders = new IAckRequiredFragmentBuilder[c - 1];
				for (int i = 1; i < c; i++) {
					otherBuilders[i - 1] = arr.get(i);
				}
				this.postAckReqiredFragmentToSend(arr.get(0), otherBuilders);
			} else {
				this.postAckReqiredFragmentToSend(arr.get(0));
			}
		}
	}

	final void breakReceive(int packageID) {
		DebugHelper.fault("break receive " + packageID);
		final NetPackageReceivingEntry<?> rpe;
		synchronized (this.inLock) {
			rpe = this.receivingPackages.remove(packageID);
		}
		if (rpe != null) {
			rpe.breakResolve();
		}
	}

	final void breakSend(int packageID) {
		DebugHelper.fault("break send " + packageID);
		NetPackageSendingEntry<?> spe;
		synchronized (this.outLock) {
			spe = this.sendingPackages.remove(packageID);
			if (spe == null) {
				return;
			}
			this.waitingBuildingPackages.remove(spe);
		}
		spe.setResolved(false);
	}

	/**
	 * 处理接收的片段，并返回缓冲区可否重用
	 * 
	 * @param received
	 * @return
	 * @throws Throwable
	 */
	protected final boolean dispatchFragment(DataFragment received)
			throws Throwable {
		DebugHelper.trace("receive ", received);
		final byte ctrlFlag = received.readByte();
		switch (ctrlFlag & CTRL_FLAG_TYPE_MASK) {
		case CTRL_FLAG_PACKAGE:
			this.onPackageFragment(ctrlFlag, received);
			break;
		case CTRL_FLAG_BREAK_PACKAGE_RECEIVE:
			this.onBreakPackageReceiveFragment(received);
			return true;
		case CTRL_FLAG_BREAK_PACKAGE_SEND:
			this.onBreakPackageSendFragment(received);
			return true;
		case CTRL_FLAG_PACKAGE_RESOLVED:
			this.onPackageResolvedFragment(received);
			return true;
		case CTRL_FLAG_RESEND_PACKAGE:
			this.onResendPackageFragment(received);
			return true;
		case CTRL_FLAG_CLOSE:
			this.onCloseFragment(ctrlFlag, received);
			return true;
		case CTRL_FLAG_KEEP_ALIVE:
			// do nothing
			return true;
		case CTRL_FLAG_ECHO:
			this.onEchoFragment(received);
			return true;
		case CTRL_FLAG_ACK:
			this.onAckFragment(received);
			return true;
		default:
			throw new IllegalStateException("无法识别的数据类型");
		}
		return false;
	}

	/**
	 * 发送线程提取片断来发送<br>
	 * 返回需要发送的片断，<br>
	 * 如果返回null表示没有需要发送的片断，<br>
	 * 此时发送线程可以回归池中
	 * 
	 * @return 返回需要发送的片断或null
	 */
	final void receiveThreadRun(InputStream in) throws Throwable {
		// 发送echo和resend
		IAckRequiredFragmentBuilder builder = this.resendBuilder();
		if (builder != null) {
			this.postAckReqiredFragmentToSend(this.echoBuilder, builder);
		} else {
			this.postAckReqiredFragmentToSend(this.echoBuilder);
		}
		for (;;) {
			DataFragment received = this.allocDataFragment();
			for (;;) {
				received = this.conn.receive(in, received);
				if (!this.dispatchFragment(received)) {
					break;
				}
			}
		}
	}

	// //////////////接收线程相关////////////////////////
	// /////////////////////////////////////////////////
}