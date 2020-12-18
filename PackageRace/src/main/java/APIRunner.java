import com.google.gson.Gson;
import com.google.gson.JsonParser;
import spark.Filter;

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
       // runner.controller.createPostNordAPIGetRequest(aPackage);
        //staticFiles.location("/public"); // Static files
        // När man skickar en förfrågan till en server vill inte servern ta emot förfrågan utam måste först kolla om klienten är ok
        after((Filter) (request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET");
        });

        get("/", (request, response) -> {

           // boolean parcelCreated = runner.controller.createPackage();
//            if(parcelCreated){
//                //do something
//            }
            response.type("application/json");
            return "{}";

        });
    }


}
