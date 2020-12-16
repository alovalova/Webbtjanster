import com.google.gson.Gson;
import com.google.gson.JsonParser;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.util.Calendar;

import static spark.Spark.get;

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
        APIRunner runner = new APIRunner();

        get("/", (request, response) -> {
            System.out.println("Are we there yet?");


            //ska hämta aktuell information om ett paket.
//            boolean parcelCreated = runner.controller.createParcel();
//            if(parcelCreated){
//                //do something
//            }

            return "";

        });



    }


}
