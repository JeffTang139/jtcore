package org.eclipse.jt.core.def;

/**
 * 名称标识的定义的容器接口
 * 
 * @author Jeff Tang
 * 
 * @param <TDefine>
 */
public interface ModifiableNamedElementContainer<TElement extends Namable>
        extends NamedElementContainer<TElement>, ModifiableContainer<TElement> {
	// Nothing
}
