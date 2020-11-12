package urlshortener.fixtures;

import urlshortener.domain.SystemInfo;

public class SystemInfoFixture {
    public static SystemInfo systemInfo(){
        return new SystemInfo(1L,1L,1L, null, null);
    }
}
