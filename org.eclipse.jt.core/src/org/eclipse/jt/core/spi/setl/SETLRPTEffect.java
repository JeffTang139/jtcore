package org.eclipse.jt.core.spi.setl;

import org.eclipse.jt.core.def.table.TableDefine;
import org.eclipse.jt.core.def.table.TableFieldDefine;
import org.eclipse.jt.core.type.GUID;


/**
 * ��ȡ��ĳ�����Ӱ��
 * 
 * @author Jeff Tang
 * 
 */
public class SETLRPTEffect {
	/**
	 * ���Ӱ��ı�
	 */
	public TableDefine table;

	/**
	 * Ӱ�쵽���ֶ�
	 */
	public TableFieldDefine[] fields;
	/**
	 * ��λID��
	 */
	public GUID[] unitIDs;
	/**
	 * ʱ���ַ�����
	 */
	public String[] timeStrings;
	/**
	 * ������
	 */
	public GUID[] rptSolutionIDs;
}
