package org.eclipse.jt.core.resource;

/**
 * 可设置类别的资源修改器
 * 
 * @author Jeff Tang
 * 
 * @param <TFacade>
 * @param <TImpl>
 * @param <TKeysHolder>
 */
public interface CategorialResourceModifier<TFacade, TImpl extends TFacade, TKeysHolder>
        extends ResourceModifier<TFacade, TImpl, TKeysHolder>,
        CategorialResourceQuerier {

}
