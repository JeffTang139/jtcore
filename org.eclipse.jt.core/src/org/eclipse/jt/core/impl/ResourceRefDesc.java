/**
 * Copyright (C) 2007-2008 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ResourceRefDisc.java
 * Date 2008-10-20
 */
package org.eclipse.jt.core.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

import org.eclipse.jt.core.resource.ResourceKeyFields;
import org.eclipse.jt.core.resource.ResourceToken;


/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
@Deprecated
class ResourceRefDesc<TImpl> {
    final Class<TImpl> clazz;
    final ResourceRefDescField<?>[] items;

    static final ResourceRefDescField<?>[] empty = {};

    private ResourceRefDesc(Class<TImpl> clazz, ResourceRefDescField<?>[] items) {
        this.clazz = clazz;
        this.items = items;
    }

    final void setReferenceFields(ContextImpl<?, ?, ?> context, TImpl impl) {
        for (int i = 0, len = this.items.length; i < len; i++) {
            this.items[i].setReference(context, impl);
        }
    }

    static final <TImpl> ResourceRefDesc<TImpl> desc(final Class<TImpl> clazz) {
        if (clazz == null) {
            throw new NullPointerException();
        }
        ArrayList<ResourceRefDescField<?>> list = new ArrayList<ResourceRefDescField<?>>();
        for (Class<?> cls = clazz; cls != Object.class && !cls.isEnum(); cls = cls
                .getSuperclass()) {
            Field[] classFields = cls.getDeclaredFields();
            Field classField;
            for (int i = 0; i < classFields.length; i++) {
                classField = classFields[i];
                System.out.println(classField);
                if ((classField.getModifiers() & Modifier.STATIC) != 0) {
                    continue;
                }
                ResourceKeyFields anno = classField
                        .getAnnotation(ResourceKeyFields.class);
                if (anno == null) {
                    continue;
                }
                if (classField.getType() != ResourceToken.class) {
                    throw new IllegalDeclarationException(String.format(
                            "资源引用字段的类型声明不合要求（%s）", classField));
                }
                Type type = classField.getGenericType();
                ParameterizedType pFieldType = null;
                Class<?> facadeClass = null;
                if (type instanceof ParameterizedType) {
                    pFieldType = (ParameterizedType) type;
                    type = pFieldType.getActualTypeArguments()[0];
                    if (type instanceof Class<?>) {
                        facadeClass = (Class<?>) type;
                    } else {
                        System.out.println(type);
                        throw new IllegalDeclarationException(String.format(
                                "资源引用字段的类型声明中指明的引用资源的外观类型有误（<%s>）", type));
                    }
                } else {
                    throw new IllegalDeclarationException(String.format(
                            "资源引用字段的类型声明中没有指明引用资源的外观类型（%s）", classField));
                }
                if (anno.value().length > 3) {
                    throw new IllegalDeclarationException(String.format(
                            "为资源引用字段（%s.%s）指定的键字段超过了三个", classField
                                    .getDeclaringClass().getName(), classField
                                    .getName()));
                }
                Field[] keys = new Field[anno.value().length];
                for (int l = 0, len = anno.value().length; l < len; l++) {
                    try {
                        keys[l] = cls.getDeclaredField(anno.value()[l]);
                    } catch (NoSuchFieldException e) {
                        throw new IllegalDeclarationException(String.format(
                                "为资源引用字段（%s.%s）指定的键字段（%s）不存在", classField
                                        .getDeclaringClass().getName(),
                                classField.getName(), anno.value()[l]));
                    }
                }
                list.add(ResourceRefDescField.newInstance(classField,
                        facadeClass, keys));
            }
        }

        if (list.isEmpty()) {
            return null;
        } else {
            return new ResourceRefDesc<TImpl>(clazz, list
                    .toArray(new ResourceRefDescField[list.size()]));
        }
    }
}
