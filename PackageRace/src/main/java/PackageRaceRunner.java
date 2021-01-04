public class PackageRaceRunner {
    private Package aPackage;
    private APIController controller;

    public PackageRaceRunner() {
        controller=new APIController();
    }

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

        //controller.createResponse();
    }

}
