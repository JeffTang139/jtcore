package org.eclipse.jt.core.impl;

import java.util.Random;

/**
 * acl：long[]<br>
 * 1.两个相邻的long表示一个项<br>
 * 2.用2的n次方作hash空间的大小<br>
 * 3.用hash空间大小的1/2作hash冲突后的存储区域<br>
 * 4.用最后一个long+2表示冲突区的第一个空闲位置的反向偏移位置， <br>
 * 5.空闲位置+2表示下一个空闲位置的反向偏移位置<br>
 * 
 * @author Jeff Tang
 * 
 */
public final class ACLHelper {

	/**
	 * 
	 * @param acl
	 * @param resID
	 * @return
	 */
	static final int getAuthCode(long[] acl, long resID) {
		if (acl == null || acl.length == 0) {
			return 0;
		}
		int hashIndex = resHashIndex(resID, (acl.length - 1) / 3);
		long ID = acl[hashIndex];
		for (;;) {
			if (ID == resID) {
				return (int) (acl[hashIndex + 1] >>> 32);
			} else {
				hashIndex = (int) (acl[hashIndex + 1] & 0x00000000FFFFFFFFL);
				if (hashIndex <= 0) {
					return 0;
				}
				ID = acl[hashIndex];
			}
		}
	}

	static final long[] initACL(int size) {
		if (size <= DEFAULT_HASH_SIZE) {
			return new long[DEFAULT_HASH_SIZE * 2 + 1];
		}
		int initSize = DEFAULT_HASH_SIZE * 2;
		while (initSize < size) {
			initSize *= 2;
		}
		return new long[initSize * 2 + 1];
	}

	static final boolean isEmpty(final long[] acl) {
		if (acl == null || acl.length == 0) {
			return true;
		}
		for (int index = 0, capacity = (acl.length / 3) * 2; index < capacity; index += 2) {
			int hashIndex = index;
			for (;;) {
				if (acl[hashIndex] != 0L) {
					return false;
				}
				hashIndex = (int) (acl[hashIndex + 1] & 0x00000000FFFFFFFFL);
				if (hashIndex <= 0) {
					break;
				}
			}
		}
		return true;
	}

	static final long[] setAuthCode(long[] acl, long resID, int authCode) {
		return setOpAuth(acl, resID, 0xFFFFFFFF00000000L,
				((long) authCode) << 32);
	}

	/**
	 * 
	 * @param opShift
	 *            32,34,36...62
	 * @param auth
	 *            0,1,2,3
	 * @return
	 */
	static final long[] setOpAuth(long[] acl, long resID, int opShift, int auth) {
		return setOpAuth(acl, resID, OPERATE_MASK << opShift,
				((auth) & OPERATE_MASK) << opShift);
	}

