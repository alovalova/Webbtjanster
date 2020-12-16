import java.time.LocalDate;
import java.time.Period;

/**
 * @author
 * @created 09/12/2020
 * @project Group20
 */
public class Parcel {
    String parcelDepartureDate;
    String departureCountry;
    String departureZip;
    String departureCity;

    String parcelArrivalTime;
    String parcelArrivalDate;
    String arrivalCountry;
    String arrivalZip;
    String arrivalAddress;


    public Parcel() {
        parcelDepartureDate = "2020-12-15";
        departureCountry = "SE";
        departureZip = "24136";

        arrivalCountry = "SE";
        arrivalZip = "75260";
    }

    public Parcel(String parcelDepartureDate, String departureCountry, String arrivalCountry, String departureZip, String arrivalZip, String departureCity, String arrivalAddress) {
        this.parcelDepartureDate = parcelDepartureDate;
        this.departureCountry = departureCountry;
        this.arrivalCountry = arrivalCountry;
        this.departureZip = departureZip;
        this.arrivalZip = arrivalZip;
        this.departureCity = departureCity;
        this.arrivalAddress = arrivalAddress;
    }

    public String getParcelDepartureDate() {
        return parcelDepartureDate;
    }

    public void setParcelDepartureDate(String parcelDepartureDate) {
        this.parcelDepartureDate = parcelDepartureDate;
    }

    public String getParcelArrivalTime() {
        return parcelArrivalTime;
    }

    public void setParcelArrivalTime(String parcelArrivalTime) {
        this.parcelArrivalTime = parcelArrivalTime;
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

    public String getParcelArrivalDate() {
        return parcelArrivalDate;
    }

    public void setParcelArrivalDate(String parcelArrivalDate) {
        this.parcelArrivalDate = parcelArrivalDate;
    }

    public int getTransitTime() {
        int transitTime = 0;

        // create date instances
        LocalDate localDate1 = LocalDate.parse(parcelDepartureDate);
        LocalDate localDate2 = LocalDate.parse(parcelArrivalDate);

        // calculate difference
        int days = Period.between(localDate1, localDate2).getDays();

        // print days
        System.out.println("Days between " + localDate1 + " and " + localDate2 + ": " + days);

        return transitTime;
    }

}
