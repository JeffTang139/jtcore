package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.obja.StructClass;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.impl.DataPackageReceiver.NetPackageReceivingStarter;
import org.eclipse.jt.core.impl.NSerializer.NSerializerFactory;
import org.eclipse.jt.core.impl.NUnserializer.ObjectTypeQuerier;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.ObjectDataType;


/**
 * 网络节点
 * 
 * @author Jeff Tang
 * 
 */
public class NetNodeImpl implements NSerializerFactoryProvider {
	private NetNodeImpl nextInCluster;

	final NetNodeImpl getNextNodeInCluster() {
		return this.nextInCluster;
	}

	final void setNextNodeInCluster(NetNodeImpl node) {
		this.nextInCluster = node;
	}

	//
	// /**
	// * 两阶段建立连接的第一阶段，使用主动连接建立连接，返回非null值表示已经存在通道
	// */
	// final NetChannel initiativeChannelConnecting(NetChannel channel) {
	// synchronized (this) {
	// switch (this.state) {
	// case STATE_PASSIVE_CHANNEL_HALF:
	// // 出现冲突，比较
	// if (!this.highPRIWithRemote()) {
	// try {// 本地优先级低，等待被动连接
	// this.wait();
	// } catch (InterruptedException e) {
	// throw Utils.tryThrowException(e);
	// }
	// if (this.state == STATE_INITIATIVE_CHANNEL) {
	// // 被动连接建立成功
	// return this.channel;
	// }
	// // 被动连接建立失败
	// }
	// case STATE_NO_CHANNEL:
	// this.state = STATE_INITIATIVE_CHANNEL_HALF;
	// this.channel = channel;
	// return null;
	// default:
	// throw new IllegalStateException("错误的状态：" + this.state);
	// }
	// }
	// }
	//
	// /**
	// * 两阶段建立连接的第二阶段，返回可用的Channel或者null表示需要重新建立
	// */
	// final NetChannel finishInitiativeChannel() {
	// synchronized (this) {
	// switch (this.state) {
	// case STATE_INITIATIVE_CHANNEL_HALF:
	// this.state = STATE_INITIATIVE_CHANNEL;
	// return this.channel;
	// case STATE_PASSIVE_CHANNEL_HALF:
	// try {
	// this.wait();
	// } catch (InterruptedException e) {
	// throw Utils.tryThrowException(e);
	// }
	// switch (this.state) {
	// case STATE_NO_CHANNEL:
	// // 被动连接建立失败，需要重试验
	// case STATE_PASSIVE_CHANNEL:
	// return this.channel;
	// }
	// // 否则出错
	// default:
	// throw new IllegalStateException("错误的状态：" + this.state);
	// }
	// }
	// }
	//
	// /**
	// * 两阶段建立连接的第一阶段，使用主动连接建立连接
	// */
	// final boolean passiveChannelConnecting(NetChannel channel) {
	// synchronized (this) {
	// switch (this.state) {
	// case STATE_INITIATIVE_CHANNEL_HALF:
	// // 出现冲突，比较
	// if (!this.highPRIWithRemote()) {
	// return false;
	// }
	// case STATE_NO_CHANNEL:
	// this.state = STATE_PASSIVE_CHANNEL_HALF;
	// this.channel = channel;
	// return true;
	// default:
	// throw new IllegalStateException("错误的状态：" + this.state);
	// }
	// }
	// }
	//
	// /**
	// * 两阶段建立连接的第二阶段，返回可用的Channel或者null表示需要重新建立
	// */
	// final void passiveChannelConnected() {
	// synchronized (this) {
	// switch (this.state) {
	// case STATE_PASSIVE_CHANNEL_HALF:
	// this.state = STATE_PASSIVE_CHANNEL;
	// this.notifyAll();
	// default:
	// throw new IllegalStateException("错误的状态：" + this.state);
	// }
	// }
	// }
	//
	// /**
	// * 关联注定连接
	// *
	// * @param channel
	// */
	// final void attachChannel1(NetChannel channel, boolean startOrFinish) {
	// synchronized (this) {
	// switch (this.state) {
	// case STATE_NO_CHANNEL:
	// if (startOrFinish) {
	// this.state = STATE_INITIATIVE_CHANNEL_HALF;
	// } else {
	//
	// }
	// }
	// }
	// }

