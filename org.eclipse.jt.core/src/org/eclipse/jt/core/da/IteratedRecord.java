package org.eclipse.jt.core.da;

/**
 * 被迭代的记录对象
 * 
 * @author Jeff Tang
 * 
 */
public interface IteratedRecord {

	RecordSetFieldContainer<? extends ReadOnlyRecordSetField> getFields();
}
