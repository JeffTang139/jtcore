package org.eclipse.jt.core.impl;

import java.util.HashMap;

import org.eclipse.jt.core.auth.Operation;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.impl.ResourceServiceBase.AuthorizableResourceProvider;
import org.eclipse.jt.core.resource.ResourceService.OperationMap;


/**
 * Ȩ�޲���ӳ���<br>
 * ��ĳ����Դ��ĳ���������һ����Դ��ĳ�������Ȩ��Ӱ��ʱ����Ҫ�������ǵĲ���ӳ���<br>
 * ����ʵ��Ϊһ����ϣ�����
 * 
 * @param <TResourceFacade>
 *            ӳ��Դ��Դ�����
 * @param <TMapToResourceFacade>
 *            ӳ��Ŀ����Դ�����
 * @author Jeff Tang
 */
final class OperationMapImpl<TResourceFacade, TMapToResourceFacade>
		extends
		HashMap<Enum<? extends Operation<? super TResourceFacade>>, Enum<? extends Operation<? super TMapToResourceFacade>>>
		implements OperationMap<TResourceFacade, TMapToResourceFacade> {

	@SuppressWarnings("unchecked")
	private AuthorizableResourceProvider opProvider;
	@SuppressWarnings("unchecked")
	private AuthorizableResourceProvider mapToOpProvider;

	final OperationEntry[] buildMap() {
		if (this.isEmpty()) {
			return null;
		}
		final OperationEntry[] opEntrys = this.opProvider.operations;
		final OperationEntry[] mapToEntrys = this.mapToOpProvider.operations;
		final OperationEntry[] opmap = new OperationEntry[opEntrys.length];
		int maped = 0;
		for (int i = 0; i < opmap.length; i++) {
			Enum<?> toOp = this.get(opEntrys[i].operation);
			if (toOp != null) {
				opmap[i] = mapToEntrys[toOp.ordinal()];
				maped++;
			}
		}
		if (maped == 0) {
			return null;
		}
		return opmap;
	}

	@SuppressWarnings("unchecked")
	final void setProvider(AuthorizableResourceProvider opProvider,
			AuthorizableResourceProvider mapToOpProvider) {
		if (opProvider == null) {
			throw new NullArgumentException("opProvider");
		}
		if (mapToOpProvider == null) {
			throw new NullArgumentException("mapToOpProvider");
		}
		if (!this.isEmpty()) {
			this.clear();
		}
		this.opProvider = opProvider;
		this.mapToOpProvider = mapToOpProvider;
	}

	private static final long serialVersionUID = 1L;

	public final void map(
			Enum<? extends Operation<? super TResourceFacade>> operation,
			Enum<? extends Operation<? super TMapToResourceFacade>> mapToOperation) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (mapToOperation == null) {
			throw new NullArgumentException("mapToOperation");
		}
		if (!this.opProvider.operationEnumClass.isInstance(operation)) {
			throw new IllegalArgumentException("operation ��������");
		}
		if (!this.mapToOpProvider.operationEnumClass.isInstance(mapToOperation)) {
			throw new IllegalArgumentException("mapToOperation ��������");
		}
		if (this.put(operation, mapToOperation) != null) {
			throw new IllegalArgumentException(operation + "�ظ�ӳ��");
		}
	}

}
