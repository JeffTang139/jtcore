package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.type.Digester;

/**
 * ��¼�ṹ���壬һ�������Ͳ��ܸ��ģ���˱���ȵ�query�ֶ�ȷ�����ٴ���
 * 
 * @author Jeff Tang
 * 
 */
final class RecordStructDefine extends StructDefineImpl {

	@Override
	final String structTypeNamePrefix() {
		return "record:";
	}

	@Override
	public final void digestType(Digester digester) {
		throw new UnsupportedOperationException();
	}

	static {
		// DataTypeUndigester.regUndigester(new DataTypeUndigester(
		// TypeCodeSet.XX) {
		// @Override
		// protected DataType doUndigest(Undigester undigester)
		// throws IOException, StructDefineNotFoundException {
		// return undigestType(undigester);
		// }
		// });
	}

	RecordStructDefine(QueryStatementImpl statement) {
		super(statement.name, DynObj.class);
		for (int i = 0, c = statement.columns.size(); i < c; i++) {
			QueryColumnImpl column = statement.columns.get(i);
			StructFieldDefineImpl field = new StructFieldDefineImpl(this,
					column.name, statement.tryGetDeterminateColumnType(i));
			this.fields.add(field);
			column.field = field;
		}
		this.prepareAccessInfo();
	}
}
