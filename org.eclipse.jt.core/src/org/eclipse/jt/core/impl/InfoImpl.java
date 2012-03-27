package org.eclipse.jt.core.impl;

import java.util.ArrayList;

import org.eclipse.jt.core.info.Info;
import org.eclipse.jt.core.type.Convert;
import org.eclipse.jt.core.type.GUID;


/**
 * 信息实现
 * 
 * @author Jeff Tang
 * 
 */
class InfoImpl implements Info {
	/**
	 * 日志记录ID
	 */
	private volatile GUID id;

	final boolean tryAllocID(ApplicationImpl app) {
		final GUID id = this.id;
		if (id == null) {
			this.id = app.newRECID();
			return true;
		}
		return false;
	}

	/**
	 * 获得日志记录ID
	 */
	public final GUID getID() {
		final GUID id = this.id;
		if (id == null) {
			throw new UnsupportedOperationException("id is null");
		}
		return id;
	}

	/**
	 * 所属定义
	 */
	final InfoDefineImpl define;
	/**
	 * 所属父过程
	 */
	final ProcessInfoImpl process;

	/**
	 * 所属父过程
	 */
	public final ProcessInfoImpl getProcess() {
		return this.process;
	}

	/**
	 * 获得确实的过程
	 */
	ProcessInfoImpl getRealProcess() {
		return this.process;
	}

	/**
	 * 结束确实的过程
	 * 
	 * @return 返回“当前”信息节点，或者null表示出现错误
	 */
	ProcessInfoImpl finishRealProcess(short invokeDepth) {
		final ProcessInfoImpl process = this.process;
		return process != null ? process.finishRealProcess(invokeDepth) : null;
	}

	/**
	 * 下一个兄弟信息，所有同级信息形成环
	 */
	private InfoImpl next;

	final void insertAfter(InfoImpl after) {
		if (after == null) {
			this.next = this;
		}
		this.next = after.next;
		after.next = this;
	}

	/**
	 * 开始时间
	 */
	final long time;
	/**
	 * 参数数组
	 */
	private final Object[] params;

	/**
	 * 设置参数
	 */
	final void setParam(int index, Object value) {
		InfoParameterDefineImpl paramDefine = this.define.parameters.get(index);
		this.params[index] = Convert.toType(paramDefine.type, value);
	}

	InfoImpl(InfoDefineImpl define, ProcessInfoImpl process, Object param1,
	        Object param2, Object param3, Object[] others, int otherOffset) {
		this.define = define;
		this.process = process;
		this.time = System.currentTimeMillis();
		ArrayList<InfoParameterDefineImpl> pas = define.parameters;
		final int ps = pas.size();
		Object[] params;
		if (ps > 0) {
			params = new Object[ps];
			params[0] = pas.get(0).convertWithDefault(param1);
			if (ps > 1) {
				params[1] = pas.get(1).convertWithDefault(param2);
				if (ps > 2) {
					params[2] = pas.get(2).convertWithDefault(param3);
					int i = 3;
					if (others != null) {
						for (int othersL = others.length; i < ps
						        && otherOffset < othersL; i++, otherOffset++) {
							params[i] = pas.get(i).convertWithDefault(
							        others[otherOffset]);
						}
					}
					while (i < ps) {// 填充默认值
						params[i] = pas.get(i++).convertWithDefault(null);
					}
				}
			}
		} else {
			params = Utils.emptyObjectArray;
		}
		this.params = params;
		if (process != null) {
			process.appendChild(this);
		}
	}

	public final InfoDefineImpl getDefine() {
		return this.define;
	}

	public final Object getParam(int index) {
		return 0 <= index && index < this.params.length ? this.params[index]
		        : null;
	}

	public final long getTime() {
		return this.time;
	}
}
