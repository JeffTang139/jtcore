package org.eclipse.jt.core;

import org.eclipse.jt.core.impl.TimeRelatedSequenceImpl;

/**
 * 时间相关的递增序列（服务器簇）<br>
 * <br>
 * 时间相关性序列的特性如下<br>
 * 1. 相同名义下的序列产生的序列值绝不重复<br>
 * 2. 序列具有时间相关性，即序列的值中可以分离出时间信息<br>
 * 3. 相同名义下的不同序列对应不同的簇，簇的分布：数据库内[0]、主服务器[1]、从服务器[2..15]<br>
 * 4. 同名义下不同序列间不严格按照生成序列值的物理时间成序，但序列值以16ms的精度成序 <br>
 * <br>
 * 该功能为了在群集节点以及数据库内部共用一组递增序列值，而无需相互协调（为效率考虑）<br>
 * <br>
 * 序列的结构如下：<br>
 * 1. 时间段使用36位[28..63]，按16毫秒为单位计量，略少于35年后会回绕导致冲突<br>
 * 2. 递增序列使用24位[4..27]，共有16M个的序列空间<br>
 * 3. 簇标识使用4位[0..3]，最多表示16个序列簇<br>
 * <br>
 * <br>
 * 限制<br>
 * 1. 理论上每个序列簇的最大不失真输出功率为16M个/16ms或略多于1亿个/s，实际上实现本身达不到这个速度<br>
 * 2. 序列在使用略少于35年后会回绕导致序列冲突。 <br>
 * 
 * @author Jeff Tang
 * 
 */
public interface TimeRelatedSequence {
	/**
	 * 获得下一个序列值，当前值随之改变
	 */
	public long next();

	/**
	 * 最后一个值，这个值随时都会改变。没有实用价值
	 */
	public long last();

	/**
	 * 帮助器接口
	 * 
	 * @author Jeff Tang
	 * 
	 */
	public static interface Helper {
		/**
		 * 根据序列值返回序列产生时间
		 * 
		 * @return 返回毫秒数
		 */
		public long timeOf(long seq);
	}

	/**
	 * 帮助器实例
	 */
	public static final Helper helper = TimeRelatedSequenceImpl.helper;
}
