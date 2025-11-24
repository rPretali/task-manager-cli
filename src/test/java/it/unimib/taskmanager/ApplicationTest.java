package it.unimib.taskmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationTest {

    private Application application;

    @BeforeEach
    void setUp() {
        application = Application.createDefault();
    }

    // ---------------------------------------------------------------------
    // CATEGORIES
    // ---------------------------------------------------------------------

    @Test
    void createCategory_withValidName_returnsCategory() {
        Category category = application.createCategory("Work");

        assertNotNull(category);
        assertTrue(category.getId() > 0);
        assertEquals("Work", category.getName());
        assertEquals(1, application.listCategories().size());
    }

    @Test
    void createCategory_withBlankName_returnsNull() {
        Category category = application.createCategory("   ");

        assertNull(category);
        assertTrue(application.listCategories().isEmpty());
    }

    @Test
    void createCategory_withDuplicateName_returnsNullAndKeepsSingleCategory() {
        Category first = application.createCategory("University");
        assertNotNull(first);

        Category duplicate = application.createCategory("University");
        assertNull(duplicate);

        List<Category> categories = application.listCategories();
        assertEquals(1, categories.size());
        assertEquals("University", categories.get(0).getName());
    }

    @Test
    void deleteCategory_removesCategoryAndDetachesFromTasks() {
        Category cat = application.createCategory("Personal");
        assertNotNull(cat);

        Task task = application.createTask("Buy milk", "Remember to buy milk", cat.getId());
        assertNotNull(task);
        assertNotNull(task.getCategory());

        boolean deleted = application.deleteCategory(cat.getId());
        assertTrue(deleted);

        assertTrue(application.listCategories().isEmpty());

        List<Task> tasks = application.listTasks();
        assertEquals(1, tasks.size());
        assertNull(tasks.get(0).getCategory());
    }

    // ---------------------------------------------------------------------
    // TASKS
    // ---------------------------------------------------------------------

    @Test
    void createTask_withValidCategory_returnsTask() {
        Category cat = application.createCategory("Uni");
        assertNotNull(cat);

        Task task = application.createTask("Study", "Processo e Sviluppo del Software", cat.getId());

        assertNotNull(task);
        assertEquals("Study", task.getTitle());
        assertEquals("Processo e Sviluppo del Software", task.getDescription());
        assertEquals(cat.getId(), task.getCategory().getId());

        List<Task> tasks = application.listTasks();
        assertEquals(1, tasks.size());
    }

    @Test
    void createTask_withInvalidCategory_returnsNull() {
        Task task = application.createTask("Orphan", "Invalid category", 9999);

        assertNull(task);
        assertTrue(application.listTasks().isEmpty());
    }

    @Test
    void updateTaskTitle_andDescription_changeValues() {
        Category cat = application.createCategory("Work");
        Task task = application.createTask("Old title", "Old description", cat.getId());
        int id = task.getId();

        boolean titleUpdated = application.updateTaskTitle(id, "New title");
        boolean descriptionUpdated = application.updateTaskDescription(id, "New description");

        assertTrue(titleUpdated);
        assertTrue(descriptionUpdated);

        Task updated = application.listTasks().get(0);
        assertEquals("New title", updated.getTitle());
        assertEquals("New description", updated.getDescription());
    }

    @Test
    void markTaskDone_andThenPending_togglesDoneFlag() {
        Category cat = application.createCategory("Uni");
        Task task = application.createTask("Study", "Read slides", cat.getId());
        int id = task.getId();

        assertFalse(task.isDone());

        boolean markedDone = application.markTaskDone(id);
        assertTrue(markedDone);
        Task afterDone = application.listTasks().get(0);
        assertTrue(afterDone.isDone());

        boolean markedPending = application.markTaskPending(id);
        assertTrue(markedPending);
        Task afterPending = application.listTasks().get(0);
        assertFalse(afterPending.isDone());
    }

    @Test
    void deleteTask_removesTaskFromList() {
        Category cat = application.createCategory("Misc");
        Task t1 = application.createTask("One", "First task", cat.getId());
        Task t2 = application.createTask("Two", "Second task", cat.getId());

        assertEquals(2, application.listTasks().size());

        boolean deleted = application.deleteTask(t1.getId());
        assertTrue(deleted);

        List<Task> tasks = application.listTasks();
        assertEquals(1, tasks.size());
        assertEquals(t2.getId(), tasks.get(0).getId());
    }
}
