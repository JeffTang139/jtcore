package org.eclipse.jt.core.resource;

import org.eclipse.jt.core.impl.ResourceTokenMissing;

/**
 * 资源标识
 * 
 * 
 * @param <TFacade>
 */
public interface ResourceToken<TFacade> extends ResourceStub<TFacade> {
    /**
     * 获得父极资源标识(资源树上级，父资源，被引用资源)
     * 
     * @param <TSuperFacade>
     *            父极资源外观类型
     * @param superTokenFacadeClass
     *            父极资源外观类
     * @return 返回父极资源标识，顶层资源返回null
     * @throws IllegalArgumentException
     *             不存在这样的父极资源则抛出异常
     */
    public <TSuperFacade> ResourceToken<TSuperFacade> getSuperToken(
            Class<TSuperFacade> superTokenFacadeClass)
            throws IllegalArgumentException;

    /**
     * 返回某类直接下级资源的链表(资源树下级，子资源，引用资源)
     * 
     * @param <TSubFacade>
     *            子类外观
     * @param subTokenFacadeClass
     *            子类外观类
     * @return 返回第一个子链节点，或者null表示没有孩子
     * @throws IllegalArgumentException
     *             不存在这样的父极资源则抛出异常
     */
    public <TSubFacade> ResourceTokenLink<TSubFacade> getSubTokens(
            Class<TSubFacade> subTokenFacadeClass)
            throws IllegalArgumentException;

    /**
     * 返回本类资源的直接下级链表(资源树下级)
     * 
     * @return 返回第一个子链节点，或者null表示没有孩子
     */
    public ResourceTokenLink<TFacade> getChildren();

    /**
     * 获得本类资源的父节点(资源树上级)
     * 
     * @return 返回父标识或者null
     */
    public ResourceToken<TFacade> getParent();

    /**
     * 空标识（无法定位资源值）
     */
    @SuppressWarnings("unchecked")
    public static final ResourceToken MISSING = ResourceTokenMissing.TOKEN;
}
