package urlshortener.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import urlshortener.domain.SystemInfo;
import urlshortener.repository.SystemInfoRepository;

@RestController
public class SystemInfoController {


    private final SystemInfoRepository systemInfoRepository;

    public SystemInfoController(SystemInfoRepository systemInfoRepository) {
        this.systemInfoRepository = systemInfoRepository;
    }

    @RequestMapping(value = "/stats", method = RequestMethod.GET)
    public ResponseEntity<SystemInfo> getSystemInfo(){
        SystemInfo serverInfo = systemInfoRepository.getSystemInfo();
        return new ResponseEntity<>(serverInfo, HttpStatus.OK);
    }

}
