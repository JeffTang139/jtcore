package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.impl.DeclaratorBase;

/**
 *
 * 数据库语句声明器基类
 *
 * @param <TStatementDefine>
 *            语句类型
 *
 * @author Jeff Tang
 */
public abstract class StatementDeclarator<TStatementDefine extends StatementDefine>
		extends DeclaratorBase {

	@Override
	public abstract StatementDefine getDefine();

	StatementDeclarator(boolean cleanByCoreTag) {
		super(cleanByCoreTag);
		// 使得其他包的类无法继承
	}
}
