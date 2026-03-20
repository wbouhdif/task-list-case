package com.ortecfinance.tasklist.console;

import com.ortecfinance.tasklist.services.TaskService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public final class InitializeConsole {

    public static void startConsole() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(System.out, true);

        TaskService taskService = new TaskService();
        ConsoleHandler consoleHandler = new ConsoleHandler(in, out, taskService);
        consoleHandler.run();
    }
}
