package it.unimib.taskmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory repository for Task entities.
 *
 * Responsibilities:
 * - Assign a unique id to each new Task.
 * - Store tasks in memory using a HashMap.
 * - Provide CRUD operations for Task.
 *
 * This repository simulates a storage layer (like a database),
 * but everything is held in memory for the duration of execution.
 */
public class TaskRepository {

    /**
     * Internal storage of tasks, keyed by their id.
     */
    private final Map<Integer, Task> tasks = new HashMap<>();

    /**
     * Next id to assign when creating a new task.
     */
    private int nextId = 1;

    /**
     * Creates a new Task with a generated id and the provided data.
     * The new Task is then stored in the internal map.
     *
     * @param title       short title of the task
     * @param description optional description
     * @param category    category assigned to this task
     * @return the newly created Task instance
     */
    public Task create(String title, String description, Category category) {
        Task task = new Task(nextId, title, description, category);
        tasks.put(nextId, task);
        nextId++;

        return task;
    }

    /**
     * Returns a snapshot list of all stored tasks.
     * The returned list is a copy, so external modifications
     * do not impact the internal repository state.
     *
     * @return list of all tasks
     */
    public List<Task> findAll() {
        return new ArrayList<>(tasks.values());
    }

    /**
     * Finds a task by its id.
     *
     * @param id identifier of the task
     * @return Optional containing the task if found, or empty otherwise
     */
    public Optional<Task> findById(int id) {
        return Optional.ofNullable(tasks.get(id));
    }

    /**
     * Finds all tasks whose title contains the given text, ignoring case.
     *
     * @param text part of the title to search for
     * @return list of tasks whose title contains the given text
     */
    public List<Task> findByTitle(String text) {
        List<Task> result = new ArrayList<>();

        if (text == null || text.isEmpty()) {
            return result;
        }

        String normalized = text.toLowerCase();

        for (Task task : tasks.values()) {
            String title = task.getTitle();

            if (title != null && title.toLowerCase().contains(normalized)) {
                result.add(task);
            }
        }

        return result;
    }

    /**
     * Deletes a task by its id.
     *
     * @param id identifier of the task to remove
     * @return true if the task was successfully removed
     */
    public boolean deleteById(int id) {
        return tasks.remove(id) != null;
    }

    /**
     * Removes all tasks and resets the id counter.
     * Useful for unit tests where a clean state is needed.
     */
    public void clear() {
        tasks.clear();
        nextId = 1;
    }
}
