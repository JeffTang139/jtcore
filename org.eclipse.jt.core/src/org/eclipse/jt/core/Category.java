/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File Category.java
 * Date May 13, 2009
 */
package org.eclipse.jt.core;

import org.eclipse.jt.core.type.GUID;

/**
 * 类别接口。<br/>
 * 
 * “类别”一般作为一种标识来使用，主要用于对一些“相同的对象”做逻辑上的分组，而类别对象就用作这些分组的唯一标识。
 * 这里所说的“类别”并不完全指该“类别接口”所定义的对象，而是一个一般意义上的逻辑概念。
 * 
 * 类别作为一种标识，是有一定的类型要求的，主要原则是便于使用，同时要支持D&A-Core框架中所定义的序列化。
 * 所以类别可以是原始类型的装箱类型，或着枚举、字符串、GUID类型。如果不是上述这些类型中的一种，用于作类别的对象就需要实现该类别接口。
 * 
 * 类别接口要求实现类中必须提供一个有效的标识符，这个标识符的类型就必须是原始类型的装箱类型，或枚举、字符串、GUID类型中的一种。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public interface Category {
    /**
     * 获取类别的标识符，返回的标识符不得为空（<code>null</code>）。
     * 
     * <pre>
     *  有效的标识符必须是以下类型中的一种：
     *      1. 字符串；
     *      2. 任何枚举类型；
     *      3. 原始类型的装箱类型；
     *      4. org.eclipse.jt.core.type.GUID。
     * </pre>
     * 
     * @return 有效的类别标识符，不得为空（<code>null</code>）。
     */
    Object getIdentifier();

    // /////////////////////////////////////////////////////////////////////////

    /**
     * 这个类放在这里并不合适，但目前为了便于维护，保证各处代码的一致性，暂时放在这里。<br/>
     * 上层开发者在使用资源类别时，不应使用这个类的任务方法或属性。
     * 
     * @author Jeff Tang
     * @version 2009-05-11
     */
    public static final class Helper {
        private Helper() {
        }

        /**
         * 支持的类型参见 <code>org.eclipse.jt.core.Category.getIdentifier()</code>
         * 方法的注释。
         * 
         * @param idType
         *            要检查的标识符的类型。
         * @return 指定类型的对象是否可以作为资源类别的标识符。
         */
        public static boolean isSupportedIdType(Class<?> idType) {
            if (idType == null) {
                return false;
            }
            return (idType == String.class
                    || Enum.class.isAssignableFrom(idType)
                    || idType == GUID.class || idType == Integer.class
                    || idType == Long.class || idType == Short.class
                    || idType == Byte.class || idType == Character.class
                    || idType == Double.class || idType == Float.class || idType == Boolean.class);
        }
    }
}
