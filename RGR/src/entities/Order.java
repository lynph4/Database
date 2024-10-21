package entities;

public record Order(
    int orderID, 
    String orderDate, 
    String courierPhone, 
    String deliveryDate, 
    String clientEmail, 
    int rating, 
    String deliveryAddress) {
    }