package org.eclipse.jt.core.resource;

/**
 * ������������Դ�޸���
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
