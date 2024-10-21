package common;

public class ClientFilterParameters {
    private String orderStartDate;
    private int maxMealPrice;
    private String email;

    public ClientFilterParameters(String orderStartDate, int maxMealPrice, String email) {
        this.orderStartDate = orderStartDate;
        this.maxMealPrice = maxMealPrice;
        this.email = email;
    }

    public String getOrderStartDate() {
        return orderStartDate;
    }

    public int getMaxMealPrice() {
        return maxMealPrice;
    }

    public String getEmail() {
        return email;
    }
}
