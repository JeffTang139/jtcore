package org.eclipse.jt.core.spi.metadata;

import java.util.logging.Logger;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.invoke.Task;


/**
 * ֪ͨϵͳװ��Ԫ�������񣬱������첽�������<br>
 * װ�سɹ����л�վ�㣬ʧ�����׳��쳣<br>
 * ���첽����Ľ��Ȱ������£�<br>
 * [0%..5%] : ��վ������ <br>
 * (5%..100%] : װ��Ա���� <br>
 * 
 * @author Jeff Tang
 * 
 */
public class LoadAllMetaDataTask extends Task<LoadAllMetaDataTask.LoadMode> {
	/**
	 * װ��ģʽ
	 */
	public enum LoadMode {
		/**
		 * �ϲ�Ԫ����
		 */
		MERGE,
		/**
		 * �滻Ԫ����
		 */
		REPLACE,
	}

	/**
	 * Ԫ����
	 */
	public final byte[] metaData;
	/**
	 * �����¼��
	 */
	public final Logger logger;

	/**
	 * �����ӳٺ����������κ�ʱ�����ö���Ч
	 */
	private volatile long restartDelay = 30000;

	/**
	 * �����ӳٺ����������κ�ʱ�����ö���Ч
	 */
	public final long getRestartDelay() {
		return this.restartDelay;
	}

	/**
	 * ������ʱ��
	 */
	public volatile long finishTime;

	/**
	 * �����ӳٺ����������κ�ʱ�����ö���Ч
	 */
	public final void setRestartDelay(long restartDelay) {
		synchronized (this) {
			this.restartDelay = restartDelay;
			this.notifyAll();
		}
	}

	public LoadAllMetaDataTask(byte[] metaData, Logger logger) {
		if (metaData == null || metaData.length == 0) {
			throw new NullArgumentException("metaData");
		}
		this.metaData = metaData;
		this.logger = logger;
	}
}
