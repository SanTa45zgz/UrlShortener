package urlshortener.fixtures;

import urlshortener.domain.GeoLocation;

public class GeoLocationFixture {

    public static GeoLocation someGeoLocation() {
        return new GeoLocation("127.0.0.1", "", "", "", "Undefined");
    }
}
