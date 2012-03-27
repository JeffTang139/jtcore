package org.eclipse.jt.core.def.table;

import org.eclipse.jt.core.def.DefineBase;

/**
 * Ë÷Òı×Ö¶Î¶¨Òå
 * 
 * @author Jeff Tang
 * 
 */
public interface IndexItemDefine extends DefineBase {
	/**
	 * ÅÅĞò×Ö¶Î
	 * 
	 * @return ·µ»ØÅÅĞò×Ö¶Î
	 */
	public TableFieldDefine getField();

	/**
	 * ÊÇ·ñÊÇÉıĞò
	 * 
	 * @return ·µ»Ø
	 */
	public boolean isDesc();
}
