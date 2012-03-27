package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.ObjectQuerier;
import org.eclipse.jt.core.def.MetaElementType;
import org.eclipse.jt.core.def.info.InfoGroupDeclarator;
import org.eclipse.jt.core.def.info.InfoGroupDeclare;
import org.eclipse.jt.core.def.info.InfoKind;
import org.eclipse.jt.core.misc.SXElement;

public final class InfoGroupDefineImpl extends NamedDefineImpl implements
		InfoGroupDeclare, Prepareble, Declarative<InfoGroupDeclarator> {

	public final MetaElementType getMetaElementType() {
		return MetaElementType.INFO;
	}

	private final NamedDefineContainerImpl<InfoDefineImpl> infos = new NamedDefineContainerImpl<InfoDefineImpl>();

	final InfoGroupDeclarator declarator;

	public final void load(ObjectQuerier querier) {

	}

	public final InfoGroupDeclarator getDeclarator() {
		return this.declarator;
	}

	public InfoGroupDefineImpl(String name, InfoGroupDeclarator declarator) {
		super(name);
		this.declarator = declarator;
	}

	public final NamedDefineContainerImpl<InfoDefineImpl> getInfos() {
		return this.infos;
	}

	private final InfoDefineImpl internalNewInformation(String name,
			InfoKind kind, String msgfmt) {
		InfoDefineImpl info = new InfoDefineImpl(name, kind, msgfmt);
		this.infos.add(info);
		return info;
	}

	public final InfoDefineImpl newProcess(String name, String messageFrmt) {
		return this.internalNewInformation(name, InfoKind.PROCESS, messageFrmt);
	}

	public final InfoDefineImpl newWarning(String name, String messageFrmt) {
		return this.internalNewInformation(name, InfoKind.WARNING, messageFrmt);
	}

	public final InfoDefineImpl newError(String name, String messageFrmt) {
		return this.internalNewInformation(name, InfoKind.ERROR, messageFrmt);
	}

	public final InfoDefineImpl newHint(String name, String messageFrmt) {
		return this.internalNewInformation(name, InfoKind.HINT, messageFrmt);
	}

	// /////////// xml //////////////////////
	static final String xml_element_infogroup = "info-group";
	static final String xml_element_infos = "infos";

	@Override
	public String getXMLTagName() {
		return xml_element_infogroup;
	}

	@Override
	public void render(SXElement element) {
		super.render(element);
		this.infos.renderInto(element, xml_element_infos, 0);
	}

	public final boolean ignorePrepareIfDBInvalid() {
		return false;
	}

	public final void ensurePrepared(ContextImpl<?, ?, ?> context,
			boolean rePrepared) {
		if (!this.isPrepared || rePrepared) {
			if (this.declarator != null) {
				for (LanguagePackage lp = context.session.application
						.findInfoGroupLanguages(this.declarator.getClass()
								.getName()); lp != null; lp = lp.next) {
					final String language = lp.language;
					for (int i = 0, c = lp.nameMessages.length; i < c; i += 2) {
						final InfoDefineImpl info = this.infos
								.find(lp.nameMessages[i]);
						if (info != null) {
							info.putLanguageMessage(language,
									lp.nameMessages[i + 1]);
						}
					}
				}
			}
			this.isPrepared = true;
		}
	}

	private boolean isPrepared;

	public final boolean isPrepared() {
		return this.isPrepared;
	}

}
