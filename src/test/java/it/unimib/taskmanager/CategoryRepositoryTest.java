package it.unimib.taskmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the CategoryRepository.
 * These tests verify the CRUD operations for categories.
 */
class CategoryRepositoryTest {

    private CategoryRepository repository;

    @BeforeEach
    void setUp() {
        repository = new CategoryRepository();
    }

    // ------------------------------------------------------------------------
    // CREATE
    // ------------------------------------------------------------------------

    @Test
    void create_assignsIncrementalId() {
        Category cat1 = repository.create("First");
        Category cat2 = repository.create("Second");
        Category cat3 = repository.create("Third");

        assertEquals(1, cat1.getId());
        assertEquals(2, cat2.getId());
        assertEquals(3, cat3.getId());
    }

    @Test
    void create_storesCategory() {
        repository.create("Work");

        List<Category> all = repository.findAll();
        assertEquals(1, all.size());
        assertEquals("Work", all.get(0).getName());
    }

    @Test
    void create_withEmptyName_stillCreates() {
        Category category = repository.create("");

        assertNotNull(category);
        assertEquals("", category.getName());
    }

    // ------------------------------------------------------------------------
    // FIND ALL
    // ------------------------------------------------------------------------

    @Test
    void findAll_emptyRepository_returnsEmptyList() {
        List<Category> all = repository.findAll();

        assertTrue(all.isEmpty());
    }

    @Test
    void findAll_returnsAllCategories() {
        repository.create("Work");
        repository.create("Personal");
        repository.create("Shopping");

        List<Category> all = repository.findAll();

        assertEquals(3, all.size());
    }

    @Test
    void findAll_returnsCopy_modificationDoesNotAffectRepository() {
        repository.create("Work");
        List<Category> all = repository.findAll();

        all.clear();

        assertEquals(1, repository.findAll().size());
    }

    // ------------------------------------------------------------------------
    // FIND BY ID
    // ------------------------------------------------------------------------

    @Test
    void findById_existingId_returnsCategory() {
        Category created = repository.create("Work");

        Optional<Category> found = repository.findById(created.getId());

        assertTrue(found.isPresent());
        assertEquals("Work", found.get().getName());
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {
        repository.create("Work");

        Optional<Category> found = repository.findById(999);

        assertTrue(found.isEmpty());
    }

    @Test
    void findById_emptyRepository_returnsEmpty() {
        Optional<Category> found = repository.findById(1);

        assertTrue(found.isEmpty());
    }

    // ------------------------------------------------------------------------
    // FIND BY NAME
    // ------------------------------------------------------------------------

    @Test
    void findByName_matchingText_returnsCategories() {
        repository.create("Work");
        repository.create("Homework");
        repository.create("Personal");

        List<Category> found = repository.findByName("work");

        assertEquals(2, found.size());
    }

    @Test
    void findByName_caseInsensitive() {
        repository.create("WORK");

        List<Category> found = repository.findByName("work");

        assertEquals(1, found.size());
    }

    @Test
    void findByName_noMatch_returnsEmptyList() {
        repository.create("Work");

        List<Category> found = repository.findByName("xyz");

        assertTrue(found.isEmpty());
    }

    @Test
    void findByName_nullText_returnsEmptyList() {
        repository.create("Work");

        List<Category> found = repository.findByName(null);

        assertTrue(found.isEmpty());
    }

    @Test
    void findByName_emptyText_returnsEmptyList() {
        repository.create("Work");

        List<Category> found = repository.findByName("");

        assertTrue(found.isEmpty());
    }

    // ------------------------------------------------------------------------
    // DELETE
    // ------------------------------------------------------------------------

    @Test
    void deleteById_existingId_removesAndReturnsTrue() {
        Category cat = repository.create("Work");

        boolean deleted = repository.deleteById(cat.getId());

        assertTrue(deleted);
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void deleteById_nonExistingId_returnsFalse() {
        repository.create("Work");

        boolean deleted = repository.deleteById(999);

        assertFalse(deleted);
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void deleteById_emptyRepository_returnsFalse() {
        boolean deleted = repository.deleteById(1);

        assertFalse(deleted);
    }

    // ------------------------------------------------------------------------
    // EXISTS BY NAME
    // ------------------------------------------------------------------------

    @Test
    void existsByName_existingName_returnsTrue() {
        repository.create("Work");

        assertTrue(repository.existsByName("Work"));
    }

    @Test
    void existsByName_caseInsensitive() {
        repository.create("Work");

        assertTrue(repository.existsByName("WORK"));
        assertTrue(repository.existsByName("work"));
    }

    @Test
    void existsByName_nonExistingName_returnsFalse() {
        repository.create("Work");

        assertFalse(repository.existsByName("Personal"));
    }

    @Test
    void existsByName_emptyRepository_returnsFalse() {
        assertFalse(repository.existsByName("Work"));
    }

    // ------------------------------------------------------------------------
    // CLEAR
    // ------------------------------------------------------------------------

    @Test
    void clear_removesAllCategories() {
        repository.create("Work");
        repository.create("Personal");

        repository.clear();

        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void clear_resetsIdCounter() {
        repository.create("Work");
        repository.create("Personal");
        repository.clear();

        Category newCat = repository.create("New");

        assertEquals(1, newCat.getId());
    }
}
