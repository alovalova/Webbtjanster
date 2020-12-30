public class PackageRaceRunner {
    private Package aPackage;
    private APIController controller;

    public PackageRaceRunner() {
        controller=new APIController();
    }

    public void start(String packageDepartureDate, String departureCountry, String arrivalCountry, String departureZip, String arrivalZip) {
        aPackage = new Package(packageDepartureDate, departureCountry, arrivalCountry, departureZip, arrivalZip);
        controller.createPackage(aPackage);
    }
}
