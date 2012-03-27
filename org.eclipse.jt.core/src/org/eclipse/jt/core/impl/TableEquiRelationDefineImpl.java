package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.ObjectQuerier;
import org.eclipse.jt.core.def.table.TableEquiRelationDeclare;
import org.eclipse.jt.core.def.table.TableFieldDefine;
import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.misc.SXMergeHelper;

/**
 * 等值表关系
 * 
 * @author Jeff Tang
 */
@SuppressWarnings("deprecation")
final class TableEquiRelationDefineImpl extends TableRelationDefineImpl
		implements TableEquiRelationDeclare {

	public final TableFieldDefineImpl getSelfField() {
		return this.selfField;
	}

	public final TableFieldDefineImpl getTargetField() {
		return this.targetField;
	}

	public final void setSelfField(TableFieldDefine selfField) {
		TableFieldDefineImpl f = (TableFieldDefineImpl) selfField;
		if (f.owner != this.owner) {
			throw new IllegalArgumentException();
		}
		if (f != this.selfField) {
			this.selfField = f;
			this.rebuildCondition();
		}
	}

	public final void setTargetField(TableFieldDefine targetField) {
		TableFieldDefineImpl f = (TableFieldDefineImpl) targetField;
		if (f.owner != this.target) {
			throw new IllegalArgumentException();
		}
		if (f != this.targetField) {
			this.targetField = f;
			this.rebuildCondition();
		}
	}

	@Override
	public final boolean isEquiRelation() {
		return true;
	}

	@Override
	public final TableFieldDefineImpl getEquiRelationSelfField() {
		return this.selfField;
	}

	@Override
	public final TableFieldDefineImpl getEquiRelationTargetField() {
		return this.targetField;
	}

	@Override
	public final TableEquiRelationDefineImpl castAsEquiRelation() {
		return this;
	}

	@Override
	public final String getXMLTagName() {
		return xml_name_equi_relation;
	}

	@Override
	public final void render(SXElement element) {
		super.render(element);
		element.setString(xml_attr_target_field, this.targetField.name);
		element.setString(xml_attr_self_field, this.selfField.name);
	}

	static final String xml_name_equi_relation = "equi-relation";
	static final String xml_attr_target_field = "target-field";
	static final String xml_attr_self_field = "self-field";

	private TableFieldDefineImpl selfField;

	private TableFieldDefineImpl targetField;

	TableEquiRelationDefineImpl(TableDefineImpl owner, String name,
			TableFieldDefineImpl selfField, TableDefineImpl target,
			TableFieldDefineImpl targetField) {
		super(owner, name, target);
		this.selfField = selfField;
		this.targetField = targetField;
		this.condition = this.owner.selfRef.expOf(selfField).xEq(
				this.expOf(targetField));
	}

	TableEquiRelationDefineImpl(TableDefineImpl owner,
			TableEquiRelationDefineImpl sample, ObjectQuerier querier) {
		super(owner, sample, querier);
		this.selfField = owner.fields.get(sample.selfField.name);
		this.targetField = owner.fields.get(sample.targetField.name);
	}

	private synchronized final void rebuildCondition() {
		this.condition = this.owner.selfRef.expOf(this.selfField).xEq(
				this.expOf(this.targetField));
	}

	@Override
	final void merge(SXElement element, SXMergeHelper helper) {
		super.merge(element, helper);
		this.selfField = this.owner.fields.get(element
				.getString(xml_attr_self_field));
		this.targetField = this.target.fields.get(element
				.getString(xml_attr_target_field));
	}

	@Override
	final TableEquiRelationDefineImpl clone(TableDefineImpl owner,
			ObjectQuerier querier) {
		return new TableEquiRelationDefineImpl(owner, this, querier);
	}

}
