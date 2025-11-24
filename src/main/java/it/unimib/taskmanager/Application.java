package it.unimib.taskmanager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Central application logic for the Task Manager CLI.
 *
 * This class coordinates:
 * - the CategoryRepository (categories CRUD)
 * - the TaskRepository (tasks CRUD)
 *
 * The CLI (Main) will call this class instead of talking directly
 * to repositories, so the user interface stays simple.
 */
public class Application {

    /**
     * Repository used to manage Category entities.
     */
    private final CategoryRepository categoryRepository;

    /**
     * Repository used to manage Task entities.
     */
    private final TaskRepository taskRepository;

    /**
     * Creates a new Application instance with the given repositories.
     *
     * @param categoryRepository repository for categories
     * @param taskRepository     repository for tasks
     */
    public Application(CategoryRepository categoryRepository,
                       TaskRepository taskRepository) {
        this.categoryRepository = categoryRepository;
        this.taskRepository = taskRepository;
    }

    /**
     * Convenience factory method that creates an Application
     * with fresh in-memory repositories.
     *
     * This is what the CLI (Main) will normally use.
     *
     * @return a new Application instance with default repositories
     */
    public static Application createDefault() {
        return new Application(new CategoryRepository(), new TaskRepository());
    }

    // ---------------------------------------------------------------------
    // CATEGORY CRUD
    // ---------------------------------------------------------------------

    /**
     * Creates a new category with the given name.
     *
     * If a category with the same name already exists, this method returns null
     * so that the CLI can inform the user.
     *
     * @param name name of the category (for example "Work", "University")
     * @return the created Category, or null if the name is already used
     */
    public Category createCategory(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null; // invalid name
        }

        if (categoryRepository.existsByName(name.trim())) {
            return null; // duplicated name
        }

