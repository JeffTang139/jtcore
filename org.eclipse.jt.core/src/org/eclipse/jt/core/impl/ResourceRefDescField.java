/**
 * Copyright (C) 2007-2008 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ResourceRefDiscItem.java
 * Date 2008-10-29
 */
package org.eclipse.jt.core.impl;

import java.lang.reflect.Field;

import org.eclipse.jt.core.resource.ResourceToken;


/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
@Deprecated
abstract class ResourceRefDescField<TFacade> {
    // final Field refField;
    final int offset;
    final Class<TFacade> fieldFacade;

    private ResourceRefDescField(Field refField, Class<TFacade> fieldFacade) {
        // this.refField = refField;
        this.fieldFacade = fieldFacade;
        this.offset = (int) Utils.objectFieldOffset(refField);
    }

    static final <TFacade> ResourceRefDescField<TFacade> newInstance(
            Field refField, Class<TFacade> fieldFacade, Field[] keys) {
        if (keys == null || keys.length == 0) {
            return new NoneKeyItem<TFacade>(refField, fieldFacade);
        } else {
            switch (keys.length) {
                case 0:
                    return new NoneKeyItem<TFacade>(refField, fieldFacade);
                case 1:
                    return new OneKeyItem<TFacade>(refField, fieldFacade,
                            keys[0]);
                case 2:
                    return new TwoKeyItem<TFacade>(refField, fieldFacade,
                            keys[0], keys[1]);
                case 3:
                    return new ThreeKeyItem<TFacade>(refField, fieldFacade,
                            keys[0], keys[1], keys[2]);
                default:
                    return new MoreKeysItem<TFacade>(refField, fieldFacade,
                            keys);
            }
        }
    }

    @Deprecated
    final void setReference(ContextImpl<?, ?, ?> context, Object obj) {
        ResourceToken<TFacade> token = this.findToken(context, obj);
        Unsf.unsafe.putObject(obj, (long) this.offset,
                token == null ? ResourceToken.MISSING : token);
    }

    abstract ResourceToken<TFacade> findToken(ContextImpl<?, ?, ?> context,
            Object obj);

    static final class NoneKeyItem<TFacade> extends
            ResourceRefDescField<TFacade> {
        private NoneKeyItem(Field refField, Class<TFacade> fieldFacade) {
            super(refField, fieldFacade);
        }

        @Override
        ResourceToken<TFacade> findToken(ContextImpl<?, ?, ?> context,
                Object obj) {
            return context.findResourceToken(this.fieldFacade);
        }
    }

    static final class OneKeyItem<TFacade> extends
            ResourceRefDescField<TFacade> {
        final int keyOffset;
        final ResourceFieldReader keyReader;

        private OneKeyItem(Field refField, Class<TFacade> fieldFacade,
                Field keyField) {
            super(refField, fieldFacade);
            this.keyOffset = (int) Utils.objectFieldOffset(keyField);
            this.keyReader = ResourceFieldReader.getReader(keyField);
        }

        @Override
        ResourceToken<TFacade> findToken(ContextImpl<?, ?, ?> context,
                Object obj) {
            return context.findResourceToken(this.fieldFacade, this.keyReader
                    .getValue(obj, this.keyOffset));
        }
    }

    static final class TwoKeyItem<TFacade> extends
            ResourceRefDescField<TFacade> {
        final int key1Offset;
        final int key2Offset;
        final ResourceFieldReader key1Reader;
        final ResourceFieldReader key2Reader;

        private TwoKeyItem(Field refField, Class<TFacade> fieldFacade,
                Field key1Field, Field key2Field) {
            super(refField, fieldFacade);
            this.key1Offset = (int) Utils.objectFieldOffset(key1Field);
            this.key2Offset = (int) Utils.objectFieldOffset(key2Field);
            this.key1Reader = ResourceFieldReader.getReader(key1Field);
            this.key2Reader = ResourceFieldReader.getReader(key2Field);
        }

        @Override
        ResourceToken<TFacade> findToken(ContextImpl<?, ?, ?> context,
                Object obj) {
            return context.findResourceToken(this.fieldFacade, this.key1Reader
                    .getValue(obj, this.key1Offset), this.key2Reader.getValue(
                    obj, this.key2Offset));
        }
    }

    static final class ThreeKeyItem<TFacade> extends
            ResourceRefDescField<TFacade> {
        final int key1Offset;
        final int key2Offset;
        final int key3Offset;
        final ResourceFieldReader key1Reader;
        final ResourceFieldReader key2Reader;
        final ResourceFieldReader key3Reader;

        private ThreeKeyItem(Field refField, Class<TFacade> fieldFacade,
                Field key1Field, Field key2Field, Field key3Field) {
            super(refField, fieldFacade);
            this.key1Offset = (int) Utils.objectFieldOffset(key1Field);
            this.key2Offset = (int) Utils.objectFieldOffset(key2Field);
            this.key3Offset = (int) Utils.objectFieldOffset(key3Field);
            this.key1Reader = ResourceFieldReader.getReader(key1Field);
            this.key2Reader = ResourceFieldReader.getReader(key2Field);
            this.key3Reader = ResourceFieldReader.getReader(key3Field);
        }

        @Override
        ResourceToken<TFacade> findToken(ContextImpl<?, ?, ?> context,
                Object obj) {
            return context.findResourceToken(this.fieldFacade, this.key1Reader
                    .getValue(obj, this.key1Offset), this.key2Reader.getValue(
                    obj, this.key2Offset), this.key3Reader.getValue(obj,
                    this.key3Offset));
        }
    }

    static final class MoreKeysItem<TFacade> extends
            ResourceRefDescField<TFacade> {
        final int[] keyOffsets;
        final ResourceFieldReader[] keyReaders;

        private MoreKeysItem(Field refField, Class<TFacade> fieldFacade,
                Field[] fields) {
            super(refField, fieldFacade);
            this.keyOffsets = new int[fields.length];
            this.keyReaders = new ResourceFieldReader[fields.length];
            for (int i = 0, len = fields.length; i < len; i++) {
                this.keyOffsets[i] = (int) Utils.objectFieldOffset(fields[i]);
                this.keyReaders[i] = ResourceFieldReader.getReader(fields[i]);
            }
        }

        @Override
        ResourceToken<TFacade> findToken(ContextImpl<?, ?, ?> context,
                Object obj) {
            Object[] otherKeys = new Object[this.keyOffsets.length - 3];
            for (int i = 0, len = otherKeys.length; i < len; i++) {
                otherKeys[i] = this.keyReaders[i + 3].getValue(obj,
                        this.keyOffsets[i + 3]);
            }
            return context.findResourceToken(this.fieldFacade,
                    this.keyReaders[0].getValue(obj, this.keyOffsets[0]),
                    this.keyReaders[1].getValue(obj, this.keyOffsets[1]),
                    this.keyReaders[2].getValue(obj, this.keyOffsets[2]),
                    otherKeys);
        }
    }
}
