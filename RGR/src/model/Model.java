package model;

import java.sql.Timestamp;
import java.sql.Date;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;
import org.apache.commons.lang3.tuple.Pair;

import common.*;
import entities.*;
import model.validation.*;
import util.Error;
import util.Result;
import util.SQLQueryRuntime;

public class Model {
    private Connection connection;

    public Model(Connection connection) {
        this.connection = connection;
    }

    public Optional<Error> addClient(Client client) {
        if (!NameValidator.isValidName(client.name())) {
            return Optional.of(new Error.ValidationError("Wrong name."));
        }

        if (!EmailValidator.isValidEmail(client.email())) {
            return Optional.of(new Error.ValidationError("Wrong email."));
        }

        if (!PhoneNumberValidator.isValidPhoneNumber(client.phone())) {
            return Optional.of(new Error.ValidationError("Wrong phone number."));
        }

        Predicate<String> isClientExists = (String email) -> {
            final String sql = "SELECT EXISTS(SELECT 1 FROM \"Client\" WHERE \"Email\" = ?)";

            try (PreparedStatement pstmt = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                pstmt.setString(1, email);
                try (ResultSet resultSet = pstmt.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getBoolean(1);
                    }
                }
            } catch (SQLException e) {
                assert false;
            }
            return false;
        };

        if (isClientExists.test(client.email())) {
            return Optional.of(new Error.DuplicateKeyError(client.email()));
        }

