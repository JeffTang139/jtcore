package org.eclipse.jt.core.spi.da;

import org.eclipse.jt.core.def.obja.StructDefine;
import org.eclipse.jt.core.exception.NullArgumentException;

/**
 * ��ϵͳ���ã�����DnaSql���潫dnaSql��������MappingQueryDecare;
 * 
 * @author Jeff Tang
 * 
 */
public class NewORMByDnaSql extends NewByDnaSql {
	public final StructDefine structDefine;

	private NewORMByDnaSql(String dnaSql, StructDefine structDefine) {
		super(dnaSql);
		if (structDefine == null) {
			throw new NullArgumentException("structDefine");
		}
		this.structDefine = structDefine;
	}
}
