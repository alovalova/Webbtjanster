import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;

import java.text.SimpleDateFormat;
import java.util.Date;

import static jdk.vm.ci.sparc.SPARC.d2;

public class ConnectionFlight {
    private String destination;
    private String origin;
    private DateTime departureDateTime;
    private DateTime arrivalDateTime;
    private String departureDate;
    private String departureTime;
    private String arrivalDate;
    private String arrivalTime;
    private APIController controller;
    private String token;
    private String firstPossibleDepartureTime;
    private String firstPossibleDepartureDate;


    public ConnectionFlight(Flight previousFlight, APIController controller) {
        this.origin = previousFlight.getDestination();
        this.departureDate = previousFlight.getDepartureDate();
        this.controller = controller;
        this.firstPossibleDepartureTime = previousFlight.getArrivalTime();
        this.firstPossibleDepartureDate = previousFlight.getArrivalDate();

    }

    public void searchDestination(String origin, String departureDate) {
        token = controller.createAmadeusAuthentication();

        Unirest.config().defaultBaseUrl("https://test.api.amadeus.com/v1");

        HttpResponse<JsonNode> flightDestinationResponse = Unirest.get("/shopping/flight-destinations")
                .header("authorization", "Bearer " + token)
                .queryString("origin", origin)
                .queryString("departureDate", departureDate)
                .queryString("oneWay", "true")
                .queryString("nonStop", "true")
                .asJson();

        System.out.println("ConnectionFlight.searchFlights: Flight origin: " + origin + " flight departureDate: " + departureDate);
        System.out.println(flightDestinationResponse.getBody());
        JSONArray data = (JSONArray) flightDestinationResponse.getBody().getObject().get("data");

        destination = data.getJSONObject(0).get("destination").toString();

        checkNewDestinationDepartureTime();
    }

    private boolean checkNewDestinationDepartureTime() {
        boolean connectionPossible;
        Unirest.config().defaultBaseUrl("https://test.api.amadeus.com/v2");

        try {
            HttpResponse<JsonNode> flightArrivalTimeResponse = Unirest.get("/shopping/flight-offers")
                    .header("authorization", "Bearer " + token)
                    .queryString("originLocationCode", origin) //first time MAD --> next destination.
                    .queryString("destinationLocationCode", destination)
                    .queryString("departureDate", departureDate)
                    .queryString("adults", 1)
                    .queryString("nonStop", "true")
                    .queryString("max", 1)
                    .asJson();

            JSONArray meta = (JSONArray) flightArrivalTimeResponse.getBody().getObject().get("data");

            JSONObject data = meta.getJSONObject(0);

            JSONArray itineraries = data.getJSONArray("itineraries");

            JSONObject itinerary = itineraries.getJSONObject(0);

            JSONArray segments = itinerary.getJSONArray("segments");

            JSONObject segment = segments.getJSONObject(0);

            JSONObject departure = segment.getJSONObject("departure");

            JSONObject arrival = segment.getJSONObject("arrival");

            departureTime = departure.getString("at");
            arrivalTime = arrival.getString("at");

            if (!compareDepartureTimes()) {
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean compareDepartureTimes() {
        boolean possibleConnection=false;

        StringBuilder departureBuilder = new StringBuilder();
        StringBuilder firstPossibleDepartureBuilder = new StringBuilder();

        departureBuilder.append(departureDate + " " + departureTime);
        firstPossibleDepartureBuilder.append(firstPossibleDepartureDate + " " + firstPossibleDepartureTime);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Date departure = null;
        Date firstPossibleDeparture = null;
        int days = 0;
        int hours = 0;
        int minutes = 0;

        try {
            departure = format.parse(departureBuilder.toString());
            firstPossibleDeparture = format.parse(firstPossibleDepartureBuilder.toString());

            if(departure.compareTo(firstPossibleDeparture) > 0)
                possibleConnection = true;
            else
                possibleConnection = false;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return possibleConnection;
    }

}


