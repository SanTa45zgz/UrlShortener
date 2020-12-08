package urlshortener.domain;

public class GeoLocation {
    private String ipAddress;
    private String city;
    private String latitude;
    private String longitude;
    private String country;

    public GeoLocation(String ipAddress, String city, String latitude, String longitude, String country) {
        this.ipAddress = ipAddress;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
