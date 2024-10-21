package view;

import java.util.Scanner;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;

import common.*;
import entities.*;

public class View {
    private Scanner scanner;

    private void clearScreen() {  
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
    }  

    public View() {
        scanner = new Scanner(System.in);
    }

    public int getUserChoice(int min, int max) {
        System.out.print(">> ");

        if (!scanner.hasNextInt()) {
            scanner.nextLine();
            displayTryAgainMessage();
            getUserChoice(min, max);
        }

        int choice = scanner.nextInt();
        if (choice < min || choice > max) {
            displayTryAgainMessage();
            getUserChoice(min, max);
        }

        scanner.nextLine();
        return choice;
    }

    public void waitForInput() {
        System.out.println("Press any key to continue.");
        scanner.nextLine();
    }

    public void displayTryAgainMessage() {
        System.out.println("\rInvalid input. Please try again.");
    }
    
    public void displayMessage(String message) {
        System.out.println(message);
    }

    public void displayError(String message) {
        System.out.println("Error! " + message);
        System.out.flush();
        waitForInput();
    }

    public void displayMainMenu() {
        clearScreen();

        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║        WELCOME TO THE DELIVERY MANAGEMENT SYSTEM      ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        System.out.println("                   Your Efficiency, Our Priority");
        System.out.println();

        System.out.println("═════════════════════════════════════════════════════════");
        System.out.println("       Select a table to manage from the options below:");
        System.out.println("═════════════════════════════════════════════════════════");
        System.out.println();

        System.out.println("  [1] CLIENTS TABLE      - Manage client information");
        System.out.println("  [2] COURIERS TABLE     - Manage courier details");
        System.out.println("  [3] MEALS TABLE        - Manage available dishes");
        System.out.println("  [4] ORDERS TABLE       - Track and update orders");
        System.out.println();

        System.out.println("═════════════════════════════════════════════════════════");
        System.out.println("  Press the corresponding number to access the table.");
        System.out.println("═════════════════════════════════════════════════════════");
        System.out.println();
    }

    public void displayClientsMenu() {
        clearScreen();

        System.out.println("═════════════════════════════════════════════════════════");
        System.out.println("               CLIENTS TABLE MANAGEMENT");
        System.out.println("═════════════════════════════════════════════════════════");
        System.out.println("  [1] Add Client");
        System.out.println("  [2] View Clients");
        System.out.println("  [3] Update Client");
        System.out.println("  [4] Delete Client");
        System.out.println("  [5] Get Client with Most Orders");
        System.out.println("  [6] Generate Random Clients Data");
        System.out.println("  [7] Back to Main Menu");
    }

    public Client promptForClientDetails() {
        System.out.print("Enter client first name: ");
        String firstName = scanner.next();
        System.out.print("Enter client last name: ");
        String lastName = scanner.next();
        System.out.print("Enter client email: ");
        String email = scanner.next();
        System.out.print("Enter client phone number: ");
        String phone = scanner.next();
        scanner.nextLine();
        
        return new Client(email, firstName + " " + lastName, phone);
    }

    public String promptForClientEmail() {
        System.out.print("Enter the client's email: ");
        return scanner.nextLine();
    }

    public Client promptForClientUpdateDetails(Client existingClient) {
        System.out.println("Updating details for client: " + existingClient.name());
        
        System.out.println("Current Name: " + existingClient.name());
        System.out.print("Enter new name (or press Enter to keep current): ");
        String name = scanner.nextLine();
        if (name.trim().isEmpty()) {
            name = existingClient.name(); 
        }

        System.out.println("Current Phone: " + existingClient.phone());
        System.out.print("Enter new phone (or press Enter to keep current): ");
        String phone = scanner.nextLine();
        if (phone.trim().isEmpty()) {
            phone = existingClient.phone();
        }

        return new Client(existingClient.email(), name, phone);
    }

    public void displayClients(List<Client> clients) {
        String format = "| %-32s | %-20s | %-10s |%n";
        String separator = "+" + "-".repeat(34) + "+" + "-".repeat(22) + "+" + "-".repeat(12) + "+";
        
        System.out.println(separator);
        System.out.format("| %-32s | %-20s | %-10s |%n", "Email", "Name", "Phone");
        System.out.println(separator);
        
        for (Client client : clients) {
            System.out.format(format, client.email(), client.name(), client.phone());
        }

        System.out.println(separator);
        waitForInput();
    }

