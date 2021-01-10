import com.google.gson.Gson;
import spark.Filter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
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

            PackageRaceRunner packageRaceRunner = new PackageRaceRunner();
            packageRaceRunner.run(packageDepartureDate, departureCountry, arrivalCountry, departureZip, arrivalZip);

            response.type("application/json");

            APIController controller = packageRaceRunner.getController();

//            ShowError error = new ShowError(404,"stirng").doGet(response.body());
            Response res = controller.getRes();
            response.body(runner.gson.toJson(res));

            return response.body();

        });
    }
    public static class ShowError extends HttpServlet {
        private int httpCode;
        private String errorMessage;

        public ShowError(int httpCode, String errorMessage) {
            this.httpCode = httpCode;
            this.errorMessage = errorMessage;
        }

        // Method to handle GET method request.
        public void doGet(HttpServletResponse response)
                throws ServletException, IOException {

            // Set error code and reason.
            try {
                System.out.println("error: " + errorMessage + " httpCode: " + httpCode);
                response.sendError(httpCode, errorMessage);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
