package org.eclipse.jt.core.impl;

/*
 * Java CSV is a stream based library for reading and writing
 * CSV and other delimited data.
 *   
 * Copyright (C) Bruce Dunwiddie bruce@csvreader.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.util.HashMap;

/**
 * A stream based parser for parsing delimited text data from a file or a
 * stream.
 */
public class CsvReader {
	private Reader inputStream = null;

	private String fileName = null;

	// this holds all the values for switches that the user is allowed to set
	private UserSettings userSettings = new UserSettings();

	private Charset charset = null;

	private boolean useCustomRecordDelimiter = false;

	// this will be our working buffer to hold data chunks
	// read in from the data file

	private DataBuffer dataBuffer = new DataBuffer();

	private ColumnBuffer columnBuffer = new ColumnBuffer();

	private RawRecordBuffer rawBuffer = new RawRecordBuffer();

	private boolean[] isQualified = null;

	private String rawRecord = "";

	private HeadersHolder headersHolder = new HeadersHolder();

	// these are all more or less global loop variables
	// to keep from needing to pass them all into various
	// methods during parsing

	private boolean startedColumn = false;

	private boolean startedWithQualifier = false;

	private boolean hasMoreData = true;

	private char lastLetter = '\0';

	private boolean hasReadNextLine = false;

	private int columnsCount = 0;

	private long currentRecord = 0;

	private String[] values = new String[StaticSettings.INITIAL_COLUMN_COUNT];

	private boolean initialized = false;

	private boolean closed = false;

	/**
	 * Double up the text qualifier to represent an occurance of the text
	 * qualifier.
	 */
	public static final int ESCAPE_MODE_DOUBLED = 1;

	/**
	 * Use a backslash character before the text qualifier to represent an
	 * occurance of the text qualifier.
	 */
	public static final int ESCAPE_MODE_BACKSLASH = 2;

	/**
	 * Creates a {@link com.csvreader.CsvReader CsvReader} object using a file
	 * as the data source.
	 * 
	 * @param fileName
	 *            The path to the file to use as the data source.
	 * @param delimiter
	 *            The character to use as the column delimiter.
	 * @param charset
	 *            The {@link java.nio.charset.Charset Charset} to use while
	 *            parsing the data.
	 */
	public CsvReader(String fileName, char delimiter, Charset charset)
	        throws FileNotFoundException {
		if (fileName == null) {
			throw new IllegalArgumentException(
			        "Parameter fileName can not be null.");
		}

		if (charset == null) {
			throw new IllegalArgumentException(
			        "Parameter charset can not be null.");
		}

		if (!new File(fileName).exists()) {
			throw new FileNotFoundException("File " + fileName
			        + " does not exist.");
		}

		this.fileName = fileName;
		this.userSettings.Delimiter = delimiter;
		this.charset = charset;

		this.isQualified = new boolean[this.values.length];
	}

	/**
	 * Creates a {@link com.csvreader.CsvReader CsvReader} object using a file
	 * as the data source.&nbsp;Uses ISO-8859-1 as the
	 * {@link java.nio.charset.Charset Charset}.
	 * 
	 * @param fileName
	 *            The path to the file to use as the data source.
	 * @param delimiter
	 *            The character to use as the column delimiter.
	 */
	public CsvReader(String fileName, char delimiter)
	        throws FileNotFoundException {
		this(fileName, delimiter, Charset.defaultCharset());
	}

	/**
	 * Creates a {@link com.csvreader.CsvReader CsvReader} object using a file
	 * as the data source.&nbsp;Uses a comma as the column delimiter and
	 * ISO-8859-1 as the {@link java.nio.charset.Charset Charset}.
	 * 
	 * @param fileName
	 *            The path to the file to use as the data source.
	 */
	public CsvReader(String fileName) throws FileNotFoundException {
		this(fileName, Letters.COMMA);
	}

	/**
	 * Constructs a {@link com.csvreader.CsvReader CsvReader} object using a
	 * {@link java.io.Reader Reader} object as the data source.
	 * 
	 * @param inputStream
	 *            The stream to use as the data source.
	 * @param delimiter
	 *            The character to use as the column delimiter.
	 */
	public CsvReader(Reader inputStream, char delimiter) {
		if (inputStream == null) {
			throw new IllegalArgumentException(
			        "Parameter inputStream can not be null.");
		}

		this.inputStream = inputStream;
		this.userSettings.Delimiter = delimiter;
		this.initialized = true;

		this.isQualified = new boolean[this.values.length];
	}

	/**
	 * Constructs a {@link com.csvreader.CsvReader CsvReader} object using a
	 * {@link java.io.Reader Reader} object as the data source.&nbsp;Uses a
	 * comma as the column delimiter.
	 * 
	 * @param inputStream
	 *            The stream to use as the data source.
	 */
	public CsvReader(Reader inputStream) {
		this(inputStream, Letters.COMMA);
	}

	/**
	 * Constructs a {@link com.csvreader.CsvReader CsvReader} object using an
	 * {@link java.io.InputStream InputStream} object as the data source.
	 * 
	 * @param inputStream
	 *            The stream to use as the data source.
	 * @param delimiter
	 *            The character to use as the column delimiter.
	 * @param charset
	 *            The {@link java.nio.charset.Charset Charset} to use while
	 *            parsing the data.
	 */
	public CsvReader(InputStream inputStream, char delimiter, Charset charset) {
		this(new InputStreamReader(inputStream, charset), delimiter);
	}

	/**
	 * Constructs a {@link com.csvreader.CsvReader CsvReader} object using an
	 * {@link java.io.InputStream InputStream} object as the data
	 * source.&nbsp;Uses a comma as the column delimiter.
	 * 
	 * @param inputStream
	 *            The stream to use as the data source.
	 * @param charset
	 *            The {@link java.nio.charset.Charset Charset} to use while
	 *            parsing the data.
	 */
	public CsvReader(InputStream inputStream, Charset charset) {
		this(new InputStreamReader(inputStream, charset));
	}

	public boolean getCaptureRawRecord() {
		return this.userSettings.CaptureRawRecord;
	}

