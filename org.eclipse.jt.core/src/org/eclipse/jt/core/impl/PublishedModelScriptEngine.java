package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.model.ModelScriptEngine;

final class PublishedModelScriptEngine extends PublishedElement {
	final Class<? extends ModelScriptEngine<?>> clazz;
	ModelScriptEngine<?> ref;

	@SuppressWarnings("unchecked")
	PublishedModelScriptEngine(Class<? extends ModelScriptEngine> clazz) {
		this.clazz = (Class) clazz;
	}

	final static StartupStepBase<PublishedModelScriptEngine> create = new StartupStepBase<PublishedModelScriptEngine>(
	        StartupStep.MODEL_SCRIPT_ENGINE_PRI, "实例化模型脚本引擎") {

		@Override
		public StartupStep<PublishedModelScriptEngine> doStep(
		        ResolveHelper helper, PublishedModelScriptEngine target)
		        throws Throwable {
			target.ref = helper.newObject(target.clazz, target.space);
			helper.regModelScriptEngine(target.ref);
			return null;
		}
	};
}
