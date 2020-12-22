import com.google.gson.Gson;
import kong.unirest.HttpRequestWithBody;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.time.LocalDateTime;

/**
 * @author
 * @created 16/12/2020
 * @project Group20
 */
public class APIController {

    Flight flight;
    Flights flights;
    Package aPackage;
    int flightTransitTime = 0;

    Gson gson;

    public APIController() {
        createFlight();
        createFlights();
        gson=  new Gson();
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

    public void createNewFlightDestination(Package aPackage) {

//        countTransitTime(aPackage);

        Unirest.config().defaultBaseUrl("https://test.api.amadeus.com/v1/security/oauth2/token/"); // för anrop till andras APIer.

        String clientID = "A7JmGIf5KhiJRPHI2w4syqghle0P581l";
        String clientSecretKey = "nRBxGUXe116FG4fk";
        String clientCredentials= "dborgstroem@gmail.com";

        String postBody = "grant_type=dborgstroem@gmail.com&client_id=A7JmGIf5KhiJRPHI2w4syqghle0P581l" +
                "&client_secret=nRBxGUXe116FG4fk";

        HttpResponse<JsonNode> res = Unirest.post("https://test.api.amadeus.com/v1/security/oauth2/token")
                .field("grant_type", "client_credentials")
                .field("client_id", clientID)
                .field("client_secret", clientSecretKey)
                .asEmpty();

        System.out.println("PostResponse: " +res.getBody() +"\n" + res.getStatusText()+"\n" + res.getHeaders());
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
//      "transitTimes":[
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
//          "daysPickup":["MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY"]}]}}