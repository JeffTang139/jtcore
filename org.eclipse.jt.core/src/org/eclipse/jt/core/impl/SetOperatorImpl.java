package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.query.SetOperator;

/**
 * ���������
 * 
 * @author Jeff Tang
 * 
 */
public enum SetOperatorImpl implements SetOperator {

	/**
	 * ��
	 */
	UNION {

		@Override
		protected boolean unionAll() {
			return false;
		}
	},

	/**
	 * ��
	 */
	UNION_ALL {

		@Override
		protected boolean unionAll() {
			return true;
		}

	},

	/**
	 * ��
	 */
	DIFFERENCE {

	},

	/**
	 * ��
	 */
	INTERSECT {

	};

	protected boolean unionAll() {
		throw new UnsupportedOperationException();
	}

}
