package com.shivam151990.lld.logger.simple;

import com.shivam151990.lld.logger.simple.sink.ConsoleLog;
import com.shivam151990.lld.logger.simple.sink.LogSink;

import java.time.LocalDate;

class Logger {
    private static volatile Logger instance;
    private LogLevel logLevel;
    private LogSink logSink;

    private Logger() {
        logLevel = LogLevel.INFO;
        logSink = new ConsoleLog();
    }

    public static Logger getInstance() {
        if (instance == null) {
            synchronized (Logger.class) {
                if (instance == null) {
                    instance = new Logger();
                }
            }
        }
        return instance;
    }

    public void setLogLevel(LogLevel level) {
        logLevel = level;
    }

    public void setLogSink(LogSink sink) {
        logSink = sink;
    }

    public void error(String message) {
        if (logLevel.ordinal() <= LogLevel.ERROR.ordinal()) {
            log(LogLevel.ERROR, message);
        }
    }

    public void warn(String message) {
        if (logLevel.ordinal() <= LogLevel.WARNING.ordinal()) {
            log(LogLevel.WARNING, message);
        }
    }

    public void debug(String message) {
        if (logLevel.ordinal() <= LogLevel.DEBUG.ordinal()) {
            log(LogLevel.DEBUG, message);
        }
    }

    public void trace(String message) {
        if (logLevel.ordinal() <= LogLevel.TRACE.ordinal()) {
            log(LogLevel.TRACE, message);
        }
    }

    public void info(String message) {
        if (logLevel.ordinal() <= LogLevel.INFO.ordinal()) {
            log(LogLevel.INFO, message);
        }
    }

    private void log(LogLevel level, String message) {
        if (logLevel.ordinal() <= level.ordinal()) {
            String logMessage = "[" + LocalDate.now() + "] [" + level + "] " + message;
            logSink.log(logMessage);
        }
    }
}
