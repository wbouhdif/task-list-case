package com.ortecfinance.tasklist.console;

import com.ortecfinance.tasklist.services.TaskService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

public class ConsoleHandler {

    private static final String QUIT = "quit";

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
            taskService.handleUnknownInput("");
            return;
        }

        Command command = Command.from(formattedInput[0]);
        if (command == null) {
            taskService.handleUnknownInput(formattedInput[0]);
            return;
        }
        String[] commandRest = Arrays.copyOfRange(formattedInput, 1, formattedInput.length);

        switch (command) {
            case SHOW:
                taskService.show();
                break;
            case ADD:
                taskService.add(commandRest);
                break;
            case CHECK:
                taskService.check(commandRest);
                break;
            case UNCHECK:
                taskService.uncheck(commandRest);
                break;
            case DEADLINE:
                taskService.addDeadline(commandRest);
                break;
            case HELP:
                taskService.showAllCommands();
                break;
            case VIEW_BY_DEADLINE:
                taskService.viewByDeadline();
                break;

            default:
                taskService.handleUnknownInput(formattedInput[0]);
                break;
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