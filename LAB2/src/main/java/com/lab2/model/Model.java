package com.lab2.model;

import com.lab2.common.*;
import com.lab2.dto.*;
import com.lab2.entity.*;
import com.lab2.model.validation.*;
import com.lab2.service.*;
import com.lab2.util.Error;
import com.lab2.util.Result;
import com.lab2.util.SQLQueryRuntime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class Model {
    private EntityManagerFactory emf;
    private ClientService clientService;
    private CourierService courierService;
    private MealService mealService;
    private OrderService orderService;

    public Model() {
        this.emf = Persistence.createEntityManagerFactory("FoodDeliveryPU");
        this.clientService = new ClientService(emf);
        this.courierService = new CourierService(emf);
        this.mealService = new MealService(emf);
        this.orderService = new OrderService(emf);
    }

    private <T, D, ID> Result<Error, T> getRecord(Service<T, D, ID> service, ID identifier) throws IllegalStateException {
        try {
            Optional<T> record = service.findRecord(identifier);
            if (record.isPresent()) {
                return new Result.Success<>(record.get());
            }
        } catch (RuntimeException e) {
            throw new IllegalStateException("An unexpected error occurred while collecting record from the database.", e);
        }
    
        return new Result.Failure<>(new Error.RecordNotFound(identifier.toString()));
    }

    private <T, D, ID> List<T> getAllRecords(Service<T, D, ID> service) throws IllegalStateException {
        try {
            List<T> records = service.getAllRecords();
            return records;
        } catch (RuntimeException e) {
            throw new IllegalStateException("An unexpected error occurred while fetching records from the database.", e);
        }
    }

    private <T, D, ID> void addRecord(Service<T, D, ID> service, D dto) throws IllegalStateException {
        try {
            service.addRecord(dto);
        } catch (RuntimeException e) {
            throw new IllegalStateException("An unexpected error occurred while adding record to the database.", e);
        }
    }

    private <T, D, ID> boolean updateRecord(Service<T, D, ID> service, D dto) throws IllegalStateException {
        try {
            return service.updateRecord(dto);
        } catch (RuntimeException e) {
            throw new IllegalStateException("An unexpected error occurred while updating record in the database.", e);
        }
    }

    private <T, D, ID> boolean deleteRecord(Service<T, D, ID> service, ID identifier) throws IllegalStateException {
        try {
            return service.deleteRecord(identifier);
        } catch (RuntimeException e) {
            throw new IllegalStateException("An unexpected error occurred while deleting record from the database.", e);
        }
    }

    private Optional<Error> validateEntity(List<ValidationRule> validationRules) throws IllegalStateException {
        return validationRules.stream()
                .filter(rule -> !rule.isValid())
                .map(rule -> (Error) new Error.ValidationError(rule.getErrorMessage()))
                .findFirst();
    }

    public Optional<Error> validateClient(ClientDTO client) throws IllegalStateException {
        List<ValidationRule> validationRules = Arrays.asList(
            new ValidationRule(client.getName(), new NameValidator(), "Name"),
            new ValidationRule(client.getEmail(), new EmailValidator(), "Email"),
            new ValidationRule(client.getPhone(), new PhoneNumberValidator(), "Phone")
        );
        return validateEntity(validationRules);
    }

    public Optional<Error> addClient(ClientDTO client) throws IllegalStateException {
        Optional<Error> validationError = validateClient(client);
        if (validationError.isPresent()) {
            return validationError;
        }

        switch (getClient(client.getEmail())) {
            case Result.Success<?,Client> _ -> {
                return Optional.of(new Error.DuplicateKeyError(client.getEmail()));
            }
            default -> {
                addRecord(clientService, client);
                return Optional.empty();
            }
        }
    }

    public List<Client> getAllClients() throws IllegalStateException {
        return getAllRecords(clientService);
    }

    public Result<Error, Client> getClient(String email) throws IllegalStateException {
        return getRecord(clientService, email);
    }

    public Optional<Error> updateClient(ClientDTO client) throws IllegalStateException {
        Optional<Error> validationError = validateClient(client);
        if (validationError.isPresent()) {
            return validationError;
        }

        if (!updateRecord(clientService, client)) {
            return Optional.of(new Error.RecordNotFound(client.getEmail()));
        }

        return Optional.empty();
    }

    public Optional<Error> deleteClient(String email) throws IllegalStateException {
        if (!deleteRecord(clientService, email)) {
            return Optional.of(new Error.RecordNotFound(email));
        }

        return Optional.empty();
    }

    public Optional<Pair<Client, Integer>> getClientWithMostOrders() throws IllegalStateException {       
        try {
            return clientService.getClientWithMostOrders();
        } catch (RuntimeException _) {
            throw new IllegalStateException("An unexpected error occurred while fetching a client with most orders from the database.");
        }
    }

    public void generateRandomClients(int numberOfRecords) throws IllegalStateException {
        try {
            clientService.generateRandomClients(numberOfRecords);
        } catch (RuntimeException _) {
            throw new IllegalStateException("An unexpected error occurred while generating random clients in the database.");
        }
    }

    private Optional<Error> validateCourier(CourierDTO courier) throws IllegalStateException {
        return Stream.of(
            new ValidationRule(courier.getPhone(), new PhoneNumberValidator(), "Phone"),
            new ValidationRule(courier.getName(), new NameValidator(), "Name")
        )
        .filter(rule -> !rule.isValid())
        .map(rule -> (Error) new Error.ValidationError(rule.getErrorMessage()))
        .findFirst();
    }

    public Optional<Error> addCourier(CourierDTO courier) throws IllegalStateException {
        Optional<Error> validationError = validateCourier(courier);
        if (validationError.isPresent()) {
            return validationError;
        }

        switch (getCourier(courier.getPhone())) {
            case Result.Success<?,Courier> _ -> {
                return Optional.of(new Error.DuplicateKeyError(courier.getPhone()));
            }
            default -> {
                addRecord(courierService, courier);
                return Optional.empty();
            }
        }
    }

    public List<Courier> getAllCouriers() throws IllegalStateException {
        return getAllRecords(courierService);
    }

    public Result<Error, Courier> getCourier(String phone) throws IllegalStateException {
        return getRecord(courierService, phone);
    }

    public Optional<Error> updateCourier(CourierDTO courier) throws IllegalStateException {
        Optional<Error> validationError = validateCourier(courier);
        if (validationError.isPresent()) {
            return validationError;
        }

        if (!updateRecord(courierService, courier)) {
            return Optional.of(new Error.RecordNotFound(courier.getPhone()));
        }

        return Optional.empty();
    }

    public Optional<Error> deleteCourier(String phone) throws IllegalStateException {
        if (!deleteRecord(courierService, phone)) {
            return Optional.of(new Error.RecordNotFound(phone));
        }

        return Optional.empty();
    }

    public List<Pair<Courier, Integer>> getCouriersWithMostOrders(int numberOfRecords) throws IllegalStateException {
        return courierService.getCouriersWithMostOrders(numberOfRecords);
    }

    public void generateRandomCouriers(int numberOfRecords) {
        courierService.generateRandomCouriers(numberOfRecords);
    }

    public Optional<Error> validateMeal(MealDTO meal) throws IllegalStateException {
        List<ValidationRule> validationRules = Arrays.asList(
            new ValidationRule(meal.getName(), new MealNameValidator(), "Name")
        );
        return validateEntity(validationRules);
    }

    public Optional<Error> addMeal(MealDTO meal) throws IllegalStateException {
        Optional<Error> validationError = validateMeal(meal);
        if (validationError.isPresent()) {
            return validationError;
        }

        switch (getOrder(meal.getOrderID())) {
            case Result.Failure<Error, ?> failure -> {
                if (failure.error() instanceof Error.RecordNotFound _) {
                    return Optional.of(new Error.ForeignKeyConstraintError("Order ID", String.valueOf(meal.getOrderID())));
                }
                else {
                    return Optional.of(new Error.UnknownError());
                }
            }
            default -> {
                break;
            }
        }

        switch (getMeal(meal.getMealID())) {
            case Result.Success<?,Meal> _ -> {
                return Optional.of(new Error.DuplicateKeyError(String.valueOf(meal.getMealID())));
            }
            default -> {
                addRecord(mealService, meal);
                return Optional.empty();
            }
        }
    }

    public List<Meal> getAllMeals() throws IllegalStateException {
        return getAllRecords(mealService);
    }

    public Optional<Error> updateMeal(MealDTO meal) throws IllegalStateException {
        Optional<Error> validationError = validateMeal(meal);
        if (validationError.isPresent()) {
            return validationError;
        }

        if (!updateRecord(mealService, meal)) {
            return Optional.of(new Error.RecordNotFound(String.valueOf(meal.getMealID())));
        }

        return Optional.empty();
    }

    public Result<Error, Meal> getMeal(long mealID) throws IllegalStateException {
        return getRecord(mealService, mealID);
    }

    public Optional<Error> deleteMeal(long mealID) {
        if (!deleteRecord(mealService, mealID)) {
            return Optional.of(new Error.RecordNotFound(String.valueOf(mealID)));
        }

        return Optional.empty();
    }

    public Result<Error, Order> getOrder(long orderID) throws IllegalStateException {
        return getRecord(orderService, orderID);
    }

    public Optional<Error> validateOrder(OrderDTO order) throws IllegalStateException {
        List<ValidationRule> validationRules = Arrays.asList(
            new ValidationRule(order.getDeliveryAddress(), new AddressValidator(), "Address")
        );
        return validateEntity(validationRules);
    }

    public Optional<Error> addOrder(OrderDTO order) throws IllegalStateException {
        Optional<Error> validationError = validateOrder(order);
        if (validationError.isPresent()) {
            return validationError;
        }

        switch (getOrder(order.getOrderID())) {
            case Result.Success<?,Order> _ -> {
                return Optional.of(new Error.DuplicateKeyError(String.valueOf(order.getOrderID())));
            }
            default -> {
                break;
            }
        }

        switch (getCourier(order.getCourierPhone())) {
            case Result.Failure<Error, ?> failure -> {
                if (failure.error() instanceof Error.RecordNotFound _) {
                    return Optional.of(new Error.ForeignKeyConstraintError("Courier Phone", order.getCourierPhone()));
                }
                else {
                    return Optional.of(new Error.UnknownError());
                }
            }
            default -> {
                break;
            }
        }

        switch (getClient(order.getClientEmail())) {
            case Result.Failure<Error, ?> failure -> {
                if (failure.error() instanceof Error.RecordNotFound _) {
                    return Optional.of(new Error.ForeignKeyConstraintError("Client Email", order.getClientEmail()));
                }
                else {
                    return Optional.of(new Error.UnknownError());
                }
            }
            default -> {
                addRecord(orderService, order);
                return Optional.empty();
            }
        }
    }

    public List<Order> getAllOrders() throws IllegalStateException {
        return getAllRecords(orderService);
    }

    public Optional<Error> updateOrder(OrderDTO order) throws IllegalStateException {
        Optional<Error> validationError = validateOrder(order);
        if (validationError.isPresent()) {
            return validationError;
        }

        if (!updateRecord(orderService, order)) {
            return Optional.of(new Error.RecordNotFound(String.valueOf(order.getOrderID())));
        }

        return Optional.empty();
    }

    public Optional<Error> deleteOrder(long orderID) throws IllegalStateException {
        if (!deleteRecord(orderService, orderID)) {
            return Optional.of(new Error.RecordNotFound(String.valueOf(orderID)));
        }

        return Optional.empty();
    }

    public Result<Error, ClientAnalytics> fetchClientAnalytics(ClientFilterParameters parameters) throws IllegalStateException {
        EntityManager em = emf.createEntityManager();
    
        LocalDateTime startOrderDate;
        try {
            startOrderDate = LocalDateTime.parse(parameters.getOrderStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (DateTimeParseException e) {
            return new Result.Failure<>(new Error.ValidationError("Wrong date format."));
        }
    
        try {
            String jpql = """
                SELECT new com.lab2.common.ClientAnalytics(
                    c.name,
                    COUNT(DISTINCT o.orderID),
                    SUM(m.price)
                )
                FROM Client c
                JOIN Order o ON c.email = o.client.email
                JOIN Meal m ON o.orderID = m.order.orderID
                WHERE o.orderDate >= :startOrderDate
                AND m.price <= :maxMealPrice
                AND c.email LIKE :email
                GROUP BY c.email, c.name
                ORDER BY SUM(m.price) DESC
                """;

            // Begin SQL query measurement scope
            SQLQueryRuntime.beginScope();

            TypedQuery<ClientAnalytics> query = em.createQuery(jpql, ClientAnalytics.class);
            query.setParameter("startOrderDate", startOrderDate);
            query.setParameter("maxMealPrice", parameters.getMaxMealPrice());
            query.setParameter("email", parameters.getEmail());
    
            List<ClientAnalytics> resultList = query.getResultList();
    
            // End SQL query measurement scope
            SQLQueryRuntime.endScope();

            if (!resultList.isEmpty()) {
                return new Result.Success<>(resultList.get(0));
            } else {
                return new Result.Failure<>(new Error.RecordNotFound("No client analytics found."));
            }
        } catch (RuntimeException e) {
            throw new IllegalStateException("An unexpected error occurred while fetching client analytics.", e);
        } finally {
            em.close();
        }
    }

    public Result<Error, ArrayList<CourierAnalytics>> fetchCourierAnalytics(CourierFilterParameters parameters) throws IllegalStateException {
        LocalDateTime startDeliveryDate;
        try {
            startDeliveryDate = LocalDate.parse(parameters.getStartDeliveryDate()).atStartOfDay();
        } catch (DateTimeParseException e) {
            return new Result.Failure<>(new Error.ValidationError("Wrong date format."));
        }

        if (parameters.getMinRating() < 1 || parameters.getMinRating() > 5) {
            return new Result.Failure<>(new Error.ValidationError("Wrong rating."));
        }

        EntityManager em = emf.createEntityManager();
        ArrayList<CourierAnalytics> couriers = new ArrayList<>();

        try {
            final String jpql = """
                    SELECT new com.lab2.common.CourierAnalytics(
                        co.name,
                        co.phone,
                        AVG(o.rating),
                        MAX(o.deliveryDate),
                        MIN(o.orderDate)
                    )
                    FROM Courier co
                    JOIN Order o ON co.phone = o.courier.phone
                    WHERE o.deliveryDate >= :startDeliveryDate
                    AND o.rating >= :minRating
                    GROUP BY co.name, co.phone
                    ORDER BY AVG(o.rating) DESC
                    """;

            // Begin SQL query measurement scope
            SQLQueryRuntime.beginScope();

            TypedQuery<CourierAnalytics> query = em.createQuery(jpql, CourierAnalytics.class);
            query.setParameter("startDeliveryDate", startDeliveryDate);
            query.setParameter("minRating", parameters.getMinRating());

            // End SQL query measurement scope
            SQLQueryRuntime.endScope();

            couriers.addAll(query.getResultList());
            return new Result.Success<>(couriers);

        } catch (RuntimeException e) {
            throw new IllegalStateException("An unexpected error occurred while fetching courier analytics.", e);
        } finally {
            em.close();
        }
    }
}