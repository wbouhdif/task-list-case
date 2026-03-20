package com.ortecfinance.tasklist.services;

import com.ortecfinance.tasklist.Task;

import java.time.LocalDate;
import java.util.*;

public class TaskService {

    private final Map<String, List<Task>> tasks = new LinkedHashMap<>();
    private long lastAssignedId = 0;

    public Map<String, List<Task>> getAllProjectsWithTasks() {
        return tasks;
    }

    public void addProject(String name) {
        tasks.put(name, new ArrayList<>());
    }

    public boolean addTaskToProject(String project, String description) {
        List<Task> projectTasks = tasks.get(project);
        if (projectTasks == null) {
            return false;
        }
        projectTasks.add(new Task(nextId(), description, false));
        return true;
    }

    public boolean markTaskAsDone(int id) {
        return setTaskFinished(id, true);
    }

    public boolean markTaskAsUndone(int id) {
        return setTaskFinished(id, false);
    }

    public boolean addDeadlineToTask(int id, LocalDate deadline) {
        Task task = findById(id);
        if (task == null) {
            return false;
        }
        task.setDeadline(deadline);
        return true;
    }

    public List<Task> getAllTasksSortedByDeadline() {
        List<Task> allTasks = new ArrayList<>();
        for (List<Task> projectTasks : tasks.values()) {
            allTasks.addAll(projectTasks);
        }

        allTasks.sort(Comparator.comparing(
                Task::getDeadline,
                Comparator.nullsLast(Comparator.naturalOrder()))
        );

        return allTasks;
    }

    private boolean setTaskFinished(int id, boolean isFinished) {
        Task task = findById(id);
        if (task == null) {
            return false;
        }
        task.setIsFinished(isFinished);
        return true;
    }

    private Task findById(int id) {
        for (Map.Entry<String, List<Task>> project : tasks.entrySet()) {
            for (Task task : project.getValue()) {
                if (task.getId() == id) {
                    return task;
                }
            }
        }
        return null;
    }

    private long nextId() {
        return ++lastAssignedId;
    }
}