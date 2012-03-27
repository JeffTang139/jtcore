
package org.eclipse.jt.dbdrv.sqlserver.jdbc3;

import com.microsoft.sqlserver.jdbc.SQLServerDriver;
import java.sql.Driver;

public class SQLServerDriverJDBC3
{

    private SQLServerDriverJDBC3()
    {
    }

    public static final Driver driver;

    static 
    {
        String jv = System.getProperty("java.version");
        if(jv.startsWith("1.5"))
            driver = new SQLServerDriver();
        else
            driver = null;
    }
}
