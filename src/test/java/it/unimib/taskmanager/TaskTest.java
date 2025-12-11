package it.unimib.taskmanager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Task entity.
 * These tests verify the behavior of the Task class in isolation.
 */
class TaskTest {

    @Test
    void constructor_setsAllFields() {
        Category category = new Category(1, "Work");

        Task task = new Task(1, "Title", "Description", category);

        assertEquals(1, task.getId());
        assertEquals("Title", task.getTitle());
        assertEquals("Description", task.getDescription());
        assertEquals(category, task.getCategory());
        assertFalse(task.isDone());
    }

    @Test
    void constructor_newTaskIsPending() {
        Task task = new Task(1, "Task", "Desc", null);

        assertFalse(task.isDone());
    }

    @Test
    void constructor_withNullCategory_createsTask() {
        Task task = new Task(1, "Title", "Description", null);

        assertNull(task.getCategory());
    }

    @Test
    void setTitle_updatesTitle() {
        Task task = new Task(1, "Old Title", "Desc", null);

        task.setTitle("New Title");

        assertEquals("New Title", task.getTitle());
    }

    @Test
    void setDescription_updatesDescription() {
        Task task = new Task(1, "Title", "Old Description", null);

        task.setDescription("New Description");

        assertEquals("New Description", task.getDescription());
    }

    @Test
    void setDescription_toNull_setsNull() {
        Task task = new Task(1, "Title", "Description", null);

        task.setDescription(null);

        assertNull(task.getDescription());
    }

    @Test
    void setCategory_updatesCategory() {
        Category oldCategory = new Category(1, "Old");
        Category newCategory = new Category(2, "New");
        Task task = new Task(1, "Title", "Desc", oldCategory);

        task.setCategory(newCategory);

        assertEquals(newCategory, task.getCategory());
    }

    @Test
    void setCategory_toNull_removesCategory() {
        Category category = new Category(1, "Work");
        Task task = new Task(1, "Title", "Desc", category);

        task.setCategory(null);

        assertNull(task.getCategory());
    }

    @Test
    void markDone_setsStatusToTrue() {
        Task task = new Task(1, "Task", "Desc", null);
        assertFalse(task.isDone());

        task.markDone();

        assertTrue(task.isDone());
    }

    @Test
    void markPending_setsStatusToFalse() {
        Task task = new Task(1, "Task", "Desc", null);
        task.markDone();
        assertTrue(task.isDone());

        task.markPending();

        assertFalse(task.isDone());
    }

    @Test
    void markDone_calledMultipleTimes_staysDone() {
        Task task = new Task(1, "Task", "Desc", null);

        task.markDone();
        task.markDone();
        task.markDone();

        assertTrue(task.isDone());
    }

    @Test
    void equals_sameInstance_returnsTrue() {
        Task task = new Task(1, "Task", "Desc", null);

        assertEquals(task, task);
    }

    @Test
    void equals_sameId_returnsTrue() {
        Category cat = new Category(1, "Work");
        Task task1 = new Task(1, "Title", "Desc", cat);
        Task task2 = new Task(1, "Different", "Other", null);

        assertEquals(task1, task2);
    }

    @Test
    void equals_differentId_returnsFalse() {
        Task task1 = new Task(1, "Task", "Desc", null);
        Task task2 = new Task(2, "Task", "Desc", null);

        assertNotEquals(task1, task2);
    }

    @Test
    void equals_withNull_returnsFalse() {
        Task task = new Task(1, "Task", "Desc", null);

        assertNotEquals(null, task);
    }

    @Test
    void equals_withDifferentType_returnsFalse() {
        Task task = new Task(1, "Task", "Desc", null);

        assertNotEquals(task, "Not a task");
    }

    @Test
    void hashCode_sameId_sameHashCode() {
        Task task1 = new Task(1, "Title1", "Desc1", null);
        Task task2 = new Task(1, "Title2", "Desc2", null);

        assertEquals(task1.hashCode(), task2.hashCode());
    }

    @Test
    void hashCode_differentId_differentHashCode() {
        Task task1 = new Task(1, "Task", "Desc", null);
        Task task2 = new Task(2, "Task", "Desc", null);

        assertNotEquals(task1.hashCode(), task2.hashCode());
    }

    @Test
    void toString_containsRelevantInfo() {
        Category category = new Category(1, "Work");
        Task task = new Task(42, "My Task", "Description", category);

        String result = task.toString();

        assertTrue(result.contains("42"));
        assertTrue(result.contains("My Task"));
    }
}
