package urlshortener.domain;

import com.sun.tools.javac.util.Pair;

import java.net.URI;
import java.util.List;

public class SystemInfo {
    private Long numUsers;
    private Long numClicks;
    private Long numUris;
    private List<Pair<String,Long>> topUris;
    private GeoLocation location;

    public SystemInfo(Long numUsers, Long numClicks, Long numUris) {
        this.numUsers = numUsers;
        this.numClicks = numClicks;
        this.numUris = numUris;
    }


    public SystemInfo(Long numUsers, Long numClicks, Long numUris, List<Pair<String,Long>> topUris, GeoLocation location) {
        this.numUsers = numUsers;
        this.numClicks = numClicks;
        this.numUris = numUris;
        this.topUris = topUris;
        this.location = location;
    }

    public SystemInfo(Long numUsers, Long numClicks, Long numUris, List<Pair<String,Long>> topUris) {
        this.numUsers = numUsers;
        this.numClicks = numClicks;
        this.numUris = numUris;
        this.topUris = topUris;
    }

    public Long getNumUsers() {
        return numUsers;
    }

    public Long getNumClicks() {
        return numClicks;
    }

    public Long getNumUris() {
        return numUris;
    }

    public List<Pair<String,Long>> getTopUris() {
        return topUris;
    }

    public GeoLocation getLocation() {
        return location;
    }

    public void setLocation(GeoLocation location) {
        this.location = location;
    }

}
