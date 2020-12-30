import com.google.gson.Gson;
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

/**
 * @author
 * @created 16/12/2020
 * @project Group20
 */
public class APIController {

    //private ConnectionFlight flight;
    private Flights flights;
    //private Package aPackage;
    private int remainingHours;

    private Gson gson;

    public APIController() {
        gson = new Gson();
    }


    /**
     * Call to Post Nord's API to get delivery time and date for the package and populates the package with the delivery values
     *
     * @param aPackage
     */
    public void createPostNordAPIGetRequest(Package aPackage) {
        Unirest.config().defaultBaseUrl("http://api2.postnord.com/rest/transport");
        HttpResponse<JsonNode> res = null;

        try {
            if (aPackage.checkPackage()) {
                res = Unirest.get("/v1/transittime/getTransitTimeInformation.json")
                        .queryString("apikey", "fdb2ec79dd43fa36ef38b82d9dd0d10e")
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
            System.out.println("postnord body: " + res.getBody());
            JSONObject transitTimeResponse = (JSONObject) res.getBody().getObject().get("se.posten.loab.lisp.notis.publicapi.serviceapi.TransitTimeResponse");
            JSONArray transitTimes = (JSONArray) transitTimeResponse.get("transitTimes");

            System.out.println("TransitTimes: " + transitTimes);

            String deliveryTime = transitTimes.getJSONObject(0).get("deliveryTime").toString();
            String deliveryDate = (String) transitTimes.getJSONObject(0).get("deliveryDate");

            System.out.println("Delivery Time: " + deliveryTime + " Delivery Date: " + deliveryDate);

            aPackage.setPackageArrivalTime(deliveryTime);
            aPackage.setPackageArrivalDate(deliveryDate);
            aPackage.setPostNordResponse(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (aPackage.postNordResponseOk()) {
            remainingHours = aPackage.getTransitTime();
        } else {
            System.out.println("Felaktig respons från PostNord");
        }
    }

    /**
     * Creates a Flight object, a Flights object
     */
    public void startFlying(Package aPackage) {
        remainingHours = aPackage.getTransitTime();
        flights = new Flights();

        ConnectionFlight startFlight = new ConnectionFlight("MAD", aPackage.getPackageDepartureDate(), this); //startFlight skapas med origin mad och depdate som paketet
        startFlight.searchDestination(); //startFlight får ankomstort och ankomsttid
        checkIfTimeIsLeft(aPackage, startFlight);


        ConnectionFlight secondFlight = new ConnectionFlight(startFlight, this); //skapa ett anslutande flyg
        secondFlight.searchDestination(); //det anslutande flyget får ankomstort och ankomsttid

    }

    private void continueFlying(Package aPackage, ConnectionFlight previousFlight) {
        ConnectionFlight nextFlight = new ConnectionFlight(previousFlight, this);
        nextFlight.searchDestination();
        checkIfTimeIsLeft(aPackage, nextFlight);
    }

    public void checkIfTimeIsLeft(Package aPackage, ConnectionFlight flight) {
        if (timeIsLeft(aPackage, flight)) {
            System.out.println("time is left");
            flights.addFlight(flight);
            continueFlying(aPackage, flight);
        } else {
            System.out.println("time is not left");
            createResponse();
        }
    }

    /**
     * @return
     */
    public boolean timeIsLeft(Package aPackage, ConnectionFlight flight) {
        int previous = flights.getFlights().size() - 1;
        int waitingTime;
        if (previous >= 0) {
            ConnectionFlight previousFlight = flights.getFlights().get(previous);
            waitingTime = calculateWaitingTime(previousFlight);
        } else {
            Flight firstFlight = new Flight();
            firstFlight.setDepartureDate(aPackage.getPackageDepartureDate());
            firstFlight.setDepartureTime("08:30");
            waitingTime = calculateWaitingTime(firstFlight);
        }

        int flightDuration = flight.getDuration(); // 3h

        remainingHours -= flightDuration - waitingTime;
        if (remainingHours > 0) {
            System.out.println("remainingHours: " + remainingHours + " flightDuration: " + flightDuration + " waitingTime: " + waitingTime);
            return true;
        } else {
            return false;
        }
    }

    public int calculateWaitingTime(Flight previousFlight) {
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

        Date d1 = null;
        Date d2 = null;
        int days = 0;
        int hours = 0;
        int minutes = 0;

        try {
            d1 = format.parse(departed.toString());
            d2 = format.parse(arrived.toString());


            DateTime dt1 = new DateTime(d1);
            DateTime dt2 = new DateTime(d2);

            days = Days.daysBetween(dt1, dt2).getDays();
            hours = Hours.hoursBetween(dt1, dt2).getHours() % 24;
            minutes = Minutes.minutesBetween(dt1, dt2).getMinutes() % 60;
        } catch (Exception e) {
            e.printStackTrace();
        }

        waitingTime = countWaitingTime(days, hours, minutes);
        return waitingTime;
    }

    public int countWaitingTime(int days, int hours, int minutes) {
        int transitHours = 0;
        int addToHours = 0;

        if (minutes < 30) {
            addToHours = 0;
        } else
            addToHours = 1;

        transitHours = days * 24 + hours + addToHours;

        return transitHours;
    }

    public void createResponse() {
        Response res = new Response();
        // fyll i responsen i objektet och skicka över till APIRunner.
    }

    /**
     * Makes a authentication call to the Amadeus API and get a token
     * @return the token
     */
    public String createAmadeusAuthentication() {
        Unirest.config().defaultBaseUrl("https://test.api.amadeus.com/v1");

        String clientID = "A7JmGIf5KhiJRPHI2w4syqghle0P581l";
        String clientSecretKey = "nRBxGUXe116FG4fk";

        HttpResponse<JsonNode> tokenResponse = Unirest.post("/security/oauth2/token")
                .field("grant_type", "client_credentials")
                .field("client_id", clientID)
                .field("client_secret", clientSecretKey)
                .asJson();

        return (String) tokenResponse.getBody().getObject().get("access_token");
    }
}

//    public void createNewFlightDestination() {
//
//        String token = createAmadeusAuthentication();
//
//        Unirest.config().defaultBaseUrl("https://test.api.amadeus.com/v1");
//
//        HttpResponse<JsonNode> flightDestinationResponse = Unirest.get("/shopping/flight-destinations")
//                .header("authorization", "Bearer " + token)
//                .queryString("origin", flight.getOrigin()) //first time MAD --> next destination.
//                .queryString("departureDate", flight.getDepartureDate())
//                .queryString("oneWay", "true")
//                .queryString("nonStop", "true")
//                .asJson();
//
//        System.out.println("Flight origin: " + flight.getOrigin() + " flight departureDate: " + flight.getDepartureDate());
//        System.out.println(flightDestinationResponse.getBody());
//        JSONArray data = (JSONArray) flightDestinationResponse.getBody().getObject().get("data");
//
//        String destination = data.getJSONObject(0).get("destination").toString();
//
//        flight.setDestination(destination);
//        createNewFlightArrivalTime(token);
//
//    }

//    /**
//     * Calls a GET-method at the Amadeus API to receive a flight time and date for departure and arrival.
//     * @param token the authorization token.
//     */
//    public void createNewFlightArrivalTime(String token) {
//        Unirest.config().defaultBaseUrl("https://test.api.amadeus.com/v2");
//        String duration = "";
//        String departureDateTime = "";
//        String arrivalDateTime = "";
//        try {
//
//            HttpResponse<JsonNode> flightArrivalTimeResponse = Unirest.get("/shopping/flight-offers")
//                    .header("authorization", "Bearer " + token)
//                    .queryString("originLocationCode", flight.getOrigin()) //first time MAD --> next destination.
//                    .queryString("destinationLocationCode", flight.getDestination())
//                    .queryString("departureDate", flight.getDepartureDate())
//                    .queryString("adults", 1)
//                    .queryString("nonStop", "true")
//                    .queryString("max", 1)
//                    .asJson();
//
//            JSONArray meta = (JSONArray) flightArrivalTimeResponse.getBody().getObject().get("data");
//            JSONObject data = meta.getJSONObject(0);
//            JSONArray itineraries = data.getJSONArray("itineraries");
//            JSONObject itinerary = itineraries.getJSONObject(0);
//            JSONArray segments = itinerary.getJSONArray("segments");
//            JSONObject segment = segments.getJSONObject(0);
//            JSONObject departure = segment.getJSONObject("departure");
//            JSONObject arrival = segment.getJSONObject("arrival");
//
//            duration = itinerary.get("duration").toString();
//
//            departureDateTime = departure.getString("at");
//            arrivalDateTime = arrival.getString("at");
//
//            System.out.println("departureDT: " + departureDateTime + " arrivalDT: " + arrivalDateTime + " duration: " + duration);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        setDepartureDateAndTime(departureDateTime);
//        setArrivalDateAndTime(arrivalDateTime);
//
//        setDuration(duration);
//
//        checkIfTimeIsLeft();
//    }




//
//    public void setDepartureDateAndTime(String dateTime) {
//        String date = dateTime.substring(0, 10);
//        String time = dateTime.substring(11, 16);
//
//        flight.setDepartureDate(date);
//        flight.setDepartureTime(time);
//    }
//
//    public void setArrivalDateAndTime(String dateTime) {
//        String date = dateTime.substring(0, 10);
//        String time = dateTime.substring(11, 16);
//
//        flight.setArrivalDate(date);
//        flight.setArrivalTime(time);
//    }

//    public void setDuration(String duration) {
//        String[] durationArray = duration.split("PT");
//        String hoursMinutes = durationArray[1];
//        String[] hoursMinutesArray = hoursMinutes.split("H");
//        int hours = Integer.parseInt(hoursMinutesArray[0]);
//        int minutes = Integer.parseInt(hoursMinutesArray[1].split("M")[0]);
//
//        if (minutes >= 30) {
//            hours++;
//        }
//        flight.setDuration(hours);
//    }



//    /**
//     * Create a package with the parameters from frontEnd
//     * @param aPackage
//     */
//    public void createPackage(Package aPackage) {
//        if (checkPackage(aPackage)) {
//            System.out.println("APIController.createPackage: " + "Package " + aPackage.getPackageDepartureDate() + " is created");
//        } else {
//            System.out.println("APIController.createPackage: " + "Package is not created");
//        }
//    }

//    /**
//     * Check the parameters of a package
//     * @param aPackage the package to be checked
//     * @return true if all mandatory parameters are assigned
//     */
//    public boolean checkPackage(Package aPackage) {
//        return aPackage.getPackageDepartureDate() != null &&
//               aPackage.getDepartureCountry() != null &&
//               aPackage.getDepartureZip() != null &&
//               aPackage.getArrivalCountry() != null &&
//               aPackage.getArrivalZip() != null;
//    }

// ,"data":[{
//      "type":"flight-offer","id":"1","source":"GDS",
//      "instantTicketingRequired":false,
//      "nonHomogeneous":false,
//      "oneWay":false,
//      "lastTicketingDate":"2020-12-29",
//      "numberOfBookableSeats":4,
//      "itineraries":[
//          {"duration":"PT1H45M",
//          "segments":[
//              {"departure":{
//                          "iataCode":"MAD","terminal":"2",
//                          "at":"2020-12-31T15:15:00"},
//              "arrival":{
//                      "iataCode":"NTE",
//                      "at":"2020-12-31T17:00:00"},
//              "carrierCode":"V7",
//              "number":"2273",
//              "aircraft":{"code":"319"},
//              "operating":{"carrierCode":"V7"},
//              "duration":"PT1H45M",
//              "id":"1",
//              "numberOfStops":0,
//              "blacklistedInEU":false}]
//      }],"price":{"currency":"EUR","total":"63.94","base":"25.00","fees":[{"amount":"0.00","type":"SUPPLIER"},{"amount":"0.00","type":"TICKETING"}],"grandTotal":"63.94"},"pricingOptions":{"fareType":["PUBLISHED"],"includedCheckedBagsOnly":true},"validatingAirlineCodes":["V7"],"travelerPricings":[{"travelerId":"1","fareOption":"STANDARD","travelerType":"ADULT","price":{"currency":"EUR","total":"63.94","base":"25.00"},"fareDetailsBySegment":[{"segmentId":"1","cabin":"ECONOMY","fareBasis":"DV7PACK","class":"D","includedCheckedBags":{"quantity":1}}]}]}],"dictionaries":{"locations":{"MAD":{"cityCode":"MAD","countryCode":"ES"},"NTE":{"cityCode":"NTE","countryCode":"FR"}},"aircraft":{"319":"AIRBUS A319"},"currencies":{"EUR":"EURO"},"carriers":{"V7":"VOLOTEA"}}}