	/**
	 * 管理器
	 */
	final NetNodeManagerImpl owner;
	/**
	 * 所属集群
	 */
	final NetClusterImpl cluster;
	/**
	 * 对应的网络通道
	 */
	final NetChannelImpl channel;

	/**
	 * 向远程序列化数据的序列化器工厂
	 */
	private final NSerializerFactory serializerFactory;

	public final NSerializerFactory getNSerializerFactory() {
		return this.serializerFactory;
	}

	public NetNodeManagerImpl getOwner() {
		return this.owner;
	}

	public NetNodeImpl(NetNodeManagerImpl owner, NetClusterImpl cluster,
			NetChannelImpl netChannel, NetNodeImpl nextInCluster) {
		if (netChannel == null) {
			throw new NullArgumentException("netChannel");
		}
		if (cluster == null) {
			throw new NullArgumentException("cluster");
		}
		if (owner == null) {
			throw new NullArgumentException("owner");
		}
		this.serializerFactory = NSerializer
				.getRemoteCompatibleFactory(netChannel
						.getRemoteSerializeVersion());
		this.channel = netChannel;
		this.cluster = cluster;
		this.owner = owner;
		this.nextInCluster = nextInCluster;
	}

	// //////////////////////////////////////////
	// //// 本地请求有关/////////////////////////
	/**
	 * 创建与该节点间的网络会话
	 */
	public final NetSessionImpl newSession() {
		return new NetSessionImpl(this);
	}

	/**
	 * 发向远程的请求
	 */
	private final IntKeyMap<NetRequestImpl> requestsToRemote = new IntKeyMap<NetRequestImpl>();

	final void registerLocalRequest(NetRequestImpl request) {
		synchronized (this.requestsToRemote) {
			this.requestsToRemote.put(request.requestID, request);
		}
	}

	final NetRequestImpl unRegisterLocalRequest(int requestID) {
		synchronized (this.requestsToRemote) {
			return this.requestsToRemote.remove(requestID);
		}
	}

	// //// 本地请求有关/////////////////////////
	// //////////////////////////////////////////

	// //////////////////////////////////////////
	// //// 远程请求有关/////////////////////////
	/**
	 * 来自远程的请求
	 */
	private final IntKeyMap<RemoteRequestImpl> requestsFromRemote = new IntKeyMap<RemoteRequestImpl>();

	final RemoteRequestImpl unRegisterRemoteRequest(int requestID) {
		synchronized (this.requestsFromRemote) {
			return this.requestsFromRemote.remove(requestID);
		}
	}

	final void onRequestPackageArriving(DataInputFragment fragment,
			NetPackageReceivingStarter starter) {
		final byte requestPackageType = fragment.readByte();
		final int requestID = fragment.readInt();
		switch (requestPackageType) {
		case NetRequestImpl.REQUEST_PACKAGE_TYPE_POST:
			final RemoteRequestImpl remoteRequest = new RemoteRequestImpl(this,
					requestID);
			synchronized (this.requestsFromRemote) {
				RemoteRequestImpl oldRemoteRequest = this.requestsFromRemote
						.put(requestID, remoteRequest);
				if (oldRemoteRequest != null) {
					// 出现了重复ID的请求，忽略旧请求
					this.requestsFromRemote.put(requestID, oldRemoteRequest);
					return;
				}
			}
			remoteRequest.startReceiveData(starter, requestPackageType);
			break;
		case NetRequestImpl.REQUEST_PACKAGE_TYPE_CANCEL:
			final RemoteRequestImpl existsRemoteRequest;
			synchronized (this.requestsFromRemote) {
				existsRemoteRequest = this.requestsFromRemote.get(requestID);
			}
			if (existsRemoteRequest != null) {
				existsRemoteRequest.startReceiveData(starter,
						requestPackageType);
			}
			break;
		case NetRequestImpl.REQUEST_PACKAGE_TYPE_INFO:
		case NetRequestImpl.REQUEST_PACKAGE_TYPE_EXCEPTION:
		case NetRequestImpl.REQUEST_PACKAGE_TYPE_RESPONSE:
		case NetRequestImpl.REQUEST_PACKAGE_TYPE_CANCELED:
			final NetRequestImpl request;
			synchronized (this.requestsToRemote) {
				request = this.requestsToRemote.get(requestID);
			}
			if (request != null
					&& request.session.netNode.channel == this.channel) {
				request.startReceivingRespose(starter, requestPackageType);
			}
			break;
		}
	}

