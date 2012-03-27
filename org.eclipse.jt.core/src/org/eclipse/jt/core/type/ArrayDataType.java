package org.eclipse.jt.core.type;

public interface ArrayDataType extends ObjectDataType {
	public DataType getComponentType();

	public Class<?> getComponentJavaClass();

	public boolean isPrimitive();
}