	private static final long[] setOpAuth(long[] acl, long resID, long mask,
			final long authMask) {
		if (acl == null || acl.length == 0) {
			if (authMask == 0) {
				return acl;
			}
			acl = initACL(DEFAULT_HASH_SIZE);
			int hashIndex = resHashIndex(resID, DEFAULT_CAPACITY);
			acl[hashIndex] = resID;
			acl[hashIndex + 1] = authMask;
			return acl;
		}
		final int aclSize = acl.length - 1;
		final int hashCapacity = aclSize / 3;
		int hashIndex = resHashIndex(resID, hashCapacity);
		if (acl[hashIndex] == 0L) {
			// hash区对应位置为空
			if (authMask != 0L) {
				acl[hashIndex++] = resID;
				acl[hashIndex] &= ~mask;
				acl[hashIndex] |= authMask;
				return acl;
			}
		} else if (acl[hashIndex] == resID) {
			// hash区对应位置存储了指定资源的授权信息
			if (authMask == 0L) {
				acl[hashIndex++] = 0L;
				acl[hashIndex] &= 0x00000000FFFFFFFFL;
			} else {
				acl[hashIndex++] = resID;
				acl[hashIndex] &= ~mask;
				acl[hashIndex] |= authMask;
			}
			return acl;
		}
		// hash区对应位置不为空， 且不为指定资源，查找下一个
		if (authMask == 0L) {// 回收冲突空间的情况
			for (;;) {
				int nextIndex = (int) (acl[++hashIndex] & 0x00000000FFFFFFFFL);
				if (nextIndex > 0) {// 有下一个
					if (acl[nextIndex] == resID) {// 下一个为要找的资源， 回收下一个冲突空间
						acl[nextIndex++] = 0L;
						acl[hashIndex] = (acl[hashIndex] & 0xFFFFFFFF00000000L)
								| ((int) (acl[nextIndex] & 0x00000000FFFFFFFFL));
						acl[nextIndex] = ((int) (acl[aclSize] & 0x00000000FFFFFFFFL))
								+ (nextIndex - 1) - aclSize;
						acl[aclSize] = aclSize - (nextIndex + 1);
						return acl;
					}
					hashIndex = nextIndex;
				} else {
					return acl;
				}
			}
		} else {
			for (;;) {
				int nextIndex = (int) (acl[hashIndex + 1] & 0x00000000FFFFFFFFL);
				if (nextIndex > 0) {// 有下一个
					if (acl[nextIndex] == resID) {// 找到
						acl[++nextIndex] &= ~mask;
						acl[nextIndex] |= authMask;
						return acl;
					}
					hashIndex = nextIndex;
				} else {// 没找到，需要添加
					final int firstFreeOffset = (int) (acl[aclSize] & 0x00000000FFFFFFFFL) + 2;
					if (firstFreeOffset <= hashCapacity) {
						final int firstFreeIndex = aclSize - firstFreeOffset;
						hashIndex++;
						acl[hashIndex] = (acl[hashIndex] & 0xFFFFFFFF00000000L)
								| (firstFreeIndex & 0x00000000FFFFFFFFL);
						acl[aclSize] = acl[firstFreeIndex + 1]
								+ firstFreeOffset;
						acl[firstFreeIndex] = resID;
						acl[firstFreeIndex + 1] = authMask;
						return acl;
					} else {
						// 需重新hash
						int newACLSize = aclSize << 1;
						int newHashCapacity = hashCapacity << 1;
						long[] newACL = new long[newACLSize + 1];
						int freeIndex = newACLSize - 1;
						int newHashIndex = resHashIndex(resID, newHashCapacity);
						newACL[newHashIndex++] = resID;
						newACL[newHashIndex] = authMask;
						long oldResID, oldAuth, existID;
						for (int index = 0; index < aclSize; index += 2) {
							oldResID = acl[index];
							if (oldResID == 0L) {
								continue;
							}
							oldAuth = acl[index + 1] & 0xFFFFFFFF00000000L;
							newHashIndex = resHashIndex(oldResID,
									newHashCapacity);
							existID = newACL[newHashIndex];
							newACL[newHashIndex++] = oldResID;
							if (existID == 0L) {
								newACL[newHashIndex] = oldAuth;
							} else {
								newACL[freeIndex--] = newACL[newHashIndex];
								newACL[newHashIndex] = oldAuth | freeIndex;
								newACL[freeIndex--] = existID;
							}
							if (freeIndex <= newHashCapacity) {
								newACLSize = newACLSize << 1;
								newHashCapacity = newHashCapacity << 1;
								newACL = new long[newACLSize + 1];
								freeIndex = newACLSize - 1;
								newHashIndex = resHashIndex(resID,
										newHashCapacity);
								newACL[newHashIndex++] = resID;
								newACL[newHashIndex] = authMask;
								index = 0;
							}
						}
						newACL[newACLSize] = newACLSize - 1 - freeIndex;
						return newACL;
					}
				}
			}
		}
	}

	/**
	 * 默认hash容量
	 */
	private static final int DEFAULT_CAPACITY = 1 << 3;

	private static final int DEFAULT_HASH_SIZE = DEFAULT_CAPACITY * 3 / 2;

	private static final long OPERATE_MASK = 0x3L;

	private static final int resHashIndex(long resID, int hashCapacity) {
		return (int) (((hashCapacity - 1) & ((resID >>> 28) ^ (resID >>> 4))) << 1);
	}

	private ACLHelper() {
	}

	public static final void main(String[] args) {
		final Random random = new Random();
		final int IDCount = random.nextInt(500);
		final long[] resIDs = new long[IDCount];
		for (int index = 0; index < IDCount; index++) {
			resIDs[index] = random.nextLong();
		}
		long[] acl = null;
		try {
			int auth;
			for (int index = 0; index < 1000; index++) {
				if (random.nextBoolean()) {
					auth = 0;
				} else {
					auth = random.nextInt();
				}
				long id = resIDs[random.nextInt(IDCount)];
				// System.out.println(index + "\t" + id + "\t" + auth);
				acl = ACLHelper.setAuthCode(acl, id, auth);
			}
			System.out.println("Test over.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}