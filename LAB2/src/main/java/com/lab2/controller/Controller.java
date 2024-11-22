package com.lab2.controller;

import com.lab2.common.*;
import com.lab2.dto.*;
import com.lab2.entity.*;
import com.lab2.model.Model;
import com.lab2.util.Error;
import com.lab2.util.Result;
import com.lab2.util.SQLQueryRuntime;
import com.lab2.view.View;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;

public class Controller {
    private Model model;
    private View view;
    private Mapper mapper;

    public Controller() throws IllegalStateException {
        model = new Model();
        view = new View();
        mapper = new Mapper();
    }

    public void handleClient() throws IllegalStateException {
        boolean backToMainMenu = false;
        while (true)
        {
            view.displayClientsMenu();
            switch (view.getUserChoice(1, 7))
            {
                case 1 -> {
                    ClientDTO client = view.promptForClientDetails();
                    model.addClient(client).ifPresentOrElse(
                        value -> {
                            if (value instanceof Error.ValidationError e) {
                                view.displayError(e.description());
                            }
                            else if (value instanceof Error.DuplicateKeyError e) {
                                view.displayError("Client with this email already exists: " + e.key());
                            }
                        },
                        () -> {
                            view.displayMessage("Client successfully added: " + client.getName());
                            view.waitForInput();
                        }
                    );
                }
                case 2 -> {
                    List<Client> clients = model.getAllClients();
                    view.displayClients(clients.stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList()));
                }
                case 3 -> {
                    String email = view.promptForClientEmail();
                    switch (model.getClient(email)) {
                        case Result.Success<?,Client> success -> {
                            ClientDTO client = view.promptForClientUpdateDetails(mapper.toDto(success.value()));
                            model.updateClient(client).ifPresentOrElse(
                                value -> {
                                    if (value instanceof Error.ValidationError e) {
                                        view.displayError(e.description());
                                    }
                                },
                                () -> {
                                    view.displayMessage("Client details updated successfully.");
                                    view.waitForInput();
                                }
                            );
                        }
                        case Result.Failure<Error, ?> failure -> {
                            if (failure.error() instanceof Error.RecordNotFound e) {
                                view.displayError("Client not found with email: '" + e.name() + "'");
                            }
                        }
                    }
                }
                case 4 -> {
                    String email = view.promptForClientEmail();
                    switch (model.getClient(email)) {
                        case Result.Success<?,Client> _ -> {
                            model.deleteClient(email);
                            view.displayMessage("Client successfully deleted.");
                            view.waitForInput();
                        }
                        case Result.Failure<Error, ?> failure -> {
                            if (failure.error() instanceof Error.RecordNotFound e) {
                                view.displayError("Client not found with email: '" + e.name() + "'");
                            }
                        }
                    }
                }
                case 5 -> {
                    model.getClientWithMostOrders().ifPresentOrElse(
                        value -> {
                            view.displayClientWithMostOrders(mapper.toDto(value.getLeft()), value.getRight());
                            view.displayQueryRuntime(SQLQueryRuntime.measure());
                            view.waitForInput();
                        },
                        () -> {
                            view.displayMessage("No clients found.");
                        }
                    );
                }
                case 6 -> {
                    int numberOfRecords = view.promptForNumberOfRecords();
                    model.generateRandomClients(numberOfRecords);
                    view.displayMessage("Successfully generated " + numberOfRecords + " clients.");
                    view.waitForInput();
                }
                case 7 -> {
                    backToMainMenu = true;
                }
                default -> {
                    assert false : "Unhandled choice";
                }
            }
            if (backToMainMenu)
                break;
        }
    }

    public void handleCourier() {
        boolean backToMainMenu = false;
        while (true)
        {
            view.displayCouriersMenu();
            switch (view.getUserChoice(1, 7))
            {
                case 1 -> {
                    CourierDTO courier = view.promptForCourierDetails();
                    model.addCourier(courier).ifPresentOrElse(
                        value -> {
                            if (value instanceof Error.ValidationError e) {
                                view.displayError(e.description());
                            }
                            else if (value instanceof Error.DuplicateKeyError e) {
                                view.displayError("Courier with this phone number already exists: " + e.key());
                            }
                        },
                        () -> {
                            view.displayMessage("Courier successfully added: " + courier.getName());
                            view.waitForInput();
                        }
                    );
                }
                case 2 -> {
                    List<Courier> couriers = model.getAllCouriers();
                    view.displayCouriers(couriers.stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList()));
                }
                case 3 -> {
                    String phone = view.promptForCourierPhone();
                    switch (model.getCourier(phone)) {
                        case Result.Success<?,Courier> success -> {
                            CourierDTO courier = view.promptForCourierUpdateDetails(mapper.toDto(success.value()));
                            model.updateCourier(courier).ifPresentOrElse(
                                value -> {
                                    if (value instanceof Error.ValidationError e) {
                                        view.displayError(e.description());
                                    }
                                },
                                () -> {
                                    view.displayMessage("Courier details updated successfully.");
                                    view.waitForInput();
                                }
                            );
                        }
                        case Result.Failure<Error, ?> failure -> {
                            if (failure.error() instanceof Error.RecordNotFound e) {
                                view.displayError("Courier not found with phone number: '" + e.name() + "'");
                            }
                        }
                    }
                }
                case 4 -> {
                    String phone = view.promptForCourierPhone();
                    switch (model.getCourier(phone)) {
                        case Result.Success<?,Courier> _ -> {
                            model.deleteCourier(phone);
                            view.displayMessage("Courier successfully deleted.");
                            view.waitForInput();
                        }
                        case Result.Failure<Error, ?> failure -> {
                            if (failure.error() instanceof Error.RecordNotFound e) {
                                view.displayError("Courier not found with phone: '" + e.name() + "'");
                            }
                        }
                    }
                }
                case 5 -> {
                    int numberOfRecods = view.promptForNumberOfRecords();
                    List<Pair<Courier, Integer>> couriers = model.getCouriersWithMostOrders(numberOfRecods);
                    if (!couriers.isEmpty()) {
                        view.displayCouriersWithMostOrders(couriers.stream()
                            .map(pair -> Pair.of(mapper.toDto(pair.getLeft()), pair.getRight()))
                            .collect(Collectors.toList()));
                        view.displayQueryRuntime(SQLQueryRuntime.measure());
                        view.waitForInput();
                        
                    }
                    else {
                        view.displayMessage("No couriers found.");
                    }
                }
                case 6 -> {
                    int numberOfRecords = view.promptForNumberOfRecords();
                    model.generateRandomCouriers(numberOfRecords);
                    view.displayMessage("Successfully generated " + numberOfRecords + " couriers.");
                    view.waitForInput();
                }
                case 7 -> {
                    backToMainMenu = true;
                }
                default -> {
                    assert false : "Unhandled choice";
                }
            }
            if (backToMainMenu)
                break;
        }
    }

    public void handleMeal() {
        boolean backToMainMenu = false;
        while (true)
        {
            view.displayMealsMenu();
            switch (view.getUserChoice(1, 5))
            {
                case 1 -> {
                    MealDTO meal = view.promptForMealDetails();
                    model.addMeal(meal).ifPresentOrElse(
                        value -> {
                            if (value instanceof Error.ValidationError e) {
                                view.displayError(e.description());
                            }
                            else if (value instanceof Error.DuplicateKeyError e) {
                                view.displayError("Meal with this ID already exists: " + e.key());
                            }
                            else if (value instanceof Error.ForeignKeyConstraintError e) {
                                view.displayForeignKeyConstraintError(e.field(), e.value());
                            }
                        },
                        () -> {
                            view.displayMessage("Meal successfully added: " + meal.getMealID());
                            view.waitForInput();
                        }
                    );
                }
                case 2 -> {
                    List<Meal> meals = model.getAllMeals();
                    view.displayMeals(meals.stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList()));
                }
                case 3 -> {
                    int mealID = view.promptForMealID();
                    switch (model.getMeal(mealID)) {
                        case Result.Success<?,Meal> success -> {
                            MealDTO meal = view.promptForMealUpdateDetails(mapper.toDto(success.value()));
                            model.updateMeal(meal).ifPresentOrElse(
                                value -> {
                                    if (value instanceof Error.ValidationError e) {
                                        view.displayError(e.description());
                                    }
                                },
                                () -> {
                                    view.displayMessage("Meal details updated successfully.");
                                    view.waitForInput();
                                }
                            );
                        }
                        case Result.Failure<Error, ?> failure -> {
                            if (failure.error() instanceof Error.RecordNotFound e) {
                                view.displayError("Meal not found with ID: '" + e.name() + "'");
                            }
                        }
                    }
                }
                case 4 -> {
                    int mealID = view.promptForMealID();
                    switch (model.getMeal(mealID)) {
                        case Result.Success<?,Meal> _ -> {
                            model.deleteMeal(mealID);
                            view.displayMessage("Meal successfully deleted.");
                            view.waitForInput();
                        }
                        case Result.Failure<Error, ?> failure -> {
                            if (failure.error() instanceof Error.RecordNotFound e) {
                                view.displayError("Meal not found with ID: '" + e.name() + "'");
                            }
                        }
                    }
                }
                case 5 -> {
                    backToMainMenu = true;
                }
                default -> {
                    assert false : "Unhandled choice";
                }
            }
            if (backToMainMenu)
                break;
        }
    }

    public void handleOrder() {
        boolean backToMainMenu = false;
        while (true)
        {
            view.displayOrdersMenu();
            switch (view.getUserChoice(1, 7))
            {
                case 1 -> {
                    OrderDTO order = view.promptForOrderDetails();
                    model.addOrder(order).ifPresentOrElse(
                        value -> {
                            if (value instanceof Error.ValidationError e) {
                                view.displayError(e.description());
                            }
                            else if (value instanceof Error.DuplicateKeyError e) {
                                view.displayError("Order with this ID already exists: " + e.key());
                            }
                            else if (value instanceof Error.ForeignKeyConstraintError e) {
                                view.displayForeignKeyConstraintError(e.field(), e.value());
                            }
                        },
                        () -> {
                            view.displayMessage("Order successfully added: " + order.getOrderID());
                            view.waitForInput();
                        }
                    );
                }
                case 2 -> {
                    List<Order> orders = model.getAllOrders();
                    view.displayOrders(orders.stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList()));
                }
                case 3 -> {
                    int orderID = view.promptForOrderID();
                    switch (model.getOrder(orderID)) {
                        case Result.Success<?,Order> success -> {
                            OrderDTO order = view.promptForOrderUpdateDetails(mapper.toDto(success.value()));
                            model.updateOrder(order).ifPresentOrElse(
                                value -> {
                                    if (value instanceof Error.ValidationError e) {
                                        view.displayError(e.description());
                                    }
                                },
                                () -> {
                                    view.displayMessage("Order details updated successfully.");
                                    view.waitForInput();
                                }
                            );
                        }
                        case Result.Failure<Error, ?> failure -> {
                            if (failure.error() instanceof Error.RecordNotFound e) {
                                view.displayError("Order not found with ID: '" + e.name() + "'");
                            }
                        }
                    }
                }
                case 4 -> {
                    int orderID = view.promptForOrderID();
                    switch (model.getOrder(orderID)) {
                        case Result.Success<?,Order> _ -> {
                            model.deleteOrder(orderID);
                            view.displayMessage("Order successfully deleted.");
                            view.waitForInput();
                        }
                        case Result.Failure<Error, ?> failure -> {
                            if (failure.error() instanceof Error.RecordNotFound e) {
                                view.displayError("Order not found with ID: '" + e.name() + "'");
                            }
                        }
                    }
                }
                case 5 -> {
                    ClientFilterParameters parameters = view.promptForClientFilterParameters();
                    switch (model.fetchClientAnalytics(parameters)) {
                        case Result.Success<?,ClientAnalytics> success -> {
                            view.displayClientAnalytics(success.value());
                            view.displayQueryRuntime(SQLQueryRuntime.measure());
                            view.waitForInput();
                        }
                        case Result.Failure<Error, ?> failure -> {
                            if (failure.error() instanceof Error.RecordNotFound _) {
                                view.displayError("No records found for the given filters.");
                            }
                            else if (failure.error() instanceof Error.ValidationError e) {
                                view.displayError(e.description());
                            }
                        }
                    }
                }
                case 6 -> {
                    CourierFilterParameters parameters = view.promptForCourierFilterParameters();
                    switch (model.fetchCourierAnalytics(parameters)) {
                        case Result.Success<?,ArrayList<CourierAnalytics>> success -> {
                            view.displayCourierAnalytics(success.value());
                            view.displayQueryRuntime(SQLQueryRuntime.measure());
                            view.waitForInput();
                        }
                        case Result.Failure<Error, ?> failure -> {
                            if (failure.error() instanceof Error.RecordNotFound _) {
                                view.displayError("No records found for the given filters.");
                            }
                            else if (failure.error() instanceof Error.ValidationError e) {
                                view.displayError(e.description());
                            }
                        }
                    }
                }
                case 7 -> {
                    backToMainMenu = true;
                }
                default -> {
                    assert false : "Unhandled choice";
                }
            }
            if (backToMainMenu)
                break;
        }
    }

    public void start() throws IllegalStateException {
        while (true)
        {
            view.displayMainMenu();
            switch (view.getUserChoice( 1,4)) 
            {
                case 1 -> {
                    handleClient();
                }
                case 2 -> {
                    handleCourier();
                }
                case 3 -> {
                    handleMeal();
                }
                case 4 -> {
                    handleOrder();
                }
                default -> {
                    assert false : "Unhandled choice";
                }
            }
        }
    }
}
