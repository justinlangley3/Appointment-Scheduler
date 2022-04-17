/*
 * Created By: 	Justin A. Langley
 * Course: 	C195 - Software II 
 * Language: 	Java
 *
 * @(#)Database.java   2021.05.19 at 06:10:46 EDT
 * Version: 1.0.0
 */



package Util.DB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

//~--- classes ----------------------------------------------------------------

public final class Database
{
    private static final String     URL      = "db.properties";
    private static final Database   INSTANCE = new Database();
    private static final DataSource ds       = DataSourceFactory.getMysqlDataSource(URL);
    private static Connection       conn;

    //~--- constructors -------------------------------------------------------

    private Database() {}

    //~--- methods ------------------------------------------------------------

    public void closeConnection(Connection conn)
    {
        try {
            if ((conn != null) && (!conn.isClosed()))
            {
                conn.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName())
                  .log(Level.SEVERE, null, ex);
        }
    }

    public void closeResultSet(ResultSet rs)
    {
        try {
            if ((rs != null) && (!rs.isClosed()))
            {
                rs.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName())
                  .log(Level.SEVERE, null, ex);
        }
    }

    public void closeStatement(CallableStatement cs)
    {
        try {
            if ((cs != null) && (!cs.isClosed()))
            {
                cs.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName())
                  .log(Level.SEVERE, null, ex);
        }
    }

    //~--- get methods --------------------------------------------------------

    public Connection getConnection()
    {
        try {
            if (this.conn != null)
            {
                return this.conn;
            }
            else
            {
                conn = ds.getConnection();

                return conn;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName())
                  .log(Level.SEVERE, null, ex);

            return null;
        }
    }

    public static Database getInstance()
    {
        return INSTANCE;
    }
}

/*
 * @(#)Database.java 2021.05.19 at 06:10:46 EDT
 * EOF
 */
