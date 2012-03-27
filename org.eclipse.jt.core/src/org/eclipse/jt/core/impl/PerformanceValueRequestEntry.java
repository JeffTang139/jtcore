package org.eclipse.jt.core.impl;

/**
 * ÐÔÄÜ¼à¿ØÇëÇó
 * 
 * @author Jeff Tang
 * 
 */
final class PerformanceValueRequestEntry {
	ServiceBase<?>.PerformanceProvider<?> provider;
	int position;
	Object value;
	int nextCapcity;
	PerformanceValueRequestEntry nextInSameSession;

	PerformanceValueRequestEntry(
			ServiceBase<?>.PerformanceProvider<?> provider, int position,
			int nextCapcity) {
		this.provider = provider;
		this.position = position;
		this.nextCapcity = nextCapcity;
	}
}
