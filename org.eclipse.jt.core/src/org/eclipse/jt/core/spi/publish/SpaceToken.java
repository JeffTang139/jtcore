package org.eclipse.jt.core.spi.publish;

/**
 * ø’º‰±Í ∂
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
