import java.time.LocalDateTime;

/**
 * @author
 * @created 09/12/2020
 * @project Group20
 */
public class Parcel {
    String parcelDepartureTime;
    String departureCountry;
    String departureZip;
    String departureCity;

    String parcelArrivalTime;
    String arrivalCountry;
    String arrivalZip;
    String arrivalCity;

    public Parcel() {
        parcelDepartureTime = "2020-12-15";
        departureCountry = "SE";
        departureZip = "24136";

        arrivalCountry = "SE";
        arrivalZip = "75260";
    }

    public Parcel(String parcelDepartureTime, String departureCountry, String arrivalCountry, String departureZip, String arrivalZip, String departureCity, String arrivalCity) {
        this.parcelDepartureTime = parcelDepartureTime;
        this.departureCountry = departureCountry;
        this.arrivalCountry = arrivalCountry;
        this.departureZip = departureZip;
        this.arrivalZip = arrivalZip;
        this.departureCity = departureCity;
        this.arrivalCity = arrivalCity;
    }

    public String getParcelDepartureTime() {
        return parcelDepartureTime;
    }

    public void setParcelDepartureTime(String parcelDepartureTime) {
        this.parcelDepartureTime = parcelDepartureTime;
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

    public String getArrivalCity() {
        return arrivalCity;
    }

    public void setArrivalCity(String arrivalCity) {
        this.arrivalCity = arrivalCity;
    }
}
