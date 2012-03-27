package org.eclipse.jt.core.spi.setl;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.def.table.TableFieldDefine;
import org.eclipse.jt.core.spi.setl.SETLExternalHelper.EntityPaths;
import org.eclipse.jt.core.type.GUID;


/**
 * ʵ�����ð����ӿ�
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
	 * �����Ϊ��α�ʶ����
	 */
	public TableFieldDefine getPathField();

	/**
	 * ���ݶ����Token��Χ���ʵ�ʵ�Path
	 */
	public void fillEntityToken(Context context, String from, String to,
	        boolean includeFrom, boolean includeTo, EntityPaths paths);

	/**
	 * �����һ����ο�ʼλ��
	 */
	public int nextLevelOffset(Context context, String path, int offset);
}