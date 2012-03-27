package org.eclipse.jt.core.impl;

import java.util.ArrayList;

import org.eclipse.jt.core.ObjectQuerier;
import org.eclipse.jt.core.serial.DataObjectTranslator;


@SuppressWarnings("unchecked")
public class DOT_TreeNodeImpl implements
		DataObjectTranslator<TreeNodeImpl, Object[]> {
	final static short VERSION = 0x0100;

	private DOT_TreeNodeImpl() {

	}

	public final boolean supportAssign() {
		return false;
	}

	public final Object[] toDelegateObject(TreeNodeImpl root) {
		final ArrayList<Object> list = new ArrayList<Object>();
		if (root instanceof TreeNodeRoot) {
			list.add(Mark.INDENT);
			list.add(root.getElement());
		}
		dfs(root, list);
		return list.toArray();
	}

	/**
	 * 缩进标记
	 * 
	 * @author Jeff Tang
	 * 
	 */
	private enum Mark {
		/**
		 * 层次加深
		 */
		INDENT,
		/**
		 * 层次减少
		 */
		UNINDENT,
	}

	private static void dfs(TreeNodeImpl root, ArrayList<Object> list) {
		if (root instanceof TreeNodeRoot) {
			int count = root.getChildCount();
			if (count > 0) {
				list.add(Mark.INDENT);
				for (int i = 0; i < count; i++) {
					dfs(root.getChild(i), list);
				}
				list.add(Mark.UNINDENT);
			}
		} else {
			list.add(root.getElement());
			int count = root.getChildCount();
			if (count > 0) {
				list.add(Mark.INDENT);
				for (int i = 0; i < count; i++) {
					dfs(root.getChild(i), list);
				}
				list.add(Mark.UNINDENT);
			}
		}
	}

	public short getVersion() {
		return VERSION;
	}

	public TreeNodeImpl recoverObject(TreeNodeImpl destHint, Object[] objects,
			ObjectQuerier querier, short serialVersion) {
		TreeNodeImpl source = recoverTreeInObjects(objects);
		if (destHint == null) {
			destHint = source;
		} else {
			copyTree(destHint, source);
		}
		return destHint;
	}

	public static void copyTree(TreeNodeImpl dest, TreeNodeImpl source) {
		if (!dest.getElement().equals(source.getElement())) {
			dest.setElement(source.getElement());
			if (source.getChildCount() < dest.getChildCount()) {
				for (int i = dest.getChildCount() - 1; i > source
						.getChildCount() - 1; i--) {
					dest.remove(i);
				}
			} else if (source.getChildCount() > dest.getChildCount()) {
				for (int i = dest.getChildCount(); i < source.getChildCount(); i++) {
					dest.append(source.getChild(i).getElement());
				}
			} else {
				if (source.getChildCount() > 0 && dest.getChildCount() == 0) {
					for (int i = 0; i < source.getChildCount(); i++) {
						dest.append(source.getChild(i).getElement());
					}
				} else if (dest.getChildCount() > 0
						&& source.getChildCount() == 0) {
					dest.clear();
				} else if (source.getChildCount() > 0
						&& dest.getChildCount() > 0) {
					for (int i = 0; i < source.getChildCount(); i++) {
						copyTree(dest.getChild(i), source.getChild(i));
					}
				}
			}
		}
		for (int i = 0; i < source.getChildCount(); i++) {
			copyTree(dest.getChild(i), source.getChild(i));
		}
	}

	public static TreeNodeImpl recoverTreeInObjects(Object[] objects) {

		int i = 0;
		if (objects[i] == Mark.INDENT) {
			TreeNodeImpl root = new TreeNodeRoot(objects[++i], 0);
			for (; i < objects.length - 1; i++) {
				if (objects[i] == Mark.INDENT) {
					root = root.getChild(root.getChildCount() - 1);
				} else if (objects[i] == Mark.UNINDENT) {
					root = root.getParent();
				} else {
					root.append(objects[i]);
				}
			}
			return root;
		} else {
			TreeNodeImpl root = new TreeNodeImpl(null, null);
			for (; i < objects.length; i++) {
				if (objects[i] == Mark.INDENT) {
					root = root.getChild(root.getChildCount() - 1);
				} else if (objects[i] == Mark.UNINDENT) {
					root = root.getParent();
				} else {
					root.append(objects[i]);
				}
			}
			return root;
		}
	}

	public short supportedVerionMin() {
		return VERSION;
	}

}
