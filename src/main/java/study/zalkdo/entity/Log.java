package study.zalkdo.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@NoArgsConstructor
@Entity(name="SERVER_IP")
@Setter
@Getter
public class Log implements Serializable {

    @Id
    private String log_id;

    private String server_ip;

    private Date login_dt;
}
