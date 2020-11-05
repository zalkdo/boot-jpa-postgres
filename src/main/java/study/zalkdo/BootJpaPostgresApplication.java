package study.zalkdo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class BootJpaPostgresApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootJpaPostgresApplication.class, args);
    }

}
