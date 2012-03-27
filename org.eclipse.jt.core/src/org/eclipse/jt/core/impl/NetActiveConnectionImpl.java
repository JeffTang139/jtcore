package org.eclipse.jt.core.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.eclipse.jt.core.exception.NullArgumentException;

import sun.net.www.http.HttpClient;


class NetActiveConnectionImpl extends NetConnectionImpl {
	/**
	 * ���ӳ������Ե�������
	 */
	private final int CONNECT_MAX_RETRY = 3;

	private final URL remoteAddr;
	private HttpClient httpIn;
	private HttpClient httpOut;
	/**
	 * ���ӳ��Դ��������ӳɹ�һ�ξ͹���
	 */
	private int tryCount;

	NetActiveConnectionImpl(NetChannelImpl owner, URL remoteAddr) {
		super(owner);
		if (remoteAddr == null) {
			throw new NullArgumentException("remoteAddr");
		}
		this.remoteAddr = remoteAddr;
	}

	@Override
	public void ensureConnected() {
		try {
			this.connect();
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	private final void connect() throws Throwable {
		try {
			synchronized (this.lock) {
				for (;;) {
					switch (this.state) {
					case NONE:
						// û�����ӣ�����������
						this.setStateNoSync(State.CONNECTING);
						DebugHelper.strike("connect to " + this.remoteAddr
								+ " [retry " + this.tryCount + "]");
						break;
					case CONNECTING:
					case CLOSING:
						this.lock.wait();
						continue;
					case STABLE:
					case RUNNING:
						return;
					case CLOSED:
						throw new IOException("�����ѹر�");
					case FAIL:
						throw new IOException("����ʧ��");
					case DISPOSED:
						throw new IOException("����������");
					}
					break;
				}
			}
			DnaHttpClient httpIn = new DnaHttpClient(this.remoteAddr,
					this.owner.getApplication().localNodeID);
			InputStream input = httpIn.openInput();
			this.initializeInputStream(input);
			DnaHttpClient httpOut = new DnaHttpClient(this.remoteAddr,
					this.owner.getApplication().localNodeID);
			OutputStream output = httpOut.openOutput();
			this.initializeOutputStream(output);
			synchronized (this.lock) {
				switch (this.state) {
				case NONE:
				case CLOSING:
				case CLOSED:
				case DISPOSED:
					return;
				case CONNECTING:
					break;
				default:
					throw new IllegalStateException("����״̬��" + this.state);
				}
				// �������ӳɹ�״̬
				this.setStateNoSync(State.STABLE);
				this.httpIn = httpIn;
				this.httpOut = httpOut;
				this.input = input;
				this.output = output;
				// �������Ӵ���
				this.tryCount = 0;
				DebugHelper.strike("confirm connect to " + this.remoteAddr);
			}
		} catch (InterruptedException e) {
			throw e;
		} catch (Throwable e) {
			RETRY: {
				synchronized (this.lock) {
					if (this.state == State.FAIL) {
						break RETRY;
					}
					if (++this.tryCount >= CONNECT_MAX_RETRY) {
						// ����ʧ��
						this.setStateNoSync(State.FAIL);
						throw new IOException("����ʧ�ܣ��������Ӵ�������������");
					}
					// �˳����ͺͽ����߳�
					this.exitSendAndReceiveThreadNoSync();
					switch (this.state) {
					case CLOSING:
						this.setStateNoSync(State.NONE);
					case NONE:
						break;
					case CLOSED:
					case DISPOSED:
					case FAIL:
						return;
					default:
						throw new IllegalStateException("����״̬��" + this.state);
					}
				}
				e.printStackTrace();
				this.connect();
				return;
			}
			this.disconnect();
			throw e;
		}
	}

	@Override
	public void start() {
		synchronized (this.lock) {
			switch (this.state) {
			case STABLE:
				break;
			case RUNNING:
				return;
			default:
				throw new IllegalStateException("����״̬��" + this.state);
			}
			this.setStateNoSync(State.RUNNING);
		}
		// ���������߳�
		this.owner.getApplication().overlappedManager
				.startWork(new SendFragmentWork());
		// ���������߳�
		this.owner.getApplication().overlappedManager
				.startWork(new ReceiveFragmentWork());
	}

	@Override
	protected void exitSendAndReceiveThreadNoSync() {
		super.exitSendAndReceiveThreadNoSync();
		// ����״̬
		if (this.httpIn != null) {
			this.httpIn.closeServer();
			this.httpIn = null;
		}
		if (this.httpOut != null) {
			this.httpOut.closeServer();
			this.httpOut = null;
		}
	}

	private final class SendFragmentWork extends Work {
		@Override
		protected final void doWork(WorkingThread thread) throws Throwable {
			OutputStream out;
			synchronized (lock) {
				switch (state) {
				case RUNNING:
					break;
				case CLOSING:
					return;
				default:
					throw new IllegalStateException("����״̬��" + state);
				}
				out = output;
				sendThread = Thread.currentThread();
			}
			DebugHelper.strike("send thread start on " + owner.remoteNodeID);
			try {
				owner.sendThreadRun(out);
			} catch (Throwable e) {
			} finally {
				DebugHelper.strike("exit send thread");
				synchronized (lock) {
					output = null;
					sendThread = null;
					lock.notifyAll();
				}
				owner.getApplication().overlappedManager.startWork(new Work() {
					@Override
					protected void doWork(WorkingThread thread)
							throws Throwable {
						exitAndResetConnection();
					}
				});
			}
		}
	}

	private final class ReceiveFragmentWork extends Work {
		@Override
		protected final void doWork(WorkingThread thread) throws Throwable {
			InputStream in;
			synchronized (lock) {
				switch (state) {
				case RUNNING:
					break;
				case CLOSING:
					return;
				default:
					throw new IllegalStateException("����״̬��" + state);
				}
				in = input;
				receiveThread = Thread.currentThread();
			}
			DebugHelper.strike("receive thread start on " + owner.remoteNodeID);
			try {
				owner.receiveThreadRun(in);
			} catch (Throwable e) {
			} finally {
				DebugHelper.strike("exit receive thread");
				synchronized (lock) {
					input = null;
					receiveThread = null;
					lock.notifyAll();
				}
				owner.getApplication().overlappedManager.startWork(new Work() {
					@Override
					protected void doWork(WorkingThread thread)
							throws Throwable {
						exitAndResetConnection();
					}
				});
			}
		}
	}

	private final void exitAndResetConnection() throws Throwable {
		RESET: {
			synchronized (this.lock) {
				// �˳����ͺͽ����߳�
				this.exitSendAndReceiveThreadNoSync();
				switch (this.state) {
				case CLOSING:
				case NONE:
					if (this.shutdown) {
						this.setStateNoSync(State.CLOSED);
						break RESET;
					}
					this.setStateNoSync(State.NONE);
					break;
				case CLOSED:
				case FAIL:
					break RESET;
				case DISPOSED:
					return;
				default:
					throw new IllegalStateException("����״̬��" + this.state);
				}
			}
			this.connect();
			this.start();
			return;
		}
		this.disconnect();
	}
}
