import java.util.ArrayList;
import java.util.List;

public class Response {
    private String packageDeliveryTime = "2021-01-18 18:30";
    private List departureCities;
    private List departureTimes;
    private List arrivalCities;
    private List arrivalTimes;
    private List waitingTimes;
    private String errorMessage = "";

    public Response() {
        departureCities = new ArrayList();
        departureTimes = new ArrayList();
        arrivalCities = new ArrayList();
        arrivalTimes = new ArrayList();
        waitingTimes = new ArrayList();
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setPackageDeliveryTime(String packageDeliveryTime) {
        this.packageDeliveryTime = packageDeliveryTime;
    }

    public void addArrivalCity(String arrivalCity) {
        arrivalCities.add(arrivalCity);
    }

    public void addArrivalTime(String arrivalTime) {
        arrivalTimes.add(arrivalTime);
    }

    public void addDepartureCity(String departureCity) {
        departureCities.add(departureCity);
    }

    public void addDepartureTime(String departureTime) {
        departureTimes.add(departureTime);
    }
    public void addWaitingTimes(int waitingTime){
        waitingTimes.add(waitingTime);

    }
}