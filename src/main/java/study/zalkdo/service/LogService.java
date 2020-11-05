package study.zalkdo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import study.zalkdo.dto.LogDto;
import study.zalkdo.entity.Log;
import study.zalkdo.repository.LogRepository;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

@Service
public class LogService {

    @Autowired
    private LogRepository logRepository;

    @SneakyThrows
    public void save(LogDto dto){

        ObjectMapper mapper = new ObjectMapper();
        Log log = mapper.convertValue(dto, Log.class);
        String ips = "";
        Enumeration e = NetworkInterface.getNetworkInterfaces();
        while(e.hasMoreElements()){
            NetworkInterface n = (NetworkInterface) e.nextElement();
            Enumeration ee = n.getInetAddresses();
            while(ee.hasMoreElements()){
                InetAddress i = (InetAddress) ee.nextElement();
                if(!(i.isLoopbackAddress()||i.isLinkLocalAddress())&&i.isSiteLocalAddress())
                    ips += "\t-"+n.getName() + " :: "+i.getHostAddress() + " \n";
            }
        }
        log.setServer_ip(ips);
        log.setLog_id(UUID.randomUUID().toString());
        logRepository.save(log);
    }

    public List<LogDto> findAll(){
        List<Log> results = logRepository.findAll();
        List<LogDto> transResults = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        for(Log log : results){
            transResults.add(mapper.convertValue(log, LogDto.class));
        }
        return transResults;
    }
}
