package org.eclipse.jt.core.auth;

import org.eclipse.jt.core.User;

public interface UserAuthorityChecker extends ActorAuthorityChecker {

	public User getUser();

}
