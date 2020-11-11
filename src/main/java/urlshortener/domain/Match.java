package urlshortener.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Match implements Serializable {

    private String threatType;

    private String platformType;

    private String threatEntryType;

    private Map<String, String> threat;

    private Map<String, List<Map<String, String>>> threatEntryMetadata;

    private String cacheDuration;

    public String getThreatType() {
        return threatType;
    }

    public void setThreatType(String threatType) {
        this.threatType = threatType;
    }

    public String getPlatformType() {
        return platformType;
    }

    public void setPlatformType(String platformType) {
        this.platformType = platformType;
    }

    public String getThreatEntryType() {
        return threatEntryType;
    }

    public void setThreatEntryType(String threatEntryType) {
        this.threatEntryType = threatEntryType;
    }

    public Map<String, String> getThreat() {
        return threat;
    }

    public void setThreat(Map<String, String> threat) {
        this.threat = threat;
    }

    public Map<String, List<Map<String, String>>> getThreatEntryMetadata() {
        return threatEntryMetadata;
    }

    public void setThreatEntryMetadata(Map<String, List<Map<String, String>>> threatEntryMetadata) {
        this.threatEntryMetadata = threatEntryMetadata;
    }

    public String getCacheDuration() {
        return cacheDuration;
    }

    public void setCacheDuration(String cacheDuration) {
        this.cacheDuration = cacheDuration;
    }
}
