/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ResourceKeysRepeatException.java
 * Date 2009-2-4
 */
package org.eclipse.jt.core.impl;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public class ResourceKeysRepeatException extends RuntimeException {
    private static final long serialVersionUID = 4928147540644548694L;

    @SuppressWarnings("unchecked")
    ResourceKeysRepeatException(Class<?> facadeClass, Class<?> implClass,
            Class<?> keysHolderClass, ResourceProviderBase[] providers,
            Object keys, Object newKeys) {
        super(buildMessage(facadeClass, implClass, keysHolderClass, providers,
                keys, newKeys));
    }

    @SuppressWarnings("unchecked")
    private static String buildMessage(Class<?> facadeClass,
            Class<?> implClass, Class<?> keysHolderClass,
            ResourceProviderBase[] providers, Object keys, Object newKeys) {
        return String
                .format(
                        "要添加的资源的某些键值与已经存在的资源的键值有重复：%n"
                                + "\t-->资源类型%n\t\tTFacade:     %s%n\t\tTImpl:       %s%n\t\tTKeysHolder: %s%n"
                                + "\t-->要添加的资源的键：%n%s%n" + "\t-->已存在的资源的键：%n%s",
                        facadeClass, implClass, keysHolderClass, getKeys(
                                providers, newKeys), getKeys(providers, keys));
    }

    @SuppressWarnings("unchecked")
    private static String getKeys(ResourceProviderBase[] providers, Object keys) {
        StringBuilder k = new StringBuilder();
        for (int i = 0, len = providers.length; i < len; i++) {
            if (i > 0) {
                k.append("\n");
            }
            k.append("\t\t");
            k.append(i + 1);
            k.append(". ");
            k.append(getKey(providers[i], keys));
        }
        return k.toString();
    }

    @SuppressWarnings("unchecked")
    ResourceKeysRepeatException(Class<?> facadeClass, Class<?> implClass,
            Class<?> keysHolderClass, ResourceProviderBase xProvider,
            ResourceProviderBase yProvider, Object iKeys, Object jKeys,
            Object newKeys) {
        super(buildMessage(facadeClass, implClass, keysHolderClass, xProvider,
                yProvider, iKeys, jKeys, newKeys));
    }

    @SuppressWarnings("unchecked")
    private static String buildMessage(Class<?> facadeClass,
            Class<?> implClass, Class<?> keysHolderClass,
            ResourceProviderBase xProvider, ResourceProviderBase yProvider,
            Object iKeys, Object jKeys, Object newKeys) {
        return String
                .format(
                        "要添加的资源的某些键值与已经存在的资源的键值有冲突：%n"
                                + "\t-->资源类型%n\t\tTFacade:     %s%n\t\tTImpl:       %s%n\t\tTKeysHolder: %s%n"
                                + "\t-->要添加的资源产生冲突的键：%n\t\t1. %s%n\t\t2. %s%n"
                                + "\t-->已经存在的资源的键：%n"
                                + "\t\t资源A.%n\t\t\t1. %s%n\t\t\t2. %s%n"
                                + "\t\t资源B.%n\t\t\t1. %s%n\t\t\t2. %s",
                        facadeClass, implClass, keysHolderClass, getKey(
                                xProvider, newKeys),
                        getKey(yProvider, newKeys), getKey(xProvider, iKeys),
                        getKey(yProvider, iKeys), getKey(xProvider, jKeys),
                        getKey(yProvider, jKeys));
    }

    @SuppressWarnings("unchecked")
    private static String getKey(ResourceProviderBase provider, Object keys) {
        StringBuilder key = new StringBuilder("(");
        Object keypart = provider.getKey1(keys);
        if (keypart != null) {
            key.append(keypart);

            keypart = provider.getKey2(keys);
            if (keypart != null) {
                key.append(", ");
                key.append(keypart);

                keypart = provider.getKey3(keys);
                if (keypart != null) {
                    key.append(", ");
                    key.append(keypart);
                }
            }
        }
        key.append(")");
        return key.toString();
    }
}
