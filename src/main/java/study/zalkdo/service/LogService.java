package study.zalkdo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import study.zalkdo.dto.LogDto;
import study.zalkdo.entity.Log;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import study.zalkdo.repository.LogReposigory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;

@Service
public class LogService {

    @Autowired
    LogReposigory logReposigory;

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
        logReposigory.save(log);
    }

    public List<LogDto> findAll(){
        List<Log> results = logReposigory.findAll();
        ObjectMapper mapper = new ObjectMapper();
        Jackson
    }
}
