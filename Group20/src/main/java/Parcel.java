import java.time.LocalDateTime;

/**
 * @author
 * @created 09/12/2020
 * @project Group20
 */
public class Parcel {
    LocalDateTime parcelDepartureTime;
    LocalDateTime parcelArrivalTime;
    String departureCountry;
    String arrivalCountry;
    String departureZip;
    String arrivalZip;
    String departureCity;
    String arrivalCity;

    public Parcel(){

    }

    public Parcel(LocalDateTime parcelDepartureTime, String departureCountry, String arrivalCountry, String departureZip, String arrivalZip, String departureCity, String arrivalCity) {
        this.parcelDepartureTime = parcelDepartureTime;
        this.departureCountry = departureCountry;
        this.arrivalCountry = arrivalCountry;
        this.departureZip = departureZip;
        this.arrivalZip = arrivalZip;
        this.departureCity = departureCity;
        this.arrivalCity = arrivalCity;
    }

    public LocalDateTime getParcelDepartureTime() {
        return parcelDepartureTime;
    }

    public void setParcelDepartureTime(LocalDateTime parcelDepartureTime) {
        this.parcelDepartureTime = parcelDepartureTime;
    }

    public LocalDateTime getParcelArrivalTime() {
        return parcelArrivalTime;
    }

    public void setParcelArrivalTime(LocalDateTime parcelArrivalTime) {
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