	// //// 远程请求有关/////////////////////////
	// //////////////////////////////////////////
	// //////////////////////////////////////////
	// //// 类型请求有关/////////////////////////

	final static byte TYPE_PACKAGE = 0x02;

	@StructClass
	static class RemoteTypeQuery {
		final GUID typeID;
		final int requestID;
		final boolean remoteRequest;
		final boolean simpleQuery;

		RemoteTypeQuery(GUID typeID, boolean simpleQuery, int requestID,
				boolean remoteRequest) {
			this.typeID = typeID;
			this.simpleQuery = simpleQuery;
			this.requestID = requestID;
			this.remoteRequest = remoteRequest;
		}

		RemoteTypeQuery(RemoteTypeQueryResult sample, boolean simpleQuery) {
			this.simpleQuery = simpleQuery;
			this.typeID = sample.typeID;
			this.requestID = sample.requestID;
			this.remoteRequest = sample.remoteRequest;
		}
	}

	/**
	 * 简单类型结果
	 * 
	 * @author Jeff Tang
	 * 
	 */
	@StructClass
	private static class RemoteTypeQueryResult {
		final GUID typeID;
		/**
		 * 对应的Java类类型，如果找不到则返回null
		 */
		private final String className;
		final Object typeInfo;// SerializationStructInfo，SerializationEnumInfo
		final int requestID;
		final boolean remoteRequest;

		public final String getClassName() {
			if (this.className != null) {
				final int i = this.className.indexOf('@');
				if (i >= 0) {
					return this.className.substring(0, i);
				}
			}
			return this.className;
		}

		RemoteTypeQueryResult(RemoteTypeQuery query, ObjectDataType dataType) {
			if (dataType != null && query.simpleQuery) {// 暂时只支持简单类型请求
				this.className = dataType.getJavaClass().getName();
				this.typeInfo = null;
			} else {
				this.className = null;
				this.typeInfo = null;
			}
			this.typeID = query.typeID;
			this.requestID = query.requestID;
			this.remoteRequest = query.remoteRequest;
		}
	}

	/**
	 * 类型请求和结果包还原器
	 * 
	 * @author Jeff Tang
	 * 
	 */
	private static final class TypePackageResolver extends
			SerializedDataResolver<NetNodeImpl> {
		TypePackageResolver() {
			super(ObjectTypeQuerier.staticObjectTypeQuerier);
		}

		@Override
		protected void finishUnserialze(Object unserialzedObject,
				NetNodeImpl attachment) {
			final Class<?> oc = unserialzedObject.getClass();
			if (oc == RemoteTypeQuery.class) {
				// 查询类型请求
				final RemoteTypeQuery rtq = (RemoteTypeQuery) unserialzedObject;
				final DataType dt = attachment.owner.application
						.findDataType(rtq.typeID);
				final ObjectDataType odt = dt instanceof ObjectDataType ? (ObjectDataType) dt
						: null;
				attachment.postTypePackageObject(new RemoteTypeQueryResult(rtq,
						odt));
			} else if (oc == RemoteTypeQueryResult.class) {
				// 查询类型结果
				final RemoteTypeQueryResult rtqr = (RemoteTypeQueryResult) unserialzedObject;
				final String className = rtqr.getClassName();
				if (className != null && className.length() > 0) {
					// 远程有该类型
					final Class<?> clazz = attachment.owner.application
							.tryLoadClass(className);
					if (clazz != null) {
						// 本地有该类
						final DataType odi = DataTypeBase
								.dataTypeOfJavaClass(clazz);
						if (rtqr.typeID.equals(odi.getID())) {
							// 唤醒等待类型的请求
							attachment.onTypeQueryResult(rtqr.requestID,
									rtqr.remoteRequest, true);
							return;
						}
					}
					// 本地没有该类，或类型不符，则发起完整的类型请求
					attachment.postTypePackageObject(new RemoteTypeQuery(rtqr,
							false));
				} else {
					// 通知等待类型的请求失败
					attachment.onTypeQueryResult(rtqr.requestID,
							rtqr.remoteRequest, false);
				}
			}
		}
	}

