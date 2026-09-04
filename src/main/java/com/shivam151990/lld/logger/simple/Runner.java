package com.shivam151990.lld.logger.simple;

import com.shivam151990.lld.logger.simple.sink.ConsoleLog;
import com.shivam151990.lld.logger.simple.sink.FileLog;

public class Runner {
    public static void main(String[] args) {
        Logger logger = Logger.getInstance();

        logger.setLogSink(new ConsoleLog());

        logger.setLogLevel(LogLevel.ERROR);

        logger.info("This is an info message");
        logger.warn("This is a warning message");
        logger.error("This is an error message");

        // Using FileLogSink
        logger.setLogSink(new FileLog());

        logger.info("Logging to a file");
    }
}
