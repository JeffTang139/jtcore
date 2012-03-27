package org.eclipse.jt.dbdrv.mysql;

import com.mysql.jdbc.NonRegisteringDriver;
import java.sql.Driver;

public final class MySQLDriver5
{

    private MySQLDriver5()
    {
    }

    public static final Driver driver;

    static 
    {
        Driver d;
        try
        {
            d = new NonRegisteringDriver();
        }
        catch(Throwable e)
        {
            d = null;
            e.printStackTrace();
        }
        driver = d;
    }
}
