import javax.servlet.ServletException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Chanon Borgström & Sofia Hallberg
 * @created 09/12/2020
 * @project Group20
 */

public class Response {
    private String packageDeliveryTime = "";
    private String packageDeliveryDate = "";
    private String packageRemainingHours = "";
    private List departureCities;
    private List departureTimes;
    private List arrivalCities;
    private List arrivalTimes;
    private List waitingTimes;

    public Response() {
        departureCities = new ArrayList();
        departureTimes = new ArrayList();
        arrivalCities = new ArrayList();
        arrivalTimes = new ArrayList();
        waitingTimes = new ArrayList();
    }

    public void setPackageRemainingHours(String packageRemainingHours) {
        this.packageRemainingHours = packageRemainingHours;
    }

    public void setPackageDeliveryDate(String packageDeliveryDate) {
        this.packageDeliveryDate = packageDeliveryDate;
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

    public void addWaitingTimes(int waitingTime) {
        waitingTimes.add(waitingTime);
    }

    public List getDepartureTimes() {
        return departureTimes;
    }

}