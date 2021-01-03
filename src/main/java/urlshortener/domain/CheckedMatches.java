package urlshortener.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CheckedMatches {

    private final List<String> urls;

    private final List<String> matches;

    public CheckedMatches(List<String> urls, List<String> matches) {
        this.urls = urls;
        this.matches = matches;
    }

    public List<String> getUrls() {
        return urls;
    }

    public List<String> getMatches() {
        return matches;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String url : urls) {
            stringBuilder.append(url).append('¡');
        }
        if (stringBuilder.length() > 1) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        stringBuilder.append("¡#¡");
        for (String match : matches) {
            stringBuilder.append(match).append('¡');
        }
        if (!stringBuilder.substring(stringBuilder.length() - 3).equals("¡#¡") && stringBuilder.substring(stringBuilder.length() - 1).equals("¡")) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        return stringBuilder.toString();
    }

    public static CheckedMatches createCheckedMatches(String value) {
        String[] parts = value.split("¡#¡");
        String[] urlList = parts[0].split("¡");
        List<String> urls = new ArrayList<>(Arrays.asList(urlList));
        List<String> matches = new ArrayList<>();
        if (parts.length > 1) {
            String[] matchList = parts[1].split("¡");
            matches = new ArrayList<>(Arrays.asList(matchList));
        }
        return new CheckedMatches(urls, matches);
    }
}
