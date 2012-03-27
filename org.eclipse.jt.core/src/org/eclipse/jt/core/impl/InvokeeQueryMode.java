package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.impl.ServiceBase.EventListener;
import org.eclipse.jt.core.service.Publish.Mode;

/**
 * ���ҵ�������ģʽ
 * 
 * @author Jeff Tang
 * 
 */
@SuppressWarnings("unchecked")
enum InvokeeQueryMode {
	IN_SPACE {
		@Override
		final ServiceInvokeeBase findInvokeeBase(ServiceInvokeeEntry entry,
		        Class<?> key1Class, Class<?> key2Class, Class<?> key3Class,
		        int mask) {
			for (ServiceInvokeeBase invokee = entry.first; invokee != null; invokee = invokee.next) {
				if (invokee.match(key1Class, key2Class, key3Class, mask)) {
					return invokee;
				}
			}
			return null;
		}

		@Override
		final EventListenerChain collectEvent(ServiceInvokeeEntry entry,
		        EventListenerChain chain) {
			for (ServiceInvokeeBase invokee = entry.first; invokee != null; invokee = invokee.next) {
				if (invokee.match(null, null, null,
				        ServiceInvokeeBase.MASK_EVENT)) {
					if (chain == null) {
						chain = new EventListenerChain((EventListener) invokee);
					} else {
						chain = chain.putIn((EventListener) invokee);
					}
				}
			}
			return chain;
		}
	},
	IN_SITE {
		@Override
		final ServiceInvokeeBase findInvokeeBase(ServiceInvokeeEntry entry,
		        Class<?> key1Class, Class<?> key2Class, Class<?> key3Class,
		        int mask) {
			for (ServiceInvokeeBase invokee = entry.first; invokee != null; invokee = invokee.next) {
				if (invokee.match(key1Class, key2Class, key3Class, mask)) {
					return invokee;
				}
			}
			return null;
		}

		@Override
		final EventListenerChain collectEvent(ServiceInvokeeEntry entry,
		        EventListenerChain chain) {
			for (ServiceInvokeeBase invokee = entry.first; invokee != null; invokee = invokee.next) {
				if (invokee.match(null, null, null,
				        ServiceInvokeeBase.MASK_EVENT)) {
					if (chain == null) {
						chain = new EventListenerChain((EventListener) invokee);
					} else {
						chain = chain.putIn((EventListener) invokee);
					}
				}
			}
			return chain;
		}
	},
	FROM_SUB_SITE {
		@Override
		final ServiceInvokeeBase findInvokeeBase(ServiceInvokeeEntry entry,
		        Class<?> key1Class, Class<?> key2Class, Class<?> key3Class,
		        int mask) {
			for (ServiceInvokeeBase invokee = entry.first; invokee != null; invokee = invokee.next) {
				if (invokee.match(key1Class, key2Class, key3Class, mask)
				        && (invokee.publishMode == Mode.SITE_PROTECTED || invokee.publishMode == Mode.SITE_PUBLIC)) {
					return invokee;
				}
			}
			return null;
		}

		@Override
		final EventListenerChain collectEvent(ServiceInvokeeEntry entry,
		        EventListenerChain chain) {
			for (ServiceInvokeeBase invokee = entry.first; invokee != null; invokee = invokee.next) {
				if (invokee.match(null, null, null,
				        ServiceInvokeeBase.MASK_EVENT)
				        && (invokee.publishMode == Mode.SITE_PROTECTED || invokee.publishMode == Mode.SITE_PUBLIC)) {
					if (chain == null) {
						chain = new EventListenerChain((EventListener) invokee);
					} else {
						chain = chain.putIn((EventListener) invokee);
					}
				}
			}
			return chain;
		}
	},
	FROM_OTHER_SITE {
		@Override
		final ServiceInvokeeBase findInvokeeBase(ServiceInvokeeEntry entry,
		        Class<?> key1Class, Class<?> key2Class, Class<?> key3Class,
		        int mask) {
			for (ServiceInvokeeBase invokee = entry.first; invokee != null; invokee = invokee.next) {
				if (invokee.match(key1Class, key2Class, key3Class, mask)
				        && invokee.publishMode == Mode.SITE_PUBLIC) {
					return invokee;
				}
			}
			return null;
		}

		@Override
		final EventListenerChain collectEvent(ServiceInvokeeEntry entry,
		        EventListenerChain chain) {
			for (ServiceInvokeeBase invokee = entry.first; invokee != null; invokee = invokee.next) {
				if (invokee.match(null, null, null,
				        ServiceInvokeeBase.MASK_EVENT)
				        && invokee.publishMode == Mode.SITE_PUBLIC) {
					if (chain == null) {
						chain = new EventListenerChain((EventListener) invokee);
					} else {
						chain = chain.putIn((EventListener) invokee);
					}
				}
			}
			return chain;
		}
	};
	/**
	 * ���ݵ�ǰ����ģʽ���ҷ��������ĵ�����
	 * 
	 * @param entry
	 * @param key1Class
	 * @param key2Class
	 * @param key3Class
	 * @param mask
	 * @return
	 */
	abstract ServiceInvokeeBase findInvokeeBase(ServiceInvokeeEntry entry,
	        Class<?> key1Class, Class<?> key2Class, Class<?> key3Class, int mask);

	/**
	 * ���ݵ�ǰ����ģʽ�ռ������������¼�������
	 * 
	 * @param mode
	 * @param collector
	 */
	abstract EventListenerChain collectEvent(ServiceInvokeeEntry entry,
	        EventListenerChain chain);
}
