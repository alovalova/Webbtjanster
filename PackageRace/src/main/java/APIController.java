import com.google.gson.Gson;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

/**
 * @author
 * @created 16/12/2020
 * @project Group20
 */
public class APIController {

    private Flight flight;
    private Flights flights;
    private Package aPackage;
    private int remainingHours = 0;

    private Gson gson;

    public APIController() {
        gson = new Gson();
    }

    /**
     * creates a flight object with the origin parameters.
     */
    private void createStartFlight() {
        flight = new Flight();
        flight.setOrigin("MAD");
        //flight.setDepartureDate(aPackage.getPackageDepartureDate());
        flight.setDepartureDate("2020-12-31"); // till för testning.
    }

    private void createNewFlight(){
        flight = new Flight();
    }

    /**
     * creates a flights array.
     */
    public void createFlights() {
        flights = new Flights();
    }

    /**
     * Creates a new package with the parameters.
     */
    public void createPackage(String packageDepartureDate, String departureCountry, String arrivalCountry, String departureZip, String arrivalZip) {
        aPackage = new Package(packageDepartureDate, departureCountry, departureZip, arrivalCountry, arrivalZip);
        if (checkPackage(aPackage)) {
            System.out.println("APIController.createPackage: " + "Package " + aPackage + " is created");
        }
        System.out.println("APIController.createPackage: " + "Package is not created");
    }

    /**
     * Checks the package's values.
     *
     * @param aPackage the package to be checked.
     * @return true if all parameters are set.
     */
    public boolean checkPackage(Package aPackage) {
        return aPackage.getPackageDepartureDate() != null &&
                aPackage.getDepartureCountry() != null &&
                aPackage.getDepartureZip() != null &&
                aPackage.getArrivalCountry() != null &&
                aPackage.getArrivalZip() != null;
    }

    /**
     * Calls for Post Nord's API to get delivery Time and Date for the package.
     */
    public void createPostNordAPIGetRequest() {
        Unirest.config().defaultBaseUrl("http://api2.postnord.com/rest/transport"); // för anrop till Post Nords API

        HttpResponse<JsonNode> res = null;

        try {
            if (checkPackage(aPackage)) {
                res = Unirest.get("/v1/transittime/getTransitTimeInformation.json")
                        .queryString("apikey", "673d1dcaedbcd206cf3130c3cd01bb7d")
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

            System.out.println("TransitTimes: " + transitTimes);

            String deliveryTime = transitTimes.getJSONObject(0).get("deliveryTime").toString();
            String deliveryDate = (String) transitTimes.getJSONObject(0).get("deliveryDate"); // hur hanterar vi detta?

            System.out.println("Delivery Time: " + deliveryTime + " Delivery Date: " + deliveryDate);

            aPackage.setPackageArrivalTime(deliveryTime);
            aPackage.setPackageArrivalDate(deliveryDate);
            aPackage.setPostNordResponse(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (aPackage.isPostNordResponse()) {
            remainingHours = aPackage.getTransitTime();
            createStartFlight();
            createFlights();
            createNewFlightDestination();
        } else {
            System.out.println("felmeddelande");
        }

    }

    /**
     * Makes a authentication call to the Amadeus API to get a token.
     *
     * @return the token.
     */
    public String createAmadeusAuthentication() {
        Unirest.config().defaultBaseUrl("https://test.api.amadeus.com/v1"); // för anrop till andras APIer.

        String clientID = "A7JmGIf5KhiJRPHI2w4syqghle0P581l";
        String clientSecretKey = "nRBxGUXe116FG4fk";

        HttpResponse<JsonNode> tokenResponse = Unirest.post("/security/oauth2/token")
                .field("grant_type", "client_credentials")
                .field("client_id", clientID)
                .field("client_secret", clientSecretKey)
                .asJson();

        System.out.println("TokenResponse: " + tokenResponse.getBody() + tokenResponse.getStatusText());

        return (String) tokenResponse.getBody().getObject().get("access_token");
    }

    /**
     * Calls a GET-method at the Amadeus API to receive a flight destination.
     */
    public void createNewFlightDestination() {

        String token = createAmadeusAuthentication();

        Unirest.config().defaultBaseUrl("https://test.api.amadeus.com/v1");

        HttpResponse<JsonNode> flightDestinationResponse = Unirest.get("/shopping/flight-destinations")
                .header("authorization", "Bearer " + token)
                .queryString("origin", flight.getOrigin()) //first time MAD --> next destination.
                .queryString("departureDate", flight.getDepartureDate())
                .queryString("oneWay", "true")
                .queryString("nonStop", "true")
                .asJson();

        JSONArray data = (JSONArray) flightDestinationResponse.getBody().getObject().get("data");

        String destination = data.getJSONObject(0).get("destination").toString();

        flight.setDestination(destination);
        createNewFlightArrivalTime(token);

    }

    /**
     * Calls a GET-method at the Amadeus API to receive a flight time and date for departure and arrival.
     *
     * @param token the authorization token.
     */
    public void createNewFlightArrivalTime(String token) {
        Unirest.config().defaultBaseUrl("https://test.api.amadeus.com/v2");

        HttpResponse<JsonNode> flightArrivalTimeResponse = Unirest.get("/shopping/flight-offers")
                .header("authorization", "Bearer " + token)
                .queryString("originLocationCode", flight.getOrigin()) //first time MAD --> next destination.
                .queryString("destinationLocationCode", flight.getDestination())
                .queryString("departureDate", flight.getDepartureDate())
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

        String duration = itinerary.get("duration").toString();

        String departureDateTime = departure.getString("at");
        String arrivalDateTime = arrival.getString("at");

        System.out.println("departureDT: " + departureDateTime + " arrivalDT: " + arrivalDateTime + " duration: " + duration);

        setDepartureDateAndTime(departureDateTime);
        setArrivalDateAndTime(arrivalDateTime);

        setDuration(duration);

        callANewFlight();

    }

    public void callANewFlight() {
        while (timeIsLeft()) {
            flights.addFlight(flight);
            createNewFlight();
            createNewFlightDestination();
        }
    }

    /**
     * @return
     */
    public boolean timeIsLeft() {
        int timeToDeparture = flight.getDuration(); // 3h
        int arrivedTime= Integer.parseInt(flight.getArrivalTime()); // 14:00

        remainingHours -= timeToDeparture;

        return true;
    }


    public void setDepartureDateAndTime(String dateTime) {
        String date = dateTime.substring(0, 10);
        String time = dateTime.substring(11, 16);

        flight.setDepartureDate(date);
        flight.setDepartureTime(time);
    }

    public void setArrivalDateAndTime(String dateTime) {
        String date = dateTime.substring(0, 10);
        String time = dateTime.substring(11, 16);

        flight.setArrivalDate(date);
        flight.setArrivalTime(time);
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
        flight.setDuration(hours);
    }


}


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