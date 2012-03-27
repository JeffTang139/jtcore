package org.eclipse.jt.core.resource;

/**
 * 资源插入器
 * 
 * @author Jeff Tang
 * 
 * @param <TFacade>
 *            资源外观
 * @param <TImpl>
 *            资源修改器
 * @param <TKeysHolder>
 *            资源键源
 * @param <TResourceMetaData>
 *            资源原数据
 */
public interface ResourceInserter<TFacade, TImpl extends TFacade, TKeysHolder>
		extends ResourcePutter<TFacade, TImpl, TKeysHolder> {
	/**
	 * 获得所属资源的快照
	 * 
	 * @param <TOwnerFacade>所属资源的句柄
	 * @param ownerFacadeClass
	 *            所属资源读取接口类
	 * @return 获得所属资源的快照
	 */
	public <TOwnerFacade> ResourceToken<TOwnerFacade> getOwnerResource(
			Class<TOwnerFacade> ownerFacadeClass);
}