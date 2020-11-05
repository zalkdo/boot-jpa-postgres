package study.zalkdo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import study.zalkdo.dto.LogDto;
import study.zalkdo.service.LogService;

import java.util.Date;
import java.util.List;

@RestController
public class LogController {

    @Autowired
    private LogService logService;

    @GetMapping("/list")
    public List<LogDto> findAll(@RequestParam(value = "name", defaultValue = "World") String name) {
        List<LogDto> logList = logService.findAll();
        return logList;
    }

    @GetMapping("/save")
    public void save(){
        LogDto dto = new LogDto();
        dto.setLogin_dt(new Date());
        logService.save(dto);
    }
}
