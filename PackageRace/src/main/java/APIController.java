import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.time.LocalDateTime;

/**
 * @author
 * @created 16/12/2020
 * @project Group20
 */
public class APIController {

    private Flight flight;
    private Flights flights;
    private Package aPackage;
    private int flightTransitTime = 0;

    private Gson gson;

    public APIController() {
        createFlight();
        createFlights();
        gson = new Gson();
    }

    public void createFlights() {
        flights = new Flights();
    }

    private void createFlight() {
        flight = new Flight();
        flight.setOrigin("CPH");
        flight.setDepartureTime(LocalDateTime.now()); //Sends the departure set from now.
    }

    /**
     * Creates a new parcel with the parameters.
     *
     * @return
     */
    public Package createPackage(String packageDepartureDate, String departureCountry, String arrivalCountry, String departureZip, String arrivalZip) {
        aPackage = new Package(packageDepartureDate, departureCountry, departureZip, arrivalCountry, arrivalZip);
        if (checkPackage(aPackage)) {
            System.out.println("APIController.createPackage: " + "Package " + aPackage + " is created");
            return aPackage;
        }
        System.out.println("APIController.createPackage: " + "Package is not created");
        return null;
    }

    public boolean checkPackage(Package aPackage) {
        return aPackage.getPackageDepartureDate() != null &&
                aPackage.getDepartureCountry() != null &&
                aPackage.getDepartureZip() != null &&
                aPackage.getArrivalCountry() != null &&
                aPackage.getArrivalZip() != null;
    }

    public void createPostNordAPIGetRequest(Package aPackage) {
        Unirest.config().defaultBaseUrl("http://api2.postnord.com/rest/transport"); // för anrop till Post Nords API

        HttpResponse<JsonNode> res = null;

        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        aPackage.setPostNordResponse(true);
    }

    /**
     * counts the parcel's transit time with the flight's transit time.
     * If there's time left it a false value is given.
     *
     * @param aPackage
     * @return if it's there yet or not.
     */
    public boolean countTransitTime(Package aPackage) {
        boolean areWeThereYet = false;
        int parcelTransitTime = aPackage.getTransitTime();

        areWeThereYet = true;

        return areWeThereYet;
    }

    public String createAmadeusAuthentication() {
        Unirest.config().defaultBaseUrl("https://test.api.amadeus.com/v1"); // för anrop till andras APIer.

        String clientID = "A7JmGIf5KhiJRPHI2w4syqghle0P581l";
        String clientSecretKey = "nRBxGUXe116FG4fk";

        HttpResponse<JsonNode> tokenResponse = Unirest.post("/security/oauth2/token")
                .field("grant_type", "client_credentials")
                .field("client_id", clientID)
                .field("client_secret", clientSecretKey)
                .asJson();

        System.out.println("TokenResponse: "+tokenResponse.getBody()+tokenResponse.getStatusText());

        return (String) tokenResponse.getBody().getObject().get("access_token");
    }

