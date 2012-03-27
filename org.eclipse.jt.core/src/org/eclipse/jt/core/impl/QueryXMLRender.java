package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.misc.SXElement;

final class QueryXMLRender {

	public final void visitSelect(SelectImpl<?, ?> select) {
		// super.render(element);
		// this.rootRelationRef.rendTreeInto(element
		// .append(xml_element_references));
		// element.setBoolean(xml_attr_distinct, this.distinct);
		// this.columns.renderInto(element, xml_element_columns, 0);
		// if (this.where != null) {
		// this.where.renderInto(element.append(xml_element_condition));
		// }
		// element.setEnum(xml_attr_groupby_type, this.groupbyType);
		// if (this.groupbys != null) {
		// this.groupbys.renderInto(element, xml_element_groupbys, 0);
		// }
		// if (this.having != null) {
		// this.having.renderInto(element.append(xml_element_having));
		// }
		// if (this.orderbys != null) {
		// this.orderbys.renderInto(element, xml_element_orders, 0);
		// }
	}

	static final String xml_element_columns = "columns";
	static final String xml_element_references = "references";
	static final String xml_element_condition = "condition";
	static final String xml_element_orders = "orders";
	static final String xml_element_groupbys = "groupbys";
	static final String xml_element_having = "having";
	static final String xml_attr_distinct = "distinct";
	static final String xml_attr_groupby_type = "groupby-type";

	public void render(SXElement element) {

	}
}
