package org.eclipse.jt.core.exception;

import org.eclipse.jt.core.def.query.JoinedRelationRefDefine;
import org.eclipse.jt.core.def.query.QuJoinedRelationRefDefine;
import org.eclipse.jt.core.def.query.SelectDefine;

public final class NullJoinConditionException extends CoreException {

	private static final long serialVersionUID = -4907540339306929567L;

	public NullJoinConditionException(JoinedRelationRefDefine join) {
		super("��������[" + join.getName() + "]����������Ϊ��.");
	}

	public NullJoinConditionException(SelectDefine select,
			QuJoinedRelationRefDefine join) {
		super("��ѯ[" + select.getName() + "]�е���������[" + join.getName()
				+ "]δ������������");
	}
}
