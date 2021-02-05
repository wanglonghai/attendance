package com.wanglonghai.attendance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class AttendanceApplication {

    public static void main(String[] args) {
        log.info("准备启动");
        log.info("*****************application start*****************");
        SpringApplication.run(AttendanceApplication.class, args);
    }

}
