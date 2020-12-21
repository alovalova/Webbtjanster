public class TestClass {
    public static void main(String[] args) {
        Package testPackage=new Package();
        APIController controller=new APIController();
        controller.createNewFlightDestination(testPackage);
        //System.out.println(testPackage.getTransitTime());
    }
}
