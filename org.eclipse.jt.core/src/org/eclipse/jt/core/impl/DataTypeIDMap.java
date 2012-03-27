package org.eclipse.jt.core.impl;

import java.util.Arrays;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.GUID;


final class DataTypeIDMap {

	DataTypeIDMap() {
		this.map = new MapEntry[DEFAULT_MAP_CAPACITY];
		this.mapSize = 0;
		this.mapCapacity_1 = DEFAULT_MAP_CAPACITY - 1;
		this.rehashSize = DEFAULT_MAP_CAPACITY * 0.75;
		final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
		this.readLock = lock.readLock();
		this.writeLock = lock.writeLock();
		this.registInternalDataType();
	}

	/**
	 * @return 如果Map中已存在一个dataType与指定的dataType的ID相同，则返回已存在的dataType，否则返回null
	 */
	final DataType put(final DataType dataType) {
		if (dataType == null) {
			throw new NullPointerException();
		}
		this.writeLock.lock();
		try {
			final GUID dataTypeID = dataType.getID();
			final int hashIndex = dataTypeID.hashCode() / this.mapCapacity_1;
			final DataType existEntry = this.internalGet(hashIndex, dataTypeID);
			if (existEntry != null) {
				return existEntry;
			}
			this.map[hashIndex] = new MapEntry(dataType, this.map[hashIndex]);
			if (++this.mapSize >= this.rehashSize) {
				final int newMapCapacity = this.map.length * 2;
				final int newMapCapacity_1 = newMapCapacity - 1;
				final MapEntry[] newMap = new MapEntry[newMapCapacity];
				for (MapEntry entry : this.map) {
					while (entry != null) {
						final int newHashIndex = entry.dataType
								.getID().hashCode()
								/ newMapCapacity_1;
						entry.nextInHash = newMap[newHashIndex];
						newMap[newHashIndex] = entry;
					}
				}
				this.map = newMap;
				this.mapCapacity_1 = newMapCapacity_1;
				this.rehashSize = newMapCapacity * 0.75;
			}
		} finally {
			this.writeLock.unlock();
		}
		throw new UnsupportedOperationException();
	}

	/**
	 * @return 返回null表示没有找到dataType映射
	 */
	final DataType get(final GUID dataTypeID) {
		if (dataTypeID == null) {
			throw new NullPointerException();
		}
		this.readLock.lock();
		try {
			return this.internalGet(dataTypeID.hashCode() / this.mapCapacity_1,
					dataTypeID);
		} finally {
			this.readLock.unlock();
		}
	}

	final void clear() {
		Arrays.fill(this.map, null);
		this.mapSize = 0;
		this.rehashSize = this.map.length * 0.75;
	}

	private final DataType internalGet(final int hashIndex,
			final GUID dataTypeID) {
		MapEntry entry = this.map[hashIndex];
		while (entry != null) {
			if (entry.dataType.getID().equals(dataTypeID)) {
				return entry.dataType;
			}
			entry = entry.nextInHash;
		}
		return null;
	}

	private final void registInternalDataType() {
		// boolean的对象类型
		this.internalPut(BooleanType.TYPE);
		this.internalPut(ByteType.TYPE);
		this.internalPut(ShortType.TYPE);
		this.internalPut(IntType.TYPE);
		this.internalPut(FloatType.TYPE);
		this.internalPut(LongType.TYPE);
		this.internalPut(DoubleType.TYPE);
		// GUID的对象类型
		this.internalPut(GUIDType.TYPE);
		// 字符串的对象类型
		this.internalPut(StringType.TYPE);
		// Bytes的对象类型
		this.internalPut(BytesType.TYPE);
		// 整数数组类型
		this.internalPut(IntArrayDataType.TYPE);
		// 字符数组类型
		this.internalPut(CharArrayDataType.TYPE);
		// 长整数数组类型
		this.internalPut(LongArrayDataType.TYPE);
		// 短整数数组类型
		this.internalPut(ShortArrayDataType.TYPE);
		// 单精度浮点数数组类型
		this.internalPut(FloatArrayDataType.TYPE);
		// 双精度浮点数数组类型
		this.internalPut(DoubleArrayDataType.TYPE);
		// 布尔数组类型
		this.internalPut(BooleanArrayDataType.TYPE);
		// Class的对象类型
		this.internalPut(ClassType.TYPE);
	}

	private final void internalPut(final DataType dataType) {
		final int hashIndex = dataType.getID().hashCode()
				/ this.mapCapacity_1;
		this.map[hashIndex] = new MapEntry(dataType, this.map[hashIndex]);
	}

	private static final int DEFAULT_MAP_CAPACITY = 64;

	private MapEntry[] map;

	private int mapSize;

	private int mapCapacity_1;

	private double rehashSize;

	private final ReadLock readLock;

	private final WriteLock writeLock;

	private final class MapEntry {

		MapEntry(final DataType dataType, final MapEntry nextInHash) {
			this.dataType = dataType;
			this.nextInHash = nextInHash;
		}

		final DataType dataType;

		MapEntry nextInHash;

	}

}