	public void setCaptureRawRecord(boolean captureRawRecord) {
		this.userSettings.CaptureRawRecord = captureRawRecord;
	}

	public String getRawRecord() {
		return this.rawRecord;
	}

	/**
	 * Gets whether leading and trailing whitespace characters are being trimmed
	 * from non-textqualified column data. Default is true.
	 * 
	 * @return Whether leading and trailing whitespace characters are being
	 *         trimmed from non-textqualified column data.
	 */
	public boolean getTrimWhitespace() {
		return this.userSettings.TrimWhitespace;
	}

	/**
	 * Sets whether leading and trailing whitespace characters should be trimmed
	 * from non-textqualified column data or not. Default is true.
	 * 
	 * @param trimWhitespace
	 *            Whether leading and trailing whitespace characters should be
	 *            trimmed from non-textqualified column data or not.
	 */
	public void setTrimWhitespace(boolean trimWhitespace) {
		this.userSettings.TrimWhitespace = trimWhitespace;
	}

	/**
	 * Gets the character being used as the column delimiter. Default is comma,
	 * ','.
	 * 
	 * @return The character being used as the column delimiter.
	 */
	public char getDelimiter() {
		return this.userSettings.Delimiter;
	}

	/**
	 * Sets the character to use as the column delimiter. Default is comma, ','.
	 * 
	 * @param delimiter
	 *            The character to use as the column delimiter.
	 */
	public void setDelimiter(char delimiter) {
		this.userSettings.Delimiter = delimiter;
	}

	public char getRecordDelimiter() {
		return this.userSettings.RecordDelimiter;
	}

	/**
	 * Sets the character to use as the record delimiter.
	 * 
	 * @param recordDelimiter
	 *            The character to use as the record delimiter. Default is
	 *            combination of standard end of line characters for Windows,
	 *            Unix, or Mac.
	 */
	public void setRecordDelimiter(char recordDelimiter) {
		this.useCustomRecordDelimiter = true;
		this.userSettings.RecordDelimiter = recordDelimiter;
	}

	/**
	 * Gets the character to use as a text qualifier in the data.
	 * 
	 * @return The character to use as a text qualifier in the data.
	 */
	public char getTextQualifier() {
		return this.userSettings.TextQualifier;
	}

	/**
	 * Sets the character to use as a text qualifier in the data.
	 * 
	 * @param textQualifier
	 *            The character to use as a text qualifier in the data.
	 */
	public void setTextQualifier(char textQualifier) {
		this.userSettings.TextQualifier = textQualifier;
	}

	/**
	 * Whether text qualifiers will be used while parsing or not.
	 * 
	 * @return Whether text qualifiers will be used while parsing or not.
	 */
	public boolean getUseTextQualifier() {
		return this.userSettings.UseTextQualifier;
	}

	/**
	 * Sets whether text qualifiers will be used while parsing or not.
	 * 
	 * @param useTextQualifier
	 *            Whether to use a text qualifier while parsing or not.
	 */
	public void setUseTextQualifier(boolean useTextQualifier) {
		this.userSettings.UseTextQualifier = useTextQualifier;
	}

	/**
	 * Gets the character being used as a comment signal.
	 * 
	 * @return The character being used as a comment signal.
	 */
	public char getComment() {
		return this.userSettings.Comment;
	}

	/**
	 * Sets the character to use as a comment signal.
	 * 
	 * @param comment
	 *            The character to use as a comment signal.
	 */
	public void setComment(char comment) {
		this.userSettings.Comment = comment;
	}

	/**
	 * Gets whether comments are being looked for while parsing or not.
	 * 
	 * @return Whether comments are being looked for while parsing or not.
	 */
	public boolean getUseComments() {
		return this.userSettings.UseComments;
	}

	/**
	 * Sets whether comments are being looked for while parsing or not.
	 * 
	 * @param useComments
	 *            Whether comments are being looked for while parsing or not.
	 */
	public void setUseComments(boolean useComments) {
		this.userSettings.UseComments = useComments;
	}

	/**
	 * Gets the current way to escape an occurance of the text qualifier inside
	 * qualified data.
	 * 
	 * @return The current way to escape an occurance of the text qualifier
	 *         inside qualified data.
	 */
	public int getEscapeMode() {
		return this.userSettings.EscapeMode;
	}

	/**
	 * Sets the current way to escape an occurance of the text qualifier inside
	 * qualified data.
	 * 
	 * @param escapeMode
	 *            The way to escape an occurance of the text qualifier inside
	 *            qualified data.
	 * @exception IllegalArgumentException
	 *                When an illegal value is specified for escapeMode.
	 */
	public void setEscapeMode(int escapeMode) throws IllegalArgumentException {
		if (escapeMode != ESCAPE_MODE_DOUBLED
		        && escapeMode != ESCAPE_MODE_BACKSLASH) {
			throw new IllegalArgumentException(
			        "Parameter escapeMode must be a valid value.");
		}

		this.userSettings.EscapeMode = escapeMode;
	}

	public boolean getSkipEmptyRecords() {
		return this.userSettings.SkipEmptyRecords;
	}

	public void setSkipEmptyRecords(boolean skipEmptyRecords) {
		this.userSettings.SkipEmptyRecords = skipEmptyRecords;
	}

	/**
	 * Safety caution to prevent the parser from using large amounts of memory
	 * in the case where parsing settings like file encodings don't end up
	 * matching the actual format of a file. This switch can be turned off if
	 * the file format is known and tested. With the switch off, the max column
	 * lengths and max column count per record supported by the parser will
	 * greatly increase. Default is true.
	 * 
	 * @return The current setting of the safety switch.
	 */
	public boolean getSafetySwitch() {
		return this.userSettings.SafetySwitch;
	}

	/**
	 * Safety caution to prevent the parser from using large amounts of memory
	 * in the case where parsing settings like file encodings don't end up
	 * matching the actual format of a file. This switch can be turned off if
	 * the file format is known and tested. With the switch off, the max column
	 * lengths and max column count per record supported by the parser will
	 * greatly increase. Default is true.
	 * 
	 * @param safetySwitch
	 */
	public void setSafetySwitch(boolean safetySwitch) {
		this.userSettings.SafetySwitch = safetySwitch;
	}

