/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File Category.java
 * Date May 13, 2009
 */
package org.eclipse.jt.core;

import org.eclipse.jt.core.type.GUID;

/**
 * ���ӿڡ�<br/>
 * 
 * �����һ����Ϊһ�ֱ�ʶ��ʹ�ã���Ҫ���ڶ�һЩ����ͬ�Ķ������߼��ϵķ��飬���������������Щ�����Ψһ��ʶ��
 * ������˵�ġ���𡱲�����ȫָ�á����ӿڡ�������Ķ��󣬶���һ��һ�������ϵ��߼����
 * 
 * �����Ϊһ�ֱ�ʶ������һ��������Ҫ��ģ���Ҫԭ���Ǳ���ʹ�ã�ͬʱҪ֧��D&A-Core���������������л���
 * ������������ԭʼ���͵�װ�����ͣ�����ö�١��ַ�����GUID���͡��������������Щ�����е�һ�֣����������Ķ������Ҫʵ�ָ����ӿڡ�
 * 
 * ���ӿ�Ҫ��ʵ�����б����ṩһ����Ч�ı�ʶ���������ʶ�������;ͱ�����ԭʼ���͵�װ�����ͣ���ö�١��ַ�����GUID�����е�һ�֡�
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public interface Category {
    /**
     * ��ȡ���ı�ʶ�������صı�ʶ������Ϊ�գ�<code>null</code>����
     * 
     * <pre>
     *  ��Ч�ı�ʶ�����������������е�һ�֣�
     *      1. �ַ�����
     *      2. �κ�ö�����ͣ�
     *      3. ԭʼ���͵�װ�����ͣ�
     *      4. org.eclipse.jt.core.type.GUID��
     * </pre>
     * 
     * @return ��Ч������ʶ��������Ϊ�գ�<code>null</code>����
     */
    Object getIdentifier();

    // /////////////////////////////////////////////////////////////////////////

    /**
     * �����������ﲢ�����ʣ���ĿǰΪ�˱���ά������֤���������һ���ԣ���ʱ�������<br/>
     * �ϲ㿪������ʹ����Դ���ʱ����Ӧʹ�����������񷽷������ԡ�
     * 
     * @author Jeff Tang
     * @version 2009-05-11
     */
    public static final class Helper {
        private Helper() {
        }

        /**
         * ֧�ֵ����Ͳμ� <code>org.eclipse.jt.core.Category.getIdentifier()</code>
         * ������ע�͡�
         * 
         * @param idType
         *            Ҫ���ı�ʶ�������͡�
         * @return ָ�����͵Ķ����Ƿ������Ϊ��Դ���ı�ʶ����
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
