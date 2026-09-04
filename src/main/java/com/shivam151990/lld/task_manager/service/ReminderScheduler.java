package com.shivam151990.lld.task_manager.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ReminderScheduler {

    private final ScheduledExecutorService scheduler;

    public ReminderScheduler() {
        scheduler = Executors.newScheduledThreadPool(1);
    }

    public void scheduleAt(LocalDateTime when, Runnable action) {
        long delay = Duration.between(LocalDateTime.now(), when).toMillis();
        if (delay < 0) {
            delay = 0;
        }
        scheduler.schedule(action, delay, TimeUnit.MILLISECONDS);
    }

    public void shutdown() {
        scheduler.shutdown();
    }
}
