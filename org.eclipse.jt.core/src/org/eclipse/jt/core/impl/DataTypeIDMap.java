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
	 * @return ���Map���Ѵ���һ��dataType��ָ����dataType��ID��ͬ���򷵻��Ѵ��ڵ�dataType�����򷵻�null
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
	 * @return ����null��ʾû���ҵ�dataTypeӳ��
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
		// boolean�Ķ�������
		this.internalPut(BooleanType.TYPE);
		this.internalPut(ByteType.TYPE);
		this.internalPut(ShortType.TYPE);
		this.internalPut(IntType.TYPE);
		this.internalPut(FloatType.TYPE);
		this.internalPut(LongType.TYPE);
		this.internalPut(DoubleType.TYPE);
		// GUID�Ķ�������
		this.internalPut(GUIDType.TYPE);
		// �ַ����Ķ�������
		this.internalPut(StringType.TYPE);
		// Bytes�Ķ�������
		this.internalPut(BytesType.TYPE);
		// ������������
		this.internalPut(IntArrayDataType.TYPE);
		// �ַ���������
		this.internalPut(CharArrayDataType.TYPE);
		// ��������������
		this.internalPut(LongArrayDataType.TYPE);
		// ��������������
		this.internalPut(ShortArrayDataType.TYPE);
		// �����ȸ�������������
		this.internalPut(FloatArrayDataType.TYPE);
		// ˫���ȸ�������������
		this.internalPut(DoubleArrayDataType.TYPE);
		// ������������
		this.internalPut(BooleanArrayDataType.TYPE);
		// Class�Ķ�������
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
