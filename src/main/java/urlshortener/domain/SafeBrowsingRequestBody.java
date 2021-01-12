package urlshortener.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SafeBrowsingRequestBody {

    private final String clientId = "unizarProjectSistemas";

    private final String clientVersion = "1.0.0";

    private final List<String> threatTypes = new ArrayList<>();

    private final List<String> platformTypes = new ArrayList<>();

    private final List<String> threatEntryTypes = new ArrayList<>();

    private final List<String> threatEntries = new ArrayList<>();

    public SafeBrowsingRequestBody() {
        // Threat type to add to the filter
        threatTypes.add("MALWARE");
        threatTypes.add("SOCIAL_ENGINEERING");

        // Operative System to add to the filter
        platformTypes.add("WINDOWS");

        // Threat entry type to search
        threatEntryTypes.add("URL");
    }

    public void setThreatEntries(List<String> urls) {
        threatEntries.clear();
        threatEntries.addAll(urls);
    }

    public Map<String, Object> createRequestBody() {

        Map<String, Object> client = new HashMap<>();
        client.put("clientId", clientId);
        client.put("clientVersion", clientVersion);

        Map<String, Object> threatInfo = new HashMap<>();
        threatInfo.put("threatTypes", threatTypes);
        threatInfo.put("platformTypes", platformTypes);
        threatInfo.put("threatEntryTypes", threatEntryTypes);
        threatInfo.put("threatEntries", prepareThreatEntries());

        Map<String, Object> map = new HashMap<>();
        map.put("client", client);
        map.put("threatInfo", threatInfo);

        return map;
    }

    private Object prepareThreatEntries() {
        List<Map<String, String>> list = new ArrayList<>();
        for (String url : threatEntries) {
            Map<String, String> map = new HashMap<>();
            map.put("url", url);
            list.add(map);
        }
        return list;
    }
}
