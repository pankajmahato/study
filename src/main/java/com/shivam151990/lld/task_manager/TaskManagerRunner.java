package com.shivam151990.lld.task_manager;

import com.shivam151990.lld.task_manager.filter.TaskFilter;
import com.shivam151990.lld.task_manager.model.Priority;
import com.shivam151990.lld.task_manager.model.Task;
import com.shivam151990.lld.task_manager.model.User;
import com.shivam151990.lld.task_manager.repository.InMemoryTaskRepository;
import com.shivam151990.lld.task_manager.service.EmailNotificationService;
import com.shivam151990.lld.task_manager.service.ReminderScheduler;
import com.shivam151990.lld.task_manager.service.TaskHistoryService;
import com.shivam151990.lld.task_manager.service.TaskService;

import java.time.LocalDateTime;
import java.util.UUID;

public class TaskManagerRunner {

    public static void main(String[] args) throws InterruptedException {

        // Bean Initialization
        ReminderScheduler scheduler = new ReminderScheduler();
        TaskService taskService = new TaskService(
                new InMemoryTaskRepository(),
                scheduler,
                new EmailNotificationService(),
                new TaskHistoryService()
        );

        // create users
        User shivam = new User(UUID.randomUUID(), "Shivam", "shivam@example.com");
        User ashish   = new User(UUID.randomUUID(), "Ashish",   "ashish@example.com");

        // Create Tasks
        Task t1 = new Task.Builder("Implement feature X")
                .description("Details...1")
                .dueDate(LocalDateTime.now().plusDays(2))
                .priority(Priority.HIGH)
                .assignee(shivam)
                .build();

        Task t2 = new Task.Builder("Implement feature y")
                .description("Details...2")
                .dueDate(LocalDateTime.now().plusDays(2))
                .priority(Priority.HIGH)
                .assignee(ashish)
                .build();

        Task t3 = new Task.Builder("Implement feature z")
                .description("Details...3")
                .dueDate(LocalDateTime.now().plusDays(2))
                .priority(Priority.MEDIUM)
                .assignee(shivam)
                .build();


        taskService.create(t1);
        taskService.create(t2);
        taskService.create(t3);

        // set reminder
        taskService.setReminder(t1.getId(), LocalDateTime.now().plusSeconds(2));

        Thread.sleep(5_000);

        // Search Service
        System.out.println(taskService.search(TaskFilter.byPriority(Priority.MEDIUM)));


        scheduler.shutdown();
        System.out.println();
        System.out.println("SERVICE STOPPED");
    }
}