	/**
	 * Gets the count of columns found in this record.
	 * 
	 * @return The count of columns found in this record.
	 */
	public int getColumnCount() {
		return this.columnsCount;
	}

	/**
	 * Gets the index of the current record.
	 * 
	 * @return The index of the current record.
	 */
	public long getCurrentRecord() {
		return this.currentRecord - 1;
	}

	/**
	 * Gets the count of headers read in by a previous call to
	 * {@link com.csvreader.CsvReader#readHeaders readHeaders()}.
	 * 
	 * @return The count of headers read in by a previous call to
	 *         {@link com.csvreader.CsvReader#readHeaders readHeaders()}.
	 */
	public int getHeaderCount() {
		return this.headersHolder.Length;
	}

	/**
	 * Returns the header values as a string array.
	 * 
	 * @return The header values as a String array.
	 * @exception IOException
	 *                Thrown if this object has already been closed.
	 */
	public String[] getHeaders() throws IOException {
		this.checkClosed();

		if (this.headersHolder.Headers == null) {
			return null;
		} else {
			// use clone here to prevent the outside code from
			// setting values on the array directly, which would
			// throw off the index lookup based on header name
			String[] clone = new String[this.headersHolder.Length];
			System.arraycopy(this.headersHolder.Headers, 0, clone, 0,
			        this.headersHolder.Length);
			return clone;
		}
	}

	public void setHeaders(String[] headers) {
		this.headersHolder.Headers = headers;

		this.headersHolder.IndexByName.clear();

		if (headers != null) {
			this.headersHolder.Length = headers.length;
		} else {
			this.headersHolder.Length = 0;
		}

		// use headersHolder.Length here in case headers is null
		for (int i = 0; i < this.headersHolder.Length; i++) {
			this.headersHolder.IndexByName.put(headers[i], new Integer(i));
		}
	}

	public String[] getValues() throws IOException {
		this.checkClosed();

		// need to return a clone, and can't use clone because values.Length
		// might be greater than columnsCount
		String[] clone = new String[this.columnsCount];
		System.arraycopy(this.values, 0, clone, 0, this.columnsCount);
		return clone;
	}

	/**
	 * Returns the current column value for a given column index.
	 * 
	 * @param columnIndex
	 *            The index of the column.
	 * @return The current column value.
	 * @exception IOException
	 *                Thrown if this object has already been closed.
	 */
	public String get(int columnIndex) throws IOException {
		this.checkClosed();

		if (columnIndex > -1 && columnIndex < this.columnsCount) {
			return this.values[columnIndex];
		} else {
			return "";
		}
	}

	/**
	 * Returns the current column value for a given column header name.
	 * 
	 * @param headerName
	 *            The header name of the column.
	 * @return The current column value.
	 * @exception IOException
	 *                Thrown if this object has already been closed.
	 */
	public String get(String headerName) throws IOException {
		this.checkClosed();

		return this.get(this.getIndex(headerName));
	}

	/**
	 * Creates a {@link com.csvreader.CsvReader CsvReader} object using a string
	 * of data as the source.&nbsp;Uses ISO-8859-1 as the
	 * {@link java.nio.charset.Charset Charset}.
	 * 
	 * @param data
	 *            The String of data to use as the source.
	 * @return A {@link com.csvreader.CsvReader CsvReader} object using the
	 *         String of data as the source.
	 */
	public static CsvReader parse(String data) {
		if (data == null) {
			throw new IllegalArgumentException(
			        "Parameter data can not be null.");
		}

		return new CsvReader(new StringReader(data));
	}

