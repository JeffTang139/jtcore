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
 * ������ӳ��Ĳ�ѯ,��ÿ����ѯ����а󶨵�һ��javaʵ���������.
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
	 * �Ƿ��Զ���
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
				System.err.println("��ORM����[" + this.getName() + "]��,Java�ֶ�["
						+ field.getName() + "]������["
						+ field.getType().toString() + "]���ܰ󶨵�����Ϊ["
						+ column.getType() + "]���������.");
			}
		}
		column.field = field;
		return column;
	}

	/**
	 * ����ӳ��Ķ���
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
					throw new IllegalArgumentException("ʵ�幤��������ʵ�����Ͳ���");
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
				throw new IllegalArgumentException("�ڰ󶨲�ѯ����[" + this.name
						+ "]��,�ѽ���ѯ�����[" + bounded.name + "]�󶨵�ʵ���ֶ�["
						+ javaFieldName + "],�������ٽ���ѯ�����[" + column.name
						+ "]�󶨵���ʵ���ֶ���.");
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
			// ��δ���ֶ�
			if (mapped.containsKey(javaField.name)) {
				continue;
			}
			for (QuRelationRef relaitonRef : this.rootRelationRef()) {
				if (!(relaitonRef instanceof QuTableRef)) {
					continue;
				}
				QuTableRef tableRef = (QuTableRef) relaitonRef;
				// �ñ�����Ŀ���Ƿ���ͬ���ı��ֶ�
				TableFieldDefineImpl tf = tableRef.getTarget().fields
						.find(javaField.name);
				if (tf != null) {
					AssignCapability ac = javaField.type.isAssignableFrom(tf
							.getType());
					// ���ֶ�������ת����java�ֶ�����
					if (ac == AssignCapability.NO) {
						continue;
					}
					// �Ҹ������µĸ��ֶλ�δ���
					if (this.findColumn(tableRef, tf) == null) {
						// �������
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
				+ "]�ĸ����ò�֧�ָ���.");
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
