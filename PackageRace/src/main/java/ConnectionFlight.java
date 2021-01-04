import com.google.gson.Gson;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONException;
import kong.unirest.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * @author Chanon Borgström & Sofia Hallberg
 * @created 30/12/2020
 * @project Group20
 */
public class ConnectionFlight {
    private String destination;
    private String origin;
    private String departureDate;
    private String nextDepartureDate;
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
    private String printClassMsg = "ConnectionFlight.";


    private int waitingTime;

    private ConnectionFlight previousFlight;
    private int destinationIndex = 0;
    private int nextDateIndex = 0;

    public ConnectionFlight() {
    }

    /**
     * creates a ConnectionFlight
     *
     * @param origin        the origin for the flight
     * @param departureDate date of departure for the connectionFlight
     * @param controller    the used controller
     */
    public ConnectionFlight(String origin, String departureDate, APIController controller) {
        this.origin = origin;
        this.departureDate = departureDate;
        nextDepartureDate = departureDate;
        this.controller = controller;
        this.firstPossibleDepartureTime = "08:30";
        this.firstPossibleDepartureDate = departureDate;
        gson = new Gson();
        destinationList = new ArrayList<>();
        controller.createAmadeusAuthentication();
        token = controller.getToken();
    }

    /**
     * creates a ConnectionFlight
     *
     * @param previousFlight  the flight to connect to
     * @param controller the used controller
     */
    public ConnectionFlight(ConnectionFlight previousFlight, APIController controller) {
        this.previousFlight = previousFlight;
        this.origin = previousFlight.getDestination();
        this.departureDate = previousFlight.getArrivalDate();
        nextDepartureDate = departureDate;
        this.controller = controller;
        this.firstPossibleDepartureTime = previousFlight.getArrivalTime();
        this.firstPossibleDepartureDate = previousFlight.getArrivalDate();
        gson = new Gson();
        destinationList = new ArrayList<>();
        token = controller.getToken();

        System.out.print("\n" + printClassMsg + "ConnectionFlight: flight's values: ");
        System.out.print("departureTime: " + previousFlight.getDepartureTime());
        System.out.print(" departureDate: " + previousFlight.getDepartureDate());
        System.out.print(" arrivalTime: " + previousFlight.getArrivalTime());
        System.out.print(" arrivalDate: " + previousFlight.getArrivalDate() + "\n");
    }

    /**
     * Search all possible destination from the origin on the departure date and puts the destination into an array
     * and populates the connectionFlight with the first possible destination and arrival time
     */
    public void searchDestination(String departureDate) {
        Unirest.config().defaultBaseUrl("https://test.api.amadeus.com/v1");

        try {
            HttpResponse<JsonNode> flightDestinationResponse = Unirest.get("/shopping/flight-destinations")
                    .header("authorization", "Bearer " + token)
                    .queryString("origin", origin)
                    .queryString("departureDate", departureDate)
                    .queryString("oneWay", "true")
                    .queryString("nonStop", "true")
                    .asJson();

//            System.out.println(printClassMsg + "searchDestination: Flight origin: " + origin + " flight departureDate: " + departureDate);

            JSONObject flightData = flightDestinationResponse.getBody().getObject();
            JSONArray flights = flightData.getJSONArray("data");
            System.out.println(printClassMsg + "searchDestination.tryPhrase: " + flightDestinationResponse.getBody());
            if (flights.length() > 0) {
                for (int i = 0; i < flights.length(); i++) {
                    JSONObject flight = flights.getJSONObject(i);
                    destinationList.add(flight.get("destination").toString());
                }
            }
        }
        //TODO: fortsätt här imorgon och försök lösa att ta nästa destination så att inte programmet stannar.
        catch (JSONException e) {
            if (destinationIndex > previousFlight.getDestinationList().size() - 1) {
                controller.createResponse();
                controller.createErrorMessageResponse("Amadeus: ConnectionFlight.searchDestination.CatchPhrase.NoMoreDestinationsInList");
            } else {
                try {
                    if (nextDateIndex < 5) {
                        nextDepartureDate = getNextDate(departureDate);
                        nextDateIndex++;
                        searchDestination(nextDepartureDate);
                    }
                } catch (ParseException parseException) {
                    parseException.printStackTrace();
                }
                origin = previousFlight.getDestinationList().get(destinationIndex);
                System.out.println(printClassMsg + "searchDestination.catchPhrase: nextOrigin: " + origin + " destinationListSize: " + previousFlight.getDestinationList().size());
                destinationIndex++;
                nextDateIndex = 0;
                nextDepartureDate = this.departureDate;
                searchDestination(nextDepartureDate);
            }

        }
        System.out.println(printClassMsg + "searchDestination.afterCatchPhrase");

        if (!checkNewDestinationAndDepartureTime()) {
            try {
                nextDepartureDate = getNextDate(departureDate);
                System.out.println(printClassMsg + "SearchDestination: getNextDate");
                searchDestination(nextDepartureDate);
            } catch (ParseException e) {
                controller.createResponse();
                controller.createErrorMessageResponse("Amadeus: ConnectionFlight.searchDestination.CatchPhrase.TimeAndDateIsOut");
            }
        } else {
            System.out.println(printClassMsg + "searchDestination: is created with destination: " + destination);
            destinationIndex =0;
            controller.checkIfTimeIsLeft(controller.getaPackage(),this);
        }
    }


