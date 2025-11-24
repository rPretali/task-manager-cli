package it.unimib.taskmanager;

import java.util.Objects;

/**
 * Represents a single task in the TODO list.
 * A task has a title, an optional description, belongs to a category
 * and can be either done or not done.
 *
 * This entity is part of a CRUD:
 * - Create: create a new task
 * - Read: list or get existing tasks
 * - Update: change title, description, category or done status
 * - Delete: remove a task
 */
public class Task {

    /**
     * Unique identifier for the task.
     * It is assigned by the TaskRepository and never changes.
     */
    private final int id;

    /**
     * Short, human readable title for the task.
     */
    private String title;

    /**
     * Optional longer description with more details about the task.
     */
    private String description;

    /**
     * Category that this task belongs to.
     */
    private Category category;

    /**
     * Flag that indicates whether the task is completed or not.
     * false = pending (not done), true = done.
     */
    private boolean done;

    /**
     * Constructs a new Task.
     * By default, a new task is created in the pending state.
     *
     * @param id          unique identifier of the task
     * @param title       short title
     * @param description optional longer description
     * @param category    category that the task belongs to
     */
    public Task(int id, String title, String description, Category category) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.done = false;
    }

    /**
     * Returns the unique identifier of this task.
     *
     * @return task id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the current title of this task.
     *
     * @return task title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Updates the title of this task.
     *
     * @param title new title for the task
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the current description of this task.
     *
     * @return task description, may be null or empty
     */
    public String getDescription() {
        return description;
    }

    /**
     * Updates the description of this task.
     *
     * @param description new description for the task
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the category assigned to this task.
     *
     * @return category of the task
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Updates the category of this task.
     *
     * @param category new category for the task
     */
    public void setCategory(Category category) {
        this.category = category;
    }

    /**
     * Returns true if the task is completed, false otherwise.
     *
     * @return completion status
     */
    public boolean isDone() {
        return done;
    }

    /**
     * Marks the task as completed.
     */
    public void markDone() {
        this.done = true;
    }

    /**
     * Marks the task as not completed.
     */
    public void markPending() {
        this.done = false;
    }

    /**
     * Returns a string representation of this task.
     */
    @Override
    public String toString() {
        return "Task{" +
                "id =" + id +
                ", title =" + title +
                ", category =" + category +
                ", done =" + done +
                '}';
    }

    /**
     * Two Task objects are considered equal if they have the same id.
     *
     * @param o other object to compare
     * @return true if the other object is a Task with the same id
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Task)) {
            return false;
        }

        Task task = (Task) o;

        return id == task.id;
    }

    /**
     * Hash code based only on the id field.
     * This allows usage in hash-based collections.
     *
     * @return hash code for this task
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
