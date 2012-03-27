package org.eclipse.jt.core.impl;

enum NameCaseMode {

	CASE_SENSITIVE(true) {

		@Override
		String transKey(String name) {
			return name;
		}

		@Override
		String transDisplay(String name) {
			return name;
		}
	},

	CASE_INSENSITIVE_DISPLAY_LOWER(false) {

		@Override
		String transKey(String name) {
			return name.toLowerCase();
		}

		@Override
		String transDisplay(String name) {
			return name.toLowerCase();
		}
	},

	CASE_INSENSITIVE_DISPLAY_SPECIFIC(false) {

		@Override
		String transKey(String name) {
			return name.toLowerCase();
		}

		@Override
		String transDisplay(String name) {
			return name;
		}

	};

	final boolean caseSensitive;

	NameCaseMode(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	final Namespace newInstance() {
		return new NamespaceImpl(this);
	}

	abstract String transKey(String name);

	abstract String transDisplay(String name);

	static final class NamespaceImpl implements Namespace {

		final StringKeyMap<String> names = new StringKeyMap<String>();
		final NameCaseMode mode;

		NamespaceImpl(NameCaseMode mode) {
			this.mode = mode;
		}

		public final boolean contains(String name) {
			return this.names.containsKey(this.mode.transKey(name));
		}

		public final void add(String name) {
			this.names.put(this.mode.transKey(name),
					this.mode.transDisplay(name));
		}

		public final void remove(String name) {
			this.names.remove(this.mode.transKey(name));
		}

		public final void clear() {
			this.names.clear();
		}

	}
}
