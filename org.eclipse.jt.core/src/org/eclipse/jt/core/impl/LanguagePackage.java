package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.NullArgumentException;

public class LanguagePackage {
	final String language;
	final String[] nameMessages;
	LanguagePackage next;

	LanguagePackage(String language, String[] nameValues) {
		if (language == null || language.length() == 0) {
			throw new NullArgumentException("language");
		}
		if (nameValues == null || nameValues.length == 0) {
			throw new NullArgumentException("nameValues");
		}
		this.language = language;
		this.nameMessages = nameValues;
	}
}
