import org.joda.time.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Representation of a package with departure and destination parameters
 * @author Chanon Borgström & Sofia Hallberg
 * @created 09/12/2020
 * @project Group20
 */
public class Package {
    private String packageDepartureDate;
    private String departureCountry;
    private String departureZip;
    private String packageArrivalTime;
    private String packageArrivalDate;
    private String arrivalCountry;
    private String arrivalZip;
    private boolean postNordResponse = false;

    /**
     * Creates a Package with hard coded parameters for testing
     */
    public Package() {
        packageDepartureDate = "2021-01-31";
        departureCountry = "SE";
        departureZip = "24136";
        arrivalCountry = "SE";
        arrivalZip = "75260";
    }

    /**
     * Creates a Package
     * @param arrivalCountry
     * @param arrivalZip
     * @param departureCountry
     * @param departureZip
     * @param packageDepartureDate
     */
    public Package(String packageDepartureDate, String departureCountry, String arrivalCountry, String departureZip, String arrivalZip) {
        this.packageDepartureDate = packageDepartureDate;
        this.departureCountry = departureCountry;
        this.arrivalCountry = arrivalCountry;
        this.departureZip = departureZip;
        this.arrivalZip = arrivalZip;
    }

    /**
     * Check the parameters of a package
     * @return true if all mandatory parameters are assigned
     */
    public boolean checkPackage() {
        return packageDepartureDate != null &&
                departureCountry != null &&
                departureZip != null &&
                arrivalCountry != null &&
                arrivalZip != null;
    }

    public String getPackageDepartureDate() {
        return packageDepartureDate;
    }

    public String getPackageArrivalTime() {
        return packageArrivalTime;
    }

    public void setPackageArrivalTime(String packageArrivalTime) {
        this.packageArrivalTime = packageArrivalTime;
    }

    public String getDepartureCountry() {
        return departureCountry;
    }

    public String getArrivalCountry() {
        return arrivalCountry;
    }

    public String getDepartureZip() {
        return departureZip;
    }

    public String getArrivalZip() {
        return arrivalZip;
    }

    public void setPackageArrivalDate(String packageArrivalDate) {
        this.packageArrivalDate = packageArrivalDate;
    }

    /**
     * Calculates the transit time for a package
     * @return the transit time in hours
     */
    public int getTransitTime() {
        int transitTime = 0;
        StringBuilder departureBuilder = new StringBuilder();
        departureBuilder.append(packageDepartureDate + " " + "08:00");

        StringBuilder arrivalBuilder = new StringBuilder();
        for (int i = 0; i < packageArrivalDate.length(); i++) {
            if (i == 4) {
                arrivalBuilder.append("-");
                arrivalBuilder.append(packageArrivalDate.charAt(i));
            } else if (i == 6) {
                arrivalBuilder.append("-");
                arrivalBuilder.append(packageArrivalDate.charAt(i));
            }else{
                arrivalBuilder.append(packageArrivalDate.charAt(i));
            }
        }
        arrivalBuilder.append(" " + packageArrivalTime);

        String dateStart = departureBuilder.toString();
        String dateStop = arrivalBuilder.toString();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Date d1 = null;
        Date d2 = null;
        int days = 0;
        int hours = 0;
        int minutes = 0;

        try {
            d1 = format.parse(dateStart);
            d2 = format.parse(dateStop);

            DateTime dt1 = new DateTime(d1);
            DateTime dt2 = new DateTime(d2);

            days = Days.daysBetween(dt1, dt2).getDays();
            hours = Hours.hoursBetween(dt1, dt2).getHours() % 24;
            minutes = Minutes.minutesBetween(dt1, dt2).getMinutes() % 60;
        } catch (Exception e) {
            e.printStackTrace();
        }

        transitTime = countTransitTime(days, hours, minutes);
        return transitTime;
    }

    /**
     * Counts the transit time in days, hours and minutes into hours
     * @param days
     * @param hours
     * @param minutes
     * @return the total transit time in hours
     */
    public int countTransitTime(int days, int hours, int minutes) {
        int transitHours = 0;
        int addToHours = 0;

        if (minutes < 30) {
            addToHours = 0;
        } else
            addToHours = 1;

        transitHours = days * 24 + hours + addToHours;

        return transitHours;
    }

    public boolean postNordResponseOk() {
        return postNordResponse;
    }

    public void setPostNordResponse(boolean postNordResponse) {
        this.postNordResponse = postNordResponse;
    }
}
