package org.eclipse.jt.core.impl;

/**
 * ��ѯ������ʹ�õ�<strong>����</strong>��ϵ����
 * 
 * <p>
 * ������ϵ����Ϊ��sql��from�Ӿ���,�������ϲ㲢�Եѿ��������ӵĹ�ϵ����.����,������ʡ���������ͼ�������sql�����:
 * 
 * <blockquote>
 * 
 * <pre>
 * select * from a join (b join c join d) join e, f join g, h
 * </pre>
 * 
 * </blockquote>
 * 
 * a, f, h���ڸ����Ĺ�ϵ����.
 * 
 * <p>
 * QuRootRelationRef��QuJoinedRelationRef����������:
 * <ul>
 * <li>QuRootRelationRef��prevSibling��nextSibling������ͬΪQuRootRelationRef.
 * <li>QuJoinedRelationRefֻ������ΪQuJoinedRelationRef��nextSibling.
 * <li>���ߵ�firstChild���Ͷ�ΪQuJoinedRelationRef.
 * </ul>
 * 
 * <p>
 * �ӹ�ϵ�����Ͻ�,��������֮���Ǳ�ʾ�ѿ�����������,��������֮����������������ʽ(��DNA�Ľӿڶ�����һ����ϵ)����-�ѿ�������ѡ������.
 * ��Ȼ����ʮ������,�����ǵ�Root��newJoin�������µĽṹ�仯,�����߼������ָ����״���,Ҳ�����ͨ�õ�sql�﷨.
 * 
 * <p>
 * ���и�����ϵ�������һ��˫�򲻳ɻ�����.����ÿһ��������ϵ,����һ�����Ľṹ.
 * ������ϵ���ü̳��˵������ӿ�,��������֮��ĸ�����ϵ����,����ÿһ��������ϵ���õ����ͽṹ,������������е���.
 * 
 * @author Jeff Tang
 * 
 */
interface QuRootRelationRef extends QuRelationRef, Iterable<QuRelationRef> {

	QuRootQueryRef castAsQueryRef();

	QuRootTableRef castAsTableRef();

	QuRootRelationRef prev();

	QuRootRelationRef next();

	QuRootRelationRef last();

	QuRelationRef findRelationRef(String name);

	QuRelationRef findRelationRef(Relation target);

	QuRootRelationRef findRootRelationRef(String name);

	/**
	 * Ŀ���ѯ���������Ե�ǰ����Ϊ�����ĸ���ϵ����,���������еݹ��join��next
	 * 
	 * @param target
	 *            ���Ƶ���Ŀ���ѯ����.
	 * @param args
	 *            ��������,�����������ʽʱ�Ӹ�Ŀ����Ҳ�������.
	 */
	void cloneTo(SelectImpl<?, ?> target, ArgumentOwner args);

	void render(ISqlSelectBuffer buffer, TableUsages usages);
}
