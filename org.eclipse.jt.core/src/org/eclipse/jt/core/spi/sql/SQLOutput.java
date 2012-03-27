package org.eclipse.jt.core.spi.sql;

/**
 * 分析器输出捕获接口
 * 
 * @author Jeff Tang
 * 
 */
public interface SQLOutput {
	/**
	 * 将错误信息以异常形式抛出
	 */
	public static final SQLOutput THROW_EXCEPTION = new SQLOutput() {
		public void raise(SQLParseException ex) {
			throw ex;
		}
	};

	/**
	 * 输出错误信息到标准输出设备
	 */
	public static final SQLOutput PRINT_TO_CONSOLE = new SQLOutput() {
		public void raise(SQLParseException ex) {
			System.err.println("DNASQL Error: at line " + (ex.line + 1)
					+ " col " + (ex.col + 1) + " \n\t" + ex.getMessage());
		}
	};

	/**
	 * 输出异常
	 * 
	 * @param ex
	 */
	public void raise(SQLParseException ex);
}
