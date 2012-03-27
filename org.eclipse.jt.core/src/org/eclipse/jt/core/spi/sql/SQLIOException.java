package org.eclipse.jt.core.spi.sql;

import java.io.IOException;

/**
 * IO�쳣 (������װ��IOException)
 * 
 * @author Jeff Tang
 * 
 */
public class SQLIOException extends SQLParseException {
	private static final long serialVersionUID = 1L;

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.IO_ERROR;
	}

	public SQLIOException(IOException ex) {
		super(0, 0, ex);
	}
}
