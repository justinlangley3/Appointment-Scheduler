/*
 * Created By: 	Justin A. Langley
 * Course: 	C195 - Software II 
 * Language: 	Java
 *
 * @(#)Log.java   2021.05.19 at 06:11:10 EDT
 * Version: 1.0.0
 */



package Util.Security;

import java.io.IOException;

import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Filter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

//~--- classes ----------------------------------------------------------------

public class Log
{
    static final Logger logger = Logger.getLogger(Log.class.getName());

    //~--- constructors -------------------------------------------------------

    public Log()
    {
        logger.setLevel(Level.INFO);
    }

    //~--- methods ------------------------------------------------------------

    public void log(Level level, String msg)
    {
        try {
            LogManager.getLogManager()
                      .readConfiguration(Log.class.getClassLoader()
                                                  .getResourceAsStream("logging.properties"));
        } catch (SecurityException | IOException ex) {
            Logger.getLogger(Log.class.getName())
                  .log(Level.SEVERE, null, ex);
        }

        logger.setLevel(Level.INFO);
        logger.addHandler(new ConsoleHandler());

        try {
            Handler fileHandler = new FileHandler("security.log", 1024 * 256, 1, true);

            fileHandler.setFormatter(new LogFormatter());
            fileHandler.setFilter(new LogFilter());
            logger.addHandler(fileHandler);
            logger.log(level, msg);
            fileHandler.flush();
            fileHandler.close();
        } catch (SecurityException | IOException ex) {
            Logger.getLogger(Log.class.getName())
                  .log(Level.SEVERE, null, ex);
        }
    }

    //~--- inner classes ------------------------------------------------------

    public class LogFilter
            implements Filter
    {
        @Override
        public boolean isLoggable(LogRecord log)
        {
            if (log.getLevel() != Level.INFO)
            {
                return false;
            }

            return true;
        }
    }


    public class LogFormatter
            extends Formatter
    {
        @Override
        public String format(LogRecord record)
        {
            return "[" + record.getLevel() + "]" + " " + new Date(record.getMillis()) + " :: " + record.getMessage()
                   + "\r\n";
        }
    }
}

/*
 * @(#)Log.java 2021.05.19 at 06:11:10 EDT
 * EOF
 */
