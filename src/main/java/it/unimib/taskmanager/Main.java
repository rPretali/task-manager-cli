package it.unimib.taskmanager;

import java.util.List;
import java.util.Scanner;

/**
 * Entry point and CLI for the Task Manager application.
 *
 * Responsibilities:
 * - Show menus to the user
 * - Read user input from the console
 * - Call the Application methods
 *
 * All business logic is delegated to the Application class.
 */
public class Main {

    public static void main(String[] args) {
        Application app = Application.createDefault();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to Task Manager CLI");

        boolean exit = false;
        while (!exit) {
            printMainMenu();
            int choice = readInt(scanner, "\nChoose an option: ");

            switch (choice) {
                case 1:
                    handleCategoryMenu(app, scanner);
                    break;
                case 2:
                    handleTaskMenu(app, scanner);
                    break;
                case 0:
                    exit = true;
                    System.out.println("\nExiting.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

        scanner.close();
    }

    /**
     * Prints the main menu options.
     */
    private static void printMainMenu() {
        System.out.println();
        System.out.println("---------------------------------------");
        System.out.println(" Main menu");
        System.out.println("---------------------------------------");
        System.out.println("1. Manage categories");
        System.out.println("2. Manage tasks");
        System.out.println("0. Exit");
        System.out.println("---------------------------------------");
    }

    /**
     * Handles the category sub-menu and related operations.
     *
     * @param app     application logic
     * @param scanner scanner for user input
     */
    private static void handleCategoryMenu(Application app, Scanner scanner) {
        boolean back = false;

        while (!back) {
            printCategoryMenu();
            int choice = readInt(scanner, "\nChoose an option: ");

            switch (choice) {
                case 1:
                    listCategories(app);
                    break;
                case 2:
                    createCategory(app, scanner);
                    break;
                case 3:
                    searchCategoriesByName(app, scanner);
                    break;
                case 4:
                    renameCategory(app, scanner);
                    break;
                case 5:
                    deleteCategory(app, scanner);
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    /**
     * Prints the category sub-menu options.
     */
    private static void printCategoryMenu() {
        System.out.println();
        System.out.println("--- Category menu ---");
        System.out.println("1. List categories");
        System.out.println("2. Create category");
        System.out.println("3. Search categories by name");
        System.out.println("4. Rename category");
        System.out.println("5. Delete category");
        System.out.println("0. Back to main menu");
    }

    /**
     * Handles the task sub-menu and related operations.
     *
     * @param app     application logic
     * @param scanner scanner for user input
     */
    private static void handleTaskMenu(Application app, Scanner scanner) {
        boolean back = false;

        while (!back) {
            printTaskMenu();
            int choice = readInt(scanner, "\nChoose an option: ");

            switch (choice) {
                case 1:
                    listTasks(app);
                    break;
                case 2:
                    createTask(app, scanner);
                    break;
                case 3:
                    searchTasksByTitle(app, scanner);
                    break;
                case 4:
                    filterTasksByCategory(app, scanner);
                    break;
                case 5:
                    filterTasksByStatus(app, scanner);
                    break;
                case 6:
                    modifyTask(app, scanner);
                    break;
                case 7:
                    markTaskDone(app, scanner);
                    break;
                case 8:
                    markTaskPending(app, scanner);
                    break;
                case 9:
                    deleteTask(app, scanner);
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    /**
     * Prints the task sub-menu options.
     */
    private static void printTaskMenu() {
        System.out.println();
        System.out.println("--- Task menu ---");
        System.out.println("1. List all tasks");
        System.out.println("2. Create task");
        System.out.println("3. Search tasks by title");
        System.out.println("4. Filter tasks by category");
        System.out.println("5. Filter tasks by status (done/pending)");
        System.out.println("6. Modify task (title / description / category)");
        System.out.println("7. Mark task as done");
        System.out.println("8. Mark task as pending");
        System.out.println("9. Delete task");
        System.out.println("0. Back to main menu");
    }

    // ------------------------------------------------------------------------
    // Helpers: safe input and simple printing
    // ------------------------------------------------------------------------

    /**
     * Reads an integer from the user, showing the given prompt.
     * If the user enters invalid input, it will ask again.
     *
     * @param scanner scanner to read from
     * @param prompt  text to show to the user
     * @return integer entered by the user
     */
    private static int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                String line = scanner.nextLine();

                return Integer.parseInt(line.trim());
            } catch (NumberFormatException ex) {
                System.out.println("Invalid number. Please try again.");
            }
        }
    }

    /**
     * Reads a non-null line of text from the user.
     *
     * @param scanner scanner to read from
     * @param prompt  text to show to the user
     * @return the line entered by the user (may be empty)
     */
    private static String readLine(Scanner scanner, String prompt) {
        System.out.print(prompt);

        return scanner.nextLine();
    }

    private static void printCategories(List<Category> categories) {
        if (categories.isEmpty()) {
            System.out.println("No categories found.");

            return;
        }

        System.out.println("Categories:");

        for (Category category : categories) {
            System.out.println("[" + category.getId() + "] " + category.getName());
        }
    }

    private static void printTasks(List<Task> tasks) {
        if (tasks.isEmpty()) {
            System.out.println("No tasks found.\n");

            return;
        }

        System.out.println("Tasks:");

        for (Task task : tasks) {
            String categoryName = (task.getCategory() != null)
                    ? task.getCategory().getName()
                    : "(no category)";

            System.out.println("[" + task.getId() + "] " + task.getTitle());
            String description = task.getDescription();

            if (description != null && !description.isBlank()) {
                System.out.println("    description: " + description);
            }

            System.out.println("    category: " + categoryName + " | done: " + task.isDone() + "\n");
        }
    }

    // ------------------------------------------------------------------------
    // Category actions
    // ------------------------------------------------------------------------

    private static void listCategories(Application app) {
        List<Category> categories = app.listCategories();
        printCategories(categories);
    }

    private static void createCategory(Application app, Scanner scanner) {
        String name = readLine(scanner, "\nEnter category name: ");
        Category category = app.createCategory(name);

        if (category == null) {
            System.out.println("Could not create category. Name may be invalid or already used.");
        } else {
            System.out.println("Category created with id " + category.getId());
        }
    }

    private static void searchCategoriesByName(Application app, Scanner scanner) {
        String text = readLine(scanner, "\nEnter text to search in category names: ");
        List<Category> categories = app.searchCategoriesByName(text);
        printCategories(categories);
    }

    private static void renameCategory(Application app, Scanner scanner) {
        listCategories(app);
        int id = readInt(scanner, "\nEnter category id to rename: ");
        String newName = readLine(scanner, "\nEnter new category name: ");
        boolean result = app.renameCategory(id, newName);

        if (result) {
            System.out.println("Category renamed.");
        } else {
            System.out.println("Category not found or name invalid.");
        }
    }

    private static void deleteCategory(Application app, Scanner scanner) {
        listCategories(app);
        int id = readInt(scanner, "\nEnter category id to delete: ");
        boolean result = app.deleteCategory(id);

        if (result) {
            System.out.println("Category deleted. Tasks using this category now have no category.");
        } else {
            System.out.println("Category not found.");
        }
    }

    // ------------------------------------------------------------------------
    // Task actions
    // ------------------------------------------------------------------------

    private static void listTasks(Application app) {
        List<Task> tasks = app.listTasks();
        printTasks(tasks);
    }

    private static void createTask(Application app, Scanner scanner) {
        String title = readLine(scanner, "\nEnter task title: ");
        if (title.trim().isEmpty()) {
            System.out.println("Invalid title. Task not created.\n");

            return;
        }

        String description = readLine(scanner, "\nEnter task description (optional): ");

        // Check categories before asking for an id
        List<Category> categories = app.listCategories();
        if (categories.isEmpty()) {
            System.out.println("No categories available. Create a category first.\n");

            return;
        }

        printCategories(categories);
        int categoryId = readInt(scanner, "\nEnter category id: ");

        Task task = app.createTask(title, description, categoryId);
        if (task == null) {
            System.out.println("Could not create task. Invalid category or invalid title.\n");
        } else {
            System.out.println("Task created with id " + task.getId() + "\n");
        }
    }

    private static void searchTasksByTitle(Application app, Scanner scanner) {
        String text = readLine(scanner, "\nEnter text to search in task titles: ");
        List<Task> tasks = app.searchTasksByTitle(text);
        printTasks(tasks);
    }

    private static void filterTasksByCategory(Application app, Scanner scanner) {
        List<Category> categories = app.listCategories();

        if (categories.isEmpty()) {
            System.out.println("No categories available. Create a category first.\n");

            return;
        }

        printCategories(categories);
        int categoryId = readInt(scanner, "\nEnter category id to filter tasks: ");

        List<Task> tasks = app.listTasksByCategory(categoryId);
        printTasks(tasks);
    }

    private static void filterTasksByStatus(Application app, Scanner scanner) {
        System.out.println("Filter by status:");
        System.out.println("1. Done");
        System.out.println("2. Pending");

        int choice = readInt(scanner, "\nChoose an option: ");
        boolean done;

        if (choice == 1) {
            done = true;
        } else if (choice == 2) {
            done = false;
        } else {
            System.out.println("Invalid choice. Returning to task menu.\n");

            return;
        }

        List<Task> tasks = app.listTasksByDoneStatus(done);
        printTasks(tasks);
    }

    private static void modifyTask(Application app, Scanner scanner) {
        boolean back = false;

        while (!back) {
            System.out.println();
            System.out.println("--- Modify task ---");
            System.out.println("1. Change title");
            System.out.println("2. Change description");
            System.out.println("3. Change category");
            System.out.println("0. Back");

            int choice = readInt(scanner, "\nChoose an option: ");

            switch (choice) {
                case 1:
                    changeTaskTitle(app, scanner);
                    break;
                case 2:
                    changeTaskDescription(app, scanner);
                    break;
                case 3:
                    changeTaskCategory(app, scanner);
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void markTaskDone(Application app, Scanner scanner) {
        listTasks(app);
        int taskId = readInt(scanner, "\nEnter task id to mark as done: ");

        boolean result = app.markTaskDone(taskId);

        if (result) {
            System.out.println("Task marked as done.\n");
        } else {
            System.out.println("Task not found.\n");
        }
    }

    private static void markTaskPending(Application app, Scanner scanner) {
        listTasks(app);
        int taskId = readInt(scanner, "\nEnter task id to mark as pending: ");

        boolean result = app.markTaskPending(taskId);

        if (result) {
            System.out.println("Task marked as pending.\n");
        } else {
            System.out.println("Task not found.\n");
        }
    }

    private static void deleteTask(Application app, Scanner scanner) {
        listTasks(app);
        int taskId = readInt(scanner, "Enter task id to delete: ");

        boolean result = app.deleteTask(taskId);

        if (result) {
            System.out.println("Task deleted.\n");
        } else {
            System.out.println("Task not found.\n");
        }
    }

    private static void changeTaskTitle(Application app, Scanner scanner) {
        listTasks(app);
        int id = readInt(scanner, "\nEnter task id to modify title: ");
        String newTitle = readLine(scanner, "\nEnter new title: ");

        if (newTitle.trim().isEmpty()) {
            System.out.println("Invalid title. Operation cancelled.");
            System.out.println();

            return;
        }

        boolean result = app.updateTaskTitle(id, newTitle);

        if (result) {
            System.out.println("Title updated.\n");
        } else {
            System.out.println("Task not found.\n");
        }
    }

    private static void changeTaskDescription(Application app, Scanner scanner) {
        listTasks(app);
        int id = readInt(scanner, "\nEnter task id to modify description: ");
        String newDesc = readLine(scanner, "\nEnter new description (optional): ");

        boolean result = app.updateTaskDescription(id, newDesc);

        if (result) {
            System.out.println("Description updated.\n");
        } else {
            System.out.println("Task not found.\n");
        }
    }

    private static void changeTaskCategory(Application app, Scanner scanner) {
        listTasks(app);
        int taskId = readInt(scanner, "\nEnter task id to modify category: ");

        List<Category> categories = app.listCategories();

        if (categories.isEmpty()) {
            System.out.println("No categories available. Create a category first.\n");

            return;
        }

        printCategories(categories);
        int categoryId = readInt(scanner, "\nEnter new category id: ");

        boolean result = app.updateTaskCategory(taskId, categoryId);

        if (result) {
            System.out.println("Task category updated successfully.\n");
        } else {
            System.out.println("Task or category not found.\n");
        }
    }
}