	final AsyncIOStub<NetNodeImpl> postTypePackageObject(Object toPost) {
		return this.channel.startSendingPackage(
				new SerializedDataBuilder<NSerializerFactoryProvider>(toPost,
						TYPE_PACKAGE), this);
	}

	/**
	 * 查找类型或者请求远程
	 */
	final DataType findDataTypeOrQueryRemote(GUID typeID, int requestID,
			boolean remoteRequest) {
		final DataType dt = this.owner.application.findDataType(typeID);
		if (dt == null) {
			this.postTypePackageObject(new RemoteTypeQuery(typeID, true,
					requestID, remoteRequest));
		}
		return dt;
	}

	final void onTypePackageArriving(NetPackageReceivingStarter starter) {
		starter.startReceivingPackage(new TypePackageResolver(), this);
	}

	private final void onTypeQueryResult(int requestID, boolean remoteRequest,
			boolean succeed) {
		if (remoteRequest) {
			final RemoteRequestImpl rq;
			synchronized (this.requestsFromRemote) {
				rq = this.requestsFromRemote.get(requestID);
			}
			if (rq != null) {
				rq.onTypeResolved(succeed);
			}
		} else {
			final NetRequestImpl rq;
			synchronized (this.requestsToRemote) {
				rq = this.requestsToRemote.get(requestID);
			}
			if (rq != null) {
				rq.onTypeResolved(succeed);
			}
		}
	}

	// //// 类型请求有关/////////////////////////
	// //////////////////////////////////////////

	// //////////////////////////////////////////
	// //// 远程事务有关/////////////////////////
	private final IntKeyMap<TransactionImpl> transactionsFromRemote = new IntKeyMap<TransactionImpl>();

	final TransactionImpl ensureRemoteTransaction(Site site, int transactionID) {
		if (transactionID == 0) {
			throw new IllegalArgumentException("transactionID不可以为0");
		}
		TransactionImpl trans;
		synchronized (this.transactionsFromRemote) {
			trans = this.transactionsFromRemote.get(transactionID);
			if (trans != null) {
				if (trans.site != site) {
					throw new IllegalStateException("事务所所属site不一致");
				}
				return trans;
			}
			trans = new TransactionImpl(site, transactionID, false);
			this.transactionsFromRemote.put(trans.id, trans);
		}
		return trans;
	}

	final void tryRemoveTransaction(TransactionImpl transaction) {
		if (transaction == null || transaction.isLocal || !transaction.isEmpty()) {
			return;
		}
		synchronized (this.transactionsFromRemote) {
			final TransactionImpl exist = this.transactionsFromRemote
					.remove(transaction.id);
			if (exist == null){
				return;
			}
			if (exist != transaction) {
				this.transactionsFromRemote.put(exist.id, exist);
			}
		}
	}

	// //// 远程事务有关/////////////////////////
	// //////////////////////////////////////////
	final void dispose(final Throwable e) {
		synchronized (this.requestsFromRemote) {
			if (!this.requestsFromRemote.isEmpty()) {
				this.requestsFromRemote
						.visitAll(new ValueVisitor<RemoteRequestImpl>() {
							public void visit(int key, RemoteRequestImpl value) {
								value.onNetNodeDisposed(e);
							}
						});
				this.requestsFromRemote.clear();
			}
		}
		synchronized (this.requestsToRemote) {
			if (!this.requestsToRemote.isEmpty()) {
				this.requestsToRemote
						.visitAll(new ValueVisitor<NetRequestImpl>() {
							public void visit(int key, NetRequestImpl value) {
								value.onNetNodeDisposed(e);
							}
						});
				this.requestsToRemote.clear();
			}
		}
		synchronized (this.transactionsFromRemote) {
			if (!this.transactionsFromRemote.isEmpty()) {
				this.transactionsFromRemote
						.visitAll(new ValueVisitor<TransactionImpl>() {
							public void visit(int key, TransactionImpl value) {
								value.resetTrans(false);
							}
						});
				this.transactionsFromRemote.clear();
			}
		}
	}
}