    /**
     * Check if a flight to a destination is possible to connect to
     *
     * @return true if the flight to the destination is possible to connect to
     */
    private boolean checkNewDestinationAndDepartureTime() {
        boolean destinationIs = false;
        Unirest.config().defaultBaseUrl("https://test.api.amadeus.com/v2");
        int destinationIndex = 0;
        //System.out.println(printClassMsg + "checkNewDestinationAndDepartureTime: DepartureDate: " + departureDate + " origin: " + origin);

        while (!destinationIs){
            try {
                if (destinationIndex < destinationList.size()-1) {
                    System.out.println(printClassMsg + "checkNewDestinationAndDepartureTime: origin: " + origin);
                    System.out.println(printClassMsg + "checkNewDestinationAndDepartureTime: destination: " + destinationList.get(destinationIndex));

                    HttpResponse<JsonNode> flightArrivalTimeResponse = Unirest.get("/shopping/flight-offers")
                            .header("authorization", "Bearer " + token)
                            .queryString("originLocationCode", origin)
                            .queryString("destinationLocationCode", destinationList.get(destinationIndex))
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

                    setDepartureDateAndTime(departure.getString("at"));
                    setArrivalDateAndTime(arrival.getString("at"));

                    if (!compareDepartureTimes()) {
                        System.out.println(printClassMsg + "checkNewDestinationAndDepartureTime: compareDepartureTimes is false");
                        destinationIndex++;
                    } else {
                        this.destination = arrival.getString("iataCode");
                        System.out.println(printClassMsg + "checkNewDestinationAndDepartureTime: compareDepartureTimes is true");
                        setDuration(itinerary.get("duration").toString());
                        return true;
                    }
                }else{
                    System.out.println(printClassMsg + "checkNewDestinationAndDepartureTime: is false");
                    destinationIs =true;
                    return false;
                }

            } catch (Exception e) {
                try {
                    if (destinationIndex < destinationList.size()-1) {
                        destinationIndex++;
                    }else{
                        if (nextDateIndex < 5) {
                            nextDepartureDate = getNextDate(departureDate);
                            departureDate = nextDepartureDate;
                            System.out.println(printClassMsg + "checkNewDestinationAndDepartureTimeCatchPhrase: getNextDate");
                            nextDateIndex++;
                            searchDestination(departureDate);

                        } else {
                            controller.createResponse();
                            controller.createErrorMessageResponse("Amadeus: ConnectionFlight.checkNewDestinationAndDepartureTime.TimeAndDateIsOut");
                        }
                    }
                } catch (ParseException parseException) {
                    controller.createResponse();
                    controller.createErrorMessageResponse("Amadeus: ConnectionFlight.checkNewDestinationAndDepartureTime.NoMoreDestination");
                }
            }
        }
        return destinationIs;
    }

    /**
     * Assigns the local parameters departureDate and departureTime with values from a string
     *
     * @param dateTime the string representation of departure date and time
     */
    public void setDepartureDateAndTime(String dateTime) {
        departureDate = dateTime.substring(0, 10);
        departureTime = dateTime.substring(11, 16);
    }

    /**
     * Assigns the local parameters arrivalDate and arrivalTime with values from a string
     *
     * @param dateTime the string representation of arrival date and time
     */
    public void setArrivalDateAndTime(String dateTime) {
        arrivalDate = dateTime.substring(0, 10);
        arrivalTime = dateTime.substring(11, 16);
    }

    /**
     * Assigns the local parameter duration with value from a string
     *
     * @param duration the string representation of arrival date and time
     */
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

    /**
     * Check if a connection flight is possible by comparing the connection flights departure time with the previous flights arrival time
     *
     * @return possibleConnection set to true if the connection flight is possible
     */
    public boolean compareDepartureTimes() {
        boolean possibleConnection = false;

        StringBuilder departureBuilder = new StringBuilder();
        StringBuilder firstPossibleDepartureBuilder = new StringBuilder();

        departureBuilder.append(departureDate).append(" ").append(departureTime);
        firstPossibleDepartureBuilder.append(firstPossibleDepartureDate).append(" ").append(firstPossibleDepartureTime);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Date departure = null;
        Date firstPossibleDeparture = null;

        try {
            departure = format.parse(departureBuilder.toString());
            firstPossibleDeparture = format.parse(firstPossibleDepartureBuilder.toString());

            if (departure.compareTo(firstPossibleDeparture) > 0)
                possibleConnection = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return possibleConnection;
    }

    /**
     * Takes a string representing a date and converts it to a string representing the next day
     *
     * @return a string representtion of the next date
     */
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

    public String getDestination() {
        return destination;
    }

    public int getDuration() {
        return duration;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(String arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getFirstPossibleDepartureTime() {
        return firstPossibleDepartureTime;
    }

    public void setFirstPossibleDepartureTime(String firstPossibleDepartureTime) {
        this.firstPossibleDepartureTime = firstPossibleDepartureTime;
    }

    public String getFirstPossibleDepartureDate() {
        return firstPossibleDepartureDate;
    }

    public void setFirstPossibleDepartureDate(String firstPossibleDepartureDate) {
        this.firstPossibleDepartureDate = firstPossibleDepartureDate;
    }

    public ArrayList<String> getDestinationList() {
        return destinationList;
    }

    public void setDestinationList(ArrayList<String> destinationList) {
        this.destinationList = destinationList;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }
}


