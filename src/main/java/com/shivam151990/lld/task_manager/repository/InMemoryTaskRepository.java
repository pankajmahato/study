package com.shivam151990.lld.task_manager.repository;

import com.shivam151990.lld.task_manager.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryTaskRepository implements TaskRepository {
    private final ConcurrentMap<UUID, Task> store;

    public InMemoryTaskRepository() {
        store = new ConcurrentHashMap<>();
    }

    @Override
    public Task save(Task t) {
        store.put(t.getId(), t);
        return t;
    }

    @Override
    public Optional<Task> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public void delete(UUID id) {
        store.remove(id);
    }

    @Override public List<Task> findAll() {
        return new ArrayList<>(store.values());
    }
}