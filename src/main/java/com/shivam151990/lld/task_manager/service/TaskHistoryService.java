package com.shivam151990.lld.task_manager.service;

import com.shivam151990.lld.task_manager.model.TaskHistory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TaskHistoryService {

    private final List<TaskHistory> taskHistories;

    public TaskHistoryService() {
        taskHistories = new ArrayList<>();
    }

    public void save(TaskHistory history) {
        taskHistories.add(history);
    }

    public List<TaskHistory> get(UUID userId) {
        return taskHistories
                .stream()
                .filter(h -> h.userId() == userId)
                .toList();
    }
}
