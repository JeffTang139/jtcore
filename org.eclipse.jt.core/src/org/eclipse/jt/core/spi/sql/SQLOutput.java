package org.eclipse.jt.core.spi.sql;

/**
 * �������������ӿ�
 * 
 * @author Jeff Tang
 * 
 */
public interface SQLOutput {
	/**
	 * ��������Ϣ���쳣��ʽ�׳�
	 */
	public static final SQLOutput THROW_EXCEPTION = new SQLOutput() {
		public void raise(SQLParseException ex) {
			throw ex;
		}
	};

	/**
	 * ���������Ϣ����׼����豸
	 */
	public static final SQLOutput PRINT_TO_CONSOLE = new SQLOutput() {
		public void raise(SQLParseException ex) {
			System.err.println("DNASQL Error: at line " + (ex.line + 1)
					+ " col " + (ex.col + 1) + " \n\t" + ex.getMessage());
		}
	};

	/**
	 * ����쳣
	 * 
	 * @param ex
	 */
	public void raise(SQLParseException ex);
}
