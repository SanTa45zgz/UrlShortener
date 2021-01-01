package urlshortener.domain;

import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class RedirectList {

    private final List<Pair<String, Long>> redirects;

    public RedirectList(List<Pair<String, Long>> redirects) {
        this.redirects = redirects;
    }

    public List<Pair<String, Long>> getRedirects() {
        return redirects;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Pair<String, Long> pair : redirects) {
            result.append(pair.getFirst()).append(":").append(pair.getSecond()).append(";");
        }
        if (result.length() > 1) {
            result.deleteCharAt(result.length() - 1);
        }
        return result.toString();
    }

    public static RedirectList createHttpRedirects(String value) {
        List<Pair<String, Long>> list = new ArrayList<>();
        String[] pairs = value.split(";");
        for (String pair : pairs) {
            String[] parameters = pair.split(":");
            if (parameters.length > 1) {
                list.add(Pair.of(parameters[0], Long.decode(parameters[1])));
            }
        }
        return new RedirectList(list);
    }
}
