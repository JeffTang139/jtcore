package org.eclipse.jt.core.impl;

interface OMVisitable {

	<TContext> void visit(OMVisitor<TContext> visitor, TContext context);

}
