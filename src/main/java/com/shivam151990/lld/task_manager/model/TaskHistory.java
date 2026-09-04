package com.shivam151990.lld.task_manager.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskHistory(UUID taskId, Status from, Status to, LocalDateTime timestamp, UUID userId) { }
