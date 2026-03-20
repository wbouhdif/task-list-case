package com.ortecfinance.tasklist;

import com.ortecfinance.tasklist.services.TaskService;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TaskServiceTest {

    @Test
    void should_createEmptyProject_when_addProjectIsCalled() {
        TaskService taskService = new TaskService();

        taskService.addProject("training");

        Map<String, List<Task>> projects = taskService.getAllProjectsWithTasks();
        assertThat(projects.containsKey("training"), is(true));
        assertThat(projects.get("training"), is(empty()));
    }

    @Test
    void should_addTaskToExistingProject_when_addTaskToProjectIsCalled() {
        TaskService taskService = new TaskService();
        taskService.addProject("training");

        boolean added = taskService.addTaskToProject("training", "Learn TDD");

        Map<String, List<Task>> projects = taskService.getAllProjectsWithTasks();
        assertThat(added, is(true));
        assertThat(projects.get("training"), hasSize(1));
        assertThat(projects.get("training").getFirst().getDescription(), is("Learn TDD"));
        assertThat(projects.get("training").getFirst().isFinished(), is(false));
    }

    @Test
    void should_markTaskAsDone_when_markTaskAsDoneIsCalledWithExistingId() {
        TaskService taskService = new TaskService();
        taskService.addProject("training");
        taskService.addTaskToProject("training", "Learn TDD");

        boolean updated = taskService.markTaskAsDone(1);

        Task task = taskService.getAllProjectsWithTasks().get("training").getFirst();
        assertThat(updated, is(true));
        assertThat(task.isFinished(), is(true));
    }

    @Test
    void should_markTaskAsUndone_when_markTaskAsUndoneIsCalledWithExistingId() {
        TaskService taskService = new TaskService();
        taskService.addProject("training");
        taskService.addTaskToProject("training", "Learn TDD");
        taskService.markTaskAsDone(1);

        boolean updated = taskService.markTaskAsUndone(1);

        Task task = taskService.getAllProjectsWithTasks().get("training").getFirst();
        assertThat(updated, is(true));
        assertThat(task.isFinished(), is(false));
    }

    @Test
    void should_addDeadlineToTask_when_addDeadlineToTaskIsCalledWithExistingId() {
        TaskService taskService = new TaskService();
        taskService.addProject("training");
        taskService.addTaskToProject("training", "Learn TDD");

        LocalDate deadline = LocalDate.of(2027, 1, 1);
        boolean updated = taskService.addDeadlineToTask(1, deadline);

        Task task = taskService.getAllProjectsWithTasks().get("training").getFirst();
        assertThat(updated, is(true));
        assertThat(task.getDeadline(), is(deadline));
    }
}