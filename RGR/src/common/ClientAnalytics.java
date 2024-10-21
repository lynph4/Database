package common;

public class ClientAnalytics {
    private String name;
    private int orderCount;
    private int totalSpent;

    public ClientAnalytics(String name, int orderCount, int totalSpent) {
        this.name = name;
        this.orderCount = orderCount;
        this.totalSpent = totalSpent;
    }

    public String getName() {
        return name;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public int getTotalSpent() {
        return totalSpent;
    }
}
