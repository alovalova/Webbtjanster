import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    private List departureCities;
    private List departureTimes;
    private List arrivalCities;
    private List arrivalTimes;
    private List waitingTimes;
    private List<ShowError> errorMessages;

    public Response() {
        departureCities = new ArrayList();
        departureTimes = new ArrayList();
        arrivalCities = new ArrayList();
        arrivalTimes = new ArrayList();
        waitingTimes = new ArrayList();
        errorMessages = new ArrayList<>();
    }

    public void addErrorMessage(int httpCode,String errorMessage) {
        new ShowError(httpCode,errorMessage);
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

    public class ShowError extends HttpServlet {
        private int httpCode;
        private String errorMessage;

        public ShowError(int httpCode, String errorMessage) {
            this.httpCode = httpCode;
            this.errorMessage = errorMessage;
        }

        // Method to handle GET method request.
        public void doGet(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {

            // Set error code and reason.
            try {
                System.out.println("error: " + errorMessage + " httpCode: " + httpCode);
                response.sendError(httpCode, errorMessage);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}