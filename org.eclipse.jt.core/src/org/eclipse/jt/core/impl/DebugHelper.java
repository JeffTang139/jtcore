package org.eclipse.jt.core.impl;

import java.io.PrintStream;
import java.util.Date;

final class DebugHelper {
	private static final String CLASS_NAME = DebugHelper.class.getName();

	private static final int DEBUG_LEVEL = Integer.getInteger(
			"org.eclipse.jt.debug.net.channel", 0);

	private static final int DEBUG_BUFF_SIZE = Integer.getInteger(
			"org.eclipse.jt.debug.net.channel.buff_size", 256);

	static final void trace(String msg, PrintStream out, int stackDepth) {
		out.printf("[%1$tT.%1$tL] %2$s %3$s\n", new Date(), Thread
				.currentThread(), msg);
		if (stackDepth > 0) {
			StackTraceElement[] trace = Thread.currentThread().getStackTrace();
			int i = 1;
			int c = trace.length;
			for (; i < c; i++) {
				if (!trace[i].getClassName().equals(CLASS_NAME)) {
					break;
				}
			}
			if (c > i + stackDepth) {
				c = i + stackDepth;
			}
			for (; i < c; i++) {
				System.err.println("\t" + trace[i]);
			}
		}
	}

	static final void trace(String msg, int depth) {
		if (DEBUG_LEVEL > 1) {
			trace(msg, System.out, depth);
		}
	}

	static final void trace(String msg) {
		trace(msg, 0);
	}

	static final void strike(String msg, int depth) {
		if (DEBUG_LEVEL > 0) {
			trace(msg, System.err, depth);
		}
	}

	static final void strike(String msg) {
		strike(msg, 0);
	}

	static final void fault(String msg) {
		if (DEBUG_LEVEL > 0) {
			strike(msg);
		}
	}

	static final void trace(String msg, byte[] buff, int offset, int size) {
		if (DEBUG_LEVEL > 2) {
			if (buff != null) {
				StringBuilder sb = new StringBuilder();
				int pos = offset + size;
				int c = pos;
				if (DEBUG_BUFF_SIZE > 0 && DEBUG_BUFF_SIZE < c) {
					c = DEBUG_BUFF_SIZE;
				}
				for (int i = offset; i < c; i++) {
					int l = buff[i] & 0x0f;
					if (l > 9) {
						l = l - 10 + 'A';
					} else {
						l += '0';
					}
					int h = ((buff[i]) >> 4) & 0x0f;
					if (h > 9) {
						h = h - 10 + 'A';
					} else {
						h += '0';
					}
					sb.append((char) h).append((char) l);
				}
				if (c < pos) {
					trace(msg + sb.toString() + " ...(" + (pos - c) + " more)");
				} else {
					trace(msg + sb.toString());
				}
			} else {
				trace(msg);
			}
		}
	}

	static final void trace(String msg, DataFragment fragment) {
		if (DEBUG_LEVEL > 1) {
			PrintStream out = System.out;
			int pos = fragment.getPosition();
			fragment.setPosition(fragment.getAvailableOffset() + 4);
			byte ctrlFlag = fragment.readByte();
			switch (ctrlFlag & NetChannelImpl.CTRL_FLAG_TYPE_MASK) {
			case NetChannelImpl.CTRL_FLAG_PACKAGE:
				msg += "package " + fragment.readInt();
				break;
			case NetChannelImpl.CTRL_FLAG_BREAK_PACKAGE_RECEIVE:
				msg += "break receive " + fragment.readInt();
				out = System.err;
				break;
			case NetChannelImpl.CTRL_FLAG_BREAK_PACKAGE_SEND:
				msg += "break send " + fragment.readInt() + " [ack ";
				msg += fragment.readInt() + "]";
				out = System.err;
				break;
			case NetChannelImpl.CTRL_FLAG_PACKAGE_RESOLVED:
				msg += "resolved " + fragment.readInt() + " [ack ";
				msg += fragment.readInt() + "]";
				break;
			case NetChannelImpl.CTRL_FLAG_RESEND_PACKAGE:
				msg += "resend [generation " + fragment.readInt()
						+ "] package ";
				while (fragment.remain() > 4) {
					msg += fragment.readInt() + " ";
				}
				msg += "[ack " + fragment.readInt() + "]";
				break;
			case NetChannelImpl.CTRL_FLAG_CLOSE:
				msg += "close";
				if ((ctrlFlag & NetChannelImpl.CTRL_FLAG_SUBTYPE_MASK) != 0) {
					msg += " cancel";
				}
				break;
			case NetChannelImpl.CTRL_FLAG_KEEP_ALIVE:
				msg += "keep-alive";
				break;
			case NetChannelImpl.CTRL_FLAG_ECHO:
				msg += "echo [ack " + fragment.readInt() + "]";
				break;
			case NetChannelImpl.CTRL_FLAG_ACK:
				msg += "ack " + fragment.readInt();
				break;
			default:
				throw new IllegalStateException("无法识别的数据类型");
			}
			fragment.setPosition(pos);
			trace(msg, out, 0);
		}
	}
}
