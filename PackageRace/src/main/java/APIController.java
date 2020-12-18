import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.time.LocalDateTime;
import java.util.Calendar;

/**
 * @author
 * @created 16/12/2020
 * @project Group20
 */
public class APIController {

    Flight flight;
    Flights flights;
    int flightTransitTime = 0;

    public APIController() {
        createFlight();
        createFlights();
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
    public boolean createPackage(String packageDepartureDate, String departureCountry, String arrivalCountry, String departureZip, String arrivalZip, String departureCity, String arrivalCity) {
        boolean isCreated = false;

        Package aPackage = new Package(packageDepartureDate, departureCountry, arrivalCountry, departureZip, arrivalZip, departureCity, arrivalCity);
        if (checkPackage(aPackage)) {
            createPostNordAPIGetRequest(aPackage);
        }

        return isCreated;
    }

    public boolean checkPackage(Package aPackage) {
        return aPackage.getPackageDepartureDate()!= null &&
                aPackage.getDepartureCountry()!=null &&
                aPackage.getDepartureZip()!=null &&
                aPackage.getArrivalCountry()!=null &&
                aPackage.getArrivalZip()!=null;
    }

    public void createPostNordAPIGetRequest(Package aPackage) {
        Unirest.config().defaultBaseUrl("http://api2.postnord.com/rest/transport"); // för anrop till andras APIer.

        HttpResponse<JsonNode> res = Unirest.get("/v1/transittime/getTransitTimeInformation.json")
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

        //printouts
        System.out.println("First response: " + res.getBody());

        JSONObject transitTimeResponse = (JSONObject) res.getBody().getObject().get("se.posten.loab.lisp.notis.publicapi.serviceapi.TransitTimeResponse");
        JSONArray transitTimes = (JSONArray) transitTimeResponse.get("transitTimes");

        System.out.println("TransitTimes: " + transitTimes);

        String deliveryTime = transitTimes.getJSONObject(0).get("deliveryTime").toString();
        Calendar deliveryDate = (Calendar) transitTimes.getJSONObject(0).get("deliveryDate"); // hur hanterar vi detta?

        System.out.println("Delivery Time: " + deliveryTime + " Delivery Date: " + deliveryDate);

        aPackage.setPackageArrivalTime(deliveryTime);
        aPackage.setPackageArrivalDate(deliveryDate.toString());

        countTransitTime(aPackage);
    }

    /**
     * counts the parcel's transit time with the flight's transit time.
     * If there's time left it a false value is given.
     *
     * @return if it's there yet or not.
     * @param aPackage
     */
    public boolean countTransitTime(Package aPackage) {
        boolean areWeThereYet = false;
        int parcelTransitTime = aPackage.getTransitTime();

        while (flightTransitTime < parcelTransitTime) {
            createNewFlightDestination();

        }
        areWeThereYet = true;


        return areWeThereYet;
    }

    public void createNewFlightDestination() {



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