package com.lab2.view;

import java.util.Scanner;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;

import com.lab2.common.*;
import com.lab2.dto.*;

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

    public ClientDTO promptForClientDetails() {
        System.out.print("Enter client first name: ");
        String firstName = scanner.next();
        System.out.print("Enter client last name: ");
        String lastName = scanner.next();
        System.out.print("Enter client email: ");
        String email = scanner.next();
        System.out.print("Enter client phone number: ");
        String phone = scanner.next();
        scanner.nextLine();
        
        return ClientDTO.builder()
            .email(email)
            .name(firstName + " " + lastName)
            .phone(phone)
            .build();
    }

    public String promptForClientEmail() {
        System.out.print("Enter the client's email: ");
        return scanner.nextLine();
    }

    public ClientDTO promptForClientUpdateDetails(ClientDTO existingClient) {
        System.out.println("Updating details for client: " + existingClient.getName());
        
        System.out.println("Current Name: " + existingClient.getName());
        System.out.print("Enter new name (or press Enter to keep current): ");
        String name = scanner.nextLine();
        if (name.trim().isEmpty()) {
            name = existingClient.getName(); 
        }

        System.out.println("Current Phone: " + existingClient.getPhone());
        System.out.print("Enter new phone (or press Enter to keep current): ");
        String phone = scanner.nextLine();
        if (phone.trim().isEmpty()) {
            phone = existingClient.getPhone();
        }

        return ClientDTO.builder()
            .email(existingClient.getEmail())
            .name(name)
            .phone(phone)
            .build();
    }

    public void displayClients(List<ClientDTO> clients) {
        String format = "| %-32s | %-20s | %-10s |%n";
        String separator = "+" + "-".repeat(34) + "+" + "-".repeat(22) + "+" + "-".repeat(12) + "+";
        
        System.out.println(separator);
        System.out.format("| %-32s | %-20s | %-10s |%n", "Email", "Name", "Phone");
        System.out.println(separator);
        
        for (ClientDTO client : clients) {
            System.out.format(format, client.getEmail(), client.getName(), client.getPhone());
        }

        System.out.println(separator);
        waitForInput();
    }

    public void displayClientWithMostOrders(ClientDTO client, int orderCount) {
        String separator = "+" + "-".repeat(13) + "+" + "-".repeat(34) + "+" + "-".repeat(22) + "+" + "-".repeat(12) + "+";
        System.out.println("\n" + "*".repeat(57));
        System.out.println(" CLIENT WITH MOST ORDERS");
        System.out.println(separator);
        System.out.format("| %-11s | %-32s | %-20s | %-10s |\n", "Order Count", "Email", "Name", "Phone");
        System.out.printf("| %-11s | %-32s | %-20s | %-10s |\n",
            orderCount, client.getEmail(), client.getName(), client.getPhone());
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

    public CourierDTO promptForCourierDetails() {
        System.out.print("Enter courier phone: ");
        String phoneNumber = scanner.next();
        System.out.print("Enter courier first name: ");
        String firstName = scanner.next();
        System.out.print("Enter courier last name: ");
        String lastName = scanner.next();
        System.out.print("Enter courier transport: ");
        String transport = scanner.next();
        scanner.nextLine();
        
        return CourierDTO.builder()
            .phone(phoneNumber)
            .name(firstName + " " + lastName)
            .transport(transport)
            .build();
    }

    public void displayCouriers(List<CourierDTO> couriers) {
        String format = "| %-10s | %-20s | %-20s |%n";
        String separator = "+" + "-".repeat(12) + "+" + "-".repeat(22) + "+" + "-".repeat(22) + "+";
        
        System.out.println(separator);
        System.out.format("| %-10s | %-20s | %-20s |%n", "Phone", "Name", "Transport");
        System.out.println(separator);
        
        for (CourierDTO client : couriers) {
            System.out.format(format, client.getPhone(), client.getName(), client.getTransport());
        }

        System.out.println(separator);
        waitForInput();
    }

    public String promptForCourierPhone() {
        System.out.print("Enter the courier's phone: ");
        return scanner.nextLine();
    }

    public CourierDTO promptForCourierUpdateDetails(CourierDTO existingCourier) {
        System.out.println("Updating details for courier: " + existingCourier.getName());
        
        System.out.println("Current Name: " + existingCourier.getName());
        System.out.print("Enter new name (or press Enter to keep current): ");
        String name = scanner.nextLine();
        if (name.trim().isEmpty()) {
            name = existingCourier.getName(); 
        }

        System.out.println("Current Transport: " + existingCourier.getTransport());
        System.out.print("Enter new transport (or press Enter to keep current): ");
        String transport = scanner.nextLine();
        if (transport.trim().isEmpty()) {
            transport = existingCourier.getTransport();
        }

        return CourierDTO.builder()
            .phone(existingCourier.getPhone())
            .name(name)
            .transport(transport)
            .build();
    }

    public void displayCouriersWithMostOrders(List<Pair<CourierDTO, Integer>> couriers) {
        String separator = "+" + "-".repeat(22) + "+" + "-".repeat(12) + "+" + "-".repeat(13) + "+";
        System.out.println("\n" + "*".repeat(57));
        System.out.println(" TOP " + couriers.size() + " COURIERS WITH THE MOST ORDERS");
        System.out.println(separator);
        System.out.format("| %-20s | %-10s | %-11s |\n", "Name", "Phone", "Order Count");
        
        for (Pair<CourierDTO, Integer> courier : couriers) {
            System.out.printf("| %-20s | %-10s | %-11s |\n", courier.getLeft().getName(), courier.getLeft().getPhone(), courier.getRight());
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

    public MealDTO promptForMealDetails() {
        int mealID = promptForInteger("Enter meal ID: ");
        int orderID = promptForInteger("Enter meal order ID: ");
        System.out.print("Enter meal name: ");
        String name = scanner.next();
        int price = promptForInteger("Enter meal price: ");
        int weight = promptForInteger("Enter meal weight: ");
        int servingSize = promptForInteger("Enter meal serving size: ");
        
        return MealDTO.builder()
            .mealID(mealID)
            .orderID(orderID)
            .name(name)
            .price(price)
            .weight(weight)
            .servingSize(servingSize)
            .build();
    }

    public void displayMeals(List<MealDTO> meals) {
        String format = "| %-10s | %-10s | %-20s | %-10s | %-10s | %-15s |%n";
        String separator = "+" + "-".repeat(12) + "+" + "-".repeat(12) + "+" + "-".repeat(22) + "+"+ "-".repeat(12) + "+"+ "-".repeat(12) + "+"+ "-".repeat(17) + "+";
        
        System.out.println(separator);
        System.out.format(format, "Meal ID", "Order ID", "Name", "Price", "Weight", "Serving Size");
        System.out.println(separator);
        
        for (MealDTO meal : meals) {
            System.out.printf(format, meal.getMealID(), meal.getOrderID(), meal.getName(), meal.getPrice(), meal.getWeight(), meal.getServingSize());
        }

        System.out.println(separator);
        waitForInput();
    }

    public int promptForMealID() {
        return promptForInteger("Please enter the meal's ID: ");
    }

    public MealDTO promptForMealUpdateDetails(MealDTO existingMeal) {
        System.out.println("Updating details for meal: " + existingMeal.getName());
        
        System.out.println("Current Name: " + existingMeal.getName());
        System.out.print("Enter new name (or press Enter to keep current): ");
        String name = scanner.nextLine();
        if (name.trim().isEmpty()) {
            name = existingMeal.getName(); 
        }

        System.out.println("Current Price: " + existingMeal.getPrice());
        int price = promptForIntegerOrSkip("Enter new price (or press Enter to keep current): ");
        System.out.println("Current Weight: " + existingMeal.getWeight());
        int weight = promptForIntegerOrSkip("Enter new weight (or press Enter to keep current): ");
        System.out.println("Current Serving Size: " + existingMeal.getServingSize());
        int servingSize = promptForIntegerOrSkip("Enter new serving size (or press Enter to keep current): ");

        return MealDTO.builder()
            .mealID(existingMeal.getMealID())
            .orderID(existingMeal.getOrderID())
            .name(name)
            .price(price)
            .weight(weight)
            .servingSize(servingSize)
            .build();
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

    public OrderDTO promptForOrderDetails() {
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
        
        return OrderDTO.builder()
            .orderID(orderID)
            .orderDate(orderDate)
            .courierPhone(courierPhone)
            .deliveryDate(deliveryDate)
            .clientEmail(clientEmail)
            .rating(rating)
            .deliveryAddress(deliveryAddress)
            .build();
    }

    public void displayOrders(List<OrderDTO> orders) {
        String format = "| %-10s | %-21s | %-15s | %-21s | %-32s | %-7s | %-20s |%n";
        String separator = "+" + "-".repeat(12) + "+" + "-".repeat(23) + "+" + "-".repeat(17) + "+" + "-".repeat(23) + "+" + "-".repeat(34) + "+" + "-".repeat(9) + "+" + "-".repeat(22) + "+";
        
        System.out.println(separator);
        System.out.format(format, "Order ID", "Order Date", "Courier Phone", "Delivery Date", "Client Email", " Rating", "Delivery Address");
        System.out.println(separator);
        
        for (OrderDTO order : orders) {
            System.out.printf(format, order.getOrderID(), order.getOrderDate(), order.getCourierPhone(), order.getDeliveryDate(),
                order.getClientEmail(), order.getRating(), order.getDeliveryAddress());
        }

        System.out.println(separator);
        waitForInput();
    }

    public int promptForOrderID() {
        return promptForInteger("Please enter the order's ID: ");
    }

    public OrderDTO promptForOrderUpdateDetails(OrderDTO existingOrder) {
        System.out.println("Updating details for order: " + existingOrder.getOrderID());
        
        System.out.println("Current Delivery Date: " + existingOrder.getDeliveryDate());
        System.out.print("Enter new delivery date (yyyy-mm-dd hh:mm) (or press Enter to keep current): ");
        String deliveryDate = scanner.nextLine();
        if (deliveryDate.trim().isEmpty()) {
            deliveryDate = existingOrder.getDeliveryDate(); 
        }

        System.out.println("Current Rating: " + existingOrder.getRating());
        int rating = promptForIntegerOrSkip("Enter new rating between 1 and 5 (or press Enter to keep current): ");
        System.out.println("Current Delivery Address: " + existingOrder.getDeliveryAddress());
        System.out.print("Enter order delivery address (street №) (or press Enter to keep current): ");
        String deliveryAddress = scanner.nextLine();
        if (deliveryAddress.trim().isEmpty()) {
            deliveryAddress = existingOrder.getDeliveryAddress(); 
        }

        return OrderDTO.builder()
            .orderID(existingOrder.getOrderID())
            .orderDate(existingOrder.getOrderDate())
            .courierPhone(existingOrder.getCourierPhone())
            .deliveryDate(deliveryDate)
            .clientEmail(existingOrder.getClientEmail())
            .rating(rating)
            .deliveryAddress(deliveryAddress)
            .build();
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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (CourierAnalytics analytics : couriers) {
            System.out.printf(format, analytics.getName(), analytics.getPhone(), analytics.getAverageRating(),
                analytics.getLastDeliveryDate().format(formatter), analytics.getFirstOrderDate().format(formatter));
        }
        System.out.println(separator);
    }

    public void displayQueryRuntime(long timeInMs) {
        System.out.println("Query runtime: " + timeInMs + " msec.");
    }
}