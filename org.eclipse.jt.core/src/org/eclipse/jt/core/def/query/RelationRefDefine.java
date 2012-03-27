package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.NamedDefine;

/**
 * ��ϵ����
 * 
 * <p>
 * ��ϵ���ü����һ����ϵԪ����Ĵ��.��ϵ���ÿ�����Ϊ�Ǹ��ṹ���ڹ�ϵԪ�����ά���.
 * 
 * @author Jeff Tang
 * 
 */
@SuppressWarnings({ "unused", "deprecation" })
public interface RelationRefDefine extends NamedDefine, MoRelationRefDefine {

	/**
	 * ��ȡĿ��Ԫ��ϵ����
	 * 
	 * @return ��ϵ��Ԫ����
	 */
	public RelationDefine getTarget();

	/**
	 * �Ƿ��Ǳ�����
	 * 
	 * @return
	 */
	@Deprecated
	public boolean isTableReference();

	/**
	 * �Ƿ��ǲ�ѯ����
	 * 
	 * @return
	 */
	@Deprecated
	public boolean isQueryReference();

}
