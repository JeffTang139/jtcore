package org.eclipse.jt.dbdrv.db2;


import com.ibm.db2.jcc.DB2Driver;
import java.sql.Driver;

public final class DB2Driver9
{

    private DB2Driver9()
    {
    }

    public static final Driver driver = new DB2Driver();

}