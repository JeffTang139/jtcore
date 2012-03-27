/**
 * Copyright (C) 2007-2008 JeffTang Software Co., Ltd. All rights reserved.
 *
 * File SortUtil.java
 * Date 2008-9-9
 */
package org.eclipse.jt.core.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.impl.Unsf;


/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public final class SortUtil {

    static final long ARRAYLIST_ELEMENTDATA_OFFSET = tryGetFieldOffset(
            ArrayList.class, "elementData");

    private SortUtil() {
    }

    private static long tryGetFieldOffset(Class<?> clazz, String fieldName) {
        if (Unsf.unsafe != null) {
            try {
                return Unsf.unsafe.objectFieldOffset(clazz
                        .getDeclaredField(fieldName));
            } catch (Throwable e) {
                return 0;
            }
        }
        return 0;
    }

    private static <TItem> int qsPartition(final TItem[] array, int low,
            int high, final Comparator<? super TItem> comparator) {
        TItem pivot = array[low];
        while (low < high) {
            while (low < high && comparator.compare(array[high], pivot) >= 0) {
                --high;
            }
            array[low] = array[high];
            while (low < high && comparator.compare(array[low], pivot) <= 0) {
                ++low;
            }
            array[high] = array[low];
        }
        array[low] = pivot;
        return low;
    }

    private static <TItem> void quickSort(final TItem[] array, final int low,
            final int high, final Comparator<? super TItem> comparator) {
        if (low < high) {
            int pivotLoc = qsPartition(array, low, high, comparator);
            quickSort(array, low, pivotLoc - 1, comparator);
            quickSort(array, pivotLoc + 1, high, comparator);
        }
    }

    @SuppressWarnings("unchecked")
    public static <TItem> void sort(final List<TItem> list,
            final Comparator<? super TItem> comparator) {
        if (list == null) {
            return;
        }
        int size = list.size();
        if (size <= 1) {
            return;
        }
        if (comparator == null) {
            throw new NullArgumentException("comparator");
        }

        if (ARRAYLIST_ELEMENTDATA_OFFSET > 0 && list instanceof ArrayList<?>) {
            TItem[] elements;
            try {
                elements = (TItem[]) Unsf.unsafe.getObject(list,
                        ARRAYLIST_ELEMENTDATA_OFFSET);
            } catch (Throwable e) {
                elements = null;
            }
            if (elements != null) {
                // quickSort(elements, 0, list.size() - 1, comparator);
                Arrays.sort(elements, 0, size, comparator);
                return;
            }
        }

        Collections.sort(list, comparator);
    }

    /**
     * [fromIndex, toIndex)
     */
    public static <TItem> void sort(final TItem[] array, int fromIndex,
            int toIndex, final Comparator<? super TItem> comparator) {
        if (array == null || array.length <= 1) {
            return;
        }
        if (comparator == null) {
            throw new NullArgumentException("comparator");
        }
        Arrays.sort(array, fromIndex, toIndex, comparator);

        // if (fromIndex > toIndex) {
        // throw new IllegalArgumentException("fromIndex(" + fromIndex
        // + ") > toIndex(" + toIndex + ")");
        // }
        // if (fromIndex < 0) {
        // throw new ArrayIndexOutOfBoundsException(fromIndex);
        // }
        // if (toIndex > array.length) {
        // throw new ArrayIndexOutOfBoundsException(toIndex);
        // }
        // if (toIndex - fromIndex <= 1) {
        // return;
        // }
        //
        // quickSort(array, fromIndex, toIndex - 1, comparator);
    }
}
