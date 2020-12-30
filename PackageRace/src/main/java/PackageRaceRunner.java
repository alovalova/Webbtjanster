public class PackageRaceRunner {
    private Package aPackage;
    private APIController controller;

    public PackageRaceRunner() {
        controller=new APIController();
    }

    public void run(String packageDepartureDate, String departureCountry, String arrivalCountry, String departureZip, String arrivalZip) {
        aPackage = new Package(packageDepartureDate, departureCountry, arrivalCountry, departureZip, arrivalZip);
        if (aPackage.checkPackage()) {
            System.out.println("APIController.createPackage: " + "Package " + aPackage.getPackageDepartureDate() + " is created");
        } else {
            System.out.println("APIController.createPackage: " + "Package is not created");
        }

        controller.createPostNordAPIGetRequest(aPackage);
        controller.startFlying(aPackage);
        controller.createResponse();
    }

}
