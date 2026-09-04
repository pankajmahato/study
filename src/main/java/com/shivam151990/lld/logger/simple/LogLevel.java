package com.shivam151990.lld.logger.simple;

enum LogLevel {
    TRACE(1),
    DEBUG(2),
    INFO(3),
    WARNING(4),
    ERROR(5);

    final int level;

    LogLevel(int level) {
        this.level = level;
    }
}
