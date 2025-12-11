package it.unimib.taskmanager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Category entity.
 * These tests verify the behavior of the Category class in isolation.
 */
class CategoryTest {

    @Test
    void constructor_setsIdAndName() {
        Category category = new Category(1, "Work");

        assertEquals(1, category.getId());
        assertEquals("Work", category.getName());
    }

    @Test
    void constructor_withEmptyName_createsCategory() {
        Category category = new Category(1, "");

        assertEquals(1, category.getId());
        assertEquals("", category.getName());
    }

    @Test
    void setName_updatesName() {
        Category category = new Category(1, "Old Name");

        category.setName("New Name");

        assertEquals("New Name", category.getName());
    }

    @Test
    void setName_toNull_setsNull() {
        Category category = new Category(1, "Work");

        category.setName(null);

        assertNull(category.getName());
    }

    @Test
    void getId_returnsImmutableId() {
        Category category = new Category(42, "Test");

        assertEquals(42, category.getId());
    }

    @Test
    void equals_sameCategoryInstance_returnsTrue() {
        Category category = new Category(1, "Work");

        assertEquals(category, category);
    }

    @Test
    void equals_sameCategoryId_returnsTrue() {
        Category cat1 = new Category(1, "Work");
        Category cat2 = new Category(1, "Different Name");

        assertEquals(cat1, cat2);
    }

    @Test
    void equals_differentCategoryId_returnsFalse() {
        Category cat1 = new Category(1, "Work");
        Category cat2 = new Category(2, "Work");

        assertNotEquals(cat1, cat2);
    }

    @Test
    void equals_withNull_returnsFalse() {
        Category category = new Category(1, "Work");

        assertNotEquals(null, category);
    }

    @Test
    void equals_withDifferentType_returnsFalse() {
        Category category = new Category(1, "Work");
        String notACategory = "Work";

        assertNotEquals(category, notACategory);
    }

    @Test
    void hashCode_sameId_sameHashCode() {
        Category cat1 = new Category(1, "Work");
        Category cat2 = new Category(1, "Other");

        assertEquals(cat1.hashCode(), cat2.hashCode());
    }

    @Test
    void hashCode_differentId_differentHashCode() {
        Category cat1 = new Category(1, "Work");
        Category cat2 = new Category(2, "Work");

        assertNotEquals(cat1.hashCode(), cat2.hashCode());
    }

    @Test
    void toString_containsIdAndName() {
        Category category = new Category(1, "Work");

        String result = category.toString();

        assertTrue(result.contains("1"));
        assertTrue(result.contains("Work"));
    }
}