        return categoryRepository.create(name.trim());
    }

    /**
     * Returns all categories currently stored in the system.
     *
     * @return list of all categories
     */
    public List<Category> listCategories() {
        return categoryRepository.findAll();
    }

    /**
     * Searches categories whose name contains the given text (case-insensitive).
     *
     * @param text text to search in the category name
     * @return list of categories matching the search
     */
    public List<Category> searchCategoriesByName(String text) {
        return categoryRepository.findByName(text);
    }

    /**
     * Renames an existing category.
     *
     * @param categoryId id of the category to rename
     * @param newName    new name for the category
     * @return true if the category was found and renamed, false otherwise
     */
    public boolean renameCategory(int categoryId, String newName) {
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);

        if (categoryOpt.isEmpty()) {
            return false;
        }

        Category category = categoryOpt.get();
        category.setName(newName);

        return true;
    }

    /**
     * Deletes a category by id.
     *
     * After deleting the category, all tasks that were using this category
     * will have their category set to null. This avoids dangling references
     * to a non-existing category.
     *
     * @param categoryId id of the category to delete
     * @return true if a category was deleted, false if the id was not found
     */
    public boolean deleteCategory(int categoryId) {
        boolean deleted = categoryRepository.deleteById(categoryId);

        if (!deleted) {
            return false;
        }

        // Detach the category from all tasks that were using it
        for (Task task : taskRepository.findAll()) {
            Category category = task.getCategory();

            if (category != null && category.getId() == categoryId) {
                task.setCategory(null);
            }
        }

        return true;
    }


    // ---------------------------------------------------------------------
    // TASK CRUD
    // ---------------------------------------------------------------------

    /**
     * Creates a new task.
     *
     * If the category id does not exist, this method returns null.
     * The CLI can check for null and show an error message to the user.
     *
     * @param title       task title
     * @param description task description (may be empty)
     * @param categoryId  id of an existing category
     * @return the created Task, or null if the category id is invalid
     */
    public Task createTask(String title, String description, int categoryId) {
        if (title == null || title.trim().isEmpty()) {
            return null; // invalid title
        }

        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);

        if (categoryOpt.isEmpty()) {
            return null; // invalid category
        }

        Category category = categoryOpt.get();
        String safeDescription = (description == null) ? "" : description.trim();

        return taskRepository.create(title.trim(), safeDescription, category);
    }

    /**
     * Returns all tasks currently stored in the system.
     *
     * @return list of all tasks
     */
    public List<Task> listTasks() {
        return taskRepository.findAll();
    }

    /**
     * Searches tasks whose title contains the given text (case-insensitive).
     *
     * @param text text to search in the task title
     * @return list of tasks matching the search
     */
    public List<Task> searchTasksByTitle(String text) {
        return taskRepository.findByTitle(text);
    }

    /**
     * Returns all tasks that belong to a given category.
     *
     * @param categoryId id of the category
     * @return list of tasks that have this category
     */
    public List<Task> listTasksByCategory(int categoryId) {
        List<Task> result = new ArrayList<>();

        for (Task task : taskRepository.findAll()) {
            Category category = task.getCategory();

            if (category != null && category.getId() == categoryId) {
                result.add(task);
            }
        }

        return result;
    }

    /**
     * Returns all tasks that match the given done status.
     *
     * @param done if true returns only completed tasks, if false only pending tasks
     * @return list of tasks matching the given status
     */
    public List<Task> listTasksByDoneStatus(boolean done) {
        List<Task> result = new ArrayList<>();

        for (Task task : taskRepository.findAll()) {
            if (task.isDone() == done) {
                result.add(task);
            }
        }

        return result;
    }

    /**
     * Updates the title of an existing task.
     *
     * @param taskId   id of the task to update
     * @param newTitle new title to set
     * @return true if the task was found and updated, false otherwise
     */
    public boolean updateTaskTitle(int taskId, String newTitle) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);

        if (taskOpt.isEmpty()) {
            return false;
        }

        Task task = taskOpt.get();
        task.setTitle(newTitle);

        return true;
    }

    /**
     * Updates the description of an existing task.
     *
     * @param taskId         id of the task to update
     * @param newDescription new description to set
     * @return true if the task was found and updated, false otherwise
     */
    public boolean updateTaskDescription(int taskId, String newDescription) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);

        if (taskOpt.isEmpty()) {
            return false;
        }

        Task task = taskOpt.get();
        task.setDescription(newDescription);

        return true;
    }

    /**
     * Updates the category of an existing task.
     *
     * @param taskId      id of the task to update
     * @param newCategoryId id of the new category
     * @return true if both task and category exist and the update was applied
     */
    public boolean updateTaskCategory(int taskId, int newCategoryId) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        Optional<Category> categoryOpt = categoryRepository.findById(newCategoryId);

        if (taskOpt.isEmpty() || categoryOpt.isEmpty()) {
            return false;
        }

        Task task = taskOpt.get();
        Category newCategory = categoryOpt.get();
        task.setCategory(newCategory);

        return true;
    }

    /**
     * Marks a task as done.
     *
     * @param taskId id of the task to mark as done
     * @return true if the task was found and updated, false otherwise
     */
    public boolean markTaskDone(int taskId) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);

        if (taskOpt.isEmpty()) {
            return false;
        }

        Task task = taskOpt.get();
        task.markDone();

        return true;
    }

    /**
     * Marks a task as pending (not done).
     *
     * @param taskId id of the task to mark as pending
     * @return true if the task was found and updated, false otherwise
     */
    public boolean markTaskPending(int taskId) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);

        if (taskOpt.isEmpty()) {
            return false;
        }

        Task task = taskOpt.get();
        task.markPending();

        return true;
    }

    /**
     * Deletes a task by id.
     *
     * @param taskId id of the task to delete
     * @return true if a task was deleted, false if the id was not found
     */
    public boolean deleteTask(int taskId) {
        return taskRepository.deleteById(taskId);
    }
}
