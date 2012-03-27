package org.eclipse.jt.core.impl;

import java.util.LinkedList;

import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.type.DataType;


final class SearchedCaseExpr extends ValueExpr {

	public final DataType getType() {
		return this.returnType;
	}

	@Override
	public final String getXMLTagName() {
		return xml_name_searched_case;
	}

	@Override
	public final void render(SXElement element) {
		super.render(element);
		this.whenCondition.renderInto(element.append(xml_element_when));
		this.returnValue.renderInto(element.append(xml_element_return));
		if (this.otherWhens != null) {
			for (int i = 0; i < this.otherWhens.length; i++) {
				this.otherWhens[i].renderInto(element.append(xml_element_when));
				this.otherReturns[i].renderInto(element
						.append(xml_element_return));
			}
		}
		if (this.defaultValue != null) {
			this.defaultValue.renderInto(element.append(xml_element_default));
		}
	}

	@Override
	final String getDescription() {
		return "搜索case语句";
	}

	static final String xml_name_searched_case = "searched-case";
	static final String xml_element_when = "when-condition";
	static final String xml_element_return = "return-value";
	static final String xml_element_default = "default-value";

	final ConditionalExpr whenCondition;
	final ValueExpr returnValue;
	final ConditionalExpr[] otherWhens;
	final ValueExpr[] otherReturns;
	final ValueExpr defaultValue;
	final DataType returnType;

	private SearchedCaseExpr(ConditionalExpr whenCondition,
			ValueExpr returnValue, ConditionalExpr[] otherWhens,
			ValueExpr[] otherReturns, ValueExpr defaultValue) {
		this.whenCondition = whenCondition;
		this.returnValue = returnValue;
		this.otherWhens = otherWhens;
		this.otherReturns = otherReturns;
		this.defaultValue = defaultValue;
		DataType rt = returnValue.getType();
		if (otherReturns != null) {
			for (ValueExpr value : otherReturns) {
				rt = rt.calcPrecedence(value.getType());
			}
		}
		if (defaultValue != null) {
			rt = rt.calcPrecedence(defaultValue.getType());
		}
		this.returnType = rt;
	}

	static final SearchedCaseExpr newSearchedCase(Object whenCondition,
			Object returnValue, Object[] others) {
		if (others == null) {
			return new SearchedCaseExpr((ConditionalExpr) whenCondition,
					ValueExpr.expOf(returnValue), null, null, null);
		} else {
			int other = others.length / 2;
			ValueExpr defaultValue = others.length % 2 == 0 ? null : ValueExpr
					.expOf(others[others.length - 1]);
			if (other == 0) {
				return new SearchedCaseExpr((ConditionalExpr) whenCondition,
						ValueExpr.expOf(returnValue), null, null, defaultValue);
			} else {
				ConditionalExpr[] otherWhens = new ConditionalExpr[other];
				ValueExpr[] otherReturns = new ValueExpr[other];
				for (int i = 0; i < other; i++) {
					if (others[2 * i] == null || others[2 * i + 1] == null) {
						throw new NullPointerException();
					}
					if (others[2 * i] instanceof ConditionalExpr) {
						otherWhens[i] = (ConditionalExpr) others[2 * i];
					} else {
						throw new IllegalArgumentException("Case语句调用参数类型错误.");
					}
					otherReturns[i] = ValueExpr.expOf(others[2 * i + 1]);
				}
				return new SearchedCaseExpr((ConditionalExpr) whenCondition,
						ValueExpr.expOf(returnValue), otherWhens, otherReturns,
						defaultValue);
			}
		}
	}

