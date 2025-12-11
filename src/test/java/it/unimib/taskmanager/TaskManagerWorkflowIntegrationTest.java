package it.unimib.taskmanager;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests that simulate real-world usage scenarios.
 * These tests verify the complete workflow of the Task Manager application.
 */
class TaskManagerWorkflowIntegrationTest {

    /**
     * Simulates a complete user workflow:
     * - Create categories
     * - Create tasks in different categories
     * - Mark tasks as done
     * - Delete a category and verify task handling
     * - Search and filter operations
     */
    @Test
    void completeWorkflow_userManagesTasksAndCategories() {
        // Setup: create a fresh application instance
        Application app = Application.createDefault();

        // Step 1: User creates two categories
        Category work = app.createCategory("Work");
        Category personal = app.createCategory("Personal");
        assertNotNull(work, "Work category should be created");
        assertNotNull(personal, "Personal category should be created");
        assertEquals(2, app.listCategories().size());

        // Step 2: User creates tasks in different categories
        Task meeting = app.createTask("Team meeting", "Weekly standup at 10am", work.getId());
        Task shopping = app.createTask("Buy groceries", "Milk, bread, eggs", personal.getId());
        Task report = app.createTask("Quarterly report", "Q4 financial analysis", work.getId());
        Task exercise = app.createTask("Go to gym", "Leg day", personal.getId());

        assertEquals(4, app.listTasks().size());

        // Step 3: User filters tasks by category
        List<Task> workTasks = app.listTasksByCategory(work.getId());
        List<Task> personalTasks = app.listTasksByCategory(personal.getId());
        assertEquals(2, workTasks.size(), "Should have 2 work tasks");
        assertEquals(2, personalTasks.size(), "Should have 2 personal tasks");

        // Step 4: User completes some tasks
        app.markTaskDone(meeting.getId());
        app.markTaskDone(shopping.getId());

        List<Task> doneTasks = app.listTasksByDoneStatus(true);
        List<Task> pendingTasks = app.listTasksByDoneStatus(false);
        assertEquals(2, doneTasks.size(), "Should have 2 completed tasks");
        assertEquals(2, pendingTasks.size(), "Should have 2 pending tasks");

        // Step 5: User decides the meeting needs follow-up, marks it pending again
        app.markTaskPending(meeting.getId());
        assertEquals(1, app.listTasksByDoneStatus(true).size());
        assertEquals(3, app.listTasksByDoneStatus(false).size());

        // Step 6: User searches for tasks
        List<Task> searchResult = app.searchTasksByTitle("report");
        assertEquals(1, searchResult.size());
        assertEquals("Quarterly report", searchResult.get(0).getTitle());

        // Step 7: User decides to delete the Work category
        boolean deleted = app.deleteCategory(work.getId());
        assertTrue(deleted);
        assertEquals(1, app.listCategories().size(), "Only Personal category should remain");

        // Step 8: Verify tasks that were in Work category now have null category
        Task orphanedMeeting = app.listTasks().stream()
                .filter(t -> t.getId() == meeting.getId())
                .findFirst()
                .orElse(null);
        assertNotNull(orphanedMeeting);
        assertNull(orphanedMeeting.getCategory(), "Meeting task should have no category");

        Task orphanedReport = app.listTasks().stream()
                .filter(t -> t.getId() == report.getId())
                .findFirst()
                .orElse(null);
        assertNotNull(orphanedReport);
        assertNull(orphanedReport.getCategory(), "Report task should have no category");

        // Step 9: User deletes a task
        app.deleteTask(exercise.getId());
        assertEquals(3, app.listTasks().size());

        // Final verification
        assertEquals(1, app.listCategories().size());
        assertEquals(3, app.listTasks().size());
    }

    /**
     * Tests edge cases in workflow:
     * - Empty operations
     * - Invalid IDs
     * - Boundary conditions
     */
    @Test
    void edgeCaseWorkflow_handlesInvalidOperationsGracefully() {
        Application app = Application.createDefault();

        // Empty state operations
        assertTrue(app.listCategories().isEmpty());
        assertTrue(app.listTasks().isEmpty());
        assertTrue(app.searchTasksByTitle("anything").isEmpty());
        assertTrue(app.searchCategoriesByName("anything").isEmpty());

        // Operations with invalid IDs
        assertFalse(app.deleteCategory(999));
        assertFalse(app.deleteTask(999));
        assertFalse(app.markTaskDone(999));
        assertFalse(app.markTaskPending(999));
        assertFalse(app.updateTaskTitle(999, "New Title"));
        assertFalse(app.updateTaskDescription(999, "New Description"));
        assertFalse(app.updateTaskCategory(999, 1));

        // Create task with invalid category
        assertNull(app.createTask("Task", "Desc", 999));

        // Create category with invalid name
        assertNull(app.createCategory(null));
        assertNull(app.createCategory(""));
        assertNull(app.createCategory("   "));

        // Verify state hasn't changed
        assertTrue(app.listCategories().isEmpty());
        assertTrue(app.listTasks().isEmpty());
    }

