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
	 * 增量提取的步骤
	 * 
	 * @author Jeff Tang
	 * 
	 */
	public enum Step {
		/**
		 * 正向提取准备
		 */
		PREPARE,
		/**
		 * 反向提取准备
		 */
		PREPARE_REVERSE,
		/**
		 * 执行
		 */
		EXECUTE,
	}

	/**
	 * 变动的源表
	 */
	public final TableDefine sourceTable;

	/**
	 * 变动记录
	 */
	public final List<DynObj> sourceEntities = new ArrayList<DynObj>();
	/**
	 * 变动的纪录ID列表
	 */
	public final List<GUID> sourceRECIDs = new ArrayList<GUID>();
	/**
	 * 任务的代
	 */
	private long generation;

	/**
	 * 尝试更新任务的代
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
