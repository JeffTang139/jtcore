package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.arg.ArgumentRefExpression;
import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.type.DataType;

/**
 * 参数引用表达式实现
 * 
 * @author Jeff Tang
 * 
 */
final class ArgumentRefExpr extends ValueExpr implements ArgumentRefExpression {

	@Override
	public final String getDescription() {
		return "参数引用表达式";
	}

	public final StructFieldDefineImpl getArgument() {
		return this.arg;
	}

	public final DataType getType() {
		return this.determined == null ? this.arg.type : this.determined;
	}

	@Override
	public final String toString() {
		return "@".concat(this.arg.name);
	}

	@Override
	public final String getXMLTagName() {
		return xml_name_arg_ref;
	}

	@Override
	public final void render(SXElement element) {
		super.render(element);
		element.setAttribute(xml_attr_arg_name, this.arg.name);
	}

	static final String xml_name_arg_ref = "argument-ref";
	static final String xml_attr_arg_name = "argument";

	final StructFieldDefineImpl arg;

	ArgumentRefExpr(StructFieldDefineImpl arg) {
		if (arg == null) {
			throw new NullPointerException();
		}
		this.arg = arg;
		if (!(arg.getType() instanceof EnumTypeImpl<?>)) {
			this.determined = arg.type;
		}
	}

	@Override
	final ValueExpr clone(RelationRefDomain domain, ArgumentOwner args) {
		if (args == null) {
			throw new NullPointerException();
		}
		return new ArgumentRefExpr(args.getArgument(this.arg.name));
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitArgumentRefExpr(this, context);
	}

	@Override
	final ValueExpr clone(RelationRef fromSample, RelationRef from,
			RelationRef toSample, RelationRef to) {
		throw new UnsupportedOperationException();
	}

	// CORE2.5
	final void determineType(DataType type) {
		if (type == null) {
			throw new NullPointerException();
		}
		if (!type.isDBType()) {
			throw new UnsupportedOperationException();
		}
		if (this.determined == null) {
			this.determined = type;
		} else if (this.determined != type) {
			throw new UnsupportedOperationException();
		}
	}

	final DataType getDeterminedType() {
		return this.determined;
	}

	private DataType determined;

	@Override
	final void render(ISqlExprBuffer buffer, TableUsages usages) {
		buffer.loadVar(new ArgumentReserver(this.arg, this.getDeterminedType()));
	}
}
