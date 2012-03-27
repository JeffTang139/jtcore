package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.impl.ResourceServiceBase.ResourceIndexInfo;

@Deprecated
final class ResourceRefInfo {
    // final Class<TFacade> refFacade;
    ResourceIndexInfo rii;
    // volatile ResourceGroup<TFacade, ?, ?> group;
    StructFieldDefineImpl tokenAccessor;
    FieldValueAccessor key1Getter = nullGetter;
    FieldValueAccessor key2Getter = nullGetter;
    FieldValueAccessor key3Getter = nullGetter;
    FieldValueAccessor[] otherKeyGetters;

    ResourceRefInfo next;

    @Deprecated
    ResourceRefInfo() {
        // this.refFacade = refFacade;
    }

    private final static Object[] empty = {};

    @SuppressWarnings("unused")
    private Object getKey(int index, Object so) {
        switch (index) {
        case 0:
            return this.key1Getter.internalGet(so);
        case 1:
            return this.key2Getter.internalGet(so);
        case 2:
            return this.key3Getter.internalGet(so);
        default:
            if (this.otherKeyGetters == null
                    || (index - 3) >= this.otherKeyGetters.length) {
                return null;
            } else {
                return this.otherKeyGetters[index - 3].internalGet(so);
            }
        }
    }

    @SuppressWarnings("unused")
    private Object[] getOtherKeys(Object so) {
        if (this.otherKeyGetters == null) {
            return empty;
        } else {
            Object[] keys = new Object[this.otherKeyGetters.length];
            for (int i = 0; i < keys.length; i++) {
                keys[i] = this.otherKeyGetters[i].internalGet(so);
            }
            return keys;
        }
    }

    @Deprecated
    final void refreshResrouceToken(ContextImpl<?, ?, ?> context, Object so) {
        // ResourceToken<?> old = (ResourceToken<?>) this.tokenAccessor
        // .internalGet(so);
        // if (old != null && old.isAvailable()) {
        // final ResourceItem<?, ?, ?> oldItem = (ResourceItem<?, ?, ?>) old;
        // ResourceProviderBase provider;
        // if (this.rii.subIndexInfo == null) {
        // provider = oldItem.group
        // .getResourceIndex(this.rii.resourceIndexIndex).provider;
        // if (provider.keysEqual(oldItem.keys, this.key1Getter
        // .internalGet(so), this.key2Getter.internalGet(so),
        // this.key3Getter.internalGet(so))) {
        // return;
        // }
        // } else {
        // ArrayList<ResourceItem<?, ?, ?>> items = new
        // ArrayList<ResourceItem<?, ?, ?>>();
        // ResourceItem<?, ?, ?> item = oldItem;
        // do {
        // items.add(item);
        // item = item.group.ownerResource;
        // } while (item != null);
        // int c = 0, i = items.size() - 1;
        // RestResourceIndexInfo rii = this.rii;
        // boolean noDiff = true;
        // do {
        // item = items.get(i);
        // provider = item.group
        // .getResourceIndex(rii.resourceIndexIndex).provider;
        // if (!provider.keysEqual(item.keys, this.getKey(c, so), this
        // .getKey(c + 1, so), this.getKey(c + 2, so))) {
        // noDiff = false;
        // break;
        // }
        // c += rii.keyCount;
        // i--;
        // rii = rii.subIndexInfo;
        // } while (rii != null && i >= 0);
        // if (noDiff) {
        // Assertion.ASSERT(rii == null && i < 0);
        // return;
        // }
        // }
        // }
        // ResourceItem<?, ?, ?> resItem =
        // context.internalFindResource(this.rii,
        // ContextImpl.FIND_RESOURCE, null,
        // // FIXME 现在不会是ResourceTypeImpl了，所以这里肯定不能运行通过。
        // ((ResourceTypeImpl<?>) this.tokenAccessor.type).category,
        // this.key1Getter.internalGet(so), this.key2Getter
        // .internalGet(so), this.key3Getter.internalGet(so), this
        // .getOtherKeys(so));
        // ResourceToken<?> nw = resItem == null ? ResourceToken.MISSING :
        // resItem;
        // this.tokenAccessor.internalSet(so, nw);
    }

    @Deprecated
    final static FieldValueAccessor nullGetter = new FieldValueAccessor() {
        public Object internalGet(Object so) {
            return null;
        }

        public void internalSet(Object so, Object value) {
            throw new UnsupportedOperationException();
        }
    };
}
