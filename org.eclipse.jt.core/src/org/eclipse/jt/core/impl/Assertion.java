/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File Assertion.java
 * Date 2009-2-26
 */
package org.eclipse.jt.core.impl;

/**
 * 断言。主要目的是在运行过程中发现潜在的问题（对于在测试阶段验证程序的正确性有极大帮助）。<br/>
 * 用于模拟Java的断言机制。<br/>
 * 方便之处在于启动程序时不需要打开相应的开关。<br/>
 * 不利之处在于无法短路一些可能并无严重影响的判断。所以应慎用。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
// XXX 正式发布代码前，可考虑将ASSERT方法中的内容注释掉。
public final class Assertion {
    private Assertion() {
    }

    /**
     * 断言真值表达式的值为真（<code>true</code>）。<br/>
     * 如果真值表达式的值为真，则什么都不会发生，就相当没有调用过该方法。<br/>
     * 如果真值表达式的值为假，则抛出断言错误，错误信息就是“断言错误”。
     * 
     * @param exp
     *            断言为真的真值表达式。
     */
    public static void ASSERT(boolean exp) {
        if (!exp) {
            throw new AssertionError("断言错误");
        }
    }

    /**
     * 断言真值表达式的值为真（<code>true</code>）。<br/>
     * 如果真值表达式的值为真，则什么都不会发生，就相当没有调用过该方法。<br/>
     * 如果真值表达式的值为假，则抛出断言错误，错误信息就是方法参数中给定的失败信息。<br/>
     * 这个失败信息可用于描述断言失败时现场的情况。
     * 
     * @param exp
     *            断言为真的真值表达式。
     * @param failedMsg
     *            失败信息，用于描述断言失败时现场的相关情况。
     */
    public static void ASSERT(boolean exp, String failedMsg) {
        if (!exp) {
            throw new AssertionError(failedMsg);
        }
    }

    /**
     * 断言真值表达式的值为真（<code>true</code>）。<br/>
     * 如果真值表达式的值为真，则什么都不会发生，就相当没有调用过该方法。<br/>
     * 如果真值表达式的值为假，则抛出断言错误，错误信息就是方法参数中给定的失败信息对象所携带的信息。<br/>
     * 这个失败信息可用于描述断言失败时现场的情况。
     * 
     * @param exp
     *            断言为真的真值表达式。
     * @param failedMsgObj
     *            失败信息对象，用于描述断言失败时现场的相关情况的对象。
     */
    public static void ASSERT(boolean exp, Object failedMsgObj) {
        if (!exp) {
            throw new AssertionError(failedMsgObj == null ? "null"
                    : failedMsgObj.toString());
        }
    }
}
