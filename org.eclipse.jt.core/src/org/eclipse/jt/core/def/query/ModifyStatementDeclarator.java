package org.eclipse.jt.core.def.query;

/**
 * ����ɾ������������������
 * 
 * @author Jeff Tang
 * 
 * @param <TStatement>
 */
public abstract class ModifyStatementDeclarator<TStatement extends ModifyStatementDefine>
        extends StatementDeclarator<TStatement> {

	@Override
	public abstract ModifyStatementDefine getDefine();

	ModifyStatementDeclarator(boolean cleanByCoreTag) {
		super(cleanByCoreTag);
		// ʹ�������������޷��̳�
	}
}
