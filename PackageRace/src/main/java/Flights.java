import java.util.List;
import java.util.Stack;

/**
 * Class containing a number of ConnectionFlight objects
 * @author Chanon Borgström & Sofia Hallberg
 * @created 09/12/2020
 * @project Group20
 */
public class Flights {

    List<ConnectionFlight> flights;

    public Flights() {
        flights = new Stack<>();
    }

    public void addFlight(ConnectionFlight flight){
        flights.add(flight);
    }

    public List<ConnectionFlight> getFlights() {
        return flights;
    }
}
