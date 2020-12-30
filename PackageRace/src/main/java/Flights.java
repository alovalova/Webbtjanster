import java.util.List;
import java.util.Stack;

/**
 * Class containing a number of Flight objects
 * @author Chanon Borgström & Sofia Hallberg
 * @created 09/12/2020
 * @project Group20
 */
public class Flights {

    List<Flight> flights;

    public Flights() {
        flights = new Stack<>();
    }

    public void addFlight(Flight flight){
        flights.add(flight);
    }

    public List<Flight> getFlights() {
        return flights;
    }
}
