package com.ortecfinance.tasklist.services;

import com.ortecfinance.tasklist.Task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.lang.System.out;

public class TaskService {

    private final Map<String, List<Task>> tasks = new LinkedHashMap<>();
    private long lastAssignedId = 0;


    public void show() {
        for (Map.Entry<String, List<Task>> project : tasks.entrySet()) {
            out.println(project.getKey());
            for (Task task : project.getValue()) {
                out.printf("    [%c] %d: %s%n", (task.isFinished() ? 'x' : ' '), task.getId(), task.getDescription());
            }
            out.println();
        }
    }

    public void add(String[] addArgs) {
        if (addArgs.length == 0) {
            handleUnknownInput("add");
            return;
        }

        String subcommand = addArgs[0];

        if ("project".equals(subcommand)) {
            if (addArgs.length < 2) {
                out.println("Usage: add project <project name>");
                return;
            }
            String projectName = addArgs[1];
            addProject(projectName);

        } else if ("task".equals(subcommand)) {
            if (addArgs.length < 3) {
                out.println("Usage: add task <project name> <task description>");
                return;
            }
            String projectName = addArgs[1];
            String description = String.join(" ", Arrays.copyOfRange(addArgs, 2, addArgs.length));
            addTask(projectName, description);

        } else {
            handleUnknownInput(subcommand);
        }
    }

    private void addProject(String name) {
        tasks.put(name, new ArrayList<>());
    }

    private void addTask(String project, String description) {
        List<Task> projectTasks = tasks.get(project);
        if (projectTasks == null) {
            out.printf("Could not find a project with the name \"%s\".", project);
            out.println();
            return;
        }
        projectTasks.add(new Task(nextId(), description, false));
    }

    private void check(String idString) {
        setDone(idString, true);
    }

    private void uncheck(String idString) {
        setDone(idString, false);
    }

    private void setDone(String idString, boolean done) {
        int id = Integer.parseInt(idString);
        for (Map.Entry<String, List<Task>> project : tasks.entrySet()) {
            for (Task task : project.getValue()) {
                if (task.getId() == id) {
                    task.setDone(done);
                    return;
                }
            }
        }
        out.printf("Could not find a task with an ID of %d.", id);
        out.println();
    }

    private void showAllCommands() {
        out.println("Commands:");
        out.println("  show");
        out.println("  add project <project name>");
        out.println("  add task <project name> <task description>");
        out.println("  check <task ID>");
        out.println("  uncheck <task ID>");
        out.println();
    }

    private void handleUnknownInput(String command) {
        if (command.isEmpty()) {
            out.printf("Please enter a command. Type 'help' for available commands.");
            out.println();
            return;
        }
        out.printf("I don't know what the command \"%s\" is. Please use one of the following commands", command);
        out.println();
        showAllCommands();
    }

    private long nextId() {
        return ++lastAssignedId;
    }
}
