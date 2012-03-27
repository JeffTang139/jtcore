package org.eclipse.jt.core.misc;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Random;

import org.eclipse.jt.core.type.Convert;


public final class Obfuscater {
	public static String obfuscate(String password) {
		return obfuscate(password, true);
	}

	public static String obfuscate(String password, boolean withMark) {
		if (password == null) {
			password = "";
		} else {
			password = unobfuscate(password, !withMark);
		}
		final ByteBuffer bb = Convert.utf8.encode(CharBuffer.wrap(password));
		final int pwl = bb.remaining();
		final byte[] buf = new byte[pwl + 2];
		bb.get(buf, 0, pwl);
		code(buf, buf[pwl] = buf[pwl + 1] = (byte) random.nextInt());
		StringBuilder marked = new StringBuilder(4 * ((2 + 2 + pwl) / 3)
		        + (withMark ? mark.length() : 0));
		if (withMark) {
			marked.append(mark);
		}
		Convert.bytesToBase64(buf, marked);
		return marked.toString();
	}

	public static String unobfuscate(String obfuscated) {
		return unobfuscate(obfuscated, false);
	}

	public static String unobfuscate(String obfuscated, boolean tryNoMark) {
		if (obfuscated == null || obfuscated.length() == 0) {
			return obfuscated;
		}
		if (obfuscated.startsWith(mark)) {
			obfuscated = obfuscated.substring(mark.length());
		} else if (!tryNoMark) {
			return obfuscated;
		}
		try {
			byte[] buf = Convert.base64ToBytes(obfuscated);
			final int bl = buf.length;
			if (bl >= 2) {
				final byte seed = buf[bl - 1];
				code(buf, seed);
				if (buf[bl - 2] == seed) {
					return Convert.utf8.decode(ByteBuffer.wrap(buf, 0, bl - 2))
					        .toString();
				}
			}
		} catch (Throwable e) {
		}
		return obfuscated;
	}

	private Obfuscater() {

	}

	private final static String mark = "OBF:";
	private final static Random random = new Random();

	private static void code(byte[] buf, byte seed) {
		int s = seed;
		for (int i = 0, c = buf.length - 1; i < c; i++) {
			s = ~(s * 31 + 31415);
			buf[i] ^= s;
		}
	}
}
