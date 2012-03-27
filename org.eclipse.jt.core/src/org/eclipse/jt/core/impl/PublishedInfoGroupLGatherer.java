package org.eclipse.jt.core.impl;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.spi.publish.BundleToken;


public class PublishedInfoGroupLGatherer extends
		PublishedElementGatherer<PublishedElement> {

	final static String xml_attr_path = "path";
	final static String xml_attr_language = "language";
	private final ConcurrentHashMap<String, String> languages = new ConcurrentHashMap<String, String>();

	private final boolean getLanguage(SXElement element) {
		final String language = element.getParent().getAttribute(
				xml_attr_language, null);
		if (language == null || language.length() == 0) {
			this.language = null;
			return false;
		}
		if ((this.language = this.languages.get(language)) == null) {
			this.language = language.intern();
			this.languages.put(this.language, this.language);
		}
		return true;
	}

	private final PublishedElement flag = new PublishedElement();
	private String infoGroupFullName;
	private String language;
	private ArrayList<String> infoNameMessages = new ArrayList<String>();
	private final static Charset utf8 = Charset.forName("UTF8");

	@Override
	protected final PublishedElement parseElement(SXElement element,
			BundleToken bundle) throws Throwable {

		if (!this.getLanguage(element)) {
			return null;
		}
		final String path = element.getAttribute(xml_attr_path, null);
		if (path == null || path.length() == 0) {
			return null;
		}
		this.infoNameMessages.clear();
		final CsvReader reader = new CsvReader(bundle.getResource(path)
				.openStream(), utf8);
		try {
			while (reader.readRecord()) {
				String infoName = reader.get(0);
				if (infoName == null || infoName.length() == 0) {
					continue;
				}
				String infoValue = reader.get(2);
				if (infoValue == null || infoValue.length() == 0) {
					continue;
				}
				this.infoNameMessages.add(infoName);
				this.infoNameMessages.add(infoValue);
			}
		} finally {
			reader.close();
		}
		this.infoGroupFullName = (path.endsWith(".lg") ? path.substring(0, path
				.length() - 3) : path).replace('/', '.');
		return this.infoNameMessages.isEmpty() ? null : this.flag;
	}

	@Override
	void afterGatherElement(PublishedElement pe, ResolveHelper helper) {
		pe.bundle = null;
		pe.space = null;
		final String[] infoNameMessages = this.infoNameMessages
				.toArray(new String[this.infoNameMessages.size()]);
		this.infoNameMessages.clear();
		helper.regInfoGroupLanguage(this.infoGroupFullName, this.language,
				infoNameMessages);
	}
}
