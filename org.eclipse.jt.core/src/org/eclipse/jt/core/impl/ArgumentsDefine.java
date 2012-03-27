package org.eclipse.jt.core.impl;

final class ArgumentsDefine extends StructDefineImpl {

	@Override
	final String structTypeNamePrefix() {
		return "arguments:";
	}

	ArgumentsDefine(Class<?> soClass) {
		super("args", soClass);
		this.tryLoadJavaFields(true);
	}

}
