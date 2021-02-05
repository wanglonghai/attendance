package com.wanglonghai.attendance.business;

import com.wanglonghai.attendance.business.Service.AttendanceCheckService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @ClassName AttendanceJob
 * @Author Gavin
 * @Date 2021/2/5 10:05
 * @Description 描述
 * @Version 1.0
 */
@Component
@Slf4j
public class AttendanceJob {
    @Autowired
    AttendanceCheckService attendanceCheckService;
    //每天  8，12，20 点26分11秒
    //@Scheduled(cron = "1 2 8,12,20 * * ?")
    @Scheduled(cron = "0/10 * * * * ?")
    public void doAttendance(){
        log.error("*****************Scheduled start*****************");
        String token=attendanceCheckService.login();
        if(StringUtils.isNotBlank(token)){
            Boolean result=attendanceCheckService.getQrCode(token)
                    &&attendanceCheckService.dk(token);
        }
        log.error("*****************Scheduled end*****************");
    }

}
