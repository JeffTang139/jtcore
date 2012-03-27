package org.eclipse.jt.core.spi.setl;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.def.table.TableFieldDefine;
import org.eclipse.jt.core.spi.setl.SETLExternalHelper.EntityPaths;
import org.eclipse.jt.core.type.GUID;


/**
 * 实体引用帮助接口
 * 
 * @author Jeff Tang
 * 
 */
public interface SETLEntityRefHelper {

	public interface EntityRefs {
		public Object findNode(GUID recid);

		public Object addChild(Object parent, GUID recid, String code);
	}

	public void fillEntityRefs(Context context, EntityRefs entityRefs);

	/**
	 * 获得作为层次标识的列
	 */
	public TableFieldDefine getPathField();

	/**
	 * 根据定义的Token范围获得实际的Path
	 */
	public void fillEntityToken(Context context, String from, String to,
	        boolean includeFrom, boolean includeTo, EntityPaths paths);

	/**
	 * 获得下一个层次开始位置
	 */
	public int nextLevelOffset(Context context, String path, int offset);
}