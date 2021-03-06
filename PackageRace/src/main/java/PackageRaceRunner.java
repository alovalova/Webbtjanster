/**
 * Class for starting and running a package race
 * @author Chanon Borgström & Sofia Hallberg
 * @created 30/12/2020
 * @project Group20
 */
public class PackageRaceRunner {
    private Package aPackage;
    private APIController controller;

    /**
     * Create a PackageRaceRunner object
     */
    public PackageRaceRunner() {
        controller=new APIController();
    }

    /**
     * Create a Package objects with the parameters from the client, start a request to get the arrival date and starts the search for flights
     * @param arrivalCountry
     * @param arrivalZip
     * @param departureCountry
     * @param departureZip
     * @param packageDepartureDate
     * @return
     */
    public boolean run(String packageDepartureDate, String departureCountry, String arrivalCountry, String departureZip, String arrivalZip) {
        aPackage = new Package(packageDepartureDate, departureCountry, arrivalCountry, departureZip, arrivalZip);
        if (aPackage.checkPackage()) {
            System.out.println("PackageRaceRunner.run: " + "Package " + aPackage.getPackageDepartureDate() + " is created");
            if (controller.createPostNordAPIGetRequest(aPackage) && (controller.startFlying(aPackage))){
                return true;
            }else{
                System.out.println("startFlying is false");
                return false;
            }
        } else {
            System.out.println("PackageRaceRunner.run: " + "Package is not created");
            controller.createErrorMessageResponse(400,"Invalid values");
        }
        return true;
    }

    public APIController getController() {
        return controller;
    }
}
