package org.eclipse.jt.core.spi.setl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jt.core.def.table.TableDefine;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.impl.DynObj;
import org.eclipse.jt.core.invoke.Task;
import org.eclipse.jt.core.type.GUID;


public class SETLIncrementExecuteTask extends
        Task<SETLIncrementExecuteTask.Step> {
	/**
	 * ������ȡ�Ĳ���
	 * 
	 * @author Jeff Tang
	 * 
	 */
	public enum Step {
		/**
		 * ������ȡ׼��
		 */
		PREPARE,
		/**
		 * ������ȡ׼��
		 */
		PREPARE_REVERSE,
		/**
		 * ִ��
		 */
		EXECUTE,
	}

	/**
	 * �䶯��Դ��
	 */
	public final TableDefine sourceTable;

	/**
	 * �䶯��¼
	 */
	public final List<DynObj> sourceEntities = new ArrayList<DynObj>();
	/**
	 * �䶯�ļ�¼ID�б�
	 */
	public final List<GUID> sourceRECIDs = new ArrayList<GUID>();
	/**
	 * ����Ĵ�
	 */
	private long generation;

	/**
	 * ���Ը�������Ĵ�
	 * 
	 * @param generation
	 */
	public final void updateGeneration(long generation) {
		if (generation != this.generation) {
			this.sourceEntities.clear();
			this.sourceRECIDs.clear();
			this.generation = generation;
		}
	}

	public SETLIncrementExecuteTask(TableDefine sourceTable) {
		if (sourceTable == null) {
			throw new NullArgumentException("sourceTable");
		}
		this.sourceTable = sourceTable;
	}
}
