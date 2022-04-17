/*
 * Created By: 	Justin A. Langley
 * Course: 	C195 - Software II 
 * Language: 	Java
 *
 * @(#)DataSourceFactory.java   2021.05.20 at 10:55:46 EDT
 * Version: 1.0.0
 */



package Util.DB;

import java.io.IOException;

import java.sql.SQLException;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.mysql.cj.jdbc.MysqlDataSource;

//~--- classes ----------------------------------------------------------------

/**
 * A factory class containing methods for building DataSource objects
 *
 * Methods:
 *      getMysqlDataSource()
 *
 */
public final class DataSourceFactory
{

    /**
     * This method takes the location of a .properties file containing DB
     * connection properties and builds it into an object of type DataSource
     *
     * @param path The location of the .properties file
     * @return DataSource
     */
    public static DataSource getMysqlDataSource(String path)
    {
        Properties      props = new Properties();
        MysqlDataSource ds    = null;

        try {
            props.load(DataSourceFactory.class.getClassLoader()
                                              .getResourceAsStream(path));

            ds = new MysqlDataSource();

            ds.setAutoReconnect(Boolean.parseBoolean(props.getProperty("MYSQL_DB_AUTORECONNECT")));
            ds.setCacheCallableStmts(Boolean.parseBoolean(props.getProperty("MYSQL_DB_CACHE_CALLABLE_STMTS")));
            ds.setCallableStmtCacheSize(Integer.valueOf(props.getProperty("MYSQL_DB_CALLABLE_STMT_CACHE_SIZE")));
            ds.setTcpKeepAlive(Boolean.parseBoolean(props.getProperty("MYSQL_DB_SET_TCP_KEEPALIVE")));
            ds.setTcpNoDelay(Boolean.parseBoolean(props.getProperty("MYSQL_DB_SET_TCP_NODELAY")));
            ds.setURL(props.getProperty("MYSQL_DB_URL"));
            ds.setUser(props.getProperty("MYSQL_DB_USERNAME"));
            ds.setPassword(props.getProperty("MYSQL_DB_PASSWORD"));
        } catch (IOException ex) {
            Logger.getLogger(DataSourceFactory.class.getName())
                  .log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(DataSourceFactory.class.getName())
                  .log(Level.SEVERE, null, ex);
        }

        return ds;
    }
}

/*
 * @(#)DataSourceFactory.java 2021.05.20 at 10:55:46 EDT
 * EOF
 */