        final String sql = "INSERT INTO \"Client\"(\"Email\", \"Name\", \"Phone\") VALUES(?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, client.email());
            pstmt.setString(2, client.name());
            pstmt.setString(3, client.phone());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("An unexpected error occurred while adding a client to the database.");
        }

        return Optional.empty();
    }

    public ArrayList<Client> getAllClients() throws IllegalStateException {
        ArrayList<Client> clients = new ArrayList<>();
        final String sql = "SELECT * FROM \"Client\"";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                String email = resultSet.getString("Email");
                String name = resultSet.getString("Name");
                String phone = resultSet.getString("Phone");

                Client client = new Client(email, name, phone);
                clients.add(client);
            }
        } catch (SQLException e){
            throw new IllegalStateException("An unexpected error occurred while collecting clients from the database.");
        }

        return clients;
    }

    public Result<Error, Client> getClient(String email) throws IllegalStateException {
        final String sql = "SELECT \"Name\", \"Phone\" FROM \"Client\" WHERE \"Email\" = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet resultSet = pstmt.executeQuery();
            
            if (resultSet.next()) {
                String name = resultSet.getString("Name");
                String phone = resultSet.getString("Phone");

                Client client = new Client(email, name, phone);
                return new Result.Success<>(client);
            }
        } catch (SQLException e){
            throw new IllegalStateException("An unexpected error occurred while collecting client from the database.");
        }

        return new Result.Failure<>(new Error.RecordNotFound(email));
    }

    public Optional<Error> updateClient(Client client) throws IllegalStateException {
        if (!NameValidator.isValidName(client.name())) {
            return Optional.of(new Error.ValidationError("Wrong name."));
        }

        if (!EmailValidator.isValidEmail(client.email())) {
            return Optional.of(new Error.ValidationError("Wrong email."));
        }

        if (!PhoneNumberValidator.isValidPhoneNumber(client.phone())) {
            return Optional.of(new Error.ValidationError("Wrong phone number."));
        }

        final String sql = "UPDATE \"Client\" SET \"Name\" = COALESCE(?, \"Name\"), \"Phone\" = COALESCE(?, \"Phone\") WHERE \"Email\" = ?";
    
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, client.name());
            pstmt.setString(2, client.phone());
            pstmt.setString(3, client.email());

            int rowCount = pstmt.executeUpdate();
            if (rowCount == 0) {
                return Optional.of(new Error.RecordNotFound(client.email()));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("An unexpected error occurred while updating the client with email '" +
                client.email() + "' from the database.");
        }

        return Optional.empty();
    }

    public Optional<Error> deleteClient(String email) {
        final String sql = "DELETE FROM \"Client\" WHERE \"Email\" = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);

            int rowCount = pstmt.executeUpdate();
            if (rowCount == 0) {
                return Optional.of(new Error.RecordNotFound(email));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("An unexpected error occurred while deleting the client with email '" +
                email + "' from the database.");
        }

        return Optional.empty();
    }

    public Optional<Pair<Client, Integer>> getClientWithMostOrders() {
        final String sql = """
                    WITH MaxOrderClient AS (
                    SELECT \"Client Email\", COUNT(*) AS OrderCount
                    FROM \"Order\"
                    GROUP BY \"Client Email\"
                    ORDER BY OrderCount DESC
                    LIMIT 1
                )
                SELECT c.*, moc.OrderCount
                FROM \"Client\" c
                JOIN MaxOrderClient moc ON c."Email" = moc.\"Client Email\";
                """;
                
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            SQLQueryRuntime.beginScope();
            ResultSet resultSet = pstmt.executeQuery();
            SQLQueryRuntime.endScope();
            
            if (resultSet.next()) {
                String email = resultSet.getString("Email");
                String name = resultSet.getString("Name");
                String phone = resultSet.getString("Phone");
                int orderCount = resultSet.getInt("OrderCount");

                Client client = new Client(email, name, phone);
                return Optional.of(Pair.of(client, Integer.valueOf(orderCount)));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("An unexpected error occurred while fetching a client with most orders from the database.");
        }

        return Optional.empty();
    }

    public void generateRandomClients(int numberOfRecords) {
        final String sql = """
                INSERT INTO \"Client\" (\"Email\", \"Name\", \"Phone\")
                SELECT
                    LOWER(first_name || '.' || last_name || FLOOR(RANDOM() * 10000000)::text || '@' || domain) AS \"Email\",
                    first_name || ' ' || last_name AS "Name",
                    LPAD(FLOOR(RANDOM() * 10000000000)::text, 10, '0') AS \"Phone\"
                FROM (
                    SELECT 
                        (ARRAY[
                            'Ava', 'Ben', 'Cal', 'Dan', 'Eli', 'Fin', 'Gus', 
                            'Hal', 'Ivy', 'Jax', 'Kai', 'Leo', 'Mia', 'Nia', 
                            'Oli', 'Pax', 'Ray', 'Sky', 'Tia', 'Zoe'
                        ])[FLOOR(RANDOM() * 20) + 1] AS first_name,

                        (ARRAY[
                            'Doe', 'Lee', 'Kim', 'Zhu', 'Wang', 'Liu', 
                            'Gar', 'Ali', 'Bai', 'Hsu', 'Roy', 'Joy', 
                            'Lin', 'Tan', 'Yin'
                        ])[FLOOR(RANDOM() * 15) + 1] AS last_name,

                        (ARRAY[
                            'ex.com', 'tm.com', 'sm.com', 'dm.com', 'rnd.com', 
                            'ml.com', 'd.com', 'svc.com', 'w.com', 'u.com'
                        ])[FLOOR(RANDOM() * 10) + 1] AS domain
                    FROM generate_series(1, ?) AS s
                ) AS names;
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, numberOfRecords);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("An unexpected error occurred while generating random clients in the database.");
        }
    }

    public Optional<Error> addCourier(Courier courier) {
        if (!PhoneNumberValidator.isValidPhoneNumber(courier.phone())) {
            return Optional.of(new Error.ValidationError("Wrong phone number."));
        }

        if (!NameValidator.isValidName(courier.name())) {
            return Optional.of(new Error.ValidationError("Wrong name."));
        }

        Predicate<String> isCourierExists = (String phone) -> {
            final String sql = "SELECT EXISTS(SELECT 1 FROM \"Courier\" WHERE \"Phone\" = ?)";

            try (PreparedStatement pstmt = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                pstmt.setString(1, phone);
                try (ResultSet resultSet = pstmt.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getBoolean(1);
                    }
                }
            } catch (SQLException e) {
                throw new IllegalStateException("An unexpected error occurred while checking the availability of the courier in the database.");
            }
            return false;
        };

        if (isCourierExists.test(courier.phone())) {
            return Optional.of(new Error.DuplicateKeyError(courier.phone()));
        }
        
        final String sql = "INSERT INTO \"Courier\"(\"Phone\", \"Name\", \"Transport\") VALUES(?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, courier.phone());
            pstmt.setString(2, courier.name());
            pstmt.setString(3, courier.transport());
            
            int rowCount = pstmt.executeUpdate();
            System.out.println("" + rowCount);
        } catch (SQLException e) {
            throw new IllegalStateException("An unexpected error occurred while adding a courier to the database.");
        }

        return Optional.empty();
    }

    public ArrayList<Courier> getAllCouriers() throws IllegalStateException {
        ArrayList<Courier> couriers = new ArrayList<>();
        final String sql = "SELECT * FROM \"Courier\"";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                String phone = resultSet.getString("Phone");
                String name = resultSet.getString("Name");
                String transport = resultSet.getString("Transport");

                Courier client = new Courier(phone, name, transport);
                couriers.add(client);
            }
        } catch (SQLException e){
            throw new IllegalStateException("An unexpected error occurred while collecting couriers from the database.");
        }

        return couriers;
    }

    public Result<Error, Courier> getCourier(String phone) throws IllegalStateException {
        final String sql = """
                SELECT * FROM \"Courier\"
                WHERE \"Phone\" = ?
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, phone);
            ResultSet resultSet = pstmt.executeQuery();
            
            if (resultSet.next()) {
                String name = resultSet.getString("Name");
                String transport = resultSet.getString("Transport");

                Courier courier = new Courier(phone, name, transport);
                return new Result.Success<>(courier);
            }
        } catch (SQLException e){
            throw new IllegalStateException("An unexpected error occurred while collecting courier from the database.");
        }

        return new Result.Failure<>(new Error.RecordNotFound(phone));
    }

    public Optional<Error> updateCourier(Courier courier) throws IllegalStateException {
        if (!PhoneNumberValidator.isValidPhoneNumber(courier.phone())) {
            return Optional.of(new Error.ValidationError("Wrong phone number."));
        }

        if (!NameValidator.isValidName(courier.name())) {
            return Optional.of(new Error.ValidationError("Wrong name."));
        }

        final String sql = "UPDATE \"Courier\" SET \"Name\" = COALESCE(?, \"Name\"), \"Transport\" = COALESCE(?, \"Transport\") WHERE \"Phone\" = ?";
    
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, courier.name());
            pstmt.setString(2, courier.transport());
            pstmt.setString(3, courier.phone());

            int rowCount = pstmt.executeUpdate();
            if (rowCount == 0) {
                return Optional.of(new Error.RecordNotFound(courier.phone()));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("An unexpected error occurred while updating the courier with phone number '" +
                courier.phone() + "' from the database.");
        }

        return Optional.empty();
    }

    public Optional<Error> deleteCourier(String phone) {
        final String sql = """
                DELETE FROM \"Courier\"
                WHERE \"Phone\" = ?
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, phone);

            int rowCount = pstmt.executeUpdate();
            if (rowCount == 0) {
                return Optional.of(new Error.RecordNotFound(phone));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("An unexpected error occurred while deleting the courier with phone number '" +
                phone + "' from the database.");
        }

        return Optional.empty();
    }

    public ArrayList<Pair<Courier, Integer>> getCouriersWithMostOrders(int numberOfRecords) {
        final String sql = """
                SELECT 
                    \"Courier\".\"Name\", 
                    \"Courier\".\"Phone\", 
                    \"Courier\".\"Transport\", 
                    COUNT(\"Order\".\"Order ID\") AS "Order Count"
                FROM \"Courier\"
                LEFT JOIN "Order" ON \"Courier\".\"Phone\" = \"Order\".\"Courier Phone\"
                GROUP BY \"Courier\".\"Name\", \"Courier\".\"Phone\"
                ORDER BY "Order Count" DESC
                LIMIT ?;
                """;

        ArrayList<Pair<Courier, Integer>> couriers = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, numberOfRecords);

            SQLQueryRuntime.beginScope();
            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                String name = resultSet.getString("Name");
                String phone = resultSet.getString("Phone");
                int orderCount = resultSet.getInt("Order Count");

                Courier courier = new Courier(phone, name, null);
                couriers.add(Pair.of(courier, Integer.valueOf(orderCount)));
            }

            SQLQueryRuntime.endScope();

        } catch (SQLException e) {
            throw new IllegalStateException("An unexpected error occurred while fetching couriers with most orders from the database.");
        }

        return couriers;
    }

    public void generateRandomCouriers(int numberOfRecords) {
        final String sql = """
                INSERT INTO \"Courier\" (\"Phone\", \"Name\", \"Transport\")
                SELECT
                    LPAD(FLOOR(RANDOM() * 10000000000)::text, 10, '0') AS \"Phone\",
                    (ARRAY['Alice', 'Bob', 'Charlie', 'David', 'Eve', 'Frank', 'Grace'])[FLOOR(RANDOM() * 7) + 1] || ' ' || 
                    (ARRAY['Smith', 'Johnson', 'Williams', 'Brown', 'Jones', 'Garcia', 'Miller'])[FLOOR(RANDOM() * 7) + 1] AS \"Name\",
                    (ARRAY['Bicycle', 'Motorbike', 'Van', 'Truck', 'Scooter'])[FLOOR(RANDOM() * 5) + 1] AS \"Transport\"
                FROM generate_series(1, ?) AS s;
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, numberOfRecords);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("An unexpected error occurred while generating random clients in the database.");
        }
    }

    public Optional<Error> addMeal(Meal meal) {
        if (!MealNameValidator.isValidName(meal.name())) {
            return Optional.of(new Error.ValidationError("Wrong name."));
        }

        switch (getOrder(meal.orderID())) {
            case Result.Failure<Error, ?> failure -> {
                if (failure.error() instanceof Error.RecordNotFound _) {
                    return Optional.of(new Error.ForeignKeyConstraintError("Order ID", String.valueOf(meal.orderID())));
                }
                else {
                    return Optional.of(new Error.UnknownError());
                }
            }
            case Result.Success<?,Order> success -> {
                break;
            }
        }

        Predicate<Integer> isMealExists = (Integer ID) -> {
            final String sql = "SELECT EXISTS(SELECT 1 FROM \"Meal\" WHERE \"Meal ID\" = ?)";

            try (PreparedStatement pstmt = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                pstmt.setInt(1, ID.intValue());
                try (ResultSet resultSet = pstmt.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getBoolean(1);
                    }
                }
            } catch (SQLException e) {
                throw new IllegalStateException("An unexpected error occurred while checking the availability of the meal in the database.");
            }
            return false;
        };

        if (isMealExists.test(Integer.valueOf(meal.mealID()))) {
            return Optional.of(new Error.DuplicateKeyError(String.valueOf(meal.mealID())));
        }

        final String sql = "INSERT INTO \"Meal\"(\"Meal ID\", \"Order ID\", \"Name\", \"Price\", \"Weight\", \"Serving Size\") VALUES(?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, meal.mealID());
            pstmt.setInt(2, meal.orderID());
            pstmt.setString(3, meal.name());
            pstmt.setInt(4, meal.price());
            pstmt.setInt(5, meal.weight());
            pstmt.setInt(6, meal.servingSize());
            
            if (pstmt.executeUpdate() == 0) {
                Optional.of(new Error.InsertError("Meal"));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("An unexpected error occurred while adding a meal to the database.");
        }

        return Optional.empty();
    }

    public ArrayList<Meal> getAllMeals() throws IllegalStateException {
        ArrayList<Meal> meals = new ArrayList<>();
        final String sql = "SELECT * FROM \"Meal\"";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                int mealID = resultSet.getInt("Meal ID");
                int orderID = resultSet.getInt("Order ID");
                String name = resultSet.getString("Name");
                int price = resultSet.getInt("Price");
                int weight = resultSet.getInt("Weight");
                int servingSize = resultSet.getInt("Serving Size");

                Meal meal = new Meal(mealID, orderID, name, price, weight, servingSize);
                meals.add(meal);
            }
        } catch (SQLException e){
            throw new IllegalStateException("An unexpected error occurred while collecting meals from the database.");
        }

        return meals;
    }

    public Optional<Error> updateMeal(Meal meal) throws IllegalStateException {
        if (!MealNameValidator.isValidName(meal.name())) {
            return Optional.of(new Error.ValidationError("Wrong name."));
        }

        final String sql = "UPDATE \"Meal\" SET \"Name\" = COALESCE(?, \"Name\"), \"Price\" = COALESCE(?, \"Price\"), \"Weight\" = COALESCE(?, \"Weight\"), \"Serving Size\" = COALESCE(?, \"Serving Size\") WHERE \"Meal ID\" = ?";
    
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, meal.name());
            pstmt.setInt(2, meal.price());
            pstmt.setInt(3, meal.weight());
            pstmt.setInt(4, meal.servingSize());
            pstmt.setInt(5, meal.mealID());

            int rowCount = pstmt.executeUpdate();
            if (rowCount == 0) {
                return Optional.of(new Error.RecordNotFound(String.valueOf(meal.mealID())));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("An unexpected error occurred while updating the meal with ID '" +
                meal.mealID() + "' from the database.");
        }

        return Optional.empty();
    }

    public Result<Error, Meal> getMeal(int mealID) throws IllegalStateException {
        final String sql = """
                SELECT * FROM \"Meal\"
                WHERE \"Meal ID\" = ?
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, mealID);
            ResultSet resultSet = pstmt.executeQuery();
            
            if (resultSet.next()) {
                int orderID = resultSet.getInt("Order ID");
                String name = resultSet.getString("Name");
                int price = resultSet.getInt("Price");
                int weight = resultSet.getInt("Weight");
                int servingSize = resultSet.getInt("Serving Size");

                Meal meal = new Meal(mealID, orderID, name, price, weight, servingSize);
                return new Result.Success<>(meal);
            }
        } catch (SQLException e){
            throw new IllegalStateException("An unexpected error occurred while collecting meal from the database.");
        }

        return new Result.Failure<>(new Error.RecordNotFound(String.valueOf(mealID)));
    }

    public Optional<Error> deleteMeal(int mealID) {
        final String sql = """
                DELETE FROM \"Meal\"
                WHERE \"Meal ID\" = ?
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, mealID);

            int rowCount = pstmt.executeUpdate();
            if (rowCount == 0) {
                return Optional.of(new Error.RecordNotFound(String.valueOf(mealID)));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("An unexpected error occurred while deleting the meal with ID '" +
                mealID + "' from the database.");
        }

        return Optional.empty();
    }

    public Result<Error, Order> getOrder(int orderID) throws IllegalStateException {
        final String sql = """
                SELECT * FROM \"Order\"
                WHERE \"Order ID\" = ?
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, orderID);
            ResultSet resultSet = pstmt.executeQuery();
            
            if (resultSet.next()) {
                String orderDate = resultSet.getTimestamp("Order Date").toString();
                String courierPhone = resultSet.getString("Courier Phone");
                String deliveryDate = resultSet.getTimestamp("Delivery Date").toString();
                String clientEmail = resultSet.getString("Client Email");
                int rating = resultSet.getInt("Rating");
                String deliveryAddress = resultSet.getString("Delivery Address");

                Order order = new Order(orderID, orderDate, courierPhone, deliveryDate, clientEmail, rating, deliveryAddress);
                return new Result.Success<>(order);
            }
        } catch (SQLException e){
            throw new IllegalStateException("An unexpected error occurred while collecting order from the database.");
        }

        return new Result.Failure<>(new Error.RecordNotFound(String.valueOf(orderID)));
    }

    public Optional<Error> addOrder(Order order) {
        Timestamp orderDate, deliveryDate;
        try {
            orderDate = Timestamp.valueOf(order.orderDate());
            deliveryDate = Timestamp.valueOf(order.deliveryDate());
        } catch (IllegalArgumentException e) {
            return Optional.of(new Error.ValidationError("Wrong date format."));
        }

        if (order.rating() < 1 || order.rating() > 5) {
            return Optional.of(new Error.ValidationError("Wrong delivery address."));
        }

        if (!AddressValidator.isValidAddress(order.deliveryAddress())) {
            return Optional.of(new Error.ValidationError("Wrong delivery address."));
        }

        Predicate<Integer> isOrderExists = (Integer ID) -> {
            final String sql = "SELECT EXISTS(SELECT 1 FROM \"Order\" WHERE \"Order ID\" = ?)";

            try (PreparedStatement pstmt = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                pstmt.setInt(1, ID.intValue());
                try (ResultSet resultSet = pstmt.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getBoolean(1);
                    }
                }
            } catch (SQLException e) {
                throw new IllegalStateException("An unexpected error occurred while checking the availability of the order in the database.");
            }
            return false;
        };

        if (isOrderExists.test(Integer.valueOf(order.orderID()))) {
            return Optional.of(new Error.DuplicateKeyError(String.valueOf(order.orderID())));
        }

        switch (getCourier(order.courierPhone())) {
            case Result.Failure<Error, ?> failure -> {
                if (failure.error() instanceof Error.RecordNotFound _) {
                    return Optional.of(new Error.ForeignKeyConstraintError("Courier Phone", order.courierPhone()));
                }
                else {
                    return Optional.of(new Error.UnknownError());
                }
            }
            case Result.Success<?,Courier> success -> {
                break;
            }
        }

        switch (getClient(order.clientEmail())) {
            case Result.Failure<Error, ?> failure -> {
                if (failure.error() instanceof Error.RecordNotFound _) {
                    return Optional.of(new Error.ForeignKeyConstraintError("Client Email", order.clientEmail()));
                }
                else {
                    return Optional.of(new Error.UnknownError());
                }
            }
            case Result.Success<?,Client> success -> {
                break;
            }
        }
        
        final String sql = "INSERT INTO \"Order\"(\"Order ID\", \"Order Date\", \"Courier Phone\", \"Delivery Date\", \"Client Email\", \"Rating\", \"Delivery Address\") VALUES(?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, order.orderID());
            pstmt.setTimestamp(2, orderDate);
            pstmt.setString(3, order.courierPhone());
            pstmt.setTimestamp(4, deliveryDate);
            pstmt.setString(5, order.clientEmail());
            pstmt.setInt(6, order.rating());
            pstmt.setString(7, order.deliveryAddress());

            if (pstmt.executeUpdate() == 0) {
                Optional.of(new Error.InsertError("Meal"));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("An unexpected error occurred while adding an order to the database.");
        }

        return Optional.empty();
    }

    public ArrayList<Order> getAllOrders() {
        ArrayList<Order> orders = new ArrayList<>();
        final String sql = "SELECT * FROM \"Order\"";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                int orderID = resultSet.getInt("Order ID");
                String orderDate = resultSet.getTimestamp("Order Date").toString();
                String courierPhone = resultSet.getString("Courier Phone");
                String deliveryDate = resultSet.getTimestamp("Delivery Date").toString();
                String clientEmail = resultSet.getString("Client Email");
                int rating = resultSet.getInt("Rating");
                String deliveryAddress = resultSet.getString("Delivery Address");

                Order order = new Order(orderID, orderDate, courierPhone, deliveryDate, clientEmail, rating, deliveryAddress);
                orders.add(order);
            }
        } catch (SQLException e){
            throw new IllegalStateException("An unexpected error occurred while collecting orders from the database.");
        }

        return orders;
    }

    public Optional<Error> updateOrder(Order order) {
        Timestamp deliveryDate;
        try {
            deliveryDate = Timestamp.valueOf(order.deliveryDate());
        } catch (IllegalArgumentException e) {
            return Optional.of(new Error.ValidationError("Wrong date format."));
        }

        if (order.rating() < 1 || order.rating() > 5) {
            return Optional.of(new Error.ValidationError("Wrong rating."));
        }

        if (!AddressValidator.isValidAddress(order.deliveryAddress())) {
            return Optional.of(new Error.ValidationError("Wrong delivery address."));
        }

        final String sql = "UPDATE \"Order\" SET \"Delivery Date\" = COALESCE(?, \"Delivery Date\"), \"Rating\" = COALESCE(?, \"Rating\"), \"Delivery Address\" = COALESCE(?, \"Delivery Address\") WHERE \"Order ID\" = ?";
    
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setTimestamp(1, deliveryDate);
            pstmt.setInt(2, order.rating());
            pstmt.setString(3, order.deliveryAddress());
            pstmt.setInt(4, order.orderID());

            int rowCount = pstmt.executeUpdate();
            if (rowCount == 0) {
                return Optional.of(new Error.RecordNotFound(String.valueOf(order.orderID())));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("An unexpected error occurred while updating the order with ID '" +
                order.orderID() + "' from the database.");
        }

        return Optional.empty();
    }

    public Optional<Error> deleteOrder(int orderID) {
        final String sql = """
                DELETE FROM \"Order\"
                WHERE \"Order ID\" = ?
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, orderID);

            int rowCount = pstmt.executeUpdate();
            if (rowCount == 0) {
                return Optional.of(new Error.RecordNotFound(String.valueOf(orderID)));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("An unexpected error occurred while deleting the order with ID '" +
                orderID + "' from the database.");
        }

        return Optional.empty();
    }

    public Result<Error, ClientAnalytics> fetchClientAnalytics(ClientFilterParameters parameters) {
        Timestamp startOrderDate;
        try {
            startOrderDate = Timestamp.valueOf(parameters.getOrderStartDate());
        } catch (IllegalArgumentException _) {
            return new Result.Failure<>(new Error.ValidationError("Wrong date format."));
        }

        final String sql = """
                SELECT 
                    c."Name" AS client_name, 
                    COUNT(DISTINCT o."Order ID") AS order_count,
                    SUM(m."Price") AS total_spent
                FROM "Client" c
                JOIN "Order" o ON c."Email" = o."Client Email"
                JOIN "Meal" m ON o."Order ID" = m."Order ID"
                WHERE o."Order Date" >= ?
                AND m."Price" <= ?
                AND c."Email" LIKE ?
                GROUP BY c."Email", c."Name"
                ORDER BY total_spent DESC;
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setTimestamp(1, startOrderDate);
            pstmt.setInt(2, parameters.getMaxMealPrice());
            pstmt.setString(3, parameters.getEmail());

            SQLQueryRuntime.beginScope();
            ResultSet resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                String clientName = resultSet.getString("client_name");
                int orderCount = resultSet.getInt("order_count");
                int totalSpent = resultSet.getInt("total_spent");
                SQLQueryRuntime.endScope();

                ClientAnalytics analytics = new ClientAnalytics(clientName, orderCount, totalSpent);
                return new Result.Success<>(analytics);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("An unexpected error occurred while fetching client analytics from the database.");
        }

        return new Result.Failure<>(new Error.RecordNotFound(""));
    }

    public Result<Error, ArrayList<CourierAnalytics>> fetchCourierAnalytics(CourierFilterParameters parameters) {
        Date startDeliveryDate;
        try {
            startDeliveryDate = Date.valueOf(parameters.getStartDeliveryDate());
        } catch (IllegalArgumentException _) {
            return new Result.Failure<>(new Error.ValidationError("Wrong date format."));
        }

        if (parameters.getMinRating() < 1 || parameters.getMinRating() > 5) {
            return new Result.Failure<>(new Error.ValidationError("Wrong rating."));
        }

        final String sql = """
                SELECT 
                    co."Name" AS courier_name, 
                    co."Phone", 
                    AVG(o."Rating") AS average_rating, 
                    MAX(o."Delivery Date") AS last_delivery_date,
                    MIN(o."Order Date") AS first_order_date
                FROM "Courier" co
                JOIN "Order" o ON co."Phone" = o."Courier Phone"
                JOIN "Meal" m ON o."Order ID" = m."Order ID"
                WHERE 
                    o."Delivery Date" >= ? AND
                    o."Rating" >= ?
                GROUP BY 
                    co."Name", 
                    co."Phone", 
                    co."Transport"
                ORDER BY average_rating DESC;
                """;

        ArrayList<CourierAnalytics> couriers = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDate(1, startDeliveryDate);
            pstmt.setInt(2, parameters.getMinRating());

            SQLQueryRuntime.beginScope();
            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                String courierName = resultSet.getString("courier_name");
                String phone = resultSet.getString("Phone");
                float averageRating = resultSet.getFloat("average_rating");
                String lastDeliveryDate = resultSet.getTimestamp("last_delivery_date").toString();
                String firstOrderDate = resultSet.getTimestamp("first_order_date").toString();

                CourierAnalytics analytics = new CourierAnalytics(courierName, phone, averageRating, lastDeliveryDate, firstOrderDate);
                couriers.add(analytics);
            }
            
            SQLQueryRuntime.endScope();

        } catch (SQLException e) {
            throw new IllegalStateException("An unexpected error occurred while fetching courier analytics from the database.");
        }

        return new Result.Success<>(couriers);
    }
}