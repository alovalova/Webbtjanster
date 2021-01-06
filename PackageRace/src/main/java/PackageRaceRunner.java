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
     */
    public void run(String packageDepartureDate, String departureCountry, String arrivalCountry, String departureZip, String arrivalZip) {
        aPackage = new Package(packageDepartureDate, departureCountry, arrivalCountry, departureZip, arrivalZip);
        if (aPackage.checkPackage()) {
            System.out.println("PackageRaceRunner.run: " + "Package " + aPackage.getPackageDepartureDate() + " is created");
        } else {
            controller.createErrorMessageResponse("Package: PackageRaceRunner.run");
            System.out.println("PackageRaceRunner.run: " + "Package is not created");
        }
        controller.createPostNordAPIGetRequest(aPackage);
        controller.startFlying(aPackage);
    }

    public APIController getController() {
        return controller;
    }
}
