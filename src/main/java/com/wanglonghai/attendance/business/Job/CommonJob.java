package com.wanglonghai.attendance.business.Job;

import com.wanglonghai.attendance.business.Service.AttendanceCheckService;
import com.wanglonghai.attendance.business.Service.WeiXinService;
import com.wanglonghai.attendance.entity.TimeEnum;
import com.wanglonghai.attendance.entity.dto.ToolRandom;
import com.wanglonghai.attendance.entity.dto.UserList;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Component
@Slf4j
public class CommonJob {
    @Autowired
    AttendanceCheckService attendanceCheckService;
    @Autowired
    UserList userList;
    /**
     *
     */
    @Scheduled(fixedDelay = 1000*60*100)//每100分钟执行一次
    public void circleLogin() {
        attendanceCheckService.mainTainToken(userList.getList().get(0).getTk());
    }
}
