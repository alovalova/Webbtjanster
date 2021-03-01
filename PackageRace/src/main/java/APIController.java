import com.google.gson.Gson;
import org.json.simple.parser.JSONParser;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Controller class
 *
 * @author Chanon Borgström & Sofia Hallberg
 * @created 16/12/2020
 * @project Group20
 */
public class APIController {

    private Flights flights;
    private Package aPackage;
    private int remainingHours;

    private String token;

    private String printClassMsg = "APIController.";

    private boolean responseDone;

    private Gson gson;
    private Response res;
    private List<String> errorMessages;
    private int httpCode;

    public APIController() {
        gson = new Gson();
        errorMessages = new ArrayList<>();
    }


    /**
     * Call to Post Nord's API to get delivery time and date for the package and populates the package with the delivery values
     *
     * @param aPackage the package which is to be checked when to be delivered.
     * @return true if the call is successful
     */
    public boolean createPostNordAPIGetRequest(Package aPackage) {
        System.out.println("kommer hit?");
        Unirest.config().defaultBaseUrl("https://api2.postnord.com/rest/transport");
        System.out.println("men inte hit?");
        HttpResponse<JsonNode> res = null;

        try {
            if (aPackage.checkPackage()) {
                res = Unirest.get("/v1/transittime/getTransitTimeInformation.json")
                        .queryString("apikey", "25a3a56f393275f8855069acbc67e196") //fdb2ec79dd43fa36ef38b82d9dd0d10e
                        .queryString("dateOfDeparture", aPackage.getPackageDepartureDate())
                        .queryString("serviceCode", "18")
                        .queryString("serviceGroupCode", aPackage.getDepartureCountry())
                        .queryString("fromAddressPostalCode", aPackage.getDepartureZip())
                        .queryString("fromAddressCountryCode", aPackage.getDepartureCountry())
                        .queryString("toAddressPostalCode", aPackage.getArrivalZip())
                        .queryString("toAddressCountryCode", aPackage.getArrivalCountry())

                        .header("Accept", "application/json")
                        .asJson();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            JSONObject transitTimeResponse = (JSONObject) res.getBody().getObject().get("se.posten.loab.lisp.notis.publicapi.serviceapi.TransitTimeResponse");
            JSONArray transitTimes = (JSONArray) transitTimeResponse.get("transitTimes");

            String deliveryTime = transitTimes.getJSONObject(0).get("deliveryTime").toString();
            String deliveryDate = (String) transitTimes.getJSONObject(0).get("deliveryDate");

            System.out.println(printClassMsg + "createPostNordAPIGetRequest: Delivery Time: " + deliveryTime + " Delivery Date: " + deliveryDate);

            aPackage.setPackageArrivalTime(deliveryTime);
            aPackage.setPackageArrivalDate(deliveryDate);
            aPackage.setPostNordResponse(true);
        } catch (Exception e) {
            createErrorMessageResponse(400, "Invalid values");
            return false;
        }
        if (aPackage.postNordResponseOk()) {
            remainingHours = aPackage.getTransitTime();
        } else {
            createErrorMessageResponse(400, "Invalid values");
        }
        return true;
    }

    /**
     * Creates a Flight object for the first flight and a Flights object
     *
     * @param aPackage the package racing with the flight
     * @return true if there is time left after the flight
     */
    public boolean startFlying(Package aPackage) {
        this.aPackage = aPackage;
        remainingHours = aPackage.getTransitTime();
        flights = new Flights();

        String packageDepartureDate = aPackage.getPackageDepartureDate();

        ConnectionFlight startFlight = new ConnectionFlight("MAD", packageDepartureDate, this); //startFlight skapas med origin mad och depdate som paketet
        if (startFlight.searchDestination(packageDepartureDate)) {
             //startFlight får ankomstort och ankomsttid
            System.out.print("\n" + printClassMsg + "startFlying: startFlight's values: ");
            System.out.print("departureTime: " + startFlight.getDepartureTime());
            System.out.print(" departureDate: " + startFlight.getDepartureDate());
            System.out.print(" arrivalTime: " + startFlight.getArrivalTime());
            System.out.print(" arrivalDate: " + startFlight.getArrivalDate());
        }else{

            return false;
        }
        if (checkIfTimeIsLeft(aPackage, startFlight)){
            return true;
        }else{
            return false;
        }
    }

    /**
     * Creates a Flight object for a succeeding flight
     *
     * @param aPackage       the package racing with the flight
     * @param previousFlight the previous flight in need of a connection flight
     */
    private void continueFlying(Package aPackage, ConnectionFlight previousFlight) {
        ConnectionFlight nextFlight = new ConnectionFlight(previousFlight, this);
        nextFlight.searchDestination(nextFlight.getDepartureDate());
        checkIfTimeIsLeft(aPackage, nextFlight);
    }

    /**
     * Check that a Flight-objects variables are assigned
     *
     * @param flight the Flight object to check
     * @return true if all parameters are assigned
     */
    public boolean checkFlight(ConnectionFlight flight) {
        return flight.getDepartureTime() != null &&
                flight.getArrivalTime() != null &&
                flight.getArrivalDate() != null;
    }

    /**
     * Decides if there is time left for an additional flight or if the transit time is exceeded
     *
     * @param aPackage the Package object racing with the flights
     * @param flight   the last flight in the race
     * @return true if there is time left after the flight
     */
    public boolean checkIfTimeIsLeft(Package aPackage, ConnectionFlight flight) {
        if (timeIsLeft(aPackage, flight)) {
            System.out.println(printClassMsg + "checkIfTimeIsLeft: time is left");
            flights.addFlight(flight);
            continueFlying(aPackage, flight);
            return true;
        } else {
            System.out.println("\n" + printClassMsg + "checkIfTimeIsLeft: time is not left");
            createResponse();
            return false;
        }
    }

    /**
     * Checks if there is time left for an additional flight
     *
     * @param flight   the last flight in the race
     * @param aPackage te Package racing with the flights
     * @return true if there is time left
     */
    public boolean timeIsLeft(Package aPackage, ConnectionFlight flight) {
        System.out.print("\n" + printClassMsg + "timeIsLeft: flight's values: ");
        System.out.print("departureTime: " + flight.getDepartureTime());
        System.out.print(" departureDate: " + flight.getDepartureDate());
        System.out.print(" arrivalTime: " + flight.getArrivalTime());
        System.out.print(" arrivalDate: " + flight.getArrivalDate());
        if (!checkFlight(flight)) {
            return false;
        }

        int previous = flights.getFlights().size() - 1;
        int waitingTime;
        if (previous >= 0) {
            ConnectionFlight previousFlight = flights.getFlights().get(previous);
            if (previousFlight.getDepartureTime() == null) {
                return false;
            }
            System.out.println("\n" + printClassMsg + "timeIsLeft: Flight heading to " + previousFlight.getDestination() + " is created");
            waitingTime = calculateWaitingTime(previousFlight);
            previousFlight.setWaitingTime(waitingTime);

        } else {
            ConnectionFlight firstFlight = new ConnectionFlight();
            firstFlight.setDepartureTime("08:30");
            firstFlight.setDepartureDate(aPackage.getPackageDepartureDate());
            firstFlight.setArrivalTime(flight.getArrivalTime());
            firstFlight.setArrivalDate(flight.getArrivalDate());
            System.out.print("\n" + printClassMsg + "timeIsLeft: firstTestFlight created ");
            System.out.print("departureTime: " + firstFlight.getDepartureTime());
            System.out.print(" departureDate: " + firstFlight.getDepartureDate());
            System.out.print(" arrivalTime: " + firstFlight.getArrivalTime());
            System.out.print(" arrivalDate: " + firstFlight.getArrivalDate());

            waitingTime = calculateWaitingTime(firstFlight);
            flight.setWaitingTime(waitingTime);
        }

        int flightDuration = flight.getDuration(); // 3h

        remainingHours = remainingHours - flightDuration;
        remainingHours = remainingHours - waitingTime;
        if (remainingHours > 0) {
            System.out.println("\n" + printClassMsg + "timeIsLeft: remainingHours: " + remainingHours + " flightDuration: " + flightDuration + " waitingTime: " + waitingTime);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Calculate the waiting time to the next flight
     *
     * @param previousFlight the arriving flight before a connection flight
     * @return the waiting time in hours
     */
    public int calculateWaitingTime(ConnectionFlight previousFlight) {
        int waitingTime;
        StringBuilder arrived = new StringBuilder();
        StringBuilder departed = new StringBuilder();

        String departureTime = previousFlight.getDepartureTime();
        String departureDate = previousFlight.getDepartureDate();

        String arrivalTime = previousFlight.getArrivalTime();
        String arrivalDate = previousFlight.getArrivalDate();

        arrived.append(arrivalDate + " " + arrivalTime);
        departed.append(departureDate + " " + departureTime);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Date depart = null;
        Date arrive = null;
        int days = 0;
        int hours = 0;
        int minutes = 0;

        try {
            depart = format.parse(departed.toString());
            arrive = format.parse(arrived.toString());

            DateTime dep = new DateTime(depart);
            DateTime arr = new DateTime(arrive);

            days = Days.daysBetween(dep, arr).getDays();
            hours = Hours.hoursBetween(dep, arr).getHours() % 24;
            minutes = Minutes.minutesBetween(dep, arr).getMinutes() % 60;
        } catch (Exception e) {
            e.printStackTrace();
        }

        waitingTime = countWaitingTime(days, hours, minutes);
        return waitingTime;
    }

    /**
     * Calculates hours from days, hours and minutes
     *
     * @param days
     * @param hours
     * @param minutes
     * @return days, hours and minutes calculated as hours
     */
    public int countWaitingTime(int days, int hours, int minutes) {
        int transitHours = 0;
        int addToHours = 0;

        if (minutes > 30) {
            addToHours = 1;
        }

        transitHours = days * 24 + hours + addToHours;

        return transitHours;
    }


    /**
     * Creates an error message from a message from the Post Nord API
     *
     * @param errorMessage the message from Post Nord API
     * @return true when an error message is created
     */
    public boolean createErrorMessageResponse(int httpCode, String errorMessage) {
        responseDone = false;
        System.out.println("ErrorMessage: " + errorMessage);
        this.httpCode = httpCode;
        errorMessages.add(errorMessage);
        return true;
    }

    /**
     * Creates a response object to respond to the client
     *
     * @return true if a Response object is created
     */
    public boolean createResponse() {
        if (flights.getFlights().get(0) != null) {
            res = new Response();
            System.out.println(printClassMsg + "package DeliveryTime: " + aPackage.getPackageArrivalTime() + " DeliveryDate: " + aPackage.getPackageArrivalDate());
            for (int i = 0; i < flights.getFlights().size(); i++) {
                String departureCity = getAirPortName(flights.getFlights().get(i).getOrigin());
                String arrivalCity = getAirPortName(flights.getFlights().get(i).getDestination());
                res.addDepartureCity(departureCity);
                res.addDepartureTime(flights.getFlights().get(i).getDepartureTime());
                res.addArrivalCity(arrivalCity);
                res.addArrivalTime(flights.getFlights().get(i).getArrivalTime());
                res.addWaitingTimes(flights.getFlights().get(i).getWaitingTime());

                System.out.println("Origin: " + departureCity
                        + " DepartureTime: " + flights.getFlights().get(i).getDepartureTime() + " DepartureDate: " + flights.getFlights().get(i).getDepartureDate()
                        + " destination: " + arrivalCity + " arrivalTime: " + flights.getFlights().get(i).getArrivalTime()
                        + " arrivalDate " + flights.getFlights().get(i).getArrivalDate()
                        + " waitingTime: " + flights.getFlights().get(i).getWaitingTime());
            }

            res.setPackageDeliveryTime(aPackage.getPackageArrivalTime());
            res.setPackageDeliveryDate(aPackage.getPackageArrivalDate());
            res.setPackageRemainingHours(Integer.toString(remainingHours));
            responseDone = true;
            return true;
        }else{
            responseDone = false;
            if (createErrorMessageResponse(404,"Flights not found")){
                return false;
            }
            return false;
        }
    }

    /**
     * Translates an IATA code into the corresponding city
     *
     * @param airportCode the IATA code
     * @return the name/location of the airport
     */
    public String getAirPortName(String airportCode) {
        JSONParser jsonParser = new JSONParser();
        String airportName = "";
        try {
            org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) jsonParser.parse(new FileReader("files/airportTimezones.json"));
            airportName = jsonObject.get(airportCode).toString();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return airportName;
    }

    /**
     * Makes a authentication call to the Amadeus API and receive a token
     */
    public void createAmadeusAuthentication() {
        Unirest.config().defaultBaseUrl("https://test.api.amadeus.com/v1");

        String clientID = "Ul00G8GNA977r0xYFLte7YIRJkSEsSJ5";
        String clientSecretKey = "fNaNGQAQNJju8Azy";

        HttpResponse<JsonNode> tokenResponse = Unirest.post("/security/oauth2/token")
                .field("grant_type", "client_credentials")
                .field("client_id", clientID)
                .field("client_secret", clientSecretKey)
                .asJson();

        token = (String) tokenResponse.getBody().getObject().get("access_token");
    }

    /**
     * Checks if the Post Nord API has responded
     *
     * @return true if the Post Nord API has responded
     */
    public boolean isResponseDone() {
        return responseDone;
    }

    public String getToken() {
        return token;
    }

    public Package getPackage() {
        return aPackage;
    }

    public Response getRes() {
        return res;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }
}
