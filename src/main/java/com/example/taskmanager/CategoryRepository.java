package com.example.taskmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory repository for Category entities.
 *
 * Responsibilities:
 * - Assign a unique id to each new Category.
 * - Store categories in memory using a HashMap.
 * - Provide basic CRUD operations for Category.
 *
 * This repository simulates a persistence layer (like a database),
 * but it only keeps data in memory for the duration of the program.
 */
public class CategoryRepository {

    /**
     * Internal storage for categories, keyed by their id.
     */
    private final Map<Integer, Category> categories = new HashMap<>();

    /**
     * Next id to assign to a newly created category.
     * It is incremented every time a new category is created.
     */
    private int nextId = 1;

    /**
     * Creates a new Category with a generated id and the given name.
     * The new category is stored in the internal map and returned.
     *
     * @param name human readable name for the new category
     * @return the created Category instance
     */
    public Category create(String name) {
        Category category = new Category(nextId, name);
        categories.put(nextId, category);
        nextId++;

        return category;
    }

    /**
     * Returns a snapshot list of all stored categories.
     * The returned list is a copy, so external modifications
     * do not affect the internal state of the repository.
     *
     * @return list of all categories
     */
    public List<Category> findAll() {
        return new ArrayList<>(categories.values());
    }

    /**
     * Finds a category by its id.
     *
     * @param id identifier of the category
     * @return Optional containing the category if found, empty otherwise
     */
    public Optional<Category> findById(int id) {
        return Optional.ofNullable(categories.get(id));
    }

    /**
     * Finds all categories whose name contains the given text, ignoring case.
     *
     * @param text part of the category name to search for
     * @return list of categories whose name contains the given text
     */
    public List<Category> findByName(String text) {
        List<Category> result = new ArrayList<>();

        if (text == null || text.isEmpty()) {
            return result;
        }

        String normalized = text.toLowerCase();

        for (Category category : categories.values()) {
            String name = category.getName();

            if (name != null && name.toLowerCase().contains(normalized)) {
                result.add(category);
            }
        }

        return result;
    }

    /**
     * Deletes a category by its id.
     *
     * @param id identifier of the category to delete
     * @return true if a category was removed, false if the id was not found
     */
    public boolean deleteById(int id) {
        return categories.remove(id) != null;
    }

    /**
     * Checks if there is already a category with the given name.
     *
     * @param name name to look for
     * @return true if a category with this name already exists, false otherwise
     */
    public boolean existsByName(String name) {
        for (Category category : categories.values()) {
            if (category.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Removes all categories and resets the id counter.
     * Useful for unit tests where a clean state is needed.
     */
    public void clear() {
        categories.clear();
        nextId = 1;
    }
}
