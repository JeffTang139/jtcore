package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.def.model.ModelScriptContext;
import org.eclipse.jt.core.def.model.ModelScriptEngine;
import org.eclipse.jt.core.exception.NullArgumentException;


/**
 * 模型脚本引擎管理器
 * 
 * @author Jeff Tang
 * 
 */
final class ModelScriptEngineManager {
	private final List<ModelScriptEngine<?>> engines = new ArrayList<ModelScriptEngine<?>>();
	private final Map<String, ModelScriptEngine<?>> bestEngines = new HashMap<String, ModelScriptEngine<?>>();

	synchronized final void regEngine(ModelScriptEngine<?> engine) {
		if (engine == null) {
			throw new NullArgumentException("engine");
		}
		if (!this.engines.contains(engine)) {
			this.engines.add(engine);
			this.bestEngines.clear();
		}
	}

	synchronized final ModelScriptEngine<?> findEngine(String language) {
		if (language == null) {
			language = "";
		} else {
			language = language.toLowerCase();
		}
		ModelScriptEngine<?> best;
		best = this.bestEngines.get(language);
		if (best != null) {
			return best == NULL ? null : best;
		}
		int bestDegree = 0;
		for (int i = 0, c = this.engines.size(); i < c; i++) {
			ModelScriptEngine<?> engine = this.engines.get(i);
			int degree = engine.suport(language);
			if (degree > bestDegree) {
				bestDegree = degree;
				best = engine;
			}
		}
		this.bestEngines.put(language, best == null ? NULL : best);
		return best;
	}

	private static ModelScriptEngine<Object> NULL = new ModelScriptEngine<Object>() {
		public final ModelScriptContext<Object> allocContext(Context context) {
			throw new UnsupportedOperationException();
		}

		public final int suport(String language) {
			return 0;
		}
	};
}