    public void createNewFlightDestination(Package aPackage) {



//        countTransitTime(aPackage);
        String token=createAmadeusAuthentication();
        System.out.println(token);
        Unirest.config().defaultBaseUrl("https://test.api.amadeus.com/v1");

        HttpResponse<JsonNode> flightDestinationResponse = Unirest.get("/shopping/flight-destinations")
                .header("authorization", "Bearer "+token)
                .queryString("origin", "MAD")
                .queryString("departureDate", "2020-12-24")
                .queryString("oneWay", "true")
                .queryString("nonStop", "true")
                .asJson();

        System.out.println("flightDestinationResponse: " +flightDestinationResponse.getBody());

        JsonArray data = (JsonArray) flightDestinationResponse.getBody().getArray().get(0);

        JsonObject type = (JsonObject) data.get(0);
        JsonObject destination = (JsonObject) type.get("destination");
        System.out.println(destination);


        //PostResponse: {"data"🙁
        //                 {"type":"flight-destination",
        //                  "origin":"MAD",
        //                  "destination":"DUS",
        //                  "departureDate":"2020-12-24",
        //                  "price":{"total":"43.94"},
        //                  "links":{"flightDates":"https://test.api.amadeus.com/v1/shopping/flight-dates?origin=MAD&destination=DUS&departureDate=2020-12-24&oneWay=true&nonStop=true&viewBy=DATE",
        //                  "flightOffers":"https://test.api.amadeus.com/v2/shopping/flight-offers?originLocationCode=MAD&destinationLocationCode=DUS&departureDate=2020-12-24&adults=1&nonStop=true"}},
        //                 {"type":"flight-destination",
        //                  "origin":"MAD",
        //                  "destination":"BOD",
        //                  "departureDate":"2020-12-24",
        //                  "price":{"total":"51.94"},
        //                  "links":{"flightDates":"https://test.api.amadeus.com/v1/shopping/flight-dates?origin=MAD&destination=BOD&departureDate=2020-12-24&oneWay=true&nonStop=true&viewBy=DATE",
        //                  "flightOffers":"https://test.api.amadeus.com/v2/shopping/flight-offers?originLocationCode=MAD&destinationLocationCode=BOD&departureDate=2020-12-24&adults=1&nonStop=true"}},
        //                 {"type":"flight-destination",
        //                  "origin":"MAD",
        //                  "destination":"STO",
        //                  "departureDate":"2020-12-24",
        //                  "price":{"total":"116.94"},
        //                  "links":{"flightDates":"https://test.api.amadeus.com/v1/shopping/flight-dates?origin=MAD&destination=STO&departureDate=2020-12-24&oneWay=true&nonStop=true&viewBy=DATE",
        //                  "flightOffers":"https://test.api.amadeus.com/v2/shopping/flight-offers?originLocationCode=MAD&destinationLocationCode=STO&departureDate=2020-12-24&adults=1&nonStop=true"}},
        //                 {"type":"flight-destination",
        //                  "origin":"MAD",
        //                  "destination":"SOF","departureDate":"2020-12-24","price":{"total":"125.94"},"links":{"flightDates":"https://test.api.amadeus.com/v1/shopping/flight-dates?origin=MAD&destination=SOF&departureDate=2020-12-24&oneWay=true&nonStop=true&viewBy=DATE","flightOffers":"https://test.api.amadeus.com/v2/shopping/flight-offers?originLocationCode=MAD&destinationLocationCode=SOF&departureDate=2020-12-24&adults=1&nonStop=true"}},{"type":"flight-destination","origin":"MAD","destination":"GVA","departureDate":"2020-12-24","price":{"total":"136.94"},"links":{"flightDates":"https://test.api.amadeus.com/v1/shopping/flight-dates?origin=MAD&destination=GVA&departureDate=2020-12-24&oneWay=true&nonStop=true&viewBy=DATE","flightOffers":"https://test.api.amadeus.com/v2/shopping/flight-offers?originLocationCode=MAD&destinationLocationCode=GVA&departureDate=2020-12-24&adults=1&nonStop=true"}},{"type":"flight-destination","origin":"MAD","destination":"NTE","departureDate":"2020-12-24","price":{"total":"156.94"},"links":{"flightDates":"https://test.api.amadeus.com/v1/shopping/flight-dates?origin=MAD&destination=NTE&departureDate=2020-12-24&oneWay=true&nonStop=true&viewBy=DATE","flightOffers":"https://test.api.amadeus.com/v2/shopping/flight-offers?originLocationCode=MAD&destinationLocationCode=NTE&departureDate=2020-12-24&adults=1&nonStop=true"}},{"type":"flight-destination","origin":"MAD","destination":"OTP","departureDate":"2020-12-24","price":{"total":"193.94"},"links":{"flightDates":"https://test.api.amadeus.com/v1/shopping/flight-dates?origin=MAD&destination=OTP&departureDate=2020-12-24&oneWay=true&nonStop=true&viewBy=DATE","flightOffers":"https://test.api.amadeus.com/v2/shopping/flight-offers?originLocationCode=MAD&destinationLocationCode=OTP&departureDate=2020-12-24&adults=1&nonStop=true"}},{"type":"flight-destination","origin":"MAD","destination":"BUH","departureDate":"2020-12-24","price":{"total":"212.94"},"links":{"flightDates":"https://test.api.amadeus.com/v1/shopping/flight-dates?origin=MAD&destination=BUH&departureDate=2020-12-24&oneWay=true&nonStop=true&viewBy=DATE","flightOffers":"https://test.api.amadeus.com/v2/shopping/flight-offers?originLocationCode=MAD&destinationLocationCode=BUH&departureDate=2020-12-24&adults=1&nonStop=true"}},{"type":"flight-destination","origin":"MAD","destination":"WAW","departureDate":"2020-12-24","price":{"total":"281.94"},"links":{"flightDates":"https://test.api.amadeus.com/v1/shopping/flight-dates?origin=MAD&destination=WAW&departureDate=2020-12-24&oneWay=true&nonStop=true&viewBy=DATE","flightOffers":"https://test.api.amadeus.com/v2/shopping/flight-offers?originLocationCode=MAD&destinationLocationCode=WAW&departureDate=2020-12-24&adults=1&nonStop=true"}},{"type":"flight-destination","origin":"MAD","destination":"MIA","departureDate":"2020-12-24","price":{"total":"2015.63"},"links":{"flightDates":"https://test.api.amadeus.com/v1/shopping/flight-dates?origin=MAD&destination=MIA&departureDate=2020-12-24&oneWay=true&nonStop=true&viewBy=DATE","flightOffers":"https://test.api.amadeus.com/v2/shopping/flight-offers?originLocationCode=MAD&destinationLocationCode=MIA&departureDate=2020-12-24&adults=1&nonStop=true"}}],"dictionaries":{"currencies":{"EUR":"EURO"},"locations":{"MAD":{"subType":"AIRPORT","detailedName":"ADOLFO SUAREZ BARAJAS"},"GVA":{"subType":"AIRPORT","detailedName":"GENEVA INTERNATIONAL"},"DUS":{"subType":"AIRPORT","detailedName":"INTERNATIONAL AIRPORT"},"MIA":{"subType":"AIRPORT","detailedName":"MIAMI INTL"},"BUH":{"subType":"CITY","detailedName":"BUCHAREST"},"BOD":{"subType":"AIRPORT","detailedName":"MERIGNAC"},"OTP":{"subType":"AIRPORT","detailedName":"HENRI COANDA"},"SOF":{"subType":"AIRPORT","detailedName":"SOFIA"},"WAW":{"subType":"AIRPORT","detailedName":"FREDERIC CHOPIN"},"NTE":{"subType":"AIRPORT","detailedName":"ATLANTIQUE"},"STO":{"subType":"CITY","detailedName":"STOCKHOLM"}}},"meta":{"currency":"EUR","links":{"self":"https://test.api.amadeus.com/v1/shopping/flight-destinations?origin=MAD&departureDate=2020-12-24&oneWay=true&nonStop=true&viewBy=DESTINATION"},"defaults":{"viewBy":"DESTINATION"}}}



        //PostResponse: {"type":"amadeusOAuth2Token",
        //               "username":"dborgstroem@gmail.com",
        //               "application_name":"School",
        //               "client_id":"A7JmGIf5KhiJRPHI2w4syqghle0P581l",
        //               "token_type":"Bearer",
        //               "access_token":"x5EcI4FZYOSpWOVR1YP5rf5lbegG",
        //               "expires_in":1799,
        //               "state":"approved",
        //               "scope":""}

//        Unirest.config().defaultBaseUrl("http://test.api.amadeus.com/v1"); // för anrop till andras APIer.

//      ClientID: "A7JmGIf5KhiJRPHI2w4syqghle0P581l", clientSecretKey: "nRBxGUXe116FG4fk";

//        HttpResponse<JsonNode> res = Unirest.get("/shopping/flight-destinations")
//                .queryString("key", "A7JmGIf5KhiJRPHI2w4syqghle0P581l")
//                .queryString("origin", "CPH") // fixa flygplatskoderna
//                .queryString("departureDate", aPackage.getPackageDepartureDate())
//                .queryString("oneWay", "true")
//                .queryString("nonStop", "true")
//
//                .header("Accept", "application/json")
//                .asJson();
//        System.out.println("First response: " + res.getBody());

    }

    public void createNewFlightArrivalTime() {


    }


}


//"se.posten.loab.lisp.notis.publicapi.serviceapi.TransitTimeResponse":
// {
//      "transitTimes"🙁
//         {"dateOfDeparture":"2020-12-15 18:30:00.0 CET",
//          "latestTimeOfBooking":"14:00",
//          "deliveryTime":"18:00",
//          "deliveryDate":"20201216",
//          "transitTimeInDays":1,
//          "possibleDeviation":false,
//          "service":{"code":"18",
//          "groupCode":"SE",
//          "name":"PostNord Parcel",
//          "pickup":true,
//          "distribution":true
//          },
//          "daysPickup"🙁"MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY"]}]}}