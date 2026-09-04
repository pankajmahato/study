package com.shivam151990.lld.task_manager.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Task {
    private final UUID id;

    @Setter
    private String title;

    @Setter
    private String description;

    @Setter
    private LocalDateTime dueDate;

    @Setter
    private Priority priority;

    @Setter
    private Status status;

    @Setter
    private User assignee;

    @Setter
    private LocalDateTime reminderAt;

    private Task(UUID id) {
        this.id = id;
        this.status = Status.PENDING;
    }

    public static class Builder {
        private final Task task;

        public Builder(String title) {
            task = new Task(UUID.randomUUID());
            task.title = title;
        }
        public Builder description(String d) {
            task.description = d;
            return this;
        }
        public Builder dueDate(LocalDateTime dt) {
            task.dueDate = dt;
            return this;
        }
        public Builder priority(Priority p) {
            task.priority = p;
            return this;
        }
        public Builder assignee(User u) {
            task.assignee = u;
            return this;
        }
        public Task build() {
            return task;
        }
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", dueDate=" + dueDate +
                ", priority=" + priority +
                ", status=" + status +
                ", assignee=" + assignee +
                ", reminderAt=" + reminderAt +
                '}';
    }
}