	static final SearchedCaseExpr newSearchedCase(SXElement element,
			RelationRefOwner refOwner, ArgumentOwner args) {
		LinkedList<ConditionalExpr> whens = new LinkedList<ConditionalExpr>();
		LinkedList<ValueExpr> returns = new LinkedList<ValueExpr>();
		for (SXElement whenElement = element.firstChild(xml_element_when); whenElement != null; whenElement = whenElement
				.nextSibling(xml_element_when)) {
			SXElement returnElement = whenElement.nextSibling();
			whens.add(ConditionalExpr.loadCondition(whenElement.firstChild(),
					refOwner, args));
			if (returnElement == null
					|| !returnElement.name.equals(xml_element_return)) {
				throw new NullPointerException("错误的xml元素");
			}
			returns.add(ValueExpr.loadValue(returnElement.firstChild(),
					refOwner, args));
		}
		ValueExpr defaultValue = null;
		SXElement defaultElement = element.firstChild(xml_element_default);
		if (defaultElement != null) {
			defaultValue = ValueExpr.loadValue(defaultElement.firstChild(),
					refOwner, args);
		}
		int c = whens.size();
		if (c == 0) {
			throw new IllegalArgumentException();
		} else {
			ConditionalExpr whenCondition = whens.get(0);
			ValueExpr returnValue = returns.get(0);
			if (c == 1) {
				return new SearchedCaseExpr(whenCondition, returnValue, null,
						null, defaultValue);
			} else {
				whens.remove(0);
				returns.remove(0);
				ConditionalExpr[] otherWhens = whens
						.toArray(new ConditionalExpr[1]);
				ValueExpr[] otherReturns = returns.toArray(new ValueExpr[1]);
				return new SearchedCaseExpr(whenCondition, returnValue,
						otherWhens, otherReturns, defaultValue);
			}
		}
	}

	static final SearchedCaseExpr newSearchedCase(SearchedCaseExpr sample,
			RelationRef from, RelationRef to, RelationRef fromSample,
			RelationRef toSample) {
		ConditionalExpr newWhen = sample.whenCondition.clone(null, from,
				toSample, to);
		ValueExpr newReturn = sample.returnValue.clone(fromSample, from,
				toSample, to);
		ValueExpr newDefault = null;
		if (sample.defaultValue != null) {
			newDefault = sample.defaultValue.clone(fromSample, from, toSample,
					to);
		}
		if (sample.otherWhens != null) {
			ConditionalExpr[] newOtherWhens = new ConditionalExpr[sample.otherWhens.length];
			ValueExpr[] newOtherReturns = new ValueExpr[sample.otherReturns.length];
			for (int i = 0; i < sample.otherWhens.length; i++) {
				newOtherWhens[i] = sample.otherWhens[i].clone(fromSample, from,
						toSample, to);
				newOtherReturns[i] = sample.otherReturns[i].clone(fromSample,
						from, toSample, to);
			}
			return new SearchedCaseExpr(newWhen, newReturn, newOtherWhens,
					newOtherReturns, newDefault);
		} else {
			return new SearchedCaseExpr(newWhen, newReturn, null, null,
					newDefault);
		}
	}

	@Override
	final ValueExpr clone(RelationRefDomain domain, ArgumentOwner args) {
		ConditionalExpr newWhen = this.whenCondition.clone(domain, args);
		ValueExpr newReturn = this.returnValue.clone(domain, args);
		ValueExpr newDefault = null;
		if (this.defaultValue != null) {
			newDefault = this.defaultValue.clone(domain, args);
		}
		if (this.otherWhens != null) {
			ConditionalExpr[] newOtherWhens = new ConditionalExpr[this.otherWhens.length];
			ValueExpr[] newOtherReturns = new ValueExpr[this.otherReturns.length];
			for (int i = 0; i < this.otherWhens.length; i++) {
				newOtherWhens[i] = this.otherWhens[i].clone(domain, args);
				newOtherReturns[i] = this.otherReturns[i].clone(domain, args);
			}
			return new SearchedCaseExpr(newWhen, newReturn, newOtherWhens,
					newOtherReturns, newDefault);
		} else {
			return new SearchedCaseExpr(newWhen, newReturn, null, null,
					newDefault);
		}
	}

	@Override
	final SearchedCaseExpr clone(RelationRef fromSample, RelationRef from,
			RelationRef toSample, RelationRef to) {
		return newSearchedCase(this, from, to, fromSample, toSample);
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitSearchedCase(this, context);
	}

	@Override
	final void render(ISqlExprBuffer buffer, TableUsages usages) {
		this.whenCondition.render(buffer, usages);
		this.returnValue.render(buffer, usages);
		int c = 2;
		if (this.otherWhens != null) {
			for (int i = 0, l = this.otherWhens.length; i < l; i++) {
				this.otherWhens[i].render(buffer, usages);
				this.otherReturns[i].render(buffer, usages);
				c += 2;
			}
		}
		if (this.defaultValue != null) {
			this.defaultValue.render(buffer, usages);
			c++;
		}
		buffer.searchedCase(c);
	}
}
