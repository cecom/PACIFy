package de.oppermann.maven.pflist.logger;

import java.util.Calendar;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */
public class Log {

    private static Log INSTANCE;
    private static final String FORMAT = "[%s] [%td.%tm.%tY %d:%tM:%d] %s%n";

    LogLevel logLevel;


    private Log() {
        logLevel = LogLevel.INFO;
    }

    public synchronized static Log getInstance() {
        if (INSTANCE == null)
            INSTANCE = new Log();
        return INSTANCE;
    }

    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public static void log(LogLevel logLevel, String message) {
        if (getInstance().getLogLevel().compareTo(logLevel) > 0)
            return;

        Calendar c = Calendar.getInstance();
        System.out.format(FORMAT, logLevel.toString(), c, c, c, c.get(Calendar.HOUR_OF_DAY), c, c.get(Calendar.SECOND), message);
    }
}
