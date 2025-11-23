package it.unimib.taskmanager;

import java.util.Objects;

/**
 * A category is essentially a label such as "Work", "University", "Home", etc.
 *
 * This entity is part of a CRUD:
 * - Create: create a new category
 * - Read: list or get existing categories
 * - Update: rename a category
 * - Delete: remove a category
 */
public class Category {

    /**
     * Unique identifier for the category.
     * It is assigned by the CategoryRepository and never changes.
     */
    private final int id;

    /**
     * Human readable name of the category, for example "Work" or "University".
     */
    private String name;

    /**
     * Constructs a new Category with a given id and name.
     *
     * @param id   unique identifier of the category
     * @param name human readable name of the category
     */
    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Returns the unique identifier of this category.
     *
     * @return category id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the current name of this category.
     *
     * @return category name
     */
    public String getName() {
        return name;
    }

    /**
     * Updates the name of this category.
     *
     * @param name new name for the category
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns a string representation of this category.
     * Useful for logging and for showing information in the CLI.
     */
    @Override
    public String toString() {
        return "Category{" +
                "id =" + id +
                ", name =" + name +
                '}';
    }

    /**
     * Two Category objects are considered equal if they have the same id.
     * This allows us to compare categories safely and to use them in collections.
     *
     * @param o other object to compare
     * @return true if the other object is a Category with the same id
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Category)) {
            return false;
        }

        Category category = (Category) o;

        return id == category.id;
    }

    /**
     * Hash code based only on the id field.
     * This allows usage in hash-based collections.
     *
     * @return hash code for this category
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
