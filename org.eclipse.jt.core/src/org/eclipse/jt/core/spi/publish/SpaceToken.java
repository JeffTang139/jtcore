package org.eclipse.jt.core.spi.publish;

/**
 * �ռ��ʶ
 * 
 * @author Jeff Tang
 * 
 */
public interface SpaceToken {
	public String getName();

	public SpaceToken getParent();

	public SpaceToken getSibling();

	public SpaceToken getFirstChild();

	public SpaceToken getSite();
}
