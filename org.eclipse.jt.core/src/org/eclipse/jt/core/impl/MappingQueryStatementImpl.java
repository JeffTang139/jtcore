package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.MetaElementType;
import org.eclipse.jt.core.def.query.MappingQueryStatementDeclare;
import org.eclipse.jt.core.def.query.ORMDeclarator;
import org.eclipse.jt.core.exception.InvalidStatementDefineException;
import org.eclipse.jt.core.exception.NamedDefineExistingException;
import org.eclipse.jt.core.exception.UnsupportedAssignmentException;
import org.eclipse.jt.core.misc.ObjectBuilder;
import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.misc.SXMergeHelper;
import org.eclipse.jt.core.type.AssignCapability;

/**
 * 带对象映射的查询,即每个查询输出列绑定到一个java实体的属性上.
 * 
 * @author Jeff Tang
 * 
 */
public final class MappingQueryStatementImpl extends QueryStatementBase
		implements MappingQueryStatementDeclare, Declarative<ORMDeclarator<?>> {

	public final ORMDeclarator<?> getDeclarator() {
		return this.declarator;
	}

	public final MetaElementType getMetaElementType() {
		return MetaElementType.ORM;
	}

	public final StructDefineImpl getMappingTarget() {
		return this.mapping;
	}

	public final void setAutoBind(boolean isAutoBind) {
		this.checkModifiable();
		this.isAutoBind = isAutoBind;
	}

	public final boolean isAutoBind() {
		return this.isAutoBind;
	}

	@Override
	public final String getXMLTagName() {
		return xml_tag;
	}

	@Override
	public final void render(SXElement element) {
		super.render(element);
		element.maskTrue(xml_attr_autobind, this.isAutoBind);
	}

	@Override
	public final MappingQueryStatementImpl clone() {
		MappingQueryStatementImpl target = new MappingQueryStatementImpl(
				this.name, this.mapping);
		super.cloneSelectTo(target, this);
		return target;
	}

	final static String xml_tag = "orm-query";
	final static String xml_attr_autobind = "auto-bind";

	/**
	 * 是否自动榜定
	 */
	private boolean isAutoBind = true;

	final ORMDeclarator<?> declarator;

	public MappingQueryStatementImpl(String name, Class<?> soClass) {
		this(name, DataTypeBase.getStaticStructDefine(soClass));
	}

	public MappingQueryStatementImpl(String name, Class<?> soClass,
			ORMDeclarator<?> declarator) {
		this(name, DataTypeBase.getStaticStructDefine(soClass), declarator);
	}

	public MappingQueryStatementImpl(String name, StructDefineImpl target) {
		this(name, target, null);
	}

	public MappingQueryStatementImpl(String name, StructDefineImpl target,
			ORMDeclarator<?> declarator) {
		super(name);
		if (target == null) {
			throw new NullPointerException();
		}
		this.mapping = target;
		this.declarator = declarator;
	}

	@Override
	protected final QueryColumnImpl newColumnOnly(String name, ValueExpr expr) {
		QueryColumnImpl column = new QueryColumnImpl(this, name, expr);
		StructFieldDefineImpl field = this.mapping.fields.get(name);
		AssignCapability ac = field.getType().isAssignableFrom(expr.getType());
		if (ac == AssignCapability.NO) {
			if (SystemVariables.VALIDATE_ASSIGN_TYPE) {
				throw new UnsupportedAssignmentException(this, column, field);
			} else {
				System.err.println("在ORM定义[" + this.getName() + "]中,Java字段["
						+ field.getName() + "]的类型["
						+ field.getType().toString() + "]不能绑定到类型为["
						+ column.getType() + "]的输出列上.");
			}
		}
		column.field = field;
		return column;
	}

	/**
	 * 构造映射的对象
	 * 
	 * @param <TEntity>
	 * 
	 * @param entityFactory
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	final <TEntity> TEntity newEntity(ObjectBuilder<TEntity> entityFactory) {
		if (entityFactory != null) {
			TEntity result;
			try {
				result = entityFactory.build();
			} catch (Throwable e) {
				throw Utils.tryThrowException(e);
			}
			if (result != null) {
				if (!this.mapping.soClass.isInstance(result)) {
					throw new IllegalArgumentException("实体工厂产生的实体类型不符");
				}
				return result;
			}
		}
		return (TEntity) this.mapping.newEmptySO();
	}

	@Override
	final void merge(SXElement element, SXMergeHelper helper) {
		super.merge(element, helper);
		this.isAutoBind = element
				.getBoolean(xml_attr_autobind, this.isAutoBind);
	}

	@Override
	final void doPrepare() throws Throwable {
		super.doPrepare();
		this.prepareMappingTarget();
		this.objRecveredDeleteSql = null;
		this.objRecveredUpdateSql = null;
		this.objIdDeleteSql = null;
		this.objIdsDeleteSql = null;
		this.objLpkDeleteSql = null;
		this.objIdQuerySql = null;
		this.objLpkQuerySql = null;
	}

	private final StringKeyMap<QueryColumnImpl> getMappedField() {
		StringKeyMap<QueryColumnImpl> mapped = new StringKeyMap<QueryColumnImpl>(
				this.mapping.fields.size());
		for (int i = 0, c = this.columns.size(); i < c; i++) {
			final QueryColumnImpl column = this.columns.get(i);
			final String javaFieldName = column.field.name;
			try {
				mapped.put(javaFieldName, column, true);
			} catch (NamedDefineExistingException e) {
				QueryColumnImpl bounded = mapped.get(javaFieldName);
				throw new IllegalArgumentException("在绑定查询定义[" + this.name
						+ "]中,已将查询输出列[" + bounded.name + "]绑定到实体字段["
						+ javaFieldName + "],无允许再将查询输出列[" + column.name
						+ "]绑定到该实体字段上.");
			}
		}
		return mapped;
	}

	private final void tryMapRest() {
		StringKeyMap<QueryColumnImpl> mapped = this.getMappedField();
		if (mapped.size() == this.mapping.fields.size()) {
			return;
		}
		next: for (int i = 0, c = this.mapping.fields.size(); i < c; i++) {
			StructFieldDefineImpl javaField = this.mapping.fields.get(i);
			// 还未绑定字段
			if (mapped.containsKey(javaField.name)) {
				continue;
			}
			for (QuRelationRef relaitonRef : this.rootRelationRef()) {
				if (!(relaitonRef instanceof QuTableRef)) {
					continue;
				}
				QuTableRef tableRef = (QuTableRef) relaitonRef;
				// 该表引用目标是否有同名的表字段
				TableFieldDefineImpl tf = tableRef.getTarget().fields
						.find(javaField.name);
				if (tf != null) {
					AssignCapability ac = javaField.type.isAssignableFrom(tf
							.getType());
					// 该字段类型能转换到java字段类型
					if (ac == AssignCapability.NO) {
						continue;
					}
					// 且该引用下的该字段还未输出
					if (this.findColumn(tableRef, tf) == null) {
						// 输出并绑定
						QueryColumnImpl column = this.newColumn(javaField.name,
								tableRef.expOf(tf));
						mapped.put(javaField.name, column);
						continue next;
					}
				}
			}
		}
	}

	private final void prepareMappingTarget() {
		if (!this.isAutoBind) {
			return;
		}
		this.tryMapRest();
	}

	static final InvalidStatementDefineException rootModifyNotSupported(
			MappingQueryStatementImpl statement) {
		return new InvalidStatementDefineException("ORM[" + statement.name
				+ "]的根引用不支持更新.");
	}

	static final void fillLpkWhere(ISqlExprBuffer where, String alias,
			TableFieldDefineImpl[] fields, ArgumentReserver[] args) {
		for (int i = 0; i < fields.length; i++) {
			where.loadField(alias, fields[i].namedb());
			where.loadVar(args[i]);
			where.eq();
		}
		where.and(args.length);
	}

	// ---------------------------------------------

	private volatile ObjRecverDeleteSql objRecveredDeleteSql;

	final ObjRecverDeleteSql getObjRecveredDeleteSql(DBAdapterImpl dbAdapter) {
		this.ensurePrepared(dbAdapter.getContext(), false);
		ObjRecverDeleteSql objRecveredDeleteSql = this.objRecveredDeleteSql;
		if (objRecveredDeleteSql == null) {
			synchronized (this) {
				objRecveredDeleteSql = this.objRecveredDeleteSql;
				if (objRecveredDeleteSql == null) {
					this.objRecveredDeleteSql = objRecveredDeleteSql = new ObjRecverDeleteSql(
							dbAdapter.lang, this);
				}
			}
		}
		return objRecveredDeleteSql;
	}

	private volatile ObjRecverUpdateSql objRecveredUpdateSql;

	final ObjRecverUpdateSql getObjRecveredUpdateSql(DBAdapterImpl dbAdapter) {
		this.ensurePrepared(dbAdapter.getContext(), false);
		ObjRecverUpdateSql objRecveredUpdateSql = this.objRecveredUpdateSql;
		if (objRecveredUpdateSql == null) {
			synchronized (this) {
				objRecveredUpdateSql = this.objRecveredUpdateSql;
				if (objRecveredUpdateSql == null) {
					this.objRecveredUpdateSql = objRecveredUpdateSql = new ObjRecverUpdateSql(
							dbAdapter.lang, this);
				}
			}
		}
		return objRecveredUpdateSql;
	}

	private volatile ObjByRecidDeleteSql objIdDeleteSql;

	final ObjByRecidDeleteSql getByRecidDeleteSql(DBAdapterImpl dbAdapter) {
		this.ensurePrepared(dbAdapter.getContext(), false);
		ObjByRecidDeleteSql byIdDeleteSql = this.objIdDeleteSql;
		if (byIdDeleteSql == null) {
			synchronized (this) {
				byIdDeleteSql = this.objIdDeleteSql;
				if (byIdDeleteSql == null) {
					this.objIdDeleteSql = byIdDeleteSql = new ObjByRecidDeleteSql(
							dbAdapter.lang, this);
				}
			}
		}
		return byIdDeleteSql;
	}

	private volatile ObjByRecidsDeleteSql objIdsDeleteSql;

	final ObjByRecidsDeleteSql getByRecidsDeleteSql(DBAdapterImpl dbAdapter) {
		this.ensurePrepared(dbAdapter.getContext(), false);
		ObjByRecidsDeleteSql byIdsDeleteSql = this.objIdsDeleteSql;
		if (byIdsDeleteSql == null) {
			synchronized (this) {
				byIdsDeleteSql = this.objIdsDeleteSql;
				if (byIdsDeleteSql == null) {
					this.objIdsDeleteSql = byIdsDeleteSql = new ObjByRecidsDeleteSql(
							dbAdapter.lang, this);
				}
			}
		}
		return byIdsDeleteSql;
	}

	private volatile ObjByLpkDeleteSql objLpkDeleteSql;

	final ObjByLpkDeleteSql getByLpkDeleteSql(DBAdapterImpl dbAdapter) {
		this.ensurePrepared(dbAdapter.getContext(), false);
		ObjByLpkDeleteSql byLpkDeleteSql = this.objLpkDeleteSql;
		if (byLpkDeleteSql == null) {
			synchronized (this) {
				byLpkDeleteSql = this.objLpkDeleteSql;
				if (byLpkDeleteSql == null) {
					this.objLpkDeleteSql = byLpkDeleteSql = new ObjByLpkDeleteSql(
							dbAdapter.lang, this);
				}
			}
		}
		return byLpkDeleteSql;
	}

	private volatile ObjByRecidQuerySql objIdQuerySql;

	final ObjByRecidQuerySql getByRecidQuerySql(DBAdapterImpl dbAdapter) {
		this.ensurePrepared(dbAdapter.getContext(), false);
		ObjByRecidQuerySql byIdQuerySql = this.objIdQuerySql;
		if (byIdQuerySql == null) {
			synchronized (this) {
				byIdQuerySql = this.objIdQuerySql;
				if (byIdQuerySql == null) {
					this.objIdQuerySql = byIdQuerySql = new ObjByRecidQuerySql(
							dbAdapter.lang, this);
				}
			}
		}
		return byIdQuerySql;
	}

	private volatile ObjByLpkQuerySql objLpkQuerySql;

	final ObjByLpkQuerySql getByLpkQuerySql(DBAdapterImpl dbAdapter) {
		this.ensurePrepared(dbAdapter.getContext(), false);
		ObjByLpkQuerySql byLpkQuerySql = this.objLpkQuerySql;
		if (byLpkQuerySql == null) {
			synchronized (this) {
				byLpkQuerySql = this.objLpkQuerySql;
				if (byLpkQuerySql == null) {
					this.objLpkQuerySql = byLpkQuerySql = new ObjByLpkQuerySql(
							dbAdapter.lang, this);
				}
			}
		}
		return byLpkQuerySql;
	}

	// i must say i dont like this guy

}
