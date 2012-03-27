package org.eclipse.jt.core.def.table;

import org.eclipse.jt.core.def.query.MappingQueryStatementDeclare;
import org.eclipse.jt.core.def.query.MappingQueryStatementDefine;
import org.eclipse.jt.core.impl.EntityTableUtil;
import org.eclipse.jt.core.impl.MappingQueryStatementImpl;
import org.eclipse.jt.core.impl.TableDefineImpl;
import org.eclipse.jt.core.misc.TypeArgFinder;

/**
 * 实体表定义
 * 
 * <p>
 * 该定义相当于省略了ORM的定义,但是对实体的查询仅限于单个或全体访问.未来考虑支持定义其他条件的查询方式.
 * 
 * @author Jeff Tang
 * 
 * @param <TEntity>
 *            表对应的实体的类型
 */
public abstract class EntityTableDeclarator<TEntity> extends TableDeclarator {

	protected final MappingQueryStatementDeclare orm;

	public final MappingQueryStatementDefine getMappingQueryDefine() {
		return this.orm;
	}

	public EntityTableDeclarator(String name) {
		super(name);
		Class<?> entityClass = TypeArgFinder.get(this.getClass(),
				EntityTableDeclarator.class, 0);
		MappingQueryStatementImpl ormImpl = null;
		this.orm = ormImpl = new MappingQueryStatementImpl(name, entityClass);
		EntityTableUtil.buildTableAndOrm((TableDefineImpl) this.table, ormImpl);
	}
}