    public void displayClientWithMostOrders(Client client, int orderCount) {
        String separator = "+" + "-".repeat(34) + "+" + "-".repeat(22) + "+" + "-".repeat(12) + "+" + "-".repeat(13) + "+";
        System.out.println("\n" + "*".repeat(57));
        System.out.println(" CLIENT WITH MOST ORDERS");
        System.out.println(separator);
        System.out.format("| %-11s | %-32s | %-20s | %-10s |\n", "Order Count", "Email", "Name", "Phone");
        System.out.printf("| %-11s | %-32s | %-20s | %-10s |\n",
            orderCount, client.email(), client.name(), client.phone());
        System.out.println(separator);
    }

    public void displayCouriersMenu() {
        clearScreen();

        System.out.println("═════════════════════════════════════════════════════════");
        System.out.println("               COURIERS TABLE MANAGEMENT");
        System.out.println("═════════════════════════════════════════════════════════");
        System.out.println("  [1] Add Courier");
        System.out.println("  [2] View Couriers");
        System.out.println("  [3] Update Courier");
        System.out.println("  [4] Delete Courier");
        System.out.println("  [5] View Couriers With the Most Orders");
        System.out.println("  [6] Generate Random Couriers Data");
        System.out.println("  [7] Back to Main Menu");
    }

    public Courier promptForCourierDetails() {
        System.out.print("Enter courier phone: ");
        String phoneNumber = scanner.next();
        System.out.print("Enter courier first name: ");
        String firstName = scanner.next();
        System.out.print("Enter courier last name: ");
        String lastName = scanner.next();
        System.out.print("Enter courier transport: ");
        String transport = scanner.next();
        scanner.nextLine();
        
        return new Courier(phoneNumber, firstName + " " + lastName, transport);
    }

    public void displayCouriers(List<Courier> couriers) {
        String format = "| %-10s | %-20s | %-20s |%n";
        String separator = "+" + "-".repeat(12) + "+" + "-".repeat(22) + "+" + "-".repeat(22) + "+";
        
        System.out.println(separator);
        System.out.format("| %-10s | %-20s | %-20s |%n", "Phone", "Name", "Transport");
        System.out.println(separator);
        
        for (Courier client : couriers) {
            System.out.format(format, client.phone(), client.name(), client.transport());
        }

        System.out.println(separator);
        waitForInput();
    }

    public String promptForCourierPhone() {
        System.out.print("Enter the courier's phone: ");
        return scanner.nextLine();
    }

    public Courier promptForCourierUpdateDetails(Courier existingCourier) {
        System.out.println("Updating details for courier: " + existingCourier.name());
        
        System.out.println("Current Name: " + existingCourier.name());
        System.out.print("Enter new name (or press Enter to keep current): ");
        String name = scanner.nextLine();
        if (name.trim().isEmpty()) {
            name = existingCourier.name(); 
        }

        System.out.println("Current Transport: " + existingCourier.transport());
        System.out.print("Enter new transport (or press Enter to keep current): ");
        String transport = scanner.nextLine();
        if (transport.trim().isEmpty()) {
            transport = existingCourier.transport();
        }

        return new Courier(existingCourier.phone(), name, transport);
    }

    public void displayCouriersWithMostOrders(List<Pair<Courier, Integer>> couriers) {
        String separator = "+" + "-".repeat(22) + "+" + "-".repeat(12) + "+" + "-".repeat(13) + "+";
        System.out.println("\n" + "*".repeat(57));
        System.out.println(" TOP " + couriers.size() + " COURIERS WITH THE MOST ORDERS");
        System.out.println(separator);
        System.out.format("| %-20s | %-10s | %-11s |\n", "Name", "Phone", "Order Count");
        
        for (Pair<Courier, Integer> courier : couriers) {
            System.out.printf("| %-20s | %-10s | %-11s |\n", courier.getLeft().name(), courier.getLeft().phone(), courier.getRight());
        }

        System.out.println(separator);
    }

    public int promptForInteger(String prompt) {
        int records = -1;
        boolean validInput = false;
    
        while (!validInput) {
            System.out.print(prompt);
    
            if (scanner.hasNextInt()) {
                records = scanner.nextInt();
                scanner.nextLine();
                validInput = true;
            } else {
                scanner.nextLine();
                displayTryAgainMessage();
            }
        }
    
        return records;
    }

    public int promptForIntegerOrSkip(String prompt) {
        int value = -1;
        boolean validInput = false;
    
        while (!validInput) {
            System.out.print(prompt);

            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                validInput = true;
            } else {
                try {
                    value = Integer.parseInt(input);
                    validInput = true;
                } catch (NumberFormatException e) {
                    displayTryAgainMessage();
                }
            }
        }
    
