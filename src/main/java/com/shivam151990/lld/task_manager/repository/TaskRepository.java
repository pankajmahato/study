package com.shivam151990.lld.task_manager.repository;

import com.shivam151990.lld.task_manager.model.Task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository {
    Task save(Task task);
    Optional<Task> findById(UUID id);
    void delete(UUID id);
    List<Task> findAll();
}
