package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.impl.DeclaratorBase;

/**
 *
 * ���ݿ��������������
 *
 * @param <TStatementDefine>
 *            �������
 *
 * @author Jeff Tang
 */
public abstract class StatementDeclarator<TStatementDefine extends StatementDefine>
		extends DeclaratorBase {

	@Override
	public abstract StatementDefine getDefine();

	StatementDeclarator(boolean cleanByCoreTag) {
		super(cleanByCoreTag);
		// ʹ�������������޷��̳�
	}
}
