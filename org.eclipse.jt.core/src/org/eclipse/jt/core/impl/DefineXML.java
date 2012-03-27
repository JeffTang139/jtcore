package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.misc.SXElement;

interface DefineXML<TDefine extends DefineBaseImpl> {

	void render(TDefine define, SXElement element);

	void merge(TDefine define, SXElement element);
}