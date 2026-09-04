package com.shivam151990.lld.task_manager.service;

import com.shivam151990.lld.task_manager.model.User;

/**
 * Notification & Reminder scheduling
 */
public interface NotificationService {
    void notify(User u, String message);
}