	/**
	 * Reads another record.
	 * 
	 * @return Whether another record was successfully read or not.
	 * @exception IOException
	 *                Thrown if an error occurs while reading data from the
	 *                source stream.
	 */
	public boolean readRecord() throws IOException {
		this.checkClosed();

		this.columnsCount = 0;
		this.rawBuffer.Position = 0;

		this.dataBuffer.LineStart = this.dataBuffer.Position;

		this.hasReadNextLine = false;

		// check to see if we've already found the end of data

		if (this.hasMoreData) {
			// loop over the data stream until the end of data is found
			// or the end of the record is found

			do {
				if (this.dataBuffer.Position == this.dataBuffer.Count) {
					this.checkDataLength();
				} else {
					this.startedWithQualifier = false;

					// grab the current letter as a char

					char currentLetter = this.dataBuffer.Buffer[this.dataBuffer.Position];

					if (this.userSettings.UseTextQualifier
					        && currentLetter == this.userSettings.TextQualifier) {
						// this will be a text qualified column, so
						// we need to set startedWithQualifier to make it
						// enter the seperate branch to handle text
						// qualified columns

						this.lastLetter = currentLetter;

						// read qualified
						this.startedColumn = true;
						this.dataBuffer.ColumnStart = this.dataBuffer.Position + 1;
						this.startedWithQualifier = true;
						boolean lastLetterWasQualifier = false;

						char escapeChar = this.userSettings.TextQualifier;

						if (this.userSettings.EscapeMode == ESCAPE_MODE_BACKSLASH) {
							escapeChar = Letters.BACKSLASH;
						}

						boolean eatingTrailingJunk = false;
						boolean lastLetterWasEscape = false;
						boolean readingComplexEscape = false;
						int escape = ComplexEscape.UNICODE;
						int escapeLength = 0;
						char escapeValue = (char) 0;

						this.dataBuffer.Position++;

						do {
							if (this.dataBuffer.Position == this.dataBuffer.Count) {
								this.checkDataLength();
							} else {
								// grab the current letter as a char

								currentLetter = this.dataBuffer.Buffer[this.dataBuffer.Position];

								if (eatingTrailingJunk) {
									this.dataBuffer.ColumnStart = this.dataBuffer.Position + 1;

									if (currentLetter == this.userSettings.Delimiter) {
										this.endColumn();
									} else if ((!this.useCustomRecordDelimiter && (currentLetter == Letters.CR || currentLetter == Letters.LF))
									        || (this.useCustomRecordDelimiter && currentLetter == this.userSettings.RecordDelimiter)) {
										this.endColumn();

										this.endRecord();
									}
								} else if (readingComplexEscape) {
									escapeLength++;

									switch (escape) {
									case ComplexEscape.UNICODE:
										escapeValue *= (char) 16;
										escapeValue += hexToDec(currentLetter);

										if (escapeLength == 4) {
											readingComplexEscape = false;
										}

										break;
									case ComplexEscape.OCTAL:
										escapeValue *= (char) 8;
										escapeValue += (char) (currentLetter - '0');

										if (escapeLength == 3) {
											readingComplexEscape = false;
										}

										break;
									case ComplexEscape.DECIMAL:
										escapeValue *= (char) 10;
										escapeValue += (char) (currentLetter - '0');

										if (escapeLength == 3) {
											readingComplexEscape = false;
										}

										break;
									case ComplexEscape.HEX:
										escapeValue *= (char) 16;
										escapeValue += hexToDec(currentLetter);

										if (escapeLength == 2) {
											readingComplexEscape = false;
										}

										break;
									}

									if (!readingComplexEscape) {
										this.appendLetter(escapeValue);
									} else {
										this.dataBuffer.ColumnStart = this.dataBuffer.Position + 1;
									}
								} else if (currentLetter == this.userSettings.TextQualifier) {
									if (lastLetterWasEscape) {
										lastLetterWasEscape = false;
										lastLetterWasQualifier = false;
									} else {
										this.updateCurrentValue();

										if (this.userSettings.EscapeMode == ESCAPE_MODE_DOUBLED) {
											lastLetterWasEscape = true;
										}

										lastLetterWasQualifier = true;
									}
								} else if (this.userSettings.EscapeMode == ESCAPE_MODE_BACKSLASH
								        && lastLetterWasEscape) {
									switch (currentLetter) {
									case 'n':
										this.appendLetter(Letters.LF);
										break;
									case 'r':
										this.appendLetter(Letters.CR);
										break;
									case 't':
										this.appendLetter(Letters.TAB);
										break;
									case 'b':
										this.appendLetter(Letters.BACKSPACE);
										break;
									case 'f':
										this.appendLetter(Letters.FORM_FEED);
										break;
									case 'e':
										this.appendLetter(Letters.ESCAPE);
										break;
									case 'v':
										this.appendLetter(Letters.VERTICAL_TAB);
										break;
									case 'a':
										this.appendLetter(Letters.ALERT);
										break;
									case '0':
									case '1':
									case '2':
									case '3':
									case '4':
									case '5':
									case '6':
									case '7':
										escape = ComplexEscape.OCTAL;
										readingComplexEscape = true;
										escapeLength = 1;
										escapeValue = (char) (currentLetter - '0');
										this.dataBuffer.ColumnStart = this.dataBuffer.Position + 1;
										break;
									case 'u':
									case 'x':
									case 'o':
									case 'd':
									case 'U':
									case 'X':
									case 'O':
									case 'D':
										switch (currentLetter) {
										case 'u':
										case 'U':
											escape = ComplexEscape.UNICODE;
											break;
										case 'x':
										case 'X':
											escape = ComplexEscape.HEX;
											break;
										case 'o':
										case 'O':
											escape = ComplexEscape.OCTAL;
											break;
										case 'd':
										case 'D':
											escape = ComplexEscape.DECIMAL;
											break;
										}

										readingComplexEscape = true;
										escapeLength = 0;
										escapeValue = (char) 0;
										this.dataBuffer.ColumnStart = this.dataBuffer.Position + 1;

										break;
									default:
										break;
									}

									lastLetterWasEscape = false;

									// can only happen for ESCAPE_MODE_BACKSLASH
								} else if (currentLetter == escapeChar) {
									this.updateCurrentValue();
									lastLetterWasEscape = true;
								} else {
									if (lastLetterWasQualifier) {
										if (currentLetter == this.userSettings.Delimiter) {
											this.endColumn();
										} else if ((!this.useCustomRecordDelimiter && (currentLetter == Letters.CR || currentLetter == Letters.LF))
										        || (this.useCustomRecordDelimiter && currentLetter == this.userSettings.RecordDelimiter)) {
											this.endColumn();

											this.endRecord();
										} else {
											this.dataBuffer.ColumnStart = this.dataBuffer.Position + 1;

											eatingTrailingJunk = true;
										}

										// make sure to clear the flag for next
										// run of the loop

										lastLetterWasQualifier = false;
									}
								}

								// keep track of the last letter because we need
								// it for several key decisions

								this.lastLetter = currentLetter;

								if (this.startedColumn) {
									this.dataBuffer.Position++;

									if (this.userSettings.SafetySwitch
									        && this.dataBuffer.Position
									                - this.dataBuffer.ColumnStart
									                + this.columnBuffer.Position > 100000) {
										this.close();

										throw new IOException(
										        "Maximum column length of 100,000 exceeded in column "
										                + NumberFormat
										                        .getIntegerInstance()
										                        .format(
										                                this.columnsCount)
										                + " in record "
										                + NumberFormat
										                        .getIntegerInstance()
										                        .format(
										                                this.currentRecord)
										                + ". Set the SafetySwitch property to false"
										                + " if you're expecting column lengths greater than 100,000 characters to"
										                + " avoid this error.");
									}
								}
							} // end else

						} while (this.hasMoreData && this.startedColumn);
					} else if (currentLetter == this.userSettings.Delimiter) {
						// we encountered a column with no data, so
						// just send the end column

						this.lastLetter = currentLetter;

						this.endColumn();
					} else if (this.useCustomRecordDelimiter
					        && currentLetter == this.userSettings.RecordDelimiter) {
						// this will skip blank lines
						if (this.startedColumn || this.columnsCount > 0
						        || !this.userSettings.SkipEmptyRecords) {
							this.endColumn();

							this.endRecord();
						} else {
							this.dataBuffer.LineStart = this.dataBuffer.Position + 1;
						}

						this.lastLetter = currentLetter;
					} else if (!this.useCustomRecordDelimiter
					        && (currentLetter == Letters.CR || currentLetter == Letters.LF)) {
						// this will skip blank lines
						if (this.startedColumn
						        || this.columnsCount > 0
						        || (!this.userSettings.SkipEmptyRecords && (currentLetter == Letters.CR || this.lastLetter != Letters.CR))) {
							this.endColumn();

							this.endRecord();
						} else {
							this.dataBuffer.LineStart = this.dataBuffer.Position + 1;
						}

						this.lastLetter = currentLetter;
					} else if (this.userSettings.UseComments
					        && this.columnsCount == 0
					        && currentLetter == this.userSettings.Comment) {
						// encountered a comment character at the beginning of
						// the line so just ignore the rest of the line

						this.lastLetter = currentLetter;

						this.skipLine();
					} else if (this.userSettings.TrimWhitespace
					        && (currentLetter == Letters.SPACE || currentLetter == Letters.TAB)) {
						// do nothing, this will trim leading whitespace
						// for both text qualified columns and non

						this.startedColumn = true;
						this.dataBuffer.ColumnStart = this.dataBuffer.Position + 1;
					} else {
						// since the letter wasn't a special letter, this
						// will be the first letter of our current column

						this.startedColumn = true;
						this.dataBuffer.ColumnStart = this.dataBuffer.Position;
						boolean lastLetterWasBackslash = false;
						boolean readingComplexEscape = false;
						int escape = ComplexEscape.UNICODE;
						int escapeLength = 0;
						char escapeValue = (char) 0;

						boolean firstLoop = true;

						do {
							if (!firstLoop
							        && this.dataBuffer.Position == this.dataBuffer.Count) {
								this.checkDataLength();
							} else {
								if (!firstLoop) {
									// grab the current letter as a char
									currentLetter = this.dataBuffer.Buffer[this.dataBuffer.Position];
								}

								if (!this.userSettings.UseTextQualifier
								        && this.userSettings.EscapeMode == ESCAPE_MODE_BACKSLASH
								        && currentLetter == Letters.BACKSLASH) {
									if (lastLetterWasBackslash) {
										lastLetterWasBackslash = false;
									} else {
										this.updateCurrentValue();
										lastLetterWasBackslash = true;
									}
								} else if (readingComplexEscape) {
									escapeLength++;

									switch (escape) {
									case ComplexEscape.UNICODE:
										escapeValue *= (char) 16;
										escapeValue += hexToDec(currentLetter);

										if (escapeLength == 4) {
											readingComplexEscape = false;
										}

										break;
									case ComplexEscape.OCTAL:
										escapeValue *= (char) 8;
										escapeValue += (char) (currentLetter - '0');

										if (escapeLength == 3) {
											readingComplexEscape = false;
										}

										break;
									case ComplexEscape.DECIMAL:
										escapeValue *= (char) 10;
										escapeValue += (char) (currentLetter - '0');

										if (escapeLength == 3) {
											readingComplexEscape = false;
										}

										break;
									case ComplexEscape.HEX:
										escapeValue *= (char) 16;
										escapeValue += hexToDec(currentLetter);

										if (escapeLength == 2) {
											readingComplexEscape = false;
										}

										break;
									}

									if (!readingComplexEscape) {
										this.appendLetter(escapeValue);
									} else {
										this.dataBuffer.ColumnStart = this.dataBuffer.Position + 1;
									}
								} else if (this.userSettings.EscapeMode == ESCAPE_MODE_BACKSLASH
								        && lastLetterWasBackslash) {
									switch (currentLetter) {
									case 'n':
										this.appendLetter(Letters.LF);
										break;
									case 'r':
										this.appendLetter(Letters.CR);
										break;
									case 't':
										this.appendLetter(Letters.TAB);
										break;
									case 'b':
										this.appendLetter(Letters.BACKSPACE);
										break;
									case 'f':
										this.appendLetter(Letters.FORM_FEED);
										break;
									case 'e':
										this.appendLetter(Letters.ESCAPE);
										break;
									case 'v':
										this.appendLetter(Letters.VERTICAL_TAB);
										break;
									case 'a':
										this.appendLetter(Letters.ALERT);
										break;
									case '0':
									case '1':
									case '2':
									case '3':
									case '4':
									case '5':
									case '6':
									case '7':
										escape = ComplexEscape.OCTAL;
										readingComplexEscape = true;
										escapeLength = 1;
										escapeValue = (char) (currentLetter - '0');
										this.dataBuffer.ColumnStart = this.dataBuffer.Position + 1;
										break;
									case 'u':
									case 'x':
									case 'o':
									case 'd':
									case 'U':
									case 'X':
									case 'O':
									case 'D':
										switch (currentLetter) {
										case 'u':
										case 'U':
											escape = ComplexEscape.UNICODE;
											break;
										case 'x':
										case 'X':
											escape = ComplexEscape.HEX;
											break;
										case 'o':
										case 'O':
											escape = ComplexEscape.OCTAL;
											break;
										case 'd':
										case 'D':
											escape = ComplexEscape.DECIMAL;
											break;
										}

										readingComplexEscape = true;
										escapeLength = 0;
										escapeValue = (char) 0;
										this.dataBuffer.ColumnStart = this.dataBuffer.Position + 1;

										break;
									default:
										break;
									}

									lastLetterWasBackslash = false;
								} else {
									if (currentLetter == this.userSettings.Delimiter) {
										this.endColumn();
									} else if ((!this.useCustomRecordDelimiter && (currentLetter == Letters.CR || currentLetter == Letters.LF))
									        || (this.useCustomRecordDelimiter && currentLetter == this.userSettings.RecordDelimiter)) {
										this.endColumn();

										this.endRecord();
									}
								}

								// keep track of the last letter because we need
								// it for several key decisions

								this.lastLetter = currentLetter;
								firstLoop = false;

								if (this.startedColumn) {
									this.dataBuffer.Position++;

									if (this.userSettings.SafetySwitch
									        && this.dataBuffer.Position
									                - this.dataBuffer.ColumnStart
									                + this.columnBuffer.Position > 100000) {
										this.close();

										throw new IOException(
										        "Maximum column length of 100,000 exceeded in column "
										                + NumberFormat
										                        .getIntegerInstance()
										                        .format(
										                                this.columnsCount)
										                + " in record "
										                + NumberFormat
										                        .getIntegerInstance()
										                        .format(
										                                this.currentRecord)
										                + ". Set the SafetySwitch property to false"
										                + " if you're expecting column lengths greater than 100,000 characters to"
										                + " avoid this error.");
									}
								}
							} // end else
						} while (this.hasMoreData && this.startedColumn);
					}

					if (this.hasMoreData) {
						this.dataBuffer.Position++;
					}
				} // end else
			} while (this.hasMoreData && !this.hasReadNextLine);

			// check to see if we hit the end of the file
			// without processing the current record

			if (this.startedColumn
			        || this.lastLetter == this.userSettings.Delimiter) {
				this.endColumn();

				this.endRecord();
			}
		}

		if (this.userSettings.CaptureRawRecord) {
			if (this.hasMoreData) {
				if (this.rawBuffer.Position == 0) {
					this.rawRecord = new String(this.dataBuffer.Buffer,
					        this.dataBuffer.LineStart, this.dataBuffer.Position
					                - this.dataBuffer.LineStart - 1);
				} else {
					this.rawRecord = new String(this.rawBuffer.Buffer, 0,
					        this.rawBuffer.Position)
					        + new String(this.dataBuffer.Buffer,
					                this.dataBuffer.LineStart,
					                this.dataBuffer.Position
					                        - this.dataBuffer.LineStart - 1);
				}
			} else {
				// for hasMoreData to ever be false, all data would have had to
				// have been
				// copied to the raw buffer
				this.rawRecord = new String(this.rawBuffer.Buffer, 0,
				        this.rawBuffer.Position);
			}
		} else {
			this.rawRecord = "";
		}

		return this.hasReadNextLine;
	}

