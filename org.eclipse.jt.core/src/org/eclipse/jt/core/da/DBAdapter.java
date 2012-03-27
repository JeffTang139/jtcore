package org.eclipse.jt.core.da;

import java.util.List;

import org.eclipse.jt.core.LifeHandle;
import org.eclipse.jt.core.def.obja.StructDefine;
import org.eclipse.jt.core.def.query.DeleteStatementDeclare;
import org.eclipse.jt.core.def.query.InsertStatementDeclare;
import org.eclipse.jt.core.def.query.MappingQueryStatementDeclare;
import org.eclipse.jt.core.def.query.MappingQueryStatementDefine;
import org.eclipse.jt.core.def.query.ModifyStatementDeclarator;
import org.eclipse.jt.core.def.query.ModifyStatementDefine;
import org.eclipse.jt.core.def.query.ORMDeclarator;
import org.eclipse.jt.core.def.query.QueryStatementDeclarator;
import org.eclipse.jt.core.def.query.QueryStatementDeclare;
import org.eclipse.jt.core.def.query.QueryStatementDefine;
import org.eclipse.jt.core.def.query.StatementDeclarator;
import org.eclipse.jt.core.def.query.StatementDeclare;
import org.eclipse.jt.core.def.query.StatementDefine;
import org.eclipse.jt.core.def.query.StoredProcedureDeclarator;
import org.eclipse.jt.core.def.query.StoredProcedureDefine;
import org.eclipse.jt.core.def.query.UpdateStatementDeclare;
import org.eclipse.jt.core.def.table.EntityTableDeclarator;
import org.eclipse.jt.core.def.table.HierarchyDefine;
import org.eclipse.jt.core.def.table.TableDeclarator;
import org.eclipse.jt.core.def.table.TableDefine;
import org.eclipse.jt.core.type.GUID;


/**
 * ���ݿ�������<br>
 * 
 * 
 * @author Jeff Tang
 * 
 */
public interface DBAdapter extends LifeHandle {

	/**
	 * ��ȡ�µĵ�����ȫ�ֲ��ظ���RECID�����ڱ��¼�Ĳ���
	 */
	public GUID newRECID();

	/**
	 * ����µĵ����ı����ݿⲻ�ظ����а汾��
	 */
	public long newRECVER();

	/**
	 * ������ѯ����
	 */
	public QueryStatementDeclare newQueryStatement();

	/**
	 * ��ָ����ѯ����Ϊ����,���Ʒ���һ���µĲ�ѯ����
	 * 
	 * @param sample
	 * @return
	 */
	public QueryStatementDeclare newQueryStatement(QueryStatementDefine sample);

	/**
	 * ����������䶨��
	 * 
	 * @param table
	 * @return
	 */
	public InsertStatementDeclare newInsertStatement(TableDefine table);

	/**
	 * ����������䶨��
	 * 
	 * @param table
	 * @return
	 */
	public InsertStatementDeclare newInsertStatement(TableDeclarator table);

	/**
	 * ����ɾ����䶨��
	 * 
	 * @param table
	 * @return
	 */
	public DeleteStatementDeclare newDeleteStatement(TableDefine table);

	/**
	 * ����ɾ����䶨��
	 * 
	 * @param table
	 * @return
	 */
	public DeleteStatementDeclare newDeleteStatement(TableDeclarator table);

	/**
	 * ����������䶨��
	 * 
	 * @param table
	 * @return
	 */
	public UpdateStatementDeclare newUpdateStatement(TableDefine table);

	/**
	 * ����������䶨��
	 * 
	 * @param table
	 * @param name
	 * @return
	 */
	public UpdateStatementDeclare newUpdateStatement(TableDefine table,
			String name);

	/**
	 * ����������䶨��
	 * 
	 * @param table
	 * @return
	 */
	public UpdateStatementDeclare newUpdateStatement(TableDeclarator table);

	/**
	 * ����������䶨��
	 * 
	 * @param table
	 * @param name
	 * @return
	 */
	public UpdateStatementDeclare newUpdateStatement(TableDeclarator table,
			String name);

	/**
	 * ����ORM��ѯ���
	 */
	public MappingQueryStatementDeclare newMappingQueryStatement(
			Class<?> entityClass);

	/**
	 * ����ORM��ѯ���
	 * 
	 * @param entityClass
	 * @param name
	 * @return
	 */
	public MappingQueryStatementDeclare newMappingQueryStatement(
			Class<?> entityClass, String name);

	/**
	 * ����ORM��ѯ���
	 */
	public MappingQueryStatementDeclare newMappingQueryStatement(
			EntityTableDeclarator<?> table);

	/**
	 * ����ORM��ѯ���
	 */
	public MappingQueryStatementDeclare newMappingQueryStatement(
			StructDefine model);

