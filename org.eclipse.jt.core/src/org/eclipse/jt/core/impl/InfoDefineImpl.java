package org.eclipse.jt.core.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import org.eclipse.jt.core.def.FieldDefine;
import org.eclipse.jt.core.def.info.ErrorInfoDeclare;
import org.eclipse.jt.core.def.info.HintInfoDeclare;
import org.eclipse.jt.core.def.info.InfoDeclare;
import org.eclipse.jt.core.def.info.InfoKind;
import org.eclipse.jt.core.def.info.ProcessInfoDeclare;
import org.eclipse.jt.core.def.info.WarningInfoDeclare;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.misc.SXMergeHelper;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.Typable;
import org.eclipse.jt.core.type.TypeFactory;


/**
 * 信息定义接口实现
 * 
 * @author Jeff Tang
 * 
 */
class InfoDefineImpl extends NamedDefineImpl implements InfoDeclare,
		ErrorInfoDeclare, HintInfoDeclare, WarningInfoDeclare,
		ProcessInfoDeclare {
	private String message = "";// 不可为null
	/**
	 * 格式化信息，按顺序分别记录文本长度、参数位置，参数占位长度<br>
	 * 为正值时为文本长度与参数占位长度，为0或负值时为参数偏移量<br>
	 * 参数位置后紧跟的是参数占位长度<br>
	 * 例如：<br>
	 * "文本文本{参数A}文本{参数B1}{参数A}文本"的格式化文本对应格式化信息为<br>
	 * {4,0,3,2,-1,4,0,3,2}
	 */
	private short[] frmtInfo;

	public final String getMessage() {
		return this.message;
	}

	public final void setMessage(String value) {
		this.message = Utils.noneNull(value);
	}

	private final short[] ensureFormatInfo() {
		short[] frmtInfo = this.frmtInfo;
		if (frmtInfo == null) {
			this.frmtInfo = frmtInfo = this.buildFrmtInfo(this.message, true);
		}
		return frmtInfo;
	}

	private LocaleInfoImpl langs;

	private LocaleInfoImpl findLocaleInfo(String language) {
		for (LocaleInfoImpl lang = this.langs; lang != null; lang = lang.next) {
			// language is intern() ed
			if (lang.language == language) {
				return lang;
			}
		}
		return null;
	}

	final void putLanguageMessage(String language, String message) {
		if (this.findLocaleInfo(language) == null) {
			this.langs = new LocaleInfoImpl(language, message, this.langs);
		}
	}

	static final short[] emptyFrmtInfo = {};

	/**
	 * 格式化文本参数以"{参数名}"形式表现
	 */
	short[] buildFrmtInfo(String message, boolean appendParam) {
		final int frmtL = message.length();
		if (frmtL == 0) {
			return emptyFrmtInfo;
		}
		short[] frmtInfo = emptyFrmtInfo;
		int infoSize = 0;
		for (int i = 0, start = 0, pStart = -1; i < frmtL; i++) {
			switch (message.charAt(i)) {
			case '{':
				pStart = i + 1;
				break;
			case '}':
				if (pStart < 0) {
					break;
				}
				int pEnd = i;
				for (int j = pStart; j < i; j++) {
					if (message.charAt(j) == ':') {
						// 存在指定类型的参数
						pEnd = j;
						break;// for
					}
				}
				int index = this.parameters.indexOfName(message, pStart, pEnd
						- pStart);
				if (index < 0) {
					tryAppendParam: {
						if (appendParam && pEnd != i) {
							// 指定类型的参数，尝试追加参数
							final String typeName = message.substring(pEnd + 1,
									i);
							final DataType pType = TypeFactory.typeOf(typeName,
									null);
							if (pType != UnknownType.TYPE) {
								InfoParameterDefineImpl param = new InfoParameterDefineImpl(
										this, message.substring(pStart, pEnd),
										pType);
								index = this.parameters.size();
								this.parameters.add(param);
								break tryAppendParam;
							}
						}
						pStart = -1;
						break;// switch
					}
				}
				if (frmtInfo == emptyFrmtInfo) {
					frmtInfo = new short[4];
				} else if (frmtInfo.length - infoSize < 3) {
					// 文本+参数位置+参数占位长度可能会用到3个short
					final short[] newFrmtInfo = new short[frmtInfo.length * 2];
					System.arraycopy(frmtInfo, 0, newFrmtInfo, 0, infoSize);
					frmtInfo = newFrmtInfo;
				}
				final int textL = pStart - 1 - start;
				if (textL > 0) {
					// 文本长度
					frmtInfo[infoSize++] = (short) textL;
				}
				// 参数偏移
				frmtInfo[infoSize++] = (short) -index;
				// 参数占位长度
				frmtInfo[infoSize++] = (short) (i - pStart + 2);
				start = i + 1;
				pStart = -1;
				break;// switch
			}
		}
		if (frmtInfo.length != infoSize) {
			short[] newFrmtInfo = new short[infoSize];
			System.arraycopy(frmtInfo, 0, newFrmtInfo, 0, infoSize);
			frmtInfo = newFrmtInfo;
		}
		return frmtInfo;
	}

	final void formatMessage(Locale locale, Appendable to, Object p1,
			Object p2, Object p3, Object[] params) {
		if (to == null) {
			throw new NullArgumentException("to");
		}
		this.format(locale, to, p1, p2, p3, params, true);
	}

	final String formatMessage(Locale locale, Object p1, Object p2, Object p3,
			Object[] params) {
		return this.format(locale, null, p1, p2, p3, params, true);
	}

	final void formatMessage(Locale locale, Appendable to, Object[] params) {
		if (to == null) {
			throw new NullArgumentException("to");
		}
		this.format(locale, to, null, null, null, params, false);
	}

	final String formatMessage(Locale locale, Object[] params) {
		return this.format(locale, null, null, null, null, params, false);
	}

	/**
	 * 格式化
	 * 
	 * @param withPX
	 *            参数是否规格化过
	 */
	private String format(Locale locale, Appendable to, Object p1, Object p2,
			Object p3, Object[] params, boolean withPX) {
		final boolean returnString = to == null;
		final short[] aFrmtInfo;
		final String aMessage;
		findLocale: {
			if (locale != null) {
				final String language = locale.getLanguage();
				if (language.length() > 0) {
					final LocaleInfoImpl l = this.findLocaleInfo(language);
					if (l != null) {
						aMessage = l.message;
						aFrmtInfo = l.ensureFormatInfo(this);
						break findLocale;
					}
				}
			}
			aMessage = this.message;
			aFrmtInfo = this.ensureFormatInfo();
		}
		try {
			if (aFrmtInfo == emptyFrmtInfo) {
				if (returnString) {
					return aMessage;
				} else {
					to.append(aMessage);
					return null;
				}
			}
			if (returnString) {
				to = new StringBuilder();
			}
			final int msgl = aMessage.length();
			final int pl = params != null ? params.length : 0;
			int start = 0;
			final ArrayList<InfoParameterDefineImpl> pas = this.parameters;
			for (int i = 0, c = aFrmtInfo.length; i < c; i++) {
				int v = aFrmtInfo[i];
				if (v > 0) {// 文本
					int oldStart = start;
					start += v;
					// 追加文本
					to.append(aMessage, oldStart, start);
				} else {// 参数
					v = -v;
					Object p;
					if (withPX) {// 没有进行过参数的规格化
						switch (v) {
						case 0:
							p = p1;
							break;
						case 1:
							p = p2;
							break;
						case 2:
							p = p3;
							break;
						default:
							final int pi = v - 3;
							if (pi < pl) {
								p = params[pi];
							} else {
								p = null;
							}
						}
						p = pas.get(v).convertWithDefault(p);
					} else if (v < pl) {
						p = params[v];
					} else {
						p = null;
					}
					// 追加参数
					if (p != null) {
						to.append(p.toString());
					}
					// 跳过参数文本
					start += aFrmtInfo[++i];
				}
			}
			to.append(aMessage, start, msgl);
		} catch (IOException e) {
			throw Utils.tryThrowException(e);
		}
		return returnString ? to.toString() : null;
	}

	final InfoKind kind;

	public final InfoKind getKind() {
		return this.kind;
	}

	final NamedDefineContainerImpl<InfoParameterDefineImpl> parameters = new NamedDefineContainerImpl<InfoParameterDefineImpl>();

	public final NamedDefineContainerImpl<InfoParameterDefineImpl> getParameters() {
		return this.parameters;
	}

	public final InfoParameterDefineImpl newParameter(String name, DataType type) {
		this.checkModifiable();
		InfoParameterDefineImpl param = new InfoParameterDefineImpl(this, name,
				type);
		this.parameters.add(param);
		return param;
	}

	public final InfoParameterDefineImpl newParameter(String name,
			Typable typable) {
		return this.newParameter(name, (DataType) typable.getType());
	}

	public final InfoParameterDefineImpl newParameter(FieldDefine sample) {
		return this.newParameter(sample.getName(), sample.getType());
	}

	public InfoDefineImpl(String name, InfoKind kind, String message) {
		super(name);
		if (kind == null) {
			throw new NullArgumentException("kind");
		}
		this.kind = kind;
		this.isReportToUser = kind.defaultReportToUser;
		this.setMessage(message);
	}

	private boolean isNeedLog;

	public final boolean isNeedLog() {
		return this.isNeedLog;
	}

	public void setNeedLog(boolean value) {
		this.isNeedLog = value;
	}

	private boolean isReportToUser;

	public final boolean isReportToUser() {
		return this.isReportToUser;
	}

	public final boolean setReportToUser(boolean value) {
		return this.isReportToUser;
	}

	// ////////////////////////////
	// /// XML
	// ////////////////////////////
	// ///////////xml///////////////////
	static final String xml_attr_kind = "kind";
	static final String xml_attr_log = "log";
	static final String xml_attr_user = "user";

	static final String xml_element_info = "info";
	static final String xml_element_params = "params";

	@Override
	public String getXMLTagName() {
		return xml_element_info;
	}

	@Override
	public void render(SXElement element) {
		super.render(element);
		element.setEnum(xml_attr_kind, this.kind);
		element.setBoolean(xml_attr_log, this.isNeedLog);
		element.setBoolean(xml_attr_user, this.isReportToUser);
		this.parameters.renderInto(element, xml_element_params, 0);
	}

	@Override
	void merge(SXElement element, SXMergeHelper helper) {
		super.merge(element, helper);
		this.isNeedLog = element.getBoolean(xml_attr_log, this.isNeedLog);
		this.isReportToUser = element.getBoolean(xml_attr_user,
				this.isReportToUser);
		for (SXElement paramElement = element.firstChild(xml_element_params,
				InfoParameterDefineImpl.xml_element_name_param); paramElement != null; paramElement = paramElement
				.nextSibling(InfoParameterDefineImpl.xml_element_name_param)) {
			String fn = paramElement.getAttribute(
					NamedDefineImpl.xml_attr_name, null);
			InfoParameterDefineImpl param = this.parameters.find(fn);
			if (param == null) {
				param = new InfoParameterDefineImpl(this, fn, paramElement
						.getAsType(FieldDefineImpl.xml_attr_type,
								helper.querier));
				this.parameters.add(param);
			}
			param.merge(paramElement, helper);
		}
	}
}
