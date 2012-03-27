package org.eclipse.jt.core.impl;

/**
 * ÈÝÆ÷¼àÌýÆ÷
 * 
 * @author Jeff Tang
 * 
 */
interface ContainerListener {

	public void beforeMoving(ContainerImpl<?> container, int from, int to);

	public void beforeRemoving(ContainerImpl<?> container, int index);

	public void beforeRemoving(ContainerImpl<?> container, Object o);

	public void beforeClearing(ContainerImpl<?> container);

}
