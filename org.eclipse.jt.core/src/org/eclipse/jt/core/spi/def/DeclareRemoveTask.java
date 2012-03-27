package org.eclipse.jt.core.spi.def;

import org.eclipse.jt.core.def.MetaElement;
import org.eclipse.jt.core.def.MetaElementType;
import org.eclipse.jt.core.def.NamedDefine;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.invoke.Return;
import org.eclipse.jt.core.invoke.SimpleTask;

/**
 * ģ�Ͷ����ύ����<br>
 * <code>context.handle(new DeclareRemoveTask(MetaElementType.TABLE,"YourTableName")) </code>
 * 
 * @author Jeff Tang
 * 
 */
public final class DeclareRemoveTask extends SimpleTask {
	/**
	 * ��Ҫ�Ƴ��Ķ��������
	 */
	public final MetaElementType type;
	/**
	 * ���������
	 */
	public final String name;
	/**
	 * ��ʾϵͳ�Ƿ����ύ��Ӱ������ʱ��<br>
	 * ��ʹ��Ϊtrue,ϵͳҲ�������������Ƿ�Ӱ������ʱ<br>
	 * �������������Ի᷵�أ�true����Ӱ��������ʱ��false����û��Ӱ������ʱ
	 */
	@Return
	public boolean applyToRuntime;

	public DeclareRemoveTask(NamedDefine define, boolean applyToRuntime) {
		if (!(define instanceof MetaElement)) {
			throw new IllegalArgumentException("��Ч�Ķ���");
		}
		this.type = ((MetaElement) define).getMetaElementType();
		this.name = define.getName();
		this.applyToRuntime = applyToRuntime;
	}

	public DeclareRemoveTask(MetaElementType type, String name,
	        boolean applyToRuntime) {
		if (type == null) {
			throw new NullArgumentException("type");
		}
		if (name == null || name.length() == 0) {
			throw new NullArgumentException("name");
		}
		this.type = type;
		this.name = name;
		this.applyToRuntime = applyToRuntime;
	}
}
