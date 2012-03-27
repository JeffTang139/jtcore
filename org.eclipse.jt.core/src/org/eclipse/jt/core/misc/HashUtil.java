package org.eclipse.jt.core.misc;

/**
 * ¹þÏ£°ïÖúÀà
 * 
 * @author Jeff Tang
 * 
 */
public final class HashUtil {
	private HashUtil() {

	}

	public static int hash(int h) {
		h ^= (h >>> 20) ^ (h >>> 12);
		return h ^ (h >>> 7) ^ (h >>> 4);
	}

	public static int hash(Object obj1, Object obj2) {
		int h = obj1 != null ? obj1.hashCode() : 0;
		if (obj2 != null) {
			h ^= obj2.hashCode();
		}
		h ^= (h >>> 20) ^ (h >>> 12);
		return h ^ (h >>> 7) ^ (h >>> 4);
	}

	public static int hash(Object obj1, Object obj2, Object obj3) {
		int h = obj1 != null ? obj1.hashCode() : 0;
		if (obj2 != null) {
			h ^= obj2.hashCode();
		}
		if (obj3 != null) {
			h ^= obj3.hashCode();
		}
		h ^= (h >>> 20) ^ (h >>> 12);
		return h ^ (h >>> 7) ^ (h >>> 4);
	}

	public static int hash(Object obj) {
		int h = obj.hashCode();
		h ^= (h >>> 20) ^ (h >>> 12);
		return h ^ (h >>> 7) ^ (h >>> 4);
	}

	public static int identityHash(Object obj) {
		if (obj == null) {
			return 0;
		}
		int h = System.identityHashCode(obj);
		h ^= (h >>> 20) ^ (h >>> 12);
		return h ^ (h >>> 7) ^ (h >>> 4);
	}

}