        return value;
    }

    public int promptForNumberOfRecords() {
        return promptForInteger("Enter the number of records: ");
    }

    public void displayForeignKeyConstraintError(String field, String value) {
        System.out.println("Error! Foreign key constraint violated for " + field + ": " + value);
        System.out.println("Please ensure that the referenced record exists in the database.");
        System.out.println("Operation aborted. Please try again.");
        System.out.flush();
        waitForInput();
    }

    public void displayMealsMenu() {
        clearScreen();

        System.out.println("═════════════════════════════════════════════════════════");
        System.out.println("               MEALS TABLE MANAGEMENT");
        System.out.println("═════════════════════════════════════════════════════════");
        System.out.println("  [1] Add Meal");
        System.out.println("  [2] View Meals");
        System.out.println("  [3] Update Meal");
        System.out.println("  [4] Delete Meal");
        System.out.println("  [5] Back to Main Menu");
    }

    public Meal promptForMealDetails() {
        int mealID = promptForInteger("Enter meal ID: ");
        int orderID = promptForInteger("Enter meal order ID: ");
        System.out.print("Enter meal name: ");
        String name = scanner.next();
        int price = promptForInteger("Enter meal price: ");
        int weight = promptForInteger("Enter meal weight: ");
        int servingSize = promptForInteger("Enter meal serving size: ");
        
        return new Meal(mealID, orderID, name, price, weight, servingSize);
    }

    public void displayMeals(List<Meal> meals) {
        String format = "| %-10s | %-10s | %-20s | %-10s | %-10s | %-15s |%n";
        String separator = "+" + "-".repeat(12) + "+" + "-".repeat(12) + "+" + "-".repeat(22) + "+"+ "-".repeat(12) + "+"+ "-".repeat(12) + "+"+ "-".repeat(17) + "+";
        
        System.out.println(separator);
        System.out.format(format, "Meal ID", "Order ID", "Name", "Price", "Weight", "Serving Size");
        System.out.println(separator);
        
        for (Meal meal : meals) {
            System.out.printf(format, meal.mealID(), meal.orderID(), meal.name(), meal.price(), meal.weight(), meal.servingSize());
        }

        System.out.println(separator);
        waitForInput();
    }

    public int promptForMealID() {
        return promptForInteger("Please enter the meal's ID: ");
    }

    public Meal promptForMealUpdateDetails(Meal existingMeal) {
        System.out.println("Updating details for meal: " + existingMeal.name());
        
        System.out.println("Current Name: " + existingMeal.name());
        System.out.print("Enter new name (or press Enter to keep current): ");
        String name = scanner.nextLine();
        if (name.trim().isEmpty()) {
            name = existingMeal.name(); 
        }

        System.out.println("Current Price: " + existingMeal.price());
        int price = promptForIntegerOrSkip("Enter new price (or press Enter to keep current): ");
        System.out.println("Current Weight: " + existingMeal.weight());
        int weight = promptForIntegerOrSkip("Enter new weight (or press Enter to keep current): ");
        System.out.println("Current Serving Size: " + existingMeal.weight());
        int servingSize = promptForIntegerOrSkip("Enter new serving size (or press Enter to keep current): ");

        return new Meal(existingMeal.mealID(), existingMeal.orderID(), name, price, weight, servingSize);
    }

    public void displayOrdersMenu() {
        clearScreen();

        System.out.println("═════════════════════════════════════════════════════════");
        System.out.println("               ORDERS TABLE MANAGEMENT");
        System.out.println("═════════════════════════════════════════════════════════");
        System.out.println("  [1] Add Order");
        System.out.println("  [2] View Orders");
        System.out.println("  [3] Update Order");
        System.out.println("  [4] Delete Order");
        System.out.println("  [5] Fetch Client Analytics");
        System.out.println("  [6] Fetch Courier Analytics");
        System.out.println("  [7] Back to Main Menu");
    }

    public Order promptForOrderDetails() {
        int orderID = promptForInteger("Enter order ID: ");
        System.out.print("Enter order date (yyyy-mm-dd hh:mm): ");
        String orderDate = scanner.nextLine() + ":00";
        System.out.print("Enter order courier phone: ");
        String courierPhone = scanner.next();
        scanner.nextLine();
        System.out.print("Enter order delivery date (yyyy-mm-dd hh:mm): ");
        String deliveryDate = scanner.nextLine() + ":00";
        System.out.print("Enter order client email: ");
        String clientEmail = scanner.next();
        int rating = promptForInteger("Enter order rating between 1 and 5: ");
        System.out.print("Enter order delivery address (street №): ");
        String deliveryAddress = scanner.nextLine();
        
        return new Order(orderID, orderDate, courierPhone, deliveryDate, clientEmail, rating, deliveryAddress);
    }

    public void displayOrders(List<Order> orders) {
        String format = "| %-10s | %-21s | %-15s | %-21s | %-32s | %-7s | %-20s |%n";
        String separator = "+" + "-".repeat(12) + "+" + "-".repeat(23) + "+" + "-".repeat(17) + "+" + "-".repeat(23) + "+" + "-".repeat(34) + "+" + "-".repeat(9) + "+" + "-".repeat(22) + "+";
        
        System.out.println(separator);
        System.out.format(format, "Order ID", "Order Date", "Courier Phone", "Delivery Date", "Client Email", " Rating", "Delivery Address");
        System.out.println(separator);
        
        for (Order order : orders) {
            System.out.printf(format, order.orderID(), order.orderDate(), order.courierPhone(), order.deliveryDate(),
                order.clientEmail(), order.rating(), order.deliveryAddress());
        }

        System.out.println(separator);
        waitForInput();
    }

    public int promptForOrderID() {
        return promptForInteger("Please enter the order's ID: ");
    }

    public Order promptForOrderUpdateDetails(Order existingOrder) {
        System.out.println("Updating details for order: " + existingOrder.orderID());
        
        System.out.println("Current Delivery Date: " + existingOrder.deliveryDate());
        System.out.print("Enter new delivery date (yyyy-mm-dd hh:mm) (or press Enter to keep current): ");
        String deliveryDate = scanner.nextLine();
        if (deliveryDate.trim().isEmpty()) {
            deliveryDate = existingOrder.deliveryDate(); 
        }
        else {
            deliveryDate += ":00";
        }

        System.out.println("Current Rating: " + existingOrder.rating());
        int rating = promptForIntegerOrSkip("Enter new rating between 1 and 5 (or press Enter to keep current): ");
        System.out.println("Current Delivery Address: " + existingOrder.deliveryAddress());
        System.out.print("Enter order delivery address (street №) (or press Enter to keep current): ");
        String deliveryAddress = scanner.nextLine();
        if (deliveryAddress.trim().isEmpty()) {
            deliveryAddress = existingOrder.deliveryAddress(); 
        }

        return new Order(existingOrder.orderID(), existingOrder.orderDate(), existingOrder.courierPhone(), deliveryDate,
            existingOrder.clientEmail(), rating, deliveryAddress);
    }

    public ClientFilterParameters promptForClientFilterParameters() {
        System.out.print("Enter the start date of order (yyyy-mm-dd hh:mm): ");
        String startOrderDate = scanner.nextLine() + ":00";
        int maxMealPrice = promptForInteger("Enter maximum price of meal: ");
        System.out.print("Enter client's email: ");
        String email = scanner.next();
        scanner.nextLine();

        return new ClientFilterParameters(startOrderDate, maxMealPrice, email);
    }

    public void displayClientAnalytics(ClientAnalytics analytics) {
        String format = "| %-20s | %-12s | %-12s |%n";
        String separator = "+" + "-".repeat(22) + "+" + "-".repeat(14) + "+" + "-".repeat(14) + "+";
        
        System.out.println(separator);
        System.out.format(format, "Client Name", "Order Count", "Total Spent");
        System.out.println(separator);
        System.out.printf(format, analytics.getName(), analytics.getOrderCount(), analytics.getTotalSpent());
        System.out.println(separator);
    }

    public CourierFilterParameters promptForCourierFilterParameters() {
        System.out.print("Enter the start date of delivery (yyyy-mm-dd): ");
        String startDeliveryDate = scanner.nextLine();
        int minRating = promptForInteger("Enter minimum rating: ");

        return new CourierFilterParameters(startDeliveryDate, minRating);
    }

    public void displayCourierAnalytics(List<CourierAnalytics> couriers) {
        String format = "| %-20s | %-12s | %-15s | %-21s | %-21s |%n";
        String separator = "+" + "-".repeat(22) + "+" + "-".repeat(14) + "+" + "-".repeat(17) + "+" + "-".repeat(23) + "+" + "-".repeat(23) + "+";
        
        System.out.println(separator);
        System.out.format(format, "Courier Name", "Phone", "Average Rating", "Last Delivery Date", "First Order Date");
        System.out.println(separator);

        for (CourierAnalytics analytics : couriers) {
            System.out.printf(format, analytics.getName(), analytics.getPhone(), analytics.getAverageRating(), analytics.getLastDeliveryDate(), analytics.getFirstOrderDate());
        }
        System.out.println(separator);
    }

    public void displayQueryRuntime(long timeInMs) {
        System.out.println("Query runtime: " + timeInMs + " msec.");
    }
}