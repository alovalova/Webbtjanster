import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

import static spark.Spark.get;

/**
 * @author
 * @created 09/12/2020
 * @project Group20
 */
public class APIRunner {
    public static void main(String[] args) {

        JsonParser parser = new JsonParser();
        Gson gson = new Gson();
        Parcel parcel = new Parcel();

        get("/", (request, response) -> { // för vår server som får indata från webbclienten
            // skapa och lägg in info om paket.
            return "";
        });

        Unirest.config().defaultBaseUrl("http://api2.postnord.com/rest/transport"); // för anrop till andras APIer.


        HttpResponse <JsonNode> response = Unirest.get("/v1/transittime/getTransitTimeInformation.json")
                .queryString("apikey", "673d1dcaedbcd206cf3130c3cd01bb7d")
                .queryString("dateOfDeparture", parcel.getParcelDepartureTime())
                .queryString("serviceCode", "18")
                .queryString("serviceGroupCode", parcel.getDepartureCountry())
                .queryString("fromAddressPostalCode", parcel.getDepartureZip())
                .queryString("fromAddressCountryCode", parcel.getDepartureCountry())
                .queryString("toAddressPostalCode",parcel.getArrivalZip())
                .queryString("toAddressCountryCode",parcel.getArrivalCountry())

                .header("Accept", "application/json")
                .asJson()

        ;

        System.out.println("response: " + response.getBody());


    }


}
