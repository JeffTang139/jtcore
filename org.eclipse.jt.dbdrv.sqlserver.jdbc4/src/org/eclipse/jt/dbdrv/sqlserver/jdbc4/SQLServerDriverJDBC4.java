
package org.eclipse.jt.dbdrv.sqlserver.jdbc4;

import com.microsoft.sqlserver.jdbc.SQLServerDriver;
import java.sql.Driver;

public class SQLServerDriverJDBC4
{

    private SQLServerDriverJDBC4()
    {
    }

    public static final Driver driver;

    static 
    {
        String jv = System.getProperty("java.version");
        if(jv.startsWith("1.6"))
            driver = new SQLServerDriver();
        else
            driver = null;
    }
}
