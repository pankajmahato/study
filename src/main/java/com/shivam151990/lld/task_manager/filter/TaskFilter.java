package com.shivam151990.lld.task_manager.filter;

import com.shivam151990.lld.task_manager.model.Priority;
import com.shivam151990.lld.task_manager.model.Status;
import com.shivam151990.lld.task_manager.model.Task;
import com.shivam151990.lld.task_manager.model.User;

import java.time.LocalDateTime;
import java.util.function.Predicate;

/**
 * Strategies for filtering of Task
 */
public interface TaskFilter extends Predicate<Task> {
    default TaskFilter and(TaskFilter other) {
        return task -> this.test(task) && other.test(task);
    }
    static TaskFilter byPriority(Priority p) {
        return task -> task.getPriority() == p;
    }
    static TaskFilter byStatus(Status s) {
        return task -> task.getStatus() == s;
    }
    static TaskFilter byAssignee(User u) {
        return task -> u.equals(task.getAssignee());
    }
    static TaskFilter dueBefore(LocalDateTime dt) {
        return task -> task.getDueDate() != null && task.getDueDate().isBefore(dt);
    }
}
