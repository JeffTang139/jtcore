package org.eclipse.jt.core.def;

/**
 * ���Ʊ�ʶ�Ķ���������ӿ�
 * 
 * @author Jeff Tang
 * 
 * @param <TDefine>
 */
public interface ModifiableNamedElementContainer<TElement extends Namable>
        extends NamedElementContainer<TElement>, ModifiableContainer<TElement> {
	// Nothing
}
