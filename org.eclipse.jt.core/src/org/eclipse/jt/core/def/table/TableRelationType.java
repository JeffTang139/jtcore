package org.eclipse.jt.core.def.table;

/**
 * 关系
 * 
 * @author Jeff Tang
 * 
 */
public enum TableRelationType {

	/**
	 * 参考：前者参考后者，后者参考前者，
	 */
	REFERENCE,
	/**
	 * 依赖：前者依赖后者，即前者的生命周期被后者包含
	 */
	DEPENDENCE,
	/**
	 * 拥有：前者拥有后者，被后者依赖，前者管理后者的生命周期。
	 */
	OWNERSHIP,
	/**
	 * 共生：两者互相依赖，同时出现，同时消亡
	 */
	SYMBIOSIS
}