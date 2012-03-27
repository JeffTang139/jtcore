package org.eclipse.jt.core.spi.metadata;

import java.util.HashSet;

import org.eclipse.jt.core.invoke.Event;


/**
 * 调整元数据通知事件
 * 
 * @author Jeff Tang
 * 
 */
public class AdjustMetaDataEvent extends Event {
	/**
	 * 重置全部参数的站点ID为当前站点ID
	 */
	public final static String ACTION_RESET_SITE_ID = "重置站点ID";
	/**
	 * 重置全部参数的下发属性为ture
	 */
	public final static String ACTION_PUBLISH_ALL = "全部下发";

	/**
	 * 判断是否包含某项调整动作
	 */
	public final boolean hasAction(String action) {
		return this.actions.contains(action);
	}

	public AdjustMetaDataEvent(String action) {
		this.putAction(action);
	}

	public AdjustMetaDataEvent(String action, String... others) {
		this.putAction(action);
		this.putActions(others);
	}

	public AdjustMetaDataEvent(String[] actions) {
		if (this.putActions(actions) == 0) {
			throw new IllegalArgumentException("没有任何调整参数的动作");
		}
	}

	private final HashSet<String> actions = new HashSet<String>();

	private boolean putAction(String action) {
		if (action == null || action.length() == 0) {
			throw new IllegalArgumentException("调整参数的动作为空");
		}
		return this.actions.add(action);
	}

	private int putActions(String[] actions) {
		int i = 0;
		if (actions != null && actions.length > 0) {
			for (String action : actions) {
				if (this.putAction(action)) {
					i++;
				}
			}
		}
		return i;
	}
}
