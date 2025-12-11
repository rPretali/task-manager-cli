package it.unimib.taskmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the TaskRepository.
 * These tests verify the CRUD operations for tasks.
 */
class TaskRepositoryTest {

    private TaskRepository repository;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        repository = new TaskRepository();
        testCategory = new Category(1, "Test Category");
    }

    // ------------------------------------------------------------------------
    // CREATE
    // ------------------------------------------------------------------------

    @Test
    void create_assignsIncrementalId() {
        Task task1 = repository.create("First", "Desc", testCategory);
        Task task2 = repository.create("Second", "Desc", testCategory);
        Task task3 = repository.create("Third", "Desc", testCategory);

        assertEquals(1, task1.getId());
        assertEquals(2, task2.getId());
        assertEquals(3, task3.getId());
    }

    @Test
    void create_setsAllFields() {
        Task task = repository.create("Title", "Description", testCategory);

        assertEquals("Title", task.getTitle());
        assertEquals("Description", task.getDescription());
        assertEquals(testCategory, task.getCategory());
        assertFalse(task.isDone());
    }

    @Test
    void create_storesTask() {
        repository.create("Task", "Desc", testCategory);

        assertEquals(1, repository.findAll().size());
    }

    @Test
    void create_withNullCategory_createsTask() {
        Task task = repository.create("Task", "Desc", null);

        assertNotNull(task);
        assertNull(task.getCategory());
    }

    // ------------------------------------------------------------------------
    // FIND ALL
    // ------------------------------------------------------------------------

    @Test
    void findAll_emptyRepository_returnsEmptyList() {
        List<Task> all = repository.findAll();

        assertTrue(all.isEmpty());
    }

    @Test
    void findAll_returnsAllTasks() {
        repository.create("Task1", "Desc", testCategory);
        repository.create("Task2", "Desc", testCategory);
        repository.create("Task3", "Desc", testCategory);

        List<Task> all = repository.findAll();

        assertEquals(3, all.size());
    }

    @Test
    void findAll_returnsCopy_modificationDoesNotAffectRepository() {
        repository.create("Task", "Desc", testCategory);
        List<Task> all = repository.findAll();

        all.clear();

        assertEquals(1, repository.findAll().size());
    }

    // ------------------------------------------------------------------------
    // FIND BY ID
    // ------------------------------------------------------------------------

    @Test
    void findById_existingId_returnsTask() {
        Task created = repository.create("Task", "Desc", testCategory);

        Optional<Task> found = repository.findById(created.getId());

        assertTrue(found.isPresent());
        assertEquals("Task", found.get().getTitle());
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {
        repository.create("Task", "Desc", testCategory);

        Optional<Task> found = repository.findById(999);

        assertTrue(found.isEmpty());
    }

    @Test
    void findById_emptyRepository_returnsEmpty() {
        Optional<Task> found = repository.findById(1);

        assertTrue(found.isEmpty());
    }

    // ------------------------------------------------------------------------
    // FIND BY TITLE
    // ------------------------------------------------------------------------

    @Test
    void findByTitle_matchingText_returnsTasks() {
        repository.create("Buy milk", "Desc", testCategory);
        repository.create("Buy bread", "Desc", testCategory);
        repository.create("Study", "Desc", testCategory);

        List<Task> found = repository.findByTitle("buy");

        assertEquals(2, found.size());
    }

    @Test
    void findByTitle_caseInsensitive() {
        repository.create("BUY MILK", "Desc", testCategory);

        List<Task> found = repository.findByTitle("buy milk");

        assertEquals(1, found.size());
    }

    @Test
    void findByTitle_noMatch_returnsEmptyList() {
        repository.create("Task", "Desc", testCategory);

        List<Task> found = repository.findByTitle("xyz");

        assertTrue(found.isEmpty());
    }

    @Test
    void findByTitle_nullText_returnsEmptyList() {
        repository.create("Task", "Desc", testCategory);

        List<Task> found = repository.findByTitle(null);

        assertTrue(found.isEmpty());
    }

    @Test
    void findByTitle_emptyText_returnsEmptyList() {
        repository.create("Task", "Desc", testCategory);

        List<Task> found = repository.findByTitle("");

        assertTrue(found.isEmpty());
    }

    @Test
    void findByTitle_partialMatch_returnsTasks() {
        repository.create("Meeting with team", "Desc", testCategory);

        List<Task> found = repository.findByTitle("team");

        assertEquals(1, found.size());
    }

    // ------------------------------------------------------------------------
    // DELETE
    // ------------------------------------------------------------------------

    @Test
    void deleteById_existingId_removesAndReturnsTrue() {
        Task task = repository.create("Task", "Desc", testCategory);

        boolean deleted = repository.deleteById(task.getId());

        assertTrue(deleted);
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void deleteById_nonExistingId_returnsFalse() {
        repository.create("Task", "Desc", testCategory);

        boolean deleted = repository.deleteById(999);

        assertFalse(deleted);
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void deleteById_emptyRepository_returnsFalse() {
        boolean deleted = repository.deleteById(1);

        assertFalse(deleted);
    }

    @Test
    void deleteById_deletesOnlySpecifiedTask() {
        Task task1 = repository.create("Task1", "Desc", testCategory);
        Task task2 = repository.create("Task2", "Desc", testCategory);

        repository.deleteById(task1.getId());

        assertEquals(1, repository.findAll().size());
        assertEquals(task2.getId(), repository.findAll().get(0).getId());
    }

    // ------------------------------------------------------------------------
    // CLEAR
    // ------------------------------------------------------------------------

    @Test
    void clear_removesAllTasks() {
        repository.create("Task1", "Desc", testCategory);
        repository.create("Task2", "Desc", testCategory);

        repository.clear();

        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void clear_resetsIdCounter() {
        repository.create("Task1", "Desc", testCategory);
        repository.create("Task2", "Desc", testCategory);
        repository.clear();

        Task newTask = repository.create("New", "Desc", testCategory);

        assertEquals(1, newTask.getId());
    }
}
