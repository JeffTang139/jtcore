package org.eclipse.jt.core.spi.da;

/**
 * 由系统调用，请求DnaSql引擎将dnaSql变异生成QueryDecare;
 * 
 * @author Jeff Tang
 * 
 */
public class NewQueryDeclareByDnaSql extends NewByDnaSql {

	private NewQueryDeclareByDnaSql(String dnaSql) {
		super(dnaSql);
	}
}
