package org.eclipse.jt.core.impl;

/**
 * ��ѯ������ʹ�õ���Ϊ�����Ĺ�ϵ���ó�����
 * 
 * @param <TRelation>
 *            ���õ�Ŀ���ϵԪ��������
 * 
 * @author Jeff Tang
 */
abstract class QuRootRelationRefImpl<TRelation extends Relation> extends
		QuRelationRefImpl<TRelation, QuRootRelationRef, QuRelationRef>
		implements QuRootRelationRef {

	final QuRootRelationRef prev;

	QuRootRelationRefImpl(SelectImpl<?, ?> owner, String name,
			TRelation target, QuRootRelationRef prev) {
		super(owner, name, target);
		this.prev = prev;
	}

	public final QuRootRelationRef prev() {
		return this.prev;
	}

	public final QuRelationRef findRelationRef(String name) {
		for (QuRelationRef ref : this) {
			if (ref.getName().equals(name)) {
				return ref;
			}
		}
		return null;
	}

	public final QuRelationRef findRelationRef(Relation target) {
		for (QuRelationRef ref : this) {
			if (ref.getTarget() == target) {
				return ref;
			}
		}
		return null;
	}

	public final QuRootRelationRef findRootRelationRef(String name) {
		for (QuRootRelationRef r = this; r != null; r = r.next()) {
			if (r.getName().equals(name)) {
				return r;
			}
		}
		return null;
	}

	public final void cloneTo(SelectImpl<?, ?> target, ArgumentOwner args) {
		QuRootRelationRef selfClone = this.cloneSelfTo(target, args);
		QuJoinedRelationRef joins = this.getJoins();
		if (joins != null) {
			joins.cloneTo(selfClone, args);
		}
		QuRootRelationRef next = this.next();
		if (next != null) {
			next.cloneTo(target, args);
		}
	}

	/**
	 * ֻ����������Ŀ���ѯ����
	 * 
	 * @param owner
	 *            Ŀ���ѯ����
	 * @param args
	 *            ��������
	 * @return
	 */
	protected abstract QuRootRelationRef cloneSelfTo(SelectImpl<?, ?> owner,
			ArgumentOwner args);

	public final void render(ISqlSelectBuffer buffer, TableUsages usages) {
		ISqlRelationRefBuffer self = this.renderSelf(buffer, usages);
		QuJoinedRelationRef join = this.getJoins();
		if (join != null) {
			join.render(self, usages);
		}
		QuRootRelationRef next = this.next();
		if (next != null) {
			next.render(buffer, usages);
		}
	}

	abstract ISqlRelationRefBuffer renderSelf(ISqlSelectBuffer buffer,
			TableUsages usages);

}
