package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.exp.HierarchyOperator;

public enum HierarchyOperatorImpl implements HierarchyOperator {

	/**
	 * 指示父节点RECID的表达式
	 */
	PARENT_RECID {

		@Override
		final GUIDType getType() {
			return GUIDType.TYPE;
		}

	},

	/**
	 * 指示相对第n级祖先结点的表达式
	 */
	RELATIVE_ANCESTOR_RECID {

		@Override
		final GUIDType getType() {
			return GUIDType.TYPE;
		}

	},

	/**
	 * 指示绝对第n级父节点的表达式
	 */
	ABUSOLUTE_ANCESTOR_RECID {

		@Override
		final GUIDType getType() {
			return GUIDType.TYPE;
		}

	},

	/**
	 * 指示节点级次深度的表达式
	 */
	LEVEVL_OF {

		@Override
		final IntType getType() {
			return IntType.TYPE;
		}

	};

	abstract DataTypeBase getType();

}