    /**
     * Tests the scenario where user manages multiple categories
     * and reassigns tasks between them.
     */
    @Test
    void categoryReassignmentWorkflow_tasksCanBeMoved() {
        Application app = Application.createDefault();

        // Create categories
        Category urgent = app.createCategory("Urgent");
        Category later = app.createCategory("Later");
        Category done = app.createCategory("Done");

        // Create task in Urgent
        Task task = app.createTask("Important task", "Must be done today", urgent.getId());
        assertEquals(urgent.getId(), task.getCategory().getId());

        // Move task to Later
        boolean moved = app.updateTaskCategory(task.getId(), later.getId());
        assertTrue(moved);

        Task updated = app.listTasks().get(0);
        assertEquals(later.getId(), updated.getCategory().getId());

        // Mark as done and move to Done category
        app.markTaskDone(task.getId());
        app.updateTaskCategory(task.getId(), done.getId());

        Task finalTask = app.listTasks().get(0);
        assertTrue(finalTask.isDone());
        assertEquals(done.getId(), finalTask.getCategory().getId());
    }

    /**
     * Tests search functionality across various scenarios.
     */
    @Test
    void searchWorkflow_findsTasksAndCategories() {
        Application app = Application.createDefault();

        // Setup
        Category work = app.createCategory("Work Projects");
        Category home = app.createCategory("Home Work");
        app.createTask("Work on presentation", "Slides for Monday", work.getId());
        app.createTask("Do homework", "Math exercises", home.getId());
        app.createTask("Clean office", "Organize desk", work.getId());

        // Search categories containing "work"
        List<Category> categoryResults = app.searchCategoriesByName("work");
        assertEquals(2, categoryResults.size());

        // Search tasks containing "work"
        List<Task> taskResults = app.searchTasksByTitle("work");
        assertEquals(2, taskResults.size());

        // Case insensitive search
        assertEquals(2, app.searchTasksByTitle("WORK").size());
        assertEquals(2, app.searchCategoriesByName("WORK").size());

        // Partial match
        assertEquals(1, app.searchTasksByTitle("presentation").size());

        // No match
        assertTrue(app.searchTasksByTitle("xyz123").isEmpty());
        assertTrue(app.searchCategoriesByName("xyz123").isEmpty());
    }

    /**
     * Tests bulk operations to ensure data consistency.
     */
    @Test
    void bulkOperationsWorkflow_maintainsDataConsistency() {
        Application app = Application.createDefault();

        // Create multiple categories
        for (int i = 1; i <= 5; i++) {
            app.createCategory("Category " + i);
        }
        assertEquals(5, app.listCategories().size());

        // Create multiple tasks in each category
        List<Category> categories = app.listCategories();
        for (Category cat : categories) {
            for (int i = 1; i <= 3; i++) {
                app.createTask("Task " + i + " in " + cat.getName(), "Description", cat.getId());
            }
        }
        assertEquals(15, app.listTasks().size());

        // Mark half as done
        List<Task> tasks = app.listTasks();
        for (int i = 0; i < tasks.size() / 2; i++) {
            app.markTaskDone(tasks.get(i).getId());
        }

        int doneCount = app.listTasksByDoneStatus(true).size();
        int pendingCount = app.listTasksByDoneStatus(false).size();
        assertEquals(15, doneCount + pendingCount);

        // Delete a category and verify task count stays the same (just orphaned)
        int firstCategoryId = categories.get(0).getId();
        int tasksInFirstCategory = app.listTasksByCategory(firstCategoryId).size();
        app.deleteCategory(firstCategoryId);

        assertEquals(4, app.listCategories().size());
        assertEquals(15, app.listTasks().size()); // Tasks should still exist

        // Verify orphaned tasks
        long orphanedTasks = app.listTasks().stream()
                .filter(t -> t.getCategory() == null)
                .count();
        assertEquals(tasksInFirstCategory, orphanedTasks);
    }
}
