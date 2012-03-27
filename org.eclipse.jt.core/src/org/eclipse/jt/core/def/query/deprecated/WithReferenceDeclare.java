package org.eclipse.jt.core.def.query.deprecated;

import org.eclipse.jt.core.def.query.RelationRefDeclare;

public interface WithReferenceDeclare extends WithReferenceDefine,
		RelationRefDeclare {

	public WithDeclare getTarget();
}