	public MappingQueryStatementDeclare newMappingQueryStatement(
			StructDefine model, String name);

	/**
	 * ����D&ASql
	 * 
	 * @param dnaSql
	 *            D&ASql�ı���������String/StringBuilder/StringBuffer
	 * @return �������������
	 */
	public StatementDeclare parseStatement(CharSequence dnaSql);

	/**
	 * ����D&ASql
	 * 
	 * @param dnaSql
	 *            D&ASql�ı���������String/StringBuilder/StringBuffer
	 * @param clz
	 *            ��䶨������
	 * @return �������������
	 */
	public <TStatement extends StatementDeclare> TStatement parseStatement(
			CharSequence dnaSql, Class<TStatement> clz);

	/**
	 * ִ�в�ѯ,װ�ؼ�¼��
	 * 
	 * @param query
	 *            ��ѯ����
	 * @param argValues
	 *            ����ֵ
	 * @return ��¼��
	 */
	public RecordSet openQuery(QueryStatementDefine query, Object... argValues);

	/**
	 * ִ�в�ѯ,װ�ؼ�¼��
	 * 
	 * @param query
	 *            ��ѯ����
	 * @param argValues
	 *            ����ֵ
	 * @return ��¼��
	 */
	public RecordSet openQuery(QueryStatementDeclarator query,
			Object... argValues);

	/**
	 * ִ�д����޶��Ĳ�ѯ,װ�ؼ�¼��
	 * 
	 * @param query
	 *            ��ѯ����
	 * @param offset
	 *            ��ָ��ƫ������ʼ���ؽ��,�ӵ�1�п�ʼ������ƫ����Ϊ0
	 * @param rowCount
	 *            �ܷ�������
	 * @param argValues
	 *            ����
	 * @return
	 */
	public RecordSet openQueryLimit(QueryStatementDefine query, long offset,
			long rowCount, Object... argValues);

	/**
	 * ִ�д����޶��Ĳ�ѯ,װ�ؼ�¼��
	 * 
	 * @param query
	 *            ��ѯ����
	 * @param offset
	 *            ��ָ��ƫ������ʼ���ؽ��,�ӵ�1�п�ʼ������ƫ����Ϊ0
	 * @param rowCount
	 *            �ܷ�������
	 * @param argValues
	 *            ����
	 * @return
	 */
	public RecordSet openQueryLimit(QueryStatementDeclarator query,
			long offset, long rowCount, Object... argValues);

	/**
	 * ִ�в�ѯ,ʹ��ָ���������������
	 * 
	 * @param query
	 * @param action
	 * @param argValues
	 */
	public void iterateQuery(QueryStatementDefine query,
			RecordIterateAction action, Object... argValues);

	/**
	 * ִ�в�ѯ,ʹ��ָ���������������
	 * 
	 * @param query
	 * @param action
	 * @param argValues
	 */
	public void iterateQuery(QueryStatementDeclarator query,
			RecordIterateAction action, Object... argValues);

	/**
	 * ִ�д����޶��Ĳ�ѯ,ʹ��ָ���������������
	 * 
	 * @param query
	 * @param action
	 * @param offset
	 * @param rowCount
	 * @param argValues
	 */
	public void iterateQueryLimit(QueryStatementDefine query,
			RecordIterateAction action, long offset, long rowCount,
			Object... argValues);

	/**
	 * ִ�д����޶��Ĳ�ѯ,ʹ��ָ���������������
	 * 
	 * @param query
	 * @param action
	 * @param offset
	 * @param rowCount
	 * @param argValues
	 */
	public void iterateQueryLimit(QueryStatementDeclarator query,
			RecordIterateAction action, long offset, long rowCount,
			Object... argValues);

	/**
	 * ִ�в�ѯ,���ؽ����һ�е�һ�е�ֵ
	 * 
	 * @param query
	 *            ��ѯ����
	 * @param argValues
	 *            ����
	 * @return
	 */
	public Object executeScalar(QueryStatementDefine query, Object... argValues);

	/**
	 * ִ�в�ѯ,���ؽ����һ�е�һ�е�ֵ
	 * 
	 * @param query
	 *            ��ѯ����
	 * @param argValues
	 *            ����
	 * @return
	 */
	public Object executeScalar(QueryStatementDeclarator query,
			Object... argValues);

	/**
	 * ��ȡ��ѯ�����������
	 * 
	 * @param query
	 * @param argValues
	 * @return
	 */
	public int rowCountOf(QueryStatementDefine query, Object... argValues);

	/**
	 * ��ȡ��ѯ�����������
	 * 
	 * @param query
	 * @param argValues
	 * @return
	 */
	public int rowCountOf(QueryStatementDeclarator query, Object... argValues);

