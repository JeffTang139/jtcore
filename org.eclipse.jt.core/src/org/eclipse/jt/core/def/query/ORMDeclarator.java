package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.ObjectQuerier;
import org.eclipse.jt.core.impl.DNASql;
import org.eclipse.jt.core.impl.MappingQueryStatementImpl;
import org.eclipse.jt.core.misc.TypeArgFinder;

/**
 * 实体绑定声明器<br>
 * 实体绑定声明器用于声明一个实体绑定的查询定义,
 * 
 * @author Jeff Tang
 * 
 * @param <TEntity>
 */
public abstract class ORMDeclarator<TEntity> extends
		StatementDeclarator<MappingQueryStatementDefine> {

	public ORMDeclarator() {
		super(false);
		this.orm = (MappingQueryStatementImpl) DNASql.parseForDeclarator(this);
	}

	public ORMDeclarator(ObjectQuerier oQuerier) {
		this();
	}

	public ORMDeclarator(String name) {
		super(true);
		this.orm = new MappingQueryStatementImpl(name, TypeArgFinder.get(this
				.getClass(), ORMDeclarator.class, 0), this);
	}

	protected final MappingQueryStatementDeclare newORM() {
		return ((MappingQueryStatementImpl) this.orm).clone();
	}

	@Override
	public final MappingQueryStatementDefine getDefine() {
		return this.orm;
	}

	protected final MappingQueryStatementDeclare orm;

	private final static Class<?>[] intf_classes = { MappingQueryStatementDefine.class };

	@Override
	protected final Class<?>[] getDefineIntfRegClasses() {
		return ORMDeclarator.intf_classes;
	}
}
