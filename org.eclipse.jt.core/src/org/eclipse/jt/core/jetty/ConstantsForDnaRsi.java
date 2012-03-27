/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File DnaRsiHeaderFields.java
 * Date 2009-10-21
 */
package org.eclipse.jt.core.jetty;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public class ConstantsForDnaRsi {
    private ConstantsForDnaRsi() {
    }

    public static final String CHAR_SET = "UTF-8";

    static final String CRLF = "\r\n";
    static final String NAME_VALUE_SEPARATOR = ": ";

    // HFN: Header Field Name
    // HFV: Header Field Value

    public static final String HFN_USER_AGENT = "User-Agent";
    public static final String HFV_USER_AGENT = "DNA-CORE.RSI";

    // for HTTP/1.1
    public static final String HFN_HOST = "HOST";
    public static final String HFV_HOST = "rsi.jt.org.cn";

    // D&A-Core RSI response signal
    public static final String RSI_RESPONSE_SIGNAL = "D&A-Core RSI response OK";

    // -------------------------------------------------------------------------

    private static volatile byte[] http_request_content;

    public static ByteBuffer getDnaCoreRsiHttpRequest() {
        byte[] content = http_request_content;
        if (content == null) {
            content = buildHttpRequest10Content();
        }
        return ByteBuffer.wrap(content.clone());
    }

    /**
     * HTTP/1.0 Request Header:
     * 
     * <pre>
     *      HEAD * HTTP/1.0\r\n                                              17
     *      User-Agent: DNA-CORE.RSI\r\n                                     26
     *      \r\n                                                              2
     * </pre>
     */
    private static byte[] buildHttpRequest10Content() {
        StringBuilder builder = new StringBuilder(45);
        builder.append("HEAD * HTTP/1.0\r\n");

        builder.append(HFN_USER_AGENT);
        builder.append(NAME_VALUE_SEPARATOR);
        builder.append(HFV_USER_AGENT);
        builder.append(CRLF);

        builder.append(CRLF);

        String msg = builder.toString();

        try {
            http_request_content = msg.getBytes(CHAR_SET);
        } catch (UnsupportedEncodingException ignore) {
            http_request_content = ascii2bytes(msg);
        }

        return http_request_content;
    }

    /**
     * HTTP/1.1 Request Header:
     * 
     * <pre>
     *      HEAD * HTTP/1.1\r\n                                              17
     *      HOST: rsi.jt.org.cn\r\n                                       24
     *      User-Agent: DNA-CORE.RSI\r\n                                     26
     *      \r\n                                                              2
     * </pre>
     */
    @SuppressWarnings("unused")
    private static byte[] buildHttpRequest11Content() {
        StringBuilder builder = new StringBuilder(69);
        builder.append("HEAD * HTTP/1.1\r\n");

        // Required by HTTP/1.1
        builder.append(HFN_HOST);
        builder.append(NAME_VALUE_SEPARATOR);
        builder.append(HFV_HOST);
        builder.append(CRLF);

        builder.append(HFN_USER_AGENT);
        builder.append(NAME_VALUE_SEPARATOR);
        builder.append(HFV_USER_AGENT);
        builder.append(CRLF);

        builder.append(CRLF);

        String msg = builder.toString();

        try {
            http_request_content = msg.getBytes(CHAR_SET);
        } catch (UnsupportedEncodingException ignore) {
            http_request_content = ascii2bytes(msg);
        }

        return http_request_content;
    }

    /**
     * Each of characters in the <code>ascii</code> must be ASCII character.
     */
    public static byte[] ascii2bytes(String ascii) {
        if (ascii == null) {
            return null;
        }
        final int len = ascii.length();
        byte[] bytes = new byte[len];
        for (int i = 0; i < len; i++) {
            bytes[i] = (byte) ascii.charAt(i);
        }
        return bytes;
    }
}
