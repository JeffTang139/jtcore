package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.def.model.ModelScriptEngine;

/**
 * JavaScript 脚本引擎
 * 
 * @author Jeff Tang
 * 
 */
public final class ModelJavaScriptEngine implements
        ModelScriptEngine<org.mozilla.javascript.Function> {
	/**
	 * 引擎全局区域
	 */
	private final org.mozilla.javascript.ScriptableObject engineScope = new org.mozilla.javascript.ScriptableObject() {
		private static final long serialVersionUID = 1L;

		@Override
		public String getClassName() {
			return "Engine";
		}
	};

	/**
	 * 获得引擎全局区域对象
	 */
	final org.mozilla.javascript.Scriptable getScope() {
		return this.engineScope;
	}

	public final ModelJavaScriptContext allocContext(Context context) {
		return new ModelJavaScriptContext(this, context);
	}

	public final int suport(String language) {
		if (language == null || language.length() == 0) {
			return 1;
		}
		if ("javascript".equalsIgnoreCase(language)) {
			return 10;
		}
		return 0;
	}

}
