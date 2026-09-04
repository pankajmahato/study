package com.shivam151990.lld.logger.simple.sink;

public class FileLog implements LogSink {

    @Override
    public void log(String message) {
        System.out.println("FILE: " + message);
    }
}
