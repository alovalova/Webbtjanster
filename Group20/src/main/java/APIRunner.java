import com.google.gson.Gson;
import com.google.gson.JsonParser;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import spark.Filter;

import java.util.Calendar;

import static spark.Spark.*;

/**
 * @author Chanon Borgström % Sofia Hallberg
 * @created 09/12/2020
 * @project Group20
 */
public class APIRunner {

    JsonParser parser = new JsonParser();
    Gson gson = new Gson();
    APIController controller = new APIController();

    public static void main(String[] args) {
        port(5000);
        APIRunner runner = new APIRunner();
        // staticFiles.location("/public"); // Static files
        after((Filter) (request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET");
        });



        get("/", (request, response) -> {
            System.out.println("Are we there yet?");


            //ska hämta aktuell information om ett paket.
//            boolean parcelCreated = runner.controller.createParcel();
//            if(parcelCreated){
//                //do something
//            }
            response.type("application/json");
            return "{}";

        });



    }


}
