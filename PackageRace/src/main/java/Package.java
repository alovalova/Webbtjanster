
import org.joda.time.*;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

/**
 * @author
 * @created 09/12/2020
 * @project Group20
 */
public class Package {
    private String packageDepartureDate;
    private String departureCountry;
    private String departureZip;
    private String departureCity;

    private String packageArrivalTime;
    private String packageArrivalDate;
    private String arrivalCountry;
    private String arrivalZip;
    private String arrivalAddress;


    public Package() {
        packageDepartureDate = "2020-12-15";
        departureCountry = "SE";
        departureZip = "24136";

        arrivalCountry = "SE";
        arrivalZip = "75260";
    }

    public Package(String packageDepartureDate, String departureCountry, String arrivalCountry, String departureZip, String arrivalZip, String departureCity, String arrivalAddress) {
        this.packageDepartureDate = packageDepartureDate;
        this.departureCountry = departureCountry;
        this.arrivalCountry = arrivalCountry;
        this.departureZip = departureZip;
        this.arrivalZip = arrivalZip;
        this.departureCity = departureCity;
        this.arrivalAddress = arrivalAddress;
    }

    public String getPackageDepartureDate() {
        return packageDepartureDate;
    }

    public void setPackageDepartureDate(String packageDepartureDate) {
        this.packageDepartureDate = packageDepartureDate;
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

    public void setDepartureCountry(String departureCountry) {
        this.departureCountry = departureCountry;
    }

    public String getArrivalCountry() {
        return arrivalCountry;
    }

    public void setArrivalCountry(String arrivalCountry) {
        this.arrivalCountry = arrivalCountry;
    }

    public String getDepartureZip() {
        return departureZip;
    }

    public void setDepartureZip(String departureZip) {
        this.departureZip = departureZip;
    }

    public String getArrivalZip() {
        return arrivalZip;
    }

    public void setArrivalZip(String arrivalZip) {
        this.arrivalZip = arrivalZip;
    }

    public String getDepartureCity() {
        return departureCity;
    }

    public void setDepartureCity(String departureCity) {
        this.departureCity = departureCity;
    }

    public String getArrivalAddress() {
        return arrivalAddress;
    }

    public void setArrivalAddress(String arrivalAddress) {
        this.arrivalAddress = arrivalAddress;
    }

    public String getPackageArrivalDate() {
        return packageArrivalDate;
    }

    public void setPackageArrivalDate(String packageArrivalDate) {
        this.packageArrivalDate = packageArrivalDate;
    }

    public int getTransitTime() {
        int transitTime = 0;

        String dateStart = "01-14-2012 09:29:58";
        String dateStop = "01-17-2012 08:31:48";

        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

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

    public int countTransitTime(int days, int hours, int minutes) {
        int transitHours=0;
        int addToHours=0;

        if(minutes<30) {
            addToHours = 0;
        }
        else
            addToHours = 1;

        transitHours=days*24+hours+addToHours;

        return transitHours;
    }

}
