package org.eclipse.jt.core;

import java.util.Comparator;

/**
 * 由于考虑不周，多派生出该接口<br>
 * 请使用java.util.Comparator<TItem>借口替代本接口，方法实现不用做改动。
 * 
 * @author Jeff Tang
 * 
 * @param <TItem>
 */
@Deprecated
public interface SortComparator<TItem> extends Comparator<TItem> {
}
