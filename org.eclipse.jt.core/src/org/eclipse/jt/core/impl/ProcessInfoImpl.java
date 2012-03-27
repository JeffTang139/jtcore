package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.info.InfoKind;
import org.eclipse.jt.core.info.ProcessInfo;
import org.eclipse.jt.core.spi.log.LogEntryKind;

/**
 * 过程信息实现类
 * 
 * @author Jeff Tang
 * 
 */
final class ProcessInfoImpl extends InfoImpl implements ProcessInfo {

	/**
	 * 获得确实的过程
	 */
	@Override
	final ProcessInfoImpl getRealProcess() {
		return (this.result & RESULT_MASK_FINISH) != 0 ? this.process : this;
	}

	/**
	 * 正值表示毫秒耗时，负值表示秒消耗
	 */
	private int duration;

	/**
	 * 局部进度保存
	 */
	final float contextProgressSave;
	/**
	 * 局部进度比例保存
	 */
	final float progressQuotietySave;
	/**
	 * 过程深度
	 */
	final short depth;
	/**
	 * 完成掩码
	 */
	final static byte RESULT_MASK_FINISH = 1;
	/**
	 * 失败掩码
	 */
	final static byte RESULT_MASK_ERROR = 2;
	/**
	 * 过程出现错误，但还没有结束
	 */
	final static byte RESULT_ERROR = RESULT_MASK_ERROR;
	/**
	 * 过程完成
	 */
	final static byte RESULT_SUCCESS = RESULT_MASK_FINISH;
	/**
	 * 过程失败
	 */
	final static byte RESULT_FAIL = RESULT_MASK_FINISH | RESULT_MASK_ERROR;
	/**
	 * 过程结果，0表示还没有结束
	 */
	private byte result;

	/**
	 * 获取消耗时长
	 */
	public final boolean hasError() {
		return (this.result & RESULT_MASK_ERROR) != 0;
	}

	/**
	 * 过程是否结束
	 */
	public final boolean isFinished() {
		return (this.result & RESULT_MASK_FINISH) != 0;
	}

	final LogEntryKind getLogKind() {
		switch (this.result) {
		case RESULT_SUCCESS:
			return LogEntryKind.PROCESS_SUCCESS;
		case RESULT_FAIL:
			return LogEntryKind.PROCESS_FAIL;
		case 0:
			return LogEntryKind.PROCESS_BEGIN;
		default:
			throw new IllegalStateException("无效的过程状态:" + this.result);
		}
	}

	/**
	 * 获得耗时
	 */
	public final long getDuration() {
		// 正值表示毫秒耗时，负值表示秒消耗
		if (this.duration >= 0) {
			return this.duration;
		} else {
			return -this.duration * 1000;
		}
	};

	final static UnsupportedOperationException finishError() {
		return new UnsupportedOperationException("结束过程的调用次数多于开始过程的次数");
	}

	/**
	 * 触发结束动作
	 */
	@Override
	final ProcessInfoImpl finishRealProcess(short invokeDepth) {
		if (this.isFinished()) {
			// 已经结束的过程调用上级过程的结束
			return super.finishRealProcess(invokeDepth);
		}
		if (this.depth <= invokeDepth) {
			return null;// 错误
		}
		this.result |= RESULT_MASK_FINISH;
		// 正值表示毫秒耗时，负值表示秒消耗
		long du = System.currentTimeMillis() - super.time;
		if (du > Integer.MAX_VALUE) {
			this.duration = -(int) (du / 1000);
		} else {
			this.duration = (int) du;
		}
		return this;
	}

	/**
	 * 最后一个子信息，所有子信息形成环
	 */
	private InfoImpl lastChild;

	/**
	 * 添加子信息
	 */
	final void appendChild(InfoImpl child) {
		child.insertAfter(this.lastChild);
		this.lastChild = child;
		if (child.define.kind == InfoKind.ERROR) {
			this.result |= RESULT_MASK_ERROR;
		}
	}

	ProcessInfoImpl(InfoDefineImpl define, ProcessInfoImpl process,
	        Object param1, Object param2, Object param3, Object[] others,
	        int otherOffset, float contextProgressSave,
	        float progressQuotietySave, short depth) {
		super(define, process, param1, param2, param3, others, otherOffset);
		this.contextProgressSave = contextProgressSave;
		this.progressQuotietySave = progressQuotietySave;
		this.depth = depth;
	}
}
