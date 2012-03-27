package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.query.SetOperator;

/**
 * 集合运算符
 * 
 * @author Jeff Tang
 * 
 */
public enum SetOperatorImpl implements SetOperator {

	/**
	 * 与
	 */
	UNION {

		@Override
		protected boolean unionAll() {
			return false;
		}
	},

	/**
	 * 与
	 */
	UNION_ALL {

		@Override
		protected boolean unionAll() {
			return true;
		}

	},

	/**
	 * 差
	 */
	DIFFERENCE {

	},

	/**
	 * 交
	 */
	INTERSECT {

	};

	protected boolean unionAll() {
		throw new UnsupportedOperationException();
	}

}
