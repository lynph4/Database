package entities;

public record Meal(
    int mealID,
    int orderID,
    String name, 
    int price, 
    int weight, 
    int servingSize) {
}