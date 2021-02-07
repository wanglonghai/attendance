package com.wanglonghai.attendance.business.controller;

import com.wanglonghai.attendance.business.Service.AttendanceCheckService;
import com.wanglonghai.attendance.entity.TimeEnum;
import com.wanglonghai.attendance.entity.dto.ToolRandom;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName AttendanceController
 * @Author Gavin
 * @Date 2021/2/7 11:09
 * @Description 描述
 * @Version 1.0
 */
@RestController
@RequestMapping(value = "/wanglonghai")
@Slf4j
public class AttendanceController {
    @Autowired
    ToolRandom toolRandom;
    @Autowired
    AttendanceCheckService attendanceCheckService;
    @GetMapping(value ="/doAttendance")
    public String doAttendance(String timeTip){
        TimeEnum timeEnum=TimeEnum.valueOfStr(timeTip);
        String dateStr=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String resultStr="未知";
        if(!toolRandom.judgeDate(timeEnum,dateStr)){
            log.info(String.format("*****************manual attendance start,attendanceTime: %s %s*****************",dateStr,timeEnum.getCode()));
            String token=attendanceCheckService.login();
            if(StringUtils.isNotBlank(token)){
                Boolean result=attendanceCheckService.getQrCode(token)
                        &&attendanceCheckService.dk(token);
                toolRandom.markDate(timeEnum,dateStr);
                resultStr=String.format("*****************%s-%s,attendance %s*****************",dateStr,timeEnum.getCode(),result?"success":"fail!");;
            }else{
                resultStr=String.format("*****************%s-%s,attendance fail,login fail*****************",dateStr,timeEnum.getCode());;
            }
            log.info(resultStr);
        }else{
            resultStr=String.format("*****************已经打开了*****************",timeEnum.getCode());
            log.warn(resultStr);
        }
        return resultStr;
    }
}
