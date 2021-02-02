import com.google.gson.Gson;
import spark.Filter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static spark.Spark.*;

/**
 * @author Chanon Borgström & Sofia Hallberg
 * @created 09/12/2020
 * @project Group20
 */
public class APIRunner {

    private Gson gson = new Gson();

    /**
     * Main method that starts the server and runs the API
     */
    public static void main(String[] args) {
        port(5000);
        APIRunner runner = new APIRunner();
        after((Filter) (request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET");
        });

        get("/v1/getDestinations", (request, response) -> {

            String packageDepartureDate = request.queryParams("departureDate");
            String departureCountry = request.queryParams("departureCountry");
            String departureZip = request.queryParams("departureZip");
            String arrivalCountry = request.queryParams("arrivalCountry");
            String arrivalZip = request.queryParams("arrivalZip");

            response.type("application/json");
            PackageRaceRunner packageRaceRunner = new PackageRaceRunner();
            APIController controller = packageRaceRunner.getController();
            if (packageRaceRunner.run(packageDepartureDate, departureCountry, arrivalCountry, departureZip, arrivalZip)){
                if (controller.isResponseDone()) {
                    Response res = controller.getRes();
                    response.body(runner.gson.toJson(res));
                    response.status(200);
                }else{
                    response.status(controller.getHttpCode());
                    System.out.println(response.status());
                    response.body(runner.gson.toJson(controller.getErrorMessages()));
                }
            }else{
                System.out.println("PackageRunner.run is false");
                response.status(controller.getHttpCode());
                System.out.println(response.status());
                response.body(runner.gson.toJson(controller.getErrorMessages()));
            }
            return response;
        });
    }

}
