import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileNotFoundException;
import java.io.FileReader;

import static spark.Spark.*;

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