	/**
	 * @exception IOException
	 *                Thrown if an error occurs while reading data from the
	 *                source stream.
	 */
	private void checkDataLength() throws IOException {
		if (!this.initialized) {
			if (this.fileName != null) {
				this.inputStream = new BufferedReader(new InputStreamReader(
				        new FileInputStream(this.fileName), this.charset),
				        StaticSettings.MAX_FILE_BUFFER_SIZE);
			}

			this.charset = null;
			this.initialized = true;
		}

		this.updateCurrentValue();

		if (this.userSettings.CaptureRawRecord && this.dataBuffer.Count > 0) {
			if (this.rawBuffer.Buffer.length - this.rawBuffer.Position < this.dataBuffer.Count
			        - this.dataBuffer.LineStart) {
				int newLength = this.rawBuffer.Buffer.length
				        + Math.max(this.dataBuffer.Count
				                - this.dataBuffer.LineStart,
				                this.rawBuffer.Buffer.length);

				char[] holder = new char[newLength];

				System.arraycopy(this.rawBuffer.Buffer, 0, holder, 0,
				        this.rawBuffer.Position);

				this.rawBuffer.Buffer = holder;
			}

			System.arraycopy(this.dataBuffer.Buffer, this.dataBuffer.LineStart,
			        this.rawBuffer.Buffer, this.rawBuffer.Position,
			        this.dataBuffer.Count - this.dataBuffer.LineStart);

			this.rawBuffer.Position += this.dataBuffer.Count
			        - this.dataBuffer.LineStart;
		}

		try {
			this.dataBuffer.Count = this.inputStream.read(
			        this.dataBuffer.Buffer, 0, this.dataBuffer.Buffer.length);
		} catch (IOException ex) {
			this.close();

			throw ex;
		}

		// if no more data could be found, set flag stating that
		// the end of the data was found

		if (this.dataBuffer.Count == -1) {
			this.hasMoreData = false;
		}

		this.dataBuffer.Position = 0;
		this.dataBuffer.LineStart = 0;
		this.dataBuffer.ColumnStart = 0;
	}

