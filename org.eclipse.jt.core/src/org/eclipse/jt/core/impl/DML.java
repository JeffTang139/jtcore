
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.query.DMLDeclare;

// Referenced classes of package org.eclipse.jt.core.impl:
//            RelationRefOwner, SubQueryImpl, DerivedQueryImpl

interface DML
    extends DMLDeclare, RelationRefOwner
{

    public abstract SubQueryImpl newSubQuery();

    public abstract DerivedQueryImpl newDerivedQuery();
}
