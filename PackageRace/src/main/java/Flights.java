import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author
 * @created 09/12/2020
 * @project Group20
 */
public class Flights {

    List<Flight> flights;

    public Flights() {
        flights = new ArrayList<>();
    }

    public void addFlight(Flight flight){
        flights.add(flight);
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public String readAirportCode(String airportCode, JsonParser parser){
        String airportName ="";

        try {
            JsonObject jsonObject = (JsonObject) parser.parse(new FileReader("files/airportTimeZones.json"));
            airportName = jsonObject.get(airportCode).getAsString();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return airportName;
    }

}
