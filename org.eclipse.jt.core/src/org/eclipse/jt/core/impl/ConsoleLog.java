/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File TempLog.java
 * Date 2009-6-12
 */
package org.eclipse.jt.core.impl;

import static java.lang.System.err;
import static java.lang.System.out;

import java.util.Date;

/**
 * Print messages onto console.
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public final class ConsoleLog {
    public static String now() {
        return String.format("%1$tY-%1$tm-%1$td %1$tT.%1$tL", new Date());
    }

    public static void info(String info) {
        out.format("%s INFO - %s%n", now(), info);
    }

    public static void info(String format, Object... args) {
        out.format("%s INFO - %s%n", now(), String.format(format, args));
    }

    public static void init(String info) {
        out.format("%s INIT - %s%n", now(), info);
    }

    public static void init(String format, Object... args) {
        out.format("%s INIT - %s%n", now(), String.format(format, args));
    }

    public static void debugError(String info) {
        err.format("%s INFO [DEBUG] - %s%n", now(), info);
    }

    public static void debugError(String format, Object... args) {
        err
                .format("%s INFO [DEBUG] - %s%n", now(), String.format(format,
                        args));
    }

    public static void debugInfo(String info) {
        out.format("%s INFO [DEBUG] - %s%n", now(), info);
    }

    public static void debugInfo(String format, Object... args) {
        out
                .format("%s INFO [DEBUG] - %s%n", now(), String.format(format,
                        args));
    }
}