	/**
	 * Read the first record of data as column headers.
	 * 
	 * @return Whether the header record was successfully read or not.
	 * @exception IOException
	 *                Thrown if an error occurs while reading data from the
	 *                source stream.
	 */
	public boolean readHeaders() throws IOException {
		boolean result = this.readRecord();

		// copy the header data from the column array
		// to the header string array

		this.headersHolder.Length = this.columnsCount;

		this.headersHolder.Headers = new String[this.columnsCount];

		for (int i = 0; i < this.headersHolder.Length; i++) {
			String columnValue = this.get(i);

			this.headersHolder.Headers[i] = columnValue;

			// if there are duplicate header names, we will save the last one
			this.headersHolder.IndexByName.put(columnValue, new Integer(i));
		}

		if (result) {
			this.currentRecord--;
		}

		this.columnsCount = 0;

		return result;
	}

	/**
	 * Returns the column header value for a given column index.
	 * 
	 * @param columnIndex
	 *            The index of the header column being requested.
	 * @return The value of the column header at the given column index.
	 * @exception IOException
	 *                Thrown if this object has already been closed.
	 */
	public String getHeader(int columnIndex) throws IOException {
		this.checkClosed();

		// check to see if we have read the header record yet

		// check to see if the column index is within the bounds
		// of our header array

		if (columnIndex > -1 && columnIndex < this.headersHolder.Length) {
			// return the processed header data for this column

			return this.headersHolder.Headers[columnIndex];
		} else {
			return "";
		}
	}

	public boolean isQualified(int columnIndex) throws IOException {
		this.checkClosed();

		if (columnIndex < this.columnsCount && columnIndex > -1) {
			return this.isQualified[columnIndex];
		} else {
			return false;
		}
	}

