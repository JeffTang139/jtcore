package org.eclipse.jt.core.impl;

/**
 * ������Դ��Ŀ��
 * 
 * @author Jeff Tang
 * 
 */
enum ResourceDemandFor {
    /**
     * Ϊ������
     */
    INVALID {
        @Override
        final <TFacade, TImpl extends TFacade, TKeysHolder> void acquire(
                ResourceHandleImpl<TFacade, TImpl, TKeysHolder> handle,
                ResourceItem<TFacade, TImpl, TKeysHolder> res) {
            handle.exclusive(res, 0);
        }

        @Override
        void modeCompatible(ResourceHandleImpl<?, ?, ?> handle) {
            handle.toBeCompatibleWithExclusive(0);
        }
    },
    /**
     * Ϊ���޸�
     */
    MODIFY {
        @Override
        final <TFacade, TImpl extends TFacade, TKeysHolder> void acquire(
                ResourceHandleImpl<TFacade, TImpl, TKeysHolder> handle,
                ResourceItem<TFacade, TImpl, TKeysHolder> res) {
            handle.exclusive(res, 0);
        }

        @Override
        void modeCompatible(ResourceHandleImpl<?, ?, ?> handle) {
            handle.toBeCompatibleWithExclusive(0);
        }
    },
    READ_THEN_MODIFY {
        @Override
        final <TFacade, TImpl extends TFacade, TKeysHolder> void acquire(
                ResourceHandleImpl<TFacade, TImpl, TKeysHolder> handle,
                ResourceItem<TFacade, TImpl, TKeysHolder> res) {
            handle.shareUpgradable(res, 0);
        }

        @Override
        void modeCompatible(ResourceHandleImpl<?, ?, ?> handle) {
            handle.toBeCompatibleWithShareUpgradable();
        }
    },
    READ {
        @Override
        final <TFacade, TImpl extends TFacade, TKeysHolder> void acquire(
                ResourceHandleImpl<TFacade, TImpl, TKeysHolder> handle,
                ResourceItem<TFacade, TImpl, TKeysHolder> res) {
            handle.share(res, 0);
        }

        @Override
        void modeCompatible(ResourceHandleImpl<?, ?, ?> handle) {
        }
    },
    INVALID_DELAY {
        @Override
        final <TFacade, TImpl extends TFacade, TKeysHolder> void acquire(
                ResourceHandleImpl<TFacade, TImpl, TKeysHolder> handle,
                ResourceItem<TFacade, TImpl, TKeysHolder> res) {
            handle.share(res, 0);
        }

        @Override
        void modeCompatible(ResourceHandleImpl<?, ?, ?> handle) {
        }
    },
    REGISTER {
        @Override
        final <TFacade, TImpl extends TFacade, TKeysHolder> void acquire(
                ResourceHandleImpl<TFacade, TImpl, TKeysHolder> handle,
                ResourceItem<TFacade, TImpl, TKeysHolder> res) {
            handle.share(res, 0);
        }

        @Override
        void modeCompatible(ResourceHandleImpl<?, ?, ?> handle) {
        }
    },
    APPEND {
        @Override
        final <TFacade, TImpl extends TFacade, TKeysHolder> void acquire(
                ResourceHandleImpl<TFacade, TImpl, TKeysHolder> handle,
                ResourceItem<TFacade, TImpl, TKeysHolder> res) {
            handle.shareUpgradable(res, 0);
        }

        @Override
        void modeCompatible(ResourceHandleImpl<?, ?, ?> handle) {
            handle.toBeCompatibleWithShareUpgradable();
        }

    };
    abstract <TFacade, TImpl extends TFacade, TKeysHolder> void acquire(
            ResourceHandleImpl<TFacade, TImpl, TKeysHolder> handle,
            ResourceItem<TFacade, TImpl, TKeysHolder> res);

    abstract void modeCompatible(ResourceHandleImpl<?, ?, ?> handle);
}
