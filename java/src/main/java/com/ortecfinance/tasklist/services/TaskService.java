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

    public void check(String[] idString) {
        setTaskToDone(idString, true);
    }

    public void uncheck(String[] idString) {
        setTaskToDone(idString, false);
    }


    private void setTaskToDone(String[] idString, boolean isFinished) {
        if (idString.length < 1) {
            out.println("Usage: " + (isFinished ? "check <task ID>" : "uncheck <task ID>"));
            return;
        }

        String taskID = idString[0];

        if (isValidNumber(taskID)) {
            int id = Integer.parseInt(taskID);

            for (Map.Entry<String, List<Task>> project : tasks.entrySet()) {
                for (Task task : project.getValue()) {
                    if (task.getId() == id) {
                        task.setIsFinished(isFinished);
                        return;
                    }
                }
            }
            out.printf("Could not find a task with an ID of %d.%n", id);
            return;
        }
        out.printf("\"%s\" is not a valid task ID.%n", taskID);
    }

    public void showAllCommands() {
        out.println("Commands:");
        out.println("  show");
        out.println("  add project <project name>");
        out.println("  add task <project name> <task description>");
        out.println("  deadline <task ID> <deadline date>");
        out.println("  view-by-deadline");
        out.println("  check <task ID>");
        out.println("  uncheck <task ID>");
        out.println();
    }

    public void handleUnknownInput(String command) {
        if (command.isEmpty() ) {
            out.print("Please enter a command. Type 'help' for available commands.");
            out.println();
            return;
        }
        out.printf("I don't know what the command \"%s\" is. Please use one of the following commands", command);
        out.println();
        showAllCommands();
    }

    private boolean isValidNumber(String idString) {
        try {
            Integer.parseInt(idString);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidDate(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        try {
            LocalDate.parse(dateString, formatter);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private long nextId() {
        return ++lastAssignedId;
    }


    public void addDeadline(String[] deadlineArgs) {
        if (deadlineArgs.length < 2) {
            out.println("Usage: deadline <task ID> <date>");
            return;
        }

        String taskID = deadlineArgs[0];
        String dateString = deadlineArgs[1];

        if (isValidNumber(taskID)) {
            int id = Integer.parseInt(taskID);

            if (!isValidDate(dateString)) {
                out.printf("\"%s\" is not a valid date. Use dd-MM-yyyy.%n", dateString);
                return;
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate deadline = LocalDate.parse(dateString, formatter);

            for (Map.Entry<String, List<Task>> project : tasks.entrySet()) {
                for (Task task : project.getValue()) {
                    if (task.getId() == id) {
                        task.setDeadline(deadline);
                        return;
                    }
                }
            }
            out.printf("Could not find a task with an ID of %d.%n", id);
            return;
        }

        out.printf("\"%s\" is not a valid task ID.%n", taskID);

    }

    public void viewByDeadline() {
        List<Task> allTasks = new ArrayList<>();
        for (List<Task> projectTasks : tasks.values()) {
            allTasks.addAll(projectTasks);
        }

        allTasks.sort(Comparator.comparing(
                Task::getDeadline,
                Comparator.nullsLast(Comparator.naturalOrder()))
        );

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        LocalDate currentDeadline = null;
        boolean inNoDeadlineBlock = false;

        for (Task task : allTasks) {
            LocalDate deadline = task.getDeadline();

            if (deadline != null) {
                if (!deadline.equals(currentDeadline)) {
                    currentDeadline = deadline;
                    inNoDeadlineBlock = false;
                    out.println(deadline.format(formatter) + ":");
                }
                out.printf("    %d: %s%n", task.getId(), task.getDescription());
            } else {
                if (!inNoDeadlineBlock) {
                    out.println("No deadline:");
                    inNoDeadlineBlock = true;
                }
                out.printf("    %d: %s%n", task.getId(), task.getDescription());
            }
        }
        out.println();
    }
}
