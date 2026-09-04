package com.shivam151990.lld.task_manager.service;

import com.shivam151990.lld.task_manager.filter.TaskFilter;
import com.shivam151990.lld.task_manager.model.Status;
import com.shivam151990.lld.task_manager.model.Task;
import com.shivam151990.lld.task_manager.model.TaskHistory;
import com.shivam151990.lld.task_manager.model.User;
import com.shivam151990.lld.task_manager.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class TaskService {
    private final TaskRepository repo;
    private final ReminderScheduler reminderScheduler;
    private final NotificationService notifier;
    private final TaskHistoryService historyService;

    public TaskService(TaskRepository r, ReminderScheduler sch, NotificationService ntf, TaskHistoryService historyService) {
        this.repo = r;
        this.reminderScheduler = sch;
        this.notifier = ntf;
        this.historyService = historyService;
    }

    /**
     * Create new Task
     * @param task task to be updated
     * @return saved task
     */
    public Task create(Task task) {
        return repo.save(task);
    }

    /**
     * Assign user to the task
     * @param taskId taskId of the task
     * @param user user for to be updated for the task
     * @return updated task
     */
    public Task assign(UUID taskId, User user) {
        return update(taskId, task -> task.setAssignee(user));
    }

    /**
     * Deletes task
     * @param taskId taskId of the task
     */
    public void delete(UUID taskId) {
        repo.delete(taskId);
    }

    /**
     * Update task status
     * @param taskId taskId of the task
     * @return updated task
     */
    public Task markCompleted(UUID taskId) {
        Status old = repo.findById(taskId).orElseThrow().getStatus();
        Task t = update(taskId, task -> task.setStatus(Status.COMPLETED));
        TaskHistory history = new TaskHistory(
                t.getId(),
                old,
                Status.COMPLETED,
                LocalDateTime.now(),
                t.getAssignee().getId()
        );
        historyService.save(history);
        return repo.save(t);
    }

    public List<Task> search(TaskFilter filter) {
        return repo.findAll()
                .stream()
                .filter(filter)
                .toList();
    }

    public void setReminder(UUID taskId, LocalDateTime when) {
        Task t = update(taskId, task -> task.setReminderAt(when));
        reminderScheduler.scheduleAt(when, () -> notifier.notify(
                t.getAssignee(), "Reminder: '" + t.getTitle() + "' due at " + when
        ));
    }

    private Task update(UUID taskId, Consumer<Task> updater) {
        Task t = repo.findById(taskId).orElseThrow();
        updater.accept(t);
        return repo.save(t);
    }
}
