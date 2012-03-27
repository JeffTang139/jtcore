package org.eclipse.jt.core.impl;

interface Namespace {

	boolean contains(String name);

	void add(String name);

	void remove(String name);

	void clear();

}
