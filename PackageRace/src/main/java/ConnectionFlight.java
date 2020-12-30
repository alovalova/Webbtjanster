import com.google.gson.Gson;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ConnectionFlight extends Flight {
    private String destination;
    private String origin;
    private String departureDate;
    private String departureTime;
    private String arrivalDate;
    private String arrivalTime;
    private APIController controller;
    private String token;
    private String firstPossibleDepartureTime;
    private String firstPossibleDepartureDate;
    private int duration;
    private Gson gson;
    private ArrayList<String> destinationList;

    public ConnectionFlight(String origin, String departureDate, APIController controller) {
        this.origin = origin;
        this.departureDate = departureDate;
        this.controller = controller;
        this.firstPossibleDepartureTime = "08:30";
        this.firstPossibleDepartureDate = departureDate;
        gson = new Gson();
        destinationList = new ArrayList<>();
        token = controller.createAmadeusAuthentication();
    }

    public ConnectionFlight(Flight previousFlight, APIController controller) {
        this.origin = previousFlight.getDestination();
        System.out.println("getDestination i konstruktorn "+previousFlight.getDestination());
        this.departureDate = previousFlight.getDepartureDate();
        this.controller = controller;
        this.firstPossibleDepartureTime = previousFlight.getArrivalTime();
        this.firstPossibleDepartureDate = previousFlight.getArrivalDate();
        gson = new Gson();
        destinationList = new ArrayList<>();
        token = controller.createAmadeusAuthentication();
    }

    public void searchDestination(String origin, String departureDate) {
        this.origin=origin;

        Unirest.config().defaultBaseUrl("https://test.api.amadeus.com/v1");

        try {
            HttpResponse<JsonNode> flightDestinationResponse = Unirest.get("/shopping/flight-destinations")
                    .header("authorization", "Bearer " + token)
                    .queryString("origin", origin)
                    .queryString("departureDate", departureDate)
                    .queryString("oneWay", "true")
                    .queryString("nonStop", "true")
                    .asJson();

            System.out.println("ConnectionFlight.searchFlights: Flight origin: " + origin + " flight departureDate: " + departureDate);
            // System.out.println(flightDestinationResponse.getBody());
            JSONArray data = (JSONArray) flightDestinationResponse.getBody().getObject().get("data");

            JSONObject flightData = flightDestinationResponse.getBody().getObject();
            // Skapa en array flights med JSON-flight-objekt
            JSONArray flights = flightData.getJSONArray("data");
            for (int i = 0; i < flights.length(); i++) {
                JSONObject flight = flights.getJSONObject(i);
                destinationList.add(flight.get("destination").toString());
                //System.out.println(destinationList.get(i));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //destination = data.getJSONObject(0).get("destination").toString();

        if (!checkNewDestinationDepartureTime()) {
            try {
                departureDate = getNextDate(departureDate);
                System.out.println("SearchDestination getNextDate");
                searchDestination(this.origin, departureDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }

    private boolean checkNewDestinationDepartureTime() {
        boolean destinationPossible = false;
        Unirest.config().defaultBaseUrl("https://test.api.amadeus.com/v2");
        int destinationIndex = 0;
        System.out.println("Origin i andra API-anropet: " + origin);

        while (!destinationPossible) {
            try {
                if (destinationIndex < destinationList.size()) {
                    HttpResponse<JsonNode> flightArrivalTimeResponse = Unirest.get("/shopping/flight-offers")
                            .header("authorization", "Bearer " + token)
                            .queryString("originLocationCode", origin)
                            .queryString("destinationLocationCode", destinationList.get(destinationIndex))
                            .queryString("departureDate", departureDate)
                            .queryString("adults", 1)
                            .queryString("nonStop", "true")
                            .queryString("max", 1)
                            .asJson();

                    System.out.println("FlightArrivalTimeResponse: " + flightArrivalTimeResponse.getBody());


                    JSONArray meta = (JSONArray) flightArrivalTimeResponse.getBody().getObject().get("data");
                    JSONObject data = meta.getJSONObject(0);
                    JSONArray itineraries = data.getJSONArray("itineraries");
                    JSONObject itinerary = itineraries.getJSONObject(0);
                    JSONArray segments = itinerary.getJSONArray("segments");
                    JSONObject segment = segments.getJSONObject(0);
                    JSONObject departure = segment.getJSONObject("departure");
                    JSONObject arrival = segment.getJSONObject("arrival");

                    setDepartureDateAndTime(departure.getString("at"));
                    setArrivalDateAndTime(arrival.getString("at"));


                    if (!compareDepartureTimes()) {
                        System.out.println("CheckNewDestinationDepartureTime if !compareDepartureTimes");
                        destinationIndex++;
                        if (destinationIndex == destinationList.size()) {
                            destinationPossible = false;
                            return false;
                        }
                    } else {
                        System.out.println("CheckNewDestinationDepartureTime if compareDepartureTimes");
                        this.destination = arrival.getString("iataCode");
                        System.out.println("Destination is: " +destination);
                        setDuration(itinerary.get("duration").toString());
                        destinationPossible = true;

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;

    }

    public void setDepartureDateAndTime(String dateTime) {
        departureDate = dateTime.substring(0, 10);
        departureTime = dateTime.substring(11, 16);
    }

    public void setArrivalDateAndTime(String dateTime) {
        arrivalDate = dateTime.substring(0, 10);
        arrivalTime = dateTime.substring(11, 16);
    }

    public void setDuration(String duration) {
        String[] durationArray = duration.split("PT");
        String hoursMinutes = durationArray[1];
        String[] hoursMinutesArray = hoursMinutes.split("H");
        int hours = Integer.parseInt(hoursMinutesArray[0]);
        int minutes = Integer.parseInt(hoursMinutesArray[1].split("M")[0]);

        if (minutes >= 30) {
            hours++;
        }
        this.duration = hours;
    }

    public boolean compareDepartureTimes() {
        boolean possibleConnection = false;

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

            if (departure.compareTo(firstPossibleDeparture) > 0)
                possibleConnection = true;
            else
                possibleConnection = false;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return possibleConnection;
    }

    public static String getNextDate(String curDate) throws ParseException {
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        final Date date = format.parse(curDate);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        return format.format(calendar.getTime());
    }


    public String getOrigin() {
        return origin;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    @Override
    public String getDestination() {
        return destination;
    }
}


