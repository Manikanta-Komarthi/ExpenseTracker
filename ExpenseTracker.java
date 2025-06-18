
import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

class Transaction {
    String type;         // income or expense
    String category;     // salary, food, rent, etc.
    double amount;
    LocalDate date;

    public Transaction(String type, String category, double amount, LocalDate date) {
        this.type = type.toLowerCase();
        this.category = category.toLowerCase();
        this.amount = amount;
        this.date = date;
    }

    public String toString() {
        return String.format("%s | %s | %s | Rs. %.2f", date, type.toUpperCase(), category, amount);
    }
}

public class ExpenseTracker {
    private static final Scanner sc = new Scanner(System.in);
    private static final List<Transaction> transactions = new ArrayList<>();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n== Expense Tracker Menu ==");
            System.out.println("1. Add transaction manually");
            System.out.println("2. Load transactions from file");
            System.out.println("3. View monthly summary");
            System.out.println("4. View all transactions");
            System.out.println("5. Exit");
            System.out.print("Choose option: ");
            int choice = -1;
            try {
                System.out.print("Choose option: ");
                choice = sc.nextInt();
                sc.nextLine(); // Consume newline
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number.");
                sc.nextLine(); // Clear the invalid input
            }

            switch (choice) {
                case 1 -> addManualEntry();
                case 2 -> loadFromFile();
                case 3 -> showMonthlySummary();
                case 4 -> viewAllTransactions();
                case 5 -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void addManualEntry() {
        System.out.print("Enter type (income/expense): ");
        String type = sc.nextLine().toLowerCase();

        if (!type.equals("income") && !type.equals("expense")) {
            System.out.println("Invalid type.");
            return;
        }

        System.out.print("Enter category (e.g., salary/food/rent): ");
        String category = sc.nextLine();

        System.out.print("Enter amount: ");
        double amount = sc.nextDouble();
        sc.nextLine(); // consume newline

        LocalDate date = LocalDate.now();

        transactions.add(new Transaction(type, category, amount, date));
        System.out.println("Transaction added successfully.");
    }

    private static void loadFromFile() {
        System.out.print("Enter filename to load (e.g., input.txt): ");
        String filename = sc.nextLine();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            int count = 0;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 4) continue;

                String type = parts[0].trim();
                String category = parts[1].trim();
                double amount = Double.parseDouble(parts[2].trim());
                LocalDate date = LocalDate.parse(parts[3].trim(), formatter);

                transactions.add(new Transaction(type, category, amount, date));
                count++;
            }
            System.out.println(count + " transaction(s) loaded from file.");
        } catch (Exception e) {
            System.out.println("Error loading file: " + e.getMessage());
        }
    }

    private static void showMonthlySummary() {
        System.out.print("Enter month and year (e.g., 06-2025): ");
        String monthYear = sc.nextLine();
        DateTimeFormatter myFormatter = DateTimeFormatter.ofPattern("MM-yyyy");

        double income = 0, expense = 0;
        Map<String, Double> categoryTotals = new HashMap<>();

        for (Transaction t : transactions) {
            if (myFormatter.format(t.date).equals(monthYear)) {
                if (t.type.equals("income")) {
                    income += t.amount;
                } else {
                    expense += t.amount;
                }
                String key = t.type + "-" + t.category;
                categoryTotals.put(key, categoryTotals.getOrDefault(key, 0.0) + t.amount);
            }
        }

        System.out.println("\n== Monthly Summary for " + monthYear + " ==");
        System.out.printf("Total Income: Rs. %.2f\n", income);
        System.out.printf("Total Expense: Rs. %.2f\n", expense);
        System.out.printf("Balance: Rs. %.2f\n", income - expense);
        System.out.println("\nBreakdown by Category:");
        categoryTotals.forEach((cat, amt) -> System.out.println("  " + cat + ": Rs. " + amt));
    }

    private static void viewAllTransactions() {
        if (transactions.isEmpty()) {
            System.out.println("No transactions to display.");
            return;
        }

        System.out.println("\n== All Transactions ==");
        for (Transaction t : transactions) {
            System.out.println(t);
        }
    }
}

