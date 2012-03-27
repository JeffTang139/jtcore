package org.eclipse.jt.core.def.query;

/**
 * 增、删、改语句的声明器基类
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
		// 使得其他包的类无法继承
	}
}
