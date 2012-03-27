package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.MissingDefineException;
import org.eclipse.jt.core.def.query.DerivedQueryDeclare;
import org.eclipse.jt.core.exception.InvalidDerivedQueryDomainException;

/**
 * ������ѯ����(������ͼ)
 * 
 * <p>
 * ���ṩfrom�Ӿ估with�Ӿ�ʹ��
 * 
 * @author Jeff Tang
 * 
 */
final class DerivedQueryImpl extends
		SelectImpl<DerivedQueryImpl, DerivedQueryColumnImpl> implements
		DerivedQueryDeclare {

	@Override
	public final String getXMLTagName() {
		return xml_name;
	}

	static final String xml_name = "derived-query";

	/**
	 * �����dq����
	 * 
	 * <p>
	 * ���Ƕ��嵱ǰdqΪwith,��������dqֻ��ʹ���ڸ����from,union�Ӿ���.
	 * <p>
	 * �ϸ��Ͻ�,dq��owner�����Ͳ�Ӧ����RelationRefDomain,��Ŀǰ����ʹ�÷�Χ��ȫһ��.
	 * 
	 */
	final RelationRefDomain owner;

	DerivedQueryImpl(RelationRefDomain owner) {
		this(owner, "derived-query", false);
	}

	DerivedQueryImpl(RelationRefDomain owner, String name, boolean with) {
		super(name);
		this.owner = owner;
		this.isWith = with;
	}

	final boolean isWith;

	@Override
	protected final DerivedQueryColumnImpl newColumnOnly(String name,
			ValueExpr value) {
		return new DerivedQueryColumnImpl(this, name, value);
	}

	public final RelationRefDomain getDomain() {
		return this.owner.getDomain();
	}

	public final DerivedQueryImpl getWith(String name) {
		if (this.isWith) {
			return this.fromWithCanOnlyAccessBefore(name);
		} else {
			return this.owner.getWith(name);
		}
	}

	final DerivedQueryImpl fromWithCanOnlyAccessBefore(String name) {
		NamedDefineContainerImpl<DerivedQueryImpl> withs = ((Withable) this.owner)
				.getWiths();
		for (int i = 0, c = withs.size(); i < c; i++) {
			DerivedQueryImpl with = withs.get(i);
			if (with == this) {
				throw new IllegalArgumentException();
			}
			if (with.name == name || with.name.equals(name)) {
				return with;
			}
		}
		throw new MissingDefineException();
	}

	final void validateDomain(RelationRefDomain from) {
		if (this.isWith) {
			DerivedQueryImpl dq = from.getWith(this.name);
			if (dq != this) {
				throw new InvalidDerivedQueryDomainException();
			}
		} else if (this.owner != from) {
			throw new InvalidDerivedQueryDomainException();
		}
	}

}
