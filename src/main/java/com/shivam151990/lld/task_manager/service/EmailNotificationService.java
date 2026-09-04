package com.shivam151990.lld.task_manager.service;

import com.shivam151990.lld.task_manager.model.User;

public class EmailNotificationService implements NotificationService {

    @Override
    public void notify(User u, String msg) {
        System.out.printf("[EMAIL to %s] %s\n", u.getEmail(), msg);
    }
}
