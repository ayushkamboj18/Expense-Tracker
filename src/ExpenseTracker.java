import java.io.*; // For handling file I/O
import java.text.NumberFormat; // For formatting numbers in currency format
import java.text.SimpleDateFormat; // For formatting date and time
import java.util.*; // For handling user input and data storage and manipulation.

public class ExpenseTracker {
    // Define file names for expenses and budgets files
    private static final String EXPENSES_FILE_NAME = "expenses.txt";
    private static final String BUDGETS_FILE_NAME = "budgets.txt"; // File to store budgets
    private static final double CONVERSION_RATE = 110.70; // USD to BDT conversion rate

    private static final Scanner userInput = new Scanner(System.in);
    private static List<Expense> expenses = new ArrayList<>(); // List to store expenses

    // Map to store category budgets for each category with default values set to 0
    private static Map<String, Double> categoryBudgets = new HashMap<>();

    public static void main(String[] args) {
        loadExpensesFromFile(); // Load expenses from file if available before starting the application.
        loadBudgetsFromFile(); // Load budgets from file if available before starting the application.

        // Start the application loop to handle user input and perform corresponding actions
        while (true) {
            displayMenu(); // Display the menu and get user input
            int choice = getUserChoice();

            switch (choice) { // Handle user input and perform corresponding actions
                case 1: // For Record an expense
                    recordExpense();
                    break;
                case 2: // For View expense summary
                    viewExpenseSummary();
                    break;
                case 3: // For Set budget
                    setBudget();
                    saveBudgetsToFile();
                    System.out.println("Budget set successfully.");
                    break;
                case 4: // For View budgets
                    viewAllBudgets();
                    break;
                case 5: // For View expense history
                    viewExpenseHistory();
                    break;
                case 6: // For Convert currency
                    convertCurrency();
                    break;
                case 7: // For Save and Load data
                    saveExpensesToFile();
                    System.out.println("Expenses saved successfully.");
                    break;
                case 8: // For Save and Load data
                    loadExpensesFromFile();
                    System.out.println("Expenses loaded successfully.");
                    break;
                case 9: // For Exit
                    saveExpensesToFile();
                    saveBudgetsToFile();
                    System.out.println("Exiting Expense Tracker. Goodbye!");
                    System.exit(0);
                default: // Invalid choice entered
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // Display a menu and get user input choice
    private static void displayMenu() {
        System.out.println("\n===== Expense Tracker Menu =====");
        System.out.println("1. Record an expense");
        System.out.println("2. View expense summary");
        System.out.println("3. Set budget");
        System.out.println("4. View budgets");
        System.out.println("5. View expense history");
        System.out.println("6. Convert currency");
        System.out.println("7. Save expenses");
        System.out.println("8. Load expenses");
        System.out.println("9. Exit");
        System.out.print("Enter your choice: ");
    }

    // Get user input choice and validate it as an integer
    private static int getUserChoice() {
        while (!userInput.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number.");
            userInput.next(); // consume the invalid input
        }
        return userInput.nextInt(); // return the valid integer
    }

    // Record an expense and save it to the expense list after validating the input values
    private static void recordExpense() {
        double amount = getValidDoubleInput("Enter the expense amount: ");
        userInput.nextLine(); // Consume the newline character

        System.out.print("Enter the expense category: ");
        String category = userInput.nextLine();

        System.out.print("Enter a brief description: ");
        String description = userInput.nextLine();

        // Validate the input values and add the expense to the list
        Expense expense = new Expense(amount, category, description);
        expenses.add(expense); // Add the expense to the list

        System.out.println("Expense recorded successfully.");
    }

    // View a summary of total spending and spending per category in the expense list
    private static void viewExpenseSummary() {

        // Check if there are any expenses recorded in the list and calculate
        // total spending and category-wise spending.
        if (expenses.isEmpty()) {
            System.out.println("No expenses recorded.");
        } else {

            // Calculate total spending and category-wise spending in the list
            double totalSpending = expenses.stream().mapToDouble(Expense::getAmount).sum();
            System.out.printf("Total Spending: $%.2f\n", totalSpending);

            // Create a map to store category-wise spending and display it in the list
            Map<String, Double> categorySpending = new HashMap<>();

            // Calculate category-wise spending in the list
            for (Expense expense : expenses) {

                // Check if the category exists in the map and add the expense amount to the map
                categorySpending.merge(expense.getCategory(), expense.getAmount(), Double::sum);
            }

            System.out.println("\nCategory-wise Spending:");
            categorySpending.forEach((category, spending) ->
                    System.out.printf("%s: $%.2f\n", category, spending));
        }
    }

    // Set a budget for a specific category and save it to the budget list
    private static void setBudget() {
        System.out.print("Enter the expense category to set a budget: ");

        // Convert to lowercase for case-insensitive comparison in the list of expenses
        String category = userInput.nextLine().toLowerCase();

        // Check if the category exists in the list of expenses and get the current budget
        if (categoryBudgets.containsKey(category)) {
            System.out.printf("Current budget for category '%s': $%.2f\n",
                    category, categoryBudgets.get(category));
        }

        double newBudget = getValidDoubleInput("Enter the new budget amount for the category: ");
        categoryBudgets.put(category, newBudget); // Add the new budget to the list of budgets

        System.out.println("Budget set successfully for the category: " + category);
    }

    // View all budgets saved in the budget list
    private static void viewAllBudgets() {

        // Check if there are any budgets saved in the list
        if (categoryBudgets.isEmpty()) {
            System.out.println("No budgets set yet.");
        } else { // Display all budgets in the list
            System.out.println("\n===== Expense Budgets =====");
            categoryBudgets.forEach((category, budget) ->
                    System.out.printf("Category: %s - Budget: $%.2f\n", category, budget));
        }
    }

    // Convert expenses from one currency to another using
    // the exchange rate of 1 USD to the target currency
    private static void convertCurrency() {

        // Check if there are any expenses recorded in the list
        if (expenses.isEmpty()) {
            System.out.println("No expenses recorded. Cannot perform currency conversion.");
            return;
        }

        userInput.nextLine(); // Consume any lingering newline characters

        boolean validCurrencyCode = false; // Flag to track if the currency code is valid
        Currency targetCurrency = null; // Variable to store the target currency

        // Get the target currency code from the user and validate it
        while (!validCurrencyCode) {
            System.out.print("Enter the target currency code (e.g., USD): ");
            String targetCurrencyCode = userInput.nextLine().toUpperCase();

            try {
                // Check if the currency code is valid and set the target currency variable
                targetCurrency = Currency.getInstance(targetCurrencyCode);
                validCurrencyCode = true; // Break the loop if the currency code is valid
            } catch (IllegalArgumentException e) { // Invalid currency code
                System.out.println("Invalid currency code. Please enter a valid currency code.");
            }
        }

        System.out.println("\n===== Currency Conversion =====");

        // Use NumberFormat to format currency amounts in the target currency with 2 decimal places
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
        currencyFormatter.setCurrency(targetCurrency); // Set the target currency for formatting

        for (Expense expense : expenses) {
            // Convert the expense amount from USD to the target currency
            double convertedAmount = expense.getAmount() * CONVERSION_RATE;

            // Format original and converted amounts using NumberFormat
            String originalAmountFormatted = currencyFormatter.format(expense.getAmount());
            String convertedAmountFormatted = currencyFormatter.format(convertedAmount);

            System.out.printf("%s - Original: %s - Converted: %s\n",
                    expense.getDescription(), originalAmountFormatted, convertedAmountFormatted);
        }
    }

    // Get a valid double input from the user and validate it
    private static double getValidDoubleInput(String prompt) {
        double value; // Variable to store the input value

        // Loop until the user enters a valid double value
        while (true) {
            System.out.print(prompt); // Print the prompt message

            // Check if the user input is a valid double value
            if (userInput.hasNextDouble()) {
                value = userInput.nextDouble(); // Get the input value

                // Check if the value is non-negative
                if (value >= 0) {
                    break;
                } else { // Value is negative
                    System.out.println("Invalid input. Please enter a non-negative value.");
                }
            } else { // Invalid input
                System.out.println("Invalid input. Please enter a valid number.");
                userInput.next(); // consume the invalid input
            }
        }
        return value; // Return the valid double value
    }

    // View the expense history of all expenses
    private static void viewExpenseHistory() {

        // Check if there are any expenses recorded in the list
        if (expenses.isEmpty()) {
            System.out.println("No expenses recorded.");
        } else { // Display all expenses in the list
            System.out.println("\n===== Expense History =====");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            // Display each expense in the list with its timestamp, category, amount, and description
            for (Expense expense : expenses) {
                System.out.printf("%s - Category: %s - Amount: $%.2f - Description: %s\n",
                        dateFormat.format(expense.getTimestamp()), expense.getCategory(),
                        expense.getAmount(), expense.getDescription());
            }
        }
    }

    @SuppressWarnings("unchecked") // Load and save the budget list to a file
    private static void loadBudgetsFromFile() {

        // Load the budget list from a file if it exists
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(BUDGETS_FILE_NAME))) {
            categoryBudgets = (Map<String, Double>) ois.readObject(); // Read the budget list from the file
            System.out.println("Budgets loaded successfully.");
        } catch (IOException | ClassNotFoundException e) { // No existing budgets found
            System.out.println("No existing budgets found. Starting with an empty budget list.");
        }
    }

    // Save the budget list to a file if it exists
    private static void saveBudgetsToFile() {

        // Save the budget list to a file if it exists
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BUDGETS_FILE_NAME))) {
            oos.writeObject(categoryBudgets); // Write the budget list to the file
            System.out.println("Budgets saved successfully.");
        } catch (IOException e) { // Error saving the budget list
            System.out.println("Error saving budgets to file.");
        }
    }

    @SuppressWarnings("unchecked") // Load the expense list from a file if it exists
    private static void loadExpensesFromFile() {

        // Load the expense list from a file if it exists
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(EXPENSES_FILE_NAME))) {
            expenses = (List<Expense>) ois.readObject(); // Read the expense list from the file
            System.out.println("Expenses loaded successfully.");
        } catch (IOException | ClassNotFoundException e) { // No existing expenses found
            System.out.println("No existing expenses found. Starting with an empty expense list.");
        }
    }

    // Save the expense list to a file if it exists
    private static void saveExpensesToFile() {

        // Save the expense list to a file if it exists
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(EXPENSES_FILE_NAME))) {
            oos.writeObject(expenses); // Write the expense list to the file
            System.out.println("Expenses saved successfully.");
        } catch (IOException e) { // Error saving the expense list
            System.out.println("Error saving expenses to file.");
        }
    }
}