package com.ortecfinance.tasklist;

import java.time.LocalDate;

public final class Task {
    private final long id;
    private final String description;
    private boolean isFinished;
    private LocalDate deadline;

    public Task(long id, String description, boolean isFinished) {
        this.id = id;
        this.description = description;
        this.isFinished = isFinished;
        this.deadline = null;
    }

    public long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setIsFinished(boolean isFinished) {
        this.isFinished = isFinished;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }
}