	/**
	 * ��ȡ��ѯ�����������
	 * 
	 * @param query
	 * @param argValues
	 * @return
	 */
	public long rowCountOfL(QueryStatementDefine query, Object... argValues);

	/**
	 * ��ȡ��ѯ�����������
	 * 
	 * @param query
	 * @param argValues
	 * @return
	 */
	public long rowCountOfL(QueryStatementDeclarator query, Object... argValues);

	/**
	 * ִ�����ݿ�������
	 * 
	 * @param statement
	 *            ��䶨��
	 * @param argValues
	 *            ����ֵ
	 * @return ���¼���
	 */
	public int executeUpdate(ModifyStatementDefine statement,
			Object... argValues);

	/**
	 * ִ�����ݿ�������
	 * 
	 * @param statement
	 * @param argValues
	 * @return
	 */
	public int executeUpdate(ModifyStatementDeclarator<?> statement,
			Object... argValues);

	public void executeUpdate(StoredProcedureDefine procedure,
			Object... argValues);

	public void executeUpdate(StoredProcedureDeclarator procedure,
			Object... argValues);

	/**
	 * ׼�����ݿ���䶨��
	 * 
	 * @param statement
	 *            ���ݿ��������
	 * @return ���ݿ�ִ������
	 */
	public DBCommand prepareStatement(StatementDefine statement);

	/**
	 * ׼�����ݿ���䶨��
	 * 
	 * @param statement
	 *            ���ݿ���䶨��
	 * @return ���ݿ�ִ������
	 */
	public DBCommand prepareStatement(StatementDeclarator<?> statement);

	/**
	 * ����DNASql׼�����ݿ���䶨�壬���Է������ݿ�
	 * 
	 * @param DNASql
	 *            D&ASql�����Ϳ�����String,StringBuilder,CharBuffer��
	 * @return �������ݿ���ʶ���
	 */
	public DBCommand prepareStatement(CharSequence dnaSql);

	/**
	 * ����ʵ����������
	 * 
	 * @param <TEntity>
	 *            ʵ������
	 * @param orm
	 *            ʵ��ӳ��
	 * @return ���ط�����
	 */
	public <TEntity> ORMAccessor<TEntity> newORMAccessor(
			ORMDeclarator<TEntity> orm);

	/**
	 * ����ʵ����������
	 * 
	 * @param <TEntity>
	 * @param entityClass
	 * @param mappingQuery
	 * @return
	 */
	public <TEntity> ORMAccessor<TEntity> newORMAccessor(
			Class<TEntity> entityClass, MappingQueryStatementDefine mappingQuery);

	/**
	 * ����ʵ����������
	 * 
	 * @param mappingQuery
	 *            ʵ���ѯ����
	 * @return
	 */
	public ORMAccessor<Object> newORMAccessor(
			MappingQueryStatementDefine mappingQuery);

	/**
	 * ����ʵ����������
	 * 
	 * @param <TEntity>
	 * @param table
	 *            ʵ���������
	 * @return
	 */
	public <TEntity> ORMAccessor<TEntity> newORMAccessor(
			EntityTableDeclarator<TEntity> table);

	/**
	 * ���½ڵ���ָ�����ζ����еĸ��ڵ�
	 * 
	 * @param hierarchy
	 *            ���ζ���
	 * @param parent
	 *            ���ڵ�
	 * @param child
	 *            �ӽڵ�
	 */
	public void hierarchyMoveTo(HierarchyDefine hierarchy, GUID parent,
			GUID child);

	/**
	 * ���½ڵ���ָ�����ζ����еĸ��ڵ�
	 * 
	 * @param hierarchy
	 *            ���ζ���
	 * @param parent
	 *            ���ڵ�
	 * @param child
	 *            �ӽڵ�
	 * @param others
	 *            �����ӽڵ�
	 */
	public void hierarchyMoveTo(HierarchyDefine hierarchy, GUID parent,
			GUID child, GUID... others);

	/**
	 * ���½ڵ���ָ�����ζ����еĸ��ڵ�
	 * 
	 * @param hierarchy
	 *            ���ζ���
	 * @param parent
	 *            ���ڵ�
	 * @param children
	 *            �ӽڵ�
	 */
	public void hierarchyMoveTo(HierarchyDefine hierarchy, GUID parent,
			List<GUID> children);

	/**
	 * ���½ڵ���ָ�����ζ����еĸ��ڵ�
	 * 
	 * @param hierarchy
	 *            ���ζ���
	 * @param parent
	 *            ���ڵ�
	 * @param children
	 *            �ӽڵ�
	 */
	public void hierarchyMoveTo(HierarchyDefine hierarchy, GUID parent,
			Iterable<GUID> children);

}
