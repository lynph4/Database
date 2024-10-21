package common;

public class CourierAnalytics {
    private String name;
    private String phone;
    private float averageRating;
    private String lastDeliveryDate;
    private String firstOrderDate;

    public CourierAnalytics(String name, String phone, float averageRating, String lastDeliveryDate, String firstOrderDate) {
        this.name = name;
        this.phone = phone;
        this.averageRating = averageRating;
        this.lastDeliveryDate = lastDeliveryDate;
        this.firstOrderDate = firstOrderDate;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public float getAverageRating() {
        return averageRating;
    }

    public String getLastDeliveryDate() {
        return lastDeliveryDate;
    }

    public String getFirstOrderDate() {
        return firstOrderDate;
    }
}