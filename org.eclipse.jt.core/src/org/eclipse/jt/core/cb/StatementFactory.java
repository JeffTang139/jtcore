package org.eclipse.jt.core.cb;

import org.eclipse.jt.core.def.query.DeleteStatementDeclare;
import org.eclipse.jt.core.def.query.InsertStatementDeclare;
import org.eclipse.jt.core.def.query.MappingQueryStatementDeclare;
import org.eclipse.jt.core.def.query.QueryStatementDeclare;
import org.eclipse.jt.core.def.query.UpdateStatementDeclare;
import org.eclipse.jt.core.def.table.TableDefine;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.impl.DeleteStatementImpl;
import org.eclipse.jt.core.impl.InsertStatementImpl;
import org.eclipse.jt.core.impl.MappingQueryStatementImpl;
import org.eclipse.jt.core.impl.QueryStatementImpl;
import org.eclipse.jt.core.impl.TableDefineImpl;
import org.eclipse.jt.core.impl.UpdateStatementImpl;

/**
 * 语句工厂类
 * 
 * <p>
 * 仅提供测试使用
 * 
 * @author Jeff Tang
 * 
 */
public interface StatementFactory {

	public QueryStatementDeclare newQueryStatement(String name);

	public MappingQueryStatementDeclare newMappingQueryStatement(String name,
			Class<?> entityClass);

	public InsertStatementDeclare newInsertStatement(String name,
			TableDefine table);

	public DeleteStatementDeclare newDeleteStatement(String name,
			TableDefine table);

	public UpdateStatementDeclare newUpdateStatement(String name,
			TableDefine table);

	public static final StatementFactory instance = new StatementFactory() {

		public final QueryStatementDeclare newQueryStatement(String name) {
			if (name == null || name.length() == 0) {
				throw new NullArgumentException("名称");
			}
			return new QueryStatementImpl(name);
		}

		public final MappingQueryStatementDeclare newMappingQueryStatement(
				String name, Class<?> entityClass) {
			if (name == null || name.length() == 0) {
				throw new NullArgumentException("名称");
			}
			if (entityClass == null) {
				throw new NullArgumentException("实体类");
			}
			return new MappingQueryStatementImpl(name, entityClass);
		}

		public final InsertStatementDeclare newInsertStatement(String name,
				TableDefine table) {
			if (name == null || name.length() == 0) {
				throw new NullArgumentException("名称");
			}
			if (table == null) {
				throw new NullArgumentException("表定义");
			}
			return new InsertStatementImpl(name, (TableDefineImpl) table);
		}

		public final DeleteStatementDeclare newDeleteStatement(String name,
				TableDefine table) {
			if (name == null || name.length() == 0) {
				throw new NullArgumentException("名称");
			}
			if (table == null) {
				throw new NullArgumentException("表定义");
			}
			return new DeleteStatementImpl(name, (TableDefineImpl) table);
		}

		public final UpdateStatementDeclare newUpdateStatement(String name,
				TableDefine table) {
			if (name == null || name.length() == 0) {
				throw new NullArgumentException("名称");
			}
			if (table == null) {
				throw new NullArgumentException("表定义");
			}
			return new UpdateStatementImpl(name, (TableDefineImpl) table);
		}

	};
}
