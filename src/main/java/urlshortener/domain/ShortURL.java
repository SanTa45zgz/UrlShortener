package urlshortener.domain;

import io.swagger.v3.oas.annotations.media.Schema;

import java.net.URI;
import java.sql.Date;

@Schema(name = "ShortUrl", type = "object")
public class ShortURL {

    @Schema(
            description = "Hash identifier of the shorted url",
            example = "58f3ae21",
            required = true
    )
    private String hash;
    @Schema(
            description = "Target url of the shorted url",
            example = "http://google.com",
            required = true
    )
    private String target;
    @Schema(
            description = "Uri of the shorted url",
            example = "",
            required = true
    )
    private URI uri;
    @Schema(
            description = "Sponsor of the shorted url",
            example = "",
            required = true
    )
    private String sponsor;
    @Schema(
            description = "Creation date of the shorted url",
            example = "2021-01-02",
            required = true,
            type = "date"
    )
    private Date created;
    @Schema(
            description = "Owner identifier of the shorted url",
            example = "a14b0b88-892e-4d5b-8835-204522bb3e2b",
            required = true
    )
    private String owner;
    @Schema(
            description = "Mode of the shorted url",
            example = "307",
            required = true
    )
    private Integer mode;
    @Schema(
            description = "Safeness of the shorted url",
            example = "true | false",
            required = true
    )
    private Boolean safe;
    @Schema(
            description = "User request ip of the url shortened",
            example = "0:0:0:0:0:0:0:1",
            required = true
    )
    private String ip;
    @Schema(
            description = "Country of the user that makes the request",
            example = "Spain",
            required = true
    )
    private String country;

    public ShortURL(String hash, String target, URI uri, String sponsor,
                    Date created, String owner, Integer mode, Boolean safe, String ip,
                    String country) {
        this.hash = hash;
        this.target = target;
        this.uri = uri;
        this.sponsor = sponsor;
        this.created = created;
        this.owner = owner;
        this.mode = mode;
        this.safe = safe;
        this.ip = ip;
        this.country = country;
    }

    public ShortURL() {
    }

    public String getHash() {
        return hash;
    }

    public String getTarget() {
        return target;
    }

    public URI getUri() {
        return uri;
    }

    public Date getCreated() {
        return created;
    }

    public String getOwner() {
        return owner;
    }

    public Integer getMode() {
        return mode;
    }

    public String getSponsor() {
        return sponsor;
    }

    public Boolean getSafe() {
        return safe;
    }

    public String getIP() {
        return ip;
    }

    public String getCountry() {
        return country;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void setUri(java.net.URI uri) {
        this.uri = uri;
    }

    public void setSponsor(String sponsor) {
        this.sponsor = sponsor;
    }

    public void setCreated(java.sql.Date created) {
        this.created = created;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public void setSafe(Boolean safe) {
        this.safe = safe;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
