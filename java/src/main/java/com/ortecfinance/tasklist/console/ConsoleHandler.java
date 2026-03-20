package com.ortecfinance.tasklist.console;

import com.ortecfinance.tasklist.Task;
import com.ortecfinance.tasklist.services.TaskService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ConsoleHandler {

    private static final String QUIT = "quit";
    private static final DateTimeFormatter DEADLINE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final BufferedReader in;
    private final PrintWriter out;
    private final TaskService taskService;

    public ConsoleHandler(BufferedReader in, PrintWriter out, TaskService taskService) {
        this.in = in;
        this.out = out;
        this.taskService = taskService;
    }

    public void run() {
        out.println("Welcome to TaskList! Type 'help' for available commands.");
        while (true) {
            out.print("> ");
            out.flush();
            String command;
            try {
                command = in.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (command.equals(QUIT)) {
                break;
            }
            execute(command);
        }
    }

    private void execute(String commandLine) {
        String[] formattedInput = formatInput(commandLine);

        if (formattedInput.length == 0) {
            handleUnknownInput("");
            return;
        }

        Command command = Command.from(formattedInput[0]);
        if (command == null) {
            handleUnknownInput(formattedInput[0]);
            return;
        }
        String[] commandRest = Arrays.copyOfRange(formattedInput, 1, formattedInput.length);

        switch (command) {
            case SHOW:
                printProjects(taskService.getAllProjectsWithTasks());
                break;
            case ADD:
                handleAdd(commandRest);
                break;
            case CHECK:
                handleCheck(commandRest);
                break;
            case UNCHECK:
                handleUncheck(commandRest);
                break;
            case DEADLINE:
                handleDeadline(commandRest);
                break;
            case HELP:
                showAllCommands();
                break;
            case VIEW_BY_DEADLINE:
                printTasksByDeadline(taskService.getAllTasksSortedByDeadline());
                break;

            default:
                handleUnknownInput(formattedInput[0]);
                break;
        }
    }

    private void handleAdd(String[] args) {
        if (args.length == 0) {
            handleUnknownInput("add");
            return;
        }

        String subcommand = args[0];

        if ("project".equals(subcommand)) {
            if (args.length < 2) {
                out.println("Usage: add project <project name>");
                return;
            }

            String projectName = args[1];
            taskService.addProject(projectName);

        } else if ("task".equals(subcommand)) {
            if (args.length < 3) {
                out.println("Usage: add task <project name> <task description>");
                return;
            }

            String projectName = args[1];
            String description = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

            boolean added = taskService.addTaskToProject(projectName, description);
            if (!added) {
                out.printf("Could not find a project with the name \"%s\".%n", projectName);
            }

        } else {
            handleUnknownInput(subcommand);
        }
    }

    private void handleCheck(String[] args) {
        if (args.length < 1) {
            out.println("Usage: check <task ID>");
            return;
        }

        String taskId = args[0];

        if (!isValidNumber(taskId)) {
            out.printf("\"%s\" is not a valid task ID.%n", taskId);
            return;
        }

        int id = Integer.parseInt(taskId);
        boolean updated = taskService.markTaskAsDone(id);
        if (!updated) {
            out.printf("Could not find a task with an ID of %d.%n", id);
        }
    }

    private void handleUncheck(String[] args) {
        if (args.length < 1) {
            out.println("Usage: uncheck <task ID>");
            return;
        }

        String taskId = args[0];

        if (!isValidNumber(taskId)) {
            out.printf("\"%s\" is not a valid task ID.%n", taskId);
            return;
        }

        int id = Integer.parseInt(taskId);
        boolean updated = taskService.markTaskAsUndone(id);
        if (!updated) {
            out.printf("Could not find a task with an ID of %d.%n", id);
        }
    }

    private void handleDeadline(String[] args) {
        if (args.length < 2) {
            out.println("Usage: deadline <task ID> <date>");
            return;
        }

        String taskId = args[0];
        String dateString = args[1];

        if (!isValidNumber(taskId)) {
            out.printf("\"%s\" is not a valid task ID.%n", taskId);
            return;
        }

        if (!isValidDate(dateString)) {
            out.printf("\"%s\" is not a valid date. Use dd-MM-yyyy.%n", dateString);
            return;
        }

        int id = Integer.parseInt(taskId);
        LocalDate deadline = LocalDate.parse(dateString, DEADLINE_FORMATTER);

        boolean updated = taskService.addDeadlineToTask(id, deadline);
        if (!updated) {
            out.printf("Could not find a task with an ID of %d.%n", id);
        }
    }

    private void printProjects(Map<String, List<Task>> projects) {
        for (Map.Entry<String, List<Task>> project : projects.entrySet()) {
            out.println(project.getKey());
            for (Task task : project.getValue()) {
                out.printf("    [%c] %d: %s%n",
                        task.isFinished() ? 'x' : ' ',
                        task.getId(),
                        task.getDescription());
            }
            out.println();
        }
    }

    private void printTasksByDeadline(List<Task> allTasks) {
        LocalDate currentDeadline = null;
        boolean inNoDeadlineBlock = false;

        for (Task task : allTasks) {
            LocalDate deadline = task.getDeadline();

            if (deadline != null) {
                if (!deadline.equals(currentDeadline)) {
                    currentDeadline = deadline;
                    inNoDeadlineBlock = false;
                    out.println(deadline.format(DEADLINE_FORMATTER) + ":");
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

    private void showAllCommands() {
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

    private void handleUnknownInput(String command) {
        if (command.isEmpty()) {
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

    private String[] formatInput(String commandLine) {
        if (commandLine == null) {
            return new String[0];
        }
        String trimmed = commandLine.trim();
        if (trimmed.isEmpty()) {
            return new String[0];
        }
        return trimmed.split("\\s+");
    }
}