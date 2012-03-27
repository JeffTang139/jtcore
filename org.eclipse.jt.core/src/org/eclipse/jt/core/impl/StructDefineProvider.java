/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File StructDefineProvider.java
 * Date 2009-3-12
 */
package org.eclipse.jt.core.impl;

/**
 * 结构定义提供器。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
interface StructDefineProvider {
    /**
     * 根据指定的结构定义摘要信息，返回相应的结构定义适配对象。
     * 
     * @param structSummary
     *            结构定义摘要信息。
     * @return 结构定义适配对象。
     * @throws StructDefineNotFoundException
     *             找不到相关的结构定义对象。
     */
    StructAdapter getStructDefine(StructSummary structSummary)
            throws StructDefineNotFoundException;
}
