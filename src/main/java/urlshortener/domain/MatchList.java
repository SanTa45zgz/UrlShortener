package urlshortener.domain;

import java.io.Serializable;
import java.util.List;

public class MatchList implements Serializable {

    private List<Match> matches;

    public MatchList() {}

    public List<Match> getMatches() {
        return matches;
    }

    public void setMatches(List<Match> matches) {
        this.matches = matches;
    }
}
