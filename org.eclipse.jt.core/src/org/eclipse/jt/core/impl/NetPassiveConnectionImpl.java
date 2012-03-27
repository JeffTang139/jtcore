package org.eclipse.jt.core.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class NetPassiveConnectionImpl extends NetConnectionImpl {
	public NetPassiveConnectionImpl(NetChannelImpl owner) {
		super(owner);
	}

	/**
	 * ���𲢵ȴ����ӳɹ�
	 */
	@Override
	public void ensureConnected() {
		try {
			synchronized (this.lock) {
				long left = CONNECTION_TIMEOUT;
				long start = System.currentTimeMillis();
				for (;;) {
					switch (this.state) {
					case STABLE:
					case RUNNING:
						// ������
						break;
					case NONE:
					case CONNECTING:
					case CLOSING:
						// �ȴ��������
						if (left < 0) {
							throw new IOException("�ȴ����ӳ�ʱ");
						}
						this.lock.wait(left);
						left -= System.currentTimeMillis() - start;
						continue;
					case CLOSED:
						throw new IOException("�����ѹر�");
					case FAIL:
						throw new IOException("����ʧ��");
					case DISPOSED:
						throw new IllegalStateException("����������");
					}
					break;
				}
			}
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	final void servletOutputThreadEntry(OutputStream out) throws Throwable {
		DebugHelper.strike("accept output from " + this.owner.remoteNodeID);
		synchronized (this.lock) {
			for (;;) {
				switch (this.state) {
				case NONE:
					// û�����ӣ�����������
					this.setStateNoSync(State.CONNECTING);
					this.sendThread = Thread.currentThread();
					break;
				default:
					DebugHelper.strike("refuse connection from "
							+ this.owner.remoteNodeID);
					// CONNECTING
					// STABLE
					// RUNNING
					// CLOSING
					// CLOSED
					// FAIL
					// DISPOSED
					throw new IllegalStateException("����״̬��" + this.state);
				}
				break;
			}
		}
		try {
			this.initializeOutputStream(out);
			synchronized (this.lock) {
				// �ȴ�input����
				DebugHelper.strike("waiting input from "
						+ this.owner.remoteNodeID);
				long start = System.currentTimeMillis();
				long left = CONNECTION_TIMEOUT;
				for (;;) {
					switch (this.state) {
					case CONNECTING:
						if (left < 0) {
							// ��ʱ
							this.setStateNoSync(State.FAIL);
							DebugHelper.fault("connection timeout from "
									+ this.owner.remoteNodeID);
							continue;
						}
						this.lock.wait(left);
						left = CONNECTION_TIMEOUT + start
								- System.currentTimeMillis();
						continue;
					case STABLE:
					case RUNNING:
						break;
					case CLOSING:
					case FAIL:
						return;
					default:
						throw new IllegalStateException("����״̬��" + this.state);
					}
					break;
				}
				DebugHelper.strike("confirm output from "
						+ this.owner.remoteNodeID);
				this.waitForRunningNoSync();
				this.output = out;
			}
			DebugHelper.strike("send thread start on "
					+ this.owner.remoteNodeID);
			this.owner.sendThreadRun(out);
		} catch (Throwable e) {
		} finally {
			DebugHelper.fault("disconnect output from "
					+ this.owner.remoteNodeID);
			synchronized (this.lock) {
				this.sendThread = null;
				this.output = null;
				this.lock.notifyAll();
				switch (this.state) {
				case CLOSING:
					return;
				case CONNECTING:
				case STABLE:
				case RUNNING:
					break;
				default:
					throw new IllegalStateException("����״̬��" + this.state);
				}
			}
			this.owner.getApplication().overlappedManager.startWork(new Work() {
				@Override
				protected void doWork(WorkingThread thread) throws Throwable {
					exitAndWaitConnection();
				}
			});
		}
	}

	final void servletInputThreadEntry(InputStream in) throws Throwable {
		DebugHelper.strike("accept input from " + this.owner.remoteNodeID);
		synchronized (this.lock) {
			for (;;) {
				switch (this.state) {
				case CONNECTING:
					if (this.sendThread == null) {
						throw new IllegalStateException();
					}
					// ��������
					this.receiveThread = Thread.currentThread();
					break;
				case CLOSING:
				case CLOSED:
					// �˳�
					DebugHelper.strike("refused connection from "
							+ this.owner.remoteNodeID);
					throw new IOException("�����ѹر�");
				case FAIL:
					DebugHelper.strike("refused connection from "
							+ this.owner.remoteNodeID);
					throw new IOException("����ʧ��");
				case DISPOSED:
					DebugHelper.strike("refused connection from "
							+ this.owner.remoteNodeID);
					throw new IOException("����������");
				default:
					throw new IllegalStateException("����״̬��" + this.state);
				}
				break;
			}
		}
		try {
			this.initializeInputStream(in);
			synchronized (this.lock) {
				switch (this.state) {
				case CLOSING:
					return;
				case CONNECTING:
					break;
				default:
					throw new IllegalStateException("����״̬��" + this.state);
				}
				this.setStateNoSync(State.STABLE);
				DebugHelper.strike("confirm input from "
						+ this.owner.remoteNodeID);
				this.waitForRunningNoSync();
				this.input = in;
			}
			DebugHelper.strike("receive thread start on "
					+ this.owner.remoteNodeID);
			this.owner.receiveThreadRun(in);
		} finally {
			DebugHelper.fault("disconnect input from "
					+ this.owner.remoteNodeID);
			synchronized (this.lock) {
				this.receiveThread = null;
				this.input = null;
				this.lock.notifyAll();
				switch (this.state) {
				case CLOSING:
					return;
				case CONNECTING:
				case STABLE:
				case RUNNING:
					break;
				default:
					throw new IllegalStateException("����״̬��" + this.state);
				}
			}
			this.owner.getApplication().overlappedManager.startWork(new Work() {
				@Override
				protected void doWork(WorkingThread thread) throws Throwable {
					exitAndWaitConnection();
				}
			});
		}
	}

	/**
	 * �ȴ��߳���ֹ�����ҵȴ������ӽ���
	 * 
	 * @throws InterruptedException
	 */
	private final void exitAndWaitConnection() throws InterruptedException {
		synchronized (this.lock) {
			// �ȴ�servlet�߳��˳�
			this.exitSendAndReceiveThreadNoSync();
			switch (this.state) {
			case DISPOSED:
				// ������
				return;
			case CLOSED:
				// ��Ȼ�Ͽ�����
			case FAIL:
				// ��ʱ������ԭ��������ʧЧ
				break;
			case CLOSING:
			case NONE:
				if (this.shutdown) {
					this.setStateNoSync(State.CLOSED);
					break;
				}
				this.setStateNoSync(State.NONE);
				// �쳣�Ͽ����ȴ�������
				DebugHelper.strike("waiting connection from "
						+ this.owner.remoteNodeID);
				long start = System.currentTimeMillis();
				long left = CONNECTION_TIMEOUT;
				while (left > 0) {
					this.lock.wait(left);
					switch (this.state) {
					case NONE:
						break;
					default:
						return;
					}
					left = CONNECTION_TIMEOUT + start
							- System.currentTimeMillis();
				}
				// ��ʱ
				this.setStateNoSync(State.FAIL);
				DebugHelper.fault("connection timeout from "
						+ this.owner.remoteNodeID);
				break;
			default:
				// CONNECTING
				// STABLE
				// RUNNING
				throw new IllegalStateException("����״̬��" + this.state);
			}
		}
		this.disconnect();
	}

	@Override
	public void start() {
		synchronized (this.lock) {
			switch (this.state) {
			case STABLE:
				this.setStateNoSync(State.RUNNING);
			case RUNNING:
				break;
			default:
				throw new IllegalStateException();
			}
		}
	}

	/**
	 * �ȴ��ⲿ����startָʾ�߳�����
	 * 
	 * @throws InterruptedException
	 */
	private final boolean waitForRunningNoSync() throws InterruptedException {
		for (;;) {
			switch (this.state) {
			case CLOSING:
			case CLOSED:
				return false;
			case RUNNING:
				return true;
			case STABLE:
				this.lock.wait();
				continue;
			default:
				throw new IllegalStateException("����״̬��" + this.state);
			}
		}
	}
}
