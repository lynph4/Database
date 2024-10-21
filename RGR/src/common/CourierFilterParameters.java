package common;

public class CourierFilterParameters {
    private String startDeliveryDate;
    private int minRating;

    public CourierFilterParameters(String startDeliveryDate, int minRating) {
        this.startDeliveryDate = startDeliveryDate;
        this.minRating = minRating;
    }

    public String getStartDeliveryDate() {
        return startDeliveryDate;
    }

    public int getMinRating() {
        return minRating;
    }
}
