import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import spark.Filter;

import static spark.Spark.*;

/**
 * @author Chanon Borgström % Sofia Hallberg
 * @created 09/12/2020
 * @project Group20
 */
public class APIRunner {

    private JsonParser parser = new JsonParser();
    private Gson gson = new Gson();

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

            String packageDepartureDate = request.queryParams("departureDate");
            String departureCountry = request.queryParams("departureCountry");
            String departureZip = request.queryParams("departureZip");
            String arrivalCountry = request.queryParams("arrivalCountry");
            String arrivalZip = request.queryParams("arrivalZip");

            PackageRaceRunner packageRaceRunner = new PackageRaceRunner();
            packageRaceRunner.run(packageDepartureDate, departureCountry, arrivalCountry, departureZip, arrivalZip);

            response.type("application/json");

            APIController controller = packageRaceRunner.getController();
            System.out.println("response: "+ controller.isResponseDone() + " response: " + controller.getRes());

            Response res = controller.getRes();
            System.out.println(res.toString());
            response.body(runner.gson.toJson(res));

            return response.body();

        });
    }


}
