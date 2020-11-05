package study.zalkdo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class LogDto {

    private String log_id;

    private String server_ip;

    private Date login_dt;
}
