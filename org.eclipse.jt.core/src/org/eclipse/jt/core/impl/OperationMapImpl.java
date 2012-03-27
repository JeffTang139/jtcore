package org.eclipse.jt.core.impl;

import java.util.HashMap;

import org.eclipse.jt.core.auth.Operation;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.impl.ResourceServiceBase.AuthorizableResourceProvider;
import org.eclipse.jt.core.resource.ResourceService.OperationMap;


/**
 * 权限操作映射表<br>
 * 当某类资源的某项操作受另一类资源的某项操作的权限影响时，需要定义他们的操作映射表。<br>
 * 该类实际为一个哈希表对象。
 * 
 * @param <TResourceFacade>
 *            映射源资源外观类
 * @param <TMapToResourceFacade>
 *            映射目标资源外观类
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
			throw new IllegalArgumentException("operation 类型有误");
		}
		if (!this.mapToOpProvider.operationEnumClass.isInstance(mapToOperation)) {
			throw new IllegalArgumentException("mapToOperation 类型有误");
		}
		if (this.put(operation, mapToOperation) != null) {
			throw new IllegalArgumentException(operation + "重复映射");
		}
	}

}
