package org.eclipse.jt.core.exception;

import org.eclipse.jt.core.def.table.TableRelationDefine;

public final class NotEquiRelationException extends CoreException {

	private static final long serialVersionUID = -8365196318550032672L;

	public NotEquiRelationException(TableRelationDefine relation) {
		super("��ϵ����[" + relation.getName() + "]���ǵ�ֵ���ϵ����.");
	}
}