	/**
	 * @exception IOException
	 *                Thrown if a very rare extreme exception occurs during
	 *                parsing, normally resulting from improper data format.
	 */
	private void endColumn() throws IOException {
		String currentValue = "";

		// must be called before setting startedColumn = false
		if (this.startedColumn) {
			if (this.columnBuffer.Position == 0) {
				if (this.dataBuffer.ColumnStart < this.dataBuffer.Position) {
					int lastLetter = this.dataBuffer.Position - 1;

					if (this.userSettings.TrimWhitespace
					        && !this.startedWithQualifier) {
						while (lastLetter >= this.dataBuffer.ColumnStart
						        && (this.dataBuffer.Buffer[lastLetter] == Letters.SPACE || this.dataBuffer.Buffer[lastLetter] == Letters.TAB)) {
							lastLetter--;
						}
					}

					currentValue = new String(this.dataBuffer.Buffer,
					        this.dataBuffer.ColumnStart, lastLetter
					                - this.dataBuffer.ColumnStart + 1);
				}
			} else {
				this.updateCurrentValue();

				int lastLetter = this.columnBuffer.Position - 1;

				if (this.userSettings.TrimWhitespace
				        && !this.startedWithQualifier) {
					while (lastLetter >= 0
					        && (this.columnBuffer.Buffer[lastLetter] == Letters.SPACE || this.columnBuffer.Buffer[lastLetter] == Letters.SPACE)) {
						lastLetter--;
					}
				}

				currentValue = new String(this.columnBuffer.Buffer, 0,
				        lastLetter + 1);
			}
		}

		this.columnBuffer.Position = 0;

		this.startedColumn = false;

		if (this.columnsCount >= 100000 && this.userSettings.SafetySwitch) {
			this.close();

			throw new IOException(
			        "Maximum column count of 100,000 exceeded in record "
			                + NumberFormat.getIntegerInstance().format(
			                        this.currentRecord)
			                + ". Set the SafetySwitch property to false"
			                + " if you're expecting more than 100,000 columns per record to"
			                + " avoid this error.");
		}

		// check to see if our current holder array for
		// column chunks is still big enough to handle another
		// column chunk

		if (this.columnsCount == this.values.length) {
			// holder array needs to grow to be able to hold another column
			int newLength = this.values.length * 2;

			String[] holder = new String[newLength];

			System.arraycopy(this.values, 0, holder, 0, this.values.length);

			this.values = holder;

			boolean[] qualifiedHolder = new boolean[newLength];

			System.arraycopy(this.isQualified, 0, qualifiedHolder, 0,
			        this.isQualified.length);

			this.isQualified = qualifiedHolder;
		}

		this.values[this.columnsCount] = currentValue;

		this.isQualified[this.columnsCount] = this.startedWithQualifier;

		currentValue = "";

		this.columnsCount++;
	}

	private void appendLetter(char letter) {
		if (this.columnBuffer.Position == this.columnBuffer.Buffer.length) {
			int newLength = this.columnBuffer.Buffer.length * 2;

			char[] holder = new char[newLength];

			System.arraycopy(this.columnBuffer.Buffer, 0, holder, 0,
			        this.columnBuffer.Position);

			this.columnBuffer.Buffer = holder;
		}
		this.columnBuffer.Buffer[this.columnBuffer.Position++] = letter;
		this.dataBuffer.ColumnStart = this.dataBuffer.Position + 1;
	}

	private void updateCurrentValue() {
		if (this.startedColumn
		        && this.dataBuffer.ColumnStart < this.dataBuffer.Position) {
			if (this.columnBuffer.Buffer.length - this.columnBuffer.Position < this.dataBuffer.Position
			        - this.dataBuffer.ColumnStart) {
				int newLength = this.columnBuffer.Buffer.length
				        + Math.max(this.dataBuffer.Position
				                - this.dataBuffer.ColumnStart,
				                this.columnBuffer.Buffer.length);

				char[] holder = new char[newLength];

				System.arraycopy(this.columnBuffer.Buffer, 0, holder, 0,
				        this.columnBuffer.Position);

				this.columnBuffer.Buffer = holder;
			}

			System.arraycopy(this.dataBuffer.Buffer,
			        this.dataBuffer.ColumnStart, this.columnBuffer.Buffer,
			        this.columnBuffer.Position, this.dataBuffer.Position
			                - this.dataBuffer.ColumnStart);

			this.columnBuffer.Position += this.dataBuffer.Position
			        - this.dataBuffer.ColumnStart;
		}

		this.dataBuffer.ColumnStart = this.dataBuffer.Position + 1;
	}

	/**
	 * @exception IOException
	 *                Thrown if an error occurs while reading data from the
	 *                source stream.
	 */
	private void endRecord() throws IOException {
		// this flag is used as a loop exit condition
		// during parsing

		this.hasReadNextLine = true;

		this.currentRecord++;
	}

	/**
	 * Gets the corresponding column index for a given column header name.
	 * 
	 * @param headerName
	 *            The header name of the column.
	 * @return The column index for the given column header name.&nbsp;Returns
	 *         -1 if not found.
	 * @exception IOException
	 *                Thrown if this object has already been closed.
	 */
	public int getIndex(String headerName) throws IOException {
		this.checkClosed();

		Object indexValue = this.headersHolder.IndexByName.get(headerName);

		if (indexValue != null) {
			return ((Integer) indexValue).intValue();
		} else {
			return -1;
		}
	}

	/**
	 * Skips the next record of data by parsing each column.&nbsp;Does not
	 * increment {@link com.csvreader.CsvReader#getCurrentRecord
	 * getCurrentRecord()}.
	 * 
	 * @return Whether another record was successfully skipped or not.
	 * @exception IOException
	 *                Thrown if an error occurs while reading data from the
	 *                source stream.
	 */
	public boolean skipRecord() throws IOException {
		this.checkClosed();

		boolean recordRead = false;

		if (this.hasMoreData) {
			recordRead = this.readRecord();

			if (recordRead) {
				this.currentRecord--;
			}
		}

		return recordRead;
	}

