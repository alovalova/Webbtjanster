public class TestClass {
    public static void main(String[] args) {
        APIController controller=new APIController();
        controller.createPackage("2021-01-09","SE","SE","75260","24136");
        controller.createPostNordAPIGetRequest();
//        controller.createNewFlightDestination(testPackage);
        //System.out.println(testPackage.getTransitTime());
    }
}
