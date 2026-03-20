package com.ortecfinance.tasklist.console;

public enum Command {
    SHOW("show"),
    ADD("add"),
    CHECK("check"),
    UNCHECK("uncheck"),
    DEADLINE("deadline"),
    HELP("help"),
    QUIT("quit"),
    VIEW_BY_DEADLINE("view-by-deadline");

    private final String keyword;

    Command(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    public static Command from(String input) {
        for (Command c : values()) {
            if (c.keyword.equalsIgnoreCase(input)) {
                return c;
            }
        }
        return null;
    }
}