	/**
	 * Skips the next line of data using the standard end of line characters and
	 * does not do any column delimited parsing.
	 * 
	 * @return Whether a line was successfully skipped or not.
	 * @exception IOException
	 *                Thrown if an error occurs while reading data from the
	 *                source stream.
	 */
	public boolean skipLine() throws IOException {
		this.checkClosed();

		// clear public column values for current line

		this.columnsCount = 0;

		boolean skippedLine = false;

		if (this.hasMoreData) {
			boolean foundEol = false;

			do {
				if (this.dataBuffer.Position == this.dataBuffer.Count) {
					this.checkDataLength();
				} else {
					skippedLine = true;

					// grab the current letter as a char

					char currentLetter = this.dataBuffer.Buffer[this.dataBuffer.Position];

					if (currentLetter == Letters.CR
					        || currentLetter == Letters.LF) {
						foundEol = true;
					}

					// keep track of the last letter because we need
					// it for several key decisions

					this.lastLetter = currentLetter;

					if (!foundEol) {
						this.dataBuffer.Position++;
					}

				} // end else
			} while (this.hasMoreData && !foundEol);

			this.columnBuffer.Position = 0;

			this.dataBuffer.LineStart = this.dataBuffer.Position + 1;
		}

		this.rawBuffer.Position = 0;
		this.rawRecord = "";

		return skippedLine;
	}

	/**
	 * Closes and releases all related resources.
	 */
	public void close() {
		if (!this.closed) {
			this.close(true);

			this.closed = true;
		}
	}

	/**
	 * 
	 */
	private void close(boolean closing) {
		if (!this.closed) {
			if (closing) {
				this.charset = null;
				this.headersHolder.Headers = null;
				this.headersHolder.IndexByName = null;
				this.dataBuffer.Buffer = null;
				this.columnBuffer.Buffer = null;
				this.rawBuffer.Buffer = null;
			}

			try {
				if (this.initialized) {
					this.inputStream.close();
				}
			} catch (Exception e) {
				// just eat the exception
			}

			this.inputStream = null;

			this.closed = true;
		}
	}

	/**
	 * @exception IOException
	 *                Thrown if this object has already been closed.
	 */
	private void checkClosed() throws IOException {
		if (this.closed) {
			throw new IOException(
			        "This instance of the CsvReader class has already been closed.");
		}
	}

	/**
	 * 
	 */
	@Override
	protected void finalize() {
		this.close(false);
	}

	private class ComplexEscape {
		private static final int UNICODE = 1;

		private static final int OCTAL = 2;

		private static final int DECIMAL = 3;

		private static final int HEX = 4;
	}

	private static char hexToDec(char hex) {
		char result;

		if (hex >= 'a') {
			result = (char) (hex - 'a' + 10);
		} else if (hex >= 'A') {
			result = (char) (hex - 'A' + 10);
		} else {
			result = (char) (hex - '0');
		}

		return result;
	}

	private class DataBuffer {
		public char[] Buffer;

		public int Position;

		// / <summary>
		// / How much usable data has been read into the stream,
		// / which will not always be as long as Buffer.Length.
		// / </summary>
		public int Count;

		// / <summary>
		// / The position of the cursor in the buffer when the
		// / current column was started or the last time data
		// / was moved out to the column buffer.
		// / </summary>
		public int ColumnStart;

		public int LineStart;

		public DataBuffer() {
			this.Buffer = new char[StaticSettings.MAX_BUFFER_SIZE];
			this.Position = 0;
			this.Count = 0;
			this.ColumnStart = 0;
			this.LineStart = 0;
		}
	}

	private class ColumnBuffer {
		public char[] Buffer;

		public int Position;

		public ColumnBuffer() {
			this.Buffer = new char[StaticSettings.INITIAL_COLUMN_BUFFER_SIZE];
			this.Position = 0;
		}
	}

	private class RawRecordBuffer {
		public char[] Buffer;

		public int Position;

		public RawRecordBuffer() {
			this.Buffer = new char[StaticSettings.INITIAL_COLUMN_BUFFER_SIZE
			        * StaticSettings.INITIAL_COLUMN_COUNT];
			this.Position = 0;
		}
	}

	private class Letters {
		public static final char LF = '\n';

		public static final char CR = '\r';

		public static final char QUOTE = '"';

		public static final char COMMA = ',';

		public static final char SPACE = ' ';

		public static final char TAB = '\t';

		public static final char POUND = '#';

		public static final char BACKSLASH = '\\';

		public static final char NULL = '\0';

		public static final char BACKSPACE = '\b';

		public static final char FORM_FEED = '\f';

		public static final char ESCAPE = '\u001B'; // ASCII/ANSI escape

		public static final char VERTICAL_TAB = '\u000B';

		public static final char ALERT = '\u0007';
	}

	private class UserSettings {
		// having these as publicly accessible members will prevent
		// the overhead of the method call that exists on properties
		// public boolean CaseSensitive;

		public char TextQualifier;

		public boolean TrimWhitespace;

		public boolean UseTextQualifier;

		public char Delimiter;

		public char RecordDelimiter;

		public char Comment;

		public boolean UseComments;

		public int EscapeMode;

		public boolean SafetySwitch;

		public boolean SkipEmptyRecords;

		public boolean CaptureRawRecord;

		public UserSettings() {
			// this.CaseSensitive = true;
			this.TextQualifier = Letters.QUOTE;
			this.TrimWhitespace = true;
			this.UseTextQualifier = true;
			this.Delimiter = Letters.COMMA;
			this.RecordDelimiter = Letters.NULL;
			this.Comment = Letters.POUND;
			this.UseComments = false;
			this.EscapeMode = CsvReader.ESCAPE_MODE_DOUBLED;
			this.SafetySwitch = true;
			this.SkipEmptyRecords = true;
			this.CaptureRawRecord = true;
		}
	}

	private class HeadersHolder {
		public String[] Headers;

		public int Length;

		public HashMap<String, Integer> IndexByName;

		public HeadersHolder() {
			this.Headers = null;
			this.Length = 0;
			this.IndexByName = new HashMap<String, Integer>();
		}
	}

	private class StaticSettings {
		// these are static instead of final so they can be changed in unit test
		// isn't visible outside this class and is only accessed once during
		// CsvReader construction
		public static final int MAX_BUFFER_SIZE = 1024;

		public static final int MAX_FILE_BUFFER_SIZE = 4 * 1024;

		public static final int INITIAL_COLUMN_COUNT = 10;

		public static final int INITIAL_COLUMN_BUFFER_SIZE = 50;
	}
}
