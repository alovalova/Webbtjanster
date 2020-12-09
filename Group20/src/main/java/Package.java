import java.time.LocalDateTime;

/**
 * @author
 * @created 09/12/2020
 * @project Group20
 */
public class Package {
    LocalDateTime packageDepartureTime;
    LocalDateTime packageArrivalTime;
    String departureCountry;
    String arrivalCountry;
    String departureZip;
    String arrivalZip;
    String departureCity;
    String arrivalCity;

    public Package(){

    }

    public Package(LocalDateTime packageDepartureTime, String departureCountry, String arrivalCountry, String departureZip, String arrivalZip, String departureCity, String arrivalCity) {
        this.packageDepartureTime = packageDepartureTime;
        this.departureCountry = departureCountry;
        this.arrivalCountry = arrivalCountry;
        this.departureZip = departureZip;
        this.arrivalZip = arrivalZip;
        this.departureCity = departureCity;
        this.arrivalCity = arrivalCity;
    }

    public LocalDateTime getPackageDepartureTime() {
        return packageDepartureTime;
    }

    public void setPackageDepartureTime(LocalDateTime packageDepartureTime) {
        this.packageDepartureTime = packageDepartureTime;
    }

    public LocalDateTime getPackageArrivalTime() {
        return packageArrivalTime;
    }

    public void setPackageArrivalTime(LocalDateTime packageArrivalTime) {
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

    public String getArrivalCity() {
        return arrivalCity;
    }

    public void setArrivalCity(String arrivalCity) {
        this.arrivalCity = arrivalCity;
    }
}
