package org.eclipse.jt.core.def.info;

/**
 * 含有关键字的抽象接口
 * 
 * @author Jeff Tang
 * 
 */
public interface Keyworded {
	/**
	 * 添加关键字
	 * 
	 * @param keyword
	 *            关键字
	 */
	public void addKeyword(Enum<?> keyword);

	/**
	 * 设置某类关键字
	 * 
	 * @param <TKeyword>
	 *            关键字类型
	 * @param one
	 *            第一个关键字
	 * @param others
	 *            其余的关键字
	 */
	public <TKeyword extends Enum<TKeyword>> void setKeywords(TKeyword one,
			TKeyword... others);

	/**
	 * 设置某类关键字
	 * 
	 * @param <TKeyword>
	 *            关键字类型
	 * @param one
	 *            第一个关键字
	 * @param others
	 *            其余的关键字
	 */
	public <TKeyword extends Enum<TKeyword>> void setKeywords(TKeyword one);

	/**
	 * 移除某类关键字
	 * 
	 * @param keywordType
	 *            关键字类
	 */
	public void removeKeywords(Class<? extends Enum<?>> keywordType);

	/**
	 * 移除关键字
	 * 
	 * @return 返回之前该关键字是否存在
	 */
	public boolean removeKeyword(Enum<?> keyword);
}
