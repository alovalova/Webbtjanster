import com.google.gson.JsonParser;

import static spark.Spark.get;

/**
 * @author
 * @created 09/12/2020
 * @project Group20
 */
public class APIRunner {
    public static void main(String[] args) {

        JsonParser parser = new JsonParser();

        get("api.amadeus.com/v1/shopping/flight-destinations", (request, response) ->{
                    return "";
        });

        get("api.amadeus.com/v1/shopping/flight-destinations", (request, response) -> {
            return "";
        });

    }

}
