package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.exp.HierarchyOperator;

public enum HierarchyOperatorImpl implements HierarchyOperator {

	/**
	 * ָʾ���ڵ�RECID�ı��ʽ
	 */
	PARENT_RECID {

		@Override
		final GUIDType getType() {
			return GUIDType.TYPE;
		}

	},

	/**
	 * ָʾ��Ե�n�����Ƚ��ı��ʽ
	 */
	RELATIVE_ANCESTOR_RECID {

		@Override
		final GUIDType getType() {
			return GUIDType.TYPE;
		}

	},

	/**
	 * ָʾ���Ե�n�����ڵ�ı��ʽ
	 */
	ABUSOLUTE_ANCESTOR_RECID {

		@Override
		final GUIDType getType() {
			return GUIDType.TYPE;
		}

	},

	/**
	 * ָʾ�ڵ㼶����ȵı��ʽ
	 */
	LEVEVL_OF {

		@Override
		final IntType getType() {
			return IntType.TYPE;
		}

	};

	abstract DataTypeBase getType();

}
