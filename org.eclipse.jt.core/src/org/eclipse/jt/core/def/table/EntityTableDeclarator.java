package org.eclipse.jt.core.def.table;

import org.eclipse.jt.core.def.query.MappingQueryStatementDeclare;
import org.eclipse.jt.core.def.query.MappingQueryStatementDefine;
import org.eclipse.jt.core.impl.EntityTableUtil;
import org.eclipse.jt.core.impl.MappingQueryStatementImpl;
import org.eclipse.jt.core.impl.TableDefineImpl;
import org.eclipse.jt.core.misc.TypeArgFinder;

/**
 * ʵ�����
 * 
 * <p>
 * �ö����൱��ʡ����ORM�Ķ���,���Ƕ�ʵ��Ĳ�ѯ�����ڵ�����ȫ�����.δ������֧�ֶ������������Ĳ�ѯ��ʽ.
 * 
 * @author Jeff Tang
 * 
 * @param <TEntity>
 *            ���Ӧ��ʵ�������
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
