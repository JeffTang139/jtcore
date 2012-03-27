package org.eclipse.jt.core.impl;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.ValueConvertException;

import sun.net.www.MessageHeader;


public class NetChannelManagerImpl implements NetChannelManager {
	final ApplicationImpl application;
	final long channelVersion;
	private final HashMap<GUID, NetChannelImpl> channels = new HashMap<GUID, NetChannelImpl>();

	NetChannelManagerImpl(ApplicationImpl application) {
		this.application = application;
		this.channelVersion = application.newRECVER();
	}

	private final AtomicInteger packageIDSeed = new AtomicInteger();

	final NetChannelImpl ensureChannel(GUID remoteNodeID) {
		synchronized (this.channels) {
			NetChannelImpl channel = this.channels.get(remoteNodeID);
			if (channel == null) {
				channel = new NetChannelImpl(this, remoteNodeID);
				this.channels.put(remoteNodeID, channel);
			}
			return channel;
		}
	}

	final int newPackageID() {
		return this.packageIDSeed.incrementAndGet();
	}

	private GUID queryRemoteNodeID(URL address) {
		try {
			final MessageHeader mh = new MessageHeader();
			final DnaHttpClient hc = new DnaHttpClient(address,
					this.application.localNodeID);
			try {
				hc.get(mh);
			} finally {
				hc.closeServer();
			}
			String remoteNodeIDStr = mh
					.findValue(DnaHttpClient.HTTP_HEADER_NODE_ID);
			if (remoteNodeIDStr == null) {
				throw new IOException("无法获取远程节点的NodeID");
			}
			try {
				return GUID.valueOf(remoteNodeIDStr);
			} catch (ValueConvertException e) {
				throw new IOException("远程节点的NodeID格式不正确");
			}
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	public NetChannelImpl getChannel(URL address) {
		try {
			GUID remoteNodeID = this.queryRemoteNodeID(address);
			NetChannelImpl channel = this.ensureChannel(remoteNodeID);
			channel.setURL(address);
			channel.connectAndStart();
			return channel;
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	public DataPackageReceiver getNetIOHandler() {
		return this.receiver;
	}

	final int getVersion() {
		return 1;
	}

	final void unuseChannel(NetChannelImpl channel) {
		synchronized (this.channels) {
			if (this.channels.remove(channel.remoteNodeID) == null) {
				return;
			}
		}
		this.receiver.channelDisabled(channel);
	}

	// /////////////////////////////////////////////////
	// //////////////构造线程相关////////////////////////
	private final Queue<NetPackageSendingEntry<?>> waitingBuildingPackages = new LinkedList<NetPackageSendingEntry<?>>();
	/**
	 * 构造线程个数
	 */
	private int buildingThreadCount;
	/**
	 * 构造线程的最大个数
	 */
	private int maxBuildingThreadCount = Runtime.getRuntime()
			.availableProcessors();

	/**
	 * 构造线程的入口
	 * 
	 * @throws Throwable
	 */
	private final void buildThreadRun() throws Throwable {
		for (;;) {
			final NetPackageSendingEntry<?> pkgToBuild;
			synchronized (this.waitingBuildingPackages) {
				if (!this.waitingBuildingPackages.isEmpty()) {
					pkgToBuild = this.waitingBuildingPackages.poll();
				} else {
					this.buildingThreadCount--;
					return;
				}
			}
			try {
				pkgToBuild.buildAndPostFragmentToSend();
			} catch (Throwable e) {
				final int newThreads;
				synchronized (this.waitingBuildingPackages) {
					this.buildingThreadCount--;
					newThreads = this.preNewBuildThreadNoSync();
				}
				this.newBuildThread(newThreads);
				throw e;
			}
		}
	}

	/**
	 * 构造线程工作
	 */
	private final class BuildFragmentWork extends Work {
		@Override
		protected final void doWork(WorkingThread thread) throws Throwable {
			NetChannelManagerImpl.this.buildThreadRun();
		}
	}

	private final int preNewBuildThreadNoSync() {
		final int wbpc = this.waitingBuildingPackages.size();
		if (wbpc == 0) {
			return 0;
		}
		int needThread = this.maxBuildingThreadCount - this.buildingThreadCount;
		if (needThread == 0) {
			return 0;
		}
		if (needThread > wbpc) {
			needThread = wbpc;
		}
		this.buildingThreadCount += needThread;
		return needThread;
	}

	private final void newBuildThread(int count) {
		while (count-- > 0) {
			this.application.overlappedManager
					.startWork(new BuildFragmentWork());
		}
	}

	private final void newResolveThread(int count) {
		while (count-- > 0) {
			this.application.overlappedManager
					.startWork(new ResolveFragmentWork());
		}
	}

	/**
	 * 将包放入构造列队并启动构造线程
	 * 
	 * @param pkgToBuild
	 */
	final void offerFragmentBuild(NetPackageSendingEntry<?> pkgToBuild) {
		final int newThreads;
		synchronized (this.waitingBuildingPackages) {
			this.waitingBuildingPackages.offer(pkgToBuild);
			newThreads = this.preNewBuildThreadNoSync();
		}
		this.newBuildThread(newThreads);
	}

	// //////////////构造线程相关////////////////////////
	// /////////////////////////////////////////////////
	// /////////////////////////////////////////////////
	// //////////////还原线程相关////////////////////////
	private DataPackageReceiver receiver;
	/**
	 * 等待还原的包
	 */
	private final Queue<NetPackageReceivingEntry<?>> waitingResolvingPackages = new ConcurrentLinkedQueue<NetPackageReceivingEntry<?>>();
	/**
	 * 最大等待还原包的个数
	 */
	private int maxWaitingResolvingPackageCount = 512;
	/**
	 * 还原线程个数
	 */
	private int resolvingThreadCount;
	/**
	 * 还原线程的最大个数
	 */
	private int maxResolvingThreadCount = Runtime.getRuntime()
			.availableProcessors();

	private final int preNewResolveThreadNoSync() {
		final int wbpc = this.waitingResolvingPackages.size();
		if (wbpc == 0) {
			return 0;
		}
		int needThread = this.maxResolvingThreadCount
				- this.resolvingThreadCount;
		if (needThread == 0) {
			return 0;
		}
		if (needThread > wbpc) {
			needThread = wbpc;
		}
		this.resolvingThreadCount += needThread;
		return needThread;
	}

	public final DataPackageReceiver setNetIOHandler(
			DataPackageReceiver receiver) {
		final DataPackageReceiver old = this.receiver;
		this.receiver = receiver;
		return old;
	}

	final void offerFragmentResolve(NetPackageReceivingEntry<?> rpe)
			throws InterruptedException {
		final int newThreads;
		synchronized (this.waitingResolvingPackages) {
			while (this.waitingResolvingPackages.size() >= this.maxWaitingResolvingPackageCount) {
				this.waitingResolvingPackages.wait();
			}
			this.waitingResolvingPackages.offer(rpe);
			newThreads = this.preNewResolveThreadNoSync();
		}
		this.newResolveThread(newThreads);
	}

	/**
	 * 构造线程的入口
	 * 
	 * @throws Throwable
	 */
	private final void resolveThreadRun() throws Throwable {
		for (;;) {
			final NetPackageReceivingEntry<?> pkgToResolve;
			synchronized (this.waitingResolvingPackages) {
				if (!this.waitingResolvingPackages.isEmpty()) {
					pkgToResolve = this.waitingResolvingPackages.poll();
				} else {
					this.resolvingThreadCount--;
					return;
				}
			}
			try {
				pkgToResolve.resolveDataFragment();
			} catch (Throwable e) {
				final int newThreads;
				synchronized (this.waitingResolvingPackages) {
					this.resolvingThreadCount--;
					newThreads = this.preNewResolveThreadNoSync();
				}
				this.newResolveThread(newThreads);
				throw e;
			}
		}
	}

	private final class ResolveFragmentWork extends Work {
		@Override
		protected final void doWork(WorkingThread thread) throws Throwable {
			NetChannelManagerImpl.this.resolveThreadRun();
		}
	}

	final void offerPackageReceiving(NetPackageReceivingEntry<?> rpe,
			DataInputFragment fragment) {
		final DataPackageReceiver receiver = this.receiver;
		if (receiver != null) {
			try {
				receiver.packageArriving(rpe.channel, fragment, rpe);
			} catch (Throwable e) {
				rpe.startReceivingPackage(null, null);
			}
		}
	}
	// //////////////还原线程相关////////////////////////
	// /////////////////////////////////////////////////
}
